/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.specification.hsm.actions;

import gov.nih.nci.caadapter.common.validation.ValidatorResults;
//import gov.nih.nci.caadapter.hl7.clone.meta.CloneMeta;
import gov.nih.nci.caadapter.hl7.validation.MIFAssociationValidator;
import gov.nih.nci.caadapter.hl7.validation.MIFAttributeValidator;
import gov.nih.nci.caadapter.hl7.validation.MIFClassValidator;
import gov.nih.nci.caadapter.ui.common.actions.DefaultValidateAction;
import gov.nih.nci.caadapter.ui.specification.hsm.HSMPanel;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;
/**
 * This class defines the action to invoke validation of HSM.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.7 $
 *          date        $Date: 2008-09-29 20:18:56 $
 */
public class ValidateHSMAction extends AbstractHSMContextCRUDAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: ValidateHSMAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/ValidateHSMAction.java,v 1.7 2008-09-29 20:18:56 wangeug Exp $";

	private static final String COMMAND_NAME = DefaultValidateAction.COMMAND_NAME;
	private static final Character COMMAND_MNEMONIC = DefaultValidateAction.COMMAND_MNEMONIC;
	private static final ImageIcon IMAGE_ICON = DefaultValidateAction.IMAGE_ICON;
	private static final String TOOL_TIP_DESCRIPTION = DefaultValidateAction.TOOL_TIP_DESCRIPTION;

	private transient JTree tree;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public ValidateHSMAction(HSMPanel parentPanel)
	{
		this(COMMAND_NAME, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public ValidateHSMAction(String name, HSMPanel parentPanel)
	{
		this(name, IMAGE_ICON, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public ValidateHSMAction(String name, Icon icon, HSMPanel parentPanel)
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


	/**
	 * Invoked when an action occurs.
	 */
	protected boolean doAction(ActionEvent e)
	{
		//no need to check the change status as of now
		JTree mifTree=parentPanel.getTree();
		TreePath treePath = mifTree.getSelectionPath();
		if (treePath == null)
		{
			//use root as the default selection.
			Object rootObj = mifTree.getModel().getRoot();
			if(rootObj instanceof DefaultMutableTreeNode)
			{
				treePath = new TreePath(((DefaultMutableTreeNode)rootObj).getPath());
			}
		}

		DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
		Object obj = targetNode.getUserObject();
		ValidatorResults results =null;
		if (obj instanceof MIFClass)
		{
			MIFClassValidator cloneValidator = new MIFClassValidator((MIFClass)obj,true);
			results = cloneValidator.validate();
		}
		else if (obj instanceof MIFAssociation)
		{
			MIFAssociationValidator asscValidator = new MIFAssociationValidator((MIFAssociation)obj);
			results = asscValidator.validate();
		}
		else if (obj instanceof MIFAttribute)
		{
			MIFAttributeValidator attrValidator = new MIFAttributeValidator((MIFAttribute)obj);
			results = attrValidator.validate();
		}
		else //if (!(obj instanceof MIFClass))
		{
			JOptionPane.showMessageDialog(parentPanel.getParent(), "Please select an object (Clone|MIFAssocation|MIFAttribute) for validation", "Wrong Selection", JOptionPane.WARNING_MESSAGE);
			setSuccessfullyPerformed(false);
		}
		 if (results!=null)
		 {
			parentPanel.getController().displayValidationMessage(results);
		System.out.println("ValidateHSMAction.doAction()..validate object:"+((DatatypeBaseObject)obj).getXmlPath());
			parentPanel.getMessagePane().setValidatedElement(((DatatypeBaseObject)obj).getXmlPath());
			setSuccessfullyPerformed(true);
		 }
		return isSuccessfullyPerformed();
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 *
 * **/