/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.ui.mapping.jgraph;

import gov.nih.nci.caadapter.common.function.FunctionConstant;
import gov.nih.nci.caadapter.hl7.map.FunctionVocabularyMapping;
import gov.nih.nci.caadapter.ui.common.functions.FunctionBoxCell;
import gov.nih.nci.caadapter.ui.common.functions.FunctionBoxMutableViewInterface;
import gov.nih.nci.caadapter.ui.common.jgraph.MiddlePanelJGraph;
import gov.nih.nci.caadapter.ui.mapping.jgraph.actions.*;

import org.jgraph.graph.*;

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
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.6 $
 *          date        $Date: 2008-11-21 16:18:58 $
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
	private AddFunctionalBoxAction addFunctionalBoxAction;
	private ConstantEditAction constantEditAction;
    private VacabularyMappingEditAction vacabularyMappingEditAction;
    private GraphDeleteAllAction deleteAllAction;
    
    private JPopupMenu popupMenu = null;

	public MiddlePanelMarqueeHandler(MiddlePanelJGraph graph, MiddlePanelJGraphController controller)
	{
		this.graph = graph;
		this.controller = controller;
		deleteAction = new GraphDeleteAction(controller);
		//register it in graph to map the "Delete" key stroke.
//			graph.registerKeyboardAction(deleteAction, deleteAction.getAcceleratorKey(), JComponent.WHEN_FOCUSED);
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
//		System.out.println("mousePressed().:(x="+e.getX()+",y="+e.getY()+")" );

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
//			System.out.println("delete action's mnemonic is: " + deleteAction.getMnemonic());
			JMenuItem menuItem = new JMenuItem(deleteAction);

			popupMenu.add(menuItem);
			popupMenu.addSeparator();

            // Insert Function
			addFunctionalBoxAction = new AddFunctionalBoxAction(controller.getMiddlePanel(), controller);
			menuItem = new JMenuItem(addFunctionalBoxAction);
			popupMenu.add(menuItem);

            //Edit constant
			constantEditAction = new ConstantEditAction(controller.getMiddlePanel(), controller);
			menuItem = new JMenuItem(constantEditAction);
			popupMenu.add(menuItem);

            //Edit constant
			vacabularyMappingEditAction = new VacabularyMappingEditAction(controller.getMiddlePanel(), controller);
			menuItem = new JMenuItem(vacabularyMappingEditAction);
			popupMenu.add(menuItem);

            //Delete All Action
            deleteAllAction = new GraphDeleteAllAction(controller.getMiddlePanel(), controller);
            menuItem = new JMenuItem(deleteAllAction);
            popupMenu.add(menuItem);

        }

		constantEditAction.setEnabled(false);
        vacabularyMappingEditAction.setEnabled(false);
        boolean graphHasSelection = !graph.isSelectionEmpty();
		if(graphHasSelection)
		{//context-sensitively enable or disable Constant Edit Action.
			Object selectedObj = graph.getSelectionCell();
			if(selectedObj instanceof FunctionBoxCell)
			{
				Object userObject = ((FunctionBoxCell) selectedObj).getUserObject();
				if(userObject instanceof FunctionBoxMutableViewInterface)
				{
					FunctionConstant constant = ((FunctionBoxMutableViewInterface)userObject).getFunctionConstant();
					if(constant!=null)
					{
						constantEditAction.setEnabled(true);
					}
                    FunctionVocabularyMapping vm = ((FunctionBoxMutableViewInterface)userObject).getFunctionVocabularyMapping();
					if(vm!=null)
					{
						vacabularyMappingEditAction.setEnabled(true);
					}
                }
			}
		}

		addFunctionalBoxAction.setEnabled(false);//.setEnabled(!graphHasSelection);
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
			if (portCell.getParent() instanceof FunctionBoxCell)
			{
//				System.out.println("port " + localPort + "'s parent is FunctionBoxCell. Will return true.");
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.5  2008/11/17 20:11:04  wangeug
 * HISTORY      : Move FunctionComponent and VocabularyMap from HL7 module to common module
 * HISTORY      :
 * HISTORY      : Revision 1.4  2008/11/10 21:30:52  wangeug
 * HISTORY      : GME release:version 4.2
 * HISTORY      :
 * HISTORY      : Revision 1.3  2008/06/09 19:54:06  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2008/03/04 16:09:23  schroedn
 * HISTORY      : Added deleteAll jGraph menu item
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:57  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.26  2006/09/06 18:24:44  umkis
 * HISTORY      : The new implement of Vocabulary Mapping function.
 * HISTORY      :
 * HISTORY      : Revision 1.25  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.24  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.23  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.22  2005/12/29 23:06:17  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.21  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.20  2005/12/14 21:37:19  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.19  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/11/17 16:11:14  umkis
 * HISTORY      : (defect# 196) clearing selection can be run when only left button click.
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/11/17 04:47:10  umkis
 * HISTORY      : (defect# 196) For selection clearing when mouse clicking(pressing) on any empty place of middle (JGraph) panel.
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/11/02 20:24:13  jiangsc
 * HISTORY      : Made add function context sensitive.
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/10/24 20:31:00  jiangsc
 * HISTORY      : Turned off auto-scroll feature to comprise mapping issue.
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/10/07 21:48:57  jiangsc
 * HISTORY      : Enabled Constant Edit capability
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/09/29 16:34:59  jiangsc
 * HISTORY      : Rename classes
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/08/24 22:28:41  jiangsc
 * HISTORY      : Enhanced JGraph implementation;
 * HISTORY      : Save point of CSV and HSM navigation update;
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/08/04 22:22:10  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 */
