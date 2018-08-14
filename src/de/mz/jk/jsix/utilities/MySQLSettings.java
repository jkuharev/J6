package de.mz.jk.jsix.utilities;



import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import de.mz.jk.jsix.mysql.MySQL;

/**
 * <h3>Settings</h3>
 * flat file configuration storage using Java properties
 * @author Joerg Kuharev
 * @version 2011.02.17 11:30
 */
public class MySQLSettings 
{
	private static final long serialVersionUID = 20110217L;
	private MySQLProperties properties = null;
	
	/**
	 * configuration storage by given file path
	 * @param filePath
	 * @param titleComment
	 */
	public MySQLSettings(MySQL db, String table)
	{
		this.properties = new MySQLProperties(db, table);
	}
	
	/**
	 * store properties to file system
	 */
	public void store()
	{
		properties.store();
	}
	
	/**
	 * check if a key is there
	 * @param key
	 * @return true or false
	 */
	public boolean isKey(String key)
	{
		return (properties.getProperty(key) != null);
	}
	
	/**
	 * load a key named value from project global configuration file,<br>
	 * if the key does not exist a key->value pair will be created
	 * using defaultValue and it will be returned
	 * @param key
	 * @param defaultValue
	 * @return value of key named property
	 */
	public String getValue(String key, String defaultValue)
	{
		try{
			return getValue(key);
		}catch(Exception e){
			setValue(key, defaultValue);
			return defaultValue;
		}
	}
	/**
	 * load a key named value from project global configuration file,<br>
	 * if the key does not exist a key->value pair will be created
	 * using defaultValue and it will be returned
	 * @param key
	 * @param defaultValue
	 * @return value of key named property
	 */
	public double getValue(String key, double defaultValue)
	{
		try{
			return Double.parseDouble(getValue(key));
		}catch(Exception e){
			setValue(key, defaultValue+"");
			return defaultValue;
		}
	}
	/**
	 * load a key named value from project global configuration file,<br>
	 * if the key does not exist a key->value pair will be created
	 * using defaultValue and it will be returned
	 * @param key
	 * @param defaultValue
	 * @return value of key named property
	 */
	public float getValue(String key, float defaultValue)
	{
		try{
			return Float.parseFloat(getValue(key));
		}catch(Exception e){
			setValue(key, defaultValue+"");
			return defaultValue;
		}
	}
	/**
	 * load a key named value from project global configuration file,<br>
	 * if the key does not exist a key->value pair will be created
	 * using defaultValue and it will be returned
	 * @param key
	 * @param defaultValue
	 * @return value of key named property
	 */
	public int getValue(String key, int defaultValue)
	{
		try{
			return Integer.parseInt(getValue(key));
		}catch(Exception e){
			setValue(key, defaultValue+"");
			return defaultValue;
		}
	}
	/**
	 * load a key named value from project global configuration file,<br>
	 * if the key does not exist a key->value pair will be created
	 * using defaultValue and it will be returned
	 * @param key
	 * @param defaultValue
	 * @return value of key named property
	 */
	public boolean getValue(String key, boolean defaultValue)
	{
		try{
			return Boolean.parseBoolean(getValue(key));
		}catch(Exception e){
			setValue(key, Boolean.toString(defaultValue));
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
		if(properties.containsKey(key)) 
			return properties.getProperty(key);
		else
			throw new Exception("key '"+key+"' is not defined!");					
	}
	
	/**
	 * save a key named value to project global configuration file
	 * @param key
	 * @param value
	 */
	public synchronized void setValue(String key, String value)
	{
		properties.setProperty(key, value);
	}

	/**
	 * store array of objects' string representations
	 * @param key
	 * @param values array of objects
	 */
	public synchronized void setArray(String key, Object[] values)
	{	
		String array = "";
		for(Object v : values)
		{
			array += v.toString().replaceAll("\n", "{ENDL}") + "\n";
		}		
		
		properties.setProperty(key, array);
	}
	
	/**
	 * store array of objects' string representations
	 * @param key
	 * @param values array of objects
	 */
	public synchronized String[] getArray(String key, Object[] defaultValues)
	{		
		if( !properties.containsKey(key) )
		{
			setArray(key, defaultValues);
		}		
		
		String allValues = properties.getProperty(key);
		String[] res = allValues.split("\n");
		for(int i=0; i<res.length; i++) res[i] = res[i].replace("{ENDL}", "\n");
		return res;
	}
	
	/**
	 * remove a key/value pair from configuration file
	 * @param key
	 */
	public synchronized void remove(String key)
	{
		properties.remove(key);
	}
	

/**
 * 
 * <h3>{@link MySQLProperties}</h3>
 * @author Joerg Kuharev
 * @version 17.03.2011 14:21:31
 */
	public class MySQLProperties extends Properties
	{
		private static final long serialVersionUID = 20110217L;
		private static final int MAX_LINE_LENGTH = 80;
		private MySQL db = null;
		private String table = "settings";
				
		/**
		 * loaded from a file
		 * @param path
		 */
		public MySQLProperties(MySQL db, String table)
		{
			this.db = db;
			this.table = table;
			init();
			load();	
		}
		
		/**
		 * 
		 */
		private void init()
		{
			db.executeSQL("CREATE TABLE IF NOT EXISTS `"+table+"` (k TEXT, v TEXT, FULLTEXT INDEX(k), FULLTEXT INDEX(v))");
		}

		/**
		 * load from file
		 */
		public void load()
		{
			try
			{
				db.executeSQL("OPTIMIZE TABLE `"+table+"`");
				ResultSet rs = db.executeSQL("SELECT k, v FROM `"+table+"`");
				while(rs.next())
				{
					this.setProperty(rs.getString("k"), rs.getString("v"));
				}
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

		/**
		 * store into file
		 */
		private void store() 
		{
			try
			{
				Enumeration keys = this.keys();
				while(keys.hasMoreElements())
				{
					String k = keys.nextElement().toString();
					String v = getProperty(k);
					db.executeSQL("INSERT INTO `"+table+"` SET k='" + URLEncoder.encode(k) + "', v='" + URLEncoder.encode(v) + "'");
				}
				db.executeSQL("OPTIMIZE TABLE `"+table+"`");
			} 
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		@SuppressWarnings("unchecked")
		public synchronized Enumeration keys() 
		{
			Enumeration keysEnum = super.keys();
			Vector keyList = new Vector();
			while(keysEnum.hasMoreElements())
			{
				keyList.add(keysEnum.nextElement());
			}
			Collections.sort(keyList);
			return keyList.elements();
		}
	}
}
