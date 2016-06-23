package cz.cas.mbu.cydataseries.internal.data;

import java.util.List;

import cz.cas.mbu.cydataseries.TimeSeries;

public class TimeSeriesImpl extends AbstractDataSeries<Double, Double> implements TimeSeries {

	private double[] indexArray;
	private double[][] dataArray;
	
	public TimeSeriesImpl(Long suid, String name, int[] rowIDs, List<String> rowNames, double[] indexArray, double[][] dataArray) {
		super(suid, name, rowIDs, rowNames);
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
