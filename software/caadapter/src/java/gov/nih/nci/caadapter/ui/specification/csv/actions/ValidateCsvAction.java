/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.specification.csv.actions;

import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.DefaultValidateAction;
import gov.nih.nci.caadapter.ui.specification.csv.CSVPanel;
import gov.nih.nci.caadapter.ui.specification.csv.wizard.CSVValidationWizard;

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
 *          date        $Date: 2008-06-09 19:54:07 $
 */
public class ValidateCsvAction extends AbstractCsvContextCRUDAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: ValidateCsvAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/actions/ValidateCsvAction.java,v 1.2 2008-06-09 19:54:07 phadkes Exp $";

	private static final String COMMAND_NAME = DefaultValidateAction.COMMAND_NAME;
	private static final Character COMMAND_MNEMONIC = DefaultValidateAction.COMMAND_MNEMONIC;
	private static final ImageIcon IMAGE_ICON = DefaultValidateAction.IMAGE_ICON;
	private static final String TOOL_TIP_DESCRIPTION = DefaultValidateAction.TOOL_TIP_DESCRIPTION;

	private transient JTree tree;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public ValidateCsvAction(CSVPanel parentPanel)
	{
		this(COMMAND_NAME, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public ValidateCsvAction(String name, CSVPanel parentPanel)
	{
		this(name, IMAGE_ICON, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public ValidateCsvAction(String name, Icon icon, CSVPanel parentPanel)
	{
		super(name, icon, parentPanel);
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

	private JTree getTree()
	{
		if (this.tree == null)
		{
			this.tree = parentPanel.getTree();
		}
		return this.tree;
	}

	/**
	 * Invoked when an action occurs.
	 */
	protected boolean doAction(ActionEvent e)
	{
		//no need to check the change status as of now
//		super.doAction(e);
//		if (!isSuccessfullyPerformed())
//		{
//			return;
//		}
//		TreePath treePath = getTree().getSelectionPath();
//		if (treePath == null)
//		{
//			//use root as the default selection.
//			Object rootObj = getTree().getModel().getRoot();
//			if(rootObj instanceof DefaultMutableTreeNode)
//			{
//				treePath = new TreePath(((DefaultMutableTreeNode)rootObj).getPath());
//			}
//		}
//
//		if(treePath==null)
//		{//still no treepath, has to abort action.
//			JOptionPane.showMessageDialog(tree.getRootPane().getParent(), "Tree has no selection", "No Selection", JOptionPane.WARNING_MESSAGE);
//			setSuccessfullyPerformed(false);
//			return;
//		}
//		DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
//		Object obj = targetNode.getUserObject();
//		if (!(obj instanceof CloneMeta))
//		{
//			JOptionPane.showMessageDialog(tree.getRootPane().getParent(), "Please select a clone meta object", "Wrong Selection", JOptionPane.WARNING_MESSAGE);
//			setSuccessfullyPerformed(false);
//			return;
//		}
//		else
//		{
//		CSVMeta rootMeta = parentPanel.getCSVMeta(false);
//		if(rootMeta!=null)
//		{
//			CSVMetaValidator metaValidator = new CSVMetaValidator(rootMeta);
//			ValidatorResults results = metaValidator.validate();
//			parentPanel.getController().displayValidationMessage(results);
//		}

		CSVValidationWizard wizard = null;
		Container parentContainer = parentPanel.getRootContainer();
		if (parentContainer instanceof Frame)
		{
			wizard = new CSVValidationWizard((Frame) parentContainer, COMMAND_NAME, true, parentPanel);
		}
		else if (parentContainer instanceof Dialog)
		{
			wizard = new CSVValidationWizard((Dialog) parentContainer, COMMAND_NAME, true, parentPanel);
		}
		if (wizard != null)
		{
			DefaultSettings.centerWindow(wizard);
			wizard.setVisible(true);
			if (wizard.isToDockValidationResult())
			{
				ValidatorResults validatorResults = wizard.getValidatorResults();
//				if(validatorResults.getAllMessages().size()>0)
//				{
				parentPanel.getController().displayValidationMessage(validatorResults, false);
//				}
			}
		}

		setSuccessfullyPerformed(true);
		return isSuccessfullyPerformed();
//		}
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:18:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/11/02 20:24:23  jiangsc
 * HISTORY      : Enhanced.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/10/21 18:26:37  jiangsc
 * HISTORY      : First round validation implementation in CSV module.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/26 21:42:21  jiangsc
 * HISTORY      : Validation action
 * HISTORY      :
 */
