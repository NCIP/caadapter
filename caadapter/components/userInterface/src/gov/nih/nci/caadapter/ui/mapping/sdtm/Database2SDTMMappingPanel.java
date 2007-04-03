package gov.nih.nci.caadapter.ui.mapping.sdtm;

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.BaseResult;
import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.MetaParser;
import gov.nih.nci.caadapter.common.csv.CSVMetaParserImpl;
import gov.nih.nci.caadapter.common.csv.CSVMetaResult;
import gov.nih.nci.caadapter.ui.common.context.ContextManager;
import gov.nih.nci.caadapter.ui.common.context.MenuConstants;
import gov.nih.nci.caadapter.ui.common.tree.MappingTargetTree;
import gov.nih.nci.caadapter.ui.common.tree.TreeDefaultDropTransferHandler;
import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
import gov.nih.nci.caadapter.ui.common.nodeloader.DBMMapTargetNodeLoader;
import gov.nih.nci.caadapter.ui.common.nodeloader.HSMMapTargetNodeLoader;
import gov.nih.nci.caadapter.ui.common.nodeloader.SCMMapSourceNodeLoader;
import gov.nih.nci.caadapter.sdtm.*;

import gov.nih.nci.caadapter.ui.common.actions.TreeCollapseAllAction;
import gov.nih.nci.caadapter.ui.common.actions.TreeExpandAllAction;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.MappingFileSynchronizer;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.caadapter.ui.mapping.mms.actions.MmsTargetTreeDropTransferHandler;
import gov.nih.nci.caadapter.ui.mapping.sdtm.actions.SdtmDropTransferHandler;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.ui.common.tree.DefaultTargetTreeNode;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * The class is the main panel to construct the UI and initialize the utilities
 * to facilitate mapping functions.
 * 
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v3.2 revision $Revision: 1.1 $
 */
public class Database2SDTMMappingPanel extends AbstractMappingPanel
{
    private static final String LOGID = "$RCSfile: Database2SDTMMappingPanel.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/sdtm/Database2SDTMMappingPanel.java,v 1.1 2007-04-03 16:17:57 wangeug Exp $";
    private static final String SELECT_SCS = "Open SCS file...";
    private static final String SELECT_TARGET = "Open SDTM definition file...";
    private SdtmDropTransferHandler sdtmTargetTreeDropTransferHandler = null;
    static ArrayList<String> _retArray = null;
    private String defineXMLLocation = null;
    private SDTMMappingGenerator sdtmMappingGenerator = null; //new SDTMMappingGenerator();

    public Database2SDTMMappingPanel(String name) 
    {
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setLayout(new BorderLayout());
		this.add(getCenterPanel(false), BorderLayout.CENTER);
		fileSynchronizer = new MappingFileSynchronizer(this);
    }

    public SDTMMappingGenerator getSDTMappingGenerator() {
    	return sdtmMappingGenerator;
    }
 
    protected JPanel getTopLevelLeftPanel() 
    {
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
		sourceLocationArea.setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 10), 24));
		sourceLocationPanel.add(sourceLocationArea, BorderLayout.CENTER);
		JButton openSCSButton = new JButton(SELECT_SCS);
	    sourceLocationPanel.add(openSCSButton, BorderLayout.EAST);
		openSCSButton.setMnemonic('O');
		openSCSButton.setToolTipText("Select SCS file...");
		openSCSButton.addActionListener(this);
		sourceButtonPanel.add(sourceLocationPanel, BorderLayout.NORTH);
//		sourceScrollPane = DefaultSettings.createScrollPaneWithDefaultFeatures();
		sourceScrollPane.setSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 4), (int) (Config.FRAME_DEFAULT_HEIGHT / 1.5)));
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
		targetLocationArea.setEditable(false);
		targetLocationArea.setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 10), 24));
		targetLocationPanel.add(targetLocationArea, BorderLayout.CENTER);
		JButton openTargetButton = new JButton(SELECT_TARGET);
		targetLocationPanel.add(openTargetButton, BorderLayout.EAST);
		openTargetButton.setMnemonic('T');
		openTargetButton.setToolTipText("Open SDTM Structure file");
		openTargetButton.addActionListener(this);
		targetButtonPanel.add(targetLocationPanel, BorderLayout.NORTH);
