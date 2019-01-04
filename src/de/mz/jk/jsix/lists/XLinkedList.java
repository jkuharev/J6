/** ISOQuant, de.mz.jk.jsix.ui.lists.linked, 06.05.2011*/
package de.mz.jk.jsix.lists;

import de.mz.jk.jsix.lang.PayloadCarrier;

/**
 * XLinkedList is a in fact a node of a linked list
 * that provides linked list functionality to any 
 * user defined object type (payload). 
 * A XLinkedList node is capable
 * to dynamically change its precursor/successor and 
 * to find tail and head elements of the whole chain.
 * 
 * <h3>{@link XLinkedList}</h3>
 * 
 * WARNING: this implementation does not resolve cyclic linkage,
 * 				thus getting head or tail can cause endless loops.
 * 
 * TODO: add cyclic linkage check,
 * 			e.g. by testing the equality of next node in the loop
 * 			with the current object.
 * 			Chicken or the egg: 
 * 				what element of the endless loop
 * 				is the actual head or tail?
 * 			Possible solutions:
 * 				a) raise an error
 * 				b) return current object 
 * 
 * @author Joerg Kuharev
 * @version 06.05.2011 10:53:01
 */
@SuppressWarnings("unchecked")
public class XLinkedList<XLinkedListType extends XLinkedList, PayloadType> extends PayloadCarrier<PayloadType>
{
	protected XLinkedListType precursor=null;
	protected XLinkedListType successor=null;
	
	/**
	 * construct a linked list node having empty user data ( = null)
	 */
	public XLinkedList(){}
	
	/**
	 * make a node carrying given payload 
	 * @param elementData the payload
	 */
	public XLinkedList( PayloadType elementData )
	{ 
		setPayload( elementData ); 
	}
	
	/**
	 * set new leading (precursor) element.<br>
	 * ATTENTION: potentially existing old precursor is not unlinked
	 * but not reachable from this alignment any more.  
	 * @param precursor the precursor to set
	 * @return this node
	 */
	public XLinkedListType setPrecursor(XLinkedListType precursor)
	{
		this.precursor = precursor;
		precursor.successor = (XLinkedListType)this;
		return (XLinkedListType)this;
	}
	
	/**
	 * @return the precursor
	 */
	public XLinkedListType getPrecursor()
	{
		return precursor;
	}
	
	/**
	 * set new trailing (successor) element.<br>
	 * ATTENTION: potentially existing old successor is not unlinked
	 * but not reachable from this alignment any more.
	 * @param successor the successor to set
	 * @return this node
	 */
	public XLinkedListType setSuccessor(XLinkedListType successor)
	{
		this.successor = successor;
		successor.precursor = (XLinkedListType) this;
		return (XLinkedListType) this;
	}
	
	/**
	 * @return the successor
	 */
	public XLinkedListType getSuccessor()
	{
		return successor;
	}
	
	/**
	 * @return the head, the first leading element
	 */
	public XLinkedListType getHeadElement()
	{ 
		XLinkedListType res = (XLinkedListType) this; 
		while(res.precursor!=null) res=(XLinkedListType) res.precursor;
		return res;
	}
	
	/**
	 * @return the tail, the last trailing element  
	 */
	public XLinkedListType getTailElement()
	{ 
		XLinkedListType res = (XLinkedListType)this; 
		while(res.successor!=null) res=(XLinkedListType)res.successor; 
		return res; 
	}
	
	/**
	 * add new tail at the end of current list
	 * @param tail linked list to be appended
	 * @return the last element of concatenated list (tail)
	 */
	public XLinkedListType append(XLinkedListType tail)
	{
		getTailElement().setSuccessor( tail.getHeadElement() );
		return (XLinkedListType) tail.getTailElement();
	}
	
	/**
	 * calculate the size if this linked list 
	 * by counting elements while running from head to tail.<br>
	 * ATTENTION: whole list is iterated on every call of getSize()
	 * @return the calculated size
	 */
	public int getSize()
	{
		XLinkedList e = getHeadElement();
		int size;
		for(size=0; e.successor!=null; size++) e = e.successor;
		return size;
	}
}
