/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/HSMTreeMouseAdapter.java,v 1.9 2007-08-29 18:48:54 wangeug Exp $
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


package gov.nih.nci.caadapter.ui.specification.hsm;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.ui.specification.hsm.actions.*;

import javax.swing.JPopupMenu;
//import javax.swing.JMenuItem;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFUtil;

/**
 * This class defines the mouse listener to responds mouse events occurred on the tree view of HSM Panel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.9 $
 *          date        $Date: 2007-08-29 18:48:54 $
 */
public class HSMTreeMouseAdapter extends MouseAdapter
{
    private HSMPanel parentPanel = null;
    private JPopupMenu popupMenu;
    private JPopupMenu enablePopupMenu;

    private AddCloneAction addCloneAction;
    private RemoveCloneAction removeCloneAction;
    private ForceOptionCloneAction forceOptionCloneAction;
    private AddMultipleCloneAction addMultipleCloneAction;
    private RemoveMultipleCloneAction removeMultipleCloneAction;
    private AddMultipleAttributeAction addMultipleAttributeAction;
    private RemoveMultipleAttributeAction removeMultipleAttributeAction;
    private SelectChoiceAction selectChoiceAction;
    private ValidateHSMAction validateHSMAction;
    private SelectAddressPartsAction addAddressPartsAction;
    private SelectAddressPartsAction removeAddressPartsAction;
    private EnableAttributeDatafieldAction enableDatafield;
    private EnableAttributeDatafieldAction disableDatafield;
    public HSMTreeMouseAdapter(HSMPanel parentPanel)
    {
        super();
        this.parentPanel = parentPanel;
        forceOptionCloneAction=new ForceOptionCloneAction(this.parentPanel);
        addCloneAction = new AddCloneAction(this.parentPanel);
        removeCloneAction = new RemoveCloneAction(this.parentPanel);
        addMultipleCloneAction = new AddMultipleCloneAction(this.parentPanel);
        removeMultipleCloneAction = new RemoveMultipleCloneAction(this.parentPanel);
        addMultipleAttributeAction = new AddMultipleAttributeAction(this.parentPanel);
        removeMultipleAttributeAction = new RemoveMultipleAttributeAction(this.parentPanel);
        selectChoiceAction = new SelectChoiceAction(this.parentPanel);
        validateHSMAction = new ValidateHSMAction(this.parentPanel);
        addAddressPartsAction=new SelectAddressPartsAction(SelectAddressPartsAction.ADD_PART_COMMAND_NAME,this.parentPanel);
        removeAddressPartsAction=new SelectAddressPartsAction(SelectAddressPartsAction.REMOVE_PART_COMMAND_NAME,this.parentPanel);
        enableDatafield=new EnableAttributeDatafieldAction(this.parentPanel,true);
        disableDatafield=new EnableAttributeDatafieldAction(this.parentPanel,false);
        
    }

    public void mousePressed(MouseEvent mouseEvent)
    {
        showIfPopupTrigger(mouseEvent);
    }

    public void mouseReleased(MouseEvent mouseEvent)
    {
        showIfPopupTrigger(mouseEvent);
    }

    private void retrievePopupMenu()
    {
    	if (enablePopupMenu == null)
        {
        	enablePopupMenu = new JPopupMenu("Datafield Manipulation");
            //already initiated in constructor.
        	enablePopupMenu.add(enableDatafield);
        	enablePopupMenu.add(disableDatafield);
        }
    	
        if (popupMenu == null)
        {
            popupMenu = new JPopupMenu("Tree Manipulation");
            //already initiated in constructor.
            popupMenu.add(addCloneAction);
            popupMenu.add(removeCloneAction);
            popupMenu.addSeparator();
            popupMenu.add(addMultipleCloneAction);
			popupMenu.add(removeMultipleCloneAction);
            popupMenu.add(addMultipleAttributeAction);
            popupMenu.add(removeMultipleAttributeAction);
            popupMenu.addSeparator();
            popupMenu.add(selectChoiceAction);
            popupMenu.addSeparator();
            popupMenu.add(validateHSMAction);
            popupMenu.add(forceOptionCloneAction);
            popupMenu.addSeparator();
            popupMenu.add(addAddressPartsAction); 
            popupMenu.add(removeAddressPartsAction);
        }
    }

	private void setAllEnabled(boolean value)
	{
		forceOptionCloneAction.setEnabled(value);
	    addCloneAction.setEnabled(value);
	    removeCloneAction.setEnabled(value);
		addMultipleCloneAction.setEnabled(value);
	    removeMultipleCloneAction.setEnabled(value);
	    addMultipleAttributeAction.setEnabled(value);
	    removeMultipleAttributeAction.setEnabled(value);
	    selectChoiceAction.setEnabled(value);
	    validateHSMAction.setEnabled(value);
	    addAddressPartsAction.setEnabled(value);
	    removeAddressPartsAction.setEnabled(value);
	    enableDatafield.setEnabled(value);
	    disableDatafield.setEnabled(value);
	}

