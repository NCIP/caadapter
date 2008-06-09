/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.hl7.mif.v1;

import java.util.Hashtable;

import gov.nih.nci.caadapter.hl7.datatype.XSDParserUtil;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;

import org.w3c.dom.Node;

/**
 * The class will parse a MIF class from the mif XML file.
 * 
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision $Revision: 1.4 $ date $Date: 2008-06-09 19:53:50 $
 */
public class ClassParser {
	public MIFClass parseClass(Node node, String prefix, Hashtable<String, String> participantTraversalName) {
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
        				MIFClass specializedMIFClass = specializedClass.parseSpecializedClass(specializedChild, prefix,null);
        				if (participantTraversalName!=null)
        					specializedMIFClass.setTraversalName(participantTraversalName.get(specializedMIFClass.getName()));
        				
        				specializedMIFClass.setSortKey(XSDParserUtil.getAttribute(child, "sortKey"));
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
