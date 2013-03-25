/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.mapping.sdtm;

import gov.nih.nci.caadapter.common.*;
import gov.nih.nci.caadapter.common.csv.CSVMetaParserImpl;
import gov.nih.nci.caadapter.common.csv.CSVMetaResult;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.dataviewer.util.OpenDatabaseConnectionHelper;
import gov.nih.nci.caadapter.sdtm.ParseSDTMXMLFile;
import gov.nih.nci.caadapter.sdtm.SDTMMappingGenerator;
import gov.nih.nci.caadapter.sdtm.SDTMMetadata;
import gov.nih.nci.caadapter.sdtm.meta.QBTableMetaData;
import gov.nih.nci.caadapter.sdtm.meta.QueryBuilderMeta;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
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
import gov.nih.nci.caadapter.ui.mapping.sdtm.actions.OpenDataViewerHelper;
import gov.nih.nci.caadapter.ui.mapping.sdtm.actions.QBGetPasswordWindow;
import gov.nih.nci.caadapter.ui.mapping.sdtm.actions.QBTransformAction;
import gov.nih.nci.caadapter.ui.mapping.sdtm.actions.SdtmDropTransferHandler;
import gov.nih.nci.caadapter.hl7.map.impl.MappingImpl;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import org.jgraph.graph.DefaultGraphCell;

/**
 * The class is the main panel to construct the UI and initialize the utilities
 * to facilitate mapping functions.
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.2 revision $Revision: 1.26 $
 * @date       $Date: 2008-09-29 21:31:27 $
 */
public class Database2SDTMMappingPanel extends AbstractMappingPanel
{
    private static final String LOGID = "$RCSfile: Database2SDTMMappingPanel.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/sdtm/Database2SDTMMappingPanel.java,v 1.26 2008-09-29 21:31:27 phadkes Exp $";
    private static final String SELECT_SCS = "Open SCS file...";
    private static final String SELECT_TARGET = "Open SDTM definition file...";
    private SdtmDropTransferHandler sdtmTargetTreeDropTransferHandler = null;
    static ArrayList<String> _retArray = null;
    private String defineXMLLocation = null;
    private SDTMMappingGenerator sdtmMappingGenerator = null;
    private static String OPENDB = "Choose Database";
    private boolean connectDB = false;
    private JButton openTargetButton = null;
    private AbstractMainFrame _mainFrame = null;
    private Hashtable connectionParameters = null;
    private JButton _dbCon = null;
    private JButton openSCSButton = null;
    private boolean openDBmap = false;
    private Hashtable sqlQueries = null;
    private JButton transformBut = null;
    private JButton _commonBut = null;
    private boolean isDataBase = false;
    private boolean connectException = false;

    public JButton get_dbCon()
    {
        return _dbCon;
    }

    public JButton getOpenSCSButton()
    {
        return openSCSButton;
    }

    public JButton getTransFormBut()
    {
        return transformBut;
    }

    public static Hashtable getDomainFieldsList()
    {
        return domainFieldsList;
    }

    private static Hashtable domainFieldsList = null;

    public Database2SDTMMappingPanel(AbstractMainFrame mf)
    {
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setLayout(new BorderLayout());
        this.add(getCenterPanel(false), BorderLayout.CENTER);
        fileSynchronizer = new MappingFileSynchronizer(this);
        this._mainFrame = mf;
    }

