/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.ui.mapping.catrend;

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.BaseResult;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.caadapter.common.MetaParser;
import gov.nih.nci.caadapter.common.SDKMetaData;
import gov.nih.nci.caadapter.common.csv.CSVMetaParserImpl;
import gov.nih.nci.caadapter.common.csv.CSVMetaResult;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.map.impl.MappingImpl;
import gov.nih.nci.caadapter.mms.generator.CumulativeMappingGenerator;
import gov.nih.nci.caadapter.mms.generator.CumulativeMappingToMappingFileGenerator;
import gov.nih.nci.caadapter.mms.metadata.ModelMetadata;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.MappableNode;
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
import gov.nih.nci.caadapter.ui.mapping.hl7.actions.RefreshMapAction;
import gov.nih.nci.caadapter.ui.mapping.mms.MMSRenderer;
import gov.nih.nci.caadapter.ui.mapping.mms.actions.MmsTargetTreeDropTransferHandler;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociation;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAttribute;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLDependency;
import gov.nih.nci.ncicb.xmiinout.domain.UMLModel;
import gov.nih.nci.ncicb.xmiinout.domain.UMLPackage;
import gov.nih.nci.ncicb.xmiinout.domain.UMLTaggedValue;
import gov.nih.nci.ncicb.xmiinout.util.ModelUtil;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * The class is the main panel to construct the UI and initialize the utilities
 * to facilitate mapping functions.
 * 
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v3.2 revision $Revision: 1.1 $ date $Date:
 *          2007/04/03 16:17:57 $
 */
public class CsvToXmiMappingPanel extends AbstractMappingPanel {
	private static final String LOGID = "$RCSfile: CsvToXmiMappingPanel.java,v $";

	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/catrend/CsvToXmiMappingPanel.java,v 1.1 2007-11-29 14:26:31 wangeug Exp $";

    private MmsTargetTreeDropTransferHandler mmsTargetTreeDropTransferHandler = null;
	private static final String SELECT_XMI = "Open XMI File...";
    private static final String SELECT_CSV = "Open CSV Meta File...";
    private static final String SAVE_MAP = "Save Map File...";
    
	private static HashSet<String> primaryKeys = new HashSet<String>();
	private static HashSet<String> lazyKeys = new HashSet<String>();
    private static HashSet<String> clobKeys = new HashSet<String>();
    private static HashSet<String> discriminatorKeys = new HashSet<String>();
    private static Hashtable<String, String> discriminatorValues = new Hashtable<String, String>();

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
        JButton saveMapButton = new JButton(SAVE_MAP);
		centerFuncationPanel.add(saveMapButton, BorderLayout.CENTER);

		saveMapButton.addActionListener(this);
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
		// The following is changed by eric for the need of loading dbm file as the source, todo need refactory
		String fileExtension = FileUtil.getFileExtension(file, true);

		TreeNode node;
		if (Config.CSV_METADATA_FILE_DEFAULT_EXTENTION.equals(fileExtension))
		{
			// generate GUI nodes from object graph.
			SCMMapSourceNodeLoader scmMapSourceNodeLoader = new SCMMapSourceNodeLoader();
			node = scmMapSourceNodeLoader.loadData(metaInfo);
		}
		else
		{
			throw new ApplicationException("Unknow Source File Extension:" + file,
				new IllegalArgumentException());
		}

