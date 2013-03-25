/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ui.common.jgraph;

import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.ui.common.MappableNode;
import gov.nih.nci.caadapter.ui.common.TransferableNode;
import gov.nih.nci.caadapter.ui.common.functions.FunctionBoxCell;
import gov.nih.nci.caadapter.ui.common.functions.FunctionBoxDefaultPort;
import gov.nih.nci.caadapter.ui.common.tree.DefaultMappableTreeNode;
import gov.nih.nci.caadapter.ui.common.tree.DefaultTargetTreeNode;
import gov.nih.nci.caadapter.ui.common.tree.DefaultSourceTreeNode;
import gov.nih.nci.caadapter.ui.common.tree.HL7SDKDropTargetAdapter;
import java.awt.geom.Rectangle2D;
import gov.nih.nci.caadapter.ui.common.tree.DropCompatibleComponent;
import gov.nih.nci.cbiit.cmps.core.ElementMeta;

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
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-10-09 18:24:23 $
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
	private MappingDataManager mappingDataMananger;

	private DataFlavor[] acceptableDropFlavors = TransferableNode.transferDataFlavors;
	private DataFlavor[] preferredLocalFlavors = {TransferableNode.LOCAL_NODE_FLAVOR};

	// drop variables
	protected int acceptableDropAction = DnDConstants.ACTION_MOVE; //DnDConstants.ACTION_COPY_OR_MOVE;
	protected DropTarget dropTarget;
	protected HL7SDKDropTargetAdapter dropTargetAdapter;

	public MiddlePanelJGraphDropTargetHandler(JGraph mGraph, MappingDataManager mappingDataMananger, int acceptableDropAction) //int acceptableDragAction,
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
		//set up drop stuff
		this.dropTargetAdapter = new HL7SDKDropTargetAdapter(this,
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
			if((object instanceof FunctionBoxDefaultPort)) // || (object instanceof FunctionBoxCell))
			{//does not highlight edge
				this.getGraph().setSelectionCell(object);
			}
			else if (object instanceof FunctionBoxCell)
			{
				Object nearestPort = this.findNearestFunctionPortOnFunctionBox(dropLocation);
				if (nearestPort !=null)
					this.getGraph().setSelectionCell(nearestPort);
			}
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
		if(isDataContainsTargetClassObject(data, FunctionMeta.class) || isDataContainsTargetClassObject(data, DefaultGraphCell.class)
				||isDataContainsTargetClassObject(data, ElementMeta.class))
		{
			return true;
		}
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
			FunctionBoxDefaultPort nearPort = this.findNearestFunctionPortOnFunctionBox(dropLocation);
			if (nearPort!=null)
			{
				Rectangle2D portBounds=getGraph().getCellBounds(nearPort);
				portView=getGraph().getPortViewAt(portBounds.getX(), portBounds.getY());
			}
		}
		if (portView != null)
		{
			Object portObj = portView.getCell();
			if (portObj instanceof FunctionBoxDefaultPort)
			{
				FunctionBoxDefaultPort port = (FunctionBoxDefaultPort) portObj;
				boolean result = !port.isMapped();
				if(result)
				{//further check if the input matches from source and output matches from target
					List selectionList = ((TransferableNode) data).getSelectionList();
					Object userSelection = null;
					if(selectionList!=null && selectionList.size()>0)
					{
						userSelection = selectionList.get(0);
					}
					result = (userSelection instanceof DefaultSourceTreeNode && port.isInput()) || (userSelection instanceof DefaultTargetTreeNode && !port.isInput());
				}
				return result;
			}
		}
		return false;
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
		else if(isDataContainsTargetClassObject(transferredData, FunctionMeta.class))
		{
			java.util.List dragSourceObjectList = dragSourceObjectSelection.getSelectionList();
			int size = dragSourceObjectList.size();
			boolean errorFree = true;
			for (int i = 0; i < size; i++)
			{
				Object sourceNode = dragSourceObjectList.get(i);
				FunctionMeta userObject = null;
				if(sourceNode instanceof DefaultMutableTreeNode)
				{
					userObject = (FunctionMeta)((DefaultMutableTreeNode)sourceNode).getUserObject();
				}
				else if(sourceNode instanceof FunctionMeta)
				{
					userObject = (FunctionMeta)sourceNode;
				}
				if (errorFree)
				{
					errorFree = mappingDataMananger.addFunction(userObject, dropLocation);
				}
				else
				{
					break;
				}
			}
		}
		else
		{
			// Find Cell in Model Coordinates
//			DefaultGraphCell targetNode = (DefaultGraphCell) getGraph().getFirstCellForLocation(dropLocation.getX(), dropLocation.getY());
			PortView slctPortView=(PortView)getGraph().getPortViewAt(dropLocation.getX(), dropLocation.getY());
			if (slctPortView==null)
			{
				FunctionBoxDefaultPort nearPort = this.findNearestFunctionPortOnFunctionBox(dropLocation);
				if (nearPort!=null)
				{
					Rectangle2D portBounds=getGraph().getCellBounds(nearPort);
					slctPortView=getGraph().getPortViewAt(portBounds.getX(), portBounds.getY());
				}
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
	public HL7SDKDropTargetAdapter getDropTargetAdapter()
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
	private FunctionBoxDefaultPort findNearestFunctionPortOnFunctionBox(Point2D dropLocation)
	{
		FunctionBoxDefaultPort rtnPort=null;
		Object object = getGraph().getFirstCellForLocation(dropLocation.getX(), dropLocation.getY());
		if (!(object instanceof FunctionBoxCell))
			return rtnPort;

		List<DefaultGraphCell> children=((FunctionBoxCell)object).getChildren();
		int disDif=0;
		for (DefaultGraphCell cell: children)
		{
			if (cell instanceof FunctionBoxDefaultPort)
			{
				FunctionBoxDefaultPort fPort=(FunctionBoxDefaultPort)cell;
				//only the un-mapped port interested
				if (fPort.isMapped())
					continue;
				Rectangle2D cellBounds=getGraph().getCellBounds(fPort);
				//caculate the distance between the cellBounds center and the clicked point
				double tempXDif=cellBounds.getCenterX()-dropLocation.getX();
				double tempYDif=cellBounds.getCenterY()-dropLocation.getY();
				int tempDis=(int)Math.sqrt(tempXDif*tempXDif+tempYDif*tempYDif);
				//remember the first un-mapped port or the closer one
				if (tempDis<disDif||disDif==0)
				{
					rtnPort=fPort;
					disDif=tempDis;
				}
			}
		}
		return rtnPort;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2008/06/09 19:53:51  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.21  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.20  2006/06/13 18:12:12  jiangsc
 * HISTORY      : Upgraded to catch Throwable instead of Exception.
 * HISTORY      :
 * HISTORY      : Revision 1.19  2006/01/26 22:53:15  jiangsc
 * HISTORY      : Fix drap and drop issue on mapping panel.
 * HISTORY      :
 * HISTORY      : Revision 1.18  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/12/29 23:06:17  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/12/23 16:37:59  jiangsc
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/12/14 21:37:19  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/11/11 19:23:59  jiangsc
 * HISTORY      : Support Pseudo Root in Mapping Panel.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/10/24 20:31:00  jiangsc
 * HISTORY      : Turned off auto-scroll feature to comprise mapping issue.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/10/18 17:01:03  jiangsc
 * HISTORY      : Changed one function signature in DragDrop component;
 * HISTORY      : Enhanced drag-drop status monitoring in HL7MappingPanel;
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/24 22:28:40  jiangsc
 * HISTORY      : Enhanced JGraph implementation;
 * HISTORY      : Save point of CSV and HSM navigation update;
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/04 22:22:11  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/07/19 22:28:16  jiangsc
 * HISTORY      : 1) Renamed FunctionalBox to FunctionBox to be consistent;
 * HISTORY      : 2) Added SwingWorker to OpenObjectToDbMapAction;
 * HISTORY      : 3) Save Point for Function Change.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/07/15 18:58:52  jiangsc
 * HISTORY      : 1) Reconstucted Menu bars;
 * HISTORY      : 2) Integrated FunctionPane to display property;
 * HISTORY      : 3) Enabled drag and drop functions to mapping panel.
 * HISTORY      :
 */
