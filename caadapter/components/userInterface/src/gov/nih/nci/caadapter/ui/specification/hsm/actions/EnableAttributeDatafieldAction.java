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
import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.ui.common.nodeloader.NewHSMBasicNodeLoader;
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
public class EnableAttributeDatafieldAction extends AbstractHSMContextCRUDAction
{
    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: EnableAttributeDatafieldAction.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/EnableAttributeDatafieldAction.java,v 1.4 2008-06-09 19:54:07 phadkes Exp $";

    private static final String COMMAND_ENABLE_DATA = "Enable Attribute Field";
    private static final String COMMAND_DISABLE_DATA = "Disable Attribute Field";
    private static final Character COMMAND_MNEMONIC = new Character('E');

    private boolean enableFlag=true; //as default to enable one data field

    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    public EnableAttributeDatafieldAction(HSMPanel parentPanel, boolean actionType)
    {
        this(null, parentPanel, actionType);
    }


    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a the specified icon.
     */
    public EnableAttributeDatafieldAction(Icon icon, HSMPanel parentPanel , boolean actionType)
    {
        super(null,icon, parentPanel);
        if (actionType)
        	setName(COMMAND_ENABLE_DATA);
        else
        	setName(COMMAND_DISABLE_DATA);

        setMnemonic(COMMAND_MNEMONIC);
        setActionCommandType(DOCUMENT_ACTION_TYPE);
        enableFlag=actionType;
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

        if (obj instanceof Attribute)
        {
        	Attribute mifDt = (Attribute) obj;
        	mifDt.setEnabled(enableFlag);
        	//reload the current node only if "nullFlavor" field
        	if (!mifDt.isSimple())
        	{
        		if (!enableFlag)
        		{
        			//disable a complexType field, remove all its children
        			targetNode.removeAllChildren();
        			mifDt.setReferenceDatatype(null);
        		}
        		else
        		{
        			NewHSMBasicNodeLoader mifTreeLoader=new NewHSMBasicNodeLoader(true);
        			DefaultMutableTreeNode newTtargetNode = mifTreeLoader.buildObjectNode(mifDt,null);
        			NewHSMBasicNodeLoader.refreshSubTreeByGivenMifObject(targetNode, newTtargetNode, tree);        		}
        		}
  				((DefaultTreeModel) tree.getModel()).nodeStructureChanged(targetNode);
        	}
			return isSuccessfullyPerformed();
		}
}
