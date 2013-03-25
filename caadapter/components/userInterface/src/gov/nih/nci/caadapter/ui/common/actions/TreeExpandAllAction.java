/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.actions;

import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * This class defines the tree "Expand All" action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public class TreeExpandAllAction extends AbstractContextAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: TreeExpandAllAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/actions/TreeExpandAllAction.java,v 1.3 2008-06-09 19:53:51 phadkes Exp $";

	private static final String COMMAND_NAME = "Expand All";
	private static final ImageIcon IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("treeExpandAll.gif"));
	private static final Character COMMAND_MNEMONIC = new Character('E');
	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Event.CTRL_MASK, false);
	private static final String TOOL_TIP_DESCRIPTION = "Expand one level down on selected tree nodes (Ctrl+'+')";

	private transient JTree tree;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public TreeExpandAllAction(JTree tree)
	{
		this(COMMAND_NAME, tree);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public TreeExpandAllAction(String name, JTree tree)
	{
		this(name, IMAGE_ICON, tree);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public TreeExpandAllAction(String name, Icon icon, JTree tree)
	{
		super(name, icon);
		this.tree = tree;
		setAdditionalAttributes();
	}

	/**
	 * Will be called by the constructor to set additional attributes.
	 */
	protected void setAdditionalAttributes()
	{//override super class's one to plug in its own attributes.
		setMnemonic(COMMAND_MNEMONIC);
		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
		setShortDescription(TOOL_TIP_DESCRIPTION);
	}

	/**
	 * Set the new tree value.
	 * @param newTree
	 */
	public void setTree(JTree newTree)
	{
		this.tree = newTree;
	}

	private void expandChildrenPathUnderParentPath(TreePath parentTreePath, TreePath[] existingPathsToExpand)
	{
		if(parentTreePath==null)
		{
			return;
		}
		Object obj = parentTreePath.getLastPathComponent();
		if(obj instanceof DefaultMutableTreeNode)
		{
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) obj;
			int size = parentNode.getChildCount();
			TreePath[] childrenPathArray = new TreePath[size];
			for(int i=0; i<size; i++)
			{
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) parentNode.getChildAt(i);
				childrenPathArray[i] = new TreePath(childNode.getPath());
			}

			size = childrenPathArray.length;
			TreePath[] newPathArrayToExpand = new TreePath[size + existingPathsToExpand.length];
			//copy two arrays together to help filter out those that is about to expand so as to avoid undefinite loop;
			System.arraycopy(childrenPathArray, 0, newPathArrayToExpand, 0, size);
			System.arraycopy(existingPathsToExpand, 0, newPathArrayToExpand, size, existingPathsToExpand.length);

			for (int i = size - 1; i >= 0; i--)
			{//expand last row first
				if (tree.isExpanded(childrenPathArray[i]) && isTreePathInArray(childrenPathArray[i], newPathArrayToExpand))
				{
					expandChildrenPathUnderParentPath(childrenPathArray[i], newPathArrayToExpand);
				}
				else
				{
					tree.expandPath(childrenPathArray[i]);
				}
			}

		}
	}

	private boolean isTreePathInArray(TreePath treePath, TreePath[] treePathArray)
	{
		boolean result = false;
		if(treePathArray!=null)
		{
			for(int i=0; i<treePathArray.length; i++)
			{
				if(GeneralUtilities.areEqual(treePath, treePathArray[i]))
				{
					result = true;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e) throws Exception
	{
		if (tree != null&&tree.getModel().getRoot()!=null)
		{
			/**
			 * Impelementation:
			 * Due to GUI performance concern, we abandoned the attempt to expand all possible tree node,
			 * instead, we changed the implementation as following:
			 * Expand all selected tree node;
			 * If nothing is selected, use root as default;
			 */
			TreePath[] selectedPaths = tree.getSelectionPaths();
			if (selectedPaths == null || selectedPaths.length == 0)
			{
				Object root = tree.getModel().getRoot();
				selectedPaths = new TreePath[1];
				selectedPaths[0] = new TreePath(root);//implies the root
			}
			int size = selectedPaths.length;
			for (int i = size - 1; i >= 0; i--)
			{//expand last row first
				if (tree.isExpanded(selectedPaths[i]) && isTreePathInArray(selectedPaths[i], selectedPaths))
				{
					expandChildrenPathUnderParentPath(selectedPaths[i], selectedPaths);
				}
				else
				{
					tree.expandPath(selectedPaths[i]);
				}
			}
		}
		setSuccessfullyPerformed(true);
		return isSuccessfullyPerformed();
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return tree;
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2007/10/09 18:32:14  wangeug
 * HISTORY      : do not generate null exception message
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 18:56:26  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 23:06:18  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/14 21:37:20  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/20 18:19:27  jiangsc
 * HISTORY      : Updated tooltip text to refect the fix to defect #121.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/10/12 21:41:54  jiangsc
 * HISTORY      : Implemented Expand All function
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/26 15:57:43  jiangsc
 * HISTORY      : TreeExpandAll and TreeCollapseAll Class package move
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/04 22:22:21  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/25 22:02:26  jiangsc
 * HISTORY      : 1) Added expand all and collapse all;
 * HISTORY      : 2) Added toolbar on the mapping panel;
 * HISTORY      : 3) Consolidated menus;
 * HISTORY      :
 */
