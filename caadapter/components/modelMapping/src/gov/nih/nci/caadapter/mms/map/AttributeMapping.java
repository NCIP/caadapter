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
	 * @param attribute
	 * @param value
	 */
	public AttributeMapping(AttributeMetadata attributeMetadata, ColumnMetadata columnMetadata){
	      this.attributeMetadata = attributeMetadata;
	      this.columnMetadata = columnMetadata;
	}

	/**
	 *
	 * @param attribute
	 */
	public AttributeMetadata getAttributeMetadata(){
		return attributeMetadata;
	}

	public ColumnMetadata getColumnMetadata(){
		return columnMetadata;
	}

	/**
	 *
	 * @param attributeName
	 */
	public void setAttributeMetadata(AttributeMetadata attributeMetadata){
         this.attributeMetadata = attributeMetadata;
	}

	/**
	 *
	 * @param value
	 */
	public void setColumnMetadata(ColumnMetadata columnMetadata){
         this.columnMetadata = columnMetadata;
	}

}