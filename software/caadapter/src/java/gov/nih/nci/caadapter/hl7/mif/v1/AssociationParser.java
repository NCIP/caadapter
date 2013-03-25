/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.hl7.mif.v1;

import java.util.Hashtable;
import gov.nih.nci.caadapter.hl7.datatype.XSDParserUtil;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The class will parse an MIF association section  from the mif XML file.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: altturbo $
 * @version Since caAdapter v4.0 revision $Revision: 1.9 $ date $Date: 2009-11-11 20:27:28 $
 */
public class AssociationParser {
	public MIFAssociation parseAttribute(Node node, String prefix) {
		MIFAssociation mifAssociation = new MIFAssociation();

		Node child = node.getFirstChild();
//		set the association sortedKey
		mifAssociation.setSortKey(XSDParserUtil.getAttribute(node, "sortKey"));

        String annotation = null;

        while (child != null) {
        	if (child.getNodeName().endsWith("targetConnection")){//.equals(prefix+"targetConnection")) {
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
        			if (itemNode.getNodeName().endsWith("participantClassSpecialization")){//.equals(prefix+"participantClassSpecialization"))	{
        				if (itemNode.getChildNodes().getLength()==0)
        				{//The participantClass is a choice item
        					String participantClassName=XSDParserUtil.getAttribute(itemNode,"className");
        					String participantTraversalName=XSDParserUtil.getAttribute(itemNode,"traversalName");
        					if (participantClassName!=null
        						&&participantTraversalName!=null)
        						participantClassTraversalName.put(participantClassName, participantTraversalName);
        				}
        				else
        				{
        					//The participantClass is a list, its children are choice items
        					NodeList chioceChildrenList=itemNode.getChildNodes();
        					for (int choiceChildrenIndex=0;choiceChildrenIndex<chioceChildrenList.getLength();choiceChildrenIndex++)
        					{
        						Node choiceChild=chioceChildrenList.item(choiceChildrenIndex);
        						if (choiceChild.getNodeName().endsWith("specialization")){//.equals(prefix+"specialization"))	{
        	        				String participantClassName=XSDParserUtil.getAttribute(choiceChild,"className");
        	        				String participantTraversalName=XSDParserUtil.getAttribute(choiceChild,"traversalName");
        	        				if (participantClassName!=null
        	        						&&participantTraversalName!=null)
        	        					participantClassTraversalName.put(participantClassName, participantTraversalName);
        						}
        					}
        				}
        			}

        		}
        		mifAssociation.setParticipantTraversalNames(participantClassTraversalName);

        		Node targetConnectionChild = child.getFirstChild();
        		while (targetConnectionChild != null) {
                	if (targetConnectionChild.getNodeName().endsWith("participantClass")){//.equals(prefix+"participantClass")) {
                		SpecializedClassParser specializedClassParser = new SpecializedClassParser();
                		MIFClass participantClass = specializedClassParser.parseSpecializedClass(targetConnectionChild, prefix,participantClassTraversalName);
                		mifAssociation.setMifClass(participantClass);
                	}
        			targetConnectionChild = targetConnectionChild.getNextSibling();
        		}
        	}
            if (child.getNodeName().equals(prefix+"annotation")||child.getNodeName().equals("annotation")||
                child.getNodeName().equals(prefix+"annotations")||child.getNodeName().equals("annotations"))
            {
                annotation = MIFParserUtil.searchAnnotation(child);
            }
            child = child.getNextSibling();
		}
//&umkis        if ((mifAssociation != null)&&(annotation != null)) mifAssociation.setAnnotation(annotation);
        return mifAssociation;
	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.8  2008/12/11 17:05:37  wangeug
 * HISTORY :MIF Parsing: A item of a choice is a list of other MIFClass.
 * HISTORY :
 * HISTORY :Revision 1.7  2008/09/29 15:42:45  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */