/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmts.ui.actions;

import gov.nih.nci.cbiit.cmts.ui.mapping.MappingMainPanel;
import gov.nih.nci.cbiit.cmts.ui.util.GeneralUtilities;

import javax.swing.Icon;
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

	private static final String TOOL_TIP_DESCRIPTION = "Save a Mapping File";
	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public SaveMapAction(MappingMainPanel mappingPanel)
	{
		this(DefaultSaveAction.COMMAND_NAME, mappingPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public SaveMapAction(String name, MappingMainPanel mappingPanel)
	{
		this(name, DefaultSaveAction.IMAGE_ICON, mappingPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public SaveMapAction(String name, Icon icon, MappingMainPanel mappingPanel)
	{
		super(name, icon, mappingPanel);
//		setAdditionalAttributes();
	}

	/**
	 * Will be called by the constructor to set additional attributes.
	 */
	protected void setAdditionalAttributes()
	{//override super class's one to plug in its own attributes.
		setIcon(DefaultSaveAction.IMAGE_ICON);
		setMnemonic(DefaultSaveAction.COMMAND_MNEMONIC);
		setAcceleratorKey(DefaultSaveAction.ACCELERATOR_KEY_STROKE);
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
		File fileFromPanel = mappingPanel.getSaveFile();
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
