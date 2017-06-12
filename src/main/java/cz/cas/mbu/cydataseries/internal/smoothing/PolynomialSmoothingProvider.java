package cz.cas.mbu.cydataseries.internal.smoothing;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import cz.cas.mbu.cydataseries.SingleParameterSmoothingProvider;

public class PolynomialSmoothingProvider implements SingleParameterSmoothingProvider {
	
	
	@Override
	public double[] smooth(double[] sourceX, double[] noisyY, double[] estimateX, double parameter) {
		int degree = (int)Math.round(parameter);
		if(degree < 1 || degree > sourceX.length) {
			throw new IllegalArgumentException("Degree must be between 1 and number of data points.");
		}
		if(sourceX.length != noisyY.length) {
			throw new IllegalArgumentException("X and Y input must have the same length.");
		}
		
		
		PolynomialCurveFitter curveFitter = PolynomialCurveFitter.create(degree).withMaxIterations(10000);
		WeightedObservedPoints points = new WeightedObservedPoints();
		for(int i = 0; i < sourceX.length; i++) {
			points.add(sourceX[i], noisyY[i]);
		}
		PolynomialFunction func = new PolynomialFunction(curveFitter.fit(points.toList()));
		
		double[] result = new double[estimateX.length];
		for(int i = 0; i < estimateX.length; i++) {
			result[i] = func.value(estimateX[i]);
		}
		return result;
	}

	@Override
	public String getName() {		
		return "Polynomial";
	}

	@Override
	public ParameterDisplayAid getDisplayAid(double[] sourceX) {
		return new ParameterDisplayAid() {
			
			@Override
			public String getParameterName() {
				return "Degree";
			}

			@Override
			public double smoothingAmountToParameterValue(double amount) {				
				return Math.round(((sourceX.length - 1) * amount) + 1);
			}
			
			@Override
			public double parameterValueToSmoothingAmount(double parameter) {
				return (parameter - 1) / (sourceX.length - 1);
			}
			
			@Override
			public double bestParameterGuess() {
				return sourceX.length / 2;
			}
		};
	}
}
