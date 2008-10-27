/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmps.ui.tree;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceListener;

/**
 * This interface defines functionality that should be implemented in the class that would
 * support drag function. These functions will give access to both DragGestureAdapter and
 * DragSourceAdapter for initiating and confirming drag action, updating status, and fetching
 * tranferable data, etc..
 * 
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-10-27 20:06:30 $
 *
 */
public interface DragCompatibleComponent
{
	/**
	 * Called by the DragSourceAdapter in dragDropEnd if the operation
	 * is DnDConstants.ACTION_MOVE
	 */
	public void move();

	/**
	 * What are the drag actions supported by this component. Could be
	 * DnDConstants.ACTION_MOVE, DnDConstants.ACTION_COPY or
	 * DnDConstants.ACTION_COPY_OR_MOVE.  Called by the
	 * DragSourceAdapter in dragDropEnd and the DragGestureAdapter in
	 * dragGestureRecognized
	 */
	public int getDragAction();

	/**
	 * Called by the DragGestureAdapter in dragGestureRecognized
	 */
	public Transferable getDragStartData(DragGestureEvent e);

	/**
	 * Called by the DragGestureAdapter in dragGestureRecognized. This
	 * is the listener that is registered with the drag source in the
	 * startDrag method.
	 *
	 * @return usually the DragSourceAdapter
	 */
	public DragSourceListener getDragSourceListener();

	/**
	 * Is the location within the component draggable?  Called by the
	 * DragGestureAdapter in dragGestureRecognized. If false is
	 * returned then there will be no drag initiated.
	 */
	public boolean isStartDragOk(DragGestureEvent e);

	/**
	 * Because when a drag is over a component, a selection listener would be notified
	 * as if there were an item being selected.
	 * These two function will allow DropTargetAdapter to notify the selection listener(s)
	 * of the drop target component if the drag comes or the actual selection occurs.
	 */
	public void setInDragDropMode(boolean flag);

	/**
	 * Answers if current component is under dragging mode.
	 *
	 * @return if current component is under dragging mode.
	 */
	public boolean isInDragDropMode();

}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 */

