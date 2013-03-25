/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.common.jgraph;


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
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-10-09 18:14:05 $
 */
public class DefaultGraphUI extends BasicGraphUI
{

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
					int transferHandlerSupportedActions =  DnDConstants.ACTION_LINK;//graph.getTransferHandler().getSourceActions(graph);
					int action = (event.isControlDown() && graph.isCloneable()) ? (DnDConstants.ACTION_COPY | transferHandlerSupportedActions) : (DnDConstants.ACTION_MOVE | transferHandlerSupportedActions);
					setInsertionLocation(event.getPoint());
					try
					{
						th.exportAsDrag(graph, event, action);
					}
					catch (Exception ex)
					{
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
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2008/06/09 19:53:51  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/18 17:01:03  jiangsc
 * HISTORY      : Changed one function signature in DragDrop component;
 * HISTORY      : Enhanced drag-drop status monitoring in HL7MappingPanel;
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/25 15:17:07  jiangsc
 * HISTORY      : Added description.
 * HISTORY      :
 */
