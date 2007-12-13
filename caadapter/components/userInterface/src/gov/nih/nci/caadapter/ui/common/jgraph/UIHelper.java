/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/jgraph/UIHelper.java,v 1.8 2007-12-13 21:08:29 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.ui.common.jgraph;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.function.meta.ParameterMeta;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;
import gov.nih.nci.caadapter.ui.common.MappableNode;
import gov.nih.nci.caadapter.ui.common.tree.DefaultMappableTreeNode;
import gov.nih.nci.caadapter.ui.common.tree.DefaultTargetTreeNode;
import gov.nih.nci.caadapter.ui.common.tree.DefaultSourceTreeNode;
import gov.nih.nci.caadapter.common.metadata.AttributeMetadata;
import gov.nih.nci.caadapter.common.metadata.ObjectMetadata;
import gov.nih.nci.caadapter.common.metadata.AssociationMetadata;
import gov.nih.nci.caadapter.common.metadata.TableMetadata;
import gov.nih.nci.caadapter.common.metadata.ColumnMetadata;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.Set;
import java.util.Enumeration;

/**
 * This class defines a list of utilities to help carry out some general functionality used in UI.
 * This class utilizes singleton pattern.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.8 $
 *          date        $Date: 2007-12-13 21:08:29 $
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
		if (metaData instanceof ObjectMetadata)
			linkColor=MAPPING_LINK_OBJECT_COLOR;
		else if(metaData instanceof TableMetadata)
			linkColor=MAPPING_LINK_OBJECT_COLOR;
		else if (metaData instanceof AttributeMetadata)
			linkColor=MAPPING_LINK_ATTRIBUTE_COLOR;
		else if(metaData instanceof ColumnMetadata)
		{
			linkColor=MAPPING_LINK_ATTRIBUTE_COLOR;
			ColumnMetadata column=(ColumnMetadata)metaData;
			String columnType=column.getType();
			if (columnType!=null&&columnType.equals(ColumnMetadata.TYPE_ASSOCIATION))
				linkColor=MAPPING_LINK_ASSOCIATION_COLOR;
		}
		else if (metaData instanceof AssociationMetadata)
			linkColor=MAPPING_LINK_ASSOCIATION_COLOR;
		
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
	public static final MappableNode constructMappableNode(Object treeRoot, MetaObject metaObject)
	{
		MappableNode result = null;
		if(treeRoot instanceof DefaultMappableTreeNode)
		{//either source or target
			DefaultMappableTreeNode root = (DefaultMappableTreeNode) treeRoot;
			result = (MappableNode) root.findFirstTreeNodeMatchUserObject(metaObject);
		}
		if(result==null)
		{
			Log.logError(internalInstance, "Could not find the metaObject '" + metaObject + "' in the given tree rooted by '" + treeRoot + "'.");
			Log.logError(internalInstance, "treeRoot is of type '" + (treeRoot==null? "null" : treeRoot.getClass().getName()) + "'");
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
	            Log.logError(internalInstance, (new StringBuilder()).append("Could not find the datatypeBaseObject '").append(dtObjectXmlPath).append("' in the given tree rooted by '").append(treeRoot).append("'.").toString());
	            Log.logError(internalInstance, (new StringBuilder()).append("treeRoot is of type '").append(treeRoot != null ? treeRoot.getClass().getName() : "null").append("'").toString());
	    }
	    return result;
	}
	
    public static DefaultMutableTreeNode findTreeNodeWithXmlPath(DefaultMutableTreeNode treeNode, String nodeXmlPath)
    {
        
        Object userObj = treeNode.getUserObject();
        if(userObj instanceof DatatypeBaseObject)
        {
            DatatypeBaseObject dtUserObj = (DatatypeBaseObject)userObj;
            if(dtUserObj.getXmlPath().equals(nodeXmlPath))
                return treeNode;
        }
        else if (userObj instanceof MetaObject )
        {
        	String objXmlPath=((MetaObject)userObj).getXmlPath();
        	if (nodeXmlPath.equalsIgnoreCase(objXmlPath))
        		return treeNode;
        }

        for(Enumeration childEnum = treeNode.children(); childEnum.hasMoreElements();)
        {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)childEnum.nextElement();
            DefaultMutableTreeNode childUserObj = findTreeNodeWithXmlPath(childNode, nodeXmlPath);
            if(childUserObj != null)
                return childUserObj;
        }

        return null;
    }
	/**
	 * Return true if port matches the type of input (isInputPort is true) or output (isInputPort is false);
	 * @param port
	 * @param isInputPort
	 * @return true if port matches the type of input (isInputPort is true) or output (isInputPort is false);
	 */
	public static final boolean isPortTypeMatch(DefaultPort port, boolean isInputPort)
	{
		boolean result = false;
		Object obj = port.getUserObject();
		if(obj instanceof ParameterMeta)
		{
			ParameterMeta paramMeta = (ParameterMeta) obj;
			result = (isInputPort ==paramMeta.isInput());
		}
		return result;
	}

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
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.7  2007/12/06 20:46:56  wangeug
 * HISTORY      : support both data model and object model
 * HISTORY      :
 * HISTORY      : Revision 1.6  2007/12/03 15:25:47  wangeug
 * HISTORY      : mappingParser: find target node from an Xmi tree node
 * HISTORY      :
 * HISTORY      : Revision 1.5  2007/07/03 18:37:48  wangeug
 * HISTORY      : construct map node with xmlpath
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/06/14 15:44:58  wangeug
 * HISTORY      : set link color of target table based on column type:
 * HISTORY      : TYPE_ATTRIBUTE or TYPE_ASSOCIATION
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/06/12 20:17:16  wangeug
 * HISTORY      : set colors with links
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/04/19 14:05:44  wangeug
 * HISTORY      : set link color based on linkType
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.26  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.25  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.24  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.23  2005/12/29 23:06:17  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.22  2005/12/14 21:37:19  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.21  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.20  2005/11/11 19:23:59  jiangsc
 * HISTORY      : Support Pseudo Root in Mapping Panel.
 * HISTORY      :
 * HISTORY      : Revision 1.19  2005/11/02 20:23:56  jiangsc
 * HISTORY      : Enhanced to select only not-mapped port
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/10/21 15:11:55  jiangsc
 * HISTORY      : Resolve scrolling issue.
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/10/20 22:29:29  jiangsc
 * HISTORY      : Resolve scrolling issue.
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/10/18 17:01:03  jiangsc
 * HISTORY      : Changed one function signature in DragDrop component;
 * HISTORY      : Enhanced drag-drop status monitoring in HL7MappingPanel;
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/09/27 21:47:59  jiangsc
 * HISTORY      : Customized edge rendering and initially added a link highlighter class.
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/08/24 22:28:41  jiangsc
 * HISTORY      : Enhanced JGraph implementation;
 * HISTORY      : Save point of CSV and HSM navigation update;
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/08/24 21:09:29  jiangsc
 * HISTORY      : minor update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/08/22 21:35:28  jiangsc
 * HISTORY      : Changed BaseComponentFactory and other UI classes to use File instead of string name;
 * HISTORY      : Added first implementation of Function Constant;
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/08/04 22:22:10  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 */
