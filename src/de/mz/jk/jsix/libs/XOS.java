package de.mz.jk.jsix.libs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <h3>{@link XOS}</h3>
 * @author jkuharev
 * @version Feb 16, 2016 1:02:52 PM
 */
public class XOS
{
	/**
	 * get value of an environment variable
	 * @param envVarName
	 * @param caseSensitive
	 * @return
	 */
	public static String getEnvironmentVariable(String envVarName, boolean caseSensitive)
	{
		Map<String, String> env = System.getenv();
		for ( String k : env.keySet() )
		{
			if (( k == envVarName ) || ( caseSensitive && k.toLowerCase() == envVarName.toLowerCase() ))
				return env.get( k );
		}
		return null;
	}

	public static class ProcessDescription
	{
		public String cmd = "";
		public int pid = 0;
		public float cpu = 0;
		public long mem = 0;

		@Override public String toString()
		{
			return "PID:" + pid + "\t%CPU:" + cpu + "\t%MEM:" + mem + "\tCMD:" + cmd;
		}
	}

	public static boolean DEBUG = false;
	private static String OperatingSystem = System.getProperty( "os.name" ).toLowerCase();

	/**
	 * list running processes on a Windows or Unix computer
	 * @return
	 * @throws Exception
	 */
	public static List<ProcessDescription> listProcesses() throws Exception
	{
			return OperatingSystem.toLowerCase().contains( "windows" ) ? listWindowsProcesses() : listUnixProcesses();
	}

