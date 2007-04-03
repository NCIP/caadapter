/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.common;

/**
 * This Adapter class defines common API for all Metadata objects
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v3.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:02:37 $
 */

public interface SDKMetaData {

	public String getXPath();
	public boolean isMapped();
	public void setMapped(boolean isMapped);

}
