/*L
 * Copyright SAIC.
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
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:50 $
 */
public class CMETUtil {
	private static CMETInfoParser cmetInfoParser = null;
	private CMETUtil() {}
	public static CMETInfoParser getCMETInfoParser() {
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

	public static CMETRef getCMETClass(String className) {
		CMETInfoParser cmetInfoParser = getCMETInfoParser();
		for(CMETRef cmetRef:cmetInfoParser.getCMETRefs()) {
			if (cmetRef.getClassName().equals(className)) return cmetRef;
			if (cmetRef.getName().equals(className)) return cmetRef;
		}
		return null;
	}
}
