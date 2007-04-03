/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/jgraph/MiddlePanelScrollAdjustmentCoordinator.java,v 1.1 2007-04-03 16:17:57 wangeug Exp $
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


package gov.nih.nci.caadapter.ui.mapping.jgraph;

import gov.nih.nci.caadapter.ui.common.tree.MappingSourceTree;
import gov.nih.nci.caadapter.ui.common.tree.MappingTargetTree;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;

import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;

import java.awt.Container;
import java.awt.Component;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

/**
 * This class defines ...
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:57 $
 */
public class MiddlePanelScrollAdjustmentCoordinator implements AdjustmentListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: MiddlePanelScrollAdjustmentCoordinator.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/jgraph/MiddlePanelScrollAdjustmentCoordinator.java,v 1.1 2007-04-03 16:17:57 wangeug Exp $";

	public static final int FROM_SOURCE_TREE = 1;
	public static final int FROM_TARGET_TREE = 2;
	public static final int FROM_UNKNOWN_SOURCE = -1;

	private int scrollSource = FROM_UNKNOWN_SOURCE;

	private MappingMiddlePanel middlePanel;
	private JScrollPane affectedScrollPane;
	private boolean inScrollingMode = false;

	public MiddlePanelScrollAdjustmentCoordinator(MappingMiddlePanel middlePanel, JScrollPane affectedScrollPane)
	{
		this.middlePanel = middlePanel;
		this.affectedScrollPane = affectedScrollPane;
	}

	public int getScrollSource()
	{
		return scrollSource;
	}

	public void clearScrollSource()
	{
		this.scrollSource = FROM_UNKNOWN_SOURCE;
	}

	/**
	 * Invoked when the value of the adjustable has changed.
	 */
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		if(middlePanel.getGraphAdjustmentAdapter().isInScrollingMode())
		{//ignore if the other is scrolling
			return;
		}
		//start scrolling
		inScrollingMode = true;

		int componentSource = FROM_UNKNOWN_SOURCE;
		JScrollPane localScrollPane = null;
		JScrollBar localScrollBar = null;
		JTree treeComponent = null;
		Object obj = e.getSource();
//		System.out.println("source is of type: '" + (obj==null? "null" : obj.getClass().getName() + "'."));
		if(obj instanceof JScrollBar)
		{
			localScrollBar = (JScrollBar) obj;
			Container container = localScrollBar.getParent();
			if(container instanceof JScrollPane)
			{
				localScrollPane = (JScrollPane) container;
				Component comp = localScrollPane.getViewport().getView();
				if(comp instanceof MappingSourceTree)
				{
					componentSource = FROM_SOURCE_TREE;
					treeComponent = (MappingSourceTree) comp;
				}
				else if(comp instanceof MappingTargetTree)
				{
					componentSource = FROM_TARGET_TREE;
					treeComponent = (MappingTargetTree) comp;
				}
			}
		}
		scrollSource = componentSource;
		if(componentSource!=FROM_UNKNOWN_SOURCE)
		{
			int localMin = localScrollBar.getMinimum();
			int localMax = localScrollBar.getMaximum();
			int scrollValue = e.getValue();
			int heightHidden = (int) localScrollPane.getViewport().getViewPosition().getY();

			double ratio = ((double) scrollValue) / ((double) (localMax - localMin));
//			System.out.println("scroll min: '" + localMin + "',max='" + localMax + "',scrollValue='" + scrollValue + "'.");
//			System.out.println("ratio:'" + ratio + "'.");
//			System.out.println("hidden: '" + heightHidden + "'.");
			scrollAffectedScrollPane(ratio);
		}

		//end scrolling
		inScrollingMode = false;
	}

	private void scrollAffectedScrollPane(double ratio)
	{
		if(affectedScrollPane==null)
		{
			return;
		}
		JScrollBar verticalBar = affectedScrollPane.getVerticalScrollBar();
		if(verticalBar==null || !verticalBar.isVisible())
		{
			return;
		}
		int localMin = verticalBar.getMinimum();
		int localMax = verticalBar.getMaximum();

		int scrollValue = (int) (((double)(localMax - localMin)) * ratio);
//		System.out.println("MiddlePanelScrollAdjustmentCoordinator: affected scroll min: '" + localMin + "',max='" + localMax + "',scrollValue='" + scrollValue + "'.");
		verticalBar.setValue(scrollValue);
	}

	public boolean isInScrollingMode()
	{
		return inScrollingMode;
	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.9  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 23:06:17  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/12/14 21:37:19  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/10/20 20:31:49  jiangsc
 * HISTORY      : to Scroll consistently for source, target, and map panel on the HL7MappingPanel.
 * HISTORY      :
 */
