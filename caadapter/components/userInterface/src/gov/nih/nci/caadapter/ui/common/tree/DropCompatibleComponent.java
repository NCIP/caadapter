/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.tree;


import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;

/**
 * This interface defines functionality that should be implemented in the class that would
 * support drop function. These functions will give access to DropTargetAdapter for
 * confirming drop action, updating status, etc..
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:52 $
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
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/18 17:01:03  jiangsc
 * HISTORY      : Changed one function signature in DragDrop component;
 * HISTORY      : Enhanced drag-drop status monitoring in HL7MappingPanel;
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/22 20:53:15  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
