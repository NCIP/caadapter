/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.hl7.mif.v1;

import gov.nih.nci.caadapter.hl7.mif.CMETRef;

/**
 * The class provides Utilities to access the CMET meta info.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2009-03-13 14:54:43 $
 */
public class CMETUtil {
	private static CMETInfoParser cmetInfoParser = null;
//	private CMETUtil() {}
	private static CMETInfoParser getCMETInfoParser() {
	  try {
		if (cmetInfoParser == null) {
			cmetInfoParser = new CMETInfoParser();
			cmetInfoParser.loadCMETInofs();
		}
	  }catch(Exception e) {
		  e.printStackTrace();
	  }
		return cmetInfoParser;
	}
	public static CMETRef getCMET(String name) {
		CMETInfoParser cmetInfoParser = getCMETInfoParser();
		for(CMETRef cmetRef:cmetInfoParser.getCMETRefs()) {
			if (cmetRef.getName().equals(name)) return cmetRef;
			if (cmetRef.getClassName().equals(name)) return cmetRef;
		}
		return null;
	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.3  2008/09/29 15:42:44  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */