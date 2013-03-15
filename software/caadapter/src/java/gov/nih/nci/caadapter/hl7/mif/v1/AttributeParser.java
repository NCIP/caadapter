/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.hl7.mif.v1;

import gov.nih.nci.caadapter.hl7.datatype.XSDParserUtil;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;

import org.w3c.dom.Node;

/**
 * The class will parse an MIF attribute section  from the mif XML file.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0 revision $Revision: 1.4 $ date $Date: 2008-09-29 15:42:45 $
 */
public class AttributeParser {
	public static synchronized MIFAttribute parseAttribute(Node node, String prefix) {
		MIFAttribute mifAtrribute = new MIFAttribute();
		mifAtrribute.setName(XSDParserUtil.getAttribute(node, "name"));
		mifAtrribute.setConformance(XSDParserUtil.getAttribute(node, "conformance"));
		mifAtrribute.setDefaultFrom(XSDParserUtil.getAttribute(node, "defaultFrom"));
		mifAtrribute.setDefaultValue(XSDParserUtil.getAttribute(node, "defaultValue"));
		mifAtrribute.setFixedValue(XSDParserUtil.getAttribute(node, "fixedValue"));
		if (XSDParserUtil.getAttribute(node, "isMandatory")!= null) {
			mifAtrribute.setMandatory(Boolean.parseBoolean(XSDParserUtil.getAttribute(node, "isMandatory")));
		}
		else {
			mifAtrribute.setMandatory(false);
		}

		if (XSDParserUtil.getAttribute(node, "isStructural")!= null) {
			mifAtrribute.setStrutural(Boolean.parseBoolean(XSDParserUtil.getAttribute(node, "isStructural")));
		}
		else {
			mifAtrribute.setStrutural(false);
		}

		if (XSDParserUtil.getAttribute(node, "minimumMultiplicity")!= null)
			mifAtrribute.setMinimumMultiplicity(Integer.parseInt(XSDParserUtil.getAttribute(node, "minimumMultiplicity")));
		else
			mifAtrribute.setMinimumMultiplicity(-2);

		if (XSDParserUtil.getAttribute(node, "maximumMultiplicity")!= null) {
			if (XSDParserUtil.isMultiple(node, "maximumMultiplicity"))
				mifAtrribute.setMaximumMultiplicity(-1);
			else
				mifAtrribute.setMaximumMultiplicity(Integer.parseInt(XSDParserUtil.getAttribute(node, "maximumMultiplicity")));
			}
		else {
			mifAtrribute.setMaximumMultiplicity(-2);
		}
		if (XSDParserUtil.getAttribute(node, "minimumSupportedLength") != null)
			mifAtrribute.setMinimumSupportedLength(Integer.parseInt(XSDParserUtil.getAttribute(node, "minimumSupportedLength")));
		mifAtrribute.setSortKey(XSDParserUtil.getAttribute(node, "sortKey"));

		Node child = node.getFirstChild();

        while (child != null) {
//        	if (child.getNodeName().equals(prefix+"supplierDomainSpecification"))
        	if (child.getNodeName().endsWith("supplierDomainSpecification"))
        	{
        		mifAtrribute.setDomainName(XSDParserUtil.getAttribute(child, "domainName"));
        		mifAtrribute.setMnemonic(XSDParserUtil.getAttribute(child, "mnemonic"));
        		mifAtrribute.setCodingStrength(XSDParserUtil.getAttribute(child, "codingStrength"));
        	}
        	if (child.getNodeName().endsWith("type")) {//.equals(prefix+"type")) {
        		String type = "";
        		String typeName = XSDParserUtil.getAttribute(child, "name");
        		if (!(typeName.equals("SET")||typeName.equals("BAG")||typeName.equals("LIST"))) {
        			type = XSDParserUtil.getAttribute(child, "name");
        		}

        		Node firstChild = XSDParserUtil.getFirstChildElement(child);
        		if (firstChild!= null) {
        			if (firstChild.getNodeName().endsWith("supplierBindingArgumentDatatype")) { //.equals("supplierBindingArgumentDatatype")) {
        				if (!type.equals("")) type = type + "_";
        				type = type + XSDParserUtil.getAttribute(firstChild, "name");
        				Node secondChild = XSDParserUtil.getNextElement(firstChild);
        				if (secondChild != null)
        					if (secondChild.getNodeName().endsWith("supplierBindingArgumentDatatype")) {//.equals("supplierBindingArgumentDatatype")) {
        						if (!type.equals("")) type = type + "_";
        						type = type + XSDParserUtil.getAttribute(secondChild, "name");
        					}
        			}
        		}
        		mifAtrribute.setType(type);
        	}
            child = child.getNextSibling();
        }

        return mifAtrribute;
	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 */