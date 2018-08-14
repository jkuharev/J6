/** JSiX, de.mz.jk.jsix.utilities, 10.07.2013*/
package de.mz.jk.jsix.utilities;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

import javax.swing.SwingUtilities;

/**
 * <h3>{@link XGUIUtils}</h3>
 * @author kuharev
 * @version 10.07.2013 16:39:16
 */
public class XGUIUtils
{
	/**
	 * ensure that parent dialog window containing child component is resizable
	 * @param childComponent
	 */
	public static void makeParentDialogResizable(final Component childComponent)
	{
		childComponent.addHierarchyListener(new HierarchyListener()
		{
			public void hierarchyChanged(HierarchyEvent e)
			{
				Window window = SwingUtilities.getWindowAncestor(childComponent);
				if (window instanceof Dialog)
				{
					Dialog dialog = (Dialog) window;
					if (!dialog.isResizable())
					{
						dialog.setResizable(true);
					}
				}
			}
		});
	}
}
