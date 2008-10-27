/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmps.ui.tree;


import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;

/**
 * This interface defines functionality that should be implemented in the class that would
 * support drop function. These functions will give access to DropTargetAdapter for
 * confirming drop action, updating status, etc..
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-10-27 20:06:30 $
 *
 */
public interface DropCompatibleComponent
{
	/**
	 * Called by the DropTargetAdapter in dragEnter, dragOver and
	 * dragActionChanged
	 */
	public void dragUnderFeedback(boolean ok, DropTargetDragEvent e);

	/**
	 * Called by the DropTargetAdapter in dragEnter, dragOver and
	 * dragActionChanged
	 */
	public boolean isDropOk(DropTargetDragEvent e);

	/**
	 * Called by the DropTargetAdapter in dragExit and drop
	 */
	public void undoDragUnderFeedback();

	/**
	 * Called by the DropTargetAdapter in drop
	 * return true if add action succeeded
	 * otherwise return false
	 */
	public boolean setDropData(Object transferredData, DropTargetDropEvent e, DataFlavor chosen);

	/**
	 * Because when a drag is over a component, a selection listener would be notified
	 * as if there were an item being selected.
	 * These two function will allow DropTargetAdapter to notify the selection listener(s)
	 * of the drop target component if the drag comes or the actual selection occurs.
	 */
	public void setInDragDropMode(boolean flag);

	/**
	 * Answers if current component is under dragging mode.
	 * @return if current component is under dragging mode.
	 */
	public boolean isInDragDropMode();

	/**
	 * Return the drop target adapter.
	 * @return the drop target adapter.
	 */
	public HL7SDKDropTargetAdapter getDropTargetAdapter();
}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 */

