/*L
 * Copyright SAIC, SAIC-Frederick.
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

import gov.nih.nci.cbiit.cmts.ui.main.MainFrame;
import gov.nih.nci.cbiit.cmts.ui.main.MainMenuBar;
import gov.nih.nci.cbiit.cmts.ui.main.MainFrameContainer;
import gov.nih.nci.cbiit.cmts.ui.util.GeneralUtilities;
import gov.nih.nci.cbiit.cmts.ui.actions.NewMapFileAction;
import gov.nih.nci.cbiit.cmts.ui.actions.OpenMapFileAction;
import gov.nih.nci.cbiit.cmts.ui.actions.DefaultSaveAsAction;
import gov.nih.nci.cbiit.cmts.ui.mapping.MappingMainPanel;
import gov.nih.nci.cbiit.cmts.ui.message.MessagePanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Manage the context change effect, including notify menus to update.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.3 $
 * @date       $Date: 2009-11-23 18:31:57 $
 */
public class ContextManager implements ChangeListener
{

    private MainMenuBar menu = null;
    private ToolBarHandler toolBarHandler = null;
    private MainFrameContainer mainFrame = null;
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
    public void initContextManager(MainFrameContainer owner)
    {
    	mainFrame = owner;
    	contextFileManager = new ContextFileManager(mainFrame);
//    	menu = new MainMenuBar(mainFrame);
    	clientMenuActions = Collections.synchronizedMap(new HashMap<String, MenuActionMaps>());

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
//		else if (menuName.equalsIgnoreCase(MenuConstants.REPORT_MENU_NAME))
//			menuMaps.addReportMenuAction(actionName, action);
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
//		else if (menuName.equalsIgnoreCase(MenuConstants.REPORT_MENU_NAME))
//			menuMaps.getReportMenuActions().remove(actionName);
		else if (menuName.equalsIgnoreCase(MenuConstants.TOOLBAR_MENU_NAME))
			menuMaps.getToolBarMenuActions().remove(actionName);
    	clientMenuActions.put(svcName, menuMaps);
    }
    public MainMenuBar getMenu()
    {
        return menu;
    }

