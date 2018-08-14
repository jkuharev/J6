package de.mz.jk.jsix.mysql;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

public class MySQLConnectionDialog extends JDialog implements ActionListener, WindowListener
{
	public static void main(String[] args) 
	{
		MySQL DB = inputConnectionData(null, "localhost", "root", "", "mysql");
		System.out.println( (DB!=null) ?  DB.toString() : "connection dialog aborted" );
	}
	
	public static MySQL inputConnectionData(Frame parent, String host, String user, String pass)
	{
		MySQLConnectionDialog dlg = new MySQLConnectionDialog(parent, host, user, pass);
		dlg.showSchema = false;
		dlg.setModal(true);
		if(parent!=null) dlg.setLocationRelativeTo(parent);
		dlg.setVisible(true);
		return dlg.db;
	}
	
	public static MySQL inputConnectionData(Frame parent, String host, String schema, String user, String pass)
	{
		MySQLConnectionDialog dlg = new MySQLConnectionDialog(parent, host, schema, user, pass);
		dlg.setModal(true);
		if(parent!=null) dlg.setLocationRelativeTo(parent);
		dlg.setVisible(true);
		return dlg.db;
	}
	
	private MySQLConnectionDialog(Frame parent, String host, String schema, String user, String pass) 
	{
		super(parent);
		showSchema = true;
		hidePassword = true;
		initialize();
		if(host!=null) txtHost.setText(host);
		if(user!=null) txtUser.setText(user);
		if(pass!=null) txtPass.setText(pass);
		if(schema!=null) txtSchema.setText(schema);
	}
	
	private MySQLConnectionDialog(Frame parent, String host, String user, String pass) 
	{
		super(parent);
		showSchema = false;
		hidePassword = true;
		initialize();
		if(host!=null) txtHost.setText(host);
		if(user!=null) txtUser.setText(user);
		if(pass!=null) txtPass.setText(pass);
	}
	
	private MySQL db = null;
	
	private JButton btnOk = new JButton("Ok");
	private JButton btnCancel = new JButton("Cancel");
	
	private JTextField txtHost = new JTextField(20);
	private JTextField txtUser = new JTextField(20);
	private JTextField txtPass = null;
	private JTextField txtSchema = new JTextField(20);
	
	private boolean showSchema = false;
	private boolean hidePassword = true;

	private JLabel lblHost = new JLabel("host / ip");
	private JLabel lblUser = new JLabel("user name");
	private JLabel lblPass = new JLabel("password");
	private JLabel lblSchema = new JLabel("schema");
	private JLabel lblMessage = new JLabel("<html><center>Please type your database connection data then press <b>Ok</b>.</center></html>");
	private JPanel pnlInputs = new JPanel();
	private JPanel pnlButtons = new JPanel();
	private JPanel pnlMessage = new JPanel();
	
	/**
	 * This method initializes this
	 */
	private void initialize() 
	{
		txtPass = (hidePassword) ? new JPasswordField(20) : new JTextField(20);
		
		this.addWindowListener(this);
		this.setLayout( new BorderLayout() );
        // this.setSize(new Dimension(400, 200));
		this.setTitle("database connection data dialog");
		
		pnlMessage.setLayout( new FlowLayout(FlowLayout.CENTER) );
		pnlMessage.add(lblMessage);
		pnlMessage.setBorder(BorderFactory.createEtchedBorder());
		this.add(pnlMessage, BorderLayout.NORTH);
		
		btnOk.addActionListener(this);
		btnCancel.addActionListener(this);
		
		pnlButtons.add(btnOk);
		pnlButtons.add(btnCancel);
		pnlButtons.setBorder(BorderFactory.createEtchedBorder());
		this.add(pnlButtons, BorderLayout.SOUTH);
		
		
		GroupLayout layout = new GroupLayout(pnlInputs);
		pnlInputs.setLayout(layout);
		pnlInputs.setBorder(BorderFactory.createEtchedBorder());
		
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		SequentialGroup vGrp = layout.createSequentialGroup();
		vGrp.addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(lblHost)
				.addComponent(txtHost)
			);
		if(showSchema)
			vGrp.addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(lblSchema)
				.addComponent(txtSchema)
			);
		
		vGrp.addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(lblUser)
				.addComponent(txtUser)
			);
		vGrp.addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(lblPass)
				.addComponent(txtPass)
			);
		
		ParallelGroup lGrp = layout.createParallelGroup(GroupLayout.Alignment.TRAILING);
		lGrp.addComponent(lblHost);
		if(showSchema)lGrp.addComponent(lblSchema);
		lGrp.addComponent(lblUser);
		lGrp.addComponent(lblPass);
				
		ParallelGroup rGrp = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		rGrp.addComponent(txtHost);
		if(showSchema)rGrp.addComponent(txtSchema);
		rGrp.addComponent(txtUser);
		rGrp.addComponent(txtPass);		

		layout.setVerticalGroup(vGrp);
		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup( lGrp ).addGroup( rGrp ));
		
		this.add(pnlInputs, BorderLayout.CENTER);
		
		this.pack();
	}

	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().equals(btnOk))
		{
			db = new MySQL();
				db.setHost( txtHost.getText() );
				db.setUser( txtUser.getText() );
				db.setPass( txtPass.getText() );
				if(showSchema)
					db.setSchema(txtSchema.getText() );
		}
		
		dispose();
	}
	 public void windowClosing(WindowEvent evt)
	{
		this.dispose();
	}
	 public void windowActivated(WindowEvent evt){}
	 public void windowClosed(WindowEvent evt){}
	 public void windowDeactivated(WindowEvent evt){}
	 public void windowDeiconified(WindowEvent evt){}
	 public void windowIconified(WindowEvent evt){}
	 public void windowOpened(WindowEvent evt){}
}  //  @jve:decl-index=0:visual-constraint="9,8"
