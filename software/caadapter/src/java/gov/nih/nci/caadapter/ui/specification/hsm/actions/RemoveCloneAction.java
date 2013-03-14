/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.specification.hsm.actions;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFUtil;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.specification.hsm.HSMPanel;
import gov.nih.nci.caadapter.ui.specification.hsm.wizard.AssociationListWizard;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines the remove optional clone action.
 *
 * @author OWNER: Eric Chen
 * @author LAST UPDATE $Author: altturbo $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.9 $
 *          date        $Date: 2009-04-03 15:42:09 $
 */
public class RemoveCloneAction extends AbstractHSMContextCRUDAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: RemoveCloneAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/RemoveCloneAction.java,v 1.9 2009-04-03 15:42:09 altturbo Exp $";

	private static final String COMMAND_NAME = "Remove Optional Clone";
	private static final Character COMMAND_MNEMONIC = new Character('C');

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public RemoveCloneAction(HSMPanel parentPanel)
	{
		this(COMMAND_NAME, null, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public RemoveCloneAction(String name, Icon icon, HSMPanel parentPanel)
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
		if(!isSuccessfullyPerformed())
		{
			return false;
		}
		JTree tree=parentPanel.getTree();
		TreePath treePath = tree.getSelectionPath();
		if(treePath==null)
		{
			JOptionPane.showMessageDialog(tree.getRootPane().getParent(),
                "Tree has no selection",
				"No Selection",
                JOptionPane.WARNING_MESSAGE);
			setSuccessfullyPerformed(false);
			return false;
		}

        DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
		Object obj = targetNode.getUserObject();
		MIFClass mifClass =null;
		MIFClass choiceSelected=null;
		if(obj instanceof MIFClass)
		{
			mifClass = (MIFClass) obj;
		}
		else if (obj instanceof MIFAssociation )
		{
			MIFAssociation mifAssc=(MIFAssociation)obj;
			mifClass=mifAssc.getMifClass();
			if (mifAssc.isChoiceSelected())
				choiceSelected=mifAssc.findChoiceSelectedMifClass();
		}

		try
		{
            final List<MIFAssociation> removeableAsscs = MIFUtil.findRemovableAssociation(mifClass);
            final List <DatatypeBaseObject>baseList=new ArrayList<DatatypeBaseObject>();

            for(MIFAssociation removeableAssc:removeableAsscs)
            	baseList.add((DatatypeBaseObject)removeableAssc);

//            if (choiceSelected!=null)
//            {
//            	final List<MIFAssociation> choiceClassRemovableAsscs = MIFUtil.findRemovableAssociation(choiceSelected);
//            	for(MIFAssociation removableAssc:choiceClassRemovableAsscs)
//                	baseList.add((DatatypeBaseObject)removableAssc);
//            }

            AssociationListWizard cloneListWizard =
                new AssociationListWizard(baseList, false, (JFrame)tree.getRootPane().getParent(), "Clone(s) To Be Removed", true);
            DefaultSettings.centerWindow(cloneListWizard);
            cloneListWizard.setVisible(true);
            if (cloneListWizard.isOkButtonClicked())
            {
                List<DatatypeBaseObject> userSelectedAssociation = cloneListWizard.getUserSelectedAssociation();
                for (DatatypeBaseObject oneAssc:userSelectedAssociation)
                {
                	oneAssc.setOptionChosen(false);
                	//remove the node from targetNode
                	for(int i=0;i<targetNode.getChildCount();i++)
                	{
                		DefaultMutableTreeNode oneAsscNode =(DefaultMutableTreeNode)targetNode.getChildAt(i);
                		Object oneChildObj=oneAsscNode.getUserObject();
                		if (oneChildObj instanceof MIFAssociation)
                		{
                			MIFAssociation oneChildAssc=(MIFAssociation)oneChildObj;
                			if (oneChildAssc.getName().equals(oneAssc.getName()))
                    		{
                    			oneAsscNode.removeFromParent();
                    			break;
                    		}
                		}
                	}
                }
                ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(targetNode);
            }
            setSuccessfullyPerformed(true);
		}
		catch (Exception e1)
		{
			Log.logException(getClass(), e1);
			reportThrowableToUI(e1, parentPanel);
			setSuccessfullyPerformed(false);
		}
		return isSuccessfullyPerformed();
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.8  2009/02/12 20:36:45  wangeug
 * HISTORY      : use sortedChoice() to include all choiceItems from sub-list
 * HISTORY      :
 * HISTORY      : Revision 1.7  2008/09/29 20:18:56  wangeug
 * HISTORY      : enforce code standard: license file, file description, changing history
 * HISTORY      :
 *
 * **/