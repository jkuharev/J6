package de.mz.jk.jsix.ui;


import java.io.*;

import javax.swing.JTextArea;

import de.mz.jk.jsix.libs.XJava;

public class JTextAreaOutputStream
{
	/** the text area used for output */
	private JTextArea textArea;
	
	/** peakCount single write calls */
	private int writeCounter = 0;
	
	/** max number of writes to check text size */
	private int checkThreshold = 1023;
	
	/** max allowed text length */
	private int maxTextSize = 48000;
	
	/** size of text to drop on cleaning */
	private int cleanSize = 2048;
	
	/** the stream used for output */
	private PrintStream outStream = null;
	/** separate stream for err */
	private PrintStream errStream = null;
	
	/** backup std out device */
	private static PrintStream stdOut = System.out;
	
	/** backup std err device */
	private static PrintStream stdErr = System.err;

	/** the log file path */
	private File logFile = null;
	
	/** the log file stream */
	private FileOutputStream logFileStream = null;

	/** also output to std out ? */
	private boolean syncToStdOut = true;

	/**
	 * capture std out and std err to a JTextArea
	 * @param textArea target JTextArea object
	 * @param captureStdOut if true std out will be captured
	 * @param captureStdErr if true std err will be captured
	 */
	public JTextAreaOutputStream(JTextArea textArea, boolean captureStdOut, boolean captureStdErr)
	{
		this.textArea = textArea;
		outStream = new SynchronizedPrintStream( new CloningOutputStream( this, stdOut ) );
		errStream = new SynchronizedPrintStream( new CloningOutputStream( this, stdErr ) );
		if(captureStdOut) captureStdOut();
		if(captureStdErr) captureStdErr();
	}

	public JTextAreaOutputStream( JTextArea textArea, boolean captureStdOut, boolean captureStdErr, File logFile )
	{
		this(textArea, captureStdOut, captureStdErr);
		if(logFile != null)
		try
		{
			if( !logFile.exists() ) logFile.createNewFile();
			if( logFile.canWrite() )
			{
				this.logFileStream = new FileOutputStream( logFile );
				this.logFile = logFile;
				outStream.println("--------------------------------------------------------------------------------");
				outStream.println("log file path is '" + logFile.getAbsolutePath()+"'" );
				outStream.println("additional logging enabled!" );
			}
			else 
				throw new Exception();
		}
		catch (Exception e)
		{
				errStream.println( "--------------------------------------------------------------------------------" );
				errStream.println( "log file path is '" + logFile.getAbsolutePath() + "'" );
				errStream.println( "additional logging disabled!" );
				errStream.println( "error: log file is not writeable!" );
				errStream.println( "for enabling additional logging, make log file writeable and restart application!" );
		}
		outStream.println("logging session started at " + XJava.timeStamp("yyyy-MM-dd HH:mm:ss"));
		outStream.println("--------------------------------------------------------------------------------");
	}
	
	/**
	 * @param syncToStdOut the syncToStdOut to set
	 */
	public void enableSyncToStdOut(boolean syncToStdOut)
	{
		this.syncToStdOut = syncToStdOut;
	}

	@Override protected void finalize() throws Throwable
	{
		super.finalize();
		try{outStream.close();} catch (Exception e){}
		try{errStream.close();} catch (Exception e){}
		try{logFileStream.flush(); logFileStream.close();} catch (Exception e){}
	}
	
	public synchronized void write(int b, PrintStream additionalStream)
	{
		if (syncToStdOut && additionalStream != null) additionalStream.write( b );

		if(logFileStream!=null) try{logFileStream.write(b);}catch (Exception e){}
		
		if(++writeCounter > checkThreshold)
		{
			int len = textArea.getText().length();
			if( len > maxTextSize )
			{
				String newText = textArea.getText().substring(cleanSize);
				textArea.setText( newText );
				writeCounter = 0;
			}
		}
		
		textArea.append(""+(char)b);
	    textArea.setCaretPosition(textArea.getText().length());
	}
	
	/**
	 * @return the PrintStream for outputting into the user defined text area
	 */
	public PrintStream getPrintStream(){return outStream;}
	
	/** start capturing std out */
	public void captureStdOut(){ System.setOut( outStream ); }
	
	/** start capturing std err */
	public void captureStdErr(){ System.setErr( outStream ); }
	
	/** stop capturing std out */
	public void resetStdOut(){ System.setOut( stdOut ); }
	
	/** stop capturing std err */
	public void resetStdErr(){ System.setErr( stdErr ); }
	
	/**
	 * 
	 * <h3>{@link ErrOutStream}</h3>
	 * @author kuharev
	 * @version 09.05.2014 13:23:50
	 */
	static class CloningOutputStream extends OutputStream
	{
		private JTextAreaOutputStream parent = null;
		private PrintStream additionaStream = null;

		public CloningOutputStream(JTextAreaOutputStream parent, PrintStream additionaStream)
		{
			this.parent = parent;
			this.additionaStream = additionaStream;
		}

		@Override public void write(int b) throws IOException
		{
			parent.write( b, additionaStream );
		}
	}

	/**
	 * <h3>{@link SynchronizedPrintStream}</h3>
	 * synchronized wrapping of print/println methods from PrintStream
	 * @author Joerg Kuharev
	 * @version 01.04.2011 10:27:39
	 */
	static class SynchronizedPrintStream extends PrintStream
	{
		public SynchronizedPrintStream( OutputStream out )
		{
			super(out, true);
		}
		
		@Override public synchronized void print(boolean x){super.print(x);}
		@Override public synchronized void print(char x){super.print(x);}
		@Override public synchronized void print(char[] x){super.print(x);}
		@Override public synchronized void print(double x){super.print(x);}
		@Override public synchronized void print(float x){super.print(x);}
		@Override public synchronized void print(int x){super.print(x);}
		@Override public synchronized void print(long x){super.print(x);}
		@Override public synchronized void print(Object x){super.print(x);}
	
		@Override public synchronized void println(boolean x){super.println(x);}
		@Override public synchronized void println(char x){super.println(x);}
		@Override public synchronized void println(char[] x){super.println(x);}
		@Override public synchronized void println(double x){super.println(x);}
		@Override public synchronized void println(float x){super.println(x);}
		@Override public synchronized void println(int x){super.println(x);}
		@Override public synchronized void println(long x){super.println(x);}
		@Override public synchronized void println(Object x){super.println(x);}
	}
}
