/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmps.ui.jgraph;

import org.jgraph.graph.*;

import gov.nih.nci.cbiit.cmps.ui.actions.GraphDeleteAction;
import gov.nih.nci.cbiit.cmps.ui.actions.GraphDeleteAllAction;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * This marquee handler overrides basic functionalities provided by basic Marquee handler
 * to plug in customized handlings of key and mouse driven events.
 * The MiddlePanelJGraphController class will deal with real implementation of some of those actions
 * and mainly focuses on drag-and-drop and handlings of repaint of graph, for example.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2008-12-10 15:43:02 $
 *
 */
public class MiddlePanelMarqueeHandler extends BasicMarqueeHandler
{
	private MiddlePanelJGraph graph;
	private MiddlePanelJGraphController controller;

	// Custom MarqueeHandler
	// MarqueeHandler that Connects Vertices and Displays PopupMenus

	//	// Holds the Start and the Current Point
	//	protected Point2D start, current;
	//
	// Holds the First and the Current Port
	protected PortView port, firstPort;

	private GraphDeleteAction deleteAction;
	//	private AddFunctionalBoxAction addFunctionalBoxAction;
	//	private ConstantEditAction constantEditAction;
	//    private VacabularyMappingEditAction vacabularyMappingEditAction;
	private GraphDeleteAllAction deleteAllAction;

	private JPopupMenu popupMenu = null;

	public MiddlePanelMarqueeHandler(MiddlePanelJGraph graph, MiddlePanelJGraphController controller)
	{
		this.graph = graph;
		this.controller = controller;
		deleteAction = new GraphDeleteAction(controller);
		//register it in graph to map the "Delete" key stroke.
		graph.registerKeyboardAction(deleteAction, deleteAction.getAcceleratorKey(), JComponent.WHEN_FOCUSED);
		this.graph.getInputMap().put(deleteAction.getAcceleratorKey(), deleteAction.getName());
		this.graph.getActionMap().put(deleteAction.getName(), deleteAction);
	}

	// Override to Gain Control (for PopupMenu and ConnectMode)
	public boolean isForceMarqueeEvent(MouseEvent e)
	{
		if (e.isShiftDown())
		{
			return false;
		}
		// If Right Mouse Button we want to Display the PopupMenu
		if (SwingUtilities.isRightMouseButton(e))
			// Return Immediately
			return true;
		// Find and Remember Port
		port = getSourcePortAt(e.getPoint());
		// If Port Found and in ConnectMode (=Ports Visible)
		if (port != null && graph.isPortsVisible())
		{
			return isValidPort(port);
		}//end if (port!=null ...
		else
		{
			return false;
		}
		// Else Call Superclass
		//		return super.isForceMarqueeEvent(e);
	}

	// Display PopupMenu or Remember Start Location and First Port
	public void mousePressed(final MouseEvent e)
	{
		System.out.println("mousePressed().:(x="+e.getX()+",y="+e.getY()+")" );

		// following if block was inserted by umkis   (defect# 196)
		// For selection clearing when mouse clicking(pressing) on any empty place of middle (JGraph) panel.
		if (SwingUtilities.isLeftMouseButton(e))
		{
			//System.out.println("mouse Left Pressed().");
			GraphSelectionModel gModel = graph.getSelectionModel();
			gModel.clearSelection();
		}

		currentPoint = e.getPoint();
		// If Right Mouse Button
		if (SwingUtilities.isRightMouseButton(e))
		{
			// Find Cell in Model Coordinates
			Object cell = graph.getFirstCellForLocation(e.getX(), e.getY());
			// Create PopupMenu for the Cell
			JPopupMenu menu = createPopupMenu(e.getPoint(), cell);
			// Display PopupMenu
			menu.show(graph, e.getX(), e.getY());
			// Else if in ConnectMode and Remembered Port is Valid
		}
		else if (port != null && graph.isPortsVisible())
		{
			// Remember Start Location
			startPoint = graph.toScreen(port.getLocation(null));
			// Remember First Port
			firstPort = port;
		}
		else
		{//do nothing
			// Call Superclass
			//			super.mousePressed(e);
		}
	}

