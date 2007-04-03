/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/HSMPanel.java,v 1.1 2007-04-03 16:18:15 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.ui.specification.hsm;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.hl7.util.HL7Util;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.clone.meta.HL7V3Meta;
import gov.nih.nci.caadapter.hl7.clone.meta.HL7V3MetaFileParser;
import gov.nih.nci.caadapter.hl7.clone.meta.HL7V3MetaResult;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.TreeCollapseAllAction;
import gov.nih.nci.caadapter.ui.common.actions.TreeExpandAllAction;
import gov.nih.nci.caadapter.ui.common.context.DefaultContextManagerClientPanel;
import gov.nih.nci.caadapter.ui.common.context.ContextManager;
import gov.nih.nci.caadapter.ui.common.context.MenuConstants;
import gov.nih.nci.caadapter.ui.common.message.ValidationMessagePane;
import gov.nih.nci.caadapter.ui.common.nodeloader.HSMBasicNodeLoader;
import gov.nih.nci.caadapter.ui.common.nodeloader.HSMTreeNodeLoader;
import gov.nih.nci.caadapter.ui.common.tree.AutoscrollableTree;
import gov.nih.nci.caadapter.ui.common.tree.HSMTreeCellRenderer;

import org.hl7.meta.MessageType;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

/**
 * The class is the main panel to construct the UI and initialize the utilities to
 * facilitate HSM meta-data management.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:18:15 $
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
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/HSMPanel.java,v 1.1 2007-04-03 16:18:15 wangeug Exp $";
 
    private JSplitPane rightSplitPane;
    private TreeExpandAllAction treeExpandAllAction;
    private TreeCollapseAllAction treeCollapseAllAction;
    private JScrollPane treeScrollPane;
    private AutoscrollableTree hsmTree;

    private HSMNodePropertiesPane propertiesPane;
    private boolean propertiesPaneVisible;

    private ValidationMessagePane validationMessagePane;
    private boolean messagePaneVisible;

    private HL7V3Meta hl7V3MetaRoot;
    private MessageType messageType;

    private HSMPanelController controller;
    private HSMBasicNodeLoader hsmNodeLoader = new HSMTreeNodeLoader();;

    private JPanel placeHolderForValidationMessageDisplay;
    private JPanel placeHolderForPropertiesDisplay;

    public HSMPanel()
    {
        initialize();
    }

    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     */
    public HSMPanel(MessageType messageType)
    {
        this.messageType = messageType;
        initialize();
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
        treeScrollPane.setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 2), (int) (Config.FRAME_DEFAULT_HEIGHT / 1.5)));

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

        rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        DefaultSettings.setDefaultFeatureForJSplitPane(rightSplitPane);
        rightSplitPane.setBorder(BorderFactory.createEmptyBorder());
        rightSplitPane.setDividerLocation(0.5);

        //for place holding
        JLabel dummyHolderForPropertiesDisplay = new JLabel("For Properties Display...");
        JLabel dummyHolderForValidationMessageDisplay = new JLabel("For Validation Message Display...");

        placeHolderForValidationMessageDisplay = new JPanel(new BorderLayout());
        dummyHolderForValidationMessageDisplay.setEnabled(false);
        placeHolderForValidationMessageDisplay.add(dummyHolderForValidationMessageDisplay, BorderLayout.NORTH);
        placeHolderForValidationMessageDisplay.setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 3), (int) (Config.FRAME_DEFAULT_HEIGHT / 4)));
        rightSplitPane.setTopComponent(placeHolderForValidationMessageDisplay);
        //place holder
        placeHolderForPropertiesDisplay = new JPanel(new BorderLayout());
