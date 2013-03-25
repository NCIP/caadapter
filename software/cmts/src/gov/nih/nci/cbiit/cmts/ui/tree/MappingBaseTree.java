/*L
 * Copyright SAIC, SAIC-Frederick.
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


import gov.nih.nci.cbiit.cmts.ui.common.MappableNode;
import gov.nih.nci.cbiit.cmts.ui.mapping.MappingMiddlePanel;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.List;


/**
 * This class provides some abstract implementations of commonly used tree features in this application.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.3 $
 * @date       $Date: 2008-12-04 21:34:20 $
 *
 */
public abstract class MappingBaseTree extends AutoscrollableTree implements TreeExpansionListener
{
	private JPanel mappingMiddlePanel = null;
	private TreeNode rootTreeNode;
	public MappingBaseTree(JPanel m, TreeNode root)
	{
		this.mappingMiddlePanel = m;
		this.setCellRenderer(new DefaultMappingTreeCellRender());
		this.addTreeExpansionListener(this);
		rootTreeNode=root;
		if (root != null)
		{
			DefaultTreeModel dtm = new DefaultTreeModel(root);
			setModel(dtm);
		}
		else
		{//load in dummy data--implemented by sub-classes
			loadData();
		}
	}

	public void treeExpanded(TreeExpansionEvent event)
	{
		((MappingMiddlePanel)mappingMiddlePanel).renderInJGraph();//.repaint();
	}

	public void treeCollapsed(TreeExpansionEvent event)
	{
		((MappingMiddlePanel)mappingMiddlePanel).renderInJGraph();//.repaint();
	}

	private boolean isNodeMappedOrHasMappedDecendant(MappableNode mappable)
	{
		if (mappable.isMapped())
			return true;
		DefaultMutableTreeNode treeNode=(DefaultMutableTreeNode)mappable;
		if(treeNode.getChildCount()==0)
			return false;
		for(int i=0; i<treeNode.getChildCount();i++)
		{
			DefaultMutableTreeNode childNode=(DefaultMutableTreeNode)treeNode.getChildAt(i);
			boolean isChildMapped=false;
			if (childNode instanceof MappableNode)
				isChildMapped=isNodeMappedOrHasMappedDecendant((MappableNode)childNode);
			if (isChildMapped)
				return true;
		}
		
		return false;
	}
	
	/**
	 * Find visible tree node if it is mapped or has mapped descendant
	 * @return
	 */
	public List<DefaultMutableTreeNode> getAllVisibleMappedNode()
	{
		ArrayList<DefaultMutableTreeNode> rtnList=new ArrayList<DefaultMutableTreeNode>();
		for (int i=0;i<getRowCount();i++)
		{
			TreePath rowPath=getPathForRow(i);
			if (rowPath==null)
				continue;
			DefaultMutableTreeNode lastNode=(DefaultMutableTreeNode)rowPath.getLastPathComponent();
			if (lastNode instanceof MappableNode)
			{
				MappableNode mappedNode=(MappableNode)lastNode;
				if (isNodeMappedOrHasMappedDecendant(mappedNode))
					 rtnList.add(lastNode);
			}
//
//			Object usrObj=lastNode.getUserObject();
//			if (usrObj instanceof TableMetadata)
//				rtnList.add(((TableMetadata)usrObj).getXPath());
//			else if (usrObj instanceof ColumnMetadata)
//				rtnList.add(((ColumnMetadata)usrObj).getXPath());
//
//			else if (usrObj instanceof  MetaObject)
//				rtnList.add(((MetaObject)usrObj).getXmlPath());
		}
		return rtnList;
	}
	public void expandAll()
	{
		int size = getRowCount();
//		for (int i = 0; i < getRowCount(); i++)
		/**
		 * Insted of using getRowCount() in the for loop, this method will just expand once;
		 * Since expandRow method will change the row count on the fly, using getRowCount() will return different value each time.
		 * To optimize CPU efficiency, just implement expandAll at one level.
		 * See: gov.nih.nci.caadapter.ui.main.tree.actions.TreeExpandAllAction
		 */
		/* +5 is too expand the first element to the leaf*/
		for (int i = 0; i < size+5; i++)
		{
			if (i<getRowCount())
				expandRow(i);
		}
	}


//	protected void paintComponent(Graphics g)
//	{
//		//System.out.println("enter MappingBaseTree.paintComponent.");
//		super.paintComponent(g);
//		//System.out.println("leave MappingBaseTree.paintComponent.");
//	}

	protected abstract void loadData();

	/**
	 * If any of the ancestor nodes are collapsed,
	 * returns path to the first visible ancestor.
	 *
	 * @param path
	 * @return path to the first visible ancestor
	 */
	public TreePath getFirstVisibleAncestor(TreePath path)
	{
		while (!isVisible(path))
			path = path.getParentPath();
		return path;
	}

	public TreeNode getRootTreeNode() {
		return rootTreeNode;
	}
}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.2  2008/12/03 20:46:14  linc
 * HISTORY: UI update.
 * HISTORY:
 * HISTORY: Revision 1.1  2008/10/27 20:06:30  linc
 * HISTORY: GUI first add.
 * HISTORY:
 */

