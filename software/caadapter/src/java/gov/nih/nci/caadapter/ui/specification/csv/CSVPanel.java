/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.specification.csv;

import gov.nih.nci.caadapter.common.csv.CSVMetaParserImpl;
import gov.nih.nci.caadapter.common.csv.CSVMetaResult;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVMetaImpl;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVSegmentMetaImpl;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.TreeCollapseAllAction;
import gov.nih.nci.caadapter.ui.common.actions.TreeExpandAllAction;
import gov.nih.nci.caadapter.ui.common.context.ContextManager;
import gov.nih.nci.caadapter.ui.common.context.DefaultContextManagerClientPanel;
import gov.nih.nci.caadapter.ui.common.context.MenuConstants;
import gov.nih.nci.caadapter.ui.common.message.ValidationMessagePane;
import gov.nih.nci.caadapter.ui.common.nodeloader.SCMTreeNodeLoader;
import gov.nih.nci.caadapter.ui.common.tree.*;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

/**
 * This class is the main entry point of CSV specification panel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.5 $
 *          date        $Date: 2008-06-09 19:54:06 $
 */
public class CSVPanel extends DefaultContextManagerClientPanel //JPanel implements ContextManagerClient
{
    private JTabbedPane rightTabbedPane;
    private TreeExpandAllAction treeExpandAllAction;
    private TreeCollapseAllAction treeCollapseAllAction;
    private JScrollPane treeScrollPane;
    // the display.
    private AutoscrollableTree tree = null;
    private CSVMetadataTreeNodePropertiesPane propertiesPane = null;
    private boolean propertiesPaneVisible = false;
    private CSVTreeChangeAdapter navigationController;
    private CSVMeta csvMeta = null;
    private DropCompatibleComponent dropTransferHandler = null;
    private ValidationMessagePane validationMessagePane;
    private boolean messagePaneVisible;

    public CSVPanel() {
        initialize();
    }

    public CSVMetadataTreeNodePropertiesPane getPropertiesPane() {
        if (propertiesPane == null) {
            propertiesPane = new CSVMetadataTreeNodePropertiesPane(this);
        }
        return this.propertiesPane;
    }

    public ValidationMessagePane getMessagePane() {
        if (validationMessagePane == null) {
            validationMessagePane = new ValidationMessagePane();
        }
        validationMessagePane.setMinimumSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 3), (int) (Config.FRAME_DEFAULT_HEIGHT / 4)));
        return validationMessagePane;
    }

    private void initialize() {
        this.setLayout(new BorderLayout());
        treeScrollPane = new JScrollPane();
        treeScrollPane.setPreferredSize(new Dimension(Config.FRAME_DEFAULT_WIDTH / 3, Config.FRAME_DEFAULT_HEIGHT / 2));
        initializeTree(getCSVMeta(true));
        JPanel treePanel = new JPanel(new BorderLayout());
        JPanel treeNorthPanel = new JPanel(new BorderLayout());
        treeExpandAllAction = new TreeExpandAllAction(tree);
        treeCollapseAllAction = new TreeCollapseAllAction(tree);
        JToolBar treeToolBar = new JToolBar("Tree Navigation ToolBar");
        treeToolBar.setFloatable(false);
        treeToolBar.add(treeExpandAllAction);
        treeToolBar.add(treeCollapseAllAction);
        treeNorthPanel.add(treeToolBar, BorderLayout.WEST);
        treePanel.add(treeNorthPanel, BorderLayout.NORTH);
        treePanel.add(treeScrollPane, BorderLayout.CENTER);
        rightTabbedPane = new JTabbedPane();

        //for place holding
        JLabel dummyHolderForPropertiesDisplay = new JLabel("For Properties Display...");
        JLabel dummyHolderForValidationMessageDisplay = new JLabel("For Validation Message Display...");
        JPanel placeHolderForValidationMessageDisplay = new JPanel(new BorderLayout());
        dummyHolderForValidationMessageDisplay.setEnabled(false);
        placeHolderForValidationMessageDisplay.add(dummyHolderForValidationMessageDisplay, BorderLayout.NORTH);
        placeHolderForValidationMessageDisplay.setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 3), (int) (Config.FRAME_DEFAULT_HEIGHT / 4)));
        JPanel placeHolderForPropertiesDisplay = new JPanel(new BorderLayout());
        placeHolderForPropertiesDisplay.add(dummyHolderForPropertiesDisplay, BorderLayout.NORTH);
        dummyHolderForPropertiesDisplay.setEnabled(false);
        placeHolderForPropertiesDisplay.setPreferredSize(new Dimension(Config.FRAME_DEFAULT_WIDTH / 3, Config.FRAME_DEFAULT_HEIGHT / 3));
        rightTabbedPane.add("Properties", placeHolderForPropertiesDisplay);
        rightTabbedPane.add("Validation Message", placeHolderForValidationMessageDisplay);//.setTopComponent(placeHolderForValidationMessageDisplay);
        //end of temporary place takers.
        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        DefaultSettings.setDefaultFeatureForJSplitPane(centerSplitPane);
        centerSplitPane.setBorder(BorderFactory.createEmptyBorder());
        centerSplitPane.setDividerLocation(0.4);
        centerSplitPane.setLeftComponent(treePanel);
        centerSplitPane.setRightComponent(rightTabbedPane);
        this.add(centerSplitPane, BorderLayout.CENTER);
    }

    public CSVTreeChangeAdapter getController() {
        if (this.navigationController == null) {
            this.navigationController = new CSVTreeChangeAdapter(this);
        }
        return this.navigationController;
    }

    private ValidatorResults initializeTree(File file) {
        ValidatorResults validatorResults = new ValidatorResults();
        CSVMetaParserImpl parser = new CSVMetaParserImpl();
        try {
            CSVMetaResult csvMetaResult = parser.parse(new FileReader(file));
            csvMeta = csvMetaResult.getCsvMeta();
            validatorResults.addValidatorResults(csvMetaResult.getValidatorResults());
            if (validatorResults.hasFatal()) {//return immediately
                return validatorResults;
            }
            initializeTree(csvMeta);
        }
        catch (Exception e1) {
            DefaultSettings.reportThrowableToLogAndUI(this, e1, null, this, false, true);
            return null;
        }
        return validatorResults;
    }

    private JTree initializeTree(CSVMeta csvMeta) {
        TreeNode nodes = null;
        CSVMetadataTreeModel treeModel = null;
        try {
            SCMTreeNodeLoader nodeLoader = new SCMTreeNodeLoader();
            nodes = nodeLoader.loadData(csvMeta);
        }
        catch (Throwable e) {
            DefaultSettings.reportThrowableToLogAndUI(this, e, "Error occurred during tree initialitation", this, true, true);
        }
        if (nodes != null) {
            treeModel = new CSVMetadataTreeModel(nodes);
        } else {
            treeModel = new CSVMetadataTreeModel();
        }
        tree = new AutoscrollableTree(treeModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        navigationController = new CSVTreeChangeAdapter(this);
        //register the mouse listener to clean up drag-and-drop flag
        tree.addMouseListener(navigationController);
        tree.getSelectionModel().addTreeSelectionListener(navigationController);
        tree.getModel().addTreeModelListener(navigationController);
        new TreeDefaultDragTransferHandler(tree, DnDConstants.ACTION_COPY_OR_MOVE);
        this.dropTransferHandler = new CSVTreeDropTransferHandler(tree, DnDConstants.ACTION_COPY_OR_MOVE);
        tree.addMouseListener(new CSVMetadataTreeMouseAdapter(this));
        treeScrollPane.getViewport().setView(tree);
        if (treeExpandAllAction != null) {//will skip the first initialization of just the tree.
            treeExpandAllAction.setTree(tree);
            treeCollapseAllAction.setTree(tree);
            tree.getInputMap().put(treeCollapseAllAction.getAcceleratorKey(), treeCollapseAllAction.getName());
            tree.getActionMap().put(treeCollapseAllAction.getName(), treeCollapseAllAction);
            tree.getInputMap().put(treeExpandAllAction.getAcceleratorKey(), treeExpandAllAction.getName());
            tree.getActionMap().put(treeExpandAllAction.getName(), treeExpandAllAction);
        }
        return tree;
    }

    public JTree getTree() {
        return tree;
    }

    public DropCompatibleComponent getDropTransferHandler() {
        return this.dropTransferHandler;
    }

    public CSVMeta getCSVMeta(boolean createIfNull) {
        if (this.csvMeta == null && createIfNull) {
            CSVSegmentMeta rootUserObject = new CSVSegmentMetaImpl("ROOT", null);
            csvMeta = new CSVMetaImpl(rootUserObject);
        }
        return csvMeta;
    }

    public void setCsvMeta(CSVMeta csvMeta) {
        this.csvMeta = csvMeta;
        initializeTree(csvMeta);
    }

    public ValidatorResults setSaveFile(File saveFile, boolean refreshTree) {
        ValidatorResults validatorResults = new ValidatorResults();
        if(ContextManager.getContextManager().getContextFileManager() == null)
                  this.saveFile = saveFile;
        if (super.setSaveFile(saveFile))//!GeneralUtilities.areEqual(this.saveFile, saveFile))
        {
            //			this.saveFile = saveFile;
            if (refreshTree) {
                validatorResults.addValidatorResults(initializeTree(this.saveFile));
            }
        }
        return validatorResults;
    }

    public boolean isPropertiesPaneVisible() {
        return propertiesPaneVisible;
    }

    public void setPropertiesPaneVisible(boolean newValue) {
        if (propertiesPaneVisible != newValue) {
            propertiesPaneVisible = newValue;
            if (propertiesPaneVisible) {
                rightTabbedPane.setComponentAt(0, getPropertiesPane());
            }
        }
    }

    CSVTreeChangeAdapter getDefaultNavigationAdapter() {
        return navigationController;
    }

    /**
     * Return whether the mapping module is in drag-and-drop mode.
     *
     * @return whether the mapping module is in drag-and-drop mode.
     */
    public boolean isInDragDropMode() {
        if (this.dropTransferHandler != null) {
            return dropTransferHandler.isInDragDropMode();
        } else {
            return false;
        }
    }

    /**
     * Set a new value for the mode.
     *
     * @param newValue
     */
    public void setInDragDropMode(boolean newValue) {
        if (this.dropTransferHandler != null) {
            dropTransferHandler.setInDragDropMode(newValue);
        }
    }

    public boolean isMessagePaneVisible() {
        return messagePaneVisible;
    }

    public void setMessagePaneVisible(boolean newValue) {
        if (this.messagePaneVisible != newValue) {
            this.messagePaneVisible = newValue;
            if (this.messagePaneVisible) {
                rightTabbedPane.setComponentAt(1, getMessagePane());
            }
        }
    }

    /**
     * Indicate whether or not it is changed.
     */
    public boolean isChanged() {
        return this.navigationController.isDataChanged();
    }

    /**
     * Explicitly set the value.
     *
     * @param newValue
     */
    public void setChanged(boolean newValue) {
        this.navigationController.setDataChanged(newValue);
    }

    /**
     * Return a list menu items under the given menu to be updated.
     *
     * @param menu_name
     * @return the map contains the action information.
     */
    public Map getMenuItems(String menu_name) {
        Action action = null;
        ContextManager contextManager = ContextManager.getContextManager();
        Map<String, Action> actionMap = contextManager.getClientMenuActions(MenuConstants.CSV_SPEC, menu_name);
        if (MenuConstants.FILE_MENU_NAME.equals(menu_name)) {
            JRootPane rootPane = this.getRootPane();
            if (rootPane != null) {//rootpane is not null implies this panel is fully displayed;
                //on the flip side, if it is null, it implies it is under certain construction.
                contextManager.enableAction(ActionConstants.NEW_CSV_SPEC, false);
                contextManager.enableAction(ActionConstants.OPEN_CSV_SPEC, true);
            }
        }
        //since the action depends on the panel instance,
        //the old action instance should be removed
        if (actionMap != null)
            contextManager.removeClientMenuAction(MenuConstants.CSV_SPEC, menu_name, "");

//		if (actionMap==null)
//		{
        action = new gov.nih.nci.caadapter.ui.specification.csv.actions.SaveCsvAction(this);
        contextManager.addClientMenuAction(MenuConstants.CSV_SPEC, MenuConstants.FILE_MENU_NAME, ActionConstants.SAVE, action);
        contextManager.addClientMenuAction(MenuConstants.CSV_SPEC, MenuConstants.TOOLBAR_MENU_NAME, ActionConstants.SAVE, action);
        action.setEnabled(true);
        action = new gov.nih.nci.caadapter.ui.specification.csv.actions.SaveAsCsvAction(this);
        contextManager.addClientMenuAction(MenuConstants.CSV_SPEC, MenuConstants.FILE_MENU_NAME, ActionConstants.SAVE_AS, action);
        contextManager.addClientMenuAction(MenuConstants.CSV_SPEC, MenuConstants.TOOLBAR_MENU_NAME, ActionConstants.SAVE_AS, action);
        action.setEnabled(true);
        action = new gov.nih.nci.caadapter.ui.specification.csv.actions.ValidateCsvAction(this);
        contextManager.addClientMenuAction(MenuConstants.CSV_SPEC, MenuConstants.FILE_MENU_NAME, ActionConstants.VALIDATE, action);
        contextManager.addClientMenuAction(MenuConstants.CSV_SPEC, MenuConstants.TOOLBAR_MENU_NAME, ActionConstants.VALIDATE, action);
        action.setEnabled(true);
        action = new gov.nih.nci.caadapter.ui.specification.csv.actions.CloseCsvAction(this);
        contextManager.addClientMenuAction(MenuConstants.CSV_SPEC, MenuConstants.FILE_MENU_NAME, ActionConstants.CLOSE, action);
        action.setEnabled(true);
        action = new gov.nih.nci.caadapter.ui.specification.csv.actions.GenerateReportAction(this);
        contextManager.addClientMenuAction(MenuConstants.CSV_SPEC, MenuConstants.REPORT_MENU_NAME, ActionConstants.GENERATE_REPORT, action);
        contextManager.addClientMenuAction(MenuConstants.CSV_SPEC, MenuConstants.TOOLBAR_MENU_NAME, ActionConstants.GENERATE_REPORT, action);
        action.setEnabled(true);
        actionMap = contextManager.getClientMenuActions(MenuConstants.CSV_SPEC, menu_name);
//		}
        return actionMap;
    }

    /**
     * return the open action inherited with this client.
     */
    public Action getDefaultOpenAction() {
        ContextManager contextManager = ContextManager.getContextManager();
        Action openAction = null;
        if (contextManager != null) {//contextManager is not null implies this panel is fully displayed;
            //on the flip side, if it is null, it implies it is under certain construction.
            openAction = contextManager.getDefinedAction(ActionConstants.OPEN_CSV_SPEC);
        }
        return openAction;
    }

    /**
     * Explicitly reload information from the internal given file.
     *
     * @throws Exception
     */
    public void reload() throws Exception {
        setSaveFile(getSaveFile(), true);
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.4  2007/10/15 19:49:36  jayannah
 * HISTORY      : Added a public API for the transformation of the CSV and DB in order to be compliant with the caCore.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/07/10 17:43:17  wangeug
 * HISTORY      : update code:reset propertyPane/validationPane with JTabbedPane
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/04/19 13:59:51  wangeug
 * HISTORY      : clean code
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:18:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.37  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.36  2006/01/26 22:40:30  jiangsc
 * HISTORY      : Fix drap and drop issue on CSV panel
 * HISTORY      :
 * HISTORY      : Revision 1.35  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.34  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.33  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.32  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.31  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.30  2005/11/18 20:28:14  jiangsc
 * HISTORY      : Enhanced context-sensitive menu navigation and constructions.
 * HISTORY      :
 * HISTORY      : Revision 1.28  2005/11/14 19:55:51  jiangsc
 * HISTORY      : Implementing UI enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.27  2005/10/25 17:13:27  jiangsc
 * HISTORY      : Added Validation implemenation.
 * HISTORY      :
 * HISTORY      : Revision 1.26  2005/10/24 18:49:04  jiangsc
 * HISTORY      : minor update
 * HISTORY      :
 * HISTORY      : Revision 1.25  2005/10/21 18:26:17  jiangsc
 * HISTORY      : Validation Class name changes.
 * HISTORY      :
 * HISTORY      : Revision 1.24  2005/10/18 17:01:03  jiangsc
 * HISTORY      : Changed one function signature in DragDrop component;
 * HISTORY      : Enhanced drag-drop status monitoring in HL7MappingPanel;
 * HISTORY      :
 * HISTORY      : Revision 1.23  2005/10/13 17:37:41  jiangsc
 * HISTORY      : Enhanced UI reporting on exceptions.
 * HISTORY      :
 * HISTORY      : Revision 1.22  2005/10/12 21:42:46  jiangsc
 * HISTORY      : Added validation on invalid file type.
 * HISTORY      :
 * HISTORY      : Revision 1.21  2005/10/07 20:09:03  jiangsc
 * HISTORY      : Enhanced the Look and Feel of Validation and Properties.
 * HISTORY      :
 * HISTORY      : Revision 1.20  2005/10/04 20:49:14  jiangsc
 * HISTORY      : UI Enhancement to fix data inconsistency between tree and properties panel.
 * HISTORY      :
 * HISTORY      : Revision 1.19  2005/09/29 21:14:59  jiangsc
 * HISTORY      : Added Generate Report action support
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/09/15 16:01:54  giordanm
 * HISTORY      : trying to get result objects working for CSVMetaParser... (result objects contain the information a service returns as well as a list of validation errors/warnings)
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/08/26 21:43:52  jiangsc
 * HISTORY      : Added tree actions
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/08/24 22:28:35  jiangsc
 * HISTORY      : Enhanced JGraph implementation;
 * HISTORY      : Save point of CSV and HSM navigation update;
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/08/23 19:57:00  jiangsc
 * HISTORY      : Name change
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/08/23 19:54:47  jiangsc
 * HISTORY      : Name change
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/08/23 19:26:15  jiangsc
 * HISTORY      : File name update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/08/19 20:38:21  jiangsc
 * HISTORY      : To implement Add Segment/Field
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/08/19 18:54:37  jiangsc
 * HISTORY      : Added reshuffle functionality.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/08/18 15:30:16  jiangsc
 * HISTORY      : First implementation on Switch control.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/08/11 22:10:29  jiangsc
 * HISTORY      : Open/Save File Dialog consolidation.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/08/05 20:35:47  jiangsc
 * HISTORY      : 0)Implemented field sequencing on CSVPanel but needs further rework;
 * HISTORY      : 1)Removed (Yes/No) for questions;
 * HISTORY      : 2)Removed double-checking after Save-As;
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/04 18:54:07  jiangsc
 * HISTORY      : Consolidated tabPane management into MainFrame
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/02 22:28:52  jiangsc
 * HISTORY      : Newly enhanced context-sensitive menus and toolbar.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/07/27 22:41:19  jiangsc
 * HISTORY      : Consolidated context sensitive menu implementation.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/07/25 21:56:50  jiangsc
 * HISTORY      : 1) Added expand all and collapse all;
 * HISTORY      : 2) Added toolbar on the mapping panel;
 * HISTORY      : 3) Consolidated menus;
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/22 20:53:12  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/06/29 21:21:39  jiangsc
 * HISTORY      : More functionality.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/06/24 20:38:26  jiangsc
 * HISTORY      : Save Point
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/06/24 19:53:09  jiangsc
 * HISTORY      : Save Point
 * HISTORY      :
 * HISTORY      : Revision 1.19  2005/06/23 14:30:05  jiangsc
 * HISTORY      : Updated CSVPanel implementation to support basic drag and drop.
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/06/21 23:03:03  jiangsc
 * HISTORY      : Put in new CSVPanel Implementation.
 * HISTORY      :
 */
