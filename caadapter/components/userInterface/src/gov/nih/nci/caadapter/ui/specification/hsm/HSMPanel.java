/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.specification.hsm;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.v1.MIFParserUtil;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.TreeCollapseAllAction;
import gov.nih.nci.caadapter.ui.common.actions.TreeExpandAllAction;
import gov.nih.nci.caadapter.ui.common.context.DefaultContextManagerClientPanel;
import gov.nih.nci.caadapter.ui.common.context.ContextManager;
import gov.nih.nci.caadapter.ui.common.context.MenuConstants;
import gov.nih.nci.caadapter.ui.common.message.ValidationMessagePane;
import gov.nih.nci.caadapter.ui.common.nodeloader.NewHSMBasicNodeLoader;
import gov.nih.nci.caadapter.ui.common.tree.AutoscrollableTree;
import gov.nih.nci.caadapter.ui.common.tree.MIFTreeCellRenderer;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JRootPane;
import javax.swing.JTree;
import javax.swing.Action;
import javax.swing.JToolBar;
import javax.swing.BorderFactory;
import javax.swing.tree.TreeNode;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.io.File;
import java.util.Map;

/**
 * The class is the main panel to construct the UI and initialize the utilities to
 * facilitate HSM meta-data management.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.6 $
 *          date        $Date: 2008-06-09 19:54:07 $
 */
public class HSMPanel extends DefaultContextManagerClientPanel//extends JPanel implements ContextManagerClient
{
    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: HSMPanel.java,v $";
    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/HSMPanel.java,v 1.6 2008-06-09 19:54:07 phadkes Exp $";

    private JTabbedPane rightTabbedPane;
    private TreeExpandAllAction treeExpandAllAction;
    private TreeCollapseAllAction treeCollapseAllAction;
    private JScrollPane treeScrollPane;
    private AutoscrollableTree hsmTree;

    private HSMNodePropertiesPane propertiesPane;
    private boolean propertiesPaneVisible=true;

    private ValidationMessagePane validationMessagePane;
    private boolean messagePaneVisible=true;

    private HSMPanelController controller;
//    private JPanel placeHolderForValidationMessageDisplay;
//    private JPanel placeHolderForPropertiesDisplay;

    /**
     * Default constructor to being used as HSMPanel.newInstance()
     *
     */
    public HSMPanel()
    {
        initialize();
    }
    public HSMPanel(String mifFileName)
    {
        initialize();
        TreeNode root=loadTreeNodeWithMIF(mifFileName);

    	initializeTreeWithMIFTreeNode(root);
    }

    private void initialize()
    {
        this.setLayout(new BorderLayout());

        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        DefaultSettings.setDefaultFeatureForJSplitPane(centerSplitPane);
        centerSplitPane.setBorder(BorderFactory.createEmptyBorder());
        centerSplitPane.setDividerLocation(0.5);

        hsmTree = new AutoscrollableTree();
        treeScrollPane = new JScrollPane(hsmTree);
        treeScrollPane.setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 3), (int) (Config.FRAME_DEFAULT_HEIGHT / 2)));

        JPanel treePanel = new JPanel(new BorderLayout());
        JPanel treeNorthPanel = new JPanel(new BorderLayout());
        treeExpandAllAction = new TreeExpandAllAction(hsmTree);
        treeCollapseAllAction = new TreeCollapseAllAction(hsmTree);
        JToolBar treeToolBar = new JToolBar("Tree Navigation ToolBar");
        treeToolBar.setFloatable(false);
        treeToolBar.add(treeExpandAllAction);
        treeToolBar.add(treeCollapseAllAction);
        treeNorthPanel.add(treeToolBar, BorderLayout.WEST);
        treePanel.add(treeNorthPanel, BorderLayout.NORTH);
        treePanel.add(treeScrollPane, BorderLayout.CENTER);

        rightTabbedPane = new JTabbedPane();//new JSplitPane(JSplitPane.VERTICAL_SPLIT);
//        DefaultSettings.setDefaultFeatureForJSplitPane(rightTabbedPane);
        rightTabbedPane.setBorder(BorderFactory.createEmptyBorder());

        //for place holding
        setPropertiesPaneVisible(false);
        setMessagePaneVisible(false);

        centerSplitPane.setLeftComponent(treePanel);
        centerSplitPane.setRightComponent(rightTabbedPane);
        this.add(centerSplitPane, BorderLayout.CENTER);
    }

    public HSMPanelController getController()
    {
        if(this.controller==null)
        {
            this.controller = new HSMPanelController(this);
        }
        return this.controller;
    }

    public boolean isPropertiesPaneVisible()
    {
        return propertiesPaneVisible;
    }

    public void setPropertiesPaneVisible(boolean newValue)
    {
        if (propertiesPaneVisible != newValue)
        {
            propertiesPaneVisible = newValue;
            if (propertiesPaneVisible)
            {
                rightTabbedPane.setComponentAt(0, getPropertiesPane());
            }
            else
            {//set null implies removement
                //place holder
            	if (rightTabbedPane.getTabCount()>1)
            		return;
                JLabel dummyHolderForPropertiesDisplay = new JLabel("For Properties Display...");
                dummyHolderForPropertiesDisplay.setEnabled(false);
                JPanel placeHolderForPropertiesDisplay = new JPanel(new BorderLayout());
                placeHolderForPropertiesDisplay.add(dummyHolderForPropertiesDisplay, BorderLayout.NORTH);
                placeHolderForPropertiesDisplay.setPreferredSize(new Dimension(Config.FRAME_DEFAULT_WIDTH / 3, Config.FRAME_DEFAULT_HEIGHT /2 ));/// 3));
                rightTabbedPane.add("Properties ", placeHolderForPropertiesDisplay);
            }
        }
    }

    public boolean isMessagePaneVisible()
    {
        return messagePaneVisible;
    }

    public void setMessagePaneVisible(boolean newValue)
    {
        if(this.messagePaneVisible!= newValue)
        {
            this.messagePaneVisible = newValue;
            if(this.messagePaneVisible)
            {
            	 rightTabbedPane.setComponentAt(1,getMessagePane());
            }
            else
            {
                JLabel dummyHolderForValidationMessageDisplay = new JLabel("For Validation Message Display...");
                JPanel placeHolderForValidationMessageDisplay = new JPanel(new BorderLayout());
                dummyHolderForValidationMessageDisplay.setEnabled(false);
                placeHolderForValidationMessageDisplay.add(dummyHolderForValidationMessageDisplay, BorderLayout.NORTH);
                placeHolderForValidationMessageDisplay.setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 3), (int) (Config.FRAME_DEFAULT_HEIGHT / 4)));
                rightTabbedPane.add("Validation Message",placeHolderForValidationMessageDisplay);
            }
        }
    }

    public HSMNodePropertiesPane getPropertiesPane()
    {
        if(this.propertiesPane==null)
        {
            propertiesPane = new HSMNodePropertiesPane(this);
            propertiesPane.setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 3), (int) (Config.FRAME_DEFAULT_HEIGHT / 3)));
        }
        return this.propertiesPane;
    }

    public ValidationMessagePane getMessagePane()
    {
        if (validationMessagePane == null)
        {
        	//display message with elemnt validated
            validationMessagePane = new ValidationMessagePane(true);
        }
        validationMessagePane.setMinimumSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 3), (int) (Config.FRAME_DEFAULT_HEIGHT / 4)));
        return validationMessagePane;
    }

    private ValidatorResults initializeTreeWithFile(File saveFile)
    {
        ValidatorResults validatorResults = new ValidatorResults();
        try
        {
        	NewHSMBasicNodeLoader newHsmNodeLoader=new NewHSMBasicNodeLoader(true);
        	TreeNode root = newHsmNodeLoader.loadData(saveFile);
        	initializeTreeWithMIFTreeNode(root);
        }
        catch (Throwable e1)
        {
			Log.logException(this, e1);
			DefaultSettings.reportThrowableToLogAndUI(this, e1, null, this, false, true);
			Message msg = MessageResources.getMessage("GEN0", new Object[]{e1.getMessage()});
			validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
		}
        return validatorResults;
    }

    private TreeNode loadTreeNodeWithMIF(String mifFileName)
    {
        if(mifFileName==null)
        {
            return null;
        }
        try
        {
        	MIFClass mifClass=MIFParserUtil.getMIFClass(mifFileName);
        	NewHSMBasicNodeLoader newHsmNodeLoader=new NewHSMBasicNodeLoader(true,true);
        	return newHsmNodeLoader.loadData(mifClass);
        }
        catch(Throwable e)
        {
            Log.logException(this.getClass(), "Cannot initialize the tree anymore!", e);
            DefaultSettings.reportThrowableToLogAndUI(this, e, "Error occurred during tree initialitation", this, true, true);
            return null;
        }
    }

    private void initializeTreeWithMIFTreeNode(TreeNode root )
    {
        try
        {
            hsmTree = new AutoscrollableTree(root);
            hsmTree.getSelectionModel().addTreeSelectionListener(getController());
            hsmTree.getModel().addTreeModelListener(getController());
            treeScrollPane.getViewport().setView(hsmTree);
            hsmTree.addMouseListener(new HSMTreeMouseAdapter(this));
            hsmTree.setCellRenderer(new MIFTreeCellRenderer());
            treeExpandAllAction.setTree(hsmTree);
            treeCollapseAllAction.setTree(hsmTree);
            hsmTree.getInputMap().put(treeCollapseAllAction.getAcceleratorKey(), treeCollapseAllAction.getName());
            hsmTree.getActionMap().put(treeCollapseAllAction.getName(), treeCollapseAllAction);
            hsmTree.getInputMap().put(treeExpandAllAction.getAcceleratorKey(), treeExpandAllAction.getName());
            hsmTree.getActionMap().put(treeExpandAllAction.getName(), treeExpandAllAction);
        }
        catch(Throwable e)
        {
            Log.logException(this.getClass(), "Cannot initialize the tree anymore!", e);
            DefaultSettings.reportThrowableToLogAndUI(this, e, "Error occurred during tree initialitation", this, true, true);
        }
    }

    public JTree getTree()
    {
        return hsmTree;
    }


    public ValidatorResults setSaveFile(File saveFile, boolean refreshTree)
    {
        ValidatorResults validatorResults = new ValidatorResults();
        try
        {
            if (super.setSaveFile(saveFile))
            {
                if (refreshTree)
                {
                    validatorResults.addValidatorResults(initializeTreeWithFile(this.saveFile));
                }
            }
        }
        catch(Throwable e)
        {
            Log.logException(this.getClass(), "Cannot initialize the file!", e);
            DefaultSettings.reportThrowableToLogAndUI(this, e, "", this, false, true);
			Message msg = MessageResources.getMessage("GEN0", new Object[]{e.getMessage()});
			validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
		}
        return validatorResults;
    }

    /**
     * Indicate whether or not it is changed.
     */
    public boolean isChanged()
    {
        return getController().isDataChanged();
    }

    /**
     * Explicitly set the value.
     *
     * @param newValue
     */
    public void setChanged(boolean newValue)
    {
        this.getController().setDataChanged(newValue);
    }

    /**
     * Return a list menu items under the given menu to be updated.
     *
     * @param menu_name
     * @return the action map
     */
    public Map getMenuItems(String menu_name)
	{
		Action action = null;
		ContextManager contextManager = ContextManager.getContextManager();
		Map <String, Action>actionMap = contextManager.getClientMenuActions(MenuConstants.HSM_FILE, menu_name);
		if (MenuConstants.FILE_MENU_NAME.equals(menu_name))
		{
			JRootPane rootPane = this.getRootPane();
			if (rootPane != null)
			{//rootpane is not null implies this panel is fully displayed;
				//on the flip side, if it is null, it implies it is under certain construction.
				contextManager.enableAction(ActionConstants.NEW_HSM_FILE, false);
                contextManager.enableAction(ActionConstants.OPEN_HSM_FILE, true);
			}
		}
		//since the action depends on the panel instance,
		//the old action instance should be removed
		if (actionMap!=null)
			contextManager.removeClientMenuAction(MenuConstants.CSV_SPEC, menu_name, "");

//		if (actionMap==null)
//		{
				action = new gov.nih.nci.caadapter.ui.specification.hsm.actions.SaveHsmAction(this);
				contextManager.addClientMenuAction(MenuConstants.HSM_FILE, MenuConstants.FILE_MENU_NAME,ActionConstants.SAVE, action);
				contextManager.addClientMenuAction(MenuConstants.HSM_FILE, MenuConstants.TOOLBAR_MENU_NAME,ActionConstants.SAVE, action);
				action.setEnabled(true);

				action = new gov.nih.nci.caadapter.ui.specification.hsm.actions.SaveAsHsmAction(this);
				contextManager.addClientMenuAction(MenuConstants.HSM_FILE, MenuConstants.FILE_MENU_NAME,ActionConstants.SAVE_AS, action);
				contextManager.addClientMenuAction(MenuConstants.HSM_FILE, MenuConstants.TOOLBAR_MENU_NAME,ActionConstants.SAVE_AS, action);
				action.setEnabled(true);

				action = new gov.nih.nci.caadapter.ui.specification.hsm.actions.ValidateHSMAction(this);
				contextManager.addClientMenuAction(MenuConstants.HSM_FILE, MenuConstants.FILE_MENU_NAME,ActionConstants.VALIDATE, action);
				contextManager.addClientMenuAction(MenuConstants.HSM_FILE, MenuConstants.TOOLBAR_MENU_NAME,ActionConstants.VALIDATE, action);
				action.setEnabled(true);

				//enable "ADD ALL OPTION" action
				action =new gov.nih.nci.caadapter.ui.specification.hsm.actions.EnableAllOptionCloneAction(this);
				contextManager.addClientMenuAction(MenuConstants.HSM_FILE, MenuConstants.TOOLBAR_MENU_NAME,ActionConstants.REFRESH, action);

				action = new gov.nih.nci.caadapter.ui.specification.hsm.actions.CloseHSMAction(this);
				contextManager.addClientMenuAction(MenuConstants.HSM_FILE, MenuConstants.FILE_MENU_NAME,ActionConstants.CLOSE, action);
				action.setEnabled(true);

				actionMap = contextManager.getClientMenuActions(MenuConstants.HSM_FILE, menu_name);
//		}
		return actionMap;
	}

    /**
     * return the open action inherited with this client.
     */
    public Action getDefaultOpenAction()
    {
    	ContextManager contextManager = ContextManager.getContextManager();
        Action openAction = null;
        if (contextManager != null)
        {//contextManager is not null implies this panel is fully displayed;
            //on the flip side, if it is null, it implies it is under certain construction.
            openAction = contextManager.getDefinedAction(ActionConstants.OPEN_HSM_FILE);
        }
        return openAction;
    }

    /**
     * Explicitly reload information from the internal given file.
     *
     * @throws Exception
     */
    public void reload() throws Exception
    {
        setSaveFile(getSaveFile(), true);
    }
}
