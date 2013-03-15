/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.specification.hsm.actions;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.ui.specification.hsm.HSMPanel;

import javax.swing.JTree;
import javax.swing.JOptionPane;
import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;


/**
 * This class enable/disable the datatype field of one MIFAttribute
 *
 */
public class ForceOptionCloneAction extends AbstractHSMContextCRUDAction
{
    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: ForceOptionCloneAction.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/ForceOptionCloneAction.java,v 1.3 2008-06-09 19:54:07 phadkes Exp $";

    private static final String DEFAULT_COMMAND_FORCE_CLONE = "Force XML";
    public static final String ENABLE_COMMAND_FORCE_CLONE = "Enable Force XML";
    public static final String DISABLE_COMMAND_FORCE_CLONE = "Disable Force XML";

    private static final Character COMMAND_MNEMONIC = new Character('F');

    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    public ForceOptionCloneAction(HSMPanel parentPanel)
    {
        this(null, parentPanel);
    }


    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a the specified icon.
     */
    public ForceOptionCloneAction(Icon icon, HSMPanel parentPanel)
    {
        super(null,icon, parentPanel);

       	setName(DEFAULT_COMMAND_FORCE_CLONE);
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

        if (obj instanceof MIFAssociation)
        {
        	MIFAssociation mifAssc = (MIFAssociation) obj;
        	if (getName().equals(ENABLE_COMMAND_FORCE_CLONE))
        		mifAssc.setOptionForced(true);
        	else if (getName().equals(DISABLE_COMMAND_FORCE_CLONE))
        		mifAssc.setOptionForced(false);
        	//reload the current node only if "nullFlavor" field
        	setName(DEFAULT_COMMAND_FORCE_CLONE);
        	setSuccessfullyPerformed(true);
  			((DefaultTreeModel) tree.getModel()).nodeStructureChanged(targetNode);
        }
		return isSuccessfullyPerformed();
	}
}
