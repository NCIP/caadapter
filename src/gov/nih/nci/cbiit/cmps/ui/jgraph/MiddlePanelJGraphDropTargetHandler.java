/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmps.ui.jgraph;

import gov.nih.nci.cbiit.cmps.ui.common.MappableNode;
import gov.nih.nci.cbiit.cmps.ui.common.TransferableNode;
import gov.nih.nci.cbiit.cmps.ui.tree.CmpsDropTargetAdapter;
import gov.nih.nci.cbiit.cmps.ui.tree.DropCompatibleComponent;

import java.awt.geom.Rectangle2D;

import org.jgraph.JGraph;
import org.jgraph.graph.*;

import javax.swing.tree.DefaultMutableTreeNode;


import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * The class defines a customized drop hanlder for middle panel.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2008-12-03 20:46:14 $
 *
 */
public class MiddlePanelJGraphDropTargetHandler implements DropCompatibleComponent
{
	//to indicate if current GUI status is in drag-and-drop
	//so as to let controller not update the table contents
	private boolean dragging = false;

	/**
	 * A Reference to the JGraph View it Supports
	 */
	protected JGraph mGraph;
	private MiddlePanelJGraphController mappingDataMananger;

	private boolean drawFeedback;

	private DataFlavor[] acceptableDropFlavors = TransferableNode.transferDataFlavors;
	private DataFlavor[] preferredLocalFlavors = {TransferableNode.LOCAL_NODE_FLAVOR};

	// drop variables
	protected int acceptableDropAction = DnDConstants.ACTION_MOVE; //DnDConstants.ACTION_COPY_OR_MOVE;
	protected DropTarget dropTarget;
	protected CmpsDropTargetAdapter dropTargetAdapter;

	public MiddlePanelJGraphDropTargetHandler(JGraph mGraph, MiddlePanelJGraphController mappingDataMananger)
	{
		this(mGraph, mappingDataMananger, DnDConstants.ACTION_MOVE);//DnDConstants.ACTION_MOVE,
	}

	public MiddlePanelJGraphDropTargetHandler(JGraph mGraph, MiddlePanelJGraphController mappingDataMananger, int acceptableDropAction) //int acceptableDragAction,
	{
		this.mGraph = mGraph;
		this.mappingDataMananger = mappingDataMananger;
//		this.acceptableDragAction = acceptableDragAction;
		this.acceptableDropAction = acceptableDropAction;
		initDragAndDrop();
	}

	public JGraph getGraph()
	{
		return mGraph;
	}

	/**
	 * set up the drag and drop listeners. This must be called
	 * after the constructor.
	 */
	protected void initDragAndDrop()
	{
//		// set up drag stuff
//		this.dragSource = DragSource.getDefaultDragSource();
//		this.dragGestureAdapter = new HL7SDKDragGestureAdapter(this);
//		this.dragSourceAdapter = new HL7SDKDragSourceAdapter(this);
//		// component, action, listener
//		this.dragSource.createDefaultDragGestureRecognizer(this.getGraph(), this.acceptableDragAction, this.dragGestureAdapter);

		//set up drop stuff
		this.dropTargetAdapter = new CmpsDropTargetAdapter(this,
				acceptableDropAction,
				acceptableDropFlavors,
				preferredLocalFlavors);

		// component, ops, listener, accepting
		this.dropTarget = new DropTarget(this.getGraph(),
				acceptableDropAction,
				this.dropTargetAdapter,
				true);
		this.dropTarget.setActive(true);
	}
	
	/**
	 * Called by the DropTargetAdapter in dragEnter, dragOver and
	 * dragActionChanged
	 */
	public void dragUnderFeedback(boolean ok, DropTargetDragEvent e)
	{
		Point2D dropLocation = e.getLocation();
		// Find Cell in Model Coordinates
		Object object = getGraph().getFirstCellForLocation(dropLocation.getX(), dropLocation.getY());
		if (object != null)
		{
//			if((object instanceof FunctionBoxDefaultPort)) // || (object instanceof FunctionBoxCell))
//			{//does not highlight edge
//				this.getGraph().setSelectionCell(object);
//			}
//			else if (object instanceof FunctionBoxCell)
//			{
//				Object nearestPort = this.findNearestFunctionPortOnFunctionBox(dropLocation);
//				if (nearestPort !=null)
//					this.getGraph().setSelectionCell(nearestPort);
//			}
		}
	}

