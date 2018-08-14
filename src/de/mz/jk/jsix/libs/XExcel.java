/** ISOQuant, isoquant.plugins.report.xls, 11.08.2011*/
package de.mz.jk.jsix.libs;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.mz.jk.jsix.ui.iProcessProgressListener;
import de.mz.jk.jsix.utilities.Bencher;

/**
 * <h3>{@link XExcel}</h3>
 * wrapper for easy usage of excel sheet functionality offered by org.apache.poi.hssf library
 * @author Joerg Kuharev
 * @version 11.08.2011 10:32:27
 */
public class XExcel
{
	public static boolean DEBUG = false;

	public static final short FONT_WEIGHT_BOLD = Font.BOLDWEIGHT_BOLD;
	public static final short FONT_WEIGHT_NORMAL = Font.BOLDWEIGHT_NORMAL;
	
	private Workbook workBook = null;
	private File xlsFile = new File( System.currentTimeMillis() + ".xls" );
	
	private Map<String, Sheet> sheets = null;
	private Map<String, CellStyle> styles = null;
	
	private CellStyle defaultCellStyle = null;
	
	private iProcessProgressListener progressListener = null;

	/** initialize workbook as XLS or XLSX (OOXML) Spread Sheet format */
	public void initWorkBook(boolean useOOXML)
	{
		workBook = useOOXML ? new XSSFWorkbook() : new HSSFWorkbook();
		sheets = new HashMap<String, Sheet>();
		styles = new HashMap<String, CellStyle>();
		defaultCellStyle = createCellStyle( "default" );
		createCellStyle( "hlab", CellStyle.ALIGN_CENTER, 0, 10, FONT_WEIGHT_BOLD );
		createCellStyle( "vlab", CellStyle.VERTICAL_BOTTOM, 90, 10, FONT_WEIGHT_BOLD );
	}

	/** 
	 * automatically use Excel 97 compatible XLS Spreadsheet format 
	 */
	public XExcel(){ initWorkBook(false); }
	
	/**
	 * used has to define between Excel 97 XLS or Excel 2007 (OO XML) format
	 * @param useOOXML if true OOXML(XLSX) is used instead of XLS
	 */
	public XExcel(boolean useOOXML){ initWorkBook(useOOXML); }

	/**
	 * automatically decide between XLS and XLSX depending on file extension
	 * @param xlsFile if file extension is xlsx OOXML format is used otherwise old Excel 97 XLS is used
	 */
	public XExcel( File xlsFile )
	{
		initWorkBook( xlsFile.getName().toLowerCase().endsWith("xlsx") );
		setXlsFile( xlsFile ); 
	}

	/** 
	 * set a progress listener
	 * @param progressListener
	 */
	public void setProgressListener(iProcessProgressListener progressListener)
	{
		this.progressListener = progressListener;
	}
	
	/**
	 * create a new (or replace existing) cell style
	 * @param styleName
	 * @param alignment one of CellStyle.ALIGN* constants
	 * @param rotation degree of rotation
	 * @param fontSize in points e.g. 10
	 * @param fontWeight either Font.BOLDWEIGHT* constants or XEcel.FONT_WEIGHT* constants
	 * @return
	 */
	public CellStyle createCellStyle(String styleName, short alignment, int rotation, int fontSize, short fontWeight)
	{
		CellStyle style = workBook.createCellStyle();
		style.setAlignment( alignment );
		style.setRotation( (short)rotation );
		
		Font font = workBook.createFont();
		font.setFontHeightInPoints( (short)fontSize );
		font.setBoldweight( fontWeight );
		
		style.setFont( font );
		return setCellStyle(styleName, style); 
	}

	/**
	 * add existing cell style
	 * @param styleName
	 * @param style
	 * @return
	 */
	public CellStyle setCellStyle(String styleName, CellStyle style)
	{
		styles.put(styleName, style);
		return style;
	}

	/**
	 * create a new and clean cell style
	 * @param styleName
	 * @return
	 */
	public CellStyle createCellStyle(String styleName)
	{
		CellStyle style = workBook.createCellStyle();
		styles.put(styleName, style);
		return style;
	}
	
	/**
	 * get a cell style if it exists otherwise get default style<br>
	 * there are some predefined styles:<br>
	 * "hlab" - for horizontal labels (bold, centered)
	 * "vlab" - for vertical labels (bold, centered, at the bottom of a cell)
	 * @param styleName
	 * @return
	 */
	public CellStyle getCellStyle(String styleName)
	{
		if (styles.containsKey( styleName )) { return styles.get( styleName ); }
		System.out.println( "undefined font style '" + styleName + "'" );
		return defaultCellStyle;
	}
	
	public File getXlsFile(){return xlsFile;}
	public void setXlsFile(File xlsFile){this.xlsFile = xlsFile;}
	
	private Sheet createSheet(String sheetTitle)
	{
		Sheet sheet = workBook.createSheet(sheetTitle);
		sheets.put(sheetTitle, sheet);
		return sheet;
	}

