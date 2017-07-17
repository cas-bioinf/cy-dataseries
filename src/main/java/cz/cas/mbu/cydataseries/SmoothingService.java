package cz.cas.mbu.cydataseries;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Service for smoothing time series.
 * @author Martin
 *
 */
public interface SmoothingService {
	TimeSeries smooth(TimeSeries noisyData, double[] estimateX, SingleParameterSmoothingProvider provider, double parameter, String resultName);

	/**
	 * 
	 * @param noisyData
	 * @param estimateX
	 * @param provider
	 * @param parameter
	 * @param resultName
	 * @param rowGrouping Map of result row name to row indices (not row IDs) that will be smoothed to give the output
	 * @return
	 */
	TimeSeries smooth(TimeSeries noisyData, double[] estimateX, SingleParameterSmoothingProvider provider, double parameter, String resultName, Map<String, List<Integer>> rowGrouping);	
	

	/**
	 * Smooth a single row
	 * @param noisyData
	 * @param estimateX
	 * @param provider
	 * @param parameter
	 * @param rows
	 * @return
	 */
	double[] smooth(TimeSeries noisyData, double[] estimateX, SingleParameterSmoothingProvider provider, double parameter, List<Integer> rows);
	
	/**
	 * Get a row grouping where all rows with the same name in the series are mapped to a single row in the output.
	 * @param series
	 * @return
	 */
	Map<String, List<Integer>> getDefaultRowGrouping(TimeSeries series);
	
	/**
	 * Get the union of all time points in a collection of time series.
	 * @param timeSeries
	 * @return
	 */
	double[] mergeTimePoints(Collection<TimeSeries> timeSeries);
	
	List<SingleParameterSmoothingProvider> getSmoothingProviders();
}
