/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.ui.common;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/* AbstractMenuBar (extends JMenuBar) for the UI
 * @author OWNER: $Author: phadkes $
 * @author LAST UPDATE $Author: phadkes $
 * @since      caAdapter  v4.2    
 * @version    $Revision: 1.4 $
 * @date       $Date: 2008-09-24 17:59:38 $
 */
public abstract class AbstractMenuBar extends JMenuBar {

	/**
	 * Enable the given action with the given value.
	 *
	 * @param actionConstant
	 *            the value defined in ActionConstants.
	 * @param value
	 *            either true or false.
	 */
	public abstract void enableAction(String actionConstant, boolean value);
	public abstract void enableCloseAllAction(boolean newValue);
	public abstract Action getDefinedAction(String actionConstant);
	public abstract JMenuItem getDefinedMenuItem(String actionConstant);
	public  abstract JMenu getDefinedMenu(String actionConstant);
	public abstract void resetMenus(boolean hasActiveDocument);
}


/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/