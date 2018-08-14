package de.mz.jk.jsix.utilities;

/**
 * <h3>Bencher</h3>
 * wrapper for simple execution time benchmarking
 * @author Joerg Kuharev
 * @version 28.12.2010 16:57:07
 */
public class Bencher 
{
	public static final int MILLISECONDS = 0;
	public static final int SECONDS = 1;
	public static final int MINUTES = 2;
	public static final int HOURS = 3;
	
	private long s = 0;
	private long e = 0;
	private long d = 0;
	
	private int ms = -1;
	private int sec = -1;
	private int min = -1;
	private int hours = -1;
	private int days = -1;

	/** timer is not started, use start() to start it manually */
	public Bencher(){}
	
	/** @param started if true timer is immidiately started */
	public Bencher(boolean started){if(started) start();}
	
	/** start timer */
	public Bencher start()
	{
		s = e = System.currentTimeMillis();
		return this;
	}
	
	/** stop timer */
	public Bencher stop()
	{
		e = System.currentTimeMillis();
		d = e - s;
		
		ms = (int)(d % 1000); 
		sec = (int)((d-ms) % 60000);
		min = (int)(d % 3600000);
		hours = (int)(d % 216000000);
		days = (int)(d % 5184000000l);
		
		return this;
	}
	
	/**
	 * whole time difference between start() and stop() using given units
	 * @param unit: use class constant values
	 * @return
	 */
	public double getTime(int unit)
	{
		double dd = (double)d;
		switch(unit)
		{
			case SECONDS: 
				return dd / 1000;
			case MINUTES: 
				return dd / 60000;
			case HOURS: 
				return dd / 3600000;
			default: 
				return d;
		}
	}
	
	public double getSec() {return ((double)(int)((getTime(SECONDS))*100))/100; }
	public String getSecString() {return getSec()+"s"; }
	
	public int ms(){ return ms;}
	public int sec(){ return sec;}
	public int min(){ return min; }
	public int hours(){ return hours; }
	public int days(){ return days; }
	
	public String toString()
	{
		String res = "";
		if(days>0) res += days+"d ";
		if(hours>0) res += hours+"h ";
		res += min+"min "+sec+"s "+ms+"ms";
		return res;
	}
}
