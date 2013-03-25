/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.ui.mapping.mms;

import gov.nih.nci.caadapter.common.BaseResult;
import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.MetaObject;
import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.caadapter.common.MetaParser;
import gov.nih.nci.caadapter.common.SDKMetaData;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.Iso21090Util;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.map.impl.MappingImpl;
import gov.nih.nci.caadapter.mms.generator.CumulativeMappingGenerator;
import gov.nih.nci.caadapter.mms.generator.HBMGenerateCacoreIntegrator;
import gov.nih.nci.caadapter.common.metadata.AssociationMetadata;
import gov.nih.nci.caadapter.common.metadata.AttributeMetadata;
import gov.nih.nci.caadapter.common.metadata.ModelMetadata;
import gov.nih.nci.caadapter.common.metadata.ObjectMetadata;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.Iso21090uiUtil;
import gov.nih.nci.caadapter.ui.common.MappableNode;
import gov.nih.nci.caadapter.ui.common.MappingFileSynchronizer;
import gov.nih.nci.caadapter.ui.common.actions.TreeCollapseAllAction;
import gov.nih.nci.caadapter.ui.common.actions.TreeExpandAllAction;
import gov.nih.nci.caadapter.ui.common.context.ContextManager;
import gov.nih.nci.caadapter.ui.common.context.MenuConstants;
import gov.nih.nci.caadapter.ui.common.tree.DefaultSourceTreeNode;
import gov.nih.nci.caadapter.ui.common.tree.DefaultTargetTreeNode;
import gov.nih.nci.caadapter.ui.common.tree.TreeDefaultDropTransferHandler;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.caadapter.ui.mapping.hl7.actions.RefreshMapAction;
import gov.nih.nci.caadapter.ui.mapping.mms.actions.MmsTargetTreeDropTransferHandler;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAttribute;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLDependency;
import gov.nih.nci.ncicb.xmiinout.domain.UMLInterface;
import gov.nih.nci.ncicb.xmiinout.domain.UMLModel;
import gov.nih.nci.ncicb.xmiinout.domain.UMLPackage;
import gov.nih.nci.ncicb.xmiinout.domain.UMLTaggedValue;
import gov.nih.nci.ncicb.xmiinout.util.ModelUtil;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;


/**
 * The class is the main panel to construct the UI and initialize the utilities
 * to facilitate mapping functions.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @since     caAdatper v3.2
 * @version    $Revision: 1.43 $
 * @date       $Date: 2009-09-30 17:08:26 $
 */
public class Object2DBMappingPanel extends AbstractMappingPanel {
	private static final String LOGID = "$RCSfile: Object2DBMappingPanel.java,v $";

	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/mms/Object2DBMappingPanel.java,v 1.43 2009-09-30 17:08:26 wangeug Exp $";

    private MmsTargetTreeDropTransferHandler mmsTargetTreeDropTransferHandler = null;

	private static final String SELECT_XMI = "Open XMI file...";
    private static final String SELECT_XSD = "Open XSD file...";
    private static final String GENERATE_HBM = "Generate HBM Files";

	public Object2DBMappingPanel() {
		this("defaultObjectToDatabaseMapping");
	}

