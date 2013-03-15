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
import gov.nih.nci.caadapter.ui.common.nodeloader.NewHSMBasicNodeLoader;
import gov.nih.nci.caadapter.ui.specification.hsm.HSMPanel;
import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFUtil;

import java.util.List;
import java.util.HashSet;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;

/**
 * This class defines the remove multiple attribute action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:54:07 $
 */
public class RemoveMultipleComplexDatatypeAction extends AbstractHSMContextCRUDAction
{
    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: RemoveMultipleComplexDatatypeAction.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/actions/RemoveMultipleComplexDatatypeAction.java,v 1.2 2008-06-09 19:54:07 phadkes Exp $";

    private static final String COMMAND_NAME = "Remove Multiple Attribute";
    private static final Character COMMAND_MNEMONIC = new Character('u');

    /**
     * Defines an <code>Action</code> object with a default
     * description string and default icon.
     */
    public RemoveMultipleComplexDatatypeAction(HSMPanel parentPanel)
    {
        this(COMMAND_NAME, null, parentPanel);
    }


    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a the specified icon.
     */
    public RemoveMultipleComplexDatatypeAction(String name, Icon icon, HSMPanel parentPanel)
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
        JTree tree= parentPanel.getTree();
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
        	Attribute dtAttr = (Attribute) obj;
            try
            {
            	Attribute dtAttrClonned=(Attribute)dtAttr.clone();
            	DefaultMutableTreeNode parentNode =(DefaultMutableTreeNode)targetNode.getParent();
            	Object parentObj=parentNode.getUserObject();

            	Datatype parentDatatype=null;
            	if (parentObj instanceof MIFAttribute)
            	{
            		MIFAttribute parentMif=(MIFAttribute)parentObj;
            		if (parentMif.getDatatype().isAbstract())
            			parentDatatype=parentMif.getConcreteDatatype();
            		else
            			parentDatatype=parentMif.getDatatype();
            	}
            	else if (parentObj instanceof Attribute)
            	{
            		Attribute parentDtAttribute=(Attribute)parentObj;
            		//if the parentAttribute reference abstract datatype
            		//here return the concrete datatype
            		parentDatatype=parentDtAttribute.getReferenceDatatype();
            	}

            	if (parentDatatype==null)
            	{
            		JOptionPane.showMessageDialog(tree.getRootPane().getParent(), "Invalid selection",
                            "Parent Attribute is not found", JOptionPane.WARNING_MESSAGE);
                        setSuccessfullyPerformed(false);
                        return false;
            	}

            	MIFUtil.removeDatatypeAttributeWithXmlName(parentDatatype,dtAttr.getNodeXmlName());
            	//this sibling Attribute object has been reset with new multiplicity index,
            	//reload them
            	parentNode.remove(targetNode);
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
 * HISTORY      : Revision 1.1  2007/10/24 18:37:48  wangeug
 * HISTORY      : initial load
 * HISTORY      :
 * HISTORY      : Revision 1.5  2007/08/21 15:50:42  wangeug
 * HISTORY      : remove multiple MIFAttribute/MIFAssociation(clone) if it exists with a chosen MIFClass of a choice group
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
 * HISTORY      : Revision 1.2  2005/09/26 19:29:46  chene
 * HISTORY      : Add Clone Attribute Multiple Class in order to support Multiple Attribute
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/24 22:30:56  jiangsc
 * HISTORY      : New actions
 * HISTORY      :
 */
