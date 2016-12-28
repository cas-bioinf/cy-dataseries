package cz.cas.mbu.cydataseries.internal.smoothing;

import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

public class LinearSmoothingProvider implements SingleParameterSmoothingProvider {

	@Override
	public double[] smooth(double[] sourceX, double[] noisyY, double[] estimateX, double parameter) {

		if(sourceX.length < 2)
		{
			return new double[sourceX.length];
		}
		
		LinearInterpolator interpolator = new LinearInterpolator();		
		PolynomialSplineFunction estimateFunc = interpolator.interpolate(sourceX, noisyY);
		double[] result = new double[estimateX.length];
		for(int i =0; i < estimateX.length;i++)
		{
			if(estimateFunc.isValidPoint(estimateX[i]))
			{
				result[i] = estimateFunc.value(estimateX[i]);
			}
			else
			{
				result[i] = Double.NaN;
			}
		}
		return result ;
	}

	@Override
	public String getName() {
		return "Linear (no parameter)";
	}

	@Override
	public ParameterDisplayAid getDisplayAid(double[] sourceX) {
		return null; //No aid necessary
	}

}
