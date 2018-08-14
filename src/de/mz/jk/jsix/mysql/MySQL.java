package de.mz.jk.jsix.mysql;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mz.jk.jsix.utilities.Bencher;

/**
 * wrap JDBC access to a MySQL database
 * <h3>MySQL</h3>
 * common operations for MySQL database
 * @author Joerg Kuharev
 * @version 07.01.2011 10:39:33
 */
public class MySQL
{
	public static enum StorageEngine
	{
		MyISAM, InnoDB, Memory, Heap, Merge, Archive, CSV, Federated, NDB;
	}

	public static boolean DEFAULT_EXTRA_VERBOSITY = false;
	public static SQLBatchExecutionListener defExecListener = new StdOutSQLBatchExecutionAdapter();
	
	private String pass = "";
	private String user = "root";
	private String schema = "mysql";
	private String host = "localhost";
	private Connection con = null;
	private Statement stmt = null;
	private StorageEngine defaultStorageEngine = StorageEngine.MyISAM;
	private StorageEngine temporaryStorageEngine = StorageEngine.MyISAM;

	/**
	 * create a MySQL object and connect to given database
	 * @param host
	 * @param schema
	 * @param user
	 * @param pass
	 */
	public MySQL(String host, String schema, String user, String pass) // throws
// Exception
	{
		this.setHost(host);
		this.setSchema(schema);
		this.setUser(user);
		this.setPass(pass);
	}

	/**
	 * create a MySQL object and enforce connection to given database
	 * @param host
	 * @param schema
	 * @param user
	 * @param pass
	 * @param connect if true a db connection will be enforced
	 */
	public MySQL(String host, String schema, String user, String pass, boolean connect)
	{
		this(host, schema, user, pass);
		if (connect) getForcedConnection();
	}

	/**
	 * create a MySQL object using existing connection object
	 * @param con
	 */
	public MySQL(Connection con)
	{
		setConnection(con);
	}

	/**
	 * use setter functions to set database options,<br>
	 * or call use MySQL(host, db, user, pass)
	 */
	public MySQL()
	{}

	/**
	 * @param defaultStorageEngine the defaultStorageEngine to set
	 */
	public void setDefaultStorageEngine(StorageEngine defaultStorageEngine)
	{
		this.defaultStorageEngine = defaultStorageEngine;
	}

	/**
	 * @return the defaultStorageEngine
	 */
	public StorageEngine getDefaultStorageEngine()
	{
		return defaultStorageEngine;
	}

	/**
	 * @param temporaryStorageEngine the temporaryStorageEngine to set
	 */
	public void setTemporaryStorageEngine(StorageEngine temporaryStorageEngine)
	{
		this.temporaryStorageEngine = temporaryStorageEngine;
	}

	/**
	 * @return the temporaryStorageEngine
	 */
	public StorageEngine getTemporaryStorageEngine()
	{
		return temporaryStorageEngine;
	}

	/**
	 * manually set the connection,
	 * while manually setting the connection,
	 * other database information 
	 * like host, schema, etc. is not checked!
	 * @param con
	 */
	public synchronized void setConnection(Connection con)
	{
		setConnection( con, defaultStorageEngine, temporaryStorageEngine );
	}

