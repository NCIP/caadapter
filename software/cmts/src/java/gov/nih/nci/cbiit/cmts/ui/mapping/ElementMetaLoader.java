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
 * @since     CMTS v1.0
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

	public TreeNode loadDataForRoot(Object o, Object root)
	{
		nodeRoot = root;
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
	 * Based on the given object type, this function will convert the meta-data tree to a TreeNode-based tree structure, whose root is the returned TreeNode.
	 * @param o the meta-data object
	 * @return the root node representing the TreeNode structure mapping the given meta-data tree.
	 * @throws NodeLoader.MetaDataloadException
	 */
	public TreeNode loadData(Object o)
	{

		return loadDataForRoot(o, o);
	}

	/**
	 * Called by loadData().
	 *
	 * @param s
	 * @return a tree node to wrap the given meta data and its sub-tree.
	 */
    private DefaultMutableTreeNode processElement(ElementMeta s)
    {
        DefaultMutableTreeNode node = processElement(s, 0, null);
        //if (nodeMode == TARGET_MODE) displayNode(node, 0);
        return node;
    }
    private void displayNode(DefaultMutableTreeNode node, int depth)
    {
        String space = "";
        for(int i=0;i<depth;i++) space = space + "   ";

        String seq ="";
        if (node.getParent() != null) seq = "" + node.getParent().getIndex(node);

        //System.out.println("CCCX VV("+nodeMode+") " + space + "("+depth+")"+ seq + "--" + node.toString());
        depth++;
        for (int i=0;i<node.getChildCount();i++)
        {
            displayNode((DefaultMutableTreeNode)node.getChildAt(i), depth);
        }
    }
    private DefaultMutableTreeNode processElement(ElementMeta s, int depth, ElementMeta parent)
	{
        DefaultMutableTreeNode node = constructTreeNode(s, true);

        String space = "";
        for(int i=0;i<depth;i++) space = space + "   ";

        //String choiced = "";
        //if ((parent != null)&&(parent.getName().indexOf("<choice") >= 0)) choiced = " :"+s.isIsChosen()+":";

        //System.out.println("CCCX VV("+nodeMode+") " + space + "("+depth+")" + s.getName() + choiced + ", this childCount=" + node.getChildCount() + ", parent childCount=" + node.getParent().getChildCount());
		List<ElementMeta> childs = s.getChildElement();
		List<AttributeMeta> fields = s.getAttrData();

        //ignore attributes and child elements for not chosen choice element
		try {
			if (s.isIsChoice()&!s.isIsChosen())
            {
                //System.out.println("This is not Chosen : " + s.getId() + " : " + s.getName());
                return node;
            }
            //if ((s.isIsRecursive())&&(!s.isAtivated())&&(s.getChildElement().size() == 0)) return node;
            if ((s.isIsRecursive())&&(!s.isAtivated())) return node;
        } catch ( NullPointerException np) {

			System.out.println("ElementMetaLoader.processElement()..NullPointerException.:meta:"+s);
			np.printStackTrace();
			// TODO: handle exception
		}

        for (int i = 0; i < fields.size(); i++)
		{
			AttributeMeta fieldMeta = fields.get(i);
            if (s.isCDE_Element())
            {
                if (fieldMeta.getName().equals("PUBLICID")
                        || fieldMeta.getName().equals("VERSION"))
                    continue;
            }
            node.add(constructTreeNode(fieldMeta, false));
		}
        depth++;
		for (int i = 0; i < childs.size(); i++)
		{
			ElementMeta childMeta = childs.get(i);
			DefaultMutableTreeNode subNode = processElement(childMeta, depth, s);
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
