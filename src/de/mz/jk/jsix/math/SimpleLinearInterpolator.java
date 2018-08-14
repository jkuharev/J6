package de.mz.jk.jsix.math;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * linear interpolation between neighbors of point series<br>
 * @author Jšrg Kuharev
 */
public class SimpleLinearInterpolator implements Serializable
{
	private static final long serialVersionUID = 201107071410L;
	
	protected List<Double> X = new ArrayList<Double>();
	protected List<Double> Y = new ArrayList<Double>();
	
	protected int size = 0;
	protected boolean defBounds = false; // true if bounds are defined
	
	protected double minX = 0.0;
	protected double maxX = 0.0;
	
	protected int lXi = 0;
	
	final public double defaultMinDistanceBetweenXValues = 0.000001;
	private double minDistBetweenX = defaultMinDistanceBetweenXValues;
	
	private SimpleLinearInterpolator(){}
	
	/**
	 * construct polygon interpolation from X an Y values.
	 * e.g. x = 1, 2, 3, 4; y = 1.0, 1.2, 1.4, 1.8;<br> 
	 * describing polygon through points (x, y) = (1, 1), (2, 1.2), (3, 1.4), (4, 1.8)
	 * @param x values (must be in increasing order, duplicate x values will be removed)
	 * @param y values (corresponding y values must have identical indexes as their x-values) 
	 */
	public SimpleLinearInterpolator(List<Double> x, List<Double> y) 
	{
		init(x,y);
	}

	/**
	 * construct polygon interpolation from X an Y values.
	 * e.g. x = 1, 2, 3, 4; y = 1.0, 1.2, 1.4, 1.8;<br> 
	 * describing polygon through points (x, y) = (1, 1), (2, 1.2), (3, 1.4), (4, 1.8)
	 * and ensure the given minimum distance between x values
	 * @param x values (must be in increasing order, duplicate x values will be removed)
	 * @param y values (corresponding y values must have identical indexes as their x-values) 
	 * @param minDistX
	 */
	public SimpleLinearInterpolator(List<Double> x, List<Double> y, double minDistX) 
	{
		this.minDistBetweenX = minDistX;
		init(x,y);
	}	
	
	/**
	 * construct polygon interpolation from X an Y values.
	 * e.g. x = 1, 2, 3, 4; y = 1.0, 1.2, 1.4, 1.8;<br> 
	 * describing polygon through points (x, y) = (1, 1), (2, 1.2), (3, 1.4), (4, 1.8)<br>
	 * additional points (boundaries) are added at the beginning and at the end of polygon for enlarging
	 * valid x/y-value ranges
	 * @param x values (must be in increasing order, duplicate x values will be removed)
	 * @param y values (corresponding y values must have identical indexes as their x-values)
	 * @param minX left bound x,  must be lower than minimum x value 
	 * @param minY left bound y value
	 * @param maxX right bound x, must be greater than maximum x value 
	 * @param maxY right bound y value
	 */
	public SimpleLinearInterpolator(List<Double> x, List<Double> y, double minX, double minY, double maxX, double maxY) 
	{
		init(x,y);
		addBounds(minX, minY, maxX, maxY);
	}
	
	/** @return all known x values (including bounds) */
	public List<Double> getAllX(){ return X; }
	
	/** @return all known y values (including bounds) */
	public List<Double> getAllY(){ return Y; }
	
	/**
	 * @param x
	 * @param y
	 */
	private void init(List<Double> x, List<Double> y)
	{
		this.X = x;
		this.Y = y;
		
		// determine size
		size = Math.min( x.size(), y.size() );
		
		// trim to size
		if(X.size()>size) X=X.subList(0, size);
		if(Y.size()>size) Y=Y.subList(0, size);
		
		removeGaps();
		removeMultipleX( minDistBetweenX );
		
		minX = X.get(0);
		maxX = X.get(size-1);
	}

