/*L
 * Copyright SAIC.
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
import gov.nih.nci.caadapter.hl7.mif.v1.MIFParserUtil;

/**
 * The class will parse a complex HL7 Datatype from the xsd file.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: altturbo $
 * @version Since caAdapter v4.0 revision $Revision: 1.5 $ date $Date: 2009-11-11 20:27:44 $
 */

public class ComplexTypeParser {

    private static HashSet allChild = new HashSet();
    private static HashSet allWithinComplexContent = new HashSet();
    private static HashSet allRestriction = new HashSet();
    private static HashSet allExtension = new HashSet();
    private static HashSet allSequence = new HashSet();

    /**
     * @param node the section of xsd which defines a complex HL7 v3 datatype
     * @param prefix the prefix to all xml elements. For example <xs:attribute>
     * xs: is the prefix of the element
     */

    public static Datatype parseComplex(Node node, String prefix) {

        Datatype datatype = new Datatype();

        datatype.setSimple(false);
        datatype.setName(XSDParserUtil.getAttribute(node,"name"));
        datatype.setAbstract(false);
        if (XSDParserUtil.getAttribute(node, "abstract")!= null) {
            if (XSDParserUtil.getAttribute(node, "abstract").equals("true")) {
                datatype.setAbstract(true);
            }
        }
        Node child = node.getFirstChild();

        String annotation = null;

        while (child != null) {

            //Parsing child node with type "complexContent"
            allChild.add(child.getNodeName());
            if (child.getNodeName().equals(prefix+"complexContent")) {
                Node complexContentChild = child.getFirstChild();
                while (complexContentChild != null) {
                    allWithinComplexContent.add(complexContentChild.getNodeName());
                    if (complexContentChild.getNodeName().equals(prefix+"restriction")){
                        datatype.setParents(XSDParserUtil.getAttribute(complexContentChild,"base"));
                        Node restrictionChild = complexContentChild.getFirstChild();
                        while (restrictionChild!= null) {
                            allRestriction.add(restrictionChild.getNodeName());
                            if (restrictionChild.getNodeName().equals(prefix+"sequence")) {
                                processSequence(datatype, restrictionChild, prefix);
                            }
                            if (restrictionChild.getNodeName().equals(prefix+"attribute")) {
                                datatype.addAttribute(XSDParserUtil.getAttribute(restrictionChild, "name"), addAttribute(restrictionChild, true));
                            }
                            restrictionChild = restrictionChild.getNextSibling();
                        }
                    }
                    if (complexContentChild.getNodeName().equals(prefix+"extension")){
                        datatype.setParents(XSDParserUtil.getAttribute(complexContentChild,"base"));
                        Node extensionChild = complexContentChild.getFirstChild();
                        while (extensionChild!= null) {
                            allExtension.add(extensionChild.getNodeName());
                            if (extensionChild.getNodeName().equals(prefix+"sequence")) {
                                processSequence(datatype, extensionChild, prefix);
                            }
                            if (extensionChild.getNodeName().equals(prefix+"attribute")) {
                                datatype.addAttribute(XSDParserUtil.getAttribute(extensionChild, "name"), addAttribute(extensionChild, true));
                            }
                            if (extensionChild.getNodeName().equals(prefix+"choice")) {
                                processInterval(datatype, extensionChild, prefix);
                            }
                            extensionChild = extensionChild.getNextSibling();
                        }
                    }
                    complexContentChild = complexContentChild.getNextSibling();
                }
            }
            if (child.getNodeName().equals("#text"));//ignore for now
            if (child.getNodeName().equals(prefix+"annotation")||child.getNodeName().equals("annotation")||
                child.getNodeName().equals(prefix+"annotations")||child.getNodeName().equals("annotations"))
            {
                annotation = MIFParserUtil.searchAnnotation(child);
            }

            if (child.getNodeName().equals(prefix+"attribute")) {
                datatype.addAttribute(XSDParserUtil.getAttribute(child, "name"), addAttribute(child, true));
            }

            child = child.getNextSibling();
        }
//&umkis        if ((datatype != null)&&(annotation != null)) datatype.setAnnotation(annotation);

        return datatype;
    }
    public static void printMeta() {
        System.out.println("all child nodes = " + allChild);
        System.out.println("all nodes within complexContent nodes is " + allWithinComplexContent);
        System.out.println("all nodes within restriction node = " + allRestriction);
        System.out.println("all nodes within extension node = " + allExtension);
        System.out.println("all nodes within sequence node = " + allSequence);
    }

