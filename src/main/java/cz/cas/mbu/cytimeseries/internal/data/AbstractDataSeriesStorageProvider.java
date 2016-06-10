package cz.cas.mbu.cytimeseries.internal.data;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cas.mbu.cytimeseries.DataSeries;
import cz.cas.mbu.cytimeseries.DataSeriesException;
import cz.cas.mbu.cytimeseries.DataSeriesStorageProvider;
import cz.cas.mbu.cytimeseries.internal.DataSeriesStorageManager;

public abstract class AbstractDataSeriesStorageProvider  implements DataSeriesStorageProvider {

	private final Logger logger = LoggerFactory.getLogger(AbstractDataSeriesStorageProvider.class); 
	
	public AbstractDataSeriesStorageProvider() {
		super();
	}

	protected abstract DataSeriesBuilder getSeriesBuilder();
	
	@Override
	public DataSeries<?, ?> loadDataSeries(File file, String name, long suid) throws IOException {
		try (CSVParser parser = new CSVParser(new FileReader(file), DataSeriesStorageManager.CSV_FORMAT))
		{
			int numIndex =	parser.getHeaderMap().size() - 2; //One column are the row names and one are ids
			if(numIndex <= 0)
			{
				throw new DataSeriesException("Data series does not contain any values.");
			}
			List<String> index = new ArrayList<>(numIndex);
			Iterator<Map.Entry<String, Integer>> headerIterator = parser.getHeaderMap().entrySet().iterator();
			if(! headerIterator.next().getKey().equals("Id"))
			{
				logger.error("Didn't find id column for DS.");
			}
			
			if(! headerIterator.next().getKey().equals("RowName"))
			{
				logger.error("Didn't find row name column for DS.");
			}
			
			for(int i = 0; i < numIndex; i++)
			{
				if(!headerIterator.hasNext())
				{
					logger.error("Inconsistent header");
					break;
				}
				
				Map.Entry<String,Integer> headerEntry = headerIterator.next();
				
				if(headerEntry.getValue().intValue() != i + 2)
				{
					logger.error("Inconsistent header indexing");
					break;
				}
				
				index.add(headerEntry.getKey());
			}
			
			int numRows = (int)parser.getRecordNumber();
			String[] rowNames = new String[numRows];
			int[] rowIds = new int[numRows];
			
			List<CSVRecord> recordList = parser.getRecords();
			for(int row = 0; row < numRows; row++)
			{
				rowIds[row] = Integer.parseInt(recordList.get(row).get(0)); 
				rowNames[row] = recordList.get(row).get(1); 
			}
			
			DataSeriesBuilder builder = getSeriesBuilder();
			
			builder.name(name).suid(suid);
			builder.parseIndex(index).rowIds(rowIds).rowNames(rowNames);
			
			for(int row = 0; row < numRows; row++)
			{
				for(int col = 0; col < numIndex; col++)
				{
					builder.setDataPoint(row, col, recordList.get(row).get(col + 2));					
				}
			}
			
			return builder.build();
			
		}
	}
	
	protected Function<Object, String> getIndexWriteTransform()
	{
		return (x) -> x.toString();
	}
	
	protected Function<Object, String> getDataWriteTransform()
	{
		return (x) -> x.toString();		
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
			printer.printRecord(dataSeries.getIndex().stream().map(getIndexWriteTransform()));
			
			for(int row = 0; row < dataSeries.getRowCount(); row++)
			{
				printer.print(dataSeries.getRowID(row));				
				printer.print(dataSeries.getRowName(row));
				printer.printRecord(dataSeries.getRowData(row).stream().map(getDataWriteTransform()));				
			}
		}
		
	}

	protected abstract static class DataSeriesBuilder 
	{
		protected String[] _rowNames;
		protected int[] _rowIds;
		protected String _name;
		protected Long _suid;
		
		public DataSeriesBuilder rowNames(String[] rowNames)
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