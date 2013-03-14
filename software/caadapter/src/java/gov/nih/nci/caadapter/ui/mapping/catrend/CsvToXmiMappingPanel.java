/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.mapping.catrend;

import gov.nih.nci.caadapter.common.BaseResult;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.MetaParser;
import gov.nih.nci.caadapter.common.csv.CSVMetaParserImpl;
import gov.nih.nci.caadapter.common.csv.CSVMetaResult;
import gov.nih.nci.caadapter.common.map.BaseComponent;
import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.map.Mapping;
import gov.nih.nci.caadapter.hl7.map.impl.MapParserImpl;
import gov.nih.nci.caadapter.common.metadata.XmiModelMetadata;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.MappingFileSynchronizer;
import gov.nih.nci.caadapter.ui.common.actions.TreeCollapseAllAction;
import gov.nih.nci.caadapter.ui.common.actions.TreeExpandAllAction;
import gov.nih.nci.caadapter.ui.common.context.ContextManager;
import gov.nih.nci.caadapter.ui.common.context.MenuConstants;
import gov.nih.nci.caadapter.ui.common.nodeloader.SCMMapSourceNodeLoader;
import gov.nih.nci.caadapter.ui.common.tree.DefaultSourceTreeNode;
import gov.nih.nci.caadapter.ui.common.tree.DefaultTargetTreeNode;
import gov.nih.nci.caadapter.ui.common.tree.TreeDefaultDropTransferHandler;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.caadapter.ui.mapping.catrend.actions.CsvToXmiTargetTreeDropTransferHandler;
import gov.nih.nci.caadapter.ui.mapping.hl7.actions.RefreshMapAction;
import gov.nih.nci.caadapter.ui.mapping.mms.MMSRenderer;
import gov.nih.nci.caadapter.ui.mapping.mms.MMSRendererPK;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAttribute;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLModel;
import gov.nih.nci.ncicb.xmiinout.domain.UMLPackage;
import gov.nih.nci.ncicb.xmiinout.domain.UMLTaggedValue;

import gov.nih.nci.ncicb.xmiinout.handler.XmiInOutHandler;
import gov.nih.nci.ncicb.xmiinout.util.ModelUtil;


import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


/**
 * The class is the main panel to construct the UI and initialize the utilities
 * to facilitate mapping functions.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v3.2 revision $Revision: 1.16 $ date $Date:
 *          2007/04/03 16:17:57 $
 */
public class CsvToXmiMappingPanel extends AbstractMappingPanel {
	private static final String LOGID = "$RCSfile: CsvToXmiMappingPanel.java,v $";

	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/catrend/CsvToXmiMappingPanel.java,v 1.16 2008-09-29 20:27:57 wangeug Exp $";
	public static String MAPPING_TARGET_DATA_MODEL="CSV_TO_XMI_DATA_MODEL";
	public static String MAPPING_TARGET_OBJECT_MODEL="CSV_TO_XMI_OBJECT_MODEL";
    private CsvToXmiTargetTreeDropTransferHandler csvToXmiTargetTreeDropTransferHandler = null;
	private String mappingTarget=MAPPING_TARGET_OBJECT_MODEL;
    private static final String SELECT_XMI = "Open XMI File...";
    private static final String SELECT_CSV = "Open CSV Meta File...";
    private String sourceFileName;
    private String targetFileName;
    private XmiModelMetadata xmiModelMeta;
    private CsvToXmiMappingReportPanel reportPanel;
	public CsvToXmiMappingPanel() {
		this("CsvToXmiMapping");
	}

	public CsvToXmiMappingPanel(String name) {
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setLayout(new BorderLayout());
		this.add(getCenterPanel(false), BorderLayout.CENTER);
		fileSynchronizer = new MappingFileSynchronizer(this);
    }