//		JTextField textField = new JTextField("For Properties Display...");
//		textField.setEnabled(false);
//		panel.add(textField, BorderLayout.NORTH);
        placeHolderForPropertiesDisplay.add(dummyHolderForPropertiesDisplay, BorderLayout.NORTH);
        dummyHolderForPropertiesDisplay.setEnabled(false);
        placeHolderForPropertiesDisplay.setPreferredSize(new Dimension(Config.FRAME_DEFAULT_WIDTH / 3, Config.FRAME_DEFAULT_HEIGHT / 3));
        rightSplitPane.setBottomComponent(placeHolderForPropertiesDisplay);

        centerSplitPane.setLeftComponent(treePanel);
        centerSplitPane.setRightComponent(rightSplitPane);
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
//				CSVMetadataTreeNodePropertiesPane propPane = getPropertiesPane();
                rightSplitPane.setBottomComponent(getPropertiesPane());
            }
            else
            {//set null implies removement
                rightSplitPane.setBottomComponent(placeHolderForPropertiesDisplay);
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
                rightSplitPane.setTopComponent(getMessagePane());
            }
            else
            {
                rightSplitPane.setTopComponent(placeHolderForValidationMessageDisplay);
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
            validationMessagePane = new ValidationMessagePane();
        }
        validationMessagePane.setMinimumSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 3), (int) (Config.FRAME_DEFAULT_HEIGHT / 4)));
        return validationMessagePane;
    }

    public HL7V3Meta getHl7V3MetaRoot()
    {
        return hl7V3MetaRoot;
    }

    public void setHl7V3MetaRoot(HL7V3Meta hl7V3MetaRoot)
    {
        this.hl7V3MetaRoot = hl7V3MetaRoot;
        initializeTree(hl7V3MetaRoot);
    }

    private ValidatorResults initializeTree(File saveFile)
    {
        ValidatorResults validatorResults = new ValidatorResults();
        HL7V3MetaFileParser parser = HL7V3MetaFileParser.instance();
        try
        {
            HL7V3MetaResult hl7V3MetaResult = parser.parse(new FileReader(saveFile));
            this.hl7V3MetaRoot = hl7V3MetaResult.getHl7V3Meta();
            validatorResults.addValidatorResults(hl7V3MetaResult.getValidatorResults());
            if (validatorResults.hasFatal())
            {//return immediately
                return validatorResults;
            }
            if (getMessageType() == null)
            {
                messageType = HL7Util.getMessageType(hl7V3MetaRoot.getMessageID());
            }
            initializeTree(hl7V3MetaRoot);
        }
        catch (Throwable e1)
        {
			System.out.println("Logging exception within initializeTree(File saveFile).");
			DefaultSettings.reportThrowableToLogAndUI(this, e1, null, this, false, true);
			Message msg = MessageResources.getMessage("GEN0", new Object[]{e1.getMessage()});
			validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.FATAL, msg));
		}
        return validatorResults;
    }

    private JTree initializeTree(HL7V3Meta hl7V3MetaRoot)
    {
        if(hl7V3MetaRoot==null)
        {
            return null;
        }
        try
        {
            TreeNode root = hsmNodeLoader.loadData(this.hl7V3MetaRoot);
            hsmTree = new AutoscrollableTree(root);
//			((DefaultTreeModel)hsmTree.getModel()).setRoot(root);
            hsmTree.getSelectionModel().addTreeSelectionListener(getController());
            hsmTree.getModel().addTreeModelListener(getController());
            treeScrollPane.getViewport().setView(hsmTree);
            hsmTree.addMouseListener(new HSMTreeMouseAdapter(this));
            hsmTree.setCellRenderer(new HSMTreeCellRenderer());

            treeExpandAllAction.setTree(hsmTree);
            treeCollapseAllAction.setTree(hsmTree);
            hsmTree.getInputMap().put(treeCollapseAllAction.getAcceleratorKey(), treeCollapseAllAction.getName());
            hsmTree.getActionMap().put(treeCollapseAllAction.getName(), treeCollapseAllAction);
            hsmTree.getInputMap().put(treeExpandAllAction.getAcceleratorKey(), treeExpandAllAction.getName());
            hsmTree.getActionMap().put(treeExpandAllAction.getName(), treeExpandAllAction);

            //			treeScrollPane.setMinimumSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 2), (int) (Config.FRAME_DEFAULT_HEIGHT / 1.5)));
            return hsmTree;
        }
        catch(Throwable e)
        {
            Log.logException(this.getClass(), "Cannot initialize the tree anymore!", e);
            DefaultSettings.reportThrowableToLogAndUI(this, e, "Error occurred during tree initialitation", this, true, true);
            return null;
        }
    }

    public JTree getTree()
    {
        return hsmTree;
    }

    public HSMBasicNodeLoader getDefaultHSMNodeLoader()
    {
        return hsmNodeLoader;
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
                    validatorResults.addValidatorResults(initializeTree(this.saveFile));
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
     * HL7 v3 Message Type
     * @return message type
     */
    public MessageType getMessageType()
    {
        return messageType;
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

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.34  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.33  2006/06/13 18:12:12  jiangsc
 * HISTORY      : Upgraded to catch Throwable instead of Exception.
 * HISTORY      :
 * HISTORY      : Revision 1.32  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.31  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.30  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.29  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.28  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.27  2005/11/18 20:28:14  jiangsc
 * HISTORY      : Enhanced context-sensitive menu navigation and constructions.
 * HISTORY      :
 * HISTORY      : Revision 1.25  2005/11/14 19:55:51  jiangsc
 * HISTORY      : Implementing UI enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.24  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.23  2005/10/21 18:26:17  jiangsc
 * HISTORY      : Validation Class name changes.
 * HISTORY      :
 * HISTORY      : Revision 1.22  2005/10/13 18:53:44  jiangsc
 * HISTORY      : Added validation on invalid file type to map and HSM modules.
 * HISTORY      :
 * HISTORY      : Revision 1.21  2005/10/13 17:37:41  jiangsc
 * HISTORY      : Enhanced UI reporting on exceptions.
 * HISTORY      :
 * HISTORY      : Revision 1.20  2005/10/07 18:40:15  jiangsc
 * HISTORY      : Enhanced the Look and Feel of Validation and Properties.
 * HISTORY      :
 * HISTORY      : Revision 1.19  2005/10/04 20:51:33  jiangsc
 * HISTORY      : Validation enhancement.
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/09/16 16:20:16  giordanm
 * HISTORY      : HL7V3 parser is not returning a result object not a just a meta object.
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/09/09 22:42:03  chene
 * HISTORY      : Saved Point
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/09/08 19:37:03  chene
 * HISTORY      : Saved point
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/08/28 18:12:28  jiangsc
 * HISTORY      : Implemented Validation on HSM panel.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/08/24 22:28:37  jiangsc
 * HISTORY      : Enhanced JGraph implementation;
 * HISTORY      : Save point of CSV and HSM navigation update;
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/08/19 21:09:57  jiangsc
 * HISTORY      : Save Point.
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/08/18 15:30:17  jiangsc
 * HISTORY      : First implementation on Switch control.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/08/17 20:01:38  chene
 * HISTORY      : Refactor HL7V3MetaFileParser to a singleton
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/08/12 18:38:11  jiangsc
 * HISTORY      : Enable HL7 V3 Message to be saved in multiple XML file.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/08/08 17:12:53  jiangsc
 * HISTORY      : Support Abstract Datatype.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/08/05 20:35:51  jiangsc
 * HISTORY      : 0)Implemented field sequencing on CSVPanel but needs further rework;
 * HISTORY      : 1)Removed (Yes/No) for questions;
 * HISTORY      : 2)Removed double-checking after Save-As;
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/04 22:22:28  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/03 16:56:16  jiangsc
 * HISTORY      : Further consolidation of context sensitive management.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/02 22:28:54  jiangsc
 * HISTORY      : Newly enhanced context-sensitive menus and toolbar.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/07/29 22:00:00  jiangsc
 * HISTORY      : Enhanced HSMPanel
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/28 18:18:42  jiangsc
 * HISTORY      : Can Open HSM Panel
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/27 22:41:13  jiangsc
 * HISTORY      : Consolidated context sensitive menu implementation.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/27 13:57:46  jiangsc
 * HISTORY      : Added the first round of HSMPanel.
 * HISTORY      :
 */
