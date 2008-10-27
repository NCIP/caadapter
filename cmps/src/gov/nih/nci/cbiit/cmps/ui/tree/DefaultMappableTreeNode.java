/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmps.ui.tree;

import gov.nih.nci.cbiit.cmps.core.BaseMeta;
import gov.nih.nci.cbiit.cmps.ui.common.MappableNode;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * This class is to provide the basic implementation of MappableNode on a Tree Node,
 * overriding equals() and hashCode() methods, and other functions to facilitate usage of
 * various occassions.
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-10-27 20:06:30 $
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

	/**
	 * Return true if both mapFlag and userObject is equal.
	 *
	 * @param o
	 * @return true if both are equals; false otherwise.
	 */
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof DefaultMappableTreeNode)) return false;

		final DefaultMappableTreeNode DefaultMappableTreeNode = (DefaultMappableTreeNode) o;

		if (mapFlag != DefaultMappableTreeNode.mapFlag) return false;
		Object mUserObject = getUserObject();
		Object thatUserObject = DefaultMappableTreeNode.getUserObject();
		boolean boolEquals = mUserObject == null ? thatUserObject == null : mUserObject.equals(thatUserObject);

		return boolEquals;
	}

	/**
	 * Include mapFlag and userObject in comparison.
	 * @return the hashCode of this object.
	 */
	public int hashCode()
	{
		int result = (mapFlag ? 1 : 0);
		Object userObject = getUserObject();
		result = result * 31 + (userObject==null ? 0 : userObject.hashCode());
		return result;
	}

	/**
	 * This method will traverse the sub-tree whose root is itself and including itself to
	 * find a tree Node whose userObject matches the given one. If none is found, return null.
	 * @param userObject
	 * @return the default mutable tree node matching the given object.
	 */
	public DefaultMutableTreeNode findFirstTreeNodeMatchUserObject(Object userObject)
	{
		Object selfUserObject = getUserObject();
		boolean boolEquals = selfUserObject==null? userObject==null : selfUserObject.equals(userObject);
		if(boolEquals)
		{
			return this;
		}
		else
		{
			String userObjectXmlpath=null;
			String selfObjectXmlPath=null;
			if (userObject instanceof BaseMeta)
				userObjectXmlpath=((BaseMeta)userObject).getId();
//			if (selfUserObject instanceof BaseObject)
//				selfObjectXmlPath=((BaseObject)selfUserObject).getXmlPath();
			if (userObjectXmlpath.equalsIgnoreCase(selfObjectXmlPath))	
				return this;
			
			DefaultMutableTreeNode foundTarget = null;
			int childCount = getChildCount();
			for(int i=0; i<childCount; i++)
			{
				foundTarget = null;
				Object obj = getChildAt(i);
				if(obj instanceof DefaultMappableTreeNode)
				{
					DefaultMappableTreeNode childNode = (DefaultMappableTreeNode) obj;
					foundTarget = childNode.findFirstTreeNodeMatchUserObject(userObject);
					if(foundTarget!=null)
					{
						break;
					}
				}
			}
			return foundTarget;
		}
	}


	/**
	 * Answers if a given tree node contains a user object that is of type of the given class.
	 * @param treeNode
	 * @param userClass
	 * @return tre if it contains.
	 */
	protected boolean doesTreeNodeContainUserObjectOfType(TreeNode treeNode, Class userClass)
	{
		boolean result = false;
		if(treeNode instanceof DefaultMutableTreeNode)
		{
			Object userObject = ((DefaultMutableTreeNode)treeNode).getUserObject();
			if(userClass!=null && userObject!=null && userClass.isAssignableFrom(userObject.getClass()))
			{
				result = true;
			}
		}
		return result;
	}

	/**
	 * Will return the number of child node that has user object of given class type.
	 * @param userClass
	 * @return the number of child node of given type.
	 */
	public synchronized int getNumberOfChildNodeWithUserObjectOfType(Class userClass)
	{
		int size = this.getChildCount();
		int count = 0;
		for (int i = 0; i < size; i++)
		{
			TreeNode childNode = this.getChildAt(i);
			if (doesTreeNodeContainUserObjectOfType(childNode, userClass))
			{
				count++;
			}
		}
		return count;
	}

}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 */

