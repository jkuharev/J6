/** VisualAttributes, de.mz.jk.visa.att, 20.11.2012*/
package de.mz.jk.jsix.ui.visa;

import java.text.DecimalFormat;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

/**
 * <h3>{@link EditableAttributeForDouble}</h3>
 * @author kuharev
 * @version 20.11.2012 12:44:08
 */
public class EditableAttributeForDouble extends EditableAttribute<Double>
{
	private JFormattedTextField txtField = null;
	private String format = "0.##";
	
	/**
	 * @de.mz.jk.par name
	 * @de.mz.jk.par defValue
	 * @de.mz.jk.par theValue
	 * @de.mz.jk.par DecimalFormat this format string will be passed to DecimalFormat-Formatter monitoring input values
	 */
	public EditableAttributeForDouble(String name, Double defaultValue, Double currentValue, String DecimalFormat )
	{
		super(name, defaultValue, currentValue);
		this.format = DecimalFormat;
	}

	@Override protected void initEditorComponent()
	{
		txtField = new JFormattedTextField( new DecimalFormat(format) );
	}
	
	@Override protected JComponent getEditorComponent(){ return txtField; }

	@Override public Double getEditorValue(){ return ((Number) txtField.getValue() ).doubleValue(); }
	@Override public void showValue(Double value){ txtField.setText(value.toString()); }

	@Override public boolean accept(Double value){ return true; }
}
