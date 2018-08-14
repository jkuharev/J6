/** JSiX, de.mz.jk.jsix.ui.visa, Aug 17, 2017*/
package de.mz.jk.jsix.ui.visa;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import de.mz.jk.jsix.ui.FormBuilder;

/**
 * <h3>{@link visaExample}</h3>
 * @author jkuharev
 * @version Aug 17, 2017 2:25:55 PM
 */
public class visaExample
{
	public static void main(String[] args)
	{
		List<EditableAttribute<?>> att = new ArrayList<EditableAttribute<?>>();
		EditableAttribute<?> x_ = new EditableAttributeForDouble( "x", 0.0, 1.0, "0.##" );
		EditableAttribute<?> y_ = new EditableAttributeForDouble( "y", 0.0, 1.0, "0.##" );
		EditableAttribute<?> t_ = new EditableAttributeForString( "type", "unused", "used" );
		att.add( x_ );
		att.add( y_ );
		att.add( t_ );
		FormBuilder fb = new FormBuilder();
		for ( EditableAttribute<?> a : att )
		{
			fb.add( a.getName(), a.getComponent() );
		}
		JOptionPane.showMessageDialog( null, fb.getFormContainer() );
		System.out.println( "x = " + x_.getValue() );
		System.out.println( "y = " + y_.getValue() );
		System.out.println( "t = " + t_.getEditorValue() );
	}
}
