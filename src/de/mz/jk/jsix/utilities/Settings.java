package de.mz.jk.jsix.utilities;

import java.io.*;
import java.util.*;

/**
 * <h3>Settings</h3>
 * flat file configuration storage using Java properties
 * @author Joerg Kuharev
 * @version 2011.02.17 11:30
 */
public class Settings
{
	private static final long serialVersionUID = 20130307L;
	public final static String defaultConfigFilePath = "config.ini";
	public final static String defaultConfigFileComment = "configuration file";
	private String cfgFilePath = defaultConfigFilePath;
	private String cfgTitleComment = defaultConfigFileComment;
	private SortedProperties prop = null;
	private boolean rereadOnGet = true;

	/**
	 * default settings object referencing 
	 * config file located as ./config.ini
	 */
	public Settings()
	{
		this(defaultConfigFilePath, defaultConfigFileComment);
	}

	/**
	 * configuration storage by given file path
	 * @param cfgFilePath
	 * @param titleComment
	 */
	public Settings(String filePath, String titleComment)
	{
		setTitleComment(titleComment);
		setConfigurationFilePath(filePath);
	}

	/**
	 * set if the config file should be loaded on every get/set value request.
	 * default behavior is to update values on every set/get.
	 * @param reread
	 */
	public void shouldRereadConfigFileOnEachRequest(boolean reread)
	{
		rereadOnGet = reread;
	}

	/**
	 * set a comment shown in title of configuration file
	 * @param titleComment
	 */
	public void setTitleComment(String titleComment)
	{
		this.cfgTitleComment = titleComment;
	}

	/**
	 * set file path for storing configuration data
	 * @param cfgFilePath
	 */
	public void setConfigurationFilePath(String filePath)
	{
		this.cfgFilePath = filePath;
	}

	public String getTitleComment()
	{
		return cfgTitleComment;
	}

	public String getConfigurationFilePath()
	{
		return cfgFilePath;
	}

	/**
	 * THIS IS NOT THE CONTENT OF CONFIG FILE
	 * string build of config file path and comment
	 * e.g. "configuration file (config.ini)"
	 */
	@Override public String toString()
	{
		return cfgTitleComment + " (cfgFilePath)";
	}

	/**
	 * array of key-value-pairs as String arrays
	 * { {key1, value1}, {key2, value2}, ... }
	 * @return
	 */
	public String[][] toKVArray()
	{
		if (prop == null || rereadOnGet) reread();
		Object[] keys = prop.keySet().toArray();
		String[][] res = new String[keys.length][2];
		for (int i = 0; i < keys.length; i++)
		{
			String key = keys[i].toString();
			res[i] = new String[] { key, prop.getProperty(key) };
		}
		return res;
	}

	/**
	 * initialize and get properties object
	 * @return settings as java properties 
	 */
	public Properties getProperties()
	{
		if (prop == null || rereadOnGet) reread();
		return prop;
	}

	/**
	 * reread configuration file
	 */
	private void reread()
	{
		this.prop = new SortedProperties(cfgFilePath, cfgTitleComment);
	}

	/** 
	 * @return set of known keys 
	 */
	public Set<Object> getKeys()
	{
		if (prop == null || rereadOnGet) reread();
		return prop.keySet();
	}

	/** 
	 * @return set of known keys 
	 */
	public List<String> getKeysAsSortedStringList()
	{
		Set<Object> keySet = getKeys();
		ArrayList<String> keyList = new ArrayList<String>(keySet.size());
		for (Object key : keySet)
			keyList.add((String) key);
		Collections.sort(keyList);
		return keyList;
	}

	/**
	 * load a key named value from project global configuration file,<br>
	 * if the key does not exist a key->value pair will be created
	 * using defaultValue and it will be returned
	 * @param key
	 * @param defaultValue
	 * @param readOnly
	 * @return value of key named property
	 */
	public String getStringValue(String key, String defaultValue, boolean readOnly)
	{
		try
		{
			return getValue(key);
		}
		catch (Exception e)
		{
			if (!readOnly) setValue(key, defaultValue);
			return defaultValue;
		}
	}

