/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.common.context;

/**
 * Maintians map for MenuActions
 * @author OWNER: $Author: phadkes $
 * @author LAST UPDATE $Author: phadkes $
 * @since      caAdapter  v4.2
 * @version    $Revision: 1.3 $
 * @date       $Date: 2008-09-24 17:46:09 $
*/
import javax.swing.Action;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MenuActionMaps {
	protected Map<String, Action> fileMenuActions;
	protected Map<String, Action> reportMenuActions;
	protected Map<String, Action> toolBarMenuActions;
	private String clientName;

	public MenuActionMaps(String svcName)
	{
		clientName=svcName;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public Map<String, Action> getFileMenuActions() {
		if (fileMenuActions==null)
			fileMenuActions=Collections.synchronizedMap(new HashMap<String, Action>());
		return fileMenuActions;
	}

	public Map<String, Action> getReportMenuActions() {
		if (reportMenuActions==null)
			reportMenuActions=Collections.synchronizedMap(new HashMap<String, Action>());

		return reportMenuActions;
	}

	public Map<String, Action> getToolBarMenuActions() {
		if (toolBarMenuActions==null)
			toolBarMenuActions=Collections.synchronizedMap(new HashMap<String, Action>());

		return toolBarMenuActions;
	}

	public Map getMenuActionWithMenuName(String menuName)
	{
		if (menuName==null|menuName.trim().equals(""))
			return null;
		Map rtnMap=null;
		if (menuName.equalsIgnoreCase(MenuConstants.FILE_MENU_NAME))
			rtnMap= getFileMenuActions();
		else if (menuName.equalsIgnoreCase(MenuConstants.REPORT_MENU_NAME))
			rtnMap= getReportMenuActions();
		else if (menuName.equalsIgnoreCase(MenuConstants.TOOLBAR_MENU_NAME))
			rtnMap= getToolBarMenuActions();
		return rtnMap;
	}

	public void addFileMenuAction(String actionName, Action newAction)
	{
		getFileMenuActions().put(actionName, newAction);
	}

	public void addReportMenuAction(String actionName, Action newAction)
	{
		getReportMenuActions().put(actionName, newAction);
	}
	public void addToolBarMenuAction(String actionName, Action newAction)
	{
		getToolBarMenuActions().put(actionName, newAction);
	}
}
/**
 * HISTORY $Log: not supported by cvs2svn $
*/