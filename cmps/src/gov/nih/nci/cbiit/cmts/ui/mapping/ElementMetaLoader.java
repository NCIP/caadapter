/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmps.ui.mapping;

import gov.nih.nci.cbiit.cmps.core.AttributeMeta;
import gov.nih.nci.cbiit.cmps.core.Component;
import gov.nih.nci.cbiit.cmps.core.ElementMeta;
import gov.nih.nci.cbiit.cmps.ui.tree.DefaultMappableTreeNode;
import gov.nih.nci.cbiit.cmps.ui.tree.DefaultSourceTreeNode;
import gov.nih.nci.cbiit.cmps.ui.tree.DefaultTargetTreeNode;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import java.util.Collections;
import java.util.List;

/**
 * This class defines a basic node loader implementation focusing on how to traverse 
 * meta data tree to convert them to a Java UI tree structure, whose nodes are either
 * DefaultMutableTreeNode or its various descendants that may individual requirement.
 *
 * Therefore, it is highly recommended to individual panel developers to sub-class this loader
 * class, with main purpose of providing customized DefaultMutableTreeNode descendant implementation,
 * while leaving the algorithm of traversing CSV SCM meta data tree defined here intact.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2008-12-09 19:04:17 $
 *
 */
public class ElementMetaLoader
{
	public static final int SOURCE_MODE = 0;
	public static final int TARGET_MODE = 1;
	int mode = SOURCE_MODE;
	Object obj = null;

	public ElementMetaLoader(int mode){
		this.mode = mode;
	}
	
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
		DefaultMutableTreeNode node = null;
		switch(mode){
		case SOURCE_MODE:
			node = new DefaultSourceTreeNode(getUserObject(userObject), allowsChildren);
			break;
		case TARGET_MODE:
			node = new DefaultTargetTreeNode(getUserObject(userObject), allowsChildren);
			break;
		default:
			node = new DefaultMappableTreeNode(getUserObject(userObject), allowsChildren);
		}
		return node;
	}

	public class MyTreeObject {
		String name;
		Object obj;
		Object rootObj;
		
		public MyTreeObject(String name, Object obj) {
			this.rootObj = ElementMetaLoader.this.obj;
			this.name = name;
			this.obj = obj;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @return the obj
		 */
		public Object getObj() {
			return obj;
		}
		/**
		 * @return the rootObj
		 */
		public Object getRootObj() {
			return rootObj;
		}
		
		public String toString(){
			return name;
		}
		
	}
	private Object getUserObject(Object userObject) {
		if(userObject instanceof ElementMeta){
			return new MyTreeObject(((ElementMeta)userObject).getName(), userObject);
		}if(userObject instanceof AttributeMeta){
			return new MyTreeObject(((AttributeMeta)userObject).getName(), userObject);
		}
		
		return null;
	}

	/**
	 * Refresh subtree whose root is the given treeNode reflecting the given object.
	 * After the refreshing, the given tree will be notified for the update information.
	 * @param targetNode
	 * @param object
	 * @param tree if null, no corresponding tree update information will be broadcast.
	 */
	public static void refreshSubTreeByNewMetaObject(DefaultMutableTreeNode targetNode, DefaultMutableTreeNode newNode, JTree tree) 
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

	/**
	 * Based on the given object type, this function will convert the meta-data tree to a TreeNode-based tree structure, whose root is the returned TreeNode.
	 * @param o the meta-data object
	 * @return the root node representing the TreeNode structure mapping the given meta-data tree.
	 * @throws NodeLoader.MetaDataloadException
	 */
	public TreeNode loadData(Object o)
	{
		this.obj = o;
		if (o instanceof ElementMeta){
			return processElement((ElementMeta)o);
		}else if(o instanceof Component){
			return processElement(((Component)o).getRootElement());
		}else{
			throw new RuntimeException("ElementMetaNodeLoader.loadData() input " +
					"not recognized. " + o);
		}
	}

	/**
	 * Called by loadData().
	 *
	 * @param s
	 * @return a tree node to wrap the given meta data and its sub-tree.
	 */
	private DefaultMutableTreeNode processElement(ElementMeta s)
	{
		DefaultMutableTreeNode node = constructTreeNode(s);

		List<ElementMeta> childs = s.getChildElement();
		List<AttributeMeta> fields = s.getAttrData();
		//Collections.sort(fields, new CSVFieldMetaColumnNumberComparator());

		for (int i = 0; i < fields.size(); i++)
		{
			AttributeMeta fieldMeta = fields.get(i);
			node.add(constructTreeNode(fieldMeta, false));
		}

		for (int i = 0; i < childs.size(); i++)
		{
			ElementMeta childMeta = childs.get(i);
			DefaultMutableTreeNode subNode = processElement(childMeta);
			node.add(subNode);
		}
		return node;
	}

}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.1  2008/12/03 20:46:14  linc
 * HISTORY: UI update.
 * HISTORY:
 */
