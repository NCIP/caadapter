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


import gov.nih.nci.cbiit.cmts.common.XSDParser;
import gov.nih.nci.cbiit.cmts.core.BaseMeta;
import gov.nih.nci.cbiit.cmts.core.ElementMeta;
import gov.nih.nci.cbiit.cmts.core.MetaConstants;
import gov.nih.nci.cbiit.cmts.ui.common.MappableNode;
import gov.nih.nci.cbiit.cmts.ui.mapping.ElementMetaLoader;
import gov.nih.nci.cbiit.cmts.ui.mapping.MappingMiddlePanel;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.*;
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
/**
 * @author wangeug
 *
 */
public abstract class MappingBaseTree extends AutoscrollableTree implements TreeExpansionListener
{
	private JPanel mappingMiddlePanel = null;
	private TreeNode rootTreeNode;
	private XSDParser schemaParser;
	public MappingBaseTree(JPanel m, TreeNode root)
	{
		this.mappingMiddlePanel = m;
		this.setCellRenderer(new DefaultMappingTreeCellRender());
		this.addTreeExpansionListener(this);
		rootTreeNode=root;
		DefaultTreeModel dtm = new DefaultTreeModel(root);
		setModel(dtm);
	}

	public XSDParser getSchemaParser() {
		return schemaParser;
	}

	public void setSchemaParser(XSDParser schemaParser) {
		this.schemaParser = schemaParser;
	}

	public void treeExpanded(TreeExpansionEvent event)
	{
        TreePath slctPath=event.getPath();
		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) slctPath.getLastPathComponent();
        ElementMetaLoader.MyTreeObject treeSelection =(ElementMetaLoader.MyTreeObject)treeNode.getUserObject();
	    BaseMeta selectedBaseMeta=(BaseMeta)treeSelection.getUserObject();
        ElementMeta selectedMeta = null;

		gov.nih.nci.cbiit.cmts.core.Component rootComponent=(gov.nih.nci.cbiit.cmts.core.Component) treeSelection.getRootObject();

        if (selectedBaseMeta instanceof ElementMeta) selectedMeta = (ElementMeta) selectedBaseMeta;
        else
        {
            System.out.println("Error treeExpanded(TreeExpansionEvent event) : not element Meta");
            return;
        }

