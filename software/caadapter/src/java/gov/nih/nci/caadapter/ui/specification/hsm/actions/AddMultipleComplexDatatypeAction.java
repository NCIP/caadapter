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
import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFUtil;

import gov.nih.nci.caadapter.ui.common.nodeloader.NewHSMBasicNodeLoader;
import gov.nih.nci.caadapter.ui.common.tree.DefaultHSMTreeMutableTreeNode;
import gov.nih.nci.caadapter.ui.specification.hsm.HSMPanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;

/**
 * This class defines the action add duplicated record if the Datatype Attribute reference a complex Datatype.
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-09-29 20:18:57 $
 */
public class AddMultipleComplexDatatypeAction extends AbstractHSMContextCRUDAction
{
    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: AddMultipleComplexDatatypeAction.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/AddMultipleComplexDatatypeAction.java,v 1.3 2008-09-29 20:18:57 wangeug Exp $";

    private static final String COMMAND_NAME = "Add Multiple Attribute";
    private static final Character COMMAND_MNEMONIC = new Character('M');


    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    public AddMultipleComplexDatatypeAction(HSMPanel parentPanel)
    {
        this(COMMAND_NAME, null, parentPanel);
    }


    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a the specified icon.
     */
    public AddMultipleComplexDatatypeAction(String name, Icon icon, HSMPanel parentPanel)
    {
        super(name, icon, parentPanel);
        setMnemonic(COMMAND_MNEMONIC);
        setActionCommandType(DOCUMENT_ACTION_TYPE);
    }

    /**
     * Invoked when an action occurs.
     */
    protected boolean doAction(ActionEvent e)
    {
        super.doAction(e);
        if (!isSuccessfullyPerformed())
        {
            return false;
        }
        JTree tree=parentPanel.getTree();
        TreePath treePath = tree.getSelectionPath();
        if (treePath == null)
        {
            JOptionPane.showMessageDialog(tree.getRootPane().getParent(), "Tree has no selection",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            setSuccessfullyPerformed(false);
            return false;
        }
        DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
        Object obj = targetNode.getUserObject();

        if (obj instanceof Attribute)
        {
        	Attribute dtAttr = (Attribute) obj;
            try
            {
            	Attribute dtAttrClonned=(Attribute)dtAttr.clone();
            	DefaultMutableTreeNode parentNode =(DefaultMutableTreeNode)targetNode.getParent();
            	Object parentObj=parentNode.getUserObject();

            	Datatype parentDatatype=null;
            	if (parentObj instanceof MIFAttribute)
            	{
            		MIFAttribute parentMif=(MIFAttribute)parentObj;
            		if (parentMif.getDatatype().isAbstract())
            			parentDatatype=parentMif.getConcreteDatatype();
            		else
            			parentDatatype=parentMif.getDatatype();
            	}
            	else if (parentObj instanceof Attribute)
            	{
            		Attribute parentDtAttribute=(Attribute)parentObj;
            		//if the parentAttribute reference abstract datatype
            		//here return the concrete datatype
            		parentDatatype=parentDtAttribute.getReferenceDatatype();
            	}

            	if (parentDatatype==null)
            	{
            		JOptionPane.showMessageDialog(tree.getRootPane().getParent(), "Invalid selection",
                            "Parent Attribute is not found", JOptionPane.WARNING_MESSAGE);
                        setSuccessfullyPerformed(false);
                        return false;
            	}
            	int exitingMIFCount=MIFUtil.getMaximumAttributeMultiplicityIndexWithName(parentDatatype, dtAttr.getName());
            	dtAttrClonned.setMultiplicityIndex(exitingMIFCount+1);
            	parentDatatype.addAttribute(dtAttrClonned.getNodeXmlName(),dtAttrClonned);

            	NewHSMBasicNodeLoader mifTreeLoader=new NewHSMBasicNodeLoader(true);
            	DefaultHSMTreeMutableTreeNode hsmNode=(DefaultHSMTreeMutableTreeNode)targetNode;
            	DefaultMutableTreeNode clonnedAttrNode =mifTreeLoader.buildObjectNode(dtAttrClonned, hsmNode.getRootMif());
            	int oldNodeIndex =parentNode.getIndex(targetNode);
            	parentNode.insert(clonnedAttrNode, oldNodeIndex+exitingMIFCount+1);

            	((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
            	setSuccessfullyPerformed(true);
            }
            catch (Exception e1)
            {
                Log.logException(getClass(), e1);
                reportThrowableToUI(e1, parentPanel);
                setSuccessfullyPerformed(false);
            }
        }
		return isSuccessfullyPerformed();
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 *
 * **/