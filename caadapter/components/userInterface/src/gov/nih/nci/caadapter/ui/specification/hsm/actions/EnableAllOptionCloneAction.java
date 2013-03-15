/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.specification.hsm.actions;

import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.nodeloader.NewHSMBasicNodeLoader;
import gov.nih.nci.caadapter.ui.common.tree.DefaultHSMTreeMutableTreeNode;
import gov.nih.nci.caadapter.ui.specification.hsm.HSMPanel;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import javax.swing.JTree;
import javax.swing.JOptionPane;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.util.HashSet;

/**
 * This class defines the action to invoke validation of HSM.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-06-09 19:54:07 $
 */
public class EnableAllOptionCloneAction extends AbstractHSMContextCRUDAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: EnableAllOptionCloneAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/EnableAllOptionCloneAction.java,v 1.4 2008-06-09 19:54:07 phadkes Exp $";

	private static final String COMMAND_NAME = "Select All Options";
	private static final Character COMMAND_MNEMONIC = new Character('S');
	private static final ImageIcon IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("enableOptionAll.gif"));
	private static final String TOOL_TIP_DESCRIPTION = "Select All Optional Clones";

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public EnableAllOptionCloneAction(HSMPanel parentPanel)
	{
		this(COMMAND_NAME, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public EnableAllOptionCloneAction(String name, HSMPanel parentPanel)
	{
		this(name, IMAGE_ICON, parentPanel);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public EnableAllOptionCloneAction(String name, Icon icon, HSMPanel parentPanel)
	{
		super(name, icon, parentPanel);
		setMnemonic(COMMAND_MNEMONIC);
		//		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setShortDescription(TOOL_TIP_DESCRIPTION);
	}


	/**
	 * Invoked when an action occurs.
	 */
	protected boolean doAction(ActionEvent e)
	{
		boolean superGood=super.doAction(e);
		if (!superGood)
			return superGood;

		//no need to check the change status as of now
		JTree mifTree=parentPanel.getTree();
		TreePath treePath = null;
		//use root as the default selection.
		Object rootObj = mifTree.getModel().getRoot();
		if(rootObj instanceof DefaultMutableTreeNode)
		{
				treePath = new TreePath(((DefaultMutableTreeNode)rootObj).getPath());
		}

		DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
		Object obj = targetNode.getUserObject();
		if (obj instanceof MIFClass)
		{
			MIFClass rootMif=(MIFClass)obj;
			enableAllOptionalClones(rootMif);
			NewHSMBasicNodeLoader mifTreeLoader=new NewHSMBasicNodeLoader(true);
			DefaultHSMTreeMutableTreeNode hsmNode=(DefaultHSMTreeMutableTreeNode)targetNode;
			DefaultMutableTreeNode  newTreeMifNode =mifTreeLoader.buildObjectNode(rootMif, hsmNode.getRootMif());

            ((DefaultTreeModel) mifTree.getModel()).setRoot(newTreeMifNode);//.nodeStructureChanged(parentNode);
            ((DefaultTreeModel) mifTree.getModel()).reload();
		}
		else //if (!(obj instanceof MIFClass))
		{
			JOptionPane.showMessageDialog(parentPanel.getParent(), "Error to enable optional clones", "Wrong Selection", JOptionPane.WARNING_MESSAGE);
			setSuccessfullyPerformed(false);
		}
		return isSuccessfullyPerformed();
	}
	private void enableAllOptionalClones(MIFClass mifClass)
	{
		HashSet <MIFAssociation>asscHash=mifClass.getAssociations();
		for (MIFAssociation assc:asscHash)
		{
			if (!assc.isMandatory())
				assc.setOptionChosen(true);
			MIFClass asscMif=assc.getMifClass();
			enableAllOptionalClones(asscMif);
		}


	}
}
