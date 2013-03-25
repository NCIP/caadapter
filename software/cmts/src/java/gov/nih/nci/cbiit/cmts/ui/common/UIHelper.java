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

package gov.nih.nci.cbiit.cmts.ui.common;


import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

import gov.nih.nci.cbiit.cmts.core.AttributeMeta;
import gov.nih.nci.cbiit.cmts.core.BaseMeta;
import gov.nih.nci.cbiit.cmts.core.Component;
import gov.nih.nci.cbiit.cmts.core.ElementMeta;
import gov.nih.nci.cbiit.cmts.ui.mapping.ElementMetaLoader;
import gov.nih.nci.cbiit.cmts.ui.tree.DefaultMappableTreeNode;
import gov.nih.nci.cbiit.cmts.ui.tree.DefaultSourceTreeNode;
import gov.nih.nci.cbiit.cmts.ui.tree.DefaultTargetTreeNode;
import gov.nih.nci.cbiit.cmts.ui.function.FunctionBoxGraphCell;
import gov.nih.nci.cbiit.cmts.ui.function.FunctionBoxGraphPort;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Enumeration;
import java.util.StringTokenizer;

/**
 * This class defines a list of utilities to help carry out some general functionality used in UI.
 * This class utilizes singleton pattern.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2008-12-10 15:43:02 $
 */
public final class UIHelper
{
	//to defin the vertex that represents the almost invisible cell for source or target tree
	public static final int VERTEX_CELL_WIDTH = 3;
	public static final int VERTEX_CELL_HEIGHT = 3;
	public static final Color DEFAULT_VERTEX_COLOR = Color.BLACK;
	public static final Color DEFAULT_VERTEX_BORDER_COLOR = Color.BLACK;
	public static final Color DEFAULT_MAPPING_LINK_COLOR = Color.BLUE.darker().darker();
	public static final Color MAPPING_LINK_OBJECT_COLOR = Color.green.darker();
	public static final Color MAPPING_LINK_ATTRIBUTE_COLOR = Color.blue;
	public static final Color MAPPING_LINK_ASSOCIATION_COLOR = Color.RED;
	private static final Dimension invisibleVertexDimension = new Dimension(VERTEX_CELL_WIDTH, VERTEX_CELL_HEIGHT);
	//location of port in cell
	public static final int PORT_LEFT = 0;
	public static final int PORT_RIGHT = 1;
	public static final int PORT_NORTH = 2;
	public static final int PORT_SOUTH = 3;

	private static final String PORT_INPUT_STRING = "Input";
	private static final String PORT_OUTPUT_STRING = "Output";

	//used most time for Logging purposes.
	private static final UIHelper internalInstance = new UIHelper();

	private UIHelper()
	{

	}

	public static final int getDefaultFunctionalBoxInputOrientation()
	{
		return PORT_LEFT;
	}

	public static final int getDefaultFunctionalBoxOutputOrientation()
	{
		return PORT_RIGHT;
	}

	public static final String getDefaultFunctionalBoxInputCaption()
	{
		return PORT_INPUT_STRING;
	}

	public static final String getDefaultFunctionalBoxOutputCaption()
	{
		return PORT_OUTPUT_STRING;
	}

	//	public static final void timeMessage(Object sender, String msg, long timeLapse)
	//	{
	//		Log.logInfo(sender, msg + " time(milisec): " + timeLapse);
	//	}

	private static final boolean isPortOrientationMatch(DefaultGraphCell cell, boolean isInputData)
	{
		Object cellLabel = cell.getUserObject();
		if(cellLabel!=null)
		{
			String cellLabelStr = (String) cellLabel;
			String defaultCaption = isInputData ? getDefaultFunctionalBoxInputCaption() : getDefaultFunctionalBoxOutputCaption();
			return (cellLabelStr.contains(defaultCaption));
		}
		else
		{
			return false;
		}
	}

