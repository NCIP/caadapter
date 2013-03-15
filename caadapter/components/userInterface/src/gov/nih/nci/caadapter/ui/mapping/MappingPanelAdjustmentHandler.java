/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.mapping;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import javax.swing.JComponent;

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
