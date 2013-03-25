/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.common.context;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.ui.common.ToolBarHandler;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.AbstractMenuBar;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Manage the context change effect, including notify menus to update.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.5 $
 *          date        $Date: 2009-02-03 15:49:21 $
 */
public class ContextManager implements ChangeListener//, PropertyChangeListener
{
    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: ContextManager.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/context/ContextManager.java,v 1.5 2009-02-03 15:49:21 wangeug Exp $";

    private AbstractMenuBar menu = null;
    private ToolBarHandler toolBarHandler = null;
    private AbstractMainFrame mainFrame = null;
    private JComponent currentPanel = null;
    private ContextFileManager contextFileManager = null;

    private boolean inClosingAllOrShutdownMode = false;
    private Map<String, MenuActionMaps> clientMenuActions=null;
    private static ContextManager mgrSingleton;

    /**
     * Private constructor works for singleton instance
     *
     */
    private ContextManager()
    {
    }

    public void setToolBarHandler(ToolBarHandler newToolBarHandler)
    {
    	toolBarHandler =newToolBarHandler;
    }

    /**
     * initialize the ContextManager only once with mainFrame
     * @param owner
     */
    public void initContextManager(AbstractMainFrame owner)
    {
    	mainFrame = owner;
    	contextFileManager = new ContextFileManager(mainFrame);
//    	menu = new MainMenuBar(mainFrame);
    	clientMenuActions = Collections.synchronizedMap(new HashMap<String, MenuActionMaps>());

		if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_HELP_MENU_ACTIVATED))
		{
			toolBarHandler.addAction(getDefinedAction(ActionConstants.HELP_TOPIC), true);
		}
    }

    public static ContextManager getContextManager()
    {
    	if (mgrSingleton==null)
    		mgrSingleton=new ContextManager();
    	return mgrSingleton;
    }

    public Map getClientMenuActions(String svcName, String menuName)
    {
    	MenuActionMaps menuMaps=(MenuActionMaps)clientMenuActions.get(svcName);
    	if (menuMaps!=null)
    		return menuMaps.getMenuActionWithMenuName(menuName);
    	else
    		return null;
    }

    public void addClientMenuAction(String svcName,String menuName, String actionName, Action action)
    {
    	MenuActionMaps menuMaps=(MenuActionMaps)clientMenuActions.get(svcName);
    	if (menuMaps==null)
    		menuMaps=new MenuActionMaps(svcName);

    	if (menuName.equalsIgnoreCase(MenuConstants.FILE_MENU_NAME))
    		menuMaps.addFileMenuAction(actionName, action);
		else if (menuName.equalsIgnoreCase(MenuConstants.REPORT_MENU_NAME))
			menuMaps.addReportMenuAction(actionName, action);
		else if (menuName.equalsIgnoreCase(MenuConstants.TOOLBAR_MENU_NAME))
			menuMaps.addToolBarMenuAction(actionName, action);
    	clientMenuActions.put(svcName, menuMaps);
    }

    public void removeClientMenuAction (String svcName, String menuName, String actionName)
    {
    	if (svcName==null||svcName.equals(""))
    		return;

    	//remove all action for the service
    	if (menuName==null||menuName.equals(""))
    		clientMenuActions.remove(svcName);

    	MenuActionMaps menuMaps=(MenuActionMaps)clientMenuActions.get(svcName);
    	if (menuMaps==null)
    		return;
    	if (menuName.equalsIgnoreCase(MenuConstants.FILE_MENU_NAME))
    		menuMaps.getFileMenuActions().remove(actionName);
		else if (menuName.equalsIgnoreCase(MenuConstants.REPORT_MENU_NAME))
			menuMaps.getReportMenuActions().remove(actionName);
		else if (menuName.equalsIgnoreCase(MenuConstants.TOOLBAR_MENU_NAME))
			menuMaps.getToolBarMenuActions().remove(actionName);
    	clientMenuActions.put(svcName, menuMaps);
    }
    public AbstractMenuBar getMenu()
    {
        return menu;
    }

    public void setMenu(AbstractMenuBar newMenu)
    {
    	menu=newMenu;
    }
    public ToolBarHandler getToolBarHandler()
    {
        return toolBarHandler;
    }

    public ContextFileManager getContextFileManager()
    {
        return contextFileManager;
    }

    /**
     * Return the currently active panel.
     *
     * @return the current panel.
     */
    public JComponent getCurrentPanel()
    {
        return currentPanel;
    }

    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param e a ChangeEvent object
     */
    public void stateChanged(ChangeEvent e)
    {
//		Log.logInfo(this, "State Changed...");
        if (isInClosingAllOrShutdownMode())
        {//ignore change if is about to closing all or shutting down.
            return;
        }
        int selectedIndex = mainFrame.getTabbedPane().getSelectedIndex();
        JComponent nowPanel = null;
        if (selectedIndex != -1)
        {
            nowPanel = (JComponent) mainFrame.getTabbedPane().getComponentAt(selectedIndex);
        }

        if (!GeneralUtilities.areEqual(nowPanel, currentPanel))
        {
            currentPanel = nowPanel;
            updateMenu();
        }

		/**
		 * Temporary solution
		 */
		if(nowPanel instanceof DefaultContextManagerClientPanel)
		{
			((DefaultContextManagerClientPanel)nowPanel).synchronizeRegisteredFile(true);

		}
	}

    public void notifySaveActionPerformedOnContextClient(ContextManagerClient client)
    {
        contextFileManager.notifyAffectContextMangerClients(client);
    }

    /**
     * We take pro-active approach to update the menu after user change tab view but
     * before the actual menu is selected.
     */
    private synchronized void updateMenu()
    {
        try
        {
            if(currentPanel instanceof ContextManagerClient)
            {
                ContextManagerClient contextClient = (ContextManagerClient) currentPanel;
                getToolBarHandler().removeAllActions();
                java.util.List<Action> actions = contextClient.getToolbarActionList();
                for(int i=0;i<actions.size();i++)
				{//includes open, save, close, validate, etc.
					Action action = actions.get(i);
					if(action!=null)
					{
						getToolBarHandler().addAction(action, true);
					}
				}

                JButton closeButton = null;
                Action action = contextClient.getDefaultCloseAction();
                if(action!=null)
                {
                	action.setEnabled(true);
                    closeButton = new JButton(action);
                    closeButton.setEnabled(true);
                    closeButton.setText("");
                }

                //update the toolbar
        		if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_HELP_MENU_ACTIVATED))
        		{
        			getToolBarHandler().addAction(getDefinedAction(ActionConstants.HELP_TOPIC), true);
        		}

                mainFrame.updateToolBar(toolBarHandler.getToolBar(), closeButton);

                //update the menus
                menu.resetMenus(true);
                Map actionMap = contextClient.getMenuItems(MenuConstants.FILE_MENU_NAME);
                updateMenu(actionMap, MenuConstants.FILE_MENU_NAME);
                actionMap = contextClient.getMenuItems(MenuConstants.REPORT_MENU_NAME);
                updateMenu(actionMap, MenuConstants.REPORT_MENU_NAME);
            }
            else
            {//we could possibly be here b/c user closed the last tab, roll back to menu's original state.
                getToolBarHandler().removeAllActions();

                if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_HELP_MENU_ACTIVATED))
        		{
        			getToolBarHandler().addAction(getMenu().getDefinedAction(ActionConstants.HELP_TOPIC), true); //.helpTopicAction
        		}

                mainFrame.updateToolBar(toolBarHandler.getToolBar());
                menu.resetMenus(false);
            }
            menu.repaint();
        }
        catch(Throwable t)
        {
            Log.logException(this, "Exception occurred in updateMenu()", t);
        }
    }
    private void updateMenu(Map actionMap, String menu_name)
    {
        if (menu_name == MenuConstants.FILE_MENU_NAME)
        {
            updateMenuAction(actionMap, ActionConstants.NEW_CSV_TO_HL7_MAP_FILE);
            updateMenuAction(actionMap, ActionConstants.OPEN_V2_TO_V3_MAP_FILE);
            updateMenuAction(actionMap, ActionConstants.NEW_CSV_SPEC);
            updateMenuAction(actionMap, ActionConstants.OPEN_CSV_SPEC);
            updateMenuAction(actionMap, ActionConstants.NEW_HSM_FILE);
            updateMenuAction(actionMap, ActionConstants.OPEN_HSM_FILE);
            updateMenuAction(actionMap, ActionConstants.NEW_CSV_TO_HL7_V3_MESSAGE);
            updateMenuAction(actionMap, ActionConstants.OPEN_HL7_V3_MESSAGE);

            updateMenuAction(actionMap, ActionConstants.SAVE);
            updateMenuAction(actionMap, ActionConstants.SAVE_AS);
            updateMenuAction(actionMap, ActionConstants.VALIDATE);
            updateMenuAction(actionMap, ActionConstants.ANOTATE);
            updateMenuAction(actionMap, ActionConstants.NEW_O2DB_MAP_FILE);
            boolean updated = updateMenuAction(actionMap, ActionConstants.CLOSE);
            if (updated)
            {
                menu.enableCloseAllAction(true);
            }
        }//end of if(menu_name==FILE_MENU_NAME)
        else if(menu_name == MenuConstants.REPORT_MENU_NAME)
        {
            if(actionMap==null || actionMap.isEmpty())
            {//remove menu and its items completely
                menu.getDefinedMenu(MenuConstants.REPORT_MENU_NAME).setEnabled(false);
            }
            else
            {
//                menu.reportMenu.setEnabled(true);
                menu.getDefinedMenu(MenuConstants.REPORT_MENU_NAME).setEnabled(true);
                updateMenuAction(actionMap, ActionConstants.GENERATE_REPORT);
            }
        }
    }

    private boolean updateMenuAction(Map actionMap, String actionConstant)
    {
    	JMenuItem menuItem=this.getMenu().getDefinedMenuItem(actionConstant);
    	if (menuItem==null)
    		return false;
        Action act = (Action) actionMap.get(actionConstant);

        if (act != null)
        {
            menuItem.setAction(null);
            menuItem.setAction(act);
            menuItem.setEnabled(true);
            return true;
        }
        else
        {
            return false;
        }
    }

