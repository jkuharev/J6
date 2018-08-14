package de.mz.jk.jsix.math.matrix;

public class ArrayMatrix<TYPE> extends Matrix<TYPE> 
{
	TYPE[][] data = null;
	
	@SuppressWarnings("unchecked")
	public ArrayMatrix(int width, int height) 
	{
		super(width, height);
		data = (TYPE[][]) new Object[width][height];
	}
	
	public ArrayMatrix(TYPE[][] data)
	{
		super(data[0].length, data.length);
		this.data = data; 
	}
	
	/** resizing is not allowed */
	@Override
	public void setWidth(int width){}
	
	/** resizing is not allowed */
	@Override
	public void setHeight(int height){}
	
	@Override
	public TYPE getCell(int x, int y)
	{
		return data[x][y];
	}

	@Override
	public TYPE setCell(TYPE cellContent, int x, int y) 
	{
		TYPE old = data[x][y];
		data[x][y] = cellContent;
		return old;
	}
}
