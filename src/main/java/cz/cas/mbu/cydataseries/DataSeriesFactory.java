package cz.cas.mbu.cydataseries;

import java.util.List;

public interface DataSeriesFactory {
	/**
	 * 
	 * @param name
	 * @param rowNames
	 * @param timePoints
	 * @param data the first index corresponds to rows, second index to time points
	 * @return
	 */
	TimeSeries createTimeSeries(String name, List<String> rowNames, double[] timePoints, double[][]data);
}
