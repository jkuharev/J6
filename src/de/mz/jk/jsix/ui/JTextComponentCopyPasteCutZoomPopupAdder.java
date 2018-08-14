/** JSiX, de.mz.jk.jsix.ui, 13.08.2013 */
package de.mz.jk.jsix.ui;

import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

/**
 * <h3>{@link JTextComponentCopyPasteCutZoomPopupAdder}</h3>
 * @author kuharev
 * @version 13.08.2013 15:29:29
 */
public class JTextComponentCopyPasteCutZoomPopupAdder implements MouseListener
{
	private JTextComponent textComponent = null;
	private Font defaultFont = null;

	public JTextComponentCopyPasteCutZoomPopupAdder(JTextComponent textComponent)
	{
		super();
		this.textComponent = textComponent;
		textComponent.addMouseListener(this);
		defaultFont = textComponent.getFont();
	}

	@Override public void mouseClicked(MouseEvent e)
	{}

	@Override public void mousePressed(MouseEvent me)
	{
		if (!me.isPopupTrigger()) return;
		Component comp = SwingUtilities.getDeepestComponentAt(me.getComponent(), me.getX(), me.getY());
		// create popup menu and show
		JTextComponent tc = (JTextComponent) comp;
		JPopupMenu menu = new JPopupMenu();
		menu.add(new CopyAction(tc));
		if (tc.isEditable()) menu.add(new PasteAction(tc));
		if (tc.isEditable()) menu.add(new CutAction(tc));
		if (tc.isEditable()) menu.add(new DeleteAction(tc));
		menu.addSeparator();
		menu.add(new SelectAllAction(tc));
		menu.addSeparator();
		menu.add(new ZoomInAction(tc));
		menu.add(new ZoomOutAction(tc));
		menu.add(new ResetZoomAction(tc, defaultFont));
		Point pt = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), tc);
		menu.show(tc, pt.x, pt.y);
	}

	@Override public void mouseReleased(MouseEvent e)
	{}

	@Override public void mouseEntered(MouseEvent e)
	{}

	@Override public void mouseExited(MouseEvent e)
	{}
}

// @author Santhosh Kumar T - santhosh@in.fiorano.com
class CutAction extends AbstractAction
{
	JTextComponent comp;

	public CutAction(JTextComponent comp)
	{
		super("Cut");
		this.comp = comp;
	}

	public void actionPerformed(ActionEvent e)
	{
		comp.cut();
	}

	public boolean isEnabled()
	{
		return comp.isEditable()
				&& comp.isEnabled()
				&& comp.getSelectedText() != null;
	}
}

// @author Santhosh Kumar T - santhosh@in.fiorano.com
class PasteAction extends AbstractAction
{
	JTextComponent comp;

	public PasteAction(JTextComponent comp)
	{
		super("Paste");
		this.comp = comp;
	}

	public void actionPerformed(ActionEvent e)
	{
		comp.paste();
	}

	public boolean isEnabled()
	{
		if (comp.isEditable() && comp.isEnabled())
		{
			Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
			return contents.isDataFlavorSupported(DataFlavor.stringFlavor);
		}
		else return false;
	}
}

// @author Santhosh Kumar T - santhosh@in.fiorano.com
class DeleteAction extends AbstractAction
{
	JTextComponent comp;

	public DeleteAction(JTextComponent comp)
	{
		super("Delete");
		this.comp = comp;
	}

	public void actionPerformed(ActionEvent e)
	{
		comp.replaceSelection(null);
	}

	public boolean isEnabled()
	{
		return comp.isEditable()
				&& comp.isEnabled()
				&& comp.getSelectedText() != null;
	}
}

// @author Santhosh Kumar T - santhosh@in.fiorano.com
class CopyAction extends AbstractAction
{
	JTextComponent comp;

	public CopyAction(JTextComponent comp)
	{
		super("Copy");
		this.comp = comp;
	}

	public void actionPerformed(ActionEvent e)
	{
		comp.copy();
	}

	public boolean isEnabled()
	{
		return comp.isEnabled()
				&& comp.getSelectedText() != null;
	}
}

// @author Santhosh Kumar T - santhosh@in.fiorano.com
class SelectAllAction extends AbstractAction
{
	JTextComponent comp;

	public SelectAllAction(JTextComponent comp)
	{
		super("Select All");
		this.comp = comp;
	}

	public void actionPerformed(ActionEvent e)
	{
		comp.selectAll();
	}

	public boolean isEnabled()
	{
		return comp.isEnabled() && comp.getText().length() > 0;
	}
}

class ZoomInAction extends AbstractAction
{
	JTextComponent comp;

	public ZoomInAction(JTextComponent comp)
	{
		super("Zoom In");
		this.comp = comp;
	}

	public void actionPerformed(ActionEvent e)
	{
		Font font = comp.getFont();
		comp.setFont(font.deriveFont((float) (font.getSize() + 1)));
	}

	public boolean isEnabled()
	{
		return comp.isEnabled() && comp.getText().length() > 0;
	}
}

class ZoomOutAction extends AbstractAction
{
	JTextComponent comp;

	public ZoomOutAction(JTextComponent comp)
	{
		super("Zoom Out");
		this.comp = comp;
	}

	public void actionPerformed(ActionEvent e)
	{
		Font font = comp.getFont();
		int newFontSize = font.getSize() - 1;
		comp.setFont(font.deriveFont((float) newFontSize));
		System.out.println("changing document font size from " + font.getSize() + " to " + newFontSize + " points...");
	}

	public boolean isEnabled()
	{
		return comp.isEnabled() && comp.getText().length() > 0;
	}
}

class ResetZoomAction extends AbstractAction
{
	JTextComponent comp;
	Font font;

	public ResetZoomAction(JTextComponent comp, Font font)
	{
		super("Reset zoom");
		this.comp = comp;
		this.font = font;
	}

	public void actionPerformed(ActionEvent e)
	{
		comp.setFont(font);
		System.out.println("resetting document font size ...");
	}

	public boolean isEnabled()
	{
		return comp.isEnabled() && comp.getText().length() > 0;
	}
}
