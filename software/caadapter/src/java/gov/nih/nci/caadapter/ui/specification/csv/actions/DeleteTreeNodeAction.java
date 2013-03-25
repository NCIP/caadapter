/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ui.specification.csv.actions;

import gov.nih.nci.caadapter.ui.specification.csv.CSVPanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * This class defines the delete tree node action.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-06-09 19:54:07 $
 */
public class DeleteTreeNodeAction extends AbstractCsvContextCRUDAction
{
	private static final String COMMAND_NAME = "Delete";
	private static final Character COMMAND_MNEMONIC = new Character('D');
	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);

	private transient JTree tree;
	private boolean toShowDeleteWarning = true;

	private boolean successfulDelete;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public DeleteTreeNodeAction(CSVPanel parentPanel, boolean toShowDeleteWarning)
	{
		this(COMMAND_NAME, null, parentPanel, toShowDeleteWarning);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public DeleteTreeNodeAction(String name, Icon icon, CSVPanel parentPanel, boolean toShowDeleteWarning)
	{
		super(name, icon, parentPanel);
		this.toShowDeleteWarning = toShowDeleteWarning;
		setMnemonic(COMMAND_MNEMONIC);
		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
	}

	private JTree getTree()
	{
		if(this.tree==null)
		{
			this.tree = parentPanel.getTree();
		}
		return this.tree;
	}

	public boolean isToShowDeleteWarning()
	{
		return toShowDeleteWarning;
	}

	public void setToShowDeleteWarning(boolean toShowDeleteWarning)
	{
		this.toShowDeleteWarning = toShowDeleteWarning;
	}

	public boolean isSuccessfulDelete()
	{
		return successfulDelete;
	}

	private void setSuccessfulDelete(boolean successfulDelete)
	{
		this.successfulDelete = successfulDelete;
		setSuccessfullyPerformed(successfulDelete);
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
		DefaultTreeModel treeModel = (DefaultTreeModel) getTree().getModel();
		TreePath[] treePathArray = tree.getSelectionPaths();
		if(treePathArray.length==0)
		{
			setSuccessfulDelete(false);
			return false;
		}

		if(toShowDeleteWarning)
		{
            int userChoice = JOptionPane.showConfirmDialog(tree.getRootPane().getParent(),
					constructMessage(treePathArray), "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(userChoice!=JOptionPane.YES_OPTION)
			{
				setSuccessfulDelete(false);
				return false;
			}
		}

		for(int i=treePathArray.length-1; i>-1; i--)
		{
			MutableTreeNode treeNode = (MutableTreeNode) treePathArray[i].getLastPathComponent();
			if(treeNode!=null)
			{
				if(treeNode.getParent()!=null)
				{
					DefaultMutableTreeNode parentTreeNode=(DefaultMutableTreeNode)treeNode.getParent();
					treeModel.removeNodeFromParent(treeNode);
					getParentPanel().getPropertiesPane().setDisplayData(parentTreeNode);
				}
				else
				{
					treeModel.setRoot(null);
					getParentPanel().setPropertiesPaneVisible(false);
				}
			}
		}
		setSuccessfulDelete(true);
		return isSuccessfulDelete();
	}

	private Object[] constructMessage(TreePath[] treePathArray)
	{
		Object[] messages = new Object[treePathArray.length + 1];
		messages[0] = "Are you sure you want to delete those selected tree elements?";
		for(int i=0; i<treePathArray.length; i++)
		{
			messages[i+1] = treePathArray[i].getLastPathComponent();
		}
		return messages;
	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2007/10/05 19:12:18  wangeug
 * HISTORY      : update right propertyPanel once treeNode being deleted from left treePanel
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/12 15:48:49  umkis
 * HISTORY      : csv cardinality
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:18:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/10/18 14:52:14  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/23 19:11:29  jiangsc
 * HISTORY      : action status update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/22 16:02:39  jiangsc
 * HISTORY      : Work on Add Field/Segment
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/05 20:35:46  jiangsc
 * HISTORY      : 0)Implemented field sequencing on CSVPanel but needs further rework;
 * HISTORY      : 1)Removed (Yes/No) for questions;
 * HISTORY      : 2)Removed double-checking after Save-As;
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/03 22:07:53  jiangsc
 * HISTORY      : Save Point.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/22 20:53:09  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
