/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.sdtm;

/*
 * ParseXMLFile.java
 *

 */

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.2 revision
 * $Revision: 1.4 $
 * $Date: 2008-06-06 18:55:19 $
 */
public class ParseSDTMXMLFile {
    private String xmlFileName;

    //private final static String targetFileName = "c:/example2.xml";
    public Node root;
    int counter = 0;
    String _mapKey = "";
    StringBuffer _mapValue;
    ArrayList<String> _retArray = new ArrayList<String>();
    HashMap<String, String> _returnSDTMStructure = new HashMap<String, String>();

    /**
     * Creates a new instance of ParseXMLFile
     */
    public ParseSDTMXMLFile(String SDTMFileName) {
        this.xmlFileName = SDTMFileName;
        // parse XML file -> XML document will be build
        Document doc = parseFile(xmlFileName);
        // get root node of xml tree structure
        root = doc.getDocumentElement();
        // write node and its child nodes into System.out
        //System.out.println("Statemend of XML document...");
        writeDocumentToOutput(root, 0);
        //System.out.println("... end of statement");
        // write Document into XML file
        // saveXMLDocument(targetFileName, doc);
    }

    public ArrayList getSDTMStructure() {
        return _retArray;
    }

    /**
     * Returns element value
     *
     * @param elem element (it is XML tag)
     * @return Element value otherwise empty String
     */
    public final static String getElementValue(Node elem) {
        Node kid;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (kid = elem.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
                    if (kid.getNodeType() == Node.TEXT_NODE) {
                        return kid.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    private String getIndentSpaces(int indent) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < indent; i++) {
            buffer.append(" ");
        }
        return buffer.toString();
    }

    /**
     * Writes node and all child nodes into System.out
     *
     * @param node   XML node from from XML tree wrom which will output
     *               statement start
     * @param indent number of spaces used to indent output
     */
    public void writeDocumentToOutput(Node node, int indent) {
        // get element name
        String nodeName = node.getNodeName();
        // get element value
        // String nodeValue = getElementValue(node);
        // get attributes of element
        if (nodeName.equalsIgnoreCase("ItemGroupDef")) {
            // System.out.println("\nItemGroupDef");
            NamedNodeMap attributes = node.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                StringBuffer _sourceDataAsXPath = new StringBuffer();
                Node attribute = attributes.item(i);
                if (attribute.getNodeName().equalsIgnoreCase("Name")) {
                    // System.out.println(attribute.getNodeName() + "-" +
                    // attribute.getNodeValue());
                    _mapKey = attribute.getNodeValue();
                    //System.out.println(" map key is "+_mapKey);
                    _mapValue = new StringBuffer();
                    ArrayList<String> _tmp = new ArrayList<String>();
                    // System.out.println( " The value is "+t.toString());
                    Node t = node.getParentNode();
                    _tmp.add(t.getNodeName().toString());
                    do {
                        try {
                            t = t.getParentNode();
                            _tmp.add(t.getNodeName().toString());
                        } catch (Exception ee) {
                            break;
                        }
                        // System.out.println( " The value is "+t.toString());
                    } while (true);
                    for (int l = 1; l < _tmp.size() + 1; l++) {
                        try {
                            int sizeNow = _tmp.size() - l;
                            _sourceDataAsXPath.append("\\" + _tmp.get(sizeNow));
                        } catch (Exception ed) {
                            // ed.printStackTrace();
                        }
                    }
                    _sourceDataAsXPath.append("\\ItemGroupDef\\" + attribute.getNodeValue());
                    _mapKey = "KEY," + attribute.getNodeValue() + "&" + _sourceDataAsXPath;
                    // System.out.println("The path is
                    // "+_sourceDataAsXPath);
                }
            }
        }
        if (nodeName.equalsIgnoreCase("ItemRef")) {
            if (node.getParentNode().getNodeName().equalsIgnoreCase("ItemGroupDef")) {
                _mapValue = new StringBuffer();
                // System.out.println("\n\t\tItemRef");
                NamedNodeMap attributes = node.getAttributes();
                for (int i = 0; i < attributes.getLength(); i++) {
                    Node attribute = attributes.item(i);
                    // System.out.print(attribute.getNodeName() + "-" +
                    // attribute.getNodeValue() + ",");
                    StringBuffer _dataAsXPath = new StringBuffer();
                    ArrayList<String> _tmp = new ArrayList<String>();
                    // System.out.println( " The value is "+t.toString());
                    Node t = node.getParentNode();
                    _tmp.add(t.getNodeName().toString());
                    do {
                        try {
                            t = t.getParentNode();
                            _tmp.add(t.getNodeName().toString());
                        } catch (Exception ee) {
                            break;
                        }
                        // System.out.println( " The value is "+t.toString());
                    } while (true);
                    for (int l = 1; l < _tmp.size() + 1; l++) {
                        try {
                            int sizeNow = _tmp.size() - l;
                            _dataAsXPath.append("\\" + _tmp.get(sizeNow));
                        } catch (Exception ed) {
                            // ed.printStackTrace();
                        }
                    }
                    _dataAsXPath.append("\\" + attribute.getNodeValue());
                    _mapValue.append(attribute.getNodeValue() + "&" + _dataAsXPath + ",");
                    //System.out.println("-------------------------------------->"+attribute.getNodeValue());
                }
                if (_mapKey.length() > 0) {
                    // System.out.println(_mapKey);
                    _retArray.add(_mapKey);
                }
                //System.out.println("Value is " + _mapValue);
                String _tmp = "VALUE," + _mapValue;
                _retArray.add(_tmp);
                _mapKey = "";
                // _returnSDTMStructure.put( _mapKey, _mapValue.toString());
            }
        }
        NamedNodeMap attributes = node.getAttributes();
        // System.out.println(getIndentSpaces(indent) + "NodeName: " + nodeName
        // + ", NodeValue: " + nodeValue);
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            // System.out.println(getIndentSpaces(indent + 2) +
            // "AttributeName: " + attribute.getNodeName() + ",
            // attributeValue: " + attribute.getNodeValue());
        }
        // write all child nodes recursively
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                writeDocumentToOutput(child, indent + 2);
            }
        }
    }

    /**
     * Saves XML Document into XML file.
     *
     * @param fileName XML file name
     * @param doc      XML document to save
     * @return <B>true</B> if method success <B>false</B> otherwise
     */
    public boolean saveXMLDocument(String fileName, Document doc) {
        //System.out.println("Saving XML file... " + fileName);
        // open output stream where XML Document will be saved
        File xmlOutputFile = new File(fileName);
        FileOutputStream fos;
        Transformer transformer;
        try {
            fos = new FileOutputStream(xmlOutputFile);
        } catch (FileNotFoundException e) {
            System.out.println("Error occured: " + e.getMessage());
            return false;
        }
        // Use a Transformer for output
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            System.out.println("Transformer configuration error: " + e.getMessage());
            return false;
        }
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(fos);
        // transform source into result will do save
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            System.out.println("Error transform: " + e.getMessage());
        }
        //System.out.println("XML file saved.");
        return true;
    }

    /**
     * Parses XML file and returns XML document.
     *
     * @param fileName XML file to parse
     * @return XML document or <B>null</B> if error occured
     */
    public Document parseFile(String fileName) {
        //System.out.println("Parsing XML file... " + fileName);
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
        File sourceFile = new File(fileName);
        try {
            doc = docBuilder.parse(sourceFile);
        } catch (SAXException e) {
            System.out.println("Wrong XML file structure: " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.out.println("Could not read source file: " + e.getMessage());
        }
        //System.out.println("XML file parsed");
        return doc;
    }

    /**
     * Starts XML parsing example
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new ParseSDTMXMLFile("c:\\define.xml");
    }
}
/**
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2007/08/16 19:04:58  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */
