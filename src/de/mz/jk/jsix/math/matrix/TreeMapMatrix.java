package de.mz.jk.jsix.math.matrix;

import java.util.Map;
import java.util.TreeMap;

/**
 * sparse representation of a matrix stored in a linearized TreeMap<br>
 * only set cells of matrix will allocate memory<br>
 * not previously set cells are handled as null and do not need any memory 
 * @author Jšrg Kuharev
 * @param <TYPE> type of objects to store
 */
public class TreeMapMatrix <TYPE> extends Matrix<TYPE>
{
//	public static void main(String[] args) throws Exception 
//	{
//		System.out.println(Short.MIN_VALUE + " .. " +  Short.MAX_VALUE);
//		System.out.println(Integer.MIN_VALUE + " .. " +  Integer.MAX_VALUE);
//		System.out.println(Long.MIN_VALUE + " .. " +  Long.MAX_VALUE);
//		
//		Runtime rt = Runtime.getRuntime();
//		Matrix<DTWMatrixCell> M = new TreeMapMatrix<DTWMatrixCell>(100, 100000);
//		
//		Bencher t = new Bencher().start();
//		for(int x=0; x<M.getWidth(); x++)
//		for(int y=0; y<M.getHeight(); y++)
//		{
//			DTWMatrixCell cell = new DTWMatrixCell();
//			cell.score = x + y;
//			M.setCell( cell, x, y );
//		}
//		
//		System.out.println("duration: "+t.stop().getSec()+"s");
//		System.out.println((rt.totalMemory() - rt.freeMemory())/1024/1024 + "MB belegt!");
//		
//		
//		FileOutputStream fout = new FileOutputStream(new File("d:\\etc\\desktop\\matrix.csv"));
//		PrintStream out = new PrintStream(fout);
//		M.toCSV(out, ";");
//	}
	
	protected Map<Long, TYPE> data = new TreeMap<Long, TYPE>();
	
	protected final long xFactor = 1000000000L;
	
	/**
	 * just providing width and height of matrix<br>
	 * @param width
	 * @param height
	 */
	public TreeMapMatrix(int width, int height){super(width, height);}
	
	/**
	 * !!! be carefully, HashMatrix does not check the size of matrix
	 */
	public TreeMapMatrix(){super(0,0);}
	
	@Override
	public TYPE getCell(int x, int y)
	{
		TYPE cell = getCell(xy2i(x, y));
		return (cell==null) ? defaultNullCell : cell;
	}
	
	@Override
	public TYPE setCell(TYPE cellContent, int x, int y)
	{
		if(x>=width) width = x + 1;
		if(y>=height) height = y + 1;
		return setCell(cellContent, xy2i(x, y));
	}
	
	protected TYPE getCell(long index){return data.get(index);}
	protected TYPE setCell(TYPE cellContent, long index){return data.put(index, cellContent);}
	
	/**
	 * make linear index from 2d x,y coordinate<br>
	 * @param x
	 * @param y
	 * @return long i = x*xFactor + y
	 */
	protected long xy2i(int x, int y)
	{
		return x * xFactor + y;
	}
	
	/**
	 * make x,y coordinates from linear index
	 * @param i linear index
	 * @return array xy with x stored in xy[0] and y stored in xy[1] 
	 */
	protected int[] i2xy(long i)
	{
		int[] xy = new int[2];
		xy[0] = (int) (i / xFactor);
		xy[1] = (int) (i - xy[0]);
		return xy;
	}
}
