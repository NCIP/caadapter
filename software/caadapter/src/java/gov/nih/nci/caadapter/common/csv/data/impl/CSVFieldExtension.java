/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.common.csv.data.impl;
import gov.nih.nci.caadapter.common.csv.meta.*;
/**
 * sets CSVField Extensions
 * @author OWNER: $Author: phadkes $
 * @author LAST UPDATE $Author: phadkes $
 * @since      caAdapter  v4.2
 * @version    $Revision: 1.3 $
 * @date       $Date: 2008-09-24 20:00:10 $
*/
public class CSVFieldExtension extends CSVFieldImpl {

	private boolean valueSetFlag=false;
	public CSVFieldExtension(CSVFieldMeta meta)
	{
		super(meta);
	}
	/*
	 * @return true -- If a non-empty value is set with the CSV field
	 */
	public boolean isValueSet() {
		return valueSetFlag;
	}
	/**
	 * Set new value with the valueSetFlag
	 * @param resetValueFlag
	 */
	public void setValueSetFlag(boolean resetValueFlag) {
		valueSetFlag = resetValueFlag;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
