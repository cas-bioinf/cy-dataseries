package cz.cas.mbu.cytimeseries.internal.data;

import java.util.List;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cas.mbu.cytimeseries.DataSeries;

public class TimeSeriesStorageProviderImpl extends AbstractDataSeriesStorageProvider {

	private final Logger logger = LoggerFactory.getLogger(TimeSeriesStorageProviderImpl.class); 
	
	private static final String INDEX_PREFIX = "Time_";
	
	
	@Override
	public Class<? extends DataSeries<?, ?>> getProvidedClass() {
		return TimeSeriesImpl.class;
	}

	@Override
	protected Function<Object, String> getIndexWriteTransform() {
		return (x) -> { return INDEX_PREFIX + Double.toString((Double)x); };	
	}

	
	
	@Override
	protected DataSeriesBuilder getSeriesBuilder() {
		return new TimeSeriesBuilder();
	}



	private class TimeSeriesBuilder extends DataSeriesBuilder
	{
		double[] indexArray;
		double [][] dataArray;
		
		

		@Override
		public DataSeriesBuilder parseIndex(List<String> indexStrings) {
			indexArray = new double[indexStrings.size()];
			for(int i = 0; i < indexStrings.size(); i++)
			{
				String rawString = indexStrings.get(i);
				if(!rawString.startsWith(INDEX_PREFIX))
				{
					indexArray[i] = Double.NaN;
					logger.error("Index '" + rawString + "' does not start with '" + INDEX_PREFIX + "'");					
				}
				else
				{
					String numberString = rawString.substring(INDEX_PREFIX.length());
					indexArray[i] = Double.parseDouble(numberString);					
				}
			}
			return this;
		}

		@Override
		public DataSeriesBuilder setDataPoint(int index, int row, String data) {
			if(dataArray == null) //lazy init
			{
				dataArray = new double[_rowIds.length][indexArray.length];
			}
			
			dataArray[index][row] = Double.parseDouble(data);
			
			return this;
		}

		@Override
		public DataSeries<?, ?> build() {
			return new TimeSeriesImpl(_suid, _name, _rowIds, _rowNames, indexArray, dataArray);
		}
		
	}
	
}
