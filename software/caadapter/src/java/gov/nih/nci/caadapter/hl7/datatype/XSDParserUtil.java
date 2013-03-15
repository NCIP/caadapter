/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.hl7.datatype;
/**
 * The class defines Utilitis of parsing a XSD file.
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-09-29 15:48:56 $
 */

import java.util.HashSet;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class XSDParserUtil {
	/*
	 * xsdTypes defined datetypes that are defined in xsd and are used by HL7 v3 datatypes.
	 */
	private static String[] xsdTypes = { "string",
				"boolean",
				"decimal",
				"double",
				"token",
				"NMTOKEN",
				"base64Binary",
				"anyURI",
				"integer"
	  };

	/**
	 * This method will determine whether a datatype is XSD defined datatype or
	 * a HL7 v3 defined datatype
	 *
	 * @param name the name of the datatype
	 * @param prefix the prefix to all xml elements. For example <xs:attribute>
	 * xs: is the prefix of the element
	 * @return true if it is a XSD defined datatype. Otherwise, return false.
	 */

	private static HashSet allElement = new HashSet();

	public static boolean isXSDType(String name, String prefix) {
		  for (int i=0; i<xsdTypes.length;i++) {
			  if ((prefix+xsdTypes[i]).equals(name)) return true;
		  }
		  return false;
	  }

	/**
	 * This method will get the value of an attribute for a given node
	 *
	 * @param node the XML node which contain the attribute
	 * @param name the name of the attrbiute
	 * @return String the string value of the attribute.
	 */

	public static String getAttribute(Node node, String name) {
		NamedNodeMap attrs = node.getAttributes();
		if (attrs == null) return null;
		Attr attr = (Attr)attrs.getNamedItem(name);
		if (attr == null) return null;
		else return attr.getNodeValue();
	}

	/**
	 * Get a first XML element of a given node. For example, with the following structure
	 *   <node1>
	 *     <node2>
	 *   getFirstChildElement(node1) will return a reference to <node2>
	 *
	 * @param node the pareent XML node
	 * @return Node the first child of a given XML node.
	 */


	  public static Node getFirstChildElement(Node node) {
		  if (node == null) return null;
          Node child = node.getFirstChild();
		  if (child == null) return null;
          if (child.getNodeType() == Node.ELEMENT_NODE) return child;
          while (child != null) {
        	  if (child.getNodeType() == Node.ELEMENT_NODE) return child;
              child = child.getNextSibling();
          }
          return null;
		}

		/**
		 * Get a sibling XML element of a given node. For example, with the following structure
		 *   <node1>
		 *     <node2>
		 *     <node3>
		 *   getNextElement(node2) will return a reference to <node3>
		 *
		 * @param node the pareent XML node
		 * @return Node the next XML sibling element.
		 */
	  public static Node getNextElement(Node node) {
          Node child = node.getNextSibling();
          if (child == null) return null;
          if (child.getNodeType() == Node.ELEMENT_NODE) return child;
          while (child != null) {
        	  if (child.getNodeType() == Node.ELEMENT_NODE) return child;
              child = child.getNextSibling();
          }
          return null;
		}

	  public static HashSet printAllNames(String fileName) throws Exception {
		  	allElement.removeAll(allElement);

	  		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	  		DocumentBuilder db = dbf.newDocumentBuilder();

			Document doc = db.parse(fileName);
			count(doc);

			Iterator allElementIterator = allElement.iterator();
			while (allElementIterator.hasNext()) {
				System.out.println(allElementIterator.next());
			}

			return allElement;
	  }

	  public static void count(Node node) {
	        if (node == null) {
	            return;
	        }

	        int type = node.getNodeType();
	        switch (type) {
	            case Node.DOCUMENT_NODE: {
	                Document document = (Document)node;
	                count(document.getDocumentElement());
	                break;
	            }

	            case Node.ELEMENT_NODE: {
	            	allElement.add(node.getNodeName());
	                Node child = node.getFirstChild();
	                while (child != null) {
	                    count(child);
	                    child = child.getNextSibling();
	                }
	                break;
	            }
	        }
	  }
	  public static boolean isMultiple(Node node, String attribute) {
		  if (XSDParserUtil.getAttribute(node, attribute).equals("unbounded") ||
			  XSDParserUtil.getAttribute(node, attribute).equals("*"))
			  return true;
		  return false;
		  }
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 */