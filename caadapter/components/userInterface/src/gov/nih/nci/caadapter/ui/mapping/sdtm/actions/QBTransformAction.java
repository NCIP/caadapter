package gov.nih.nci.caadapter.ui.mapping.sdtm.actions;

import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
import gov.nih.nci.caadapter.dataviewer.util.GetConnectionSingleton;
import gov.nih.nci.caadapter.dataviewer.util.QBParseMappingFile;
import gov.nih.nci.caadapter.dataviewer.util.QBTransformHelper;
import gov.nih.nci.caadapter.sdtm.ParseSDTMXMLFile;
import gov.nih.nci.caadapter.sdtm.SDTMMetadata;
import gov.nih.nci.caadapter.sdtm.util.CSVMapFileReader;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.tree.DefaultTargetTreeNode;
import gov.nih.nci.caadapter.ui.main.MainMenuBar;
import gov.nih.nci.caadapter.ui.mapping.sdtm.DBConnector;
import gov.nih.nci.caadapter.ui.mapping.sdtm.Database2SDTMMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.sdtm.RDSFixedLenghtInput;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * The class helps as a helper during transformation
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: jayannah $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.10 $
 *          $Date: 2007-08-29 20:41:12 $
 */
public class QBTransformAction {
    JFileChooser directoryLoc, saveXLSLocation = null;
    File directory = null;
    private Connection con = null;
    HashMap fixedLengthRecords = null;
    boolean fixedLengthIndicator = false;
    Hashtable sqlAsColumnMap = null;

