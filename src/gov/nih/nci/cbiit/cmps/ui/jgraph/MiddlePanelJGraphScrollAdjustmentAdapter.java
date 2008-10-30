/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmps.ui.jgraph;


import gov.nih.nci.cbiit.cmps.ui.mapping.MappingMiddlePanel;

import javax.swing.JScrollPane;
import javax.swing.JScrollBar;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

/**
 * This class defines ...
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-10-30 16:02:13 $
 *
 */
public class MiddlePanelJGraphScrollAdjustmentAdapter implements AdjustmentListener
{
	private MappingMiddlePanel middlePanel;

	private boolean inScrollingMode = false;

	public boolean isInScrollingMode()
	{
		return inScrollingMode;
	}

	public MiddlePanelJGraphScrollAdjustmentAdapter(MappingMiddlePanel middlePanel)
	{
		this.middlePanel = middlePanel;
	}

	/**
	 * Invoked when the value of the adjustable has changed.
	 */
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
//		if(middlePanel!=null)
//		{
//			middlePanel.repaint();
//		}

		if(middlePanel.getAdjustmentCoordinator().isInScrollingMode())
		{//ignore if the other one is scrolling mode.
			return;
		}
		//start scrolling
		inScrollingMode = true;

		JScrollBar localScrollBar = (JScrollBar) e.getSource();
		int localMin = localScrollBar.getMinimum();
		int localMax = localScrollBar.getMaximum();
		int scrollValue = e.getValue();
		double ratio = ((double) scrollValue) / ((double) (localMax - localMin));
//		scrollAffectedScrollPane(middlePanel.getMappingPanel().getSourceScrollPane(), ratio);
//		scrollAffectedScrollPane(middlePanel.getMappingPanel().getTargetScrollPane(), ratio);

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
 */
