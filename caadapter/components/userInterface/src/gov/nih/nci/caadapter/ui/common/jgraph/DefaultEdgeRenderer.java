/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/jgraph/DefaultEdgeRenderer.java,v 1.2 2008-06-03 20:35:08 linc Exp $
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
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: linc $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-03 20:35:08 $
 */
public class DefaultEdgeRenderer extends EdgeRenderer
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: DefaultEdgeRenderer.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/jgraph/DefaultEdgeRenderer.java,v 1.2 2008-06-03 20:35:08 linc Exp $";

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
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/08/01 21:44:06  jiangsc
 * HISTORY      : rollback version
 * HISTORY      :
 * HISTORY      : Revision 1.6  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.5  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/09/27 21:47:57  jiangsc
 * HISTORY      : Customized edge rendering and initially added a link highlighter class.
 * HISTORY      :
 */

 
