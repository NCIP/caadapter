/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */
package gov.nih.nci.caadapter.ui.common.tree;


import java.awt.*;
import java.awt.dnd.*;

/**
 * This Adapter class implements java.awt.dnd.DragSourceListener interface to provide
 * support for Drag action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:52 $
 */
public class HL7SDKDragSourceAdapter implements DragSourceListener
{
	protected DragCompatibleComponent dragComponent;
	protected boolean cursorWorkaround;
	protected Cursor currentCursor;
	protected Cursor copyDropCursor = DragSource.DefaultCopyDrop;
	protected Cursor copyNoDropCursor = DragSource.DefaultCopyNoDrop;
	protected Cursor moveDropCursor = DragSource.DefaultMoveDrop;
	protected Cursor moveNoDropCursor = DragSource.DefaultMoveNoDrop;
	protected Cursor linkDropCursor = DragSource.DefaultLinkDrop;
	protected Cursor linkNoDropCursor = DragSource.DefaultLinkNoDrop;

	public void setCopyDropCursor(Cursor c)
	{
		this.copyDropCursor = c;
	}

	public void setCopyNoDropCursor(Cursor c)
	{
		this.copyNoDropCursor = c;
	}

	public void setMoveDropCursor(Cursor c)
	{
		this.moveDropCursor = c;
	}

	public void setMoveNoDropCursor(Cursor c)
	{
		this.moveNoDropCursor = c;
	}

	//Constructor
	public HL7SDKDragSourceAdapter(DragCompatibleComponent c)
	{
		this.dragComponent = c;
	}

	public void setCursorWorkaround(boolean b)
	{
		this.cursorWorkaround = b;
	}

	/**
	 * @param e the event
	 */
	public void dragDropEnd(DragSourceDropEvent e)
	{
		dragComponent.setInDragDropMode(false);
		if (e.getDropSuccess() == false)
		{
			return;
		}
//		Log.logInfo(this, getClass().getName() + ".dragDropEnd() is called");
		/*
		* the dropAction should be what the drop target specified
		* in acceptDrop
		*/
		//Debug.println("DragSourceAdapter.dragDropEnd() is called.");
		int dragAction = this.dragComponent.getDragAction();

		// this is the action selected by the drop target
		if ((e.getDropAction() & DnDConstants.ACTION_MOVE) != 0 &&
				(dragAction & DnDConstants.ACTION_MOVE) != 0)
		{
			this.dragComponent.move();
		}
	}

	/*
	*
	*/
	void setDragOverFeedback(DragSourceDragEvent e)
	{
		DragSourceContext context = e.getDragSourceContext();
		/*
		if(debug) {
		Debug.println("In DragSourceAdapter: setDragOverFeedback.");
		Debug.println(context);
		}
		*/
		int dropOp = e.getDropAction();
		int targetAct = e.getTargetActions();
		int ra = dropOp & targetAct;
		Cursor c = null;

		if (ra == DnDConstants.ACTION_NONE)
		{ // no drop possible
			if ((dropOp & DnDConstants.ACTION_LINK) == DnDConstants.ACTION_LINK)
				c = linkNoDropCursor;
			else if ((dropOp & DnDConstants.ACTION_MOVE) == DnDConstants.ACTION_MOVE)
				c = moveNoDropCursor;
			else
				c = copyNoDropCursor;
		}
		else
		{ // drop possible
			if ((ra & DnDConstants.ACTION_LINK) == DnDConstants.ACTION_LINK)
			{
				c = linkDropCursor;
			}
			else if ((ra & DnDConstants.ACTION_MOVE) == DnDConstants.ACTION_MOVE)
			{
				c = moveDropCursor;
			}
			else
			{
				c = copyDropCursor;
			}
		}


		// there is a bug in DragSourceContext.
		// here is the work around: set the cursor to null first!
		if (cursorWorkaround)
		{
			context.setCursor(null);
		}
		this.currentCursor = c;
		context.setCursor(c);
	}

	/**
	 * @param e the event
	 */
	public void dragEnter(DragSourceDragEvent e)
	{
		dragComponent.setInDragDropMode(true);
		setDragOverFeedback(e);
	}

	/**
	 * @param e the event
	 */
	public void dragOver(DragSourceDragEvent e)
	{
		dragComponent.setInDragDropMode(true);
		setDragOverFeedback(e);
	}

	/**
	 * @param e the event
	 */
	public void dragExit(DragSourceEvent e)
	{
		//let the dragDropEnd() turn off the the mode indicator.
//		dragComponent.setInDragDropMode(false);
		DragSourceContext context = e.getDragSourceContext();
		if (cursorWorkaround)
		{
			context.setCursor(null);
		}

		this.currentCursor = this.copyNoDropCursor;
		context.setCursor(this.currentCursor);
	}

	/**
	 * for example, press shift during drag to change to
	 * a link action
	 *
	 * @param e the event
	 */
	public void dropActionChanged(DragSourceDragEvent e)
	{
		dragComponent.setInDragDropMode(true);
		setDragOverFeedback(e);
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
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
 * HISTORY      : Revision 1.6  2005/11/23 19:48:52  jiangsc
 * HISTORY      : Enhancement on mapping validations.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/24 20:31:00  jiangsc
 * HISTORY      : Turned off auto-scroll feature to comprise mapping issue.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/17 20:41:31  jiangsc
 * HISTORY      : Removed some comments
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/22 20:53:14  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
