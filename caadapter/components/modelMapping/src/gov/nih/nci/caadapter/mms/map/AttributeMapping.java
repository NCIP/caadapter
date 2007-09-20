/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.mms.map;
import gov.nih.nci.caadapter.mms.metadata.AttributeMetadata;
import gov.nih.nci.caadapter.mms.metadata.ColumnMetadata;

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