    public Database2SDTMMappingPanel(AbstractMainFrame mf, String _conn)
    {
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setLayout(new BorderLayout());
        this._mainFrame = mf;
        try {
            this.add(getCenterPanel(false), BorderLayout.CENTER);
        } catch (Exception e) {
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        fileSynchronizer = new MappingFileSynchronizer(this);
    }

    public Hashtable getSqlQueries()
    {
        return sqlQueries;
    }

    public Database2SDTMMappingPanel(AbstractMainFrame mf, String _conn, boolean openDatabaseMap)
    {
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setLayout(new BorderLayout());
        this._mainFrame = mf;
        this.openDBmap = true;
        this.add(getCenterPanel(false), BorderLayout.CENTER);
        fileSynchronizer = new MappingFileSynchronizer(this);
        sqlQueries = new Hashtable();
        openDBmap = openDatabaseMap;
    }

    public Hashtable getConnectionParameters() throws SQLException
    {
        try {
            connectionParameters.put("connection", DBConnector.getConnection());
        } catch (Exception e) {
            try {
                connectionParameters.put("connection", DBConnector.getDBConnection(connectionParameters.get("URL").toString(), connectionParameters.get("Driver").toString(), connectionParameters.get("UserID").toString(), connectionParameters.get("PWD").toString()));
            } catch (Exception e1) {
                // e1.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
                JOptionPane.showMessageDialog(this, e1.getMessage().toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return connectionParameters;
    }

    public SDTMMappingGenerator getSDTMappingGenerator()
    {
        return sdtmMappingGenerator;
    }

    public JButton get_commonBut()
    {
        return _commonBut;
    }

    public AbstractMainFrame get_mainFrame()
    {
        return _mainFrame;
    }

    public JButton getTransformBut()
    {
        return transformBut;
    }

    public JButton getOpenTargetButton()
    {
        return openTargetButton;
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
        openSCSButton = new JButton(SELECT_SCS);
        sourceLocationPanel.add(openSCSButton, BorderLayout.EAST);
        openSCSButton.setMnemonic('O');
        openSCSButton.setToolTipText("Select SCS file...");
        openSCSButton.addActionListener(this);
        sourceButtonPanel.add(sourceLocationPanel, BorderLayout.NORTH);
        // sourceScrollPane = DefaultSettings.createScrollPaneWithDefaultFeatures();
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
        openTargetButton = new JButton(SELECT_TARGET);
        targetLocationPanel.add(openTargetButton, BorderLayout.EAST);
        openTargetButton.setMnemonic('T');
        openTargetButton.setToolTipText("Open Target Structure file");
        openTargetButton.addActionListener(this);
        targetButtonPanel.add(targetLocationPanel, BorderLayout.NORTH);
        // targetScrollPane = DefaultSettings.createScrollPaneWithDefaultFeatures();
        targetButtonPanel.add(targetScrollPane, BorderLayout.CENTER);
        targetButtonPanel.setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 5), (int) (Config.FRAME_DEFAULT_HEIGHT / 1.5)));
        // construct middle panel
        //JPanel centerFunctionPanel = new JPanel(new BorderLayout(2, 0));
        JPanel centerFunctionPanel = new JPanel(new BorderLayout());
        //JPanel centerFunctionPanel = new JPanel(new FlowLayout());
        JPanel middleContainerPanel = new JPanel(new BorderLayout());
        JLabel placeHolderLabel = new JLabel();
        placeHolderLabel.setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 3.5), 24));
        middlePanel = new MappingMiddlePanel(this);
        middlePanel.setKind("SDTM");
        sdtmMappingGenerator = new SDTMMappingGenerator();
        sdtmMappingGenerator.set_sdtmMappingGeneratorReference(sdtmMappingGenerator);
        middlePanel.setSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 3), (int) (Config.FRAME_DEFAULT_HEIGHT / 1.5)));
        _dbCon = new JButton("Choose Database");
        _dbCon.setActionCommand("Choose Database");
        _commonBut = new JButton("Data Viewer");
        _commonBut.setActionCommand("dataview");
        _commonBut.addActionListener(this);
        transformBut = new JButton("Transform");
        transformBut.setActionCommand("transform");
        transformBut.addActionListener(this);
        if (!openDBmap) {
            _commonBut.setEnabled(false);
            transformBut.setEnabled(false);
        }
        JPanel westpanel = new JPanel(new FlowLayout());
        westpanel.add(_dbCon);
        westpanel.add(_commonBut);
        //westpanel.add(transformBut);
        _dbCon.setPreferredSize(new Dimension(135, 19));
        transformBut.setPreferredSize(new Dimension(100, 19));
        _commonBut.setPreferredSize(new Dimension(110, 19));
        //_dbCon.setBounds(2,2,2,2);
        //centerFunctionPanel.add(westpanel, BorderLayout.CENTER);
