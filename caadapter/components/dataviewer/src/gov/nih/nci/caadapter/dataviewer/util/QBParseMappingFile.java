package gov.nih.nci.caadapter.dataviewer.util;

import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
import org.w3c.dom.Document;
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
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: May 2, 2007
 * Time: 2:37:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class QBParseMappingFile
{

    private File mappingFile;

    private Node root;

    private Hashtable<String, ArrayList> hashTable = null;

    private HashSet<String> hashTblColumnsSet = null;

    public QBParseMappingFile(File file)
    {
        hashTable = new Hashtable<String, ArrayList>();
        hashTblColumnsSet = new HashSet<String>();
        this.mappingFile = file;
        Document doc = parseFile();
        root = doc.getDocumentElement();
        writeDocumentToOutput(root, 0);
    }

    public Document parseFile()
    {
        DocumentBuilder docBuilder;
        Document doc = null;
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setIgnoringElementContentWhitespace(true);
        try
        {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e)
        {
            System.out.println("Wrong parser configuration: " + e.getMessage());
            return null;
        }
        try
        {
            doc = docBuilder.parse(mappingFile);
        } catch (SAXException e)
        {
            System.out.println("Wrong XML file structure: " + e.getMessage());
            return null;
        } catch (IOException e)
        {
            System.out.println("Could not read source file: " + e.getMessage());
        }
        return doc;
    }

    public void writeDocumentToOutput(Node node, int indent)
    {
        if (node.getNodeName().equalsIgnoreCase("link"))
        {
            NodeList children = node.getChildNodes();
            String tempStr = "", tempDomain = "", tempTable = "", tempSt4Columns = "";
            for (int i = 0; i < children.getLength(); i++)
            {
                if (!(children.item(i).getNodeName()).equalsIgnoreCase("#text"))
                {
                    tempStr = children.item(i).getTextContent();
                    if (tempStr.startsWith("\\#document"))
                    {
                        tempDomain = formatDomain(tempStr);
                    } else if (tempStr.startsWith("\\Object Model"))
                    {
                        tempTable = formatTable(tempStr);
                        tempSt4Columns = tempStr;
                    }
                    if (tempDomain.length() > 0 && tempTable.length() > 0)
                    {
                        processXMLContents(tempDomain, tempTable);
                        formatColumn(tempDomain, tempSt4Columns);
                    }
                }
            }
        }
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++)
        {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE)
            {
                writeDocumentToOutput(child, indent + 2);
            }
        }
    }

    private void addColumns(String tempSt)
    {
        //        String tableName = tempSt.substring(0, tempSt.indexOf("+"));
        //        String columnName = tempSt.substring(tempSt.indexOf("+") + 1, tempSt.length());
        //        if (!hashTblColumns.containsKey(tableName))
        //        {
        //            ArrayList _tmpAry = new ArrayList();
        //            _tmpAry.add(columnName);
        //            hashTblColumns.put(tableName, _tmpAry);
        //        } else
        //        {
        //            ArrayList _tmpAry = hashTblColumns.get(tableName);
        //            if (_tmpAry != null && _tmpAry.size() > 0)
        //            {
        //                _tmpAry.add(columnName);
        //                hashTblColumns.remove(tableName);
        //                hashTblColumns.put(tableName, _tmpAry);
        //            }
        //        }
    }

    public HashSet<String> getHashTblColumns()
    {
        return hashTblColumnsSet;
    }

    public Hashtable<String, ArrayList> getHashTable()
    {
        return hashTable;
    }

    private void processXMLContents(String domainString, String tableString)
    {
        if (!hashTable.containsKey(domainString))
        {
            ArrayList _tmpAry = new ArrayList();
            _tmpAry.add(tableString);
            hashTable.put(domainString, _tmpAry);
        } else
        {
            ArrayList _tmpAry = hashTable.get(domainString);
            if (_tmpAry != null && _tmpAry.size() > 0)
            {
                _tmpAry.add(tableString);
                hashTable.remove(domainString);
                hashTable.put(domainString, _tmpAry);
            }
        }
    }

    private String formatDomain(String str)
    {
        EmptyStringTokenizer _empStr = new EmptyStringTokenizer(str, "\\");
        return _empStr.getTokenAt(6).substring(0, 2);
    }

    private String formatTable(String str)
    {
        EmptyStringTokenizer _empStr = new EmptyStringTokenizer(str, "\\");
        return _empStr.getTokenAt(2) + "." + _empStr.getTokenAt(3);
    }

    private void formatColumn(String domain, String str)
    {
        EmptyStringTokenizer _empStr = new EmptyStringTokenizer(str, "\\");
        hashTblColumnsSet.add(domain + "~" + _empStr.getTokenAt(2) + "." + _empStr.getTokenAt(3) + "~" + _empStr.getTokenAt(4));
    }

    public static void main(String args[])
    {
        QBParseMappingFile _qb = new QBParseMappingFile(new File("c:\\w.map"));
        System.out.println("value " + _qb.hashTblColumnsSet);
    }
}