    /*
        This constructor is used by the mapping panel
     */
    public QBTransformAction(AbstractMainFrame _mainFrame, final Database2SDTMMappingPanel mappingPanel, Connection _con) throws Exception {
        //this(_mainFrame, mappingPanel, "");
        this.con = _con;
        directoryLoc = new JFileChooser(System.getProperty("user.dir"));
        directoryLoc.setDialogTitle("Transforming file " + mappingPanel.getSaveFile().getName() + ", please choose directory..");
        directoryLoc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = directoryLoc.showOpenDialog(_mainFrame);
        if (((String) MainMenuBar.getCaAdapterPreferences().get("FIXED_LENGTH_VAR")).equalsIgnoreCase("Fixed")) {
            fixedLengthIndicator = true;
            CSVMapFileReader csvMapFileReader = new CSVMapFileReader(new File(mappingPanel.getSaveFile().getAbsolutePath()));
            //Prepare the list here and keep it ready so that number of blanks corresponding to the
            //value set by the user will be applied appropriately
            RDSFixedLenghtInput rdsFixedLenghtInput = new RDSFixedLenghtInput(_mainFrame, csvMapFileReader.getTargetKeyList());
            fixedLengthRecords = rdsFixedLenghtInput.getUserValues();
        }
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            directory = directoryLoc.getSelectedFile();
            try {
                //a wait dialog window
                final Dialog queryWaitDialog = new Dialog(_mainFrame);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            processTransform4SQLStatments(mappingPanel.getSaveFile().getAbsolutePath(), mappingPanel.getDefineXMLLocation(), directory.getAbsolutePath().toString());
                            queryWaitDialog.dispose();
                        } catch (Exception e) {
                            if (queryWaitDialog != null)
                                queryWaitDialog.dispose();
                            JOptionPane.showMessageDialog(mappingPanel, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        } finally {
                            // todo: Do not close connection here, close only when the panel closes
                            //GetConnectionSingleton.closeConnection();
                            System.gc(); //call garbage collector just in case to wipe out the references
                        }
                    }
                }).start();
                queryWaitDialog.setTitle("Transforming file " + mappingPanel.getSaveFile().getAbsolutePath() + "in Progress");
                queryWaitDialog.setSize(350, 100);
                queryWaitDialog.setLocation(450, 450);
                queryWaitDialog.setLayout(new BorderLayout());
                LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
                JPanel waitLabel = new JPanel();
                waitLabel.setBorder(lineBorder);
                waitLabel.add(new JLabel("      Transformation in progress, Please wait ..."));
                queryWaitDialog.add(new JLabel("                       "), BorderLayout.NORTH);
                queryWaitDialog.add(waitLabel, BorderLayout.CENTER);
                queryWaitDialog.setVisible(true);
                //wait dialog window needs to be destroyed
            } catch (Exception e) {
                throw e;
            }
        }
    }

    /*
        This constructor is used by the menu
     */
    public QBTransformAction(final AbstractMainFrame _mainFrame, final String mapFile, final String defineXMLocation, Connection _con) throws Exception {
        //process _con since it is always null
        if (_con == null) {
            String params = getDBParams(mapFile);
            _con = getConnection(_mainFrame, params, mapFile);
        }
        this.con = _con;
        directoryLoc = new JFileChooser(System.getProperty("user.dir"));
        directoryLoc.setDialogTitle("Transforming file " + mapFile + ", please choose directory..");
        directoryLoc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = directoryLoc.showOpenDialog(_mainFrame);
        if (((String) MainMenuBar.getCaAdapterPreferences().get("FIXED_LENGTH_VAR")).equalsIgnoreCase("Fixed")) {
            fixedLengthIndicator = true;
            CSVMapFileReader csvMapFileReader = new CSVMapFileReader(new File(mapFile));
            //Prepare the list here and keep it ready so that number of blanks corresponding to the
            //value set by the user will be applied appropriately
            RDSFixedLenghtInput rdsFixedLenghtInput = new RDSFixedLenghtInput(_mainFrame, csvMapFileReader.getTargetKeyList());
            fixedLengthRecords = rdsFixedLenghtInput.getUserValues();
        }
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            directory = directoryLoc.getSelectedFile();
            try {
                //a wait dialog window
                final Dialog queryWaitDialog = new Dialog(_mainFrame);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            processTransform4SQLStatments(mapFile, defineXMLocation, directory.getAbsolutePath().toString());
                            queryWaitDialog.dispose();
                        } catch (Exception e) {
                            queryWaitDialog.dispose();
                            JOptionPane.showMessageDialog(_mainFrame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        } finally {
                            GetConnectionSingleton.closeConnection(); // needs to be closed since this is being called from menu
                            System.gc();
                        }
                    }
                }).start();
                queryWaitDialog.setTitle("Transforming file " + mapFile + "in Progress");
                queryWaitDialog.setSize(350, 100);
                queryWaitDialog.setLocation(450, 450);
                queryWaitDialog.setLayout(new BorderLayout());
                LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
                JPanel waitLabel = new JPanel();
                waitLabel.setBorder(lineBorder);
                waitLabel.add(new JLabel("      Transformation in progress, Please wait ..."));
                queryWaitDialog.add(new JLabel("                       "), BorderLayout.NORTH);
                queryWaitDialog.add(waitLabel, BorderLayout.CENTER);
                queryWaitDialog.setVisible(true);
                //wait dialog window needs to be destroyed
            } catch (Exception e) {
                throw e;
            }
        }
    }

    public static Hashtable getAllFieldsForDomains(File SDTMXmlFile) {
        ParseSDTMXMLFile _parseSDTMFile = new ParseSDTMXMLFile(SDTMXmlFile.getAbsolutePath().toString());
        ArrayList _retArray = _parseSDTMFile.getSDTMStructure();
        Hashtable domainFieldsList = new Hashtable();
        DefaultMutableTreeNode pNode = null;
        String domainString = "";
        ArrayList fieldsString = null;
        String _tempHolder;
        domainFieldsList = new Hashtable();
        for (int k = 0; k < _retArray.size(); k++) {
            if (_retArray.get(k).toString().startsWith("KEY")) {
                if (fieldsString != null)
                    domainFieldsList.put(domainString, fieldsString);
                fieldsString = new ArrayList();
                EmptyStringTokenizer _str = new EmptyStringTokenizer(_retArray.get(k).toString(), ",");
                pNode = new DefaultMutableTreeNode(_str.getTokenAt(1).substring(0, 2));
                domainString = pNode.toString();
            } else {
                EmptyStringTokenizer _str = new EmptyStringTokenizer(_retArray.get(k).toString(), ",");
                _tempHolder = _str.getTokenAt(1);
                pNode.add(new DefaultTargetTreeNode(new SDTMMetadata(pNode.toString(), _tempHolder, _str.getTokenAt(2), _str.getTokenAt(3), _str.getTokenAt(4))));
                if (!_tempHolder.startsWith(domainString))
                    _tempHolder = domainString + "." + _tempHolder;
                fieldsString.add(_tempHolder.substring(0, _tempHolder.indexOf('&')));
            }
        }
        return domainFieldsList;
    }

    public void processTransform4SQLStatments(String savedMapFile, String defineXML, String xlsFile) throws Exception {
        try {
            QBParseMappingFile qb = new QBParseMappingFile(new File(savedMapFile));
            Hashtable tempTable = getAllFieldsForDomains(new File(defineXML));
            //prepare the column AS columnname list
            QBTransformHelper qbTransformHelper = new QBTransformHelper(qb.getHashSQLfromMappings());
            this.sqlAsColumnMap = qbTransformHelper.getSqlColumnNames();
            Iterator _iter = qb.getHashSQLfromMappings().keySet().iterator();
            while (_iter.hasNext()) {
                //get the domain name
                String domainName = _iter.next().toString();
                //create a file with domain name
                FileWriter fstream = new FileWriter(xlsFile + "\\" + domainName + ".txt");
                BufferedWriter out = new BufferedWriter(fstream);
                //get the query and fire it
                String query = qb.getHashSQLfromMappings().get(domainName);
                //System.out.println("Query is " + query);
                ResultSet rs = con.createStatement().executeQuery(query);
                //get all the columns for the 'domainName'
                ArrayList columns = qb.getHashTableTransform().get(domainName);
                int sizeOfDomain = ((ArrayList)tempTable.get(domainName)).size();
                out.write(tempTable.get(domainName).toString().substring(1, tempTable.get(domainName).toString().indexOf(']')));
                ArrayList domainHeader = (ArrayList) tempTable.get(domainName);
                //compute the number of commas for each mapped columnvalue and set if the retrieved value is
                while (rs.next()) {// each row begins here
                    try {
                        int sizeOfEachRow = ((ArrayList) tempTable.get(domainName)).size();
                        ArrayList _tempArray = new ArrayList(sizeOfEachRow + 1);
                        for (int j = 0; j < (sizeOfEachRow + 1); j++) {
                            _tempArray.add(" ");//add empty buffer
                        }
                        for (int i = 0; i < columns.size(); i++) {
                            EmptyStringTokenizer emt = new EmptyStringTokenizer(columns.get(i).toString(), "~");
                            EmptyStringTokenizer getColumn = new EmptyStringTokenizer(emt.nextToken(), ".");
                            String testString = getColumn.getTokenAt(1) + "_" + getColumn.getTokenAt(2);
                            /*
                               Read the columnames hashmap and obtain the right 'AS' value to obtain the data from the result set
                             */
                            String customColName = fetchCustColNameFromSQLMap(domainName, testString);
                            String _dataStr = rs.getString(customColName);
                            String empt1 = emt.nextToken().toString();
                            int position = (new Integer(((ArrayList) tempTable.get(domainName)).indexOf(empt1)));
                            if (fixedLengthRecords != null && fixedLengthRecords.containsKey(empt1)) {
                                try {
                                    _tempArray = implementFixedRec(position, _dataStr, _tempArray, new Integer(fixedLengthRecords.get(empt1).toString()).intValue());
                                } catch (Exception e) {
                                    String runTimePrp = System.getProperty("debug", "false");
                                    if (new Boolean(runTimePrp))
                                        System.out.println("Problem for target field \"" + empt1 + "\" at position \"" + position + "\" and date was \"" + _dataStr + "\"");
                                }
                            } else {
                                _tempArray.remove(position);
                                try {
                                    _tempArray.add(position, _dataStr);
                                } catch (Exception e) {
                                    System.out.println("error at " + position);
                                    throw e;
                                }
                            }
                        }
                        int index = _tempArray.size();
                        _tempArray.remove(index - 1);
                        out.write("\n" + _tempArray.toString().substring(1, _tempArray.toString().indexOf("]")));
                        //out.write("\n" + _tempArray.toString().substring(1, sizeOfEachRow));
                    } catch (Exception e) {
                        e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
                        continue;
                    }
                }// result set
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private ArrayList implementFixedRec(int position, String srcData, ArrayList _tempArray, int fixedsize) throws Exception {
        StringBuffer _setSize;
        if (!(srcData.length() > fixedsize)) {
            int finalSize = fixedsize - srcData.length();
            _setSize = new StringBuffer();
            _setSize.append(srcData);
            for (int i = 0; i < finalSize; i++) {
                _setSize.append(" ");
            }
            _tempArray.add(position, _setSize.toString());
            _tempArray.remove(position+1);
        } else {
            _tempArray.add(position, srcData.substring(0, fixedsize));
            _tempArray.remove(position+1);
        }
         return _tempArray;
    }

    private String fetchCustColNameFromSQLMap(String domainName, String colName) {
        HashMap tempMap = (HashMap) sqlAsColumnMap.get(domainName);
        return (String) tempMap.get(colName);
    }

    private Connection getConnection(AbstractMainFrame _mainFrame, String params, String mapFile) {
        Connection conn;
        QBGetPasswordWindow getPass = new QBGetPasswordWindow(_mainFrame, params, mapFile.toString());
        String pass = getPass.getPassword();
        EmptyStringTokenizer empt = new EmptyStringTokenizer(params, "~");
        try {
            conn = DBConnector.getDBConnection(empt.getTokenAt(0), empt.getTokenAt(1), empt.getTokenAt(2), pass);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(_mainFrame, e.getMessage().toString() + "\n\n Please restart the application", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return conn;
    }

    private String getDBParams(String mapFileName) {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(mapFileName));
            System.out.println("Root element of the doc is " + doc.getDocumentElement().getNodeName());
            NodeList compLinkNodeList = doc.getElementsByTagName("components");
            String _xmlFileName = "";
            for (int s = 0; s < compLinkNodeList.getLength(); s++) {
                Node node = compLinkNodeList.item(s);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element firstCompElement = (Element) node;
                    NodeList targetNode = firstCompElement.getElementsByTagName("component");
                    Element targetName1 = (Element) targetNode.item(0);
                    targetName1.getAttribute("location").toString();
                    if (targetName1.getAttribute("kind").toString().equalsIgnoreCase("SCS")) {
                        // _scsFileName = targetName1.getAttribute("location").toString();
                    } else if (targetName1.getAttribute("kind").toString().equalsIgnoreCase("Database")) {
                        return targetName1.getAttribute("param").toString();
                    }
                    Element targetName2 = (Element) targetNode.item(1);
                    if (targetName2.getAttribute("kind").toString().equalsIgnoreCase("XML")) {
                        _xmlFileName = targetName2.getAttribute("location").toString();
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.9  2007/08/29 19:35:37  jayannah
 * fixed a bug which was adding a extra comma at the end and also when the specified fixed length was lesser than the length of the data, the data must be truncated to honor the specified length
 *
 * Revision 1.8  2007/08/17 15:16:30  jayannah
 * added wait window during transformation
 *
 * Revision 1.7  2007/08/16 19:39:45  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */
