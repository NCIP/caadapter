/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.datatype;

/**
 * The class provides Utilities to access the Datatype info.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wuye $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-05-16 20:20:58 $
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
}