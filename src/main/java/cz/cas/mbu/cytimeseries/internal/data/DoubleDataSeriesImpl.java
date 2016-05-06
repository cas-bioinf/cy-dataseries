package cz.cas.mbu.cytimeseries.internal.data;

import java.util.List;

import cz.cas.mbu.cytimeseries.DoubleDataSeries;

public class DoubleDataSeriesImpl<INDEX> extends AbstractListIndexDataSeries<INDEX, Double> implements DoubleDataSeries<INDEX> {

	private double[][] dataArray;

	public DoubleDataSeriesImpl(List<Long> rowSuids, List<INDEX> indexData, Class<INDEX> indexClass,
			double[][] dataArray) {
		super(rowSuids, indexData, indexClass);
		this.dataArray = dataArray;
	}

	@Override
	public double[][] getDataArray() {
		return dataArray;
	}

	
}
