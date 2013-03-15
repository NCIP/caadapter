/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.hl7.transformation.data;

/**
 * @author wuye
 *
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
