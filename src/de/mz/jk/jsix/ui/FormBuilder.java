/** ISOQuant_1.0, de.mz.jk.jsix.ui, 31.03.2011*/
package de.mz.jk.jsix.ui;

import java.awt.Font;

import javax.swing.*;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

/**
 * <h3>{@link FormBuilder}</h3>
 * make it easy to build a form using a swing container and
 * placing labelled elements on it by group layout 
 * @author Joerg Kuharev
 * @version 31.03.2011 12:46:58
 */
public class FormBuilder
{
	public static void main(String[] args)
	{
		FormBuilder fb = new FormBuilder();
			fb.add("label1", new JTextField());
			fb.add("label2", new JTextArea(20,80));
			fb.add("label3", new JTextField());
		
		JOptionPane.showMessageDialog(null, fb.getFormContainer());
	}
	
	private GroupLayout layout = null;
	private SequentialGroup vGrp = null; 
	private SequentialGroup hGrp = null; 
	private ParallelGroup lGrp = null;
	private ParallelGroup rGrp = null;
	
	private JComponent formContainer = null;
	
	private boolean boldCaptions = true;
	
	/**
	 * create form builder and use a new JPanel for placing elements<br>
	 * use {@link getFormContainer()} to obtain the panel
	 */
	public FormBuilder()
	{
		this( new JPanel() );
		formContainer.setBorder( BorderFactory.createEtchedBorder() );
	}
	
	/**
	 * create form builder and use given swing container for placing elements
	 * @param formContainer
	 */
	public FormBuilder(JComponent formContainer)
	{
		this.formContainer = formContainer;
		layout = new GroupLayout(formContainer);
		formContainer.setLayout(layout);
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		vGrp = layout.createSequentialGroup();
		hGrp = layout.createSequentialGroup();
		
		lGrp = layout.createParallelGroup(GroupLayout.Alignment.TRAILING);
		rGrp = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

		hGrp.addGroup( lGrp ).addGroup( rGrp );
		
		layout.setVerticalGroup(vGrp);
		layout.setHorizontalGroup(hGrp);	
	}
	
	/**
	 * set if captions should be bold
	 * @param boldCaptions
	 */
	public void setBoldCaptions(boolean boldCaptions){ this.boldCaptions = boldCaptions; }
	
	/**
	 * add labelled element to the form container
	 * @param <Type> the type of element
	 * @param caption the text used as caption for the element
	 * @param component the swing element
	 * @return the added element
	 */
	public <Type extends JComponent> Type add(String caption, Type component)
	{
		return add(caption, boldCaptions, component);
	}
	
	/**
	 * add labelled element to the form container
	 * @param <Type> the type of element
	 * @param caption the text used as caption for the element
	 * @param boldCaptionFont if true caption text will be bold
	 * @param component the swing element
	 * @return the added element
	 */
	public <Type extends JComponent> Type add(String caption, boolean boldCaptionFont, Type component)
	{
		JLabel lbl = new JLabel(caption);
		if(boldCaptionFont) lbl.setFont( lbl.getFont().deriveFont(Font.BOLD) );
		add( lbl, component );
		return component;
	}
	
	/**
	 * add two labels, e.g. column captions
	 * @param leftText
	 * @param rightText
	 */
	public void add(String leftText, String rightText, boolean leftBold, boolean rightBold)
	{
		JLabel left = new JLabel(leftText);
		JLabel right = new JLabel(rightText);
		if(leftBold) left.setFont(left.getFont().deriveFont(Font.BOLD));
		if(rightBold) right.setFont(right.getFont().deriveFont(Font.BOLD));
		add( left, right );
	}
	
	/**
	 * add two GUI components to columns
	 * @param leftComponent
	 * @param rightComponent
	 */
	public void add(JComponent leftComponent, JComponent rightComponent)
	{
		vGrp.addGroup(
			layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(leftComponent)
				.addComponent(rightComponent)
		);
		
		lGrp.addComponent(leftComponent);
		rGrp.addComponent(rightComponent);
	}
	

	/**
	 * obtain container including placed elements
	 * @return the swing container
	 */
	public JComponent getFormContainer()
	{
		return formContainer;
	}

	/**
	 * add vertical space of given point size
	 * @param gapSize
	 */
	public void addSpace(int gapSize)
	{
		vGrp.addGap(gapSize);
	}
}
