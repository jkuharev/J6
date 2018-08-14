package de.mz.jk.jsix.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JTabbedPane;

public class ClosableTabPanel extends JTabbedPane implements MouseListener 
{
	private static final long serialVersionUID = 20110308L;
	
	public ClosableTabPanel() 
	{
		super();
		addMouseListener(this);
	}
	
	public void addTab(String title, Component component)
	{
		this.addTab(title, component, null);
	}
	
	public void addTab(String title, Component component, Icon extraIcon) 
	{
		super.addTab(title, new CloseTabIcon(extraIcon), component);
		setSelectedIndex(getTabCount()-1);
	}
	
	public void mouseClicked(MouseEvent e)
	{
		int tabNumber=getUI().tabForCoordinate(this, e.getX(), e.getY());
		if(tabNumber<0) return;
		if(getIconAt(tabNumber)==null || tabNumber==0) return;
		Rectangle rect=((CloseTabIcon)getIconAt(tabNumber)).getBounds();
		if (rect.contains(e.getX(), e.getY())) this.removeTabAt(tabNumber);
	}
	
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}

	/**
	* The class which generates the [x] icon for the tabs.
	*/
	public static class CloseTabIcon implements Icon 
	{
		private int x_pos;
		private int y_pos;
		private int width;
		private int height;
		private Icon fileIcon;
		
		/**
		 * construct an [x] icon with or without underlying extra to the [x] icon.
		 * This value is null if no extra icon is required.
		 * @param fileIcon the underlying icon or null for a monochrome [x]  
		 */
		public CloseTabIcon(Icon fileIcon) 
		{
			this.fileIcon=fileIcon;
			width=16;
			height=16;
		}
		
		@Override public void paintIcon(Component c, Graphics g, int x, int y) 
		{
			this.x_pos=x;
			this.y_pos=y;
			Color col=g.getColor();
			g.setColor(Color.black);
			int y_p=y+2;
			g.drawLine(x+1, y_p, x+12, y_p);
			g.drawLine(x+1, y_p+13, x+12, y_p+13);
			g.drawLine(x, y_p+1, x, y_p+12);
			g.drawLine(x+13, y_p+1, x+13, y_p+12);
			g.drawLine(x+3, y_p+3, x+10, y_p+10);
			g.drawLine(x+3, y_p+4, x+9, y_p+10);
			g.drawLine(x+4, y_p+3, x+10, y_p+9);
			g.drawLine(x+10, y_p+3, x+3, y_p+10);
			g.drawLine(x+10, y_p+4, x+4, y_p+10);
			g.drawLine(x+9, y_p+3, x+3, y_p+9);
			g.setColor(col);
			if(fileIcon!=null) fileIcon.paintIcon(c, g, x+width, y_p);
		}
		
		@Override public int getIconWidth() 
		{
			return width + (fileIcon != null? fileIcon.getIconWidth() : 0);
		}
		
		@Override public int getIconHeight() 
		{
			return height;
		}
		
		public Rectangle getBounds() 
		{
			return new Rectangle(x_pos, y_pos, width, height);
		}
	}
}
