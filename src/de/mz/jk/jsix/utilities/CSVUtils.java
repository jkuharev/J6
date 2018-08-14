/** JSiX, de.mz.jk.jsix.utilities, 12.03.2014*/
package de.mz.jk.jsix.utilities;

import java.io.PrintStream;

import de.mz.jk.jsix.libs.XJava;

/**
 * <h3>{@link CSVUtils}</h3>
 * @author kuharev
 * @version 12.03.2014 15:12:19
 */
public class CSVUtils
{
	/** */
	public final static String defaultQuoteChar = "\"";
	/** CSV = comma  separated values, thus comma is the default column separator */
	public final static String defaultColSep = ",";
	/** surprise, surprise: a point is the default decimal point */
	public final static String defaultDecPoint = ".";

	protected String quoteChar = defaultQuoteChar;
	protected String colSep = defaultColSep;
	protected String decPoint = defaultDecPoint;

	protected PrintStream printStream = System.out;

	public CSVUtils()
	{}

	public CSVUtils(PrintStream defaultStream)
	{
		setPrintStream( defaultStream );
	}

	public String getQuoteChar()
	{
		return quoteChar;
	}

	public CSVUtils setQuoteChar(String quoteChar)
	{
		this.quoteChar = quoteChar;
		return this;
	}

	public String getColSep()
	{
		return colSep;
	}

	public CSVUtils setColSep(String colSep)
	{
		this.colSep = colSep;
		return this;
	}

	public String getDecPoint()
	{
		return decPoint;
	}

	public CSVUtils setDecPoint(String decPoint)
	{
		this.decPoint = decPoint;
		return this;
	}

	public CSVUtils setPrintStream(PrintStream printStream)
	{
		this.printStream = printStream;
		return this;
	}

	public PrintStream getPrintStream()
	{
		return printStream;
	}

	/** apply current quote settings to a string value and write it to out */
	public CSVUtils printTxtCell(PrintStream out, String value)
	{
		out.print( quoteChar );
		try
		{
			String txt = XJava.decURL( value );
			out.print( txt.replaceAll( quoteChar, " " ).replaceAll( "\n", "" ) );
		}
		catch (Exception e)
		{}
		out.print( quoteChar );
		return this;
	}

	/** apply current decimal point settings to a numeric value and write it to out */
	public CSVUtils printNumCell(PrintStream out, String value)
	{
		try
		{
			out.print( value.replaceAll( "\\.", decPoint ) );
		}
		catch (Exception e)
		{}
		return this;
	}

	public <TYPE extends Number> CSVUtils printNumCell(PrintStream out, TYPE value)
	{
		return printNumCell( out, value.toString() );
	}

	public CSVUtils printNumCell(PrintStream out, float value)
	{
		return printNumCell( out, Float.valueOf( value ).toString() );
	}

	public CSVUtils printNumCell(PrintStream out, double value)
	{
		return printNumCell( out, Double.valueOf( value ).toString() );
	}

	/** print column separator */
	public CSVUtils printColSep(PrintStream out)
	{
		out.print( colSep );
		return this;
	}

	/** print end line character */
	public CSVUtils endLine(PrintStream out)
	{
		out.println();
		return this;
	}

	public CSVUtils txtCell(String value)
	{
		return printTxtCell( printStream, value );
	}

	public CSVUtils numCell(String value)
	{
		return printNumCell( printStream, value );
	}

	public <TYPE extends Number> CSVUtils numCell(TYPE value)
	{
		return numCell( value.toString() );
	}

	public CSVUtils numCell(float value)
	{
		return numCell( Float.valueOf( value ).toString() );
	}

	public CSVUtils numCell(double value)
	{
		return numCell( Double.valueOf( value ).toString() );
	}

	/** print column separator */
	public CSVUtils colSep()
	{
		return printColSep( printStream );
	}

	/** print end line character  */
	public CSVUtils endLine()
	{
		return endLine( printStream );
	}

	/** flush and close output stream */
	public void close()
	{
		printStream.flush();
		printStream.close();
	}
}