//        centerFunctionPanel.add(_dbCon);
//        centerFunctionPanel.add(_commonBut);
//        centerFunctionPanel.add(transformBut);
        _dbCon.addActionListener(this);
        westpanel.setPreferredSize(new Dimension((int) (Config.FRAME_DEFAULT_WIDTH / 3.5), 24));
        //centerFunctionPanel.add(placeHolderLabel, BorderLayout.EAST);
        middleContainerPanel.add(westpanel, BorderLayout.NORTH);
        // middleContainerPanel.add(placeHolderLabel, BorderLayout.NORTH);
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
        if (openDBmap) {
            _dbCon.setEnabled(false);
            openTargetButton.setEnabled(false);
            openSCSButton.setEnabled(false);
        }
        return topCenterPanel;
    }

    public boolean isConnectDB()
    {
        return connectDB;
    }

    public boolean isOpenDBmap()
    {
        return openDBmap;
    }

    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();
        try {
            boolean everythingGood = true;
            if (SELECT_SCS.equals(command)) {
                _dbCon.setEnabled(false);
                File file = DefaultSettings.getUserInputOfFileFromGUI(this, ".scs", "Open SCS file ...", false, false);
                if (file != null) {
                    everythingGood = processOpenSourceTree(file, true, true);
                    openSCSButton.setEnabled(false);
                } else {
                    openSCSButton.setEnabled(true);
                    _dbCon.setEnabled(true);
                }
            } else if (SELECT_TARGET.equals(command)) {
                File file = DefaultSettings.getUserInputOfFileFromGUI(this, ".xml", "Open SDTM Structure file ...", false, false);
                if (file != null) {
                    buildTargetTree(null, file, true);
                    openTargetButton.setEnabled(false);
                } else {
                    openTargetButton.setEnabled(true);
                }
            } else if (OPENDB.equalsIgnoreCase(command)) {
                isDataBase = true;
                //new changes begin
                try {
                    connectDB = true;
                    openSCSButton.setEnabled(false);
                    OpenDatabaseConnectionHelper _openDatabaseConnectionHelper = new OpenDatabaseConnectionHelper(_mainFrame);
                    if (!_openDatabaseConnectionHelper.isCancelled()) {
                        connectionParameters = _openDatabaseConnectionHelper.getDatabaseConnectionInfo();
                        final Dialog _queryWaitDialog = new Dialog(_mainFrame);
                        new Thread(new Runnable()
                        {
                            public void run()
                            {
                                try {
                                    processOpenSourceTree(null, true, true);
                                    _queryWaitDialog.dispose();
                                } catch (Exception e1) {
                                    _queryWaitDialog.dispose();
                                    JOptionPane.showMessageDialog(_mainFrame, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                                    _dbCon.setEnabled(true);
                                }
                            }
                        }).start();
                        _queryWaitDialog.setTitle("Connection in Progress");
                        _queryWaitDialog.setSize(350, 100);
                        _queryWaitDialog.setLocationRelativeTo(null);
                        _queryWaitDialog.setLayout(new BorderLayout());
                        LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
                        JPanel _waitLabel = new JPanel();
                        _waitLabel.setBorder(lineBorder);
                        _waitLabel.add(new JLabel("      Connecting to database, Please wait ..."));
                        _queryWaitDialog.add(new JLabel("                       "), BorderLayout.NORTH);
                        _queryWaitDialog.add(_waitLabel, BorderLayout.CENTER);
                        _queryWaitDialog.setVisible(true);
                        openSCSButton.setEnabled(connectException);
                        _dbCon.setEnabled(connectException);
                    } else {
                        _dbCon.setEnabled(true);
                        openSCSButton.setEnabled(true);
                    }
                } catch (Exception ew) {
                    JOptionPane.showMessageDialog(_mainFrame, ew.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }

                // new changes end

//                final Dialog _queryWaitDialog = new Dialog(_mainFrame);
//                new Thread(new Runnable()
//                {
//                    public void run()
//                    {
//                    }
//                }).start();
                _dbCon.setEnabled(false);
            } else if (command.equalsIgnoreCase("Transform")) {
                try {
                    new QBTransformAction(_mainFrame, this, (Connection) connectionParameters.get("connection"));
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(this, e1.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (command.equalsIgnoreCase("dataview")) {
                /**
                 * open the helper to bypass another window
                 */
                new OpenDataViewerHelper(_mainFrame, this, getSaveFile(), transformBut, false).launchQueryBuilder();
            } else if (!everythingGood) {
                Message msg = MessageResources.getMessage("GEN3", new Object[0]);
                JOptionPane.showMessageDialog(this, msg.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e1) {
            DefaultSettings.reportThrowableToLogAndUI(this, e1, "", this, false, false);
        }
    }

    public boolean openDataBaseMapFileFromOpenMapFile(String params)
    {
        boolean retVal = false;
        try {
            QBGetPasswordWindow getPass = new QBGetPasswordWindow(_mainFrame, params, this.getSaveFile().toString());
            if (getPass.isResult()) {
                String pass = getPass.getPassword();
                EmptyStringTokenizer empt = new EmptyStringTokenizer(params, "~");
                connectDB = true;
                connectionParameters = new Hashtable();
                connectionParameters.put("URL", empt.getTokenAt(0));
                connectionParameters.put("UserID", empt.getTokenAt(2));
                connectionParameters.put("PWD", pass);
                connectionParameters.put("SCHEMA", empt.getTokenAt(3));
                connectionParameters.put("Driver", empt.getTokenAt(1));
                try {
                    connectionParameters.put("connection", DBConnector.getDBConnection(empt.getTokenAt(0), empt.getTokenAt(1), empt.getTokenAt(2), pass));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, e.getMessage().toString(), "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                processOpenSourceTree(null, true, true);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;
    }

    public void openDataBaseMapFile(String params)
    {
        try {
            QBGetPasswordWindow getPass = new QBGetPasswordWindow(_mainFrame, params, this.getSaveFile().toString());
            String pass = getPass.getPassword();
            EmptyStringTokenizer empt = new EmptyStringTokenizer(params, "~");
            connectDB = true;
            connectionParameters = new Hashtable();
            connectionParameters.put("URL", empt.getTokenAt(0));
            connectionParameters.put("UserID", empt.getTokenAt(2));
            connectionParameters.put("PWD", pass);
            connectionParameters.put("SCHEMA", empt.getTokenAt(3));
            connectionParameters.put("Driver", empt.getTokenAt(1));
            try {
                connectionParameters.put("connection", DBConnector.getDBConnection(empt.getTokenAt(0), empt.getTokenAt(1), empt.getTokenAt(2), pass));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage().toString(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            processOpenSourceTree(null, true, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected TreeNode loadSourceTreeData(Object metaInfo, File absoluteFile) throws Exception
    {
        if (!connectDB) {
            TreeNode node = new DefaultMutableTreeNode("SCS Model");
            // build source tree end
            SCMMapSourceNodeLoader scmMapSourceNodeLoader = new SCMMapSourceNodeLoader();
            node = scmMapSourceNodeLoader.loadData(metaInfo);
            return node;
        } else {
            TreeNode node = new DefaultMutableTreeNode("Data Model");
            return createSourceNodes(node, "");
        }
    }

    protected void buildSourceTree(Object metaInfo, File file, boolean isToResetGraph) throws Exception
    {
        super.buildSourceTree(metaInfo, file, isToResetGraph);
        if (connectDB) {
            sTree.setCellRenderer(new SourceRenderer());
            ToolTipManager.sharedInstance().registerComponent(sTree);
        }
        sTree.addTreeSelectionListener(middlePanel.getGraphController().getHighLighter());
    }

    public TreeNode createSourceNodes(TreeNode nodes, String flavor)
    {
        ArrayList ary = null;
        try {


            ary = DBConnector.getSchemaCollection(connectionParameters);




        } catch (Exception e) {
            JOptionPane.showMessageDialog(_mainFrame, "         " + e.getMessage().toString() + "                 ", "Could not connect to Database : " + connectionParameters.get("URL").toString(), JOptionPane.ERROR_MESSAGE);
            connectException = true;
            openSCSButton.setEnabled(true);
            _dbCon.setEnabled(true);
        }
        DefaultMutableTreeNode pNode = null;
        DefaultSourceTreeNode tableNode = null;
        if (ary != null) {
            for (int k = 0; k < ary.size(); k++) {
                if (ary.get(k) instanceof String) {
                    String _tmpStr = ary.get(k).toString();
                    String _str1 = _tmpStr.substring(3, _tmpStr.length());
                    pNode = new DefaultMutableTreeNode(_str1);
                    ((DefaultMutableTreeNode) nodes).add(pNode);
                } else if (ary.get(k) instanceof QBTableMetaData) {
                    tableNode = new DefaultSourceTreeNode(ary.get(k));
                    if (pNode != null)
                        pNode.add(tableNode);
                } else if (ary.get(k) instanceof QueryBuilderMeta) {
                    if (tableNode != null) {
                        tableNode.add(new DefaultSourceTreeNode(ary.get(k)));
                    }
                }
            }
        }
        return nodes;
    }

    public static ArrayList getDefaultTargetTreeNodeList()
    {
        return defaultTargetTreeNodeList;
    }

    private static ArrayList defaultTargetTreeNodeList;

    @SuppressWarnings("unchecked")
    public static void createTargetNodes(TreeNode nodes, File SDTMXmlFile)
    {
        defaultTargetTreeNodeList = new ArrayList();
        ParseSDTMXMLFile _parseSDTMFile = new ParseSDTMXMLFile(SDTMXmlFile.getAbsolutePath().toString());
        _retArray = _parseSDTMFile.getSDTMStructure();
        domainFieldsList = new Hashtable();
        DefaultMutableTreeNode pNode = null;
        String domainString = "";
        StringBuffer fieldsString = null;
        String _tempHolder;
        domainFieldsList = new Hashtable();
        for (int k = 0; k < _retArray.size(); k++) {
            if (_retArray.get(k).startsWith("KEY")) {
                if (fieldsString != null)
                    domainFieldsList.put(domainString, fieldsString.toString().substring(0, fieldsString.toString().lastIndexOf(',')));
                fieldsString = new StringBuffer();
                EmptyStringTokenizer _str = new EmptyStringTokenizer(_retArray.get(k), ",");
                pNode = new DefaultMutableTreeNode(_str.getTokenAt(1).substring(0, 2));
                ((DefaultMutableTreeNode) nodes).add(pNode);
                defaultTargetTreeNodeList.add(pNode);
                domainString = pNode.toString();
            } else {
                EmptyStringTokenizer _str = new EmptyStringTokenizer(_retArray.get(k), ",");
                _tempHolder = _str.getTokenAt(1);
                pNode.add(new DefaultTargetTreeNode(new SDTMMetadata(pNode.toString(), _tempHolder, _str.getTokenAt(2), _str.getTokenAt(3), _str.getTokenAt(4))));
                fieldsString.append(_tempHolder.substring(0, _tempHolder.indexOf('&')) + ",");
            }
        }
        //System.out.println("wait");
    }

    protected TreeNode loadTargetTreeData(Object metaInfo, File file) throws Exception
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
        tTree.setCellRenderer(new TargetRenderer());
        tTree.addTreeSelectionListener(middlePanel.getGraphController().getHighLighter());
        ToolTipManager.sharedInstance().registerComponent(tTree);
        sdtmTargetTreeDropTransferHandler = new SdtmDropTransferHandler(tTree, getMappingDataManager(), DnDConstants.ACTION_LINK, sdtmMappingGenerator);
    }

    /**
     * Called by actionPerformed() and overridable by descendant classes.
     *
     * @param file
     * @throws Exception
     */
    protected boolean processOpenSourceTree(File file, boolean isToResetGraph, boolean supressReportIssuesToUI) throws Exception
    {
        MetaObject metaInfo = null;
        BaseResult returnResult = null;
        MetaParser parser = null;
        if (!connectDB) {
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
        }
        buildSourceTree(metaInfo, file, isToResetGraph);
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
        buildTargetTree(null, file, isToResetGraph);
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
        new OpenSDTMMapFile(this, file.toString());
        MappingImpl newMappingImpl = new MappingImpl();
        newMappingImpl.setSourceComponent(null);
        newMappingImpl.setTargetComponent(null);
        middlePanel.getMappingDataManager().setMappingData(newMappingImpl);
        return null;
    }

    public Map getMenuItems(String menu_name)
    {
        Action action = null;
        ContextManager contextManager = ContextManager.getContextManager();
        Map<String, Action> actionMap = contextManager.getClientMenuActions(MenuConstants.DB_TO_SDTM, menu_name);
        if (MenuConstants.FILE_MENU_NAME.equals(menu_name)) {
            JRootPane rootPane = this.getRootPane();
            if (rootPane != null) {//rootpane is not null implies this panel is fully displayed;
                //on the flip side, if it is null, it implies it is under certain construction.
                contextManager.enableAction(ActionConstants.NEW_O2DB_MAP_FILE, false);
                //contextManager.enableAction(ActionConstants.OPEN_MAP_FILE, true);
            }
        }
        //since the action depends on the panel instance,
        //the old action instance should be removed
        if (actionMap != null)
            contextManager.removeClientMenuAction(MenuConstants.CSV_SPEC, menu_name, "");
        //              if (actionMap==null)
        //              {
        action = new gov.nih.nci.caadapter.ui.mapping.sdtm.actions.SaveSdtmAction(this, sdtmMappingGenerator);
        contextManager.addClientMenuAction(MenuConstants.DB_TO_SDTM, MenuConstants.FILE_MENU_NAME, ActionConstants.SAVE, action);
        contextManager.addClientMenuAction(MenuConstants.DB_TO_SDTM, MenuConstants.TOOLBAR_MENU_NAME, ActionConstants.SAVE, action);
        action.setEnabled(false);
        action = new gov.nih.nci.caadapter.ui.mapping.sdtm.actions.SaveAsSdtmAction(this, sdtmMappingGenerator);
        contextManager.addClientMenuAction(MenuConstants.DB_TO_SDTM, MenuConstants.FILE_MENU_NAME, ActionConstants.SAVE_AS, action);
        contextManager.addClientMenuAction(MenuConstants.DB_TO_SDTM, MenuConstants.TOOLBAR_MENU_NAME, ActionConstants.SAVE_AS, action);
        action.setEnabled(false);
        action = new gov.nih.nci.caadapter.ui.mapping.mms.actions.AnotateAction(this);
        contextManager.addClientMenuAction(MenuConstants.DB_TO_SDTM, MenuConstants.FILE_MENU_NAME, ActionConstants.ANOTATE, action);
        action.setEnabled(true);
        action = new gov.nih.nci.caadapter.ui.mapping.hl7.actions.ValidateMapAction(this);
        contextManager.addClientMenuAction(MenuConstants.DB_TO_SDTM, MenuConstants.FILE_MENU_NAME, ActionConstants.VALIDATE, action);
        contextManager.addClientMenuAction(MenuConstants.DB_TO_SDTM, MenuConstants.TOOLBAR_MENU_NAME, ActionConstants.VALIDATE, action);
        action.setEnabled(true);
        action = new gov.nih.nci.caadapter.ui.mapping.sdtm.actions.SDTMCloseAction(this);
        contextManager.addClientMenuAction(MenuConstants.DB_TO_SDTM, MenuConstants.FILE_MENU_NAME, ActionConstants.CLOSE, action);
        action.setEnabled(true);
        action = new gov.nih.nci.caadapter.ui.mapping.sdtm.actions.GenerateSDTMReportAction(this);
        contextManager.addClientMenuAction(MenuConstants.DB_TO_SDTM, MenuConstants.REPORT_MENU_NAME, ActionConstants.GENERATE_REPORT, action);
        contextManager.addClientMenuAction(MenuConstants.DB_TO_SDTM, MenuConstants.TOOLBAR_MENU_NAME, ActionConstants.GENERATE_REPORT, action);
        action.setEnabled(true);
        action = new gov.nih.nci.caadapter.ui.mapping.hl7.actions.RefreshMapAction(this);
        contextManager.addClientMenuAction(MenuConstants.DB_TO_SDTM, MenuConstants.TOOLBAR_MENU_NAME, ActionConstants.REFRESH, action);
        action.setEnabled(true);
        actionMap = contextManager.getClientMenuActions(MenuConstants.DB_TO_SDTM, menu_name);
        //              }
        return actionMap;
    }

    /**
     * return the open action inherited with this client.
     */
    public Action getDefaultOpenAction()
    {
        ContextManager contextManager = ContextManager.getContextManager();
        return contextManager.getDefinedAction(ActionConstants.OPEN_CSV2SDTM_MAP_FILE_TXT);
    }

    /**
     * Explicitly reload information from the internal given file.
     *
     * @throws Exception
     */
    public void reload() throws Exception
    {
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
    public void reload(Map<MappingFileSynchronizer.FILE_TYPE, File> changedFileMap)
    {
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

    public String getDefineXMLLocation()
    {
        return defineXMLLocation;
    }

    public void setDefineXMLLocation(String defineXMLLocation)
    {
        this.defineXMLLocation = defineXMLLocation;
    }

    public TreeNode getTargetNodes()
    {
        if (tTree != null)
            return tTree.getRootTreeNode();
        else
            return null;
    }

    public TreeNode getSourceNodes()
    {
        if (sTree != null)
            return sTree.getRootTreeNode();
        else
            return null;
    }

    public SDTMMappingGenerator getSdtmMappingGenerator()
    {
        return sdtmMappingGenerator;
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     */
    protected static ImageIcon createImageIcon(String path)
    {
        //java.net.URL imgURL = Database2SDTMMappingPanel.class.getResource(path);
        java.net.URL imgURL = DefaultSettings.class.getClassLoader().getResource("images/" + path);
        if (imgURL != null) {
            //System.out.println("class.getResource is "+imgURL.toString());
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + imgURL.toString() + " & " + path);
            return null;
        }
    }

    private class SourceRenderer extends DefaultTreeCellRenderer
    {
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
        {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            ImageIcon tutorialIcon;
            try {
                String _tmpStr = (String) ((DefaultMutableTreeNode) value).getUserObject();
                if (_tmpStr.equalsIgnoreCase("Data Model")) {
                    tutorialIcon = createImageIcon("database.png");
                    setIcon(tutorialIcon);
                    setToolTipText("Data model");
                } else {
                    tutorialIcon = createImageIcon("schema.png");
                    setIcon(tutorialIcon);
                    setToolTipText("Schema");
                }
                return this;
            } catch (Exception e) {
                //continue;
            }
            try {
                QBTableMetaData qbTableMetaData = (QBTableMetaData) ((DefaultSourceTreeNode) value).getUserObject();
                if (qbTableMetaData.getType().equalsIgnoreCase("TABLE")) {
                    tutorialIcon = createImageIcon("table.png");
                    setIcon(tutorialIcon);
                    setToolTipText("Table");
                } else if (qbTableMetaData.getType().equalsIgnoreCase("VIEW")) {
                    tutorialIcon = createImageIcon("view.png");
                    setIcon(tutorialIcon);
                    setToolTipText("View");
                }
            } catch (ClassCastException e) {
                try {
                    QueryBuilderMeta queryBuilderMeta = (QueryBuilderMeta) ((DefaultSourceTreeNode) value).getUserObject();
                    setToolTipText("Column ; Type= " + queryBuilderMeta.getTypeName() + " ; Size= " + queryBuilderMeta.getColumnSize());
                    tutorialIcon = createImageIcon("column.png");
                    setIcon(tutorialIcon);
                } catch (Exception ee) {
                    try {
                        //String queryBuilderMeta = (String) ((DefaultSourceTreeNode) value).getUserObject();
                        tutorialIcon = createImageIcon("load.gif");
                        setIcon(tutorialIcon);
                    } catch (Exception e1) {
                        setToolTipText(null);
                    }
                }
            }
            return this;
        }
    }

    private class TargetRenderer extends DefaultTreeCellRenderer
    {
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
        {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            ImageIcon tutorialIcon;
            try {
                String _tmpStr = (String) ((DefaultMutableTreeNode) value).getUserObject();
                if (!_tmpStr.equalsIgnoreCase("SDTM Data Structure")) {
                    tutorialIcon = createImageIcon("book.png");
                    setIcon(tutorialIcon);
                    setToolTipText(_tmpStr);
                } else {
                    tutorialIcon = createImageIcon("house.png");
                    setIcon(tutorialIcon);
                    setToolTipText("SDTM Root");
                }
            } catch (Exception e) {
                SDTMMetadata sdtmMetadata = (SDTMMetadata) ((DefaultTargetTreeNode) value).getUserObject();
                setToolTipText(sdtmMetadata.getName() + "; isMandatory " + sdtmMetadata.getMandatory());
                tutorialIcon = createImageIcon("blue.png");
                setIcon(tutorialIcon);
            }
            return this;
        }
    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.25  2008/06/09 19:54:06  phadkes
 * New license text replaced for all .java files.
 *
 * Revision 1.24  2007/11/05 15:41:58  jayannah
 * Changed the message/wording
 *
 * Revision 1.23  2007/10/19 17:49:04  jayannah
 * Changes to add link selection highlighter for the map file
 *
 * Revision 1.22  2007/10/18 20:16:22  jayannah
 * -Added a new method in MappingMiddlePanel to get the reference to MiddlePanelJGraphController
 * -Added a new method in MiddlePanelJGraphController to get the reference to linkselectionhighlighter
 * -Added linkselectionhighlighter to source and targe trees as tree selection listener
 *
 * Revision 1.21  2007/10/18 19:13:39  jayannah
 * Changes to highlist the target node when the source is selected and vice versa
 *
 * Revision 1.20  2007/09/13 14:41:38  jayannah
 * took care of enabling the connect database button when there is a connect exception
 *
 * Revision 1.19  2007/09/13 13:51:41  jayannah
 * Changes made to ensure that flow is correct, the save , reopen etc
 *
 * Revision 1.18  2007/09/11 15:31:42  jayannah
 * made changes to pass the instance of the entire panel when the data viewer is opened
 *
 * Revision 1.17  2007/08/31 21:22:27  jayannah
 * commented sysouts
 *
 * Revision 1.16  2007/08/29 21:26:52  jayannah
 * fixed bugs to gracelfully cancel during times when database open is cancelled by the user and proper enabling and disabling of the buttons
 *
 * Revision 1.15  2007/08/29 21:01:00  jayannah
 * Made sure that the buttons on the mapping panel are correctly disabled and enables depending on the which map file is opened
 *
 * Revision 1.14  2007/08/29 14:46:48  jayannah
 * enabled the buttons for SCS file and Choose database if the user cancels during choosing a SCS file
 *
 * Revision 1.13  2007/08/22 14:27:06  jayannah
 * show transform button
 *
 * Revision 1.12  2007/08/17 20:12:05  jayannah
 * added a new image for QBAddButtons
 * Reduced the height for viewing the 3 buttons in the center panel
 *
 * Revision 1.11  2007/08/17 15:55:52  jayannah
 * Reformatted, the source file and enabled the button when the user cancelled the action
 *
 * Revision 1.10  2007/08/16 19:39:45  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */
