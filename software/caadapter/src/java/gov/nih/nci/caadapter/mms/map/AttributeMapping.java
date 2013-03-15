/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.mms.map;
import gov.nih.nci.caadapter.common.metadata.AttributeMetadata;
import gov.nih.nci.caadapter.common.metadata.ColumnMetadata;

/**
 * Class for attribute mapping
 *
 * @author OWNER: Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     caAdatper v4.0
 * @version    $Revision: 1.5 $
 * @date       $Date: 2008-09-26 20:35:27 $
 * @created 11-Aug-2006 8:18:14 AM
 */
public class AttributeMapping {

	private AttributeMetadata attributeMetadata;
	private ColumnMetadata columnMetadata;

	/**
	 *return attribute metadata associated
	 */
	public AttributeMetadata getAttributeMetadata(){
		return attributeMetadata;
	}

	/**
	 *
	 * @return column metadata associated
	 */
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
/**
 * HISTORY: $Log: not supported by cvs2svn $
 */
