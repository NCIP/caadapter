/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.jgraph;

import org.jgraph.graph.*;

import java.awt.Color;
import java.awt.event.MouseEvent;


/**
 * This class defines a customized implementation of EdgeView to provide a customized Edge Handle
 * to work around an isssue on the default implemenation about edge that will determine whether edge
 * handle is moveable or editable or not, based on the attribute specified within cell's attribute,
 * so that user of this edge could fully control the configuration of the movability and editibility
 * via attribute map, which is the rest of the system does.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public class DefaultEdgeView extends EdgeView
{
	/**
	 * Renderer for the class.
	 */
	public static transient EdgeRenderer my_renderer = new DefaultEdgeRenderer((new Color(238, 45, 83)));

	/**
	 * Constructs an empty edge view.
	 */
//	public DefaultEdgeView()
//	{
//		super();
//	}

	/**
	 * Constructs an edge view for the specified model object.
	 *
	 * @param cell reference to the model object
	 */
	public DefaultEdgeView(Object cell)
	{
		super(cell);
	}

	/**
	 * Returns the local renderer. Do not access the renderer field directly.
	 * Use this method instead!
	 */
	public EdgeRenderer getEdgeRenderer()
	{
		return (EdgeRenderer) getRenderer();
	}

	/**
	 * Returns a renderer for the class.
	 */
	public CellViewRenderer getRenderer()
	{
		return my_renderer;
	}

	/**
	 * Returns a cell handle for the view.
	 */
	public CellHandle getHandle(GraphContext context)
	{
		return new DefaultEdgeHandle(this, context);
	}

	/**
	 * Override to add validation upon movability of the underline edge cell.
	 */
	public static class DefaultEdgeHandle extends EdgeView.EdgeHandle
	{
		public DefaultEdgeHandle(EdgeView edge, GraphContext ctx)
		{
			super(edge, ctx);
		}

		protected boolean isEdgeMovable()
		{
			Edge edgeCell = (Edge) edge.getCell();
			return GraphConstants.isMoveable(edgeCell.getAttributes());
		}

		protected boolean isEdgeEditable()
		{
			Edge edgeCell = (Edge) edge.getCell();
			return GraphConstants.isEditable(edgeCell.getAttributes());
		}

		/**
		 * Invoked when the mouse pointer has been moved on a component (with no
		 * buttons down).
		 */
		public void mouseMoved(MouseEvent event)
		{
			if(isEdgeMovable())
			{
				super.mouseMoved(event);    //To change body of overridden methods use File | Settings | File Templates.
			}
			else
			{
				//do nothing
			}
		}

		// Handle mouse pressed event.
		public void mousePressed(MouseEvent event)
		{
			if(isEdgeEditable())
			{
				super.mousePressed(event);    //To change body of overridden methods use File | Settings | File Templates.
			}
			else
			{
				//do nothing;
			}
		}

		public void mouseDragged(MouseEvent event)
		{
			if(isEdgeMovable() && isEdgeEditable())
			{
				super.mouseDragged(event);    //To change body of overridden methods use File | Settings | File Templates.
			}
			else
			{
				//do nothing;
			}
		} // Handle mouse released event

		public void mouseReleased(MouseEvent e)
		{
			super.mouseReleased(e);    //To change body of overridden methods use File | Settings | File Templates.
		}
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/09/27 21:47:57  jiangsc
 * HISTORY      : Customized edge rendering and initially added a link highlighter class.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/25 15:17:07  jiangsc
 * HISTORY      : Added description.
 * HISTORY      :
 */
