/**
 * <!-- LICENSE_TEXT_START --> $Header:
 * /share/content/cvsroot/hl7sdk/src/gov/nih/nci/hl7/ui/MainMenuBar.java,v 1.44
 * 2006/10/30 20:30:26 jayannah Exp $
 * ****************************************************************** COPYRIGHT
 * NOTICE ****************************************************************** The
 * caAdapter Software License, Version 1.3 Copyright Notice. Copyright 2006
 * SAIC. This software was developed in conjunction with the National Cancer
 * Institute. To the extent government employees are co-authors, any rights in
 * such works are subject to Title 17 of the United States Code, section 105.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the Copyright Notice above,
 * this list of conditions, and the disclaimer of Article 3, below.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution. 2. The end-user
 * documentation included with the redistribution, if any, must include the
 * following acknowledgment: "This product includes software developed by the
 * SAIC and the National Cancer Institute." If no such end-user documentation is
 * to be included, this acknowledgment shall appear in the software itself,
 * wherever such third-party acknowledgments normally appear. 3. The names "The
 * National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or
 * promote products derived from this software. 4. This license does not
 * authorize the incorporation of this software into any third party proprietary
 * programs. This license does not authorize the recipient to use any trademarks
 * owned by either NCI or SAIC-Frederick. 5. THIS SOFTWARE IS PROVIDED "AS IS,"
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE)
 * ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL
 * CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. <!--
 * LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.ui.main;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.ui.common.AbstractMenuBar;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.actions.*;
import gov.nih.nci.caadapter.ui.common.context.MenuConstants;
import gov.nih.nci.caadapter.ui.common.preferences.OpenPreferenceAction;
import gov.nih.nci.caadapter.ui.help.actions.AboutAction;
import gov.nih.nci.caadapter.ui.help.actions.HelpTopicAction;
import gov.nih.nci.caadapter.ui.hl7message.actions.NewHL7V3MessageAction;
import gov.nih.nci.caadapter.ui.hl7message.actions.OpenHL7V3MessageAction;
import gov.nih.nci.caadapter.ui.mapping.NewMapFileAction;
import gov.nih.nci.caadapter.ui.mapping.OpenMapFileAction;
import gov.nih.nci.caadapter.ui.mapping.V2V3.actions.V2V3MapAction;
import gov.nih.nci.caadapter.ui.mapping.mms.actions.DefaultAnotateAction;
import gov.nih.nci.caadapter.ui.mapping.mms.actions.NewObject2DBMapAction;
import gov.nih.nci.caadapter.ui.mapping.mms.actions.OpenObjectToDbMapAction;
import gov.nih.nci.caadapter.ui.mapping.sdtm.actions.Database2SDTMAction;
import gov.nih.nci.caadapter.ui.mapping.sdtm.actions.NewSDTMStructureAction;
import gov.nih.nci.caadapter.ui.mapping.sdtm.actions.OpenSDTMMapAction;
import gov.nih.nci.caadapter.ui.specification.csv.actions.NewCsvSpecificationAction;
import gov.nih.nci.caadapter.ui.specification.csv.actions.OpenCsvSpecificationAction;
import gov.nih.nci.caadapter.ui.specification.hsm.actions.NewHSMAction;
import gov.nih.nci.caadapter.ui.specification.hsm.actions.OpenHSMAction;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class manages the definitions and instantiations of menu items. It will
 * coordinate ContextManager class to deal with context sensitive menu
 * switches.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: jayannah $
 * @version Since caAdapter v1.2 revision $Revision: 1.10 $ date $Date:
 *          2006/10/23 16:27:28 $
 */
public class MainMenuBar extends AbstractMenuBar
{

    //ContextManager contextManager = null;

    MainFrame mainFrame = null;

