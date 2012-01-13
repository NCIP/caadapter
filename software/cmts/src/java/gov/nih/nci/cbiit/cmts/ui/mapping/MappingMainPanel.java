/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.ui.mapping;

import gov.nih.nci.cbiit.cmts.common.XSDParser;
import gov.nih.nci.cbiit.cmts.core.AttributeMeta;
import gov.nih.nci.cbiit.cmts.core.Component;
import gov.nih.nci.cbiit.cmts.core.ComponentType;
import gov.nih.nci.cbiit.cmts.core.ElementMeta;
import gov.nih.nci.cbiit.cmts.core.Mapping;
import gov.nih.nci.cbiit.cmts.mapping.MappingFactory;
import gov.nih.nci.cbiit.cmts.ui.actions.SaveAsMapAction;
import gov.nih.nci.cbiit.cmts.ui.actions.SaveMapAction;
import gov.nih.nci.cbiit.cmts.ui.common.ActionConstants;
import gov.nih.nci.cbiit.cmts.ui.common.ContextManager;
import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmts.ui.common.MenuConstants;
import gov.nih.nci.cbiit.cmts.ui.dnd.TreeDragTransferHandler;
import gov.nih.nci.cbiit.cmts.ui.dnd.TreeTransferHandler;
import gov.nih.nci.cbiit.cmts.ui.function.FunctionLibraryPane;
import gov.nih.nci.cbiit.cmts.ui.jgraph.MiddlePanelJGraphController;
import gov.nih.nci.cbiit.cmts.ui.jgraph.MiddlePanelMarqueeHandler;
import gov.nih.nci.cbiit.cmts.ui.main.AbstractTabPanel;
import gov.nih.nci.cbiit.cmts.ui.main.MainFrameContainer;
import gov.nih.nci.cbiit.cmts.ui.properties.DefaultPropertiesPage;
import gov.nih.nci.cbiit.cmts.ui.tree.MappingSourceTree;
import gov.nih.nci.cbiit.cmts.ui.tree.MappingTargetTree;
import gov.nih.nci.cbiit.cmts.ui.tree.TreeMouseAdapter;
import gov.nih.nci.cbiit.cmts.ui.tree.TreeSelectionHandler;
import gov.nih.nci.cbiit.cmts.ui.util.GeneralUtilities;
import gov.nih.nci.cbiit.cmts.util.ResourceUtils;
import gov.nih.nci.caadapter.common.util.FileUtil;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.applet.Applet;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.bind.JAXBException;

import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;

/**
 * This class
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.16 $
 * @date       $Date: 2009-12-02 18:53:16 $
 *
 */
public class MappingMainPanel extends AbstractTabPanel implements ActionListener{

	private static final String Cmps_V3_MESSAGE_FILE_DEFAULT_EXTENSION = ".map";
	private static final String OPEN_DIALOG_TITLE_FOR_DEFAULT_SOURCE_FILE = "Open source data schema";
	private static final String OPEN_DIALOG_TITLE_FOR_DEFAULT_TARGET_FILE = "Open target data schema";
	private static final String SELECT_CSV_TIP = "select CSV";
	private static final String SELECT_HMD_TIP = "select HMD";
	private static final String SELECT_SOURCE = "Open Source...";
	private static final String SELECT_TARGET = "Open Target...";
	private static final String SOURCE_TREE_FILE_DEFAULT_EXTENTION = ".xsd";
	private static final String TARGET_TREE_FILE_DEFAULT_EXTENTION = ".xsd";

	private Mapping mapping = null;
    private File mapFile = null;
    private File mappingSourceFile = null;
	private File mappingTargetFile = null;
	private MappingMiddlePanel middlePanel = null;
	private JTextField sourceLocationArea = new JTextField();
	private MappingTreeScrollPane sourceScrollPane = new MappingTreeScrollPane(MappingTreeScrollPane.DRAW_NODE_TO_RIGHT);
	private MappingSourceTree sTree = null;
	private JTextField targetLocationArea = new JTextField();
	private MappingTreeScrollPane targetScrollPane = new MappingTreeScrollPane(MappingTreeScrollPane.DRAW_NODE_TO_LEFT);
	private MappingTargetTree tTree = null;
	private MiddlePanelJGraphController graphController =null;
    private CellRenderXSObject selectedRootTempStore = null;

