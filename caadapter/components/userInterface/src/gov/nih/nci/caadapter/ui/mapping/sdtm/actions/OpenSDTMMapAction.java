/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.mapping.sdtm.actions;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.SwingWorker;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.DefaultContextOpenAction;
import gov.nih.nci.caadapter.ui.common.context.ContextManagerClient;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.mapping.mms.Object2DBMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.sdtm.Database2SDTMMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.sdtm.OpenSDTMMapFile;

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
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2 revision $Revision: 1.8 $ date $Date: 2008-06-09 19:54:06 $
 */
public class OpenSDTMMapAction extends DefaultContextOpenAction
{
	protected static String COMMAND_NAME = ActionConstants.OPEN_CSV2SDTM_MAP_FILE_TXT;

	protected static Character COMMAND_MNEMONIC = new Character('P');

	protected static KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_4, Event.CTRL_MASK + Event.SHIFT_MASK, false);

	// private static final ImageIcon IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("fileOpen.gif"));
	protected static String TOOL_TIP_DESCRIPTION = "Open a CSV/Database-2-RDS Mapping File";

	private transient File openFile;

	/**
	 * Defines an <code>Action</code> object with a default description string and default icon.
	 */
	public OpenSDTMMapAction(AbstractMainFrame mainFrame) {
		this(COMMAND_NAME, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified description string and a default icon.
	 */
	public OpenSDTMMapAction(String name, AbstractMainFrame mainFrame) {
		this(name, IMAGE_ICON, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified description string and a the specified icon.
	 */
	public OpenSDTMMapAction(String name, Icon icon, AbstractMainFrame mainFrame) {
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
	 *
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
		final Database2SDTMMappingPanel mp = new Database2SDTMMappingPanel(mainFrame, "_conn", true);
		// have to add the new tab so as the panel may update its panel title in the tabbed pane.
		SwingWorker worker = new SwingWorker() {
			public Object construct()
			{
				try {
					//GeneralUtilities.setCursorWaiting(mainFrame);
					mainFrame.addNewTab(mp);
					try {
						new OpenSDTMMapFile(mp, file.getAbsolutePath().toString());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					setSuccessfullyPerformed(true);
				} catch (Throwable t) {
					Log.logException(getClass(), "May ignore and proceed", t);
					setSuccessfullyPerformed(false);
				} finally {
					// back to normal, in case exception occurred.
					//GeneralUtilities.setCursorDefault(mainFrame);
					return null;
				}
			}

			public void finished()
			{
				if (!isSuccessfullyPerformed()) {// no need to proceed further
					return;
				}
				// this variable will help determine whether or not to close the created panel in the event of validation errors or exceptions.
				boolean everythingGood = true;
				ValidatorResults validatorResults = null;
				try {
					//GeneralUtilities.setCursorWaiting(mainFrame);
					// validatorResults = mappingPanel.processOpenMapFile(file);
					// everythingGood = handleValidatorResults(validatorResults);
					/*
					 * TODO verify whether needs to call the validator
					 */
					// mainFrame.getMainContextMana/ger().getContextFileManager().registerFileUsageListener(mappingPanel);
				} catch (Throwable e1) {
					// reportThrowableToUI(e1, mainFrame);
					// log the exception, but not report
					DefaultSettings.reportThrowableToLogAndUI(this, e1, "", mainFrame, false, true);
					Message msg = MessageResources.getMessage("GEN3", new Object[0]);
					// report the nice to have message
					DefaultSettings.reportThrowableToLogAndUI(this, null, msg.toString(), mainFrame, false, false);
					everythingGood = false;
				} finally {
					// back to normal.
					//GeneralUtilities.setCursorDefault(mainFrame);
					setSuccessfullyPerformed(everythingGood);
					if (!everythingGood) {// do the clean up.
					// Message msg = MessageResources.getMessage("GEN3", new Object[0]);
					// JOptionPane.showMessageDialog(mainFrame, msg.toString(), "Error", JOptionPane.ERROR_MESSAGE);
						if (mp != null && mainFrame.hasComponentOfGivenClass(getContextClientClass(), false) != null) {
							// mainFrame.getTabbedPane().remove(mappingPanel);
							// use close action instead of removing it from tabbed directly so as to allow main frame to clean up maps.
							gov.nih.nci.caadapter.ui.mapping.hl7.actions.CloseMapAction closeAction = new gov.nih.nci.caadapter.ui.mapping.hl7.actions.CloseMapAction(mp);
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
		File file = DefaultSettings.getUserInputOfFileFromGUI(mainFrame, // getUIWorkingDirectoryPath(),
				Config.MAP_FILE_DEFAULT_EXTENTION, Config.OPEN_DIALOG_TITLE_FOR_MAP_FILE, false, false);
		if (file != null) {
			openFile = file;
			super.doAction(e);
		}
		// else
		// {
		// Log.logInfo(this, "Open command cancelled by user.");
		// }
		return isSuccessfullyPerformed();
	}

	@Override
	protected ArrayList getMissedResources() {
		// TODO Auto-generated method stub
		return CaadapterUtil.getModuleResourceMissed(Config.CAADAPTER_QUERYBUILDER_RESOURCE_REQUIRED);
	}

}
