package gov.nih.nci.caadapter.ui.mapping.sdtm;

import gov.nih.nci.caadapter.common.csv.data.CSVSegment;
import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
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
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: Jun 14, 2007
 * Time: 10:54:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class RDSHelper
{

    public static String getDefineXMLNameFromMapFile(String mapFile) throws Exception
    {
        String defineXMLName = "";
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(new File(mapFile));
        //System.out.println( "Root element of the doc is " + doc.getDocumentElement().getNodeName());
        NodeList linkNodeList = doc.getElementsByTagName("components");
        int totalPersons = linkNodeList.getLength();
        //System.out.println( "Total no of links are : " + totalPersons);
        // System.out.println( defineXMLList.toString());
        for (int s = 0; s < linkNodeList.getLength(); s++)
        {
            Node node = linkNodeList.item(s);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element firstCompElement = (Element) node;
                NodeList targetNode = firstCompElement.getElementsByTagName("component");
                Element targetName1 = (Element) targetNode.item(0);
                targetName1.getAttribute("location").toString();
                Element targetName2 = (Element) targetNode.item(1);
                if (targetName2.getAttribute("kind").toString().equalsIgnoreCase("XML"))
                {
                    defineXMLName = targetName2.getAttribute("location").toString();
                }
            }
        }
        return defineXMLName;
    }

    public static String getSCSFileFromMapFile(File mapFile) throws Exception
    {
        String scsFileName = null;
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(mapFile);
        NodeList linkNodeList = doc.getElementsByTagName("components");
        for (int s = 0; s < linkNodeList.getLength(); s++)
        {
            Node node = linkNodeList.item(s);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element firstCompElement = (Element) node;
                NodeList targetNode = firstCompElement.getElementsByTagName("component");
                Element targetName1 = (Element) targetNode.item(0);
                scsFileName = targetName1.getAttribute("location").toString();               
            }
        }
        return scsFileName;
    }

    public static Hashtable getAllFieldsForDomains(File SDTMXmlFile)
    {
        ParseSDTMXMLFile _parseSDTMFile = new ParseSDTMXMLFile(SDTMXmlFile.getAbsolutePath().toString());
        ArrayList _retArray = _parseSDTMFile.getSDTMStructure();
        Hashtable domainFieldsList = new Hashtable();
        DefaultMutableTreeNode pNode = null;
        String domainString = "";
        ArrayList fieldsString = null;
        String _tempHolder;
        domainFieldsList = new Hashtable();
        for (int k = 0; k < _retArray.size(); k++)
        {
            if (_retArray.get(k).toString().startsWith("KEY"))
            {
                if (fieldsString != null)
                    domainFieldsList.put(domainString, fieldsString);
                fieldsString = new ArrayList();
                EmptyStringTokenizer _str = new EmptyStringTokenizer(_retArray.get(k).toString(), ",");
                pNode = new DefaultMutableTreeNode(_str.getTokenAt(1).substring(0, 2));
                domainString = pNode.toString();
            } else
            {
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

    public static String getParentasXPath(CSVSegment csvsegment)
    {
        ArrayList list = new ArrayList();
        StringBuffer sb = new StringBuffer();
        try
        {
            while (true)
            {
                list.add(csvsegment.getMetaObject());
                csvsegment = csvsegment.getParentSegment();
                if (csvsegment.getParentSegment() == null)
                {
                    list.add(csvsegment.getMetaObject());
                    break;
                }
            }
        } catch (Exception e)
        {
        }
        for (int l = 1; l < list.size() + 1; l++)
        {
            try
            {
                int sizeNow = list.size() - l;
                sb.append("\\" + list.get(sizeNow));
            } catch (Exception ed)
            {
            }
        }
        return "\\Source Tree" + sb.toString();
    }
}
