/** ISOQuant, de.mz.jk.jsix.math.matrix, 26.06.2012*/
package de.mz.jk.jsix.math.matrix;

/**
 * sparse vector represented by a subarray of subsequent elements
 * <h3>{@link BandVector}</h3>
 * @author kuharev
 * @version 26.06.2012 14:15:49
 */
public class BandVector<TYPE>
{
	public static void main(String[] args)
	{
		BandVector v = new BandVector<String>(10);
		v.set(5, "x", 3);
		v.set(6, "x", 3);
		v.set(8, "x", 3);
		v.set(2, "x", 3);
		
		for(int i=0; i<v.getSize(); i++) System.out.print(i+"	");
		System.out.println();
		for(int i=0; i<v.getSize(); i++) System.out.print(v.get(i)+"	");
	}
	
	public BandVector(int size)
	{
		this.size = size;
	}
	
	private int size = 0;
	private int shift = 0;
	private TYPE[] data = null;
	
	/** @return the size */
	public int getSize(){ return size; }
	
	public TYPE set(int pos, TYPE cellContent, int ensureCapacity)
	{
		TYPE oldValue = null;
		try
		{
			oldValue = data[pos - shift]; 
		} 
		catch (Exception e)
		{
			if(data == null)
				putNew(pos, cellContent, ensureCapacity);
			else
			if(pos < shift)
				putBefore(pos, cellContent, ensureCapacity);
			else
				putAfter(pos, cellContent, ensureCapacity);
		}		
		return oldValue;
	}
	
	/**
	 * @param pos
	 * @param cell
	 * @param ensureCapacity
	 */
	private void putAfter(int pos, TYPE cellContent, int ensureCapacity)
	{
		/*
		all:				 0,1,2,3,4,5,6,7,8,9,a,b,c,d,e,f
		sparse:				          [0,1,2,3]
		new cell:			                         x
		new sparse:			          [0,1,2,3,4,5,6,7,8,9]
		*/
		int toIndex = pos + ensureCapacity;
		if( toIndex > size ) toIndex = size;
		TYPE[] newData = (TYPE[]) new Object[toIndex-shift];
		System.arraycopy(data, 0, newData, 0, data.length);
		newData[pos - shift] = cellContent;
		data = newData;
	}

	/**
	 * @param pos
	 * @param cell
	 * @param ensureCapacity
	 */
	private void putBefore(int pos, TYPE cellContent, int ensureCapacity)
	{
		/*
		all:				 0,1,2,3,4,5,6,7,8,9,a,b,c,d,e,f
		sparse:				          [0,1,2,3]
		new cell:			       x
		new sparse:			  [0,1,2,3,4,5,6,7]
		*/
		int fromIndex = pos - ensureCapacity;
		if(fromIndex<0) fromIndex = 0;
		int toIndex = shift + data.length;
		TYPE[] newData = (TYPE[]) new Object[toIndex-fromIndex];
		System.arraycopy(data, 0, newData, shift - fromIndex, data.length);
		newData[pos - fromIndex] = cellContent;
		data = newData;
		shift = fromIndex;
	}

	/**
	 * @param pos
	 * @param cellContent
	 * @param ensureCapacity
	 */
	private void putNew(int pos, TYPE cellContent, int ensureCapacity)
	{
		int halfCap = ensureCapacity / 2;
		int fromIndex = pos - halfCap;
		int toIndex = pos + halfCap;
		if(fromIndex<0) fromIndex = 0;
		if(toIndex>size) toIndex = size;
		int sparseIndex = pos - fromIndex;
		data = (TYPE[]) new Object[toIndex-fromIndex];
		data[sparseIndex] = cellContent;
		shift = fromIndex;
	}

	/**
	 * @param pos
	 * @return element at given position or
	 */
	public TYPE get(int pos)
	{
		try
		{
			return data[pos - shift]; 
		} 
		catch (Exception e)
		{
			return null;
		}
	}
}
