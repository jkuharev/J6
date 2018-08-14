package de.mz.jk.jsix.math.matrix;

import java.io.PrintStream;

/**
 * abstract Matrix representation
 * @author Jšrg Kuharev
 * @param <TYPE>
 */
public abstract class Matrix<TYPE> 
{
	protected int height=0;
	protected int width=0;

	protected TYPE defaultNullCell = null;
	
	/**
	 * zero size matrix
	 */
	public Matrix(){}
	
	/**
	 * matrix with given numbers of columns and rows
	 * @param width
	 * @param height
	 */
	public Matrix(int width, int height)
	{
		this.width=width;
		this.height=height;
	}
	
	/**
	 * @return height, number of rows
	 */
	public int getHeight() {return height;}
	
	/**
	 * @param height, number of rows
	 */
	public void setHeight(int height) {this.height=height;}
	
	/**
	 * @return width, number of columns
	 */
	public int getWidth() {return width;}
	
	/**
	 * @param width, number of columns
	 */
	public void setWidth(int width) {this.width=width;}
	
	/**
	 * check if given coordiantes are inside the matrix 
	 * @param x the column index
	 * @param y the row index
	 * @return true if x and y are inside the matrix
	 */
	public boolean inBounds(int x, int y)
	{
		return y >= 0 && y < height && x >= 0 && x < width;
	}
	
	public abstract TYPE getCell(int x, int y);
	public abstract TYPE setCell(TYPE cellContent, int x, int y);
	
	/**
	 * set a default null Cell<br>
	 * a matrix implementation may use the default null cell in case of exceptions 
	 * @param nullCell
	 */
	public void setDefaultNullCell(TYPE nullCell)
	{
		defaultNullCell = nullCell;
	}
	
	public void toCSV(PrintStream out, String sep)
	{
		for(int y=0; y<height; y++)
		{
			for(int x=0; x<width; x++)
			{
				TYPE cell = getCell(x, y);
				out.print(
					((x>0) ? sep : "") + 
					((cell==null) ? "" : cell.toString() )
				);
			}
			out.println();
		}
	}
}