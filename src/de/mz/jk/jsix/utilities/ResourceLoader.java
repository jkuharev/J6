package de.mz.jk.jsix.utilities;

import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

public class ResourceLoader 
{
	public static void setIconDefaults(String iconPath, String iconExtension, double iconScaleFactor)
	{
		ResourceLoader.defaultIconPath = iconPath;
		ResourceLoader.defaultIconExtension = iconExtension;
		ResourceLoader.defaultIconScaleFactor = iconScaleFactor;
	}
	
	private static String defaultIconPath = "icons/";
	private static String defaultIconExtension = ".png";
	private static double defaultIconScaleFactor = 1.0;
	
	/**
	 * load icon from file path 
	 * or iconName by building the file path from defaultIconPath and defaultIconExtension<br>
	 * available icons are e.g.<br><i> 
	 * annotation, change_view, chart, chip, cluster, construction, database,
	 * delete, document, edit, empty_document, expression_analysis, frame, heatmap,
	 * help, html, icons.txt, import, info, isoquant, isoquant_document, link,
	 * mindmap, normalize, open_folder, open_plgs, options, plgs, plot, plugin,
	 * printer, run, save, spreadsheet, statistics, sync_lists, timer, trash 
	 * </i><br>
	 * @param iconName file path or icon name
	 * @return icon the icon, if icon path not found OptionPane.errorIcon will be returned
	 */
	public static ImageIcon getIcon( String iconName )
    {
		URL directURL = ClassLoader.getSystemResource( iconName );
		URL iconPathURL = ClassLoader.getSystemResource( defaultIconPath + iconName + defaultIconExtension );

		ImageIcon res = null;
		
		if(iconPathURL!=null)
		{
			res = new ImageIcon( iconPathURL );
		}
		else
		if(directURL!=null)
		{
			res = new ImageIcon( directURL );
		}
		else
		{
			res = new ImageIcon(getImage(UIManager.getIcon("OptionPane.errorIcon")));
			System.err.println("Icon '"+iconName+"' not found!");
		}

		if(defaultIconScaleFactor!=1.0)
		{
			ImageIcon ico = res;
			res = new ImageIcon(
					ico.getImage().getScaledInstance(
						(int)(ico.getIconWidth()*defaultIconScaleFactor), 
						(int)(ico.getIconHeight()*defaultIconScaleFactor), 
						java.awt.Image.SCALE_SMOOTH
					)
			);
		}
		
		return res;
    }

	public static Image getImage( Icon icon ) 
	{
		if (icon instanceof ImageIcon) 
		{
			return ((ImageIcon)icon).getImage();
		}
		else 
		{
			BufferedImage image = 
				GraphicsEnvironment
				.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice()
				.getDefaultConfiguration()
				.createCompatibleImage( icon.getIconWidth(), icon.getIconHeight() );
			Graphics2D g = image.createGraphics();
			icon.paintIcon(null, g, 0, 0);
			g.dispose();
			return image;
		}
	}
	
	/**
	 * relative host path to a Class (path to its package)
	 * @param cls
	 * @return path to Class' host folder (relative to current directory)
	 */
	public static String getParentPath( Class cls )
	{
		return cls.getPackage().getName().replaceAll("\\.", "/") + "/";
	}
}
