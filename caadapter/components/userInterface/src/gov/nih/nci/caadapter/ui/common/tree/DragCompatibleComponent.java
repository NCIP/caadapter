/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/tree/DragCompatibleComponent.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $
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
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:14 $
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
