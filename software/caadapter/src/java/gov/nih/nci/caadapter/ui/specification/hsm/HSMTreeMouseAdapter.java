/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.specification.hsm;

import gov.nih.nci.caadapter.ui.specification.hsm.actions.*;

import javax.swing.JPopupMenu;
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
 *          revision    $Revision: 1.16 $
 *          date        $Date: 2009-02-12 20:35:20 $
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
    private AddMultipleComplexDatatypeAction addMultipleComplexDatatypeAction;
    private RemoveMultipleComplexDatatypeAction removeMultipleComplexDatatypeAction;

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
        addMultipleComplexDatatypeAction=new AddMultipleComplexDatatypeAction(this.parentPanel);
        removeMultipleComplexDatatypeAction=new RemoveMultipleComplexDatatypeAction(this.parentPanel);
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
        	enablePopupMenu = new JPopupMenu("Attribute Manipulation");
            //already initiated in constructor.
        	enablePopupMenu.add(enableDatafield);
        	enablePopupMenu.add(disableDatafield);
        	enablePopupMenu.addSeparator();
        	enablePopupMenu.add(addMultipleComplexDatatypeAction);
        	enablePopupMenu.add(removeMultipleComplexDatatypeAction);
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
	    addMultipleComplexDatatypeAction.setEnabled(value);
	    removeMultipleComplexDatatypeAction.setEnabled(value);
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
                    if (!mifAssc.getMifClass().getSortedChoices().isEmpty())
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
                    List<MIFAssociation> asscToAdd;
                    asscToAdd= MIFUtil.findAddableAssociation(asscMifClass);
                  //check Assc to added from the choiceSelected class
	                if (asscToAdd.size() > 0)
                        addCloneAction.setEnabled(true);
//	                else if (mifAssc.isChoiceSelected())
//	                {
//	                	//check the selected Choice item
//                    	asscToAdd= MIFUtil.findAddableAssociation(mifAssc.findChoiceSelectedMifClass());
//                    	if (asscToAdd.size() > 0)
//                    		addCloneAction.setEnabled(true);
//	                }

                    List<MIFAssociation> asscToRemove;
                    asscToRemove= MIFUtil.findRemovableAssociation(asscMifClass);
                    //check Assc to added from the choiceSelected class
		            if (asscToRemove.size() > 0)
		                	removeCloneAction.setEnabled(true);
//		            else if (mifAssc.isChoiceSelected())
//		            {
//		            	//check the selected Choice item
//                    	asscToRemove= MIFUtil.findRemovableAssociation(mifAssc.findChoiceSelectedMifClass());
//                    	if (asscToRemove.size() > 0)
//		                	removeCloneAction.setEnabled(true);
//		            }

                    if (mifAssc.isOptionChosen())
                    {
                    	forceOptionCloneAction.setEnabled(true);
                    	if (mifAssc.isOptionForced())
                    		forceOptionCloneAction.setName(ForceOptionCloneAction.DISABLE_COMMAND_FORCE_CLONE);
                    	else
                    		forceOptionCloneAction.setName(ForceOptionCloneAction.ENABLE_COMMAND_FORCE_CLONE);
                    }
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

                	if (mifDt!=null&&mifDt.getName().equals("AD")                			)
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
                	{
                		disableDatafield.setEnabled(true);
                		if (dtAttr.getMax()!=1)
                    	{
                    		if (dtAttr.getMultiplicityIndex()==0)
                    			addMultipleComplexDatatypeAction.setEnabled(true);
                    		else
                    			removeMultipleComplexDatatypeAction.setEnabled(true);
                    	}
                	}
                	else
                		enableDatafield.setEnabled(true);
                	System.out
							.println("HSMTreeMouseAdapter.showIfPopupTrigger()..dtAttr.getMax():"+dtAttr.getMax());

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
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.15  2008/09/29 20:14:14  wangeug
 * HISTORY      : enforce code standard: license file, file description, changing history
 * HISTORY      :
 *
 * **/
