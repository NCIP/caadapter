package gov.nih.nci.caadapter.ui.mapping.sdtm;

import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
import gov.nih.nci.caadapter.sdtm.SDTMMetadata;
import gov.nih.nci.caadapter.ui.common.CaadapterFileFilter;
import gov.nih.nci.caadapter.ui.common.MappableNode;
import gov.nih.nci.caadapter.ui.common.jgraph.MappingDataManager;
import org.w3c.dom.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.File;
import java.util.*;
/**
 * The class helps in opening a MAP file(both SCS and Database)
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: jayannah $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.8 $
 *          $Date: 2007-08-31 19:40:09 $
 */
public class OpenSDTMMapFile extends JDialog {
    private MappingDataManager _mappingDataMananger=null;
    private HashMap _mappedData=null;
    private Database2SDTMMappingPanel _database2SDTMMappingPanel=null;
    public String _xmlFile=null;
    JFileChooser directoryLoc, scsFile=null;
    File directory, scsFileChosen=null;
    String _scsFileName = null, _dbParams = null;
    private Hashtable xPathNodeSet = null;

    OpenSDTMMapFile() {
    }

    public OpenSDTMMapFile(Database2SDTMMappingPanel database2SDTMMappingPanel) {
        _database2SDTMMappingPanel = database2SDTMMappingPanel;
        _mappingDataMananger = database2SDTMMappingPanel.getMiddlePanel().getMappingDataManager();
    }

