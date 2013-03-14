/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
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
 * @author LAST UPDATE $Author: altturbo $
 * @version Since caAdapter v4.0 revision $Revision: 1.11 $ date $Date: 2009-11-11 20:26:39 $
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

        String annotation = null;

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
       					mifClass.addChoice(specializedMIFClass);
        			}
        			specializedChild = specializedChild.getNextSibling();
        		}
        	}
        	if (child.getNodeName().equals(prefix+"association")
        			||child.getNodeName().equals("association")) {
        		AssociationParser associationParser = new AssociationParser();
        		MIFAssociation mifAssociation = associationParser.parseAttribute(child, prefix);
        		//If this MIFClass is abstract, make all its association abstract
        		//these abstract association will be visible with its sub-class (choice item)
        		if (mifClass.isAbstractDefined())
        			mifAssociation.setAbstractDefined(true);
        		mifClass.addAssociation(mifAssociation);

        	}
            if (child.getNodeName().equals(prefix+"annotation")||child.getNodeName().equals("annotation")||
                child.getNodeName().equals(prefix+"annotations")||child.getNodeName().equals("annotations"))
            {
                annotation = MIFParserUtil.searchAnnotation(child);
            }
            child = child.getNextSibling();
        }
//&umkis        if ((mifClass != null)&&(annotation != null)) mifClass.setAnnotation(annotation);
        return mifClass;
	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.10  2009/01/05 16:40:07  wangeug
 * HISTORY :Process MIFClass with isAbstract=true
 * HISTORY :
 * HISTORY :Revision 1.9  2008/12/30 14:54:02  wangeug
 * HISTORY :Process MIFClass with isAbstract=true: create MIF class as abstract and make all its MIFAssociation abstract
 * HISTORY :
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