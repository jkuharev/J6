package de.mz.jk.jsix.math.matrix;

/**
 * a sparse matrix is a special type of a matrix with a large number of zero elements (cells without content)<br>
 * e.g.<pre>
 *   0 1 2 3 4 5 6 7 8 9
 * 0 . . . . . . . . . .
 * 1 . x . x . x . . . .
 * 2 . . . . . . . . . .
 * 3 . . . . x . . . . .
 * 4 . . x . . . . . . .
 * 5 . . . . . . . . . .
 * 6 . . . x . x . . . .
 * 7 . . . . x . . . x .
 * 8 . . . . . x . x . .
 * 9 . . . . . . . x . .
 * </pre><br>
 * with 'x' non zero elements and '.' zero elements
 * @author Jšrg Kuharev
 * @param <TYPE> Type of objects stored in cells
 */
public class SparseMatrix<TYPE> extends Matrix<TYPE> 
{
	class MatrixRow
	{
		boolean empty = true;
		int firstElement;
		TYPE[] data = (TYPE[]) new Object[0];
		
		TYPE get(int index)
		{
			try{
				return data[index+firstElement];
			}catch(Exception e){
				return null;
			}
		}
		
		void set(int index, TYPE content)
		{
			if(empty)
			{
				firstElement = index;
			}
		}
		
		void resizeTo(int newSize)
		{
			// set dimension
			TYPE[] _data = (TYPE[]) new Object[newSize];
			// copy
			for(int i=0; i<data.length; i++) _data[i] = data[i];
			// overwrite
			data = _data;
		}
	}
	
	MatrixRow[] rows = null;
	
	@SuppressWarnings("unchecked")
	public SparseMatrix(int width, int height) 
	{
		super(width, height);
		
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
		return null;//data[x][y];
	}

	@Override
	public TYPE setCell(TYPE cellContent, int x, int y) 
	{
		// TYPE old = data[x][y];
		// data[x][y] = cellContent;
		return null;//old;
	}
}
