/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ui.mapping.hl7;

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.BaseResult;
import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.MetaParser;
import gov.nih.nci.caadapter.common.csv.CSVMetaParserImpl;
import gov.nih.nci.caadapter.common.csv.CSVMetaResult;
import gov.nih.nci.caadapter.common.map.BaseComponent;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.map.Mapping;
import gov.nih.nci.caadapter.hl7.map.impl.MapParserImpl;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.v2meta.V2MetaXSDUtil;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.MappingFileSynchronizer;
import gov.nih.nci.caadapter.ui.common.actions.TreeCollapseAllAction;
import gov.nih.nci.caadapter.ui.common.actions.TreeExpandAllAction;
import gov.nih.nci.caadapter.ui.common.context.ContextManager;
import gov.nih.nci.caadapter.ui.common.context.MenuConstants;
import gov.nih.nci.caadapter.ui.common.nodeloader.NewHSMBasicNodeLoader;
import gov.nih.nci.caadapter.ui.common.nodeloader.SCMMapSourceNodeLoader;
import gov.nih.nci.caadapter.ui.common.tree.DefaultMappableTreeNode;
import gov.nih.nci.caadapter.ui.common.tree.DefaultSourceTreeNode;
import gov.nih.nci.caadapter.ui.common.tree.MappingSourceTree;
import gov.nih.nci.caadapter.ui.common.tree.TargetTreeDropTransferHandler;
import gov.nih.nci.caadapter.ui.common.tree.TreeDefaultDragTransferHandler;
import gov.nih.nci.caadapter.ui.common.tree.TreeDefaultDropTransferHandler;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.caadapter.ui.mapping.hl7.actions.RefreshMapAction;
import gov.nih.nci.cbiit.cmps.core.ElementMeta;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JRootPane;
import javax.swing.JLabel;
import javax.swing.Action;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;


import java.awt.Container;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Map;

/**
 * The class is the main panel to construct the UI and initialize the utilities to
 * facilitate mapping functions.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.19 $
 *          date        $Date: 2009-04-24 18:22:49 $
 */
public class HL7MappingPanel extends AbstractMappingPanel
{
	private static final String LOGID = "$RCSfile: HL7MappingPanel.java,v $";
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/hl7/HL7MappingPanel.java,v 1.19 2009-04-24 18:22:49 wangeug Exp $";
	private static final String SELECT_CSV_TIP = "Select a " + Config.CSV_MODULE_NAME;//CSV Specification";
	private static final String SELECT_SOURCE = "Open Source...";
	private static final String SELECT_V2_TIP = "Select an HL7 V2 Message Type";

	private static final String SELECT_TARGET = "Open Target...";
	private static final String SELECT_HMD_TIP = "Select an " + Config.HL7_V3_METADATA_MODULE_NAME;//HL7 v3 Specification";

	private TargetTreeDropTransferHandler targetTreeDropTransferHandler = null;

	private JButton openSourceButton = new JButton(SELECT_SOURCE);
	private JButton openTargetButton = new JButton(SELECT_TARGET);

	private boolean isCSVSource=true;
	/**
	 * Create a Mapping panel with CSV as source
	 * @throws Exception
	 */
	public HL7MappingPanel() throws Exception
	{
		this(true);
	}

	public HL7MappingPanel(boolean isCSVSource) throws Exception
	{
		this("","calledFromConstructor","");
		this.isCSVSource=isCSVSource;
		if (!isCSVSource)
			openSourceButton.setToolTipText(SELECT_V2_TIP);
	}

    public HL7MappingPanel(String sourceFile, String targetFile, String _flag) throws Exception
	{
    	this.setBorder(BorderFactory.createEmptyBorder());
		this.setLayout(new BorderLayout());
		this.add(getCenterPanel(true), BorderLayout.CENTER);
		fileSynchronizer = new MappingFileSynchronizer(this);

		if (!sourceFile.equals(""))
			processOpenSourceTree(new File(sourceFile), false, false);

        if ((targetFile == null)||(targetFile.equals(""))) throw new Exception("Empty Target File");
        if (!targetFile.equals("calledFromConstructor"))
        {
	        File file = new File(targetFile);
	        if (!file.exists()) throw new Exception("Target File is Not exist. : " + targetFile);
	        if (!file.isFile()) throw new Exception("Target File is Not a file. : " + targetFile);
	        boolean success = processOpenTargetTree(file, true, true);
	        if (!success) throw new Exception((MessageResources.getMessage("GEN3", new Object[0])).toString());
	    }
	}

	protected JPanel getTopLevelLeftPanel()
	{
		JPanel topCenterPanel = new JPanel(new BorderLayout());
		topCenterPanel.setBorder(BorderFactory.createEmptyBorder());
		JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		DefaultSettings.setDefaultFeatureForJSplitPane(centerSplitPane);

		//construct source panel
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
		sourceLocationPanel.add(openSourceButton, BorderLayout.EAST);
		openSourceButton.setMnemonic('S');
		openSourceButton.setToolTipText(SELECT_CSV_TIP);
		openSourceButton.addActionListener(this);
		sourceButtonPanel.add(sourceLocationPanel, BorderLayout.NORTH);
		sourceScrollPane.setSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 4), (int) (Config.FRAME_DEFAULT_HEIGHT / 1.5)));
		sourceButtonPanel.add(sourceScrollPane, BorderLayout.CENTER);

		//construct target panel
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
		targetLocationPanel.add(openTargetButton, BorderLayout.EAST);
		openTargetButton.setMnemonic('T');
		openTargetButton.setToolTipText(SELECT_HMD_TIP);
		openTargetButton.addActionListener(this);
		targetButtonPanel.add(targetLocationPanel, BorderLayout.NORTH);
//		targetScrollPane = DefaultSettings.createScrollPaneWithDefaultFeatures();
		targetButtonPanel.add(targetScrollPane, BorderLayout.CENTER);
		targetButtonPanel.setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 5), (int) (Config.FRAME_DEFAULT_HEIGHT / 1.5)));

		//construct middle panel
		JPanel middleContainerPanel = new JPanel(new BorderLayout());
		JLabel placeHolderLabel = new JLabel();
		placeHolderLabel.setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 3.5), 24));
		middlePanel = new MappingMiddlePanel(this);
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

	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		try
		{
			boolean everythingGood = true;
			if (SELECT_SOURCE.equals(command))
			{
				Container rootContainer=this.getRootContainer();
				JFrame rootFrame=(JFrame)rootContainer;
				if ( isCSVSource){
					File file = DefaultSettings.getUserInputOfFileFromGUI(this,
							Config.SOURCE_TREE_FILE_DEFAULT_EXTENTION, Config.OPEN_DIALOG_TITLE_FOR_DEFAULT_SOURCE_FILE, false, false);
					if (file != null)
					{
						everythingGood = processOpenSourceTree(file, true, true);
					}
				}
				else
				{
					System.out.println("HL7MappingPanel.actionPerformed()...sourceDatatype: v2 Schema");
					V2SchemaSelectionDialog wizard = null;
			        try
			        {
			            wizard = new V2SchemaSelectionDialog(rootFrame, "HL7 V2 Schema Selection", true);
			        }
			        catch(Exception ee)
			        {
			            JOptionPane.showMessageDialog((JFrame)getRootContainer(), "NewHSMWizard Error (1) : " + ee.getMessage(), "New H3S creation failure !! ", JOptionPane.ERROR_MESSAGE);
			            return;
			        }
			        String msg = wizard.getErrorMessage();
			        if (msg != null)
			        {
			            if (!msg.trim().equals(""))
			            {
			                JOptionPane.showMessageDialog((JFrame)getRootContainer(), "NewHSMWizard Error (2) : " + wizard.getErrorMessage(), "New H3S creation failure !! ", JOptionPane.ERROR_MESSAGE);
			                return;
			            }
			        }
			        if (wizard.isOkButtonClicked())
					{
			        	String v2Version=wizard.getFrontPage().getUserSelectedMessageVersion();
			        	String v2Schema=wizard.getFrontPage().getUserSelectedMessageSchema();
			        	processV2Meta(v2Version+"/"+v2Schema);
					}
				}
			}
			else if (SELECT_TARGET.equals(command))
			{
				File file = DefaultSettings.getUserInputOfFileFromGUI(this,
					//last added fileExtension :.h3s will be set as default
					Config.HL7_V3_MESSAGE_FILE_DEFAULT_EXTENSION+";"+Config.TARGET_TREE_FILE_DEFAULT_EXTENTION, Config.OPEN_DIALOG_TITLE_FOR_DEFAULT_TARGET_FILE, false, false);
				if (file != null)
				{
					everythingGood = processOpenTargetTree(file, true, true);
				}
			}
			if (!everythingGood)
			{
				Message msg = MessageResources.getMessage("GEN3", new Object[0]);
				JOptionPane.showMessageDialog(this, msg.toString(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		catch (Exception e1)
		{
			DefaultSettings.reportThrowableToLogAndUI(this, e1, "", this, false, false);
		}
	}

	private void processV2Meta(String v2VersionAndSchema)
	{
    	ElementMeta elmMeta =V2MetaXSDUtil.loadMessageMeta(v2VersionAndSchema, null);//, v2Schema);
    	DefaultMappableTreeNode srcNode=processElmentMeta(elmMeta);
    	V2MetaXSDUtil.resetElementHash(srcNode);
		//Build the source tree
		sTree = new MappingSourceTree(middlePanel, srcNode);
		sTree.getSelectionModel().addTreeSelectionListener((TreeSelectionListener) (getMappingDataManager().getPropertiesSwitchController()));
		sourceTreeDragTransferHandler = new TreeDefaultDragTransferHandler(sTree, DnDConstants.ACTION_LINK);
		sourceScrollPane.setViewportView(sTree);
		sTree.expandAll();

		//register collapse all and expand all actions.
		sourceTreeCollapseAllAction.setTree(sTree);
		sourceTreeExpandAllAction.setTree(sTree);
		sTree.getInputMap().put(sourceTreeCollapseAllAction.getAcceleratorKey(), sourceTreeCollapseAllAction.getName());
		sTree.getActionMap().put(sourceTreeCollapseAllAction.getName(), sourceTreeCollapseAllAction);
		sTree.getInputMap().put(sourceTreeExpandAllAction.getAcceleratorKey(), sourceTreeExpandAllAction.getName());
		sTree.getActionMap().put(sourceTreeExpandAllAction.getName(), sourceTreeExpandAllAction);

		if (tTree != null)
		{
			resetMiddlePanel();
		}

		mappingSourceFile = null;

		if (this.getRootPane() != null)
		{
			this.getRootPane().repaint();
		}
		getMappingFileSynchronizer().registerFile(MappingFileSynchronizer.FILE_TYPE.Source_File, null);
		middlePanel.getMappingDataManager().registerSourceComponent(null,null);
 		//set the source meta kind to "xsd" for v2Meta
		Mapping mappingData=middlePanel.getMappingDataManager().retrieveMappingData(false);
		middlePanel.getMappingDataManager().retrieveMappingData(false).setMappingType("V2_TO_V3");
		mappingData.setMappingType("V2_TO_V3");
		BaseComponent srcComp=mappingData.getSourceComponent();
 		srcComp.setKind(v2VersionAndSchema); //version and schema are combined

	}

private DefaultMappableTreeNode processElmentMeta(ElementMeta eMeta)
{
	DefaultMappableTreeNode rtnNode;
	rtnNode =new DefaultSourceTreeNode(eMeta, true);
	for(ElementMeta child:eMeta.getChildElement())
	{
		DefaultMappableTreeNode childNode=	processElmentMeta(child);
		rtnNode.add(childNode);
	}
	return rtnNode;

}
	protected TreeNode loadSourceTreeData( Object metaInfo, File absoluteFile)throws Exception
	{
		// The following is changed by eric for the need of loading dbm file as the source, todo need refactory
		String fileExtension = FileUtil.getFileExtension(absoluteFile, true);

		TreeNode node;
		if (Config.CSV_METADATA_FILE_DEFAULT_EXTENTION.equals(fileExtension))
		{
			// generate GUI nodes from object graph.
			SCMMapSourceNodeLoader scmMapSourceNodeLoader = new SCMMapSourceNodeLoader();
			node = scmMapSourceNodeLoader.loadData(metaInfo);
		}
		else
		{
			throw new ApplicationException("Unknow Source File Extension:" + absoluteFile,
				new IllegalArgumentException());
		}

		return node;
	}

	protected TreeNode loadTargetTreeData( Object metaInfo, File absoluteFile)throws Exception
	{
//		 The following is changed by eric for the need of loading dbm file as the source, todo need refactory
		String fileExtension = FileUtil.getFileExtension(absoluteFile, true);

		TreeNode nodes;
		if (Config.HSM_META_DEFINITION_FILE_DEFAULT_EXTENSION.equals(fileExtension)
				||Config.HL7_V3_MESSAGE_FILE_DEFAULT_EXTENSION.equals(fileExtension))
		{
			// generate GUI nodes from object graph.
	        try
	        {
	        	NewHSMBasicNodeLoader newHsmNodeLoader=new NewHSMBasicNodeLoader(false);
	        	if(metaInfo!=null&&metaInfo instanceof MIFClass)
	        		nodes=newHsmNodeLoader.loadMappingTargetData(metaInfo);
	        	else
	        		nodes = newHsmNodeLoader.loadMappingTargetData(absoluteFile);
	        }
	        catch(Throwable e)
	        {
	            Log.logException(this.getClass(), "Cannot initialize the tree anymore!", e);
	            DefaultSettings.reportThrowableToLogAndUI(this, e, "Error occurred during tree initialitation", this, true, true);
	            return null;
	        }
		}
		else
		{
			throw new ApplicationException("Unknow Source File Extension:" + absoluteFile,
				new IllegalArgumentException());
		}
		return nodes;
	}

	protected void buildTargetTree(Object metaInfo, File absoluteFile, boolean isToResetGraph) throws Exception
	{
		super.buildTargetTree(metaInfo, absoluteFile, isToResetGraph);
//		drop target for DnD from source tree.
		targetTreeDropTransferHandler = new TargetTreeDropTransferHandler(tTree, getMappingDataManager(), DnDConstants.ACTION_LINK);
	}

	/**
	 * Called by actionPerformed() and overridable by descendant classes.
	 *
	 * @param file
	 * @throws Exception
	 */
	protected boolean processOpenSourceTree(File file, boolean isToResetGraph, boolean supressReportIssuesToUI) throws Exception
	{
		String fileExtension = FileUtil.getFileExtension(file, true);

		MetaParser parser = null;
		MetaObject metaInfo = null;
		BaseResult returnResult = null;
		if(fileExtension.equalsIgnoreCase(Config.SOURCE_TREE_FILE_DEFAULT_EXTENTION))
		{
			parser = new CSVMetaParserImpl();

			returnResult = parser.parse(new FileReader(file));
			ValidatorResults validatorResults = returnResult.getValidatorResults();
			if (validatorResults != null && validatorResults.hasFatal())
			{
				Message msg = validatorResults.getMessages(ValidatorResult.Level.FATAL).get(0);
				DefaultSettings.reportThrowableToLogAndUI(this, null, msg.toString(), this, true, supressReportIssuesToUI);
				return false;
			}
			metaInfo = ((CSVMetaResult) returnResult).getCsvMeta();
		}

		buildSourceTree(metaInfo, file, isToResetGraph);
 		middlePanel.getMappingDataManager().registerSourceComponent(metaInfo, file);
 		//set the source meta kind to "xsd" for v2Meta
 		if (metaInfo==null)
 			middlePanel.getMappingDataManager().retrieveMappingData(false).setMappingType("V2_TO_V3");
// 			middlePanel.getMappingDataManager().retrieveMappingData(false).getSourceComponent().setKind("xsd");
		return true;
	}

	/**
	 * Called by actionPerformed() and overridable by descendant classes.
	 *
	 * @param file
	 * @throws Exception
	 */
	protected boolean processOpenTargetTree(File file, boolean isToResetGraph, boolean supressReportIssuesToUI) throws Exception
	{
		String fileExtension = FileUtil.getFileExtension(file, true);
		// parse the file into a meta object graph.
		MetaParser parser = null;
		MetaObject metaInfo = null;
		BaseResult returnResult = null;

		// The following is changed by eric for the need of loading dbm file as the source, todo need refactory

		// parse the file into a meta object graph.
		buildTargetTree(metaInfo, file, isToResetGraph);
		DefaultMutableTreeNode targetMIFTreeNode=(DefaultMutableTreeNode)tTree.getRootTreeNode().getChildAt(0);

		middlePanel.getMappingDataManager().registerTargetComponent((MIFClass)targetMIFTreeNode.getUserObject(), file);
		return true;
	}

	/**
	 * Called by actionPerformed() and overridable by descendant classes.
	 *
	 * @param file
	 * @throws Exception changed from protected to pulic by sean
	 */
	public ValidatorResults processOpenMapFile(File file) throws Exception
	{
		System.out.println("HL7MappingPanel.processOpenMapFile()...:"+file.getAbsolutePath());
		long stTime=System.currentTimeMillis();
		// parse the file.
		MapParserImpl parser = new MapParserImpl();
		ValidatorResults validatorResults = parser.parse(file.getParent(), new FileReader(file));
		if (validatorResults != null && validatorResults.hasFatal())
		{//immediately return if it has fatal errors.
			return validatorResults;
		}
		Mapping mapping = parser.getMapping();//returnResult.getMapping();

		//build source tree
		BaseComponent sourceComp = mapping.getSourceComponent();
		Object sourceMetaInfo = sourceComp.getMeta();
		File sourceFile = sourceComp.getFile();
		if (sourceFile!=null)
			buildSourceTree(sourceMetaInfo, sourceFile, false);
		else
		{//
			String v2schema=sourceComp.getKind();
			processV2Meta(v2schema);
			openSourceButton.setToolTipText(SELECT_V2_TIP);
			isCSVSource=false;
		}
		//build target tree
		BaseComponent targetComp = mapping.getTargetComponent();
		Object targetMetaInfo = targetComp.getMeta();
		File targetFile = targetComp.getFile();
		buildTargetTree(targetMetaInfo, targetFile, false);

		middlePanel.getMappingDataManager().setMappingData(mapping);

		//set both invisible since no use to allow user to change while mapping exists.
		if (mapping.getFunctionComponent().size() > 0 || mapping.getMaps().size() > 0)
		{
			openSourceButton.setEnabled(false);
			openTargetButton.setEnabled(false);
		}
		setSaveFile(file);
		System.out.println("HL7MappingPanel.processOpenMapFile()..timespending:"+(System.currentTimeMillis()-stTime));
		return validatorResults;
	}


	public Map getMenuItems(String menu_name)
	{
		Action action = null;
		ContextManager contextManager = ContextManager.getContextManager();
		Map <String, Action>actionMap = contextManager.getClientMenuActions(MenuConstants.CSV_TO_HL7V3, menu_name);
		if (MenuConstants.FILE_MENU_NAME.equals(menu_name))
		{
			JRootPane rootPane = this.getRootPane();
			if (rootPane != null)
			{//rootpane is not null implies this panel is fully displayed;
				//on the flip side, if it is null, it implies it is under certain construction.
				if (isCSVSource)
					contextManager.enableAction(ActionConstants.NEW_CSV_TO_HL7_MAP_FILE, false);
				else
					contextManager.enableAction(ActionConstants.NEW_V2_TO_V3_MAP_FILE, false);

				contextManager.enableAction(ActionConstants.OPEN_V2_TO_V3_MAP_FILE, true);
			}
		}
		//since the action depends on the panel instance,
		//the old action instance should be removed
		if (actionMap!=null)
			contextManager.removeClientMenuAction(MenuConstants.CSV_SPEC, menu_name, "");

//		if (actionMap==null)
//		{
				action = new gov.nih.nci.caadapter.ui.mapping.hl7.actions.SaveMapAction(this);
				contextManager.addClientMenuAction(MenuConstants.CSV_TO_HL7V3, MenuConstants.FILE_MENU_NAME,ActionConstants.SAVE, action);
				contextManager.addClientMenuAction(MenuConstants.CSV_TO_HL7V3, MenuConstants.TOOLBAR_MENU_NAME,ActionConstants.SAVE, action);
				action.setEnabled(true);

				action = new gov.nih.nci.caadapter.ui.mapping.hl7.actions.SaveAsMapAction(this);
				contextManager.addClientMenuAction(MenuConstants.CSV_TO_HL7V3, MenuConstants.FILE_MENU_NAME,ActionConstants.SAVE_AS, action);
				contextManager.addClientMenuAction(MenuConstants.CSV_TO_HL7V3, MenuConstants.TOOLBAR_MENU_NAME,ActionConstants.SAVE_AS, action);
				action.setEnabled(true);

				action = new gov.nih.nci.caadapter.ui.mapping.mms.actions.AnotateAction(this);
				contextManager.addClientMenuAction(MenuConstants.CSV_TO_HL7V3, MenuConstants.FILE_MENU_NAME,ActionConstants.ANOTATE, action);
				action.setEnabled(true);

				action = new gov.nih.nci.caadapter.ui.mapping.hl7.actions.ValidateMapAction(this);
				contextManager.addClientMenuAction(MenuConstants.CSV_TO_HL7V3, MenuConstants.FILE_MENU_NAME,ActionConstants.VALIDATE, action);
				contextManager.addClientMenuAction(MenuConstants.CSV_TO_HL7V3, MenuConstants.TOOLBAR_MENU_NAME,ActionConstants.VALIDATE, action);
				action.setEnabled(true);

				action = new gov.nih.nci.caadapter.ui.mapping.hl7.actions.CloseMapAction(this);
				contextManager.addClientMenuAction(MenuConstants.CSV_TO_HL7V3, MenuConstants.FILE_MENU_NAME,ActionConstants.CLOSE, action);
				action.setEnabled(true);

				action = new gov.nih.nci.caadapter.ui.mapping.hl7.actions.GenerateReportAction(this);
				contextManager.addClientMenuAction(MenuConstants.CSV_TO_HL7V3, MenuConstants.REPORT_MENU_NAME,ActionConstants.GENERATE_REPORT, action);
				contextManager.addClientMenuAction(MenuConstants.CSV_TO_HL7V3, MenuConstants.TOOLBAR_MENU_NAME,ActionConstants.GENERATE_REPORT, action);
				action.setEnabled(true);

				action = new RefreshMapAction(this);
				contextManager.addClientMenuAction(MenuConstants.CSV_TO_HL7V3, MenuConstants.TOOLBAR_MENU_NAME,ActionConstants.REFRESH, action);
				action.setEnabled(true);

				actionMap = contextManager.getClientMenuActions(MenuConstants.CSV_TO_HL7V3, menu_name);
//		}
		return actionMap;
	}

	/**
	 * return the open action inherited with this client.
	 */
	public Action getDefaultOpenAction()
	{
		ContextManager contextManager = ContextManager.getContextManager();
		Action openAction=contextManager.getDefinedAction(ActionConstants.OPEN_V2_TO_V3_MAP_FILE);
		return openAction;
	}

	/**
	 * Explicitly reload information from the internal given file.
	 *
	 * @throws Exception
	 */
	public void reload() throws Exception
	{
		processOpenMapFile(getSaveFile());
	}

	protected TreeDefaultDropTransferHandler getTargetTreeDropTransferHandler()
	{
		return this.targetTreeDropTransferHandler;
	}

	/**
	 * Reload the file specified in the parameter.
	 * @param changedFileMap
	 */
	public void reload(Map<MappingFileSynchronizer.FILE_TYPE, File> changedFileMap)
	{
		/**
		 * Design rationale:
		 * 1) if the changedFileMap is null, simply return;
		 * 2) if the getSaveFile() method does not return null, it implies current panel associates with a mapping file,
		 * just reload the whole mapping file so as to refresh those mapping relationship;
		 * 3) if the getSaveFile() returns null, just reload source and/or target file within the changedFileMap,
		 * and ignore the checking of MappingFileSynchronizer.FILE_TYPE.Mapping_File item in the map;
		 */
		if(changedFileMap==null)
		{
			return;
		}
		File existMapFile = getSaveFile();
		try
		{
			if(existMapFile!=null)
			{
				if(existMapFile.exists())
				{
					processOpenMapFile(existMapFile);
				}
				else
				{//exist map file does not exist anymore
					JOptionPane.showMessageDialog(this, existMapFile.getAbsolutePath() + " does not exist or is not accessible anymore", "File Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			else
			{//exist map file does not exist, simply reload source and/or target file
				Iterator it = changedFileMap.keySet().iterator();
				while(it.hasNext())
				{
					MappingFileSynchronizer.FILE_TYPE key = (MappingFileSynchronizer.FILE_TYPE) it.next();
					File file = changedFileMap.get(key);
					if(GeneralUtilities.areEqual(MappingFileSynchronizer.FILE_TYPE.Source_File, key))
					{
						processOpenSourceTree(file, true, true);
					}
					else if(GeneralUtilities.areEqual(MappingFileSynchronizer.FILE_TYPE.Target_File, key))
					{
						processOpenTargetTree(file, true, true);
					}
				}//end of while
			}//end of else
		}
		catch (Exception e)
		{
			DefaultSettings.reportThrowableToLogAndUI(this, e, "", this, false, false);
		}
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.18  2009/02/26 19:42:27  wangeug
 * HISTORY      : Disable action based on  mapping  type
 * HISTORY      :
 * HISTORY      : Revision 1.17  2009/02/03 15:49:22  wangeug
 * HISTORY      : separate menu item group: csv to HL7 V3 and HL7 V2 to HL7 V3
 * HISTORY      :
 * HISTORY      : Revision 1.16  2009/02/02 14:53:32  wangeug
 * HISTORY      : Load V2 meta with version number and message schema name; do not use the absolute path of schema file
 * HISTORY      :
 * HISTORY      : Revision 1.15  2009/01/23 18:22:00  wangeug
 * HISTORY      : Load V2 meta with version number and message schema name; do not use the absolute path of schema file
 * HISTORY      :
 * HISTORY      : Revision 1.14  2009/01/16 15:18:39  wangeug
 * HISTORY      : register MIFClass as mapping metaobject as opening a target H3S
 * HISTORY      :
 * HISTORY      : Revision 1.13  2008/10/16 14:35:57  wangeug
 * HISTORY      : set sourcecomponent.kind=2.x, linkpointer.kind="v2"
 * HISTORY      :
 * HISTORY      : Revision 1.12  2008/10/14 17:22:32  wangeug
 * HISTORY      : save mapping between v2Meta to H3S
 * HISTORY      :
 * HISTORY      : Revision 1.11  2008/10/09 18:11:47  wangeug
 * HISTORY      : load V2Meta XSD as source data meta, display on left source panel
 * HISTORY      :
 * HISTORY      : Revision 1.10  2008/09/29 20:29:07  wangeug
 * HISTORY      : enforce code standard: license file, file description, changing history
 * HISTORY      :
 * HISTORY      : Revision 1.9  2008/06/09 19:54:06  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2007/10/03 16:40:52  wangeug
 * HISTORY      : set .h3s as default to open targetFile from csvtoHl7V3 mapping panel
 * HISTORY      :
 * HISTORY      : Revision 1.7  2007/08/27 20:44:26  umkis
 * HISTORY      : fix the Bug of infinite looping when choice included HL7 transformation
 * HISTORY      :
 * HISTORY      : Revision 1.6  2007/08/13 15:53:39  wangeug
 * HISTORY      : Export MIF class as xml file or read a MIF class from an xml file
 * HISTORY      :
 * HISTORY      : Revision 1.5  2007/08/13 15:23:11  wangeug
 * HISTORY      : add new menu:open H3S with "xml" format
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/08/10 16:58:09  wangeug
 * HISTORY      : Export MIF class as xml file or read a MIF class from an xml file
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/07/31 20:54:03  wangeug
 * HISTORY      : resolve issues with preliminary test of release 4.0
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/20 17:05:29  wangeug
 * HISTORY      : integrate Hl7 transformation service
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/03 19:37:25  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.86  2006/12/19 22:48:40  umkis
 * HISTORY      : Null pointer error protetion
 * HISTORY      :
 * HISTORY      : Revision 1.85  2006/11/10 03:54:17  umkis
 * HISTORY      : constructer HL7MappingPanel(String sourceFileName, String targetFile, String _flag) was added
 * HISTORY      :
 * HISTORY      : Revision 1.84  2006/10/03 17:38:28  jayannah
 * HISTORY      : added another constructor explicity for V2V3 module
 * HISTORY      :
 * HISTORY      : Revision 1.83  2006/10/03 14:10:51  jayannah
 * HISTORY      : Added a new constructor so that the SCS file is opened in the source pane to be ready for V2V3 map for a target H3S file
 * HISTORY      :
 * HISTORY      : Revision 1.82  2006/09/26 15:56:06  wuye
 * HISTORY      : Add a new constructor for Object 2 database mapping panel
 * HISTORY      :
 * HISTORY      : Revision 1.81  2006/09/06 18:25:21  umkis
 * HISTORY      : The new implement of Vocabulary Mapping function.
 * HISTORY      :
 * HISTORY      : Revision 1.80  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.79  2006/07/13 19:51:49  jiangsc
 * HISTORY      : Save point.
 * HISTORY      :
 * HISTORY      : Revision 1.78  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.77  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.76  2005/12/29 23:06:14  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.75  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.74  2005/12/22 19:06:33  jiangsc
 * HISTORY      : Feature enhancement.
 * HISTORY      :
 * HISTORY      : Revision 1.73  2005/12/08 23:22:43  jiangsc
 * HISTORY      : Upgrade the handleValidatorResults() function.
 * HISTORY      :
 * HISTORY      : Revision 1.72  2005/12/02 23:02:57  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 * HISTORY      : Revision 1.71  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.70  2005/11/23 19:48:52  jiangsc
 * HISTORY      : Enhancement on mapping validations.
 * HISTORY      :
 * HISTORY      : Revision 1.69  2005/11/18 20:28:14  jiangsc
 * HISTORY      : Enhanced context-sensitive menu navigation and constructions.
 * HISTORY      :
 * HISTORY      : Revision 1.67  2005/11/14 19:55:51  jiangsc
 * HISTORY      : Implementing UI enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.66  2005/11/09 23:05:51  jiangsc
 * HISTORY      : Back to previous version.
 * HISTORY      :
 * HISTORY      : Revision 1.64  2005/11/02 22:36:06  chene
 * HISTORY      : change "\\" to "/"
 * HISTORY      :
 * HISTORY      : Revision 1.63  2005/10/26 18:12:29  jiangsc
 * HISTORY      : replaced printStackTrace() to Log.logException
 * HISTORY      :
 * HISTORY      : Revision 1.62  2005/10/26 16:43:36  chene
 * HISTORY      : Fix the spelling error
 * HISTORY      :
 * HISTORY      : Revision 1.61  2005/10/25 22:00:42  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.60  2005/10/24 20:31:00  jiangsc
 * HISTORY      : Turned off auto-scroll feature to comprise mapping issue.
 * HISTORY      :
 * HISTORY      : Revision 1.59  2005/10/18 17:01:03  jiangsc
 * HISTORY      : Changed one function signature in DragDrop component;
 * HISTORY      : Enhanced drag-drop status monitoring in HL7MappingPanel;
 * HISTORY      :
 * HISTORY      : Revision 1.58  2005/10/17 22:32:00  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.57  2005/10/13 18:53:44  jiangsc
 * HISTORY      : Added validation on invalid file type to map and HSM modules.
 * HISTORY      :
 * HISTORY      : Revision 1.56  2005/10/13 17:37:41  jiangsc
 * HISTORY      : Enhanced UI reporting on exceptions.
 * HISTORY      :
 * HISTORY      : Revision 1.55  2005/10/12 18:51:10  giordanm
 * HISTORY      : get MappingResult object working
 * HISTORY      :
 * HISTORY      : Revision 1.54  2005/10/10 20:49:01  jiangsc
 * HISTORY      : Enhanced dialog operation.
 * HISTORY      :
 * HISTORY      : Revision 1.53  2005/10/05 20:52:11  jiangsc
 * HISTORY      : GUI Enhancement.
 * HISTORY      :
 * HISTORY      : Revision 1.52  2005/10/05 20:50:30  jiangsc
 * HISTORY      : GUI Enhancement.
 * HISTORY      :
 * HISTORY      : Revision 1.51  2005/10/05 20:39:54  jiangsc
 * HISTORY      : GUI Enhancement.
 * HISTORY      :
 * HISTORY      : Revision 1.50  2005/09/16 23:18:55  chene
 * HISTORY      : Database prototype GUI support, but can not be loaded
 * HISTORY      :
 * HISTORY      : Revision 1.49  2005/09/16 16:20:17  giordanm
 * HISTORY      : HL7V3 parser is not returning a result object not a just a meta object.
 * HISTORY      :
 * HISTORY      : Revision 1.48  2005/09/15 16:01:49  giordanm
 * HISTORY      : trying to get result objects working for CSVMetaParser... (result objects contain the information a service returns as well as a list of validation errors/warnings)
 * HISTORY      :
 * HISTORY      : Revision 1.47  2005/08/26 15:57:45  jiangsc
 * HISTORY      : TreeExpandAll and TreeCollapseAll Class package move
 * HISTORY      :
 * HISTORY      : Revision 1.46  2005/08/22 21:35:29  jiangsc
 * HISTORY      : Changed BaseComponentFactory and other UI classes to use File instead of string name;
 * HISTORY      : Added first implementation of Function Constant;
 * HISTORY      :
 * HISTORY      : Revision 1.45  2005/08/22 17:32:46  giordanm
 * HISTORY      : change the file attribute within BaseComponent from a String to a File,  this checkin also contains some refactor work to the FileUtil.
 * HISTORY      :
 * HISTORY      : Revision 1.44  2005/08/19 21:09:38  jiangsc
 * HISTORY      : Name change
 * HISTORY      :
 * HISTORY      : Revision 1.43  2005/08/18 15:30:18  jiangsc
 * HISTORY      : First implementation on Switch control.
 * HISTORY      :
 * HISTORY      : Revision 1.42  2005/08/17 20:01:38  chene
 * HISTORY      : Refactor HL7V3MetaFileParser to a singleton
 * HISTORY      :
 * HISTORY      : Revision 1.41  2005/08/12 18:38:12  jiangsc
 * HISTORY      : Enable HL7 V3 Message to be saved in multiple XML file.
 * HISTORY      :
 * HISTORY      : Revision 1.40  2005/08/11 22:10:33  jiangsc
 * HISTORY      : Open/Save File Dialog consolidation.
 * HISTORY      :
 * HISTORY      : Revision 1.39  2005/08/08 18:05:53  giordanm
 * HISTORY      : a bunch of checkins that changes hard coded paths to relative paths.
 * HISTORY      :
 * HISTORY      : Revision 1.38  2005/08/05 20:35:54  jiangsc
 * HISTORY      : 0)Implemented field sequencing on CSVPanel but needs further rework;
 * HISTORY      : 1)Removed (Yes/No) for questions;
 * HISTORY      : 2)Removed double-checking after Save-As;
 * HISTORY      :
 * HISTORY      : Revision 1.37  2005/08/04 22:22:18  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 * HISTORY      : Revision 1.36  2005/08/02 22:28:56  jiangsc
 * HISTORY      : Newly enhanced context-sensitive menus and toolbar.
 * HISTORY      :
 * HISTORY      : Revision 1.35  2005/07/28 18:18:43  jiangsc
 * HISTORY      : Can Open HSM Panel
 * HISTORY      :
 * HISTORY      : Revision 1.34  2005/07/27 22:41:12  jiangsc
 * HISTORY      : Consolidated context sensitive menu implementation.
 * HISTORY      :
 * HISTORY      : Revision 1.33  2005/07/27 13:57:44  jiangsc
 * HISTORY      : Added the first round of HSMPanel.
 * HISTORY      :
 * HISTORY      : Revision 1.32  2005/07/25 22:32:16  jiangsc
 * HISTORY      : Added Open command.
 * HISTORY      :
 * HISTORY      : Revision 1.31  2005/07/25 21:56:44  jiangsc
 * HISTORY      : 1) Added expand all and collapse all;
 * HISTORY      : 2) Added toolbar on the mapping panel;
 * HISTORY      : 3) Consolidated menus;
 */

