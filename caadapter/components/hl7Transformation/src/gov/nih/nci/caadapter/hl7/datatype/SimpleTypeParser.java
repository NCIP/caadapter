/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.hl7.datatype;

import java.util.HashSet;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * The class will parse a simple HL7 Datatype from the xsd file.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision $Revision: 1.6 $ date $Date: 2008-06-09 19:53:50 $
 */

public class SimpleTypeParser {

	private static HashSet allChild = new HashSet();
	private static HashSet allWithinChild = new HashSet();
	private static HashSet allBase = new HashSet();

	/**
	 * @param node the section of xsd which defines a complex HL7 v3 datatype
	 * @param prefix the prefix to all xml elements. For example <xs:attribute>
	 * xs: is the prefix of the element
	 */
	public static Datatype parseSimple(Node node, String prefix) {

		Datatype datatype = new Datatype();

		datatype.setSimple(true);
		datatype.setName(XSDParserUtil.getAttribute(node,"name"));

        Node child = node.getFirstChild();

        while (child != null) {

        	//Parsing child node with type "restriction"
        	allChild.add(child.getNodeName());
        	if (child.getNodeName().equals(prefix+"restriction")) {

        		String parentType = XSDParserUtil.getAttribute(child, "base");
        		datatype.setParents(parentType);
        		allBase.add(parentType);
        		if (!XSDParserUtil.isXSDType(parentType, prefix)) {
        			//copy attribute
        			//TO DO

        		}

        		Node restrictionChild = child.getFirstChild();
                while (restrictionChild != null) {
                	allWithinChild.add(restrictionChild.getNodeName());
                	if (restrictionChild.getNodeName().equals(prefix + "enumeration")) {
                		datatype.addPredefinedValue(XSDParserUtil.getAttribute(restrictionChild, "value"));
                	}
                	if (restrictionChild.getNodeName().equals(prefix + "pattern")) {
                		datatype.addPattern(XSDParserUtil.getAttribute(restrictionChild,"value"));
                	}
                	if (restrictionChild.getNodeName().equals(prefix + "minInclusive"));//ignore for now
                	if (restrictionChild.getNodeName().equals(prefix + "minLength"));//ignore for now
                	if (restrictionChild.getNodeName().equals(prefix + "maxInclusive"));//ignore for now
                	if (restrictionChild.getNodeName().equals("#text")); //ignore
                	restrictionChild = restrictionChild.getNextSibling();
                }
        	}
        	if (child.getNodeName().equals("#text"));//ignore for now
        	if (child.getNodeName().equals(prefix+"annotation")); //ignore for now
        	if (child.getNodeName().equals(prefix+"union"))
        	{
//        		System.out.println("Datatype:" + datatype.getName() + " " + XSDParserUtil.getAttribute(child, "memberTypes"));
        		datatype.setUnions(XSDParserUtil.getAttribute(child, "memberTypes"));
        		parseSimpleWithinUnion(child, prefix, datatype);
        	}
        	if (child.getNodeName().equals(prefix+"list"))
        	{
        		datatype.setUnions(XSDParserUtil.getAttribute(child, "itemType"));
        	}

            child = child.getNextSibling();
        }
        return datatype;
	}

	public static void parseSimpleWithinUnion(Node node, String prefix, Datatype datatype) {

        Node children = node.getFirstChild();
        while (children != null) {
        	if (children.getNodeName().equals(prefix+"simpleType")) {
                Node child = children.getFirstChild();
        		while (child != null) {

        			//Parsing child node with type "restriction"
        			allChild.add(child.getNodeName());
        			if (child.getNodeName().equals(prefix+"restriction")) {
        				Node restrictionChild = child.getFirstChild();
        				while (restrictionChild != null) {
        					allWithinChild.add(restrictionChild.getNodeName());
        					if (restrictionChild.getNodeName().equals(prefix + "enumeration")) {
        						datatype.addPredefinedValue(XSDParserUtil.getAttribute(restrictionChild, "value"));
        					}
                        	restrictionChild = restrictionChild.getNextSibling();
        				}
        			}

        			child = child.getNextSibling();
        		}
        	}
        	children = children.getNextSibling();
        }
	}
	public static void printMeta() {
		System.out.println("all child nodes = " + allChild);
		System.out.println("all base nodes = " + allBase);
		System.out.println("all nodes within child nodes is " + allWithinChild);
	}
}