	/**
	 * add lowest and highest known values,<br>
	 * useful are lowest: 0, 0 and 
	 * highest: infinity, infinity<br>  
	 * e.g. addBounds(0, 0, 10000, 10000);
	 * @param minX
	 * @param minY
	 * @param maxX
	 * @param maxY
	 */
	public void addBounds(double minX, double minY, double maxX, double maxY)
	{
		// remove old bounds if defined
		if(defBounds)
		{
			X.remove(X.size()-1);
			X.remove(0);
			Y.remove(Y.size()-1);
			Y.remove(0);
		}
		
		// add bounds
		X.add(0, minX);
		Y.add(0, minY);
		X.add(maxX);
		Y.add(maxY);
		
		// renew size
		size = X.size();

		// mark bounds as defined
		defBounds = true;
		
		minX = X.get(0);
		maxX = X.get(size-1);
	}

	/**
	 * remove multiple X values<br>
	 * leave one of multiple X and assign average to Y 
	 * if delta between neighbor x values is less than xDist
	 * @param minDistX minimum distance to be ensured between neighbor x values
	 */
	public void removeMultipleX(double minDistX) 
	{
		for(int i=0; i<size && i<X.size(); i++)
		{
			double lY = Y.get(i);
			double rY = Y.get(i);
 
			// index of right neighbor
			int j=i+1;

			// while j<size and Xi==Xj 
			while( j<size && ( (X.get(j) - X.get(i) ) < minDistX ) )
			{
				// remember rY
				rY = Y.get(j);

				// remove right neighbor X and Y values 
				X.remove(j);
				Y.remove(j);
				
				// renew size
				size = X.size();
			}
			
			if(lY!=rY)
			{
				// average Y
				double meanY = (rY + lY)/2.0;
				Y.set( i, meanY ); // ( meanY<rY ) ? meanY : lY );
			}
		}
		
		// renew size
		size = X.size();
	}
	
	/**
	 * ensure minimum given distance between Y neighbor values
	 * @param minDistY minimum distance to be ensured between neighbor y values
	 */
	public void removeMultipleY(double minDistY) 
	{
		for(int i=0; i<size && i<Y.size(); i++)
		{
			double lX = X.get(i);
			double rX = lX;
 
			// index of right neighbor
			int j=i+1;

			// while j<size and Xi==Xj 
			while( j<size && ( Math.abs( Y.get(j) - Y.get(i) ) < minDistY ) )
			{
				// remember rY
				rX = X.get(j);

				// remove right neighbor X and Y values 
				X.remove(j);
				Y.remove(j);
				
				// renew size
				size = X.size();
			}
			
			if(lX!=rX)
			{
				// average X
				double meanX = (rX + lX)/2.0;
				X.set( i, meanX );
			}
		}
		
		// renew size
		size = X.size();
	}
	
	/**
	 * remove entry pairs where X or Y is null<br> 
	 */
	protected void removeGaps() 
	{
		for(int i=0; i<X.size(); i++)
		{
			// if X or Y is null then remove pair
			if( X.get(i) == null || Y.get(i) == null )
			{
				// remove current
				X.remove(i);
				Y.remove(i);
				// go back due to i++ in for-loop head
				i--; 
			}
		}
		
		// renew size
		size = X.size();
	}
	
	/**
	 * calculate y(x)<br>
	 * by linear interpolation between known Y,X pairs<br>
	 * y(x) is calculated as a linear combination of known end points<br>
	 * linear combination of end points
	 * f(x) = y0 + (dy * (x - x0)) / dx 
	 * 		= f0 + ( [f1 + f0] / [x1 - x0] ) * [x - x0]
	 * @param x
	 * @return y(x)
	 */
	public double getY(double x)
	{
		int rXi = X.indexOf( (Double)x );
		
		// entry found directly
		if(rXi>-1) return Y.get(rXi);
		
		// find right neighbor
		for( 
			rXi=( X.get(lXi) < x ) ? lXi : 0;	// speed up sequential requests by starting searching at last left neighbor 
			rXi < size && X.get(rXi) < x ;		// while right neighbors index < size and value < x
			rXi++								// move right to next one
		){/* do nothing */}
		
		// y = Y0 if x < X0
		if( rXi==0 && x < X.get(0) ) return Y.get(0);
		
		// y = Yn if x > Xn
		if( rXi >= size ) return Y.get( Y.size()-1 );
		
		// set left neighbor's index
		lXi = rXi - 1;
		
		// y(x) = y0 + (x - x0) * (y1 - y0) / (x1 - x0)
		double res = Y.get(lXi) + ( ( x - X.get(lXi) ) * ( Y.get(rXi) - Y.get(lXi) ) ) / ( X.get(rXi) - X.get(lXi) );
		
		return res;
	}
	
