/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
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
