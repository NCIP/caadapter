/*L
 * Copyright SAIC.
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
package gov.nih.nci.cbiit.cmts.ui.util;


import gov.nih.nci.cbiit.cmts.ui.mapping.MappingMainPanel;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

/**
 * This class defines ...
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.3 $
 * @date       $Date: 2009-11-03 18:31:54 $
 *
 */
public class MiddlePanelJGraphScrollAdjustmentAdapter implements AdjustmentListener
{

	private boolean inScrollingMode = false;
	private MappingMainPanel mappingPanel;

	public boolean isInScrollingMode()
	{
		return inScrollingMode;
	}

	public MiddlePanelJGraphScrollAdjustmentAdapter(MappingMainPanel mappingPane)
	{
		mappingPanel = mappingPane;
	}

	/**
	 * Invoked when the value of the adjustable has changed.
	 */
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		System.out.println("enter MiddlePanelJGraphScrollAdjustmentAdapter.adjustmentValueChanged:"+e);
		if(mappingPanel.getMiddlePanel()!=null)
		{
			mappingPanel.getMiddlePanel().repaint();
		}
//
//		if(mappingPanel.getMiddlePanel().getAdjustmentCoordinator().isInScrollingMode())
//		{//ignore if the other one is scrolling mode.
//			return;
//		}
		//start scrolling
		inScrollingMode = true;

		JScrollBar localScrollBar = (JScrollBar) e.getSource();
		int localMin = localScrollBar.getMinimum();
		int localMax = localScrollBar.getMaximum();
		int scrollValue = e.getValue();
		double ratio = ((double) scrollValue) / ((double) (localMax - localMin));
		scrollAffectedScrollPane(mappingPanel.getSourceScrollPane(), ratio);
		scrollAffectedScrollPane(mappingPanel.getTargetScrollPane(), ratio);

		//end scrolling
		inScrollingMode = false;
	}

	private void scrollAffectedScrollPane(JScrollPane affectedScrollPane, double ratio)
	{
		if (affectedScrollPane == null)
		{
			return;
		}
		JScrollBar verticalBar = affectedScrollPane.getVerticalScrollBar();
		if (verticalBar == null || !verticalBar.isVisible())
		{
			return;
		}
		int localMin = verticalBar.getMinimum();
		int localMax = verticalBar.getMaximum();

		int scrollValue = (int) (((double) (localMax - localMin)) * ratio);
		//Log.logInfo(this, "MiddlePanelJGraphScrollAdjustmentAdapter: affected scroll min: '" + localMin + "',max='" + localMax + "',scrollValue='" + scrollValue + "'.");
		verticalBar.setValue(scrollValue);
	}

}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.2  2008/12/10 15:43:02  linc
 * HISTORY: Fixed component id generator and delete link.
 * HISTORY:
 * HISTORY: Revision 1.1  2008/10/30 16:02:13  linc
 * HISTORY: updated.
 * HISTORY:
 */
