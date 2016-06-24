package cz.cas.mbu.cydataseries.internal.data;

import java.util.List;

import cz.cas.mbu.cydataseries.TimeSeries;

public class TimeSeriesImpl extends AbstractDataSeries<Double, Double> implements TimeSeries {

	private double[] indexArray;
	private double[][] dataArray;
	
	public TimeSeriesImpl(Long suid, String name, int[] rowIDs, List<String> rowNames, double[] indexArray, double[][] dataArray) {
		super(suid, name, rowIDs, rowNames);
		//Check inputs
		if(rowNames.size() != dataArray.length)
		{
			throw new IllegalArgumentException("Row names have different size than the number of rows of data.");
		}
		if(rowIDs.length != dataArray.length)
		{
			throw new IllegalArgumentException("Row IDs have different size than the number of rows of data.");
		}
		for(int row = 0; row < dataArray.length; row++)
		{
			if(indexArray.length != dataArray[row].length)
			{
				throw new IllegalArgumentException("Row no." + row + " has different size than the given time points.");
			}
		}
		
		this.indexArray = indexArray;
		this.dataArray = dataArray;
	}
	
	@Override
	public double[] getIndexArray() {
		return indexArray;
	}

	@Override
	public double[][] getDataArray() {
		return dataArray;
	}
	
}