	protected JPanel getTopLevelLeftPanel() {
		JPanel topCenterPanel = new JPanel(new BorderLayout());
		topCenterPanel.setBorder(BorderFactory.createEmptyBorder());
		JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		DefaultSettings.setDefaultFeatureForJSplitPane(centerSplitPane);

		// construct source panel
		sourceButtonPanel = new JPanel(new BorderLayout());
		sourceButtonPanel.setBorder(BorderFactory.createEmptyBorder());
		sourceLocationPanel = new JPanel(new BorderLayout(2, 0));
		sourceLocationPanel.setBorder(BorderFactory.createEmptyBorder());
		sourceTreeCollapseAllAction = new TreeCollapseAllAction(sTree);
		sourceTreeExpandAllAction = new TreeExpandAllAction(sTree);

		JToolBar sourceTreeToolBar = new JToolBar("Source Tree Tool Bar");
		sourceTreeToolBar.setFloatable(false);
		sourceTreeToolBar.add(sourceTreeExpandAllAction);
		sourceTreeToolBar.add(sourceTreeCollapseAllAction);
		sourceLocationPanel.add(sourceTreeToolBar, BorderLayout.WEST);
		sourceLocationArea.setEditable(false);
		sourceLocationArea.setPreferredSize(new Dimension(
				(int) (Config.FRAME_DEFAULT_WIDTH / 10), 24));
		sourceLocationPanel.add(sourceLocationArea, BorderLayout.CENTER);

		JButton openCsvButton = new JButton(SELECT_CSV);
		openCsvButton.setMnemonic('C');
		openCsvButton.setToolTipText("Select Csv Meta file...");
		openCsvButton.addActionListener(this);
		sourceLocationPanel.add(openCsvButton, BorderLayout.EAST);

		sourceButtonPanel.add(sourceLocationPanel, BorderLayout.NORTH);
		// sourceScrollPane =
		sourceScrollPane.setPreferredSize(new Dimension(
				(int) (Config.FRAME_DEFAULT_WIDTH / 4),
				(int) (Config.FRAME_DEFAULT_HEIGHT / 1.5)));
		sourceScrollPane.setSize(new Dimension(
				(int) (Config.FRAME_DEFAULT_WIDTH / 3),
				(int) (Config.FRAME_DEFAULT_HEIGHT / 1.5)));
		sourceButtonPanel.add(sourceScrollPane, BorderLayout.CENTER);

		// construct target panel
		targetButtonPanel = new JPanel(new BorderLayout());
		targetButtonPanel.setBorder(BorderFactory.createEmptyBorder());
		targetLocationPanel = new JPanel(new BorderLayout(2, 0));
		targetLocationPanel.setBorder(BorderFactory.createEmptyBorder());
		targetTreeCollapseAllAction = new TreeCollapseAllAction(tTree);
		targetTreeExpandAllAction = new TreeExpandAllAction(tTree);
		JToolBar targetTreeToolBar = new JToolBar("Target Tree Tool Bar");
		targetTreeToolBar.setFloatable(false);
		targetTreeToolBar.add(targetTreeExpandAllAction);
		targetTreeToolBar.add(targetTreeCollapseAllAction);
		targetLocationPanel.add(targetTreeToolBar, BorderLayout.WEST);

		JButton openXmiButton = new JButton(SELECT_XMI);
		openXmiButton.setMnemonic('X');
		openXmiButton.setToolTipText("Select XMI file...");
		openXmiButton.addActionListener(this);
		targetLocationPanel.add(openXmiButton, BorderLayout.EAST);

		targetLocationArea.setEditable(false);
		targetLocationArea.setPreferredSize(new Dimension(
				(int) (Config.FRAME_DEFAULT_WIDTH / 10), 24));
		targetLocationPanel.add(targetLocationArea, BorderLayout.CENTER);
		targetButtonPanel.add(targetLocationPanel, BorderLayout.NORTH);
		targetScrollPane.setPreferredSize(new Dimension(
				(int) (Config.FRAME_DEFAULT_WIDTH / 3),
				Config.FRAME_DEFAULT_HEIGHT / 2));
		targetButtonPanel.add(targetScrollPane, BorderLayout.CENTER);
		targetButtonPanel.setPreferredSize(new Dimension(
				(int) (Config.FRAME_DEFAULT_WIDTH / 5),
				(int) (Config.FRAME_DEFAULT_HEIGHT / 1.5)));

		// construct middle panel
		JPanel centerFuncationPanel = new JPanel(new BorderLayout(2, 0));
		JPanel middleContainerPanel = new JPanel(new BorderLayout());
		// to hold the place equates the source and target button panel so as to
		// align the drawing the graphs.
		JLabel placeHolderLabel = new JLabel();
		placeHolderLabel.setPreferredSize(new Dimension(
				(int) (Config.FRAME_DEFAULT_WIDTH / 16), 24));
		middlePanel = new MappingMiddlePanel(this);
		middlePanel.setKind("CsvToXmi");
		middlePanel.setSize(new Dimension(
				(int) (Config.FRAME_DEFAULT_WIDTH / 3),
				(int) (Config.FRAME_DEFAULT_HEIGHT / 1.5)));
		centerFuncationPanel.add(placeHolderLabel, BorderLayout.EAST);
		centerFuncationPanel.setPreferredSize(new Dimension(
				(int) (Config.FRAME_DEFAULT_WIDTH / 3.5), 24));
		middleContainerPanel.add(centerFuncationPanel, BorderLayout.NORTH);
		middleContainerPanel.add(middlePanel, BorderLayout.CENTER);

		JSplitPane rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		DefaultSettings.setDefaultFeatureForJSplitPane(rightSplitPane);
		rightSplitPane.setDividerLocation(0.5);
		rightSplitPane.setLeftComponent(middleContainerPanel);
		rightSplitPane.setRightComponent(targetButtonPanel);

		centerSplitPane.setLeftComponent(sourceButtonPanel);
		centerSplitPane.setRightComponent(rightSplitPane);

		topCenterPanel.add(centerSplitPane, BorderLayout.CENTER);
		topCenterPanel.setPreferredSize(new Dimension(
				(int) (Config.FRAME_DEFAULT_WIDTH * 0.8),
				(int) (Config.FRAME_DEFAULT_HEIGHT / 1.5)));
		return topCenterPanel;
	}

