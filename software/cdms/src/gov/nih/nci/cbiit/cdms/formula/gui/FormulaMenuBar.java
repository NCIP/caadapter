package gov.nih.nci.cbiit.cdms.formula.gui;

import gov.nih.nci.cbiit.cdms.formula.gui.constants.ActionConstants;
import gov.nih.nci.cbiit.cdms.formula.gui.constants.MenuConstants;
import gov.nih.nci.cbiit.cdms.formula.gui.action.OpenFormulaAction;

import javax.swing.*;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 15, 2010
 * Time: 9:48:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class FormulaMenuBar extends MenuBar
{

    //ContextManager contextManager = null;

    //private Map<String, AbstractContextAction> actionMap;
    private Map<String, MenuItem> menuItemMap;
    private Map<String, Menu> menuMap;

    private MenuItem newMenuItem;
    private MenuItem exitMenuItem;
    private MenuItem closeMenuItem;
    private MenuItem openMenuItem;
    private MenuItem saveMenuItem;
    private MenuItem saveAsMenuItem;
    private MenuItem calculateMenuItem;
    private FormulaMainPanel mainPanel;

    public FormulaMenuBar(FormulaMainPanel mf)//ContextManager contextManager) {
    {//this.contextManager = contextManager;
        this.mainPanel = mf;//contextManager.getMainFrame();
        initialize();
    }


    private void initialize()
    {
        //actionMap = Collections.synchronizedMap(new HashMap<String, AbstractContextAction>());
        menuItemMap = Collections.synchronizedMap(new HashMap<String, MenuItem>());
        menuMap = Collections.synchronizedMap(new HashMap<String, Menu>());

        add(constructFileMenu());
        Menu testMenu=new Menu("Test");

        calculateMenuItem = new MenuItem("Calculate");
        //actionMap.put(ActionConstants.EXIT, exitAction);
        menuItemMap.put(ActionConstants.CALCULATE, calculateMenuItem);
        testMenu.add(calculateMenuItem);
        //helpMenu.add(new JMenuItem("Help - Content and Index"));
        add(testMenu);
        Menu helpMenu=new Menu("Help");
        helpMenu.add(new MenuItem("About Formula Generator"));
        //helpMenu.add(new JMenuItem("Help - Content and Index"));
        add(helpMenu);
    }


    /* (non-Javadoc)
      * @see gov.nih.nci.caadapter.ui.main.AbstractMenuBar#enableAction(java.lang.String, boolean)
      */
//    public void enableAction(String actionConstant, boolean value)
//    {
//        Action action = getDefinedAction(actionConstant);// (Action)actionMap.get(actionConstant);
//        if (action != null)
//        {
//            action.setEnabled(value);
//        } else
//        {
//            String msg = "Action could not be found for '" + actionConstant + "'.";
//            System.err.println(msg);
//            //Log.logWarning(this.getClass(), msg);
//        }
//    }

    /* (non-Javadoc)
      * @see gov.nih.nci.caadapter.ui.main.AbstractMenuBar#getDefinedAction(java.lang.String)
      */
//    public Action getDefinedAction(String actionConstant)
//    {
//        return (Action) actionMap.get(actionConstant);
//    }

    public MenuItem getDefinedMenuItem(String actionConstant)
    {
        return (MenuItem) menuItemMap.get(actionConstant);
    }

    public Menu getDefinedMenu(String actionConstant)
    {
        return (Menu) menuMap.get(actionConstant);
    }

    private Menu constructFileMenu()
    {

        //DefaultSaveAction defaultSaveAction = new DefaultSaveAction(mainFrame);
        newMenuItem = new MenuItem("New");
        //actionMap.put(ActionConstants.SAVE, defaultSaveAction);
        menuItemMap.put("New", newMenuItem);
        //DefaultSaveAsAction defaultSaveAsAction = new DefaultSaveAsAction(mainFrame);
        openMenuItem = new MenuItem("Open");
        //actionMap.put(ActionConstants.SAVE_AS, defaultSaveAsAction);
        menuItemMap.put(ActionConstants.OPEN, openMenuItem);
        //OpenFormulaAction openAction = new OpenFormulaAction(mainPanel);
        saveMenuItem = new MenuItem("Save");
        //actionMap.put(ActionConstants.CLOSE, defaultCloseAction);
        menuItemMap.put(ActionConstants.SAVE, saveMenuItem);
        //DefaultCloseAllAction closeAllAction = new DefaultCloseAllAction(mainFrame);
        saveAsMenuItem = new MenuItem("Save As");
        //actionMap.put(ActionConstants.CLOSE_ALL, closeAllAction);
        menuItemMap.put(ActionConstants.CLOSE_ALL, saveAsMenuItem);
        //FormulaExitAction exitAction = new FormulaExitAction(formulaMain);
        closeMenuItem = new MenuItem("Close");
        //actionMap.put(ActionConstants.EXIT, exitAction);
        menuItemMap.put(ActionConstants.CLOSE, closeMenuItem);
        exitMenuItem = new MenuItem("Exit");
        //actionMap.put(ActionConstants.EXIT, exitAction);
        menuItemMap.put(ActionConstants.EXIT, exitMenuItem);

        // link them together
        Menu fileMenu = new Menu(MenuConstants.FILE_MENU_NAME);
        //fileMenu.setMnemonic('F');
//		fileMenu.add(constructNewMenu());
        fileMenu.add(newMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(openMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(closeMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        menuMap.put(MenuConstants.FILE_MENU_NAME, fileMenu);
//		defaultSaveAction.setEnabled(false);
//		defaultSaveAsAction.setEnabled(false);
//		defaultCloseAction.setEnabled(false);
//		closeAllAction.setEnabled(false);
        return fileMenu;
    }

    /*
	private Menu constructNewMenu()
	{
		Menu newGroup = new Menu("" + MenuConstants.NEW_MENU_NAME);
		//System.out.println("Activated components:\n" + CaadapterUtil.getAllActivatedComponents());
		menuMap.put(MenuConstants.NEW_MENU_NAME, newGroup);

        newGroup.add(constructNewCmtsMenu());

		return newGroup;
	}


    private Menu constructOpenMenu()
	{
		// construct actions and menu items.
		OpenMapFileAction openMapAction = new OpenMapFileAction(mainFrame);

		MenuItem openMapFileItem = new MenuItem(openMapAction);
		actionMap.put(ActionConstants.OPEN_MAP_FILE, openMapAction);
		menuItemMap.put(ActionConstants.OPEN_MAP_FILE, openMapFileItem);

		// link them together
		Menu openMenu = new Menu("      " + MenuConstants.OPEN_MENU_NAME);
		openMenu.setMnemonic('O');
		openMenu.add(openMapFileItem);

		return openMenu;
	}

    private Menu constructNewCmtsMenu()
    {
    	//user should be authorized to use HL7 artifacts
        Menu newGroup = new Menu("New");
        newGroup.setMnemonic('N');

        NewMapFileAction newMapAction = new NewMapFileAction(mainFrame);
        MenuItem newCmpsMapItem = new MenuItem(newMapAction);
        actionMap.put(ActionConstants.NEW_MAP_FILE, newMapAction);
        menuItemMap.put(ActionConstants.NEW_MAP_FILE, newCmpsMapItem);
        newGroup.add(newCmpsMapItem);

        NewTransformationAction newXmlMessage = new NewTransformationAction(ActionConstants.NEW_XML_Transformation, mainFrame);
        MenuItem newXmlTransformationItem = new MenuItem(newXmlMessage);
        actionMap.put(ActionConstants.NEW_XML_Transformation, newXmlMessage);
        menuItemMap.put(ActionConstants.NEW_XML_Transformation, newXmlTransformationItem);
        newGroup.add(newXmlTransformationItem);

        NewTransformationAction newCsvMessage = new NewTransformationAction(ActionConstants.NEW_CSV_Transformation, mainFrame);
        MenuItem newCsvTransformationItem = new MenuItem(newCsvMessage);
        actionMap.put(ActionConstants.NEW_CSV_Transformation, newCsvMessage);
        menuItemMap.put(ActionConstants.NEW_CSV_Transformation, newCsvTransformationItem);
        newGroup.add(newCsvTransformationItem);

        NewTransformationAction newHl7v2Message = new NewTransformationAction(ActionConstants.NEW_HL7_V2_Transformation, mainFrame);
        MenuItem newHl7v2TransformationItem = new MenuItem(newHl7v2Message);
        actionMap.put(ActionConstants.NEW_HL7_V2_Transformation, newHl7v2Message);
        menuItemMap.put(ActionConstants.NEW_HL7_V2_Transformation, newHl7v2TransformationItem);
        newGroup.add(newHl7v2TransformationItem);

        newGroup.addSeparator();
        NewTransformationAction xmlToCdaMessage = new NewTransformationAction(ActionConstants.NEW_XML_CDA_Transformation, mainFrame);
        MenuItem newXmlToCdaTransformationItem = new MenuItem(xmlToCdaMessage);
        actionMap.put(ActionConstants.NEW_XML_CDA_Transformation, xmlToCdaMessage);
        menuItemMap.put(ActionConstants.NEW_XML_CDA_Transformation, newXmlToCdaTransformationItem);
        newGroup.add(newXmlToCdaTransformationItem);

        NewTransformationAction csvToCdaMessage = new NewTransformationAction(ActionConstants.NEW_CSV_CDA_Transformation , mainFrame);
        MenuItem newCsvToCdaTransformationItem = new MenuItem(csvToCdaMessage);
        actionMap.put(ActionConstants.NEW_CSV_CDA_Transformation, csvToCdaMessage);
        menuItemMap.put(ActionConstants.NEW_CSV_CDA_Transformation, newXmlToCdaTransformationItem);
        newGroup.add(newCsvToCdaTransformationItem);

        NewTransformationAction hl7v2ToCdaMessage = new NewTransformationAction(ActionConstants.NEW_HL7_V2_CDA_Transformation, mainFrame);
        MenuItem newHl77v2ToCdaTransformationItem = new MenuItem(hl7v2ToCdaMessage);
        actionMap.put(ActionConstants.NEW_HL7_V2_CDA_Transformation, hl7v2ToCdaMessage);
        menuItemMap.put(ActionConstants.NEW_HL7_V2_CDA_Transformation, newXmlToCdaTransformationItem);
        newGroup.add(newHl77v2ToCdaTransformationItem);
        return newGroup;
    }


	/* (non-Javadoc)
	 * @see gov.nih.nci.caadapter.ui.main.AbstractMenuBar#resetMenus(boolean)
	 */
//    public void resetMenus(boolean hasActiveDocument)
//    {// provide structure for
        // more menus to be
        // reset
//        resetFileMenu(hasActiveDocument);
//		resetReportMenu(hasActiveDocument);
//    }

//    private void resetFileMenu(boolean hasActiveDocument)
//    {
//        resetNewSubMenu(hasActiveDocument);
//        resetOpenSubMenu(hasActiveDocument);
//        MenuItem saveMenuItem = menuItemMap.get(ActionConstants.SAVE);
//        MenuItem saveAsMenuItem = menuItemMap.get(ActionConstants.SAVE_AS);
//		MenuItem validateMenuItem = menuItemMap.get(ActionConstants.VALIDATE);
//		MenuItem closeMenuItem = menuItemMap.get(ActionConstants.CLOSE);
//		MenuItem closeAllMenuItem = menuItemMap.get(ActionConstants.CLOSE_ALL);
//		MenuItem anotateMenuItem = menuItemMap.get(ActionConstants.ANOTATE);
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
        //saveMenuItem.getAction().setEnabled(false);
        //saveAsMenuItem.getAction().setEnabled(false);
//		anotateMenuItem.getAction().setEnabled(false);
//		validateMenuItem.getAction().setEnabled(false);
//		closeMenuItem.getAction().setEnabled(false);
//		closeAllMenuItem.getAction().setEnabled(hasActiveDocument);
//    }

//    private void resetNewSubMenu(boolean hasActiveDocument)
//    {
//        if (!hasActiveDocument)
//        {
//            //			menuItemMap.get(ActionConstants.NEW_MAP_FILE).getAction().setEnabled(true);
//            resetMenuItem(ActionConstants.NEW_MAP_FILE, true);
//            //			newMapFileItem.getAction().setEnabled(true);
//            //			newCSVSpecificationItem.getAction().setEnabled(true);
//            //			menuItemMap.get(ActionConstants.NEW_CSV_SPEC).getAction().setEnabled(true);
//        }
//    }

//    private void resetMenuItem(String itemName, boolean newValue)
//    {
//        MenuItem menuItem = menuItemMap.get(itemName);
//        if (menuItem != null)
//        {
//            Action a = menuItem.getAction();
//            if (a != null)
//                a.setEnabled(newValue);
//        }
//    }

//    private void resetOpenSubMenu(boolean hasActiveDocument)
//    {
//        if (!hasActiveDocument)
//        {
//            resetMenuItem(ActionConstants.OPEN_MAP_FILE, true);
//        }
//    }

//	private void resetReportMenu(boolean hasActiveDocument)
//	{
//		if (!hasActiveDocument)
//		{
//			resetMenuItem(ActionConstants.GENERATE_REPORT, false);
//		}
//	}

//    public void enableCloseAllAction(boolean newValue)
//    {
//        Action closeAllAction = actionMap.get(ActionConstants.CLOSE_ALL);
//        if (closeAllAction != null)
//        {
//            closeAllAction.setEnabled(newValue);
//            MenuItem closeAllMenuItem = menuItemMap.get(ActionConstants.CLOSE_ALL);
//            closeAllMenuItem.setAction(null);
//            closeAllMenuItem.setAction(closeAllAction);
//            // closeAllMenuItem.invalidate();
//        }
//    }
    public MenuItem getNewMenuItem()
    {
        return newMenuItem;
    }
    public MenuItem getCloseMenuItem()
    {
        return closeMenuItem;
    }
    public MenuItem getCalculateMenuItem()
    {
        return calculateMenuItem;
    }
    public MenuItem getOpenMenuItem()
    {
        return openMenuItem;
    }
    public MenuItem getSaveMenuItem()
    {
        return saveMenuItem;
    }
    public MenuItem getSaveAsMenuItem()
    {
        return saveAsMenuItem;
    }
    public MenuItem getExitMenuItem()
    {
        return exitMenuItem;
    }
}
