package de.mz.jk.jsix.lang;

/**
 * the implementation if this interface shold convert a String object 
 * to its specific representation in user defined type.
 * This concept is typically used as a replacement for lambda expressions
 * to pass an implementation of a function that does something with a string
 * the function convert() is then implemented outside of the main logic of user code 
 * and can be dynamically different in any call of the target function.   
 * <h3>{@link XStringConverter}</h3>
 * @author jkuharev
 * @version Feb 17, 2017 3:34:01 PM
 * @param <TYPE>
 */
public interface XStringConverter<TYPE>
{
	public TYPE convert(String s);
	/** string to string (pseudo) converter that just bypasses the original string! */
	public static final XStringConverter<String> toString = new XStringConverter<String>()
	{
		public String convert(String s)
		{
			return s;
		}
	};
	/** string to integer converter by using Integer.valueOf(s) */
	public static final XStringConverter<Integer> toInteger = new XStringConverter<Integer>()
	{
		public Integer convert(String s)
		{
			return Integer.valueOf( s );
		}
	};
	/** string to double converter by using Double.valueOf(s) */
	public static final XStringConverter<Double> toDouble = new XStringConverter<Double>()
	{
		public Double convert(String s)
		{
			return Double.valueOf( s );
		}
	};
}
