/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.mif.v1;

import java.util.Hashtable;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFReferenceResolver;

/**
 * The class provides Utilities to access the MIF info.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2007-08-27 17:34:22 $
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
	/**
	 * 
	 * @return
	 */
	public static Hashtable<String, String> getDocumentElementAttributes( Node docNode )
	{
		Hashtable<String, String> rtnHash=new Hashtable<String, String>();
		if (docNode==null)
			return rtnHash;
			
		NamedNodeMap attrMap=docNode.getAttributes();
		if (attrMap != null) 
		{
			for (int i=0;i<attrMap.getLength();i++)
			{
				Node attrNode=attrMap.item(i);
				rtnHash.put(attrNode.getNodeName(), attrNode.getNodeValue());
			}
		}
		return rtnHash;
	}
}