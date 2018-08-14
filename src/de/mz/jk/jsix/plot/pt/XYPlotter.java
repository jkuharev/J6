/** ISOQuant, de.mz.jk.jsix.plot, 08.07.2011*/
package de.mz.jk.jsix.plot.pt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import ptolemy.plot.Plot;
import ptolemy.plot.PlotApplication;

/**
 * <h3>{@link XYPlotter}</h3>
 * @author Joerg Kuharev
 * @version 08.07.2011 16:20:57
 */
public class XYPlotter implements Serializable
{
	private static final long serialVersionUID = 20110713L;

	/**
	 * available point sizes (from small to large):
	 * none, pixels, points, dots, various
	 * <h3>{@link PointStyle}</h3>
	 * @author Joerg Kuharev
	 * @version 08.07.2011 17:06:37
	 */
	public static enum PointStyle
	{
		/** no points at all */ 
		none,
		/** very small */
		pixels,
		/** medium sized */
		points,
		/** big */
		dots,
		/** bigger */
		bigdots,
		/** large */
		various
	}
	
	protected String plotTitle = "";
	protected String XAxisLabel = "x";
	protected String YAxisLabel = "y";
	protected String pointStyle = PointStyle.pixels.toString();
	
	protected int pointSeries = 0;
	protected Plot plot = null;
	protected boolean initialized = false;
	
	private int winWidth=800;
	private int winHeight=600;
	
	public XYPlotter(){}
	public XYPlotter(int winWidth, int winHeight){ setWinSize(winWidth, winHeight); }
	
	protected void initPlot()
	{
		if(plot==null){ plot = new Plot(); }
		
		plot.setTitle( plotTitle );
		plot.setXLabel( XAxisLabel );
		plot.setYLabel( YAxisLabel );
		plot.setMarksStyle( pointStyle );
		
		PlotApplication app = new PlotApplication( plot );
		app.setSize(winWidth, winHeight);
		
		// work around for not killing all on closing window
		app.removeWindowListener( app.getWindowListeners()[0] );
		app.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		initialized = true;
	}
	
	public void setWinSize(int winWidth, int winHeight)
	{
		setWinWidth(winWidth);
		setWinHeight(winHeight);
	}
	
	public int getWinWidth()
	{
		return winWidth;
	}

	public void setWinWidth(int winWidth)
	{
		this.winWidth = winWidth;
	}

	public int getWinHeight()
	{
		return winHeight;
	}

	public void setWinHeight(int winHeight)
	{
		this.winHeight = winHeight;
	}

	/** @return assosiated plot object */
	public Plot getPlot()
	{
		if(plot==null) plot = new Plot();
		return plot;
	}
	
	/**
	 * @param plotTitle the plotTitle to set
	 */
	public synchronized void setPlotTitle(String plotTitle)
	{
		this.plotTitle = plotTitle;
	}
	
	/**
	 * @param xAxisLabel the xAxisLabel to set
	 */
	public synchronized void setXAxisLabel(String xAxisLabel)
	{
		this.XAxisLabel = xAxisLabel;
	}
	
	/**
	 * @param yAxisLabel the yAxisLabel to set
	 */
	public synchronized void setYAxisLabel(String yAxisLabel)
	{
		this.YAxisLabel = yAxisLabel;
	}
	
	/**
	 * set point style to one of:<br>
	 * 	points, pixels, dots, bigdots, various, none
	 * @param pointStyle the pointStyle to set
	 */
	public synchronized void setPointStyle(String pointStyle)
	{
		this.pointStyle = pointStyle;
	}
	
	/**
	 * set point style to one of:<br>
	 * 	points, pixels, dots, bigdots, various, none
	 * @param pointStyle the pointStyle to set
	 */
	public void setPointStyle(PointStyle pointStyle)
	{
		this.pointStyle = pointStyle.toString();
	}
	
	/**
	 * plot XY data
	 * @param X
	 * @param Y
	 * @param connectPoints connect points to lines
	 * @return plotID
	 */
	public synchronized <TYPE1 extends Number, TYPE2 extends Number> int plotXY(List<TYPE1> X, List<TYPE2> Y, String name, boolean connectPoints)
	{
		if(!initialized) plot = new Plot();
		
		int res = ++pointSeries;
		
		addPoints(res, X, Y, connectPoints);
		if(name!=null && name.length()>0) setLegend( res, name );
		
		return res;
	}
	
	/**
	 * plot list of values against their order in this list
	 * @param Y
	 * @param name
	 * @param connectPoints
	 * @return
	 */
	public synchronized <TYPE extends Number> int plotY(List<TYPE> Y, String name, boolean connectPoints)
	{
		List<Integer> X = new ArrayList<Integer>(Y.size());
		for(int i=0;i<Y.size(); i++) X.add(i);
		return plotXY(X, Y, name, connectPoints);
	}
	
	/**
	 * set the legend label for a particular plot
	 * @param label
	 * @param plotID
	 */
	public synchronized void setLegend(int plotID, String label)
	{
		if(!initialized) plot = new Plot();

		plot.addLegend( plotID, label );
		
		if(!initialized) 
			initPlot(); 
		else 
			plot.updateUI();
	}
	
	/**
	 * add XY data to existing plot
	 * @param X
	 * @param Y
	 * @param connectPoints connect points to lines
	 * @return index of current point series
	 */
	public synchronized <TYPE1 extends Number, TYPE2 extends Number> void addPoints(int plotID, List<TYPE1> X, List<TYPE2> Y, boolean connected)
	{
		if(!initialized) plot = new Plot();
		int n = Math.min(X.size(), Y.size());
		
		for(int i=0; i<n; i++)
		{
			plot.addPoint( plotID, X.get(i).doubleValue(), Y.get(i).doubleValue(), connected );
		}
		
		if(!initialized) 
			initPlot(); 
		else 
			plot.updateUI();
	}
	
	/**
	 * add a single point to plot
	 * @param x
	 * @param y
	 * @param plotID
	 * @param connected
	 */
	public synchronized void addPoint(int plotID, double x, double y, boolean connected)
	{
		if(!initialized) plot = new Plot();
		
		plot.addPoint( plotID, x, y, connected );
		
		if(!initialized) 
			initPlot(); 
		else 
			plot.updateUI();
	}
	
	/**
	 * plot XY data
	 * @param X
	 * @param Y
	 * @param connectPoints connect points to lines
	 */
	public synchronized void plotXY(double[] X, double[] Y, String name, boolean connectPoints)
	{
		if(!initialized) plot = new Plot();
		pointSeries++;
		int n = Math.min(X.length, Y.length);
		for(int i=0; i<n; i++)
		{

			plot.addPoint( pointSeries, X[i], Y[i], connectPoints );
		}
		plot.addLegend( pointSeries, name );
		if(!initialized) initPlot(); else plot.updateUI();
	}
}
