/** JSiX, de.mz.jk.jsix.test, 10.09.2012*/
package de.mz.jk.jsix.test;

import de.mz.jk.jsix.mysql.MySQL;

/**
 * <h3>{@link testMySQL}</h3>
 * @author kuharev
 * @version 10.09.2012 10:44:38
 */
public class testMySQL
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		MySQL db = new MySQL("localhost:3307", "mysql", "root", "", false);
		db.getForcedConnection( false );
		
		for(String dbName : db.listDatabases() ) 			System.out.println(dbName);

		
		db.closeConnection( true );
		
		
	}
}