    public MappingMainPanel(MainFrameContainer mainFrame) throws Exception
	{
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setLayout(new BorderLayout());
        this.mainFrame = mainFrame;
        middlePanel = new MappingMiddlePanel(this);
		graphController = new MiddlePanelJGraphController(this);
		MiddlePanelMarqueeHandler marquee=(MiddlePanelMarqueeHandler)middlePanel.getGraph().getMarqueeHandler();
		marquee.setController(graphController);
		this.add(getCenterPanel(true), BorderLayout.CENTER);
		this.setViewFileExtension(".map");
	}

	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		try
		{
            if (SELECT_SOURCE.equals(command))
			{
                processingButtonOpenSource();
            }
			else if (SELECT_TARGET.equals(command))
			{
                processingButtonOpenTarget();
            }
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			DefaultSettings.reportThrowableToLogAndUI(this, e1, "", this, false, false);
		}
	}
    /*
    private void refreshPanel()
    {
        if (mapping == null) return;
        MappingFactory.saveMapping(persistentFile, mappingData);
		try
		{
			if (!GeneralUtilities.areEqual(defaultFile, file))
			{//not equal, change it.
				removeFileUsageListener(defaultFile, viewerPanel);
				defaultFile = file;
			}
			postActionPerformed(viewerPanel);
//			JOptionPane.showMessageDialog(viewerPanel.getParent(), "Mapping data has been saved successfully.", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
			viewerPanel.setSaveFile(file);
			return true;
		}
		catch(Throwable e)
		{
			//restore the change value since something occurred and believe the save process is aborted.
			viewerPanel.setChanged(oldChangeValue);
			//rethrow the exeception
			e.printStackTrace();
			throw new Exception(e);
		}
        MappingMainPanel rPanel = new MappingMainPanel(mainFrame);
            rPanel.processOpenMapFile(mapFile, rMap);
    }
    */
    private void processingButtonOpenSource() throws Exception
    {
        //this.sourceButtonPanel.repaint();
        File file = DefaultSettings.getUserInputOfFileFromGUI(this, //FileUtil.getUIWorkingDirectoryPath(),
                SOURCE_TREE_FILE_DEFAULT_EXTENTION, OPEN_DIALOG_TITLE_FOR_DEFAULT_SOURCE_FILE, false, false);
        if ((file == null)||(!file.exists())||(!file.isFile())) return;
        String targetFile = targetLocationArea.getText();
        if ((targetFile != null)&&(!targetFile.trim().equals("")))
        {
            String fileH = "file:/";
            if (targetFile.toLowerCase().startsWith(fileH))
            {
                targetFile = targetFile.substring(fileH.length());
                while(targetFile.startsWith("/")) targetFile = targetFile.substring(1);
            }
            else if (targetFile.toLowerCase().indexOf(fileH) > 0)
            {
                targetFile = targetFile.substring(targetFile.toLowerCase().indexOf(fileH) + fileH.length());
                while(targetFile.startsWith("/")) targetFile = targetFile.substring(1);
            }

            File f = new File(targetFile);
            //System.out.println("CCCCC HHG : file compare : " + f.getAbsolutePath() + " :: " + file.getAbsolutePath());
            if ((f.exists())&&(f.isFile())&&(file.exists())&&(file.isFile()))
            {
                if (f.getAbsolutePath().equals(file.getAbsolutePath()))
                {
                    JOptionPane.showMessageDialog(this, "This is the same file as the Target", "Same with the Target", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        Mapping rMap = null;
        Mapping mapping1 = null;
        try
        {
            while(true)
            {
                //System.out.println("CCCCC HHH98 : " + file.getAbsolutePath());
                mapping1 = getGraphController().retrieveMappingData(false);
                if ((mapping1 == null)||(mapping1.getLinks().getLink().size() == 0))
                {
                    Mapping m = null;
                    try
                    {
                        m = getReorganizedMappingData();
                    }
                    catch(Exception e)
                    {
                        break;
                    }
                    //Mapping m = getMapping();
                    if ((m == null)||(m.getLinks().getLink().size() == 0)) break;
                    else mapping1 = m;

                }
                rMap = compareMappingData(file, ComponentType.SOURCE, mapping1);
                break;
            }
        }
        catch(Exception ee)
        {
            int res = JOptionPane.showConfirmDialog(this, ee.getMessage() + "\nAll Mapping data will be removed. Are you sure?", "WARNING: Mapping Data Removed", JOptionPane.WARNING_MESSAGE);
            ee.printStackTrace();
            if (res == JOptionPane.YES_OPTION) rMap = null;
            else return;
        }

        if (rMap != null)
        {
            //System.out.println("CCCCC Source1 (rMap != null)" + file.getName());
            int missed = 0;
            if (mapFile != null)
            {
                MappingMainPanel rPanel = new MappingMainPanel(mainFrame);
                rPanel.processOpenMapFile(mapFile, rMap);
                missed = rPanel.getGraphController().getSourceMissedLink().size();
            }
            else
            {
                String tempS = FileUtil.getTemporaryFileName(".map");
                boolean success = true;
                try
                {
                    MappingFactory.saveMapping(new File(tempS), mapping1);
                }
                catch(Exception ee)
                {
                    success = false;
                    System.out.println("Temp map saving failure: " + ee.getMessage());
                }
                if (success)
                {
                    File ff = new File(tempS);
                    MappingMainPanel rPanel = new MappingMainPanel(mainFrame);
                    rPanel.processOpenMapFile(ff, rMap);
                    missed = rPanel.getGraphController().getSourceMissedLink().size();
                    //File ff = new File(tempS);
                    ff.delete();
                }
            }
            int res = JOptionPane.YES_OPTION;
            if (missed != 0)
            {
                res = JOptionPane.showConfirmDialog(this, "" + missed + " Mapping data will be removed. Are you sure?", "WARNING: Lost Mapping Data", JOptionPane.WARNING_MESSAGE);
            }
            if (res == JOptionPane.YES_OPTION) processOpenMapFile(null, rMap);

        }
        else if (file != null)
        {
            processOpenSourceTree(file, true, true, selectedRootTempStore);
            selectedRootTempStore = null;
        }
        //else System.out.println("CCCCC Source3 else");
    }
    private void processingButtonOpenTarget() throws Exception
    {
        File file = DefaultSettings.getUserInputOfFileFromGUI(this,
                        Cmps_V3_MESSAGE_FILE_DEFAULT_EXTENSION+";"+TARGET_TREE_FILE_DEFAULT_EXTENTION, OPEN_DIALOG_TITLE_FOR_DEFAULT_TARGET_FILE, false, false);
        if ((file == null)||(!file.exists())||(!file.isFile())) return;
        String sourceFile = sourceLocationArea.getText();
        if ((sourceFile != null)&&(!sourceFile.trim().equals("")))
        {
            String fileH = "file:/";
            if (sourceFile.toLowerCase().startsWith(fileH))
            {
                sourceFile = sourceFile.substring(fileH.length());
                while(sourceFile.startsWith("/")) sourceFile = sourceFile.substring(1);
            }
            else if (sourceFile.toLowerCase().indexOf(fileH) > 0)
            {
                sourceFile = sourceFile.substring(sourceFile.toLowerCase().indexOf(fileH) + fileH.length());
                while(sourceFile.startsWith("/")) sourceFile = sourceFile.substring(1);
            }

            File f = new File(sourceFile);
            if ((f.exists())&&(f.isFile())&&(file.exists())&&(file.isFile()))
            {
                if (f.getAbsolutePath().equals(file.getAbsolutePath()))
                {
                    JOptionPane.showMessageDialog(this, "This is the same file as the Source", "Same with the Source", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        Mapping rMap = null;
        Mapping mapping1 = null;
        try
        {
            while(true)
            {
                //System.out.println("CCCCC HHH99 : " + file.getAbsolutePath());
                mapping1 = getGraphController().retrieveMappingData(false);
                if ((mapping1 == null)||(mapping1.getLinks().getLink().size() == 0))
                {
                    Mapping m = null;
                    try
                    {
                        m = getReorganizedMappingData();
                    }
                    catch(Exception e)
                    {
                        break;
                    }
                    //Mapping m = getMapping();
                    if ((m == null)||(m.getLinks().getLink().size() == 0)) break;
                    else mapping1 = m;

                }
                rMap = compareMappingData(file, ComponentType.TARGET, mapping1);
                break;
            }
        }
        catch(Exception ee)
        {
            int res = JOptionPane.showConfirmDialog(this, "New Target XSD File '"+file.getName()+"' is not matched with currnet Mapping Data. \nAll Mapping data will be removed. Are you sure?", "WARNING: Mapping Data Removed", JOptionPane.WARNING_MESSAGE);
            if (res == JOptionPane.YES_OPTION) rMap = null;
            else return;
        }

        if (rMap != null)
        {
            int missed = 0;
            if (mapFile != null)
            {
                MappingMainPanel rPanel = new MappingMainPanel(mainFrame);
                rPanel.processOpenMapFile(mapFile, rMap);
                missed = rPanel.getGraphController().getSourceMissedLink().size();
            }
            else
            {
                String tempS = FileUtil.getTemporaryFileName(".map");
                boolean success = true;
                try
                {
                    MappingFactory.saveMapping(new File(tempS), mapping1);
                }
                catch(Exception ee)
                {
                    success = false;
                    System.out.println("Temp map saving failure(Target): " + ee.getMessage());
                }
                if (success)
                {
                    File ff = new File(tempS);
                    MappingMainPanel rPanel = new MappingMainPanel(mainFrame);
                    rPanel.processOpenMapFile(ff, rMap);
                    missed = rPanel.getGraphController().getTargetMissedLink().size();
                    //File ff = new File(tempS);
                    ff.delete();
                }
            }
            int res = JOptionPane.YES_OPTION;
            if (missed != 0)
            {
                res = JOptionPane.showConfirmDialog(this, "" + missed + " Mapping data will be removed. Are you sure?", "WARNING: Lost Mapping Data", JOptionPane.WARNING_MESSAGE);
            }
            if (res == JOptionPane.YES_OPTION) processOpenMapFile(null, rMap);
        }
        else if (file != null)
        {
            //System.out.println("CCCCC Target2 (file != null) : " + file.getName());
            processOpenTargetTree(file, true, true, selectedRootTempStore);
            selectedRootTempStore = null;
        }
        //else System.out.println("CCCCC Target3 else");
    }


    protected void buildSourceTree(Object metaInfo, File absoluteFile, boolean isToResetGraph) throws Exception
	{
		TreeNode nodes=loadSourceTreeData(metaInfo,absoluteFile);
        //DefaultMutableTreeNode hNode = (DefaultMutableTreeNode) nodes;
        //System.out.println("CCCCC head node value ("+absoluteFile.getName()+") : " + hNode.getUserObject().toString());
		//Build the source tree
		sTree = new MappingSourceTree(middlePanel, nodes);
		sTree.addMouseListener(new TreeMouseAdapter());
		TreeSelectionHandler treeSelectionHanderl=new TreeSelectionHandler(getGraphController());
		sTree.getSelectionModel().addTreeSelectionListener(treeSelectionHanderl);
		sTree.setTransferHandler(new TreeDragTransferHandler());
		sTree.setDragEnabled(true);
//		sTree.setDropMode(DropMode.ON);
		sTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        sTree.repaint();
        sourceScrollPane.setViewportView(sTree);

		if (tTree != null && isToResetGraph)
		{
			if (middlePanel != null)
				middlePanel.repaint();
		}
		if (absoluteFile != null)
		{
			String absoluteFilePath = absoluteFile.getAbsolutePath();
			sourceLocationArea.setText(absoluteFilePath);
			sourceLocationArea.setToolTipText(absoluteFilePath);
			mappingSourceFile = absoluteFile;
		}
		else
		{
			mappingSourceFile = null;
		}
		if (this.getRootPane() != null)
		{
			this.getRootPane().repaint();
		}
	}

	protected void buildTargetTree(Object metaInfo, File absoluteFile, boolean isToResetGraph) throws Exception
	{
		TreeNode nodes=loadTargetTreeData(metaInfo,absoluteFile);
		//Build the target tree
		tTree = new MappingTargetTree(this.getMiddlePanel(), nodes);
		tTree.addMouseListener(new TreeMouseAdapter());
		TreeSelectionHandler treeSelectionHanderl=new TreeSelectionHandler(getGraphController());
		tTree.getSelectionModel().addTreeSelectionListener(treeSelectionHanderl);
		TreeTransferHandler targetTreeTransferHandler= new TreeTransferHandler(this);
		tTree.setTransferHandler(targetTreeTransferHandler);
		tTree.setDropMode(DropMode.ON);
		tTree.setDragEnabled(true);
		targetScrollPane.setViewportView(tTree);

		if (sTree != null && isToResetGraph)
		{
			if (middlePanel != null)
				middlePanel.repaint();
		}
		if (absoluteFile != null)
		{
			String absoluteFilePath = absoluteFile.getAbsolutePath();
			targetLocationArea.setText(absoluteFilePath);
			targetLocationArea.setToolTipText(absoluteFilePath);
			mappingTargetFile = absoluteFile;//new File(absoluteFilePath);
		}
		else
		{
			mappingTargetFile = null;
		}
		if (this.getRootPane() != null)
		{
			this.getRootPane().repaint();
		}
	}

	/**
	 * Override the extended implementation of this method by adding additional files of source and target;
	 *
	 * @return a list of file objects that this context is associated with.
	 */
	public java.util.List<File> getAssociatedFileList()
	{
		List<File> resultList = super.getAssociatedFileList();

		if (mappingSourceFile != null)
		{
			resultList.add(mappingSourceFile);
		}
		if (mappingTargetFile != null)
		{
			resultList.add(mappingTargetFile);
		}
		return resultList;
	}


	/**
	 * @return the graphController
	 */
	public MiddlePanelJGraphController getGraphController() {
		return graphController;
	}

	private JComponent getCenterPanel(boolean functionPaneRequired)
	{//construct the top level layout of mapping panel
		/**
		 * GUI Layout:
		 * JSplitPane - Horizontal:   --> leftRightSplitPane
		 *      left: JSplitPane - Horizontal: --> topCenterPanel, centerSplitPane
		 *				left: source panel; --> sourceButtonPanel
		 *				right: JSplitPane - Horizontal: --> rightSplitPane
		 *							left: middle panel for graph; -->middleContainerPanel
		 *							right: target panel; -->targetButtonPanel
		 * 		right: JSplitPane - Vertical:  -->topBottomSplitPane
		 * 				top: functional pane; -->functionPane
		 *				bottom: properties panel; -->propertiesPane
		 */

		JSplitPane leftRightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		DefaultSettings.setDefaultFeatureForJSplitPane(leftRightSplitPane);
        int locDiv = (int) (mainFrame.getAssociatedUIContainer().getWidth() * 0.85);
        leftRightSplitPane.setDividerLocation(locDiv); //.setDividerLocation(0.85);
        //System.out.println("First locDiv : " + locDiv);
        leftRightSplitPane.setLeftComponent(getTopLevelLeftPanel());

		leftRightSplitPane.setRightComponent(getTopLevelRightPanel(functionPaneRequired));
		return leftRightSplitPane;
	}

//	/**
//	 * return the close action inherited with this client.
//	 * @return the close action inherited with this client.
//	 */
//	public Action getDefaultCloseAction()
//	{//by doing this way, the menu and the panel will use the same close action.
//		Map actionMap = getMenuItems(MenuConstants.FILE_MENU_NAME);
//		Action closeAction = (Action) actionMap.get(ActionConstants.CLOSE);
//		return closeAction;
//	}

	/**
	 * return the open action inherited with this client.
	 */
	public Action getDefaultOpenAction()
	{
		ContextManager contextManager = ContextManager.getContextManager();
		Action openAction=contextManager.getDefinedAction(ActionConstants.OPEN_MAP_FILE);
		return openAction;
	}




	/**
	 * @return the mapping
	 */
	private Mapping getMapping() {
		if(this.mapping == null){
			this.mapping = new Mapping();
			getGraphController().setMappingData(mapping, false);
		}
		return mapping;
	}

	public Map getMenuItems(String menu_name)
	{
		Action action = null;
		Map <String, Action>actionMap = null;
		ContextManager contextManager = ContextManager.getContextManager();
		actionMap = contextManager.getClientMenuActions("CMTS", menu_name);
		if (MenuConstants.FILE_MENU_NAME.equals(menu_name))
		{
			JRootPane rootPane = this.getRootPane();
			if (rootPane == null){
				//rootpane is not null implies this panel is fully displayed;
				//on the flip side, if it is null, it implies it is under certain construction.
				contextManager.enableAction(ActionConstants.NEW_MAP_FILE, true);
				contextManager.enableAction(ActionConstants.OPEN_MAP_FILE, true);
				contextManager.enableAction(ActionConstants.CLOSE, false);
				contextManager.enableAction(ActionConstants.SAVE, false);
				contextManager.enableAction(ActionConstants.SAVE_AS, false);
			}else{
				contextManager.enableAction(ActionConstants.CLOSE, true);
				contextManager.enableAction(ActionConstants.SAVE, true);
				contextManager.enableAction(ActionConstants.SAVE_AS, true);
			}
		}

		//since the action depends on the panel instance,
		//the old action instance should be removed
		if (actionMap!=null)
			contextManager.removeClientMenuAction("CMTS", menu_name, "");

		action = new SaveMapAction(this);
		contextManager.addClientMenuAction("CMTS", MenuConstants.FILE_MENU_NAME,ActionConstants.SAVE, action);
		action.setEnabled(true);
		action = new SaveAsMapAction(this);
		contextManager.addClientMenuAction("CMTS", MenuConstants.FILE_MENU_NAME,ActionConstants.SAVE_AS, action);
		action.setEnabled(true);

		actionMap = contextManager.getClientMenuActions("CMTS", menu_name);
		return actionMap;
	}
	public MappingMiddlePanel getMiddlePanel() {
		return middlePanel;
	}

	/**
	 * Return the top root container (frame or dialog or window) this panel is associated with.
	 * @return the top root container (frame or dialog or window) this panel is associated with.
	 */
	public Container getRootContainer()
	{
		return DefaultSettings.findRootContainer(this);
	}



	public JScrollPane getSourceScrollPane() {
		return sourceScrollPane;
	}

	public JTree getSourceTree()
	{
		return sTree;
	}

	public JScrollPane getTargetScrollPane() {
		return targetScrollPane;
	}


	public JTree getTargetTree()
	{
		return tTree;
	}

	/**
	 * Return a list of Action objects that is included in this Context manager.
	 * @return a list of Action objects that is included in this Context manager.
	 */
	public java.util.List<Action> getToolbarActionList()
	{
		java.util.List<Action> actions =super.getToolbarActionList();
		actions.add(getDefaultOpenAction());
		//the menu bar display its buttons inorder
		Map <String, Action>actionMap = getMenuItems(MenuConstants.TOOLBAR_MENU_NAME);
		actions.add(actionMap.get(ActionConstants.SAVE));
		actions.add(actionMap.get(ActionConstants.VALIDATE));
		//add the "Refresh" menu if exist
		actions.add(actionMap.get(ActionConstants.REFRESH));
		return actions;
	}



	protected JPanel getTopLevelLeftPanel()
	{
		JPanel topCenterPanel = new JPanel(new BorderLayout());
		topCenterPanel.setBorder(BorderFactory.createEtchedBorder());
		JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		DefaultSettings.setDefaultFeatureForJSplitPane(centerSplitPane);

        int frameWidth = mainFrame.getAssociatedUIContainer().getWidth();

        //construct source panel
		JPanel sourceButtonPanel = new JPanel(new BorderLayout());
		sourceButtonPanel.setBorder(BorderFactory.createEmptyBorder());
		JPanel sourceLocationPanel = new JPanel(new BorderLayout(2, 0));
		sourceLocationPanel.setBorder(BorderFactory.createEmptyBorder());
		//		sourceTreeCollapseAllAction = new TreeCollapseAllAction(sTree);
		//		sourceTreeExpandAllAction = new TreeExpandAllAction(sTree);

		JToolBar sourceTreeToolBar = new JToolBar("Source Tree Tool Bar");
		sourceTreeToolBar.setFloatable(false);
		//		sourceTreeToolBar.add(sourceTreeExpandAllAction);
		//		sourceTreeToolBar.add(sourceTreeCollapseAllAction);
		sourceLocationPanel.add(sourceTreeToolBar, BorderLayout.WEST);

		sourceLocationArea.setEditable(false);
		//sourceLocationArea.setPreferredSize(new Dimension((DefaultSettings.FRAME_DEFAULT_WIDTH / 10), 24));
        sourceLocationArea.setPreferredSize(new Dimension((frameWidth / 10), 24));
	    sourceLocationPanel.add(sourceLocationArea, BorderLayout.CENTER);

		JButton openSourceButton = new JButton(SELECT_SOURCE);
		sourceLocationPanel.add(openSourceButton, BorderLayout.EAST);
		openSourceButton.setMnemonic('S');
		openSourceButton.setToolTipText(SELECT_CSV_TIP);
		openSourceButton.addActionListener(this);
		sourceButtonPanel.add(sourceLocationPanel, BorderLayout.NORTH);
		//sourceScrollPane.setSize(new Dimension((DefaultSettings.FRAME_DEFAULT_WIDTH / 4), (int) (DefaultSettings.FRAME_DEFAULT_HEIGHT / 1.5)));
        sourceScrollPane.setSize(new Dimension((int)((frameWidth*0.85) / 4), (int) (DefaultSettings.FRAME_DEFAULT_HEIGHT / 1.5)));
		sourceButtonPanel.add(sourceScrollPane, BorderLayout.CENTER);

		//construct target panel
		JPanel targetButtonPanel = new JPanel(new BorderLayout());
		targetButtonPanel.setBorder(BorderFactory.createEmptyBorder());
		JPanel targetLocationPanel = new JPanel(new BorderLayout(2, 0));
		targetLocationPanel.setBorder(BorderFactory.createEmptyBorder());
		//		targetTreeCollapseAllAction = new TreeCollapseAllAction(tTree);
		//		targetTreeExpandAllAction = new TreeExpandAllAction(tTree);
		JToolBar targetTreeToolBar = new JToolBar("Target Tree Tool Bar");
		targetTreeToolBar.setFloatable(false);
		//		targetTreeToolBar.add(targetTreeExpandAllAction);
		//		targetTreeToolBar.add(targetTreeCollapseAllAction);
		targetLocationPanel.add(targetTreeToolBar, BorderLayout.WEST);
		targetLocationArea.setEditable(false);
		//targetLocationArea.setPreferredSize(new Dimension((DefaultSettings.FRAME_DEFAULT_WIDTH / 10), 24));
        targetLocationArea.setPreferredSize(new Dimension((frameWidth / 10), 24));
		targetLocationPanel.add(targetLocationArea, BorderLayout.CENTER);

		JButton openTargetButton = new JButton(SELECT_TARGET);
		targetLocationPanel.add(openTargetButton, BorderLayout.EAST);
		openTargetButton.setMnemonic('T');
		openTargetButton.setToolTipText(SELECT_HMD_TIP);
		openTargetButton.addActionListener(this);
		targetButtonPanel.add(targetLocationPanel, BorderLayout.NORTH);
		targetButtonPanel.add(targetScrollPane, BorderLayout.CENTER);
		//targetButtonPanel.setPreferredSize(new Dimension((DefaultSettings.FRAME_DEFAULT_WIDTH / 5), (int) (DefaultSettings.FRAME_DEFAULT_HEIGHT / 1.5)));
        targetButtonPanel.setPreferredSize(new Dimension((int)((frameWidth*0.85) / 5), (int) (DefaultSettings.FRAME_DEFAULT_HEIGHT / 1.5)));

		//construct middle panel
		JPanel middleContainerPanel = new JPanel(new BorderLayout());
		JLabel placeHolderLabel = new JLabel();
		//placeHolderLabel.setPreferredSize(new Dimension((int) (DefaultSettings.FRAME_DEFAULT_WIDTH / 3.5), 24));
        placeHolderLabel.setPreferredSize(new Dimension((int)((frameWidth*0.85) / 3.5), 24));
		middleContainerPanel.add(placeHolderLabel, BorderLayout.NORTH);
		middleContainerPanel.add(middlePanel, BorderLayout.CENTER);

		JSplitPane rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		DefaultSettings.setDefaultFeatureForJSplitPane(rightSplitPane);
		//rightSplitPane.setDividerLocation(0.5);
        rightSplitPane.setDividerLocation(50);
        rightSplitPane.setLeftComponent(middleContainerPanel);
		rightSplitPane.setRightComponent(targetButtonPanel);

        rightSplitPane.setDividerLocation((int)((frameWidth*0.85)/3));

        centerSplitPane.setLeftComponent(sourceButtonPanel);
		centerSplitPane.setRightComponent(rightSplitPane);
        centerSplitPane.setDividerLocation(((int)((frameWidth*0.85)/3)) - (centerSplitPane.getDividerSize()*2));

        topCenterPanel.add(centerSplitPane, BorderLayout.CENTER);
		//topCenterPanel.setPreferredSize(new Dimension((int) (DefaultSettings.FRAME_DEFAULT_WIDTH * 0.8), (int) (DefaultSettings.FRAME_DEFAULT_HEIGHT / 1.5)));
        topCenterPanel.setPreferredSize(new Dimension((int) (frameWidth * 0.85), (int) (DefaultSettings.FRAME_DEFAULT_HEIGHT / 1.5)));

		return topCenterPanel;


	}

	/**
	 * This constructs function and properties panels.
	 *
	 * @return the top level right pane.
	 */
	private JComponent getTopLevelRightPanel(boolean functionPaneRequired)
	{

        FunctionLibraryPane functionPane = new FunctionLibraryPane(this);
		functionPane.setBorder(BorderFactory.createTitledBorder("Functions"));

		DefaultPropertiesPage propertiesPane = new DefaultPropertiesPage(getGraphController().getPropertiesSwitchController());
        Dimension rightMostDim = new Dimension((DefaultSettings.FRAME_DEFAULT_WIDTH / 11), 50);
	    propertiesPane.setPreferredSize(rightMostDim);
		functionPane.setPreferredSize(rightMostDim);

        JSplitPane topBottomSplitPane = null;
        if(functionPaneRequired)
		{
            topBottomSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, functionPane, propertiesPane);
            DefaultSettings.setDefaultFeatureForJSplitPane(topBottomSplitPane);
            int locDiv = (int) (mainFrame.getAssociatedUIContainer().getHeight() * 0.2);
            if (locDiv < 130) locDiv = 130;
            topBottomSplitPane.setDividerLocation(locDiv);
            //System.out.println("VVVV topBottomSplitPane.getDividerLocation():" + topBottomSplitPane.getDividerLocation());
        }
        else
        {
            topBottomSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JPanel(), propertiesPane);
            DefaultSettings.setDefaultFeatureForJSplitPane(topBottomSplitPane);
            topBottomSplitPane.setDividerLocation(10);
        }

        return topBottomSplitPane;
	}

	protected TreeNode loadSourceTreeData(Object metaInfo, File absoluteFile)throws Exception
	{
		TreeNode node = null;
		if(metaInfo instanceof Mapping){
			Mapping.Components components = ((Mapping)metaInfo).getComponents();
			List<Component> l = components.getComponent();
			for(Component c:l){
				if(c.getType().equals(ComponentType.SOURCE))
					node = (new ElementMetaLoader(ElementMetaLoader.SOURCE_MODE)).loadData(c);
			}
		}else{
			throw new RuntimeException("ElementMetaNodeLoader.loadData() input " +
					"not recognized. " + metaInfo);
		}

		return node;
	}

	protected TreeNode loadTargetTreeData( Object metaInfo, File absoluteFile)throws Exception
	{
		TreeNode node = null;
		if(metaInfo instanceof Mapping){
			Mapping.Components components = ((Mapping)metaInfo).getComponents();
			List<Component> l = components.getComponent();
			for(Component c:l){
				if(c.getType().equals(ComponentType.TARGET))
					node = new ElementMetaLoader(ElementMetaLoader.TARGET_MODE).loadData(c);
			}
		}else{
			throw new RuntimeException("ElementMetaNodeLoader.loadData() input " +
					"not recognized. " + metaInfo);
		}

		return node;
	}

	/**
	 * Called by actionPerformed() and overridable by descendant classes.
	 *
	 * @param file
	 * @throws Exception changed from protected to pulic by sean
	 */
    public void processOpenMapFile(File file) throws Exception
	{
        processOpenMapFile(file, null);
    }
    public void processOpenMapFile(File file, Mapping newMapping) throws Exception
	{

		long stTime=System.currentTimeMillis();
		// parse the file.
		Mapping mapping = null;

        if (newMapping == null)
        {
            mapping = MappingFactory.loadMapping(file);
        }
        else
        {
            //System.out.println("CCCCC FF new mapping instance" + newMapping.getLinks().getLink().size());
            mapping = newMapping;
        }
		//build source tree
		buildSourceTree(mapping, null, false);
		sTree.setSchemaParser(MappingFactory.sourceParser);
		sTree.expandAll();

		//build target tree
		buildTargetTree(mapping, null, false);
		tTree.setSchemaParser(MappingFactory.targetParser);
		tTree.expandAll();
        for (Component mapComp:mapping.getComponents().getComponent())
		{
            if (mapComp.getRootElement()==null) continue;

            JTextField tField = null;
            if (mapComp.getType().value().equals(ComponentType.SOURCE.value())) tField = sourceLocationArea;
            if (mapComp.getType().value().equals(ComponentType.TARGET.value())) tField = targetLocationArea;

            if (tField != null)
            {
                String c = mapComp.getLocation();
                if (c == null) c = "";
                else c = c.trim();
                if (!c.equals(""))
                {
                    tField.setText(mapComp.getLocation());
                    //tField.setEditable(true);
                    //tField.setFocusable(false);
                }
            }
            if (mapComp.getType().value().equals(ComponentType.TARGET.value())) targetLocationArea.setText(mapComp.getLocation());
        }

        if (newMapping == null) getGraphController().setMappingData(mapping, false);
        else
        {
            getGraphController().setMappingData(mapping, true);
        }
        if (file == null)
        {
            if (mapFile != null)
                setSaveFile(mapFile);
        }
        else
        {
            //System.out.println("CCCCC GGGG : map file = " + file.getAbsolutePath());
            mapFile = file;
            setSaveFile(file);
        }
        getMiddlePanel().renderInJGraph();
		System.out.println("CmtsMappingPanel.processOpenMapFile()..timespending:"+(System.currentTimeMillis()-stTime));
//        if (newMapping == null)
//        {
//            //this.mapping = mapping;
//            mapFile = file;
//        }
    }


	/**
	 * Called by actionPerformed() and overridable by descendant classes.
	 *
	 * @param file
	 * @throws Exception
	 */
    protected boolean processOpenSourceTree(File file, boolean isToResetGraph, boolean supressReportIssuesToUI) throws Exception
	{
        return processOpenSourceTree(file, isToResetGraph, supressReportIssuesToUI, null);
    }
    protected boolean processOpenSourceTree(File file, boolean isToResetGraph, boolean supressReportIssuesToUI, CellRenderXSObject selectedRoot) throws Exception
	{

        XSDParser p = new XSDParser();
        p.loadSchema(file.getPath(), null);//(file);
        CellRenderXSObject srcRoot = null;

        if (selectedRoot == null) srcRoot = userSelectRoot(p);
        else  srcRoot = selectedRoot;

        if(srcRoot == null || srcRoot.getCoreObject().getName().trim().length() == 0)
            return false;

        //mapping = null;
        //System.out.println("CCCCC HHH991 : " + file.getAbsolutePath() + ", xsdParser=" + p.getSchemaURI());
        MappingFactory.loadMetaXSD(getMapping(), p,srcRoot.getCoreObject().getNamespace(), srcRoot.getCoreObject().getName(), ComponentType.SOURCE);

        buildSourceTree(getMapping(), file, isToResetGraph);
        sTree.setSchemaParser(p);
        //DefaultMutableTreeNode hNode = (DefaultMutableTreeNode) sTree.getRootTreeNode();
        //System.out.println("CCCCC head node value (2 : "+file.getName()+") : " + hNode.getUserObject().toString());
        sTree.expandAll();

        return true;
	}
    /*
    protected boolean processOpenSourceTree(File file, boolean isToResetGraph, boolean supressReportIssuesToUI, Mapping anotherMapping) throws Exception
	{
//		String fileExtension = FileUtil.getFileExtension(file, true);
		XSDParser p = new XSDParser();
		p.loadSchema(file.getPath(), null);//(file);
		CellRenderXSObject srcRoot = userSelectRoot(p);
		if(srcRoot == null || srcRoot.getCoreObject().getName().trim().length() == 0)
			return false;
		mapping = null;
		MappingFactory.loadMetaXSD(getMapping(), p,srcRoot.getCoreObject().getNamespace(), srcRoot.getCoreObject().getName(), ComponentType.SOURCE);

		buildSourceTree(getMapping(), file, isToResetGraph);
		sTree.setSchemaParser(p);
        DefaultMutableTreeNode hNode = (DefaultMutableTreeNode) sTree.getRootTreeNode();
        //System.out.println("CCCCC head node value (2 : "+file.getName()+") : " + hNode.getUserObject().toString());
        sTree.expandAll();
		return true;
	}
    */
    private Mapping compareMappingData(File xsdFile, ComponentType type, Mapping mapping1) throws Exception
	{
//        Mapping mapping1 = getGraphController().retrieveMappingData(false);
//        //Mapping mapping1 = getReorganizedMappingData();
//
//        if (mapping1 == null) return null;
//
//        if (mapping1.getLinks().getLink().size() == 0) return null;

        XSDParser p = new XSDParser();
		p.loadSchema(xsdFile.getPath(), null);
		CellRenderXSObject srcRoot = userSelectRoot(p);
		if(srcRoot == null || srcRoot.getCoreObject().getName().trim().length() == 0)
			throw new Exception("Finding root object is Failure : " + xsdFile.getName());
        //Mapping mapping2 = new Mapping();
        //System.out.println("CCCCC HHH992 : " + xsdFile.getAbsolutePath() + ", xsdParser=" + p.getSchemaURI());
        //MappingFactory.loadMetaXSD(mapping2, p,srcRoot.getCoreObject().getNamespace(), srcRoot.getCoreObject().getName(), ComponentType.SOURCE);


        Mapping mapping3 = null;
        if (type == ComponentType.SOURCE) mapping3 = MappingFactory.loadMapping(mapFile, xsdFile, null, mapping1);
        else if (type == ComponentType.TARGET) mapping3 = MappingFactory.loadMapping(mapFile, null, xsdFile, mapping1);

        return mapping3;

	}


    /**
	 * Called by actionPerformed() and overridable by descendant classes.
	 *
	 * @param file
	 * @throws Exception
	 */
    protected boolean processOpenTargetTree(File file, boolean isToResetGraph, boolean supressReportIssuesToUI) throws Exception
	{
        return processOpenTargetTree(file, isToResetGraph, supressReportIssuesToUI, null);
    }
    protected boolean processOpenTargetTree(File file, boolean isToResetGraph, boolean supressReportIssuesToUI, CellRenderXSObject selectedRoot) throws Exception
	{
//		String fileExtension = FileUtil.getFileExtension(file, true);
		XSDParser p = new XSDParser();
		p.loadSchema(file.getPath(), null);
		CellRenderXSObject trgtRoot = null;

        if (selectedRoot == null) trgtRoot = userSelectRoot(p);
        else trgtRoot = selectedRoot;

        if(trgtRoot == null || trgtRoot.getCoreObject().getName().trim().length() == 0)
			return false;
        //mapping = null;
        //System.out.println("CCCCC HHH993 : " + file.getAbsolutePath() + ", xsdParser=" + p.getSchemaURI());
        MappingFactory.loadMetaXSD(getMapping(), p, trgtRoot.getCoreObject().getNamespace(), trgtRoot.getCoreObject().getName(), ComponentType.TARGET);

		buildTargetTree(getMapping(), file, isToResetGraph);
		tTree.setSchemaParser(p);
		tTree.expandAll();
		return true;

    }

	@Override
	public void setSize(int width, int height)
	{
		double topCenterFactor = 1;
		sourceLocationArea.setSize(new Dimension((width / 6), 25));
		sourceScrollPane.setSize(new Dimension((int) (width / 4.5), (int) (height * topCenterFactor)));
		targetLocationArea.setSize(new Dimension(width / 6, 25));
		targetScrollPane.setSize(new Dimension((int) (width / 4.5), (int) (height * topCenterFactor)));
		middlePanel.setSize(new Dimension((width / 4), (int) (height * topCenterFactor)));

		topCenterFactor = 0.5;
		Dimension rightMostDim = new Dimension((width / 5), (int) (height * topCenterFactor));
	}
	/**
	 * Promote user to select a root node:Element or Complex type
	 * @param xsdParser
	 * @return
	 */
	private CellRenderXSObject userSelectRoot(XSDParser xsdParser)
	{
		XSNamedMap[] map = xsdParser.getMappableNames();
		ArrayList<CellRenderXSObject> choices = new ArrayList<CellRenderXSObject>();

		for(int i=0; i<map[0].getLength(); i++)
			choices.add(new CellRenderXSObject(map[0].item(i)));//.getName();
		for(int i=0; i<map[1].getLength(); i++)
		{
			XSObject xsItem=map[1].item(i);
			if (xsItem instanceof XSComplexTypeDecl)
				choices.add(new CellRenderXSObject(xsItem));
		}
        CellRenderXSObject chosenRoot = null;
        Container con = this;
        while(con != null)
        {
            if ((con instanceof Frame)||(con instanceof Applet))
            {
                chosenRoot = (CellRenderXSObject)DefaultSettings.showListChoiceDialog(con, "choose root element", "Please choose root element", choices.toArray());
                break;
            }
            con = con.getParent();
        }
        //CellRenderXSObject chosenRoot = (CellRenderXSObject)DefaultSettings.showListChoiceDialog(MainFrame.getInstance(), "choose root element", "Please choose root element", choices);
		selectedRootTempStore = chosenRoot;
        return chosenRoot;
	}

	public void persistFile(File persistentFile)
	{

		MiddlePanelJGraphController mappingManager = getGraphController();//.getMiddlePanel().getGraphController();
		Mapping mappingData = mappingManager.retrieveMappingData(true);
		Collections.sort(mappingData.getTags().getTag());


		try {
			//set relative path for source and target schema files.

            String sourceRelatve=ResourceUtils.getRelativePath(sTree.getSchemaParser().getSchemaURI(),
					persistentFile.getCanonicalFile().toURI().toString(),
					File.separator);
			String targetRelatve=ResourceUtils.getRelativePath(tTree.getSchemaParser().getSchemaURI(),
					persistentFile.getCanonicalFile().toURI().toString(),
					File.separator);
			for (Component mapComp:mappingData.getComponents().getComponent())
			{
				if (mapComp.getRootElement()!=null)
				{
	                if (mapComp.getType() == ComponentType.SOURCE)
	                	mapComp.setLocation(sourceRelatve);
	                else if ( mapComp.getType() == ComponentType.TARGET)
	                	mapComp.setLocation(targetRelatve);
				}
			}

			MappingFactory.saveMapping(persistentFile, mappingData);
			JOptionPane.showMessageDialog(getParent(), "Mapping data has been saved successfully.", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//clear the change flag.
		getGraphController().setGraphChanged(false);
	}

    public Mapping getReorganizedMappingData()
	{

		MiddlePanelJGraphController mappingManager = getGraphController();//.getMiddlePanel().getGraphController();
		Mapping mappingData = mappingManager.retrieveMappingData(true);
		Collections.sort(mappingData.getTags().getTag());

        return mappingData;

	}
}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.15  2009/11/23 18:31:44  wangeug
 * HISTORY: create new package: ui.main
 * HISTORY:
 * HISTORY: Revision 1.14  2009/11/03 18:32:54  wangeug
 * HISTORY: clean codes: keep MiddlePanelJGraphController only with MiddleMappingPanel
 * HISTORY:
 * HISTORY: Revision 1.13  2009/10/30 14:45:30  wangeug
 * HISTORY: simplify code: only respond to link highter
 * HISTORY:
 * HISTORY: Revision 1.12  2009/10/28 16:47:06  wangeug
 * HISTORY: clean codes
 * HISTORY:
 * HISTORY: Revision 1.11  2009/10/27 19:26:16  wangeug
 * HISTORY: clean codes
 * HISTORY:
 * HISTORY: Revision 1.10  2009/10/27 18:25:08  wangeug
 * HISTORY: hook property panel with tree nodes
 * HISTORY:
 * HISTORY: Revision 1.9  2009/10/16 17:36:34  wangeug
 * HISTORY: use cell renderer
 * HISTORY:
 * HISTORY: Revision 1.8  2009/10/15 18:35:33  wangeug
 * HISTORY: clean codes
 * HISTORY:
 * HISTORY: Revision 1.7  2008/12/29 22:18:18  linc
 * HISTORY: function UI added.
 * HISTORY:
 * HISTORY: Revision 1.6  2008/12/09 19:04:17  linc
 * HISTORY: First GUI release
 * HISTORY:
 * HISTORY: Revision 1.5  2008/12/04 21:34:20  linc
 * HISTORY: Drap and Drop support with new Swing.
 * HISTORY:
 * HISTORY: Revision 1.4  2008/12/03 20:46:14  linc
 * HISTORY: UI update.
 * HISTORY:
 * HISTORY: Revision 1.3  2008/11/04 15:58:57  linc
 * HISTORY: updated.
 * HISTORY:
 * HISTORY: Revision 1.2  2008/10/30 16:02:14  linc
 * HISTORY: updated.
 * HISTORY:
 * HISTORY: Revision 1.1  2008/10/27 20:06:30  linc
 * HISTORY: GUI first add.
 * HISTORY:
 */