	/**
	 * Called by the DropTargetAdapter in dragEnter, dragOver and
	 * dragActionChanged
	 */
	public boolean isDropOk(DropTargetDragEvent e)
	{
		/**
		 * Design Rationale:
		 * 1) If the about-to-be-dropped data is a function, return true;
		 * 2) If the aimed port has not been mapped, return true;
		 * Otherwise, return false;
		 */
		//check if the enclosed is a function
		Transferable trans = e.getTransferable();
		Object data = getTransferedData(trans);
//		if(isDataContainsTargetClassObject(data, FunctionMeta.class) || isDataContainsTargetClassObject(data, DefaultGraphCell.class))
//		{
//			return true;
//		}
        //if not, check other stuff.

		Point2D dropLocation = e.getLocation();
//				PortView result = graph.getPortViewAt(dropLocation.getX(), dropLocation.getY());
		// Find Cell in Model Coordinates
//		Object object = getGraph().getFirstCellForLocation(dropLocation.getX(), dropLocation.getY());
//		Log.logInfo(this, "isDropOK: the cell is of type: '" + (object==null? "null" : object.getClass().getName() + "-" + object.toString() + "'."));
		PortView portView = getGraph().getPortViewAt(dropLocation.getX(), dropLocation.getY());
//		Log.logInfo(this, "isDropOK: the portView is of type: '" + (portView == null ? "null" : portView.getClass().getName() + "-" + portView.toString() + "'."));

		//find the nearest port
		if (portView ==null)
		{
//			FunctionBoxDefaultPort nearPort = this.findNearestFunctionPortOnFunctionBox(dropLocation);
//			if (nearPort!=null)
//			{
//				Rectangle2D portBounds=getGraph().getCellBounds(nearPort);
//				portView=getGraph().getPortViewAt(portBounds.getX(), portBounds.getY());
//			}
		}
		if (portView != null)
		{
			Object portObj = portView.getCell();
//			if (portObj instanceof FunctionBoxDefaultPort)
//			{
//				FunctionBoxDefaultPort port = (FunctionBoxDefaultPort) portObj;
//				boolean result = !port.isMapped();
//				if(result)
//				{//further check if the input matches from source and output matches from target
//					List selectionList = ((TransferableNode) data).getSelectionList();
//					Object userSelection = null;
//					if(selectionList!=null && selectionList.size()>0)
//					{
//						userSelection = selectionList.get(0);
//					}
//					result = (userSelection instanceof DefaultSourceTreeNode && port.isInput()) || (userSelection instanceof DefaultTargetTreeNode && !port.isInput());
//				}
//				return result;
//			}
		}
		return false;
//		if (object instanceof FunctionBoxCell)
//		{//accept drop if it is on a functional box.
//			PortView portView = getGraph().getPortViewAt(dropLocation.getX(), dropLocation.getY());
//			if(portView!=null)
//			{
//				Object portObj = portView.getCell();
//				if(portObj instanceof FunctionBoxDefaultPort)
//				{
//					return true;
//				}
//			}
//			return false;
//		}
//		if (object instanceof FunctionBoxDefaultPort)
//		{//accept drop if it is a port of functional box.
//			Log.logInfo(this, "I am a port of type '" + object.getClass().getName() + "'");
//			if (((DefaultPort) object).getParent() instanceof FunctionBoxCell)
//			{
//				return true;
//			}
//			else
//			{
//				return false;
//			}
//		}
//		else
//		{
//			return false;
//		}
	}

	protected boolean isDataContainsTargetClassObject(Object data, Class targetClass)
	{
		if (data instanceof TransferableNode)
		{
			TransferableNode node = (TransferableNode) data;
			List selectionList = node.getSelectionList();
			if (selectionList != null && selectionList.size() > 0)
			{
				int size = selectionList.size();
				for(int i=0; i<size; i++)
				{
					Object selObj = selectionList.get(i);
					if (selObj!=null && targetClass.isAssignableFrom(selObj.getClass()))
					{
						return true;
					}
					if (selObj instanceof DefaultMutableTreeNode)
					{
						Object userObj = ((DefaultMutableTreeNode) selObj).getUserObject();
						if (userObj!=null && targetClass.isAssignableFrom(userObj.getClass()))
						{
							return true;
						}
					}
				}//end of for loop
			}
		}
		return false;
	}

