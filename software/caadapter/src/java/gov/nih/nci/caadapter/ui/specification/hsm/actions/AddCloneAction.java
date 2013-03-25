/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.specification.hsm.actions;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.specification.hsm.HSMPanel;
import gov.nih.nci.caadapter.ui.specification.hsm.wizard.AssociationListWizard;


import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFUtil;
import gov.nih.nci.caadapter.ui.common.nodeloader.NewHSMBasicNodeLoader;
import gov.nih.nci.caadapter.ui.common.tree.DefaultHSMTreeMutableTreeNode;


import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines the add optional clone action.
 *
 * @author OWNER: Eric Chen
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.11 $
 *          date        $Date: 2009-02-12 20:36:02 $
 */
public class AddCloneAction extends AbstractHSMContextCRUDAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: AddCloneAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/AddCloneAction.java,v 1.11 2009-02-12 20:36:02 wangeug Exp $";

	private static final String COMMAND_NAME = "Add Optional Clone";
	private static final Character COMMAND_MNEMONIC = new Character('C');

//	private transient JTree tree;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public AddCloneAction(HSMPanel parentPanel)
	{
		this(COMMAND_NAME, null, parentPanel);
	}


	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public AddCloneAction(String name, Icon icon, HSMPanel parentPanel)
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
		JTree tree= parentPanel.getTree();
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
		if(obj instanceof MIFClass)
			mifClass=(MIFClass) obj;
		else if (obj instanceof MIFAssociation )
		{
			MIFAssociation mifAssc=(MIFAssociation)obj;
			mifClass=mifAssc.getMifClass();
		}
		try
		{
            final List<MIFAssociation> addableAsscs = MIFUtil.findAddableAssociation(mifClass);

            List <DatatypeBaseObject>baseList=new ArrayList<DatatypeBaseObject>();
            for(MIFAssociation addableAssc:addableAsscs)
            	baseList.add((DatatypeBaseObject)addableAssc);
            AssociationListWizard cloneListWizard =
                new AssociationListWizard(baseList, false, (JFrame)tree.getRootPane().getParent(), "Clone(s) To Be Added", true);
            DefaultSettings.centerWindow(cloneListWizard);
            cloneListWizard.setVisible(true);
            if (cloneListWizard.isOkButtonClicked())
            {
                List<DatatypeBaseObject> userSelectedAssociation = cloneListWizard.getUserSelectedAssociation();
                NewHSMBasicNodeLoader mifTreeLoader=new NewHSMBasicNodeLoader(true);
                for (DatatypeBaseObject oneAssc:userSelectedAssociation)
                {

                	if (obj instanceof DatatypeBaseObject)
                	{
                		DatatypeBaseObject targetDt=(DatatypeBaseObject)obj;
                		oneAssc.setParentXmlPath(targetDt.getXmlPath());
                	}
                	DefaultHSMTreeMutableTreeNode hsmNode=(DefaultHSMTreeMutableTreeNode)targetNode;
                	DefaultMutableTreeNode oneAsscNode =mifTreeLoader.buildObjectNode((MIFAssociation)oneAssc, hsmNode.getRootMif());
                	oneAssc.setOptionChosen(true);
                	targetNode.add(oneAsscNode);
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
 * HISTORY      : Revision 1.10  2008/12/18 17:17:46  wangeug
 * HISTORY      : set optionChosen to true after new object being cloned
 * HISTORY      :
 * HISTORY      : Revision 1.9  2008/09/29 20:18:57  wangeug
 * HISTORY      : enforce code standard: license file, file description, changing history
 * HISTORY      :
 *
 * **/