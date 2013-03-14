/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
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
