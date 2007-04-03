/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/tree/DefaultSCMTreeMutableTreeNode.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $
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

import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.util.Config;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Collections;
import java.util.Comparator;

/**
 * This class defines a customized tree node used in SCM tree manipulation.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:14 $
 */
public class DefaultSCMTreeMutableTreeNode extends DefaultMappableTreeNode
{

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
	public DefaultSCMTreeMutableTreeNode(Object userObject, boolean allowsChildren)
	{
		super(userObject, allowsChildren);
	}

	
	/**
	 * Removes <code>newChild</code> from its present parent (if it has a
	 * parent), sets the child's parent to this node, and then adds the child
	 * to this node's child array at index <code>childIndex</code>.
	 * <code>newChild</code> must not be null and must not be an ancestor of
	 * this node.
	 *
	 * @param	newChild	the MutableTreeNode to insert under this node
	 * @param	childIndex	the index in this node's child array
	 * where this node is to be inserted
	 * @exception	ArrayIndexOutOfBoundsException	if <code>childIndex</code> is out of bounds
	 * @exception	IllegalArgumentException	if <code>newChild</code> is null or is an
	 * ancestor of this node
	 * @exception	IllegalStateException	if this node does not allow
	 * children
	 * @see	#isNodeDescendant
	 */
	public void insert(MutableTreeNode newChild, int childIndex)
	{
//		Log.logInfo(this, this.getClass().getName() + "'s insert() is called.");
		int preCount = getChildCount();
		//remember previous parent before the super.insert() call, in which the parent of newChild will change to this one.
//		TreeNode previousParent = newChild.getParent();
		super.insert(newChild, childIndex);
		int afterCount = getChildCount();
		if((afterCount-preCount)==1 && newChild instanceof DefaultMutableTreeNode)
		{//insert successfully.
			DefaultMutableTreeNode newChildNode = (DefaultMutableTreeNode) newChild;
			Object userObject = newChildNode.getUserObject();
			if(userObject instanceof CSVFieldMeta)
			{
				CSVFieldMeta fieldMeta = (CSVFieldMeta) userObject;
				int newColumn = getNumberOfChildNodeWithUserObjectOfType(CSVFieldMeta.class);
				//insert always occurs at the last one, it is already inserted so the count includes the inserted node.
				fieldMeta.setColumn(newColumn + Config.DEFAULT_FIELD_COLUMN_START_NUMBER - 1);
				resortChildren(new DefaultSCMTreeMutableTreeNodeComparator());
			}
		}
		addNodeUserObjectToParentUserObject(newChild);
	}

	//no need to override remove(MutableTreeNode aChild) since it will call the next one remove(int index);

	/**
	 * Removes the child at the specified index from this node's children
	 * and sets that node's parent to null. The child node to remove
	 * must be a <code>MutableTreeNode</code>.
	 *
	 * @param	childIndex	the index in this node's child array
	 * of the child to remove
	 * @exception	ArrayIndexOutOfBoundsException	if <code>childIndex</code> is out of bounds
	 */
	public void remove(int childIndex)
	{
//		Log.logInfo(this, this.getClass().getName() + "'s remove() is called.");
		MutableTreeNode oldTreeNode = (MutableTreeNode)getChildAt(childIndex);
		super.remove(childIndex);
		int count = getChildCount();
		if((count>0) && doesTreeNodeContainUserObjectOfType(oldTreeNode, CSVFieldMeta.class))
		{//after removal of a certain type of child, and the count is greater than 0, have to re-number.
			//after the super.remove() the element has shifted downward,
			for(int i=childIndex; i<count; i++)
			{
				TreeNode treeNode = getChildAt(i);
				if(treeNode instanceof DefaultMutableTreeNode)
				{
					Object userObj = ((DefaultMutableTreeNode)treeNode).getUserObject();
					if(userObj instanceof CSVFieldMeta)
					{
						CSVFieldMeta fieldMeta = (CSVFieldMeta) userObj;
						int oldColumnNumber = fieldMeta.getColumn();
						fieldMeta.setColumn(oldColumnNumber - 1);
					}
				}
			}
		}

		removeNodeUserObjectFromParentUserObject(oldTreeNode);
	}

	/**
	 * Remove the nodeUserObject from its parent. This will help keep in sync between tree node structure and user object structure.
	 */
	protected void removeNodeUserObjectFromParentUserObject(MutableTreeNode node)
	{
		if (!(node instanceof DefaultMutableTreeNode) || !(this instanceof DefaultMutableTreeNode))
		{//no need to proceed further.
			return;
		}
		//take care of the user object path
		CSVSegmentMeta parentUserObject = (CSVSegmentMeta) this.getUserObject();
		Object childNodeUserObject = ((DefaultMutableTreeNode) node).getUserObject();
		if (childNodeUserObject instanceof CSVFieldMeta)
		{
			CSVFieldMeta localObj = (CSVFieldMeta) childNodeUserObject;
			parentUserObject.removeField(localObj);
			localObj.setSegment(null);
		}
		else if (childNodeUserObject instanceof CSVSegmentMeta)
		{
			CSVSegmentMeta localObj = (CSVSegmentMeta) childNodeUserObject;
			parentUserObject.removeSegment(localObj);
			localObj.setParent(null);
		}
	}

	/**
	 * Add the nodeUserObject to its parent. This will help keep in sync between tree node structure and user object structure.
	 */
	protected void addNodeUserObjectToParentUserObject(MutableTreeNode node)
	{
		if (!(node instanceof DefaultMutableTreeNode) || !(this instanceof DefaultMutableTreeNode))
		{//no need to proceed further.
			return;
		}
		//take care of the user object path
		CSVSegmentMeta parentUserObject = (CSVSegmentMeta) this.getUserObject();
		Object childNodeUserObject = ((DefaultMutableTreeNode) node).getUserObject();
		if (childNodeUserObject instanceof CSVFieldMeta)
		{
			CSVFieldMeta localObj = (CSVFieldMeta) childNodeUserObject;
			parentUserObject.addField(localObj);
			localObj.setSegment(parentUserObject);
		}
		else if (childNodeUserObject instanceof CSVSegmentMeta)
		{
			CSVSegmentMeta localObj = (CSVSegmentMeta) childNodeUserObject;
			parentUserObject.addSegment(localObj);
			localObj.setParent(parentUserObject);
		}
	}

	/**
	 * Removes <code>newChild</code> from its parent and makes it a child of
	 * this node by adding it to the end of this node's child array.
	 *
	 * @see		#insert
	 * @param	newChild	node to add as a child of this node
	 * @exception	IllegalArgumentException if <code>newChild</code>
	 * is null
	 * @exception	IllegalStateException	if this node does not allow
	 * children
	 */
	public void add(MutableTreeNode newChild)
	{
		super.add(newChild);
		//add() don't have to consider the sequencing anymore since super.add() will call insert() and insert() will handle it.
	}

	/**
	 * Using the given comparator, resort children list.
	 * @param comparator
	 */
	public void resortChildren(Comparator comparator)
	{
		if(children!=null)
		{
			Collections.sort(children, comparator);
		}
	}


	/**
	 * Creates a tree node with no parent, no children, but which allows
	 * children, and initializes it with the specified user object.
	 *
	 * @param userObject an Object provided by the user that constitutes
	 *                   the node's data
	 */
	public DefaultSCMTreeMutableTreeNode(Object userObject)
	{
		super(userObject);
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.13  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/24 19:09:40  jiangsc
 * HISTORY      : Implement some validation upon CRUD.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/21 22:37:49  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/18 15:57:51  giordanm
 * HISTORY      : update config-dist + fix a spelling error
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/19 18:54:38  jiangsc
 * HISTORY      : Added reshuffle functionality.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/11 22:10:30  jiangsc
 * HISTORY      : Open/Save File Dialog consolidation.
 * HISTORY      :
 */
