/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.sdtm;

import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.LinkedHashMap;

/**
 * This class file returns the linked hash map from a map file
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.3 $
 *          $Date: 2008-06-06 18:55:19 $
 */
public class GetMappings {
    public static void main(String[] arg) throws Exception {
        System.out.println(GetMappings.returnMappings("D:\\harshatest\\bb.map"));
    }

    public static LinkedHashMap returnMappings(String file) throws Exception {
        LinkedHashMap _mappedData = new LinkedHashMap();
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(new File(file));
        //System.out.println( "Root element of the doc is " + doc.getDocumentElement().getNodeName());
        NodeList linkNodeList = doc.getElementsByTagName("link");
        int totalPersons = linkNodeList.getLength();
        //System.out.println( "Total no of links are : " + totalPersons);
        // System.out.println( defineXMLList.toString());
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
                EmptyStringTokenizer _str1 = new EmptyStringTokenizer(_srcNodeVal, "\\");
                _str1.deleteTokenAt(_str1.countTokens() - 1);
                String _tmp = _str.getTokenAt(_str.countTokens() - 2);
                String sourceNodeValue = _str.getTokenAt(_str.countTokens() - 1);
                // _mappedData.put(finalTargetName, _tmp+"%"+sourceNodeValue);
                StringBuffer _sBuf = new StringBuffer();
                String _finalString = _str1.toString().substring(0, _str1.toString().lastIndexOf('\\'));
                if (_mappedData.get(_finalString) == null) {
                    _sBuf.append(sourceNodeValue + "?" + finalTargetName);
                    _mappedData.put(_finalString, _sBuf);
                } else {
                    StringBuffer _tBuf = (StringBuffer) _mappedData.get(_finalString);
                    _tBuf.append("," + sourceNodeValue + "?" + finalTargetName);
                    _mappedData.put(_finalString, _tBuf);
                }
            }
        }
        return _mappedData;
    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2007/08/16 19:04:58  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */
