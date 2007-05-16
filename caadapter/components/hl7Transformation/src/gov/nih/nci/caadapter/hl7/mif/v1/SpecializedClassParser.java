/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.hl7.mif.v1;

import gov.nih.nci.caadapter.hl7.datatype.XSDParserUtil;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;

import org.w3c.dom.Node;

/**
 * The class will parse a specializedClass section  from the mif XML file.
 * 
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wuye $
 * @version Since caAdapter v4.0 revision $Revision: 1.1 $ date $Date: 2007-05-16 20:20:59 $
 */
public class SpecializedClassParser {
	public MIFClass parseSpecializedClass(Node node,String prefix) {
		
        Node child = node.getFirstChild();
        while (child != null) {
        	if (child.getNodeName().equals(prefix+"class")) {
        		ClassParser classParser = new ClassParser();
        		return classParser.parseClass(child, prefix);
        	}
        	if (child.getNodeName().equals(prefix+"commonModelElementRef")) {
        		CMETRefParser cmetRefParser = new CMETRefParser();
        		return cmetRefParser.parseCMetRef(child, prefix,false);
        	}
        	/*
        	 * The following is not part of the spec but appears in MIF files anyway. The reference 
        	 * is a link to a class of the same CMET. (Personal observation)
        	 */
        	if (child.getNodeName().equals(prefix+"reference")) {
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
