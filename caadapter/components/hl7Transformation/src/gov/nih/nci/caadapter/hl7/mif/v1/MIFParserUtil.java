/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.mif.v1;

import gov.nih.nci.caadapter.hl7.mif.MIFClass;

/**
 * The class provides Utilities to access the MIF info.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wuye $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-05-16 20:20:59 $
 */
public class MIFParserUtil {

	public static MIFClass getMIFClass(String mifFileName) {
		MIFParser mifParser = new MIFParser();
		MIFClass mifClass = null;
		try {
			mifClass = mifParser.loadMIF(mifFileName);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return mifClass;
	}
}