//    private void updateMenu_save(Map actionMap, String menu_name)
//    {
//        if (menu_name == MenuConstants.FILE_MENU_NAME)
//        {
//            updateMenuAction(actionMap, ActionConstants.NEW_MAP_FILE, menu.newMapFileItem);
//            updateMenuAction(actionMap, ActionConstants.OPEN_MAP_FILE, menu.openMapFileItem);
//            updateMenuAction(actionMap, ActionConstants.NEW_CSV_SPEC, menu.newCSVSpecificationItem);
//            updateMenuAction(actionMap, ActionConstants.OPEN_CSV_SPEC, menu.openCSVSpecificationItem);
//            updateMenuAction(actionMap, ActionConstants.NEW_HSM_FILE, menu.newHSMFileItem);
//            updateMenuAction(actionMap, ActionConstants.OPEN_HSM_FILE, menu.openHSMFileItem);
//            updateMenuAction(actionMap, ActionConstants.NEW_HL7_V3_MESSAGE, menu.newHL7V3MessageItem);
//            updateMenuAction(actionMap, ActionConstants.OPEN_HL7_V3_MESSAGE, menu.openMapFileItem);
//
//            updateMenuAction(actionMap, ActionConstants.SAVE, menu.saveMenuItem);
//            updateMenuAction(actionMap, ActionConstants.SAVE_AS, menu.saveAsMenuItem);
//            updateMenuAction(actionMap, ActionConstants.VALIDATE, menu.validateMenuItem);
//            updateMenuAction(actionMap, ActionConstants.ANOTATE, menu.anotateMenuItem);
//            updateMenuAction(actionMap, ActionConstants.NEW_O2DB_MAP_FILE, menu.newO2DBMapFileItem);
//            boolean updated = updateMenuAction(actionMap, ActionConstants.CLOSE, menu.closeMenuItem);
//            if (updated)
//            {
//                menu.enableCloseAllAction(true);
//            }
//        }//end of if(menu_name==FILE_MENU_NAME)
//        else if(menu_name == MenuConstants.REPORT_MENU_NAME)
//        {
//            if(actionMap==null || actionMap.isEmpty())
//            {//remove menu and its items completely
//                menu.reportMenu.setEnabled(false);
//            }
//            else
//            {
//                menu.reportMenu.setEnabled(true);
//                updateMenuAction(actionMap, ActionConstants.GENERATE_REPORT, menu.generateReportMenuItem);
//            }
//        }
//    }

