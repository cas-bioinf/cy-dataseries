package cz.cas.mbu.cydataseries.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;
import org.cytoscape.application.CyUserLog;
import org.cytoscape.model.SUIDFactory;

import com.google.common.primitives.Doubles;

import cz.cas.mbu.cydataseries.SmoothingService;
import cz.cas.mbu.cydataseries.TimeSeries;
import cz.cas.mbu.cydataseries.internal.data.TimeSeriesImpl;

public class SmoothingServiceImpl implements SmoothingService{

	private final Logger userLogger = Logger.getLogger(CyUserLog.NAME); 
	

	@Override
	public TimeSeries linearKernelSmoothing(TimeSeries noisyData, double[] estimateX, double bandwidth, String resultName) {
		double[][] resultData = new double[noisyData.getRowCount()][];
		for(int row = 0; row < noisyData.getRowCount(); row++)
		{
			resultData[row] = KernelSmoothing.linearKernalEstimator(noisyData.getIndexArray(), noisyData.getRowDataArray(row), estimateX, bandwidth);
		}
		return new TimeSeriesImpl(SUIDFactory.getNextSUID(), resultName, Arrays.copyOf(noisyData.getRowIDs(),noisyData.getRowCount()), new ArrayList<>(noisyData.getRowNames()), Arrays.copyOf(estimateX,  estimateX.length), resultData);
	}

	
	@Override
	public TimeSeries linearKernelSmoothing(TimeSeries noisyData, double[] estimateX, double bandwidth, String resultName, Map<String, List<Integer>> rowGrouping) {
		double[][] resultData = new double[rowGrouping.size()][];
		int[] rowIds = new int[rowGrouping.size()];
		List<String> rowNames = new ArrayList<>();
		int row = 0;
		for(Map.Entry<String, List<Integer>> rowGroup : rowGrouping.entrySet())
		{
			resultData[row] = linearKernelSmoothing(noisyData, estimateX, bandwidth, rowGroup.getValue());
			rowIds[row] = row;
			rowNames.add(rowGroup.getKey());
			row++;
		}
		return new TimeSeriesImpl(SUIDFactory.getNextSUID(), resultName, rowIds, new ArrayList<>(noisyData.getRowNames()), Arrays.copyOf(estimateX,  estimateX.length), resultData);
	}

	@Override
	public double[] linearKernelSmoothing(TimeSeries noisyData, double[] estimateX, double bandwidth, List<Integer> rows)
	{
		double[] allRowsConcat = new double[rows.size() * noisyData.getIndexCount()];
		double[] repeatedIndex = new double[rows.size() * noisyData.getIndexCount()];
		int rowLength = noisyData.getIndexCount(); 
		for(int i = 0; i < rows.size(); i++ )
		{
			int row = rows.get(i);
			System.arraycopy(noisyData.getRowDataArray(row), 0, allRowsConcat, i * rowLength, rowLength);
			System.arraycopy(noisyData.getIndexArray(), 0, repeatedIndex, i * rowLength, rowLength);
		}
	
		//Do the smoothing
		double[] smoothedY = KernelSmoothing.linearKernalEstimator(repeatedIndex, allRowsConcat, estimateX, bandwidth);
		return smoothedY;
	}
	
	


	@Override
	public Map<String, List<Integer>> getDefaultRowGrouping(TimeSeries series) {
		return IntStream.range(0, series.getRowCount())
				.boxed() //I need to map to Integers to be able to use Collectors.groupingBy
				.collect(Collectors.groupingBy(rowIndex -> series.getRowName(rowIndex)));		
	}


	@Override
	public double[] mergeTimePoints(Collection<TimeSeries> timeSeries) {
		if(timeSeries.size() == 1)
		{
			TimeSeries ts =timeSeries.iterator().next();
			return Arrays.copyOf(ts.getIndexArray(), ts.getIndexCount());
		}
		else
		{
			List<Double> resultTimePoints = new ArrayList<>();
			timeSeries.forEach( 
					ts -> { resultTimePoints.addAll(ts.getIndex());}
					);
			resultTimePoints.sort(Double::compareTo);

			double tolerance = 0.00001;
			List<Double> resultWithoutDuplicates = new ArrayList<>(resultTimePoints.size());
			resultWithoutDuplicates.add(resultTimePoints.get(0));
			for(int resultIndex = 1; resultIndex < resultTimePoints.size(); resultIndex++)
			{
				double differenceFromPrevious = resultWithoutDuplicates.get(resultWithoutDuplicates.size() - 1) - resultTimePoints.get(resultIndex); 
				if(Math.abs(differenceFromPrevious) >= tolerance)
				{
					resultWithoutDuplicates.add(resultTimePoints.get(resultIndex));
				}
			}
			
			return Doubles.toArray(resultWithoutDuplicates);
		}
	}
		
	

}