	/**
	 * list running processes on a Windows computer
	 * @return
	 * @throws Exception
	 */
	public static List<ProcessDescription> listWindowsProcesses() throws Exception
	{
		List<ProcessDescription> listOfProcesses = new ArrayList<>();
		Process taskManProc = Runtime.getRuntime().exec( "tasklist.exe /fo csv" );
		BufferedReader input = new BufferedReader( new InputStreamReader( taskManProc.getInputStream() ) );
		for ( String line = null; ( line = input.readLine() ) != null; )
		{
			if( line.contains( "\"PID\"" ) )
			{ // skip title line
				// "Abbildname","PID","Sitzungsname","Sitz.-Nr.","Speichernutzung"
				// "Image Name","PID","Session Name","Session#","Mem Usage"
				// if (DEBUG) System.out.println( line );
			}
			else
			{ // parse individual processes
				// "csrss.exe","616","Services","0","5,500 K"
				String[] parts = line.replaceAll( "^\"|\"$", "" ).split( "\",\"", 5 );
				if(parts.length<5) throw new Exception("bad output format");
				ProcessDescription p = new ProcessDescription();
				p.cmd = XJava.stripQuotation(parts[0]);
				p.pid = Integer.parseInt( parts[1] );
				// p.cpu =
				p.mem = Long.parseLong(
						XJava.stripQuotation( parts[4] ).trim().replaceAll( "\\s.*", "" ).replace( ",", "" ) );
				listOfProcesses.add( p );
			}
		}
		input.close();
		return listOfProcesses;
	}

// // parse a memory usage string to KB
// private static float parseMemUsage(String mem)
// {
// String[] parts = mem.split("\\s+", 2);
// float value = Float.parseFloat( parts[0].trim().replace(",", "") ); // stupid
// tousands separator
// char units = parts[1].trim().toLowerCase().charAt(0);
// switch(units) // it looks like it is always K
// {
// case 't': value *= 1024;
// case 'g': value *= 1024;
// case 'm': value *= 1024;
// case 'k': //
// default:
// }
// return value;
// }

/*
ps -eo $KEYWORDS
KEYWORDS on Mac OS X
     %cpu       percentage CPU usage (alias pcpu)
     %mem       percentage memory usage (alias pmem)
     acflag     accounting flag (alias acflg)
     args       command and arguments
     comm       command
     command    command and arguments
     cpu        short-term CPU usage factor (for scheduling)
     etime      elapsed running time
     flags      the process flags, in hexadecimal (alias f)
     gid        processes group id (alias group)
     inblk      total blocks read (alias inblock)
     jobc       job control count
     ktrace     tracing flags
     ktracep    tracing vnode
     lim        memoryuse limit
     logname    login name of user who started the session
     lstart     time started
     majflt     total page faults
     minflt     total page reclaims
     msgrcv     total messages received (reads from pipes/sockets)
     msgsnd     total messages sent (writes on pipes/sockets)
     nice       nice value (alias ni)
     nivcsw     total involuntary context switches
     nsigs      total signals taken (alias nsignals)
     nswap      total swaps in/out
     nvcsw      total voluntary context switches
     nwchan     wait channel (as an address)
     oublk      total blocks written (alias oublock)
     p_ru       resource usage (valid only for zombie)
     paddr      swap address
     pagein     pageins (same as majflt)
     pgid       process group number
     pid        process ID
     ppid       parent process ID
     pri        scheduling priority
     re         core residency time (in seconds; 127 = infinity)
     rgid       real group ID
     rss        resident set size
     ruid       real user ID
     ruser      user name (from ruid)
     sess       session ID
     sig        pending signals (alias pending)
     sigmask    blocked signals (alias blocked)
     sl         sleep time (in seconds; 127 = infinity)
     start      time started
     state      symbolic process state (alias stat)
     svgid      saved gid from a setgid executable
     svuid      saved UID from a setuid executable
     tdev       control terminal device number
     time       accumulated CPU time, user + system (alias cputime)
     tpgid      control terminal process group ID
     tsess      control terminal session ID
     tsiz       text size (in Kbytes)
     tt         control terminal name (two letter abbreviation)
     tty        full name of control terminal
     ucomm      name to be used for accounting
     uid        effective user ID
     upr        scheduling priority on return from system call (alias usrpri)
     user       user name (from UID)
     utime      user CPU time (alias putime)
     vsz        virtual size in Kbytes (alias vsize)
     wchan      wait channel (as a symbolic name)
     wq         total number of workqueue threads
     wqb        number of blocked workqueue threads
     wqr        number of running workqueue threads
     wql        workqueue limit status (C = constrained thread limit, T = total thread limit)
     xstat      exit or stop status (valid only for stopped or zombie process)
*/
	/**
	 * list running processes on a Unix/Linux/Mac OS X computer
	 * @return
	 * @throws Exception
	 */
	public static List<ProcessDescription> listUnixProcesses() throws Exception
	{
		List<ProcessDescription> listOfProcesses = new ArrayList<>();
		// ps -eo pid,%cpu,rss,comm
		// pid: process id
		// %cpu: percentage of the cpu usage
		// %rss: real used (resident set) memory in KB
		// comm: the command name without command line arguments
		Process taskManProc = Runtime.getRuntime().exec( "ps -eo pid,%cpu,rss,comm" );
		BufferedReader input = new BufferedReader( new InputStreamReader( taskManProc.getInputStream() ) );
		for ( String line; ( line = input.readLine() ) != null; )
		{
			line = line.trim();
			if (line.startsWith( "PID" ))
			{ // skip title line
				/*  "PID %CPU RSS COMM" */
				// if (DEBUG) System.out.println( line );
			}
			else
			{ // parse individual processes
				/*  48   0.0  0.1 /usr/libexec/kextd */
				String[] parts = line.trim().split( "\\s+", 4 );
				if(parts.length<4) break;
				ProcessDescription p = new ProcessDescription();
				p.pid = Integer.parseInt( parts[0].trim() );
				p.cpu = Float.parseFloat( parts[1].trim() );
				p.mem = Long.parseLong( parts[2].trim() );
				p.cmd = parts[3].trim();
				listOfProcesses.add( p );
			}
		}
		input.close();
		return listOfProcesses;
	}

	/**
	 * find system processes with matching command names
	 * @param cmdPattern
	 * @param matchByRegExp true to treat cmdPattern as a regular expression or false as text
	 * @return
	 */
	public static List<ProcessDescription> findProcesses(String cmdPattern, boolean matchByRegExp)
	{
		List<ProcessDescription> res = new ArrayList<>();
		try
		{
			List<ProcessDescription> procs = listProcesses();
			for ( ProcessDescription pi : procs )
			{
				if( 
						(  matchByRegExp && pi.cmd.matches( cmdPattern ) ) || 
						( !matchByRegExp && pi.cmd.toLowerCase().contains( cmdPattern.toLowerCase() ) )
					)
					res.add( pi );
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return res;
	}

	public static void main(String[] args)
	{

	}
}
