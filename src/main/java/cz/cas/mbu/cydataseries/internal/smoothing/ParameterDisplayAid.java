package cz.cas.mbu.cydataseries.internal.smoothing;

public interface ParameterDisplayAid {
	public double bestParameterGuess();
	
	public double smoothingAmountToParameterValue(double amount);
	public double parameterValueToSmoothingAmount(double parameter);
}
