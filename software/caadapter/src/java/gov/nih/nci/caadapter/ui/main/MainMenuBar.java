/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.main;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.WebstartUtil;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.ui.common.AbstractMenuBar;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.actions.*;
import gov.nih.nci.caadapter.ui.common.context.MenuConstants;
import gov.nih.nci.caadapter.ui.common.geneInstance.BuildGenerateHL7TestInstanceAction;
import gov.nih.nci.caadapter.ui.common.preferences.OpenPreferenceAction;
import gov.nih.nci.caadapter.ui.common.resource.BuildHL7ResourceAction;
import gov.nih.nci.caadapter.ui.help.actions.AboutAction;
import gov.nih.nci.caadapter.ui.help.actions.HelpTopicAction;
import gov.nih.nci.caadapter.ui.hl7message.actions.NewHL7V3MessageAction;
import gov.nih.nci.caadapter.ui.hl7message.actions.OpenHL7V3MessageAction;
import gov.nih.nci.caadapter.ui.mapping.GME.actions.NewXsdToXmiMapAction;
import gov.nih.nci.caadapter.ui.mapping.GME.actions.OpenXsdToXmiMapAction;
import gov.nih.nci.caadapter.ui.mapping.NewMapFileAction;
import gov.nih.nci.caadapter.ui.mapping.OpenMapFileAction;
import gov.nih.nci.caadapter.ui.mapping.catrend.actions.NewCsvToXmiMapAction;
import gov.nih.nci.caadapter.ui.mapping.catrend.actions.OpenCsvToXmiMapAction;
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

import java.awt.Event;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * This class manages the definitions and instantiations of menu items. It will
 * coordinate ContextManager class to deal with context sensitive menu
 * switches.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: altturbo $
 * @since caAdapter v1.2
 * @version    $Revision: 1.51 $
 * @date       $Date: 2009-06-03 20:39:58 $
 */
public class MainMenuBar extends AbstractMenuBar
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
        add(constructPreferenceMenu());
        if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_QUERYBUILDER_MENU_ACTIVATED))
        {
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
            NewHSMAction newHSMAction = new NewHSMAction(mainFrame);
            newHSMAction.setAuthorizationRequired(true);
            JMenuItem newHSMFileItem = new JMenuItem(newHSMAction);
            actionMap.put(ActionConstants.NEW_HSM_FILE, newHSMAction);
            menuItemMap.put(ActionConstants.NEW_HSM_FILE, newHSMFileItem);

            JMenu newCsvToHL7=constructNewCSVTOV3Menu();
            newCsvToHL7.insert(newHSMAction, 1);
            newGroup.add(newCsvToHL7);

            JMenu newV2ToHL7=constructNewV2TOV3Menu();
            newV2ToHL7.insert(newHSMAction, 0);
            newGroup.add(newV2ToHL7);

            JMenu newHL7ToCsv=constructNewV3TOCSVMenu();
            newGroup.add(newHL7ToCsv);

        }
        else
        {
            //load each activated component
            if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_HL7_TRANSFORMATION_ACTIVATED))
            {
            	NewHSMAction newHSMAction = new NewHSMAction(mainFrame);
                newHSMAction.setAuthorizationRequired(true);
                JMenuItem newHSMFileItem = new JMenuItem(newHSMAction);
                actionMap.put(ActionConstants.NEW_HSM_FILE, newHSMAction);
                menuItemMap.put(ActionConstants.NEW_HSM_FILE, newHSMFileItem);

                JMenu newCsvToHL7=constructNewCSVTOV3Menu();
                newCsvToHL7.insert(newHSMAction, 1);
                newGroup.add(newCsvToHL7);

                JMenu newV2ToHL7=constructNewV2TOV3Menu();
                newV2ToHL7.insert(newHSMAction, 0);
                newGroup.add(newV2ToHL7);

                JMenu newHL7ToCsv=constructNewV3TOCSVMenu();
                newGroup.add(newHL7ToCsv);
            }
            if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_SDTM_TRANSFORMATION_ACTIVATED))
            {
                newGroup.add(constructNewDatabaseTOSDTMMenu());
            }
            if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_MODEL_MAPPING_ACTIVATED)
            		||CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_CSV_XMI_MENU_ACTIVATED))
            {
                newGroup.add(constructNewObjectTODatabaseMenu(false));
            }
            if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_XSD_XMI_MENU_ACTIVATED))
            {
                newGroup.add(constructNewObjectTODatabaseMenu(true));
            }

        }
        return newGroup;
    }

    private JMenu constructNewObjectTODatabaseMenu(boolean isGME)
    {
        JMenu newGroup = new JMenu("Model Mapping Services");
        if (isGME)
        	newGroup=new JMenu("Global Model Exchange Services");
        if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_CSV_XMI_MENU_ACTIVATED))
        {
      	//directly add menuItem
           NewCsvToXmiMapAction newCsvToXmiMapAction=new NewCsvToXmiMapAction(mainFrame);
           JMenuItem newCsvToXmiMapFileItem  = new JMenuItem(newCsvToXmiMapAction);
           newGroup.add(newCsvToXmiMapFileItem);
           actionMap.put(ActionConstants.NEW_CSV2XMI_MAP_FILE, newCsvToXmiMapAction);
           menuItemMap.put(ActionConstants.NEW_CSV2XMI_MAP_FILE, newCsvToXmiMapFileItem);
        }
        if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_XSD_XMI_MENU_ACTIVATED))
        {
	       	NewXsdToXmiMapAction newXsdToXmiMapAction=new NewXsdToXmiMapAction(mainFrame);
	    	JMenuItem newXsdToXmiMapFileItem  = new JMenuItem(newXsdToXmiMapAction);
	    	newGroup.add(newXsdToXmiMapFileItem);
	    	actionMap.put(ActionConstants.NEW_XSD2XMI_MAP_FILE, newXsdToXmiMapAction);
	    	menuItemMap.put(ActionConstants.NEW_XSD2XMI_MAP_FILE, newXsdToXmiMapFileItem);
        }
        if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_MODEL_MAPPING_ACTIVATED))
        {
	        NewObject2DBMapAction newObject2DBMapAction = new NewObject2DBMapAction(mainFrame);
	        JMenuItem newO2DBMapFileItem = new JMenuItem(newObject2DBMapAction);
	        newGroup.add(newO2DBMapFileItem);
	        actionMap.put(ActionConstants.NEW_O2DB_MAP_FILE, newObject2DBMapAction);
	        menuItemMap.put(ActionConstants.NEW_O2DB_MAP_FILE, newO2DBMapFileItem);
        }
        return newGroup;
    }

    private JMenu constructPreferenceMenu()
    {
        OpenPreferenceAction _preference = new OpenPreferenceAction(mainFrame, CaadapterUtil.getCaAdapterPreferences());
        JMenu _qb = new JMenu("Tools");
        JMenuItem _menuItem = new JMenuItem(_preference);
        _qb.add(_menuItem);

        if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_WEBSTART_ACTIVATED)
        		||CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_XSD_XMI_MENU_ACTIVATED))
        {
        	_menuItem.setEnabled(false);
        	return _qb;
        }
        else
        {
//        	webstart user is always authorized
        	CaadapterUtil.setAuthorizedUser(true);
        }

        if(!CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_HL7_CSV_TRANSFORMATION_ACTIVATED))
        	return _qb;
        //add the build resource menu for standalone application
        BuildHL7ResourceAction buildV3=new BuildHL7ResourceAction(BuildHL7ResourceAction.COMMAND_BUILD_V3, mainFrame);
        if (!CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_HL7_CSV_TRANSFORMATION_ACTIVATED))
        	buildV3.setEnabled(false);
        if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_CSV_XMI_MENU_ACTIVATED))
        {
        	buildV3.setEnabled(false);
        }
        System.out.println("MainMenuBar.constructPreferenceMenu()...WebstartUtil.isWebstartDeployed():"+WebstartUtil.isWebstartDeployed());
        if (WebstartUtil.isWebstartDeployed())
        	buildV3.setEnabled(false);

        JMenuItem _buildV3Item = new JMenuItem(buildV3);
        _qb.add(_buildV3Item);


        String property = FileUtil.searchProperty("MainMenuIncludesGeneratingInstance");
        if ((property != null)&&(property.trim().equalsIgnoreCase("true")))
        {
            Enumeration<URL> fileURLs = null;
            try
            {
                String name = "instanceGen/changeList.txt";
                fileURLs= ClassLoader.getSystemResources(name);
            }
            catch(IOException ie)
            {
                fileURLs = null;
            }
            if (fileURLs != null)
            {

                while(fileURLs.hasMoreElements())
                {
                    URL fileURL = fileURLs.nextElement();

                    String url = fileURL.toString().trim();

                    if (url.equals("")) continue;

                    BuildGenerateHL7TestInstanceAction buildTestInstance=new BuildGenerateHL7TestInstanceAction(BuildGenerateHL7TestInstanceAction.COMMAND_Generate_Test_Instance, mainFrame);
                    //if (!CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_HL7_V2V3_CONVERSION_ACTIVATED))
                    //	buildTestInstance.setEnabled(false);
                    JMenuItem _buildTestInstance = new JMenuItem(buildTestInstance);
                    _qb.add(_buildTestInstance);
                    break;
                }
            }
        }
        return _qb;
    }

    private JMenu constructNewCSVTOV3Menu()
    {
    	//user should be authorized to use HL7 artifacts
        JMenu newGroup = new JMenu("CSV to HL7 V3 Mapping and Transformation Service");
        NewMapFileAction newMapAction = new NewMapFileAction(mainFrame);
        newMapAction.setAuthorizationRequired(true);
        JMenuItem newCsvToV3MapItem = new JMenuItem(newMapAction);
        actionMap.put(ActionConstants.NEW_CSV_TO_HL7_MAP_FILE, newMapAction);
        menuItemMap.put(ActionConstants.NEW_CSV_TO_HL7_MAP_FILE, newCsvToV3MapItem);
        NewCsvSpecificationAction newCSVSpecificationAction = new NewCsvSpecificationAction(mainFrame);
        JMenuItem newCSVSpecificationItem = new JMenuItem(newCSVSpecificationAction);
        actionMap.put(ActionConstants.NEW_CSV_SPEC, newCSVSpecificationAction);
        menuItemMap.put(ActionConstants.NEW_CSV_SPEC, newCSVSpecificationItem);
        NewHL7V3MessageAction newHL7V3MessageAction = new NewHL7V3MessageAction(ActionConstants.NEW_CSV_TO_HL7_V3_MESSAGE,mainFrame);
        newHL7V3MessageAction.setAuthorizationRequired(true);
        JMenuItem newHL7V3MessageItem = new JMenuItem(newHL7V3MessageAction);
        actionMap.put(ActionConstants.NEW_CSV_TO_HL7_V3_MESSAGE, newHL7V3MessageAction);
        menuItemMap.put(ActionConstants.NEW_CSV_TO_HL7_V3_MESSAGE, newHL7V3MessageItem);
        newGroup.setMnemonic('N');
        newGroup.add(newCSVSpecificationItem);
        newGroup.add(newCsvToV3MapItem);
        newGroup.add(newHL7V3MessageItem);
        return newGroup;
    }

    private JMenu constructNewV2TOV3Menu()
    {
    	//user should be authorized to use HL7 artifacts
        JMenu newGroup = new JMenu("HL7 V2 to HL7 V3 Mapping and Transformation Service");

        NewMapFileAction newMapAction = new NewMapFileAction(ActionConstants.NEW_V2_TO_V3_MAP_FILE, mainFrame);
        newMapAction.setAuthorizationRequired(true);
        JMenuItem newCsvToV3MapItem = new JMenuItem(newMapAction);
        actionMap.put(ActionConstants.NEW_V2_TO_V3_MAP_FILE, newMapAction);
        menuItemMap.put(ActionConstants.NEW_V2_TO_V3_MAP_FILE, newCsvToV3MapItem);

        NewHL7V3MessageAction newHL7V3MessageAction = new NewHL7V3MessageAction(ActionConstants.NEW_HL7_V2_TO_HL7_V3_MESSAGE, mainFrame);
        newHL7V3MessageAction.setAuthorizationRequired(true);
        JMenuItem newHL7V3MessageItem = new JMenuItem(newHL7V3MessageAction);
        actionMap.put(ActionConstants.NEW_HL7_V2_TO_HL7_V3_MESSAGE, newHL7V3MessageAction);
        menuItemMap.put(ActionConstants.NEW_HL7_V2_TO_HL7_V3_MESSAGE, newHL7V3MessageItem);
        newGroup.setMnemonic('N');
        newGroup.add(newCsvToV3MapItem);
        newGroup.add(newHL7V3MessageItem);
        return newGroup;
    }

    private JMenu constructNewV3TOCSVMenu()
    {
        JMenu newGroup = new JMenu("HL7 V3 to CSV Transformation Service");
        NewHL7V3MessageAction newHL7V3MessageAction = new NewHL7V3MessageAction(ActionConstants.NEW_HL7_V3_TO_CSV, mainFrame);
        newHL7V3MessageAction.setAuthorizationRequired(true);
        JMenuItem newHL7V3ToCSVItem = new JMenuItem(newHL7V3MessageAction);
        actionMap.put(ActionConstants.NEW_HL7_V3_TO_CSV, newHL7V3MessageAction);
        menuItemMap.put(ActionConstants.NEW_HL7_V3_TO_CSV, newHL7V3ToCSVItem);
        newGroup.add(newHL7V3ToCSVItem);
        return newGroup;
    }

    private JMenu constructNewDatabaseTOSDTMMenu()
    {


        JMenu newGroup = new JMenu("RDS Mapping and Transformation Service");
        Database2SDTMAction newDB2SDTMAction = new Database2SDTMAction(mainFrame);
        NewSDTMStructureAction newSDTMStructureAction = new NewSDTMStructureAction(mainFrame);

        NewCsvSpecificationAction newCsvSpecAtion = new NewCsvSpecificationAction(mainFrame);
        JMenuItem newCsvSpecItem = new JMenuItem(newCsvSpecAtion);
        newGroup.add(newCsvSpecItem);
        if (actionMap.get(ActionConstants.NEW_CSV_SPEC)==null)
        {
            actionMap.put(ActionConstants.NEW_CSV_SPEC, newCsvSpecAtion);
            menuItemMap.put(ActionConstants.NEW_CSV_SPEC, newCsvSpecItem);
        }
        newGroup.add(new JMenuItem(newDB2SDTMAction));
        newGroup.add(new JMenuItem(newSDTMStructureAction));
        return newGroup;
    }

    private JMenu constructOpenMenu()
    {
        // construct actions and menu items.
        OpenMapFileAction openMapAction = new OpenMapFileAction(ActionConstants.OPEN_CSV_TO_HL7_MAP_FILE, mainFrame);

        JMenuItem openCsvToV3MapFileItem = new JMenuItem(openMapAction);
        actionMap.put(ActionConstants.OPEN_CSV_TO_HL7_MAP_FILE, openMapAction);
        menuItemMap.put(ActionConstants.OPEN_CSV_TO_HL7_MAP_FILE, openCsvToV3MapFileItem);

        OpenMapFileAction openV2ToV3MapAction = new OpenMapFileAction(ActionConstants.OPEN_V2_TO_V3_MAP_FILE,mainFrame);
        JMenuItem openV2ToV3MapFileItem = new JMenuItem(openV2ToV3MapAction);
        actionMap.put(ActionConstants.OPEN_V2_TO_V3_MAP_FILE, openMapAction);
        menuItemMap.put(ActionConstants.OPEN_V2_TO_V3_MAP_FILE, openV2ToV3MapFileItem);

        OpenObjectToDbMapAction openO2DBMapAction = new OpenObjectToDbMapAction(mainFrame);

        JMenuItem openO2DBMapFileItem = new JMenuItem(openO2DBMapAction);
        actionMap.put(ActionConstants.OPEN_O2DB_MAP_FILE, openO2DBMapAction);
        menuItemMap.put(ActionConstants.OPEN_O2DB_MAP_FILE, openO2DBMapFileItem);

        OpenCsvToXmiMapAction openCsvToXmiMapAction = new OpenCsvToXmiMapAction(mainFrame);
        JMenuItem openCsvToXmiMapFileItem = new JMenuItem(openCsvToXmiMapAction);
        actionMap.put(ActionConstants.OPEN_CSV2XMI_MAP_FILE, openCsvToXmiMapAction);
        menuItemMap.put(ActionConstants.OPEN_CSV2XMI_MAP_FILE, openCsvToXmiMapFileItem);

        OpenXsdToXmiMapAction openXsdToXmiMapAction = new OpenXsdToXmiMapAction(mainFrame);
        JMenuItem openXsdToXmiMapFileItem = new JMenuItem(openXsdToXmiMapAction);
        actionMap.put(ActionConstants.OPEN_XSD2XMI_MAP_FILE, openXsdToXmiMapAction);
        menuItemMap.put(ActionConstants.OPEN_XSD2XMI_MAP_FILE, openXsdToXmiMapFileItem);

        OpenSDTMMapAction openSDTMMapAction = new OpenSDTMMapAction(mainFrame);
        JMenuItem openSDTMMapFile = new JMenuItem(openSDTMMapAction);
        OpenCsvSpecificationAction openCSVSpecificationAction = new OpenCsvSpecificationAction(mainFrame);

        JMenuItem openCSVSpecificationItem = new JMenuItem(openCSVSpecificationAction);
        actionMap.put(ActionConstants.OPEN_CSV_SPEC, openCSVSpecificationAction);
        menuItemMap.put(ActionConstants.OPEN_CSV_SPEC, openCSVSpecificationItem);
        OpenHSMAction openHSMAction = new OpenHSMAction(ActionConstants.OPEN_HSM_FILE, mainFrame);

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

        //openCSVSpecificationItem is shared by multiple module
        boolean isOpenCsvAdded=false;
        if (CaadapterUtil.getAllActivatedComponents().isEmpty())
        {
            //set csvToV3 as default
            openMenu.add(openHSMFileItem );
            openHSMFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, Event.CTRL_MASK + Event.SHIFT_MASK, false));
            openMenu.add(openCSVSpecificationItem);
            openCsvToXmiMapFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, Event.CTRL_MASK + Event.SHIFT_MASK, false));
            openMenu.add(openCsvToXmiMapFileItem);
            openCsvToV3MapFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, Event.CTRL_MASK + Event.SHIFT_MASK, false));
            openMenu.add(openCsvToV3MapFileItem);
            openV2ToV3MapFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, Event.CTRL_MASK + Event.SHIFT_MASK, false));
            openMenu.add(openV2ToV3MapFileItem);
        }
        else
        {
        	int keyStrokeIndex=0;
            //load each activated component
        	boolean isHSMAdded=false;
            if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_HL7_TRANSFORMATION_ACTIVATED))
            {
                openMenu.add(openCSVSpecificationItem);
                isOpenCsvAdded=true;
                keyStrokeIndex++;

                openHSMFileItem.setAccelerator(KeyStroke.getKeyStroke(findKeyStrokeIndex(keyStrokeIndex), Event.CTRL_MASK + Event.SHIFT_MASK, false));
                openMenu.add(openHSMFileItem);
                isHSMAdded=true;
                keyStrokeIndex++;
                openCsvToV3MapFileItem.setAccelerator(KeyStroke.getKeyStroke(findKeyStrokeIndex(keyStrokeIndex), Event.CTRL_MASK + Event.SHIFT_MASK, false));
                openMenu.add(openCsvToV3MapFileItem);
                keyStrokeIndex++;

                openV2ToV3MapFileItem.setAccelerator(KeyStroke.getKeyStroke(findKeyStrokeIndex(keyStrokeIndex), Event.CTRL_MASK + Event.SHIFT_MASK, false));
                openMenu.add(openV2ToV3MapFileItem);
                keyStrokeIndex++;
            }

            if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_HL7_V2V3_CONVERSION_ACTIVATED))
            {
            	if (!isOpenCsvAdded)
            	{
            		openMenu.add(openCSVSpecificationItem);
            		isOpenCsvAdded=true;
            		keyStrokeIndex++;
            	}
            	if(!isHSMAdded)
            	{
	                openMenu.add(openHSMFileItem);
	                keyStrokeIndex++;
	                openV2ToV3MapFileItem.setAccelerator(KeyStroke.getKeyStroke(findKeyStrokeIndex(keyStrokeIndex), Event.CTRL_MASK + Event.SHIFT_MASK, false));
	                openMenu.add(openV2ToV3MapFileItem);
	                keyStrokeIndex++;
            	}
            }
            if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_SDTM_TRANSFORMATION_ACTIVATED))
            {
            	if (!isOpenCsvAdded)
            	{
            		openMenu.add(openCSVSpecificationItem);
            		isOpenCsvAdded=true;
            		keyStrokeIndex++;
            	}
            	openSDTMMapFile.setAccelerator(KeyStroke.getKeyStroke(findKeyStrokeIndex(keyStrokeIndex), Event.CTRL_MASK + Event.SHIFT_MASK, false));
                openMenu.add(openSDTMMapFile);
                keyStrokeIndex++;
            }
            if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_CSV_XMI_MENU_ACTIVATED))
            {
            	//only add the required menuItem
            	if (!isOpenCsvAdded)
            	{
            		openMenu.add(openCSVSpecificationItem);
            		isOpenCsvAdded=true;
            		keyStrokeIndex++;
            	}
            	openCsvToXmiMapFileItem.setAccelerator(KeyStroke.getKeyStroke(findKeyStrokeIndex(keyStrokeIndex), Event.CTRL_MASK + Event.SHIFT_MASK, false));
            	openMenu.add(openCsvToXmiMapFileItem);
            	keyStrokeIndex++;
            }

            if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_XSD_XMI_MENU_ACTIVATED))
            {
            	openXsdToXmiMapFileItem.setAccelerator(KeyStroke.getKeyStroke(findKeyStrokeIndex(keyStrokeIndex), Event.CTRL_MASK + Event.SHIFT_MASK, false));
            	openMenu.add( openXsdToXmiMapFileItem);
            	keyStrokeIndex++;
            }
            if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_MODEL_MAPPING_ACTIVATED))
            {
            	openO2DBMapFileItem.setAccelerator(KeyStroke.getKeyStroke(findKeyStrokeIndex(keyStrokeIndex), Event.CTRL_MASK + Event.SHIFT_MASK, false));
                openMenu.add(openO2DBMapFileItem);
                keyStrokeIndex++;
            }
        }
        return openMenu;
    }
