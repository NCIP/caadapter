/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.jgraph;

import gov.nih.nci.caadapter.ui.common.DataTransferActionType;
import gov.nih.nci.caadapter.ui.common.TransferableNode;

import org.jgraph.JGraph;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.GraphTransferHandler;

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
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:51 $
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
//		System.out.println("MiddlePanelJGraphTransferHandler.canImport() will return true.");
		return false;
	}

	protected Transferable createTransferable(JComponent c)
	 {
//		System.out.println("MiddlePanelJGraphTransferHandler.createTransferable() is called.");
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
//				System.out.println("Transferable is null in " + getClass().getName() + ".DragStartData!");
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
//			System.out.println("Will return selection '" + selection + "'.");
			return selection;
		}
		return null;
	}

	public int getSourceActions(JComponent c)
	{
//		System.out.println("MiddlePanelJGraphTransferHandler.getSourceActions() is called.");
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
//		System.out.println("MiddlePanelJGraphTransferHandler.importData() is called.");
		return true;
	}


}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/12/29 23:06:17  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/14 21:37:19  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/08/24 22:28:41  jiangsc
 * HISTORY      : Enhanced JGraph implementation;
 * HISTORY      : Save point of CSV and HSM navigation update;
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/04 22:22:11  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 */
