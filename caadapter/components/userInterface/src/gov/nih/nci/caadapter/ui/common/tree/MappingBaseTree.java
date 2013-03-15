/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.tree;


import gov.nih.nci.caadapter.ui.common.MappableNode;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.JPanel;
//import javax.swing.JViewport;
import java.awt.Graphics;
//import java.awt.Component;
import java.util.ArrayList;
import java.util.List;


/**
 * This class provides some abstract implementations of commonly used tree features in this application.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.5 $
 *          date        $Date: 2008-06-09 19:53:52 $
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
		mappingMiddlePanel.repaint();
	}

	public void treeCollapsed(TreeExpansionEvent event)
	{
		mappingMiddlePanel.repaint();
	}

	public void redraw()
	{
		mappingMiddlePanel.repaint();
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


	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		mappingMiddlePanel.repaint();
	}

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
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.4  2007/12/13 21:08:43  wangeug
 * HISTORY      : resolve code dependence in compiling
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/12/04 15:11:26  wangeug
 * HISTORY      : add new method: return all visible mapped nodes
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/03 19:24:46  wangeug
 * HISTORY      : initila loading hl7 code without "clone"
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.18  2006/10/23 16:25:25  wuye
 * HISTORY      : Update cell rendering to show a different color for those undragable/unmappable node.
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.16  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/12/29 23:06:14  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/11/09 23:05:51  jiangsc
 * HISTORY      : Back to previous version.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/08/28 19:09:26  jiangsc
 * HISTORY      : Improved GUI Performance on large data loading.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/17 20:35:43  jiangsc
 * HISTORY      : Removed some comments
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/04 22:22:20  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 */
