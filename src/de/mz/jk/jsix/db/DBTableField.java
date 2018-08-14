package de.mz.jk.jsix.db;

import java.io.Serializable;

/**
 * DBTableField defines a common MS Access table field
 * @author J.Kuharev
 */
public class DBTableField implements Serializable
{
	/**
	 * possible data types
	 */
	public enum DataType
	{
		// integer number types
		/** single bit */
		BIT,
		/** small integer */
		BYTE,
		/** normal integer */ 
		SHORT, 
		/** long integer */ 
		LONG,
		/** auto increment integer */
		COUNTER, 
		
		// floating point numbers
		/** single precision */
		SINGLE,	
		/** double precision */
		DOUBLE,
		
		// strings
		/** small strings having defined maximum length */
		VARCHAR, 
		/** long text */
		LONGTEXT;
	}
	
	/**
	 * possible flags
	 */
	public enum FlagType
	{
		/** the primary key */
		PRIMARY_KEY{ public String toString(){return "PRIMARY KEY";} },
		/** foreign key */
		KEY,
		/** unique index */
		UNIQUE,
		/** index */
		INDEX,
		/** not null */
		NOT_NULL{ public String toString(){return "NOT NULL";} },
		/** null */
		NULL;
	}
	
	public String 	name = "";
	public DataType type = DataType.VARCHAR;
	public int		size = 255;
	public FlagType	flag = FlagType.NULL;
	
	public DBTableField(String name, DataType type)
	{
		this.name = name;
		this.type = type;
	}
	
	public DBTableField(String name, DataType type, FlagType flag)
	{
		this(name, type);
		this.flag = flag;
	}
	
	public DBTableField(String name, DataType type, int size)
	{
		this(name, type);
		this.size = size;
	}
	
	public DBTableField(String name, DataType type, int size, FlagType flag)
	{
		this(name, type, size);
		this.flag = flag;
	}
	
	/**
	 * @return type of this column parsed to MS Access data type
	 */
	public String getMSAccessTypeDef()
	{
		switch(type)
		{
			case VARCHAR:
				return "VARCHAR("+size+")";
			default:
				return type.toString();
		}
	}
	
	/**
	 * @return type of this column parsed to MySQL data type
	 */
	public String getMySQLTypeDef()
	{
		switch(type)
		{
			case COUNTER:
				return "BIGINT NOT NULL AUTO_INCREMENT";
			case SINGLE:
				return "FLOAT";
			case BYTE:
				return "SMALLINT";
			case SHORT:
				return "INTEGER";
			case LONG:
				return "BIGINT";
			case VARCHAR:
				return "VARCHAR("+size+") DEFAULT ''";
			default:
				return type.toString();
		}
		
	}
}
