package de.mz.jk.jsix.libs;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.ccil.cowan.tagsoup.Parser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class XDOM 
{
	public static boolean DEBUG = false;
	
	/**
	 * dump xml nodes child nodes to a string
	 * @param node to dump
	 * @return plain content of the nodes child nodes
	 */
	public static String getNodeContentString(Node node)
	{
		String res = "";
		NodeList kids = node.getChildNodes();
		for(int i=0; i<kids.getLength(); i++) 
			res+=getNodeString(kids.item(i));
		return res.trim();
	}

	/**
	 * dump an xml node to a string
	 * @param node to dump
	 * @return plain content of the node
	 */
	public static String getNodeString(Node node)
	{
		try 
		{
			Source source = new DOMSource(node);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(source, result);
			return stringWriter.getBuffer().toString();
		}
		catch(Exception e){
			if(DEBUG) e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * parse a String to a DOM Document
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static Document getDOM(String xml) throws Exception
    {
		DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
		DBF.setIgnoringComments(true);
		DBF.setValidating(false);
		DBF.setExpandEntityReferences(false);
		DocumentBuilder DB = DBF.newDocumentBuilder();
		Document doc = null;
		try{
			doc = DB.parse( new ByteArrayInputStream(xml.getBytes()) );
		}catch(Exception e){
			// only known problematic characters will be replaced
			xml = xml.
				replace("�", "u").
				replace("�", "oe").
				replace("�", "Oe").
				replace("�", "ue").
				replace("�", "Ue").
				replace("�", "ae").
				replace("�", "Ae").
				replace("�", "ss");
			
			doc = DB.parse( new ByteArrayInputStream(xml.getBytes()) );
		}
		return doc;
    }
	
	/**
	 * parse xml file to a DOM document
	 * @param xmlFile input file
	 * @return
	 * @throws Exception
	 */
	public static Document getDOM(File xmlFile) throws Exception
    {
		DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
		DBF.setIgnoringComments(true);
		DBF.setValidating(false);
		DBF.setExpandEntityReferences(false);
		DocumentBuilder DB = DBF.newDocumentBuilder();
		Document doc = null;
		try{
			doc = DB.parse( new FileInputStream(xmlFile) );
		}catch(Exception e){
			doc = getDOM(XFiles.readFile(xmlFile));
		}
		return doc;
    }
	
	/**
	 * parse an xml string into a DOM Node by TAGSOUP<br>
	 * TAGSOUP is able to read broken DOM structures
	 * @param html html formatted string
	 * @return DOM Node
	 */
	public static Document getBadDOM(String html)
	{
		XMLReader reader = new Parser();
		DOMResult domresult = new DOMResult();
		Document res = null;
		try
		{
			reader.setFeature(Parser.namespacesFeature, false);
			reader.setFeature(Parser.namespacePrefixesFeature, false);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(
				new SAXSource( reader, new InputSource( new StringReader(html) ) ), 
				domresult
			);			
			res = domresult.getNode().getFirstChild().getOwnerDocument();
		}
		catch(Exception e){
			if(DEBUG) e.printStackTrace();
		}	
		return res;
	}
	

	/**
	 * parse an xml string into a DOM Node by TAGSOUP<br>
	 * TAGSOUP is able to read broken DOM structures
	 * @param html html formatted string
	 * @return DOM Node
	 */
	public static Document getBadDOM(File file)
	{
		XMLReader reader = new Parser();
		DOMResult domresult = new DOMResult();
		Document res = null;
		try
		{
			reader.setFeature(Parser.namespacesFeature, false);
			reader.setFeature(Parser.namespacePrefixesFeature, false);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(
				new SAXSource( reader, new InputSource( new FileReader(file) ) ), 
				domresult
			);			
			res = domresult.getNode().getFirstChild().getOwnerDocument();
		}
		catch(Exception e){
			if(DEBUG) e.printStackTrace();
		}		
		return res;
	}	
}
