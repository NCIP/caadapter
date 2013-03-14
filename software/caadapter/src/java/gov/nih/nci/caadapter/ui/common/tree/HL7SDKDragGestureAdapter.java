/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */
package gov.nih.nci.caadapter.ui.common.tree;


import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;

/**
 * This Adapter class implements java.awt.dnd.DragGestureListener interface to provide
 * support for initiation of Drag action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-10-09 18:17:52 $
 */
public class HL7SDKDragGestureAdapter
		implements DragGestureListener
{
	private DragCompatibleComponent dragComponent;
	private Cursor cursor = DragSource.DefaultCopyNoDrop;
	private Image dragImage;
	private Point point;

	public HL7SDKDragGestureAdapter(DragCompatibleComponent c)
	{
		this.dragComponent = c;
	}

	/**
	 * Start the drag if the operation is ok.
	 *
	 * @param e the event object
	 */
	public void dragGestureRecognized(DragGestureEvent e)
	{
		int userAcceptableDragAction = this.dragComponent.getDragAction();
		int dragAction = e.getDragAction();
//		Log.logInfo(this, "user acceptable drag action is '" + userAcceptableDragAction + "'");
//		Log.logInfo(this, "event drag action is '" + dragAction + "'");
		if ((userAcceptableDragAction & dragAction) == 0)
		{
			return;
		}

		if (this.dragComponent.isStartDragOk(e) == false)
		{
			return;
		}

		try
		{
			Transferable transferable = this.dragComponent.getDragStartData(e);
			DragSourceListener dsListener = this.dragComponent.getDragSourceListener();

			if (transferable == null)
				return;
//			Log.logInfo(this, "transferable " + transferable + " in HL7SDKDragGestureAdapter.");
			if (this.dragImage == null)
			{
				// initial cursor, transferrable, dsource listener
				e.startDrag(this.cursor,
						transferable,
						dsListener);
			}
			else
			{
				if (DragSource.isDragImageSupported())
					e.startDrag(this.cursor,
							this.dragImage,
							this.point,
							transferable,
							dsListener);
				else
					e.startDrag(this.cursor,
							transferable,
							dsListener);
			}

		}
		catch (InvalidDnDOperationException idoe)
		{
			System.err.println(idoe);
		}
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2008/06/09 19:53:52  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/24 20:31:00  jiangsc
 * HISTORY      : Turned off auto-scroll feature to comprise mapping issue.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/17 20:43:28  jiangsc
 * HISTORY      : Removed some comments
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/22 20:53:14  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
