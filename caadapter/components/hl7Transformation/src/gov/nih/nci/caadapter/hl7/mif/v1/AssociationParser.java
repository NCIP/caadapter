/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.hl7.mif.v1;

import gov.nih.nci.caadapter.hl7.datatype.XSDParserUtil;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;

import org.w3c.dom.Node;

/**
 * The class will parse an MIF association section  from the mif XML file.
 * 
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wuye $
 * @version Since caAdapter v4.0 revision $Revision: 1.1 $ date $Date: 2007-05-16 20:20:59 $
 */
public class AssociationParser {
	public MIFAssociation parseAttribute(Node node, String prefix) {
		MIFAssociation mifAssociation = new MIFAssociation();
		
		Node child = node.getFirstChild();
		
		while (child != null) {
        	if (child.getNodeName().equals(prefix+"targetConnection")) {
        		mifAssociation.setName(XSDParserUtil.getAttribute(child, "name"));
        		mifAssociation.setConformance(XSDParserUtil.getAttribute(child, "conformance"));
        		if (XSDParserUtil.getAttribute(child, "isMandatory")!= null) {
        			mifAssociation.setMandatory(Boolean.parseBoolean(XSDParserUtil.getAttribute(child, "isMandatory")));
        		}
        		else {
        			mifAssociation.setMandatory(false);
        		}
        		
        		if (XSDParserUtil.getAttribute(child, "minimumMultiplicity")!= null)
        			mifAssociation.setMinimumMultiplicity(Integer.parseInt(XSDParserUtil.getAttribute(child, "minimumMultiplicity")));
        		else 
        			mifAssociation.setMinimumMultiplicity(-2);
        		
        		if (XSDParserUtil.getAttribute(child, "maximumMultiplicity")!= null) {
        			if (XSDParserUtil.isMultiple(child, "maximumMultiplicity"))
        				mifAssociation.setMaximumMultiplicity(-1);
        			else
        				mifAssociation.setMaximumMultiplicity(Integer.parseInt(XSDParserUtil.getAttribute(child, "maximumMultiplicity")));
        			}
        		else {
        			mifAssociation.setMaximumMultiplicity(-2);							
        		}
        		mifAssociation.setSortKey(XSDParserUtil.getAttribute(child, "sortKey"));
        		
        		Node targetConnectionChild = child.getFirstChild();
        		while (targetConnectionChild != null) {
                	if (targetConnectionChild.getNodeName().equals(prefix+"participantClass")) {
                		SpecializedClassParser specializedClassParser = new SpecializedClassParser();
                		MIFClass participantClass = specializedClassParser.parseSpecializedClass(targetConnectionChild, prefix);
                		mifAssociation.setMifClass(participantClass);
                	}
        			targetConnectionChild = targetConnectionChild.getNextSibling();
        		}
        	}
			child = child.getNextSibling();
		}
        
        return mifAssociation;
	}
}
