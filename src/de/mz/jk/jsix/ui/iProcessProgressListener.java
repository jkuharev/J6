package de.mz.jk.jsix.ui;

/**
 * @author J.Kuharev
 */
public interface iProcessProgressListener 
{
	/**
	 * shows a title message
	 * @param message message to show
	 */
	public void setMessage(String message);
	
	/**
	 * updates current value of progress
	 * @param value
	 */
	public void setProgressValue(int value);
	
	/**
	 * sets maximum value
	 * @param maxValue maximum reachable value
	 */
	public void setProgressMaxValue(int maxValue);
	
	/**
	 * shows a status message
	 * @param msg the status message 
	 */
	public void setStatus(String msg);
	
	/**
	 * signalizes progress' start
	 */
	public void startProgress();
	/**
	 * start progress with a message
	 * @param Message
	 */
	public void startProgress(String Message);
	
	/**
	 * signalize progress' end
	 */
	public void endProgress();
	/**
	 * end process with a message
	 * @param Message
	 */
	public void endProgress(String Message);
}