	private void showIfPopupTrigger(MouseEvent mouseEvent)
	{
		JTree tree=parentPanel.getTree();
	    if (tree.getSelectionCount() <= 0)
	    {
            // find the selected node.
            TreePath t = tree.getClosestPathForLocation(mouseEvent.getX(), mouseEvent.getY());
            tree.setSelectionPath(t);
        }

        if (mouseEvent.isPopupTrigger())
        {
        	// setup the right-click popup menu.
            TreePath treePath = tree.getSelectionPath();
            if (treePath != null)
            {
                retrievePopupMenu();
                setAllEnabled(false);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                Object userObj = node.getUserObject();	
                if (userObj instanceof MIFClass)
                {
                    validateHSMAction.setEnabled(true);
                    MIFClass mifClass = (MIFClass) userObj;
                    final List<MIFAssociation> addableAssociations = MIFUtil.findAddableAssociation(mifClass);
                    final List<MIFAssociation> removableAssociations = MIFUtil.findRemovableAssociation(mifClass);
                    if (addableAssociations.size() > 0)
                    	addCloneAction.setEnabled(true);
                    
                    if (removableAssociations.size() > 0)
                    	removeCloneAction.setEnabled(true);
                }

                if (userObj instanceof MIFAssociation)
                {
                	validateHSMAction.setEnabled(true);
                	MIFAssociation mifAssc = (MIFAssociation) userObj;               	
                    if (!mifAssc.getMifClass().getChoices().isEmpty())
                    	selectChoiceAction.setEnabled(true);
                	if (mifAssc.getMaximumMultiplicity()!= 1)
                    {
                        
                        if (mifAssc.getMultiplicityIndex()>0)
                        {
                            removeMultipleCloneAction.setEnabled(true);
                        }
                        else
                        	addMultipleCloneAction.setEnabled(true);
                    }
                	else
                	{
                		if (MIFUtil.containChoiceAssociation(mifAssc))
                        {
                        	//it may be a 0..1 association, but it is allowed
                    		//to duplicate if there is any choice group down stream
                			 if (mifAssc.getMultiplicityIndex()>0)
                             {
                                 removeMultipleCloneAction.setEnabled(true);
                             }
                             else
                             	addMultipleCloneAction.setEnabled(true);
                        }                    		
                	}
 
                    //check if add/remove clone Item is required
                    MIFClass asscMifClass=mifAssc.getMifClass();
                    final List<MIFAssociation> asscToAdd= MIFUtil.findAddableAssociation(asscMifClass);
                    final List<MIFAssociation> asscToRemove = MIFUtil.findRemovableAssociation(asscMifClass);
                    if (asscToAdd.size() > 0)
                        addCloneAction.setEnabled(true);

                    if (asscToRemove.size() > 0)
                        removeCloneAction.setEnabled(true);
                    if (mifAssc.isOptionChosen()&&!mifAssc.isOptionForced())
                    	forceOptionCloneAction.setEnabled(true);
                }

                if (userObj instanceof MIFAttribute)
                {
                	validateHSMAction.setEnabled(true);
                	MIFAttribute mifAttr = (MIFAttribute) userObj;
                	if (mifAttr.getMaximumMultiplicity()!=1)
                    {
                        if (mifAttr.getMultiplicityIndex()>0)
                        {
                             removeMultipleAttributeAction.setEnabled(true);
                        }
                        else
                        	addMultipleAttributeAction.setEnabled(true);
                        	
                    }
                	Datatype mifDt =mifAttr.getDatatype();
                	//find concrete datatype for "ANY" type  
                	if (mifDt.isAbstract())
            			mifDt=mifAttr.getConcreteDatatype();
            		
                	if (mifDt.getName().equals("AD")                			)
                	{
                		int toAddCnt=0;
                		int toRemoveCnt=0;
                		for (Object dtAttrKey: mifDt.getAttributes().keySet())
                		{
                			Attribute dtAttr=(Attribute)mifDt.getAttributes().get((String)dtAttrKey);
                   			if (dtAttr.isOptionChosen())
                				toRemoveCnt++;
                			else
                				toAddCnt++;
                		}
                		if(toAddCnt>0)
                			addAddressPartsAction.setEnabled(true);
                		if (toRemoveCnt>0)
                			removeAddressPartsAction.setEnabled(true);
                	}
                }
                
                if (userObj instanceof Attribute)
                {
                	Attribute dtAttr = (Attribute) userObj;
                	boolean toShowPopup=false;
                	if (dtAttr.getName().equals("nullFlavor"))
                			toShowPopup=true;
                	else if (!dtAttr.isSimple())
                		toShowPopup=true;
                	
                	if (!toShowPopup)
                		return;
                	
                	if (dtAttr.isEnabled())
                		disableDatafield.setEnabled(true);
                	else
                		enableDatafield.setEnabled(true);
                	enablePopupMenu.show(mouseEvent.getComponent(),
    	                    mouseEvent.getX(), mouseEvent.getY());
                	return;
                }

                popupMenu.show(mouseEvent.getComponent(),
                    mouseEvent.getX(), mouseEvent.getY());
        }
        }	
    }
}

