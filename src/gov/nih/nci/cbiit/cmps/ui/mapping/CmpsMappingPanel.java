/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmps.ui.mapping;

import gov.nih.nci.cbiit.cmps.common.XSDParser;
import gov.nih.nci.cbiit.cmps.core.Component;
import gov.nih.nci.cbiit.cmps.core.ComponentType;
import gov.nih.nci.cbiit.cmps.core.Mapping;
import gov.nih.nci.cbiit.cmps.mapping.MappingFactory;
import gov.nih.nci.cbiit.cmps.ui.actions.SaveAsMapAction;
import gov.nih.nci.cbiit.cmps.ui.actions.SaveMapAction;
import gov.nih.nci.cbiit.cmps.ui.common.ActionConstants;
import gov.nih.nci.cbiit.cmps.ui.common.ContextManager;
import gov.nih.nci.cbiit.cmps.ui.common.ContextManagerClient;
import gov.nih.nci.cbiit.cmps.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmps.ui.common.MenuConstants;
import gov.nih.nci.cbiit.cmps.ui.function.FunctionLibraryPane;
import gov.nih.nci.cbiit.cmps.ui.jgraph.MiddlePanelJGraphController;
import gov.nih.nci.cbiit.cmps.ui.properties.DefaultPropertiesPage;
import gov.nih.nci.cbiit.cmps.ui.tree.MappingSourceTree;
import gov.nih.nci.cbiit.cmps.ui.tree.MappingTargetTree;
import gov.nih.nci.cbiit.cmps.ui.tree.TreeTransferHandler;
import gov.nih.nci.cbiit.cmps.ui.util.GeneralUtilities;
import gov.nih.nci.cbiit.cmps.util.FileUtil;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.apache.xerces.xs.XSNamedMap;

/**
 * This class 
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMPS v1.0
 * @version    $Revision: 1.11 $
 * @date       $Date: 2009-10-27 19:26:16 $
 *
 */
public class CmpsMappingPanel extends JPanel implements ActionListener, ContextManagerClient{

	private File saveFile = null;

	private FunctionLibraryPane functionPane;
	private MappingTreeScrollPane sourceScrollPane = new MappingTreeScrollPane(MappingTreeScrollPane.DRAW_NODE_TO_RIGHT);
	private MappingTreeScrollPane targetScrollPane = new MappingTreeScrollPane(MappingTreeScrollPane.DRAW_NODE_TO_LEFT);

	private JTextField sourceLocationArea = new JTextField();
	private JTextField targetLocationArea = new JTextField();
	private MappingMiddlePanel middlePanel = null;
	private MappingSourceTree sTree = null;
	private MappingTargetTree tTree = null;

	private TreeTransferHandler dndHandler = null;
	//	protected TreeCollapseAllAction sourceTreeCollapseAllAction;
	//	protected TreeExpandAllAction sourceTreeExpandAllAction;
	//	
	//	protected TreeCollapseAllAction targetTreeCollapseAllAction;
	//	protected TreeExpandAllAction targetTreeExpandAllAction;
	//
	//	protected MappingFileSynchronizer fileSynchronizer;
	//	

	private File mappingSourceFile = null;
	private File mappingTargetFile = null;
	private Mapping mapping = null;

	private static final String SELECT_SOURCE = "Open Source...";
	private static final String SELECT_TARGET = "Open Target...";
	private static final String SELECT_CSV_TIP = "select CSV";
	private static final String SELECT_HMD_TIP = "select HMD";
	private static final String OPEN_DIALOG_TITLE_FOR_DEFAULT_SOURCE_FILE = "Open source data schema";
	private static final String OPEN_DIALOG_TITLE_FOR_DEFAULT_TARGET_FILE = "Open target data schema";
	private static final String SOURCE_TREE_FILE_DEFAULT_EXTENTION = ".xsd";
	private static final String TARGET_TREE_FILE_DEFAULT_EXTENTION = ".xsd";
	private static final String Cmps_V3_MESSAGE_FILE_DEFAULT_EXTENSION = ".map";
	private static final Object CSV_METADATA_FILE_DEFAULT_EXTENTION = ".xsd";
	private static final Object HSM_META_DEFINITION_FILE_DEFAULT_EXTENSION = ".xsd";