	protected TreeNode loadSourceTreeData(Object metaInfo, File file)
			throws Exception {
		TreeNode node;
		SCMMapSourceNodeLoader scmMapSourceNodeLoader = new SCMMapSourceNodeLoader();
		node = scmMapSourceNodeLoader.loadData(metaInfo);
		return node;
	}

	protected TreeNode loadTargetTreeData(Object metaInfo, File absoluteFile)
			throws Exception {
		String targetNodeName="";
		if (getMappingTarget().equalsIgnoreCase(MAPPING_TARGET_OBJECT_MODEL))
			targetNodeName="Object Model";

		else if(getMappingTarget().equalsIgnoreCase(MAPPING_TARGET_DATA_MODEL))
			targetNodeName="Data Model";
		else
			throw new Exception("Failed in building XMI target tree: Target type is not defined");
		TreeNode node = new DefaultMutableTreeNode(targetNodeName);
		if (this.getXmiModelMeta()==null)
			setXmiModelMeta(new XmiModelMetadata(""));
		//load xmiModel data
		getXmiModelMeta().setXmiFileName(absoluteFile.getAbsolutePath());
		LinkedHashMap myMap = getXmiModelMeta().getModelMetadata();

		Set keySet = myMap.keySet();
		Iterator keySetIterator = keySet.iterator();
		while (keySetIterator.hasNext()) {
			String key = (String) keySetIterator.next();
			if (getMappingTarget().equalsIgnoreCase(MAPPING_TARGET_OBJECT_MODEL))
			{
				if (key.contains( CaadapterUtil.readPrefParams( Config.MMS_PREFIX_OBJECTMODEL ) + ".") )
					constructXmiNTreeNde(node, key, (CaadapterUtil.readPrefParams( Config.MMS_PREFIX_OBJECTMODEL ) + ".").length(), false);

			}
			else if(getMappingTarget().equalsIgnoreCase(MAPPING_TARGET_DATA_MODEL))
			{
				if (key.contains( CaadapterUtil.readPrefParams( Config.MMS_PREFIX_DATAMODEL ) + "."))
					constructXmiNTreeNde(node, key, ( CaadapterUtil.readPrefParams( Config.MMS_PREFIX_DATAMODEL ) + ".").length(),  false);
			}
		}
		return node;
	}

	protected void buildTargetTree(Object metaInfo, File absoluteFile,
			boolean isToResetGraph) throws Exception {
		super.buildTargetTree(metaInfo, absoluteFile, isToResetGraph);
		if (getMappingTarget().equalsIgnoreCase(MAPPING_TARGET_OBJECT_MODEL))
		{
			MMSRendererPK objectModelRenderer=new MMSRendererPK();
			objectModelRenderer.setXmiMeta(getXmiModelMeta());
			tTree.setCellRenderer(objectModelRenderer);
		}
		else if(getMappingTarget().equalsIgnoreCase(MAPPING_TARGET_DATA_MODEL))
		{
			MMSRenderer renderer=new MMSRenderer();
			renderer.setXmiMeta(getXmiModelMeta());
			tTree.setCellRenderer(renderer);
		}
        // instantiate the "DropTransferHandler"
		csvToXmiTargetTreeDropTransferHandler = new CsvToXmiTargetTreeDropTransferHandler(
				tTree, getMappingDataManager(), DnDConstants.ACTION_LINK);
	}

