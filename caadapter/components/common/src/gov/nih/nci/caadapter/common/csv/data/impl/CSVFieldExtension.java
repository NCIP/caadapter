/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
 
package gov.nih.nci.caadapter.common.csv.data.impl;
import gov.nih.nci.caadapter.common.csv.meta.*;
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