//    private boolean updateMenuAction(Map actionMap, String actionConstant, JMenuItem menuItem)
//    {
//        Action act = (Action) actionMap.get(actionConstant);
//        if (act != null)
//        {
//            menuItem.setAction(null);
//            menuItem.setAction(act);
//            return true;
//        }
//        else
//        {
//            return false;
//        }
//    }

    public boolean isItOKtoShutdown()
    {//todo: need real implementation
        return true;
    }

    /**
     * Answers whether the whole system is under closing all or shutting down period.
     *
     * @return true if in closing all or shut-down mode.
     */
    public boolean isInClosingAllOrShutdownMode()
    {
        return inClosingAllOrShutdownMode;
    }

    public void setInClosingAllOrShutdownMode(boolean inClosingAllOrShutdownMode, boolean runningSuccessful)
    {
        boolean oldValue = this.inClosingAllOrShutdownMode;
        this.inClosingAllOrShutdownMode = inClosingAllOrShutdownMode;
        if (oldValue && runningSuccessful)
        {//means it is previously under closingAll etc mode, thus, need to reset
            currentPanel = null;
            updateMenu();
        }
    }

    /**
     * Enable the given action with the given value.
     *
     * @param actionConstant the value defined in ActionConstants.
     * @param value          either true or false.
     */
    public void enableAction(String actionConstant, boolean value)
    {
        menu.enableAction(actionConstant, value);
    }

    /**
     * Find a defined action by the given action constant value.
     * Will return null if nothing is found.
     * @param actionConstant
     * @return the defined action.
     */
    public Action getDefinedAction(String actionConstant)
    {
        return menu.getDefinedAction(actionConstant);
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.4  2008/06/09 19:53:51  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/06/13 18:42:07  schroedn
 * HISTORY      : added option to remove help
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/06/13 17:12:17  schroedn
 * HISTORY      : added option for checking for help
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.42  2006/09/26 15:50:05  wuye
 * HISTORY      : Updating the newly added menu items
 * HISTORY      :
 * HISTORY      : Revision 1.41  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.40  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.39  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.38  2005/12/29 23:06:17  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.37  2005/12/22 19:06:32  jiangsc
 * HISTORY      : Feature enhancement.
 * HISTORY      :
 * HISTORY      : Revision 1.36  2005/12/14 21:37:19  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.35  2005/12/02 23:02:57  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 * HISTORY      : Revision 1.34  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.33  2005/11/18 20:28:14  jiangsc
 * HISTORY      : Enhanced context-sensitive menu navigation and constructions.
 * HISTORY      :
 * HISTORY      : Revision 1.32  2005/11/18 07:24:46  umkis
 * HISTORY      : replace close button to right side of toolbar panel
 * HISTORY      :
 * HISTORY      : Revision 1.31  2005/11/18 05:59:29  umkis
 * HISTORY      : Change default tool bar menu from about to help
 * HISTORY      :
 * HISTORY      : Revision 1.30  2005/11/17 21:04:53  umkis
 * HISTORY      : Change default tool bar menu from about to help
 * HISTORY      :
 * HISTORY      : Revision 1.29  2005/11/16 21:07:33  umkis
 * HISTORY      : defect# 195, basic three actions(open, save, close) are received from getToolbarActionList() of context manager panel.
 * HISTORY      :
 * HISTORY      : Revision 1.28  2005/11/16 21:05:55  umkis
 * HISTORY      : defect# 195, basic three actions(open, save, close) are received from getToolbarActionList() of context manager panel.
 * HISTORY      :
 * HISTORY      : Revision 1.27  2005/10/26 18:12:29  jiangsc
 * HISTORY      : replaced printStackTrace() to Log.logException
 * HISTORY      :
 * HISTORY      : Revision 1.26  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.25  2005/10/04 20:51:32  jiangsc
 * HISTORY      : Validation enhancement.
 * HISTORY      :
 * HISTORY      : Revision 1.24  2005/09/29 21:19:37  jiangsc
 * HISTORY      : Added Generate Report action support
 * HISTORY      :
 * HISTORY      : Revision 1.23  2005/08/30 21:14:19  jiangsc
 * HISTORY      : minor update
 * HISTORY      :
 * HISTORY      : Revision 1.22  2005/08/30 20:48:18  jiangsc
 * HISTORY      : minor update
 * HISTORY      :
 * HISTORY      : Revision 1.21  2005/08/24 22:25:08  jiangsc
 * HISTORY      : Enhanced Toolbar navigation and creation so as to work around an AWT ArrayOutofBoundException.
 * HISTORY      :
 * HISTORY      : Revision 1.20  2005/08/19 18:54:04  jiangsc
 * HISTORY      : Enhanced exit on ask saving
 * HISTORY      :
 * HISTORY      : Revision 1.19  2005/08/18 21:04:39  jiangsc
 * HISTORY      : Save point of the synchronization effort.
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/08/18 15:30:18  jiangsc
 * HISTORY      : First implementation on Switch control.
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/08/12 18:38:18  jiangsc
 * HISTORY      : Enable HL7 V3 Message to be saved in multiple XML file.
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/08/04 18:54:05  jiangsc
 * HISTORY      : Consolidated tabPane management into MainFrame
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/08/03 16:56:18  jiangsc
 * HISTORY      : Further consolidation of context sensitive management.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/08/02 22:28:57  jiangsc
 * HISTORY      : Newly enhanced context-sensitive menus and toolbar.
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/07/27 22:41:13  jiangsc
 * HISTORY      : Consolidated context sensitive menu implementation.
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/07/27 13:57:45  jiangsc
 * HISTORY      : Added the first round of HSMPanel.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/07/25 22:12:28  jiangsc
 * HISTORY      : Fixed some menu transition.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/07/25 21:56:47  jiangsc
 * HISTORY      : 1) Added expand all and collapse all;
 * HISTORY      : 2) Added toolbar on the mapping panel;
 * HISTORY      : 3) Consolidated menus;
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/07/22 17:39:26  jiangsc
 * HISTORY      : Persistence of Function involved mapping.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/07/15 18:58:49  jiangsc
 * HISTORY      : 1) Reconstucted Menu bars;
 * HISTORY      : 2) Integrated FunctionPane to display property;
 * HISTORY      : 3) Enabled drag and drop functions to mapping panel.
 * HISTORY      :
 */
