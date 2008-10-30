/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmps.ui.tree;


import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;

/**
 * This Adapter class implements java.awt.dnd.DragGestureListener interface to provide
 * support for initiation of Drag action.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-10-30 16:02:14 $
 */
public class CmpsDragGestureAdapter
		implements DragGestureListener
{
	private DragCompatibleComponent dragComponent;
	private Cursor cursor = DragSource.DefaultCopyNoDrop;
	private Image dragImage;
	private Point point;

	public CmpsDragGestureAdapter(DragCompatibleComponent c)
	{
		this.dragComponent = c;
	}

	public CmpsDragGestureAdapter(DragCompatibleComponent c, Cursor cur)
	{
		this.dragComponent = c;
		this.cursor = cur;
	}

	public CmpsDragGestureAdapter(DragCompatibleComponent c, Cursor cur, Image image, Point p)
	{
		this.dragComponent = c;
		this.cursor = cur;
		this.dragImage = image;
		this.point = p;
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
 * HISTORY: $Log: not supported by cvs2svn $
 */
