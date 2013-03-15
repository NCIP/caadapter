/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.hl7.mif.v1;

import java.util.Hashtable;
import java.util.List;

import gov.nih.nci.caadapter.hl7.datatype.XSDParserUtil;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The class will parse an MIF association section  from the mif XML file.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision $Revision: 1.5 $ date $Date: 2008-06-09 19:53:50 $
 */
public class AssociationParser {
	public MIFAssociation parseAttribute(Node node, String prefix) {
		MIFAssociation mifAssociation = new MIFAssociation();

		Node child = node.getFirstChild();
//		set the association sortedKey
		mifAssociation.setSortKey(XSDParserUtil.getAttribute(node, "sortKey"));

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
//        		mifAssociation.setSortKey(XSDParserUtil.getAttribute(child, "sortKey"));
        		//define a hashtable to keep the ClassName and ReversalName pair for future processing

        		Hashtable <String, String>participantClassTraversalName=new Hashtable<String, String>();
        		NodeList children=child.getChildNodes();
        		for (int childrenIndex=0;childrenIndex<children.getLength();childrenIndex++)
        		{
        			Node itemNode=children.item(childrenIndex);
        			if (itemNode.getNodeName().equals(prefix+"participantClassSpecialization"))
        			{
        				String participantClassName=XSDParserUtil.getAttribute(itemNode,"className");
        				String participantTraversalName=XSDParserUtil.getAttribute(itemNode,"traversalName");
        				if (participantClassName!=null
        						&&participantTraversalName!=null)
        					participantClassTraversalName.put(participantClassName, participantTraversalName);
        			}

        		}
        		mifAssociation.setParticipantTraversalNames(participantClassTraversalName);

        		Node targetConnectionChild = child.getFirstChild();
        		while (targetConnectionChild != null) {
                	if (targetConnectionChild.getNodeName().equals(prefix+"participantClass")) {
                		SpecializedClassParser specializedClassParser = new SpecializedClassParser();
                		MIFClass participantClass = specializedClassParser.parseSpecializedClass(targetConnectionChild, prefix,participantClassTraversalName);
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
