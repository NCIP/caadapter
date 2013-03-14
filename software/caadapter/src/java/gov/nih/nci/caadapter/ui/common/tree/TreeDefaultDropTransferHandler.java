/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.common.tree;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.ui.common.TransferableNode;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;

/**
 * This class provides the default implementation of drop handling on tree.
 * Please override corresponding method to provide more complex handlings.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-10-09 18:19:43 $
 */
public class TreeDefaultDropTransferHandler implements DropCompatibleComponent
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create logging mechanism
	 * to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: TreeDefaultDropTransferHandler.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/tree/TreeDefaultDropTransferHandler.java,v 1.4 2008-10-09 18:19:43 wangeug Exp $";

	//to indicate if current GUI status is in drag-and-drop
	protected boolean dragging = false;

	/**
	 * A Reference to the Tree View it Supports
	 */
	protected JTree mTree;

	protected Color plafSelectionColor;
	protected boolean drawFeedback;

	protected DataFlavor[] acceptableDropFlavors = TransferableNode.transferDataFlavors;
	protected DataFlavor[] preferredLocalFlavors = {TransferableNode.LOCAL_NODE_FLAVOR};

	// drop variables
	protected int acceptableDropAction = DnDConstants.ACTION_MOVE; //DnDConstants.ACTION_COPY_OR_MOVE;
	protected DropTarget dropTarget;
	protected HL7SDKDropTargetAdapter dropTargetAdapter;

	public TreeDefaultDropTransferHandler(JTree tree, int action)
	{
		this.mTree = tree;
		this.acceptableDropAction = action;
		initDragAndDrop();
	}

	public JTree getTree()
	{
		return mTree;
	}

	/**
	 * set up the drag and drop listeners. This must be called
	 * after the constructor.
	 */
	protected void initDragAndDrop()
	{
		TreeCellRenderer cellRenderer = this.getTree().getCellRenderer();
		if (cellRenderer instanceof DefaultTreeCellRenderer)
		{
			DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) cellRenderer;
			this.plafSelectionColor = renderer.getBackgroundSelectionColor();
		}
		else
		{
			this.plafSelectionColor = Color.blue;
		}
		//set up drop stuff
		this.dropTargetAdapter = new HL7SDKDropTargetAdapter(this,
				acceptableDropAction,
				acceptableDropFlavors,
				preferredLocalFlavors);

		// component, ops, listener, accepting
		this.dropTarget = new DropTarget(this.getTree(),
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
		TreeCellRenderer cellRenderer = this.getTree().getCellRenderer();
		if (cellRenderer instanceof DefaultTreeCellRenderer)
		{
			DefaultTreeCellRenderer renderer =
					(DefaultTreeCellRenderer) cellRenderer;
			if (ok)
			{
				renderer.setBackgroundSelectionColor(this.plafSelectionColor);
				this.drawFeedback = true;
			}
			else
			{
				renderer.setBackgroundSelectionColor(Color.red);
			}
		}

//comments out so that when drag over a folder it will not expand
		Point p = e.getLocation();
		TreePath path = this.getTree().getPathForLocation(p.x, p.y);
		if (path != null)
		{
			this.getTree().setSelectionPath(path);
//	        if(this.getTree().isExpanded(path) == false)
//		    this.getTree().expandPath(path);
		}
	}

	/**
	 * Called by the DropTargetAdapter in dragEnter, dragOver and dragActionChanged
	 * descendant class shall override this function, since current implementation if the node is a leaf, the drop is NOT OK; otherwise, it is OK;
	 * @return if drop is OK.
	 */
	public boolean isDropOk(DropTargetDragEvent e)
	{
		TransferableNode transferableNode = obtainTransferableNode(e);
		if(transferableNode==null)
		{
			return false;
		}
		Point p = e.getLocation();
		TreePath path = this.getTree().getPathForLocation(p.x, p.y);
		if (path == null) return false;
		TreeNode node = (TreeNode) path.getLastPathComponent();
		TreeModel treeModel = this.getTree().getModel();

		/**
		 * could consider to simplify the logic to accept all node,
		 * using treeModel to verify if node is a leaf could allow parties to override the
		 * isLeaf() method in tree model to present different logic;
		 */
		if (node != null && !treeModel.isLeaf(node))
		{
			java.util.List transferredDataList = transferableNode.getSelectionList();
			boolean result = false;
			int size = transferredDataList.size();
			for (int i = 0; i < size; i++)
			{
				Object transferData = transferredDataList.get(i);
				/**
				 * if node is not null and it is not the top root, and it is a leaf and
				 * the transfer data is of type DefaultSourceTreeNode
				 */
				if ((node instanceof DefaultMutableTreeNode) && (transferData instanceof TreeNode))
				{
					if(node==((TreeNode)transferData).getParent())
					{//node is already the transferData's parent.
						result = false;
					}
					else
					{
						result = !((DefaultMutableTreeNode)node).isNodeAncestor((TreeNode) transferData);
					}
				}
				else
				{
					result = false;
					break;
				}
			}
			return result;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Called by the DropTargetAdapter in dragExit and drop
	 */
	public void undoDragUnderFeedback()
	{
		this.getTree().clearSelection();
		TreeCellRenderer cellRenderer = this.getTree().getCellRenderer();
		if (cellRenderer instanceof DefaultTreeCellRenderer)
		{
			DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) cellRenderer;
			renderer.setBackgroundSelectionColor(this.plafSelectionColor);
		}
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
		Point p = e.getLocation();
		TreePath path = this.getTree().getPathForLocation(p.x, p.y);
		if (path == null)
		{
			path = this.getTree().getClosestPathForLocation(p.x, p.y);
			if (path == null)
				return false;
		}

		DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) path.getLastPathComponent();
		try
		{
			TransferableNode dragSourceObjectSelection = (TransferableNode) transferredData;

			java.util.List dragSourceObjectList = dragSourceObjectSelection.getSelectionList();
			if (dragSourceObjectList == null || dragSourceObjectList.size() < 1)
			{
				return false;
			}

			int size = dragSourceObjectList.size();
			for (int i = 0; i < size; i++)
			{
				Object obj = dragSourceObjectList.get(i);
				if((obj instanceof MutableTreeNode) && !targetNode.isNodeAncestor((TreeNode) obj))
				{//obj is a Mutable tree node and it is not an ancestor of the targetNode.
					targetNode.add((MutableTreeNode)obj);
				}
				else
				{
					System.err.println("'" + obj + "' is not recognized by this handler. Please consider override setDropData() function to provide additional handling.");
				}
			}

			TreeModel treeModel = mTree.getModel();
			if(treeModel instanceof DefaultTreeModel)
			{
				((DefaultTreeModel) treeModel).nodeStructureChanged(targetNode);
			}
			isSuccess = true;
		}
		catch (Exception exp)
		{
			Log.logException(this, exp);
			JOptionPane.showMessageDialog(mTree.getRootPane().getParent(),
					exp.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			isSuccess = false;
		}

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
	 * @return the drop target adapter.
	 */
	public HL7SDKDropTargetAdapter getDropTargetAdapter()
	{
		return dropTargetAdapter;
	}

	protected TransferableNode obtainTransferableNode(DropTargetDragEvent e)
	{
		TransferableNode transferableNode = null;
		try
		{
			transferableNode = (TransferableNode) e.getTransferable().getTransferData(TransferableNode.LOCAL_NODE_FLAVOR);
		}
		catch (Exception e1)
		{
			Log.logException(this, e1);
		}
		if (transferableNode == null)
		{
			return null;
		}
		return transferableNode;
	}

	/**
	 * Check if given data contains the given class.
	 * @param data
	 * @param targetClass
	 * @return if given data contains the given class.
	 */
	protected boolean isDataContainsTargetClassObject(Object data, Class targetClass)
	{
		if (data instanceof TransferableNode)
		{
			TransferableNode node = (TransferableNode) data;
			java.util.List selectionList = node.getSelectionList();
			if (selectionList != null && selectionList.size() > 0)
			{
				int size = selectionList.size();
				for (int i = 0; i < size; i++)
				{
					Object selObj = selectionList.get(i);
					if (selObj != null && targetClass.isAssignableFrom(selObj.getClass()))
					{
						return true;
					}
					if (selObj instanceof DefaultMutableTreeNode)
					{
						Object userObj = ((DefaultMutableTreeNode) selObj).getUserObject();
						if (userObj != null && targetClass.isAssignableFrom(userObj.getClass()))
						{
							return true;
						}
					}
				}//end of for loop
			}
		}
		return false;
	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2008/06/09 19:53:52  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/12/03 17:46:12  wangeug
 * HISTORY      : clean codes
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.16  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/01/03 18:56:26  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/11/29 16:23:53  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/10/26 18:12:29  jiangsc
 * HISTORY      : replaced printStackTrace() to Log.logException
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/10/24 19:08:40  jiangsc
 * HISTORY      : Enhanced drag and drop
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/10/18 17:01:03  jiangsc
 * HISTORY      : Changed one function signature in DragDrop component;
 * HISTORY      : Enhanced drag-drop status monitoring in HL7MappingPanel;
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/24 22:28:43  jiangsc
 * HISTORY      : Enhanced JGraph implementation;
 * HISTORY      : Save point of CSV and HSM navigation update;
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/04 22:22:22  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/04 18:06:25  jiangsc
 * HISTORY      : Updated class description in comments
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/03 22:07:57  jiangsc
 * HISTORY      : Save Point.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/06/29 21:21:44  jiangsc
 * HISTORY      : More functionality.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/06/23 14:30:07  jiangsc
 * HISTORY      : Updated CSVPanel implementation to support basic drag and drop.
 * HISTORY      :
 */
