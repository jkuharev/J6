/** apexWelder, apexWelder, Feb 15, 2017*/
package de.mz.jk.jsix.cli;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.*;

/**
 * <h3>{@link xCLI}</h3>
 * @author jkuharev
 * @version Feb 15, 2017 9:05:51 AM
 */
public abstract class SimpleCLI
{
	/** does not change the order of options */
	class DefinitionOrderOptionComparator implements Comparator<Option>
	{
		@Override public int compare(Option o1, Option o2)
		{
			return 0;
		}
	}

	/** 
	 build an array of default options for your application,
	 e.g. 
	 	new Option[]{
	 		Option.builder( "i" ).argName( "input file path" ).longOpt( "input" ).desc( "the input file" ).hasArg().build(),
	 		Option.builder( "h" ).longOpt( "help" ).desc( "show usage information" ).build()
		...}
	 * */
	public abstract Option[] getDefaultOptions();

	/** 
	 * provide the jar file base name to be displayed in the help, 
	 * e.g. if your app is packaged as foo.jar then this function should return "foo" 
	 */
	public abstract String getExecutableJarFileName();

	/** commons cli object */
	private CommandLine commandLine = null;
	private boolean helpWasDisplayed = false;
	/** all known options */
	private Map<String, Option> optionsMap = new HashMap<String, Option>();
	/** user provided options */
	public Options definedOptions = new Options();

	private String helpHeader = "";
	private String helpFooter = "";

	/**
	 * construct cli wrapper with additional options
	 * @param additionalOptions
	 */
	public SimpleCLI(List<Option> additionalOptions)
	{
		initOptions( additionalOptions );
	}

	public SimpleCLI()
	{
		initOptions( null );
	}

	public void initOptions(List<Option> additionalOptions)
	{
		// add default options
		for ( Option o : getDefaultOptions() )
		{
			optionsMap.put( o.getOpt(), o );
			definedOptions.addOption( o );
		}

		// add additional options
		if (additionalOptions != null) 
			for ( Option o : additionalOptions )
			{
				optionsMap.put( o.getOpt(), o );
				definedOptions.addOption( o );
			}
	}

	/**
	 * generate a help option by:
	 * 	Option.builder( "h" ).longOpt( "help" ).desc( "show usage information" ).build()
	 * @return
	 */
	public static Option getDefaultHelpOption()
	{
		return Option.builder( "h" ).longOpt( "help" ).desc( "show usage information" ).build();
	}

	/** 
	 * print formatted help to the standard output.
	 * The help text is displayed one single time per session,
	 * call setHelpAlreadyDisplayed(false) to redisplay it.
	 * */
	public void showHelp()
	{
		if (helpWasDisplayed) return;
		HelpFormatter formatter = new HelpFormatter();
		formatter.setOptionComparator( new DefinitionOrderOptionComparator() );
		System.out.flush();
		synchronized (System.out)
		{
			System.out.println( "----------------------------------------------------------------------" );
			formatter.printHelp( "java -jar " + getExecutableJarFileName() + ".jar", helpHeader, definedOptions, helpFooter, true );
			System.out.println( "----------------------------------------------------------------------" );
		}
		System.out.flush();
		helpWasDisplayed = true;
	}

	public void setHelpHeader(String header)
	{
		helpHeader = header;
	}

	public void setHelpFooter(String footer)
	{
		this.helpFooter = footer;
	}

	/**
	 * manually set the flag if the help was already displayed
	 * @param value
	 */
	public void setHelpWasDisplayed(boolean value)
	{
		helpWasDisplayed = value;
	}

	public CommandLine getCommandLine()
	{
		return commandLine;
	}

	public CommandLine parseCommandLine(String[] args) throws Exception
	{
		commandLine = new DefaultParser().parse( definedOptions, args );
		for ( Option o : commandLine.getOptions() )
		{
			optionsMap.put( o.getOpt(), o );
		}
		if (commandLine.hasOption( "h" )) showHelp();
		return commandLine;
	}

	public Map<String, Option> getOptionsMap()
	{
		return optionsMap;
	}

	public void dumpOptionsMap()
	{
		for ( String key : optionsMap.keySet() )
		{
			Option opt = optionsMap.get( key );
			System.out.println( "name: " + opt.getOpt() + "; value:" + opt.getValue() );
		}
	}

	public void dumpCommandLine()
	{
		for ( Option opt : commandLine.getOptions() )
		{
			System.out.println( "name: " + opt.getOpt() + "; value:" + opt.getValue() );
		}
	}
}
