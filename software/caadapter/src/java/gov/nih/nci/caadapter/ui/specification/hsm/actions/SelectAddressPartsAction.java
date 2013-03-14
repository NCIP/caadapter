/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ui.specification.hsm.actions;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;

import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.nodeloader.NewHSMBasicNodeLoader;
import gov.nih.nci.caadapter.ui.common.tree.DefaultHSMTreeMutableTreeNode;
import gov.nih.nci.caadapter.ui.specification.hsm.HSMPanel;
import gov.nih.nci.caadapter.ui.specification.hsm.wizard.AssociationListWizard;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * This class defines the add multiple action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.10 $
 *          date        $Date: 2008-09-29 20:18:57 $
 */
public class SelectAddressPartsAction extends AbstractHSMContextCRUDAction
{
    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: SelectAddressPartsAction.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/SelectAddressPartsAction.java,v 1.10 2008-09-29 20:18:57 wangeug Exp $";

    public static final String ADD_PART_COMMAND_NAME = "Add Address Parts";
    public static final String REMOVE_PART_COMMAND_NAME = "Remove Address Parts";
    private static final Character COMMAND_MNEMONIC = new Character('S');


    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    public SelectAddressPartsAction(String cmdName, HSMPanel parentPanel)
    {
        this(cmdName, null, parentPanel);
    }


    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a the specified icon.
     */
    public SelectAddressPartsAction(String name, Icon icon, HSMPanel parentPanel)
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

        if (obj instanceof MIFAttribute)
        {
        	MIFAttribute mifAttr = (MIFAttribute) obj;
        	Datatype mifDt=mifAttr.getDatatype();
        	if (mifDt.isAbstract())
        		mifDt=mifAttr.getConcreteDatatype();
           	if (mifDt==null
           			||!mifDt.getName().equals("AD"))
           	{
           		JOptionPane.showMessageDialog(tree.getRootPane().getParent(), "Invalid selection",
                    "The selected type is not \"AD\"", JOptionPane.WARNING_MESSAGE);
                setSuccessfullyPerformed(false);
                return false;
           	}
           	try
       		{
           		List <DatatypeBaseObject>baseList=new ArrayList<DatatypeBaseObject>();
                boolean toAdd=true;
                if (getName().equals(REMOVE_PART_COMMAND_NAME))
                	toAdd=false;
           		Vector a = new Vector(mifDt.getAttributes().keySet());
           	 	Collections.sort(a);
           	 	Iterator attriIt = a.iterator();
           	 	while (attriIt.hasNext()) {
           	 		String attributeName = (String)attriIt.next();
           	 		Attribute attr = (Attribute)mifDt.getAttributes().get(attributeName);
           	 		if (toAdd&&(!attr.isOptionChosen()))
           	 			baseList.add(attr);
           	 		else if((!toAdd)&&attr.isOptionChosen())
           	 			baseList.add(attr);
           	 	}

                String popupTitle="Address Parts To Be Added";
                if (!toAdd)
                	popupTitle="Address Parts To Be Removed";

                AssociationListWizard listWizard =
                    new AssociationListWizard(baseList, false, (JFrame)tree.getRootPane().getParent(),popupTitle, true);
                DefaultSettings.centerWindow(listWizard);
                listWizard.setVisible(true);
                if (listWizard.isOkButtonClicked())
                {
                    List<DatatypeBaseObject> userSelectedAssociation = listWizard.getUserSelectedAssociation();
                    if (userSelectedAssociation.size()>0)
                    {
                    	for (DatatypeBaseObject oneAddrPart:userSelectedAssociation)
                    	{
                    		//either set as "true" or "false" based on action type
                    		oneAddrPart.setOptionChosen(toAdd);
                    		oneAddrPart.setEnabled(toAdd);
                    	}
                    }
                }
                System.out.println("SelectAddressPartsAction.doAction()..addresss datatype isEnabled:"+mifAttr.getDatatype().isEnabled());

                NewHSMBasicNodeLoader mifTreeLoader=new NewHSMBasicNodeLoader(true);
                DefaultHSMTreeMutableTreeNode hsmNode=(DefaultHSMTreeMutableTreeNode)targetNode;
                DefaultMutableTreeNode  newAddressNode =mifTreeLoader.buildObjectNode(mifAttr,hsmNode.getRootMif());
            	NewHSMBasicNodeLoader.refreshSubTreeByGivenMifObject(targetNode, newAddressNode, tree);
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