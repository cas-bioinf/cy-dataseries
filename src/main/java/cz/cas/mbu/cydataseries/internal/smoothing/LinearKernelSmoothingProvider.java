package cz.cas.mbu.cydataseries.internal.smoothing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.primitives.Doubles;

public class LinearKernelSmoothingProvider implements SingleParameterSmoothingProvider{

	@Override
	public double[] smooth(double[] sourceX, double[] noisyY, double[] estimateX, double parameter) {
		return KernelSmoothing.linearKernalEstimator(sourceX, noisyY, estimateX, parameter);
	}

	
	
	@Override
	public String getName() {
		return "Linear Kernel";
	}



	@Override
	public ParameterDisplayAid getDisplayAid(double[] sourceX) {
		double minExpectedBandwidth;
		double bestGuess;
		double maxExpectedBandwidth;
		
		if(sourceX.length < 2)
		{
			minExpectedBandwidth = 0.01;
			bestGuess = 1;
			maxExpectedBandwidth = 100;
		}
		else {
			double maxDiff = 0;
			double minDiff = Double.POSITIVE_INFINITY;
			List<Double> sortedList = new ArrayList<>(Doubles.asList(sourceX));
			sortedList.sort(null);
			for(int i = 1; i < sortedList.size(); i++)
			{
				double diff = sortedList.get(i) - sortedList.get(i - 1);
				maxDiff = Math.max(maxDiff, diff);
				minDiff = Math.min(minDiff, diff);
			}
			bestGuess = Math.max(0.0001, maxDiff);
			minExpectedBandwidth = minDiff / 5;
			maxExpectedBandwidth = (sortedList.get(sortedList.size() - 1) - sortedList.get(0));					
		}	
		return new DisplayAid(bestGuess, minExpectedBandwidth, maxExpectedBandwidth);
	}

	private static class DisplayAid implements ParameterDisplayAid
	{
		private double bestGuess;
		private double minExpectedBandwidth;
		private double maxExpectedBandwidth;
		
		
		
		public DisplayAid(double bestGuess, double minExpectedBandwidth, double maxExpectedBandwidth) {
			super();
			this.bestGuess = bestGuess;
			this.minExpectedBandwidth = minExpectedBandwidth;
			this.maxExpectedBandwidth = maxExpectedBandwidth;
		}
		
		@Override
		public double bestParameterGuess() {
			return bestGuess;
		}
		@Override
		public double smoothingAmountToParameterValue(double amount) {
			return amount * amount * (maxExpectedBandwidth - minExpectedBandwidth) + minExpectedBandwidth; 			
		}
		@Override
		public double parameterValueToSmoothingAmount(double parameter) {
			return Math.sqrt((parameter - minExpectedBandwidth) / maxExpectedBandwidth);
		}

		
	}
}