private int findKeyStrokeIndex(int indx)
{
	if (indx==0)
		return KeyEvent.VK_0;
	else if (indx==1)
		return KeyEvent.VK_1;
	else if (indx==2)
		return KeyEvent.VK_2;
	else if (indx==3)
		return KeyEvent.VK_3;
	else if (indx==4)
		return KeyEvent.VK_4;
	else if (indx==5)
		return KeyEvent.VK_5;
	else if (indx==6)
		return KeyEvent.VK_6;
	else if (indx==7)
		return KeyEvent.VK_7;
	else if (indx==8)
		return KeyEvent.VK_8;
	else if (indx==9)
		return KeyEvent.VK_9;
	return  KeyEvent.VK_0;
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
            resetMenuItem(ActionConstants.NEW_CSV_TO_HL7_MAP_FILE, true);
            resetMenuItem(ActionConstants.NEW_V2_TO_V3_MAP_FILE, true);
            resetMenuItem(ActionConstants.NEW_CSV_SPEC, true);
            resetMenuItem(ActionConstants.NEW_XSD2XMI_MAP_FILE, true);
            resetMenuItem(ActionConstants.NEW_HSM_FILE, true);
            resetMenuItem(ActionConstants.NEW_CSV_TO_HL7_V3_MESSAGE, true);
            resetMenuItem(ActionConstants.NEW_HL7_V2_TO_HL7_V3_MESSAGE, true);
            resetMenuItem(ActionConstants.NEW_HL7_V3_TO_CSV, true);
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
        	resetMenuItem(ActionConstants.OPEN_V2_TO_V3_MAP_FILE, true);
            resetMenuItem(ActionConstants.OPEN_CSV_SPEC, true);
            resetMenuItem(ActionConstants.OPEN_HSM_FILE, true);
            resetMenuItem(ActionConstants.OPEN_HL7_V3_MESSAGE, true);
        }
    }

    private void resetReportMenu(boolean hasActiveDocument)
    {
        if (!hasActiveDocument)
        {
            resetMenuItem(ActionConstants.GENERATE_REPORT, false);
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
}
/**
 * HISTORY : $Log: not supported by cvs2svn $
 * HISTORY : Revision 1.50  2009/05/21 14:07:57  altturbo
 * HISTORY : add property MainMenuIncludesGeneratingInstance
 * HISTORY :
 * HISTORY : Revision 1.49  2009/04/24 18:25:10  wangeug
 * HISTORY : clean ActionConstants class
 * HISTORY :
 * HISTORY : Revision 1.48  2009/03/26 13:50:52  wangeug
 * HISTORY : include VOM for webstart release
 * HISTORY :
 * HISTORY : Revision 1.47  2009/03/25 18:00:25  wangeug
 * HISTORY : VOM integration
 * HISTORY :
 * HISTORY : Revision 1.46  2009/02/26 19:43:53  wangeug
 * HISTORY : enable action based on  mapping  type; activate "HL7 V3 to CSV" menu items
 * HISTORY :
 * HISTORY : Revision 1.45  2009/02/03 19:02:07  wangeug
 * HISTORY : separate menu item group: csv to HL7 V3 and HL7 V2 to HL7 V3
 * HISTORY :
 * HISTORY : Revision 1.44  2009/02/03 15:49:21  wangeug
 * HISTORY : separate menu item group: csv to HL7 V3 and HL7 V2 to HL7 V3
 * HISTORY :
 * HISTORY : Revision 1.43  2008/11/10 20:55:12  wangeug
 * HISTORY : GME release:version 4.2
 * HISTORY :
 * HISTORY : Revision 1.42  2008/11/10 20:13:25  wangeug
 * HISTORY : GME release:version 4.2
 * HISTORY :
 * HISTORY : Revision 1.41  2008/10/24 19:38:38  wangeug
 * HISTORY : transfer a v2 message into v3 message using SUN v2 schema
 * HISTORY :
 * HISTORY : Revision 1.40  2008/09/26 20:35:27  linc
 * HISTORY : Updated according to code standard.
 * HISTORY :
 * HISTORY : Revision 1.39  2008/09/23 15:19:42  wangeug
 * HISTORY : caAdapter 4.2 alpha release
 * HISTORY :
 * HISTORY : Revision 1.38  2008/09/08 15:15:16  linc
 * HISTORY : UI fixup for MMS 4.1
 * HISTORY :
 * HISTORY : Revision 1.37  2008/06/17 17:23:47  wangeug
 * HISTORY : setup main menu bar
 * HISTORY :
 * HISTORY : Revision 1.36  2008/06/13 15:55:20  wangeug
 * HISTORY : exclude GME module from caAdapter 4.1 beta release
 * HISTORY :
 * HISTORY : Revision 1.35  2008/06/09 19:53:53  phadkes
 * HISTORY : New license text replaced for all .java files.
 * HISTORY :
 * HISTORY : Revision 1.34  2008/06/06 20:39:54  wangeug
 * HISTORY : setup main menu bar
 * HISTORY :
 * HISTORY : Revision 1.33  2008/05/30 01:02:50  umkis
 * HISTORY : if v2v3 menu is activatted, FileUtil.getV2ResourceMetaDataLoader() will be automatically initialized.
 * HISTORY :
 * HISTORY : Revision 1.32  2008/05/29 02:31:03  umkis
 * HISTORY : 'Generate HL7 v3 Test Instance' menu will be shown in only case that resourceInstanceGen.zip is exist in the lib directory.
 * HISTORY :
 * HISTORY : Revision 1.31  2008/04/23 18:12:02  umkis
 * HISTORY : H3S test instance generator install onto ManuBar
 * HISTORY :
 * HISTORY : Revision 1.30  2008/02/28 18:13:55  schroedn
 * HISTORY : xsd added to menu
 * HISTORY :
 * HISTORY : Revision 1.29  2008/02/25 20:43:50  schroedn
 * HISTORY : Fixed grayed out menu bar for XSDtoXMI
 * HISTORY :
 * HISTORY : Revision 1.28  2008/02/04 15:09:54  schroedn
 * HISTORY : XSD to XMI Mapping - GME initial
 * HISTORY :
 * HISTORY : Revision 1.27  2008/01/04 19:40:15  wangeug
 * HISTORY : disable help menu for TDMS release
 * HISTORY :
 * HISTORY : Revision 1.26  2007/12/13 15:28:53  wangeug
 * HISTORY : support both data model and object model
 * HISTORY :
 * HISTORY : Revision 1.25  2007/11/30 17:19:43  wangeug
 * HISTORY : create CSV_TO_XMI mapping module
 * HISTORY :
 * HISTORY : Revision 1.24  2007/11/29 16:45:31  wangeug
 * HISTORY : create CSV_TO_XMI mapping module
 * HISTORY :
 * HISTORY : Revision 1.23  2007/11/29 14:26:42  wangeug
 * HISTORY : create CSV_TO_XMI mapping module
 * HISTORY :
 * HISTORY : Revision 1.22  2007/09/28 15:52:46  wangeug
 * HISTORY : disable loading V2 Resource
 * HISTORY :
 * HISTORY : Revision 1.21  2007/09/27 15:23:49  wuye
 * HISTORY : fixed for web start version
 * HISTORY :
 * HISTORY : Revision 1.20  2007/09/26 20:18:25  wangeug
 * HISTORY : view license
 * HISTORY :
 * HISTORY : Revision 1.19  2007/09/19 16:44:25  wangeug
 * HISTORY : authorized user request
 * HISTORY :
 * HISTORY : Revision 1.18  2007/09/19 13:42:11  wangeug
 * HISTORY : do not show "build resource" menu item for webstart version
 * HISTORY :
 * HISTORY : Revision 1.17  2007/09/18 15:26:33  wangeug
 * HISTORY : add tool>build resource menu
 * HISTORY :
 * HISTORY : Revision 1.16  2007/09/07 19:28:08  wangeug
 * HISTORY : relocate readPreference and savePreference methods
 * HISTORY :
 * HISTORY : Revision 1.15  2007/08/30 19:56:09  jayannah
 * HISTORY : changed the verbiage
 * HISTORY :
 * HISTORY : Revision 1.14  2007/08/13 15:22:56  wangeug
 * HISTORY : add new menu:open H3S with "xml" format
 * HISTORY :
 * HISTORY : Revision 1.13  2007/08/08 20:55:58  jayannah
 * HISTORY : Changed the verbage from SDTM to RDS
 * HISTORY :
 * HISTORY : Revision 1.12  2007/08/08 20:53:54  jayannah
 * HISTORY : Changed the verbage from SDTM to RDS
 * HISTORY :
 * HISTORY : Revision 1.11  2007/07/27 14:30:56  jayannah
 * HISTORY : Changes for preferences to check for null value
 * HISTORY :
 * HISTORY : Revision 1.10  2007/07/26 20:47:36  jayannah
 * HISTORY : provided a get method for preferences
 * HISTORY :
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
