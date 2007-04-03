package gov.nih.nci.caadapter.ui.mapping.sdtm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
import gov.nih.nci.caadapter.sdtm.SDTMMetadata;
import gov.nih.nci.caadapter.ui.common.CaadapterFileFilter;
import gov.nih.nci.caadapter.ui.common.MappableNode;
import gov.nih.nci.caadapter.ui.common.jgraph.MappingDataManager;

public class OpenSDTMMapFile extends JDialog {
	private MappingDataManager _mappingDataMananger;

	private HashMap _mappedData;

	private Database2SDTMMappingPanel _database2SDTMMappingPanel;

	public String _xmlFile;

	JFileChooser directoryLoc, scsFile;

	File directory, scsFileChosen;

	String _scsFileName = "";

	OpenSDTMMapFile() {
	}

	public OpenSDTMMapFile(Database2SDTMMappingPanel database2SDTMMappingPanel) {
		_database2SDTMMappingPanel = database2SDTMMappingPanel;
		_mappingDataMananger = database2SDTMMappingPanel.getMiddlePanel().getMappingDataManager();
	}

	public OpenSDTMMapFile(Database2SDTMMappingPanel database2SDTMMappingPanel, String mapFile) throws Exception {
		_database2SDTMMappingPanel = database2SDTMMappingPanel;
		_mappingDataMananger = database2SDTMMappingPanel.getMiddlePanel().getMappingDataManager();
		getMappingInfo(mapFile);
		database2SDTMMappingPanel.setChanged(false);
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
		 * 4.Call the set save file on the mapping panel <br>
		 */
		_database2SDTMMappingPanel.setSaveFile(new File(mapFileName));
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
					_scsFileName = targetName1.getAttribute("location").toString();
				}
				Element targetName2 = (Element) targetNode.item(1);
				if (targetName2.getAttribute("kind").toString().equalsIgnoreCase("XML")) {
					_xmlFileName = targetName2.getAttribute("location").toString();
				}
			}
		}
		if (!new File(_scsFileName).exists()) {
			CaadapterFileFilter filter = new CaadapterFileFilter();
			filter.addExtension("scs");
			String _defaultLoc = System.getProperty("user.dir") + "\\workingspace\\examples";
			directoryLoc = new JFileChooser(_defaultLoc);
			this.setTitle(_scsFileName+" not found! Please choose a different file");
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
		if (!new File(_xmlFileName).exists()) {
			CaadapterFileFilter filter = new CaadapterFileFilter();
			filter.addExtension("xml");
			String _defaultLoc = System.getProperty("user.dir") + "\\workingspace\\examples";
			directoryLoc = new JFileChooser(_defaultLoc);
			this.setTitle(_xmlFileName+" not found! Please choose a different file");
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
		createMappingsInMiddlePanel();
	}

	private void createMappingsInMiddlePanel() {
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

	public DefaultMutableTreeNode searchNode1(String nodeStr, DefaultMutableTreeNode rootNode) {
		DefaultMutableTreeNode node = null;
		java.util.Enumeration enum1 = rootNode.depthFirstEnumeration();
		while (enum1.hasMoreElements()) {
			node = (DefaultMutableTreeNode) enum1.nextElement();
			if (nodeStr.equals(processSourceNode(node))) {
				return node;
			}
		}
		// tree node with string node found return null
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
		SDTMMetadata s = (SDTMMetadata) node.getUserObject();
		// System.out.println("found************************" + s.getXPath());
		// tree node with string found
		return s.getXPath();
	}

	public static void main(String[] args) throws Exception {
		OpenSDTMMapFile n = new OpenSDTMMapFile();
		n.getMappingInfo("C:\\Documents and Settings\\Hjayanna\\My Documents\\SDTM.stuff\\map.files\\10302.map");
		System.out.print("The values are" + n._mappedData);
	}
}
