/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.ui.common.resource;

import gov.nih.nci.caadapter.hl7.mif.v1.BuildResourceUtil;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * This class is the main entry class of message wizard to collect user's inputs.
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.6 $
 *          date        $Date: 2008-09-23 15:08:49 $
 */
public class BuildHL7ResourceDialog extends JDialog implements ActionListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: BuildHL7ResourceDialog.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/resource/BuildHL7ResourceDialog.java,v 1.6 2008-09-23 15:08:49 wangeug Exp $";

	private static final String OK_COMMAND = "OK";
	private static final String CANCEL_COMMAND = "Cancel";

	private OpenHL7ResourceFrontPage frontPage;
	public BuildHL7ResourceDialog(Frame owner, String title, boolean modal, String resourceSite) throws HeadlessException
	{
		super(owner, title, modal);
		initialize(title, resourceSite);
		DefaultSettings.centerWindow(this);
	}

    private void initialize(String title,String resourceSite)
	{
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		frontPage = new OpenHL7ResourceFrontPage((JFrame)getOwner(),title,resourceSite);
		contentPane.add(frontPage, BorderLayout.CENTER);

		JPanel southPanel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));//new BorderLayout());
		JButton okButton = new JButton(OK_COMMAND);
		okButton.setMnemonic('O');
		okButton.addActionListener(this);
		JButton cancelButton = new JButton(CANCEL_COMMAND);
		cancelButton.setMnemonic('C');
		cancelButton.addActionListener(this);
		JPanel tempPanel = new JPanel(new GridLayout(1, 2));
		tempPanel.add(okButton);
		tempPanel.add(cancelButton);
		buttonPanel.add(tempPanel);//, BorderLayout.EAST);
		southPanel.add(buttonPanel, BorderLayout.NORTH);
		contentPane.add(southPanel, BorderLayout.SOUTH);
		pack();
	}

    private void updateMonitor(ProgressMonitor monitor,int step,String note)
    {
    	
    	monitor.setProgress(step);
    	monitor.setNote(note);
//    	System.out.println("BuildHL7ResourceDialog.updateMonitor()..."+monitor.getNote());
    }
    private String buildHL7V3Resource(final String mifFilePath, final String coreschemaHome, final String msgSchemaHome, final boolean isSortKeyReassigning)
    {
    	String rtnMsg="Failed to build HL7 V3 resource";
    	final ProgressMonitor monitor=new ProgressMonitor(this.getParent(),
    			this.getTitle()+"\n","Additional note",0,6);
    	monitor.setMillisToDecideToPopup(0);
    	monitor.setMillisToPopup(0);
    	updateMonitor(monitor, 1, "start");
    	final JDialog owner=this;
    	Thread localThread=new Thread
    	(
    			new Runnable()
    			{
    				public void run()
    				{
			    	//process mif.zip
					try {
				    	String targetHome=System.getProperty("user.dir");
				    	int stepCount=1;
				    	//copy mif.zip to lib directory
				    	String  mifTarget=targetHome+File.separator+"lib"+File.separator+"mif.zip";
				    	BuildResourceUtil.copyFiles(mifFilePath, mifTarget, ".zip");
						
				    	//copy core schemas:
						String targetCoreschema=targetHome+File.separator+"schemas"+File.separator+"coreschemas";
						updateMonitor(monitor, stepCount++, "Copy coreschema files "+coreschemaHome);
						BuildResourceUtil.copyFiles(coreschemaHome, targetCoreschema, ".xsd");
						
						//copy Message schemas
						String targetMsgschema=targetHome+File.separator+"schemas"+File.separator+"multicacheschemas";
						updateMonitor(monitor, stepCount++, "Copy MIF schema files "+msgSchemaHome+File.separator+"multicacheschemas");
						BuildResourceUtil.copyFiles(msgSchemaHome, targetMsgschema, "xsd");
						
						monitor.close();
						String confirmMsg="Successfully loaded HL7 V3 Standards !";
						JOptionPane.showMessageDialog(owner, confirmMsg,"Success",JOptionPane.DEFAULT_OPTION);
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

						String confirmMsg="Failed to build HL7 V3 Standards"+":"+e.getMessage();
						JOptionPane.showMessageDialog(owner, confirmMsg,"Failed",JOptionPane.DEFAULT_OPTION);
						monitor.close();
					}
    			}
    		}
    	);
    	localThread.start();
		return rtnMsg;
    }
    
    private String buildHL7V2Resource(String resourceHome, String targetSite)
    {
    	
    	return "Buid HL7 V2 resource:Waiting for integration";
    }
    
    
	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		if (OK_COMMAND.equals(command))
		{
			 String confirmMsg="ConfirmAction";
			if(this.getTitle().equals(BuildHL7ResourceAction.COMMAND_BUILD_V3))
			{
				String mifFileName=frontPage.getSelectFileHome();
				String coreSchHome=frontPage.getCoreSchemaFileDirectory();
				String messageSchHome=frontPage.getMessageSchemaFileDirectory();
				String warnMsg="Please Select MIF file";
				if (mifFileName==null||mifFileName.equals(""))
				{
					JOptionPane.showConfirmDialog(this, warnMsg,"Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				warnMsg="Please Select Coreschema Home";
				if (coreSchHome==null||coreSchHome.equals(""))
				{
					JOptionPane.showConfirmDialog(this, warnMsg,"Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				warnMsg="Please Select Multicacheschema (Message Schema)Home";
				if (messageSchHome==null||messageSchHome.equals(""))
				{
					JOptionPane.showConfirmDialog(this, warnMsg,"Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				//String targetSite=frontPage.getTargetSite();
				//never reset sortKey, use false as default
				confirmMsg=buildHL7V3Resource(mifFileName,coreSchHome,messageSchHome,false);
			}
			else if(this.getTitle().equals(BuildHL7ResourceAction.COMMAND_BUILD_V2))
			{
				confirmMsg=buildHL7V2Resource(frontPage.getSelectFileHome(), frontPage.getTargetSite());
				int userReply=JOptionPane.showConfirmDialog(this, confirmMsg,"Confirm",JOptionPane.YES_NO_OPTION);
			}
		}
 			setVisible(false);
			dispose();
	}
}
/**
 * $Log: not supported by cvs2svn $
 */
