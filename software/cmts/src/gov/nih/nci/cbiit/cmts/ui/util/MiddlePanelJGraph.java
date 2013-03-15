/*L
 * Copyright SAIC.
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


import gov.nih.nci.cbiit.cmts.ui.jgraph.MiddlePanelJGraphViewFactory;
import gov.nih.nci.cbiit.cmts.ui.jgraph.MiddlePanelMarqueeHandler;

import java.awt.Color;

import org.jgraph.JGraph;
import org.jgraph.graph.GraphModel;

/**
 * This class will handle JGraph specific rendering with some customized functions.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2008-12-03 20:46:14 $
 *
 */
public class MiddlePanelJGraph extends JGraph
{
	private static final String FUNCTION_MERGE = "Merge";
	private static final String FUNCTION_SPLIT = "Split";
	private static final String FUNCTION_CONNECT = "Connect";

	//location of port in cell
	private static final int PORT_LEFT = 0;
	private static final int PORT_RIGHT = 1;
	private static final int PORT_NORTH = 2;
	private static final int PORT_SOUTH = 3;
	private Color graphBackgroundColor = new Color(222, 238, 255);
	// Construct the Graph using the Model as its Data Source
	public MiddlePanelJGraph(GraphModel model)
	{
		super(model);
		// Make Ports Visible by Default
		setPortsVisible(true);
		// Use the Grid (but don't make it Visible)
		setGridEnabled(true);
		// Set the Grid Size to 10 Pixel
		setGridSize(6);
		// Set the Tolerance to 2 Pixel
		setTolerance(2);
		// Accept edits if click on background
		setInvokesStopCellEditing(true);
		// Allows control-drag
		setCloneable(false);
		// Jump to default port on connect
		setJumpToDefaultPort(false);
		setDoubleBuffered(true);
		setSizeable(true);
		MiddlePanelJGraphViewFactory	graphViewFactory = new MiddlePanelJGraphViewFactory();
		getGraphLayoutCache().setFactory(graphViewFactory);
		setDropEnabled(true);
		setDragEnabled(true);
		setEditable(false);
		setMoveable(true);
		MiddlePanelMarqueeHandler marqueeHandler = new MiddlePanelMarqueeHandler();//this);
		setMarqueeHandler(marqueeHandler);
		setBackground( graphBackgroundColor);
	}


//	public void paint(Graphics g)
//	{
////		Log.logInfo(this, "MiddlePanelJGraph paint() and watermark image is null? " + (waterMarkImage == null));
//		this.setOpaque(false);
//		Rectangle rect = this.getBounds();
////		g.drawImage(waterMarkImage, (int)rect.getMinX(), (int)rect.getMinY(), this);
//		int x, y;
//		int width, height;
//		Rectangle clip = g.getClipBounds();
//		width = waterMarkImage.getHeight(this);
//		height = waterMarkImage.getWidth(this);
//		if (width > 0 && height > 0)
//		{
//			for (x = clip.x; x < (clip.x + clip.width); x += width)
//			{
//				for (y = clip.y; y < (clip.y + clip.height); y += height)
//				{
//					g.drawImage(waterMarkImage, x, y, this);
////					Log.logInfo(this, x + "," + y);
//				}
//			}
//		}
//	}

//	public void paintComponent(Graphics g)
//	{
//		Log.logInfo(this, "MiddlePanelJGraph paintComponent() and watermark image is null? " + (waterMarkImage == null));
//		CellView[] cellViews = getGraphLayoutCache().getCellViews();
//		for(int i=0; i<cellViews.length; i++)
//		{
//			Component c = cellViews[i].getRendererComponent(this, false, false,
//					false);
//			if(c instanceof JComponent)
//			{
//				((JComponent)c).setOpaque(false);
//			}
//		}
//// First draw the background image - tiled
//		int x, y;
//		int width, height;
//		Rectangle clip = g.getClipBounds();
//		width = waterMarkImage.getHeight(this);
//		height = waterMarkImage.getWidth(this);
//
//		if (width > 0 && height > 0)
//		{
////			Log.logInfo(this, "hello");
//			for (x = clip.x; x < (clip.x + clip.width); x += width)
//			{
//				for (y = clip.y; y < (clip.y + clip.height); y += height)
//				{
//					g.drawImage(waterMarkImage, x, y, this);
////					Log.logInfo(this, x + "," + y);
//				}
//			}
//		}
////		Dimension d = getSize();
////		for (int x = 0; x < d.width; x += waterMarkImage.getWidth(this))
////		{
////			for (int y = 0; y < d.height; y += waterMarkImage.getHeight(this))
////			{
////				g.drawImage(waterMarkImage, 0, 0, null, this);
////			}
////		}
//		this.setOpaque(false);
//	}

//	// Override Superclass Method to Return Custom EdgeView
//	protected EdgeView createEdgeView(Object cell)
//	{
//		// Return Custom EdgeView
//		return new EdgeView(cell)
//		{
//			/**
//			 * Returns a cell handle for the view.
//			 */
//			public CellHandle getHandle(GraphContext context)
//			{
//				return new MyEdgeHandle(this, context);
//			}
//		};
//	}
//
//	//
//	// Custom Edge Handle
//	//
//
//	// Defines a EdgeHandle that uses the Shift-Button (Instead of the Right
//	// Mouse Button, which is Default) to add/remove point to/from an edge.
//	public static class MyEdgeHandle extends EdgeView.EdgeHandle
//	{
//
//		/**
//		 * @param edge
//		 * @param ctx
//		 */
//		public MyEdgeHandle(EdgeView edge, GraphContext ctx)
//		{
//			super(edge, ctx);
//		}
//
//		// Override Superclass Method
//		public boolean isAddPointEvent(MouseEvent event)
//		{
//			// Points are Added using Shift-Click
//			return event.isShiftDown();
//		}
//
//		// Override Superclass Method
//		public boolean isRemovePointEvent(MouseEvent event)
//		{
//			// Points are Removed using Shift-Click
//			return event.isShiftDown();
//		}
//
//	}

}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.1  2008/10/27 20:06:30  linc
 * HISTORY: GUI first add.
 * HISTORY:
 */

