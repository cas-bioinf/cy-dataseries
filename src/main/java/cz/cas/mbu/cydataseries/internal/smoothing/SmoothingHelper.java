package cz.cas.mbu.cydataseries.internal.smoothing;

public class SmoothingHelper {
	public static SmoothingInput filterInvalidValues(double[] sourceX, double[] sourceY)
	{
		if(sourceX.length != sourceY.length) {
			throw new IllegalArgumentException("Inputs must have the same length");
		}
		int finalLength = 0;
		for(int i = 0; i < sourceX.length; i++) {
			if(Double.isFinite(sourceX[i]) && Double.isFinite(sourceY[i])) {
				finalLength++;
			}
		}
		
		if(finalLength == sourceX.length) {
			return new SmoothingInput(sourceX, sourceY);
		} else {
			double[] resultX = new double[finalLength];
			double[] resultY = new double[finalLength];
			int nextIndex = 0;
			for(int i = 0; i < sourceX.length; i++) {
				if(Double.isFinite(sourceX[i]) && Double.isFinite(sourceY[i])) {
					resultX[nextIndex] = sourceX[i];
					resultY[nextIndex] = sourceY[i];
					nextIndex++;
				}
			}
			return new SmoothingInput(sourceX, sourceY);
		}
		
	}
	
	public static class SmoothingInput {
		private final double[] sourceX;
		private final double[] sourceY;
		
		public SmoothingInput(double[] sourceX, double[] sourceY) {
			super();
			this.sourceX = sourceX;
			this.sourceY = sourceY;
		}
		public double[] getSourceX() {
			return sourceX;
		}
		public double[] getSourceY() {
			return sourceY;
		}
		
		
		
	}
}
