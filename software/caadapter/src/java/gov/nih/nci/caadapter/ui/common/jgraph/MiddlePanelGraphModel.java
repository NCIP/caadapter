/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.jgraph;

import gov.nih.nci.caadapter.common.util.GeneralUtilities;

import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.Edge;

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines a custom model that does not allow Self-References and also remember additional
 * link data to support graph.
 *
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public class MiddlePanelGraphModel extends DefaultGraphModel
{
	private List reasonList;
	/**
	 * Override Superclass Method to provide additional checking.
	 */
	public boolean acceptsSource(Object edge, Object port)
	{
		reasonList = new ArrayList();
		Object target = ((Edge) edge).getTarget();
		boolean result = true;
		// Source only Valid if not Equal Target
		boolean lineResult = (target != port);
		if(!lineResult)
		{
			reasonList.add("The source cannot be same with the target.");
			result = lineResult;
		}
		if(target instanceof DefaultPort && port instanceof DefaultPort)
		{
			lineResult = !GeneralUtilities.areEqual(((DefaultPort)target).getParent(), ((DefaultPort) port).getParent());
			if(!lineResult)
			{
				reasonList.add("The source and target ports are originated from the same vertex.");
				result = lineResult;
			}
		}
		return result;
	}

	/**
	 * Override Superclass Method to provide additional checking.
	 */
	public boolean acceptsTarget(Object edge, Object port)
	{
		reasonList = new ArrayList();
		Object source = ((Edge) edge).getSource();
		boolean result = true;
		// Target only Valid if not Equal Source
		boolean lineResult = (source != port);
		if (!lineResult)
		{
			reasonList.add("The source cannot be same with the target.");
			result = lineResult;
		}
		if (source instanceof DefaultPort && port instanceof DefaultPort)
		{
			lineResult = !GeneralUtilities.areEqual(((DefaultPort) source).getParent(), ((DefaultPort) port).getParent());
			if (!lineResult)
			{
				reasonList.add("The source and target ports are originated from the same vertex.");
				result = lineResult;
			}
		}
		return result;
	}

	/**
	 * Return reason list.
	 * @return
	 */
	public List getNotAcceptableReasonList()
	{
		return reasonList;
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
 * HISTORY      : Revision 1.8  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 23:06:17  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/14 21:37:19  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/04 22:22:12  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 */
