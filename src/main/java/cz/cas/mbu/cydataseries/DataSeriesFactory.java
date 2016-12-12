package cz.cas.mbu.cydataseries;

import java.util.List;

/**
 * Public interface for creating the default types of data series
 * @author Martin
 *
 */
public interface DataSeriesFactory {
	/**
	 * Create a time series instance
	 * @param name
	 * @param rowNames
	 * @param timePoints
	 * @param data the first index corresponds to rows, second index to time points
	 * @return
	 */
	TimeSeries createTimeSeries(String name, List<String> rowNames, double[] timePoints, double[][]data);
	
	/**
	 * Create a named double data series instance (e.g. multiple named measurements of a quantity)
	 * @param name
	 * @param rowNames
	 * @param columnNames the names of the independent variables (e.g. measurements)
	 * @param data the first index corresponds to rows, second index to columns (indices)
	 * @return
	 */
	NamedDoubleDataSeries createNamedDoubleDataSeries(String name, List<String> rowNames, List<String> columnNames, double[][]data);

}
