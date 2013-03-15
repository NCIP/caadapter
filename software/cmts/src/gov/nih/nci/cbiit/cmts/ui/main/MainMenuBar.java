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

package gov.nih.nci.cbiit.cmts.ui.main;


import gov.nih.nci.cbiit.cmts.ui.actions.AbstractContextAction;
import gov.nih.nci.cbiit.cmts.ui.actions.DefaultCloseAllAction;
import gov.nih.nci.cbiit.cmts.ui.actions.DefaultCloseAction;
import gov.nih.nci.cbiit.cmts.ui.actions.DefaultSaveAction;
import gov.nih.nci.cbiit.cmts.ui.actions.DefaultSaveAsAction;
import gov.nih.nci.cbiit.cmts.ui.actions.DefaultExitAction;
import gov.nih.nci.cbiit.cmts.ui.actions.NewMapFileAction;
import gov.nih.nci.cbiit.cmts.ui.actions.NewTransformationAction;
import gov.nih.nci.cbiit.cmts.ui.actions.OpenMapFileAction;
import gov.nih.nci.cbiit.cmts.ui.common.ActionConstants;
import gov.nih.nci.cbiit.cmts.ui.common.MenuConstants;

import javax.swing.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class manages the definitions and instantiations of menu items. It will
 * coordinate ContextManager class to deal with context sensitive menu
 * switches.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2009-11-23 18:30:56 $
 */
@SuppressWarnings("serial")
public class MainMenuBar extends JMenuBar 
{

	//ContextManager contextManager = null;
	MainFrame mainFrame = null;
	private Map<String, AbstractContextAction> actionMap;
	private Map<String, JMenuItem> menuItemMap;
	private Map<String, JMenu> menuMap;

	public MainMenuBar(MainFrame mf)//ContextManager contextManager) {
	{//this.contextManager = contextManager;
		this.mainFrame = mf;//contextManager.getMainFrame();
		initialize();
	}

	private void initialize()
	{
		actionMap = Collections.synchronizedMap(new HashMap<String, AbstractContextAction>());
		menuItemMap = Collections.synchronizedMap(new HashMap<String, JMenuItem>());
		menuMap = Collections.synchronizedMap(new HashMap<String, JMenu>());

		add(constructFileMenu());
		JMenu helpMenu=new JMenu("Help");
		helpMenu.add(new JMenuItem("About caAdapter CMTS"));
		helpMenu.add(new JMenuItem("Help - Content and Index"));
		add(helpMenu);
	}


	/* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.main.AbstractMenuBar#enableAction(java.lang.String, boolean)
	 */
	public void enableAction(String actionConstant, boolean value)
	{
		Action action = getDefinedAction(actionConstant);// (Action)actionMap.get(actionConstant);
		if (action != null)
		{
			action.setEnabled(value);
		} else
		{
			String msg = "Action could not be found for '" + actionConstant + "'.";
			System.err.println(msg);
			//Log.logWarning(this.getClass(), msg);
		}
	}

	/* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.main.AbstractMenuBar#getDefinedAction(java.lang.String)
	 */
	public Action getDefinedAction(String actionConstant)
	{
		return (Action) actionMap.get(actionConstant);
	}

	public JMenuItem getDefinedMenuItem(String actionConstant)
	{
		return (JMenuItem) menuItemMap.get(actionConstant);
	}

	public JMenu getDefinedMenu(String actionConstant)
	{
		return (JMenu) menuMap.get(actionConstant);
	}