    public OpenSDTMMapFile(final Database2SDTMMappingPanel database2SDTMMappingPanel, final String mapFile) throws Exception {
        _database2SDTMMappingPanel = database2SDTMMappingPanel;
        _mappingDataMananger = database2SDTMMappingPanel.getMiddlePanel().getMappingDataManager();
        // start a thread here
        final Dialog _openMapfileWaitDialog = new Dialog(_database2SDTMMappingPanel.get_mainFrame());
        new Thread(new Runnable() {
            public void run() {
                try {
                    getMappingInfo(mapFile);
                    database2SDTMMappingPanel.setChanged(false);
                    _openMapfileWaitDialog.dispose();
                } catch (Exception e) {
                    _openMapfileWaitDialog.dispose();
                    JOptionPane.showMessageDialog(_database2SDTMMappingPanel.get_mainFrame(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }).start();
        _openMapfileWaitDialog.setTitle("Please wait... ");
        _openMapfileWaitDialog.setSize(350, 100);
        _openMapfileWaitDialog.setLocation(450, 450);
        _openMapfileWaitDialog.setLayout(new BorderLayout());
        LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
        JPanel _waitLabel = new JPanel();
        _waitLabel.setBorder(lineBorder);
        _waitLabel.add(new JLabel("      Opening the file, " + mapFile + " ..."));
        _openMapfileWaitDialog.add(new JLabel("                       "), BorderLayout.NORTH);
        _openMapfileWaitDialog.add(_waitLabel, BorderLayout.CENTER);
        _openMapfileWaitDialog.setVisible(true);
        // the thread ends here
    }

    public OpenSDTMMapFile(Database2SDTMMappingPanel database2SDTMMappingPanel, String mapFile, String xmlFile) throws Exception {
        _database2SDTMMappingPanel = database2SDTMMappingPanel;
        _xmlFile = xmlFile;
        _mappingDataMananger = database2SDTMMappingPanel.getMiddlePanel().getMappingDataManager();
        getMappingInfo(mapFile);
    }

    public void getMappingInfo(String mapFileName) throws Exception {
        /**
         * 1.Read the XML file <br>
         * 2.Obtain the source nodes and target nodes from the file<br>
         * 3.Call createMapping method with these two as arguments <br>
         * 4.Call the set save file on the mapping panel <br>         */
        xPathNodeSet = new Hashtable();
        _database2SDTMMappingPanel.setSaveFile(new File(mapFileName));
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
                    _scsFileName = targetName1.getAttribute("location").toString();
                    
                } else if (targetName1.getAttribute("kind").toString().equalsIgnoreCase("Database")) {
                    _dbParams = targetName1.getAttribute("param").toString();
                }
                Element targetName2 = (Element) targetNode.item(1);
                if (targetName2.getAttribute("kind").toString().equalsIgnoreCase("XML")) {
                    _xmlFileName = targetName2.getAttribute("location").toString();
                }
            }
        }
        if (_scsFileName != null) {
            if (!new File(_scsFileName).exists()) {
                CaadapterFileFilter filter = new CaadapterFileFilter();
                filter.addExtension("scs");
                String _defaultLoc = System.getProperty("user.dir") + "\\workingspace\\examples";
                directoryLoc = new JFileChooser(_defaultLoc);
                this.setTitle(_scsFileName + " not found! Please choose a different file");
                //directoryLoc.setDialogTitle(_scsFileName+" not found! Please choose a different file");
                scsFile = new JFileChooser(_defaultLoc);
                // filter.setDescription("map");
                scsFile.setFileFilter(filter);
                int returnVal = scsFile.showOpenDialog(OpenSDTMMapFile.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    scsFileChosen = scsFile.getSelectedFile();
                    _scsFileName = scsFileChosen.toString();
                } else {
                    return;
                }
            }
        }
        if (_dbParams != null) {
            //_database2SDTMMappingPanel.openDataBaseMapFile(_dbParams);
            _database2SDTMMappingPanel.openDataBaseMapFile(_dbParams);
            System.out.println("");
        }
        if (!new File(_xmlFileName).exists()) {
            CaadapterFileFilter filter = new CaadapterFileFilter();
            filter.addExtension("xml");
            String _defaultLoc = System.getProperty("user.dir") + "\\workingspace\\examples";
            directoryLoc = new JFileChooser(_defaultLoc);
            this.setTitle(_xmlFileName + " not found! Please choose a different file");
            //directoryLoc.setDialogTitle(_scsFileName+" not found! Please choose a different file");
            scsFile = new JFileChooser(_defaultLoc);
            // filter.setDescription("map");
            scsFile.setFileFilter(filter);
            int returnVal = scsFile.showOpenDialog(OpenSDTMMapFile.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                scsFileChosen = scsFile.getSelectedFile();
                _xmlFileName = scsFileChosen.toString();
            } else {
                return;
            }
        }
        if (_scsFileName != null)
            _database2SDTMMappingPanel.processOpenSourceTree(new File(_scsFileName), false, true);
        _database2SDTMMappingPanel.processOpenTargetTree(new File(_xmlFileName), false, true);
        NodeList linkNodeList = doc.getElementsByTagName("link");
        int totalPersons = linkNodeList.getLength();
        System.out.println("Total no of links are : " + totalPersons);
        _mappedData = new HashMap();
        for (int s = 0; s < linkNodeList.getLength(); s++) {
            Node node = linkNodeList.item(s);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element firstPersonElement = (Element) node;
                NodeList targetNode = firstPersonElement.getElementsByTagName("target");
                Element targetName = (Element) targetNode.item(0);
                NodeList textLNList = targetName.getChildNodes();
                String _targetName = ((Node) textLNList.item(0)).getNodeValue().trim();
                EmptyStringTokenizer _tmpEmp = new EmptyStringTokenizer(_targetName.toString(), "\\");
                String finalTargetName = _tmpEmp.getTokenAt(_tmpEmp.countTokens() - 1);
                NodeList sourceNode = firstPersonElement.getElementsByTagName("source");
                Element sourceName = (Element) sourceNode.item(0);
                NodeList textFNList = sourceName.getChildNodes();
                String _srcNodeVal = ((Node) textFNList.item(0)).getNodeValue().trim();
                EmptyStringTokenizer _str = new EmptyStringTokenizer(_srcNodeVal, "\\");
                String _tmp = _str.getTokenAt(_str.countTokens() - 2);
                String sourceNodeValue = _str.getTokenAt(_str.countTokens() - 1);
                /***************************************************************
                 * Add the target and source to the hashmap
                 **************************************************************/
                _mappedData.put(_srcNodeVal, _targetName);
                /***************************************************************
                 * also set the mappings in the 'sdtmMappingGenerator' object;
                 * this is because when the map file is opened it must have
                 * those values which existed before
                 **************************************************************/
                _database2SDTMMappingPanel.getSdtmMappingGenerator().put(_srcNodeVal, _targetName);
            }// end of if clause
        }// end of for loop with s var
        if (_dbParams != null)
            createMappingsInMiddlePanelForDB();
        else
            createMappingsInMiddlePanelForCSV();
        NodeList sqlQueryList = doc.getElementsByTagName("sql");
        for (int s = 0; s < sqlQueryList.getLength(); s++) {
            //Node node = sqlQueryList.item(s);
            NamedNodeMap nodeList = sqlQueryList.item(s).getAttributes();
            Node attr = nodeList.getNamedItem("name");
            System.out.println("the domain names are ---- " + attr.getNodeValue());
            String s1 = ((Node) sqlQueryList.item(s)).getFirstChild().toString();
            String s2 = s1.substring(s1.indexOf(": ") + 1, s1.length());
            _database2SDTMMappingPanel.getSqlQueries().put(attr.getNodeValue(), s2.substring(0, s2.lastIndexOf("]")).trim());
        }
    }

    private void createMappingsInMiddlePanelForCSV() {
        Set<String> _targetXPath = new HashSet<String>();
        Iterator it = _mappedData.keySet().iterator();
        while (it.hasNext()) {
            Object sourceNode = it.next();
            DefaultMutableTreeNode resSourceNode = searchNode1(sourceNode.toString(), (DefaultMutableTreeNode) _database2SDTMMappingPanel.getSourceNodes());
            DefaultMutableTreeNode resTargetNode = searchNode2(_mappedData.get(sourceNode.toString()).toString(), (DefaultMutableTreeNode) _database2SDTMMappingPanel.getTargetNodes());
            /*******************************************************************
             * If the target nodes XPATH is listed in the mappedData collection
             * object then call the _mappingDataManager
             ******************************************************************/
            if (resSourceNode != null && resTargetNode != null) {
                _mappingDataMananger.createMapping((MappableNode) resSourceNode, (MappableNode) resTargetNode);
            }
        }
    }

    private void createMappingsInMiddlePanelForDB() {
        Set<String> _targetXPath = new HashSet<String>();
        Iterator it = _mappedData.keySet().iterator();
        while (it.hasNext()) {
            Object sourceNode = it.next();
            DefaultMutableTreeNode resSourceNode = searchNode1(sourceNode.toString(), (DefaultMutableTreeNode) _database2SDTMMappingPanel.getSourceNodes());
            DefaultMutableTreeNode resTargetNode;
            if (_dbParams != null) {
                visitAllNodes((DefaultMutableTreeNode) _database2SDTMMappingPanel.getTargetNodes());
                resTargetNode = (DefaultMutableTreeNode) xPathNodeSet.get(_mappedData.get(sourceNode.toString()).toString());
            } else {
                resTargetNode = searchNode2(_mappedData.get(sourceNode.toString()).toString(), (DefaultMutableTreeNode) _database2SDTMMappingPanel.getTargetNodes());
            }
            /*******************************************************************
             * If the target nodes XPATH is listed in the mappedData collection
             * object then call the _mappingDataManager
             ******************************************************************/
            if (resSourceNode != null && resTargetNode != null) {
                // new DefaultMutableTreeNode()
                _mappingDataMananger.createMapping((MappableNode) resSourceNode, (MappableNode) resTargetNode);
            }
        }
    }

