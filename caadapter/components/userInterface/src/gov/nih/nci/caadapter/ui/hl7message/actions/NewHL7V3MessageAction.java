/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.hl7message.actions;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.util.SwingWorker;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.hl7message.HL7MessagePanel;
import gov.nih.nci.caadapter.ui.hl7message.OpenHL7MessageWizard;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

/**
 * This class defines the new HL7V3 message panel action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: linc $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.11 $
 *          date        $Date: 2008-06-26 19:45:51 $
 */
public class NewHL7V3MessageAction extends AbstractContextAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: NewHL7V3MessageAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/hl7message/actions/NewHL7V3MessageAction.java,v 1.11 2008-06-26 19:45:51 linc Exp $";

	private static final String COMMAND_NAME = ActionConstants.NEW_HL7_V3_MESSAGE_TXT;
	private static final Character COMMAND_MNEMONIC = new Character('H');
	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK, false);

	private AbstractMainFrame mainFrame;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public NewHL7V3MessageAction(AbstractMainFrame mainFrame)
	{
		this(COMMAND_NAME, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public NewHL7V3MessageAction(String name, AbstractMainFrame mainFrame)
	{
		this(name, null, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public NewHL7V3MessageAction(String name, Icon icon, AbstractMainFrame mainFrame)
	{
		super(name, icon);
		this.mainFrame = mainFrame;
		setMnemonic(COMMAND_MNEMONIC);
		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DESKTOP_ACTION_TYPE);
		//do not know how to set the icon location name, or just do not matter.
	}

	protected void launchPanel(final HL7MessagePanel hl7Panel, final File dataFile, final File mapFile, final ActionEvent actionEvent)
	{
		final String actionName=this.getName();
		SwingWorker worker = new SwingWorker()
		{
			public Object construct()
			{
				try
				{
					GeneralUtilities.setCursorWaiting(mainFrame);
					mainFrame.addNewTab(hl7Panel);
					setSuccessfullyPerformed(true);
				}
				catch (Throwable t)
				{
					setSuccessfullyPerformed(false);
					Log.logException(getClass(), "May ignore and proceed", t);
				}
				finally
				{
					//back to normal, in case exception occurred.
					GeneralUtilities.setCursorDefault(mainFrame);
				}
				return null;
			}

			public void finished()
			{
				boolean everythingGood = true;
				ValidatorResults validatorResults = null;
				try
				{
					if (!isSuccessfullyPerformed())
					{//no need to proceed further
						return;
					}
					GeneralUtilities.setCursorWaiting(mainFrame);
					System.out.println(".finished()..action Name:"+actionName);
					validatorResults = hl7Panel.generateMappingMessages(dataFile, mapFile);
					System.out.println(".finished()..hl7Panel message:"+hl7Panel.getMessageList());
					everythingGood = handleValidatorResults(validatorResults);
				}
				catch (Throwable e1)
				{
					DefaultSettings.reportThrowableToLogAndUI(this, e1, "", mainFrame, false, true);
					Message msg = MessageResources.getMessage("GEN3", new Object[0]);
					//report the nice to have message
					DefaultSettings.reportThrowableToLogAndUI(this, null, msg.toString(), mainFrame, false, false);
					everythingGood = false;
				}
				finally
				{
					//back to normal.
					GeneralUtilities.setCursorDefault(mainFrame);
					setSuccessfullyPerformed(everythingGood);
					if(!everythingGood)
					{
						if (hl7Panel != null && mainFrame.hasComponentOfGivenClass(HL7MessagePanel.class, false) != null)
						{
							//use close action instead of removing it from tabbed directly so as to allow main frame to clean up maps.
							gov.nih.nci.caadapter.ui.hl7message.actions.CloseHL7V3MessageAction closeAction = new gov.nih.nci.caadapter.ui.hl7message.actions.CloseHL7V3MessageAction(hl7Panel);
							closeAction.actionPerformed(actionEvent);
						}
					}
				}//end of finally
			}
		};
		worker.start();
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e) throws Exception
	{
		if (!super.isRequestAuthorized(mainFrame))
		{
			setSuccessfullyPerformed(false);
			return isSuccessfullyPerformed();
		}
//		verify resource
		if (!isResourceReady(mainFrame))
		{
			setSuccessfullyPerformed(false);
			return isSuccessfullyPerformed();
		}
//		OpenHL7MessageWizard openWizard = new OpenHL7MessageWizard(mainFrame, COMMAND_NAME, true);
		OpenHL7MessageWizard openWizard = new OpenHL7MessageWizard(mainFrame, this.getName(), true);
		DefaultSettings.centerWindow(openWizard);
		openWizard.setVisible(true);
		boolean isOKClicked = openWizard.isOkButtonClicked();
		if (isOKClicked)
		{
			File dataFile = openWizard.getDataFile();
			File mapFile = openWizard.getMapFile();
			File destFile = openWizard.getDestFile();
			openWizard = null;
			JComponent currentActivePanel = mainFrame.hasComponentOfGivenClass(HL7MessagePanel.class, true);//getMainContextManager().getCurrentPanel();
			//release resource
			HL7MessagePanel panel = null;
			System.out.println("NewHL7V3MessageAction.doAction()..currentPanel:"+currentActivePanel);
			if (currentActivePanel instanceof HL7MessagePanel)
			{
				panel = (HL7MessagePanel) currentActivePanel;
			}
			else
			{
				panel = new HL7MessagePanel();
			}
			String msgPaneName="";
			if (dataFile.getAbsolutePath().contains(Config.CSV_DATA_FILE_DEFAULT_EXTENSTION))
				msgPaneName="Hl7 V3 Message";
			else
				msgPaneName="CSV Data";
			panel.setName(msgPaneName);
			panel.setSaveFile(destFile);
			//launch panel will determine whether the command has been executed successfully.
			launchPanel(panel, dataFile, mapFile, e);
		}
//		else
//		{
//			Log.logInfo(this, COMMAND_NAME + " command cancelled by user.");
//		}
		return isSuccessfullyPerformed();
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return this.mainFrame;
	}

	@Override
	protected ArrayList getMissedResources() {
		// TODO Auto-generated method stub
		return CaadapterUtil.getModuleResourceMissed(Config.CAADAPTER_HL7_TRANSFORMATION_RESOURCE_REQUIRED);
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.10  2008/06/09 19:53:52  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2007/10/09 21:00:13  wangeug
 * HISTORY      : save csv data from hl7MessagePanel
 * HISTORY      :
 * HISTORY      : Revision 1.8  2007/10/04 18:08:48  wangeug
 * HISTORY      : verify resource based on module
 * HISTORY      :
 * HISTORY      : Revision 1.7  2007/09/19 16:42:05  wangeug
 * HISTORY      : authorized user request
 * HISTORY      :
 * HISTORY      : Revision 1.6  2007/09/10 16:42:21  wangeug
 * HISTORY      : use a local Progressor instance
 * HISTORY      :
 * HISTORY      : Revision 1.5  2007/09/04 20:45:16  wangeug
 * HISTORY      : add progressor
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/09/04 18:30:30  wangeug
 * HISTORY      : add progressor
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/09/04 17:36:58  wangeug
 * HISTORY      : add progressor
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/27 20:37:32  wangeug
 * HISTORY      : clean codes
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/03 19:33:17  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.22  2006/11/15 19:57:38  wuye
 * HISTORY      : reorgnize menu items
 * HISTORY      :
 * HISTORY      : Revision 1.21  2006/08/02 18:44:20  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.20  2006/06/13 18:12:13  jiangsc
 * HISTORY      : Upgraded to catch Throwable instead of Exception.
 * HISTORY      :
 * HISTORY      : Revision 1.19  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.18  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/12/14 21:37:16  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/11/14 19:55:51  jiangsc
 * HISTORY      : Implementing UI enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/10/26 18:12:29  jiangsc
 * HISTORY      : replaced printStackTrace() to Log.logException
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/10/17 22:06:39  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/10/12 20:47:09  jiangsc
 * HISTORY      : minor change.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/30 18:09:48  jiangsc
 * HISTORY      : Fix to #95 to avoid hour glass after map loading.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/04 18:54:04  jiangsc
 * HISTORY      : Consolidated tabPane management into MainFrame
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/02 22:23:10  jiangsc
 * HISTORY      : Newly enhanced context-sensitive menus and toolbar.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/07/27 13:57:42  jiangsc
 * HISTORY      : Added the first round of HSMPanel.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/25 21:56:46  jiangsc
 * HISTORY      : 1) Added expand all and collapse all;
 * HISTORY      : 2) Added toolbar on the mapping panel;
 * HISTORY      : 3) Consolidated menus;
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/22 20:52:58  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/15 18:58:41  jiangsc
 * HISTORY      : 1) Reconstucted Menu bars;
 * HISTORY      : 2) Integrated FunctionPane to display property;
 * HISTORY      : 3) Enabled drag and drop functions to mapping panel.
 * HISTORY      :
 */
