package de.mz.jk.jsix.plot.svg;
import java.awt.Color;
import java.io.File;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import de.mz.jk.jsix.libs.XDOM;

/**
* <pre> 
* public class test_plotter
* {
*     public static void main(String[] args) throws Exception
*     {
* 	SVGPlotter svg = new SVGPlotter();
* 	for(int i=1; i<10; i++)	
* 	{
* 	    svg.drawRect(10*i+50, 30*i+50, i*30+50, 50, "Name des " + i, "Beschreibung des "+ i);
* 	    svg.drawText(10*i+50, 30*i+50, i+". rect");
* 	}
* 	svg.drawLine(0, 300, 600, 300, "Ordinate", "auch X-Achse genannt");
* 	svg.setScale(1.5, 0.7);
*	svg.adjustCanvasSize(50, 50);
* 	svg.saveTo("test.svg");
*     }
* }
* </pre>
* @author Joerg Kuharev
* @date 2009-01-30
*/
public class SVGPlotter
{
	private String defaultXML = "<?xml version=\"1.0\"?><svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" baseProfile=\"full\"><g id=\"canvas\"></g></svg>";
	private String slash = File.separator;
	private String appDir = System.getProperty("user.dir") + slash;
	private String templateFileName = appDir + "template.svg";

	private Document doc;
	private Node root;
	private Node g;
	private Node last;
	private String textStyle = "font-size:12pt; font-face: Helvetica; fill: black";
	private int maxX = 0;
	private int maxY = 0;
	private double xscale = 1;
	private double yscale = 1;
	private boolean DEBUG = true;
	private int strokeWidth = 2;
	private Color strokeColor = Color.black;
	private Color fillColor = Color.white;

	/**
	 * SVGPlotter from default template "template.svg"
	 * @throws Exception
	 */
	public SVGPlotter() throws Exception
	{
		init();
	}

	/**
	 * SVGPlotter from given template 
	 * @param TemplateFileName
	 * @throws Exception
	 */
	public SVGPlotter(String TemplateFileName) throws Exception
	{
		this.templateFileName = TemplateFileName;
		init();
	}

	/**
	 * reads template svg document
	 * @throws Exception
	 */
	private void init() throws Exception
	{
		if (DEBUG) System.out.println("parsing file " + templateFileName);
		try
		{
			doc = XDOM.getDOM(new File(templateFileName));
		}
		catch (Exception e)
		{
			System.out.println("SVGPlotter: error parsing template file '" + templateFileName + "'.");
			doc = XDOM.getDOM( defaultXML );
		}
		doc.getDocumentElement().normalize();
		root = doc.getDocumentElement();
		NodeList gs = doc.getElementsByTagName("g");
		if (gs.getLength() > 0)
		{
			g = gs.item(0);
		}
		else
		{
			doc.createElement("g");
			root.appendChild(g);
		}
	}

