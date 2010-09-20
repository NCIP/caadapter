/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmts.ui.jgraph;

import org.jgraph.graph.*;

import java.awt.Color;
import java.awt.event.MouseEvent;


/**
 * This class defines a customized implementation of EdgeView to provide a customized Edge Handle
 * to work around an isssue on the default implemenation about edge that will determine whether edge
 * handle is moveable or editable or not, based on the attribute specified within cell's attribute,
 * so that user of this edge could fully control the configuration of the movability and editibility
 * via attribute map, which is the rest of the system does.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-10-27 20:06:30 $
 *
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
 * HISTORY: $Log: not supported by cvs2svn $
 */

