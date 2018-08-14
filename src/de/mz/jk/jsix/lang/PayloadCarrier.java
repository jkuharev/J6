/** JSiX, de.mz.jk.jsix.lang, 06.07.2012*/
package de.mz.jk.jsix.lang;

/**
 * just a simple payload carrier
 * 
 * <h3>{@link PayloadCarrier}</h3>
 * @author kuharev
 * @version 06.07.2012 10:38:23
 */
public class PayloadCarrier<PayloadType>
{
	private PayloadType payload = null;	

	/** @param payload the new payload */
	public void setPayload(PayloadType payload){ this.payload = payload; }
	
	/** @return stored payload */
	public PayloadType getPayload(){ return payload; }
}
