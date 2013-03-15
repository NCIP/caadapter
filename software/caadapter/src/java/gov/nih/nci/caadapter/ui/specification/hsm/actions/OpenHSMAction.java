/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.specification.hsm.actions;

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
import gov.nih.nci.caadapter.ui.common.actions.DefaultContextOpenAction;
import gov.nih.nci.caadapter.ui.common.context.ContextManagerClient;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.specification.hsm.HSMPanel;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

/**
 * This class defines the open HSM panel action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.7 $
 *          date        $Date: 2009-04-24 18:19:24 $
 */
public class OpenHSMAction extends DefaultContextOpenAction//AbstractContextAction
{
	@Override
	protected ArrayList getMissedResources() {
		// TODO Auto-generated method stub
		return CaadapterUtil.getModuleResourceMissed(Config.CAADAPTER_HL7_TRANSFORMATION_RESOURCE_REQUIRED);
	}

	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: OpenHSMAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/OpenHSMAction.java,v 1.7 2009-04-24 18:19:24 wangeug Exp $";

	private static final String COMMAND_NAME_H3S = ActionConstants.OPEN_HSM_FILE;
	private static final Character COMMAND_MNEMONIC = new Character('S');
	private static final KeyStroke ACCELERATOR_KEY_STROKE_H3S = KeyStroke.getKeyStroke(KeyEvent.VK_2, Event.CTRL_MASK + Event.SHIFT_MASK, false);
	private static final ImageIcon IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("fileOpen.gif"));
	private static final String TOOL_TIP_DESCRIPTION = "Open HL7 v3 Specification";

	private transient File openFile;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public OpenHSMAction(AbstractMainFrame mainFrame)
	{
		this(COMMAND_NAME_H3S, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public OpenHSMAction(String name, AbstractMainFrame mainFrame)
	{
		this(name, IMAGE_ICON, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public OpenHSMAction(String name, Icon icon, AbstractMainFrame mainFrame)
	{
		super(name, icon,  mainFrame);
	}

	protected void setAdditionalAttributes()
	{
		setMnemonic(COMMAND_MNEMONIC);

		if (!super.getName().equals(COMMAND_NAME_H3S))
		{
			KeyStroke xmlHsmStroke=KeyStroke.getKeyStroke(KeyEvent.VK_1, Event.CTRL_MASK + Event.SHIFT_MASK, false);
			setAcceleratorKey(xmlHsmStroke);

		}
		else
			setAcceleratorKey(ACCELERATOR_KEY_STROKE_H3S);
		setActionCommandType(DESKTOP_ACTION_TYPE);
		setShortDescription(TOOL_TIP_DESCRIPTION);
	}

	/**
	 * Return the real implementation of ContextClient class.
	 *
	 * @return the class object.
	 */
	protected Class getContextClientClass()
	{
		return HSMPanel.class;
	}

	/**
	 * Return the real user input of the file to be opened.
	 *
	 * @return the file object.
	 */
	protected File getFileFromUserInput()
	{
		return openFile;
	}

	/**
	 * Launch the context manager client to UI.
	 *
	 * @param panel
	 * @param file
	 */
	protected void launchPanel(final ContextManagerClient panel, final File file)
	{
		final HSMPanel hsmPanel = (HSMPanel) panel;
		//have to add the new tab so as the panel may update its panel title in the tabbed pane.
		SwingWorker worker = new SwingWorker()
		{
			public Object construct()
			{
				try
				{
					GeneralUtilities.setCursorWaiting(mainFrame);
					mainFrame.addNewTab(hsmPanel);
					setSuccessfullyPerformed(true);
				}
				catch (Throwable t)
				{
					Log.logException(getClass(), "May ignore and proceed", t);
					setSuccessfullyPerformed(false);
				}
				finally
				{
					//back to normal, in case exception occurred.
					GeneralUtilities.setCursorDefault(mainFrame);
					return null;
				}
			}

			public void finished()
			{
				if (!isSuccessfullyPerformed())
				{//no need to proceed further
					return;
				}
				boolean everythingGood = true;
				ValidatorResults validatorResults = null;
				try
				{
					GeneralUtilities.setCursorWaiting(mainFrame);
					validatorResults = hsmPanel.setSaveFile(file, true);
					everythingGood = handleValidatorResults(validatorResults);
//					mainFrame.getMainContextManager().getContextFileManager().registerFileUsageListener(hsmPanel);
				}
				catch (Throwable e1)
				{
					//log the exception, but not report
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
					if (!everythingGood)
					{
						if (hsmPanel != null && mainFrame.hasComponentOfGivenClass(getContextClientClass(), false) != null)
						{
							//use close action instead of removing it from tabbed directly so as to allow main frame to clean up maps.
							gov.nih.nci.caadapter.ui.specification.hsm.actions.CloseHSMAction closeAction = new gov.nih.nci.caadapter.ui.specification.hsm.actions.CloseHSMAction(hsmPanel);
							closeAction.actionPerformed(actionEvent);
						}
					}
				}
			}
		};
		worker.start();
	}

	/**
	 * Invoked when an action occurs.
	 */
	protected boolean doAction(ActionEvent e)
	{
//		verify resource
		if (!isResourceReady(mainFrame))
		{
			setSuccessfullyPerformed(false);
			return isSuccessfullyPerformed();
		}
		//invoke openFile dialog based on Hl7 V3 specification file type
		String specExtension=Config.HSM_META_DEFINITION_FILE_DEFAULT_EXTENSION;
		String dialogTitle=Config.OPEN_DIALOG_TITLE_FOR_H3S_HSM_FILE;
//		if (!super.getName().equals(COMMAND_NAME_H3S))
//		{
//			specExtension=Config.HL7_V3_MESSAGE_FILE_DEFAULT_EXTENSION;
//			dialogTitle=Config.OPEN_DIALOG_TITLE_FOR_XML_HSM_FILE;
//		}
		File file = DefaultSettings.getUserInputOfFileFromGUI(mainFrame, //getUIWorkingDirectoryPath(),
				specExtension, dialogTitle, false, false);
		if (file != null)
		{
			openFile = file;
			super.doAction(e);
		}
//		else
//		{
//			Log.logInfo(this, "Open command cancelled by user.");
//		}
		return isSuccessfullyPerformed();
	}

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.6  2008/09/23 15:19:34  wangeug
 * HISTORY      : caAdapter 4.2 alpha release
 * HISTORY      :
 * HISTORY      : Revision 1.5  2008/06/09 19:54:07  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/10/04 18:10:06  wangeug
 * HISTORY      : verify resource based on module
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/08/13 15:24:02  wangeug
 * HISTORY      : add new menu:open H3S with "xml" format
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/08/10 16:57:57  wangeug
 * HISTORY      : Export MIF class as xml file or read a MIF class from an xml file
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:18:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.28  2006/11/28 15:17:39  jayannah
 * HISTORY      : Changed the order and names of the menuitems
 * HISTORY      :
 * HISTORY      : Revision 1.27  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.26  2006/06/13 18:12:13  jiangsc
 * HISTORY      : Upgraded to catch Throwable instead of Exception.
 * HISTORY      :
 * HISTORY      : Revision 1.25  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.24  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.23  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.22  2005/12/14 21:37:16  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.21  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.20  2005/11/14 19:55:51  jiangsc
 * HISTORY      : Implementing UI enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.19  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/10/17 22:32:00  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/10/13 18:53:44  jiangsc
 * HISTORY      : Added validation on invalid file type to map and HSM modules.
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/10/13 17:37:41  jiangsc
 * HISTORY      : Enhanced UI reporting on exceptions.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/10/10 20:48:55  jiangsc
 * HISTORY      : Enhanced dialog operation.
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/09/09 22:41:49  chene
 * HISTORY      : Saved Point
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/08/30 18:09:49  jiangsc
 * HISTORY      : Fix to #95 to avoid hour glass after map loading.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/08/18 21:04:37  jiangsc
 * HISTORY      : Save point of the synchronization effort.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/08/18 15:30:14  jiangsc
 * HISTORY      : First implementation on Switch control.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/08/11 22:10:27  jiangsc
 * HISTORY      : Open/Save File Dialog consolidation.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/08/04 18:54:03  jiangsc
 * HISTORY      : Consolidated tabPane management into MainFrame
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/03 19:10:59  jiangsc
 * HISTORY      : Some cosmetic update and make HSMPanel able to save the same content to different file.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/03 16:56:14  jiangsc
 * HISTORY      : Further consolidation of context sensitive management.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/03 14:39:08  jiangsc
 * HISTORY      : Further consolidation of context sensitive management.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/02 22:23:12  jiangsc
 * HISTORY      : Newly enhanced context-sensitive menus and toolbar.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/29 22:00:14  jiangsc
 * HISTORY      : Enhanced HSMPanel
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/28 18:18:41  jiangsc
 * HISTORY      : Can Open HSM Panel
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/27 13:57:40  jiangsc
 * HISTORY      : Added the first round of HSMPanel.
 * HISTORY      :
 */
