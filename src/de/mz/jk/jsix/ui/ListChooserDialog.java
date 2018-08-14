package de.mz.jk.jsix.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

import javax.swing.*;

public class ListChooserDialog extends JDialog implements WindowListener
{
	private static final long serialVersionUID = 20110115L;

	public static void main(String[] args) 
	{
		System.out.println(
			ListChooserDialog.chooseItemFrom(null, new String[]{"1", "2", "3", "4"}, "eins oder zwei.", "wähle eine zahl.")
		);
	}

	/**
	 * shows a modal dialog with a list of objects
	 * one of them can be selected
	 * @param host host frame component
	 * @param list array of items for selection
	 * @param caption caption shown over the list
	 * @param title dialog window title
	 * @return index of the selected item
	 */
	public static <TYPE> int chooseItemFrom(Frame parent, TYPE[] list, String caption, String title)
	{
		ListChooserDialog dlg = new ListChooserDialog(parent);
		dlg.listPanel.setData(list);
		dlg.show(title);
		return dlg.getSelectedIndex();		
	}
	
	/**
	 * shows a modal dialog with a list of objects
	 * one of them can be selected
	 * @param host host frame component
	 * @param list array of items for selection
	 * @param caption caption shown over the list
	 * @param title dialog window title
	 * @return index of the selected item
	 */
	public static <TYPE> int[] chooseItemsFrom(Frame parent, TYPE[] list, String caption, String title)
	{
		ListChooserDialog dlg = new ListChooserDialog(parent);
		dlg.listPanel.setData(list);
		dlg.listPanel.allowMultiRowSelection(true);
		dlg.show(title);
		return dlg.getSelectedIndices();		
	}
	
	/**
	 * shows a modal dialog with a list of objects
	 * one of them can be selected
	 * @param host host frame component
	 * @param list list of items for selection
	 * @param caption caption shown over the list
	 * @param title dialog window title
	 * @return index of the selected item
	 */
	public static <TYPE> int chooseItemFrom(Frame parent, List<TYPE> list, String caption, String title)
	{
		ListChooserDialog dlg = new ListChooserDialog(parent);
		dlg.listPanel.setData(list);
		dlg.show(title);
		return dlg.getSelectedIndex();		
	}
	
	/**
	 * shows a modal dialog with a list of objects
	 * one of them can be selected
	 * @param host host frame component
	 * @param list list of items for selection
	 * @param caption caption shown over the list
	 * @param title dialog window title
	 * @return index of the selected item
	 */
	public static <TYPE> int[] chooseItemsFrom(Frame parent, List<TYPE> list, String caption, String title)
	{
		ListChooserDialog dlg = new ListChooserDialog(parent);
		dlg.listPanel.setData(list);
		dlg.listPanel.allowMultiRowSelection(true);
		dlg.show(title);
		return dlg.getSelectedIndices();		
	}
	
	private void show(String title)
	{
		setTitle(title);
		pack();
		setLocationRelativeTo( getParent() );
		setVisible(true);
	}
	
	private int getSelectedIndex(){return selectedIndex;}
	private int[] getSelectedIndices(){return selectedIndices;}
	
	private ListChooserPanel listPanel = null;
	private int selectedIndex = -1;
	private int[] selectedIndices = {};
	private boolean choiceDone = false;
	
	public boolean choiceDone(){return choiceDone;}
	
	private ListChooserDialog(Frame parent)
	{
		super(parent, true);
		listPanel = new ListChooserPanel();
		getContentPane().add( listPanel );
		addWindowListener(this);
	}

	public void windowClosing(WindowEvent evt){	cancelSelection(); }
	public void windowActivated(WindowEvent evt){}
	public void windowClosed(WindowEvent evt){}
	public void windowDeactivated(WindowEvent evt){}
	public void windowDeiconified(WindowEvent evt){}
	public void windowIconified(WindowEvent evt){}
	public void windowOpened(WindowEvent evt){}

	public void cancelSelection() 
	{
		choiceDone = true;
		dispose();
	}
	
	public void setSelectedItems(int[] selectedIndices) 
	{
		if(selectedIndices.length>0)
		this.selectedIndex = selectedIndices[0];
		this.selectedIndices = selectedIndices;
		choiceDone = true;
		dispose();
	}
	
	private class ListChooserPanel extends JPanel implements ActionListener
	{
		private static final long serialVersionUID = 20110115L;
		
		private JList list = new JList();
		private JPanel cmdPanel = new JPanel();
		private JButton btnOk = new JButton("ok");
		private JButton btnCancel = new JButton("cancel");
		private JPanel msgPanel = new JPanel();
		private JLabel msgLabel = new JLabel("Please make your choice and click ok");
		
		public ListChooserPanel()
		{
			list.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
			
			GridBagConstraints bc1 = new GridBagConstraints();
			bc1.gridx = 0;
			bc1.gridy = 0;
			bc1.insets = new Insets(5,5,5,5);

			GridBagConstraints bc2 = new GridBagConstraints();
			bc2.gridx = 1;
			bc2.gridy = 0;
			bc2.insets = new Insets(5,5,5,5);
			
			cmdPanel.setLayout( new GridBagLayout( ) );
			cmdPanel.setBorder(BorderFactory.createEtchedBorder());
			
			cmdPanel.add(btnOk, bc1);
			cmdPanel.add(btnCancel, bc2);
			
			btnOk.addActionListener(this);
			btnCancel.addActionListener(this);
			
			msgPanel.setLayout( new FlowLayout( FlowLayout.LEFT ) );
			msgPanel.add( msgLabel );
			
			this.setLayout(new BorderLayout());
	        this.setSize(new Dimension(400, 200));
	        this.add( msgPanel, BorderLayout.NORTH );
	        this.add( new JScrollPane( list ), BorderLayout.CENTER  );
	        this.add( cmdPanel, BorderLayout.SOUTH );
		}
		
		/**
		 * write a caption over the choosable list
		 * @param text the caption text
		 */
		public void setCaption(String text)
		{
			msgLabel.setText(text);
		}
		
		public void allowMultiRowSelection(boolean multiRow)
		{
			list.setSelectionMode( 
				multiRow 
					? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION 
					: ListSelectionModel.SINGLE_SELECTION 
			);
		}
		
		public <TYPE> void setData(List<TYPE> listItems)
		{
			DefaultListModel listModel = new DefaultListModel();
			for(TYPE item : listItems) listModel.addElement( item.toString() );
			list.setModel( listModel  );
			if(listItems.size()>0) list.setSelectedIndex(0);
		}
		
		public <TYPE> void setData(TYPE[] listItems)
		{
			DefaultListModel listModel = new DefaultListModel();
			for(TYPE item : listItems) listModel.addElement( item.toString() );
			list.setModel( listModel );
			if(listItems.length>0) list.setSelectedIndex(0);
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			if(e.getSource().equals(btnOk))
			{
				setSelectedItems( list.getSelectedIndices() );
			}
			else
			if(e.getSource().equals(btnCancel))
			{
				cancelSelection();
			}
		}
	}
}