	/**
	 * saves document to a file
	 * @param file
	 */
	public void saveTo(File file)
	{
		if (DEBUG) System.out.println("Speichere Datei " + file.getAbsolutePath());
		try
		{
			// root.normalize();
			Source source = new DOMSource(doc);
			Result result = new StreamResult(file);
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);
		}
		catch (Exception e)
		{}
	}

	/**
	 * adjust canvas size by maximal size and position of drawn elements
	 * with additional spaces
	 * @param xspace
	 * @param yspace
	 */
	public void adjustCanvasSize(int xspace, int yspace)
	{
		setCanvasSize(maxX + xspace, maxY + yspace);
	}

	/**
	 * draws a rectangle
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void drawRect(int x, int y, int width, int height, String name, String desc)
	{
		if (DEBUG) System.out.println("zeichne Rechteck ... x=" + x + "; y=" + y + "; width=" + width + "; height=" + height);
		Element rect = doc.createElement("rect");
		rect.setAttribute("x", x + "");
		rect.setAttribute("y", y + "");
		rect.setAttribute("width", width + "");
		rect.setAttribute("height", height + "");
		addSVGElement(rect, name, desc);
		extendMax(x+width, y+height);
	}
	
	/**
	 * draws an ellipse
	 * @param x
	 * @param y
	 * @param rx
	 * @param ry
	 * @param stroke
	 * @param fill
	 */
	public void drawEllipse(int x, int y, int rx, int ry, String name, String desc)
	{
		if (DEBUG) System.out.println("zeichne Rechteck ... x=" + x + "; y=" + y + "; xr=" + rx + "; yr=" + ry);
		Element rect = doc.createElement("ellipse");
		rect.setAttribute("cx", x + "");
		rect.setAttribute("cy", y + "");
		rect.setAttribute("rx", rx + "");
		rect.setAttribute("ry", ry + "");
		addSVGElement(rect, name, desc);
		extendMax(x+rx, y+ry);
	}

	public void drawCircle(int x, int y, int radius, String name, String desc)
	{
		if (DEBUG) System.out.println("zeichne Kreis ... x=" + x + "; y=" + y + "; radius=" + radius);
		Element rect = doc.createElement("circle");
		rect.setAttribute("cx", x + "");
		rect.setAttribute("cy", y + "");
		rect.setAttribute("r", radius + "");
		addSVGElement(rect, name, desc);
		extendMax(x+radius, y+radius);
	}
	
	/**
	 * @param element
	 * @param name
	 * @param desc
	 */
	private void addSVGElement(Element element, String name, String desc)
	{
		element.setAttribute("fill", getHex(fillColor));
		element.setAttribute("stroke", getHex(strokeColor));
		element.setAttribute("stroke-width", strokeWidth + "");
		if (name != null)
		{
			Node n = doc.createElement("title");
			n.appendChild(doc.createTextNode(name));
			element.appendChild(n);
		}
		if (desc != null)
		{
			Node d = doc.createElement("desc");
			d.appendChild(doc.createTextNode(desc));
			element.appendChild(d);
		}
		g.appendChild(element);
	}

	private void extendMax(int X, int Y)
	{
		if (X > maxX) maxX = X;
		if (Y > maxY) maxY = Y;
	}
	
	/**
	 * draws a line
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param stroke
	 */
	public void drawLine(int x1, int y1, int x2, int y2, String name, String desc)
	{
		if (DEBUG) System.out.println("zeichne Linie x1=" + x1 + "; y1=" + y1 + "; x2=" + x2 + "; y2=" + y2);
		Element rect = doc.createElement("line");
		rect.setAttribute("x1", x1 + "");
		rect.setAttribute("y1", y1 + "");
		rect.setAttribute("x2", x2 + "");
		rect.setAttribute("y2", y2 + "");
		addSVGElement(rect, name, desc);
		extendMax(x1>x2?x1:x2, y1>y2?y1:y2);
	}

	/**
	 * draws a Text
	 * @param x
	 * @param y
	 * @param text
	 */
	public void drawText(int x, int y, String text)
	{
		if (DEBUG) System.out.println("schreibe Text x=" + x + "; y=" + y + "; Text=" + text);
		Element element = doc.createElement("text");
		element.setAttribute("x", x + "");
		element.setAttribute("y", y + "");
		element.setAttribute("style", textStyle);
		element.appendChild(doc.createTextNode(text));
		g.appendChild(element);
		// recheck maxX/maxY
		extendMax(x, y);
	}

	/**
	 * sets new font style parameters
	 * <p>example: svg.setFontStyle("font-size:12pt; 
	 * font-face: Helvetica; fill: black");</p>
	 * @param fontStyle
	 */
	public void setFontStyle(String fontStyle)
	{
		textStyle = fontStyle;
	}

	/**
	 * sets canvas size
	 * @param width
	 * @param height
	 */
	public void setCanvasSize(int width, int height)
	{
		// Canvasgrš§e Šndern
		((Element) root).setAttribute("width", width + "");
		((Element) root).setAttribute("height", height + "");
	}

	/**
	 * sets canvas scale factors 
	 * @param xscale
	 * @param yscale
	 */
	public void setScale(double xscale, double yscale)
	{
		this.xscale = xscale;
		this.yscale = yscale;
		((Element) g).setAttribute("transform", "scale(" + xscale + "," + yscale + ")");
	}

	/**
	 * converts a Color to its web-hex-color representation
	 * @param c Color to convert
	 * @return Web-Hex-String like #aabbcc
	 */
	private static String getHex(java.awt.Color c)
	{
		String r = Integer.toHexString(c.getRed());
		String g = Integer.toHexString(c.getGreen());
		String b = Integer.toHexString(c.getBlue());
		return "#" + ((r.length() == 1) ? "0" : "") + r + ((g.length() == 1) ? "0" : "") + g + ((b.length() == 1) ? "0" : "") + b;
	}

	public void setStrokeWidth(int strokeWidth){this.strokeWidth = strokeWidth;}
	public int getStrokeWidth(){return strokeWidth;}
	public void setColor(Color c){this.strokeColor = c;}
	public void setFillColor(Color c){this.fillColor = c;}
	public Node getLastAppendedNode(){return last;}
	public void setLastNodesAttribute(String name, String value){((Element) last).setAttribute(name, value);}

	public static void main(String[] args) throws Exception
	{
		SVGPlotter svg = new SVGPlotter();
		svg.drawCircle(300, 300, 300, null, null);
		svg.drawCircle(300, 300, 200, null, null);
		svg.drawCircle(300, 300, 100, null, null);
		svg.drawCircle(300, 300, 50, null, null);
		svg.drawCircle(300, 300, 30, null, null);
		svg.setFillColor(Color.red);
		svg.drawCircle(300, 300, 10, null, null);
		svg.drawLine(300, 0, 300, 600, null, "Y-Axis");
		svg.drawText(300, 590, "Y-Axis");
		svg.drawLine(0, 300, 600, 300, null, "X-Axis");
		svg.drawText(550, 300, "X-Axis");
		svg.adjustCanvasSize(0, 0);
		svg.saveTo( new File("test.svg") );
	}
}
