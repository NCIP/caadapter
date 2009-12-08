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
/**
 * ToolbarHandler, adds, removes actions from toolbar.
 * @author OWNER: $Author: phadkes $
 * @author LAST UPDATE $Author: phadkes $
 * @since      caAdapter  v4.2    
 * @version    $Revision: 1.5 $
 * @date       $Date: 2008-09-24 17:59:38 $
*/

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


/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