        boolean isChanged = false;
        for (int chldCnt=treeNode.getChildCount();chldCnt>0;chldCnt--)
		{
			DefaultMutableTreeNode childNode=(DefaultMutableTreeNode)treeNode.getChildAt(chldCnt-1);
			if (childNode.getChildCount() > 0) continue;

			ElementMetaLoader.MyTreeObject chldSelection =(ElementMetaLoader.MyTreeObject)childNode.getUserObject();		
			BaseMeta chldMeta=(BaseMeta)chldSelection.getUserObject();

            if (chldMeta instanceof ElementMeta)
			{
                //System.out.println("CCCX CD Expanding branch("+ childNode.getChildCount() + ")," + childNode + ", " + chldMeta.getName());
                ElementMeta chldElmtMeta=(ElementMeta)chldMeta;
                if ((selectedMeta.getName().indexOf("<choice") >= 0)&&(!chldElmtMeta.isIsChosen())) continue;
                DefaultMutableTreeNode newChildNode = null;
                boolean cTag = false;
                if ((chldElmtMeta.getChildElement() != null)&&(chldElmtMeta.getChildElement().size() > 0))
                {
                    cTag = true;
                    //System.out.println("CCCX Child Already Exist("+selectedMeta.getName()+") : " + chldElmtMeta.getName());
                    int newNodeType=ElementMetaLoader.SOURCE_MODE;
                    if (this instanceof MappingTargetTree)
                        newNodeType=ElementMetaLoader.TARGET_MODE;
                    newChildNode = (DefaultMutableTreeNode)new ElementMetaLoader(newNodeType).loadDataForRoot(chldElmtMeta, rootComponent);
                }
                else
                {
                    if (chldElmtMeta.isIsSimple())
                            continue;
                    //if (chldElmtMeta.isIsRecursive())
                    //        continue;
                    cTag = true;
                    //System.out.println("CCCX No Child("+selectedMeta.getName()+") : " + chldElmtMeta.getName() + ", num of children:" + childNode.getChildCount());
                    newChildNode = deepLoadElementMeta(chldElmtMeta, rootComponent);
                }

                if (cTag)
                {
                    isChanged = true;

                    DefaultMutableTreeNode d = (DefaultMutableTreeNode)treeNode.getChildAt(chldCnt-1);
                    ElementMetaLoader.MyTreeObject dDel =(ElementMetaLoader.MyTreeObject)d.getUserObject();
			        BaseMeta dMeta=(BaseMeta)dDel.getUserObject();
                    if (!(dMeta instanceof ElementMeta))
                    {
                        System.out.println("Error treeExpanded(TreeExpansionEvent event) : Changed Meta ("+d.toString()+") is not a element Meta");
                        return;
                    }
                    try
                    {
                        replaceUserObject(treeNode.getChildAt(chldCnt-1), newChildNode);
                    }
                    catch(Exception ee)
                    {
                        System.out.println("Error new child replace erroe : " + ee.getMessage());
                        return;
                    }

                    //treeNode.remove(chldCnt-1);
                    //treeNode.insert(newChildNode, chldCnt-1);
                }
            }
		}
        if (isChanged)
        {
            ((DefaultTreeModel)getModel()).reload(treeNode);
		    ((MappingMiddlePanel)mappingMiddlePanel).renderInJGraph();//.repaint();
        }
        else ((MappingMiddlePanel)mappingMiddlePanel).renderInJGraph();
    }
    private void replaceUserObject(TreeNode f_Node, TreeNode t_Node) throws Exception
    {
        DefaultMutableTreeNode fNode = (DefaultMutableTreeNode) f_Node;
        DefaultMutableTreeNode tNode = (DefaultMutableTreeNode) t_Node;
        //System.out.println("CCCX DD : " + fNode.toString() + "' and '" + tNode.toString() + "'");
        ElementMetaLoader.MyTreeObject tTreeObject =(ElementMetaLoader.MyTreeObject) tNode.getUserObject();
	    fNode.setUserObject(tTreeObject);

        if ((fNode.getChildCount() == 0)&&(tNode.getChildCount() == 0)) return;
        else if ((fNode.getChildCount() == 0)&&(tNode.getChildCount() > 0))
        {
            for(int i=0;i<tNode.getChildCount();i++) fNode.add((DefaultMutableTreeNode)tNode.getChildAt(i));
            //return;
        }
        else if (fNode.getChildCount() <= tNode.getChildCount())
        {
            List<DefaultMutableTreeNode> list = new ArrayList<DefaultMutableTreeNode>();
            for(int i=0;i<fNode.getChildCount();i++)
            {
                DefaultMutableTreeNode aNode = null;
                DefaultMutableTreeNode fcNode = (DefaultMutableTreeNode) fNode.getChildAt(i);
                for(int j=0;j<tNode.getChildCount();j++)
                {
                    DefaultMutableTreeNode tcNode = (DefaultMutableTreeNode) tNode.getChildAt(j);
                    if (fcNode.toString().equals(tcNode.toString()))
                    {
                        aNode = tcNode;
                        break;
                    }
                }
                if (aNode == null) throw new Exception("Not found '" + fcNode.toString() + "' under '" + tNode.toString() + "'");
                replaceUserObject(fcNode, aNode);
                list.add(aNode);
            }

            for(int i=0;i<tNode.getChildCount();i++)
            {
                DefaultMutableTreeNode tcNode = (DefaultMutableTreeNode) tNode.getChildAt(i);
                boolean included = false;
                for (DefaultMutableTreeNode aNode:list)
                {
                    if (aNode.toString().equals(tcNode.toString())) included = true;
                }
                if (!included) fNode.add(tcNode);
            }

            //return;
        }
        else throw new Exception("Not not matched the number of children between '" + fNode.toString() + "' and '" + tNode.toString() + "'");
    }
    /**
	 * Reset the ElementMeta and create new treeNode
	 * @param meta
	 * @return DefaultMutableTreeNode
	 */
	private DefaultMutableTreeNode deepLoadElementMeta(ElementMeta meta, Object rootComponent)
	{
		//System.out.println("MappingBaseTree.deepLoadElementMeta()...deep loading:"+meta);
		int newNodeType=ElementMetaLoader.SOURCE_MODE;
		if (this instanceof MappingTargetTree)
			newNodeType=ElementMetaLoader.TARGET_MODE;

        return getSchemaParser().expandNodeWithLazyLoad(meta, newNodeType, rootComponent);

	}
	public void treeCollapsed(TreeExpansionEvent event)
	{
		System.out.println("MappingBaseTree.treeCollapsed()..path:"+event.getPath());
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

