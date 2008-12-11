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
 * @version Since caAdapter v4.0 revision $Revision: 1.7 $ date $Date: 2008-12-11 17:05:37 $
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
        				if(specializedMIFClass.getChoices().size()>0)
        				{
        					//add child choice item,the specializedMIF class is not longer a choice
        					//since it is only a list of other choice items
        					System.out.println("ClassParser.parseClass()..add child choice MIFClasss..name:"+mifClass.getName());
        					Iterator choiceIt=specializedMIFClass.getSortedChoices().iterator();
        					while(choiceIt.hasNext())
        	                {
        	                	MIFClass choiceable=(MIFClass)choiceIt.next();
        	                	System.out.println("\t\tClassParser.parseClass()..child choice ..name:"+choiceable.getName()+"..traversal:"+choiceable.getTraversalName());
        	                	mifClass.addChoice(choiceable);
        	                }    
        					specializedMIFClass.getChoices().clear();
        				}
        				else
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
 * HISTORY :Revision 1.6  2008/09/29 15:42:44  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */