/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





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
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:52 $
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
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
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
