/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.mif.v1;

import gov.nih.nci.caadapter.hl7.datatype.XSDParserUtil;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;

import org.w3c.dom.Node;

/**
 * The class will parse a commonModelElementRef section  from the mif XML file.
 * 
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wuye $
 * @version Since caAdapter v4.0 revision $Revision: 1.1 $ date $Date: 2007-05-16 20:20:59 $
 */
public class CMETRefParser {
	public MIFClass parseCMetRef(Node node,String prefix, boolean isReference) {
		MIFClass mifClass = new MIFClass();
		
   		mifClass.setReferenceName(XSDParserUtil.getAttribute(node, "name"));
   		mifClass.setName(XSDParserUtil.getAttribute(node, "name"));
   		mifClass.setReference(isReference);
        return mifClass;
	}

}
