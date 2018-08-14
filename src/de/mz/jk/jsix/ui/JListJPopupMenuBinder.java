package de.mz.jk.jsix.ui;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JList;
import javax.swing.JPopupMenu;

/**
 * JListJPopupMenuBinder binds an existing JPopupMenu object to a JList object.<br>
 * When OS specific popup menu trigger is invoked over a list item,
 * only this list item will be selected and 
 * the popup menu will be shown on current mouse position.<br>
 * <b>
 * Don't forget to implement popup menu's ActionListener!
 * You may determine targeted list item by calling <i>list.getSelectedItem()</i>
 * </b> 
 * 
 * @author J.Kuharev
 */
public class JListJPopupMenuBinder implements MouseListener
{
	private JPopupMenu menu = null;
	private JList list = null;
	private boolean mult = false;
	
	/**
	 * binds a JPopupMenu to a JList
	 * @param list the JList
	 * @param menu the JPopupMenu
	 */
	public JListJPopupMenuBinder(JList list, JPopupMenu menu, boolean allowMultiSelection)
	{
		this(list, menu);
		this.mult = allowMultiSelection;
	}

	/**
	 * binds a JPopupMenu to a JList
	 * @param list the JList
	 * @param menu the JPopupMenu
	 */
	public JListJPopupMenuBinder(JList list, JPopupMenu menu)
	{
		list.addMouseListener(this);
		this.list = list;
		this.menu = menu;
	}
	
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mousePressed(MouseEvent e){mouseReleased(e);}
	public void mouseExited(MouseEvent e){}
	public void mouseReleased(MouseEvent e)
	{	
		if( e.isPopupTrigger() && list.isEnabled() )
		{
			if(menu!=null && list.getModel().getSize()>0)
			{
				if(!mult) list.setSelectedIndex( list.locationToIndex( e.getPoint() ) );
				menu.show(list, e.getX(), e.getY());
			}
			
			e.consume();
		}
	}
}
