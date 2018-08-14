/** ISOQuant, de.mz.jk.jsix.math.interpolation, 08.08.2011*/
package de.mz.jk.jsix.math.interpolation;

import java.util.List;

/**
 * <h3>{@link CosineInterpolator}</h3>
 * @author Joerg Kuharev
 * @version 08.08.2011 14:10:51
 */
public class CosineInterpolator extends Interpolator
{
	private static final long serialVersionUID = 20110808L;

	public CosineInterpolator(List<Double> x, List<Double> y)
	{
		super(x, y);
	}
	
	public CosineInterpolator(List<Double> x, List<Double> y, double minDistX)
	{
		super(x, y, minDistX);
	}
	
	public CosineInterpolator(List<Double> x, List<Double> y, double minX, double minY, double maxX, double maxY)
	{
		super(x, y, minX, minY, maxX, maxY);
	}

	@Override protected double interpolate(double x1, double y1, double x2, double y2, double x)
	{
		double mu = scaledPosition(x1, x2, x);
		double mu2 = (1-Math.cos(mu*Math.PI))/2;
		return(y1*(1-mu2)+y2*mu2);
	}
}
