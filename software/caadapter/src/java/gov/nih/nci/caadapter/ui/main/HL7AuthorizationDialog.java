/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */
package gov.nih.nci.caadapter.ui.main;

import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * This class defines the HL7 license Authorization Dialog
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: linc $
 * @since caAdapter v1.2
 * @version    $Revision: 1.7 $
 * @date       $Date: 2008-09-26 20:35:27 $
 */
public class HL7AuthorizationDialog extends JDialog implements ActionListener {
	private String contextFilePath="";
	public static String DEFAULT_CONTEXT_FILE_PATH="/warning/hl7-authorization.txt";
	public static String HL7_V2_WARNING_CONTEXT_FILE_PATH="/warning/h7-resource-warning.txt";
	private JButton nextButton;
	private boolean viewOnly=false;
	public HL7AuthorizationDialog(JFrame owner, String arg0) throws HeadlessException {
		this(owner, arg0, null);
	}

	public HL7AuthorizationDialog(JFrame owner, String title, String filePath) throws HeadlessException {
		super(owner, title,true);
		// TODO Auto-generated constructor stub
		if (filePath==null
				||filePath.equals(""))
			contextFilePath=DEFAULT_CONTEXT_FILE_PATH;
		else
			contextFilePath=filePath;
		if (contextFilePath.equals(HL7_V2_WARNING_CONTEXT_FILE_PATH))
			setViewOnly(true);
		setSize(600, 250);
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		initUI();
		this.setResizable(false);
		DefaultSettings.centerWindow(this);
		this.setVisible(true);
	}

	private void initUI()
	{
		Container contentPane=getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(setContextTextPanel(), BorderLayout.CENTER);
		contentPane.add(setInputPanel(), BorderLayout.SOUTH);
	    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

	    addWindowListener(new WindowAdapter(){
	        public void windowClosing(WindowEvent we) {
	            Window w = we.getWindow();
	            int answer = JOptionPane.showConfirmDialog(w,
	                "Do you want to close the warning?", "Authorization: Use HL7 Artifacts", JOptionPane.YES_NO_OPTION);
	                if (answer == JOptionPane.YES_OPTION)
	                    w.dispose();
	            }
	        });
	}

	private JPanel setContextTextPanel()
	{
		JPanel rtnPanel=new JPanel();
		StringBuffer licenseBf=new StringBuffer();
		String titleMessage="";
		licenseBf.append(titleMessage);

		try {
			InputStream input=this.getClass().getResourceAsStream(contextFilePath);
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
		js2.setPreferredSize(new Dimension(this.getWidth()-40, getHeight()-60));
        mainView.setEditable(false);

		rtnPanel.add(js2);
		rtnPanel.setBorder(BorderFactory.createEtchedBorder());
		return rtnPanel;
	}

	private JPanel setInputPanel()
	{
		JPanel rtnPanel=new JPanel();
		rtnPanel.setLayout(new BorderLayout());
		rtnPanel.add(setCommandButtonPanel());
		rtnPanel.setBorder(BorderFactory.createRaisedBevelBorder());//.createEmptyBorder(10, 10, 10, 10));
		return rtnPanel;
	}

	private JPanel setCommandButtonPanel()
	{
		JPanel rtnPanel=new JPanel();
		nextButton=new JButton("Continue>");
		nextButton.addActionListener(this);
		JButton cancelButton=new JButton("Cancel");
		cancelButton.addActionListener(this);
		rtnPanel.add(cancelButton);
		if (contextFilePath !=null
				&&!contextFilePath.equalsIgnoreCase(HL7_V2_WARNING_CONTEXT_FILE_PATH))
			rtnPanel.add(nextButton);
		else
			cancelButton.setText("OK");
		return rtnPanel;
	}

	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		Object evtObj=arg0.getSource();
		if (evtObj instanceof JButton)
		{
			if (isViewOnly())
			{
				dispose();
				return;
			}
			JButton evtButton=(JButton)evtObj;
			if (evtButton==nextButton)
			{
				CaadapterUtil.setAuthorizedUser(true);
//				//launch caAdapter
//				String msg="caAdapter will not work properly because of the missing resource, \ncontinue ?";
//
//				int yesNo=JOptionPane.showConfirmDialog(this,
//						msg,
//						"Accept License Agreement",
//						JOptionPane.YES_OPTION);
//				if (yesNo==JOptionPane.YES_OPTION)
//					licenseAccepted(false);
//				else
//					return;
			}
//			else
//			{
//				System.out.println("AcceptLicenseFrame.actionPerformed()..not next button:"+evtButton);
//				licenseAccepted(false);
//			}
		}
		dispose();
		return;
	}

	public boolean isViewOnly() {
		return viewOnly;
	}

	public void setViewOnly(boolean viewOnly) {
		this.viewOnly = viewOnly;
	}

}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 */