    /**
	 * Obtain transfered data from the wrapping Transferable.
	 * @param trans
	 * @return the transfer data within the transferable
	 */
	protected Object getTransferedData(Transferable trans)
	{
		DataFlavor[] dataFlavors = trans.getTransferDataFlavors();
		Object data = null;
		for(int i=0; i<dataFlavors.length; i++)
		{
			try
			{
				data = trans.getTransferData(dataFlavors[i]);
			}
			catch(Throwable e)
			{//refresh and try next
				e.printStackTrace();
				data = null;
			}
		}
		return data;
	}

	/**
	 * Called by the DropTargetAdapter in dragExit and drop
	 */
	public void undoDragUnderFeedback()
	{
		this.getGraph().clearSelection();
		this.drawFeedback = false;
	}

	/**
	 * Called by the DropTargetAdapter in drop
	 * return true if add action succeeded
	 * otherwise return false
	 */
	public boolean setDropData(Object transferredData, DropTargetDropEvent e, DataFlavor chosen)
	{
		boolean isSuccess = false;
		Point2D dropLocation = e.getLocation();
		TransferableNode dragSourceObjectSelection = (TransferableNode) transferredData;

		if(isDataContainsTargetClassObject(transferredData, DefaultGraphCell.class))
		{//assume this is just graph cell movement within this application, not support graph cells from other application.
			isSuccess = processGraphCellsMovement(dragSourceObjectSelection, dropLocation);
		}
//		else if(isDataContainsTargetClassObject(transferredData, FunctionMeta.class))
//		{
//			java.util.List dragSourceObjectList = dragSourceObjectSelection.getSelectionList();
//			int size = dragSourceObjectList.size();
//			boolean errorFree = true;
//			for (int i = 0; i < size; i++)
//			{
//				Object sourceNode = dragSourceObjectList.get(i);
//				FunctionMeta userObject = null;
//				if(sourceNode instanceof DefaultMutableTreeNode)
//				{
//					userObject = (FunctionMeta)((DefaultMutableTreeNode)sourceNode).getUserObject();
//				}
//				else if(sourceNode instanceof FunctionMeta)
//				{
//					userObject = (FunctionMeta)sourceNode;
//				}
//				if (errorFree)
//				{
//					errorFree = mappingDataMananger.addFunction(userObject, dropLocation);
//				}
//				else
//				{
//					break;
//				}
//			}
//		}
		else
		{
			// Find Cell in Model Coordinates
//			DefaultGraphCell targetNode = (DefaultGraphCell) getGraph().getFirstCellForLocation(dropLocation.getX(), dropLocation.getY());
			PortView slctPortView=(PortView)getGraph().getPortViewAt(dropLocation.getX(), dropLocation.getY());
			if (slctPortView==null)
			{
//				FunctionBoxDefaultPort nearPort = this.findNearestFunctionPortOnFunctionBox(dropLocation);
//				if (nearPort!=null)
//				{
//					Rectangle2D portBounds=getGraph().getCellBounds(nearPort);
//					slctPortView=getGraph().getPortViewAt(portBounds.getX(), portBounds.getY());
//				}
			}
			DefaultPort targetNode = (DefaultPort) slctPortView.getCell();
//			Log.logInfo(this, "TargetNode of type: '" + (targetNode==null ? "null" : targetNode.toString() + "'"));
//			TransferableNode dragSourceObjectSelection = (TransferableNode) transferredData;
			java.util.List dragSourceObjectList = dragSourceObjectSelection.getSelectionList();
			if (dragSourceObjectList == null || dragSourceObjectList.size() < 1)
			{
	//				DesktopController.showMessage("No Selected Object is found in this Drop action.");
				return false;
			}
			int size = dragSourceObjectList.size();
			boolean errorFree = true;
			for (int i = 0; i < size; i++)
			{
				Object sourceNode = dragSourceObjectList.get(i);
				if (errorFree)
				{
					if (targetNode instanceof MappableNode && sourceNode instanceof MappableNode)
					{
						errorFree = mappingDataMananger.createMapping((MappableNode) sourceNode, (MappableNode) targetNode);
					}
					else
					{
						errorFree = false;
					}
				}
				else
				{
					break;
				}
			}
			isSuccess = errorFree;
		}//end of outer else
		return isSuccess;
	}

