/** ISOQuant, de.mz.jk.jsix.ui, 15.03.2012*/
package de.mz.jk.jsix.ui;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 * wrapped JButton with a label, icon and tool tip
 * <h3>{@link JXButton}</h3>
 * @author kuharev
 * @version 15.03.2012 16:31:42
 */
public class JXButton extends JButton
{
	public JXButton(Icon icon, String label, String toolTip)
	{
		super( );
		try{ setIcon(icon); } catch (Exception e) {}
		try{ setText(label); } catch (Exception e) {}
		try{ setToolTipText(toolTip); } catch (Exception e) {}
	}
}
