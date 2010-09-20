/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.ui.jgraph;

import org.jgraph.graph.EdgeRenderer;
import org.jgraph.graph.GraphConstants;
import org.jgraph.JGraph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;

/**
 * This class defines a customized edge renderer class to customized highlight (selected) edge rendering.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-10-27 20:06:30 $
 *
 */
public class DefaultEdgeRenderer extends EdgeRenderer
{

	/**
	 * Allow user to set customized hightlight color;
	 */
	protected Color highlightColorConfig;

	public DefaultEdgeRenderer()
	{
		super();
		this.highlightColorConfig = Color.BLUE;
	}

	public DefaultEdgeRenderer(Color highlightColor)
	{
		super();
		this.highlightColorConfig = highlightColor;
	}

	public Color getHighlightColor()
	{
		return highlightColorConfig;
	}

	public void setHighlightColor(Color highlightColor)
	{
		this.highlightColorConfig = highlightColor;
	}

	/**
	 * Paint the renderer.
	 */
	public void paint(Graphics g)
	{
		//changing colors and other attributes to what we want before the actual painting.
		Color oldHighlightColor = highlightColor;
		Color decidedHighlightColor = getDecidedHighlightColor();
		if(graph.get()!=null)
			((JGraph)graph.get()).setHighlightColor(decidedHighlightColor);
		Color oldGradientColor = null;
		if (getGradientColor() != null && !preview)
		{
			oldGradientColor = getGradientColor();
			setGradientColor(decidedHighlightColor);
		}

		float oldLineWidth = -1;
		if(selected)
		{
			oldLineWidth = lineWidth;
			lineWidth = 2;
		}

		super.paint(g);

		if (selected)
		{ // Paint Selected
			Graphics2D g2 = (Graphics2D) g;
			//			g2.setStroke(GraphConstants.SELECTION_STROKE);
			if(graph.get()!=null)
				g2.setColor(((JGraph)graph.get()).getHighlightColor());
			if (view.beginShape != null) g2.draw(view.beginShape);
			if (view.lineShape != null) g2.draw(view.lineShape);
			if (view.endShape != null) g2.draw(view.endShape);
		}

		if(graph.get()!=null)
			((JGraph)graph.get()).setHighlightColor(oldHighlightColor);
		
		if(oldGradientColor!=null)
		{
			setGradientColor(oldGradientColor);
		}
		if(oldLineWidth!=-1)
		{
			lineWidth = oldLineWidth;
		}
	}

	/**
	 * Provided for subclassers to paint a selection border.
	 */
	protected void paintSelectionBorder(Graphics g)
	{
		((Graphics2D) g).setStroke(GraphConstants.SELECTION_STROKE);
		if (childrenSelected)
		{
			if(graph.get()!=null)
				g.setColor(((JGraph)graph.get()).getGridColor());
		}
		else if (focus && selected)
		{
			if(graph.get()!=null)
				g.setColor(((JGraph)graph.get()).getLockedHandleColor());
		}
		else if (selected)
		{
			g.setColor(getDecidedHighlightColor());
		}
		if (childrenSelected || selected)
		{
			Dimension d = getSize();
			g.drawRect(0, 0, d.width-1, d.height-1);
		}
	}

	private Color getDecidedHighlightColor()
	{
		if(this.highlightColorConfig!=null)
		{
			return this.highlightColorConfig;
		}
		else
		{
			if(graph.get()!=null)
				return ((JGraph)graph.get()).getHighlightColor();
			else 
				return null;
		}
	}
}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 */