	/**
	 * Because when a drag is over a component, a selection listener would be notified
	 * as if there were an item being selected.
	 * These two function will allow DropTargetAdapter to notify the selection listener(s)
	 * of the drop target component if the drag comes or the actual selection occurs
	 */
	public void setInDragDropMode(boolean flag)
	{
		this.dragging = flag;
	}

	public boolean isInDragDropMode()
	{
		return dragging;
	}

	/**
	 * Return the drop target adapter.
	 *
	 * @return the drop target adapter.
	 */
	public CmpsDropTargetAdapter getDropTargetAdapter()
	{
		return this.dropTargetAdapter;
	}

	/**
	 * Called by the setDropData() method to solely handle graph move around.
	 * @param transferredData
	 * @param dropLocation
	 * @return true if it handles the request successfully.
	 */
	private boolean processGraphCellsMovement(TransferableNode transferredData, Point2D dropLocation)
	{
		boolean isSuccess = false;
		java.util.List dragSourceObjectList = transferredData.getSelectionList();
		Object[] cells = dragSourceObjectList.toArray(new Object[0]);
		boolean allInModel = isAllCellsInGraphModel(cells);
		CellHandle cellHandle = mGraph.getUI().getHandle();
		if(allInModel &&  cellHandle != null)
		{
			Point p = mGraph.getUI().getInsertionLocation();
//			Log.logInfo(this, "point from insertion location: '" + p + "'");
//			Log.logInfo(this, "point from drop location: '" + dropLocation + "'");
//			cellHandle.mouseReleased(new MouseEvent(mGraph, 0, 0, 0, (int)dropLocation.getX(), (int)dropLocation.getY(), 1, false));
			cellHandle.mouseReleased(new MouseEvent(mGraph, 0, 0, 0, (int)p.getX(), (int)p.getY(), 1, false));
			isSuccess = true;
		}
		return isSuccess;
	}

	private boolean isAllCellsInGraphModel(Object[] cells)
	{
		boolean allInModel = true;
		GraphModel model = mGraph.getModel();
		for (int i = 0; i < cells.length && allInModel; i++)
		{
			allInModel = allInModel && model.contains(cells[i]);
		}
		return allInModel;
	}
	
	/**
	 * Search the nearest port if Drop on a FunctionBox
	 * @param dropLocation
	 * @return
	 */
//	private FunctionBoxDefaultPort findNearestFunctionPortOnFunctionBox(Point2D dropLocation)
//	{
//		FunctionBoxDefaultPort rtnPort=null;
//		Object object = getGraph().getFirstCellForLocation(dropLocation.getX(), dropLocation.getY());
//		if (!(object instanceof FunctionBoxCell))
//			return rtnPort;
//
//		List<DefaultGraphCell> children=((FunctionBoxCell)object).getChildren();
//		int disDif=0;
//		for (DefaultGraphCell cell: children)
//		{
//			if (cell instanceof FunctionBoxDefaultPort)
//			{
//				FunctionBoxDefaultPort fPort=(FunctionBoxDefaultPort)cell;
//				//only the un-mapped port interested
//				if (fPort.isMapped())
//					continue;
//				Rectangle2D cellBounds=getGraph().getCellBounds(fPort);
//				//caculate the distance between the cellBounds center and the clicked point
//				double tempXDif=cellBounds.getCenterX()-dropLocation.getX();
//				double tempYDif=cellBounds.getCenterY()-dropLocation.getY();
//				int tempDis=(int)Math.sqrt(tempXDif*tempXDif+tempYDif*tempYDif);
//				//remember the first un-mapped port or the closer one
//				if (tempDis<disDif||disDif==0)
//				{
//					rtnPort=fPort;
//					disDif=tempDis;
//				}
//			}
//		}
//		return rtnPort;
//	}
}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.1  2008/10/30 16:02:14  linc
 * HISTORY: updated.
 * HISTORY:
 */