    public void visitAllNodes(TreeNode node) {
        // node is visited exactly once
        //process(node);
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements();) {
                DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) e.nextElement();
                try {
                    // System.out.println(((SDTMMetadata) node1.getUserObject()).getXPath());
                    xPathNodeSet.put(((SDTMMetadata) node1.getUserObject()).getXPath(), node1);
                } catch (Exception e1) {
                    // System.out.println(((String) node1.getUserObject()).toString());
                    xPathNodeSet.put(((String) node1.getUserObject()).toString(), node1);
                }
                //targetNodes.add(n);
                visitAllNodes(node1);
            }
        }
    }

    public DefaultMutableTreeNode searchNode1(String nodeStr, DefaultMutableTreeNode rootNode) {
        DefaultMutableTreeNode node = null;
        java.util.Enumeration enum1 = rootNode.depthFirstEnumeration();
        while (enum1.hasMoreElements()) {
            node = (DefaultMutableTreeNode) enum1.nextElement();
            String tmp = processSourceNode(node);
            if (nodeStr.equals(tmp)) {
                return node;
            }
        }
        // tree node with string node found return null
        return null;
    }

    public DefaultMutableTreeNode searchNode2(String nodeStr, ArrayList checkArray) {
        DefaultMutableTreeNode node = null;
        for (int i = 0; i < checkArray.size(); i++) {
            node = (DefaultMutableTreeNode) checkArray.get(i);
            String temp = processTargetNode(node);
            if (nodeStr.equals(temp)) {
                System.out.println("The returned node is " + nodeStr);
                return node;
            }
        }
        return null;
    }

   public DefaultMutableTreeNode searchNode2(String nodeStr, DefaultMutableTreeNode rootNode) {
        DefaultMutableTreeNode node = null;
        java.util.Enumeration enum1 = rootNode.depthFirstEnumeration();
        while (enum1.hasMoreElements()) {
            node = (DefaultMutableTreeNode) enum1.nextElement();
            if (nodeStr.equals(processTargetNode(node))) {
                return node;
            }
        }
        return null;
    }

    public String processSourceNode(DefaultMutableTreeNode node) {
        StringBuffer _tmpBuf = new StringBuffer();
        ArrayList<String> _tmp = new ArrayList<String>();
        _tmp.add(node.toString());
        do {
            try {
                node = (DefaultMutableTreeNode) node.getParent();
                _tmp.add(node.toString());
            } catch (Exception ee) {
                break;
            }
        } while (true);
        for (int l = 1; l < _tmp.size() + 1; l++) {
            try {
                int sizeNow = _tmp.size() - l;
                _tmpBuf.append("\\" + _tmp.get(sizeNow));
            } catch (Exception ed) {
                System.out.println("an error has occured :: here is the message " + ed.getMessage());
            }
        }
        return _tmpBuf.toString();
    }

    public String processTargetNode(DefaultMutableTreeNode node) {
        SDTMMetadata s = null;
        try {
            s = (SDTMMetadata) node.getUserObject();
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            System.out.println("+======================================= " + node.toString());
            return null;
        }
        return s.getXPath();
    }

    public String processTargetNode1(DefaultMutableTreeNode node) {
        SDTMMetadata s = (SDTMMetadata) node.getUserObject();
        return s.getXPath();
    }

    public static void main(String[] args) throws Exception {
        OpenSDTMMapFile n = new OpenSDTMMapFile();
        n.getMappingInfo("C:\\Documents and Settings\\Hjayanna\\My Documents\\SDTM.stuff\\map.files\\10302.map");
        System.out.print("The values are" + n._mappedData);
    }
}
/**
 * $Log: not supported by cvs2svn $
 * Revision 1.7  2007/08/30 16:00:38  jayannah
 * reverted back to the previous version
 *
 * Revision 1.4  2007/08/16 19:39:45  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */
