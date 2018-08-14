package de.mz.jk.jsix.mysql;
import java.util.HashSet;
import java.util.Set;

import de.mz.jk.jsix.db.DBTable;
import de.mz.jk.jsix.db.DBTableField;

public class MySQLTableCreator 
{
	public static void main(String[] args) throws Exception 
	{
		MySQL db = new MySQL("localhost", "ppc", "root", "", true);
		MySQLTableCreator c = new MySQLTableCreator( db );
		DBTable t = new DBTable("LOCTREE");
			t.addField(new DBTableField("id", DBTableField.DataType.LONG, DBTableField.FlagType.PRIMARY_KEY) );
			t.addField(new DBTableField("KeyWordLocation", DBTableField.DataType.VARCHAR, 50) );
			t.addField(new DBTableField("KeyWordConfidence", DBTableField.DataType.SHORT) );
			t.addField(new DBTableField("PredictedLocation", DBTableField.DataType.VARCHAR, 50) );
			t.addField(new DBTableField("PredictedReliability", DBTableField.DataType.BYTE) );

		c.initTable(t);
	}
	
	MySQL db = null;
	
	public MySQLTableCreator(MySQL db)
	{
		this.db = db;
	}
	
	public void initTable(DBTable t) 
	{
		if( db.tableExists(t.name) )
		{
			System.out.println("Table " + t.name + " exists, redefine columns ... ");
			checkTable(t);
		}
		else
		{
			System.out.println("Creating table " + t.name + " ... ");
			createTable( t );
		}
	}

	private void createTable(DBTable t)
	{
		String sql = "CREATE TABLE IF NOT EXISTS `"+t.name+"`( \n";
		String keys = "";
		for(int i=0; i<t.fields.size(); i++)
		{
			DBTableField f = t.fields.get(i);

			sql += (i>0?",":"") + " `"+f.name+"` " + f.getMySQLTypeDef();
			
			switch(f.flag)
			{
				case PRIMARY_KEY:
					keys += ", PRIMARY KEY (`"+f.name+"`) \n";
					break;
				case INDEX:
				case KEY:
					keys += ", INDEX (`"+f.name+"`) \n";
					break;
				case UNIQUE:
					keys += ", UNIQUE (`"+f.name+"`) \n";
					break;
				default:
			}
		}
		
		sql += keys + "\n) ENGINE=MYISAM";
		db.executeSQL(sql);
	}

	public void checkTable(DBTable t) 
	{
		Set<String> colSet = new HashSet<String>( db.listColumns(t.name) ); 
		for(DBTableField f : t.fields)
		{
			if(colSet.contains(f.name))
			{
				db.executeSQL( "ALTER TABLE `" + t.name + "` MODIFY `" + f.name +"` " + f.getMySQLTypeDef() );
			}
			else
			{
				db.executeSQL( "ALTER TABLE `" + t.name + "` ADD COLUMN `" + f.name +"` " + f.getMySQLTypeDef() );
				switch(f.flag)
				{
					case PRIMARY_KEY:
						db.executeSQL("ALTER TABLE `" + t.name + "` ADD PRIMARY KEY (`"+f.name+"`)");
						break;
					case INDEX:
					case KEY:
						db.createIndex(t.name, f.name);
						break;
					case UNIQUE:
						db.executeSQL("ALTER TABLE `" + t.name + "` ADD UNIQUE (`"+f.name+"`)");
						break;
					default:
				}
			}
		}
		db.executeSQL("ALTER TABLE `"+t.name+"` ENGINE=MYISAM");
		db.optimizeTable(t.name);
	}
		
//	/**
//	 * lists table names
//	 * @return list of table names
//	 */
//	public List<String> listTables()
//	{
//		return db.listTables();
//	}
//	
//	/**
//	 * creates a new table with given table name 
//	 * does nothing if table already exists	
//	 * @param tableName name of new table
//	 */
//	public void createTable(String tableName)
//	{
//		if(isDefinedTable(tableName)) return;
//		String sql="CREATE TABLE `"+tableName+"`";
//		try{
//			stmt.execute(sql);
//		}catch (SQLException e){
//			e.printStackTrace();
//			System.err.println("SQL Error on: "+sql);
//		}
//	}
//	
//	/**
//	 * executes sql statement and tests if it was successfull
//	 * @param sql
//	 * @return fals if some errors occure, true if not
//	 */
//	private boolean execSQL(String sql) 
//	{
//		try{
//			stmt.execute(sql);
//			return true;
//		}catch (SQLException e){
//			e.printStackTrace();
//			System.err.println(sql);
//		}
//		return false;
//	}
}
