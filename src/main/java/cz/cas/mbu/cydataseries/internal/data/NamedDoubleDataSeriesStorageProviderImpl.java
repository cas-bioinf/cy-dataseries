package cz.cas.mbu.cydataseries.internal.data;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cas.mbu.cydataseries.DataSeries;

/**
 * Storage provider for {@link NamedDoubleDataSeriesImpl}.
 * @author MBU
 *
 */
public class NamedDoubleDataSeriesStorageProviderImpl extends AbstractDataSeriesStorageProvider {

	private final Logger logger = LoggerFactory.getLogger(NamedDoubleDataSeriesStorageProviderImpl.class);

	@Override
	public Class<? extends DataSeries<?, ?>> getProvidedClass() {
	    return NamedDoubleDataSeriesImpl.class;
	}
	
	
	@Override
	public String getSeriesTypeCaption() {		
		return "Named numeric";
	}


	@Override
	protected DataSeriesBuilder getSeriesBuilder() {
		return new NamedDoubleDataSeriesBuilder();
	}


	private class NamedDoubleDataSeriesBuilder extends DataSeriesBuilder {
		List<String> indexStrings;
		double[][] dataArray;

		@Override
		public DataSeriesBuilder parseIndex(List<String> indexStrings) {
			this.indexStrings = indexStrings;
			return this;
		}

		@Override
		public DataSeriesBuilder setDataPoint(int index, int row, String data) {
			if(dataArray == null) //lazy init
			{
				dataArray = new double[_rowIds.length][indexStrings.size()];
			}
			dataArray[index][row] = Double.parseDouble(data);

            return this;
		}

		@Override
		public DataSeries<?, ?> build() {
			return new NamedDoubleDataSeriesImpl(_suid, _name, _rowIds, _rowNames, indexStrings, dataArray);
		}
	}
	
}
