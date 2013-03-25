/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.common.nodeloader;

import javax.swing.*;
import javax.swing.tree.*;

/**
 * This is a default implementation of a node loader.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public class DefaultNodeLoader implements NodeLoader
{

	/**
	 * To descendant of this class:
	 * This is an overridable function to allow descendant class to provide
	 * different tree node implementations.
	 * @param userObject
	 * @return a tree node that wraps the user object.
	 */
	protected DefaultMutableTreeNode constructTreeNode(Object userObject)
	{
		return constructTreeNode(userObject, true);
	}

	/**
	 * Overloaded version of the function above.
	 *
	 * @param userObject
	 * @param allowsChildren
	 * @return a tree node that wraps the user object.
	 */
	public DefaultMutableTreeNode constructTreeNode(Object userObject, boolean allowsChildren)
	{
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(userObject, allowsChildren);
		return node;
	}

	/**
	 * Default implementation of the function defined in interface and do nothing.
	 * @param o
	 * @return
	 * @throws NodeLoader.MetaDataloadException
	 */
	public TreeNode loadData(Object o) throws NodeLoader.MetaDataloadException
	{
		TreeNode root = constructTreeNode("Empty Node Loader");
		return root;
	}

	/**
	 * Default implementation of the function defined in interface and do nothing.
	 * @param node
	 * @param resetUUID
	 * @return
	 * @throws NodeLoader.MetaDataloadException
	 */
	public Object unLoadData(DefaultMutableTreeNode node, boolean resetUUID) throws NodeLoader.MetaDataloadException
	{
		return new Object();
	}

	/**
	 * Refresh subtree whose root is the given treeNode reflecting the given object.
	 * After the refreshing, the given tree will be notified for the update information.
	 * @param targetNode
	 * @param object
	 * @param tree if null, no corresponding tree update information will be broadcast.
	 */
	public static void refreshSubTreeByGivenMifObject(DefaultMutableTreeNode targetNode, DefaultMutableTreeNode newNode, JTree tree)
	{
		if(newNode!=null)
		{//clear out all children and add in new ones, if any
			targetNode.removeAllChildren();
			int childCount = newNode.getChildCount();
			targetNode.setAllowsChildren(true);
			for(int i=0; i<childCount; i++)
			{//once a node is moved from newNode to targetNode, it
			//is removed from the newNode children list, so always getChildAt(0)
				targetNode.add((MutableTreeNode) newNode.getChildAt(0));
			}
		}
		targetNode.setUserObject(newNode.getUserObject());
		if(tree!=null)
		{
			TreeModel treeModel = tree.getModel();
			if (treeModel instanceof DefaultTreeModel)
			{//notify change.
				((DefaultTreeModel) treeModel).nodeStructureChanged(targetNode);
			}
		}
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2007/08/10 16:49:31  wangeug
 * HISTORY      : move the static method from subclass to here: refreshTreeNode
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/04/19 14:02:55  wangeug
 * HISTORY      : clean code
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:13  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/11/11 19:23:59  jiangsc
 * HISTORY      : Support Pseudo Root in Mapping Panel.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/19 21:09:58  jiangsc
 * HISTORY      : Save Point.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/12 18:38:14  jiangsc
 * HISTORY      : Enable HL7 V3 Message to be saved in multiple XML file.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/11 22:10:35  jiangsc
 * HISTORY      : Open/Save File Dialog consolidation.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/04 22:22:27  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 */