	/**
	 * Return first un-used port.
	 * @param cell
	 * @param isInputData
	 * @return a DefaultPort object
	 */
	public static final DefaultPort getFirstUnmappedPort(DefaultGraphCell cell, boolean isInputData)
	{
		int size = cell.getChildCount();
		//		Log.logInfo(this, "Cell has " + size + " children. Cell of type '"+cell.getClass().getName() + "'.");
		DefaultPort port = null;
		boolean found = false;
		for (int i = 0; i < size; i++)
		{
			DefaultGraphCell childCell = (DefaultGraphCell) cell.getChildAt(i);
			if (childCell instanceof DefaultPort)
			{
				port = (DefaultPort) childCell;
				boolean portOrientationMatch = isPortOrientationMatch(childCell, isInputData);
				if (!port.edges().hasNext() && portOrientationMatch)
				{//not used port, empty and the port caption matches, ie, input for source, output for target
					found = true;
					break;
				}
			}
			else if(childCell instanceof DefaultGraphCell)
			{//cell contains cell, then find the first of port of that then.
				/**
				 * NOTE:
				 * Since DefaultPort is descendant of DefaultGraphCell, so has to structure this way.
				 */
				port = getFirstUnmappedPort(childCell, isInputData);
				if(port!=null)
				{
					found = true;
					break;
				}
			}
		}

		if(found)
		{
			return port;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Answer whether a given tree node is from source or target tree.
	 * @param treeNode
	 * @return whether a given tree node is from source or target tree.
	 */
	public static final boolean isDataFromSourceTree(TreeNode treeNode)
	{
		boolean isDataFromSourceTree = false;
		if (treeNode instanceof DefaultSourceTreeNode)
		{
			isDataFromSourceTree = true;
		}
		else if (treeNode instanceof DefaultTargetTreeNode)
		{
			isDataFromSourceTree = false;
		}
		else
		{
			String msg = "The data is of type '" + treeNode.getClass().getName() + "', but I don't know where it is from!";
			System.err.println(msg);
			throw new UnsupportedOperationException(msg);
		}
		return isDataFromSourceTree;
	}

	public static final Dimension getDefaultSourceOrTargetVertexDimension()
	{
		return invisibleVertexDimension;
	}

	public static final Color getLinkColor(Object metaData)
	{
		Color linkColor=DEFAULT_MAPPING_LINK_COLOR;
		if (metaData instanceof ElementMeta)
			linkColor=MAPPING_LINK_OBJECT_COLOR;
		else if (metaData instanceof AttributeMeta)
			linkColor=MAPPING_LINK_ATTRIBUTE_COLOR;

		return linkColor;
	}

	public static final AttributeMap getDefaultUnmovableEdgeStyle(Object metaData)
	{
		Color linkColor=getLinkColor(metaData);
		return getDefaultUnmovableMappingEdgeStyle(linkColor);
	}

	private static final AttributeMap getDefaultUnmovableMappingEdgeStyle(Color lineColor)
	{
		AttributeMap lineStyle = new AttributeMap();
		GraphConstants.setLineBegin(lineStyle, GraphConstants.ARROW_NONE);
		GraphConstants.setLineColor(lineStyle,lineColor);
		GraphConstants.setBeginSize(lineStyle, 10);
		GraphConstants.setFont(lineStyle, GraphConstants.DEFAULTFONT.deriveFont(10));
		GraphConstants.setBendable(lineStyle, false);
		GraphConstants.setEditable(lineStyle, false);
		GraphConstants.setMoveable(lineStyle, false);
		GraphConstants.setResize(lineStyle, false);
		GraphConstants.setSizeable(lineStyle, false);
		return lineStyle;
	}

	public static final AttributeMap getDefaultInvisibleVertexAttribute(Point position, boolean representSourceVertex)
	{
		AttributeMap map = getDefaultInvisibleVertexBounds(position, representSourceVertex);
		GraphConstants.setEditable(map, false);
		GraphConstants.setBendable(map, false);
		GraphConstants.setSelectable(map, false);
		GraphConstants.setResize(map, false);
		GraphConstants.setSizeable(map, false);
		return map;
	}

	public static final AttributeMap getDefaultInvisibleVertexBounds(Point position, boolean representSourceVertex)
	{
		AttributeMap map = new AttributeMap();
		Dimension cellDimension = getDefaultSourceOrTargetVertexDimension();
		//hide source is better? or present the target cell is better?
		double pointX = position.getX();
		if (representSourceVertex)
		{
			pointX -= Math.ceil(cellDimension.getWidth() * 2 + 0.5);
		}
		else
		{
			pointX += Math.ceil(cellDimension.getWidth() + 2 );
		}
		map = (AttributeMap) UIHelper.createBounds(map, pointX, position.getY(), cellDimension, DEFAULT_VERTEX_COLOR, false);
		//since it is invisible, explicitly set autosize to be false.
		GraphConstants.setAutoSize(map, false);
		return map;
	}

	/**
	 * copied similar function from JGraph implementation to make the vertex smaller
	 * Returns an attributeMap for the specified position and color.
	 */
	public static Map createBounds(AttributeMap map, Point2D point, Dimension dimension, Color c, boolean moveable)
	{
		return createBounds(map, point.getX(), point.getY(), dimension, c, moveable);
	}

	public static Map createBounds(AttributeMap map, double x, double y, Dimension dimension, Color c, boolean moveable)
	{
		//		final int ALPHA = 255;
		GraphConstants.setBounds(map, map.createRect(x, y, dimension.getWidth(), dimension.getHeight()));
		//		GraphConstants.setBorder(map, BorderFactory.createBevelBorder(BevelBorder.RAISED));
		GraphConstants.setForeground(map, Color.BLACK.darker().darker());//new Color(ALPHA - c.getRed(), ALPHA - c.getGreen(), ALPHA - c.getBlue(), ALPHA));
		GraphConstants.setBackground(map, c.darker());
		// Add a nice looking gradient background
		GraphConstants.setGradientColor(map, c.darker());
		// Make sure the cell is resized on insert
		//		GraphConstants.setSize();
		//		GraphConstants.setResize(map, true);
		// Add a Border Color Attribute to the Map
		GraphConstants.setBorderColor(map, Color.BLACK.darker().darker());
		GraphConstants.setFont(map, GraphConstants.DEFAULTFONT.deriveFont(Font.BOLD, 12));
		GraphConstants.setMoveable(map, moveable);
		GraphConstants.setOpaque(map, true);
		GraphConstants.setResize(map, false);
		GraphConstants.setAutoSize(map, true);
		return map;
	}

	public static final Map getDefaultFunctionBoxPortAttributes(Map map, Dimension portDimension)
	{
		GraphConstants.setSize(map, portDimension);
		GraphConstants.setSize(map, portDimension);
		GraphConstants.setSelectable(map, true);
		GraphConstants.setBorderColor(map, Color.RED.darker().darker());
		GraphConstants.setMoveable(map, true);
		return map;
	}

	/**
	 * Could return null if nothing is found.
	 * @param treeRoot
	 * @param metaObject
	 * @return MappableNode, null if nothing is found.
	 */
	public static final MappableNode constructMappableNode(Object treeRoot, Object metaObject)
	{
		MappableNode result = null;
		if(treeRoot instanceof DefaultMappableTreeNode)
		{//either source or target
			DefaultMappableTreeNode root = (DefaultMappableTreeNode) treeRoot;
			result = (MappableNode) findFirstTreeNodeMatchUserObject(root, metaObject);
		}
		if(result==null)
		{
			System.out.println("Could not find the metaObject '" + metaObject + "' in the given tree rooted by '" + treeRoot + "'.");
			//Log.logError(internalInstance, "Could not find the metaObject '" + metaObject + "' in the given tree rooted by '" + treeRoot + "'.");
			//Log.logError(internalInstance, "treeRoot is of type '" + (treeRoot==null? "null" : treeRoot.getClass().getName()) + "'");
		}
		return result;
	}

	public static final MappableNode constructMappableNodeObjectXmlPath(Object treeRoot, String dtObjectXmlPath)
	{
		MappableNode result = null;
		if(treeRoot instanceof DefaultMappableTreeNode)
		{//either source or target
			DefaultMappableTreeNode root = (DefaultMappableTreeNode) treeRoot;
			result = (MappableNode)findTreeNodeWithXmlPath(root, (String)dtObjectXmlPath);
		}
		else if (treeRoot instanceof DefaultMutableTreeNode)
		{
			DefaultMutableTreeNode rootNode =(DefaultMutableTreeNode)treeRoot;
			result = (MappableNode)findTreeNodeWithXmlPath(rootNode, (String)dtObjectXmlPath);
		}


        if(result == null)
        {
//		    int c = 0;
//            if(treeRoot instanceof DefaultMappableTreeNode) c=1;
//		    else if (treeRoot instanceof FunctionBoxGraphCell) c=2;
//            else if (treeRoot instanceof FunctionBoxGraphPort) c=3;
            if ((dtObjectXmlPath.trim().equals("constant"))||
                (dtObjectXmlPath.trim().equals("currentDate"))) {}
            else
            {
                System.out.println("UIHelper.constructMappableNodeObjectXmlPath():Could not find the data obj in the given tree rooted by '" + treeRoot + "'. path:"+ dtObjectXmlPath);
            }

            //Log.logError(internalInstance, (new StringBuilder()).append("Could not find the datatypeBaseObject '").append(dtObjectXmlPath).append("' in the given tree rooted by '").append(treeRoot).append("'.").toString());
			//Log.logError(internalInstance, (new StringBuilder()).append("treeRoot is of type '").append(treeRoot != null ? treeRoot.getClass().getName() : "null").append("'").toString());
		}

		return result;
	}

	public static DefaultMutableTreeNode findTreeNodeWithXmlPath(DefaultMutableTreeNode treeNode, String nodeXmlPath)
	{
		if (nodeXmlPath==null)
		{
			System.out.println("UIHelper.findTreeNodeWithXmlPath()..invalid node to search:"+nodeXmlPath);
			return null;
		}

		StringTokenizer st = new StringTokenizer(nodeXmlPath, "/@");
		boolean foundRoot = false;
		DefaultMutableTreeNode e = treeNode;
		while(st.hasMoreTokens() && e!=null){
			String tmp = st.nextToken();
			if(!foundRoot){
				if(e.toString().equals(tmp)){
					foundRoot = true;
					continue;
				}else
					continue;
			}else{
				int childCount = e.getChildCount();
				DefaultMutableTreeNode found = null;
				for(int i=0; i<childCount; i++){
					DefaultMutableTreeNode child = (DefaultMutableTreeNode) e.getChildAt(i);
					if(child.toString().equals(tmp)){
						found = child;
						break;
					}
				}
				if(found == null){
					return null;
				}else{
					e = found;
				}
			}
		}

		if(!foundRoot)
			return null;
		return e;
	}
	
	/**
	 * This method will traverse the sub-tree whose root is itself and including itself to
	 * find a tree Node whose userObject matches the given one. If none is found, return null.
	 * @param userObject
	 * @return the default mutable tree node matching the given object.
	 */
	public static DefaultMutableTreeNode findFirstTreeNodeMatchUserObject(DefaultMutableTreeNode treeNode, Object userObject)
	{
		Object selfUserObject = treeNode.getUserObject();
		if(selfUserObject!=null && selfUserObject instanceof ElementMetaLoader.MyTreeObject)
			selfUserObject = ((ElementMetaLoader.MyTreeObject)selfUserObject).getUserObject();
		boolean boolEquals = selfUserObject==null? userObject==null : selfUserObject.equals(userObject);
		if(boolEquals)
		{
			return treeNode;
		}
		else
		{
			DefaultMutableTreeNode foundTarget = null;
			int childCount = treeNode.getChildCount();
			for(int i=0; i<childCount; i++)
			{
				foundTarget = null;
				Object obj = treeNode.getChildAt(i);
				if(obj instanceof DefaultMappableTreeNode)
				{
					DefaultMappableTreeNode childNode = (DefaultMappableTreeNode) obj;
					foundTarget = findFirstTreeNodeMatchUserObject(childNode, userObject);
					if(foundTarget!=null)
					{
						break;
					}
				}
			}
			return foundTarget;
		}
	}

	public static String getPathStringForNode(DefaultMutableTreeNode node){
		StringBuilder sb = new StringBuilder();
		Object[] path = node.getUserObjectPath();
		for(int i=0; i<path.length; i++){
			if(path[i] instanceof ElementMetaLoader.MyTreeObject){
				Object obj = ((ElementMetaLoader.MyTreeObject)path[i]).getUserObject();
				if(obj instanceof ElementMeta){
					sb.append("/").append(((ElementMeta)obj).getName());
				}else if(obj instanceof AttributeMeta){
					sb.append("/@").append(((AttributeMeta)obj).getName());
				}else
					return "";
			}else
				return "";
		}
		return sb.toString();
	}
	
	/**
	 * Return true if port matches the type of input (isInputPort is true) or output (isInputPort is false);
	 * @param port
	 * @param isInputPort
	 * @return true if port matches the type of input (isInputPort is true) or output (isInputPort is false);
	 */
	//	public static final boolean isPortTypeMatch(DefaultPort port, boolean isInputPort)
	//	{
	//		boolean result = false;
	//		Object obj = port.getUserObject();
	//		if(obj instanceof ParameterMeta)
	//		{
	//			ParameterMeta paramMeta = (ParameterMeta) obj;
	//			result = (isInputPort ==paramMeta.isInput());
	//		}
	//		return result;
	//	}

	/**
	 * Return true if port is already mapped;
	 * @param port
	 * @return true if port is already mapped.
	 */
	public static final boolean isPortMapped(DefaultPort port)
	{
		boolean result = false;
		Set edges = port.getEdges();
		if (edges != null && !edges.isEmpty())
		{
			result = true;
		}
		return result;
	}

}
/**
 * HISTORY : $Log: not supported by cvs2svn $
 * HISTORY : Revision 1.1  2008/12/09 19:04:17  linc
 * HISTORY : First GUI release
 * HISTORY :
 * HISTORY : Revision 1.1  2008/12/03 20:46:14  linc
 * HISTORY : UI update.
 * HISTORY :
 */
