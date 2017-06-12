package cz.cas.mbu.cydataseries.internal.smoothing;

import java.beans.ParameterDescriptor;
import java.util.Arrays;

import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import cz.cas.mbu.cydataseries.SingleParameterSmoothingProvider;
import cz.cas.mbu.cydataseries.internal.smoothing.SmoothingHelper.SmoothingInput;

public class BSPlineRegressionSmoothingProvider implements SingleParameterSmoothingProvider {

	private final int splineDegree;
		
	
	public BSPlineRegressionSmoothingProvider(int splineDegree) {
		super();
		this.splineDegree = splineDegree;
	}

	/**
	 * @param divisions
	 * @param degree
	 * @param data should be non-decreasing
	 * @return
	 */
	public double[][] createBasis(int divisions, int degree, double[] data) {
		int m = data.length;
		int n = (int) (divisions + degree);
		double[][] basis = new double[m][n];
		double minValue = Arrays.stream(data).reduce(Double.POSITIVE_INFINITY, Math::min);
		double xl = minValue;
		double maxValue = Arrays.stream(data).reduce(Double.NEGATIVE_INFINITY, Math::max);
		double dx = (maxValue - xl) / (double)divisions;
		double[] t = new double[n];
		double[] P = new double[n];
		double[] B = new double[n];
		for (int i = 0; i < m; i++) {
			double rowSum = 0;
			for (int j = 0; j < n; j++) {
				t[j] = xl + (dx * (j - degree));
				P[j] = (data[i] - t[j]) / dx;
				if ((t[j] <= data[i]) & (data[i] < (t[j] + dx))) {
					B[j] = 1;
				} else {
					B[j] = 0;
				}
			}
			for (int k = 1; k <= degree; k++) {
				double B0 = B[0];
				for (int j = 0; j < (n - 1); j++) {
					B[j] = (P[j] * B[j] + (k + 1 - P[j]) * B[j + 1]) / k;
				}
				B[n - 1] = (P[n - 1] * B[n - 1] + (k + 1 - P[n - 1]) * B0) / k;
			}
			for (int j = 0; j < n; j++) {
				rowSum += B[j];
			}
			if (rowSum != 0) {
				for (int j = 0; j < n; j++) {
					basis[i][j] = B[j];
				}
			} else {
				for (int j = 0; j < n; j++) {
					basis[i][j] = basis[0][n - j - 1];
				}
			}
		}
		
		return basis;
	}
	
	@Override
	public double[] smooth(double[] sourceX, double[] noisyY, double[] estimateX, double parameter) {
		int numDivisions = (int)Math.round(parameter);
		
		SmoothingHelper.SmoothingInput filteredInput = SmoothingHelper.filterInvalidValues(sourceX, noisyY);
		sourceX = filteredInput.getSourceX();
		noisyY = filteredInput.getSourceY();

		if(numDivisions >= sourceX.length) {
			throw new IllegalArgumentException("Cannot fit with " + numDivisions + " knots as the input data (after removing NAs) has < " + (numDivisions + 1) + " data points.");
		}

		
		double[][] sourceBasis = createBasis(numDivisions, splineDegree, sourceX);
		RealMatrix matrix = new Array2DRowRealMatrix(sourceBasis);		
		QRDecomposition decomposition = new QRDecomposition(matrix);
		RealVector coefficients = decomposition.getSolver().solve(new ArrayRealVector(noisyY));
		
		double[][] estimateBasis = createBasis((int)parameter, splineDegree, estimateX);
		RealMatrix estimateBasisMatrix = new Array2DRowRealMatrix(estimateBasis);
		
		double[] result = estimateBasisMatrix.transpose().preMultiply(coefficients).toArray();
		return result;
	}

	@Override
	public String getName() {
		return "B-Spline of degree " + splineDegree;
	}

	@Override
	public ParameterDisplayAid getDisplayAid(double[] sourceX) {
		return new DisplayAid(sourceX.length / 2, sourceX.length - 1);
	}
	
	private static class DisplayAid implements ParameterDisplayAid {
		private final int bestKnotsGuess;
		private final int maxKnots;
		
		
		
		public DisplayAid(int bestKnotsGuess, int maxKnots) {
			super();
			this.bestKnotsGuess = bestKnotsGuess;
			this.maxKnots = maxKnots;
		}

		@Override
		public String getParameterName() {
			return "No. of knots";
		}

		@Override
		public double bestParameterGuess() {
			return bestKnotsGuess;
		}

		@Override
		public double smoothingAmountToParameterValue(double amount) {
			return Math.round(amount * (maxKnots - 1)) + 1;
		}

		@Override
		public double parameterValueToSmoothingAmount(double parameter) {			
			return (parameter - 1) / (double)(maxKnots - 1);
		}
		
	}

}
