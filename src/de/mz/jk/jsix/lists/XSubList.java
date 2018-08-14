/** ISOQuant, isoquant.plugins.processing.expression.align.hirschberg.quick, 11.07.2011*/
package de.mz.jk.jsix.lists;

import java.util.Arrays;
import java.util.List;

/**
 * <h3>{@link XSubList}</h3> is a List backed implementation of a SubList view to the original List.
 * {@link XSubList} does not create any own data storage.
 * {@link XSubList} does not allow any structural changes to the SubList, 
 * Read/Write procedures are passed to the original list.
 * The advantage of {@link XSubList} is the ability not to forget absolute (to original list) indexes 
 * while using relative indexes to access the data.<hr>
<pre>
	// exemplaric usage 
	public static void main(String[] args)
	{
		XSubList<Integer> sub1 = new XSubList<Integer>(new Integer[]{0,1,2,3,4,5,6,7,8,9}, 5, 10);
		
		for(int i=0; i<sub1.getSize(); i++)
		{
			System.out.println("["+i+"] = " + sub1.get(i));
		}
		
		System.out.println();
		XSubList<Integer> sub2 = sub1.getSubList(2, 4);
		
		for(int i=0; i<sub2.getSize(); i++)
		{
			System.out.println("["+i+"] = " + sub2.get(i));
		}
		
		System.out.println();
		sub1.set(2, 100);
		
		for(int i=0; i<sub2.getSize(); i++)
		{
			System.out.println("["+i+"] = " + sub2.get(i));
		}
	}
</pre>
 * 
 * @author Joerg Kuharev
 * @version 11.07.2011 10:55:43
 */
public class XSubList<PayLoadType>
{
	private List<PayLoadType> srcList = null;
	
	private int fromIndex = 0;
	private int toIndex = -1;
	private int size = 0;

	/** sublist wrapping an array (converted to a list) */
	public XSubList(PayLoadType[] originalArray)
	{
		this.srcList = Arrays.asList( originalArray );
		setBounds(0, srcList.size());
	}
	
	/** sublist wrapping a list */
	public XSubList(List<PayLoadType> originalList)
	{
		this.srcList = originalList;
		setBounds(0, originalList.size());
	}

	/** sublist of an array */
	public XSubList(PayLoadType[] originalArray, int fromIndex, int toIndex)
	{
		this.srcList = Arrays.asList( originalArray );
		setBounds(fromIndex, toIndex);
	}
	
	/** sublist of a list */
	public XSubList(List<PayLoadType> originalList, int fromIndex, int toIndex)
	{
		this.srcList = originalList;
		setBounds(fromIndex, toIndex);
	}
	
	/** 
	 * sublist of a sublist,
	 * new sublist is backed by the host sublist's original list,
	 * given bounds are relative to the host sublist 
	 */
	public XSubList(XSubList<PayLoadType> parentSubList, int fromIndex, int toIndex)
	{
		this( parentSubList.getOriginalList(), parentSubList.getAbsoluteStartIndex() + fromIndex, parentSubList.getAbsoluteStartIndex() + toIndex );
	}
	
	/** set bounds for this sublist, position is absolute to the original list */
	public void setBounds(int fromIndex, int toIndex)
	{
		if(fromIndex>toIndex)
		{
			throw new IndexOutOfBoundsException("negative index range ["+fromIndex+" : "+toIndex+"] violation!");
		}
		else
		if( fromIndex<0 || fromIndex>srcList.size() )
		{
			throw new IndexOutOfBoundsException("start index "+fromIndex+" is out of valide start index range [0 : "+(srcList.size()-1)+"]");
		}
		else
		if( toIndex<0 || toIndex>srcList.size() )
		{
			throw new IndexOutOfBoundsException("end index "+toIndex+" is out of valide end index range [0 : "+srcList.size()+"]");
		}		
		else
		{
			this.fromIndex = fromIndex;
			this.toIndex = toIndex;
		}
		
		size = toIndex - fromIndex;
	}
	
	/** get element at given (relative) position */
	public PayLoadType get(int index)
	{ 
		return srcList.get( getAbsoluteIndex(index) ); 
	}
	
	/** replace element at given (relative) position */
	public void set(int index, PayLoadType replacement)
	{
		srcList.set(getAbsoluteIndex(index), replacement);
	}
	
	/** the size of sublist */
	public int size(){ return size; }
	
	/** get data of this sublist as list object ( List.subList(from, to) ) */
	public List<PayLoadType> asList(){ return srcList.subList(fromIndex, toIndex); }
	
	/** the original list */
	public List<PayLoadType> getOriginalList(){ return srcList; }
	
	/** get new sublist of this sublist, positions are relative to this sublist */
	public XSubList<PayLoadType> subList(int fromIndex, int toIndex){ return new XSubList<PayLoadType>(this, fromIndex, toIndex); }
	
	/** 
	 * get new sublist of this sublist starting at fromIndex and including all following elements
	 * of this sublist, fromIndex is relative to this sublist 
	 */
	public XSubList<PayLoadType> subList(int fromIndex){ return new XSubList<PayLoadType>(this, fromIndex, size()); }
	
	/** find relative index corresponding to the given absolute position */
	public int getRelativeIndex(int absoluteIndex){ return absoluteIndex - fromIndex; }
	
	/** find original index corresponding to the given ralative position */
	public int getAbsoluteIndex(int index){ return fromIndex + index; }
	
	/** @return index of this sublist's start position, the index is absolute to original data */
	public int getAbsoluteStartIndex(){	return fromIndex; }
	
	/** @return index of this sublist's end position, the index is absolute to original data, 
	 * element at the end positions is not included in this sublis (exclusive position) */
	public int getAbsoluteEndIndex(){ return toIndex; }
	
	/**
	 * @param sublist
	 * @param separator
	 * @return
	 */
	public static <T> String asString(XSubList<T> sublist, String separator)
	{
		String res = "";
		for(int i=0; i<sublist.size(); i++)
		{
			res += sublist.get(i) + separator;
		}
		return res;
	}
}
