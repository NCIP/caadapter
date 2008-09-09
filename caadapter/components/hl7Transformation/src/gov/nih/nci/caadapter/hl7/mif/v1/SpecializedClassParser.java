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
import gov.nih.nci.caadapter.hl7.mif.MIFClass;

import org.w3c.dom.Node;

/**
 * The class will parse a specializedClass section  from the mif XML file.
 * 
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0 revision $Revision: 1.4 $ date $Date: 2008-09-09 18:27:23 $
 */
public class SpecializedClassParser {
	public MIFClass parseSpecializedClass(Node node,String prefix, Hashtable<String,String> participantTraversalNames) {
		
        Node child = node.getFirstChild();
        while (child != null) {
        	if (child.getNodeName().equals(prefix+"class")
        			||child.getNodeName().equals("class")) {
        		ClassParser classParser = new ClassParser();
        		return classParser.parseClass(child, prefix,participantTraversalNames);
        	}
        	if (child.getNodeName().equals(prefix+"commonModelElementRef")
        			||child.getNodeName().equals("commonModelElementRef")) {
        		CMETRefParser cmetRefParser = new CMETRefParser();
        		return cmetRefParser.parseCMetRef(child, prefix,false);
        	}
        	/*
        	 * The following is not part of the spec but appears in MIF files anyway. The reference 
        	 * is a link to a class of the same CMET. (Personal observation)
        	 */
        	if (child.getNodeName().equals(prefix+"reference")
        			||child.getNodeName().equals("reference")) {
        		CMETRefParser cmetRefParser = new CMETRefParser();
        		return cmetRefParser.parseCMetRef(child, prefix, true);
        	}
        	//templateparameter will be ignore in this release
        	/*
        	 * To Do
        	 */
        	if (child.getNodeName().equals(prefix+"templateParameter")) {
        		MIFClass mifClass = new MIFClass();
        		mifClass.setName(XSDParserUtil.getAttribute(child, "name"));
        		mifClass.setDynamic(true);
        		return mifClass;
        	}
            child = child.getNextSibling();
        }
        return null;
	}
	

}
