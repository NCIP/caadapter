/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/tree/TreeDefaultDragTransferHandler.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
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
 * revision    $Revision: 1.1 $
 * date        $Date: 2007-04-03 16:17:14 $
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
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/tree/TreeDefaultDragTransferHandler.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $";

	private JTree mTree;
	private DragSource dragSource;
	private HL7SDKDragGestureAdapter dragGestureAdapter;
	private HL7SDKDragSourceAdapter dragSourceAdapter;
//	private Color plafSelectionColor;
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
//		TreeCellRenderer cellRenderer = this.getTree().getCellRenderer();
//		if (cellRenderer instanceof DefaultTreeCellRenderer)
//		{
//			DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) cellRenderer;
//			this.plafSelectionColor = renderer.getBackgroundSelectionColor();
//		}
//		else
//		{
//			this.plafSelectionColor = Color.blue;
//		}
		// set up drag stuff
		this.dragSource = DragSource.getDefaultDragSource();
		this.dragGestureAdapter = new HL7SDKDragGestureAdapter(this);
		this.dragSourceAdapter = new HL7SDKDragSourceAdapter(this);
		// component, action, listener
		this.dragSource.createDefaultDragGestureRecognizer(this.getTree(), this.acceptableDragAction, this.dragGestureAdapter);
	}

	/**
	 * Called by the DragSourceAdapter in dragDropEnd if the operation
	 * is DnDConstants.ACTION_MOVE
	 */
	public void move()
	{
		//do nothing intentionally as of this release. It falls to the
		//drop component to handle the real remove.
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

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
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
