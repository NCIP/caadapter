/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmps.ui.tree;


import java.awt.*;
import java.awt.dnd.*;

/**
 * This Adapter class implements java.awt.dnd.DragSourceListener interface to provide
 * support for Drag action.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-10-30 16:02:14 $
 */
public class CmpsDragSourceAdapter implements DragSourceListener
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
	public CmpsDragSourceAdapter(DragCompatibleComponent c)
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
 * HISTORY: $Log: not supported by cvs2svn $
 */
