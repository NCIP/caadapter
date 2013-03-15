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
import gov.nih.nci.caadapter.hl7.mif.MIFClass;

import org.w3c.dom.Node;

/**
 * The class will parse a commonModelElementRef section  from the mif XML file.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision $Revision: 1.2 $ date $Date: 2008-06-09 19:53:50 $
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
