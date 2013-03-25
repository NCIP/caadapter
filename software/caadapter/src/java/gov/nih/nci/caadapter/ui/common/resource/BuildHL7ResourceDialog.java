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
import java.io.FileOutputStream;
import java.util.zip.ZipOutputStream;

/**
 * This class is the main entry class of message wizard to collect user's inputs.
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.9 $
 *          date        $Date: 2009-04-20 18:26:18 $
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
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/resource/BuildHL7ResourceDialog.java,v 1.9 2009-04-20 18:26:18 wangeug Exp $";

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
    }
    private String buildHL7V3Resource(final String mifFilePath, final String coreschemaHome, final String msgSchemaHome, final String hl7Home,
    		final boolean isSortKeyReassigning)
    {
    	String rtnMsg="Failed to build HL7 V3 resource";
    	final ProgressMonitor monitor=new ProgressMonitor(this.getParent(),
    			this.getTitle()+"\n","Additional note",0,6);
    	monitor.setMillisToDecideToPopup(0);
    	monitor.setMillisToPopup(0);
    	updateMonitor(monitor, 1, "start");
    	final JDialog owner=this;
    	Thread controlThread=new Thread
    	(
    			new Runnable()
    			{
    				public void run()
    				{
			    	try {
				       	int stepCount=1;
				    	//copy mif.zip to lib directory
				    	File mifSrc = new File(mifFilePath);
				    	System.out
								.println("BuildHL7ResourceDialog.buildHL7V3Resource()...Hl7 home:"+hl7Home);
				    	String  mifTarget=hl7Home+File.separator+mifSrc.getName();
				    	BuildResourceUtil.copyFiles(mifFilePath, mifTarget, ".zip");
						if(monitor.isCanceled())
						{
							File fDelte=new File(mifTarget);
							fDelte.delete();
							return;
						}
				    	//create oupput zip file stream
						String targetSchema=hl7Home+File.separator+"schemas.zip";//+File.separator+"coreschemas";
						ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(targetSchema));

						//copy core schemas:
						updateMonitor(monitor, stepCount++, "Copy coreschema files "+coreschemaHome);
						BuildResourceUtil.zipDir(outZip, coreschemaHome,"");
						if(monitor.isCanceled())
						{
							outZip.close();
							File fDelte=new File(targetSchema);
							fDelte.delete();
							return;
						}
						//copy Message schemas
						updateMonitor(monitor, stepCount++, "Copy MIF schema files "+msgSchemaHome+File.separator+"multicacheschemas");
						BuildResourceUtil.zipDir(outZip, msgSchemaHome,"xsd");
						if(monitor.isCanceled())
						{
							outZip.close();
							File fDelte=new File(targetSchema);
							fDelte.delete();
							return;
						}
						monitor.close();
						outZip.close();

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
    	controlThread.start();
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
				String hl7TargetSite=frontPage.getTargetSite();
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
				warnMsg="Please Select Target Site -- Normative Home";
				if (hl7TargetSite==null||hl7TargetSite.equals(""))
				{
					JOptionPane.showConfirmDialog(this, warnMsg,"Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}

				//String targetSite=frontPage.getTargetSite();
				//never reset sortKey, use false as default
				confirmMsg=buildHL7V3Resource(mifFileName,coreSchHome,messageSchHome,hl7TargetSite,false);
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
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.8  2009/03/25 13:58:22  wangeug
 * HISTORY      : load HL7 artifacts with new procedure
 * HISTORY      :
 * HISTORY      : Revision 1.7  2008/09/24 17:55:16  phadkes
 * HISTORY      : Changes for code standards
 * HISTORY      :
*/

