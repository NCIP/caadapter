/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.mapping.mms.actions;

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
import gov.nih.nci.caadapter.ui.mapping.mms.Object2DBMappingPanel;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

/**
 * This class defines the open Map panel action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @since caAdapter v1.2
 * @version    $Revision: 1.8 $
 * @date       $Date: 2009-06-12 15:53:01 $
 */
public class OpenObjectToDbMapAction extends DefaultContextOpenAction
{
	protected static String COMMAND_NAME = ActionConstants.OPEN_O2DB_MAP_FILE_TXT;
	protected static Character COMMAND_MNEMONIC = new Character('O');
	protected static KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_5, Event.CTRL_MASK + Event.SHIFT_MASK, false);
	protected static String TOOL_TIP_DESCRIPTION = "Open a Object-2-Database Mapping File";

    private transient File openFile;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public OpenObjectToDbMapAction(AbstractMainFrame mainFrame)
	{
		this(COMMAND_NAME, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public OpenObjectToDbMapAction(String name, AbstractMainFrame mainFrame)
	{
		this(name, IMAGE_ICON, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public OpenObjectToDbMapAction(String name, Icon icon, AbstractMainFrame mainFrame)
	{
		super(name, icon, mainFrame);
	}

    protected void setAdditionalAttributes()
	{
		setMnemonic(COMMAND_MNEMONIC);
		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DESKTOP_ACTION_TYPE);
		setShortDescription(TOOL_TIP_DESCRIPTION);
	}

	/**
	 * Return the real implementation of ContextClient class.
	 * @return the class object
	 */
	protected Class getContextClientClass()
	{
		return Object2DBMappingPanel.class;
	}

	/**
	 * Return the real user input of the file to be opened.
	 *
	 * @return the file object
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
		final Object2DBMappingPanel mappingPanel  = (Object2DBMappingPanel) panel;
		//have to add the new tab so as the panel may update its panel title in the tabbed pane.
		SwingWorker worker = new SwingWorker()
		{
			public Object construct()
			{
				try
				{
					GeneralUtilities.setCursorWaiting(mainFrame);
					mainFrame.addNewTab(mappingPanel);
					setSuccessfullyPerformed(true);
				}
				catch(Throwable t)
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
				//this variable will help determine whether or not to close the created panel in the event of validation errors or exceptions.

                boolean everythingGood = true;
				ValidatorResults validatorResults = null;
				try
				{
                    GeneralUtilities.setCursorWaiting(mainFrame);
					validatorResults = mappingPanel.processOpenMapFile(file);
					mappingPanel.setChanged(false);
					Thread.sleep(1);
				}
				catch (Throwable e1)
				{
//					reportThrowableToUI(e1, mainFrame);
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
					{//do the clean up.
//						Message msg = MessageResources.getMessage("GEN3", new Object[0]);
//						JOptionPane.showMessageDialog(mainFrame, msg.toString(), "Error", JOptionPane.ERROR_MESSAGE);
						if (mappingPanel != null && mainFrame.hasComponentOfGivenClass(getContextClientClass(), false) != null)
						{
						//						mainFrame.getTabbedPane().remove(mappingPanel);
						//use close action instead of removing it from tabbed directly so as to allow main frame to clean up maps.
							gov.nih.nci.caadapter.ui.mapping.hl7.actions.CloseMapAction closeAction = new gov.nih.nci.caadapter.ui.mapping.hl7.actions.CloseMapAction(mappingPanel);
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
		File file = DefaultSettings.getUserInputOfFileFromGUI(mainFrame, //getUIWorkingDirectoryPath(),
				Config.TAGGED_MAP_FILE_DEFAULT_EXTENTION, Config.OPEN_DIALOG_TITLE_FOR_MAP_FILE, false, false);
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

	@Override
	protected ArrayList getMissedResources() {
		// TODO Auto-generated method stub
		return CaadapterUtil.getModuleResourceMissed(Config.CAADAPTER_COMMON_RESOURCE_REQUIRED);
	}

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.7  2008/09/26 20:35:27  linc
 * HISTORY      : Updated according to code standard.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2008/06/09 19:54:06  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2007/10/04 18:09:23  wangeug
 * HISTORY      : verify resource based on module
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/09/20 16:40:14  schroedn
 * HISTORY      : License text
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/06/18 15:22:52  schroedn
 * HISTORY      : added setChanged(false) flag to fix save on close bug
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/06/07 18:31:19  schroedn
 * HISTORY      : Edits to sync with new codebase and java webstart
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:57  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.4  2006/12/20 16:37:17  wuye
 * HISTORY      : delay 1 second
 * HISTORY      :
 * HISTORY      : Revision 1.3  2006/11/28 15:20:14  jayannah
 * HISTORY      : Changed the order and names of the menuitems
 * HISTORY      :
 * HISTORY      : Revision 1.2  2006/10/20 21:30:34  wuye
 * HISTORY      : Updated OpenObjectToDbMapAction for Object 2 database mapping
 * HISTORY      :
 * HISTORY      : Revision 1.1  2006/09/26 15:48:30  wuye
 * HISTORY      : New actions for object - 2 db mapping
 * HISTORY      :
 * HISTORY      : Revision 1.24  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.23  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.22  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.21  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.20  2005/12/14 21:37:16  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.19  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/11/14 19:55:51  jiangsc
 * HISTORY      : Implementing UI enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/10/13 18:53:44  jiangsc
 * HISTORY      : Added validation on invalid file type to map and HSM modules.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/10/10 20:48:56  jiangsc
 * HISTORY      : Enhanced dialog operation.
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/08/29 17:48:55  jiangsc
 * HISTORY      : Fix to #95 to avoid hour glass after map loading.
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/08/18 21:04:37  jiangsc
 * HISTORY      : Save point of the synchronization effort.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/08/18 15:30:14  jiangsc
 * HISTORY      : First implementation on Switch control.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/08/11 22:10:27  jiangsc
 * HISTORY      : Open/Save File Dialog consolidation.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/08/04 18:54:03  jiangsc
 * HISTORY      : Consolidated tabPane management into MainFrame
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/08/03 16:56:14  jiangsc
 * HISTORY      : Further consolidation of context sensitive management.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/02 22:23:12  jiangsc
 * HISTORY      : Newly enhanced context-sensitive menus and toolbar.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/07/27 13:57:40  jiangsc
 * HISTORY      : Added the first round of HSMPanel.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/07/22 20:52:59  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
