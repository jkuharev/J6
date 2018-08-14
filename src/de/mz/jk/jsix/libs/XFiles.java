package de.mz.jk.jsix.libs;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import de.mz.jk.jsix.ui.CustomizableFileFilter;

public class XFiles
{
	private static boolean DEBUG = false;
	private static String endl = System.getProperty("line.separator");

	/**
	 * copy a file
	 * @param in source file
	 * @param out target file
	 * @throws IOException on problems
	 */
	public static void copyFile(File in, File out) throws IOException
	{
		FileChannel inChannel = new FileInputStream(in).getChannel();
		FileChannel outChannel = new FileOutputStream(out).getChannel();
		try
		{
			inChannel.transferTo(0, inChannel.size(), outChannel);
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			if (inChannel != null) inChannel.close();
			if (outChannel != null) outChannel.close();
		}
	}

	/**
	 * extracts a file's base name 
	 * e.g. getBaseName(new File("c:\autoexec.bat")); returns "autoexec"
	 * @param file file
	 * @return base name without extension
	 */
	public static String getBaseName(File file)
	{
		String name = file.getName();
		int dotpos = name.lastIndexOf(".");
		return (dotpos > 0) ? name.substring(0, dotpos) : name;
	}

	/**
	 * extracts a file's base name 
	 * e.g. getBaseName("c:\autoexec.bat"); returns "autoexec"
	 * @param filePath file
	 * @return base name without extension
	 */
	public static String getBaseName(String filePath)
	{
		return getBaseName( new File( filePath ) );
	}

	/**
	 * recursively lists all files from a directory (not including directories)
	 * using maximum recursion depth of 255
	 * @param dir directory to start from
	 * @param filter suffix filter, only files these ends with this suffix (ignoring U/L-cases) will be included
	 * @return a list of all found files
	 * @throws Exception
	 */
	static public List<File> getFileList(File dir, String filter) throws Exception
	{
		return getFileList(dir, filter, 255, false);
	}

	/**
	 * recursively lists all files from a directory matching to the filter
	 * @param dir directory to start from
	 * @param suffixFilter suffix filter, only files these ends with this suffix (ignoring U/L-cases) will be included
	 * @param depth is the maximum recursion depth for file searching, values: 0..256, depth=0 will stay in this directory
	 * @param filesAndFolders true for including files and folders, false for excluding folders from results
	 * @return a list of all found files
	 * @throws Exception
	 */
	static public List<File> getFileList(File dir, String suffixFilter, int depth, boolean filesAndFolders) throws Exception
	{
		if (!dir.isDirectory()) throw new Exception(dir + " is not a directory!");
		suffixFilter = suffixFilter.toLowerCase();
		List<File> result = new ArrayList<File>();
		List<File> fileList = Arrays.asList(dir.listFiles());
		for (File file : fileList)
		{
			if (file.isFile() || filesAndFolders)
			{
				if (file.toString().toLowerCase().endsWith(suffixFilter)) result.add(file);
			}
			if (file.isDirectory())
			{
				try
				{
					if (depth > 0)
					{
						List<File> deeperList = getFileList(file, suffixFilter, depth - 1, filesAndFolders);
						if (deeperList.size() > 0) result.addAll(deeperList);
					}
				}
				catch (Exception e)
				{
					System.err.println("Error reading '" + file + "'.");
				}
			}
		}
		return result;
	}

	/**
	 * recursively lists all files from a directory matching to the filter
	 * @param dir directory to start from
	 * @param suffixFilter suffix filter, only files these ends with this suffix (ignoring U/L-cases) will be included
	 * @param depth is the maximum recursion depth for file searching, values: 0..256, depth=0 will stay in this directory
	 * @param matchCompleteFileName if true the suffixFiler has to match the complete file name
	 * @param filesAndFolders true for including files and folders, false for excluding folders from results
	 * @return a list of all found files
	 * @throws Exception
	 */
	static public List<File> getFileList(File dir, String suffixFilter, int depth, boolean matchCompleteFileName, boolean filesAndFolders) throws Exception
	{
		if (!dir.isDirectory()) throw new Exception(dir + " is not a directory!");
		suffixFilter = suffixFilter.toLowerCase();
		List<File> result = new ArrayList<File>();
		List<File> fileList = Arrays.asList(dir.listFiles());
		for (File file : fileList)
		{
			if (file.isFile() || filesAndFolders)
			{
				if (
				// whole name equals
				matchCompleteFileName && file.getName().equalsIgnoreCase(suffixFilter) ||
						// only suffix equals
						!matchCompleteFileName && file.toString().toLowerCase().endsWith(suffixFilter))
					result.add(file);
			}
			if (file.isDirectory())
			{
				try
				{
					if (depth > 0)
					{
						List<File> deeperList = getFileList(file, suffixFilter, depth - 1, filesAndFolders);
						if (deeperList.size() > 0) result.addAll(deeperList);
					}
				}
				catch (Exception e)
				{
					System.err.println("Error reading '" + file + "'.");
				}
			}
		}
		return result;
	}

