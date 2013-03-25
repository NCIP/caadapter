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
import gov.nih.nci.caadapter.ui.common.nodeloader.NewHSMBasicNodeLoader;
import gov.nih.nci.caadapter.ui.common.tree.DefaultHSMTreeMutableTreeNode;
import gov.nih.nci.caadapter.ui.specification.hsm.HSMPanel;

import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;


/**
 * This class defines the add multiple action.
 *
 * @author OWNER: Eric Chen
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.10 $
 *          date        $Date: 2009-02-09 21:43:32 $
 */
public class AddMultipleCloneAction extends AbstractHSMContextCRUDAction
{
    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: AddMultipleCloneAction.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/AddMultipleCloneAction.java,v 1.10 2009-02-09 21:43:32 wangeug Exp $";

    private static final String COMMAND_NAME = "Add Multiple Clone";
    private static final Character COMMAND_MNEMONIC = new Character('C');

//    private transient JTree tree;

    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    public AddMultipleCloneAction(HSMPanel parentPanel)
    {
        this(COMMAND_NAME,null, parentPanel);
    }

    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a the specified icon.
     */
    public AddMultipleCloneAction(String name, Icon icon, HSMPanel parentPanel)
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

        if (obj instanceof MIFAssociation)
        {
        	MIFAssociation mifAssc = (MIFAssociation) obj;
            try
            {
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

            	MIFAssociation clonnedAssc=(MIFAssociation)mifAssc.clone();

            	int exitingAsscCount=MIFUtil.getMaximumAssociationMultiplicityIndexWithName(parentMif, mifAssc.getName());
            	clonnedAssc.setMultiplicityIndex(exitingAsscCount+1);
            	clonnedAssc.setName(clonnedAssc.getName());
//                clonnedAssc.setOptionChosen(true);  // inerted by umkis 01/29/2009
                parentMif.addAssociation(clonnedAssc);

            	NewHSMBasicNodeLoader mifTreeLoader=new NewHSMBasicNodeLoader(true);

            	if (parentObj instanceof DatatypeBaseObject )
            		clonnedAssc.setParentXmlPath(((DatatypeBaseObject)parentObj).getXmlPath());
            	DefaultHSMTreeMutableTreeNode hsmNode=(DefaultHSMTreeMutableTreeNode)targetNode;
            	DefaultMutableTreeNode  clonnedMIFAsscNode =mifTreeLoader.buildObjectNode(clonnedAssc,hsmNode.getRootMif());
            	int oldNodeIndex =parentNode.getIndex(targetNode);
            	parentNode.insert(clonnedMIFAsscNode, oldNodeIndex+exitingAsscCount+1);

            	((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
                 setSuccessfullyPerformed(true);
            }
            catch (Exception e1)
            {
                Log.logException(getClass(), e1);
                reportThrowableToUI(e1, parentPanel);
                setSuccessfullyPerformed(false);
            }
//            CloneMultipleMeta cloneMeta = (CloneMultipleMeta) obj;
//            try
//            {
//                HL7V3MetaUtil.addMultipleClone(cloneMeta);
//                parentPanel.getDefaultHSMNodeLoader().refreshSubTreeByGivenMetaObject(
//                    (DefaultMutableTreeNode)targetNode.getParent(), cloneMeta.getParentMeta(), parentPanel.getTree());
//                setSuccessfullyPerformed(true);
//            }
//            catch (Exception e1)
//            {
//                Log.logException(getClass(), e1);
//                reportThrowableToUI(e1, parentPanel);
//                setSuccessfullyPerformed(false);
//            }
        }
		return isSuccessfullyPerformed();
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.9  2009/02/03 14:20:36  umkis
 * HISTORY      : For fixing multiple clonning problem
 * HISTORY      :
 * HISTORY      : Revision 1.8  2008/09/29 20:18:57  wangeug
 * HISTORY      : enforce code standard: license file, file description, changing history
 * HISTORY      :
 * HISTORY      : Revision 1.7  2008/06/09 19:54:07  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2007/08/23 17:54:58  wangeug
 * HISTORY      : resolve recursive reference within a MIF file
 * HISTORY      :
 * HISTORY      : Revision 1.5  2007/08/15 17:57:00  wangeug
 * HISTORY      : add duplicated MIFAssociation(Clone) to chosen MIFClass within a choice group:fix bug
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/08/03 15:05:14  wangeug
 * HISTORY      : enable the Address attribute field once added
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
 * HISTORY      : Revision 1.3  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
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