	// Find Port under Mouse and Repaint Connector
	public void mouseDragged(MouseEvent e)
	{
		//		System.out.println("mouseDragged().:(x="+e.getX()+",y="+e.getY()+")" );
		// If remembered Start Point is Valid
		if (startPoint != null)
		{
			// Fetch Graphics from Graph
			Graphics g = graph.getGraphics();
			// Reset Remembered Port
			PortView newPort = getTargetPortAt(e.getPoint());
			// Do not flicker (repaint only on real changes)
			if (isValidPort(port) && isValidPort(newPort))//newPort == null || newPort != port)
			{//paint the port and connector if and only if it is valid one
				// Xor-Paint the old Connector (Hide old Connector)
				paintConnector(Color.black, graph.getBackground(), g);
				// If Port was found then Point to Port Location
				port = newPort;
				if (port != null)
					currentPoint = graph.toScreen(port.getLocation(null));
				// Else If no Port was found then Point to Mouse Location
				else
					currentPoint = graph.snap(e.getPoint());
				// Xor-Paint the new Connector
				paintConnector(graph.getBackground(), Color.black, g);
			}
		}
		else
		{//do nothing

		}
		// Call Superclass
		//		super.mouseDragged(e);
	}

	public PortView getSourcePortAt(Point2D point)
	{
		// Disable jumping
		graph.setJumpToDefaultPort(false);
		PortView result;
		try
		{
			// Find a Port View in Model Coordinates and Remember
			result = graph.getPortViewAt(point.getX(), point.getY());
		}
		finally
		{
			graph.setJumpToDefaultPort(true);
		}
		return result;
	}

	// Find a Cell at point and Return its first Port as a PortView
	protected PortView getTargetPortAt(Point2D point)
	{
		// Find a Port View in Model Coordinates and Remember
		return graph.getPortViewAt(point.getX(), point.getY());
	}

	// Connect the First Port and the Current Port in the Graph or Repaint
	public void mouseReleased(MouseEvent e)
	{
		//		System.out.println("mouseReleased(). :(x="+e.getX()+",y="+e.getY()+")" );
		// If Valid Event, Current and First Port
		if (e != null && port != null && firstPort != null
				&& firstPort != port)
		{
			if(isValidPort(port) && isValidPort(firstPort))
			{
				// Then Establish Connection
				controller.handleConnect((DefaultPort) firstPort.getCell(), (DefaultPort) port.getCell());
				e.consume();
			}
			// Else Repaint the Graph
			//			else
			//			{
			//				graph.repaint();
			//			}
		}
		else
		{
			graph.repaint();
		}
		// Reset Global Vars
		firstPort = port = null;
		startPoint = currentPoint = null;
		// Call Superclass
		super.mouseReleased(e);
	}

	// Show Special Cursor if Over Port
	public void mouseMoved(MouseEvent e)
	{
		//	System.out.println("mouseMoved().:(x="+e.getX()+",y="+e.getY()+")" );
		// Check Mode and Find Port
		if (e != null && getSourcePortAt(e.getPoint()) != null
				&& graph.isPortsVisible())
		{
			// Set Cusor on Graph (Automatically Reset)
			graph.setCursor(new Cursor(Cursor.HAND_CURSOR));
			// Consume Event
			// Note: This is to signal the BasicGraphUI's
			// MouseHandle to stop further event processing.
			e.consume();
		}
		else
		{//do nothing

		}
		// Call Superclass
		//			super.mouseMoved(e);
	}

	// Use Xor-Mode on Graphics to Paint Connector
	protected void paintConnector(Color fg, Color bg, Graphics g)
	{
		// Set Foreground
		g.setColor(fg);
		// Set Xor-Mode Color
		g.setXORMode(bg);
		// Highlight the Current Port
		paintPort(graph.getGraphics());
		// If Valid First Port, Start and Current Point
		if (firstPort != null && startPoint != null && currentPoint != null)
			// Then Draw A Line From Start to Current Point
			g.drawLine((int) startPoint.getX(), (int) startPoint.getY(),
					(int) currentPoint.getX(), (int) currentPoint.getY());
	}

