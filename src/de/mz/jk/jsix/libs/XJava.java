package de.mz.jk.jsix.libs;

import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class XJava 
{
	private static boolean DEBUG = false;

	/**
	 * decodes an url encoded string (using UTF-8 charset)
	 * @param text url encoded text
	 * @return newly decoded text or original text if decoding fails
	 */
	public static String decURL(String text)
	{
		try
		{
			return URLDecoder.decode(text, "UTF-8");
		} 
		catch(Exception e)
		{
			return text;
		}
	}

	/**
	 * dumps fields and their values from given object to standard output
	 * @param o object to dump
	 */
	public static void dump(Object o)
	{
		Field[] fields = o.getClass().getDeclaredFields();
		for(Field f : fields)
		{
			try{
				String fname = f.getName();
				Object fvalue= f.get(o);
				
				System.out.println(
					fname + "='" + fvalue.toString() + "';"
				);
			}catch(Exception e){}
		}
	}

	/**
	 * encodes a text into an url encoded string (using UTF-8 charset)
	 * @param text to be encoded
	 * @return newly encoded text or original text if encoding fails
	 */
	public static String encURL(String text)
	{
		try
		{
			return URLEncoder.encode(text, "UTF-8");
		} 
		catch(Exception e)
		{
			return text;
		}
	}

	/**
	 * copy an array of double primitives to a new list (ArrayList)
	 * @param doubleArray
	 * @return
	 */
	public static List<Double> getDoubleList(double[] doubleArray)
	{
		List<Double> res = new ArrayList<Double>(doubleArray.length);
		for(int i = 0; i < doubleArray.length; i++) res.add(doubleArray[i]);
		return res;		
	}
	
	/**
	 * converts a list of numeric values to array of doubles
	 * @param list
	 * @return
	 */
	public static <T extends Number> double[] getDoubleArray(List<T> list)
	{
		if(list!=null)
		{
			double[] res = new double[list.size()];
			for(int i=0; i<res.length; i++)
			{
				res[i] = list.get(i).doubleValue();
			}
			return res;
		}
	
		return new double[]{};
	}

	/**
	 * 
	 * @param data
	 * @return
	 */
	public static <T extends Number> double[][] getDoubleArrayMatrix(List<List<T>> data)
	{
		// find the smallest data sublist
		int nRows = data.get( 0 ).size(), nCols = data.size();
		for ( List<T> col : data )
		{
			nRows = Math.min( nRows, col.size() );
		}
		double[][] xy = new double[nRows][2];
		for ( int i = 0; i < nRows; i++ )
		{
			xy[i] = new double[nCols];
			for ( int j = 0; j < nCols; j++ )
			{
				xy[i][j] = data.get( j ).get( i ).doubleValue();
			}
		}
		return xy;
	}

	/**
	 * make a list's copy.<br>
	 * only references from elements are copied.  
	 * @param <TYPE> 
	 * @param list list to copy
	 * @return copy of list
	 */
	public static <TYPE> List<TYPE> getListCopy(List<TYPE> list)
	{
		List<TYPE> res = new ArrayList<TYPE>( list );  
		// for( TYPE x : list ) res.add(x);
		return res;
	}

	/**
	 * get array from list
	 * @param <TYPE> type of objects
	 * @param list the initial list of objects
	 * @return array of typed objects
	public static <TYPE> TYPE[] getArray(List<TYPE> list)
	{
		if(list==null || list.size()<1) return null;
		TYPE[] res = (TYPE[]) Array.newInstance( list.get(0).getClass(), list.size() );
		for (int i=0; i<res.length; i++)
		{
			res[i] = list.get(i);
		}
		return res;
	}
	*/
	
	/**
	 * get array from list
	 * @param <TYPE> type of objects
	 * @param list the initial list of objects
	 * @return array of typed objects 
	 */
	public static <TYPE> TYPE[] getArray(Collection<TYPE> list)
	{
		if(list==null || list.size()<1) return null;
		TYPE[] res = (TYPE[]) new Object[list.size()];
		list.toArray( res );
		return res;
	}
	
	/**
	 * join string representations of array elements to a single string
	 * placing a separator string between elements
	 * @param array source array
	 * @param separator separator string
	 * @return joint array
	 */
	public static String joinArray(Object[] array, String separator) 
	{
	    StringBuffer sb = new StringBuffer();
	    for(int i=0; i < array.length; i++) 
	    {
	    	if (i != 0) sb.append(separator);
	    	sb.append( (array[i]==null) ? "" : array[i].toString() );
	  	}
	  	return sb.toString();
	}

	/**
	 * join string representations of array elements to a single string
	 * placing a separator string between elements
	 * @param array source array
	 * @param separator separator string
	 * @return joint array
	 */
	public static String joinArray(double[] array, String separator) 
	{
	    StringBuffer sb = new StringBuffer();
	    for(int i=0; i<array.length; i++) 
	    {
	    	if (i != 0) sb.append(separator);
	    	sb.append( array[i] );
	  	}
	  	return sb.toString();
	}
	
	/**
	 * join string representations of array elements to a single string
	 * placing a separator string between elements
	 * @param array source array
	 * @param separator separator string
	 * @return joint array
	 */
	public static String joinArray(int[] array, String separator) 
	{
	    StringBuffer sb = new StringBuffer();
	    for(int i=0; i<array.length; i++) 
	    {
	    	if (i != 0) sb.append(separator);
	    	sb.append( array[i] );
	  	}
	  	return sb.toString();
	}
	
	/**
	 * join string representations of array elements to a single string
	 * placing a separator string between elements
	 * @param array source array
	 * @param separator separator string
	 * @return joint array
	 */
	public static String joinArray(float[] array, String separator) 
	{
	    StringBuffer sb = new StringBuffer();
	    for(int i=0; i<array.length; i++) 
	    {
	    	if (i != 0) sb.append(separator);
	    	sb.append( array[i] );
	  	}
	  	return sb.toString();
	}
	
	/**
	 * join string representations of array elements to a single string
	 * placing a separator string between elements
	 * @param array source array
	 * @param separator separator string
	 * @return joint array
	 */
	public static <TYPE> String joinList(List<TYPE> values, String separator) 
	{
	    StringBuffer sb = new StringBuffer();
	    for(int i=0; i < values.size(); i++) 
	    {
	    	if (i != 0) sb.append(separator);
	    	sb.append( values.get(i).toString() );
	  	}
	  	return sb.toString();
	}

	/**
	 * search and replace a string with an other one
	 * @param text the original text
	 * @param needle the string to be replaced within the original text
	 * @param replacement the replacement for needle
	 * @return new text
	 */
	public static String replaceString(String text, String needle, String replacement) 
	{
		String res = "";
		String[] tmp = text.split(needle);
		if(tmp.length<2) 
			return text;
		else
			res = tmp[0];
		for(int i=1; i<tmp.length; i++)
			res+=replacement+tmp[i];
		return res;
	}

	/**
	 * split string into an array char by char
	 * @param text
	 * @return
	 */
	public static String[] getStringArray(String text)
	{
		String[] res = new String[text.length()];
		for(int i=0; i<text.length(); i++) res[i] = "" + text.charAt(i);
		return res;
	}
	
	/**
	 * split string into an array char by char
	 * @param text
	 * @return
	 */
	public static List<String> getStringList(String text)
	{
		List<String> res = new ArrayList<String>(text.length());
		for(int i=0; i<text.length(); i++) res.add(text.charAt(i)+"");
		return res;
	}
	
	
	/**
	* e.g. timeStamp("yyyyMMdd-HHmmss")
	* @param format the time stamp formating string
	* @return current time formated as defined
	*/
	public static String timeStamp(String format)
	{
		DateFormat df = new SimpleDateFormat(format);
		return df.format(new Date());
	}
	
	/** @return time stamp using yyyyMMdd format */
	public static String dateStamp(){ return timeStamp("yyyyMMdd"); }
	/** @return time stamp using yyyyMMddHHmmss format */	
	public static String timeStamp(){ return timeStamp("yyyyMMddHHmmss"); }

	/**
	 * @param number
	 * @param minStringWidth
	 * @return
	 */
	public static String numString(Number value, int minStringWidth)
	{
		String s = value.toString();
		while(s.length()<minStringWidth) s = "0" + s;
		return s;
	}

	/**
	 * repeat string multiple times
	 * @param s the string to be repeated
	 * @param times how often the string is repeated
	 * @return repeated string
	 */
	public static String repeatString(String s, int times)
	{
	    StringBuffer b = new StringBuffer();
	    for(int i=0;i < times;i++)
	    {
	        b.append(s);
	    }
	    return b.toString();
	}
	
	/**
	 * build path for using in ClassLoader 
	 * from given file name and the package of given class.<br>
	 * @param c the class inside the package for looking up the resource 
	 * @param resourceName the name of resource (file) we are looking for
	 * @return valid path to the resource
	 */
	public static String getPackageResource(Class c, String resourceName)
	{
		return getPackageResource(c.getPackage(), resourceName);	
	}
	
	/**
	 * build path for using in ClassLoader 
	 * from given file name inside of given package.<br>
	 * @param p the package for looking up the resource 
	 * @param resourceName the name of resource (file) we are looking for
	 * @return valid path to the resource
	 */
	public static String getPackageResource(Package p, String resourceName)
	{
		return p.getName().replaceAll("\\.", "/") + "/" + resourceName;	
	}

	/**
	 * @param str string quoted by enclosing its content in either "" or '' or () or || or [] or {}
	 * @return stripped content
	 */
	public static String stripQuotation(String str)
	{
		str = str.trim();
		// str.matches("^[\"\'\\(\\|].*[\"\'\\)\\|]$")
		if( str.matches("^[\"\'\\(\\|\\[\\{].*[\\}\\]\"\'\\)\\|]$") ) 
			return str.substring(1, str.length() - 1);
		else
			return str;
	}

	/**
	 * create a sequence of numbers starting from given value until to given value by given step.
	 * UNCHECKED!!!
	 * @param from
	 * @param to
	 * @param step
	 * @return
	 */
	public static List<Double> fillDoubleList(double from, double to, double step)
	{
		if (to < from)
		{
			double tmp = to;
			to = from;
			from = tmp;
		}
		int n = (int)((to - from)/step) ;
		List<Double> res = new ArrayList<Double>( n );
		for ( int i = 0; i < n; i++ )
		{
			res.add( from + step * i );
		}
		return res ;
	}
	
	/**
	 * fill an array with values
	 * @param from first value (inclusive)
	 * @param to last value (exclusive)
	 * @param step the increment value
	 * @return
	 */
	public static double[] fillDoubleArray(double from, double to, double step)
	{
		if (to < from)
		{
			double tmp = to;
			to = from;
			from = tmp;
		}
		int n = (int)((to - from)/step) ;
		double[] res = new double[n];
		for ( int i = 0; i < n; i++ )
		{
			res[i] = from + step * i;
		}
		return res;
	}
	
	/**
	 * fill an array with values
	 * @param from first value (inclusive)
	 * @param to last value (exclusive)
	 * @param step the increment value
	 * @return
	 */
	public static List<Integer> fillIntegerList(int from, int to, int step)
	{
		if (to < from)
		{
			int tmp = to;
			to = from;
			from = tmp;
		}
		int n = (int)( ( to - from ) / step );
		List<Integer> res = new ArrayList<>( n );
		for ( int i = 0; i < n; i++ )
		{
			res.add( from + step * i );
		}
		return res;
	}

	/**
	 * fill an array with values
	 * @param from first value (inclusive)
	 * @param to last value (exclusive)
	 * @param step the increment value
	 * @return
	 */
	public static int[] fillIntArray(int from, int to, int step)
	{
		if (to < from)
		{
			int tmp = to;
			to = from;
			from = tmp;
		}
		int n = (int)((to - from)/step) ;
		int[] res = new int[n];
		for ( int i = 0; i < n; i++ )
		{
			res[i] = from + step * i;
		}
		return res;
	}

	/**
	 * parse string to number 
	 * @param stringValue
	 * @param defaultValue default value to return on error
	 */
	public static double parseNumber(String stringValue, double defaultValue)
	{
		try
		{
			return Double.parseDouble( stringValue );
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}

	/**
	 * parse string to number 
	 * @param stringValue
	 * @param defaultValue default value to return on error
	 */
	public static float parseNumber(String stringValue, float defaultValue)
	{
		try
		{
			return Float.parseFloat( stringValue );
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}

	/**
	 * parse string to number 
	 * @param stringValue
	 * @param defaultValue default value to return on error
	 */
	public static int parseNumber(String stringValue, int defaultValue)
	{
		try
		{
			return Integer.parseInt( stringValue );
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}

	/**
	 * parse string to number 
	 * @param stringValue
	 * @param defaultValue default value to return on error
	 */
	public static long parseNumber(String stringValue, long defaultValue)
	{
		try
		{
			return Long.parseLong( stringValue );
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}

	/**
	 * extract a map of key to value pairs from a string
	 * @param k2vString grouped map string formatted as follows "key1:value1;k2:v2;k3:v3;..."
	 * @param groupSep group separator string e.g. ";"
	 * @param valueSep value separator string e.g. ":"
	 * @return
	 */
	public static Map<String, String> decMap(String k2vString, String groupSep, String valueSep)
	{
		if (k2vString.endsWith( groupSep )) k2vString = k2vString.substring( 0, k2vString.length() - 1 );
		Map<String, String> res = new HashMap<String, String>();
		String[] elements = k2vString.split( groupSep );
		for ( String e : elements )
		{
			String[] kv = e.split( valueSep );
			if (kv.length > 1) res.put( kv[0], kv[1] );
		}
		return res;
	}

	/**
	 * package a map into a string like "key1:value1;k2:v2;k3:v3;..."
	 * @param map
	 * @param groupSep
	 * @param valueSep
	 * @return
	 */
	public static <KeyType, ValueType> String encMap(Map<KeyType, ValueType> map, String groupSep, String valueSep)
	{
		StringBuilder res = new StringBuilder();
		for ( KeyType k : map.keySet() )
		{
			res.append( k );
			res.append( valueSep );
			res.append( map.get( k ) );
			res.append( groupSep );
		}
		return res.toString();
	}

	/**
	 * generates a LinkedHashMap with the inverse mapping
	 * @param map the original map with key-to-value mapping
	 * @return new map with value-to-key mapping
	 */
	public static <KeyType, ValueType> Map<ValueType, KeyType> reverseMap(Map<KeyType, ValueType> map)
	{
		Map<ValueType, KeyType> res = new LinkedHashMap<ValueType, KeyType>();
		for ( KeyType k : map.keySet() )
			res.put( map.get( k ), k );
		return res;
	}

	/**
	 * create breaks for splitting a range of integers into (nearly) equally sized parts,
	 * as the range boundaries are included in the result, the number of calculated breaks = number of parts + 1 
	 * @param from the lower bound of the range (first break)
	 * @param to the upper bound of the range (last break)
	 * @param parts the number of parts in that the produced breaks will split the range (from...to)
	 * @return array of breaks (including from and to)
	 */
	public static int[] getBreaks(int from, int to, int parts)
	{
		int size = to - from;
		double partSize = size / (double)parts;
		double[] breakPoints = XJava.fillDoubleArray( from, to, partSize );
		int[] breaks = new int[parts + 1];
		for ( int i = 0; i < parts; i++ )
		{
			breaks[i] = (int)Math.round( breakPoints[i] );
		}
		breaks[parts] = to;
		return breaks;
	}

	/**
	 * split a list into (nearly) equally sized parts.
	 * The returned sublists are views to the provided list object produced by List.subList() function! 
	 * @param list the original list
	 * @param parts number of parts
	 * @return sublists of the original list
	 */
	public static <T> List<List<T>> splitList(List<T> list, int parts)
	{
		List<List<T>> sublists = new ArrayList<List<T>>( parts );
		int[] breaks = getBreaks( 0, list.size(), parts );
		for ( int i = 0; i < parts; i++ )
		{
			List<T> subList = new ArrayList<T>( list.subList( breaks[i], breaks[i + 1] ) );
			sublists.add( subList );
		}
		return sublists;
	}

	/**
	 * get a list of indices for sorting data according to provided dataComparator 
	 * @param data
	 * @param dataComparator
	 * @return
	 */
	public static <T> List<Integer> getSortOrder(final List<T> data, final Comparator<T> dataComparator)
	{
		final List<Integer> idx = fillIntegerList( 0, data.size(), 1 );
		Collections.sort( idx, 
				new Comparator<Integer>()
				{
					@Override public int compare(final Integer o1, final Integer o2)
					{
						return dataComparator.compare( data.get( o1 ), data.get( o2 ) );
					}
				}
		);
		return idx;
	}
	
	/**
	 * generic number comparator.
	 * Note: all numbers are converted to Double,
	 * this will generate an additional overhead!!
	 * <h3>{@link NumberComparator}</h3>
	 * @author jkuharev
	 * @version Aug 10, 2016 11:35:53 AM
	 * @param <T>
	 */
	public static class NumberComparator<T extends Number> implements Comparator<T>
	{
		private Comparator<Double> cmp = null;

		public NumberComparator(boolean ascendingOrder)
		{
			cmp = ascendingOrder ? new AscendingDoubleComparator() : new DescendingDoubleComparator();
		}

		@Override public int compare(T o1, T o2)
		{
			return cmp.compare( o1.doubleValue(), o2.doubleValue() );
		}
	}

	/**
	 * a simple implementation of Double comparator for sorting in ASCENDING order
	 * <h3>{@link AscendingDoubleComparator}</h3>
	 * @author jkuharev
	 * @version Aug 10, 2016 11:38:29 AM
	 */
	public static class AscendingDoubleComparator implements Comparator<Double>
	{
		@Override public int compare(Double a, Double b)
		{
			if (a < b) return -1;
			if (a > b) return 1;
			return 0;
		}
	}

	/**
	 * a simple implementation of Double comparator for sorting in DESCENDING order
	 * <h3>{@link AscendingDoubleComparator}</h3>
	 * @author jkuharev
	 * @version Aug 10, 2016 11:38:29 AM
	 */
	public static class DescendingDoubleComparator implements Comparator<Double>
	{
		@Override public int compare(Double a, Double b)
		{
			if (a < b) return 1;
			if (a > b) return -1;
			return 0;
		}
	}

	public static void getSortOrder_Test()
	{
		List<Double> test = getDoubleList( new double[] { 1.0, .1, 2.0, 0.3, 0.5 } );
		List<Integer> ascIdx = getSortOrder( test, new AscendingDoubleComparator() );
		List<Integer> desIdx = getSortOrder( test, new DescendingDoubleComparator() );
		
		List<Double> ascTest = new ArrayList<Double>();
		List<Double> desTest = new ArrayList<Double>();
		for ( int i : ascIdx )
			ascTest.add( test.get( i ) );
		for ( int i : desIdx )
			desTest.add( test.get( i ) );
		
		System.out.println( "src: " + joinList( test, ";	" ) );
		System.out.println( "asc: " + joinList( ascTest, ";	" ) );
		System.out.println( "des: " + joinList( desTest, ";	" ) );
	}

	/**
	 * extract elements of a list by given indices
	 * @param list the original list of elements
	 * @param idx some indices of elements to extract from the original list 
	 * @return
	 */
	public static <T> List<T> subListByIndex(List<T> list, List<Integer> idx)
	{
		List<T> res = new ArrayList<T>( idx.size() );
		for ( int i : idx )
		{
			res.add( list.get( i ) );
		}
		return res;
	}

	/**
	 * produce a message digest hash for the given user input string
	 * @param input
	 * @param algorithm SHA1, MD5
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String getHash(String input, String algorithm) throws NoSuchAlgorithmException
	{
		MessageDigest mDigest = MessageDigest.getInstance( algorithm );
		byte[] result = mDigest.digest( input.getBytes() );
		StringBuffer sb = new StringBuffer();
		for ( int i = 0; i < result.length; i++ )
		{
			sb.append( Integer.toString( ( result[i] & 0xff ) + 0x100, 16 ).substring( 1 ) );
		}
		return sb.toString();
	}

	/**
	 * get SHA1 hash for input string
	 * @param input
	 * @return
	 */
	public static String getSHA1(String input)
	{
		try
		{
			return ( getHash( input, "SHA1" ) );
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * get MD5 hash for the user input string
	 * @param input
	 * @return
	 */
	public static String getMD5(String input)
	{
		try
		{
			return ( getHash( input, "MD5" ) );
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
