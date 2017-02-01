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

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.DataSeriesException;
import cz.cas.mbu.cydataseries.DataSeriesStorageProvider;
import cz.cas.mbu.cydataseries.internal.DataSeriesStorageManagerImpl;

/**
 * A base class for all DS storage providers that can directly convert all index and data values from/to string.
 * To store a new type of DS, you usually want to extend this class and override 
 * {@link #transformDataForWrite(Object)}, {@link #transformIndexForWrite(Object). 
 * Further you should create a custom {@link #DataSeriesBuilder} subclass and override {@link #getSeriesBuilder()} to return an instance of it.
 * See {@link TimeSeriesStorageProviderImpl} for an example implementation.
 * @author MBU
 *
 */
public abstract class AbstractDataSeriesStorageProvider  implements DataSeriesStorageProvider {

	private final Logger logger = LoggerFactory.getLogger(AbstractDataSeriesStorageProvider.class); 
	
	public AbstractDataSeriesStorageProvider() {
		super();
	}

	protected abstract DataSeriesBuilder getSeriesBuilder();
	
	/**
	 * Loads the data series by first getting an DS implementation-specific builder via {@link #getSeriesBuilder()} 
	 * and then calling the relevant methods of it. 
	 */
	@Override
	public DataSeries<?, ?> loadDataSeries(File file, String name, long oldSuid) throws IOException {
		try (CSVParser parser = new CSVParser(new FileReader(file), DataSeriesStorageManagerImpl.CSV_FORMAT))
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
	
	/**
	 * Override this to transform index values for storage.
	 * The default implementation simply calls {@link Object#toString()}.
	 * @param index
	 * @return
	 */
	protected String transformIndexForWrite(Object index)
	{
		return index.toString();
	}
	
	/**
	 * Override this to transform data values for storage.
	 * The default implementation simply calls {@link Object#toString()}.
	 * @param index
	 * @return
	 */
	protected String transformDataForWrite(Object data)
	{
		return data.toString();		
	}

	/**
	 * Save data series by calling {@link #transformDataForWrite(Object)} and {@link #transformIndexForWrite(Object)}.
	 */
	@Override
	public void saveDataSeries(DataSeries<?, ?> dataSeries, File file) throws IOException {
		if(!getProvidedClass().isAssignableFrom(dataSeries.getClass()))
		{
			throw new IllegalArgumentException("Invalid type passed to saveDataSeries. Expected " + getProvidedClass().getName() + " got: " + dataSeries.getClass().getName());
		}
		try (CSVPrinter printer = new CSVPrinter(new FileWriter(file), DataSeriesStorageManagerImpl.CSV_FORMAT))
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

	/**
	 * Handles individual steps of loading a DS and finaly returns a newly created data series. 
	 * @author MBU
	 *
	 */
	protected abstract static class DataSeriesBuilder {
		protected List<String> _rowNames;
		protected int[] _rowIds;
		protected String _name;
		protected Long _suid;
		
		/**
		 * Called once when row names for all rows are parsed.
		 * @param rowNames
		 * @return
		 */
		public DataSeriesBuilder rowNames(List<String> rowNames) {
			_rowNames = rowNames;
			return this;
		}
		
		/**
		 * Called once, when ro IDs for all rows are parsed.
		 * @param rowIds
		 * @return
		 */
		public DataSeriesBuilder rowIds(int[] rowIds) {
			_rowIds = rowIds;
			return this;
		}
		
		/**
		 * Called once, when the name of the DS is parsed.
		 * @param name
		 * @return
		 */
		public DataSeriesBuilder name(String name) {
			_name = name;
			return this;
		}
		
		/**
		 * Called once, when the SUID of the DS is parsed.
		 * @param suid
		 * @return
		 */
		public DataSeriesBuilder suid(Long suid) {
			_suid = suid;
			return this;
		}
		
		/**
		 * Called once, when the index of the DS is read
		 * @param indexStrings
		 * @return
		 */
		public abstract DataSeriesBuilder parseIndex(List<String> indexStrings);
		
		/**
		 * Called for each index-row combination as it is parsed. 
		 * This is guaranteed to be called after {@link #parseIndex(List)}, {@link #rowIds(int[])} and {@link #rowNames(List)},
		 * so it is safe to allocate a data array based on the calls to this methods.
		 * @param index
		 * @param row
		 * @param data
		 * @return
		 */
		public abstract DataSeriesBuilder setDataPoint(int index, int row, String data);
		
		/**
		 * Create a DS instance from the gathered information.
		 * @return
		 */
		public abstract DataSeries<?, ?> build();
	}
}