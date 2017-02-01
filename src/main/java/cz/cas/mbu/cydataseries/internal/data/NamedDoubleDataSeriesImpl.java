package cz.cas.mbu.cydataseries.internal.data;

import java.util.List;

import cz.cas.mbu.cydataseries.NamedDoubleDataSeries;

/**
 * Named double series - typically repeated measurements.
 */
public class NamedDoubleDataSeriesImpl extends AbstractListIndexDataSeries<String, Double> implements NamedDoubleDataSeries {

	private double[][] dataArray;
	
	public NamedDoubleDataSeriesImpl(Long suid, String name, int[] rowIDs, List<String> rowNames, List<String> index, double[][] dataArray) {
		super(suid, name, rowIDs, rowNames, index, String.class);

		//Check inputs
		if(rowNames.size() != dataArray.length) {
			throw new IllegalArgumentException("Row names have different size than the number of rows of data.");
		}
		if(rowIDs.length != dataArray.length) {
			throw new IllegalArgumentException("Row IDs have different size than the number of rows of data.");
		}
		for(int row = 0; row < dataArray.length; row++) {
			if(index.size() != dataArray[row].length) {
				throw new IllegalArgumentException("Row no." + row + " has different size than the given time points.");
			}
		}
		
		this.dataArray = dataArray;
	}
	
	@Override
	public double[][] getDataArray() {
		return dataArray;
	}
	
}
