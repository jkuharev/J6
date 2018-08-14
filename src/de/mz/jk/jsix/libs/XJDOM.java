package de.mz.jk.jsix.libs;

import java.io.File;
import java.io.StringReader;
import java.util.*;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import de.mz.jk.jsix.lang.XStringConverter;

public class XJDOM 
{
	/**
	 * get a JDOM root element from xml file
	 * @param file
	 * @return root element
	 * @throws Exception
	 */
	public static Element getBadJDOMRootElement(String xml) throws Exception
	{
		SAXBuilder b = new SAXBuilder("org.ccil.cowan.tagsoup.Parser", false);
		Document d = b.build( new StringReader(xml) );
		return d.getRootElement();
	}
	
	/**
	 * get a JDOM root element from xml file
	 * @param file
	 * @return root element
	 * @throws Exception
	 */
	public static Element getBadJDOMRootElement(File file) throws Exception
	{
		SAXBuilder b = new SAXBuilder("org.ccil.cowan.tagsoup.Parser", false);
		Document d = b.build(file);
		return d.getRootElement();
	}
	
	/**
	 * get a JDOM root element from xml file
	 * @param file
	 * @return root element
	 * @throws Exception
	 */
	public static Element getJDOMRootElement(String xml) throws Exception
	{
		SAXBuilder b = new SAXBuilder(false);
		Document d = b.build( new StringReader(xml) );
		return d.getRootElement();
	}
	
	/**
	 * get a JDOM root element from xml file
	 * @param file
	 * @return root element
	 * @throws Exception
	 */
	public static Element getJDOMRootElement(File file) throws Exception
	{
		SAXBuilder b = new SAXBuilder(false);
		Document d = b.build(file);
		return d.getRootElement();
	}
	
	
	/**
	 * extract recursively all named child nodes from JDOM subtree of an element 
	 * @param host root of JDOM subtree
	 * @param name name of nodes to find
	 * @param caseSensitive true if the search should be case sensitive (while comparing names)
	 * @return list of all found named children
	 */
	public static List<Element> getChildren(Element parent, String name, boolean caseSensitive)
	{
		List<Element> result = new ArrayList<Element>();
		Iterator<Element> ci = parent.getChildren().iterator();
		while( ci.hasNext() )
		{
			Element child = ci.next();
			if((caseSensitive)?child.getName().equals(name):child.getName().equalsIgnoreCase(name))
			{
				result.add( child );
			}	
			result.addAll( getChildren(child, name, caseSensitive) );
		}
		return result;
	}
	
	/**
	 * recursively extract all (case insensitive) named child nodes from JDOM subtree of an element 
	 * @param host root of JDOM subtree
	 * @param name the name of nodes to find
	 */
	public static List<Element> getChildren(Element parent, String name)
	{
		return getChildren(parent, name, false);
	}
	
	/**
	 * recursively extract first (case insensitive) named child node from JDOM subtree of an element 
	 * @param host root of JDOM subtree
	 * @param name the name of nodes to find
	 * @return the first found child element, or null
	 */
	public static Element getFirstChild(Element parent, String name)
	{
		List<Element> elements = getChildren( parent, name, false );
		return ( elements != null && elements.size() > 0 ) ? elements.get( 0 ) : null;
	}

