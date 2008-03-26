/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/resource/BuildHL7ResourceDialog.java,v 1.4 2008-03-26 14:43:30 umkis Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-03-26 14:43:30 $
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
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/resource/BuildHL7ResourceDialog.java,v 1.4 2008-03-26 14:43:30 umkis Exp $";

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