	//	private TargetTreeDropTransferHandler targetTreeDropTransferHandler = null;

	public CmpsMappingPanel() throws Exception
	{
		this("","calledFromConstructor","");
	}

	public CmpsMappingPanel(String sourceFile, String _flag) throws Exception
	{
		this(sourceFile, "calledFromConstructor", _flag);
	}
	public CmpsMappingPanel(String sourceFile, String targetFile, String _flag) throws Exception
	{
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setLayout(new BorderLayout());
		this.add(getCenterPanel(true), BorderLayout.CENTER);
		//		fileSynchronizer = new MappingFileSynchronizer(this);

		if (!sourceFile.equals(""))
			processOpenSourceTree(new File(sourceFile), false, false);

		if ((targetFile == null)||(targetFile.equals(""))) throw new Exception("Empty Target File");
		if (!targetFile.equals("calledFromConstructor"))
		{
			File file = new File(targetFile);
			if (!file.exists()) throw new Exception("Target File is Not exist. : " + targetFile);
			if (!file.isFile()) throw new Exception("Target File is Not a file. : " + targetFile);
			boolean success = processOpenTargetTree(file, true, true);
			if (!success) throw new Exception("GEN3");
		}
	}

	protected JComponent getCenterPanel(boolean functionPaneRequired)
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
		leftRightSplitPane.setDividerLocation(0.85);
		leftRightSplitPane.setLeftComponent(getTopLevelLeftPanel());
		leftRightSplitPane.setRightComponent(getTopLevelRightPanel(functionPaneRequired));
		return leftRightSplitPane;
	}

	/**
	 * This constructs function and properties panels.
	 *
	 * @return the top level right pane.
	 */
	private JComponent getTopLevelRightPanel(boolean functionPaneRequired)
	{
		JSplitPane topBottomSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		DefaultSettings.setDefaultFeatureForJSplitPane(topBottomSplitPane);
		//topBottomSplitPane.setBorder(BorderFactory.createEtchedBorder());
		topBottomSplitPane.setDividerLocation(0.5);

		functionPane = new FunctionLibraryPane(this);
		functionPane.setBorder(BorderFactory.createTitledBorder("Functions"));
		if(functionPaneRequired)
		{
			topBottomSplitPane.setTopComponent(functionPane);
		}
		DefaultPropertiesPage propertiesPane = new DefaultPropertiesPage(middlePanel.getMiddlePanelJGraphController().getPropertiesSwitchController());//this.getMappingDataManager().getPropertiesSwitchController());
		topBottomSplitPane.setBottomComponent(propertiesPane);

		double topCenterFactor = 0.3;
		Dimension rightMostDim = new Dimension((int) (DefaultSettings.FRAME_DEFAULT_WIDTH / 11), (int) (DefaultSettings.FRAME_DEFAULT_HEIGHT * topCenterFactor));
		propertiesPane.setPreferredSize(rightMostDim);
		functionPane.setPreferredSize(rightMostDim);
		//functionPane.getFunctionTree().getSelectionModel().addTreeSelectionListener((TreeSelectionListener) (getMappingDataManager().getPropertiesSwitchController()));

		topCenterFactor = 1.5;
		rightMostDim = new Dimension((int) (DefaultSettings.FRAME_DEFAULT_WIDTH / 10), (int) (DefaultSettings.FRAME_DEFAULT_HEIGHT / topCenterFactor));
		topBottomSplitPane.setPreferredSize(rightMostDim);

		return topBottomSplitPane;
	}

	protected JPanel getTopLevelLeftPanel()
	{
		JPanel topCenterPanel = new JPanel(new BorderLayout());
		topCenterPanel.setBorder(BorderFactory.createEtchedBorder());
		JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		DefaultSettings.setDefaultFeatureForJSplitPane(centerSplitPane);

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
		sourceLocationArea.setPreferredSize(new Dimension((int) (DefaultSettings.FRAME_DEFAULT_WIDTH / 10), 24));
		sourceLocationPanel.add(sourceLocationArea, BorderLayout.CENTER);
		
		JButton openSourceButton = new JButton(SELECT_SOURCE);
		sourceLocationPanel.add(openSourceButton, BorderLayout.EAST);
		openSourceButton.setMnemonic('S');
		openSourceButton.setToolTipText(SELECT_CSV_TIP);
		openSourceButton.addActionListener(this);
		sourceButtonPanel.add(sourceLocationPanel, BorderLayout.NORTH);
		sourceScrollPane.setSize(new Dimension((int) (DefaultSettings.FRAME_DEFAULT_WIDTH / 4), (int) (DefaultSettings.FRAME_DEFAULT_HEIGHT / 1.5)));
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
		targetLocationArea.setPreferredSize(new Dimension((int) (DefaultSettings.FRAME_DEFAULT_WIDTH / 10), 24));
		targetLocationPanel.add(targetLocationArea, BorderLayout.CENTER);

		JButton openTargetButton = new JButton(SELECT_TARGET);
		targetLocationPanel.add(openTargetButton, BorderLayout.EAST);
		openTargetButton.setMnemonic('T');
		openTargetButton.setToolTipText(SELECT_HMD_TIP);
		openTargetButton.addActionListener(this);
		targetButtonPanel.add(targetLocationPanel, BorderLayout.NORTH);
		targetButtonPanel.add(targetScrollPane, BorderLayout.CENTER);
		targetButtonPanel.setPreferredSize(new Dimension((int) (DefaultSettings.FRAME_DEFAULT_WIDTH / 5), (int) (DefaultSettings.FRAME_DEFAULT_HEIGHT / 1.5)));

		//construct middle panel
		JPanel middleContainerPanel = new JPanel(new BorderLayout());
		JLabel placeHolderLabel = new JLabel();
		placeHolderLabel.setPreferredSize(new Dimension((int) (DefaultSettings.FRAME_DEFAULT_WIDTH / 3.5), 24));
		middlePanel = new MappingMiddlePanel(this);
		middlePanel.setSize(new Dimension((int) (DefaultSettings.FRAME_DEFAULT_WIDTH / 3), (int) (DefaultSettings.FRAME_DEFAULT_HEIGHT / 1.5)));
		middleContainerPanel.add(placeHolderLabel, BorderLayout.NORTH);
		middleContainerPanel.add(middlePanel, BorderLayout.CENTER);

		JSplitPane rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		DefaultSettings.setDefaultFeatureForJSplitPane(rightSplitPane);
		rightSplitPane.setDividerLocation(0.5);
		rightSplitPane.setLeftComponent(middleContainerPanel);
		rightSplitPane.setRightComponent(targetButtonPanel);

		centerSplitPane.setLeftComponent(sourceButtonPanel);
		centerSplitPane.setRightComponent(rightSplitPane);

		topCenterPanel.add(centerSplitPane, BorderLayout.CENTER);
		topCenterPanel.setPreferredSize(new Dimension((int) (DefaultSettings.FRAME_DEFAULT_WIDTH * 0.8), (int) (DefaultSettings.FRAME_DEFAULT_HEIGHT / 1.5)));

		return topCenterPanel;


	}

	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		try
		{
			boolean everythingGood = true;
			if (SELECT_SOURCE.equals(command))
			{
				//this.sourceButtonPanel.repaint();
				File file = DefaultSettings.getUserInputOfFileFromGUI(this, //FileUtil.getUIWorkingDirectoryPath(),
						SOURCE_TREE_FILE_DEFAULT_EXTENTION, OPEN_DIALOG_TITLE_FOR_DEFAULT_SOURCE_FILE, false, false);
				if (file != null)
				{
					everythingGood = processOpenSourceTree(file, true, true);
				}
			}
			else if (SELECT_TARGET.equals(command))
			{
				//this.targetButtonPanel.repaint();
				File file = DefaultSettings.getUserInputOfFileFromGUI(this,
						//						TARGET_TREE_FILE_DEFAULT_EXTENTION, OPEN_DIALOG_TITLE_FOR_DEFAULT_TARGET_FILE, false, false);
						//FileUtil.getUIWorkingDirectoryPath(),
						//					TARGET_TREE_FILE_DEFAULT_EXTENTION+";"+Cmps_V3_MESSAGE_FILE_DEFAULT_EXTENSION, OPEN_DIALOG_TITLE_FOR_DEFAULT_TARGET_FILE, false, false);
						//last added fileExtension :.h3s will be set as default
						Cmps_V3_MESSAGE_FILE_DEFAULT_EXTENSION+";"+TARGET_TREE_FILE_DEFAULT_EXTENTION, OPEN_DIALOG_TITLE_FOR_DEFAULT_TARGET_FILE, false, false);
				if (file != null)
				{
					everythingGood = processOpenTargetTree(file, true, true);
				}
			}
			if (!everythingGood)
			{
				//				Message msg = MessageResources.getMessage("GEN3", new Object[0]);
				//				JOptionPane.showMessageDialog(this, msg.toString(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			DefaultSettings.reportThrowableToLogAndUI(this, e1, "", this, false, false);
		}
	}


	/**
	 * @return the dndHandler
	 */
	public TreeTransferHandler getDndHandler() {
		if(this.dndHandler == null)
			this.dndHandler = new TreeTransferHandler(this);
		return dndHandler;
	}

	protected TreeNode loadSourceTreeData(Object metaInfo, File absoluteFile)throws Exception
	{
		String fileExtension = FileUtil.getFileExtension(absoluteFile, true);
		TreeNode node = null;
		if(metaInfo instanceof Mapping){
			Mapping.Components components = ((Mapping)metaInfo).getComponents();
			List<Component> l = components.getComponent();
			for(Component c:l){
				if(c.getType().equals(ComponentType.SOURCE))
					node = new ElementMetaLoader(ElementMetaLoader.SOURCE_MODE).loadData(c);
			}
		}else{
			throw new RuntimeException("ElementMetaNodeLoader.loadData() input " +
					"not recognized. " + metaInfo);
		}

		return node;
	}

	protected TreeNode loadTargetTreeData( Object metaInfo, File absoluteFile)throws Exception
	{
		String fileExtension = FileUtil.getFileExtension(absoluteFile, true);
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

	protected void buildSourceTree(Object metaInfo, File absoluteFile, boolean isToResetGraph) throws Exception
	{
		TreeNode nodes=loadSourceTreeData(metaInfo,absoluteFile);

		//Build the source tree
		sTree = new MappingSourceTree(middlePanel, nodes);
		sTree.getSelectionModel().addTreeSelectionListener((TreeSelectionListener) (this.getMiddlePanelJGraphController().getPropertiesSwitchController()));
		sTree.setTransferHandler(getDndHandler());
		sTree.setDragEnabled(true);
		sTree.setDropMode(DropMode.ON);
		sTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		sourceScrollPane.setViewportView(sTree);
		sTree.expandAll();

		//register collapse all and expand all actions.
		//		sourceTreeCollapseAllAction.setTree(sTree);
		//		sourceTreeExpandAllAction.setTree(sTree);
		//		sTree.getInputMap().put(sourceTreeCollapseAllAction.getAcceleratorKey(), sourceTreeCollapseAllAction.getName());
		//		sTree.getActionMap().put(sourceTreeCollapseAllAction.getName(), sourceTreeCollapseAllAction);
		//		sTree.getInputMap().put(sourceTreeExpandAllAction.getAcceleratorKey(), sourceTreeExpandAllAction.getName());
		//		sTree.getActionMap().put(sourceTreeExpandAllAction.getName(), sourceTreeExpandAllAction);

		if (tTree != null && isToResetGraph)
		{
			resetMiddlePanel();
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
		//		getMappingFileSynchronizer().registerFile(MappingFileSynchronizer.FILE_TYPE.Source_File, absoluteFile);
	}

	protected void buildTargetTree(Object metaInfo, File absoluteFile, boolean isToResetGraph) throws Exception
	{
		TreeNode nodes=loadTargetTreeData(metaInfo,absoluteFile);
		//Build the target tree
		tTree = new MappingTargetTree(this.getMiddlePanel(), nodes);
		tTree.getSelectionModel().addTreeSelectionListener((TreeSelectionListener) (getMiddlePanelJGraphController().getPropertiesSwitchController()));
		tTree.setTransferHandler(getDndHandler());
		tTree.setDropMode(DropMode.ON);
		targetScrollPane.setViewportView(tTree);
		tTree.expandAll();
		
		
		//		//register collapse all and expand all actions.
		//		targetTreeCollapseAllAction.setTree(tTree);
		//		targetTreeExpandAllAction.setTree(tTree);
		//		tTree.getInputMap().put(targetTreeCollapseAllAction.getAcceleratorKey(), targetTreeCollapseAllAction.getName());
		//		tTree.getActionMap().put(targetTreeCollapseAllAction.getName(), targetTreeCollapseAllAction);
		//		tTree.getInputMap().put(targetTreeExpandAllAction.getAcceleratorKey(), targetTreeExpandAllAction.getName());
		//		tTree.getActionMap().put(targetTreeExpandAllAction.getName(), targetTreeExpandAllAction);

		if (sTree != null && isToResetGraph)
		{
			resetMiddlePanel();
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
		//		getMappingFileSynchronizer().registerFile(MappingFileSynchronizer.FILE_TYPE.Target_File, absoluteFile);
		////		drop target for DnD from source tree.
		//		targetTreeDropTransferHandler = new TargetTreeDropTransferHandler(tTree, getMappingDataManager(), DnDConstants.ACTION_LINK);
	}

	/**
	 * Called by actionPerformed() and overridable by descendant classes.
	 *
	 * @param file
	 * @throws Exception
	 */
	protected boolean processOpenSourceTree(File file, boolean isToResetGraph, boolean supressReportIssuesToUI) throws Exception
	{
//		String fileExtension = FileUtil.getFileExtension(file, true);
		XSDParser p = new XSDParser();
		p.loadSchema(file);
		CellRenderXSObject srcRoot = userSelectRoot(p);
		if(srcRoot == null || srcRoot.getCoreObject().getName().trim().length() == 0)
			return false;
		 
		MappingFactory.loadMetaSourceXSD(getMapping(), p,srcRoot.getCoreObject().getNamespace(), srcRoot.getCoreObject().getName());

		buildSourceTree(getMapping(), file, isToResetGraph);
		return true;
	}

	/**
	 * Promote user to select a root node:Element or Complex type
	 * @param xsdParser
	 * @return
	 */
	private CellRenderXSObject userSelectRoot(XSDParser xsdParser)
	{
		XSNamedMap[] map = xsdParser.getMappableNames();
		CellRenderXSObject[] choices = new CellRenderXSObject[map[0].getLength()+map[1].getLength()];
		int pos = 0;
		for(int i=0; i<map[0].getLength(); i++)
			choices[pos++] =new CellRenderXSObject(map[0].item(i));//.getName();
		for(int i=0; i<map[1].getLength(); i++)
			choices[pos++] = new CellRenderXSObject(map[1].item(i));//.getNamespace() +":" +map[1].item(i).getName();

		CellRenderXSObject chosenRoot = (CellRenderXSObject)DefaultSettings.showListChoiceDialog(MainFrame.getInstance(), "choose root element", "Please choose root element", choices);
		return chosenRoot;
	}
	/**
	 * Called by actionPerformed() and overridable by descendant classes.
	 *
	 * @param file
	 * @throws Exception
	 */
	protected boolean processOpenTargetTree(File file, boolean isToResetGraph, boolean supressReportIssuesToUI) throws Exception
	{
//		String fileExtension = FileUtil.getFileExtension(file, true);
		XSDParser p = new XSDParser();
		p.loadSchema(file);
		CellRenderXSObject trgtRoot = userSelectRoot(p);
		if(trgtRoot == null || trgtRoot.getCoreObject().getName().trim().length() == 0)
			return false;
		MappingFactory.loadMetaTargetXSD(getMapping(), p, trgtRoot.getCoreObject().getNamespace(), trgtRoot.getCoreObject().getName());

		buildTargetTree(getMapping(), file, isToResetGraph);
		//		middlePanel.getMappingDataManager().registerTargetComponent(metaInfo, file);
		return true;
	}

	/**
	 * Called by actionPerformed() and overridable by descendant classes.
	 *
	 * @param file
	 * @throws Exception changed from protected to pulic by sean
	 */
	public void processOpenMapFile(File file) throws Exception
	{
		System.out.println("CmpsMappingPanel.processOpenMapFile()...:"+file.getAbsolutePath());
		long stTime=System.currentTimeMillis();
		// parse the file.
		Mapping mapping = MappingFactory.loadMapping(file);

		//build source tree
		buildSourceTree(mapping, null, false);
		//build target tree
		buildTargetTree(mapping, null, false);

		System.out.println("before setMappingData");
		middlePanel.getGraphController().setMappingData(mapping);
		System.out.println("after setMappingData");

		setSaveFile(file);
		System.out.println("CmpsMappingPanel.processOpenMapFile()..timespending:"+(System.currentTimeMillis()-stTime));
	}


	public Map getMenuItems(String menu_name)
	{
		Action action = null;
		Map <String, Action>actionMap = null;
		ContextManager contextManager = ContextManager.getContextManager();
		actionMap = contextManager.getClientMenuActions("CMPS", menu_name);
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
			contextManager.removeClientMenuAction("CMPS", menu_name, "");
		
		action = new SaveMapAction(this);
		contextManager.addClientMenuAction("CMPS", MenuConstants.FILE_MENU_NAME,ActionConstants.SAVE, action);
		action.setEnabled(true);
		action = new SaveAsMapAction(this);
		contextManager.addClientMenuAction("CMPS", MenuConstants.FILE_MENU_NAME,ActionConstants.SAVE_AS, action);
		action.setEnabled(true);

		actionMap = contextManager.getClientMenuActions("CMPS", menu_name);
		return actionMap;
	}

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
	 * Explicitly reload information from the internal given file.
	 *
	 * @throws Exception
	 */
	public void reload() throws Exception
	{
		//		processOpenMapFile(getSaveFile());
	}

	//	protected TreeDefaultDropTransferHandler getTargetTreeDropTransferHandler()
	//	{
	//		return this.targetTreeDropTransferHandler;
	//	}

	/**
	 * Reload the file specified in the parameter.
	 * @param changedFileMap
	 */
	//	public void reload(Map<MappingFileSynchronizer.FILE_TYPE, File> changedFileMap)
	//	{
	//		/**
	//		 * Design rationale:
	//		 * 1) if the changedFileMap is null, simply return;
	//		 * 2) if the getSaveFile() method does not return null, it implies current panel associates with a mapping file,
	//		 * just reload the whole mapping file so as to refresh those mapping relationship;
	//		 * 3) if the getSaveFile() returns null, just reload source and/or target file within the changedFileMap,
	//		 * and ignore the checking of MappingFileSynchronizer.FILE_TYPE.Mapping_File item in the map;
	//		 */
	//		if(changedFileMap==null)
	//		{
	//			return;
	//		}
	//		File existMapFile = getSaveFile();
	//		try
	//		{
	//			if(existMapFile!=null)
	//			{
	//				if(existMapFile.exists())
	//				{
	//					processOpenMapFile(existMapFile);
	//				}
	//				else
	//				{//exist map file does not exist anymore
	//					JOptionPane.showMessageDialog(this, existMapFile.getAbsolutePath() + " does not exist or is not accessible anymore", "File Error", JOptionPane.ERROR_MESSAGE);
	//					return;
	//				}
	//			}
	//			else
	//			{//exist map file does not exist, simply reload source and/or target file
	//				Iterator it = changedFileMap.keySet().iterator();
	//				while(it.hasNext())
	//				{
	//					MappingFileSynchronizer.FILE_TYPE key = (MappingFileSynchronizer.FILE_TYPE) it.next();
	//					File file = changedFileMap.get(key);
	//					if(GeneralUtilities.areEqual(MappingFileSynchronizer.FILE_TYPE.Source_File, key))
	//					{
	//						processOpenSourceTree(file, true, true);
	//					}
	//					else if(GeneralUtilities.areEqual(MappingFileSynchronizer.FILE_TYPE.Target_File, key))
	//					{
	//						processOpenTargetTree(file, true, true);
	//					}
	//				}//end of while
	//			}//end of else
	//		}
	//		catch (Exception e)
	//		{
	//			DefaultSettings.reportThrowableToLogAndUI(this, e, "", this, false, false);
	//		}
	//	}



	/**
	 * @return the mapping
	 */
	public Mapping getMapping() {
		if(this.mapping == null){
			this.mapping = new Mapping();
			this.getMiddlePanel().getMiddlePanelJGraphController().setMappingData(mapping);
		}
		return mapping;
	}

	/**
	 * @param mapping the mapping to set
	 */
	public void setMapping(Mapping mapping) {
		this.mapping = mapping;
	}


	private void resetMiddlePanel()
	{
		if (middlePanel != null)
		{
			middlePanel.resetGraph();
			middlePanel.repaint();
		}
	}

	/**
	 * Return whether the mapping module is in drag-and-drop mode.
	 * @return whether the mapping module is in drag-and-drop mode.
	 */
	public boolean isInDragDropMode()
	{
		return dndHandler.getState()!=TreeTransferHandler.READY;
	}


	public JScrollPane getSourceScrollPane() {
		return sourceScrollPane;
	}

	public JScrollPane getTargetScrollPane() {
		return targetScrollPane;
	}

	public MappingMiddlePanel getMiddlePanel() {
		return middlePanel;
	}

	public JTree getSourceTree()
	{
		return sTree;
	}

	public JTree getTargetTree()
	{
		return tTree;
	}

	/**
	 * Provide the extended implementation of this method by adding additional files of source and target;
	 *
	 * @return a list of file objects that this context is associated with.
	 */
	public java.util.List<File> getAssociatedFileList()
	{
		List<File> resultList = new ArrayList<java.io.File>();
		if(saveFile!=null)
		{
			resultList.add(saveFile);
		}
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

	public void setSize(int width, int height)
	{
		double topCenterFactor = 1;
		sourceLocationArea.setSize(new Dimension((int) (width / 6), 25));
		sourceScrollPane.setSize(new Dimension((int) (width / 4.5), (int) (height * topCenterFactor)));
		targetLocationArea.setSize(new Dimension(width / 6, 25));
		targetScrollPane.setSize(new Dimension((int) (width / 4.5), (int) (height * topCenterFactor)));
		middlePanel.setSize(new Dimension((int) (width / 4), (int) (height * topCenterFactor)));

		topCenterFactor = 0.5;
		Dimension rightMostDim = new Dimension((int) (width / 5), (int) (height * topCenterFactor));
		//		propertiesPane.setSize(rightMostDim);
		//		functionPane.setSize(rightMostDim);
	}


	/**
	 * Explicitly set the value.
	 *
	 * @param newValue
	 */
	public void setChanged(boolean newValue)
	{
		middlePanel.getGraphController().setGraphChanged(newValue);
	}


	/**
	 * Return the save file.
	 * @return the save file.
	 */
	public File getSaveFile()
	{
		return saveFile;
	}

	/**
	 * Set a new save file.
	 *
	 * @param saveFile
	 * @return true if the value is changed, false otherwise.
	 */
	public boolean setSaveFile(File saveFile)
	{
		//removed the equal check so as to support explicit refresh or reload call.
		//		ContextManager contextManager = ContextManager.getContextManager();
		boolean sameFile = GeneralUtilities.areEqual(this.saveFile, saveFile);
		if(!sameFile)
		{//remove interest in the context file manager, first for old file

			//				contextManager.getContextFileManager().removeFileUsageListener(this);
		}
		this.saveFile = saveFile;
		//		if(!sameFile)
		//		{//register interest in the context file manager for new file
		//				contextManager.getContextFileManager().registerFileUsageListener(this);
		//		}
		updateTitle(this.saveFile.getName());
		//		getMappingFileSynchronizer().registerFile(MappingFileSynchronizer.FILE_TYPE.Mapping_File, saveFile);
		return true;
	}

	
	public void synchronizeRegisteredFile(boolean notigyOberver)
	{
		//do nothing, only the "MappingFilePanel" will implement it
	}
	/**
	 * Overridable function to update Title in the tabbed pane.
	 * @param newTitle
	 */
	private void updateTitle(String newTitle)
	{
		JRootPane rootPane = getRootPane();
		if (rootPane != null)
		{
			Container container = rootPane.getParent();
			if (container instanceof MainFrame)
			{
				((MainFrame)container).setCurrentPanelTitle(newTitle);
			}
		}
	}

	/**
	 * return the close action inherited with this client.
	 * @return the close action inherited with this client.
	 */
	public Action getDefaultCloseAction()
	{//by doing this way, the menu and the panel will use the same close action.
		Map actionMap = getMenuItems(MenuConstants.FILE_MENU_NAME);
		Action closeAction = (Action) actionMap.get(ActionConstants.CLOSE);
		return closeAction;
	}

	/**
	 * return the save action inherited with this client.
	 * @return the save action inherited with this client.
	 */
	public Action getDefaultSaveAction()
	{
		Map actionMap = getMenuItems(MenuConstants.FILE_MENU_NAME);
		Action saveAction = (Action) actionMap.get(ActionConstants.SAVE);
		return saveAction;
	}

	/**
	 * Return the top root container (frame or dialog or window) this panel is associated with.
	 * @return the top root container (frame or dialog or window) this panel is associated with.
	 */
	public Container getRootContainer()
	{
		return DefaultSettings.findRootContainer(this);
	}


	/**
	 * Return a list of Action objects that is included in this Context manager.
	 * @return a list of Action objects that is included in this Context manager.
	 */
	public java.util.List<Action> getToolbarActionList()
	{
		java.util.List<Action> actions = new ArrayList<Action>();
		actions.add(getDefaultOpenAction());
		//the menu bar display its buttons inorder
		Map <String, Action>actionMap = getMenuItems(MenuConstants.TOOLBAR_MENU_NAME);
		actions.add((Action) actionMap.get(ActionConstants.SAVE));
		actions.add((Action) actionMap.get(ActionConstants.VALIDATE));
		//add the "Refresh" menu if exist
		actions.add((Action) actionMap.get(ActionConstants.REFRESH));
		return actions;
	}

	public MiddlePanelJGraphController getMiddlePanelJGraphController() {
		return middlePanel.getGraphController();
	}

	public boolean isChanged() {
		// TODO Auto-generated method stub
		return false;
	}



}

/**
 * HISTORY: $Log: not supported by cvs2svn $
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