	protected TreeDefaultDropTransferHandler getTargetTreeDropTransferHandler() {
		return csvToXmiTargetTreeDropTransferHandler;
	}

	private void constructXmiNTreeNde(TreeNode node, String fullName, int prefixLen, boolean isSourceNode)
	{
		String name = fullName.substring(prefixLen, fullName.length());
		String[] pks = name.split("\\.");
		LinkedHashMap myMap =getXmiModelMeta().getModelMetadata();
		if (pks.length <= 0)
			return;
		if (pks.length == 1) {
			if (isSourceNode)
				((DefaultMutableTreeNode) node).add(new DefaultSourceTreeNode(
						myMap.get(fullName), true));
			else
				((DefaultMutableTreeNode) node).add(new DefaultTargetTreeNode(
						myMap.get(fullName), true));
			return;
		}
		DefaultMutableTreeNode father = (DefaultMutableTreeNode) node;
		for (int i = 0; i < pks.length - 1; i++) {
			boolean exist = false;
			Enumeration children = father.children();
			while (children.hasMoreElements()) {
				DefaultMutableTreeNode current = (DefaultMutableTreeNode) children
						.nextElement();

				if (current.toString().equals(pks[i])) {
					exist = true;
					father = current;
					break;
				}
			}

			if (!exist) {
				DefaultMutableTreeNode newTreeNode = new DefaultMutableTreeNode(
						pks[i], true);
				father.add(newTreeNode);
				father = newTreeNode;
			}
		}
		DefaultMutableTreeNode newTreeNode;
		if (isSourceNode)
				newTreeNode = new DefaultSourceTreeNode(myMap.get(fullName),true);
		else
			newTreeNode = new DefaultTargetTreeNode(myMap.get(fullName), true);

	    father.add(newTreeNode);
		return;
	}

	/**
	 * Called by actionPerformed() and overridable by descendant classes.
	 *
	 * @param file
	 * @throws Exception
	 */
	private boolean processOpenSourceTree(File file, boolean isToResetGraph,
			boolean supressReportIssuesToUI) throws Exception {
		this.setSourceFileName(file.getAbsolutePath());
		BaseResult returnResult = null;

		// parse the file into a meta object graph.
		MetaParser parser = new CSVMetaParserImpl();
		returnResult = parser.parse(new FileReader(file));
		ValidatorResults validatorResults = returnResult.getValidatorResults();
		if (validatorResults != null && validatorResults.hasFatal())
		{
			Message msg = validatorResults.getMessages(ValidatorResult.Level.FATAL).get(0);
			DefaultSettings.reportThrowableToLogAndUI(this, null, msg.toString(), this, true, supressReportIssuesToUI);
			return false;
		}
		//default to Config.HSM_META_DEFINITION_FILE_DEFAULT_EXTENSION
		MetaObject metaInfo = ((CSVMetaResult) returnResult).getCsvMeta();

		buildSourceTree(metaInfo, file, isToResetGraph);
		middlePanel.getMappingDataManager().registerSourceComponent(metaInfo, file);
		return true;
	}

	/**
	 * Called by actionPerformed() and overridable by descendant classes.
	 *
	 * @param file
	 * @throws Exception
	 */
	private boolean processOpenTargetTree(File file, boolean isToResetGraph,
			boolean supressReportIssuesToUI) throws Exception {
		this.setTargetFileName(file.getAbsolutePath());
		if( CaadapterUtil.readPrefParams( Config.MMS_PREFIX_OBJECTMODEL ) == null )
		{
			CaadapterUtil.savePrefParams( Config.MMS_PREFIX_OBJECTMODEL , "Logical View.Logical Model");
		}
		if( CaadapterUtil.readPrefParams( Config.MMS_PREFIX_DATAMODEL ) == null )
		{
			CaadapterUtil.savePrefParams( Config.MMS_PREFIX_DATAMODEL , "Logical View.Data Model");
		}
		// parse the file into a meta object graph.
        MetaObject metaInfo = null;
		buildTargetTree(metaInfo, file, isToResetGraph);
		middlePanel.getMappingDataManager().registerTargetComponent(metaInfo,
				file);
		return true;
	}

