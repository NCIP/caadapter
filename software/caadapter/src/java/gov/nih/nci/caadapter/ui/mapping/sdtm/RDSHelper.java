/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.mapping.sdtm;

import gov.nih.nci.caadapter.common.csv.data.CSVSegment;
import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.sdtm.ParseSDTMXMLFile;
import gov.nih.nci.caadapter.sdtm.SDTMMetadata;
import gov.nih.nci.caadapter.ui.common.tree.DefaultTargetTreeNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * The class read the map file for the rds transformer
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.5 $
 *          $Date: 2008-06-09 19:54:06 $
 */
public class RDSHelper {
    public static String getDefineXMLNameFromMapFile(String mapFile) throws Exception {
        String defineXMLName = "";
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(new File(mapFile));
        //System.out.println( "Root element of the doc is " + doc.getDocumentElement().getNodeName());
        NodeList linkNodeList = doc.getElementsByTagName("components");
        int totalPersons = linkNodeList.getLength();
        //System.out.println( "Total no of links are : " + totalPersons);
        // System.out.println( defineXMLList.toString());
        for (int s = 0; s < linkNodeList.getLength(); s++) {
            Node node = linkNodeList.item(s);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element firstCompElement = (Element) node;
                NodeList targetNode = firstCompElement.getElementsByTagName("component");
                Element targetName1 = (Element) targetNode.item(0);
                targetName1.getAttribute("location").toString();
                Element targetName2 = (Element) targetNode.item(1);
                if (targetName2.getAttribute("kind").toString().equalsIgnoreCase("XML")) {
                    defineXMLName =FileUtil.getAssociatedFileAbsolutePath(mapFile,  targetName2.getAttribute("location").toString());
                }
            }
        }
        return defineXMLName;
    }

    public static String getSCSFileFromMapFile(File mapFile) throws Exception {
        String scsFileName = null;
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(mapFile);
        NodeList linkNodeList = doc.getElementsByTagName("components");
        for (int s = 0; s < linkNodeList.getLength(); s++) {
            Node node = linkNodeList.item(s);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element firstCompElement = (Element) node;
                NodeList targetNode = firstCompElement.getElementsByTagName("component");
                Element targetName1 = (Element) targetNode.item(0);
                scsFileName = FileUtil.getAssociatedFileAbsolutePath(mapFile.getAbsolutePath(),targetName1.getAttribute("location").toString());
            }
        }
        return scsFileName;
    }

    public static Hashtable getAllFieldsForDomains(File SDTMXmlFile) {
        // _xmlFileName = FileUtil.fileLocateOnClasspath(_xmlFileName);
        String xmlFilePath;
        try {
            String test = SDTMXmlFile.toString();
            xmlFilePath = FileUtil.fileLocateOnClasspath(test);
        } catch (FileNotFoundException e) {
            xmlFilePath = SDTMXmlFile.getAbsolutePath();
        }
        ParseSDTMXMLFile _parseSDTMFile = new ParseSDTMXMLFile(xmlFilePath);
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

    public static String getParentasXPath(CSVSegment csvsegment) {
        ArrayList list = new ArrayList();
        StringBuffer sb = new StringBuffer();
        try {
            while (true) {
                list.add(csvsegment.getMetaObject());
                csvsegment = csvsegment.getParentSegment();
                if (csvsegment.getParentSegment() == null) {
                    list.add(csvsegment.getMetaObject());
                    break;
                }
            }
        } catch (Exception e) {
        }
        for (int l = 1; l < list.size() + 1; l++) {
            try {
                int sizeNow = list.size() - l;
                sb.append("\\" + list.get(sizeNow));
            } catch (Exception ed) {
            }
        }
        return "\\Source Tree" + sb.toString();
    }
}

/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2007/11/16 17:19:38  wangeug
 * update SDTM module
 *
 * Revision 1.3  2007/10/15 21:01:14  jayannah
 * Changed the wat reading files to accomodate the working directory path
 *
 * Revision 1.2  2007/08/16 19:39:45  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */
