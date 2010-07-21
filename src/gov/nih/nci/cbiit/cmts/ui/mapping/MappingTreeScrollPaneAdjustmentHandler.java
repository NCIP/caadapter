/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.ui.mapping;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

/**
 * This class defines ...
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2008-12-10 15:43:02 $
 *
 */
public class MappingTreeScrollPaneAdjustmentHandler implements AdjustmentListener {

	/* 
	 * @see java.awt.event.AdjustmentListener#adjustmentValueChanged(java.awt.event.AdjustmentEvent)
	 */
	public void adjustmentValueChanged(AdjustmentEvent e) 
	{
		//start scrolling
		JScrollBar srcBar=(JScrollBar)e.getSource();
		JScrollPane jscroll=(JScrollPane)srcBar.getParent();
		MappingMainPanel mappingPanel=(MappingMainPanel)retrieveRootMappingPanel((JComponent)jscroll.getParent());
		if (mappingPanel==null)
			return;
		mappingPanel.getMiddlePanel().renderInJGraph();
	}

	private JComponent retrieveRootMappingPanel(JComponent childComp)
	{
		JComponent rtnComp=null;
		while (childComp.getParent()!=null)
		{
			rtnComp=(JComponent)childComp.getParent();
			if (rtnComp instanceof MappingMainPanel)
				return rtnComp;
			childComp=rtnComp;
		}
		return rtnComp;
	}

}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.1  2008/10/30 16:02:14  linc
 * HISTORY: updated.
 * HISTORY:
 */
