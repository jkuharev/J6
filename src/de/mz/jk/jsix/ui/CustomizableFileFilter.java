package de.mz.jk.jsix.ui;

import java.io.File;

/**
 * Implementation of a FileFilter that allows changing filter properties at runtime
 * @author Jšrg Kuharev
 * @since 2009-07-14
 */
public class CustomizableFileFilter extends javax.swing.filechooser.FileFilter 
{
    private String ext = "";
    private String dsc = "";

    /**
     * create a File filter for an user defined file extension
     * @param ext file extension without '.' e.g. html, xls, dll
     * @param dsc description for the file extension
     * @return
     */
    public javax.swing.filechooser.FileFilter get(String ext, String dsc)
    {
		this.ext = ext;
		this.dsc = dsc;
		return this;
    }
	
    public boolean accept(File f)
    {
		if (f.isDirectory()) return true; 
		return f.getName().toLowerCase().endsWith("."+ext);
    }

    public String getDescription()
    {
    	return dsc; 
    }
}