	/**
	 * get an existing sheet or create new one 
	 * @param sheetTitle
	 * @return
	 */
	public Sheet getSheet(String sheetTitle)
	{
		return ( sheets.containsKey(sheetTitle) ) ? sheets.get(sheetTitle) : createSheet(sheetTitle);
	}
	
	/**
	 * get an existing or create new row
	 * @param sheet target sheet
	 * @param rowIndex the row index
	 * @return a row object
	 */
	public Row getRow(Sheet sheet, int rowIndex)
	{
		Row row = sheet.getRow( rowIndex ); 
		return row==null ? sheet.createRow(rowIndex) : row;
	}
	
	public Cell setCell(Row r, int col, int val)
	{
		Cell c = r.createCell(col);
		c.setCellValue( val );
		return c;
	}
	
	public Cell setCell(Row r, int col, String val)
	{
		Cell c = r.createCell(col);
		c.setCellValue( val );
		return c;
	}
	
	public Cell setCell(Row r, int col, String val, CellStyle s)
	{
		Cell c = setCell(r,col,val);
		if(s!=null) c.setCellStyle( s );
		return c;
	}
	
	public Cell setCell(Row r, int col, Double val, CellStyle s)
	{
		Cell c = r.createCell(col);
		c.setCellValue( val );
		if(s!=null) c.setCellStyle( s );
		return c;
	}
	
	public Cell setCell(Row r, int col, Float val, CellStyle s)
	{
		Cell c = r.createCell(col);
		c.setCellValue( val );
		if(s!=null) c.setCellStyle( s );
		return c;
	}
	
	/**
	 * save to given xls file
	 * @param xlsFile
	 * @return
	 */
	public boolean save(File xlsFile)
	{
		setXlsFile(xlsFile);
		return save();
	}
	
	/**
	 * save to previously defined xls file
	 * @return
	 */
	public boolean save()
	{
		try {
			FileOutputStream out = new FileOutputStream( xlsFile );
			workBook.write( out );
			out.close();
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * autosize a range of columns 
	 * @param sheetName the name of sheet
	 * @param firstCol
	 * @param lastCol
	 */
	public void autoSizeColumns(String sheetName, int firstCol, int lastCol)
	{
		autoSizeColumns( getSheet(sheetName), firstCol, lastCol );
	}
	
	/**
	 * autosize a range of columns 
	 * @param sheetName the name of sheet
	 * @param firstCol
	 * @param lastCol
	 */
	public void autoSizeColumns(Sheet sheet, int firstCol, int lastCol)
	{
		for(int i=firstCol; i<=lastCol; i++)
		{
			sheet.autoSizeColumn(i);
		}
	}

	/**
	 * creates a complete excel sheet from a list
	 * having a list of string for each row.
	 * First row contains labels
	 * @param sheetName the name of a sheet to be created
	 * @param data the cell data
	 * @param rowOffset offset from top
	 * @param colOffset offset from left
	 * @param labelStyle the style of label cells, e.g. getCellStyle("hlab") or getCellStyle("vlab") 
	 * @return
	 */
	public Sheet createSheetFromDataList(String sheetName, List<List<String>> data, int rowOffset, int colOffset, CellStyle labelStyle)
	{
		System.out.print( "\tcreating sheet '" + sheetName + "' ... " );
		Bencher timer = new Bencher().start();
		int row = rowOffset;
		int col = colOffset;
		int maxCol = col;
		Sheet resSheet = workBook.getSheet( sheetName );
		if (resSheet == null)
			resSheet = workBook.createSheet( sheetName );
		if (progressListener != null) progressListener.setProgressMaxValue( data.size() );
		for ( List<String> line : data )
		{
			if (progressListener != null && row % 100 == 0) progressListener.setProgressValue( row );
			Row r = resSheet.createRow( row );
			int c = col;
			for ( String cell : line )
			{
				if (row == rowOffset) // title cells
				{
					setCell( r, c, cell, labelStyle );
				}
				else
				{
					if (cell == null)
						setCell( r, c, "" );
					else if (cell.matches( "[0-9]+\\.[0-9]+" )) // DOUBLE
					{
						setCell( r, c, Double.parseDouble( cell ), null );
					}
					else if (cell.matches( "[0-9]+" )) // INTEGER
					{
						setCell( r, c, Integer.parseInt( cell ) );
					}
					else
					{
						setCell( r, c, XJava.decURL( cell ) );
					}
				}
				// c++;
				if (++c > maxCol) maxCol = c;
			}
			row++;
			if (row >= 65536)
			{
				System.err.println( "\nexcel row number limit (65536 rows) for sheet '" + sheetName + "' exceeded!\n" );
				System.out.println( "\t" );
				break;
			}
		}
		for ( int i = colOffset; i < maxCol; i++ )
		{
			resSheet.autoSizeColumn( i );
		}
		System.out.println( "[" + timer.stop().getSec() + "s]" );
		if (progressListener != null) progressListener.setProgressValue( 0 );

		return resSheet;
	}
}

