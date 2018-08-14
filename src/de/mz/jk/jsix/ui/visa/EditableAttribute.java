/** VisualAttributes, de.mz.jk.visa.att, 14.09.2012*/
package de.mz.jk.jsix.ui.visa;

import javax.swing.JComponent;

/**
 * <h3>{@link EditableAttribute}</h3>
 * @author kuharev
 * @version 14.09.2012 11:03:15
 */
public abstract class EditableAttribute <ValueType>
{
	private String name = "";
	private ValueType value = null;
	private ValueType defaultValue = null;
	
	private String comment = "";
	
	public EditableAttribute(String name, ValueType defaultValue, ValueType currentValue)
	{
		setName(name);
		setDefaultValue(defaultValue);
		setValue(currentValue);
	}
	
	public EditableAttribute(String name, ValueType defaultValue)
	{
		setName(name);
		setDefaultValue(defaultValue);
		setValue(defaultValue);
	}
	
	/**
	 * @return GUI part as a JComponent for this attribute
	 */
	public JComponent getComponent()
	{
		if(getEditorComponent()==null)
		{
			initEditorComponent();
			initToolTip();
			showValue( getValue() );
		}
		return getEditorComponent();
	}
	
	protected abstract void initEditorComponent();

	protected void setDefaultValue(ValueType defaultValue){	this.defaultValue = defaultValue; }
	public ValueType getDefaultValue(){ return defaultValue; }
	
	public void setName(String name){this.name = name;}
	public String getName(){return name;}
	
	public void setValue(ValueType value){this.value = value;}
	public ValueType getValue(){return value;}
	
	/** @return type dependent editor component, specifically implemented by subtypes of {@link EditableAttribute}  */
	protected abstract JComponent getEditorComponent();
	
	/** implementation will show given value */
	public abstract void showValue(ValueType value);
	
	public void showCurrentValue(){showValue(value);}
	public void showDefaultValue(){showValue(defaultValue);}
	
	/** check value if it is acceptable by rules of implementation */
	public abstract boolean accept(ValueType value);
	
	public void setComment(String comment){	this.comment = comment; initToolTip(); }
	public String getComment(){	return comment;	}
	
	public void initToolTip()
	{
		getEditorComponent().setToolTipText(
			"<html>" +
			"<table>" +
			"<tr><td align=right>parameter name:</td><td align=left>"+getName()+"</td></tr>" +
			"<tr><td align=right>default value:</td><td align=left>"+getDefaultValue()+"</td></tr>" +
			"<tr><td align=right>usage comment:</td><td align=left>"+getComment()+"</td></tr>" +
			"</table>" +
			"</html>"
		);
	}
	
	public abstract ValueType getEditorValue();
}
