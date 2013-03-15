/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.mapping.sdtm.actions;

import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.dataviewer.util.GetConnectionSingleton;
import gov.nih.nci.caadapter.dataviewer.util.QBParseMappingFile;
import gov.nih.nci.caadapter.dataviewer.util.QBTransformHelper;
import gov.nih.nci.caadapter.sdtm.ParseSDTMXMLFile;
import gov.nih.nci.caadapter.sdtm.SDTMMetadata;
import gov.nih.nci.caadapter.sdtm.util.CSVMapFileReader;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.CaadapterFileFilter;
import gov.nih.nci.caadapter.ui.common.tree.DefaultTargetTreeNode;
import gov.nih.nci.caadapter.ui.main.MainFrame;
import gov.nih.nci.caadapter.ui.mapping.sdtm.DBConnector;
import gov.nih.nci.caadapter.ui.mapping.sdtm.Database2SDTMMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.sdtm.RDSFixedLenghtInput;
import gov.nih.nci.caadapter.ui.mapping.sdtm.RDSHelper;

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
import java.io.*;
import java.net.URL;
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
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.24 $
 *          $Date: 2008-12-10 15:37:27 $
 */
public class QBTransformAction {
    JFileChooser directoryLoc, saveXLSLocation = null;
    private AbstractMainFrame frame = null;
    File directory = null;
    private Connection con = null;
    HashMap fixedLengthRecords = null;
    JFileChooser scsFile = null;

    boolean fixedLengthIndicator = false;
    Hashtable sqlAsColumnMap = null;

    public QBTransformAction() {
    }

