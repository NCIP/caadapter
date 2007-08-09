/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/SelectAddressPartsAction.java,v 1.6 2007-08-09 15:27:24 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.ui.specification.hsm.actions;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;

import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.nodeloader.NewHSMBasicNodeLoader;
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
 *          revision    $Revision: 1.6 $
 *          date        $Date: 2007-08-09 15:27:24 $
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
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/SelectAddressPartsAction.java,v 1.6 2007-08-09 15:27:24 wangeug Exp $";

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

           	if (!mifAttr.getType().equals("AD"))
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
           		Vector a = new Vector(mifAttr.getDatatype().getAttributes().keySet());
           	 	Collections.sort(a);
           	 	Iterator attriIt = a.iterator();
           	 	while (attriIt.hasNext()) {
           	 		String attributeName = (String)attriIt.next();
           	 		Attribute attr = (Attribute)mifAttr.getDatatype().getAttributes().get(attributeName);
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
                DefaultMutableTreeNode  newAddressNode =mifTreeLoader.buildObjectNode(mifAttr);
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

