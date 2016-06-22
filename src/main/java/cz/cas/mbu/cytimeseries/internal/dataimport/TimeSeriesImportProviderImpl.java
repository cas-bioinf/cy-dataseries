package cz.cas.mbu.cytimeseries.internal.dataimport;

import org.cytoscape.model.SUIDFactory;

import cz.cas.mbu.cytimeseries.DataSeries;
import cz.cas.mbu.cytimeseries.dataimport.DataSeriesImportException;
import cz.cas.mbu.cytimeseries.dataimport.DataSeriesImportProvider;
import cz.cas.mbu.cytimeseries.dataimport.PreImportResults;
import cz.cas.mbu.cytimeseries.internal.data.TimeSeriesImpl;

public class TimeSeriesImportProviderImpl implements DataSeriesImportProvider{

	@Override
	public DataSeries<?, ?> importDataDataSeries(String name, Long suid, PreImportResults preImportResults) {
		preImportResults.checkConsistentcy();
		
		int[] rowIDs = new int[preImportResults.getCellData().length];
		for(int i = 0; i < rowIDs.length; i++)
		{
			rowIDs[i] = i;
		}
		String[] rowNames = new String[preImportResults.getRowNames().size()];
		rowNames = preImportResults.getRowNames().toArray(rowNames);
		
		double[] indexArray = new double[preImportResults.getIndexValues().size()];
		for(int index = 0; index < indexArray.length; index++)
		{
			String rawValue = preImportResults.getIndexValues().get(index);
			try {
				indexArray[index] = Double.parseDouble(rawValue);
			} catch (NumberFormatException ex)
			{
				throw new DataSeriesImportException("Could not parse index (time point) no. " + index + " with value '" + rawValue +"' as a number.", ex);
			}
		}
					
		double[][] dataArray = new double[preImportResults.getCellData().length][preImportResults.getIndexValues().size()];
		for(int row = 0; row < dataArray.length; row++)
		{
			for(int index = 0; index < dataArray[row].length; index++)
			{
				String rawValue = preImportResults.getCellData()[row][index];
				if(rawValue == null)
				{
					dataArray[row][index] = Double.NaN;
				}
				else 
				{
					try {					
						dataArray[row][index] = Double.parseDouble(rawValue); 
					}
					catch (NumberFormatException ex)
					{
						throw new DataSeriesImportException("Could not parse data at row no. " + row + ", index no. " + index + " with value '" + rawValue + "' as a number.", ex);
					}
				}
			}
		}
		return new TimeSeriesImpl(suid, name, rowIDs, rowNames, indexArray, dataArray);
	}

	@Override
	public String getDescription() {
		return "Time Series";
	}

	
}
