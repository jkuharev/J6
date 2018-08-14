package de.mz.jk.jsix.math;
import java.util.ArrayList;
import java.util.List;

/**
 * Kernel Density Estimation like descrbed in
 * {@link http://en.wikipedia.org/wiki/Kernel_density_estimation} 
 * implementing the usage of common (simplified) kernels like described in 
 * {@link http://en.wikipedia.org/wiki/Kernel_%28statistics%29} 
 * <h3>{@link KernelDensityEstimator}</h3>
 * @author kuharev
 * @version 05.12.2011 10:33:50
 */
public class KernelDensityEstimator 
{
	private double[] X;
	private double h;
	
	private double minX = Double.MAX_VALUE, maxX=Double.MIN_VALUE;

	private double sqrtPI = Math.sqrt(2.0 * Math.PI);
	public static final double halfPI = Math.PI / 2.0;
	public static final double quarterPI = Math.PI / 4.0;
	public static final double threeQuarter = 3.0 / 4.0; 

	private int kernel = GAUSSIAN;

	public KernelDensityEstimator(double[] X, double h)
	{
		setXH(X, h);
	}
	
	private void setXH(double[] X, double h)
	{
		this.X = X;
		this.h = h;
		for(int i=0; i<X.length; i++)
		{
			if(X[i]>maxX) maxX=X[i];
			if(X[i]<minX) minX=X[i];
		}
	}

	public <TYPE extends Number> KernelDensityEstimator(List<TYPE> X, double h)
	{
		int n = X.size();
		this.h = h;
		double[] x = new double[n];
		for(int i=0; i<n; i++) 
		{
			x[i] = X.get(i).doubleValue();
		}
		setXH(x, h);
	}
	
	public double getMinX(){return minX;}
	public double getMaxX(){return maxX;}
	
	/**
	 * @param kernel the kernel code to be used
	 */
	public void useKernel(int kernel)
	{
		this.kernel = kernel;
	}
	
	/**
	 * estimate density at a single position x
	 * @param x
	 * @return
	 */
	public double getDensityAt( double x )
	{
		double f = 0.0;
		for (int i = 0; i < X.length; i++) 
		{
			f += k( (x-X[i]) / h );
		}
		double density = f / h / (double) X.length ;
		return density;
	}

	/**
	 * estimate density at a collection of x values
	 * @param xValues
	 * @return
	 */
	public <TYPE extends Number> List<Double> getDensities(List<TYPE> xValues)
	{
		List<Double> res = new ArrayList<Double>(xValues.size());
		for(int i=0; i<xValues.size(); i++)
		{
			res.add( getDensityAt( xValues.get(i).doubleValue() ) );
		}
		return res;
	}
	
	private double k(double t)
	{
		switch(kernel) 
		{
			case EPANECHNIKOV:
				return epanechnikov(t);
			case UNIFORM:
				return uniform(t);
			case TRIANGULAR:
				return triangular(t);
			case QUADRATIC:
				return quadratic(t);
			case BIWEIGHT:
				return biweight(t);
			case TRIWEIGHT:
				return triweight(t);
			case COSINE:
				return cosine(t);
			case GAUSSIAN:
			default:
				return gaussian(t);
		}
	}

	static final int GAUSSIAN = 0;
	static final int EPANECHNIKOV = 1;
	static final int UNIFORM = 2;
	static final int TRIANGULAR = 3;
	static final int QUADRATIC = 4;
	static final int BIWEIGHT = 5;
	static final int TRIWEIGHT = 6;
	static final int COSINE = 7;

	public String whatKernel()
	{
		switch (kernel) {
			case 1:
				return new String("Epanechnikov");
			case 2:
				return new String("Uniform");
			case 3:
				return new String("Triangular");
			case 4:
				return new String("Quadratic");
			case 5:
				return new String("Biweight");
			case 6:
				return new String("Triweight");
			case 7:
				return new String("Cosine");
			default:
				return new String("Gaussian");
		}
	}

	private double gaussian(double x)
	{
		return Math.pow( Math.E, x * x / -2.0d ) / sqrtPI;
	}
	
	private static double w5 = Math.sqrt(5.0);
	private static double w5xThreeQuarter = w5/threeQuarter;
	private double epanechnikov(double x)
	{
		if (((x < -w5 ? 1 : 0) | (x > w5 ? 1 : 0)) != 0) { return 0.0; }
		return w5xThreeQuarter * (1.0D - 0.2000000029802322D * x * x);
	}

	private double triangular(double x)
	{
		if (((x < -1.0 ? 1 : 0) | (x > 1.0 ? 1 : 0)) != 0) { return 0.0F; }
		return 1.0 - Math.abs(x);
	}

	private double quadratic(double x)
	{
		if (((x < -1.0 ? 1 : 0) | (x > 1.0 ? 1 : 0)) != 0) { return 0.0F; }
		return (double)(0.75D * (1.0D - x*x));
	}

	private double uniform(double x)
	{
		if (((x < -1.0 ? 1 : 0) | (x > 1.0 ? 1 : 0)) != 0) {
			return 0.0F;
		}
		return 0.5F;
	}

	private double biweight(double x)
	{
		if (((x < -1.0 ? 1 : 0) | (x > 1.0 ? 1 : 0)) != 0) { return 0.0F; }
		double d = (double)(1.0D - x*x);
		return (double)(0.954929658551372D * x*x);
	}

	private double triweight(double x)
	{
		if (((x < -1.0 ? 1 : 0) | (x > 1.0 ? 1 : 0)) != 0) { return 0.0F; }
		double d = (double)(1.0D - x*x);
		return (double)(1.09375D * Math.pow(d, 3.0D));
	}
	
	private double cosine(double x)
	{
		if (((x < -1.0 ? 1 : 0) | (x > 1.0 ? 1 : 0)) != 0) { return 0.0; }
		return quarterPI * Math.cos( x * halfPI );
	}
}
