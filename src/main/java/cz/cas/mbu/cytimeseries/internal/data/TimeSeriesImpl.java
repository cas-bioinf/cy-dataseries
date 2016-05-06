package cz.cas.mbu.cytimeseries.internal.data;

import java.util.List;

import cz.cas.mbu.cytimeseries.TimeSeries;

public class TimeSeriesImpl extends AbstractDataSeries<Double, Double> implements TimeSeries {

	private double[] indexArray;
	private double[][] dataArray;
	
	public TimeSeriesImpl(List<Long> rowSUIDs, double[] indexArray, double[][] dataArray) {
		super(rowSUIDs);
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