	/**
	 * Called by actionPerformed() and overridable by descendant classes.
	 *
	 * @param file
	 * @throws Exception
	 *             changed from protected to pulic by sean
	 */
	public ValidatorResults processOpenMapFile(File file) throws Exception
	{
		// parse the file.
		MapParserImpl parser = new MapParserImpl();
		ValidatorResults validatorResults = parser.parse(file.getParent(), new FileReader(file));
		if (validatorResults != null && validatorResults.hasFatal())
		{//immediately return if it has fatal errors.
			return validatorResults;
		}
		Mapping mapping = parser.getMapping();//returnResult.getMapping();
		this.setMappingTarget(mapping.getMappingType());
		//build source tree
		BaseComponent sourceComp = mapping.getSourceComponent();
		File sourceFile = sourceComp.getFile();
		boolean srcTreeBuild=processOpenSourceTree(sourceFile, true, true);
		//build target tree
		BaseComponent targetComp = mapping.getTargetComponent();
		File targetFile = targetComp.getFile();
		boolean targetTreeBuild=processOpenTargetTree(targetFile, true, true);
		middlePanel.getMappingDataManager().setMappingData(mapping);
		setSaveFile(file);
		return validatorResults;
	}

    public ValidatorResults cvsToXmiGeneration(String mappingFile) throws Exception
    {
        File file = new File(mappingFile);
        //init() - read parameters from mappingFile
        MapParserImpl parser = new MapParserImpl();
        ValidatorResults validatorResults = parser.parse(file.getParent(), new FileReader(file));

        if (validatorResults != null && validatorResults.hasFatal())
        {//immediately return if it has fatal errors.
            return validatorResults;
        }

        Mapping mapping = parser.getMapping();//returnResult.getMapping();
        String  xmiFile = mapping.getTargetComponent().getFile().getAbsolutePath();
        System.out.println("CsvToXmiMappingPanel.cvsToXmiGeneration()..mapping target xmi file:"+xmiFile);
        XmiInOutHandler handler=getXmiModelMeta().getHandler();
        UMLModel model=handler.getModel();
        //clear out the xmi file so duplicate tagvalues are not added on multiple saves
        for( UMLPackage pkg : model.getPackages() )
        {
			clearXMLNamespacePackages( pkg );
		}

        //for each map in mapList to the following
        for (gov.nih.nci.caadapter.hl7.map.Map map : mapping.getMaps() )
        {
            UMLAttribute column = null;
            column = ModelUtil.findAttribute( model,  map.getTargetMapElement().getDataXmlPath());//.getXPath());//.getXmlPath() );
            System.out.println("Annotate Attribute :"+map.getTargetMapElement().getDataXmlPath() +"..."+map.getTargetMapElement().getXmlPath() + "XMLNamespace.. "+ map.getSourceMapElement().getXmlPath());
            if ( column != null ) {
                System.out.println("Annotate Attribute :"+map.getTargetMapElement().getXmlPath() + "XMLNamespace.. "+ map.getSourceMapElement().getXmlPath());
                column.addTaggedValue( "XMLNamespace", map.getSourceMapElement().getXmlPath() );
            }
        }

        //write xmi file
        handler.save(xmiFile);

        setSaveFile(file);
		//redraw mapping panel
		middlePanel.getMappingDataManager().setMappingData(mapping);
        return validatorResults;
    }

    private void clearXMLNamespacePackages( UMLPackage pkg )
    {
        for ( UMLClass clazz : pkg.getClasses() )
        {
            for( UMLAttribute att : clazz.getAttributes() )
            {
                for( UMLTaggedValue tagValue : att.getTaggedValues() )
                {
                    if( tagValue.getName().contains( "XMLNamespace" ))
                    {
                        System.out.println("Clearing Tagged Values");
                        att.removeTaggedValue( "XMLNamespace" );
                    }
                }
            }
        }

        for ( UMLPackage pkg2 : pkg.getPackages() )
        {
            clearXMLNamespacePackages( pkg2 );
        }
    }

