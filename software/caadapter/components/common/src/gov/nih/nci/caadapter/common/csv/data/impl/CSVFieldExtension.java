/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
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
