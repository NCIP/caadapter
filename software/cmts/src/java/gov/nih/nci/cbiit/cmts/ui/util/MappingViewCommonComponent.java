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


package gov.nih.nci.cbiit.cmts.ui.util;

import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultPort;

import gov.nih.nci.cbiit.cmts.common.PropertiesProvider;
import gov.nih.nci.cbiit.cmts.core.AttributeMeta;
import gov.nih.nci.cbiit.cmts.core.ElementMeta;
import gov.nih.nci.cbiit.cmts.ui.common.MappableNode;
import gov.nih.nci.cbiit.cmts.ui.properties.PropertiesResult;
import gov.nih.nci.cbiit.cmts.ui.tree.DefaultSourceTreeNode;
import gov.nih.nci.cbiit.cmts.ui.tree.DefaultTargetTreeNode;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A data container contains mapping information, which is the mapping either from-tree-to-functional-box,
 * from-functional-box-to-tree, or from-functional-box-to-functional-box, or direct-tree-to-tree mapping.
 *
 * This data container may act like a bridge between JGraph data structure and HL7SDK internal
 * data structure so as to keep each in sync with the other.
 *
 * This data is also served as the user object of the linkEdge object, so as to provide properties information on linkage.
 *
 * This is an immutable class and mainly participate in the mapping cache for rendering purpose.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2009-10-28 15:02:07 $
 */
public class MappingViewCommonComponent implements Comparable, java.io.Serializable, PropertiesProvider
{

	public static final String SEARCH_BY_SOURCE_NODE = "sourceNode";
	public static final String SEARCH_BY_TARGET_NODE = "targetNode";
	public static final String SEARCH_BY_SOURCE_CELL = "sourceCell";
	public static final String SEARCH_BY_TARGET_CELL = "targetCell";

	/**
	 * This function will search and return a list of MappingViewCommonComponent
	 * if any being found; an empty list if nothing is found.
	 * @param sourceList
	 * @param node
	 * @param SEARCH_BY any of the SEARCH_BY constants defined above.
	 * @return a list of MappingViewCommonComponent if any being found; an empty list if nothing is found.
	 */
	public static final List<MappingViewCommonComponent> findMappingViewCommonComponentListList(List<MappingViewCommonComponent> sourceList, Object node, String SEARCH_BY)
	{
		List<MappingViewCommonComponent> resultList = new ArrayList<MappingViewCommonComponent>();
		int size = sourceList==null ? 0 : sourceList.size();
		for(int i=0; i<size; i++)
		{
			MappingViewCommonComponent comp = sourceList.get(i);
			if(comp==null) continue;

			if(SEARCH_BY_SOURCE_NODE.equals(SEARCH_BY))
			{
				if(GeneralUtilities.areEqual(comp.getSourceNode(), node))
				{
					resultList.add(comp);
				}
			}
			else if(SEARCH_BY_TARGET_NODE.equals(SEARCH_BY))
			{
				if (GeneralUtilities.areEqual(comp.getTargetNode(), node))
				{
					resultList.add(comp);
				}
			}
			else if(SEARCH_BY_SOURCE_CELL.equals(SEARCH_BY))
			{
				if (GeneralUtilities.areEqual(comp.getSourceCell(), node))
				{
					resultList.add(comp);
				}
			}
			else if(SEARCH_BY_TARGET_CELL.equals(SEARCH_BY))
			{
				if (GeneralUtilities.areEqual(comp.getTargetCell(), node))
				{
					resultList.add(comp);
				}
			}
		}
		return resultList;
	}

	/**
	 * In current implementation, a sourceNode and could be any instance of
	 * gov.nih.nci.caadapter.ui.main.jgraph.graph.FunctionBoxDefaultPort (its isInput is false),
	 * gov.nih.nci.caadapter.ui.main.tree.DefaultSourceTreeNode,
	 */
	private MappableNode sourceNode;

	/**
	 * In current implementation, a targetNode and could be any instance of
	 * gov.nih.nci.caadapter.ui.main.jgraph.graph.FunctionBoxDefaultPort (its isInput is true),
	 * gov.nih.nci.caadapter.ui.main.tree.DefaultTargetTreeNode
	 */
	private MappableNode targetNode;

	private DefaultGraphCell sourceCell;
	private DefaultGraphCell targetCell;
	private DefaultEdge linkEdge;

	public MappingViewCommonComponent(MappableNode sourceNode, MappableNode targetNode,
									  DefaultGraphCell sourceCell, DefaultGraphCell targetCell, DefaultEdge linkEdge)
	{
		if(sourceNode==null)
		{
			throw new IllegalArgumentException("Source Node cannot be null!");
		}
		if(targetNode==null)
		{
			throw new IllegalArgumentException("Target Node cannot be null!");
		}
 
//		determineSourceAndTargetNode(sourceNode, sourceCell);
//		determineSourceAndTargetNode(targetNode, targetCell);

		this.linkEdge = linkEdge;

		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
		this.sourceCell = sourceCell;
		this.targetCell = targetCell;
	}

	private void determineSourceAndTargetNode(MappableNode paramNode, DefaultGraphCell paramCell)
	{
		if (paramNode instanceof DefaultSourceTreeNode)
		{
			this.sourceNode = paramNode;
			this.sourceCell = paramCell;
		}
//		else if (paramNode instanceof FunctionBoxDefaultPort)
//		{
//			FunctionBoxDefaultPort local = (FunctionBoxDefaultPort) paramNode;
//			if (!local.isInput())
//			{//is not an input port, that is, it is an output port, so it is a source node candidate
//				this.sourceNode = paramNode;
//				this.sourceCell = paramCell;
//			}
//			else
//			{//is an input port, so it should be the target
//				this.targetNode = paramNode;
//				this.targetCell = paramCell;
//			}
//		}
		else if (paramNode instanceof DefaultTargetTreeNode)
		{
			this.targetNode = paramNode;
			this.targetCell = paramCell;
		}
	}

	public MappableNode getSourceNode()
	{
		return sourceNode;
	}

//	public Object getSourceNodeParent()
//	{
//		if (sourceNode instanceof DefaultMutableTreeNode)
//		{
//			Object nodeObj=((DefaultMutableTreeNode)sourceNode).getUserObject();
//			if (nodeObj instanceof ElementMeta)
//			{
//				ElementMeta dtObj=(ElementMeta)nodeObj;
//				return dtObj.getId();
//			}
//			else if (nodeObj instanceof AttributeMeta)
//			{
//				AttributeMeta metaObj=(AttributeMeta)nodeObj;
//				return metaObj.getId();
//			}
//			else
//				return ((DefaultMutableTreeNode)sourceNode).getParent();
//		}
//		return sourceNode;
//	}
	public MappableNode getTargetNode()
	{
		return targetNode;
	}

//	public Object getTargetNodeParent()
//	{
//		if (targetNode instanceof DefaultMutableTreeNode)
//		{
//			Object nodeObj=((DefaultMutableTreeNode)targetNode).getUserObject();
//			if (nodeObj instanceof ElementMeta)
//			{
//				ElementMeta dtObj=(ElementMeta)nodeObj;
//				return dtObj.getId();
//			}
//			else if (nodeObj instanceof AttributeMeta)
//			{
//				AttributeMeta metaObj=(AttributeMeta)nodeObj;
//				return metaObj.getId();
//			}
//			else
//				return ((DefaultMutableTreeNode)sourceNode).getParent();
//		}
//		return targetNode;
//	}

	public DefaultGraphCell getSourceCell()
	{
		return sourceCell;
	}
	public DefaultGraphCell getTargetCell()
	{
		return targetCell;
	}

	public DefaultEdge getLinkEdge()
	{
		return linkEdge;
	}

	public boolean isSourceNode(MappableNode aNode)
	{
		return sourceNode.equals(aNode);
	}

	public boolean isTargetNode(MappableNode aNode)
	{
		return targetNode.equals(aNode);
	}

	/**
	 * Set mappable flag of source and target node to a new value.
	 * @param newValue
	 */
	public void setMappableFlag(boolean newValue)
	{
		if(sourceNode!=null)
			sourceNode.setMapStatus(newValue);
		if(targetNode!=null)
			targetNode.setMapStatus(newValue);
	}

