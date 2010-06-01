/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
 
package gov.nih.nci.caadapter.hl7.transformation.data;


/**
 * This class defines mutable flag object 
 * 
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-09-29 15:39:06 $
 */

public class MutableFlag {
boolean hasUserMappedData = false;

public MutableFlag(boolean flag) {
	hasUserMappedData = flag;
}

/**
 * @return the hasUserMappedData
 */
public boolean hasUserMappedData() {
	return hasUserMappedData;
}

/**
 * @param hasUserMappedData the hasUserMappedData to set
 */
public void setHasUserMappedData(boolean hasUserMappedData) {
	this.hasUserMappedData = hasUserMappedData;
}
}
