/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.datatype;

import java.util.Collections;
import java.util.List;

/**
 * The class provides Utilities to access the Datatype info.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.6 $
 *          date        $Date: 2009-03-13 14:50:54 $
 */
public class DatatypeParserUtil {
	private static DatatypeParser datatypeParser = null;
	public static DatatypeParser getDatatypeParser() {
	  try {
		if (datatypeParser == null) {
			datatypeParser = new DatatypeParser();
			datatypeParser.loadDatatypes();
		}
	  }catch(Exception e) {
		  e.printStackTrace();
	  }
		return datatypeParser;
	}
	public static Datatype getDatatype(String name) {
		if (name==null||name.equals(""))
			return null;
		DatatypeParser datatypeParser = getDatatypeParser();
		if (datatypeParser.getDatatypes().get(name) == null) return null;
		return (Datatype)datatypeParser.getDatatypes().get(name);
	}
	public static List <String>findSubclassListWithTypeName(String className)
	{
		List <String>rtnList=getDatatypeParser().findSubclassList(className);  
		if (rtnList!=null)
			Collections.sort(rtnList);
		return rtnList;
	}
	
	public static boolean isAbstractDatatypeWithName(String typeName)
	{
		List subClassList=findSubclassListWithTypeName(typeName);
		if (subClassList==null||subClassList.isEmpty())
			return false;
		return true;
	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.5  2008/09/29 15:48:57  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */