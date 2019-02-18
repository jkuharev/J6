package de.mz.jk.j7.db;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.mz.jk.j7.db.sql.SQLBatchExecutionListener;
import de.mz.jk.j7.db.sql.TableData;

public class QueryLib 
{
	protected static Logger log = Logger.getLogger(QueryLib.class.getName());
	
	private DB db = null;
	private Set<SQLBatchExecutionListener> batchExecListeners = new LinkedHashSet<SQLBatchExecutionListener>();
	
	private int dbVersion = -1;
	
	public QueryLib(DB db)
	{
		setDB(db);
	}
	
	public void setDB(DB db) 
	{
		this.db = db;
	}
	
	public DB getDB() 
	{
		return db;
	}
	
	/**
	 * pass through to the underlying db object 
	 */
	public QueryLib openConnection()
	{
		db.openConnection();
		return this;
	}
	
	/**
	 * pass through to the underlying db object
	 */
	public void closeConnection()
	{
		db.closeConnection();
	}
	
	/**
	 * execute a single sql query and parses the result to a string
	 * @param sql
	 * @return result dumped to a string
	 * @throws Exception
	 */
	public synchronized String dumpSQL(String sql) throws Exception
	{
		Statement stmt = db.getStatement();
		String res = "";
		
		boolean hasRes = stmt.execute(sql);
		if (!hasRes) return "successfully executed.";
		
		ResultSet rs = stmt.getResultSet();
		ResultSetMetaData rsmd = rs.getMetaData();
		int n = rsmd.getColumnCount();
		// Get the column names; column indices start from 1
		for (int i = 1; i <= n; i++)
		{
			String tabName = rsmd.getTableName(i);
			String colName = rsmd.getColumnName(i);
			String tabColName = (tabName!=null && tabName.length()>0 ? tabName+ "." : "")  + colName;
			res += tabColName + "\t";
		}
		res += "\n";
		// walk through result lines
		for (int j = 0; rs.next(); j++)
		{
			for (int i = 1; i <= n; i++)
			{
				res += rs.getString(i);
			}
			res += "\n";
		}
		return res;
	}
	
	/**
	 * number of modified rows in previously executed DML statement
	 * or zero in case if the number of rows can't be determined
	 * @return number of changed rows
	 */
	public synchronized int getUpdateCount()
	{
		try
		{
			return db.getStatement().getUpdateCount();
		}
		catch (SQLException e)
		{
			return 0;
		}
	}
	
	/**
	 * execute sql statement and notify a listener if available
	 * @param sql
	 * @param execListener
	 * @return result set
	 */
	public synchronized ResultSet executeSQL(String sql)
	{
		try
		{
			// preprocess sql statement by all listeners
			for(SQLBatchExecutionListener l : batchExecListeners) 
			{
				sql = l.processSQLStatementBeforeExecution(sql);
			}

			// start tracking time
			Long t = System.currentTimeMillis();
			
			// execute preprocessed sql
			Statement stmt = db.getStatement();
			stmt.execute(sql);
			
			// notify listeners
			for(SQLBatchExecutionListener l : batchExecListeners) 
			{
				l.sqlStatementExecutedNotification(sql, System.currentTimeMillis() - t);
			}
			
			// success
			return stmt.getResultSet();
		}
		catch (Exception e)
		{
			log.warn("failed to execute sql statement.", e);
			for(SQLBatchExecutionListener l : batchExecListeners) 
			{
				l.sqlStatementFailedNotification(sql, e);
			}			
		}
		return null;
	}
	
	/**
	 * execute a DML-SQL statement and return the number of modified records.
	 * this is the same as a sequence of executeSQL() + getUpdateCount() calls
	 * @param sql a DML statement
	 * @return number of modified rows
	 */
	public synchronized int executeUpdate(String sql)
	{
		executeSQL(sql);
		return getUpdateCount();
	}
	
	
	/**
	 * execute an insert or update statement that posts binary data,
	 * which is provided as base64 encoded string.
	 * 
	 * provided sql statement must contain '?' character as placeholder
	 * for the binary value: e.g.
	 * - INSERT INTO blob_tab (id, bin_dat) VALUES(1, ?);
	 * - UPDATE blob_tab SET bin_dat=? WHERE id=2;
	 * 
	 * ONLY one single binary file is allowed per statement.
	 * 
	 * 
	 * @param sql insert or update statement
	 * @param encodedBinaryData base64 encoded binary data
	 * @return number of updated rows
	 */
	public int executeUpdate(String sql, String encodedBinaryData)
	{
		byte[] bin = Base64.getDecoder().decode(encodedBinaryData);
		return executeUpdate(sql, bin);
	}
	
	/**
	 * execute an insert or update statement that posts binary data.
	 * provided sql statement must contain '?' character as placeholder
	 * for the binary value: e.g.
	 * - INSERT INTO blob_tab (id, bin_dat) VALUES(1, ?);
	 * - UPDATE blob_tab SET bin_dat=? WHERE id=2;
	 * 
	 * ONLY one single binary file is allowed per statement.
	 * 
	 * @param sql insert or update statement
	 * @param binData binary data
	 * @return number of updated rows
	 */
	public synchronized int executeUpdate(String sql, byte[] binData)
	{
		List<byte[]> datas = Collections.singletonList(binData);
		return executeUpdate(sql, datas);
	}
	
