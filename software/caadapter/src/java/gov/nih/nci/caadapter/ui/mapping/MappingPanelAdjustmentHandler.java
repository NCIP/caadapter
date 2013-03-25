/*L
 * Copyright SAIC, SAIC-Frederick.
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