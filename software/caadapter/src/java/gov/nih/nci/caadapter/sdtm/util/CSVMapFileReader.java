/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.sdtm.util;

import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

/**
 * This is a MAP file reader
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.3 $
 *          $Date: 2008-06-06 18:55:19 $
 */
public class CSVMapFileReader {
    private File mappingFile=null;
    private Node root=null;
    private Hashtable<String, ArrayList> hashTable = null;

    public Hashtable<String, ArrayList> getHashTableTransform() {
        return hashTableTransform;
    }

    private Hashtable<String, ArrayList> hashTableTransform = null;
    private HashSet<String> targetKeyList = null;
    private Hashtable<String, String> hashSQLfromMappings = null;

    public CSVMapFileReader(File file) {
        hashTable = new Hashtable<String, ArrayList>();
        hashTableTransform = new Hashtable<String, ArrayList>();
        targetKeyList = new HashSet<String>();
        hashSQLfromMappings = new Hashtable<String, String>();
        this.mappingFile = file;
        Document doc = parseFile();
        root = doc.getDocumentElement();
        writeDocumentToOutput(root, 0);
    }

    public Document parseFile() {
        DocumentBuilder docBuilder;
        Document doc = null;
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setIgnoringElementContentWhitespace(true);
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            System.out.println("Wrong parser configuration: " + e.getMessage());
            return null;
        }
        try {
            doc = docBuilder.parse(mappingFile);
        } catch (SAXException e) {
            System.out.println("Wrong XML file structure: " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.out.println("Could not read source file: " + e.getMessage());
        }
        return doc;
    }

    public HashSet<String> getTargetKeyList() {
        return targetKeyList;
    }

    public void writeDocumentToOutput(Node node, int indent) {
        if (node.getNodeName().equalsIgnoreCase("link")) {
            NodeList children = node.getChildNodes();
            String tempStr = "", tempDomain = "", tempTable = "", tempSt4Columns = "";
            for (int i = 0; i < children.getLength(); i++) {
                if (!(children.item(i).getNodeName()).equalsIgnoreCase("#text")) {
                    tempStr = children.item(i).getTextContent();
                    if (tempStr.startsWith("\\#document")) {
                        targetKeyList.add(getTargetFieldName(tempStr));
                        tempDomain = tempStr;
                    } else if (tempStr.startsWith("\\Source Tree")) {
                        tempTable = tempStr;
                        tempSt4Columns = tempStr;
                    }
                    if (tempDomain.length() > 0 && tempTable.length() > 0) {
                        EmptyStringTokenizer _empt = new EmptyStringTokenizer(tempDomain, "\\");
                        processXMLContents(formatDomain(tempDomain), formatTable(tempTable));
                        processXMLContents(formatDomain(tempDomain), tempTable, _empt.getTokenAt(_empt.countTokens() - 1));
                        formatColumn(tempDomain, tempSt4Columns);
                    }
                }
            }
        }
        if (node.getNodeName().equalsIgnoreCase("sql")) {
            NodeList children = node.getChildNodes();
            NamedNodeMap nNamedMap = node.getAttributes();
            Node attribute = nNamedMap.item(0);
            for (int i = 0; i < children.getLength(); i++) {
                hashSQLfromMappings.put(attribute.getNodeValue().toString(), children.item(i).getTextContent());
            }
        }
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                writeDocumentToOutput(child, indent + 2);
            }
        }
    }

    public Hashtable<String, String> getHashSQLfromMappings() {
        return hashSQLfromMappings;
    }

    public Hashtable<String, ArrayList> getHashTable() {
        return hashTable;
    }

    private void processXMLContents(String domainString, String tableString) {
        if (!hashTable.containsKey(domainString)) {
            ArrayList _tmpAry = new ArrayList();
            _tmpAry.add(tableString);
            hashTable.put(domainString, _tmpAry);
        } else {
            ArrayList _tmpAry = hashTable.get(domainString);
            if (_tmpAry != null && _tmpAry.size() > 0) {
                _tmpAry.add(tableString);
                hashTable.remove(domainString);
                hashTable.put(domainString, _tmpAry);
            }
        }
    }

    private void processXMLContents(String domainString, String tableString, String domainvalue) {
        EmptyStringTokenizer _empStr = new EmptyStringTokenizer(tableString, "\\");
        //tableString = _empStr.getTokenAt(2) + "." + _empStr.getTokenAt(3) + "." + _empStr.getTokenAt(4);
        //tableString = _empStr.getTokenAt(_empStr.countTokens() - 1);
        if (!hashTableTransform.containsKey(domainString)) {
            ArrayList _tmpAry = new ArrayList();
            _tmpAry.add(tableString + "~" + domainvalue);
            hashTableTransform.put(domainString, _tmpAry);
        } else {
            ArrayList _tmpAry = hashTableTransform.get(domainString);
            if (_tmpAry != null && _tmpAry.size() > 0) {
                _tmpAry.add(tableString + "~" + domainvalue);
                hashTableTransform.remove(domainString);
                hashTableTransform.put(domainString, _tmpAry);
            }
        }
    }

    private String formatDomain(String str) {
        EmptyStringTokenizer _empStr = new EmptyStringTokenizer(str, "\\");
        return _empStr.getTokenAt(6).substring(0, 2);
    }

    private String getTargetFieldName(String str) {
        EmptyStringTokenizer _empStr = new EmptyStringTokenizer(str, "\\");
        return _empStr.getTokenAt(6);
    }

    private String formatTable(String str) {
        EmptyStringTokenizer _empStr = new EmptyStringTokenizer(str, "\\");
        return _empStr.getTokenAt(2) + "." + _empStr.getTokenAt(3);
    }

    private void formatColumn(String domain, String str) {
        EmptyStringTokenizer _empStr = new EmptyStringTokenizer(str, "\\");
        //hashTblColumnsSet.add(formatDomain(domain) + "~" + _empStr.getTokenAt(2) + "." + _empStr.getTokenAt(3) + "~" + _empStr.getTokenAt(4));
    }

    public static void main(String args[]) {
        CSVMapFileReader _qb = new CSVMapFileReader(new File("d:\\d2.map"));
        //System.out.println("value " + _qb.hashTblColumnsSet);
        System.out.println("transform helper " + _qb.hashTableTransform);
        System.out.println("Mapped Keys " + _qb.getTargetKeyList());
        //System.out.println("SQLs are " + _qb.hashSQLfromMappings);
    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2007/08/16 19:04:58  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */
