/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.common;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

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