    // --menu and menu item list.
    // definition of menu items, refer to constructXXXMenu() functions for
    // construction.
    //JMenu fileMenu = null;
    //JMenu newMenu = null;
    //	JMenuItem newMapFileItem = null;
    //	JMenuItem newCsvToV3MapFileItem = null;
    //	JMenuItem newCSVSpecificationItem = null;
    //JMenuItem newCsvToSdtmSpecificationItem1 = null;
    //	JMenuItem newHSMFileItem = null;
    //	JMenuItem newHL7V3MessageItem = null;
    //JMenuItem newHSMFileItem_1 = null;
    //	JMenuItem newHL7V3MessageItem_1 = null;
    //	JMenuItem newO2DBMapFileItem = null;
    //	JMenuItem openMapFileItem = null;
    //	JMenuItem openO2DBMapFileItem = null;
    //	JMenuItem openSDTMMapFile = null;
    //
    //	JMenuItem openCSVSpecificationItem = null;
    //
    //	JMenuItem openHSMFileItem = null;
    //
    //	JMenuItem openHL7V3MessageItem = null;
    //	JMenuItem newSDTMStructure = null;
    //	JMenuItem saveMenuItem = null;
    //
    //	JMenuItem saveAsMenuItem = null;
    //
    //	JMenuItem validateMenuItem = null;
    //
    //	JMenuItem anotateMenuItem = null;
    //
    //	JMenuItem closeMenuItem = null;
    //
    //	JMenuItem closeAllMenuItem = null;
    //	JMenuItem exitMenuItem = null;
    //	JMenu reportMenu = null;
    //	JMenuItem generateReportMenuItem = null;
    //JMenu helpMenu = null;
    //JMenuItem helpTopicMenuItem = null;
    // JMenuItem helpTopicMenuItem2 = null;
    // JMenuItem helpManageMenuItem = null; // Kisung add
    //JMenuItem aboutMenuItem = null;
    // --end of menu and menu item list.
    // --action list
    //	DefaultSaveAction defaultSaveAction = null;
    //
    //	DefaultSaveAsAction defaultSaveAsAction = null;
    //
    //	DefaultValidateAction defaultValidateAction = null;
    //
    //	DefaultAnotateAction defaultAnotateAction = null;
    //
    //	DefaultCloseAction defaultCloseAction = null;
    //
    //	CloseAllAction closeAllAction = null;
    //	ExitAction exitAction = null;
    //AboutAction aboutAction = null;
    //HelpTopicAction helpTopicAction = null;
    // HelpAction2 helpTopicAction2 = null;
    // HelpContentManageAction helpManageAction = null; // Kisung add
    //NewMapFileAction newMapAction = null;
    //	NewMapFileAction newMapAction1 = null;
    //	OpenMapFileAction openMapAction = null;
    //	gov.nih.nci.caadapter.ui.mapping.mms.actions.OpenObjectToDbMapAction openO2DBMapAction = null;
    //	gov.nih.nci.caadapter.ui.mapping.sdtm.actions.OpenSDTMMapAction openSDTMMapAction = null;
    //	NewCsvSpecificationAction newCSVSpecificationAction = null;
    //NewCsvSpecificationAction newCSVSpecificationActionDbToSdtm = null;
    //	OpenCsvSpecificationAction openCSVSpecificationAction = null;
    //	NewHSMAction newHSMAction = null;
    //	NewHSMAction newHSMAction_1 = null;
    //	OpenHSMAction openHSMAction = null;
    //	NewHL7V3MessageAction newHL7V3MessageAction = null;
    //	NewHL7V3MessageAction newHL7V3MessageAction_1 = null;
    //	OpenHL7V3MessageAction openHL7V3MessageAction = null;
    //	NewObject2DBMapAction newObject2DBMapAction = null;
    //	V2V3MapAction newV2V3MapAction = null;
    //	Database2SDTMAction newDB2SDTMAction = null;
    //
    //	NewSDTMStructureAction newSDTMStructureAction = null;
    // --end of action list.
    // key: ActionConstants, value: soft reference of the action.

    private Map<String, AbstractContextAction> actionMap;

    private Map<String, JMenuItem> menuItemMap;

    private Map<String, JMenu> menuMap;

    //java.util.prefs.Preferences prefs;
    private static HashMap prefs;

    public MainMenuBar(MainFrame mf)//ContextManager contextManager) {
    {//this.contextManager = contextManager;
        this.mainFrame = mf;//contextManager.getMainFrame();
        initialize();
    }

    public static HashMap getCaAdapterPreferences()
    {
        return prefs;
    }

