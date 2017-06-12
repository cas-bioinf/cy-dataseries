package cz.cas.mbu.cydataseries;

import cz.cas.mbu.cydataseries.internal.smoothing.ParameterDisplayAid;

public interface SingleParameterSmoothingProvider {
	public double[] smooth(double[] sourceX, double[] noisyY, double[] estimateX, double parameter);
	/**
	 * Human-friendly name
	 * @return
	 */
	public String getName();
	
	public ParameterDisplayAid getDisplayAid(double[] sourceX);
}
