/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/tree/HL7SDKDragGestureAdapter.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $
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
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:14 $
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

	public HL7SDKDragGestureAdapter(DragCompatibleComponent c, Cursor cur)
	{
		this.dragComponent = c;
		this.cursor = cur;
	}

	public HL7SDKDragGestureAdapter(DragCompatibleComponent c, Cursor cur, Image image, Point p)
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
 * HISTORY      : $Log: not supported by cvs2svn $
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
