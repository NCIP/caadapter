/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.ui.specification.hsm.actions;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.nodeloader.NewHSMBasicNodeLoader;
import gov.nih.nci.caadapter.ui.common.tree.DefaultHSMTreeMutableTreeNode;
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
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class defines the select choice action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.10 $
 *          date        $Date: 2008-06-09 19:54:07 $
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
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/SelectChoiceAction.java,v 1.10 2008-06-09 19:54:07 phadkes Exp $";

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
                Iterator choiceIt=mifClass.getSortedChoices().iterator();
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
                    	Iterator choiceAllIt=mifClass.getSortedChoices().iterator();
                    	while(choiceAllIt.hasNext())
                    	{
                    		DatatypeBaseObject oneChoice=(DatatypeBaseObject)choiceAllIt.next();
                    		//clean the MIFAssociation Class
                    		if (oneChoice.isChoiceSelected()&&!mifClass.getAssociations().isEmpty())
                    		{
                    			ArrayList<MIFAssociation> chosenList=new ArrayList<MIFAssociation>();
                    			for(MIFAssociation ass:mifClass.getAssociations())
                    			{
                    				if (!ass.isMandatory()&&ass.isOptionChosen())
                    				{
                    					chosenList.add(ass);
                    				}
                    			}
                    			//check if require user's confirmation
                    			if (!chosenList.isEmpty())
                    			{
                    				String msgText="You will loss your pre-selected optional clone";
                    				if (chosenList.size()>1)
                    					msgText=msgText+"s";
                    				
                    				for(MIFAssociation ass:chosenList)
                        			{
                    					msgText=msgText+"\n" +ass.getName();
                        			}
                    				msgText=msgText+"\nDo you want continue ?";
                					int reply =JOptionPane.showConfirmDialog(tree.getRootPane().getParent(), msgText,
                                            "Confirm: Reset Choice", JOptionPane.YES_NO_OPTION);
                					if (reply == JOptionPane.YES_OPTION)
                				    {
                						for(MIFAssociation ass:chosenList)
                            			{
	                    					ass.setOptionChosen(false);
	                    					ass.setOptionForced(false);
                            			}
                				    }
                					else
                						return false;
                    			}
                    		}
                    		//reomve the added optionalClone from sub-chosen class
                    		List<MIFAssociation> choiceClassAsscs = MIFUtil.findRemovableAssociation(mifAssc.findChoiceSelectedMifClass());
                    		for(MIFAssociation asscFromChoice:choiceClassAsscs)
                    		{
                    			asscFromChoice.setOptionChosen(false);
                    			asscFromChoice.setOptionForced(false);
                    		}
                    		oneChoice.setChoiceSelected(false);
                    	}
                    	for (DatatypeBaseObject oneChoiceSelected:userSelectedMIFClass)
                    	{
                    		oneChoiceSelected.setParentXmlPath(mifAssc.getParentXmlPath());
                    		oneChoiceSelected.setChoiceSelected(true);
                    	}
                    	
                    	mifAssc.setChoiceSelected(true);
                    	NewHSMBasicNodeLoader mifTreeLoader=new NewHSMBasicNodeLoader(true);  
                    	DefaultHSMTreeMutableTreeNode hsmNode=(DefaultHSMTreeMutableTreeNode)targetNode;
                    	DefaultMutableTreeNode  newMIFAsscNode =mifTreeLoader.buildObjectNode(mifAssc,hsmNode.getRootMif());
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

