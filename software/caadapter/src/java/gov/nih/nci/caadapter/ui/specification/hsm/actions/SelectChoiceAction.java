/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
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
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.15 $
 *          date        $Date: 2009-02-12 20:37:13 $
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
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/SelectChoiceAction.java,v 1.15 2009-02-12 20:37:13 wangeug Exp $";

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

        if (!(obj instanceof MIFAssociation) )
        	return false;

        	MIFAssociation mifAssc = (MIFAssociation) obj;
        	MIFClass mifClass=mifAssc.getMifClass();

            try {
//                Iterator choiceIt=mifClass.getChoices().iterator();
                List <DatatypeBaseObject>baseList=new ArrayList<DatatypeBaseObject>();
                for (MIFClass choiceClass:mifClass.getSortedChoices())
                {
                	if (!choiceClass.isChoiceSelected())
                		baseList.add(choiceClass);
                }
//                while(choiceIt.hasNext())
//                {
//                	MIFClass choiceClass=(MIFClass)choiceIt.next();
//                	if (choiceClass.isAbstractDefined())
//    				{
//    					for(MIFClass concreteChild:choiceClass.getChoices())
//    						if (!concreteChild.isChoiceSelected())
//    							baseList.add(concreteChild);
//    				}
//                	else if(!choiceClass.isChoiceSelected())
//                   		baseList.add((DatatypeBaseObject)choiceClass);
//                }
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
//                    	Iterator choiceAllIt=mifClass.getChoices().iterator();
                    	MIFClass chosenItem=null;
                    	for (MIFClass choiceClass:mifClass.getSortedChoices())
                        {
                        	if (choiceClass.isChoiceSelected())
                        		chosenItem=choiceClass;
                        }
//                    	while(choiceAllIt.hasNext())
//                    	{
//                    		MIFClass oneChoice=(MIFClass)choiceAllIt.next();
//                    		if (oneChoice.isChoiceSelected())
//                    		{
//                    			chosenItem=oneChoice;
//                    			break;
//                    		}
//                    		else if (oneChoice.isAbstractDefined())
//                    		{
//                    			for(MIFClass concreteChild:oneChoice.getChoices())
//            						if (concreteChild.isChoiceSelected())
//            						{
//            							chosenItem=concreteChild;
//                            			break;
//            						}
//                    		}
//                    	}
                    	if (chosenItem!=null)
                    	{
                    		//clean the MIFAssociation Class
                    		if (chosenItem.isChoiceSelected())//&&!mifClass.getAssociations().isEmpty())
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
                    		if (chosenItem!=null)
                    			chosenItem.setChoiceSelected(false);
                    	}
                    	for (DatatypeBaseObject oneChoiceSelected:userSelectedMIFClass)
                    	{
                    		oneChoiceSelected.setParentXmlPath(mifAssc.getParentXmlPath());
                    		((MIFClass)oneChoiceSelected).setChoiceSelected(true);
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

		return isSuccessfullyPerformed();
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.14  2008/12/23 17:37:12  wangeug
 * HISTORY      : Process MIFClass with isAbstract=true:clean code
 * HISTORY      :
 * HISTORY      : Revision 1.13  2008/12/23 14:17:14  wangeug
 * HISTORY      : Process MIFClass with isAbstract=true
 * HISTORY      :
 * HISTORY      : Revision 1.12  2008/12/18 17:10:52  wangeug
 * HISTORY      : move the property:choiceSelected to children class
 * HISTORY      :
 * HISTORY      : Revision 1.11  2008/09/29 20:18:57  wangeug
 * HISTORY      : enforce code standard: license file, file description, changing history
 * HISTORY      :
 *
 * **/
