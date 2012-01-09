package gov.nih.nci.cbiit.cmts.ui.actions;

import gov.nih.nci.cbiit.cmts.ui.common.ActionConstants;
import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.Icon;
import javax.swing.JApplet;
import javax.swing.JOptionPane;

public class AboutViewAction extends AbstractContextAction {
	private static final String COMMAND_NAME = "About CMTS";
	private static final Character COMMAND_MNEMONIC = new Character('A');
	private JApplet mainApplet;
	public AboutViewAction(JApplet container) {
		super(COMMAND_NAME);
		this.setMnemonic(COMMAND_MNEMONIC);
		mainApplet=container;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean doAction(ActionEvent e) throws Exception {
		// TODO Auto-generated method stub
		 String aboutTextPath="/CMTS_About.txt";
		 
     	String warningMsg=readMessageFromFile(aboutTextPath).toString();
     	String frmName="About CMTS";
			JOptionPane.showMessageDialog(mainApplet, warningMsg, frmName, JOptionPane.DEFAULT_OPTION);
		return false;
	}
	private StringBuffer readMessageFromFile(String filePath)
	{
		StringBuffer licenseBf=new StringBuffer();
		try {
			InputStream input=this.getClass().getResourceAsStream(filePath);
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
		return licenseBf;
	}
	@Override
	protected Component getAssociatedUIComponent() {
		// TODO Auto-generated method stub
		return null;
	}

}