	/**
	 * calculates the size of a file or size of a directory
	 * by recursively checking sizes of children files/directories
	 * @param file the file object
	 * @return resulting size in bytes
	 */
	public static long getFileSize(File file)
	{
		long size = 0;
		for (File f : file.listFiles())
		{
			size += (f.isFile()) ? f.length() : getFileSize(f);
		}
		return size;
	}

	/**
	 * calculates the size of a file or size of a directory
	 * by recursively checking sizes of children files/directories
	 * @param file the file object
	 * @param units one of b/k/m/g/t/p chars symbolizing the file size units
	 * <table>
	 * <tr><td>[char]</td><td>[meaning]</td></tr>
	 * <tr><td>b</td><td>bytes</td></tr>
	 * <tr><td>k</td><td>kilobytes (1024 bytes)</td></tr>
	 * <tr><td>m</td><td>megabytes (1024 kilobytes)</td></tr>
	 * <tr><td>g</td><td>gigabytes (1024 megabytes)</td></tr>
	 * <tr><td>t</td><td>terabytes (1024 gigabytes)</td></tr>
	 * <tr><td>p</td><td>petabytes (1024 terabytes)</td></tr>
	 * </table>
	 * @param decimalPlaces how many numbers should be shown after decimal point
	 * @return resulting size
	 */
	public static double getFileSize(File file, char units, int decimalPlaces)
	{
		decimalPlaces = (decimalPlaces > 0) ? decimalPlaces + 1 : 1;
		long bytes = getFileSize(file);
		units = ("" + units).toLowerCase().toCharArray()[0];
		double unitFaktor = 1;
		switch (units)
		{
			case ('k'):
				unitFaktor = 1024;
				break;
			case ('m'):
				unitFaktor = 1024 * 1024;
				break;
			case ('g'):
				unitFaktor = 1024 * 1024 * 1024;
				break;
			case ('t'):
				unitFaktor = 1024 * 1024 * 1024 * 1024;
				break;
			case ('p'):
				unitFaktor = 1024 * 1024 * 1024 * 1024;
				break;
			default:
				unitFaktor = 1;
				break;
		}
		double res = bytes / unitFaktor;
		String d = Double.valueOf(res).toString();
		int lp = d.indexOf(".") + decimalPlaces;
		if (lp < d.length()) res = Double.parseDouble(d.substring(0, lp));
		return res;
	}

	/**
	 * calculate a relative path between 2 absolute paths
	 * @param srcFile source path
	 * @param tarFile target path
	 * @return relative path
	 */
	public static String getRelativePath(File srcFile, File tarFile)
	{
		String src[] = srcFile.getAbsoluteFile().getParentFile().toURI().toString().split("/");
		String tar[] = tarFile.getAbsoluteFile().getParentFile().toURI().toString().split("/");
		String tarName = tarFile.getName();
		String res = "";
		int lpos = 0, steps = Math.min(src.length, tar.length);
		// ungleiche Anteile von links suchen
		while (lpos < steps && src[lpos].equals(tar[lpos]))
			lpos++;
		// bis zum gleichen Ordner mit ../ absteigen
		for (int i = lpos; i < src.length; i++)
			res += "../";
		// ungleiche Pfadanteile des Ziels hinzuf���gen
		for (int i = lpos; i < tar.length; i++)
			res += tar[i] + "/";
		// Dateinamen hinzuf���gen
		res += tarName;
		return res;
	}

	/**
	 * read a file into a string
	 * @param file an existing File object
	 * @return content string
	 */
	static public String readFile(File file)
	{
		return readFile(file.getAbsolutePath());
	}

	/**
	 * read a file into a string
	 * @param fileName file name
	 * @return content string
	 */
	public static String readFile(String fileName)
	{
		BufferedReader r = null;
		String res = "", line = "";
		try
		{
			r = new BufferedReader(new FileReader(fileName));
		}
		catch (Exception e)
		{
			r = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(fileName)));
		}
		try
		{
			while ((line = r.readLine()) != null)
			{
				res += line + endl;
			}
		}
		catch (Exception e)
		{
			System.err.println("Error reading file '" + fileName + "'");
		}
		finally
		{
			try
			{
				r.close();
			}
			catch (Exception e)
			{}
		}
		return res;
	}

	/**
	 * write a string into a file (overwriting existing content)
	 * @param file target file
	 * @param newContent content string
	 * @throws Exception
	 */
	static public void writeFile(File file, String newContent) throws Exception
	{
		if (file == null) throw new IllegalArgumentException("File should not be null.");
		if (!file.exists()) file.createNewFile();
		if (!file.isFile()) throw new IllegalArgumentException("Should not be a directory: " + file);
		if (!file.canWrite()) throw new IllegalArgumentException("File cannot be written: " + file);
		Writer output = new BufferedWriter(new FileWriter(file));
		try
		{
			output.write(newContent);
		}
		finally
		{
			output.close();
		}
		if (DEBUG) System.out.println("LIB: file '" + file.getName() + "' successfully written.");
	}

	/**
	 * read user defined number of lines from a text file
	 * @param file the file to read
	 * @param maxLines number of lines
	 * @return read lines
	 */
	public static String readLines(File file, int maxLines) throws Exception
	{
		BufferedReader r = new BufferedReader(new FileReader(file));
		String res = "", line = "";
		try
		{
			for (int i = 0; i < maxLines && ((line = r.readLine()) != null); i++)
			{
				res += line + endl;
			}
			r.close();
		}
		catch (Exception e)
		{
			System.err.println("Error reading file '" + file.getAbsolutePath() + "'");
		}
		return res;
	}

	/**
	 * read a file line by line into a list of strings where each string corresponds to a line
	 * @param file
	 * @return list of strings
	 * @throws Exception
	 */
	public static List<String> readAllLines(File file) throws Exception
	{
		BufferedReader r = new BufferedReader( new FileReader( file ) );
		List<String> res = new ArrayList<>();
		String line = "";
		try
		{
			while (( line = r.readLine() ) != null)
			{
				res.add( line );
			}
			r.close();
		}
		catch (Exception e)
		{
			System.err.println( "Error reading file '" + file.getAbsolutePath() + "'" );
		}
		return res;
	}

	/**
	 * read text file line by line until given regular expression matches a line
	 * @param file the file to read
	 * @param stopExp the regular expression for reading stop
	 * @param includeStopLine true if the matching line should be included
	 * @return content of the file until regular expression matches the line or until end of file
	 * @throws Exception
	 */
	public static String readUntilMatch(File file, String stopExp, boolean includeStopLine) throws Exception
	{
		BufferedReader r = new BufferedReader( new FileReader( file ) );
		String res = "", line = "";
		try
		{
			while (( ( line = r.readLine() ) != null ))
			{
				if (line.matches( stopExp ))
				{
					if (includeStopLine) res += line + endl;
					break;
				}
				else
				{
					res += line + endl;
				}
			}
			r.close();
		}
		catch (Exception e)
		{
			System.err.println( "Error reading file '" + file.getAbsolutePath() + "'" );
		}
		return res;
	}

	/**
	 * read text file line by line until given regular expression matches a line
	 * @param file the file to read
	 * @param stopExp the regular expression for reading stop
	 * @param ignoreExp the regular expression for ignored lines
	 * @param includeStopLine true if the matching line should be included
	 * @return content of the file until regular expression matches the line or until end of file
	 * @throws Exception
	 */
	public static String readUntilMatch(File file, String stopExp, String ignoreExp, boolean includeStopLine) throws Exception
	{
		BufferedReader r = new BufferedReader(new FileReader(file));
		String res = "", line = "";
		try
		{
			Pattern stopPattern = Pattern.compile( stopExp );
			Pattern ignorePattern = Pattern.compile( ignoreExp );
			while (((line = r.readLine()) != null))
			{
				if (stopPattern.matcher( line ).matches())
				{
					if (includeStopLine) res += line + endl;
					break;
				}
				else
				{
					if (!ignorePattern.matcher( line ).matches())
					{
						res += line + endl;
					}
					else
					{
						// we ignore this line
						// bacause it matches with the ignoreExp
					}
				}
			}
			r.close();
		}
		catch (Exception e)
		{
			System.err.println("Error reading file '" + file.getAbsolutePath() + "'");
		}
		return res;
	}

	/**
	 * choose file dialog
	 * @param dialogTitle dialog title
	 * @param savingFileMode dialog type to show if true save file dialog otherwise open file dialog 
	 * @param hintFile default file
	 * @param hintDir default target directory
	 * @param fileExtension allowed file extension without any point
	 * @param parent host gui component
	 * @return chosen file
	 */
	public static File chooseFile(String dialogTitle, boolean savingFileMode, File hintFile, File hintDir, String fileExtension, Component parent)
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(dialogTitle);
		fc.resetChoosableFileFilters();
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(new CustomizableFileFilter().get(fileExtension, fileExtension + " File"));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (hintDir != null) fc.setCurrentDirectory(hintDir);
		if (hintFile != null) fc.setSelectedFile(hintFile);
		fc.setDialogType( ( savingFileMode ) ? fc.SAVE_DIALOG : fc.OPEN_DIALOG );
		/*
		System.out.println( "Note: you should see a file dialog window now.\n\tIf it's not there, Your Java VM has a bug." );
		System.out.println( "\tI am working on an alternative way to select files, please be patient.\n\tKill the app and try again." );
		 */
		int state = fc.showDialog( parent, null );
		File file = fc.getSelectedFile();
		return (state != JFileChooser.CANCEL_OPTION && file != null) ? file.getAbsoluteFile() : null;
	}

	/**
	 * choose file
	 * @param dialogTitle
	 * @param savingFileMode dialog type to show if true save file dialog otherwise open file dialog 
	 * @param hintFile
	 * @param hintDir
	 * @param fileExtensions allowed file extensions (without point), like "xls", "xlsx", ... first extension is set active by default
	 * @param parent host gui component
	 * @return chosen file
	 */
	public static File chooseFile(String dialogTitle, boolean savingFileMode, File hintFile, File hintDir, String[] fileExtensions, Component parent)
	{
		FileFilter[] ff = new FileFilter[fileExtensions.length];
		for (int i = 0; i < ff.length; i++)
		{
			String ext = fileExtensions[i];
			String dsc = ext + " File";
			if (ext.contains(";"))
			{
				String[] parts = ext.split(";");
				ext = parts[0].trim();
				dsc = (parts.length < 2) ? (ext + " File") : parts[1].trim();
			}
			ff[i] = new CustomizableFileFilter().get(ext, dsc);
		}
		return chooseFile(dialogTitle, savingFileMode, hintFile, hintDir, ff, parent);
	}

	/**
	 * choose file
	 * @param dialogTitle
	 * @param savingFileMode dialog type to show if true save file dialog otherwise open file dialog 
	 * @param hintFile
	 * @param hintDir
	 * @param fileFilters
	 * @param parent host gui component
	 * @return chosen file
	 */
	public static File chooseFile2(String dialogTitle, boolean savingFileMode, File hintFile, File hintDir, FileFilter[] fileFilters, Component parent)
	{
		if (DEBUG) System.out.println( "creating dialog: " + dialogTitle  );
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(dialogTitle);
		fc.resetChoosableFileFilters();
		if (fileFilters != null && fileFilters.length > 0)
		{
			fc.setAcceptAllFileFilterUsed(false);
			for (FileFilter ff : fileFilters)
			{
				fc.addChoosableFileFilter(ff);
			}
			fc.setFileFilter(fc.getChoosableFileFilters()[0]);
		}
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (hintDir != null) fc.setCurrentDirectory(hintDir);
		if (hintFile != null) fc.setSelectedFile(hintFile);
		if (DEBUG) System.out.println( "showing dialog window ...");
		int state = (savingFileMode) ? fc.showSaveDialog(parent) : fc.showOpenDialog(parent);
		if (DEBUG) System.out.println( "closing dialog window ...");
		File file = fc.getSelectedFile();
		return (state != JFileChooser.CANCEL_OPTION && file != null) ? file.getAbsoluteFile() : null;
	}

	public static File chooseFile(String dialogTitle, boolean savingFileMode, File hintFile, File hintDir, FileFilter[] fileFilters, Component parent)
	{
		FileDialog fc = new FileDialog( (Frame)parent, dialogTitle, savingFileMode ? FileDialog.SAVE : FileDialog.LOAD );
		if (hintDir != null) fc.setDirectory( hintDir.getAbsolutePath() );
		if (hintFile != null) fc.setFile( hintFile.getName() );
		fc.setVisible( true );
		File file = new File( fc.getDirectory(), fc.getFile() );
		return file;
	}

	/**
	 * choose a folder
	 * @param dialogTitle
	 * @param hintDir
	 * @param host
	 * @return
	 */
	public static File chooseFolder(String dialogTitle, File hintDir, Component parent)
	{
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle(dialogTitle);
		fc.resetChoosableFileFilters();
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (hintDir != null)
		{
			fc.setCurrentDirectory(hintDir.getParentFile());
			fc.setSelectedFile(hintDir);
		}
		fc.setDialogType( JFileChooser.SAVE_DIALOG );
		int state = fc.showSaveDialog( parent );
		File file = fc.getSelectedFile();
		return (state != JFileChooser.CANCEL_OPTION && file != null) ? file.getAbsoluteFile() : null;
	}

	/**
	 * @param parentComponent 
	 * @param icon 
	 * @return true if user confirm to overwrite file, otherwise false
	 */
	public static boolean overwriteFileDialog(Component parentComponent, Icon icon)
	{
		return JOptionPane.showConfirmDialog(
				parentComponent,
				"<html>You have selected an existing file<br>do you want to overwrite it?</html>",
				"overwrite file?",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE,
				icon
				) == JOptionPane.YES_OPTION;
	}

	/**
	 * create zip archive from multiuple files
	 * @param srcFilePaths
	 * @param zipFilePath
	 * @throws Exception
	 */
	static public void zipFiles(String[] srcFilePaths, String zipFilePath) throws Exception
	{
		FileOutputStream fileWriter = new FileOutputStream(zipFilePath);
		ZipOutputStream zip = new ZipOutputStream(fileWriter);
		for (String srcFile : srcFilePaths)
		{
			if (new File(srcFile).isDirectory())
			{
				addDirToZip(null, srcFile, zip);
			}
			else
			{
				addFile2Zip(null, srcFile, zip);
			}
		}
		zip.flush();
		zip.close();
	}

	/**
	 * create zip archive from a folder
	 * @param srcDirPath
	 * @param zipFilePath
	 * @throws Exception
	 */
	static public void zipDir(String srcDirPath, String zipFilePath) throws Exception
	{
		FileOutputStream fileWriter = new FileOutputStream(zipFilePath);
		ZipOutputStream zip = new ZipOutputStream(fileWriter);
		addDirToZip(null, srcDirPath, zip);
		zip.flush();
		zip.close();
	}

	/**
	 * recursively remove a file or folder (inclusive its contents)
	 * @param dir
	 * @return true if deleted
	 */
	public static boolean removeDir(File dir)
	{
		if (dir.isDirectory())
		{
			String[] files = dir.list();
			for ( int i = 0; i < files.length; i++ )
				if (!removeDir( new File( dir, files[i] ) ))
					return false;
		}
		return dir.delete();
	}

	/**
	 * recursively remove a file or folder (inclusive its contents)
	 * @param path path to the filder to remove
	 * @return true if deleted
	 */
	public static boolean removeDir(String path)
	{
		return removeDir( new File( path ) );
	}

	static private void addFile2Zip(String path, String srcFilePath, ZipOutputStream zip) throws Exception
	{
		File srcFile = new File(srcFilePath);
		if (srcFile.isDirectory())
		{
			addDirToZip(path, srcFilePath, zip);
		}
		else
		{
			byte[] buf = new byte[1024];
			int len;
			FileInputStream in = new FileInputStream(srcFilePath);
			zip.putNextEntry(
					new ZipEntry(
							(path == null)
									? srcFile.getName()
									: path + File.separatorChar + srcFile.getName()
					)
					);
			while ((len = in.read(buf)) > 0)
			{
				zip.write(buf, 0, len);
			}
		}
	}

	static private void addDirToZip(String path, String srcFolder, ZipOutputStream zip) throws Exception
	{
		File folder = new File(srcFolder);
		for (String fileName : folder.list())
		{
			addFile2Zip(
					(path == null)
							? folder.getName()
							: path + File.separatorChar + folder.getName()
					, srcFolder + File.separatorChar + fileName, zip);
		}
	}

	/** create folder and all missing parts of the given path! */
	public static boolean mkDir(String dir)
	{
		return mkDir( new File( dir ) );
	}

	/** create folder and all missing parts of the given path! */
	public static boolean mkDir(File dir)
	{
		return dir.exists() ? true : dir.mkdirs();
	}
}
