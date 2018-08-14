/** ISOQuant, de.mz.jk.jsix.math.interpolation, 08.08.2011*/
package de.mz.jk.jsix.math.interpolation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <h3>{@link LinearInterpolator}</h3>
 * @author Joerg Kuharev
 * @version 08.08.2011 14:09:42
 */
public class LinearInterpolator extends Interpolator
{
	private static final long serialVersionUID = 20110808L;

	public LinearInterpolator(List<Double> x, List<Double> y)
	{
		super(x, y);
	}
	
	public LinearInterpolator(List<Double> x, List<Double> y, double minDistX)
	{
		super(x, y, minDistX);
	}
	
	public LinearInterpolator(List<Double> x, List<Double> y, double minX, double minY, double maxX, double maxY)
	{
		super(x, y, minX, minY, maxX, maxY);
	}
	
	@Override protected double interpolate(double x1, double y1, double x2, double y2, double x)
	{
		return  y1 + (x - x1) * (y2 - y1) / (x2 - x1);
	}
	
	
	public static void serializeToCSV(Interpolator ip, File csvFile) throws Exception
	{
		PrintStream out = new PrintStream( csvFile );
		for(int i=0; i<ip.size; i++)
		{
			out.println( ip.X.get(i) +"\t"+ip.Y.get(i) );
		}
		out.flush();
		out.close();
	}
	
	public static Interpolator unserializeFromCSV(File csvFile) throws Exception
	{
		BufferedReader in = new BufferedReader(new FileReader(csvFile));
		List<Double> X = new ArrayList<Double>();
		List<Double> Y = new ArrayList<Double>();
		for(String line=""; (line=in.readLine())!=null; )
		try
		{
			String[] cells = line.split("\t");
				X.add( Double.parseDouble( cells[0] ) );
				Y.add( Double.parseDouble( cells[1] ) );
		}
		catch (Exception e)
		{}
		return new LinearInterpolator( X, Y );
	}
}
