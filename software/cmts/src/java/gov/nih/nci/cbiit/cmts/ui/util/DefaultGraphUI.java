/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.ui.util;


import org.jgraph.graph.CellHandle;
import org.jgraph.graph.GraphContext;
import org.jgraph.plaf.basic.BasicGraphUI;

import javax.swing.TransferHandler;
import java.awt.Point;

import java.awt.dnd.DnDConstants;
import java.awt.event.MouseEvent;

/**
 * This class defines a custom implementation JGraphUI so as to provide a custom version of JGraphTransferHandler
 * and a custom Root Handle support the link drag-and-drop movement.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.3 $
 * @date       $Date: 2009-01-02 16:05:17 $
 *
 */
public class DefaultGraphUI extends BasicGraphUI
{

	/**
	 * Creates an instance of TransferHandler. Used for subclassers to provide
	 * different TransferHandler.
	 */
	protected TransferHandler createTransferHandler()
	{
		return new MiddlePanelJGraphTransferHandler();
	}

	/**
	 * Constructs the "root handle" for <code>context</code>.
	 * Override this function from base implemenation so as to provide customized implementation of "root handle"
	 *
	 * @param context reference to the context of the current selection.
	 */
	public CellHandle createHandle(GraphContext context)
	{
		if (context != null && !context.isEmpty() && graph.isEnabled())
		{
			try
			{
				return new DefaultGraphRootHandle(context);
			}
			catch (NullPointerException e)
			{
				e.printStackTrace();
				// ignore for now...
			}
		}
		return null;
	}

	/**
	 * Override the BasicGraphUI.RootHandle so as to provide customized DragAndDrop Action definition
	 */
	public class DefaultGraphRootHandle extends BasicGraphUI.RootHandle
	{
		/**
		 * Creates a root handle which contains handles for the given cells. The
		 * root handle and all its childs point to the specified JGraph
		 * instance. The root handle is responsible for dragging the selection.
		 */
		public DefaultGraphRootHandle(GraphContext ctx)
		{
			super(ctx);
		}

		/**
		 * The super class implementation defines using either COPY or MOVE, however for our application,
		 * we actually use link, so we have to override this method to support LINK action.
		 *
		 * @param event
		 */
		protected void startDragging(MouseEvent event)
		{
			TransferHandler th = graph.getTransferHandler();
			if(!pointWithinBound(event.getPoint()))
			{
				//set isInDragDropMode is true so that this function could be called to adjust its status.
				isDragging = true;
				if (graph.isDragEnabled())
				{
					//using bit or operation to include both COPY (or MOVE) and LINK so as to accommodate the move out of this graphic application to other applications.
					int transferHandlerSupportedActions = graph.getTransferHandler().getSourceActions(graph);
					int action = (event.isControlDown() && graph.isCloneable()) ? (DnDConstants.ACTION_COPY | transferHandlerSupportedActions) : (DnDConstants.ACTION_MOVE | transferHandlerSupportedActions);
					setInsertionLocation(event.getPoint());
					try
					{
						th.exportAsDrag(graph, event, action);
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
						// Ignore
					}
				}
			}
			else
			{//regular mouse move.
				//do nothing
			}
		}

		private boolean pointWithinBound(Point p)
		{
			boolean result = graph.contains(p);
			return result;
		}
	}
}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.2  2008/12/03 20:46:14  linc
 * HISTORY: UI update.
 * HISTORY:
 * HISTORY: Revision 1.1  2008/10/27 20:06:30  linc
 * HISTORY: GUI first add.
 * HISTORY:
 */

