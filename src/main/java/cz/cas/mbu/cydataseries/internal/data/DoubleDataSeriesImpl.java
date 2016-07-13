package cz.cas.mbu.cydataseries.internal.data;

import java.util.List;

import cz.cas.mbu.cydataseries.DoubleDataSeries;

/**
 * Data series for double data.
 *
 * @param <INDEX>
 */
public class DoubleDataSeriesImpl<INDEX> extends AbstractListIndexDataSeries<INDEX, Double> implements DoubleDataSeries<INDEX> {

	private double[][] dataArray;

	public DoubleDataSeriesImpl(Long suid, String name, int[] rowIds, List<String> rowNames, List<INDEX> indexData,
			Class<INDEX> indexClass, double[][] dataArray) {
		super(suid, name, rowIds, rowNames, indexData, indexClass);
		this.dataArray = dataArray;
	}

	@Override
	public double[][] getDataArray() {
		return dataArray;
	}

}
