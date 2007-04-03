/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/jgraph/MiddlePanelJGraph.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $
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


import org.jgraph.JGraph;
import org.jgraph.graph.GraphModel;
import java.awt.Rectangle;
import java.awt.Graphics;

/**
 * This class will handle JGraph specific rendering with some customized functions.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:14 $
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
