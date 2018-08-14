/** VisualAttributes, de.mz.jk.visa.att, 14.09.2012*/
package de.mz.jk.jsix.ui.visa;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * <h3>{@link AttributeEditorPanel}</h3>
 * @author kuharev
 * @version 14.09.2012 11:16:32
 */
public abstract class AttributeEditorPanel <AttributeType extends EditableAttribute<?>> extends JPanel
{
	private AttributeType attribute = null;
	
	public AttributeEditorPanel(AttributeType attribute)
	{
		this.attribute = attribute;
		this.setBorder( BorderFactory.createTitledBorder( attribute.getName()) );
	}
}