    public void transformDB(String mapFile, Connection con, String dirLocation) {
        if (con == null) {
            return;
        }
        try {
        	String targetFile= RDSHelper.getDefineXMLNameFromMapFile(mapFile);
        	this.con = con;
            processTransform4SQLStatments(mapFile, targetFile, dirLocation);
            System.out.println("created the txt files at " + dirLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*

       This constructor is used by the mapping panel

    */
    public QBTransformAction(final AbstractMainFrame _mainFrame, final Database2SDTMMappingPanel mappingPanel, Connection _con) throws Exception {
        //this(_mainFrame, mappingPanel, "");
        this.con = _con;
        directoryLoc = new JFileChooser(System.getProperty("user.dir"));
        //directoryLoc.setDialogTitle("Transforming file " + mappingPanel.getSaveFile().getName() + ", please choose directory..");
        directoryLoc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = directoryLoc.showOpenDialog(_mainFrame);
        if (((String) CaadapterUtil.getCaAdapterPreferences().get("FIXED_LENGTH_VAR")).equalsIgnoreCase("Fixed")) {
            fixedLengthIndicator = true;
            CSVMapFileReader csvMapFileReader = new CSVMapFileReader(new File(mappingPanel.getSaveFile().getAbsolutePath()));
            //Prepare the list here and keep it ready so that number of blanks corresponding to the
            //value set by the user will be applied appropriately
            RDSFixedLenghtInput rdsFixedLenghtInput = new RDSFixedLenghtInput(_mainFrame, csvMapFileReader.getTargetKeyList());
            fixedLengthRecords = rdsFixedLenghtInput.getUserValues();
        }
        directoryLoc.setDialogTitle("Please choose the directory to save text files? ");
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
                            JOptionPane.showMessageDialog(_mainFrame, "Transformation was successful, TXT files were created in  \"" + directory + "\" directory", "Transfomation...", JOptionPane.INFORMATION_MESSAGE);
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
                queryWaitDialog.setTitle("Transforming file " + mappingPanel.getSaveFile().getName() + "in Progress");
                queryWaitDialog.setSize(450, 300);
                queryWaitDialog.setLocation(450, 450);
                queryWaitDialog.setLocationRelativeTo(null);
                queryWaitDialog.setLayout(new BorderLayout());
                LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
                JPanel waitLabel = new JPanel();
                waitLabel.setBorder(lineBorder);
                waitLabel.add(new JLabel("      Transformation in progress, Please waitdd ..."));
                //
                JLabel imageIcon = new JLabel(getWaitButton("animated"));
                queryWaitDialog.add(imageIcon, BorderLayout.NORTH);
                queryWaitDialog.add(waitLabel, BorderLayout.CENTER);
                ///queryWaitDialog.pack();
                queryWaitDialog.setVisible(true);
                //wait dialog window needs to be destroyed
            } catch (Exception e) {
                throw e;
            }
        }
    }

    public static ImageIcon getWaitButton(String imageName) {
        String imgLocation = "/images/_" + imageName + ".gif";
        URL imageURL = null;
        try {
            imageURL = MainFrame.class.getResource(imgLocation);
            return new ImageIcon(imageURL);
        } catch (Exception e) {
            System.out.println("Unable to find image _" + imageName);
            e.printStackTrace();
        }
        return null;
    }

    /*
       This constructor is used by the menu
    */
    public QBTransformAction(final AbstractMainFrame _mainFrame, final String mapFile, final String defineXMLocation, Connection _con) throws Exception {
        //process _con since it is always null
        if (_con == null) {
            String params = getDBParams(mapFile);
            _con = getConnection(_mainFrame, params, mapFile);
            // the user really Cancelled! BAH!
            if (_con == null) {
                return;
            }
        }
        this.con = _con;
        this.frame = _mainFrame;
        directoryLoc = new JFileChooser(System.getProperty("user.dir"));
        directoryLoc.setDialogTitle("Transforming file " + mapFile + ", please choose directory..");
        directoryLoc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = directoryLoc.showOpenDialog(_mainFrame);
        try {
            if (((String) CaadapterUtil.getCaAdapterPreferences().get("FIXED_LENGTH_VAR")).equalsIgnoreCase("Fixed")) {
                fixedLengthIndicator = true;
                CSVMapFileReader csvMapFileReader = new CSVMapFileReader(new File(mapFile));
                //Prepare the list here and keep it ready so that number of blanks corresponding to the
                //value set by the user will be applied appropriately
                RDSFixedLenghtInput rdsFixedLenghtInput = new RDSFixedLenghtInput(_mainFrame, csvMapFileReader.getTargetKeyList());
                fixedLengthRecords = rdsFixedLenghtInput.getUserValues();
            }
        } catch (Exception e) {
            // the .preferences file was not found
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
                            JOptionPane.showMessageDialog(_mainFrame, "Transformation is successful, Please check the text files at "+directory.getAbsolutePath(), "Transformation", JOptionPane.INFORMATION_MESSAGE);
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
                queryWaitDialog.setSize(400, 150);
                queryWaitDialog.setLocation(450, 300);
                queryWaitDialog.setLayout(new BorderLayout());
                LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
                JPanel masterPane = new JPanel();
                masterPane.setLayout(new BorderLayout());
                //
                JPanel waitLabel = new JPanel();
                //waitLabel.setBorder(lineBorder);
                JLabel _jl = new JLabel("      Transformation in progress, Please wait ...");
                waitLabel.add(_jl);
                _jl.setFont(new Font("SansSerif", Font.BOLD, 12));
                queryWaitDialog.add(new JLabel("                       "), BorderLayout.NORTH);
                //
                JLabel imageIcon = new JLabel(getWaitButton("animated"));
                //
                masterPane.add(imageIcon, BorderLayout.NORTH);
                masterPane.add(waitLabel, BorderLayout.CENTER);
                masterPane.setBorder(lineBorder);
                //
                queryWaitDialog.add(masterPane);
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
            try {
                defineXML = FileUtil.fileLocateOnClasspath(defineXML);
            } catch (FileNotFoundException e) {
                if(frame !=null)
                    defineXML = getAltDefineXMLFile(defineXML);
                else
                    System.out.println(defineXML +" is not found, please check and try again");
            }
            Hashtable tempTable = getAllFieldsForDomains(new File(defineXML));
            if (qb.getHashSQLfromMappings().size() == 0) {
                if (frame != null)
                    throw new Exception("The "+defineXML+" is missing SQL statements; please save the SQL's and try again!!");
                    //JOptionPane.showMessageDialog(frame, "The "+defineXML+" not found, Please save the SQL statements and try again", "File not found", JOptionPane.INFORMATION_MESSAGE);
                else
                    System.out.println("The map file is missing SQL statments; please save the map with SQL and run again");
            }
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
                int sizeOfDomain = ((ArrayList) tempTable.get(domainName)).size();
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
            _tempArray.remove(position + 1);
        } else {
            _tempArray.add(position, srcData.substring(0, fixedsize));
            _tempArray.remove(position + 1);
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
            //System.out.println("Root element of the doc is " + doc.getDocumentElement().getNodeName());
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

    private String getAltDefineXMLFile(String _xmlFileName) {
        CaadapterFileFilter filter = new CaadapterFileFilter();
        filter.addExtension("xml");
        String _defaultLoc = FileUtil.getWorkingDirPath() + File.separator + "workingspace" + File.separator + "RDS_Example";
        directoryLoc = new JFileChooser(_defaultLoc);
        //directoryLoc.setDialogTitle("Please select the define.xml file ?");
        //this.setTitle(_xmlFileName + " not found! Please choose a different file");
        //directoryLoc.setDialogTitle(_scsFileName+" not found! Please choose a different file");
        scsFile = new JFileChooser(_defaultLoc);
        // filter.setDescription("map");
        scsFile.setFileFilter(filter);
        scsFile.setDialogTitle("Please select the define.xml file...");
        int returnVal = scsFile.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return scsFile.getSelectedFile().getAbsolutePath();

        } else {
            return null;
        }

    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.23  2008/06/09 19:54:06  phadkes
 * New license text replaced for all .java files.
 *
 * Revision 1.22  2007/11/16 19:17:42  wangeug
 * create db_TO_RDS testing program
 *
 * Revision 1.21  2007/10/18 21:08:17  jayannah
 * Added a transformation confirmation message after the transformation is complete
 *
 * Revision 1.20  2007/10/17 20:52:38  jayannah
 * Added a message dialog when the SQLs are not saved
 *
 * Revision 1.19  2007/10/16 14:10:27  jayannah
 * Changed the absolute path to getName during times when the pop up is displayed to the world;
 * made changes so that the Tables cannot be mapped
 *
 * Revision 1.18  2007/10/15 19:49:32  jayannah
 * Added a public API for the transformation of the CSV and DB in order to be compliant with the caCore.
 *
 * Revision 1.17  2007/10/11 19:45:43  jayannah
 * Changed the title of the window seeking the folder request during transformation
 *
 * Revision 1.16  2007/10/11 18:51:38  jayannah
 * Added a transformation confirmation message
 *
 * Revision 1.15  2007/09/19 16:52:12  jayannah
 * added the animated icon to the process so that the user has the sense of a background process
 *
 * Revision 1.14  2007/09/13 15:37:42  jayannah
 * handled null pointer exception when the preference value doe not exist
 *
 * Revision 1.13  2007/09/07 19:29:34  wangeug
 * relocate readPreference and savePreference methods
 *
 * Revision 1.12  2007/08/31 21:10:28  jayannah
 * handled user cancel during the password input and transformation
 *
 * Revision 1.11  2007/08/31 19:40:09  jayannah
 * Commented the system out statements
 *
 * Revision 1.10  2007/08/29 20:41:12  jayannah
 * closed the connection when the transformation is requested using the menu
 *
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
