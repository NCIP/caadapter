/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.ui.common;

import javax.swing.Action;
import javax.swing.JToolBar;

/**
 * This class 
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-03 20:46:14 $
 *
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
 * HISTORY: $Log: not supported by cvs2svn $
 */
