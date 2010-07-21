/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmts.ui.function;

import gov.nih.nci.cbiit.cmts.core.FunctionDef;
import gov.nih.nci.cbiit.cmts.ui.tree.DefaultMappableTreeNode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * This class 
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-29 22:18:18 $
 */
public class FunctionTypeNodeLoader implements Serializable{

	private Map<String, List<FunctionDef>> meta;

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
		node = new DefaultMappableTreeNode(getUserObject(userObject), allowsChildren);
		return node;
	}

	public class MyTreeObject implements Serializable {
		String name;
		Object obj;
		Object rootObj;
		
		public MyTreeObject(String name, Object obj) {
			this.rootObj = FunctionTypeNodeLoader.this.meta;
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
		if(userObject instanceof FunctionDef){
			return new MyTreeObject(((FunctionDef)userObject).getName(), userObject);
		}else 
			return userObject;
	}

	/**
	 * Based on the given object type, this function will convert the meta-data tree to a TreeNode-based tree structure, whose root is the returned TreeNode.
	 * @param o the meta-data object
	 * @return the root node representing the TreeNode structure mapping the given meta-data tree.
	 * @throws NodeLoader.MetaDataloadException
	 */
	public TreeNode loadData(Map<String,List<FunctionDef>> meta)
	{
		this.meta = meta;
		DefaultMutableTreeNode root = constructTreeNode("Functions");
		for(String i:meta.keySet()){
			root.add(processGroup(i,meta.get(i)));
		}
		return root;
	}

	/**
	 * Called by loadData().
	 *
	 * @param s
	 * @return a tree node to wrap the given meta data and its sub-tree.
	 */
	private DefaultMutableTreeNode processGroup(String groupName, List<FunctionDef> l)
	{
		DefaultMutableTreeNode node = constructTreeNode(groupName);

		for (FunctionDef i:l){
			node.add(constructTreeNode(i, false));
		}
		return node;
	}
	
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 */
