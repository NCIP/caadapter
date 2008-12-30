/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.hl7.mif.v1;

import java.util.Hashtable;
import java.util.Iterator;

import gov.nih.nci.caadapter.hl7.datatype.XSDParserUtil;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;

import org.w3c.dom.Node;

/**
 * The class will parse a MIF class from the mif XML file.
 * 
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0 revision $Revision: 1.9 $ date $Date: 2008-12-30 14:54:02 $
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
		String abstractFlag=XSDParserUtil.getAttribute(node, "isAbstract");
		if ((abstractFlag!=null)&&(abstractFlag.equalsIgnoreCase("true")))
				mifClass.setAbstractDefined(true);
 
        Node child = node.getFirstChild();
        while (child != null) {
        	if (child.getNodeName().equals(prefix+"attribute")
        			||child.getNodeName().equals("attribute")) {
        		MIFAttribute mifAttribute = AttributeParser.parseAttribute(child, prefix);
        		mifClass.addAttribute(mifAttribute);
        	}
        	if (child.getNodeName().equals("specializationChild")
        			||child.getNodeName().equals(prefix+"specializationChild")) {
           		Node specializedChild = child.getFirstChild();
        		while (specializedChild != null) {
        			if (specializedChild.getNodeName().equals(prefix+"specializedClass")
        					||specializedChild.getNodeName().equals("specializedClass")) {
        				SpecializedClassParser specializedClassParser = new SpecializedClassParser();
        				System.out.println("ClassParser.parseClass()..traversal:"+participantTraversalName);
        				MIFClass specializedMIFClass = specializedClassParser.parseSpecializedClass(specializedChild, prefix,null);
        				if (participantTraversalName!=null)
        					specializedMIFClass.setTraversalName(participantTraversalName.get(specializedMIFClass.getName()));
        				
        				specializedMIFClass.setSortKey(XSDParserUtil.getAttribute(child, "sortKey"));
        				if (specializedMIFClass.isAbstractDefined())
        				{
        					for (MIFAssociation asbtractAssc:specializedMIFClass.getAssociations())
    						{
        						//make the association abstract
        						//all these abstract MIFAssociation with an abstract MIFClass
        						//will be push down concrete children MIFClass 
        						//in MIFClass.getSortChoice();
        						asbtractAssc.setAbstractDefined(true);
        					}
        				}
        				//add all the choice item into holder class since it may 
        				//be required to resolve undefined class in the future.
        				//But a item will be able chosen if it is a list of other MIFClass
       					mifClass.addChoice(specializedMIFClass);
        			}
        			specializedChild = specializedChild.getNextSibling();
        		}
        	}
        	if (child.getNodeName().equals(prefix+"association")
        			||child.getNodeName().equals("association")) {
        		AssociationParser associationParser = new AssociationParser();
        		MIFAssociation mifAssociation = associationParser.parseAttribute(child, prefix);
        		mifClass.addAssociation(mifAssociation);
        		
        	}
            child = child.getNextSibling();
        }
		return mifClass;
	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.8  2008/12/18 17:10:04  wangeug
 * HISTORY :MIF Parsing: A item of a choice is a list of other MIFClass.
 * HISTORY :
 * HISTORY :Revision 1.7  2008/12/11 17:05:37  wangeug
 * HISTORY :MIF Parsing: A item of a choice is a list of other MIFClass.
 * HISTORY :
 * HISTORY :Revision 1.6  2008/09/29 15:42:44  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */