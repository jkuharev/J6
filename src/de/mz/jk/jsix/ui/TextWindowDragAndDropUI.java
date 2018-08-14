package de.mz.jk.jsix.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/** wx2csv, , Nov 2, 2017*/
/**
 * <h3>{@link TextWindowDragAndDropUI}</h3>
 * 
 * This is a very simple implementation of a window with a scrollable text area
 *  to which the standard output and error are redirected and that is enabled 
 *  for receiving drag-and-drop events from the local file system
 *  to provide the overriding implementation with input files.
 *  
 *  For processing DnD events, you need to implement and add 
 *  a FileActionListener to override one or multiple of the following functions on your needs
 *     - filterTargetFiles( files ) : to filter the dropped files
 *     - doMultiFileAction( files ) : to process filtered files at once
 *     - doSingleFileAction( file ) : to process filtered files one-by-one
 * 
 * @author jkuharev
 * @version Nov 2, 2017 10:52:48 AM
 */
public class TextWindowDragAndDropUI extends DropTarget
{
	public static interface FileActionListener
	{
		public List<File> filterTargetFiles(List<File> files);
		public void doMultiFileAction( List<File> files );
		public void doSingleFileAction( File file );
	}
	
	public static class FileActionAdapter implements FileActionListener
	{
		public static boolean DEBUG = false;

		/** please override and adapt to your needs */
		public List<File> filterTargetFiles(List<File> listOfFiles)
		{
			if (DEBUG) System.out.println( "filtering " + listOfFiles.size() + " files." );
			return listOfFiles;
		}

		/** please override and adapt to your needs */
		public void doSingleFileAction(File file)
		{
			if (DEBUG) System.out.println( "singe file action for file " + file.getName() );
		}

		/** please override and adapt to your needs */
		public void doMultiFileAction(List<File> listOfFiles)
		{
			if (DEBUG) System.out.println( "multi file action for " + listOfFiles.size() + " files." );
		}
	}

	protected String windowTitle = "simple messaging dnd UI window";
	protected JTextArea outputTextArea = null;
	protected JFrame win = null;
	protected List<FileActionListener> registeredListeners = new ArrayList<FileActionListener>();
	protected String welcomeMessage = 
			"This UI will provide\n" + 
			"a simple interface\n" + 
			"to your software\n" + 
			"which allows to display\n" + 
			"text messages and\n" + 
			"to drag-and-drop\n" + 
			"files from file system\n" + 
			"to be used in your app\n" + 
			"\n" + 
			"Have a nice day!\n" + 
			"(c) Dr. Joerg Kuharev\n" + 
			"-------------------------------";

	public TextWindowDragAndDropUI(String title, int width, int height)
	{
		initUI( title, width, height );
	}

	public TextWindowDragAndDropUI(String title, int width, int height, String welcomeMessage)
	{
		this.welcomeMessage = welcomeMessage;
		initUI( title, width, height );
	}

	public void setWelcomeMessage(String text)
	{
		this.welcomeMessage = text;
	}

	public void initUI(String title, int width, int height)
	{
		outputTextArea = new JTextArea();
		win = new JFrame( windowTitle );

		setWindowTitle( title );

		outputTextArea.setBackground(Color.DARK_GRAY);
		outputTextArea.setForeground(Color.GREEN);
		outputTextArea.setFont(new Font("monospaced", Font.PLAIN, 11));
		
		outputTextArea.setDropTarget(this);
		
		new JTextAreaOutputStream(outputTextArea, true, true);
		win.setLayout( new BorderLayout() );
		win.add( new JScrollPane( outputTextArea ), BorderLayout.CENTER );

		win.setSize( width, height );
		win.setVisible(true);
		win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		printWelcomeMessage();
	}

	public void addFileActionListener(FileActionListener listener)
	{
		registeredListeners.add( listener );
	}

	public void drop(DropTargetDropEvent dtde)
	{
		if (dtde.isDataFlavorSupported( DataFlavor.javaFileListFlavor ))
		{
			dtde.acceptDrop( dtde.getDropAction() );
			try
			{
				List<File> listOfFiles = (List<File>)dtde.getTransferable().getTransferData( DataFlavor.javaFileListFlavor );
				
				for(FileActionListener listener : registeredListeners)
				{
					List<File> filteredFiles = listener.filterTargetFiles( listOfFiles );
					listener.doMultiFileAction( filteredFiles );
					for ( File file : filteredFiles )
					{
						listener.doSingleFileAction( file );
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void setWindowTitle(String windowTitle)
	{
		if (windowTitle != null) this.windowTitle = windowTitle;
		if (win != null) win.setTitle( windowTitle );
	}

	public String getWindowTitle()
	{
		return windowTitle;
	}

	public JFrame getWin()
	{
		return win;
	}

	public JTextArea getOutputTextArea()
	{
		return outputTextArea;
	}

	public void printWelcomeMessage()
	{
		System.out.println( welcomeMessage );
	}

	public static void main(String[] args)
	{
		TextWindowDragAndDropUI app = new TextWindowDragAndDropUI( null, 250, 400 );
		FileActionAdapter.DEBUG = true;
		app.addFileActionListener( new FileActionAdapter() );
		JTextField tf = new JTextField();
		tf.setText( "additional component" );
		app.win.add( tf, BorderLayout.SOUTH );
	}
}
