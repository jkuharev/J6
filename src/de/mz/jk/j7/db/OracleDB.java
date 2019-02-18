package de.mz.jk.j7.db;

public class OracleDB extends DB
{
	public static final String jdbcDriverClass = "oracle.jdbc.driver.OracleDriver";
	public static final String jdbcUrlTemplate = "jdbc:oracle:thin:@%host%:%port%:%sid%";
	
	// typical Oracle eXpress setup
	private String host = "localhost";
	private int port = 1521;
	private String sid = "xe";
	
	// init with default local data of a typical Oracle eXpress setup
	private String jdbcURL = getOracleJDBCConnectionString(host, port+"", sid);
	
	/**
	 * create an Oracle DB connection handler
	 * Note: you will need to define connection data and login data manually 
	 * before establishing any database connection 
	 */
	public OracleDB()
	{
		super();
	}
	
	/**
	 * create an Oracle DB connection handler
	 * @param host server address
	 * @param port tcp port
	 * @param sid service identifier
	 * @param user user name
	 * @param pass password
	 */
	public OracleDB(String host, int port, String sid, String user, String pass) 
	{
		super(user, pass);
		setConnectionData(host, port, sid);		
	}
	
	public synchronized void setConnectionData(String host, int port, String sid) 
	{
		this.host = host;
		this.port = port;
		this.sid = sid;
		this.jdbcURL = getOracleJDBCConnectionString(host, port+"", sid);
	}	
	
	@Override
	public String getDriverClass() 
	{
		return jdbcDriverClass;
	}

	@Override
	public String getConnectionAddress() 
	{
		return jdbcURL;
	}
	
	/**
	 * generate a JDBC connection url from given data
	 * @param host the address of server
	 * @param port tcp port
	 * @param sid service identifier
	 * @return
	 */
	public static String getOracleJDBCConnectionString(String host, String port, String sid)
	{
		return 
				jdbcUrlTemplate
				.replace("%host%", host)
				.replace("%port%", port)
				.replace("%sid%", sid);
	}
}