	/**
	 * load a key named value from project global configuration file,<br>
	 * if the key does not exist a key->value pair will be created
	 * using defaultValue and it will be returned
	 * @param key
	 * @param defaultValue
	 * @param readOnly
	 * @return value of key named property
	 */
	public double getDoubleValue(String key, double defaultValue, boolean readOnly)
	{
		try
		{
			return Double.parseDouble(getValue(key));
		}
		catch (Exception e)
		{
			if (!readOnly) setValue(key, defaultValue + "");
			return defaultValue;
		}
	}

	/**
	 * load a key named value from project global configuration file,<br>
	 * if the key does not exist a key->value pair will be created
	 * using defaultValue and it will be returned
	 * @param key
	 * @param defaultValue
	 * @param readOnly
	 * @return value of key named property
	 */
	public float getFloatValue(String key, float defaultValue, boolean readOnly)
	{
		try
		{
			return Float.parseFloat(getValue(key));
		}
		catch (Exception e)
		{
			if (!readOnly) setValue(key, defaultValue + "");
			return defaultValue;
		}
	}

	/**
	 * load a key named value from project global configuration file,<br>
	 * if the key does not exist a key->value pair will be created
	 * using defaultValue and it will be returned
	 * @param key
	 * @param defaultValue
	 * @param readOnly
	 * @return value of key named property
	 */
	public int getIntValue(String key, int defaultValue, boolean readOnly)
	{
		try
		{
			return Integer.parseInt(getValue(key));
		}
		catch (Exception e)
		{
			if (!readOnly) setValue(key, defaultValue + "");
			return defaultValue;
		}
	}

	/**
	 * load a key named value from project global configuration file,<br>
	 * if the key does not exist a key->value pair will be created
	 * using defaultValue and it will be returned
	 * @param key
	 * @param defaultValue
	 * @param readOnly
	 * @return value of key named property
	 */
	public boolean getBooleanValue(String key, boolean defaultValue, boolean readOnly)
	{
		try
		{
			return Boolean.parseBoolean(getValue(key));
		}
		catch (Exception e)
		{
			if (!readOnly) setValue(key, Boolean.toString(defaultValue));
			return defaultValue;
		}
	}

	/**
	 * load a key named value from project global configuration file
	 * @param key
	 * @return value of key named property
	 * @throws Exception 
	 */
	public synchronized String getValue(String key) throws Exception
	{
		if (prop == null || rereadOnGet) reread();
		if (prop.containsKey(key))
			return prop.getProperty(key);
		else throw new Exception("unknown property '" + key + "'!");
	}

	/**
	 * check if configuration key is already defined
	 * @param key
	 * @return true if the is known, otherwise false
	 */
	public synchronized boolean isSet(String key)
	{
		if (prop == null || rereadOnGet) reread();
		return prop.containsKey(key);
	}

	/**
	 * save a key named value to project global configuration file
	 * @param key
	 * @param value any object implementing toString() method
	 */
	public synchronized void setValue(String key, Object value)
	{
		if (prop == null || rereadOnGet) reread();
		prop.setProperty(key, value.toString());
		prop.store();
	}

	/**
	 * store array of objects as strings
	 * @param key
	 * @param values array of objects
	 */
	public synchronized void setArray(String key, Object[] values)
	{
		String array = "";
		for (Object v : values)
		{
			array += v.toString().replaceAll("\n", "{ENDL}") + "\n";
		}
		if (prop == null || rereadOnGet) reread();
		prop.setProperty(key, array);
		prop.store();
	}

	/**
	 * store array of objects' string representations
	 * @param key
	 * @param values array of objects
	 * @param readOnly
	 */
	public synchronized String[] getArray(String key, Object[] defaultValues, boolean readOnly)
	{
		if (!prop.containsKey(key))
			setArray(key, defaultValues);
		else if (prop == null || rereadOnGet) reread();
		String allValues = prop.getProperty(key);
		String[] res = allValues.split("\n");
		for (int i = 0; i < res.length; i++)
		{
			res[i] = res[i].replace("{ENDL}", "\n");
		}
		return res;
	}

