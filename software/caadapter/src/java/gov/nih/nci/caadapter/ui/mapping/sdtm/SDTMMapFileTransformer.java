/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.mapping.sdtm;

import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
import gov.nih.nci.caadapter.sdtm.ParseSDTMXMLFile;
import gov.nih.nci.caadapter.sdtm.SDTMRecord;
import gov.nih.nci.caadapter.sdtm.SDTM_CSVReader;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import org.apache.commons.collections.MultiMap;
import org.w3c.dom.*;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.2 revision $Revision: 1.5 $
 * @date       $Date: 2008-09-29 21:31:27 $
 */
public class SDTMMapFileTransformer
{
	HashSet _SDTMColumnKeys = new HashSet();

	ArrayList _CSVdataXPath = new ArrayList();

	HashMap _csvDataFromFile = new HashMap();

	HashMap _sdtmResultsFile = new HashMap();

	LinkedList defineXMLList = new LinkedList();

	HashMap _mappedData = new HashMap();

	AbstractMainFrame mainFrame;

	String _saveSDTMPath;

	String _csvFileName;

	public SDTMMapFileTransformer(String mapFileName, String csvFileName, AbstractMainFrame _callingFrame) throws Exception {
		// create a complete list before
		ParseSDTMXMLFile _parseSDTMFile = new ParseSDTMXMLFile("c:\\define.xml");
		ArrayList<String> _retArray = _parseSDTMFile.getSDTMStructure();
		// LinkedList defineXMLList = new LinkedList();
		for (int k = 0; k < 18; k++) {
			if (_retArray.get(k).startsWith("KEY")) {
				// EmptyStringTokenizer _str = new
				// EmptyStringTokenizer(_retArray.get(k),",");
			} else {
				EmptyStringTokenizer _str = new EmptyStringTokenizer(_retArray.get(k), ",");
				String _tmpStr = _str.getTokenAt(1).toString();
				defineXMLList.add(_tmpStr.substring(0, _tmpStr.indexOf("&")));
				// pNode.add(new DefaultTargetTreeNode(new SDTMMetadata(_str.getTokenAt(1),_str.getTokenAt(2), _str.getTokenAt(3), _str.getTokenAt(4))));
			}
		}
	}