		return node;
	}

	protected TreeNode loadTargetTreeData(Object metaInfo, File absoluteFile)
			throws Exception {
		TreeNode nodes = new DefaultMutableTreeNode("Data Model");
		ModelMetadata myModel = ModelMetadata.getInstance();
		LinkedHashMap myMap = myModel.getModelMetadata();

		Set keySet = myMap.keySet();
		Iterator keySetIterator = keySet.iterator();
		while (keySetIterator.hasNext()) {
			String key = (String) keySetIterator.next();
			if (key.contains( CaadapterUtil.readPrefParams( Config.MMS_PREFIX_DATAMODEL ) + ".")) {
				if (myMap.get(key) instanceof gov.nih.nci.caadapter.mms.metadata.ObjectMetadata) {
					construct_node(nodes, key, ( CaadapterUtil.readPrefParams( Config.MMS_PREFIX_DATAMODEL ) + ".").length(), true, false);
				} else {
					construct_node(nodes, key, ( CaadapterUtil.readPrefParams( Config.MMS_PREFIX_DATAMODEL ) + ".").length(), false, false);
				}
			}
		}
		return nodes;
	}

	protected void buildTargetTree(Object metaInfo, File absoluteFile,
			boolean isToResetGraph) throws Exception {
		super.buildTargetTree(metaInfo, absoluteFile, isToResetGraph);
		
		 tTree.setCellRenderer(new MMSRenderer());
        // instantiate the "DropTransferHandler"
		mmsTargetTreeDropTransferHandler = new MmsTargetTreeDropTransferHandler(
				tTree, getMappingDataManager(), DnDConstants.ACTION_LINK);
	}

	protected TreeDefaultDropTransferHandler getTargetTreeDropTransferHandler() {
		return this.mmsTargetTreeDropTransferHandler;
	}

	/**
	 * Returns an ImageIcon, or null if the path was invalid.
	 */
	protected static ImageIcon createImageIcon(String path)
	{
	    java.net.URL imgURL = DefaultSettings.class.getClassLoader().getResource("images/" + path);
	    if (imgURL != null)
	    {
	        return new ImageIcon(imgURL);
	    } else
	    {
	        System.err.println("Couldn't find file: " + imgURL.toString() + " & " + path);
	        return null;
	    }
	}

	private void construct_node(TreeNode node, String fullName, int prefixLen, boolean isTable, boolean isSourceNode)
	{
		String name = fullName.substring(prefixLen, fullName.length());
		String[] pks = name.split("\\.");
	
		ModelMetadata myModel = ModelMetadata.getInstance();
		LinkedHashMap myMap = myModel.getModelMetadata();
	
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

		MetaParser parser = null;
		MetaObject metaInfo = null;
		BaseResult returnResult = null;

		// parse the file into a meta object graph.
		parser = new CSVMetaParserImpl();
		returnResult = parser.parse(new FileReader(file));
		ValidatorResults validatorResults = returnResult.getValidatorResults();
		if (validatorResults != null && validatorResults.hasFatal())
		{
			Message msg = validatorResults.getMessages(ValidatorResult.Level.FATAL).get(0);
			DefaultSettings.reportThrowableToLogAndUI(this, null, msg.toString(), this, true, supressReportIssuesToUI);
			return false;
		}
		//default to Config.HSM_META_DEFINITION_FILE_DEFAULT_EXTENSION
		metaInfo = ((CSVMetaResult) returnResult).getCsvMeta();

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
		String fileName = file.getAbsolutePath();
		boolean success = CumulativeMappingGenerator.init(fileName);
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
	private ValidatorResults processOpenMapFile(File file) throws Exception {
		CumulativeMappingGenerator cumulativeMappingGenerator = CumulativeMappingGenerator.getInstance();

		// Read the XMI Mapping attributes
		String fileName = file.getAbsolutePath();

		boolean success = CumulativeMappingGenerator.init(fileName);

		if (success) {
			ModelMetadata myModel = ModelMetadata.getInstance();

			if (myModel == null) {
				JOptionPane.showMessageDialog(null, "Error opening XMI file");
			}
			boolean isSuccess;
			boolean status = false;
			String xmiFileName = "";

			// Read XMI File and construct Target and Source Trees
			status = processOpenSourceTree(file, true, true);

			TreeModel sModel = sTree.getModel();
			DefaultMutableTreeNode rootSTree = (DefaultMutableTreeNode) sModel
					.getRoot();

			TreeModel tModel = tTree.getModel();
			DefaultMutableTreeNode rootTTree = (DefaultMutableTreeNode) tModel
					.getRoot();

			Hashtable sourceNodes = new Hashtable();
			Hashtable targetNodes = new Hashtable();

			buildHash(sourceNodes, rootSTree, CaadapterUtil.readPrefParams( Config.MMS_PREFIX_OBJECTMODEL ));
			buildHash(targetNodes, rootTTree, CaadapterUtil.readPrefParams( Config.MMS_PREFIX_DATAMODEL ));

			MappingImpl newMappingImpl = new MappingImpl();
			newMappingImpl.setSourceComponent(null);
			newMappingImpl.setTargetComponent(null);

			middlePanel.getMappingDataManager().setMappingData(newMappingImpl);
			middlePanel.getMappingDataManager().clearAllGraphCells();

			setSaveFile(file);

			// Lets try to get all the details
			UMLModel myUMLModel = myModel.getModel();
			UMLClass client = null;
			UMLClass supplier = null;

			for (UMLDependency dep : myUMLModel.getDependencies()) {
				String sourceXpath = "";
				String targetXpath = "";

				client = (UMLClass) dep.getClient();
				supplier = (UMLClass) dep.getSupplier();

				StringBuffer pathKey = new StringBuffer(ModelUtil
						.getFullPackageName(client));
				targetXpath = pathKey + "." + client.getName();

				pathKey = new StringBuffer(ModelUtil
						.getFullPackageName(supplier));
				sourceXpath = pathKey + "." + supplier.getName();

				DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) sourceNodes
						.get(sourceXpath);
				DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) targetNodes
						.get(targetXpath);

				if (sourceNode == null || targetNode == null)
					continue;

				SDKMetaData sourceSDKMetaData = (SDKMetaData) sourceNode
						.getUserObject();
				SDKMetaData targetSDKMetaData = (SDKMetaData) targetNode
						.getUserObject();

				sourceSDKMetaData.setMapped(true);
				isSuccess = cumulativeMappingGenerator.map(sourceXpath,
						targetXpath);
				isSuccess = isSuccess
						&& getMappingDataManager().createMapping(
								(MappableNode) sourceNode,
								(MappableNode) targetNode);
			}

			for (UMLPackage pkg : myUMLModel.getPackages()) {
				for (UMLPackage pkg2 : pkg.getPackages()) {
					for (UMLClass clazz : pkg2.getClasses()) {
						StringBuffer pathKey = new StringBuffer(ModelUtil
								.getFullPackageName(clazz));

						for (UMLAttribute att : clazz.getAttributes()) {
							for (UMLTaggedValue tagValue : att
									.getTaggedValues()) {
								String sourceXpath = "";
								String targetXpath = "";

								if (tagValue.getName().contains(
										"mapped-attribute")
										|| tagValue.getName().contains(
												"implements-association")) {
									targetXpath = pathKey + "."
											+ clazz.getName() + "."
											+ att.getName();
									sourceXpath = CaadapterUtil.readPrefParams( Config.MMS_PREFIX_OBJECTMODEL ) + "."
											+ tagValue.getValue();
									DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) sourceNodes
											.get(sourceXpath);
									DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) targetNodes
											.get(targetXpath);

									if (sourceNode == null
											|| targetNode == null)
										continue;

									SDKMetaData sourceSDKMetaData = (SDKMetaData) sourceNode
											.getUserObject();
									SDKMetaData targetSDKMetaData = (SDKMetaData) targetNode
											.getUserObject();

									sourceSDKMetaData.setMapped(true);
									isSuccess = cumulativeMappingGenerator.map(
											sourceXpath, targetXpath);
									isSuccess = isSuccess
											&& getMappingDataManager()
													.createMapping(
															(MappableNode) sourceNode,
															(MappableNode) targetNode);
								}
							}
						}
					}
				}
			}
			
			primaryKeys = new HashSet<String>();
			lazyKeys = new HashSet<String>();
			discriminatorKeys = new HashSet<String>();
            clobKeys = new HashSet<String>();
			
			//Retrieve all the primaryKeys & lazyKeys saved as TaggedValues
			for( UMLPackage pkg : myUMLModel.getPackages() ) 
			{
				getPackages( pkg );
			}				

            myModel.setPrimaryKeys( primaryKeys );
			myModel.setLazyKeys( lazyKeys );
            myModel.setClobKeys( clobKeys );
            myModel.setDiscriminatorKeys( discriminatorKeys );
            myModel.setDiscriminatorValues(discriminatorValues);

            if ( CaadapterUtil.readPrefParams( Config.MMS_PREFIX_DATAMODEL ) != null )
            {
                myModel.setMmsPrefixDataModel(CaadapterUtil.readPrefParams( Config.MMS_PREFIX_DATAMODEL ));
            } else {
                myModel.setMmsPrefixDataModel( "Logical View.Data Model" );
            }
            if ( CaadapterUtil.readPrefParams( Config.MMS_PREFIX_OBJECTMODEL ) != null )
            {
                myModel.setMmsPrefixObjectModel(CaadapterUtil.readPrefParams( Config.MMS_PREFIX_OBJECTMODEL ));
            } else {
                myModel.setMmsPrefixObjectModel( "Logical View.Logical Model" );
            }

        } else {
			JOptionPane
					.showMessageDialog(	null, "The .map or .xmi file selected is not valid. Please check the export settings in EA and try again.");
		}
		return null;
	}

	//Recursive loop required to find all primaryKeys
	private void getPackages( UMLPackage pkg )
	{
		for ( UMLClass clazz : pkg.getClasses() )
		{
			for( UMLTaggedValue tagValue : clazz.getTaggedValues() )
			{
				if( tagValue.getName().contains( "discriminator" ))
				{	
					String packageName = "";
					UMLPackage umlPackage = clazz.getPackage();
					while (umlPackage != null)
					{
						packageName = umlPackage.getName() + "." + packageName;
						umlPackage = umlPackage.getParent();
					}
		            packageName =  packageName + clazz.getName();
		            
	    			int preLen = CaadapterUtil.readPrefParams(Config.MMS_PREFIX_OBJECTMODEL).length();
	    			String dvalue = (packageName).substring(preLen+1);

	    			discriminatorValues.put(dvalue, tagValue.getValue() );
				}
			}			
            for( UMLAttribute att : clazz.getAttributes() )
			{	
				for( UMLTaggedValue tagValue : att.getTaggedValues() )
				{
					if( tagValue.getName().contains( "id-attribute" ))
					{																						
						primaryKeys.add( tagValue.getValue() );
					}
                    if( tagValue.getName().contains( "discriminator" ) )
                    {
                        discriminatorKeys.add(clazz.getName()+"." + att.getName());
                    }
                    if( tagValue.getName().contains( "type" ) )
                    {
                        if( tagValue.getValue().equals( "CLOB") )
                        {
                            String fieldName = clazz.getName() + "." + att.getName();
                            clobKeys.add( fieldName );                            
                        }
                    }
                }
			}		
    		CumulativeMappingGenerator cumulativeMappingGenerator = CumulativeMappingGenerator.getInstance();

			for( UMLAssociation assc : clazz.getAssociations()) 
			{
				for( UMLTaggedValue tagValue : assc.getTaggedValues() )
				{
					if( tagValue.getName().contains( "lazy-load" ) && tagValue.getValue().equalsIgnoreCase("no"))
					{
			    		String fieldName = cumulativeMappingGenerator.getColumnFromAssociation(assc);
			    		if (fieldName!=null)
			    		{
			    			int preLen = CaadapterUtil.readPrefParams(Config.MMS_PREFIX_DATAMODEL).length();
			    			lazyKeys.add(fieldName.substring(preLen+1));
			    		}
					}
				}
			}				
		}
		
		for ( UMLPackage pkg2 : pkg.getPackages() )
		{
			getPackages( pkg2 );
		}
	}
	
	/**
	 * Called by actionPerformed() and overridable by descendant classes.
	 * 
	 * @param file
	 * @throws Exception
	 *             changed from protected to pulic by sean
	 */
	private ValidatorResults processOpenOldMapFile(File file) throws Exception {
		String xmiFileName = "";
		SAXBuilder builder = new SAXBuilder(false);
		Document doc = builder.build(new File(file.getAbsolutePath()));
		Element root = doc.getRootElement();
		Element components = root.getChild("components");
		if (components == null) {
			xmiFileName = "";
		} else {
			Element component = components.getChild("component");
			if (component == null) {
				xmiFileName = "";
			} else {
				xmiFileName = component.getAttributeValue("location");
			}
		}
		File newXmiFile = null;
		if (xmiFileName.equals("")) {
			newXmiFile = DefaultSettings.getUserInputOfFileFromGUI(
					(AbstractMainFrame) (this.getRootPane().getParent()),
					".xmi", "Select XMI file", false, false);
		} else {
			newXmiFile = new File(xmiFileName);
			if (!(newXmiFile.exists())) {
				newXmiFile = DefaultSettings.getUserInputOfFileFromGUI(
						(AbstractMainFrame) (this.getRootPane().getParent()),
						".xmi", "Select XMI file", false, false);
			}
		}

		if (newXmiFile == null) {
			return null;
		}
		boolean status = processOpenSourceTree(newXmiFile, true, true);

		TreeModel sModel = sTree.getModel();
		DefaultMutableTreeNode rootSTree = (DefaultMutableTreeNode) sModel
				.getRoot();
		TreeModel tModel = tTree.getModel();
		DefaultMutableTreeNode rootTTree = (DefaultMutableTreeNode) tModel
				.getRoot();
		Hashtable sourceNodes = new Hashtable();
		Hashtable targetNodes = new Hashtable();
		buildHash(sourceNodes, rootSTree, CaadapterUtil.readPrefParams( Config.MMS_PREFIX_OBJECTMODEL ));
		buildHash(targetNodes, rootTTree, CaadapterUtil.readPrefParams( Config.MMS_PREFIX_DATAMODEL ));

		MappingImpl newMappingImpl = new MappingImpl();
		newMappingImpl.setSourceComponent(null);
		newMappingImpl.setTargetComponent(null);
		middlePanel.getMappingDataManager().setMappingData(newMappingImpl);

		middlePanel.getMappingDataManager().clearAllGraphCells();

		setSaveFile(file);

		List elements = root.getChildren("link");
		Iterator i = elements.iterator();
		CumulativeMappingGenerator cumulativeMappingGenerator = CumulativeMappingGenerator
				.getInstance();
		boolean isSuccess;
		while (i.hasNext()) {
			String sourceXpath = "";
			String targetXpath = "";
			Element link = (Element) i.next();
			Element sourceElement = link.getChild("source");
			if (sourceElement == null) {
				/* TODO
				* */
			}
			sourceXpath = sourceElement.getValue();

			Element targetElement = link.getChild("target");
			if (targetElement == null) {
				/* TODO
				* */
			}
			targetXpath = targetElement.getValue();
			DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) sourceNodes
					.get(sourceXpath);
			DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) targetNodes
					.get(targetXpath);
			if (sourceNode == null || targetNode == null)
				continue;
			SDKMetaData sourceSDKMetaData = (SDKMetaData) sourceNode
					.getUserObject();
			SDKMetaData targetSDKMetaData = (SDKMetaData) targetNode
					.getUserObject();
			sourceSDKMetaData.setMapped(true);
			isSuccess = cumulativeMappingGenerator
					.map(sourceXpath, targetXpath);
			isSuccess = isSuccess
					&& getMappingDataManager().createMapping(
							(MappableNode) sourceNode,
							(MappableNode) targetNode);
		}
		return null;
	}

	private void buildHash(Hashtable hashtable, DefaultMutableTreeNode root,
			String parent) {
		if ((root.getUserObject().toString().equals("Object Model") && parent
				.equals(CaadapterUtil.readPrefParams( Config.MMS_PREFIX_OBJECTMODEL )))
				|| (root.getUserObject().toString().equals("Data Model") && parent
						.equals(CaadapterUtil.readPrefParams( Config.MMS_PREFIX_DATAMODEL )))) {
			for (int i = 0; i < root.getChildCount(); i++) {
				buildHash(hashtable, (DefaultMutableTreeNode) root
						.getChildAt(i), parent);
			}
		} else {
			String treeString;
			if (root.getUserObject() instanceof String) {
				treeString = (String) root.getUserObject();
			} else {
				treeString = ((MetaObjectImpl) root.getUserObject()).getTitle();
			}
			hashtable.put(parent + "." + treeString, root);
			if (root.isLeaf())
				return;
			for (int i = 0; i < root.getChildCount(); i++) {
				buildHash(hashtable, (DefaultMutableTreeNode) root
						.getChildAt(i), parent + "." + treeString);
			}
		}
	}

	public Map getMenuItems(String menu_name) {
		Action action = null;
		ContextManager contextManager = ContextManager.getContextManager();
		Map<String, Action> actionMap = contextManager.getClientMenuActions(
				MenuConstants.DB_TO_OBJECT, menu_name);
		if (MenuConstants.FILE_MENU_NAME.equals(menu_name)) {
			JRootPane rootPane = this.getRootPane();
			if (rootPane != null) {// rootpane is not null implies this panel
									// is fully displayed;
				// on the flip side, if it is null, it implies it is under
				// certain construction.
				contextManager.enableAction(ActionConstants.NEW_O2DB_MAP_FILE,
						false);
				contextManager.enableAction(ActionConstants.OPEN_O2DB_MAP_FILE,
						true);
			}
		}
		// since the action depends on the panel instance,
		// the old action instance should be removed
		if (actionMap != null)
			contextManager.removeClientMenuAction(MenuConstants.CSV_SPEC,
					menu_name, "");

        action = new gov.nih.nci.caadapter.ui.mapping.mms.actions.SaveObjectToDbMapAction(
				this);
		contextManager.addClientMenuAction(MenuConstants.DB_TO_OBJECT,
				MenuConstants.FILE_MENU_NAME, ActionConstants.SAVE, action);
		contextManager.addClientMenuAction(MenuConstants.DB_TO_OBJECT,
				MenuConstants.TOOLBAR_MENU_NAME, ActionConstants.SAVE, action);
		action.setEnabled(true);

		action = new gov.nih.nci.caadapter.ui.mapping.mms.actions.SaveAsObjectToDbMapAction(
				this);
		contextManager.addClientMenuAction(MenuConstants.DB_TO_OBJECT,
				MenuConstants.FILE_MENU_NAME, ActionConstants.SAVE_AS, action);
		contextManager.addClientMenuAction(MenuConstants.DB_TO_OBJECT,
				MenuConstants.TOOLBAR_MENU_NAME, ActionConstants.SAVE_AS,
				action);
		action.setEnabled(true);

		action = new gov.nih.nci.caadapter.ui.mapping.mms.actions.AnotateAction(
				this);
		contextManager.addClientMenuAction(MenuConstants.DB_TO_OBJECT,
				MenuConstants.FILE_MENU_NAME, ActionConstants.ANOTATE, action);
		action.setEnabled(true);

		action = new gov.nih.nci.caadapter.ui.mapping.mms.actions.ValidateObjectToDbMapAction(
				this);
		contextManager.addClientMenuAction(MenuConstants.DB_TO_OBJECT,
				MenuConstants.FILE_MENU_NAME, ActionConstants.VALIDATE, action);
		contextManager.addClientMenuAction(MenuConstants.DB_TO_OBJECT,
				MenuConstants.TOOLBAR_MENU_NAME, ActionConstants.VALIDATE,
				action);
		action.setEnabled(true);

		action = new gov.nih.nci.caadapter.ui.mapping.hl7.actions.CloseMapAction(
				this);
		contextManager.addClientMenuAction(MenuConstants.DB_TO_OBJECT,
				MenuConstants.FILE_MENU_NAME, ActionConstants.CLOSE, action);
		action.setEnabled(true);

		action = new gov.nih.nci.caadapter.ui.mapping.mms.actions.ValidateObjectToDbMapAction(
				this);
		contextManager.addClientMenuAction(MenuConstants.DB_TO_OBJECT,
				MenuConstants.REPORT_MENU_NAME,
				ActionConstants.GENERATE_REPORT, action);
		contextManager.addClientMenuAction(MenuConstants.DB_TO_OBJECT,
				MenuConstants.TOOLBAR_MENU_NAME,
				ActionConstants.GENERATE_REPORT, action);
		action.setEnabled(true);

		action = new RefreshMapAction(this);
		contextManager.addClientMenuAction(MenuConstants.DB_TO_OBJECT,
				MenuConstants.TOOLBAR_MENU_NAME, ActionConstants.REFRESH,
				action);
		action.setEnabled(true);

		actionMap = contextManager.getClientMenuActions(
				MenuConstants.DB_TO_OBJECT, menu_name);
		// }
		return actionMap;
	}

	/**
	 * return the open action inherited with this client.
	 */
	public Action getDefaultOpenAction() {
		ContextManager contextManager = ContextManager.getContextManager();
		return contextManager
				.getDefinedAction(ActionConstants.OPEN_O2DB_MAP_FILE);
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		try {
			boolean everythingGood = true;
			if (SELECT_XMI.equals(command)) {
				File file = DefaultSettings.getUserInputOfFileFromGUI(this, // FileUtil.getUIWorkingDirectoryPath(),
						".xmi", "Open XMI file ...", false, false);
				if (file != null)
						processOpenTargetTree(file, true, true);
			}
			else if (SELECT_CSV.equals(command)) {
				File file = DefaultSettings.getUserInputOfFileFromGUI(this, // FileUtil.getUIWorkingDirectoryPath(),
						".scs", "Open CSV meta file ...", false, false);
	
				if (file != null) 
					processOpenSourceTree(file, true, true);
			}
			else if (SAVE_MAP.equals(command))
			{
				System.out.println("Save CSV to XMI Mapping...........");
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

	public void saveMappingFile() {
		File file = getSaveFile();		
		File mapFile = new File(file.getAbsolutePath().replaceAll(".xmi", ".map"));
		
		if (file == null) {
			file = DefaultSettings
					.getUserInputOfFileFromGUI(this,
							Config.MAP_FILE_DEFAULT_EXTENTION, "Save As...",
							true, true);
			if (file == null) {
				// user cancelled the action
				return;
			}
		}
	
		try {
			CumulativeMappingToMappingFileGenerator myGenerator = new CumulativeMappingToMappingFileGenerator();
			myGenerator.setXmiFileName(CumulativeMappingGenerator.getXmiFileName().replaceAll(".xmi", ".map"));
			// Creating mapping file		
			myGenerator.createLocalMappingFile();
			XMLOutputter outp = new XMLOutputter();
			outp.setFormat(Format.getPrettyFormat());
	
			try {
				FileOutputStream myStream = new FileOutputStream(mapFile);
				outp.output(myGenerator.getDocument(), myStream);
				myStream.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			// clear the change flag.
			setChanged(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			setSaveFile(mapFile);
		}
	}

	/**
	 * Explicitly reload information from the internal given file.
	 * 
	 * @throws Exception
	 */
	public void reload() throws Exception {
		processOpenMapFile(getSaveFile());
	}

	/**
	 * Reload the file specified in the parameter.
	 * 
	 * @param changedFileMap
	 */
	public void reload(
			Map<MappingFileSynchronizer.FILE_TYPE, File> changedFileMap) {
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
}