    private void initialize()
    {
        actionMap = Collections.synchronizedMap(new HashMap<String, AbstractContextAction>());
        menuItemMap = Collections.synchronizedMap(new HashMap<String, JMenuItem>());
        menuMap = Collections.synchronizedMap(new HashMap<String, JMenu>());
        readPreferencesMap();
        // prefs = java.util.prefs.Preferences.userNodeForPackage(this.getClass());
        add(constructFileMenu());
        if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_QUERYBUILDER_MENU_ACTIVATED))
        {
            System.out.println("query builder activated");
            //add(constructOpenQueryBuilderMenu());
            add(constructPreferenceMenu());
        } else
        {
            System.out.println("query builder de-activated");
        }
        if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_HELP_MENU_ACTIVATED))
        {
            add(constructHelpMenu());
            System.out.println("help menu activated");
        } else
        {
            System.out.println("help menu de-activated");
        }
        add(constructReportMenu());
        //		constructActionMap();
    }

    private void readPreferencesMap()
    {
        try
        {
            FileInputStream f_out = new FileInputStream(System.getProperty("user.home") + "\\.caadapter");
            ObjectInputStream obj_out = new ObjectInputStream(f_out);
            prefs = (HashMap) obj_out.readObject();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
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
            Log.logWarning(this.getClass(), msg);
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
        DefaultAnotateAction defaultAnotateAction = new DefaultAnotateAction(mainFrame);
        JMenuItem anotateMenuItem = new JMenuItem(defaultAnotateAction);
        actionMap.put(ActionConstants.ANOTATE, defaultAnotateAction);
        menuItemMap.put(ActionConstants.ANOTATE, anotateMenuItem);
        DefaultValidateAction defaultValidateAction = new DefaultValidateAction(mainFrame);
        JMenuItem validateMenuItem = new JMenuItem(defaultValidateAction);
        actionMap.put(ActionConstants.VALIDATE, defaultValidateAction);
        menuItemMap.put(ActionConstants.VALIDATE, validateMenuItem);
        DefaultCloseAction defaultCloseAction = new DefaultCloseAction(mainFrame);
        JMenuItem closeMenuItem = new JMenuItem(defaultCloseAction);
        actionMap.put(ActionConstants.CLOSE, defaultCloseAction);
        menuItemMap.put(ActionConstants.CLOSE, closeMenuItem);
        CloseAllAction closeAllAction = new CloseAllAction(mainFrame);
        JMenuItem closeAllMenuItem = new JMenuItem(closeAllAction);
        actionMap.put(ActionConstants.CLOSE_ALL, closeAllAction);
        menuItemMap.put(ActionConstants.CLOSE_ALL, closeAllMenuItem);
        ExitAction exitAction = new ExitAction(mainFrame);
        JMenuItem exitMenuItem = new JMenuItem(exitAction);
        actionMap.put(ActionConstants.EXIT, exitAction);
        menuItemMap.put(ActionConstants.EXIT, exitMenuItem);
        // link them together
        JMenu fileMenu = new JMenu(MenuConstants.FILE_MENU_NAME);
        fileMenu.setMnemonic('F');
        fileMenu.add(constructNewMenu());
        fileMenu.addSeparator();
        fileMenu.add(constructOpenMenu());
        fileMenu.addSeparator();
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(validateMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(closeMenuItem);
        fileMenu.add(closeAllMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        menuMap.put(MenuConstants.FILE_MENU_NAME, fileMenu);
        defaultSaveAction.setEnabled(false);
        defaultSaveAsAction.setEnabled(false);
        defaultValidateAction.setEnabled(false);
        defaultAnotateAction.setEnabled(false);
        defaultCloseAction.setEnabled(false);
        closeAllAction.setEnabled(false);
        return fileMenu;
    }

    private JMenu constructReportMenu()
    {
        JMenu reportMenu = new JMenu(MenuConstants.REPORT_MENU_NAME);
        reportMenu.setMnemonic('R');
        reportMenu.setEnabled(false);
        // first is just place holder.
        JMenuItem generateReportMenuItem = new JMenuItem((Action) null);
        reportMenu.add(generateReportMenuItem);
        menuItemMap.put(ActionConstants.GENERATE_REPORT, generateReportMenuItem);
        menuMap.put(MenuConstants.REPORT_MENU_NAME, reportMenu);
        return reportMenu;
    }

    private JMenu constructHelpMenu()
    {
        AboutAction aboutAction = new AboutAction(mainFrame);
        HelpTopicAction helpTopicAction = new HelpTopicAction(mainFrame);
        JMenu helpMenu = new JMenu(MenuConstants.HELP_MENU_NAME);
        helpMenu.setMnemonic('H');
        JMenuItem helpAboutItem = new JMenuItem(aboutAction);
        helpMenu.add(helpAboutItem);
        JMenuItem helpTopicItem = new JMenuItem(helpTopicAction);
        helpMenu.add(helpTopicItem);// eric addied
        actionMap.put(ActionConstants.HELP_TOPIC, helpTopicAction);
        menuItemMap.put(ActionConstants.HELP_TOPIC, helpTopicItem);
        actionMap.put(ActionConstants.ABOUT, aboutAction);
        menuItemMap.put(ActionConstants.ABOUT, helpAboutItem);
        menuMap.put(MenuConstants.HELP_MENU_NAME, helpMenu);
        return helpMenu;
    }

    private JMenu constructNewMenu()
    {
        JMenu newGroup = new JMenu("      " + MenuConstants.NEW_MENU_NAME);
        System.out.println("Activated components:\n" + CaadapterUtil.getAllActivatedComponents());
        menuMap.put(MenuConstants.NEW_MENU_NAME, newGroup);
        if (CaadapterUtil.getAllActivatedComponents().isEmpty())
        {
            //set csvToV3 as default
            newGroup.add(constructNewCSVTOV3Menu());
        } else
        {
            //load each activated component
            if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_HL7_TRANSFORMATION_ACTIVATED))
            {
                newGroup.add(constructNewCSVTOV3Menu());
            }
            if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_HL7_CSV_TRANSFORMATION_ACTIVATED))
            {
                newGroup.add(constructNewV3TOCSVMenu());
            }
            if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_HL7_V2V3_CONVERSION_ACTIVATED))
            {
                newGroup.add(constructNewV2TOV3Menu());
            }
            if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_SDTM_TRANSFORMATION_ACTIVATED))
            {
                newGroup.add(constructNewDatabaseTOSDTMMenu());
            }
            if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_MODEL_MAPPING_ACTIVATED))
            {
                newGroup.add(constructNewObjectTODatabaseMenu());
            }
        }
        return newGroup;
    }

    private JMenu constructNewObjectTODatabaseMenu()
    {
        JMenu newGroup = new JMenu("Model Mapping Service");
        NewObject2DBMapAction newObject2DBMapAction = new NewObject2DBMapAction(mainFrame);
        JMenuItem newO2DBMapFileItem = new JMenuItem(newObject2DBMapAction);
        newGroup.add(newO2DBMapFileItem);
        actionMap.put(ActionConstants.NEW_O2DB_MAP_FILE, newObject2DBMapAction);
        menuItemMap.put(ActionConstants.NEW_O2DB_MAP_FILE, newO2DBMapFileItem);
        return newGroup;
    }

    private JMenu constructPreferenceMenu()
    {
        OpenPreferenceAction _preference = new OpenPreferenceAction(mainFrame, prefs);
        JMenu _qb = new JMenu("Tools");
        JMenuItem _menuItem = new JMenuItem(_preference);
        _qb.add(_menuItem);
        return _qb;
    }

    private JMenu constructNewCSVTOV3Menu()
    {
        JMenu newGroup = new JMenu("CSV To HL7 v3 Mapping and Transformation Service");
        NewMapFileAction newMapAction = new NewMapFileAction(mainFrame);
        JMenuItem newCsvToV3MapItem = new JMenuItem(newMapAction);
        actionMap.put(ActionConstants.NEW_MAP_FILE, newMapAction);
        menuItemMap.put(ActionConstants.NEW_MAP_FILE, newCsvToV3MapItem);
        NewCsvSpecificationAction newCSVSpecificationAction = new NewCsvSpecificationAction(mainFrame);
        JMenuItem newCSVSpecificationItem = new JMenuItem(newCSVSpecificationAction);
        actionMap.put(ActionConstants.NEW_CSV_SPEC, newCSVSpecificationAction);
        menuItemMap.put(ActionConstants.NEW_CSV_SPEC, newCSVSpecificationItem);
        NewHSMAction newHSMAction = new NewHSMAction(mainFrame);
        JMenuItem newHSMFileItem = new JMenuItem(newHSMAction);
        actionMap.put(ActionConstants.NEW_HSM_FILE, newHSMAction);
        menuItemMap.put(ActionConstants.NEW_HSM_FILE, newHSMFileItem);
        NewHL7V3MessageAction newHL7V3MessageAction = new NewHL7V3MessageAction(mainFrame);
        JMenuItem newHL7V3MessageItem = new JMenuItem(newHL7V3MessageAction);
        actionMap.put(ActionConstants.NEW_HL7_V3_MESSAGE, newHL7V3MessageAction);
        menuItemMap.put(ActionConstants.NEW_HL7_V3_MESSAGE, newHL7V3MessageItem);
        newGroup.setMnemonic('N');
        newGroup.add(newCSVSpecificationItem);
        newGroup.add(newHSMFileItem);
        newGroup.add(newCsvToV3MapItem);
        newGroup.add(newHL7V3MessageItem);
        return newGroup;
    }

    private JMenu constructNewV2TOV3Menu()
    {
        NewMapFileAction newMapAction1 = new NewMapFileAction(mainFrame);
        JMenuItem newCsvToV3MapItem = new JMenuItem(newMapAction1);
        JMenu newGroup = new JMenu("HL7 v2 to HL7 v3 Conversion Service");
        V2V3MapAction newV2ToV3MapAction = new V2V3MapAction(mainFrame);
        NewHSMAction newHSMAction_1 = new NewHSMAction(mainFrame);
        JMenuItem newHSMFileItem_1 = new JMenuItem(newHSMAction_1);
        NewHL7V3MessageAction newHL7V3MessageAction_1 = new NewHL7V3MessageAction(mainFrame);
        JMenuItem newHL7V3MessageItem_1 = new JMenuItem(newHL7V3MessageAction_1);
        newGroup.add(new JMenuItem(newV2ToV3MapAction));
        newGroup.add(newHSMFileItem_1);
        newGroup.add(newCsvToV3MapItem);
        newGroup.add(newHL7V3MessageItem_1);
        return newGroup;
    }

    private JMenu constructNewV3TOCSVMenu()
    {
        JMenu newGroup = new JMenu("HL7 v3 To CSV Transformation Service");
        NewHL7V3MessageAction newHL7V3MessageAction = new NewHL7V3MessageAction(ActionConstants.NEW_HL7_V3_TO_CSV, mainFrame);
        JMenuItem newHL7V3ToCSVItem = new JMenuItem(newHL7V3MessageAction);
        actionMap.put(ActionConstants.NEW_HL7_V3_TO_CSV, newHL7V3MessageAction);
        menuItemMap.put(ActionConstants.NEW_HL7_V3_TO_CSV, newHL7V3ToCSVItem);
        newGroup.add(newHL7V3ToCSVItem);
        return newGroup;
    }

    private JMenu constructNewDatabaseTOSDTMMenu()
    {
        NewCsvSpecificationAction newCSVSpecificationActionDbToSdtm = new NewCsvSpecificationAction(mainFrame);
        JMenuItem newCsvToSdtmSpecificationItem1 = new JMenuItem(newCSVSpecificationActionDbToSdtm);
        JMenu newGroup = new JMenu("SDTM Mapping and Transformation Service");
        Database2SDTMAction newDB2SDTMAction = new Database2SDTMAction(mainFrame);
        NewSDTMStructureAction newSDTMStructureAction = new NewSDTMStructureAction(mainFrame);
        newGroup.add(newCsvToSdtmSpecificationItem1);
        newGroup.add(new JMenuItem(newDB2SDTMAction));
        newGroup.add(new JMenuItem(newSDTMStructureAction));
        return newGroup;
    }

    private JMenu constructOpenMenu()
    {
        // construct actions and menu items.
        OpenMapFileAction openMapAction = new OpenMapFileAction(mainFrame);
        JMenuItem openMapFileItem = new JMenuItem(openMapAction);
        actionMap.put(ActionConstants.OPEN_MAP_FILE, openMapAction);
        menuItemMap.put(ActionConstants.OPEN_MAP_FILE, openMapFileItem);
        OpenObjectToDbMapAction openO2DBMapAction = new OpenObjectToDbMapAction(mainFrame);
        JMenuItem openO2DBMapFileItem = new JMenuItem(openO2DBMapAction);
        actionMap.put(ActionConstants.OPEN_O2DB_MAP_FILE, openO2DBMapAction);
        menuItemMap.put(ActionConstants.OPEN_O2DB_MAP_FILE, openO2DBMapFileItem);
        OpenSDTMMapAction openSDTMMapAction = new OpenSDTMMapAction(mainFrame);
        JMenuItem openSDTMMapFile = new JMenuItem(openSDTMMapAction);
        OpenCsvSpecificationAction openCSVSpecificationAction = new OpenCsvSpecificationAction(mainFrame);
        JMenuItem openCSVSpecificationItem = new JMenuItem(openCSVSpecificationAction);
        actionMap.put(ActionConstants.OPEN_CSV_SPEC, openCSVSpecificationAction);
        menuItemMap.put(ActionConstants.OPEN_CSV_SPEC, openCSVSpecificationItem);
        OpenHSMAction openHSMAction = new OpenHSMAction(mainFrame);
        JMenuItem openHSMFileItem = new JMenuItem(openHSMAction);
        actionMap.put(ActionConstants.OPEN_HSM_FILE, openHSMAction);
        menuItemMap.put(ActionConstants.OPEN_HSM_FILE, openHSMFileItem);
        OpenHL7V3MessageAction openHL7V3MessageAction = new OpenHL7V3MessageAction(mainFrame);
        JMenuItem openHL7V3MessageItem = new JMenuItem(openHL7V3MessageAction);
        actionMap.put(ActionConstants.OPEN_HL7_V3_MESSAGE, openHL7V3MessageAction);
        menuItemMap.put(ActionConstants.OPEN_HL7_V3_MESSAGE, openHL7V3MessageItem);
        // link them together
        JMenu openMenu = new JMenu("      " + MenuConstants.OPEN_MENU_NAME);
        openMenu.setMnemonic('O');
        if (CaadapterUtil.getAllActivatedComponents().isEmpty())
        {
            //set csvToV3 as default
            openMenu.add(openCSVSpecificationItem);
            openMenu.add(openHSMFileItem);
            openMenu.add(openMapFileItem);
        } else
        {
            //load each activated component
            if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_HL7_TRANSFORMATION_ACTIVATED))
            {
                openMenu.add(openCSVSpecificationItem);
                openMenu.add(openHSMFileItem);
                openMenu.add(openMapFileItem);
            }
            if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_HL7_V2V3_CONVERSION_ACTIVATED))
            {
                openMenu.add(openCSVSpecificationItem);
                openMenu.add(openHSMFileItem);
                openMenu.add(openMapFileItem);
            }
            if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_SDTM_TRANSFORMATION_ACTIVATED))
            {
                openMenu.add(openSDTMMapFile);
            }
            if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_MODEL_MAPPING_ACTIVATED))
            {
                openMenu.add(openO2DBMapFileItem);
            }
        }
        return openMenu;
    }

    /* (non-Javadoc)
      * @see gov.nih.nci.caadapter.ui.main.AbstractMenuBar#resetMenus(boolean)
      */
    public void resetMenus(boolean hasActiveDocument)
    {// provide structure for
        // more menus to be
        // reset
        resetFileMenu(hasActiveDocument);
        resetReportMenu(hasActiveDocument);
    }

    private void resetFileMenu(boolean hasActiveDocument)
    {
        resetNewSubMenu(hasActiveDocument);
        resetOpenSubMenu(hasActiveDocument);
        JMenuItem saveMenuItem = menuItemMap.get(ActionConstants.SAVE);
        JMenuItem saveAsMenuItem = menuItemMap.get(ActionConstants.SAVE_AS);
        JMenuItem validateMenuItem = menuItemMap.get(ActionConstants.VALIDATE);
        JMenuItem closeMenuItem = menuItemMap.get(ActionConstants.CLOSE);
        JMenuItem closeAllMenuItem = menuItemMap.get(ActionConstants.CLOSE_ALL);
        JMenuItem anotateMenuItem = menuItemMap.get(ActionConstants.ANOTATE);
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
        anotateMenuItem.getAction().setEnabled(false);
        validateMenuItem.getAction().setEnabled(false);
        closeMenuItem.getAction().setEnabled(false);
        closeAllMenuItem.getAction().setEnabled(hasActiveDocument);
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
            resetMenuItem(ActionConstants.NEW_CSV_SPEC, true);
            //			newHSMFileItem.getAction().setEnabled(true);
            //			menuItemMap.get(ActionConstants.NEW_HSM_FILE).getAction().setEnabled(true);
            resetMenuItem(ActionConstants.NEW_HSM_FILE, true);
            //			newHL7V3MessageItem.getAction().setEnabled(true);
            //			menuItemMap.get(ActionConstants.NEW_HL7_V3_MESSAGE).getAction().setEnabled(true);
            resetMenuItem(ActionConstants.NEW_HL7_V3_MESSAGE, true);
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
            //			openMapFileItem.getAction().setEnabled(true);
            resetMenuItem(ActionConstants.OPEN_MAP_FILE, true);
            //			menuItemMap.get(ActionConstants.OPEN_MAP_FILE).getAction().setEnabled(true);
            //			openCSVSpecificationItem.getAction().setEnabled(true);
            resetMenuItem(ActionConstants.OPEN_CSV_SPEC, true);
            //			menuItemMap.get(ActionConstants.OPEN_CSV_SPEC).getAction().setEnabled(true);
            //			openHSMFileItem.getAction().setEnabled(true);
            resetMenuItem(ActionConstants.OPEN_HSM_FILE, true);
            //			menuItemMap.get(ActionConstants.OPEN_HSM_FILE).getAction().setEnabled(true);
            //			openHL7V3MessageItem.getAction().setEnabled(true);
            resetMenuItem(ActionConstants.OPEN_HL7_V3_MESSAGE, true);
            //			menuItemMap.get(ActionConstants.OPEN_HL7_V3_MESSAGE).getAction().setEnabled(true);
        }
    }

    private void resetReportMenu(boolean hasActiveDocument)
    {
        if (!hasActiveDocument)
        {
            resetMenuItem(ActionConstants.GENERATE_REPORT, false);
            //			Action a = menuItemMap.get(ActionConstants.GENERATE_REPORT).getAction();//. generateReportMenuItem.getAction();
            //			if (a != null) {
            //				a.setEnabled(false);
            //			}
        }
    }

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

    //	private void constructActionMap()
    //	{
    //		actionMap.put(ActionConstants.SAVE, defaultSaveAction);
    //		menuItemMap.put(ActionConstants.SAVE, saveMenuItem);
    //		actionMap.put(ActionConstants.SAVE_AS, defaultSaveAsAction);
    //		menuItemMap.put(ActionConstants.SAVE_AS, saveAsMenuItem);
    //		actionMap.put(ActionConstants.ANOTATE, defaultAnotateAction);
    //		menuItemMap.put(ActionConstants.ANOTATE, anotateMenuItem);
    //		actionMap.put(ActionConstants.VALIDATE, defaultValidateAction);
    //		menuItemMap.put(ActionConstants.VALIDATE, validateMenuItem);
    //		actionMap.put(ActionConstants.CLOSE, defaultCloseAction);
    //		menuItemMap.put(ActionConstants.CLOSE, closeMenuItem);
    //		actionMap.put(ActionConstants.CLOSE_ALL, closeAllAction);
    //		menuItemMap.put(ActionConstants.CLOSE_ALL, closeAllMenuItem);
    //		actionMap.put(ActionConstants.EXIT, exitAction);
    //		menuItemMap.put(ActionConstants.EXIT, exitMenuItem);
    //		actionMap.put(ActionConstants.ABOUT, aboutAction);
    //		menuItemMap.put(ActionConstants.ABOUT, closeMenuItem);
    //		actionMap.put(ActionConstants.HELP_TOPIC, helpTopicAction);
    //		menuItemMap.put(ActionConstants.HELP_TOPIC, anotateMenuItem);
    //
    //		actionMap.put(ActionConstants.NEW_O2DB_MAP_FILE, newObject2DBMapAction);
    //		menuItemMap.put(ActionConstants.NEW_O2DB_MAP_FILE, this.newO2DBMapFileItem);
    //		actionMap.put(ActionConstants.NEW_MAP_FILE, newMapAction);
    //		menuItemMap.put(ActionConstants.NEW_MAP_FILE, newCsvToV3MapFileItem);
    //		actionMap.put(ActionConstants.OPEN_MAP_FILE, openMapAction);
    //		menuItemMap.put(ActionConstants.OPEN_MAP_FILE, this.openMapFileItem);
    //		actionMap.put(ActionConstants.OPEN_O2DB_MAP_FILE, openO2DBMapAction);
    //		menuItemMap.put(ActionConstants.OPEN_O2DB_MAP_FILE, this.openO2DBMapFileItem);
    //		actionMap.put(ActionConstants.NEW_CSV_SPEC, newCSVSpecificationAction);
    //		menuItemMap.put(ActionConstants.NEW_CSV_SPEC, this.newCSVSpecificationItem);
    //		actionMap.put(ActionConstants.OPEN_CSV_SPEC, openCSVSpecificationAction);
    //		menuItemMap.put(ActionConstants.OPEN_CSV_SPEC, this.openCSVSpecificationItem);
    //		actionMap.put(ActionConstants.NEW_HSM_FILE, newHSMAction);
    //		menuItemMap.put(ActionConstants.NEW_HSM_FILE, this.newHSMFileItem);
    //		actionMap.put(ActionConstants.OPEN_HSM_FILE, openHSMAction);
    //		menuItemMap.put(ActionConstants.OPEN_HSM_FILE, this.openHSMFileItem);
    //		actionMap.put(ActionConstants.NEW_HL7_V3_MESSAGE, newHL7V3MessageAction);
    //		menuItemMap.put(ActionConstants.NEW_HL7_V3_MESSAGE, this.newHL7V3MessageItem);
    //		actionMap.put(ActionConstants.OPEN_HL7_V3_MESSAGE, openHL7V3MessageAction);
    //		menuItemMap.put(ActionConstants.OPEN_HL7_V3_MESSAGE, this.openHL7V3MessageItem);
    // actionMap.put(ActionConstants.GENERATE_REPORT,
    // generateReportMenuItem);
    //	}
}
/**
 * HISTORY : $Log: not supported by cvs2svn $
 * HISTORY : Revision 1.9  2007/07/26 20:02:38  jayannah
 * HISTORY : Changes for preferences menu
 * HISTORY :
 * HISTORY : Revision 1.7  2007/07/23 14:03:25  wangeug
 * HISTORY : include "HL7 V3 To CSV transformation service"
 * HISTORY :
 * HISTORY : Revision 1.6  2007/07/19 19:36:58  jayannah
 * HISTORY : Changes for 4.0 release
 * HISTORY :
 * HISTORY : Revision 1.5  2007/07/18 18:22:09  jayannah
 * HISTORY : changes for preference menu
 * HISTORY :
 * HISTORY : Revision 1.4  2007/06/14 15:43:08  wangeug
 * HISTORY : fix bug: null point exception to reset menuItem if only not all modules being activated
 * HISTORY :
 * HISTORY : Revision 1.3  2007/06/13 17:12:41  schroedn
 * HISTORY : added option for checking for help
 * HISTORY :
 * HISTORY : Revision 1.2  2007/05/09 20:56:26  jayannah
 * HISTORY : added the querybuilder menu
 * HISTORY :
 * HISTORY : Revision 1.1  2007/04/03 16:17:36  wangeug
 * HISTORY : initial loading
 * HISTORY :
 * HISTORY : Revision 1.50  2006/12/06 20:24:08  wuye
 * HISTORY : Change the command name to align the menu items
 * HISTORY :
 * HISTORY : Revision 1.49  2006/11/28 15:13:26  jayannah
 * HISTORY : Changed the order and names of the menuitems
 * HISTORY :
 * HISTORY : Revision 1.48  2006/11/15 19:57:14  wuye
 * HISTORY : reorgnize menu items
 * HISTORY :
 * HISTORY : Revision 1.47  2006/11/13 16:36:06  wuye
 * HISTORY : Reorgnize the menu structure according to customer's request.
 * HISTORY :
 * HISTORY : Revision 1.46  2006/11/10 14:45:44  wuye
 * HISTORY : Reorgnized menu
 * HISTORY :
 * HISTORY : Revision 1.45  2006/11/07 15:05:35  jayannah
 * HISTORY : Added new menu item for open SDTM map functionality
 * HISTORY : HISTORY : Revision 1.44 2006/10/30 20:30:26
 * jayannah HISTORY : caAdapter_3_2_QA_10_30_2006_V2 HISTORY : HISTORY :
 * Revision 1.43 2006/10/30 19:13:43 jayannah HISTORY : added the SDTM menui
 * items HISTORY : HISTORY : Revision 1.42 2006/10/23 16:27:28 wuye HISTORY :
 * updated open o-2-db mapping menu. HISTORY : HISTORY : Revision 1.41
 * 2006/10/03 14:17:13 jayannah HISTORY : Reformatted the source code and added
 * new menuitems to enable V2V3 mapping HISTORY : HISTORY : Revision 1.40
 * 2006/09/26 15:49:26 wuye HISTORY : Add new object 2 db mapping HISTORY :
 * HISTORY : Revision 1.39 2006/08/02 18:44:25 jiangsc HISTORY : License Update
 * HISTORY : HISTORY : Revision 1.38 2006/01/03 19:16:52 jiangsc HISTORY :
 * License Update HISTORY : HISTORY : Revision 1.37 2006/01/03 18:56:24 jiangsc
 * HISTORY : License Update HISTORY : HISTORY : Revision 1.36 2005/12/29
 * 23:06:17 jiangsc HISTORY : Changed to latest project name. HISTORY : HISTORY :
 * Revision 1.35 2005/12/29 15:39:06 chene HISTORY : Optimize imports HISTORY :
 * HISTORY : Revision 1.34 2005/12/14 21:37:19 jiangsc HISTORY : Updated license
 * information HISTORY : HISTORY : Revision 1.33 2005/11/29 16:23:54 jiangsc
 * HISTORY : Updated License HISTORY : HISTORY : Revision 1.32 2005/11/17
 * 21:04:08 umkis HISTORY : menu position exchange between help and about
 * HISTORY : HISTORY : Revision 1.31 2005/10/26 17:21:09 umkis HISTORY : #156
 * HISTORY : a)Help manager Menu is droped down HISTORY : b) Change the "About
 * HL7SDK..." option to "About caAdapter..." HISTORY : c) Change the "Help..."
 * option to "Contents and Index..." HISTORY : HISTORY : Revision 1.30
 * 2005/10/20 15:37:04 umkis HISTORY : no message HISTORY : HISTORY : Revision
 * 1.29 2005/10/04 20:51:32 jiangsc HISTORY : Validation enhancement. HISTORY :
 * HISTORY : Revision 1.28 2005/09/29 21:19:52 jiangsc HISTORY : Added Generate
 * Report action support HISTORY : HISTORY : Revision 1.27 2005/09/12 21:45:19
 * umkis HISTORY : no message HISTORY : HISTORY : Revision 1.26 2005/09/07
 * 22:26:50 umkis HISTORY : no message HISTORY : HISTORY : Revision 1.25
 * 2005/08/12 18:38:18 jiangsc HISTORY : Enable HL7 V3 Message to be saved in
 * multiple XML file. HISTORY : HISTORY : Revision 1.24 2005/08/11 22:10:38
 * jiangsc HISTORY : Open/Save File Dialog consolidation. HISTORY : HISTORY :
 * Revision 1.23 2005/08/02 22:28:57 jiangsc HISTORY : Newly enhanced
 * context-sensitive menus and toolbar. HISTORY : HISTORY : Revision 1.22
 * 2005/07/27 22:41:13 jiangsc HISTORY : Consolidated context sensitive menu
 * implementation. HISTORY : HISTORY : Revision 1.21 2005/07/27 13:57:44 jiangsc
 * HISTORY : Added the first round of HSMPanel. HISTORY : HISTORY : Revision
 * 1.20 2005/07/25 22:12:28 jiangsc HISTORY : Fixed some menu transition.
 * HISTORY : HISTORY : Revision 1.19 2005/07/25 21:56:47 jiangsc HISTORY : 1)
 * Added expand all and collapse all; HISTORY : 2) Added toolbar on the mapping
 * panel; HISTORY : 3) Consolidated menus; HISTORY :
 */
