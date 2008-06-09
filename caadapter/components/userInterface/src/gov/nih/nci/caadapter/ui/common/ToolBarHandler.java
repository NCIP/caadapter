/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.ui.common;

import javax.swing.Action;
import javax.swing.JToolBar;

public interface ToolBarHandler {

	/**
	 * Add a given action to the toolbar, if the checkWithIcon is true,
	 * only add those actions with image icons.
	 * @param act
	 * @param checkWithIcon
	 */
	public abstract void addAction(Action act, boolean checkWithIcon);
	public abstract void removeAction(Action act);
	public abstract void removeAllActions();
	public abstract JToolBar getToolBar();
}