/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.main;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/**
 * This class defines the Window for Accepting License terms
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: linc $
 * @since caAdapter v1.2
 * @version    $Revision: 1.4 $
 * @date       $Date: 2008-09-26 20:35:27 $
 */
public class AcceptLicenseFrame extends JFrame implements ActionListener {
	private String licenseFilePath="";
	public static String ACCEPT_AGREEMENT_STATEMENT="I accept the terms in the license agreement";
	public static String NOT_ACCEPT_AGREEMENT_STATEMENT="I do not accept the terms in the license agreement";
	public static String DEFAULT_LICENSE_FILE_PATH="/license/caAdapter_license.txt";
	private JButton nextButton;
	private JCheckBox acceptBox;
	private JCheckBox denyBox;
	public AcceptLicenseFrame() throws HeadlessException {
		this("caAdapter License Agreement");
	}


	public AcceptLicenseFrame(String arg0) throws HeadlessException {
		this(arg0,null);
	}

	public AcceptLicenseFrame(String arg0, String filePath) throws HeadlessException {
		super(arg0);
		// TODO Auto-generated constructor stub
		if (filePath==null
				||filePath.equals(""))
			licenseFilePath=DEFAULT_LICENSE_FILE_PATH;
		else
			licenseFilePath=filePath;

		Image icon = DefaultSettings.getImage("caAdapter-icon.gif");//using default image file
		setIconImage(icon);
		setSize(600, 400);
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);

		initUI();
		this.setResizable(false);
		this.setVisible(true);
		DefaultSettings.centerWindow(this);
	}

	public void licenseAccepted(boolean isAccepted)
	{
		if (isAccepted)
		{
			dispose();
			new MainFrame().launch();
		}
		else
		{
			System.exit(-1);
		}
	}

	private void initUI()
	{
		Container contentPane=getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(setLicenseTextPanel(), BorderLayout.CENTER);
		contentPane.add(setInputPanel(), BorderLayout.SOUTH);
	    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

	    addWindowListener(new WindowAdapter(){
	        public void windowClosing(WindowEvent we) {
	            Window w = we.getWindow();
	            int answer = JOptionPane.showConfirmDialog(w,
	                "Do you want to exit the program?", "caAdapter License Agreement", JOptionPane.YES_NO_OPTION);
	                if (answer == JOptionPane.YES_OPTION)
	                    w.dispose();

	            }
	        });
	}

	private JPanel setLicenseTextPanel()
	{
		JPanel rtnPanel=new JPanel();
		StringBuffer licenseBf=new StringBuffer();
		try {
			InputStream input=this.getClass().getResourceAsStream(licenseFilePath);
			InputStreamReader reader=new InputStreamReader(input);
			BufferedReader bfReader=new BufferedReader(reader);
			String lineSt;
			lineSt = bfReader.readLine();
			while (lineSt!=null)
			{
				licenseBf.append(lineSt+"\n");
				lineSt = bfReader.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		JEditorPane mainView=new JEditorPane();
		mainView.setText(licenseBf.toString());
		JScrollPane js2 = new JScrollPane(mainView);
		js2.setPreferredSize(new Dimension(this.getWidth()-40, getHeight()-160));
        mainView.setEditable(false);

		rtnPanel.add(js2);
		rtnPanel.setBorder(BorderFactory.createEtchedBorder());
		return rtnPanel;
	}

	private JPanel setInputPanel()
	{
		JPanel rtnPanel=new JPanel();
		rtnPanel.setLayout(new BorderLayout());

		acceptBox=new JCheckBox(ACCEPT_AGREEMENT_STATEMENT);
		denyBox=new JCheckBox(NOT_ACCEPT_AGREEMENT_STATEMENT);
		ButtonGroup group=new ButtonGroup();
		group.add(acceptBox);
		group.add(denyBox);


		JPanel containPane=new JPanel();
		containPane.setBorder(BorderFactory.createEmptyBorder(5,
				20,
				5,
				50));
		containPane.setLayout(new GridLayout(3,1));
		containPane.add(acceptBox);
		containPane.add(denyBox);
		containPane.add(setCommandButtonPanel());
		rtnPanel.add(containPane);
		rtnPanel.setBorder(BorderFactory.createRaisedBevelBorder());//.createEmptyBorder(10, 10, 10, 10));
		return rtnPanel;
	}

	private JPanel setCommandButtonPanel()
	{
		JPanel rtnPanel=new JPanel();
		nextButton=new JButton("Next>");
		nextButton.addActionListener(this);
		JButton cancelButton=new JButton("Cancel");
		cancelButton.addActionListener(this);
		rtnPanel.add(nextButton);
		rtnPanel.add(cancelButton);
		return rtnPanel;
	}

	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		Object evtObj=arg0.getSource();
		if (evtObj instanceof JButton)
		{
			JButton evtButton=(JButton)evtObj;
			if (evtButton==nextButton)
			{
				//launch caAdapter
				if (acceptBox.isSelected())
					licenseAccepted(true);
				else if(denyBox.isSelected())
					licenseAccepted(false);
				else
				{
					String msg="You have not check your license agreement, \ncontinue ?";

					int yesNo=JOptionPane.showConfirmDialog(this,
							msg,
							"Accept License Agreement",
							JOptionPane.YES_OPTION);
					if (yesNo==JOptionPane.YES_OPTION)
						licenseAccepted(false);
					else
						return;
				}
			}
			else
			{
				System.out.println("AcceptLicenseFrame.actionPerformed()..action source:"+evtButton);
				licenseAccepted(false);
			}
		}

	}

}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 */