	// Use the Preview Flag to Draw a Highlighted Port
	protected void paintPort(Graphics g)
	{
		// If Current Port is Valid
		if (port != null)
		{
			// If Not Floating Port...
			boolean o = (GraphConstants.getOffset(port.getAttributes()) != null);
			// ...Then use Parent's Bounds
			//			Rectangle2D r = (o) ? port.getBounds() : port.getParentView()
			//					.getBounds();
			Rectangle2D r = port.getBounds();
			// Scale from Model to Screen
			r = graph.toScreen((Rectangle2D) r.clone());
			// Add Space For the Highlight Border
			r.setFrame(r.getX() - 3, r.getY() - 3, r.getWidth() + 6, r
					.getHeight() + 6);
			// Paint Port in Preview (=Highlight) Mode
			graph.getUI().paintCell(g, port, r, true);
		}
	}

	//
	// PopupMenu
	//
	protected JPopupMenu createPopupMenu(final Point pt, final Object cell)
	{
		if(popupMenu==null)
		{
			popupMenu = new JPopupMenu();
			System.out.println("delete action's mnemonic is: " + deleteAction.getMnemonic());
			JMenuItem menuItem = new JMenuItem(deleteAction);

			popupMenu.add(menuItem);
			popupMenu.addSeparator();

//			// Insert Function
//			addFunctionalBoxAction = new AddFunctionalBoxAction(controller.getMiddlePanel(), controller);
//			menuItem = new JMenuItem(addFunctionalBoxAction);
//			popupMenu.add(menuItem);
//
//			//Edit constant
//			constantEditAction = new ConstantEditAction(controller.getMiddlePanel(), controller);
//			menuItem = new JMenuItem(constantEditAction);
//			popupMenu.add(menuItem);
//
//			//Edit constant
//			vacabularyMappingEditAction = new VacabularyMappingEditAction(controller.getMiddlePanel(), controller);
//			menuItem = new JMenuItem(vacabularyMappingEditAction);
//			popupMenu.add(menuItem);

			//Delete All Action
			deleteAllAction = new GraphDeleteAllAction(controller.getMiddlePanel(), controller);
			menuItem = new JMenuItem(deleteAllAction);
			popupMenu.add(menuItem);

		}

		//		constantEditAction.setEnabled(false);
		//        vacabularyMappingEditAction.setEnabled(false);
		boolean graphHasSelection = !graph.isSelectionEmpty();
		if(graphHasSelection)
		{//context-sensitively enable or disable Constant Edit Action.
			Object selectedObj = graph.getSelectionCell();
			//			if(selectedObj instanceof FunctionBoxCell)
			//			{
			//				Object userObject = ((FunctionBoxCell) selectedObj).getUserObject();
			//				if(userObject instanceof FunctionBoxMutableViewInterface)
			//				{
			//					FunctionConstant constant = ((FunctionBoxMutableViewInterface)userObject).getFunctionConstant();
			//					if(constant!=null)
			//					{
			//						constantEditAction.setEnabled(true);
			//					}
			//                    FunctionVocabularyMapping vm = ((FunctionBoxMutableViewInterface)userObject).getFunctionVocabularyMapping();
			//					if(vm!=null)
			//					{
			//						vacabularyMappingEditAction.setEnabled(true);
			//					}
			//                }
			//			}
		}

		//		addFunctionalBoxAction.setEnabled(!graphHasSelection);
				deleteAction.setEnabled(graphHasSelection);

		return popupMenu;
	}

	/**
	 * Return true if and only if the port is a child of FunctionBoxCell, future enhancement may be needed.
	 * @param localPort
	 * @return true if and only if the port is a child of FunctionBoxCell, future enhancement may be needed.
	 */
	private boolean isValidPort(PortView localPort)
	{
		if(localPort==null)
		{
			return false;
		}
		Object obj = localPort.getCell();
		if (obj instanceof DefaultPort)
		{
			DefaultPort portCell = (DefaultPort) obj;
			if(!portCell.getEdges().isEmpty())
			{//return false if the port has been linked by edge.
				return false;
			}
			//			if (portCell.getParent() instanceof FunctionBoxCell)
			//			{
			////				System.out.println("port " + localPort + "'s parent is FunctionBoxCell. Will return true.");
			//				return true;
			//			}
			//			else
			//			{
			//				return false;
			//			}
		}
		else
		{
			return false;
		}
		return false;
	}

}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.1  2008/10/30 16:02:14  linc
 * HISTORY: updated.
 * HISTORY:
 */