	/**
	 * manually set the connection,
	 * while manually setting the connection,
	 * other database information 
	 * like host, schema, etc. is not checked!
	 * @param con
	 * @param defaultStorageEngine the default storage engine to use
	 */
	private synchronized void setConnection(Connection con, StorageEngine defaultStorageEngine, StorageEngine temporaryStorageEngine)
	{
		this.con = con;
		try
		{
			stmt = con.createStatement();
			try{stmt.execute( "SET default_storage_engine=" + defaultStorageEngine.toString() );} catch(Exception e){}
			try{stmt.execute( "SET storage_engine=" + defaultStorageEngine.toString() );} catch(Exception e){}
			try{stmt.execute( "SET default_tmp_storage_engine=" + defaultStorageEngine.toString() );} catch(Exception e){}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/** @return Connection object */
	public synchronized Connection getConnection(boolean verbose)
	{
		try
		{
			if (con == null || con.isClosed() || !con.isValid(10))
			{
				Connection newCon = getConnection(host, schema, user, pass, verbose);
				setConnection( newCon );
			}
		}
		catch (Exception e)
		{
			System.err.println("unable to establish connection to database");
			System.err.println("host:	" + host);
			System.err.println("user:	" + user);
			System.err.println("pass:	" + pass);
			System.err.println("schema: " + schema);
		}
		return con;
	}

	/** @return Connection object */
	public synchronized Connection getConnection()
	{
		return getConnection(true);
	}

	/** @return Connection object */
	public synchronized Connection getForcedConnection(boolean verbose)
	{
		if (con == null) setConnection( getForcedConnection( host, schema, user, pass, verbose ) );
		return con;
	}

	/** @return Connection object */
	public synchronized Connection getForcedConnection()
	{
		return getForcedConnection(true);
	}

	/**
	 * @return global statement for current connection
	 */
	public Statement getStatement()
	{
		if (con == null) getConnection();
		return stmt;
	}

	/** 
	 * close opened connection 
	*/
	public synchronized void closeConnection(boolean verbose)
	{
		try
		{
			con.close();
			if (verbose)
			{
				System.out.println("connection to 'mysql://" + host + "/" + schema + "' terminated.");
			}
		}
		catch (Exception e)
		{}
		con = null;
	}

	/** close opened connection */
	public synchronized void closeConnection()
	{
		closeConnection(true);
	}

	/**
	 * connect to a mysql database
	 * @param host the host name or ip address
	 * @param db the database name
	 * @param user the user name
	 * @param pass the password
	 * @return Connection object
	 */
	public static Connection getConnection(String host, String db, String user, String pass)
	{
		return getConnection(host, db, user, pass, true);
	}

	/**
	 * connect to a mysql database
	 * @param host the host name or ip address
	 * @param db the database name
	 * @param user the user name
	 * @param pass the password
	 * @param verbose to be verbose while connecting or not
	 * @return Connection object
	 */
	public static Connection getConnection(String host, String db, String user, String pass, boolean verbose)
	{
		Connection con = null;
		String url = "";
		try
		{
			url = "jdbc:mysql://" + host + "/" + db + "?autoReconnect=true";
			try
			{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				if (verbose) System.out.println("\nloading MySQL driver: com.mysql.jdbc.Driver");
			}
			catch (Exception e)
			{
				try
				{
					Class.forName("org.mariadb.jdbc.Driver").newInstance();
					if (verbose) System.out.println("\nloading MySQL driver: org.mariadb.jdbc.Driver");
				}
				catch (Exception ex)
				{
					System.err.println("\nunable to load MySQL driver!");
				}
			}
			con = DriverManager.getConnection(url, user, pass);
			if (verbose) System.out.println("connection successfully established to 'mysql://" + host + "/" + db + "'");
		}
		catch (Exception e)
		{
			if (verbose)
			{
				System.err.println("unable to connect to the database 'mysql://" + host + "/" + db + "'");
				e.printStackTrace();
			}
		}
		return con;
	}

	/**
	 * try to connect, if connecting fails try to create new db and connect to it
	 * @return Connection
	 */
	public static Connection getForcedConnection(String host, String db, String user, String pass, boolean verbose)
	{
		Connection con = getConnection(host, db, user, pass, verbose);
		if (con == null)
		{
			try
			{
				if (verbose) System.out.print("schema '" + db + "' does not exist.\n creating new db ... ");
				// if connecting fails then create DB and retry to connect
				MySQL DB = new MySQL(host, "mysql", user, pass);
				DB.getConnection(verbose);
				DB.dumpSQL("CREATE DATABASE IF NOT EXISTS `" + db + "`");
				if (verbose) System.out.println("[ok]");
				DB.closeConnection(verbose);
				if (verbose) System.out.println("connecting to '" + db + "' ... ");
				con = getConnection(host, db, user, pass, verbose);
			}
			catch (Exception ex)
			{
				if (verbose)
				{
					System.out.println("Forcing schema creation has failed!");
					ex.printStackTrace();
				}
			}
		}
		return con;
	}

	/**
	 * try to connect to a db schema using existing db access data, 
	 * if connecting fails then try to create a new db schema and connect to it
	 * @return Connection
	 */
	public static Connection getForcedConnection(MySQL db, String schema)
	{
		return getForcedConnection(db.getHost(), schema, db.getUser(), db.getPass(), true);
	}

	/**
	 * try to connect to a db schema using existing db access data, 
	 * if connecting fails then try to create a new db schema and connect to it
	 * @return Connection
	 */
	public static Connection getForcedConnection(MySQL db, String schema, boolean verbose)
	{
		return getForcedConnection(db.getHost(), schema, db.getUser(), db.getPass(), verbose);
	}

	/**
	 * try to connect to a db schema using existing db access data, 
	 * if connecting fails then try to create a new db schema and connect to it
	 * @return Connection
	 */
	public static Connection getForcedConnection(MySQL db)
	{
		return getForcedConnection(db.getHost(), db.getSchema(), db.getUser(), db.getPass(), true);
	}

	/**
	 * try to connect to a db schema using existing db access data, 
	 * if connecting fails then try to create a new db schema and connect to it
	 * @return Connection
	 */
	public static Connection getForcedConnection(MySQL db, boolean verbose)
	{
		return getForcedConnection(db.getHost(), db.getSchema(), db.getUser(), db.getPass(), verbose);
	}

	public void setPass(String pass)
	{
		this.pass = pass;
	}

	public String getPass()
	{
		return pass;
	}

	public void setUser(String user)
	{
		this.user = user;
	}

	public String getUser()
	{
		return user;
	}

	public void setSchema(String schema)
	{
		this.schema = schema;
	}

	public String getSchema()
	{
		return schema;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public String getHost()
	{
		return host;
	}

	public String toString()
	{
		return "host: " + host + "\n" +
				"user: " + user + "\n" +
				"pass: " + pass + "\n" +
				"db:   " + schema;
	}

	/**
	 * creates a new MySQL Object that will connect to given db name
	 * @param schemaName
	 * @return MySQL Object or null if something goes wrong
	 */
	public synchronized MySQL getDB(String schemaName)
	{
		try
		{
			return new MySQL(host, schemaName, user, pass);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * execute a single sql query and parses the result to a string
	 * @param sql
	 * @return result dumped to a string
	 * @throws Exception
	 */
	public synchronized String dumpSQL(String sql) throws Exception
	{
		String res = "";
		boolean hasRes = stmt.execute(sql);
		if (!hasRes) return "successfully executed.";
		ResultSet rs = stmt.getResultSet();
		ResultSetMetaData rsmd = rs.getMetaData();
		int n = rsmd.getColumnCount();
		// Get the column names; column indices start from 1
		for (int i = 1; i <= n; i++)
		{
			res +=
					rsmd.getTableName(i) + "." +
							rsmd.getColumnName(i) + "\t";
		}
		res += "\n";
		// walk through result lines
		for (int j = 0; rs.next(); j++)
		{
			for (int i = 1; i <= n; i++)
				res += rs.getString(i);
			res += "\n";
		}
		return res;
	}

	/**
	 * execute sql file using default execution listener
	 * @param filePath
	 */
	public synchronized ResultSet executeSQLFile(String filePath)
	{
		return executeSQLFile(filePath, defExecListener);
	}

	/**
	 * execute sql file
	 * @param filePath
	 * @param execListener parser for replacing template variables, or null to suppress parsing 
	 */
	public synchronized ResultSet executeSQLFile(String filePath, SQLBatchExecutionListener execListener)
	{
		ResultSet res = null;
		try
		{
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							ClassLoader.getSystemResourceAsStream(filePath)
					)
					);
			String line = "";
			String sql = "";
			while ((line = in.readLine()) != null)
			{
				if (line.startsWith("--"))
				{
					if (execListener != null)
						execListener.sqlCommentNotification(line);
				}
				else
				{
					sql += "\n" + line;
					if (line.trim().endsWith(";"))
					{
						res = executeSQL(sql, execListener);
						sql = "";
					}
				}
			}
			in.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * executes multiple sql statements from a string these are separated by ; delimiter
	 * @param sql
	 */
	public synchronized void executeMultipleSQLs(String sql)
	{
		executeMultipleSQLs(sql, null);
	}

	/**
	 * executes multiple sql statements from a string separated by ; delimiter
	 * and use listener for execution notifications
	 * @param sql
	 * @param executionListener
	 */
	public synchronized void executeMultipleSQLs(String sql, SQLBatchExecutionListener executionListener)
	{
		String[] SQL = sql.split(";\\s*\\n");
		for (String s : SQL)
		{
			s = s.trim() + " ;";
			if (s.length() > 0) executeSQL(s, executionListener);
		}
	}

	/**
	 * execute sql statement and notify a listener if available
	 * @param sql
	 * @param execListener
	 * @return result set
	 */
	public synchronized ResultSet executeSQL(String sql, SQLBatchExecutionListener execListener)
	{
		try
		{
			if (con == null || con.isClosed())
			{
				con = null;
				getConnection(false);
			}
			Long t = System.currentTimeMillis();
			if (execListener != null)
				sql = execListener.processSQLStatementBeforeExecution(sql);
			stmt.execute(sql);
			if (execListener != null)
				execListener.sqlStatementExecutedNotification(sql, System.currentTimeMillis() - t);
			return stmt.getResultSet();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (execListener != null) execListener.sqlStatementFailedNotification(sql, e);
			return null;
		}
	}

	/**
	 * execute a single sql statement
	 * with or without extra verbosity
	 * @param sql
	 */
	public synchronized ResultSet executeSQL(String sql, boolean extraVerbosity)
	{
		try
		{
			if (con == null || con.isClosed())
			{
				con = null;
				getConnection(false);
			}
			Bencher t = new Bencher(true);
			if (extraVerbosity)
			{
				System.out.println("-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --");
				System.out.println(sql);
			}
			stmt.execute(sql);
			if (extraVerbosity)
			{
				System.out.println("-- execution time: " + t.stop().getSecString());
				System.out.println("-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --");
			}
			return stmt.getResultSet();
		}
		catch (Exception e)
		{
			System.err.println("-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --");
			System.err.println("-- an error occured while executing sql statement:");
			System.err.println(sql);
			System.err.println("-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * execute a single sql statement
	 * with DEFAULT extra verbosity
	 * (see static constant DEFAULT_EXTRA_VERBOSITY)
	 * @param sql
	 */
	public synchronized ResultSet executeSQL(String sql)
	{
		return executeSQL(sql, DEFAULT_EXTRA_VERBOSITY);
	}

	/**
	 * number of modified rows in previously executed DML statement
	 * or zero in case of no number of rows can be determenied
	 * @param sql
	 * @return number of changed rows
	 */
	public synchronized int getUpdateCount()
	{
		try
		{
			return stmt.getUpdateCount();
		}
		catch (SQLException e)
		{
			return 0;
		}
	}

	/**
	 * list all existing databases
	 * @return List of databases 
	 */
	public synchronized List<String> listDatabases()
	{
		List<String> res = new ArrayList<String>();
		String sql = "show databases;";
		try
		{
			ResultSet rs = executeSQL(sql);
			while (rs.next())
				res.add(rs.getString(1));
		}
		catch (Exception e)
		{}
		return res;
	}

	/**
	 * list tables from connected database
	 * @return List of tables
	 */
	public synchronized List<String> listTables()
	{
		List<String> res = new ArrayList<String>();
		String sql = "show tables;";
		try
		{
			ResultSet rs = executeSQL(sql);
			while (rs.next())
				res.add(rs.getString(1));
		}
		catch (Exception e)
		{}
		return res;
	}

	/**
	 * maps table types to table names from connected database
	 * @return tables and their types
	 */
	public synchronized Map<String, String> listTableTypes()
	{
		Map<String, String> res = new HashMap<String, String>();
		String sql = "show full tables;";
		try
		{
			ResultSet rs = executeSQL(sql);
			while (rs.next())
				res.put(rs.getString(1), rs.getString(2));
		}
		catch (Exception e)
		{}
		return res;
	}

	/**
	 * list columns of a table from connected database
	 * @return List of columns 
	 */
	public synchronized List<String> listColumns(String table)
	{
		List<String> res = new ArrayList<String>();
		String sql = "explain `" + table + "`;";
		try
		{
			ResultSet rs = executeSQL(sql);
			while (rs.next())
				res.add(rs.getString(1));
		}
		catch (Exception e)
		{}
		return res;
	}

	/**
	 * check if a schema exists in database
	 * @param schemaName
	 * @return
	 */
	public synchronized boolean schemaExists(String schemaName)
	{
		List<String> dbs = listDatabases();
		for (String db : dbs)
			if (db.equalsIgnoreCase(schemaName))
				return true;
		return false;
	}

	/**
	 * check if a table exists in database
	 * @param tableName
	 * @return
	 */
	public synchronized boolean tableExists(String tableName)
	{
		List<String> tabs = listTables();
		for (String tab : tabs)
			if (tab.equalsIgnoreCase(tableName))
				return true;
		return false;
	}

	/**
	 * check if a column exists
	 * @param tableName
	 * @param columnName
	 * @return true if both table and column exist
	 */
	public synchronized boolean columnExists(String tableName, String columnName)
	{
		if (tableExists(tableName))
		{
			List<String> cols = listColumns(tableName);
			for (String col : cols)
				if (col.equalsIgnoreCase(columnName))
					return true;
		}
		return false;
	}

	/**
	 * truncate table
	 * @param tableName the name of table to truncate, DO NOT quote the table name
	 */
	public synchronized void truncateTable(String tableName)
	{
		executeSQL("TRUNCATE TABLE `" + tableName + "`");
	}

	/**
	 * drop table if exists
	 * @param tableName the table to drop, DO NOT quote the table name
	 */
	public synchronized void dropTable(String tableName)
	{
		executeSQL("DROP TABLE IF EXISTS `" + tableName + "`");
	}

	/**
	 * ATTENTION: removes an existing named database
	 * @param schemaName the name of schema to drop, DO NOT quote the schema name
	 */
	public synchronized void dropDatabase(String schemaName)
	{
		executeSQL("DROP DATABASE IF EXISTS `" + schemaName + "`;");
	}

	/**
	 * creates a named empty database
	 * @param schemaName
	 */
	public synchronized MySQL createDatabase(String schemaName)
	{
		executeSQL("CREATE DATABASE IF NOT EXISTS `" + schemaName + "`;");
		return getDB(schemaName);
	}

	/**
	 * update keys and indexes for a table
	 * @param tableName
	 */
	public synchronized void optimizeTable(String tableName)
	{
		executeSQL("OPTIMIZE TABLE `" + tableName + "`");
	}

	/**
	 * update keys and indexes for given tables
	 * @param tableNames names
	 */
	public synchronized void optimizeTables(Iterable<String> tableNames)
	{
		for (String tableName : tableNames)
			optimizeTable(tableName);
	}

	/** update keys and indexes for all tables */
	public synchronized void optimizeAllTables()
	{
		optimizeTables(listTables());
	}

	/**
	 * gets the first of a sinlge value from named table and column<br>
	 * requesting data by: SELECT colName FROM tableName WHERE condition 
	 * @param tableName the name of table
	 * @param colName the name of a column or an expression
	 * @param condition condition string or null for no condition to use
	 * @return first selected value
	 */
	public synchronized String getFirstValue(String tableName, String colName, String condition)
	{
		if (condition == null) condition = "1";
		try
		{
			ResultSet rs = executeSQL("SELECT " + colName + " FROM " + tableName + " WHERE " + condition);
			if (rs.next()) return rs.getString(1);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * get first returned value 
	 * @param sql
	 * @param column the number of column containing requested value, column numbering starts at 1
	 * @return first selected value
	 */
	public synchronized String getFirstValue(String sql, int column)
	{
		try
		{
			ResultSet rs = executeSQL(sql);
			if (rs.next()) return rs.getString(column);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * get first returned integer value,
	 * in case of error or empty result -1 is returned
	 * @param sql
	 * @param column the number of column containing requested value, column numbering starts at 1
	 * @return value from first selected row
	 */
	public synchronized int getFirstInt(String sql, int column)
	{
		try
		{
			ResultSet rs = executeSQL(sql);
			if (rs.next()) return rs.getInt(column);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * get first returned integer value,
	 * in case of error or empty result -1 is returned
	 * @param sql
	 * @param column the number of column containing requested value, column numbering starts at 1
	 * @return value from first selected row
	 */
	public synchronized double getFirstDouble(String sql, int column)
	{
		try
		{
			ResultSet rs = executeSQL(sql);
			if (rs.next()) return rs.getDouble(column);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * get query result as a list of lists of string,
	 * each list of strings represents columns of a row,
	 * first row 
	 * @param sql the query
	 * @param includeColNames if column numes should be included as first row
	 * @return
	 */
	public synchronized List<List<String>> listQuery(String sql, boolean includeColNames)
	{
		List<List<String>> res = new ArrayList<List<String>>();
		try
		{
			boolean hasRes = stmt.execute(sql);
			if (!hasRes) return res;
			ResultSet rs = stmt.getResultSet();
			ResultSetMetaData rsmd = rs.getMetaData();
			int n = rsmd.getColumnCount();
			List<String> row = null;
			if (includeColNames)
			{
				row = new ArrayList<String>(n);
				for (int i = 1; i <= n; i++)
					row.add(rsmd.getColumnName(i));
				res.add(row);
			}
			// walk through result lines
			for (int j = 0; rs.next(); j++)
			{
				row = new ArrayList<String>(n);
				for (int i = 1; i <= n; i++)
					row.add(rs.getString(i));
				res.add(row);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * get a list of values
	 * @param tableName
	 * @param colName
	 * @param condition
	 * @return
	 */
	public synchronized List<String> getStringValues(String tableName, String colName, String condition)
	{
		if (condition == null || condition.length() == 0) condition = "1";
		return getStringValues("SELECT " + colName + " FROM " + tableName + " WHERE " + condition);
	}

	/**
	 * get a list of values
	 * @param sql
	 * @return
	 */
	public synchronized List<String> getStringValues(String sql)
	{
		List<String> res = new ArrayList<String>();
		try
		{
			ResultSet rs = executeSQL(sql);
			while (rs.next())
				res.add(rs.getString(1));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * get a map of values
	 * @param sql
	 * @param keyColIndex keys 
	 * @param valueColIndex values
	 * @return
	 */
	public synchronized Map<String, String> getMap(String sql, int keyColIndex, int valueColIndex)
	{
		Map<String, String> res = new HashMap<String, String>();
		try
		{
			ResultSet rs = executeSQL(sql);
			while (rs.next())
				res.put(rs.getString(keyColIndex), rs.getString(valueColIndex));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * get a map of values
	 * @param sql
	 * @param keyColName keys 
	 * @param valueColName values
	 * @return
	 */
	public synchronized Map<String, String> getMap(String sql, String keyColName, String valueColName)
	{
		Map<String, String> res = new HashMap<String, String>();
		try
		{
			ResultSet rs = executeSQL(sql);
			while (rs.next())
				res.put(rs.getString(keyColName), rs.getString(valueColName));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * get a list of values
	 * @param tableName
	 * @param colName
	 * @param condition
	 * @return
	 */
	public synchronized List<Double> getDoubleValues(String tableName, String colName, String condition)
	{
		if (condition == null || condition.length() == 0) condition = "1";
		List<Double> res = new ArrayList<Double>();
		try
		{
			ResultSet rs = executeSQL("SELECT " + colName + " FROM " + tableName + " WHERE " + condition);
			while (rs.next())
				res.add(rs.getDouble(1));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * get a list of values
	 * @param tableName
	 * @param colName
	 * @param condition
	 * @return
	 */
	public synchronized List<Integer> getIntegerValues(String tableName, String colName, String condition)
	{
		if (condition == null || condition.length() == 0) condition = "1";
		List<Integer> res = new ArrayList<Integer>();
		try
		{
			ResultSet rs = executeSQL("SELECT " + colName + " FROM " + tableName + " WHERE " + condition);
			while (rs.next())
				res.add(rs.getInt(1));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * get a list of values
	 * @param sql
	 * @return
	 */
	public synchronized List<Integer> getIntegerValues(String sql)
	{
		List<Integer> res = new ArrayList<Integer>();
		try
		{
			ResultSet rs = executeSQL(sql);
			while (rs.next())
				res.add(rs.getInt(1));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * get a list of values (from first selected column)
	 * @param sql
	 * @return list of values
	 */
	public synchronized List<Double> getDoubleValues(String sql)
	{
		return getDoubleValues(sql, 1);
	}

	/**
	 * get a list of values
	 * @param sql
	 * @param columnIndex col index starting from 1
	 * @return list aof values
	 */
	public synchronized List<Double> getDoubleValues(String sql, int columnIndex)
	{
		List<Double> res = new ArrayList<Double>();
		try
		{
			ResultSet rs = executeSQL(sql);
			while (rs.next())
				res.add(rs.getDouble(columnIndex));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * (re)creates an index for table.column<br>
	 * @param tableName the name of table
	 * @param columnName the name of column
	 */
	public synchronized void createIndex(String tableName, String columnName)
	{
		try
		{
			stmt.execute("ALTER TABLE `" + tableName + "` DROP INDEX `" + columnName + "`");
		}
		catch (Exception e)
		{}
		executeSQL("ALTER TABLE `" + tableName + "` ADD INDEX (`" + columnName + "`)");
	}

	/**
	 * clone MySQL<br>
	 * Does not establish any connections!
	 * @return a clone of this MySQL object
	 */
	public synchronized MySQL clone()
	{
		return new MySQL(host, schema, user, pass);
	}

	/**
	 * clone structure of source table 
	 * using sql command "CREATE TABLE tarTable LIKE srcTable"
	 * and [optionally] copy all source data to the target table. 
	 * @param srcTable
	 * @param tarTable
	 * @param copyData if the source data should be copied to the target table
	 */
	public synchronized void cloneTable(String srcTable, String tarTable, boolean copyData)
	{
		optimizeTable( srcTable );
		dropTable( tarTable );
		executeSQL( "CREATE TABLE `" + tarTable + "` LIKE `" + srcTable + "`" );
		if (copyData) executeSQL( "INSERT IGNORE INTO `" + tarTable + "` SELECT * FROM `" + srcTable + "` " );
		optimizeTable( tarTable );
	}

	/**
	 * (re)create a column in given table
	 * @param tableName the name of table
	 * @param colName the name of column
	 * @param colTypeDef the type definition 
	 * @param dropIFExists if true then the probably existing column will be droped first
	 */
	public void addColumn(String tableName, String colName, String colTypeDef, boolean dropIFExists)
	{
		boolean exists = columnExists(tableName, colName);
		if (exists && dropIFExists)
		{
			executeSQL("ALTER TABLE `" + tableName + "` DROP COLUMN `" + colName + "`");
			exists = false;
		}
		if (!exists)
		{
			executeSQL("ALTER TABLE `" + tableName + "` ADD COLUMN `" + colName + "` " + colTypeDef);
		}
	}

	/**
	 * drop a column
	 * @param tableName the name of table
	 * @param colName the name of column
	 */
	public void dropColumn(String tableName, String colName)
	{
		if (columnExists(tableName, colName))
		{
			executeSQL("ALTER  TABLE `" + tableName + "` DROP COLUMN `" + colName + "`");
		}
	}
}