    /*
      * processSequence method will parse the <sequence> element within the <restrict> or <extension>
      * element.
      *
      * After reviewing the xsd document, we found all <choice> elements are actually parts of that datatype
      */
    private static void processSequence(Datatype datatype, Node node, String prefix) {
        Node sequenceChild = node.getFirstChild();
        while (sequenceChild!= null) {
            allSequence.add(sequenceChild.getNodeName());

            //choice element with in sequence is dealing with parts
            if (sequenceChild.getNodeName().equals(prefix+"choice")) {
                Node choices = sequenceChild.getFirstChild();
                while (choices != null) {
                    if (choices.getNodeName().equals(prefix+"element")) {
                        datatype.addAttribute(XSDParserUtil.getAttribute(choices, "name"), addAttribute(choices, false));
                    }
                    choices = choices.getNextSibling();
                }
            }

            //element is a variable in a complex type that is defined as another complextype
            if (sequenceChild.getNodeName().equals(prefix+"element")) {
                datatype.addAttribute(XSDParserUtil.getAttribute(sequenceChild, "name"), addAttribute(sequenceChild, false));
            }
            sequenceChild = sequenceChild.getNextSibling();
        }

    }
    /*
     * processInterval will process the timestamp related datatypes.
     *
     *  The XSD is very complicated for this type but after reviewing the XSD,
     *  it is clear the complication is due the fact that XSD needs to cover all
     *  different scenarios (including missing valus). For use we only need "low", "high", "width" and etc.
     *
     *  Therefore, the interval parser will just extract those attributes and there corresponding datatypes.
     */
    private static void processInterval(Datatype datatype, Node node, String prefix) {
        Node lowNode = XSDParserUtil.getFirstChildElement(node);

        Attribute lowAttribute = new Attribute();
        lowAttribute.setName("low");
        lowAttribute.setAttribute(false);
        lowAttribute.setType(XSDParserUtil.getAttribute(XSDParserUtil.getFirstChildElement(lowNode), "type"));
        lowAttribute.setMax(-1);
        datatype.addAttribute("low", lowAttribute);

        if (lowNode == null) return;
        Node highNode = XSDParserUtil.getNextElement(lowNode);

        Attribute highAttribute = new Attribute();
        highAttribute.setName("high");
        highAttribute.setAttribute(false);
        highAttribute.setType(XSDParserUtil.getAttribute(highNode, "type"));
        highAttribute.setMax(-1);
        datatype.addAttribute("high", highAttribute);

        if (highNode == null) return;
        Node widthNode = XSDParserUtil.getNextElement(highNode);

        Attribute widthAttribute = new Attribute();
        widthAttribute.setName("width");
        widthAttribute.setAttribute(false);
        widthAttribute.setType(XSDParserUtil.getAttribute(XSDParserUtil.getFirstChildElement(widthNode), "type"));
        widthAttribute.setMax(-1);
        datatype.addAttribute("width", widthAttribute);

        if (widthNode == null) return;
        Node centerNode = XSDParserUtil.getNextElement(widthNode);

        Attribute centerAttribute = new Attribute();
        centerAttribute.setName("center");
        centerAttribute.setAttribute(false);
        centerAttribute.setType(XSDParserUtil.getAttribute(XSDParserUtil.getFirstChildElement(centerNode), "type"));
        centerAttribute.setMax(-1);
        datatype.addAttribute("center", centerAttribute);
    }

    /*
      * addAttribute method will parse an <attribute> element and
      * create an Attribute object to be added to the corresponding
      * datatype object
      */
    private static Attribute addAttribute(Node node, boolean isAttribute) {
        Attribute attribute = new Attribute();
        attribute.setName(XSDParserUtil.getAttribute(node, "name"));
        attribute.setType(XSDParserUtil.getAttribute(node, "type"));
        attribute.setAttribute(isAttribute);
        if (XSDParserUtil.getAttribute(node, "use")!= null) {
            if (XSDParserUtil.getAttribute(node, "use").equals("optional"))
                attribute.setOptional(true);
            else if (XSDParserUtil.getAttribute(node, "use").equals("prohibited"))
                attribute.setProhibited(true);
            else {
                attribute.setOptional(false);
                attribute.setProhibited(false);
            }
        }

        if (XSDParserUtil.getAttribute(node, "default")!= null)
            attribute.setDefaultValue(XSDParserUtil.getAttribute(node, "default"));


        if (XSDParserUtil.getAttribute(node, "minOccurs")!= null)
            attribute.setMin(Integer.parseInt(XSDParserUtil.getAttribute(node, "minOccurs")));
        else
            attribute.setMin(-2);

        if (XSDParserUtil.getAttribute(node, "maxOccurs")!= null) {
            if (XSDParserUtil.isMultiple(node, "maxOccurs"))
                attribute.setMax(-1);
            else
                attribute.setMax(Integer.parseInt(XSDParserUtil.getAttribute(node, "maxOccurs")));
            }
        else {
            attribute.setMax(-2);
        }
        return attribute;
    }
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.4  2008/09/29 15:48:56  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */