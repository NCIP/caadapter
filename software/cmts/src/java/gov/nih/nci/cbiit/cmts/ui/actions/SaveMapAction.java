/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.ui.actions;

import gov.nih.nci.cbiit.cmts.ui.main.AbstractTabPanel;
import gov.nih.nci.cbiit.cmts.ui.util.GeneralUtilities;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * This class defines the concrete implementation of "Save" action.
 * <p/>
 * It will utilize the look and feel defined in DefaultSaveAction.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-09 19:04:17 $
 */
public class SaveMapAction extends SaveAsMapAction
{

	private static final String TOOL_TIP_DESCRIPTION = "Save File";
	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public SaveMapAction(AbstractTabPanel mappingPanel)
	{
		super(DefaultSaveAction.COMMAND_NAME, DefaultSaveAction.IMAGE_ICON, mappingPanel);
	}

	/**
	 * Will be called by the constructor to set additional attributes.
	 */
	protected void setAdditionalAttributes()
	{//override super class's one to plug in its own attributes.
		setIcon(DefaultSaveAction.IMAGE_ICON);
		setMnemonic(DefaultSaveAction.COMMAND_MNEMONIC);
		//hotkey//setAcceleratorKey(DefaultSaveAction.ACCELERATOR_KEY_STROKE);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
		setShortDescription(TOOL_TIP_DESCRIPTION);
	}

	/**
	 * Invoked when an action occurs.
	 */
	protected boolean doAction(ActionEvent e) throws Exception
	{
		/**
		 * Design Rationale:
		 * 1) Get the latest file from GUI panel;
		 * 2) if defaultFile and fileFromPanel are not equal, let the defaultFile be the latest value;
		 * 3) if the latest value is null, trigger SaveAs function, i.e., ask for user input;
		 * 4) if not, proceed the saving;
		 */
		File fileFromPanel = viewerPanel.getSaveFile();
		if (!GeneralUtilities.areEqual(defaultFile, fileFromPanel))
		{
			defaultFile = fileFromPanel;
		}

		if(defaultFile==null)
		{
			return super.doAction(e);
		}
		else
		{
			return processSaveFile(defaultFile);
		}
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 */
