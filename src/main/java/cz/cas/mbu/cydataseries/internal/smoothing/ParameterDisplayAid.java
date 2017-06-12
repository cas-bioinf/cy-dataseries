package cz.cas.mbu.cydataseries.internal.smoothing;

public interface ParameterDisplayAid {
	public String getParameterName();
	
	public double bestParameterGuess();
	
	/**
	 * 
	 * @param amount a value in 0..1
	 * @return a value interpretable by the algorithm
	 */
	public double smoothingAmountToParameterValue(double amount);
	
	/**
	 * Takes a parameter value interpretable by the algorithm and transforms it into the 0..1 range for visual aid
	 * @param parameter
	 * @return
	 */
	public double parameterValueToSmoothingAmount(double parameter);
}