	/**
	 * test if the given cell may cause sourceCell or targetCell removable
	 * @param cell
	 * @return if the given cell may cause sourceCell or targetCell removable
	 */
	private boolean isCellRemovable(DefaultGraphCell cell)
	{
		boolean result = sourceCell.equals(cell) || targetCell.equals(cell);
//		if(result)
//		{
		if(!(cell instanceof DefaultPort))
		{
			Set edgeSet = ((DefaultPort) cell.getChildAt(0)).getEdges();
//			result = result && !(cell instanceof FunctionBoxDefaultPort) && (edgeSet.isEmpty() || edgeSet.size()==1);
		}
		else
		{//if cell is an instance of DefaultPort, port is not removable.
			result = false;
		}
//		}
		return result;
	}
	/**
	 * Return if sourceCell or targetCell is found in the given cell list or the cell list contains the link edge;
	 * if so, it further checks if the sourceCell or targetCell is removable, if so, add to the cellList;
	 * @param cellList
	 * @return whether any matched cell is found.
	 */
	public boolean findMatchedCell(List cellList)
	{
		boolean sourceCellInList = cellList.contains(sourceCell);
		boolean targetCellInList = cellList.contains(targetCell);
//		boolean linkEdgeInList = cellList.contains(linkEdge);
		boolean result = sourceCellInList;
		result = result || targetCellInList;
		result = result || cellList.contains(linkEdge);

		if(result)
		{//test if source or target cell is removable, if so, add it to the cell list
			/**
			 * Logic:
			 * if either target or source cell is the vertex representation of source or target
			 * and only has one edge or edge is empty, and either is not a functional box,
			 * if the result is true (implies either edge, or source or target is in the list);
			 */
			if(targetCellInList && !sourceCellInList)
			{
				boolean isSourceCellShouldBeIn = isCellRemovable(sourceCell);
				if(isSourceCellShouldBeIn)
				{
					cellList.add(sourceCell);
				}
			}
			else if(sourceCellInList && !targetCellInList)
			{
				boolean isTargetCellShouldBeIn = isCellRemovable(targetCell);
				if(isTargetCellShouldBeIn)
				{
					cellList.add(targetCell);
				}
			}
			else if(!sourceCellInList && !targetCellInList)
			{//to reach this point, it implies linkEdge is in the list
				//so have to add target and source's cell into the list for completeness.
				boolean isSourceCellShouldBeIn = isCellRemovable(sourceCell);
				if (isSourceCellShouldBeIn)
				{
					cellList.add(sourceCell);
				}
				boolean isTargetCellShouldBeIn = isCellRemovable(targetCell);
				if (isTargetCellShouldBeIn)
				{
					cellList.add(targetCell);
				}
			}
		}
		return result;
//		if(sourceCell!=null && sourceCell.equals(cell))
//		{
//			return sourceCell;
//		}
//		else if(targetCell!=null && targetCell.equals(cell))
//		{
//			return targetCell;
//		}
//		else if(linkEdge!=null && linkEdge.equals(cell))
//		{
//			return linkEdge;
//		}
//		else
//		{
//			return null;
//		}
	}

	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof MappingViewCommonComponent)) return false;

		final MappingViewCommonComponent mappingViewCommonComponent = (MappingViewCommonComponent) o;

		if (linkEdge != null ? !linkEdge.equals(mappingViewCommonComponent.linkEdge) : mappingViewCommonComponent.linkEdge != null) return false;
		if (sourceCell != null ? !sourceCell.equals(mappingViewCommonComponent.sourceCell) : mappingViewCommonComponent.sourceCell != null) return false;
		if (sourceNode != null ? !sourceNode.equals(mappingViewCommonComponent.sourceNode) : mappingViewCommonComponent.sourceNode != null) return false;
		if (targetCell != null ? !targetCell.equals(mappingViewCommonComponent.targetCell) : mappingViewCommonComponent.targetCell != null) return false;
		if (targetNode != null ? !targetNode.equals(mappingViewCommonComponent.targetNode) : mappingViewCommonComponent.targetNode != null) return false;

		return true;
	}

	public int hashCode()
	{
		int result;
		result = (sourceNode != null ? sourceNode.hashCode() : 0);
		result = 29 * result + (targetNode != null ? targetNode.hashCode() : 0);
		result = 29 * result + (sourceCell != null ? sourceCell.hashCode() : 0);
		result = 29 * result + (targetCell != null ? targetCell.hashCode() : 0);
		result = 29 * result + (linkEdge != null ? linkEdge.hashCode() : 0);
		return result;
	}

	public int compareTo(Object o)
	{//implement Comparable interface
		return equals(o) ? 0 : 1;
	}

	/**
	 * Return this object's string representation
	 * @return this object's string representation
	 */
	public String toString()
	{//override the basic implementation to return nothing so that there is no link label displayed to save space.
		return "";
	}

	/**
	 * Overloaded function to provide caller an explicit way to display this object's string representation.
	 * @param explictlyCalled
	 * @return this object's string representation
	 */
	public String toString(boolean explictlyCalled)
	{
		String source = null;
		String target = null;
		if (sourceNode != null)
		{
			source = sourceNode.toString();
		}
		if (targetNode != null)
		{
			target = targetNode.toString();
		}
		return "(" + source + " - " + target + "): " + sourceCell + ", " + targetCell;// + ", " + linkEdge;
	}

	/**
	 * Return the title of this provider that may be used to distinguish from others.
	 * @return the title of this provider that may be used to distinguish from others.
	 */
	public String getTitle()
	{
		return "Link Properties";
	}

	/**
	 * This functions will return an array of PropertyDescriptor that would
	 * help reflection and/or introspection to figure out what information would be
	 * presented to the user.
	 */
	public PropertiesResult getPropertyDescriptors() throws Exception
	{
		Class beanClass = this.getClass();
		PropertyDescriptor sourceProp = new PropertyDescriptor("Source", beanClass, "getSourceNode", null);
		PropertyDescriptor sourceParent = new PropertyDescriptor("SourceParent", beanClass, "getSourceNodeParent", null);
		PropertyDescriptor targetProp = new PropertyDescriptor("Target", beanClass, "getTargetNode", null);
		PropertyDescriptor targetParent = new PropertyDescriptor("TargetParent", beanClass, "getTargetNodeParent", null);
		List<PropertyDescriptor> propList = new ArrayList<PropertyDescriptor>();
		propList.add(sourceProp);
		propList.add(sourceParent);
		propList.add(targetProp);
		propList.add(targetParent);
		PropertiesResult result = new PropertiesResult();
		result.addPropertyDescriptors(this, propList);
		return result;
	}
}
/**
 * HISTORY : $Log: not supported by cvs2svn $
 * HISTORY : Revision 1.1  2008/12/03 20:46:14  linc
 * HISTORY : UI update.
 * HISTORY :
 */