	/**
	 * find first child element and its attribute 
	 * @param parent host root of JDOM subtree
	 * @param elementName the name of the node to find
	 * @param attName the name of the attribute
	 * @param defaultValue the default value to return on error
	 * @return
	 */
	public static String getFirstChildAttributeValue(Element parent, String elementName, String attName, String defaultValue)
	{
		try
		{
			Element e = getFirstChild( parent, elementName );
			return getAttributeValue( e, attName );
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}

	/**
	 * get an attribute's value
	 * @param element owner element
	 * @param attName the name of the attribute, the name is case insensitive
	 * @param defaultValue the value to return on error
	 * @return the attribute value 
	 */
	public static String getAttributeValue(Element element, String attName, String defaultValue)
	{
		String res = getAttributeValue( element, attName );
		return ( res != null ) ? res : defaultValue;
	}

	/**
	 * get an attribute's value
	 * @param element owner element
	 * @param attName the name of the attribute, the name is case insensitive
	 * @return value or null if no such attribute found
	 */
	public static String getAttributeValue(Element element, String attName)
	{
		Attribute a = getAttribute(element, attName);
		if(a!=null) return a.getValue();
		return "";
	}
	
	/**
	 * get an attribute
	 * @param element owner element
	 * @param attName the name of the attribute, the name is case insensitive
	 * @return the attribute or null if no such attribute found
	 */
	public static Attribute getAttribute(Element element, String attName)
	{
		// check upper case
		Attribute res = element.getAttribute(attName.toUpperCase());
		if(res!=null) return res;
		// check lower case
		res = element.getAttribute(attName.toLowerCase());
		if(res!=null) return res;
		// check mixed case
		List<Attribute> as = element.getAttributes();
		for(Attribute a : as) if(a.getName().equalsIgnoreCase(attName)) return a;
		// no attribute found
		return null;
	}

	/**
	 * Package attributes of a tag series into a map.
	 * e.g. 
	 * <PARAMETERS>
	 * <PARAM k="inputFile" v="data.csv" />
	 * <PARAM k="outputFile" v="result.csv" />
	 * ...
	 * </PARAMS>
	 * @param root the element that contains a series of child elements
	 * @param childTagsName the tag name of child elements
	 * @param keyAttributeName the name of attribute that contains keys
	 * @param valueAttributeName the name of attribute that contains values to mapped to keys
	 * @return key value pairs packaged into LinkedHashMap that preserves the order of elements 
	 */
	public static Map<String, String> getAttributeMap(Element root, String childTagsName, String keyAttributeName, String valueAttributeName)
	{
		Map<String, String> res = new LinkedHashMap<String, String>();
		List<Element> fields = XJDOM.getChildren( root, childTagsName );
		for ( Element field : fields )
		{
			try
			{
				res.put(
						XJDOM.getAttributeValue( field, keyAttributeName ),
						XJDOM.getAttributeValue( field, valueAttributeName ) );
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return res;
	}

	/**
	 * Package attributes of a tag series into a map.
	 * e.g. 
	 * <PARAMETERS>
	 * <PARAM k="inputFile" v="data.csv" />
	 * <PARAM k="outputFile" v="result.csv" />
	 * ...
	 * </PARAMS>
	 * 
	 * The resulting map can store user defined types 
	 * by converting xml attribute string to appropriate types.
	 * Please use one of the predefined StringTo[String|Integer|Double] converters 
	 * or implement the XJDOM.StringConverter interface to convert to a different type. 
	 * 
	 * @param root the element that contains a series of child elements
	 * @param childTag the tag name of child elements
	 * @param kAttribute the name of attribute that contains keys
	 * @param vAttribute the name of attribute that contains values to mapped to keys
	 * @param kConverter 
	 * @param vConverter
	 * @return
	 */
	public static <KT, VT> Map<KT, VT> getAttributeMap(Element root, String childTag, String kAttribute, String vAttribute, XStringConverter<KT> kConverter, XStringConverter<VT> vConverter)
	{
		Map<KT, VT> res = new LinkedHashMap<KT, VT>();
		List<Element> fields = XJDOM.getChildren( root, childTag );
		for ( Element field : fields )
		{
			try
			{
				String k = XJDOM.getAttributeValue( field, kAttribute );
				String v = XJDOM.getAttributeValue( field, vAttribute );
				res.put(
						(KT)( kConverter != null ? kConverter.convert( k ) : k ),
						(VT)( vConverter != null ? vConverter.convert( v ) : v ) );
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return res;
	}

	/**
	 * map of all attributes of given element
	 * with attribute values mapped to the attribute names 
	 */
	public static Map<String, String> getAttributeMap(Element element)
	{
		Map<String, String> res = new LinkedHashMap<>();
		try
		{
			List<Attribute> ats = element.getAttributes();
			for ( Attribute a : ats )
			{
				String k = a.getName();
				String v = a.getValue();
				res.put( k, v );
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
		return res;
	}

	/**
	 * map of all attributes of given tag parsed from the given xml string
	 * with attribute values mapped to the attribute names 
	 */
	public static Map<String, String> getAttributeMap(String xml, String tagName)
	{
		Element doc= null, tag=null;
		try
		{
			doc = XJDOM.getBadJDOMRootElement( xml );
			tag = ( doc.getName() == tagName ) ? doc : doc.getChild( tagName );
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
		return getAttributeMap( tag );
	}
}