    /**
	 * Over ridden method to return menuItem for menuBar and toolBar
	 */
	public Map getMenuItems(String menu_name) {
		Action action = null;
		ContextManager contextManager = ContextManager.getContextManager();
		Map<String, Action> actionMap = contextManager.getClientMenuActions(
				MenuConstants.CSV_TO_XMI, menu_name);
		if (MenuConstants.FILE_MENU_NAME.equals(menu_name)) {
			JRootPane rootPane = this.getRootPane();
			if (rootPane != null) {
				contextManager.enableAction(ActionConstants.NEW_CSV2XMI_MAP_FILE,
						false);
				contextManager.enableAction(ActionConstants.OPEN_CSV2XMI_MAP_FILE,
						true);
			}
		}
		// since the action depends on the panel instance,
		// the old action instance should be removed
		if (actionMap != null)
			contextManager.removeClientMenuAction(MenuConstants.CSV_SPEC,
					menu_name, "");

        action = new gov.nih.nci.caadapter.ui.mapping.catrend.actions.SaveCsvToXmiMapAction(
				this);
		contextManager.addClientMenuAction(MenuConstants.CSV_TO_XMI,
				MenuConstants.FILE_MENU_NAME, ActionConstants.SAVE, action);
		contextManager.addClientMenuAction(MenuConstants.CSV_TO_XMI,
				MenuConstants.TOOLBAR_MENU_NAME, ActionConstants.SAVE, action);
		action.setEnabled(true);

		action = new gov.nih.nci.caadapter.ui.mapping.catrend.actions.SaveAsCsvToXmiMapAction(
				this);
		contextManager.addClientMenuAction(MenuConstants.CSV_TO_XMI,
				MenuConstants.FILE_MENU_NAME, ActionConstants.SAVE_AS, action);
		contextManager.addClientMenuAction(MenuConstants.CSV_TO_XMI,
				MenuConstants.TOOLBAR_MENU_NAME, ActionConstants.SAVE_AS,
				action);
		action.setEnabled(true);

		action = new gov.nih.nci.caadapter.ui.mapping.mms.actions.AnotateAction(
				this);
		contextManager.addClientMenuAction(MenuConstants.CSV_TO_XMI,
				MenuConstants.FILE_MENU_NAME, ActionConstants.ANOTATE, action);
		action.setEnabled(false);

		action = new gov.nih.nci.caadapter.ui.mapping.catrend.actions.ValidateCsvToXmiMapAction(
				this);
		contextManager.addClientMenuAction(MenuConstants.CSV_TO_XMI,
				MenuConstants.FILE_MENU_NAME, ActionConstants.VALIDATE, action);
		contextManager.addClientMenuAction(MenuConstants.CSV_TO_XMI,
				MenuConstants.TOOLBAR_MENU_NAME, ActionConstants.VALIDATE,
				action);
		action.setEnabled(true);

		action = new gov.nih.nci.caadapter.ui.mapping.catrend.actions.CloseCsvToXmiMapAction(this);
		contextManager.addClientMenuAction(MenuConstants.CSV_TO_XMI,
				MenuConstants.FILE_MENU_NAME, ActionConstants.CLOSE, action);
		action.setEnabled(true);

		action = new gov.nih.nci.caadapter.ui.mapping.mms.actions.ValidateObjectToDbMapAction(
				this);
		contextManager.addClientMenuAction(MenuConstants.CSV_TO_XMI,
				MenuConstants.REPORT_MENU_NAME,
				ActionConstants.GENERATE_REPORT, action);
		contextManager.addClientMenuAction(MenuConstants.CSV_TO_XMI,
				MenuConstants.TOOLBAR_MENU_NAME,
				ActionConstants.GENERATE_REPORT, action);
		action.setEnabled(true);

		action = new RefreshMapAction(this);
		contextManager.addClientMenuAction(MenuConstants.CSV_TO_XMI,
				MenuConstants.TOOLBAR_MENU_NAME, ActionConstants.REFRESH,
				action);
		action.setEnabled(false);

		actionMap = contextManager.getClientMenuActions(
				MenuConstants.CSV_TO_XMI, menu_name);
		return actionMap;
	}

