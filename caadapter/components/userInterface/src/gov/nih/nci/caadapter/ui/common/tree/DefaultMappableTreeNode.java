/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/tree/DefaultMappableTreeNode.java,v 1.2 2007-12-03 14:48:49 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.ui.common.tree;

import gov.nih.nci.caadapter.common.BaseObject;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.ui.common.MappableNode;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * This class is to provide the basic implementation of MappableNode on a Tree Node,
 * overriding equals() and hashCode() methods, and other functions to facilitate usage of
 * various occassions.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2007-12-03 14:48:49 $
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
			if (userObject instanceof BaseObject)
				userObjectXmlpath=((BaseObject)userObject).getXmlPath();
			if (selfUserObject instanceof BaseObject)
				selfObjectXmlPath=((BaseObject)selfUserObject).getXmlPath();
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
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.16  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/11/29 16:23:53  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/08/11 22:10:37  jiangsc
 * HISTORY      : Open/Save File Dialog consolidation.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/08/09 22:53:04  jiangsc
 * HISTORY      : Save Point
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/08 21:55:12  jiangsc
 * HISTORY      : Adjusted the start value of field number.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/04 20:40:20  jiangsc
 * HISTORY      : Removed some sys out sentences.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/04 18:06:25  jiangsc
 * HISTORY      : Updated class description in comments
 * HISTORY      :
*/
