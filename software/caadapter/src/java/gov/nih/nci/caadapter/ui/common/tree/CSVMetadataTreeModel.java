/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.common.tree;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVMetaImpl;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVSegmentMetaImpl;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.validation.CSVMetaValidator;
import gov.nih.nci.caadapter.ui.common.nodeloader.SCMTreeNodeLoader;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The class defines a default tree model to support the tree modification and navigation
 * in CSV Panel.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:52 $
 */
public class CSVMetadataTreeModel extends DefaultTreeModel
{
	public CSVMetadataTreeModel()
	{
		super(new DefaultSCMTreeMutableTreeNode(null), false);
		//this(new DefaultSCMTreeMutableTreeNode(null), false);
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) getRoot();
		CSVSegmentMeta rootUserObject = new CSVSegmentMetaImpl("ROOT", null);
		root.setUserObject(rootUserObject);
		root.setAllowsChildren(true);
	}
	/**
	 * Creates a tree in which any node can have children.
	 *
	 * @param root a TreeNode object that is the root of the tree
	 * @see #DefaultTreeModel(javax.swing.tree.TreeNode, boolean)
	 */
	public CSVMetadataTreeModel(TreeNode root)
	{
		super(root, false);
	}

	/**
	 * Creates a tree specifying whether any node can have children,
	 * or whether only certain nodes can have children.
	 *
	 * @param root               a TreeNode object that is the root of the tree
	 * @param asksAllowsChildren a boolean, false if any node can
	 *                           have children, true if each node is asked to see if
	 *                           it can have children
	 * @see #asksAllowsChildren
	 */
