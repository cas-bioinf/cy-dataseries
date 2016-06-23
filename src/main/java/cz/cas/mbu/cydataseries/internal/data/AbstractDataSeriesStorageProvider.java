package cz.cas.mbu.cydataseries.internal.data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.cytoscape.model.SUIDFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cas.mbu.cydataeseries.internal.DataSeriesStorageManager;
import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesException;
import cz.cas.mbu.cydataseries.DataSeriesStorageProvider;

public abstract class AbstractDataSeriesStorageProvider  implements DataSeriesStorageProvider {

	private final Logger logger = LoggerFactory.getLogger(AbstractDataSeriesStorageProvider.class); 
	
	public AbstractDataSeriesStorageProvider() {
		super();
	}

	protected abstract DataSeriesBuilder getSeriesBuilder();
	
	@Override
	public DataSeries<?, ?> loadDataSeries(File file, String name, long oldSuid) throws IOException {
		try (CSVParser parser = new CSVParser(new FileReader(file), DataSeriesStorageManager.CSV_FORMAT))
		{
			List<CSVRecord> recordList = parser.getRecords();
			
			CSVRecord headerRecord = recordList.get(0);
			
			int numIndex =	headerRecord.size() - 2; //One column are the row names and one are ids
			if(numIndex <= 0)
			{
				throw new DataSeriesException("Data series does not contain any values.");
			}
			List<String> index = new ArrayList<>(numIndex);
			if(!headerRecord.get(0).equals("Id"))
			{
				throw new DataSeriesException("Didn't find id column for DS.");
			}
			
			if(! headerRecord.get(1).equals("RowName"))
			{
				throw new DataSeriesException("Didn't find row name column for DS.");
			}
			
			//Skipping the header row
			for(int i = 0; i < numIndex; i++)
			{
				String headerEntry = headerRecord.get(i + 2);				
				index.add(headerEntry);
			}
			
			int numRows = (int)parser.getRecordNumber();
			//Note: the code below has to skip the header row!
			List<String> rowNames = new ArrayList<>(numRows - 1);
			int[] rowIds = new int[numRows - 1];
			
			for(int row = 1; row < numRows; row++)
			{
				rowIds[row - 1] = Integer.parseInt(recordList.get(row).get(0)); 
				rowNames.add(recordList.get(row).get(1)); 
			}
			
			DataSeriesBuilder builder = getSeriesBuilder();
			
			builder.name(name).suid(SUIDFactory.getNextSUID());
			builder.parseIndex(index).rowIds(rowIds).rowNames(rowNames);
			
			//Skipping the header row
			for(int row = 1; row < numRows; row++)
			{
				for(int col = 0; col < numIndex; col++)
				{
					builder.setDataPoint(row - 1, col, recordList.get(row).get(col + 2));					
				}
			}
			
			return builder.build();
			
		}
	}
	
	protected String transformIndexForWrite(Object index)
	{
		return index.toString();
	}
	
	protected String transformDataForWrite(Object data)
	{
		return data.toString();		
	}

	@Override
	public void saveDataSeries(DataSeries<?, ?> dataSeries, File file) throws IOException {
		if(!getProvidedClass().isAssignableFrom(dataSeries.getClass()))
		{
			throw new IllegalArgumentException("Invalid type passed to saveDataSeries. Expected " + getProvidedClass().getName() + " got: " + dataSeries.getClass().getName());
		}
		try (CSVPrinter printer = new CSVPrinter(new FileWriter(file), DataSeriesStorageManager.CSV_FORMAT))
		{
			printer.print("Id");
			printer.print("RowName");
			
			for(Object indexElement : dataSeries.getIndex())
			{
				printer.print(transformIndexForWrite(indexElement));
			}
			printer.println();
			
			for(int row = 0; row < dataSeries.getRowCount(); row++)
			{
				printer.print(dataSeries.getRowID(row));				
				printer.print(dataSeries.getRowName(row));
				for(Object dataElement : dataSeries.getRowData(row))
				{
					printer.print(transformDataForWrite(dataElement));
				}
				printer.println();
			}
		}
		
	}

	protected abstract static class DataSeriesBuilder 
	{
		protected List<String> _rowNames;
		protected int[] _rowIds;
		protected String _name;
		protected Long _suid;
		
		public DataSeriesBuilder rowNames(List<String> rowNames)
		{
			_rowNames = rowNames;
			return this;
		}
		
		public DataSeriesBuilder rowIds(int[] rowIds)
		{
			_rowIds = rowIds;
			return this;
		}
		
		public DataSeriesBuilder name(String name)
		{
			_name = name;
			return this;
		}
		
		public DataSeriesBuilder suid(Long suid)
		{
			_suid = suid;
			return this;
		}
		
		public abstract DataSeriesBuilder parseIndex(List<String> indexStrings);
		public abstract DataSeriesBuilder setDataPoint(int index, int row, String data);
		
		public abstract DataSeries<?, ?> build();
	}
}