package de.mz.jk.jsix.math;

import java.util.*;

public class XStat 
{
	/**
	 * get quantil value from ordered numbers
	 * @param percentile
	 * @param numbers list of numbers, numbers have to be sorted ascending
	 * @return value of calculated percentiles
	 */
	public static double getQuantil(double percentile, List<Double> numbers)
	{
		/**
		 * k = p(n+1)/100, p: percentile, k: rank of calculated quantile
		 */
		double ind = percentile*(numbers.size()+1);
		double dec = ind - Math.floor(ind); // number after decimal point 
		double lo = numbers.get((int)ind).doubleValue(); // lower value
		double hi = numbers.get( // higher value
				((int)(ind+1)<numbers.size()) ? (int)(ind+1) : (int)ind
			).doubleValue();
		return lo + (dec*(hi-lo));
	}
	
	/**
	 * get average value
	 * @param values
	 * @return
	 */
	public static double average(double[] values)
	{
		double res = .0;
		for (int i = 0; i < values.length; i++){res+=values[i];}
		return res / values.length;
	}
	
	/**
	 * get median value
	 * @param values
	 * @return
	 */
	public static double median(double[] values)
	{
		double[] copy = new double[values.length];
		for(int i = 0; i < values.length; i++) copy[i] = values[i];
		Arrays.sort(copy);
		int size = copy.length + 1;
		int lmid = (size / 2) - 1;
		int rmid = (size+1)/2 - 1;		
		double median = (copy[lmid] + copy[rmid]) / 2.0;
		return median;
	}
	
	/**
	 * get average value
	 * @param values
	 * @return
	 */
	public static <TYPE extends Number> double average(List<TYPE> values)
	{
		double res = .0;
		for(int i=0; i<values.size(); i++){ res += values.get(i).doubleValue();}
		return res / values.size();
	}
	
	/**
	 * get median value 
	 * @param values
	 * @return
	 */
	public static <TYPE extends Number> double median(List<TYPE> values)
	{		
		List<TYPE> copy = new ArrayList<TYPE>();
		copy.addAll(values);
		Collections.sort(copy, numberComparator);
		int size = copy.size() + 1;
		int lmid = (size / 2) - 1;
		int rmid = (size+1)/2 - 1;		
		double median = (copy.get(lmid).doubleValue() + copy.get(rmid).doubleValue()) / 2.0;
		return median;
	}
	
	/**
	 * ascending comparing of Numbers 
	 */
	public static Comparator<Number> numberComparator = new Comparator<Number>()
	{
		@Override public int compare(Number a, Number b)
		{
			double A = a.doubleValue();
			double B = b.doubleValue();
			if(A > B) return 1;
			if(A < B) return -1;
			return 0;
		}

	};
}
