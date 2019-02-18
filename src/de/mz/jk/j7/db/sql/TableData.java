package de.mz.jk.j7.db.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * wrapper for a sql query result
 * @author J.Kuharev
 */
public class TableData
{
	/** the original query statement string */
	public String sql = "";
	
	/** column labels */
	public String[] cols = {};
	
	/** list of rows with every row represented as a string array */
	public List<String[]> rows = new ArrayList<String[]>();
	
	/** number of columns */
	public int nCols = 0;
	
	/** number of rows */
	public int nRows = 0;
	
	/**
	 * dump content to CSV string,
	 * alias for toXSV with quoteChar="\"", colSep=",", and endLine=System.getProperty("line.separator")
	 * @param includeColNames  if column names should be included as header line
	 * @return csv string
	 */
	public String toCSV(boolean includeColNames)
	{
		return toXSV(includeColNames, "\"", ","	, System.getProperty("line.separator"));
	}
	
	/**
	 * dump table content to a character delimited string
	 * @param includeColNames if column names should be included as header line
	 * @param quoteChar quotation character, typically "\""
	 * @param colSep column separator character, typically ","
	 * @param endLine line end character, typically "\n"
	 * @return multi line string of delimited values
	 */
	public String toXSV( boolean includeColNames, String quoteChar, String colSep, String endLine)
	{
		StringBuilder csv = new StringBuilder();
		
		if(includeColNames)
		{
			csv.append( joinRow(cols, colSep, quoteChar)).append(endLine);
		}
		
		for (String[] row : rows) 
		{
			csv.append( joinRow(row, colSep, quoteChar) ).append(endLine);
		}
		
		return csv.toString();
	}
	
	private String joinRow(String[] arr, String colSep, String quoteChar )
	{
		String q = quoteChar == null ? "" : quoteChar;
		StringBuilder sb = new StringBuilder();
	    for(int i=0; i < arr.length; i++) 
	    {
	    	if (i > 0) sb.append(colSep);
	    	sb.append(q);
	    	if( arr[i]!=null ) sb.append(  arr[i].toString() );
	    	sb.append(q);
	  	}
	  	return sb.toString();
	}
}