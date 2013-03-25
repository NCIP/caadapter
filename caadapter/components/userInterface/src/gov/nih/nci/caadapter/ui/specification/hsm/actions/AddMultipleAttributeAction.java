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
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFUtil;

import gov.nih.nci.caadapter.ui.common.nodeloader.NewHSMBasicNodeLoader;
import gov.nih.nci.caadapter.ui.common.tree.DefaultHSMTreeMutableTreeNode;
import gov.nih.nci.caadapter.ui.specification.hsm.HSMPanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.List;

/**
 * This class defines the add multiple action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.7 $
 *          date        $Date: 2008-06-09 19:54:07 $
 */
public class AddMultipleAttributeAction extends AbstractHSMContextCRUDAction
{
    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: AddMultipleAttributeAction.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/AddMultipleAttributeAction.java,v 1.7 2008-06-09 19:54:07 phadkes Exp $";

    private static final String COMMAND_NAME = "Add Multiple Attribute";
    private static final Character COMMAND_MNEMONIC = new Character('M');


    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    public AddMultipleAttributeAction(HSMPanel parentPanel)
    {
        this(COMMAND_NAME, null, parentPanel);
    }


    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a the specified icon.
     */
    public AddMultipleAttributeAction(String name, Icon icon, HSMPanel parentPanel)
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

        if (obj instanceof MIFAttribute)
        {
        	MIFAttribute mifAttr = (MIFAttribute) obj;
            try
            {
            	MIFAttribute mifClonned=(MIFAttribute)mifAttr.clone();
            	DefaultMutableTreeNode parentNode =(DefaultMutableTreeNode)targetNode.getParent();
            	Object parentObj=parentNode.getUserObject();

            	MIFClass parentMif=null;
            	if (parentObj instanceof MIFClass)
            		parentMif=(MIFClass)parentObj;
            	else if (parentObj instanceof MIFAssociation)
            	{
            		MIFAssociation parentMifAssc=(MIFAssociation)parentObj;
           			if (parentMifAssc.isChoiceSelected())
   						parentMif=parentMifAssc.findChoiceSelectedMifClass();
           			else
           				parentMif=parentMifAssc.getMifClass();
            	}

            	if (parentMif==null)
            	{
            		JOptionPane.showMessageDialog(tree.getRootPane().getParent(), "Invalid selection",
                            "MIFClass is not found", JOptionPane.WARNING_MESSAGE);
                        setSuccessfullyPerformed(false);
                        return false;
            	}
            	int exitingMIFCount=MIFUtil.getMaximumMIFAttributeMultiplicityIndexWithName(parentMif, mifAttr.getName());
               	mifClonned.setMultiplicityIndex(exitingMIFCount+1);
            	parentMif.addAttribute(mifClonned);

            	NewHSMBasicNodeLoader mifTreeLoader=new NewHSMBasicNodeLoader(true);
            	DefaultHSMTreeMutableTreeNode hsmNode=(DefaultHSMTreeMutableTreeNode)targetNode;
            	DefaultMutableTreeNode clonnedAttrNode =mifTreeLoader.buildObjectNode(mifClonned, hsmNode.getRootMif());
            	int oldNodeIndex =parentNode.getIndex(targetNode);
            	parentNode.insert(clonnedAttrNode, oldNodeIndex+exitingMIFCount+1);

            	((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
            	setSuccessfullyPerformed(true);
            }
            catch (Exception e1)
            {
                Log.logException(getClass(), e1);
                reportThrowableToUI(e1, parentPanel);
                setSuccessfullyPerformed(false);
            }
        }
		return isSuccessfullyPerformed();
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.6  2007/08/23 17:54:58  wangeug
 * HISTORY      : resolve recursive reference within a MIF file
 * HISTORY      :
 * HISTORY      : Revision 1.5  2007/08/15 17:56:39  wangeug
 * HISTORY      : add duplicated MIFAttribute to chosen MIFClass within a choice group:fix bug
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/08/01 13:27:43  wangeug
 * HISTORY      : resolve issues with preliminary test of release 4.0
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/07/09 20:15:34  wangeug
 * HISTORY      : remove referenceMIFClass attribute; use mifClass instead
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/03 20:25:59  wangeug
 * HISTORY      : initila loading hl7 code without "clone"
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:18:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.6  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.5  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.4  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/12/08 22:46:27  chene
 * HISTORY      : Add /Remove Multiple Clone Support
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/09/26 22:16:38  chene
 * HISTORY      : Add CMET 999900 support
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/09/26 19:29:45  chene
 * HISTORY      : Add Clone Attribute Multiple Class in order to support Multiple Attribute
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/24 22:30:56  jiangsc
 * HISTORY      : New actions
 * HISTORY      :
 */