	public SDTMMapFileTransformer(String mapFileName, String csvFileName, AbstractMainFrame _callingFrame, String saveSDTMPath) throws Exception {
		mainFrame = _callingFrame;
		_saveSDTMPath = saveSDTMPath;
		_csvFileName = csvFileName;
		// create a complete list before
		ParseSDTMXMLFile _parseSDTMFile = new ParseSDTMXMLFile(getDefineXMlName(mapFileName));
		ArrayList<String> _retArray = _parseSDTMFile.getSDTMStructure();
		// LinkedList defineXMLList = new LinkedList();
		for (int k = 0; k < 18; k++) {
			if (_retArray.get(k).startsWith("KEY")) {
				// EmptyStringTokenizer _str = new EmptyStringTokenizer(_retArray.get(k),",");
			} else {
				EmptyStringTokenizer _str = new EmptyStringTokenizer(_retArray.get(k), ",");
				String _tmpStr = _str.getTokenAt(1).toString();
				defineXMLList.add(_tmpStr.substring(0, _tmpStr.indexOf("&")));
				// pNode.add(new DefaultTargetTreeNode(new SDTMMetadata(_str.getTokenAt(1),_str.getTokenAt(2), _str.getTokenAt(3), _str.getTokenAt(4))));
			}
		}
		SDTM_CSVReader _csvData = new SDTM_CSVReader();
		_csvData.readCSVFile(csvFileName);
		_csvDataFromFile = _csvData.get_CSVData();
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(mapFileName));
			//System.out.println("Root element of the doc is " + doc.getDocumentElement().getNodeName());
			NodeList linkNodeList = doc.getElementsByTagName("link");
			int totalPersons = linkNodeList.getLength();
			//System.out.println("Total no of links are : " + totalPersons);
			//System.out.println(defineXMLList.toString());
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
					// _mappedData.put(finalTargetName, _tmp+"%"+sourceNodeValue);
					StringBuffer _sBuf = new StringBuffer();
					if (_mappedData.get(_tmp) == null) {
						_sBuf.append(sourceNodeValue + "?" + finalTargetName);
						_mappedData.put(_tmp, _sBuf);
					} else {
						StringBuffer _tBuf = (StringBuffer) _mappedData.get(_tmp);
						_tBuf.append("," + sourceNodeValue + "?" + finalTargetName);
						_mappedData.put(_tmp, _tBuf);
					}
				}// end of if clause
			}// end of for loop with s var
			System.out.println(_mappedData);
			System.out.println(_csvDataFromFile);
			BeginTransformation();
		} catch (SAXParseException err) {
			System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
			System.out.println(" " + err.getMessage());
		} catch (SAXException e) {
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace();
		} catch (Throwable t) {
			// JOptionPane.showMessageDialog(mainFrame, "SDTM Record File created successfully");
			t.printStackTrace();
		}
		JOptionPane.showMessageDialog(mainFrame, "SDTM Record File " + _saveSDTMPath + " created successfully");
		// System.exit (0);
	}// end of main

	public String getDefineXMlName(String mapFileName) throws Exception
	{
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
				Element targetName2 = (Element) targetNode.item(1);
				if (targetName2.getAttribute("kind").toString().equalsIgnoreCase("XML")) {
					_xmlFileName = targetName2.getAttribute("location").toString();
				}
			}
		}
		return _xmlFileName;
	}

	public static void main(String argv[]) throws Exception
	{
	//	SDTMMapFileTransformer r = new SDTMMapFileTransformer("C:\\Documents and Settings\\Hjayanna\\My Documents\\SDTM.stuff\\map.files\\sample24.map", "D:\\CVS02\\hl7sdk\\workingspace\\examples\\040011\\040011.csv", null, null, "c:\\d7.sdtm");
	//	r.BeginTransformation();
	}

	public HashSet get_SDTMColumnKeys()
	{
		return _SDTMColumnKeys;
	}

	public void set_SDTMColumnKeys(HashSet columnKeys)
	{
		_SDTMColumnKeys = columnKeys;
	}

	public void startTransform()
	{
		// _mappedData _csvDataFromFile Iterate over the keys in the map
		Iterator it = _mappedData.keySet().iterator();
		while (it.hasNext()) {
			// Get key
			Object key = it.next();
			if (_csvDataFromFile.containsKey(key)) {
				String _tmp = (String) _csvDataFromFile.get(key);
				StringTokenizer _str = new StringTokenizer(_tmp.toString(), "&");
				while (_str.hasMoreTokens()) {
					LinkedList<String> _new = new LinkedList<String>();
					// dummy list initialized
					for (int j = 0; j < defineXMLList.size(); j++) {
						_new.add("");
					}
					// to retrieve the data from the csv file
					EmptyStringTokenizer _emp = new EmptyStringTokenizer(_str.nextToken(), ",");
					// to get all the fields for the segment
					// StringBuffer _md = (StringBuffer) _mappedData.get(key);
					// for (int j=0; j<_mappedData.size(); j++){
					Iterator it1 = _mappedData.keySet().iterator();
					while (it1.hasNext()) {
						// Get key
						Object key1 = it1.next();
						StringBuffer _md = (StringBuffer) _mappedData.get(key1);
						StringTokenizer _strT = new StringTokenizer(_md.toString(), ",");
						while (_strT.hasMoreTokens()) {
							StringTokenizer s = new StringTokenizer(_strT.nextToken(), "?");
							int pos_ = new Integer(s.nextToken().substring(0, 1)).intValue();
							String _sRef = s.nextToken();
							// System.out.println(pos_+" for is "+_sRef);
							_new.set(defineXMLList.indexOf(_sRef), _emp.getTokenAt(pos_ - 1));
						}
					}
					System.out.println(_new);
				}
			}
		}
	}

	public void BeginTransformation() throws Exception
	{
		/**
		 * 1. For each key in the _csvDataFromFile, check if the key exists in _mappedData <br>
		 * 1a. If exists, get the pos and the colmnname <br>
		 * 2. create SDTM record instance <br>
		 * 2a. setRecord <br>
		 * 3. print rec <br>
		 */
		SDTMRecord _sdtm = new SDTMRecord();
		MultiMap mhm = new SDTM_CSVReader().readCSVFile(_csvFileName);
		// Iterate over the keys in the map
		Iterator it = mhm.keySet().iterator();
		while (it.hasNext()) {
			// SDTMRecord _sdtm = new SDTMRecord(_csvDataFromFile);
			// Get key
			Object key = it.next();
			if (_mappedData.containsKey(key)) {
				Collection coll = (Collection) mhm.get(key);
				for (Iterator it1 = coll.iterator(); it1.hasNext();) {
					Object mappedKey = it1.next();
					StringBuffer _value = (StringBuffer) _mappedData.get(key);
					// System.out.println("==============================================");
					// System.out.println("Mappedvalues " + _value);
					// System.out.println("CSVValues are " + mappedKey);
					StringTokenizer _level0 = new StringTokenizer(_value.toString(), ",");
					while (_level0.hasMoreTokens()) {
						StringTokenizer str = new StringTokenizer(_level0.nextToken(), "?");
						int pos = new Integer(str.nextToken().substring(0, 2).trim()).intValue();
						String dataKey = str.nextToken();
						EmptyStringTokenizer emp = new EmptyStringTokenizer(mappedKey.toString(), ",");
						createRecord1(_sdtm, emp.getTokenAt(pos - 1).toString(), dataKey.replace('.', '_'));
					}
				}
			}
		}
		_sdtm.print(defineXMLList.toString(), _saveSDTMPath);
	}

	public void createRecord1(SDTMRecord _sdtmRec, String dataKey, String sdtmKey)
	{
		if (sdtmKey.startsWith("STUDYID")) {
			_sdtmRec.setSTUDYID(dataKey);
		} else if (sdtmKey.startsWith("DOMAIN")) {
			_sdtmRec.setDOMAIN(dataKey);
		} else if (sdtmKey.startsWith("USUBJID")) {
			_sdtmRec.setUSUBJID(dataKey);
		} else if (sdtmKey.startsWith("SUBJID")) {
			_sdtmRec.setSUBJID(dataKey);
		} else if (sdtmKey.startsWith("DM_RFSTDTC")) {
			_sdtmRec.setDM_RFSTDTC(dataKey);
		} else if (sdtmKey.startsWith("DM_RFENDTC")) {
			_sdtmRec.setDM_RFENDTC(dataKey);
		} else if (sdtmKey.startsWith("DM_SITEID")) {
			_sdtmRec.setDM_SITEID(dataKey);
		} else if (sdtmKey.startsWith("DM_INVID")) {
			_sdtmRec.setDM_INVID(dataKey);
		} else if (sdtmKey.startsWith("DM_BRTHDTC")) {
			_sdtmRec.setDM_BRTHDTC(dataKey);
		} else if (sdtmKey.startsWith("DM_AGE")) {
			_sdtmRec.setDM_AGE(dataKey);
		} else if (sdtmKey.startsWith("DM_AGEU")) {
			_sdtmRec.setDM_AGEU(dataKey);
		} else if (sdtmKey.startsWith("DM_SEX")) {
			_sdtmRec.setDM_SEX(dataKey);
		} else if (sdtmKey.startsWith("DM_RACE")) {
			_sdtmRec.setDM_RACE(dataKey);
		} else if (sdtmKey.startsWith("DM_ARMCD")) {
			_sdtmRec.setDM_ARMCD(dataKey);
		} else if (sdtmKey.startsWith("DM_ARM")) {
			_sdtmRec.setDM_ARM(dataKey);
		} else if (sdtmKey.startsWith("DM_COUNTRY")) {
			_sdtmRec.setDM_COUNTRY(dataKey);
		} else if (sdtmKey.startsWith("DM_DM_TC")) {
			_sdtmRec.setDM_DM_TC(dataKey);
		} else if (sdtmKey.startsWith("DM_DMDY")) {
			_sdtmRec.setDM_DM_TC(dataKey);
		}
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
