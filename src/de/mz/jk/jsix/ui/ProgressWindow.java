package de.mz.jk.jsix.ui;



import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 * ProgressWindow is a small progress showing window containing
 * a progress bar, a text label and a status bar
 * @author J.Kuharev
 */
public class ProgressWindow extends JDialog implements iProcessProgressListener
{
	private static final long serialVersionUID = 20110311L;
	private JProgressBar bar = new JProgressBar();
	private JLabel status = new JLabel(" ");
	private JLabel msg = new JLabel(" ");
	private int max;
	
	/**
	 * creates a ProgressWindow object with user defined message
	 * @param message text to show
	 */
	public ProgressWindow(String message)
	{
		this();
		setMessage(message);
	}
	
	public ProgressWindow()
	{
		getContentPane().setLayout(new BorderLayout());
		setSize(400, 80);
		getContentPane().add( msg, BorderLayout.NORTH );
		getContentPane().add( bar, BorderLayout.CENTER );
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		bar.setBorder(BorderFactory.createLoweredBevelBorder());
		getContentPane().add(status, BorderLayout.SOUTH);
		status.setBorder(BorderFactory.createLoweredBevelBorder());
	}
	
	
	public void setMessage(String message)
	{
		msg.setText(message);
	}
	
	
	public void setProgressValue(int value)
	{
		bar.setValue(value);
		setStatus(value*100/max + "%");
	}

	
	public void setProgressMaxValue(int maxValue)
	{
		max = maxValue;
		bar.setMaximum(max);
	}

	
	public void setStatus(String msg)
	{
		status.setText(msg);
	}

	
	public void endProgress() 
	{
		dispose();
	}

	
	public void startProgress() 
	{
		setVisible(true);
	}

	
	public void endProgress(String Message) 
	{
		endProgress();
	}
	
	public void startProgress(String Message) 
	{
		startProgress();
	}
}
