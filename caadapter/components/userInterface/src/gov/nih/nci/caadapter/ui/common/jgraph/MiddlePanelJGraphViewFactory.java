/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.jgraph;

import gov.nih.nci.caadapter.ui.common.functions.FunctionBoxCell;
import gov.nih.nci.caadapter.ui.common.functions.FunctionBoxDefaultPort;
import gov.nih.nci.caadapter.ui.common.functions.FunctionBoxDefaultPortView;
import gov.nih.nci.caadapter.ui.common.functions.FunctionBoxView;

import org.jgraph.graph.*;

/**
 * This class defines the customized main view factory to support JGraph based mapping tool.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:51 $
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
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/29 23:06:17  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/14 21:37:19  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/04 22:22:11  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 */
