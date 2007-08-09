/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.mif.v1;

import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFReferenceResolver;

/**
 * The class provides Utilities to access the MIF info.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2007-08-09 19:25:56 $
 */
public class MIFParserUtil {

	public static MIFClass getMIFClass(String mifFileName) {
		MIFParser mifParser = new MIFParser();
		MIFClass mifClass = null;
		try {
			mifClass = mifParser.loadMIF(mifFileName);
		//resolve the internal reference
			MIFReferenceResolver refResolver=new MIFReferenceResolver();
			refResolver.getReferenceResolved(mifClass);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return mifClass;
	}
}