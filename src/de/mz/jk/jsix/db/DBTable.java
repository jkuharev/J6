package de.mz.jk.jsix.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DBTable defines a general database table
 * @author J.Kuharev
 */
public class DBTable implements Serializable
{
	public String name = "";
	public List<DBTableField> fields = new ArrayList<DBTableField>();
	
	public DBTable(String name)
	{
		this.name = name;
	}
	
	public DBTable(String name, List<DBTableField> fields)
	{
		this.name = name;
		this.fields = fields;
	}
	
	public void addField(DBTableField field)
	{
		fields.add(field);
	}
	
	public void removeField(String fieldName)
	{
		for(DBTableField f : fields)
		{
			if(f.name.equalsIgnoreCase(fieldName))
				fields.remove(f);
		}
	}
}