    public void setMenu(MainMenuBar newMenu)
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
        else
        {
            if (mainFrame.getTabbedPane().getTabCount() >= 1)
            {
                int tabIndex = mainFrame.getTabbedPane().getTabCount() - 1;
                nowPanel = (JComponent) mainFrame.getTabbedPane().getComponentAt(tabIndex);
                mainFrame.getTabbedPane().setSelectedIndex(tabIndex);
            }
            System.out.println("ContextManagement.stateChanged(ChangeEvent e) (tabCount="+mainFrame.getTabbedPane().getTabCount()+") now=" + nowPanel + "\ncurrent=" + currentPanel);
        }
        System.out.println("ContextManagement.stateChanged(ChangeEvent e) (tabCount="+mainFrame.getTabbedPane().getTabCount()+") currentTabIndex=" + selectedIndex);
        //JOptionPane.showMessageDialog(mainFrame.getAssociatedUIComponent()," ("+mainFrame.getTabbedPane().getTabCount()+") now=" + nowPanel + "\ncurrent=" + currentPanel);
        if (((currentPanel == null)&&(nowPanel != null))||(!GeneralUtilities.areEqual(nowPanel, currentPanel)))
        {
            currentPanel = nowPanel;
            updateMenu();
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
        	//System.out.println("ContextManager.updateMenu()..current Panel:"+currentPanel);

            //if(currentPanel == null)
                //JOptionPane.showMessageDialog(currentPanel, "ContextManager.updateMenu().. null panel");
            //else
                //JOptionPane.showMessageDialog(currentPanel, "ContextManager.updateMenu().. class=" + currentPanel.getClass().getCanonicalName());

            ContextManagerClient contextClient = null;
            if(currentPanel instanceof ContextManagerClient) contextClient = (ContextManagerClient) currentPanel;
            else if(currentPanel instanceof MappingMainPanel) contextClient = (MappingMainPanel) currentPanel;
            else if(currentPanel instanceof MessagePanel) contextClient = (MessagePanel) currentPanel;
            if (contextClient != null)
            {
                //ContextManagerClient contextClient = (ContextManagerClient) currentPanel;
                getToolBarHandler().removeAllActions();
                /*
                java.util.List<Action> actions = contextClient.getToolbarActionList();
                for(int i=0;i<actions.size();i++)
				{//includes open, save, close, validate, etc.
					Action action = actions.get(i);
					if(action!=null)
					{
						getToolBarHandler().addAction(action, true);
					}
				}
                */
                JButton closeButton = null;
                Action closeAction = contextClient.getDefaultCloseAction();
                if(closeAction !=null)
                {
                	closeAction.setEnabled(true);
                    closeButton = new JButton(closeAction);
                    closeButton.setEnabled(true);
                    closeButton.setText("");
                }


                Action saveAction = contextClient.getDefaultSaveAction();



                //JButton saveAsButton = null;
                Map actionMap_ = contextClient.getMenuItems(MenuConstants.FILE_MENU_NAME);
		        Action saveAsAction = (Action) actionMap_.get(ActionConstants.SAVE_AS);
                DefaultSaveAsAction defaultSaveAsAction = null;
                if(saveAsAction!=null)
                {
                    defaultSaveAsAction = (DefaultSaveAsAction) saveAsAction;
                    defaultSaveAsAction.setEnabled(true);

                    //ImageIcon imageIco = new ImageIcon(DefaultSettings.getImage("fileSaveAs.gif"));
                    ImageIcon imageIco = new ImageIcon(DefaultSettings.getImage("ico_saveAs.bmp"));
                    defaultSaveAsAction.setIcon(imageIco);
                    defaultSaveAsAction.setShortDescription("Save AS another file");

                }



                Action openAction = contextClient.getDefaultOpenAction();
                if(openAction == null)
                {
                    openAction = new OpenMapFileAction(mainFrame);
                }




                NewMapFileAction newMapAction = new NewMapFileAction(mainFrame);
                //ImageIcon newImageIcon = new ImageIcon(DefaultSettings.getImage("_Add_tables.gif"));
                ImageIcon newImageIcon = new ImageIcon(DefaultSettings.getImage("ico_new.bmp"));
                newMapAction.setIcon(newImageIcon);
                newMapAction.setShortDescription("New Mapping File");





                //update the toolbar
                toolBarHandler.getToolBar().add(newMapAction);
                toolBarHandler.getToolBar().add(openAction);
                toolBarHandler.getToolBar().add(saveAction);
                if (defaultSaveAsAction != null) toolBarHandler.getToolBar().add(defaultSaveAsAction);
                mainFrame.updateToolBar(toolBarHandler.getToolBar(), closeButton);

                //update the menus
                menu.resetMenus(true);
                Map actionMap = contextClient.getMenuItems(MenuConstants.FILE_MENU_NAME);
                updateMenu(actionMap, MenuConstants.FILE_MENU_NAME);
//                actionMap = contextClient.getMenuItems(MenuConstants.REPORT_MENU_NAME);
//                updateMenu(actionMap, MenuConstants.REPORT_MENU_NAME);
            }
            else
            {//we could possibly be here b/c user closed the last tab, roll back to menu's original state.
                getToolBarHandler().removeAllActions();


                NewMapFileAction newMapAction = new NewMapFileAction(mainFrame);
                //ImageIcon newImageIcon = new ImageIcon(DefaultSettings.getImage("_Add_tables.gif"));
                ImageIcon newImageIcon = new ImageIcon(DefaultSettings.getImage("ico_new.bmp"));
                newMapAction.setIcon(newImageIcon);
                newMapAction.setShortDescription("New Mapping File");

                newMapAction.setEnabled(true);

                toolBarHandler.getToolBar().add(newMapAction);

                OpenMapFileAction openAction = new OpenMapFileAction(mainFrame);
                toolBarHandler.getToolBar().add(openAction);
                /*
                ContextManagerClient contextClient = ContextManagerClient;
                Action action = contextClient.getDefaultCloseAction();
                JButton closeButton = null;
                if(action!=null)
                {
                	action.setEnabled(true);
                    closeButton = new JButton(action);
                    closeButton.setEnabled(true);
                    closeButton.setText("");
                }
                */
                mainFrame.updateToolBar(toolBarHandler.getToolBar());
                menu.resetMenus(false);
            }
            menu.repaint();
        }
        catch(Throwable t)
        {
        	t.printStackTrace();
            //Log.logException(this, "Exception occurred in updateMenu()", t);
        }
    }
    private void updateMenu(Map actionMap, String menu_name)
    {
        if (menu_name == MenuConstants.FILE_MENU_NAME)
        {
            updateMenuAction(actionMap, ActionConstants.NEW_MAP_FILE);
            updateMenuAction(actionMap, ActionConstants.OPEN_MAP_FILE);

            updateMenuAction(actionMap, ActionConstants.SAVE);
            updateMenuAction(actionMap, ActionConstants.SAVE_AS);
            updateMenuAction(actionMap, ActionConstants.VALIDATE);
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
            if (act instanceof DefaultSaveAsAction)
            {
                if (menuItem.getIcon() == null)
                {
                    //ImageIcon imageIco = new ImageIcon(DefaultSettings.getImage("fileSaveAs.gif"));
                    ImageIcon imageIco = new ImageIcon(DefaultSettings.getImage("ico_saveAs.bmp"));
                    menuItem.setIcon(imageIco);
                }
            }

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
 * HISTORY : $Log: not supported by cvs2svn $
 * HISTORY : Revision 1.2  2008/12/09 19:04:17  linc
 * HISTORY : First GUI release
 * HISTORY :
 * HISTORY : Revision 1.1  2008/12/03 20:46:14  linc
 * HISTORY : UI update.
 * HISTORY :
 */
