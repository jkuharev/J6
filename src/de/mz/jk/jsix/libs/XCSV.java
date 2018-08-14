package de.mz.jk.jsix.libs;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class XCSV <CellType>
{
	protected File csvFile = null;
	protected String quote = "\"";
	protected String colSep = ";";

	protected String[] colNames = null;
	protected List<String> rowNames = new ArrayList<String>();
	protected List<CellType[]> data = new ArrayList<CellType[]>();
	
	protected int nRows = 0;
	protected int nCols = 0;
	
	protected boolean useColNames = true;
	protected boolean useRowNames = true;
	
	protected int quoteLen = quote.length();
	protected int doubleQuoteLen = quoteLen*2;
	
	/**
	 * please use setter methods to define how and what to do
	 */
	public XCSV(){}
	
	public XCSV<CellType> setCsvFile(File csvFile)
	{
		this.csvFile = csvFile;
		return this;
	}
	
	public XCSV<CellType> setColSep(String colSep)
	{
		this.colSep = colSep;
		return this;
	}
	
	public XCSV<CellType> setQuote(String quote)
	{
		this.quote = quote;
		quoteLen = quote.length();
		doubleQuoteLen = quoteLen*2;
		return this;
	}
	
	public XCSV<CellType> setUseColNames(boolean useColNames) 
	{
		this.useColNames = useColNames;
		return this;
	}
	
	public XCSV<CellType> setUseRowNames(boolean useRowNames) 
	{
		this.useRowNames = useRowNames;
		return this;
	}
	
	public String getQuoteChar(){return quote;}
	public String getSepChar(){return colSep;}
	
	public int getRowCount(){return nRows;}
	public int getColCount(){return nCols;}
	
	public List<String> getColNamesList(){ return Arrays.asList(colNames); }
	public String[] getColNames(){return colNames;}
	public List<String> getRowNames(){return rowNames;}
	public List<CellType[]> getData(){return data;}
	
	private String[] stripLine(String line)
	{			
		String[] res = line.split( colSep );
		for(int i = 0; i < res.length; i++)
		{
			String v = res[i];
			if( 	v.length()>=doubleQuoteLen	// min length of both quotes 
				&&	v.startsWith(quote)		// starts with quote
				&&	v.endsWith(quote) )		// ends with quote
			{
				res[i] = v.substring(quoteLen, v.length()-quoteLen);
			}
		}
		return res;
	}
	
	/**
	 * initiate reading 
	 * @return
	 */
	public XCSV<CellType> readAtOnce()
	{
		BufferedReader r = null;
		String line = "";
		data = new ArrayList<CellType[]>();
		
		try{ 
			r = new BufferedReader( new FileReader(csvFile) ); 
		}
		catch (Exception e){
			r = new BufferedReader( 
				new InputStreamReader( 
						ClassLoader.getSystemResourceAsStream( csvFile.toString() ) 
				) 
			);
		}

		try
		{ 
			final int cellShift = (useRowNames) ? 1 : 0;

			for(int i=0; (line=r.readLine())!=null; i++)
			{
				String[] cells = stripLine(line);
				if( i==0 ) // first line
				{
					nCols = cells.length - cellShift; 
					colNames = new String[nCols];
					for(int ci=0; ci<nCols; ci++)
					{
						int cellIndex = ci+cellShift; 
						colNames[ci] = (useColNames) ? cells[cellIndex] : "" + cellIndex;
					}
					// title line read next
					if(useColNames) continue;
				}
				
				CellType[] rowData = (CellType[]) new Object[ cells.length - cellShift ];
				rowNames.add( (useRowNames) ? cells[0] : "" + i );
				for(int ci=cellShift; ci<cells.length; ci++)
				{
					int colIndex = ci-cellShift;
					rowData[colIndex] = parseCellValue(colIndex, cells[ci]);
				}
				data.add( rowData );
				nRows = i;
			} 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.err.println("failed to read csv file '"+csvFile+"'"); 
		}
		finally{ try{r.close();} catch (Exception e){} }
		
		return this;
	}
	
	public abstract CellType parseCellValue(int colIndex, String stringValue);
	
	/**
	 * processed CSV file contains only decimal numbers 
	 * <h3>{@link AllValuesAreDoubles}</h3>
	 * @author kuharev
	 * @version 13.03.2013 15:18:18
	 */
	public static class AllValuesAreDoubles extends XCSV<Double>
	{
		private char decPoint = ',';
		
		public AllValuesAreDoubles(File csvFile){ this.setCsvFile( csvFile );	}
		public AllValuesAreDoubles(){}
		
		public AllValuesAreDoubles setDecPoint(char decPoint)
		{
			this.decPoint = decPoint;
			return this;
		}
		public char getDecPoint(){return decPoint;}
		
		public Double parseCellValue(int colIndex, String stringValue)
		{
			try{ return Double.parseDouble( stringValue.replace(decPoint, '.') ); } catch(Exception e){ }
			return null;
		}
		
		public double[][] getDataAsArray( double defaultCellValue )
		{
			double[][] res = new double[nRows][];
			for(int row=0; row<nRows; row++)
			{
				res[row] = new double[nCols];
				Object[] dataRow = data.get(row);
				for(int col=0; col<nCols; col++)
				{
					try
					{
						res[row][col] = (Double) dataRow[col];
					}
					catch(Exception e)
					{
						res[row][col] = defaultCellValue;
					}
				}
			}
			return res;
		}
	}
	
	/**
	 * processed CSV contains string fields
	 * <h3>{@link AllValuesAreStrings}</h3>
	 * @author kuharev
	 * @version 13.03.2013 15:19:14
	 */
	public static class AllValuesAreStrings extends XCSV<String>
	{	
		public AllValuesAreStrings(File csvFile){ this.setCsvFile( csvFile );	}
		public AllValuesAreStrings(){}
		
		public String parseCellValue(int colIndex, String stringValue){return stringValue;}
	}
	
	public static <ArrayType> void writeCSV(File file, List<ArrayType[]> data, String colSep, String decPoint, String quote, String[] colNames, String[] rowNames) throws Exception
	{
		PrintStream out = new PrintStream(file);
		if(colNames!=null)
		{
			if(rowNames!=null) out.print(quote+"title"+quote+colSep);
			out.println(quote + XJava.joinArray(colNames, ""+quote+colSep+quote) + quote);
		}
		for(int i=0; i<data.size(); i++)
		{
			if(rowNames!=null) out.print(quote+rowNames[i]+quote+colSep);
			out.println( XJava.joinArray( data.get(i) , colSep ).replaceAll("\\.", decPoint) );
		}
		out.flush();
		out.close();
	}
	
	public static <ArrayType> void writeCSV(File file, ArrayType[][] data, String colSep, String decPoint, String quote, String[] colNames, String[] rowNames) throws Exception
	{
		PrintStream out = new PrintStream(file);
		if(colNames!=null)
		{
			if(rowNames!=null) out.print(quote+"title"+quote+colSep);
			out.println(quote + XJava.joinArray(colNames, ""+quote+colSep+quote) + quote);
		}
		for(int i=0; i<data.length; i++)
		{
			if(rowNames!=null) out.print(quote+rowNames[i]+quote+colSep);
			out.println( XJava.joinArray( data[i] , colSep ).replaceAll("\\.", decPoint) );
		}
		out.flush();
		out.close();
	}
	
	public static void writeCSV(File file, int[][] data, String colSep, String decPoint, String quote, String[] colNames, String[] rowNames) throws Exception
	{
		PrintStream out = new PrintStream(file);
		if(colNames!=null)
		{
			if(rowNames!=null) out.print(quote+"title"+quote+colSep);
			out.println(quote + XJava.joinArray(colNames, ""+quote+colSep+quote) + quote);
		}
		for(int i=0; i<data.length; i++)
		{
			if(rowNames!=null) out.print(quote+rowNames[i]+quote+colSep);
			out.println( XJava.joinArray( data[i] , colSep ).replaceAll("\\.", decPoint) );
		}
		out.flush();
		out.close();
	}
	
	public static void writeCSV(File file, double[][] data, String colSep, String decPoint, String quote, String[] colNames, String[] rowNames) throws Exception
	{
		PrintStream out = new PrintStream(file);
		if(colNames!=null)
		{
			if(rowNames!=null) out.print(quote+"title"+quote+colSep);
			out.println(quote + XJava.joinArray(colNames, ""+quote+colSep+quote) + quote);
		}
		for(int i=0; i<data.length; i++)
		{
			if(rowNames!=null) out.print(quote+rowNames[i]+quote+colSep);
			out.println( XJava.joinArray( data[i] , colSep ).replaceAll("\\.", decPoint) );
		}
		out.flush();
		out.close();
	}
}