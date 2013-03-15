/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.tree;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceListener;

/**
 * This interface defines functionality that should be implemented in the class that would
 * support drag function. These functions will give access to both DragGestureAdapter and
 * DragSourceAdapter for initiating and confirming drag action, updating status, and fetching
 * tranferable data, etc..
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:52 $
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
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/24 20:31:00  jiangsc
 * HISTORY      : Turned off auto-scroll feature to comprise mapping issue.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/22 20:53:14  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