//	public CSVMetadataTreeModel(TreeNode root, boolean asksAllowsChildren)
//	{
//		super(root, asksAllowsChildren);
//	}

	/**
	 * Returns whether the specified node is a leaf node.
	 * The way the test is performed depends on the
	 * <code>askAllowsChildren</code> setting.
	 *
	 * @param node the node to check
	 * @return true if the node is a leaf node
	 * @see #asksAllowsChildren
	 * @see javax.swing.tree.TreeModel#isLeaf
	 */
	public boolean isLeaf(Object node)
	{
		boolean result = true;
		if(node instanceof DefaultMutableTreeNode)
		{
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
			Object obj = treeNode.getUserObject();
		    if(obj instanceof CSVSegmentMeta)
			{//only segment meta could have children
				result = false;
			}
			return result;
		}
		else
		{
			return super.isLeaf(node);
		}
	}

	/**
	 * Return if a given child is a child of parent node.
	 * @param parent
	 * @param child
	 * @return true if a given child is a child of parent.
	 */
	public boolean isNodeChild(TreeNode parent, TreeNode child)
	{
		if(parent==null || child==null)
		{
			return false;
		}
		if(parent.getChildCount()==0)
		{
			return false;
		}
		return GeneralUtilities.areEqual(parent, child.getParent());
	}

	/**
	 *
	 * @param parent
	 * @param child
	 * @param isToCopyChild if true, the child will be copied to the new parent instead of moving under the parent.
	 * @return
	 * @throws IllegalArgumentException
	 */
	public boolean addChild(DefaultMutableTreeNode parent, MutableTreeNode child, boolean isToCopyChild) throws IllegalArgumentException
	{
		return addChildren(parent, Arrays.asList(new Object[]{child}), isToCopyChild);
	}

	/**
	 *
	 * @param parent
	 * @param childrenList
	 * @param isToCopyChildren if true, each of the child will be copied to the new parent instead of moving under the parent.
	 * @return
	 * @throws IllegalArgumentException
	 */
	public boolean addChildren(DefaultMutableTreeNode parent, List childrenList, boolean isToCopyChildren) throws IllegalArgumentException
	{
//		Object userObj = parent.getUserObject();
		if(isLeaf(parent))//!(userObj instanceof CSVSegmentMeta))
		{
//			JOptionPane.showMessageDialog(null,
//					"The parent node is not of type '" + CSVSegmentMeta.class.getName() + "', so it does not accept children.", JOptionPane.ERROR_MESSAGE);
			throw new IllegalArgumentException("The parent node is not of type '" + CSVSegmentMeta.class.getName() + "', so it does not accept children.");
		}
		if(childrenList==null)
		{
			return false;
		}
        int size = childrenList.size();
		for(int i=0; i<size; i++)
		{
			MutableTreeNode node = (MutableTreeNode) childrenList.get(i);
			if(isNodeChild(parent, node))
			{
				throw new IllegalArgumentException("The parent node already contains child of '" + node + "'.");
			}

			if(isToCopyChildren)
			{
				DefaultMutableTreeNode anotherNode = (DefaultMutableTreeNode) getDeepCopy((DefaultMutableTreeNode)node);
				parent.add(anotherNode);
			}
			else
			{
				//remember previous parent so as to notify structure change
				DefaultMutableTreeNode previousParent = (DefaultMutableTreeNode) node.getParent();
				fireTreeNodesRemoved(previousParent, previousParent.getPath(), new int[]{previousParent.getIndex(node)}, new Object[]{node});
				parent.add(node);
				//after the add call, node is no longer previousParent's child, so need to notify the change
//				fireTreeStructureChanged(previousParent, previousParent.getPath(), new int[]{previousParent.getIndex(node)}, new Object[]{node});
			}
		}
		if(size>0)
		{
			fireTreeStructureChanged(parent, parent.getPath(), new int[0], childrenList.toArray());
		}
		return true;
	}

	/**
	 * The clone() method in DefaultMutableTreeNode class only return a shallow copy of itself.
	 *
	 * This function will create and return a copy of the sub-tree of the given tree node, including the given node,
	 * its parent will set to be null, to avoid deletion from add.
	 *
	 * @param sourceNode
	 * @return the tree node after copying.
	 */
	protected DefaultMutableTreeNode getDeepCopy(DefaultMutableTreeNode sourceNode)
	{
		if (!(sourceNode instanceof DefaultMutableTreeNode))
		{
			Log.logInfo(this, "Tree node is not valid.");
			return null;
		}
		DefaultMutableTreeNode returnNode = null;//new DefaultMutableTreeNode();
		SCMTreeNodeLoader nodeLoader = new SCMTreeNodeLoader();
		try
		{
			returnNode = nodeLoader.constructTreeNode(((MetaObjectImpl) sourceNode.getUserObject()).clone(false), sourceNode.getAllowsChildren());
		}
		catch (CloneNotSupportedException e)
		{
			Log.logException(this, e);
			returnNode = nodeLoader.constructTreeNode("", sourceNode.getAllowsChildren());
		}

		returnNode.setParent(null);

		//Start deep "clone" procedure
		int childCount = sourceNode.getChildCount();
		if (childCount == 0)
		{//it is a leaf
			return returnNode;
		}

		List newChildrenList = new ArrayList();
		for (int i = 0; i < childCount; i++)
		{
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) sourceNode.getChildAt(i);
			//should not call DefaultMutableTreeNode's add() or insert() method since either
			// will automatically remove the given childNode from its previous parent,
			// which will change the previous parent's child structure
			// along the insert to new destination. Thus, using interim list to save;
			newChildrenList.add(childNode);
		}

		int size = newChildrenList.size();
		for(int i=0; i<size; i++)
		{
			DefaultMutableTreeNode newChildNode = this.getDeepCopy((DefaultMutableTreeNode) newChildrenList.get(i));
			newChildNode.setParent(null);
			returnNode.add(newChildNode);
		}

		return returnNode;
	}

	/**
	 * This method is better to be called "BEFORE" actually add the given child to the parent.
	 *
	 * Apply the business logic in validators to verify if the given child is acceptable to the given parent node.
	 * @param childNode
	 * @param parentNode
	 * @param isToCopyChildren if true, each of the child will be potentially copied to the new parent instead of moving under the parent.
	 * @return true if acceptable; false otherwise.
	 */
	public boolean isChildAcceptableToParent(DefaultMutableTreeNode childNode, DefaultMutableTreeNode parentNode, boolean isToCopyChildren)
	{
		if(childNode==null || parentNode==null)
		{
			return false;
		}

		if(isLeaf(parentNode))
		{//parent node should not be leaf in the context
			return false;
		}

		//now, neither child nor parent is null and parent is a node that can contain children, i.e. Segment
		boolean result = false;
		Object childUserObject = childNode.getUserObject();
		CSVSegmentMeta parentUserObject = (CSVSegmentMeta) parentNode.getUserObject();

		Object[] pathToRoot = parentNode.getUserObjectPath();
		CSVSegmentMeta metaRoot = (CSVSegmentMeta) pathToRoot[0];
		CSVMetaImpl csvMeta = new CSVMetaImpl(metaRoot);
		CSVMetaValidator validator = new CSVMetaValidator(csvMeta);
		ValidatorResults validatorResults = new ValidatorResults();
		if (childUserObject instanceof CSVSegmentMeta)
		{
			CSVSegmentMeta localObj = (CSVSegmentMeta) childUserObject;
			validatorResults.addValidatorResults(validateSegmentChild(validator, localObj, parentUserObject, isToCopyChildren));
			result = (validatorResults.getAllMessages().size()==0);
		}
		else if (childUserObject instanceof CSVFieldMeta)
		{
			CSVFieldMeta localObj = (CSVFieldMeta) childUserObject;
			validatorResults.addValidatorResults(validateFieldChild(validator, localObj, parentUserObject, isToCopyChildren));
			result = validatorResults.isValid();//(validatorResults.getAllMessages().size() == 0);
		}
		else
		{
			//unknown data zone.
			result = false;
		}
		return result;
	}

	protected ValidatorResults validateSegmentChild(CSVMetaValidator validator, CSVSegmentMeta childObject, CSVSegmentMeta parentObject, boolean isToCopyChildren)
	{
		//remember the old one so as to roll it back
		CSVSegmentMeta oldParentUserObject = null;
		oldParentUserObject = childObject.getParent();
		if (oldParentUserObject != null && !isToCopyChildren)
		{
			oldParentUserObject.removeSegment(childObject);
		}
		parentObject.addSegment(childObject);

		ValidatorResults validatorResults = new ValidatorResults();

		//Check if 2 or more segments with same name in SCM.
		validatorResults.addValidatorResults(validator.ScmRule1());
		//Check if it is ALLCAPS.
		validatorResults.addValidatorResults(validator.ScmRule4());

		//roll back
		if (oldParentUserObject != null && !isToCopyChildren)
		{
			oldParentUserObject.addSegment(childObject);
		}
		parentObject.removeSegment(childObject);

		return validatorResults;
	}

	protected ValidatorResults validateFieldChild(CSVMetaValidator validator, CSVFieldMeta childObject, CSVSegmentMeta parentObject, boolean isToCopyChildren)
	{
		//remember the old one so as to roll it back
		CSVSegmentMeta oldParentUserObject = null;
		oldParentUserObject = childObject.getSegment();
		if (oldParentUserObject != null && !isToCopyChildren)
		{
			oldParentUserObject.removeField(childObject);
		}
		parentObject.addField(childObject);

		ValidatorResults validatorResults = new ValidatorResults();

		//Check if 2 or more fields with same name in same segment in SCM (case-insensitive).
		validatorResults.addValidatorResults(validator.ScmRule2());

		//Will not check if the field has the default field name, since it will not violate anything
		//NOTE: current design of this function is used during drap and drop, if reuse, please consider re-engineer this part.
//		//Check if field with default field name in SCM.
//		validatorResults.addValidatorResults(validator.ScmRule7());

		//Field name valiation
		validatorResults.addValidatorResults(validator.ScmRule8());

		//roll back
		if (oldParentUserObject != null && !isToCopyChildren)
		{
			oldParentUserObject.addField(childObject);
		}
		parentObject.removeField(childObject);

		return validatorResults;
	}

	//	public void notifyTreeNodeChanged(DefaultMutableTreeNode treeNode)
//	{
//		if(treeNode!=null)
//		{
//			fireTreeNodesChanged(treeNode, treeNode.getPath(), new int[0], new Object[0]);
//		}
//	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.20  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.19  2006/01/30 16:14:36  jiangsc
 * HISTORY      : Updated validation scheme to ignore Warning messages on field or segment manipulations.
 * HISTORY      :
 * HISTORY      : Revision 1.18  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/10/26 18:12:29  jiangsc
 * HISTORY      : replaced printStackTrace() to Log.logException
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/10/24 19:09:40  jiangsc
 * HISTORY      : Implement some validation upon CRUD.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/10/24 17:21:33  jiangsc
 * HISTORY      : minor update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/10/21 22:37:49  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/10/12 21:42:46  jiangsc
 * HISTORY      : Added validation on invalid file type.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/11 22:10:29  jiangsc
 * HISTORY      : Open/Save File Dialog consolidation.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/04 18:05:15  jiangsc
 * HISTORY      : Refactorized clone() methods to have explicit clone(boolean copyUUID)
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/03 22:07:54  jiangsc
 * HISTORY      : Save Point.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/07/22 20:53:11  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
