package cz.cas.mbu.cydataseries;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SmoothingService {
	TimeSeries linearKernelSmoothing(TimeSeries noisyData, double[] estimateX, double bandwidth, String resultName);
	
	/**
	 * 
	 * @param noisyData
	 * @param estimateX
	 * @param bandwidth
	 * @param resultName
	 * @param rowGrouping Map of result row name to row indices (not row IDs) that will be smoothed to give the output
	 * @return
	 */
	TimeSeries linearKernelSmoothing(TimeSeries noisyData, double[] estimateX, double bandwidth, String resultName, Map<String, List<Integer>> rowGrouping);	
	
	/**
	 * Smooth a single row
	 * @param noisyData
	 * @param estimateX
	 * @param bandwidth
	 * @param rows
	 * @return
	 */
	double[] linearKernelSmoothing(TimeSeries noisyData, double[] estimateX, double bandwidth, List<Integer> rows);
	
	Map<String, List<Integer>> getDefaultRowGrouping(TimeSeries series);
	
	double[] mergeTimePoints(Collection<TimeSeries> timeSeries);
}
