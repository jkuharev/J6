package de.mz.jk.jsix.ui;

import java.awt.Font;
import java.awt.Rectangle;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

/**
 * JEditorPane extended to follow internal html links
 * <h3>{@link JEditorPaneWithLinks}</h3>
 * @author kuharev
 * @version 15.08.2013 10:10:53
 */
class JEditorPaneWithLinks extends JEditorPane implements HyperlinkListener
{
	private static final long serialVersionUID = 20130815L;

	public JEditorPaneWithLinks()
	{}

	public JEditorPaneWithLinks(URL url) throws Exception
	{
		super(url);
		addHyperlinkListener(this);
	}

	public void scrollToReference(String reference)
	{
		Document d = getDocument();
		if (d instanceof HTMLDocument)
		{
			HTMLDocument doc = (HTMLDocument) d;
			HTMLDocument.Iterator iter = doc.getIterator(HTML.Tag.A);
			for (; iter.isValid(); iter.next())
			{
				AttributeSet a = iter.getAttributes();
				String nm = (String) a.getAttribute(HTML.Attribute.NAME);
				String id = (String) a.getAttribute(HTML.Attribute.ID);
				if (((nm != null) && nm.equals(reference)) || ((id != null) && id.equals(reference)))
				{
					// found a matching reference in the document.
					try
					{
						Rectangle r = modelToView(iter.getStartOffset());
						if (r != null)
						{
							// the view is visible, scroll it to the
							// center of the current visible area.
							Rectangle vis = getVisibleRect();
							// r.y -= (vis.height / 2);
							r.height = vis.height;
							scrollRectToVisible(r);
						}
					}
					catch (BadLocationException ble)
					{
						UIManager.getLookAndFeel().provideErrorFeedback(this);
					}
				}
			}
		}
	}

	public void hyperlinkUpdate(HyperlinkEvent e)
	{
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
		{
			JEditorPane pane = (JEditorPane) e.getSource();
			if (e instanceof HTMLFrameHyperlinkEvent)
			{
				HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
				HTMLDocument doc = (HTMLDocument) pane.getDocument();
				doc.processHTMLFrameHyperlinkEvent(evt);
			}
			else
			{
				try
				{
					pane.setPage(e.getURL());
					pane.scrollToReference(e.getURL().getRef().toString());
				}
				catch (Throwable t)
				{
					t.printStackTrace();
				}
			}
		}
	}

	@Override public void setFont(Font font)
	{
		Document doc = getDocument();
		if (doc instanceof HTMLDocument)
		{
			String bodyRule = "body { font-family: " + font.getFamily() + "; font-size: " + font.getSize() + "pt; }";
			((HTMLDocument) doc).getStyleSheet().addRule(bodyRule);
			System.out.println("font changed to '" + font.getFamily() + "', " + font.getSize() + "pt");
		}
		super.setFont(font);
	}
}
