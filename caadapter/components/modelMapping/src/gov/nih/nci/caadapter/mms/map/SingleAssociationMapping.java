/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.mms.map;
import gov.nih.nci.caadapter.common.metadata.AssociationMetadata;
import gov.nih.nci.caadapter.common.metadata.ColumnMetadata;


/**
 * This class represents the mapping between a single association role
 * name and a foregeign key column of a table.
 * @version 1.0
 * @created 11-Aug-2006 8:18:19 AM
 */
public class SingleAssociationMapping {

	private AssociationMetadata associationEndMetadata;
	private ColumnMetadata columnMetadata;

	public SingleAssociationMapping(){
	}

	/**
	 *
	 * @param columnMetadata
	 */
	public SingleAssociationMapping(ColumnMetadata columnMetadata, AssociationMetadata associationEndMetadata, AssociationMetadata otherAssociationEndMetadata){
		this.associationEndMetadata = associationEndMetadata;
		this.columnMetadata = columnMetadata;
	}

	public AssociationMetadata getAssociationEndMetadata(){
		return associationEndMetadata;
	}

	public ColumnMetadata getColumnMetadata(){
		return columnMetadata;
	}

	/**
	 *
	 * @param associationEndMetadata
	 */
	public void setAssociationEndMetadata(AssociationMetadata associationEndMetadata){
         this.associationEndMetadata = associationEndMetadata;
	}
	/**
	 *
	 * @param columnMetadata
	 */
	public void setColumnMetadata(ColumnMetadata columnMetadata){
		this.columnMetadata = columnMetadata;
	}

	/**
	 * @ colMetdata
	 */
	public boolean containsColumnMetadata(ColumnMetadata colMetadata) {
		boolean containsColumnMetadata = false;
		if (this.columnMetadata.getXPath().equals(colMetadata.getXPath())) {
			containsColumnMetadata = true;
		}
		return containsColumnMetadata;
	}
}
