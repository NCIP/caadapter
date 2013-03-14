/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.common.nodeloader;

import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.ui.common.tree.DefaultSourceTreeNode;
import gov.nih.nci.caadapter.ui.common.tree.DefaultMappableTreeNode;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * This class helps convert a CSV meta object graph (SCM) into a graph of TreeNodes.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public class SCMMapSourceNodeLoader extends SCMBasicNodeLoader
{
	/**
	 * @param userObject
	 * @return a tree node that wraps the user object.
	 * @override the base implementation of this function.
	 * This is an overridable function to allow descendant class to provide
	 * different tree node implementations.
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
		DefaultSourceTreeNode node = new DefaultSourceTreeNode(userObject, allowsChildren);
		return node;
	}

	/**
	 * Based on the given object type, this function will convert the meta-data tree to a TreeNode-based tree structure, whose root is the returned TreeNode.
	 *
	 * @param o the meta-data object
	 * @return the root node representing the TreeNode structure mapping the given meta-data tree.
	 * @throws gov.nih.nci.caadapter.ui.common.nodeloader.NodeLoader.MetaDataloadException
	 *
	 */
	public TreeNode loadData(Object o) throws MetaDataloadException
	{
		TreeNode resultRoot = null;
		TreeNode realRoot = super.loadData(o);
		if (o instanceof CSVMeta)
		{//construct the pseudo root.
//			PseudoRootTreeNode node = new PseudoRootTreeNode("Source Tree", true);
			DefaultMappableTreeNode node = new DefaultMappableTreeNode("Source Tree", true);
			node.add((MutableTreeNode) realRoot);
			resultRoot = node;
		}
		else
		{
			resultRoot = realRoot;
		}
		return resultRoot;
	}

	/**
	 * Given the node as the root of UI tree structure, this function will traverse the UI tree structure
	 * and construct a user object tree structure and return the root of the meta-data user object tree.
	 *
	 * @param treeNode  the root of the sub-tree to be processed.
	 * @param resetUUID if true, will tell loader to reset UUID field; otherwise, it will keep existing UUID;
	 *                  The reason to have the option is that the original data may come from another CSV metadata file and
	 *                  UUIDs of those data should be re-assigned before being persisted.
	 * @return the root of the meta-data user object tree.
	 * @throws gov.nih.nci.caadapter.ui.main.nodeloader.NodeLoader.MetaDataloadException
	 *
	 */
	public CSVMeta unLoadData(DefaultMutableTreeNode treeNode, boolean resetUUID) throws MetaDataloadException
	{
		DefaultMutableTreeNode realRoot = treeNode;
		if (treeNode instanceof DefaultMappableTreeNode)
		{
			realRoot = (DefaultMutableTreeNode) treeNode.getChildAt(0);
		}
		return super.unLoadData(realRoot, resetUUID);
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2007/04/19 14:04:24  wangeug
 * HISTORY      : clean code
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:13  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/11/11 19:23:59  jiangsc
 * HISTORY      : Support Pseudo Root in Mapping Panel.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/11 22:10:36  jiangsc
 * HISTORY      : Open/Save File Dialog consolidation.
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/08/04 22:22:27  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 */
