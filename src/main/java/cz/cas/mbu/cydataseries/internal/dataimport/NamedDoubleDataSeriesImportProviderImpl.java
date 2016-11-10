package cz.cas.mbu.cydataseries.internal.dataimport;

import java.util.ArrayList;
import java.util.List;

import cz.cas.mbu.cydataseries.DataSeries;
import cz.cas.mbu.cydataseries.NamedDoubleDataSeries;
import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportException;
import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportProvider;
import cz.cas.mbu.cydataseries.dataimport.PreImportResults;
import cz.cas.mbu.cydataseries.internal.data.NamedDoubleDataSeriesImpl;

public class NamedDoubleDataSeriesImportProviderImpl implements DataSeriesImportProvider{

	@Override
	public DataSeries<?, ?> importDataDataSeries(String name, Long suid, PreImportResults preImportResults) {
		preImportResults.checkConsistentcy();
		
		int[] rowIDs = new int[preImportResults.getCellData().length];
		for(int i = 0; i < rowIDs.length; i++)
		{
			rowIDs[i] = i;
		}
		List<String> rowNames = preImportResults.getRowNames();
		
		List<String> indexArray = new ArrayList<>(preImportResults.getIndexValues());
					
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
		return new NamedDoubleDataSeriesImpl(suid, name, rowIDs, rowNames, indexArray, dataArray);
	}

	@Override
	public String getDescription() {
		return "Named numeric series";
	}

	@Override
	public Class<? extends DataSeries<?, ?>> getImportedClass() {
		return NamedDoubleDataSeries.class;
	}
	
	

}
