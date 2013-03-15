/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.jgraph;


import org.jgraph.JGraph;
import org.jgraph.graph.GraphModel;
import java.awt.Rectangle;
import java.awt.Graphics;

/**
 * This class will handle JGraph specific rendering with some customized functions.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public class MiddlePanelJGraph extends JGraph
{
//	private static final String FUNCTION_MERGE = "Merge";
//	private static final String FUNCTION_SPLIT = "Split";
//	private static final String FUNCTION_CONNECT = "Connect";

//	//location of port in cell
//	private static final int PORT_LEFT = 0;
//	private static final int PORT_RIGHT = 1;
//	private static final int PORT_NORTH = 2;
//	private static final int PORT_SOUTH = 3;

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
//		this.setOpaque(false);
	}

	/**
	 * Notification from the <code>UIManager</code> that the L&F has changed.
	 * Replaces the current UI object with the latest version from the
	 * <code>UIManager</code>. Subclassers can override this to support
	 * different GraphUIs.
	 *
	 * @see javax.swing.JComponent#updateUI
	 */
	public void updateUI()
	{
		setUI(new DefaultGraphUI());
//		Container rootPane = getRootPane();
//		if(rootPane!=null)
//		{
//			setBackground(rootPane.getBackground());
//			setForeground(rootPane.getForeground());
//		}
		invalidate();
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
