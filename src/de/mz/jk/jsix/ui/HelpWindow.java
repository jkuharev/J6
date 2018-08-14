package de.mz.jk.jsix.ui;

import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

/**
 * help window 
 * @author Jšrg Kuharev
 * @since 2009-07-14
 */
public class HelpWindow extends JDialog
{
	private JEditorPane htmlPane = null;
	private String htmlFilePath = "de/mz/jk/jsix/help/jsix.html";
	private Frame parentWindow = null;
	Dimension windowSize = new Dimension(640, 480);

	public static void main(String[] args)
	{
		new HelpWindow();
	}

	public HelpWindow()
	{
		this(null);
		showHelpWindow();
	}

	public HelpWindow(Frame parent, String helpFilePath)
	{
		parentWindow = parent;
		htmlFilePath = helpFilePath;
	}

	public HelpWindow(Frame parent)
	{
		parentWindow = parent;
	}

	public void setURL(String url)
	{
		this.htmlFilePath = url;
	}

	public JDialog showHelpWindow(String url)
	{
		setURL(url);
		return showHelpWindow();
	}

	@Override public void setSize(Dimension d)
	{
		this.windowSize = d;
		super.setSize(d);
	}

	public JDialog showHelpWindow()
	{
		if (parentWindow != null) setTitle("help page for " + parentWindow.getTitle());
		try
		{
			htmlPane = new JEditorPaneWithLinks(ClassLoader.getSystemResource(htmlFilePath));
			htmlPane.setEditable(false);
			add(new JScrollPane(htmlPane));
			new JTextComponentCopyPasteCutZoomPopupAdder(htmlPane);
			setSize(windowSize);
			setLocationRelativeTo(parentWindow);
			setModal(true);
			setVisible(true);
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		}
		catch (Exception ioe)
		{
			System.err.println("Error displaying " + htmlFilePath);
		}
		return this;
	}
}
