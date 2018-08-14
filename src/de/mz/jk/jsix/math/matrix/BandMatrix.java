package de.mz.jk.jsix.math.matrix;


/**
 * @TODO complete and check this implementation 
 * 
 * {@link http://en.wikipedia.org/wiki/Band_matrix}
 * a band matrix is a special case of a sparse matrix.<br>
 * The difference is that non zero elements build 'bands'.
 * These bands are continuous ranges of elements having leading and trailing zero elements.<br>
 * This implementation handles different bandwidths for each row.
 * e.g.<pre>
 *   0 1 2 3 4 5 6 7 8 9
 * 0 x x x x . . . . . .
 * 1 x x x x x . . . . .
 * 2 . x x x x . . . . .
 * 3 . . x x x x . . . .
 * 4 . . x x x x . . . .
 * 5 . . . x x x x . . .
 * 6 . . . x x x x x . .
 * 7 . . . . x x x x x .
 * 8 . . . . . x x x x x
 * 9 . . . . . . x x x x
 * </pre><br>
 * with 'x' non zero elements and '.' zero elements
 * @author Jšrg Kuharev
 * @param <TYPE> Type of objects stored in cells
 */
public class BandMatrix<TYPE> extends Matrix<TYPE> 
{
	public static void main(String[] args)
	{
		Matrix<String> m = new BandMatrix<String>(10, 10);
		m.setDefaultNullCell("-");
		for(int y=0; y<10; y++)
		{
			for(int x=y-2; x < (y+2); x++)
			{
				m.setCell(""+x, x, y);
			}
		}
		
		m.setCell("x", 6, 4);
		m.setCell("x", 9, 0);
		
		System.out.print("-:	");
		for(int x=-3; x<13; x++)	System.out.print(x+"	");
		System.out.println();
		
		for(int y=-3; y<13; y++)
		{
			System.out.print(y+":	");
			for(int x=-3; x<13; x++)
			{
				System.out.print(m.getCell(x, y) + "\t");
			}
			System.out.println();
		}
	}
	
	private int ensureCapacity = 100;
	private int halfCapacity = ensureCapacity / 2;
	
	/** the 2D matrix data */
	TYPE[][] data = null;
	
	/** beginning position for the first cell in each row */
	int[] shift = null;
	
	/**
	 * band matrix with defined outer bounds
	 * @param width
	 * @param height
	 */
	@SuppressWarnings("unchecked")
	public BandMatrix(int width, int height) 
	{
		super(width, height);
		init();
	}
	
	private void init() 
	{
		data = (TYPE[][]) new Object[height][];
		shift = new int[height];
	}

	/** resizing is not allowed */
	private void throwUnresizableException(){throw new RuntimeException("resize operation not supported!");}
	
	@Override public void setWidth(int width){ throwUnresizableException(); }
	@Override public void setHeight(int height){ throwUnresizableException(); }
	
	@Override
	public TYPE getCell(int x, int y)
	{
		try {
			int _x = x - shift[y];
			return (data[y][_x]==null) ? defaultNullCell : data[y][_x];
		} catch (Exception e) { 		
			return defaultNullCell;
		}
	}
	
	@Override
	public TYPE setCell(TYPE cellContent, int x, int y) 
	{
		// do nothing in index out of bounds
		if(!inBounds(x, y)) return defaultNullCell;
		
		TYPE old = null;
		
		// real x index in row vector
		int _x = x - shift[y];
		
		try
		{
			// try to get old value
			old = data[y][_x];
			
			// if we are here then cell exists
			// assign new content
			data[y][_x] = cellContent;
		}

		// cell does not exist
		catch(Exception e)
		{			
			// if row does not exist
			if(data[y]==null)
			{
				// create new row with a single cell but ensured half capacity to both sides
				int fromIndex = x - halfCapacity;
				int toIndex = x + halfCapacity;
				if(fromIndex<0) fromIndex = 0;
				if(toIndex>width) toIndex = width;
				int sparseIndex = x - fromIndex;
				data[y] = (TYPE[]) new Object[toIndex-fromIndex];
				data[y][sparseIndex] = cellContent;
				shift[y] = fromIndex;
			}
			// row exists but cell does not exist
			else
			{
				// new cell is before row vector
				if(_x < 0)
				{
					/*
					all:				 0,1,2,3,4,5,6,7,8,9,a,b,c,d,e,f
					sparse:				          [0,1,2,3]
					new cell:			       x
					new sparse:			  [0,1,2,3,4,5,6,7]
					*/
					int fromIndex = x - ensureCapacity;
					if(fromIndex<0) fromIndex = 0;
					int toIndex = shift[y] + data[y].length;
					TYPE[] newData = (TYPE[]) new Object[toIndex-fromIndex];
					System.arraycopy(data[y], 0, newData, shift[y] - fromIndex, data[y].length);
					newData[x - fromIndex] = cellContent;
					data[y] = newData;
					shift[y] = fromIndex;
				}
				// new cell is after end of row vector
				else
				{ 
					/*
					all:				 0,1,2,3,4,5,6,7,8,9,a,b,c,d,e,f
					sparse:				          [0,1,2,3]
					new cell:			                         x
					new sparse:			          [0,1,2,3,4,5,6,7,8,9]
					*/
					int toIndex = x + ensureCapacity;
					if( toIndex > width ) toIndex = width;
					TYPE[] newData = (TYPE[]) new Object[toIndex-shift[y]];
					System.arraycopy(data[y], 0, newData, 0, data[y].length);
					newData[x - shift[y]] = cellContent;
					data[y] = newData;
					// shift unchanged
				}
			}
		}
		
		return old;
	}
}
