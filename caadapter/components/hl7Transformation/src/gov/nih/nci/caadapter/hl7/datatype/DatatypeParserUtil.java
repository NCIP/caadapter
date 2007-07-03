/**
 * <!-- LICENSE_TEXT_START -->
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
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2007-07-03 18:25:43 $
 */
public class DatatypeParserUtil {
	private static DatatypeParser datatypeParser = null;
	private DatatypeParserUtil() {}
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