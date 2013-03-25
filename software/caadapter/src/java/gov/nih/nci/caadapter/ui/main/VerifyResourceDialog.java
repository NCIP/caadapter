/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.main;

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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


/**
 * This class defines the Verify Resource Dialog
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: linc $
 * @since caAdapter v1.2
 * @version    $Revision: 1.5 $
 * @date       $Date: 2008-09-26 20:35:27 $
 */
public class VerifyResourceDialog extends JDialog implements ActionListener {
	private String contextFilePath="";
	public static String DEFAULT_CONTEXT_FILE_PATH="/warning/hl7-resource-warning.txt";
	private JButton nextButton;

	public VerifyResourceDialog(JFrame owner, String arg0, ArrayList rsrc) throws HeadlessException {
		this(owner, arg0, null, rsrc);
	}

	public VerifyResourceDialog(JFrame owner, String title, String filePath, ArrayList rsrc) throws HeadlessException {
		super(owner, title);
		// TODO Auto-generated constructor stub
		if (filePath==null
				||filePath.equals(""))
			contextFilePath=DEFAULT_CONTEXT_FILE_PATH;
		else
			contextFilePath=filePath;

		setSize(600, 400);
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);

		initUI(rsrc);
		this.setResizable(false);
		DefaultSettings.centerWindow(this);
		this.setVisible(true);
}

	public void licenseAccepted(boolean isAccepted)
	{
		if (isAccepted)
		{
			dispose();
		}
		else
		{
			System.exit(-1);
		}
	}

	private void initUI(ArrayList list)
	{
		Container contentPane=getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(setContextTextPanel(list), BorderLayout.CENTER);
		contentPane.add(setInputPanel(), BorderLayout.SOUTH);
	    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

	    addWindowListener(new WindowAdapter(){
	        public void windowClosing(WindowEvent we) {
	            Window w = we.getWindow();
	            int answer = JOptionPane.showConfirmDialog(w,
	                "Do you want to close the warning message ?", "caAdapter: Resources Missing", JOptionPane.YES_NO_OPTION);
	                if (answer == JOptionPane.YES_OPTION)
	                    w.dispose();
	            }
	        });
	}

	public static String setWarningContext(ArrayList list, String txtFilePath)
	{
		StringBuffer licenseBf=new StringBuffer();
		String titleMessage="The following resource/library files are not found :\n\n";
		licenseBf.append(titleMessage);

		for(String missingFileNme:(ArrayList<String>)list)
			licenseBf.append("\t"+missingFileNme+"\n");
		licenseBf.append("\n\n");


		try {
			InputStream input=VerifyResourceDialog.class.getClass().getResourceAsStream(txtFilePath);
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
		return licenseBf.toString();
	}
	private JPanel setContextTextPanel(ArrayList list)
	{
		JPanel rtnPanel=new JPanel();
//		StringBuffer licenseBf=new StringBuffer();
//		String titleMessage="The following resource/library files are not found :\n\n";
//		licenseBf.append(titleMessage);
//
//		for(String missingFileNme:(ArrayList<String>)list)
//			licenseBf.append("\t"+missingFileNme+"\n");
//		licenseBf.append("\n\n");
//
//
//		try {
//			InputStream input=this.getClass().getResourceAsStream(contextFilePath);
//			InputStreamReader reader=new InputStreamReader(input);
//			BufferedReader bfReader=new BufferedReader(reader);
//			String lineSt;
//			lineSt = bfReader.readLine();
//			while (lineSt!=null)
//			{
//				licenseBf.append(lineSt+"\n");
//				lineSt = bfReader.readLine();
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.exit(0);
//		}
		JEditorPane mainView=new JEditorPane();
		mainView.setText(setWarningContext(list,contextFilePath));
		JScrollPane js2 = new JScrollPane(mainView);
		js2.setPreferredSize(new Dimension(this.getWidth()-40, getHeight()-60));
        mainView.setEditable(false);

		rtnPanel.add(js2);
//		JLabel txtLabel=new JLabel();
//		txtLabel.setText(licenseBf.toString());
//		rtnPanel.add(txtLabel);
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
//				String msg="caAdapter will not work properly because of the missed resources, \ncontinue ?";
//
//				int yesNo=JOptionPane.showConfirmDialog(this,
//						msg,
//						"Resources Missing",
//						JOptionPane.YES_OPTION);
//				if (yesNo==JOptionPane.YES_OPTION)
					licenseAccepted(true);
//				else
//					return;
			}
			else
			{
				System.out.println("AcceptLicenseFrame.actionPerformed()..action source:"+evtButton);
				licenseAccepted(false);
			}
			dispose();
		}
	}

}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 */
