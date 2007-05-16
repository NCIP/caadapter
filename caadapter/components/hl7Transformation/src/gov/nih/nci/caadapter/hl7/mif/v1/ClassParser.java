/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.hl7.mif.v1;

import gov.nih.nci.caadapter.hl7.datatype.XSDParserUtil;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;

import org.w3c.dom.Node;

/**
 * The class will parse a MIF class from the mif XML file.
 * 
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wuye $
 * @version Since caAdapter v4.0 revision $Revision: 1.1 $ date $Date: 2007-05-16 20:20:59 $
 */
public class ClassParser {
	public MIFClass parseClass(Node node, String prefix) {
		MIFClass mifClass = new MIFClass();
		mifClass.setName(XSDParserUtil.getAttribute(node, "name"));
		if (XSDParserUtil.getAttribute(node, "sortKey") == null) {
			mifClass.setSortKey("");
		}
		else {
			mifClass.setSortKey(XSDParserUtil.getAttribute(node, "sortKey"));
		}
		mifClass.setName(XSDParserUtil.getAttribute(node, "name"));
		
        Node child = node.getFirstChild();
        while (child != null) {
        	if (child.getNodeName().equals(prefix+"attribute")) {
        		MIFAttribute mifAttribute = AttributeParser.parseAttribute(child, prefix);
        		mifClass.addAttribute(mifAttribute);
        	}
        	if (child.getNodeName().equals(prefix+"specializationChild")) {
        		Node specializedChild = child.getFirstChild();
        		while (specializedChild != null) {
        			if (specializedChild.getNodeName().equals(prefix+"specializedClass")) {
        				SpecializedClassParser specializedClass = new SpecializedClassParser();
        				MIFClass specializedMIFClass = specializedClass.parseSpecializedClass(specializedChild, prefix);
        				mifClass.addChoice(specializedMIFClass);
        			}
        			specializedChild = specializedChild.getNextSibling();
        		}
        	}
        	if (child.getNodeName().equals(prefix+"association")) {
        		AssociationParser associationParser = new AssociationParser();
        		MIFAssociation mifAssociation = associationParser.parseAttribute(child, prefix);
        		mifClass.addAssociation(mifAssociation);
        		
        	}
            child = child.getNextSibling();
        }
		return mifClass;
	}
}
