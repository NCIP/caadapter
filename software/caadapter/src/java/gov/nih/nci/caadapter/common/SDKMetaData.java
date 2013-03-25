/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.common;

/**
 * This Adapter class defines common API for all Metadata objects
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-09-24 19:51:48 $
 */

public interface SDKMetaData {

	public String getXPath();
	public boolean isMapped();
	public void setMapped(boolean isMapped);

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
