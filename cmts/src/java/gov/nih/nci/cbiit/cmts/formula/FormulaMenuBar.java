package gov.nih.nci.cbiit.cmts.formula;

import gov.nih.nci.cbiit.cmts.ui.actions.*;
import gov.nih.nci.cbiit.cmts.ui.common.ActionConstants;
import gov.nih.nci.cbiit.cmts.ui.common.MenuConstants;

import javax.swing.*;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 10, 2010
 * Time: 4:57:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class FormulaMenuBar extends JMenuBar
{

	//ContextManager contextManager = null;
	FormulaMain formulaMain = null;
	private Map<String, AbstractContextAction> actionMap;
	private Map<String, JMenuItem> menuItemMap;
	private Map<String, JMenu> menuMap;

    private JMenuItem newMenuItem;
    private JMenuItem exitMenuItem;

    public FormulaMenuBar(FormulaMain mf)//ContextManager contextManager) {
	{//this.contextManager = contextManager;
		this.formulaMain = mf;//contextManager.getMainFrame();
		initialize();
	}

	private void initialize()
	{
		actionMap = Collections.synchronizedMap(new HashMap<String, AbstractContextAction>());
		menuItemMap = Collections.synchronizedMap(new HashMap<String, JMenuItem>());
		menuMap = Collections.synchronizedMap(new HashMap<String, JMenu>());

		add(constructFileMenu());
		JMenu helpMenu=new JMenu("Help");
		helpMenu.add(new JMenuItem("About Formula Generator"));
		//helpMenu.add(new JMenuItem("Help - Content and Index"));
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

        //DefaultSaveAction defaultSaveAction = new DefaultSaveAction(mainFrame);
		newMenuItem = new JMenuItem("New");
		//actionMap.put(ActionConstants.SAVE, defaultSaveAction);
		menuItemMap.put("New", newMenuItem);
		//DefaultSaveAsAction defaultSaveAsAction = new DefaultSaveAsAction(mainFrame);
		JMenuItem openMenuItem = new JMenuItem("Open");
		//actionMap.put(ActionConstants.SAVE_AS, defaultSaveAsAction);
		menuItemMap.put(ActionConstants.OPEN, openMenuItem);
		//DefaultCloseAction defaultCloseAction = new DefaultCloseAction(mainFrame);
		JMenuItem saveMenuItem = new JMenuItem("Save");
		//actionMap.put(ActionConstants.CLOSE, defaultCloseAction);
		menuItemMap.put(ActionConstants.SAVE, saveMenuItem);
		//DefaultCloseAllAction closeAllAction = new DefaultCloseAllAction(mainFrame);
		JMenuItem saveAsMenuItem = new JMenuItem("Save As");
		//actionMap.put(ActionConstants.CLOSE_ALL, closeAllAction);
		menuItemMap.put(ActionConstants.CLOSE_ALL, saveAsMenuItem);
		FormulaExitAction exitAction = new FormulaExitAction(formulaMain);
		exitMenuItem = new JMenuItem("Exit");
		actionMap.put(ActionConstants.EXIT, exitAction);
		menuItemMap.put(ActionConstants.EXIT, exitMenuItem);

		// link them together
		JMenu fileMenu = new JMenu(MenuConstants.FILE_MENU_NAME);
		fileMenu.setMnemonic('F');
//		fileMenu.add(constructNewMenu());
		fileMenu.add(newMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(openMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(saveMenuItem);
		fileMenu.add(saveAsMenuItem);
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
        actionMap.put(ActionConstants.NEW_HL7_V2_Transformation, newHl7v2Message);
        menuItemMap.put(ActionConstants.NEW_HL7_V2_Transformation, newHl7v2TransformationItem);
        newGroup.add(newHl7v2TransformationItem);

        newGroup.addSeparator();
        NewTransformationAction xmlToCdaMessage = new NewTransformationAction(ActionConstants.NEW_XML_CDA_Transformation, mainFrame);
        JMenuItem newXmlToCdaTransformationItem = new JMenuItem(xmlToCdaMessage);
        actionMap.put(ActionConstants.NEW_XML_CDA_Transformation, xmlToCdaMessage);
        menuItemMap.put(ActionConstants.NEW_XML_CDA_Transformation, newXmlToCdaTransformationItem);
        newGroup.add(newXmlToCdaTransformationItem);

        NewTransformationAction csvToCdaMessage = new NewTransformationAction(ActionConstants.NEW_CSV_CDA_Transformation , mainFrame);
        JMenuItem newCsvToCdaTransformationItem = new JMenuItem(csvToCdaMessage);
        actionMap.put(ActionConstants.NEW_CSV_CDA_Transformation, csvToCdaMessage);
        menuItemMap.put(ActionConstants.NEW_CSV_CDA_Transformation, newXmlToCdaTransformationItem);
        newGroup.add(newCsvToCdaTransformationItem);

        NewTransformationAction hl7v2ToCdaMessage = new NewTransformationAction(ActionConstants.NEW_HL7_V2_CDA_Transformation, mainFrame);
        JMenuItem newHl77v2ToCdaTransformationItem = new JMenuItem(hl7v2ToCdaMessage);
        actionMap.put(ActionConstants.NEW_HL7_V2_CDA_Transformation, hl7v2ToCdaMessage);
        menuItemMap.put(ActionConstants.NEW_HL7_V2_CDA_Transformation, newXmlToCdaTransformationItem);
        newGroup.add(newHl77v2ToCdaTransformationItem);
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
    public JMenuItem getNewMenuItem()
    {
        return newMenuItem;
    }
    public JMenuItem getExitMenuItem()
    {
        return exitMenuItem;
    }
}
