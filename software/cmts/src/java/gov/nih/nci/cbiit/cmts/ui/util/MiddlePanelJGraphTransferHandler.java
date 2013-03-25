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
package gov.nih.nci.cbiit.cmts.ui.util;

import org.jgraph.JGraph;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.GraphTransferHandler;

import gov.nih.nci.cbiit.cmts.ui.common.DataTransferActionType;

import javax.swing.JComponent;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is used to handle the transfer of a <code>Transferable</code>
 * to and from Swing components.  The <code>Transferable</code> is used to
 * represent data that is exchanged via a cut, copy, or paste
 * to/from a clipboard.  It is also used in drag-and-drop operations
 * to represent a drag from a component, and a drop to a component.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.3 $
 * @date       $Date: 2009-11-24 18:30:36 $
 *
 */
public class MiddlePanelJGraphTransferHandler extends GraphTransferHandler//TransferHandler
{
	protected DataFlavor[] acceptableFlavors = TransferableNode.transferDataFlavors;
	protected DataFlavor[] preferredLocalFlavors = {TransferableNode.LOCAL_NODE_FLAVOR};
	protected List acceptableFlavorsList = Arrays.asList(acceptableFlavors);
	protected List preferredLocalFlavorsList = Arrays.asList(preferredLocalFlavors);

	/**
	 * Indicates whether a component would accept an import of the given
	 * set of data flavors prior to actually attempting to import it.
	 *
	 * @param comp            the component to receive the transfer; this
	 *                        argument is provided to enable sharing of <code>TransferHandlers</code>
	 *                        by multiple components
	 * @param transferFlavors the data formats available
	 * @return true if the data can be inserted into the component, false otherwise
	 */
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors)
	{
//		System.out.println("MiddlePanelJGraphTransferHandler.canImport() is called.");
		for (int i = 0; i < transferFlavors.length; i++)
		{
			if (acceptableFlavorsList.contains(transferFlavors[i]))
			{
//				System.out.println("MiddlePanelJGraphTransferHandler.canImport() will return true.");
				return true;
			}
		}
//		System.out.println("MiddlePanelJGraphTransferHandler.canImport() will return false.");
		return false;
	}

	protected Transferable createTransferable(JComponent c)
	 {
		System.out.println("MiddlePanelJGraphTransferHandler.createTransferable() is called.");
		if (c instanceof JGraph)
		{
			JGraph graph = (JGraph) c;
			GraphModel model = graph.getModel();
			Object[] nodeArray = new Object[0];
			if (!graph.isSelectionEmpty())
			{
				nodeArray = graph.getDescendants(graph.order(graph
						.getSelectionCells()));
//				ParentMap pm = ParentMap.create(model, nodeArray, false, true);
//				ConnectionSet cs = ConnectionSet.create(model, nodeArray, false);
//				Map viewAttributes = GraphConstants.createAttributes(nodeArray,
//						graph.getGraphLayoutCache());
//				Rectangle2D bounds = graph.getCellBounds(graph
//						.getSelectionCells());
			}
//			Object[] nodeArray = graph.getSelectionCells();

			if (nodeArray == null || nodeArray.length < 1)
			{
				System.out.println("Transferable is null in " + getClass().getName() + ".DragStartData!");
				return null;
			}

			ArrayList selectionList = new ArrayList();
			for (int i = 0; i < nodeArray.length; i++)
			{
				Object dragSourceObject = nodeArray[i];
//				if(dragSourceObject instanceof FunctionBoxCell)
//				{
				selectionList.add(dragSourceObject);
//				}
			}
			DataTransferActionType actionType = DataTransferActionType.LINK;
			TransferableNode selection = new TransferableNode(selectionList, actionType, false);
			System.out.println("Will return selection '" + selection + "'.");
			return selection;
		}
		return null;
	}

	public int getSourceActions(JComponent c)
	{
		System.out.println("MiddlePanelJGraphTransferHandler.getSourceActions() is called.");
		return DnDConstants.ACTION_LINK;
	}

	/**
	 * Do nothing, drop will actually be handled separately by MiddlePanelJGraphDropTargetHandler.
	 * @param comp
	 * @param t
	 * @return if data is imported.
	 */
	public boolean importDataImpl(JComponent comp, Transferable t)
	{
		System.out.println("MiddlePanelJGraphTransferHandler.importData() is called.");
		return true;
	}


}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.2  2009/01/02 16:05:17  linc
 * HISTORY: updated.
 * HISTORY:
 * HISTORY: Revision 1.1  2008/10/30 16:02:14  linc
 * HISTORY: updated.
 * HISTORY:
 */
