/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.mapping.hl7.actions;

import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.ui.common.actions.DefaultSaveAction;
import gov.nih.nci.caadapter.ui.mapping.hl7.HL7MappingPanel;

import javax.swing.Icon;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * This class defines the concrete implementation of "Save" action.
 * <p/>
 * It will utilize the look and feel defined in DefaultSaveAction.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:54:06 $
 */
public class SaveMapAction extends SaveAsMapAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: SaveMapAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/hl7/actions/SaveMapAction.java,v 1.2 2008-06-09 19:54:06 phadkes Exp $";

	private static final String TOOL_TIP_DESCRIPTION = "Save a Mapping File";
	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public SaveMapAction(HL7MappingPanel mappingPanel)
	{
		this(DefaultSaveAction.COMMAND_NAME, mappingPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public SaveMapAction(String name, HL7MappingPanel mappingPanel)
	{
		this(name, DefaultSaveAction.IMAGE_ICON, mappingPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public SaveMapAction(String name, Icon icon, HL7MappingPanel mappingPanel)
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
 * HISTORY      : Revision 1.1  2007/07/03 19:37:42  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.18  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.16  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/10/26 16:22:10  jiangsc
 * HISTORY      : Face lift to provide better error report.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/09/30 18:06:05  jiangsc
 * HISTORY      : Resolved save defect.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/08/03 16:56:17  jiangsc
 * HISTORY      : Further consolidation of context sensitive management.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/08/03 14:39:11  jiangsc
 * HISTORY      : Further consolidation of context sensitive management.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/02 22:28:55  jiangsc
 * HISTORY      : Newly enhanced context-sensitive menus and toolbar.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/07/27 22:41:11  jiangsc
 * HISTORY      : Consolidated context sensitive menu implementation.
 * HISTORY      :
 */
