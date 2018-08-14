package de.mz.jk.jsix.ui;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.*;

/**
 * <h3>TableFactory</h3>
 * handle table data depending on its type
 * @author Joerg Kuharev
 * @version 11.01.2011 12:32:15
 *
 */
public class TableFactory extends AbstractTableModel
{
	private static final long serialVersionUID = 20130307L;

	public static void main(String[] args)
	{
		Object[] tt = new Object[]{"ok", "text", "zahl"};
		Object[][] td = new Object[][]{
			new Object[]{true, "hallo", 123},
			new Object[]{true, "huhu", 3},
			new Object[]{false, "hehe", 0},
			new Object[]{true, "haha", 45345}
		};
		
		TableFactory atm = new TableFactory(td, tt, new Boolean[]{true, false, true});
		
		JOptionPane.showMessageDialog(null, atm.getScrollableTable(true) );
		
		Object[][] tdd = atm.getData();
		
		System.out.println("ok;	text;	zahl");
		for(int i=0; i<tdd.length; i++)
		{
			Object[] row = tdd[i];
			System.out.println( row[0] +";	" + row[1] +";	" + row[2] );
		}

//		JDialog win = new JDialog();
//			win.setTitle("Project attributes");
//			win.setSize(640, 480 );
//			win.add( atm.getScrollableTable(true) );			
//			win.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			win.setModal(true);
//			win.setVisible(true);
	}
	
	private Boolean[]	colEditables = null;
	private Class[]		colClasses = null;
	private Object[]	colTitles = null;
	private Object[][]	rowData = null;
	private JTable		table = new JTable();
	
	/**
	 * make a table model and table with user data,
	 * number of columns will be determined by the size of column titles,
	 * rowData is an array of cells with rowData[row][col]
	 * @param rowData array of rows, each row is an array of its column data 
	 * @param colTitles titles of columns
	 */
	public TableFactory(Object[][] rowData, Object[] colTitles)
	{
		setTitles(colTitles);
		setData( rowData );
		// addTableModelListener(this);
	}
	
	/**
	 * make a table model and table with user data,
	 * number of columns will be determined by the size of column titles,
	 * rowData is an array of cells with rowData[row][col],
	 * @param rowData array of cell contents like rowData[row][col]
	 * @param colTitles array of column titles
	 * @param editableColumns array with true at editable column positions
	 */
	public TableFactory(Object[][] rowData, Object[] colTitles, Boolean[] editableColumns)
	{
		this(rowData, colTitles);
		setEditableColumns(editableColumns);
	}
	
	private void determineClasses()
	{
		// System.out.println("check classes:");
		colClasses = new Class[colTitles.length];
		for(int i=0; i<colClasses.length; i++)
		{
			try {
				colClasses[i] = rowData[0][i].getClass();
			} catch (Exception e) {
				colClasses[i] = String.class;
			}
		}
	}

	/**
	 * @return the table
	 */
	public JTable getTable(boolean autosize)
	{
		if(autosize) autosizeColumns();
		return table;
	}
	
	/**
	 * wrap the table with a scroll pane
	 * @return scroll pane containing the table
	 */
	public JScrollPane getScrollableTable(boolean autosize)
	{
		return new JScrollPane( getTable(autosize) );
	}
	
	/**
	 * set column titles
	 * @param titles
	 */
	protected void setTitles(Object[] titles)
	{
		this.colTitles = titles;
	}
	
	/**
	 * set row oriented table data<br>
	 * <b>ATTENTION:<b> do setTitle(titles); before call this function
	 * @param data the dats array with data[row][col]
	 */
	protected void setData(Object[][] data)
	{
		rowData = data;
		determineClasses();
		table.setModel(this);
	}
	
	/**
	 * get data back
	 * @return the row oriented array of cells with data[row][col] accession method
	 */
	public Object[][] getData()
	{
		return rowData;
	}
	
	/**
	 * @param editableCols array with true at editable column positions
	 */
	protected void setEditableColumns(Boolean[] editableCols)
	{
		this.colEditables = editableCols;
	}
	
	@Override public String getColumnName(int col)
	{
		try {
			return colTitles[col].toString();
		} catch (Exception e) {
			return "";
		}		
	}
	
	@Override public int getColumnCount()
	{
		return (colTitles!=null) ? colTitles.length : 0;
	}
	
	@Override public int getRowCount()
	{
		return (rowData!=null) ? rowData.length : 0;
	}
	
	@Override public Object getValueAt(int row, int col)
	{
		try {
			return rowData[row][col];
		} catch (Exception e) {
			return "";
		}
	}
	
	@Override public Class getColumnClass(int c) 
	{
		try {
			return colClasses[c];
		} catch (Exception e) {
			return String.class;
		}
	}
	
	@Override public boolean isCellEditable(int row, int col)
	{
		try{
			return colEditables[col];
		}catch(Exception e){
			return false;
		}
	}
	
	@Override public void setValueAt(Object value, int row, int col) 
	{
        rowData[row][col] = value;
        fireTableCellUpdated(row, col);
	}
	
	public void autosizeColumns() 
	{
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int margin = 5;

        for (int i = 0; i < table.getColumnCount(); i++) 
        {
            DefaultTableColumnModel colModel  = (DefaultTableColumnModel) table.getColumnModel();
            TableColumn             col       = colModel.getColumn(i);
            TableCellRenderer renderer = (col.getHeaderRenderer()==null) 
            	? table.getTableHeader().getDefaultRenderer()
            	: col.getHeaderRenderer();
            Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
            int width = comp.getPreferredSize().width;

            for (int r = 0; r < table.getRowCount(); r++)
            {
                renderer = table.getCellRenderer(r, i);
                comp     = renderer.getTableCellRendererComponent(table, table.getValueAt(r, i), false, false, r, i);
                width = Math.max(width, comp.getPreferredSize().width);
            }
            
            width += 2 * margin;

            col.setPreferredWidth(width);
        }

        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        //table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
    }

/*
	@Override public void tableChanged(TableModelEvent e)
	{
		if(e.getType() == TableModelEvent.UPDATE)
		{
			int r = e.getFirstRow();
			int c = e.getColumn();
			
			System.out.println("cell("+r+","+c+")=" + getValueAt(r, c));
		}
	}
*/
}
