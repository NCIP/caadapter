/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.ui.jgraph;

import gov.nih.nci.cbiit.cmts.ui.function.FunctionBoxCell;
import gov.nih.nci.cbiit.cmts.ui.function.FunctionBoxDefaultPort;
import gov.nih.nci.cbiit.cmts.ui.function.FunctionBoxDefaultPortView;
import gov.nih.nci.cbiit.cmts.ui.function.FunctionBoxView;

import org.jgraph.graph.*;

/**
 * This class defines the customized main view factory to support JGraph based mapping tool.
 * 
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-10-30 16:02:14 $
 * 
 */
public class MiddlePanelJGraphViewFactory extends DefaultCellViewFactory
{
	/**
	 * Creates and returns a default <code>GraphView</code>.
	 * @return the default <code>GraphView</code>
	 */
	protected VertexView createVertexView(Object v)
	{
		if (v instanceof FunctionBoxCell)
		{		
			return new FunctionBoxView(v);
		}
		return super.createVertexView(v);
	}

	/**
	 * Constructs an EdgeView view for the specified object.
	 * @return a view class of given edge class.
	 */
	protected EdgeView createEdgeView(Object cell)
	{
		if(cell instanceof DefaultEdge)
		{
			return new DefaultEdgeView(cell);
		}
		return super.createEdgeView(cell);
	}

	protected PortView createPortView(Object port)
	{
		if(port instanceof FunctionBoxDefaultPort)
		{
			return new FunctionBoxDefaultPortView(port);
		}
		return super.createPortView(port);
	}
}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 */