	/**
	 * execute an insert or update statement that posts binary data.
	 * provided sql statement must contain '?' character as placeholder
	 * for the binary value: e.g.
	 * - INSERT INTO blob_tab (id, file1, file2) VALUES(1, ?, ?);
	 * - UPDATE blob_tab SET bin_dat=? WHERE id=2;
	 * 
	 * multiple blobs per statement allowed
	 * 
	 * @param sql insert or update statement
	 * @param binDatas binary data
	 * @return number of updated rows
	 */
	public synchronized int executeUpdate(String sql, List<byte[]> binDatas)
	{
		try
		{
			// preprocess sql statement by all listeners
			for(SQLBatchExecutionListener l : batchExecListeners) 
			{
				sql = l.processSQLStatementBeforeExecution(sql);
			}

			// start tracking time
			Long t = System.currentTimeMillis();
			
			// execute preprocessed sql
			PreparedStatement ps = db.getConnection().prepareStatement(sql);
			
			if(binDatas!=null && binDatas.size()>0 && sql.contains("?"))
			{
				int blob_count = 0;
				for(byte[] blob : binDatas)
				{
					blob_count++;
					ps.setBytes(blob_count, blob);
				}
			}
			
			ps.execute();
			
			// notify listeners
			for(SQLBatchExecutionListener l : batchExecListeners) 
			{
				l.sqlStatementExecutedNotification(sql, System.currentTimeMillis() - t);
			}

			// success
			return ps.getUpdateCount();
		}
		catch (Exception e)
		{
			log.warn("failed to execute sql statement.", e);
			log.debug(sql);
			for(SQLBatchExecutionListener l : batchExecListeners) 
			{
				l.sqlStatementFailedNotification(sql, e);
			}
		}
		
		return 0;
	}
	
	
	/**
	 * execute query and capture result as QueryResult object.
	 * any data that is not automatically converted to a plain string
	 * is retrieved as a byte array, and stored as Base64 encoded string!
	 * BLOB values result in base64 strings!!!
	 * @param sql the query
	 * @return
	 */
	public synchronized TableData listSelect(String sql)
	{
		TableData res = new TableData();
		try
		{
			// execute statement
			Statement stmt = db.getStatement();
			boolean hasRes = stmt.execute(sql);

			// nothing to report
			if (!hasRes) return res;
			
			// get result
			ResultSet rs = stmt.getResultSet();
			
			// fill column names
			ResultSetMetaData rsmd = rs.getMetaData();	
			res.nCols = rsmd.getColumnCount();
			res.cols = new String[res.nCols];
			
			for (int ci = 1; ci <= res.nCols; ci++)
			{
				res.cols[ci-1] = rsmd.getColumnName(ci);
			}
			
			// collect result lines
			for(int ri = 1; rs.next(); ri++)
			{
				String[] row = new String[res.nCols];
				for (int ci = 1; ci <= res.nCols; ci++)
				{
					try {
						row[ci-1] = rs.getString(ci);
					} catch (Exception e) {
						row[ci-1] = Base64.getEncoder().encodeToString( rs.getBytes(ci) );						
					}
				}
				res.rows.add(row);
				res.nRows = ri;
			}
		}
		catch (Exception e)
		{
			log.warn("failed to retrieve result data for query", e);
		}
		return res;
	}
	
	/**
	 * execute query and capture only one single column as a list of strings
	 * @param sql the query
	 * @param colNum number of the column to list (1st column is colNum=1)
	 * @return list of elements in the defined column
	 */
	public synchronized List<String> listACol(String sql, int colNum)
	{
		List<String> res = new ArrayList<String>();
		try
		{
			// execute statement
			Statement stmt = db.getStatement();
			boolean hasRes = stmt.execute(sql);
			// nothing to report
			if (!hasRes) return res;
			// get result
			ResultSet rs = stmt.getResultSet();
			// collect result lines
			while(rs.next())
			{
				String value = rs.getString(colNum);
				res.add(value);
			}
		}
		catch (Exception e)
		{
			log.warn("failed to retrieve result data for query", e);
		}
		return res;
	}
	
	/**
	 * retrieve first column as a list of strings
	 * @param sql a query
	 * @return list of values in the first column
	 */
	public synchronized List<String> listFirstCol(String sql)
	{
		return listACol(sql, 1);
	}
	
	/**
	 * select a single value
	 * @param sql a query
	 * @return single value from the first row and column returned
	 */
	public synchronized String selectSingleValue(String sql)
	{
		String res = "";
		try
		{
			// execute statement
			Statement stmt = db.getStatement();
			boolean hasRes = stmt.execute(sql);
			// nothing to report
			if (!hasRes) return res;
			// get result
			ResultSet rs = stmt.getResultSet();
			// collect result
			if(rs.next())
			{
				String value = rs.getString(1);
				res = value;
			}
		}
		catch (Exception e)
		{
			log.warn("failed to retrieve result data for query", e);
		}
		return res;
	}
	
	public String getDatabaseProductName()
	{
		try {
			DatabaseMetaData meta = db.getConnection().getMetaData();
			return meta.getDatabaseProductName();
		} catch (Exception e) {
			log.warn("failed to retrieve database meta information", e);
			return "UNKNOWN";
		}
	}
	
	public String getDatabaseProductVersion()
	{
		try {
			DatabaseMetaData meta = db.getConnection().getMetaData();
			return meta.getDatabaseProductVersion();
		} catch (Exception e) {
			log.warn("failed to retrieve database meta information", e);
			return "UNKNOWN";
		}
	}
	
	public int getDatabaseMajorVersion()
	{
		if(dbVersion < 0)
		try {
			DatabaseMetaData meta = db.getConnection().getMetaData();
			dbVersion = meta.getDatabaseMajorVersion();
		} catch (Exception e) {
			log.warn("failed to retrieve database meta information", e);
			dbVersion = 0;
		}
		return dbVersion;
	}
}
