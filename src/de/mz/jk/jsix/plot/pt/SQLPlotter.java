/** MySQLPlotter, plot, 09.05.2011*/
package de.mz.jk.jsix.plot.pt;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import de.mz.jk.jsix.mysql.MySQL;

import ptolemy.plot.Plot;

/**
 * <h3>{@link SQLPlotter}</h3>
 * @author Joerg Kuharev
 * @version 09.05.2011 12:34:37
 */
public class SQLPlotter extends XYPlotter
{
	private MySQL db = null;

	public SQLPlotter(MySQL db)
	{
		setDB(db);
	}
	
	public SQLPlotter(MySQL db, int winWidth, int winHeight)
	{
		super(winWidth, winHeight);
		setDB(db);
	}	
	
	/**
	 * @param db
	 */
	private void setDB(MySQL db)
	{
		this.db = db.clone();
		this.db.getConnection();
	}

	/**
	 * plot XY data from sql by setting 
	 * x-values from first requested column and
	 * y-values from all following columns
	 * @param sql
	 * @param connectPoints connect points to lines
	 * @throws Exception
	 */
	public void plot(String sql, boolean connectPoints) 
	{
		if(!initialized) plot = new Plot();
		
		try {
			ResultSet rs = db.executeSQL(sql);
			ResultSetMetaData meta = rs.getMetaData();
			
			int n = meta.getColumnCount();
			
			// need more than 2 columns
			if(n<2) return;
			
			for(int ri=0; rs.next(); ri++)
			{
				double x = rs.getDouble(1);
				for(int ci=2; ci<=n; ci++)
				{
					try
					{
						double y = rs.getDouble(ci);
						plot.addPoint(ci+pointSeries, x, y, connectPoints);
					} catch (Exception e){ }
				}
			}
			
			if(XAxisLabel.length()<1) plot.setXLabel( meta.getColumnLabel(1) );
			for(int ci=2; ci<=n; ci++)
			{
				plot.addLegend( ci+pointSeries, meta.getColumnLabel(ci) );
			}
			
			pointSeries += n;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!initialized) initPlot(); else plot.updateUI();
	}
	
	/**
	 * plot XY data from sql by setting 
	 * x-values from first requested column and
	 * y-values from all following columns
	 * @param sql
	 * @param connectPoints connect points to lines
	 * @throws Exception
	 */
	public void plotSlope(String sql, boolean connectPoints)
	{
		if(!initialized) plot = new Plot();
		
		try
		{
			ResultSet rs = db.executeSQL(sql);
			ResultSetMetaData meta = rs.getMetaData();
			
			int n = meta.getColumnCount();
			
			// need more than 2 columns
			if(n<2) return;
			
			double x0 = 0.0;
			double[] y0 = new double[n+1];
			
			for(int ri=0; rs.next(); ri++)
			{
				double x = rs.getDouble(1);
				
				for(int ci=2; ci<=n; ci++)
				{
					try
					{
						double y = rs.getDouble(ci);
						
						double dx = x - x0;
						double dy = y - y0[ci];
						
						double _y = (dx!=0) ? dy/dx : 0;
						
						plot.addPoint(ci+pointSeries, x, _y, connectPoints);
						
						y0[ci] = y;
					} catch (Exception e){ }
				}
				
				x0 = x;
			}
			
			if(XAxisLabel.length()<1) plot.setXLabel( meta.getColumnLabel(1) );
			for(int ci=2; ci<=n; ci++)
			{
				plot.addLegend( ci+pointSeries, meta.getColumnLabel(ci) );
			}
			
			pointSeries += n;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!initialized) initPlot(); else plot.updateUI();
	}

	/**
	 * 
	 */
	public MySQL getDB(){return db;}
}
