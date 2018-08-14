/** ISOQuant, isoquant.gui, 15.03.2012 */
package de.mz.jk.jsix.utilities;

import javax.swing.UIManager;

/**
 * <h3>{@link XLookAndFeelChanger}</h3>
 * @author kuharev
 * @version 15.03.2012 14:32:31
 */
public class XLookAndFeelChanger
{
	public static final String NIMBUS = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
	public static final String METAL = "javax.swing.plaf.metal.MetalLookAndFeel";
	public static final String MOTIF = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
	public static final String GTK = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
	public static final String NATIVE = UIManager.getSystemLookAndFeelClassName();

	/**
	 * change look and feel by complete class name.
	 * you may want to use predefined constants
	 * @param lafClassName
	 */
	public static void changeLookAndFeelByClassName(String lafClassName, boolean verbose)
	{
		try
		{
			UIManager.setLookAndFeel(lafClassName);
			if (verbose) System.out.println("Look And Feel successfully changed to:	" + lafClassName);
		}
		catch (Exception e)
		{
			if (verbose) e.printStackTrace();
		}
	}

	/** 
	 * @param lafName one of: nimbus, metal, motif, native
	 */
	public static void changeLookAndFeelByName(String lafName, boolean verbose)
	{
		if (lafName.toLowerCase().matches(".*nimbus.*"))
			changeLookAndFeelByClassName(NIMBUS, verbose);
		else if (lafName.toLowerCase().matches(".*metal.*"))
			changeLookAndFeelByClassName(METAL, verbose);
		else if (lafName.toLowerCase().matches(".*motif.*"))
			changeLookAndFeelByClassName(MOTIF, verbose);
		else if (lafName.toLowerCase().matches(".*gtk.*"))
			changeLookAndFeelByClassName(GTK, verbose);
		else changeLookAndFeelByClassName(NATIVE, verbose);
	}
}
