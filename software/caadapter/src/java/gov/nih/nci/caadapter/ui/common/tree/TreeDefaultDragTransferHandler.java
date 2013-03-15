/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.tree;

import gov.nih.nci.caadapter.ui.common.DataTransferActionType;
import gov.nih.nci.caadapter.ui.common.TransferableNode;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceListener;
import java.util.ArrayList;

/**
 * Implement full drag and drop handling.
 * As of version 1.0, this class only handles drag from the tree.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version     Since caAdapter v1.2
 * revision    $Revision: 1.4 $
 * date        $Date: 2008-11-13 20:22:43 $
 */
public class TreeDefaultDragTransferHandler implements DragCompatibleComponent
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create logging mechanism
	 * to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: TreeDefaultDragTransferHandler.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/tree/TreeDefaultDragTransferHandler.java,v 1.4 2008-11-13 20:22:43 wangeug Exp $";

	private JTree mTree;
	private HL7SDKDragGestureAdapter dragGestureAdapter;
	private HL7SDKDragSourceAdapter dragSourceAdapter;
	private boolean inDragDropMode;

	//default setting
	private int acceptableDragAction = DnDConstants.ACTION_MOVE; //DnDConstants.ACTION_COPY_OR_MOVE;

	public TreeDefaultDragTransferHandler(JTree tree)
	{
		this(tree, DnDConstants.ACTION_MOVE);
	}

	public TreeDefaultDragTransferHandler(JTree tree, int dragAction)
	{
		this.mTree = tree;
		this.acceptableDragAction = dragAction;
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
		// set up drag stuff

		dragGestureAdapter = new HL7SDKDragGestureAdapter(this);
		dragSourceAdapter = new HL7SDKDragSourceAdapter(this);
		// component, action, listener
		DragSource dragSource= DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(this.getTree(), this.acceptableDragAction, this.dragGestureAdapter);
	}
	/**
	 * What are the drag actions supported by this component. Could be
	 * DnDConstants.ACTION_MOVE, DnDConstants.ACTION_COPY or
	 * DnDConstants.ACTION_COPY_OR_MOVE.  Called by the
	 * DragSourceAdapter in dragDropEnd and the DragGestureAdapter in
	 * dragGestureRecognized
	 */
	public int getDragAction()
	{
		return this.acceptableDragAction;
	}

	/**
	 * Called by the DragGestureAdapter in dragGestureRecognized
	 */
	public Transferable getDragStartData(DragGestureEvent e)
	{
		int dndAction = e.getDragAction();
		//ask table directly for selections instead of figuring it out from the DragGestureEvent instance to support possible multi-selection
		int[] rows = this.getTree().getSelectionRows();

		if (rows == null || rows.length < 1)
		{
//			System.out.println("Transferable is null in " + getClass().getName() + ".DragStartData!");
			return null;
		}

		ArrayList selectionList = new ArrayList();
		for (int i = 0; i < rows.length; i++)
		{
			if(rows[i]<0)
			{//will purposefully exclude any not-visible ones.
				continue;
			}
			TreePath path = this.getTree().getPathForRow(rows[i]);
			if (path == null)
			{//if any path is not found, return null;
				return null;
			}
			TreeNode dragSourceObject = (TreeNode) path.getLastPathComponent();
			selectionList.add(dragSourceObject);
		}
		DataTransferActionType actionType = DataTransferActionType.getDataTransferActionType(dndAction);
		TransferableNode selection = new TransferableNode(selectionList, actionType, false);
		return selection;
	}

	/**
	 * Called by the DragGestureAdapter in dragGestureRecognized. This
	 * is the listener that is registered with the drag source in the
	 * startDrag method.
	 *
	 * @return usually the DragSourceAdapter
	 */
	public DragSourceListener getDragSourceListener()
	{
		return this.dragSourceAdapter;
	}

	/**
	 * Is the location within the component draggable?  Called by the
	 * DragGestureAdapter in dragGestureRecognized. If false is
	 * returned then there will be no drag initiated.
	 */
	public boolean isStartDragOk(DragGestureEvent e)
	{
		Point p = e.getDragOrigin();
		TreePath treePath = this.getTree().getPathForLocation(p.x, p.y);
		if (treePath == null)
		{
//			System.out.println("Is not a correct selection...");
//			System.out.println("startDrag rejecting ");
			this.setInDragDropMode(false);
			return false;
		}

//        DeskObject obj = (DeskObject) treePath.getLastPathComponent();
//        if(obj intanceof Desktop) return false;

		if (this.getTree().isEditing())
		{
			this.getTree().stopEditing();
		}

		boolean returnValue = false;
		TreeNode node = (TreeNode) treePath.getLastPathComponent();
		//disable the drag of the null node or the root, etc.
		if (node == null || node.getParent()==null)
		{
			returnValue = false;
		}
		else
		{
			returnValue = true;
		}
		this.setInDragDropMode(returnValue);
		return returnValue;
	}

	/**
	 * Because when a drag is over a component, a selection listener would be notified
	 * as if there were an item being selected.
	 * These two function will allow DropTargetAdapter to notify the selection listener(s)
	 * of the drop target component if the drag comes or the actual selection occurs.
	 */
	public void setInDragDropMode(boolean flag)
	{
		inDragDropMode = flag;
	}

	/**
	 * Answers if current component is under dragging mode.
	 *
	 * @return if current component is under dragging mode.
	 */
	public boolean isInDragDropMode()
	{
		return inDragDropMode;
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.common.tree.DragCompatibleComponent#move()
	 */
	public void move() {
		// TODO Auto-generated method stub

	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2008/10/09 18:19:29  wangeug
 * HISTORY      : clean code/remove unnecessary constructor
 * HISTORY      :
 * HISTORY      : Revision 1.2  2008/06/09 19:53:52  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/11/29 16:23:53  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/11/11 19:23:59  jiangsc
 * HISTORY      : Support Pseudo Root in Mapping Panel.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/11/08 23:09:47  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/24 20:31:00  jiangsc
 * HISTORY      : Turned off auto-scroll feature to comprise mapping issue.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/25 20:13:13  jiangsc
 * HISTORY      : Enabled root mapping
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/04 22:22:22  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/06/23 14:30:07  jiangsc
 * HISTORY      : Updated CSVPanel implementation to support basic drag and drop.
 * HISTORY      :
 */
