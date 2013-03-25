/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.ui.jgraph;


import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.Edge;

import gov.nih.nci.cbiit.cmts.ui.util.GeneralUtilities;

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines a custom model that does not allow Self-References and also remember additional
 * link data to support graph.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2008-12-03 20:46:14 $
 *
 */
public class MiddlePanelGraphModel extends DefaultGraphModel
{
	private List<String> reasonList;
	/**
	 * Override Superclass Method to provide additional checking.
	 */
	public boolean acceptsSource(Object edge, Object port)
	{
		reasonList = new ArrayList<String>();
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
		reasonList = new ArrayList<String>();
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
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.1  2008/10/27 20:06:30  linc
 * HISTORY: GUI first add.
 * HISTORY:
 */