	public Object2DBMappingPanel(String name) {
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
		JButton openXMIButton = new JButton(SELECT_XMI);
		sourceLocationPanel.add(openXMIButton, BorderLayout.EAST);

        openXMIButton.setMnemonic('O');
		openXMIButton.setToolTipText("Select XMI file...");
		openXMIButton.addActionListener(this);
		sourceButtonPanel.add(sourceLocationPanel, BorderLayout.NORTH);
		// sourceScrollPane =
		// DefaultSettings.createScrollPaneWithDefaultFeatures();
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
		targetLocationArea.setEditable(false);
		targetLocationArea.setPreferredSize(new Dimension(
				(int) (Config.FRAME_DEFAULT_WIDTH / 10), 24));
		targetLocationPanel.add(targetLocationArea, BorderLayout.CENTER);
		targetButtonPanel.add(targetLocationPanel, BorderLayout.NORTH);
		// targetScrollPane =
		// DefaultSettings.createScrollPaneWithDefaultFeatures();
		targetScrollPane.setPreferredSize(new Dimension(
				(int) (Config.FRAME_DEFAULT_WIDTH / 3),
				Config.FRAME_DEFAULT_HEIGHT / 2));
		targetButtonPanel.add(targetScrollPane, BorderLayout.CENTER);
		targetButtonPanel.setPreferredSize(new Dimension(
				(int) (Config.FRAME_DEFAULT_WIDTH / 5),
				(int) (Config.FRAME_DEFAULT_HEIGHT / 1.5)));

		// construct middle panel
		middlePanel = new MappingMiddlePanel(this);
		middlePanel.setKind("o2db");
		middlePanel.setSize(new Dimension(
				(int) (Config.FRAME_DEFAULT_WIDTH / 3),
				(int) (Config.FRAME_DEFAULT_HEIGHT / 1.5)));

        JPanel centerTopPanel = new JPanel(new BorderLayout(2, 0));
//        JButton generateHMBButton = new JButton(GENERATE_HBM);
//        centerTopPanel.add(generateHMBButton, BorderLayout.CENTER);
//        generateHMBButton.addActionListener(this);
//        //to hold the place equates the source and target button panel so as to
//        //align the drawing the graphs.
//		JLabel placeHolderLabel = new JLabel();
//		placeHolderLabel.setPreferredSize(new Dimension(
//				(int) (Config.FRAME_DEFAULT_WIDTH / 16), 24));
//
//		centerTopPanel.add(placeHolderLabel, BorderLayout.EAST);
		centerTopPanel.setPreferredSize(new Dimension(
				(int) (Config.FRAME_DEFAULT_WIDTH / 3.5), 24));

		JPanel middleContainerPanel = new JPanel(new BorderLayout());
		middleContainerPanel.add(centerTopPanel, BorderLayout.NORTH);
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

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		try {
			boolean everythingGood = true;
			if (SELECT_XMI.equals(command)) {
				File file = DefaultSettings.getUserInputOfFileFromGUI(this, // FileUtil.getUIWorkingDirectoryPath(),
						".xmi", "Open XMI file ...", false, false);
				if (file != null) {
					// everythingGood = processOpenSourceTree(file, true, true);
					ValidatorResults results = processOpenMapFile(file);
				}
			}
			else if (GENERATE_HBM.equals(command)) {
				File fileFromPanel = getSaveFile();

				if (fileFromPanel == null) {
					if (!isSourceTreePopulated() || !isTargetTreePopulated()) {
						String msg = "Conduct object to database mapping before saving the map specification.";
						JOptionPane.showMessageDialog(this, msg, "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				try {
					JFileChooser fileChooser = new JFileChooser(fileFromPanel);
					fileChooser
							.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int result = fileChooser.showOpenDialog(this);
					switch (result) {
					case JFileChooser.APPROVE_OPTION:
						if (!fileChooser.getSelectedFile().exists()) {
							boolean mkdirResult = fileChooser.getSelectedFile()
									.mkdirs();
							if (!mkdirResult) {
								JOptionPane
										.showMessageDialog(
												this,
												"Error creating specified directory, please make sure the directory name is correct!",
												"Error",
												JOptionPane.ERROR_MESSAGE);
								return;
							}
						}
						break;
					case JFileChooser.CANCEL_OPTION:
						System.out.println("HBM Generatoin Cancelled!");
						return;
					case JFileChooser.ERROR_OPTION:
						return;
					}
					saveMappingFile();
					String outputDir=fileChooser.getSelectedFile().getAbsolutePath();
					UMLModel model=CumulativeMappingGenerator.getInstance().getMetaModel().getHandler().getModel();
					HBMGenerateCacoreIntegrator.getInstance().generateMapping(model,outputDir);
					JOptionPane.showMessageDialog(getParent(),
							"HBM files are generated at "+ fileChooser.getSelectedFile().getAbsolutePath(),
							"HBM Generation Complete",
							JOptionPane.INFORMATION_MESSAGE);

				} catch (Exception ex) {
					ex.printStackTrace();
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

	private void saveMappingFile() {
		File file = getSaveFile();

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
		String mapFileName = file.getAbsolutePath().replaceAll(".xmi", ".map");
		try {
//			CumulativeMappingToMappingFileGenerator.writeMappingFile(new File(mapFileName), file.getAbsolutePath());
			ModelMetadata xmiMetada = CumulativeMappingGenerator.getInstance().getMetaModel();
			xmiMetada.getHandler().save( file.getAbsolutePath());
			setChanged(false);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			setSaveFile(file);
		}
	}

	protected TreeNode loadSourceTreeData(Object metaInfo, File file)
			throws Exception {
		TreeNode nodes = new DefaultMutableTreeNode("Object Model");
		ModelMetadata myModel = CumulativeMappingGenerator.getInstance().getMetaModel();
		LinkedHashMap myMap = myModel.getModelMetadata();

		Set keySet = myMap.keySet();
		Iterator keySetIterator = keySet.iterator();
		while (keySetIterator.hasNext()) {
			String key = (String) keySetIterator.next();
            if (key.contains( CaadapterUtil.readPrefParams( Config.MMS_PREFIX_OBJECTMODEL ) + ".") ) {
				//only directly construct new trerNode for the ObjectMetadata, leave AttributeMeta and AssociateMeta
            	//to be constructed indirectly as creating the ObjectMetadata
            	if (myMap.get(key) instanceof gov.nih.nci.caadapter.common.metadata.ObjectMetadata) {
					construct_node(nodes, key, (CaadapterUtil.readPrefParams( Config.MMS_PREFIX_OBJECTMODEL ) + ".").length(),  true);
				}
			}
		}
		return nodes;
	}

	private void construct_node(TreeNode node, String fullName, int prefixLen, boolean isSourceNode)
    {
		String name = fullName.substring(prefixLen, fullName.length());
		String[] pks = name.split("\\.");

		ModelMetadata myModel = CumulativeMappingGenerator.getInstance().getMetaModel();
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
		//find the package tree node of an object;
		//create the missing nodes
		//in case the object name contains ".", more token should removed from the full name
		MetaObject meta=(MetaObject)myMap.get(fullName);
		StringTokenizer nameToken=new StringTokenizer(meta.getName(), ".");

		DefaultMutableTreeNode packageNode = (DefaultMutableTreeNode) node;
		for (int i = 0; i < pks.length - nameToken.countTokens(); i++) {
			boolean exist = false;
			Enumeration children = packageNode.children();
			while (children.hasMoreElements()) {
				DefaultMutableTreeNode current = (DefaultMutableTreeNode) children
						.nextElement();

				if (current.toString().equals(pks[i])) {
					exist = true;
					packageNode = current;
					break;
				}
			}

			if (!exist) {
				DefaultMutableTreeNode newTreeNode = new DefaultMutableTreeNode(
						pks[i], true);
				packageNode.add(newTreeNode);
				packageNode = newTreeNode;
			}
		}
		DefaultMutableTreeNode newTreeNode;
		if (isSourceNode)
		{
			newTreeNode = new DefaultSourceTreeNode(myMap.get(fullName),true);
			//process Attributes associated with an object
			ObjectMetadata objectMeta=(ObjectMetadata)newTreeNode.getUserObject();
			int startLevel=0;
			if (objectMeta.getXPath().indexOf(Iso21090Util.ISO21090_DATAYPE_PACKAGE)>-1)
				startLevel=1;
			for (AttributeMetadata objectAttr:objectMeta.getAttributes())
			{
				DefaultSourceTreeNode attrNode=new DefaultSourceTreeNode(objectAttr, true);
				addIsoComplexTypeAttribute(startLevel,attrNode);
				newTreeNode.add(attrNode);
			}
			//process Association associted with an object
			for (AssociationMetadata asscMeta: objectMeta.getAssociations())
			{
				newTreeNode.add(new DefaultSourceTreeNode(asscMeta,false));
			}
		}
		else
			newTreeNode = new DefaultTargetTreeNode(myMap.get(fullName), true);

        packageNode.add(newTreeNode);
		return;
	}
	/**
	 * If an object attribute is an ISO data type, add all attributes of the ISO data type as
	 * child tree node to the object attribute
	 * @param attrLevel
	 * @param elementNode
	 * @param metaHash
	 */
	private void addIsoComplexTypeAttribute(int attrLevel,DefaultSourceTreeNode elementNode )
	{
		if (attrLevel>3)
			return;

		AttributeMetadata attributeMeta=(AttributeMetadata)elementNode.getUserObject();
		ObjectMetadata childObject =Iso21090Util.resolveAttributeDatatype(attributeMeta.getDatatype());
		if (childObject==null)
		{
			if (Iso21090Util.isCollectionDatatype(attributeMeta.getDatatype()))
			{
				addCollectoinStructure(elementNode);
				return;
			}
			else
			{
				//process the sequence data
				//it is a complex datatype with collection of attributes
				//two data types: AD, EN
				addComplexTypeSequence(elementNode);
				return;
			}
		}
		for (AttributeMetadata attrMeta:childObject.getAttributes())
		{
			DefaultSourceTreeNode childAttrNode=new DefaultSourceTreeNode(attrMeta,true);
			elementNode.add(childAttrNode);
			addIsoComplexTypeAttribute(attrLevel+1,childAttrNode );
		}
	}

	private void addCollectoinStructure(DefaultSourceTreeNode elementNode)
	{
		//Add the content of DSET
		ObjectMetadata dsetObject=Iso21090Util.resolveAttributeDatatype("DSET");
		for (AttributeMetadata attrMeta:dsetObject.getAttributes())
		{
			DefaultSourceTreeNode childAttrNode=new DefaultSourceTreeNode(attrMeta,true);
			elementNode.add(childAttrNode);
			if (attrMeta.getDatatype().equals("Set(T)"))
			{
				MetaObject collectionMeta=(MetaObject)elementNode.getUserObject();

				String collectoinElementName=null;
				if(collectionMeta instanceof AttributeMetadata)
					collectoinElementName= Iso21090Util.findElementDatatypeName(((AttributeMetadata)collectionMeta).getDatatype());
				if (collectoinElementName==null)
					continue;

				ObjectMetadata collectionElementObject =Iso21090Util.resolveAttributeDatatype(collectoinElementName);
				//return here if the data type is not define for a colleciton element
				//case I: ISO21090 data define DSET with type<T> which not defined
				//case II: Invalid element type
				if (collectionElementObject==null)
					continue;

				//process content of collection element
				for (AttributeMetadata elmntAttrMeta:collectionElementObject.getAttributes())
				{
					DefaultSourceTreeNode elementAttrNode=new DefaultSourceTreeNode(elmntAttrMeta,true);
					childAttrNode.add(elementAttrNode);
					addIsoComplexTypeAttribute(0,elementAttrNode );
				}
			}
			else
				addIsoComplexTypeAttribute(0,childAttrNode );
		}
	}
	/**
	 * If an object attribute is a sequence of ISO data types, add all elements of the sequence as
	 * child tree node to the object attribute. Each child ISO data type is flat down to its bottom
	 * @param elementNode
	 * @param metaHash
	 */
	private void addComplexTypeSequence(DefaultSourceTreeNode elementNode)
	{
		AttributeMetadata elementMeta=(AttributeMetadata)elementNode.getUserObject();
		List<ObjectMetadata> sequenceObjects =Iso21090Util.findSequenceDatatypes(elementMeta.getDatatype());
		if (sequenceObjects==null)
			return;

		for (ObjectMetadata objectMeta:sequenceObjects)
		{
//			DefaultSourceTreeNode childObjectNode=new DefaultSourceTreeNode(objectMeta,true);
//			elementNode.add(childObjectNode);
			for (AttributeMetadata objectAttr:objectMeta.getAttributes())
			{
				DefaultSourceTreeNode attrNode=new DefaultSourceTreeNode(objectAttr, true);
				addIsoComplexTypeAttribute(1,attrNode);
				elementNode.add(attrNode);
			}
		}
	}
	protected TreeNode loadTargetTreeData(Object metaInfo, File absoluteFile)
			throws Exception {
		TreeNode nodes = new DefaultMutableTreeNode("Data Model");
		ModelMetadata myModel = CumulativeMappingGenerator.getInstance().getMetaModel();
		LinkedHashMap myMap = myModel.getModelMetadata();

		Set keySet = myMap.keySet();
		Iterator keySetIterator = keySet.iterator();
		while (keySetIterator.hasNext()) {
			String key = (String) keySetIterator.next();
			if (key.contains( CaadapterUtil.readPrefParams( Config.MMS_PREFIX_DATAMODEL ) + ".")) {
				if (myMap.get(key) instanceof gov.nih.nci.caadapter.common.metadata.ObjectMetadata) {
					construct_node(nodes, key, ( CaadapterUtil.readPrefParams( Config.MMS_PREFIX_DATAMODEL ) + ".").length(),  false);
				} else {
					construct_node(nodes, key, ( CaadapterUtil.readPrefParams( Config.MMS_PREFIX_DATAMODEL ) + ".").length(),  false);
				}
			}
		}
		return nodes;
	}

	protected void buildTargetTree(Object metaInfo, File absoluteFile,
			boolean isToResetGraph) throws Exception {
		super.buildTargetTree(metaInfo, absoluteFile, isToResetGraph);

		 tTree.setCellRenderer(new MMSRenderer());
		 sTree.setCellRenderer(new MMSRendererPK());

        // instantiate the "DropTransferHandler"
		mmsTargetTreeDropTransferHandler = new MmsTargetTreeDropTransferHandler(
				tTree, getMappingDataManager(), DnDConstants.ACTION_LINK);
	}

	/**
	 * Called by actionPerformed() and overridable by descendant classes.
	 *
	 * @param file
	 * @throws Exception
	 */
	protected boolean processOpenSourceTree(File file, boolean isToResetGraph,
			boolean supressReportIssuesToUI) throws Exception {

        if( CaadapterUtil.readPrefParams( Config.MMS_PREFIX_OBJECTMODEL ) == null )
        {
              CaadapterUtil.savePrefParams( Config.MMS_PREFIX_OBJECTMODEL , "Logical View.Logical Model");
        }
        if( CaadapterUtil.readPrefParams( Config.MMS_PREFIX_DATAMODEL ) == null )
        {
              CaadapterUtil.savePrefParams( Config.MMS_PREFIX_DATAMODEL , "Logical View.Data Model");
        }

        MetaObject metaInfo = null;
		buildSourceTree(metaInfo, file, isToResetGraph);
		middlePanel.getMappingDataManager().registerSourceComponent(metaInfo,
				file);
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
	 */
	protected boolean processOpenTargetTree(File file, boolean isToResetGraph,
			boolean supressReportIssuesToUI) throws Exception {
		// parse the file into a meta object graph.
		MetaParser parser = null;
		BaseResult returnResult = null;
		returnResult = parser.parse(new FileReader(file));
		ValidatorResults validatorResults = returnResult.getValidatorResults();
		if (validatorResults != null && validatorResults.hasFatal()) {
			Message msg = validatorResults.getMessages(
					ValidatorResult.Level.FATAL).get(0);
			DefaultSettings.reportThrowableToLogAndUI(this, null, msg
					.toString(), this, true, supressReportIssuesToUI);
			return false;
		}

		buildTargetTree(null, file, isToResetGraph);
		middlePanel.getMappingDataManager().registerTargetComponent(null, file);
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
		// Read the XMI Mapping attributes
		String fileName = file.getAbsolutePath();
		boolean success = CumulativeMappingGenerator.init(fileName);
		if (success) {
			ModelMetadata xmiModelMeta = CumulativeMappingGenerator.getInstance().getMetaModel();
			if (xmiModelMeta == null) {
				JOptionPane.showMessageDialog(null, "Error opening XMI file");
			}
			boolean isSuccess;
			// Read XMI File and construct Target and Source Trees
			processOpenSourceTree(file, true, true);
			DefaultMutableTreeNode rootSTree = (DefaultMutableTreeNode) sTree.getModel().getRoot();
			DefaultMutableTreeNode rootTTree = (DefaultMutableTreeNode) tTree.getModel().getRoot();

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
			processXmiModel(xmiModelMeta,sourceNodes, targetNodes);
        } else {
			JOptionPane.showMessageDialog(	null, "The .map or .xmi file selected is not valid. Please check the export settings in EA and try again.");
		}
		return null;
	}

	private void processXmiModel(ModelMetadata myModelMeta, Hashtable sourceNodes, Hashtable targetNodes)
	{
		CumulativeMappingGenerator cumulativeMappingGenerator = CumulativeMappingGenerator.getInstance();
		// Lets try to get all the details
		UMLModel myUMLModel = myModelMeta.getModel();
		//read and set model prefix
        if ( CaadapterUtil.readPrefParams( Config.MMS_PREFIX_DATAMODEL ) != null )
        {
            myModelMeta.setMmsPrefixDataModel(CaadapterUtil.readPrefParams( Config.MMS_PREFIX_DATAMODEL ));
        } else {
            myModelMeta.setMmsPrefixDataModel( "Logical View.Data Model" );
        }
        if ( CaadapterUtil.readPrefParams( Config.MMS_PREFIX_OBJECTMODEL ) != null )
        {
            myModelMeta.setMmsPrefixObjectModel(CaadapterUtil.readPrefParams( Config.MMS_PREFIX_OBJECTMODEL ));
        } else {
            myModelMeta.setMmsPrefixObjectModel( "Logical View.Logical Model" );
        }

        boolean isSuccess;
		// create Object-table dependency mapping UI
		for (UMLDependency dep : myUMLModel.getDependencies())
		{
			String sourceXpath = "";
			String targetXpath = "";

			UMLClass client = (UMLClass) dep.getClient();
			if (dep.getSupplier() instanceof UMLInterface)
			{
				Log.logInfo(this, "found UMLInterface:"+((UMLInterface)dep.getSupplier()).getName());
				continue;
			}
			UMLClass supplier = (UMLClass) dep.getSupplier();

			StringBuffer pathKey = new StringBuffer(ModelUtil.getFullPackageName(client));
			targetXpath = pathKey + "." + client.getName();

			pathKey = new StringBuffer(ModelUtil.getFullPackageName(supplier));
			sourceXpath = pathKey + "." + supplier.getName();

			DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) sourceNodes.get(sourceXpath);
			DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) targetNodes.get(targetXpath);

			if (sourceNode == null || targetNode == null)
			{
				Log.logInfo(this, "Dependency missing--- source:"+sourceXpath +" ; target:"+targetXpath);
				continue;
			}

			SDKMetaData sourceSDKMetaData = (SDKMetaData) sourceNode.getUserObject();
			sourceSDKMetaData.setMapped(true);
			//loading XMI and create mapping UI
			isSuccess = cumulativeMappingGenerator.map(sourceXpath,	targetXpath,null, null, false);
			isSuccess = isSuccess&& getMappingDataManager().createMapping(
							(MappableNode) sourceNode,
							(MappableNode) targetNode);
			if (!isSuccess)
			{
				Log.logInfo(this, "No UI link is created for Dependency--- source:"+sourceXpath +" ; target:"+targetXpath +"...:"+CumulativeMappingGenerator.getInstance().getErrorMessage());
			}
		}
		//create class.attribute--table.column mapping
//		myModelMeta.getPreservedMappedTag().clear();
		for (UMLPackage pkg : myUMLModel.getPackages())
		{
			for (UMLPackage pkg2 : pkg.getPackages()) {
				for (UMLClass clazz : pkg2.getClasses()) {
					StringBuffer pathKey = new StringBuffer(ModelUtil.getFullPackageName(clazz));

					for (UMLAttribute att : clazz.getAttributes()) {
						for (UMLTaggedValue tagValue : att.getTaggedValues()) {
							String sourceXpath = "";
							String targetXpath = "";

							if (tagValue.getName().contains("mapped-attribute")
									|| tagValue.getName().contains("implements-association")) {
								targetXpath = pathKey + "."
									+ clazz.getName() + "."
									+ att.getName();
								sourceXpath = CaadapterUtil.readPrefParams( Config.MMS_PREFIX_OBJECTMODEL ) + "."
										+ tagValue.getValue();
								DefaultMutableTreeNode sourceNode = (DefaultMutableTreeNode) sourceNodes
										.get(sourceXpath);
								DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) targetNodes
										.get(targetXpath);

								if (sourceNode == null)
								{
									Log.logInfo(this, "Mapping source node missing--- source:"+sourceXpath +" ; target:"+targetXpath);
									continue;
								}
								if (targetNode == null)
								{
									Log.logInfo(this, "Mapping target node missing--- source:"+sourceXpath +" ; target:"+targetXpath);
									continue;
								}
								SDKMetaData sourceSDKMetaData = (SDKMetaData) sourceNode.getUserObject();
								sourceSDKMetaData.setMapped(true);
								AttributeMetadata annotationSDKMetadata=null;
								String tagRelativePath="";
								String anntationPath="";
								if (sourceSDKMetaData instanceof AttributeMetadata)
								{
									annotationSDKMetadata=Iso21090uiUtil.findAnnotationAttribute((DefaultMutableTreeNode)sourceNode);
									if (annotationSDKMetadata!=null)
										anntationPath=annotationSDKMetadata.getXPath();
									tagRelativePath=Iso21090uiUtil.findAttributeRelativePath((DefaultMutableTreeNode) sourceNode);
								}
								isSuccess = cumulativeMappingGenerator.map(sourceSDKMetaData.getXPath(), targetXpath,anntationPath,tagRelativePath, false);
								isSuccess = isSuccess&&
										getMappingDataManager().createMapping((MappableNode) sourceNode,(MappableNode) targetNode);
							}//tag level loop
						}//tag list level loop
					}//attribute level loop
				}//table level loop
			}//data model package level loop
		}//model level package level loop
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
				treeString = ((MetaObjectImpl) root.getUserObject()).getName();
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

	/**
	 * Explicitly reload information from the internal given file.
	 *
	 * @throws Exception
	 */
	public void reload() throws Exception {
		processOpenMapFile(getSaveFile());
	}

	protected TreeDefaultDropTransferHandler getTargetTreeDropTransferHandler() {
		return this.mmsTargetTreeDropTransferHandler;
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
}

/**
 * HISTORY : $Log: not supported by cvs2svn $
 * HISTORY : Revision 1.42  2009/09/29 17:39:28  wangeug
 * HISTORY : exclude valueDomain from mapping panel view
 * HISTORY :
 * HISTORY : Revision 1.41  2009/07/30 17:37:31  wangeug
 * HISTORY : clean codes: implement 4.1.1 requirements
 * HISTORY :
 * HISTORY : Revision 1.40  2009/07/14 16:36:48  wangeug
 * HISTORY : clean codes
 * HISTORY :
 * HISTORY : Revision 1.39  2009/07/10 19:57:04  wangeug
 * HISTORY : MMS re-engineering
 * HISTORY :
 * HISTORY : Revision 1.38  2009/06/12 15:53:49  wangeug
 * HISTORY : clean code: caAdapter MMS 4.1.1
 * HISTORY :
 * HISTORY : Revision 1.37  2008/09/26 20:35:27  linc
 * HISTORY : Updated according to code standard.
 * HISTORY :
 * HISTORY : Revision 1.36  2008/06/09 19:54:06  phadkes
 * HISTORY : New license text replaced for all .java files.
 * HISTORY :
 * HISTORY : Revision 1.35  2008/06/03 20:12:03  wangeug
 * HISTORY : use logger and preserve the primary key tag if mapped to an Object rather than an Object.Attribute
 * HISTORY :
 * HISTORY : Revision 1.34  2008/05/30 17:35:05  wangeug
 * HISTORY : add list to keep preserved mapping information
 * HISTORY :
 * HISTORY : Revision 1.33  2008/05/29 14:35:16  wangeug
 * HISTORY : use caCORE SDK 4.0 process "mapped-attributes" tagvalue
 * HISTORY :
 * HISTORY : Revision 1.32  2008/05/22 15:48:49  wangeug
 * HISTORY : integrate with caCORE SDK to generate Hibernate mapping
 * HISTORY :
 * HISTORY : Revision 1.31  2007/12/13 21:09:33  wangeug
 * HISTORY : resolve code dependence in compiling
 * HISTORY :
 * HISTORY : Revision 1.30  2007/11/16 17:18:36  wangeug
 * HISTORY : clean codes: remove unused "import"
 * HISTORY :
 * HISTORY : Revision 1.29  2007/10/11 19:06:26  schroedn
 * HISTORY : fixed HBMGenerator save error
 * HISTORY :
 * HISTORY : Revision 1.28  2007/09/21 04:41:08  wuye
 * HISTORY : removed system.out
 * HISTORY :
 * HISTORY : Revision 1.27  2007/09/20 16:40:14  schroedn
 * HISTORY : License text
 * HISTORY :
 * HISTORY : Revision 1.26  2007/09/17 15:08:14  wuye
 * HISTORY : added modify discriminator value capability
 * HISTORY :
 * HISTORY : Revision 1.25  2007/09/14 22:40:08  wuye
 * HISTORY : Fixed discriminator issue
 * HISTORY :
 * HISTORY : Revision 1.24  2007/09/14 15:06:25  wuye
 * HISTORY : Added support for table per inheritence structure
 * HISTORY :
 * HISTORY : Revision 1.23  2007/09/13 21:39:32  wuye
 * HISTORY : change arraylist to hashmap
 * HISTORY :
 * HISTORY : Revision 1.22  2007/09/13 20:48:31  schroedn
 * HISTORY : CLob, Discriminator, Lazy/Eager
 * HISTORY :
 * HISTORY : Revision 1.21  2007/09/13 18:53:40  wuye
 * HISTORY : Code re-org
 * HISTORY :
 * HISTORY : Revision 1.20  2007/09/13 14:20:15  schroedn
 * HISTORY : Changes the graphics for Clob/Lazy/Eager/Discriminator
 * HISTORY :
 * HISTORY : Revision 1.19  2007/09/12 20:56:00  wuye
 * HISTORY : Fixed the load from association "lazy-load"
 * HISTORY :
 * HISTORY : Revision 1.18  2007/09/12 17:57:55  schroedn
 * HISTORY : CLob, Discriminator, Lazy/Eager
 * HISTORY :
 * HISTORY : Revision 1.17  2007/09/12 16:04:15  schroedn
 * HISTORY : PreferenceManager -> CaadapterUtil
 * HISTORY :
 * HISTORY : Revision 1.16  2007/09/12 14:56:46  schroedn
 * HISTORY : *** empty log message ***
 * HISTORY :
 * HISTORY : Revision 1.14  2007/09/11 20:38:40  schroedn
 * HISTORY : CLob, Discriminator, Lazy/Eager
 * HISTORY :
 * HISTORY : Revision 1.12  2007/09/05 15:16:33  schroedn
 * HISTORY : Added icons to PK and Lazy/Eager
 * HISTORY :
 * HISTORY : Revision 1.11  2007/08/30 19:32:08  schroedn
 * HISTORY : fixed bug with loading without preferences set
 * HISTORY :
 * HISTORY : Revision 1.10  2007/08/28 18:36:08  schroedn
 * HISTORY : Added a NULL check for preferences
 * HISTORY :
 * HISTORY : Revision 1.9  2007/08/10 15:57:39  schroedn
 * HISTORY : New Feature - Preferences to change prefex in XMI
 * HISTORY :
 * HISTORY : Revision 1.8  2007/08/09 18:14:31  schroedn
 * HISTORY : New Feature - Preferences to change prefex in XMI
 * HISTORY :
 * HISTORY : Revision 1.7  2007/08/09 16:24:40  schroedn
 * HISTORY : New Feature - Preferences to change prefex in XMI
 * HISTORY :
 * HISTORY : Revision 1.6  2007/08/07 20:50:47  schroedn
 * HISTORY : New Feature, Primary Key and Lazy/Eager functions added to MMS
 * HISTORY :
 * HISTORY : Revision 1.5  2007/08/07 15:54:47  schroedn
 * HISTORY : New Feature, Primary Key and Lazy/Eager functions added to MMS
 * HISTORY :
 * HISTORY : Revision 1.4  2007/07/03 19:33:48  wangeug
 * HISTORY : initila loading hl7 code without "clone"
 * HISTORY :
 * HISTORY : Revision 1.3  2007/06/13 17:22:08  schroedn
 * HISTORY : removed functions
 * HISTORY :
 * HISTORY : Revision 1.2  2007/06/07 16:17:00  schroedn
 * HISTORY : Edits to sync with new codebase and java webstart
 * HISTORY : HISTORY : Revision 1.1
 * 2007/04/03 16:17:57 wangeug HISTORY : initial loading HISTORY : HISTORY :
 * Revision 1.14 2006/12/20 16:25:39 wuye HISTORY : Update HMB open file option
 * HISTORY : HISTORY : Revision 1.13 2006/12/12 16:53:39 wuye HISTORY : Comment
 * out System.out HISTORY : HISTORY : Revision 1.12 2006/11/15 06:28:21 wuye
 * HISTORY : added checking whether the mapping file is valide during open map
 * file process HISTORY : HISTORY : Revision 1.11 2006/11/14 15:24:16 wuye
 * HISTORY : Added validation funcationality HISTORY : HISTORY : Revision 1.10
 * 2006/11/10 14:43:41 wuye HISTORY : Disable the validate button on toolbar
 * HISTORY : HISTORY : Revision 1.9 2006/10/30 19:51:38 wuye HISTORY : Add a
 * dialog for hbm generation HISTORY : HISTORY : Revision 1.8 2006/10/30
 * 16:28:57 wuye HISTORY : Modified the Menu structure HISTORY : HISTORY :
 * Revision 1.7 2006/10/23 16:20:25 wuye HISTORY : Made changes to ignore
 * undragable node HISTORY : HISTORY : Revision 1.6 2006/10/20 21:31:28 wuye
 * HISTORY : Added annotate and hbm file generation funcationality HISTORY :
 * HISTORY : Revision 1.5 2006/10/10 17:13:25 wuye HISTORY : Added delete
 * funcationality HISTORY : HISTORY : Revision 1.2 2006/09/28 19:30:38 wuye
 * HISTORY : Removed classes that are not used HISTORY : HISTORY : Revision 1.1
 * 2006/09/26 15:47:48 wuye HISTORY : New object 2 database mapping panel
 * HISTORY :
 */
