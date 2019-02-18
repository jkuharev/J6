package de.mz.jk.j7.db.sql;

/**
 * <h3>{@link SQLBatchExecutionListener}</h3>
 * lets the caller of sql batch executing functions
 * know what happens while sql execution 
 * @author kuharev
 * @version 20.10.2011 11:04:37
 */
public interface SQLBatchExecutionListener 
{
	/**
	 * sql statement found
	 * let caller process the sql statement before its execution 
	 * implementation example: <br><br>
	  	protected String processSQLStatementBeforeExecution(String template){<br>
	 	&nbsp;return template<br>
	 	&nbsp;&nbsp;&nbsp;&nbsp;.replaceAll( "NEEDLE" , "REPLACEMENT")<br>
	 	&nbsp;&nbsp;&nbsp;&nbsp;.replaceAll( "NEEDLE2" , "REPLACEMENT2");<br>
	 	}
	 */
	public String processSQLStatementBeforeExecution(String sql);
	
	/**
	 * notify that sql statement was executed successfully 
	 * @param sql
	 * @param ms execution duration in milliseconds 
	 */
	public void sqlStatementExecutedNotification(String sql, long ms);
	
	/**
	 * notify that sql statement execution has failed
	 * and pass through the exception
	 * @param sql
	 * @param e
	 */
	public void sqlStatementFailedNotification(String sql, Exception e);
	
	/**
	 * comment line found
	 * @param comment
	 */
	public void sqlCommentNotification(String comment);
}