	/**
	 * remove a key/value pair from configuration file
	 * @param key
	 */
	public synchronized void remove(String key)
	{
		if (prop == null || rereadOnGet) reread();
		prop.remove(key);
		prop.store();
	}

	/**
	 * read config file line by line
	 * @return
	 */
	public String readToString()
	{
		String res = "";
		try
		{
			BufferedReader input = new BufferedReader(new FileReader(cfgFilePath));
			try
			{
				String line = null;
				while ((line = input.readLine()) != null)
				{
					res += line + "\n";
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				input.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * write config file from string
	 */
	public void saveFromString(String configFileContent)
	{
		try
		{
			BufferedWriter output = new BufferedWriter(new FileWriter(cfgFilePath));
			try
			{
				output.write(configFileContent);
				output.flush();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				output.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		reread();
	}

	/**
	 * explicitly store all properties to config file
	 */
	public void store()
	{
		prop.store();
	}

	/**
	 * <h3>SortedProperties</h3>
	 * 
	 * @author Joerg Kuharev
	 * @version 29.12.2010 09:59:27
	 */
	public static class SortedProperties extends Properties
	{
		private static final long serialVersionUID = 20110217L;
		private static final int MAX_LINE_LENGTH = 80;
		private String path = "config.ini";
		private String comment = "";

		/**
		 * empty properties
		 */
		public SortedProperties()
		{}

		/**
		 * loaded from a file
		 * @param path
		 */
		public SortedProperties(String path, String comment)
		{
			this.path = path;
			this.comment = comment;
			load();
		}

		/**
		 * load from given properties file
		 * @param path
		 */
		public void load(String path)
		{
			this.path = path;
		}

		/**
		 * load from file
		 */
		public void load()
		{
			File file = new File(path);
			BufferedInputStream in = null;
			try
			{
				if (!file.exists())
				{
					System.out.print("file '" + path + "' not found, creating file ... ");
					file.createNewFile();
					System.out.println("[done]");
				}
				in = new BufferedInputStream(new FileInputStream(file));
				load(in);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					in.close();
				}
				catch (Exception e2)
				{}
			}
		}

		/**
		 * store into given file
		 * @param path file path
		 * @param comment
		 */
		public void store(String path, String comment)
		{
			this.path = path;
			this.comment = comment;
			store();
		}

		/**
		 * store into file
		 */
		private void store()
		{
			BufferedOutputStream out = null;
			try
			{
				out = new BufferedOutputStream(new FileOutputStream(path));
				store(out, comment);
			}
			catch (Exception e)
			{
				System.err.println("unable to write file: " + path);
			}
			finally
			{
				try
				{
					out.close();
				}
				catch (Exception e2)
				{}
			}
		}

		@SuppressWarnings("unchecked") public synchronized Enumeration keys()
		{
			Enumeration keysEnum = super.keys();
			Vector keyList = new Vector();
			while (keysEnum.hasMoreElements())
			{
				keyList.add(keysEnum.nextElement());
			}
			Collections.sort(keyList);
			return keyList.elements();
		}

		/** 
		 * let's make some writing rules for the property file:<br> 
		 * each comment line starts with '# ',
		 * key-value separator is '=',
		 * a value is splitted in multiple lines 
		 * if its length is more than 80 chars and if it contains multiple lines
		 */
		public void store(OutputStream os, String comments)
		{
			PrintStream out = new PrintStream(os);
			// write comments
			String[] cmts = comments.split("\n");
			for (String cmt : cmts)
				out.println("# " + cmt);
			Enumeration<String> keys = this.keys();
			while (keys.hasMoreElements())
			{
				String k = keys.nextElement();
				String v = this.getProperty(k);
				writeProperty(k, v, out);
			}
			out.flush();
			out.close();
		}

		/**
		 * @param k the key
		 * @param v the value
		 * @param out print stream
		 */
		private void writeProperty(String k, String v, PrintStream out)
		{
			v = v.replaceAll("\\\\", "\\\\\\\\");
			v = v.replaceAll("\n", "\\\\n");
			out.println(k + "=" + v);
		}
	}
}
