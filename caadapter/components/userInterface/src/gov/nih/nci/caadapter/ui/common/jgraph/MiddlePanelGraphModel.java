/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/jgraph/MiddlePanelGraphModel.java,v 1.1 2007-04-03 16:17:14 wangeug Exp $
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
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:14 $
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
