package de.mz.jk.jsix.math;

import java.util.Collection;

public class XMath 
{
	/** LN(2), useful for LOG2(x) = LN(x)/LN(2) */
	public static final double ln2 = Math.log(2);
	
	/**
	 * calculates log2 from a number<br>
	 * <b>LOG2(x) = LN(x)/LN(2)</b>
	 * @param number
	 * @return log2 from number
	 */
	public static double log2(double number)
	{
		return Math.log(number)/ln2;
	}
	
	/**
	 * get minimum value
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static int min(int a, int b, int c){ return (a<b && a<c) ? a : ((b<c) ? b : c); }
	
	/**
	 * get minimum value
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return
	 */
	public static int min(int a, int b, int c, int d){ int abc = min(a,b,c); return (d<abc) ? d : abc; }
	
	/**
	 * get minimum value
	 * @param vector
	 * @return
	 */
	public static int min(int...vector)
	{ 
		int min=vector[0]; 
		for(int i=0; i<vector.length; i++) if(vector[i]<min) min=vector[i]; 
		return min;
	}
	
	/**
	 * get minimum value
	 * @param Collection of Numbers
	 * @return
	 */
	public static <T extends Number> T min(Collection<T> numbers)
	{ 
		T min = null;
		for ( T v : numbers )
			if (min == null || v.doubleValue() < min.doubleValue()) min = v;
		return min;
	}

	/**
	 * get maximum value
	 * @param Collection of Numbers
	 * @return
	 */
	public static <T extends Number> T max(Collection<T> numbers)
	{
		T max = null;
		for ( T v : numbers )
			if (max == null || v.doubleValue() > max.doubleValue()) max = v;
		return max;
	}

	/**
	 * <h3>{@link NumberRange}</h3>
	 * @author jkuharev
	 * @version Feb 20, 2017 11:41:17 AM
	 * @param <T>
	 */
	public static class NumberRange<T>
	{
		public T min = null;
		public T max = null;
	}

	/**
	 * get min and max value
	 * @param Collection of Numbers
	 * @return
	 */
	public static <T extends Number> NumberRange<T> range(Collection<T> numbers)
	{
		NumberRange<T> res = new NumberRange<>();
		Boolean init = true;
		for ( T v : numbers )
		{
			if (init)
			{
				res.max = v;
				res.min = v;
				init = false;
				continue;
			}
			
			if (v.doubleValue() > res.min.doubleValue()) res.max = v;
			if (v.doubleValue() < res.max.doubleValue()) res.min = v;
		}
		return res;
	}

	/**
	 * summarize two vectors,
	 * vectors must be equally size
	 * @param a
	 * @param b
	 * @return sum vector
	 */
	public static int[] sum(int[] a, int[] b)
	{
		int n = a.length;
		int[] sum = new int[n];
		for(int i=0; i<n; i++) sum[i] = a[i] + b[i];
		return sum;
	}
	
	/**
	 * summarize two vectors,
	 * vectors must have equal sizes
	 * @param a
	 * @param b
	 * @return sum vector
	 */
	public static double[] sum(double[] a, double[] b)
	{
		int n = a.length;
		double[] sum = new double[n];
		for(int i=0; i<n; i++) sum[i] = a[i] + b[i];
		return sum;
	}
	
	/**
	 * summarize two vectors,
	 * vectors must have equal sizes
	 * @param a
	 * @param b
	 * @return sum vector
	 */
	public static float[] sum(float[] a, float[] b)
	{
		int n = a.length;
		float[] sum = new float[n];
		for(int i=0; i<n; i++) sum[i] = a[i] + b[i];
		return sum;
	}
	
	/**
	 * find outer min value occurances in a vector 
	 * @param vector
	 * @return array with the length of two containing first and last index of minimum value
	 */
	public static int[] indexesOfMin(int[] vector)
	{
		int firstMin = 0;
		int lastMin = 0;
		for(int i=1; i<vector.length; i++)
		{
			if( vector[i]<vector[firstMin] )
				firstMin = lastMin = i;
			else 
			if( vector[i]==vector[firstMin] )
				lastMin = i;
		}
		return new int[]{firstMin, lastMin};
	}
}
