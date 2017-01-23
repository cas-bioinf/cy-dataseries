package cz.cas.mbu.cydataseries.internal.dataimport;

import java.util.List;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.TimeSeries;
import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportException;
import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportProvider;
import cz.cas.mbu.cydataseries.dataimport.PreImportResults;
import cz.cas.mbu.cydataseries.internal.data.TimeSeriesImpl;

/**
 * Import provider for {@link TimeSeriesImpl}. Can be consided an example implementation.
 * @author MBU
 *
 */
public class TimeSeriesImportProviderImpl implements DataSeriesImportProvider{

	@Override
	public DataSeries<?, ?> importDataDataSeries(String name, Long suid, PreImportResults preImportResults) {
		preImportResults.checkConsistentcy();
		
		int[] rowIDs = new int[preImportResults.getCellData().length];
		for(int i = 0; i < rowIDs.length; i++)
		{
			rowIDs[i] = i;
		}
		List<String> rowNames = preImportResults.getRowNames();
		
		double[] indexArray = new double[preImportResults.getIndexValues().size()];
		for(int index = 0; index < indexArray.length; index++)
		{
			String rawValue = preImportResults.getIndexValues().get(index);
			try {
				indexArray[index] = Double.parseDouble(rawValue);
			} catch (NumberFormatException ex)
			{
				throw new DataSeriesImportException("Could not parse index (time point) no. " + index + " with value '" + rawValue +"' as a number.\nTime series requires all index values to be numbers.", ex);
			}
		}
					
		double[][] dataArray = ImportProviderUtils.parseDataArrayAsDoubles(preImportResults);
		return new TimeSeriesImpl(suid, name, rowIDs, rowNames, indexArray, dataArray);
	}


	@Override
	public String getDescription() {
		return "Time Series";
	}

	@Override
	public Class<? extends DataSeries<?, ?>> getImportedClass() {
		return TimeSeries.class;
	}
	
	

}
