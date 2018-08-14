/** ISOQuant, de.mz.jk.jsix.mysql, 20.10.2011*/
package de.mz.jk.jsix.mysql;

/**
 * <h3>{@link StdOutSQLBatchExecutionAdapter}</h3>
 * @author kuharev
 * @version 20.10.2011 14:05:50
 */
public class StdOutSQLBatchExecutionAdapter implements SQLBatchExecutionListener
{
	@Override public String processSQLStatementBeforeExecution(String sql){return sql;}
	@Override public void sqlStatementExecutedNotification(String sql, long ms){}
	@Override public void sqlStatementFailedNotification(String sql, Exception e)
	{
		System.err.println("-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --");
		System.err.println(sql);
		System.err.println("-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --");
		e.printStackTrace();
	}
	@Override public void sqlCommentNotification(String comment)
	{
		if( comment.matches("--\\s*@\\w*\\s+.*") )
		{
			// String commentType = comment.replaceFirst("--\\s*@", "").replaceFirst("\\s+.*", "");
			// String commentContent = comment.replaceFirst("--\\s*@\\w*\\s+", "");
			System.out.println( comment.replaceFirst("--\\s*@\\w*\\s+", "") );
		}
	}
}
