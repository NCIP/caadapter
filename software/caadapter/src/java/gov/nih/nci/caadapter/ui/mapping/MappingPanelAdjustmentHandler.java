/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.ui.mapping;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import javax.swing.JComponent;
/**
 * The class defines the AjustmentHandler of mapping panel
 * source and target tree panel.
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-09-29 20:23:52 $
 */
public class MappingPanelAdjustmentHandler implements AdjustmentListener {

	private ArrayList <JComponent> observingComponents =new ArrayList<JComponent>();
	/* 
	 * @see java.awt.event.AdjustmentListener#adjustmentValueChanged(java.awt.event.AdjustmentEvent)
	 */
	public void adjustmentValueChanged(AdjustmentEvent arg0) 
	{
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
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2008/09/29 20:21:32  wangeug
 * HISTORY      : enforce code standard: license file, file description, changing history
 * HISTORY      :
 * 
 * **/