	/** 
	 * number of contained points 
	 * @return the number of points used for interpolation
	 */
	public int getSize(){ return size; }
	
	/**
	 * 
	 * @param li
	 * @param file
	 * @throws Exception 
	 */
	public static void serialize(SimpleLinearInterpolator li, File file) throws Exception
	{
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		
		try
		{
			fos = new FileOutputStream( file );
			oos = new ObjectOutputStream( fos );
			oos.writeObject( li );
			oos.flush();
		} 
		finally
		{
			if(oos!=null) oos.close();
			if(fos!=null) fos.close();			
		}	
	}
	
	public static void serializeToCSV(SimpleLinearInterpolator ip, File csvFile) throws Exception
	{
		PrintStream out = new PrintStream( csvFile );
		for(int i=0; i<ip.size; i++)
		{
			out.println( ip.X.get(i) +"\t"+ip.Y.get(i) );
		}
		out.flush();
		out.close();
	}
	
	public static SimpleLinearInterpolator unserializeFromCSV(File csvFile) throws Exception
	{
		SimpleLinearInterpolator ip = new SimpleLinearInterpolator();
		BufferedReader in = new BufferedReader(new FileReader(csvFile));
		for(String line=""; (line=in.readLine())!=null; )
		try
		{
			String[] cells = line.split("\t");
			Double x = Double.parseDouble(cells[0]);
			Double y = Double.parseDouble(cells[1]);
			ip.X.add( x );
			ip.Y.add( y );
		} catch (Exception e) {}
		
		ip.size = ip.X.size();
		ip.minX = ip.X.get(0);
		ip.maxX = ip.X.get(ip.size-1);
		
		return ip;
	}
	
	public static SimpleLinearInterpolator unserialize(File file) throws Exception
	{
		SimpleLinearInterpolator li = null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		
		try
		{
			fis = new FileInputStream( file );
			ois = new ObjectInputStream( fis );
			li = (SimpleLinearInterpolator) ois.readObject();
		} 
		finally
		{
			if(ois!=null) ois.close();
			if(fis!=null) fis.close();			
		}
		
		return li;
	}
	
	/**
	 * determine median Y from multiple interpolator functions
	 * @param functions
	 * @param x
	 * @return
	 */
	public static double getMedianY(List<SimpleLinearInterpolator> functions, double x) 
	{
		switch( functions.size() )
		{
			case 0: 
				return x;
			case 1: 
				return functions.get(0).getY(x);
			case 2:
				// reducing overhead by manually calculating average of 2 values
				return (functions.get(0).getY(x) + functions.get(1).getY(x))/2.0;
			default:
				double[] fxs = new double[functions.size()];
				
				for(int i=0; i<functions.size(); i++)
				{
					fxs[i] = functions.get(i).getY( x );
				}
				
				// sort times into ascending order 
				Arrays.sort(fxs);
				
				// median calculated by: sum of two middle values / 2

				/*
					fxs[] = {0,1,2,3}
					-> size = 4 + 1 = 5
					-> lmid = 5/2 - 1 = (int)2.5 - 1 = 2 - 1 = 1
					-> rmid = 6/2 - 1 = (int)3.0 - 1 = 3 - 1 = 2
					median = (1 + 2) / 2.0
				*/
				int size = fxs.length + 1;
				int lmid = (size / 2) - 1;
				int rmid = (size+1)/2 - 1;		
				double median = (fxs[lmid] + fxs[rmid]) / 2.0;
				return median;
		}
	}
	
	/**
	 * determine average Y from multiple interpolator functions
	 * @param functions
	 * @param x
	 * @return
	 */
	public static double getAverageY(List<SimpleLinearInterpolator> functions, double x) 
	{
		switch( functions.size() )
		{
			case 0: 
				return x;
			case 1: 
				return functions.get(0).getY(x);
			default:
				double sum = 0.0;
				for(int i=0; i<functions.size(); i++) sum += functions.get(i).getY( x );
				return sum / functions.size();
		}
	}
}
