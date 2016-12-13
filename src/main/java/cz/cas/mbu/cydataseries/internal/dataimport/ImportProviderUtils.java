package cz.cas.mbu.cydataseries.internal.dataimport;

import cz.cas.mbu.cydataseries.dataimport.DataSeriesImportException;
import cz.cas.mbu.cydataseries.dataimport.PreImportResults;

public class ImportProviderUtils {
	public static double[][] parseDataArrayAsDoubles(PreImportResults preImportResults) {
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
				else {
					rawValue = rawValue.trim();
					if(rawValue.isEmpty() || rawValue.equalsIgnoreCase("null") || rawValue.equalsIgnoreCase("NA") || rawValue.equalsIgnoreCase("NaN"))
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
		}
		return dataArray;
	}

}
