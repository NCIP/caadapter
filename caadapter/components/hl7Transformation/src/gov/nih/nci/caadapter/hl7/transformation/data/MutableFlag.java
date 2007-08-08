/**
 * 
 */
package gov.nih.nci.caadapter.hl7.transformation.data;

/**
 * @author wuye
 *
 */
public class MutableFlag {
boolean hasUserMappedData = false;

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
