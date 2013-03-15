/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





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
 *          revision    $Revision: 1.7 $
 *          date        $Date: 2009-03-18 15:49:45 $
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
		if (datatypeParser.getDatatypes().get(name) == null)
			return CustomerDatatypeUtil.getCustomerDatatype(name);
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
 * HISTORY :Revision 1.6  2009/03/13 14:50:54  wangeug
 * HISTORY :clean code
 * HISTORY :
 * HISTORY :Revision 1.5  2008/09/29 15:48:57  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */