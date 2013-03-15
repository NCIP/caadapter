/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.common;

/**
 * This Adapter class defines common API for all Metadata objects
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:49 $
 */

public interface SDKMetaData {

	public String getXPath();
	public boolean isMapped();
	public void setMapped(boolean isMapped);

}
