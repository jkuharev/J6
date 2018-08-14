/** ISOQuant, de.mz.jk.jsix.math, 11.07.2011*/
package de.mz.jk.jsix.math;

import java.util.ArrayList;
import java.util.List;

/**
 * simple moving average smoothing methods
 * <h3>{@link SimpleMovingAverage}</h3>
 * @author Joerg Kuharev
 * @version 11.07.2011 16:23:21
 */
public class SimpleMovingAverage
{
	/*
	public static void main(String[] args)
	{
		double[] a = {1,2,10,2,8,3,5,3,3,9,8,7,4,3,3,2,2,1};
		double[] b = smooth(a, 4);
		double[] x = new double[a.length];
		
		for(int i=0; i<a.length; i++)
		{
			x[i] = i;
			System.out.println(
				i + "\t" +
				(int)a[i] + "\t" +
				Math.round(b[i]*100.0)/100.0
			);
		}
		
		XYPlotter p = new XYPlotter();
		p.plotXY(x, a, "original", true);
		p.plotXY(x, b, "smoothed", true);
	}
*/
	

	/**
	 * simple moving average smoothing
	 * @param data input data
	 * @param radius half the number of neighbors around of current position used for sma calulations
	 * @return smoothed data
	 */
	public static double[] smooth(double[] data, int radius)
	{			
		double[] res = new double[data.length];
		
		int firstValidIndex = radius;
		int lastValidIndex = data.length - radius - 1;
		
		int size = radius*2+1;
		double sum = 0;
		
		int leftIndex=0;
		int rightIndex=size;
		
		for(int i=0; i<data.length; i++)
		{
			if( i<firstValidIndex || i>lastValidIndex )
			{
				// take original values
				res[i] = data[i];
				continue;
			}
			else if(i==firstValidIndex)
			{ 
				// initial SMA calculation
				for(int j=leftIndex; j<rightIndex; j++)
				{
					sum += data[j];
				}
			}
			else
			{
				// remove previous left
				sum -= data[leftIndex];
				
				// add next right
				sum += data[rightIndex];

				// step forward
				leftIndex++;
				rightIndex++;				
			}
			
			res[i] = sum / size;
		}
		
		return res;
	}
	
	/**
	 * simple moving average smoothing
	 * @param <TYPE>
	 * @param data input data
	 * @param radius half the number of neighbors around of current position used for sma calulations
	 * @return
	 */
	public static <TYPE extends Number> List<Double> smooth(List<TYPE> data, int radius)
	{			
		List<Double> res = new ArrayList<Double>(data.size());
		
		int firstValidIndex = radius;
		int lastValidIndex = data.size() - radius - 1;
		
		int size = radius*2+1;
		double sum = 0;
		
		int leftIndex=0;
		int rightIndex=size;
		
		for(int i=0; i<data.size(); i++)
		{
			if( i<firstValidIndex || i>lastValidIndex )
			{
				// take original values
				res.add( data.get(i).doubleValue() );
				continue;
			}
			else if(i==firstValidIndex)
			{ 
				// initial SMA calculation
				for(int j=leftIndex; j<rightIndex; j++) sum += data.get(j).doubleValue();
			}
			else
			{
				// remove previous left
				sum -= data.get(leftIndex).doubleValue();
				
				// add next right
				sum += data.get(rightIndex).doubleValue();

				// step forward
				leftIndex++;
				rightIndex++;				
			}
			
			res.add( sum / size );
		}
		
		return res;
	}
}
