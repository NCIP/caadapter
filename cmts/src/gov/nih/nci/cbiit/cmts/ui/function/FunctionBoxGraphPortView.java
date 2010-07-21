/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */


package gov.nih.nci.cbiit.cmts.ui.function;

import org.jgraph.JGraph;
import org.jgraph.graph.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * This class defines a custom view class associated with the custom Port definition class.
 * @see FunctionBoxGraphPort
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-29 22:18:18 $
 */
public class FunctionBoxGraphPortView extends PortView
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: FunctionBoxDefaultPortView.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/cmts/src/gov/nih/nci/cbiit/cmts/ui/function/FunctionBoxDefaultPortView.java,v 1.1 2008-12-29 22:18:18 linc Exp $";

	/**
	 * Default size for all ports is 6.
	 */
	public static transient int MY_SIZE = 10;
	public static transient FunctionBoxDefaultPortRenderer renderer = new FunctionBoxDefaultPortRenderer();

	/**
	 * Constructs an empty portview.
	 */
	public FunctionBoxGraphPortView()
	{
		super();
	}

	/**
	 * Constructs a view that holds a reference to the specified cell, anchor
	 * and parent vertex.
	 *
	 * @param cell reference to the cell in the model
	 */
	public FunctionBoxGraphPortView(Object cell)
	{
		super(cell);
	}

	/**
	 * Returns the bounds for the port view.
	 */
	public Rectangle2D getBounds()
	{
		super.SIZE =MY_SIZE;
		return super.getBounds();
//		Rectangle2D bounds = getAttributes().createRect(getLocation(null));
//		if (bounds != null)
//			bounds.setFrame(bounds.getX() - SIZE / 2, bounds.getY() - SIZE / 2,
//					bounds.getWidth() + SIZE, bounds.getHeight() + SIZE);
//		return bounds;
	}

	/**
	 * Returns a renderer for the class.
	 */
	public CellViewRenderer getRenderer()
	{
		return FunctionBoxGraphPortView.renderer;    //To change body of overridden methods use File | Settings | File Templates.
	}
}

class FunctionBoxDefaultPortRenderer extends JComponent implements CellViewRenderer, Serializable
{
	/**
	 * Cache the current graph for drawing
	 */
	protected transient JGraph graph;

	/**
	 * Cache the current edgeview for drawing.
	 */
	protected transient PortView view;

	/**
	 * Cached hasFocus and selected value.
	 */
	protected transient boolean hasFocus, selected, preview;

	private transient Color defaultGradientColor = new Color(219, 219, 219);//new Color(255, 255, 255);
	private transient Color defaultSelectionBorderColor = new Color(219, 10, 10);//new Color(255, 255, 255);
    private transient Color defaultMappedFillColor = new Color(128, 128, 128);//new Color(81, 81, 255);


	/**
	 * Cached default foreground and default background.
	 */
	transient protected Color defaultForeground;
	transient protected Color defaultBackground;
	transient protected Color defaultBorderColor;

	transient protected Color bordercolor;

	transient protected Color gradientColor;

	/**
	 * Cached borderwidth.
	 */
	transient protected int borderWidth;

	public FunctionBoxDefaultPortRenderer()
	{
		defaultForeground = (UIManager.getColor("MenuItem.selectionBackground"));
		defaultBackground = (UIManager.getColor("Tree.selectionBorderColor"));
		defaultBorderColor = new Color(0, 0, 0);//(UIManager.getColor("Tree.selectionBorderColor"));
	}

	/**
	 * Configure and return the renderer based on the passed in
	 * components. The value is typically set from messaging the
	 * graph with <code>convertValueToString</code>.
	 * We recommend you check the value's class and throw an
	 * illegal argument exception if it's not correct.
	 *
	 * @param graph   the graph that that defines the rendering context.
	 * @param view    the view that should be rendered.
	 * @param sel     whether the object is selected.
	 * @param focus   whether the object has the focus.
	 * @param preview whether we are drawing a preview.
	 * @return	the component used to render the value.
	 */
	public Component getRendererComponent(JGraph graph, CellView view, boolean sel, boolean focus, boolean preview)
	{
		if (view instanceof PortView && graph != null)
		{
			this.graph = graph;
			this.view = (PortView) view;
			installAttributes(this.view);
			this.hasFocus = focus;
			this.selected = sel;
			this.preview = preview;
			return this;
		}
		return null;
	}