//		targetScrollPane = DefaultSettings.createScrollPaneWithDefaultFeatures();
		targetButtonPanel.add(targetScrollPane, BorderLayout.CENTER);
		targetButtonPanel.setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 5), (int) (Config.FRAME_DEFAULT_HEIGHT / 1.5)));
		
		// construct middle panel
		JPanel middleContainerPanel = new JPanel(new BorderLayout());
		JLabel placeHolderLabel = new JLabel();
		placeHolderLabel.setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 3.5), 24));
		middlePanel = new MappingMiddlePanel(this);
		middlePanel.setKind("SDTM");
		sdtmMappingGenerator = new SDTMMappingGenerator();
		sdtmMappingGenerator.set_sdtmMappingGeneratorReference(sdtmMappingGenerator);
		
		middlePanel.setSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 3), (int) (Config.FRAME_DEFAULT_HEIGHT / 1.5)));
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
		topCenterPanel.setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH * 0.8), (int) (Config.FRAME_DEFAULT_HEIGHT / 1.5)));
		
		return topCenterPanel;
    }  

    public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		try {
		    boolean everythingGood = true;
		    if (SELECT_SCS.equals(command)) {
			File file = DefaultSettings.getUserInputOfFileFromGUI(this, ".scs", "Open SCS file ...", false, false);
			if (file != null) {
			    everythingGood = processOpenSourceTree(file, true, true);
			}
		    }
		    if (SELECT_TARGET.equals(command)) {
			File file = DefaultSettings.getUserInputOfFileFromGUI(this, ".xml", "Open SDTM Structure file ...", false, false);
			if (file != null) {
			    // everythingGood = processOpenSourceTree(file, true,
	                        // true);
			    buildTargetTree(null, file, true);
			}
		    }
		    if (!everythingGood) {
			Message msg = MessageResources.getMessage("GEN3", new Object[0]);
			JOptionPane.showMessageDialog(this, msg.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		    }
		} catch (Exception e1) {
		    DefaultSettings.reportThrowableToLogAndUI(this, e1, "", this, false, false);
		}
    }
    
    protected TreeNode loadSourceTreeData( Object metaInfo, File absoluteFile)throws Exception
    {
    	TreeNode node = new DefaultMutableTreeNode("Object Model");
    	// build source tree end
    	SCMMapSourceNodeLoader scmMapSourceNodeLoader = new SCMMapSourceNodeLoader();
    	node = scmMapSourceNodeLoader.loadData(metaInfo);
    	return node;
    }
  
    protected void buildSourceTree(Object metaInfo, File file, boolean isToResetGraph) throws Exception 
    {
    	super.buildSourceTree(metaInfo, file, isToResetGraph);
		sTree.addTreeSelectionListener(new TreeSelectionListener() {
		    public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) sTree.getLastSelectedPathComponent();
				if (node == null)
				    return;
		    }
		});
	}

    @SuppressWarnings("unchecked")
    public static void createTargetNodes(TreeNode nodes, File SDTMXmlFile) {
		ParseSDTMXMLFile _parseSDTMFile = new ParseSDTMXMLFile(SDTMXmlFile.getAbsolutePath().toString());
		_retArray = _parseSDTMFile.getSDTMStructure();
		DefaultMutableTreeNode pNode = null;
		for (int k = 0; k < _retArray.size(); k++) {
		    if (_retArray.get(k).startsWith("KEY")) {
			EmptyStringTokenizer _str = new EmptyStringTokenizer(_retArray.get(k), ",");
			pNode = new DefaultMutableTreeNode(_str.getTokenAt(1).substring(0, 2));
			((DefaultMutableTreeNode) nodes).add(pNode);
		    } else {
			EmptyStringTokenizer _str = new EmptyStringTokenizer(_retArray.get(k), ",");
			pNode.add(new DefaultTargetTreeNode(new SDTMMetadata(_str.getTokenAt(1), _str.getTokenAt(2), _str.getTokenAt(3), _str.getTokenAt(4))));
		    }
		}
    }
    
    protected TreeNode loadTargetTreeData( Object metaInfo, File file)throws Exception
	{
		String fileName = file.getAbsolutePath();
		sdtmMappingGenerator.setScsDefineXMLFIle(fileName);
		TreeNode targetNodes = new DefaultMutableTreeNode("SDTM Data Structure");	
		setDefineXMLLocation(fileName);
		createTargetNodes(targetNodes, file);
		return targetNodes;
	}
    
    protected void buildTargetTree(Object metaInfo, File absoluteFile, boolean isToResetGraph) throws Exception
	{	
		super.buildTargetTree(metaInfo, absoluteFile, isToResetGraph);
		//instantiate the "DropTransferHandler"
		sdtmTargetTreeDropTransferHandler = new SdtmDropTransferHandler(tTree, getMappingDataManager(), DnDConstants.ACTION_LINK, sdtmMappingGenerator);
	}

    /**
         * Called by actionPerformed() and overridable by descendant classes.
         * 
         * @param file
         * @throws Exception
         */
    protected boolean processOpenSourceTree(File file, boolean isToResetGraph, boolean supressReportIssuesToUI) throws Exception {
		MetaObject metaInfo = null;
		BaseResult returnResult = null;
		MetaParser parser = null;
		parser = new CSVMetaParserImpl();
		returnResult = parser.parse(new FileReader(file));
		ValidatorResults validatorResults = returnResult.getValidatorResults();
		if (validatorResults != null && validatorResults.hasFatal()) {
		    Message msg = validatorResults.getMessages(ValidatorResult.Level.FATAL).get(0);
		    DefaultSettings.reportThrowableToLogAndUI(this, null, msg.toString(), this, true, supressReportIssuesToUI);
		    return false;
		}
		metaInfo = ((CSVMetaResult) returnResult).getCsvMeta();
		((CSVMetaResult) returnResult).getCsvMeta().getRootSegment();
		sdtmMappingGenerator.setScsSDTMFile(file.getAbsolutePath());
		buildSourceTree(metaInfo, file, isToResetGraph);
		return true;
    }

    /**
         * Called by actionPerformed() and overridable by descendant classes.
         * 
         * @param file
         * @throws Exception
         */
    protected boolean processOpenTargetTree(File file, boolean isToResetGraph, boolean supressReportIssuesToUI) throws Exception {
    	buildTargetTree(null, file, isToResetGraph);
	return true;
    }

        
    /**
         * Called by actionPerformed() and overridable by descendant classes.
         * 
         * @param file
         * @throws Exception
         *                 changed from protected to pulic by sean
         */
    public ValidatorResults processOpenMapFile(File file) throws Exception {
    	new OpenSDTMMapFile(this, file.toString());
    	return null;
    }

	public Map getMenuItems(String menu_name)
	{
		Action action = null;
		ContextManager contextManager = ContextManager.getContextManager();
		Map <String, Action>actionMap = contextManager.getClientMenuActions(MenuConstants.DB_TO_SDTM, menu_name);
		if (MenuConstants.FILE_MENU_NAME.equals(menu_name))
		{
			JRootPane rootPane = this.getRootPane();
			if (rootPane != null)
			{//rootpane is not null implies this panel is fully displayed;
				//on the flip side, if it is null, it implies it is under certain construction.
				contextManager.enableAction(ActionConstants.NEW_O2DB_MAP_FILE, false);
				//contextManager.enableAction(ActionConstants.OPEN_MAP_FILE, true);
			}
		}
		//since the action depends on the panel instance,
		//the old action instance should be removed
		if (actionMap!=null)
			contextManager.removeClientMenuAction(MenuConstants.CSV_SPEC, menu_name, "");

//		if (actionMap==null)
//		{
				action = new gov.nih.nci.caadapter.ui.mapping.sdtm.actions.SaveSdtmAction(this, sdtmMappingGenerator);
				contextManager.addClientMenuAction(MenuConstants.DB_TO_SDTM, MenuConstants.FILE_MENU_NAME,ActionConstants.SAVE, action);
				contextManager.addClientMenuAction(MenuConstants.DB_TO_SDTM, MenuConstants.TOOLBAR_MENU_NAME,ActionConstants.SAVE, action);
				action.setEnabled(true);

				action = new gov.nih.nci.caadapter.ui.mapping.sdtm.actions.SaveAsSdtmAction(this, sdtmMappingGenerator);
				contextManager.addClientMenuAction(MenuConstants.DB_TO_SDTM, MenuConstants.FILE_MENU_NAME,ActionConstants.SAVE_AS, action);
				contextManager.addClientMenuAction(MenuConstants.DB_TO_SDTM, MenuConstants.TOOLBAR_MENU_NAME,ActionConstants.SAVE_AS, action);
				action.setEnabled(true);

				action = new gov.nih.nci.caadapter.ui.mapping.mms.actions.AnotateAction(this);
				contextManager.addClientMenuAction(MenuConstants.DB_TO_SDTM, MenuConstants.FILE_MENU_NAME,ActionConstants.ANOTATE, action);
				action.setEnabled(true);

				action = new gov.nih.nci.caadapter.ui.mapping.hl7.actions.ValidateMapAction(this);
				contextManager.addClientMenuAction(MenuConstants.DB_TO_SDTM, MenuConstants.FILE_MENU_NAME,ActionConstants.VALIDATE, action);
				contextManager.addClientMenuAction(MenuConstants.DB_TO_SDTM, MenuConstants.TOOLBAR_MENU_NAME,ActionConstants.VALIDATE, action);
				action.setEnabled(true);

				action = new gov.nih.nci.caadapter.ui.mapping.hl7.actions.CloseMapAction(this);
				contextManager.addClientMenuAction(MenuConstants.DB_TO_SDTM, MenuConstants.FILE_MENU_NAME,ActionConstants.CLOSE, action);
				action.setEnabled(true);
				
				action = new gov.nih.nci.caadapter.ui.mapping.sdtm.actions.GenerateSDTMReportAction(this);
				contextManager.addClientMenuAction(MenuConstants.DB_TO_SDTM, MenuConstants.REPORT_MENU_NAME,ActionConstants.GENERATE_REPORT, action);			
				contextManager.addClientMenuAction(MenuConstants.DB_TO_SDTM, MenuConstants.TOOLBAR_MENU_NAME,ActionConstants.GENERATE_REPORT, action);
				action.setEnabled(true);
				
				action = new gov.nih.nci.caadapter.ui.mapping.hl7.actions.RefreshMapAction(this);			
				contextManager.addClientMenuAction(MenuConstants.DB_TO_SDTM, MenuConstants.TOOLBAR_MENU_NAME,ActionConstants.REFRESH, action);
				action.setEnabled(true);
				
				actionMap = contextManager.getClientMenuActions(MenuConstants.DB_TO_SDTM, menu_name);
//		}		
		return actionMap;
	}
	
    /**
         * return the open action inherited with this client.
         */
    public Action getDefaultOpenAction() {
    	ContextManager contextManager = ContextManager.getContextManager();
    	return contextManager.getDefinedAction(ActionConstants.OPEN_CSV2SDTM_MAP_FILE_TXT);
    }


    /**
         * Explicitly reload information from the internal given file.
         * 
         * @throws Exception
         */
    public void reload() throws Exception {
    	//processOpenMapFile(getSaveFile());
    	processOpenSourceTree(getSaveFile(), false, true);
    }
    
    protected TreeDefaultDropTransferHandler getTargetTreeDropTransferHandler()
	{
		return this.sdtmTargetTreeDropTransferHandler;
	}
 
    /**
         * Reload the file specified in the parameter.
         * 
         * @param changedFileMap
         */
    public void reload(Map<MappingFileSynchronizer.FILE_TYPE, File> changedFileMap) {
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
		    JOptionPane.showMessageDialog(this, existMapFile.getAbsolutePath() + " does not exist or is not accessible anymore", "File Error", JOptionPane.ERROR_MESSAGE);
		    return;
		}
	    } else {// exist map file does not exist, simply reload source
		// and/or target file
		Iterator it = changedFileMap.keySet().iterator();
		while (it.hasNext()) {
		    MappingFileSynchronizer.FILE_TYPE key = (MappingFileSynchronizer.FILE_TYPE) it.next();
		    File file = changedFileMap.get(key);
		    if (GeneralUtilities.areEqual(MappingFileSynchronizer.FILE_TYPE.Source_File, key)) {
			processOpenSourceTree(file, true, true);
		    } else if (GeneralUtilities.areEqual(MappingFileSynchronizer.FILE_TYPE.Target_File, key)) {
			processOpenTargetTree(file, true, true);
		    }
		}// end of while
	    }// end of else
	} catch (Exception e) {
	    DefaultSettings.reportThrowableToLogAndUI(this, e, "", this, false, false);
	}
    }

    public String getDefineXMLLocation() {
    	return defineXMLLocation;
    }

    public void setDefineXMLLocation(String defineXMLLocation) {
    	this.defineXMLLocation = defineXMLLocation;
    }

    public TreeNode getTargetNodes() {
    	if (tTree!=null)
    		return tTree.getRootTreeNode();
    	else
    		return null;
    }

    public TreeNode getSourceNodes() {
    	if (sTree!=null)
    		return sTree.getRootTreeNode();
    	else
    		return null;
    }

    public SDTMMappingGenerator getSdtmMappingGenerator() {
    	return sdtmMappingGenerator;
    }
}
