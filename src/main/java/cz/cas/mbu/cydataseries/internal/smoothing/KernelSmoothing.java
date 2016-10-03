package cz.cas.mbu.cydataseries.internal.smoothing;

public class KernelSmoothing {
	
	private static final double SQRT_TWO_PI = Math.sqrt(2 * Math.PI);
	
	private static double kernelFunction(double z)
	{
		return Math.exp(-z * z / 2) / SQRT_TWO_PI;
	}
	
	public static double[] linearKernalEstimator(double[] sourceX, double[] noisyY, double[] estimateX, double bandwidth)
	{
		if(sourceX.length != noisyY.length)
		{
			throw new IllegalArgumentException("sourceX and noisyY must have the same length");
		}
		
		if(bandwidth <= 0)
		{
			throw new IllegalArgumentException("bandwidth has to be positive");
		}
		
		double[] estimateY = new double[estimateX.length];
				
		double[] distanceX = new double[sourceX.length]; //preallocate
		double[] kernelValues = new double[sourceX.length]; //preallocate
		
		for(int estimateIndex = 0; estimateIndex < estimateX.length; estimateIndex++)
		{
			if(!Double.isFinite(estimateX[estimateIndex]))
			{
				estimateY[estimateIndex] = Double.NaN;
				continue;
			}
			
			double s1 = 0;
			double s2 = 0;
			double sumKernel = 0;
			for(int sourceIndex = 0; sourceIndex < sourceX.length; sourceIndex++)
			{
				if(!Double.isFinite(sourceX[sourceIndex]) || !Double.isFinite(noisyY[sourceIndex]))
				{
					continue;
				}
				
				double distance = estimateX[estimateIndex] - sourceX[sourceIndex];
				distanceX[sourceIndex] = distance;
				
				double kernelValue = kernelFunction(distance / bandwidth);				
				kernelValues[sourceIndex] = kernelValue;
				sumKernel += kernelValue;
				
				double weighedDistance = distance * kernelValue;
				s1 += weighedDistance;
				s2 += distance * s1;
			}
			
			double numeratorSum = 0;
			for(int sourceIndex = 0; sourceIndex < sourceX.length; sourceIndex++)
			{
				if(!Double.isFinite(sourceX[sourceIndex]) || !Double.isFinite(noisyY[sourceIndex]))
				{
					continue;
				}
				
				numeratorSum += (s2 - s1 * distanceX[sourceIndex]) * kernelValues[sourceIndex] * noisyY[sourceIndex];
			}
			
			double denominator = s2 * sumKernel - s1 * s1;
			
			estimateY[estimateIndex] = numeratorSum / denominator;			
		}
		
		return estimateY;
		
		/**
		  - Original matlab code this method is based on
		  
		% Gaussian kernel function
		kerf=@(z)exp(-z.*z/2)/sqrt(2*pi);

		r.x=linspace(min(x),max(x),N);
		r.f=zeros(1,N);
		for k=1:N
		    d=r.x(k)-x;
		    z=kerf(d/h);
		    s1=d.*z;
		    s2=sum(d.*s1);
		    s1=sum(s1);
		    r.f(k)=sum((s2-s1*d).*z.*y)/(s2*sum(z)-s1*s1);
		end		
		 */
	}
}
