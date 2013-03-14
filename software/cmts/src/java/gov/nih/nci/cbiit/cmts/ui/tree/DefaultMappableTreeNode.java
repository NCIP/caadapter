/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.ui.tree;

import gov.nih.nci.cbiit.cmts.core.BaseMeta;
import gov.nih.nci.cbiit.cmts.ui.common.MappableNode;
import gov.nih.nci.cbiit.cmts.ui.mapping.ElementMetaLoader;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * This class is to provide the basic implementation of MappableNode on a Tree Node,
 * overriding equals() and hashCode() methods, and other functions to facilitate usage of
 * various occassions.
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2008-12-03 20:46:14 $
 *
 */
public class DefaultMappableTreeNode extends DefaultMutableTreeNode implements MappableNode
{
	private boolean mapFlag = false;

	/**
	 * Creates a tree node with no parent, no children, but which allows
	 * children, and initializes it with the specified user object.
	 *
	 * @param userObject an Object provided by the user that constitutes
	 *                   the node's data
	 */
	public DefaultMappableTreeNode(Object userObject)
	{
		super(userObject);
	}

	/**
	 * Creates a tree node with no parent, no children, initialized with
	 * the specified user object, and that allows children only if
	 * specified.
	 *
	 * @param userObject     an Object provided by the user that constitutes
	 *                       the node's data
	 * @param allowsChildren if true, the node is allowed to have child
	 *                       nodes -- otherwise, it is always a leaf node
	 */
	public DefaultMappableTreeNode(Object userObject, boolean allowsChildren)
	{
		super(userObject, allowsChildren);
	}

	public void setMapStatus(boolean newValue)
	{
		mapFlag = newValue;
	}

	public boolean isMapped()
	{
		return mapFlag;
	}


//	/**
//	 * Answers if a given tree node contains a user object that is of type of the given class.
//	 * @param treeNode
//	 * @param userClass
//	 * @return tre if it contains.
//	 */
//	protected  static boolean doesTreeNodeContainUserObjectOfType(TreeNode treeNode, Class userClass)
//	{
//		boolean result = false;
//		if(treeNode instanceof DefaultMutableTreeNode)
//		{
//			Object userObject = ((DefaultMutableTreeNode)treeNode).getUserObject();
//			if(userClass!=null && userObject!=null && userClass.isAssignableFrom(userObject.getClass()))
//			{
//				result = true;
//			}
//		}
//		return result;
//	}
//
//	/**
//	 * Will return the number of child node that has user object of given class type.
//	 * @param userClass
//	 * @return the number of child node of given type.
//	 */
//	public synchronized int getNumberOfChildNodeWithUserObjectOfType(Class userClass)
//	{
//		int size = this.getChildCount();
//		int count = 0;
//		for (int i = 0; i < size; i++)
//		{
//			TreeNode childNode = this.getChildAt(i);
//			if (doesTreeNodeContainUserObjectOfType(childNode, userClass))
//			{
//				count++;
//			}
//		}
//		return count;
//	}

}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.1  2008/10/27 20:06:30  linc
 * HISTORY: GUI first add.
 * HISTORY:
 */