	private JMenu constructFileMenu()
	{
		DefaultSaveAction defaultSaveAction = new DefaultSaveAction(mainFrame);
		JMenuItem saveMenuItem = new JMenuItem(defaultSaveAction);
		actionMap.put(ActionConstants.SAVE, defaultSaveAction);
		menuItemMap.put(ActionConstants.SAVE, saveMenuItem);
		DefaultSaveAsAction defaultSaveAsAction = new DefaultSaveAsAction(mainFrame);
		JMenuItem saveAsMenuItem = new JMenuItem(defaultSaveAsAction);
		actionMap.put(ActionConstants.SAVE_AS, defaultSaveAsAction);
		menuItemMap.put(ActionConstants.SAVE_AS, saveAsMenuItem);
		DefaultCloseAction defaultCloseAction = new DefaultCloseAction(mainFrame);
		JMenuItem closeMenuItem = new JMenuItem(defaultCloseAction);
		actionMap.put(ActionConstants.CLOSE, defaultCloseAction);
		menuItemMap.put(ActionConstants.CLOSE, closeMenuItem);
		DefaultCloseAllAction closeAllAction = new DefaultCloseAllAction(mainFrame);
		JMenuItem closeAllMenuItem = new JMenuItem(closeAllAction);
		actionMap.put(ActionConstants.CLOSE_ALL, closeAllAction);
		menuItemMap.put(ActionConstants.CLOSE_ALL, closeAllMenuItem);
		DefaultExitAction exitAction = new DefaultExitAction(mainFrame);
		JMenuItem exitMenuItem = new JMenuItem(exitAction);
		actionMap.put(ActionConstants.EXIT, exitAction);
		menuItemMap.put(ActionConstants.EXIT, exitMenuItem);
		// link them together
		JMenu fileMenu = new JMenu(MenuConstants.FILE_MENU_NAME);
		fileMenu.setMnemonic('F');
//		fileMenu.add(constructNewMenu());
		fileMenu.add(constructNewCmtsMenu());
		fileMenu.addSeparator();
		fileMenu.add(constructOpenMenu());
		fileMenu.addSeparator();
		fileMenu.add(saveMenuItem);
		fileMenu.add(saveAsMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(closeMenuItem);
		fileMenu.add(closeAllMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(exitMenuItem);
		menuMap.put(MenuConstants.FILE_MENU_NAME, fileMenu);
		defaultSaveAction.setEnabled(false);
		defaultSaveAsAction.setEnabled(false);
		defaultCloseAction.setEnabled(false);
		closeAllAction.setEnabled(false);
		return fileMenu;
	}


	private JMenu constructNewMenu()
	{
		JMenu newGroup = new JMenu("" + MenuConstants.NEW_MENU_NAME);
		//System.out.println("Activated components:\n" + CaadapterUtil.getAllActivatedComponents());
		menuMap.put(MenuConstants.NEW_MENU_NAME, newGroup);
		
        newGroup.add(constructNewCmtsMenu());

		return newGroup;
	}

	private JMenu constructOpenMenu()
	{
		// construct actions and menu items.
		OpenMapFileAction openMapAction = new OpenMapFileAction(mainFrame);

		JMenuItem openMapFileItem = new JMenuItem(openMapAction);
		actionMap.put(ActionConstants.OPEN_MAP_FILE, openMapAction);
		menuItemMap.put(ActionConstants.OPEN_MAP_FILE, openMapFileItem);

		// link them together
		JMenu openMenu = new JMenu("      " + MenuConstants.OPEN_MENU_NAME);
		openMenu.setMnemonic('O');
		openMenu.add(openMapFileItem);

		return openMenu;
	}
		
    private JMenu constructNewCmtsMenu()
    {
    	//user should be authorized to use HL7 artifacts
        JMenu newGroup = new JMenu("New");
        newGroup.setMnemonic('N');

        NewMapFileAction newMapAction = new NewMapFileAction(mainFrame);
        JMenuItem newCmpsMapItem = new JMenuItem(newMapAction);
        actionMap.put(ActionConstants.NEW_MAP_FILE, newMapAction);
        menuItemMap.put(ActionConstants.NEW_MAP_FILE, newCmpsMapItem);
        newGroup.add(newCmpsMapItem);
        
        NewTransformationAction newXmlMessage = new NewTransformationAction(ActionConstants.NEW_XML_Transformation, mainFrame);
        JMenuItem newXmlTransformationItem = new JMenuItem(newXmlMessage);
        actionMap.put(ActionConstants.NEW_XML_Transformation, newXmlMessage);
        menuItemMap.put(ActionConstants.NEW_XML_Transformation, newXmlTransformationItem);
        newGroup.add(newXmlTransformationItem);
        
        NewTransformationAction newCsvMessage = new NewTransformationAction(ActionConstants.NEW_CSV_Transformation, mainFrame);
        JMenuItem newCsvTransformationItem = new JMenuItem(newCsvMessage);
        actionMap.put(ActionConstants.NEW_CSV_Transformation, newCsvMessage);
        menuItemMap.put(ActionConstants.NEW_CSV_Transformation, newCsvTransformationItem);  
        newGroup.add(newCsvTransformationItem);
        
        NewTransformationAction newHl7v2Message = new NewTransformationAction(ActionConstants.NEW_HL7_V2_Transformation, mainFrame);
        JMenuItem newHl7v2TransformationItem = new JMenuItem(newHl7v2Message);
        actionMap.put(ActionConstants.NEW_CSV_Transformation, newHl7v2Message);
        menuItemMap.put(ActionConstants.NEW_CSV_Transformation, newHl7v2TransformationItem);  
        newGroup.add(newHl7v2TransformationItem);
        
        return newGroup;
    }

	
	/* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.main.AbstractMenuBar#resetMenus(boolean)
	 */
	public void resetMenus(boolean hasActiveDocument)
	{// provide structure for
		// more menus to be
		// reset
		resetFileMenu(hasActiveDocument);
//		resetReportMenu(hasActiveDocument);
	}

	private void resetFileMenu(boolean hasActiveDocument)
	{
		resetNewSubMenu(hasActiveDocument);
		resetOpenSubMenu(hasActiveDocument);
		JMenuItem saveMenuItem = menuItemMap.get(ActionConstants.SAVE);
		JMenuItem saveAsMenuItem = menuItemMap.get(ActionConstants.SAVE_AS);
//		JMenuItem validateMenuItem = menuItemMap.get(ActionConstants.VALIDATE);
//		JMenuItem closeMenuItem = menuItemMap.get(ActionConstants.CLOSE);
//		JMenuItem closeAllMenuItem = menuItemMap.get(ActionConstants.CLOSE_ALL);
//		JMenuItem anotateMenuItem = menuItemMap.get(ActionConstants.ANOTATE);
		//		saveMenuItem.setAction(null);
		//		saveMenuItem.setAction(defaultSaveAction);
		//		actionMap.put(ActionConstants.SAVE, defaultSaveAction);
		//		saveAsMenuItem.setAction(null);
		//		saveAsMenuItem.setAction(defaultSaveAsAction);
		//		actionMap.put(ActionConstants.SAVE_AS, defaultSaveAsAction);
		//		anotateMenuItem.setAction(null);
		//		anotateMenuItem.setAction(defaultAnotateAction);
		//		actionMap.put(ActionConstants.ANOTATE, defaultAnotateAction);
		//		validateMenuItem.setAction(null);
		//		validateMenuItem.setAction(defaultValidateAction);
		//		actionMap.put(ActionConstants.VALIDATE, defaultValidateAction);
		//		closeMenuItem.setAction(null);
		//		closeMenuItem.setAction(defaultCloseAction);
		//		actionMap.put(ActionConstants.CLOSE, defaultCloseAction);
		saveMenuItem.getAction().setEnabled(false);
		saveAsMenuItem.getAction().setEnabled(false);
//		anotateMenuItem.getAction().setEnabled(false);
//		validateMenuItem.getAction().setEnabled(false);
//		closeMenuItem.getAction().setEnabled(false);
//		closeAllMenuItem.getAction().setEnabled(hasActiveDocument);
	}

	private void resetNewSubMenu(boolean hasActiveDocument)
	{
		if (!hasActiveDocument)
		{
			//			menuItemMap.get(ActionConstants.NEW_MAP_FILE).getAction().setEnabled(true);
			resetMenuItem(ActionConstants.NEW_MAP_FILE, true);
			//			newMapFileItem.getAction().setEnabled(true);
			//			newCSVSpecificationItem.getAction().setEnabled(true);
			//			menuItemMap.get(ActionConstants.NEW_CSV_SPEC).getAction().setEnabled(true);
		}
	}

	private void resetMenuItem(String itemName, boolean newValue)
	{
		JMenuItem menuItem = menuItemMap.get(itemName);
		if (menuItem != null)
		{
			Action a = menuItem.getAction();
			if (a != null)
				a.setEnabled(newValue);
		}
	}

	private void resetOpenSubMenu(boolean hasActiveDocument)
	{
		if (!hasActiveDocument)
		{
			resetMenuItem(ActionConstants.OPEN_MAP_FILE, true);
		}
	}

//	private void resetReportMenu(boolean hasActiveDocument)
//	{
//		if (!hasActiveDocument)
//		{
//			resetMenuItem(ActionConstants.GENERATE_REPORT, false);
//		}
//	}

	public void enableCloseAllAction(boolean newValue)
	{
		Action closeAllAction = actionMap.get(ActionConstants.CLOSE_ALL);
		if (closeAllAction != null)
		{
			closeAllAction.setEnabled(newValue);
			JMenuItem closeAllMenuItem = menuItemMap.get(ActionConstants.CLOSE_ALL);
			closeAllMenuItem.setAction(null);
			closeAllMenuItem.setAction(closeAllAction);
			// closeAllMenuItem.invalidate();
		}
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
