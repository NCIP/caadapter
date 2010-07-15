/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmts.ui.mapping;

import gov.nih.nci.cbiit.cmts.core.AttributeMeta;
import gov.nih.nci.cbiit.cmts.core.BaseMeta;
import gov.nih.nci.cbiit.cmts.core.Component;
import gov.nih.nci.cbiit.cmts.core.ElementMeta;
import gov.nih.nci.cbiit.cmts.ui.tree.DefaultMappableTreeNode;
import gov.nih.nci.cbiit.cmts.ui.tree.DefaultSourceTreeNode;
import gov.nih.nci.cbiit.cmts.ui.tree.DefaultTargetTreeNode;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import java.io.Serializable;
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
public class ElementMetaLoader implements Serializable
{
	public static final int SOURCE_MODE = 0;
	public static final int TARGET_MODE = 1;
	private int nodeMode = SOURCE_MODE;
	private Object nodeRoot = null;

	public ElementMetaLoader(int mode){
		this.nodeMode = mode;
	}

	/**
	 * Based on the given object type, this function will convert the meta-data tree to a TreeNode-based tree structure, whose root is the returned TreeNode.
	 * @param o the meta-data object
	 * @return the root node representing the TreeNode structure mapping the given meta-data tree.
	 * @throws NodeLoader.MetaDataloadException
	 */
	public TreeNode loadData(Object o)
	{
		this.nodeRoot = o;
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
		DefaultMutableTreeNode node = constructTreeNode(s, true);
	
		List<ElementMeta> childs = s.getChildElement();
		List<AttributeMeta> fields = s.getAttrData();
		//ignore attributes and child elements for not chosen choice element
		if (s.isIsChoice()&!s.isIsChosen())
			return node;
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

	/**
	 * Construct a tree node
	 *
	 * @param userObject
	 * @param allowsChildren
	 * @return a tree node that wraps the user object.
	 */
	private DefaultMutableTreeNode constructTreeNode(Object userObject, boolean allowsChildren)
	{
		DefaultMutableTreeNode node = null;
		switch(nodeMode){
		case SOURCE_MODE:
			node = new DefaultSourceTreeNode(new MyTreeObject(((BaseMeta)userObject).getName(),userObject), allowsChildren);
			break;
		case TARGET_MODE:
			node = new DefaultTargetTreeNode(new MyTreeObject(((BaseMeta)userObject).getName(),userObject), allowsChildren);
			break;
		default:
			node = new DefaultMappableTreeNode(new MyTreeObject(((BaseMeta)userObject).getName(),userObject), allowsChildren);
		}
		return node;
	}

	public class MyTreeObject implements Serializable {
		private String name;
		private Object userObject;
		private Object rootObject;
		
		public MyTreeObject(String name, Object obj) {
			this.rootObject = ElementMetaLoader.this.nodeRoot;
			this.name = name;
			this.userObject = obj;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the obj
		 */
		public Object getUserObject() {
			return userObject;
		}
		/**
		 * @return the rootObj
		 */
		public Object getRootObject() {
			return rootObject;
		}
		
		public String toString(){
			return name;
		}
	}
}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.1  2008/12/03 20:46:14  linc
 * HISTORY: UI update.
 * HISTORY:
 */
