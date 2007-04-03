/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/jgraph/DefaultEdgeView.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $
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
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:14 $
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
