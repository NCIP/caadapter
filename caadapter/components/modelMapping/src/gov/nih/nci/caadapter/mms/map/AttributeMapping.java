/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.mms.map;
import gov.nih.nci.caadapter.common.metadata.AttributeMetadata;
import gov.nih.nci.caadapter.common.metadata.ColumnMetadata;

/**
 * @version 1.0
 * @created 11-Aug-2006 8:18:14 AM
 */
public class AttributeMapping {

	private AttributeMetadata attributeMetadata;
	private ColumnMetadata columnMetadata;


	public AttributeMapping(){
	}

	/**
	 * @param attributeMetadata
	 */
	public AttributeMapping(AttributeMetadata attributeMetadata) {
		super();
		// TODO Auto-generated constructor stub
		this.attributeMetadata = attributeMetadata;
	}

	/**
	 * @param columnMetadata
	 */
	public AttributeMapping(ColumnMetadata columnMetadata) {
		super();
		// TODO Auto-generated constructor stub
		this.columnMetadata = columnMetadata;
	}

	/**
	 *
	 * @param attributeMetadata
	 * @param columnMetadata
	 */
	public AttributeMapping(AttributeMetadata attributeMetadata, ColumnMetadata columnMetadata){
	      this.attributeMetadata = attributeMetadata;
	      this.columnMetadata = columnMetadata;
	}

	/**
	 *
	 */
	public AttributeMetadata getAttributeMetadata(){
		return attributeMetadata;
	}

	public ColumnMetadata getColumnMetadata(){
		return columnMetadata;
	}

	/**
	 *
	 * @param attributeMetadata
	 */
	public void setAttributeMetadata(AttributeMetadata attributeMetadata){
         this.attributeMetadata = attributeMetadata;
	}

	/**
	 *
	 * @param columnMetadata
	 */
	public void setColumnMetadata(ColumnMetadata columnMetadata){
         this.columnMetadata = columnMetadata;
	}

}
