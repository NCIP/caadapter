/*L
 * Copyright SAIC.
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
package gov.nih.nci.cbiit.cmts.ui.mapping;

import gov.nih.nci.cbiit.cmts.ui.common.MappableNode;

import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.SwingUtilities;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Component;

/**
 * This class defines ...
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.3 $
 * @date       $Date: 2008-12-04 21:34:20 $
 *
 */
public class MappingTreeScrollPane extends JScrollPane 
{
	final public static String DRAW_NODE_TO_LEFT="MAPPING_SOURCE";
	final public static String DRAW_NODE_TO_RIGHT="MAPPING_TARGET";
	final private static Color highlightColor=Color.blue;
//	final private static Color normalColor=Color.gray;
	final private static int BROKEN_LINE_PIECE=3;
	
	private String paneType;
	public MappingTreeScrollPane(String type)
	{
		super();
		paneType=type;
		setBackground(Color.white);
		setBorder(BorderFactory.createEmptyBorder());
		MappingTreeScrollPaneAdjustmentHandler handler=new MappingTreeScrollPaneAdjustmentHandler();
		this.getVerticalScrollBar().addAdjustmentListener(handler);
	}
	
	/**
	 * Override method to see the lines drawn from tree node to border
	 */
	public void setViewportView(Component view)
	{
		super.setViewportView(view);
		if (view instanceof JTree)
		{
			getViewport().setOpaque(false);
			((JTree)view).setOpaque(false);
		}
	}
	/**
	 * Override method to paint the tree and mapping lines
	 * @see java.awt.Container#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g)
	{
		try {
			super.paintComponent(g);
			//paint the tree
			//System.out.println("enter MappingTreeScrollPane.paintComponent()");
			Component viewComp=this.getViewport().getView();
			if (viewComp!=null&&viewComp instanceof JTree)
			{
				Color dftColor=g.getColor();
				JTree mappingTree=(JTree)viewComp;
				DefaultMutableTreeNode treeRoot=(DefaultMutableTreeNode)mappingTree.getModel().getRoot();
				recursiveDrawLeaf(g, mappingTree, treeRoot);
				g.setColor(dftColor);
			}
			//System.out.println("leave MappingTreeScrollPane.paintComponent()."+viewComp.getClass()+":"+viewComp.getBounds());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Recuisively draw lines for each mapped node
	 * SourceTree: Node/visible Ancestor --->ScrollPane right border
	 * Target Tree: ScrollPane right borde <-- Node/visible Ancestor
	 * @param g Graphics of the ScrollPane
	 * @param tree Container tree of the treeNode to be drawn
	 * @param treeNode TreeNode to be drawn
	 */
	private void recursiveDrawLeaf(Graphics g, JTree tree, DefaultMutableTreeNode treeNode )
    {
		if (treeNode instanceof MappableNode)
    	{
			boolean drawNode=false;
			MappableNode mappedNode =(MappableNode)treeNode;
			if (mappedNode.isMapped())
					drawNode=true;
			
			if ((!drawNode)&&(treeNode.getChildCount()>0))
			{
				//check the userObject for SDKMeta
				Object userObject=treeNode.getUserObject();
				if(userObject instanceof ElementMetaLoader.MyTreeObject)
					userObject = ((ElementMetaLoader.MyTreeObject)userObject).getUserObject();
//				if (userObject  instanceof ElementMeta )
//				{
//					ElementMeta sdkMeta=(ElementMeta)userObject;
//					if (sdkMeta.isIsEnabled())
//						drawNode=true;
//					else
//					{
//						//For TableMeteData
//						int nodeCnt=treeNode.getChildCount();
//			    		for (int i=0; i<nodeCnt;i++)
//			    		{
//			    			DefaultMutableTreeNode childNode=(DefaultMutableTreeNode)treeNode.getChildAt(i);
//			    			if(childNode instanceof MappableNode)
//			    			{
//				    			MappableNode mappedChild =(MappableNode)childNode;		    				
//			    				if (mappedChild.isMapped())
//					    		{
//				    				//as long as one Column is mapped, the Table is mapped
//					    			drawNode=true;
//					    			break;
//					    		}
//				    		}
//			    		}
//					}
//				}
			}

			if (drawNode)
			{
				//go following if link-to-border is required
				Rectangle treeNodeBound =findVisibleTreeNodeOnPath(tree, treeNode);
				if(treeNodeBound==null) return;
	    		//convert the position from Tree to ScrollPane
				Point panelPoint =SwingUtilities.convertPoint(tree, treeNodeBound.getLocation(), this);
	    		//do not draw if the node is out of view bound
				int yPos=panelPoint.y+treeNodeBound.height - treeNodeBound.height/2;
    		
	    		if (yPos>this.getViewportBorderBounds().height)
	    			return;
			
				int xStart=panelPoint.x+treeNodeBound.width + BROKEN_LINE_PIECE;
	    		int xEnd=this.getBounds().width;
	    		
	    		//set line ends with target tree 
	    		if (paneType.equals(DRAW_NODE_TO_LEFT))
	    		{
	    			xStart=0;
	    			xEnd =panelPoint.x;
	    		}
	   			Font dftFont=g.getFont();
	   			TreePath slctPath=tree.getSelectionPath();
	   			if (slctPath!=null)
	   			{
	   				//draw "highlight" line
	    			DefaultMutableTreeNode slctNode=(DefaultMutableTreeNode)slctPath.getLastPathComponent();
	    			if (treeNode==slctNode)
	    			{
	    				g.setColor(highlightColor);
	    				g.setFont(new Font(dftFont.getName(), dftFont.getStyle(), dftFont.getSize()*4));
	    				drawHorizontalLinkFromNodeToPaneBorder(g, xStart, xEnd, yPos-1);
	    			}
	   			}
	   			//draw the regular line with default  color and font
//	   			Color linkColor=UIHelper.getLinkColor(treeNode.getUserObject());
//	   			g.setColor(linkColor);
	   			g.setFont(dftFont);
	   			drawHorizontalLinkFromNodeToPaneBorder(g, xStart, xEnd, yPos);
			}
    	}
		
		if (treeNode.getChildCount()>0)
    	{
    		//Recursively process all children nodes
    		int nodeCnt=treeNode.getChildCount();
    		for (int i=0; i<nodeCnt;i++)
    		{
    			DefaultMutableTreeNode childNode=(DefaultMutableTreeNode)treeNode.getChildAt(i);
    			Rectangle treeNodeBound =findVisibleTreeNodeOnPath(tree, childNode);
        		//convert the position from Tree to ScrollPane
        		Point panelPoint =SwingUtilities.convertPoint(tree, treeNodeBound.getLocation(), this);
        		//only recursively draw child node if the node is inside view SOUTH bound
        		int yPos=panelPoint.y+treeNodeBound.height - treeNodeBound.height/2;
        		if (yPos>this.getViewportBorderBounds().height)
        			return;
    			recursiveDrawLeaf(g, tree, childNode);   				
    		}
    	}
    }
	/**
	 * Find the view bound of a tree node if it is visible
	 * Otherwise, recursively search it's first ancestor
	 * @param tree
	 * @param treeNode
	 * @return
	 */
	private Rectangle findVisibleTreeNodeOnPath(JTree tree, DefaultMutableTreeNode treeNode )
	{
		DefaultTreeModel treeModel=(DefaultTreeModel)tree.getModel();
		Rectangle treeNodeBound= null;//tree.getPathBounds(new TreePath(treeModel.getPathToRoot(treeNode)));
		//recursively find visible ancestor node bound
		while (treeNodeBound==null && treeNode!=null)
		{
			treeNodeBound=tree.getPathBounds(new TreePath(treeModel.getPathToRoot(treeNode)));
			treeNode=(DefaultMutableTreeNode) treeNode.getParent();
		}
		return treeNodeBound;
	}
	/*
	 * Draw the link from a tree node to the container scrollPane border
	 */
	private void drawHorizontalLinkFromNodeToPaneBorder(Graphics g, int xStart, int xEnd, int yPos)
	{
		int xPieceStart=xStart;
		while (xPieceStart<xEnd)
		{
			g.drawLine(xPieceStart, yPos, Math.min(xEnd,xPieceStart+BROKEN_LINE_PIECE), yPos);
			xPieceStart+=2*BROKEN_LINE_PIECE;
		}
	}
	
	public String getPaneType() {
		return paneType;
	}
	
}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.2  2008/12/03 20:46:14  linc
 * HISTORY: UI update.
 * HISTORY:
 * HISTORY: Revision 1.1  2008/10/30 16:02:14  linc
 * HISTORY: updated.
 * HISTORY:
 */
