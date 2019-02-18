package de.mz.jk.j7.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.apache.log4j.Logger;

public abstract class DB 
{
	protected static Logger log = Logger.getLogger(DB.class.getName());
	
	/**
	 * create database handler
	 * Note: please provide user credentials separately
	 */
	public DB(){}
	
	/**
	 * create database handler and provide user credentials
	 * @param user
	 * @param pass
	 */
	public DB(String user, String pass) 
	{
		setLoginData(user, pass);
	}

	/** change this to enable transaction mode */
	public boolean AUTOCOMMIT_BY_DEFAULT = true;
	
	protected String user = "root";
	protected String pass = "admin";
	protected Connection con = null;
	protected Statement stmt = null;

	protected QueryLib dbUtils = null;
	
	/**
	 * get db utils which will use this database connection
	 * @return configured db utils
	 */
	public QueryLib getQueryLib() 
	{
		if(dbUtils==null)
		{
			dbUtils = new QueryLib(this);
		}
		
		return dbUtils;
	}
	
	/**
	 * implementation specific JDBC Driver class as fully qualified class name
	 * @return fully qualified class name as string
	 */
	public abstract String getDriverClass();
	
	/**
	 * implementation specific pre-assembled database connection locator (URL) 
	 * @return connection string
	 */
	public abstract String getConnectionAddress();
	
	
	/**
	 * set login credentials
	 * @param user
	 * @param pass
	 */
	public synchronized void setLoginData(String user, String pass) 
	{
		this.user = user;
		this.pass = pass;
	}
	
	/**
	 * retrieve recent sql connection
	 * @return
	 */
	public Connection getConnection() 
	{
		return con;	
	}
	
	/**
	 * retrieve default statement for the recent connection
	 * @return statement object
	 */
	public Statement getStatement()
	{ 
		return stmt; 
	}
	
	/**
	 * establish a new connection
	 */
	public synchronized void openConnection()
	{
		String jdbcURL = getConnectionAddress();
		String jdbcDriver = getDriverClass();
		
		try
		{
			Class.forName(jdbcDriver);
			log.info("connecting to '" + jdbcURL + "' ...");
			con = DriverManager.getConnection(jdbcURL, user, pass);
			setAutoCommit(AUTOCOMMIT_BY_DEFAULT);
			stmt = con.createStatement();
		}
		catch (Exception e)
		{
			log.debug("failed to connect to '"+jdbcURL+"' using jdbc driver '"+jdbcDriver+"'!", e);
		}
	}
	
	/**
	 * enable / disable autocommit
	 * @param enabled
	 */
	public synchronized void setAutoCommit(boolean enableAutocommit)
	{
		try 
		{
			con.setAutoCommit(enableAutocommit);
			log.debug("autocommit " + (enableAutocommit ? "enabled" : "disabled") + ".");
		}
		catch (Exception e) 
		{
			log.debug("failed to " + (enableAutocommit ? "enable" : "disable") + " autocommit.", e);
		}
	}

	/** 
	 * close opened connection	
	 */
	public synchronized void closeConnection()
	{
		try
		{
			con.close();
			log.info("database connection closed.");
		}
		catch (Exception e)
		{
			log.debug("failed to close database connection", e);
		}
		con = null;
	}	
}
