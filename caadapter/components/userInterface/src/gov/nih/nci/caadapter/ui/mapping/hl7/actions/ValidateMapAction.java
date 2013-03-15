/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.mapping.hl7.actions;

import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.map.Mapping;
//import gov.nih.nci.caadapter.hl7.validation.MapValidator;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.common.actions.DefaultValidateAction;
import gov.nih.nci.caadapter.ui.common.jgraph.MappingDataManager;
import gov.nih.nci.caadapter.ui.common.message.ValidationMessageDialog;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * This class defines the action to invoke validation of HSM.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:54:06 $
 */
public class ValidateMapAction extends AbstractContextAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: ValidateMapAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/hl7/actions/ValidateMapAction.java,v 1.2 2008-06-09 19:54:06 phadkes Exp $";

	private static final String COMMAND_NAME = DefaultValidateAction.COMMAND_NAME;
	private static final Character COMMAND_MNEMONIC = DefaultValidateAction.COMMAND_MNEMONIC;
	private static final ImageIcon IMAGE_ICON = DefaultValidateAction.IMAGE_ICON;
	private static final String TOOL_TIP_DESCRIPTION = DefaultValidateAction.TOOL_TIP_DESCRIPTION;

	private transient AbstractMappingPanel parentPanel;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public ValidateMapAction(AbstractMappingPanel parentPanel)
	{
		this(COMMAND_NAME, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public ValidateMapAction(String name, AbstractMappingPanel parentPanel)
	{
		this(name, IMAGE_ICON, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public ValidateMapAction(String name, Icon icon, AbstractMappingPanel parentPanel)
	{
		super(name, icon);
		this.parentPanel = parentPanel;
		setAdditionalAttributes();
	}

	protected void setAdditionalAttributes()
	{
		setMnemonic(COMMAND_MNEMONIC);
		//		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
		setShortDescription(TOOL_TIP_DESCRIPTION);
		//do not know how to set the icon location name, or just do not matter.
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e) throws Exception
	{
		MappingDataManager mappingManager = parentPanel.getMappingDataManager();
		Mapping mappingData = mappingManager.retrieveMappingData(true);
		if (mappingData != null)
		{
			//create the dialog
			Container container = parentPanel.getRootContainer();
			ValidationMessageDialog dlg = null;
			if (container instanceof Frame)
			{
				dlg = new ValidationMessageDialog((Frame) container, true);
			}
			else if (container instanceof Dialog)
			{
				dlg = new ValidationMessageDialog((Dialog) container, true);
			}
			if (dlg != null)
			{
//				MapValidator metaValidator = new MapValidator(mappingData);
//				ValidatorResults results = metaValidator.validate();
//				dlg.setValidatorResults(results);
				DefaultSettings.centerWindow(dlg);
				dlg.setVisible(true);
			}
		}
		setSuccessfullyPerformed(true);
		return isSuccessfullyPerformed();
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return parentPanel;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/07/03 19:37:42  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/21 18:26:17  jiangsc
 * HISTORY      : Validation Class name changes.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/05 20:52:31  jiangsc
 * HISTORY      : GUI Enhancement.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/10/05 20:50:29  jiangsc
 * HISTORY      : GUI Enhancement.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/10/05 20:39:53  jiangsc
 * HISTORY      : GUI Enhancement.
 * HISTORY      :
 */