	public void paint(Graphics g)
	{
		g.setColor(graph.getBackground());
		g.setXORMode(graph.getBackground());
		super.paint(g);
		paintCustomizedPort(g);
		paintSelectionBorder(g);
	}


	private void paintCustomizedPort(Graphics g)
	{
		/**
		 * Design rationale:
		 * preview 	true: gradientColor;
		 * 			false: selected	true
		 */
		Color oldColor = g.getColor();
		Polygon triangle = null;

		boolean offset = (GraphConstants.getOffset(view.getAllAttributes()) != null);
		boolean mapped = isPortMapped();

		triangle = getTrianglePolygon(false, offset, preview);
		if(preview)
		{
			g.setColor(gradientColor.darker().darker());
			g.fillPolygon(triangle);
		}
		else if(offset)
		{
			g.setColor(getForeground());
			g.fillPolygon(triangle);
		}

		
			triangle = getTrianglePolygon(false, true, true);
			if (selected)
			{
				g.setColor(defaultSelectionBorderColor);
				g.fillPolygon(triangle);
			}
			else
			{
				if (mapped)
				{
					Color prevColor = g.getColor();
					g.setColor(defaultMappedFillColor);
					g.fillPolygon(triangle);
					//draw the border
					g.setColor(prevColor);
					g.drawPolygon(triangle);
				}
				else
				{
					g.setColor(getForeground());
					g.drawPolygon(triangle);
				}
			}
		//return the old settings
		g.setColor(oldColor);
	}

	/**
	 * Provided for subclassers to paint a selection border.
	 */
	protected void paintSelectionBorder(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		Stroke previousStroke = g2.getStroke();
		g2.setStroke(GraphConstants.SELECTION_STROKE);
		if (hasFocus && selected)
			g.setColor(graph.getLockedHandleColor());
		else if (selected)
			g.setColor(graph.getHighlightColor());
		boolean offset = (GraphConstants.getOffset(view.getAllAttributes()) != null);
		Polygon triangle = getTrianglePolygon(true, offset, preview);

		if (selected)
		{
			g.drawPolygon(triangle);
		}
		g2.setStroke(previousStroke);
	}

	private boolean isPortMapped()
	{
		boolean result = false;
		Object portObj = this.view.getCell();
		if(portObj instanceof DefaultPort)
		{
			DefaultPort port = (DefaultPort) portObj;
			Set edges = port.getEdges();
			if(edges!=null && !edges.isEmpty())
			{
				result = true;
			}
		}
		return result;
	}

	/**
	 * Install the attributes of specified cell in this renderer instance. This
	 * means, retrieve every published key from the cells hashtable and set
	 * global variables or superclass properties accordingly.
	 *
	 * @param view the cell view to retrieve the attribute values from.
	 */
	protected void installAttributes(CellView view)
	{
		Map map = view.getAllAttributes();
		Color foreground = GraphConstants.getForeground(map);
		setForeground((foreground != null) ? foreground : defaultForeground);
		Color background = GraphConstants.getBackground(map);
		setBackground((background != null) ? background : defaultBackground);
		Color gradientColor = GraphConstants.getGradientColor(map);
		setGradientColor((gradientColor==null) ? defaultGradientColor : gradientColor);
		Font font = GraphConstants.getFont(map);
		if(font!=null)
		{
			setFont(font);
		}
	}

	private Polygon getTrianglePolygon(boolean forBorder, boolean offset, boolean preview)
	{
		int b = 0;
		Dimension d = getSize();
//			Log.logInfo(this, "Default size is: '" + d + "'.");
		if (forBorder)
		{//may be changed to default border width
			b = 4;//borderWidth
		}
		Polygon result = null;
		// construct the triangle
		int start = 0;
		int width = d.width - b; // allow for border
		int height = d.height - b; // allow for border
		int halfHeight = height / 2;

		if (!offset)
		{
			start = 1;
			width -= 3;
			height -= 3;
		}
		if (!preview)
		{
			start = 1;
			width -= 4;
			height -= 4;
		}

		int[] xpoints = {start, width, start};
		int[] ypoints = {start, halfHeight, height};

		result = new Polygon(xpoints, ypoints, 3);
		return result;
	}

	public Color getGradientColor()
	{
		return gradientColor;
	}

	public void setGradientColor(Color gradientColor)
	{
		this.gradientColor = gradientColor;
	}

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 */