	/**
	 * return the open action inherited with this client.
	 */
	public Action getDefaultOpenAction() {
		ContextManager contextManager = ContextManager.getContextManager();
		return contextManager.getDefinedAction(ActionConstants.OPEN_CSV2XMI_MAP_FILE);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		try {
			boolean everythingGood = true;
			if (SELECT_XMI.equals(command)) {
				File file = DefaultSettings.getUserInputOfFileFromGUI(this, // FileUtil.getUIWorkingDirectoryPath(),
						".xmi", "Open XMI file ...", false, false);
				if (file != null)
				{
						processOpenTargetTree(file, true, true);
						//redraw mapping panel
						Mapping currentMapping= middlePanel.getMappingDataManager().retrieveMappingData(false);
						middlePanel.getMappingDataManager().setMappingData(currentMapping);
				}
			}
			else if (SELECT_CSV.equals(command)) {
				File file = DefaultSettings.getUserInputOfFileFromGUI(this, // FileUtil.getUIWorkingDirectoryPath(),
						".scs", "Open CSV meta file ...", false, false);

				if (file != null)
				{
					processOpenSourceTree(file, true, true);
					Mapping currentMapping= middlePanel.getMappingDataManager().retrieveMappingData(false);
					middlePanel.getMappingDataManager().setMappingData(currentMapping);
				}
			}

			if (!everythingGood) {
				Message msg = MessageResources
						.getMessage("GEN3", new Object[0]);
				JOptionPane.showMessageDialog(this, msg.toString(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e1) {
			DefaultSettings.reportThrowableToLogAndUI(this, e1, "", this,
					false, false);
		}
	}


	/**
	 * Explicitly reload information from the internal given file.
	 *
	 * @throws Exception
	 */
	public void reload() throws Exception {
		if( getSaveFile() != null)
            processOpenMapFile(getSaveFile());
	}

	/**
	 * Reload the file specified in the parameter.
	 *
	 * @param changedFileMap
	 */
	public void reload(	Map<MappingFileSynchronizer.FILE_TYPE, File> changedFileMap) {
		/**
		 * Design rationale: 1) if the changedFileMap is null, simply return; 2)
		 * if the getSaveFile() method does not return null, it implies current
		 * panel associates with a mapping file, just reload the whole mapping
		 * file so as to refresh those mapping relationship; 3) if the
		 * getSaveFile() returns null, just reload source and/or target file
		 * within the changedFileMap, and ignore the checking of
		 * MappingFileSynchronizer.FILE_TYPE.Mapping_File item in the map;
		 */
		if (changedFileMap == null) {
			return;
		}
		File existMapFile = getSaveFile();
		try {
			if (existMapFile != null) {
				if (existMapFile.exists()) {
					processOpenMapFile(existMapFile);
				} else {// exist map file does not exist anymore
					JOptionPane.showMessageDialog(this, existMapFile
							.getAbsolutePath()
							+ " does not exist or is not accessible anymore",
							"File Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			} else {// exist map file does not exist, simply reload source
					// and/or target file
				Iterator it = changedFileMap.keySet().iterator();
				while (it.hasNext()) {
					MappingFileSynchronizer.FILE_TYPE key = (MappingFileSynchronizer.FILE_TYPE) it
							.next();
					File file = changedFileMap.get(key);
					if (GeneralUtilities.areEqual(MappingFileSynchronizer.FILE_TYPE.Source_File, key)) {
						processOpenSourceTree(file, true, true);
					} else if (GeneralUtilities.areEqual(MappingFileSynchronizer.FILE_TYPE.Target_File, key)) {
						processOpenTargetTree(file, true, true);
					}
				}// end of while
			}// end of else
		} catch (Exception e) {
			DefaultSettings.reportThrowableToLogAndUI(this, e, "", this, false,
					false);
		}
	}


	public String getMappingTarget() {
		return mappingTarget;
	}

	public void setMappingTarget(String mappingTarget) {
		this.mappingTarget = mappingTarget;
	}

	public CsvToXmiMappingReportPanel getReportPanel() {
		return reportPanel;
	}

	public void setReportPanel(CsvToXmiMappingReportPanel reportPanel) {
		this.reportPanel = reportPanel;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

	public String getTargetFileName() {
		return targetFileName;
	}

	public void setTargetFileName(String targetFileName) {
		this.targetFileName = targetFileName;
	}

	public XmiModelMetadata getXmiModelMeta() {
		return xmiModelMeta;
	}

	public void setXmiModelMeta(XmiModelMetadata xmiModelMeta) {
		this.xmiModelMeta = xmiModelMeta;
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
**/