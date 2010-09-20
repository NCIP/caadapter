/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.ui.jgraph;


import gov.nih.nci.cbiit.cmts.ui.mapping.MappingMiddlePanel;

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
public class MiddlePanelGraphScrollAdjustmentHandler implements AdjustmentListener
{
	/**
	 * Invoked when the value of the adjustable has changed.
	 */
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		//start scrolling
		if (e.getSource() instanceof JScrollBar)
		{
			JScrollBar srcBar=(JScrollBar)e.getSource();
			JScrollPane jscroll=(JScrollPane)srcBar.getParent();
			if (jscroll.getParent() instanceof MappingMiddlePanel)
			{
				MappingMiddlePanel middlePanel=(MappingMiddlePanel)jscroll.getParent();
				middlePanel.renderInJGraph();
			}
		}
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
