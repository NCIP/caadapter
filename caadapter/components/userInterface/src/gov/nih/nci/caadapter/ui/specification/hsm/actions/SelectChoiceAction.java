/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/SelectChoiceAction.java,v 1.3 2007-08-07 18:09:54 wangeug Exp $
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
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.nodeloader.NewHSMBasicNodeLoader;
import gov.nih.nci.caadapter.ui.specification.hsm.HSMPanel;
import gov.nih.nci.caadapter.ui.specification.hsm.wizard.AssociationListWizard;

import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class defines the select choice action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2007-08-07 18:09:54 $
 */
public class SelectChoiceAction extends AbstractHSMContextCRUDAction {
    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: SelectChoiceAction.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/SelectChoiceAction.java,v 1.3 2007-08-07 18:09:54 wangeug Exp $";

    private static final String COMMAND_NAME = "Select Choice";
    private static final Character COMMAND_MNEMONIC = new Character('S');

    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    public SelectChoiceAction(HSMPanel parentPanel) {
        this(COMMAND_NAME, null, parentPanel);
    }

    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a the specified icon.
     */
    public SelectChoiceAction(String name, Icon icon, HSMPanel parentPanel) {
        super(name, icon, parentPanel);
        setMnemonic(COMMAND_MNEMONIC);
        setActionCommandType(DOCUMENT_ACTION_TYPE);
    }


    /**
     * Invoked when an action occurs.
     */
    protected boolean doAction(ActionEvent e) {
        super.doAction(e);
        if (!isSuccessfullyPerformed()) {
            return false;
        }
        JTree tree=parentPanel.getTree();
        TreePath treePath =tree.getSelectionPath();
        if (treePath == null) {
            JOptionPane.showMessageDialog(tree.getRootPane().getParent(), "Tree has no selection",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            setSuccessfullyPerformed(false);
            return false;
        }
        DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
        Object obj = targetNode.getUserObject();
       
        if (obj instanceof MIFAssociation) {
        	MIFAssociation mifAssc = (MIFAssociation) obj;
        	MIFClass mifClass=mifAssc.getMifClass();
        	
            try {
                Iterator choiceIt=mifClass.getChoices().iterator();
                List <DatatypeBaseObject>baseList=new ArrayList<DatatypeBaseObject>();
                while(choiceIt.hasNext())
                {
                	MIFClass choiceable=(MIFClass)choiceIt.next();
                	if(!choiceable.isChoiceSelected())
                   		baseList.add((DatatypeBaseObject)choiceable);             
                }
                AssociationListWizard cloneListWizard =
                        new AssociationListWizard(baseList, true, (JFrame) tree.getRootPane().getParent(), "Clone List", true);
                DefaultSettings.centerWindow(cloneListWizard);
                cloneListWizard.setVisible(true);
                if (cloneListWizard.isOkButtonClicked()) 
                {
                    final List<DatatypeBaseObject> userSelectedMIFClass = cloneListWizard.getUserSelectedAssociation();                
                    if (userSelectedMIFClass.size()==0)
                    {
                    	 JOptionPane.showMessageDialog(tree.getRootPane().getParent(), "No choice being selected",
                                 "Invalid Action", JOptionPane.WARNING_MESSAGE);
                         setSuccessfullyPerformed(false);
                         return false;
                    }
                    else  if (userSelectedMIFClass.size()>1)
                    {
                    	JOptionPane.showMessageDialog(tree.getRootPane().getParent(), "Only one choice should be selected",
                                "Invalid Action", JOptionPane.WARNING_MESSAGE);
                        setSuccessfullyPerformed(false);
                        return false;
                    }
                    else
                    {
                    	//remove existing selection
                    	Iterator choiceAllIt=mifClass.getChoices().iterator();
                    	while(choiceAllIt.hasNext())
                    	{
                    		DatatypeBaseObject oneChoice=(DatatypeBaseObject)choiceAllIt.next();
                    		oneChoice.setChoiceSelected(false);
                    	}
                    	for (DatatypeBaseObject oneChoiceSelected:userSelectedMIFClass)
                    		oneChoiceSelected.setChoiceSelected(true);
                    	
                    	mifAssc.setChoiceSelected(true);
                    	NewHSMBasicNodeLoader mifTreeLoader=new NewHSMBasicNodeLoader(true);    	
                    	DefaultMutableTreeNode  newMIFAsscNode =mifTreeLoader.buildObjectNode(mifAssc);
                    	DefaultMutableTreeNode parentNode=(DefaultMutableTreeNode)targetNode.getParent();
                    	int oldAssIndx=parentNode.getIndex(targetNode);
                    	parentNode.remove(targetNode);
                    	parentNode.insert(newMIFAsscNode,oldAssIndx);
                    	((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
                    }
                }
                
                setSuccessfullyPerformed(true);
            } catch (Exception e1) {
                Log.logException(getClass(), e1);
                reportThrowableToUI(e1, parentPanel);
                setSuccessfullyPerformed(false);
            }
        }
		return isSuccessfullyPerformed();
	}
}

