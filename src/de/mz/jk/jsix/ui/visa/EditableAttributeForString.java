/** VisualAttributes, de.mz.jk.visa.att, 20.11.2012*/
package de.mz.jk.jsix.ui.visa;

import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 * <h3>{@link EditableAttributeForString}</h3>
 * @author kuharev
 * @version 20.11.2012 11:01:12
 */
public class EditableAttributeForString extends EditableAttribute<String>
{
	private JTextField txtField = null; 
	
	/**
	 * @de.mz.jk.par name
	 * @de.mz.jk.par defValue
	 */
	public EditableAttributeForString(String name, String defaultValue, String currentValue)
	{
		super( name, defaultValue, currentValue );
	}

	@Override protected JComponent getEditorComponent(){ return txtField; }
	@Override protected void initEditorComponent(){ txtField = new JTextField(); }
	
	@Override public String getEditorValue(){ return txtField.getText(); }
	@Override public void showValue(String value){ txtField.setText( value ); }

	@Override public boolean accept(String value){ return true; }
}
