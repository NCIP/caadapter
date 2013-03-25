/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





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
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.5 $
 *          date        $Date: 2008-06-09 19:53:51 $
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
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/resource/BuildHL7ResourceDialog.java,v 1.5 2008-06-09 19:53:51 phadkes Exp $";

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
    private String buildHL7V3Resource(final String resourceHome, final String targetSite, final boolean isSortKeyReassigning)
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
				    	String targetHome=targetSite.substring(0,targetSite.lastIndexOf(File.separator));
				    	BuildResourceUtil.RESOURCE_DIR=targetHome+File.separator+"temp";

						String mifZipPath=resourceHome+"/processable/mif/mif.zip";
						int stepCount=0;
						updateMonitor(monitor, stepCount++, "Serialize MIF files");
						BuildResourceUtil.parserMifFromZipFile(mifZipPath, isSortKeyReassigning);
						updateMonitor(monitor, stepCount++, "Create message index");
						BuildResourceUtil.parerMifIndexFromZipFile(mifZipPath);

						//process "core schema"/datatypes
						String coreSchemaSrcHome=resourceHome+File.separator+"processable"+File.separator+"coreschemas";
						updateMonitor(monitor, stepCount++, "Serialize datatype files");
						BuildResourceUtil.loadDatatypes(coreSchemaSrcHome);
						updateMonitor(monitor, stepCount++, "Create ZIP");
						BuildResourceUtil.zipDir(targetSite, BuildResourceUtil.RESOURCE_DIR);

						//copy schema:
						//find parentDir of tagetHome../lib/
//						System.out.println(".run()..targetSite:"+targetSite);
						String schemaHome=targetSite.substring(0,targetSite.lastIndexOf(File.separator));
//						System.out.println(".run()..schemaHome:"+schemaHome);
						schemaHome=schemaHome.substring(0, schemaHome.lastIndexOf(File.separator));
//						System.out.println(".run()..schemaHome:"+schemaHome);
						schemaHome=schemaHome+File.separator+"schemas";
						updateMonitor(monitor, stepCount++, "Copy coreschema files "+schemaHome+ File.separator+"coreschemas");
						BuildResourceUtil.copyFiles(coreSchemaSrcHome, schemaHome+File.separator+"coreschemas", ".xsd");

						String mifSchemaSrcHome=resourceHome+File.separator+"processable"+File.separator+"multicacheschemas";
						updateMonitor(monitor, stepCount++, "Copy MIF schema files "+schemaHome+File.separator+"multicacheschemas");
						BuildResourceUtil.copyFiles(mifSchemaSrcHome, schemaHome+File.separator+"multicacheschemas", "xsd");
						monitor.close();
						String confirmMsg="HL7 V3 resource being successfully built at: "+targetSite;
						JOptionPane.showMessageDialog(owner, confirmMsg,"Success",JOptionPane.DEFAULT_OPTION);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
			//			rtnMsg=
						String confirmMsg="Failed to build HL7 V3 resource"+":"+e.getMessage();
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
				String srcHome=frontPage.getSelectFileHome();
				String targetSite=frontPage.getTargetSite();

				confirmMsg=buildHL7V3Resource(srcHome,targetSite,frontPage.isSortKeyReassigning());

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

