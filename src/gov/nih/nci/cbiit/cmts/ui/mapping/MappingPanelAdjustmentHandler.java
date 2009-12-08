/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmps.ui.mapping;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import javax.swing.JComponent;

/**
 * This class defines ...
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2008-12-10 15:43:02 $
 *
 */
public class MappingPanelAdjustmentHandler implements AdjustmentListener {

	private ArrayList <JComponent> observingComponents =new ArrayList<JComponent>();
	/* 
	 * @see java.awt.event.AdjustmentListener#adjustmentValueChanged(java.awt.event.AdjustmentEvent)
	 */
	public void adjustmentValueChanged(AdjustmentEvent arg0) 
	{
		//System.out.println("enter MappingPanelAdjustmentHandler.adjustmentValueChanged:"+arg0);
		if (!arg0.getValueIsAdjusting())
			return;	
		for (JComponent obsrvComp: observingComponents)
			obsrvComp.repaint();
	}

	/**
	 * Add one component to observe the adjustment change
	 */
	public void addAdjustmentObserver(JComponent comp)
	{
		if (!observingComponents.contains(comp))
			observingComponents.add(comp);
	}
}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.1  2008/10/30 16:02:14  linc
 * HISTORY: updated.
 * HISTORY:
 */
