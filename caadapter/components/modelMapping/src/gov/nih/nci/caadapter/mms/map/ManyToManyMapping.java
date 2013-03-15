/*L
 * Copyright SAIC.
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
 * This object is used to hold the mapping between an association and a
 * correlation table.
 * @version 1.0
 * @created 11-Aug-2006 8:18:16 AM
 */
public class ManyToManyMapping {

	private AssociationMetadata associationEndMetadata;
	private AssociationMetadata otherAssociationEndMetadata;
	private ColumnMetadata thisEndColumn;
	private ColumnMetadata otherEndColumn;

	public ManyToManyMapping(){
	}

	/**
	 *
	 * @param thisEndColumn
	 * @param associationEndMetadata
	 */
	public ManyToManyMapping(ColumnMetadata thisEndColumn, AssociationMetadata associationEndMetadata){
		this.associationEndMetadata = associationEndMetadata;
		this.thisEndColumn = thisEndColumn;

	}

	public AssociationMetadata getAssociationEndMetadata(){
		return associationEndMetadata;
	}

	/**
	 *
	 * @param otherAssociationEndRoleName
	 */
	public void setAssociationEndMetadata(AssociationMetadata associationEndMetadata){
		this.associationEndMetadata = associationEndMetadata;
	}

	/**
	 * @return Returns the thisEndColumn.
	 */
	public ColumnMetadata getThisEndColumn() {
		return thisEndColumn;
	}

	/**
	 * @param thisEndColumn The thisEndColumn to set.
	 */
	public void setThisEndColumn(ColumnMetadata thisEndColumn) {
		this.thisEndColumn = thisEndColumn;
	}
	/**
	 * @return Returns the otherAssociationEndMetadata.
	 */
	public AssociationMetadata getOtherAssociationEndMetadata() {
		return otherAssociationEndMetadata;
	}

	/**
	 * @param otherAssociationEndMetadata The otherAssociationEndMetadata to set.
	 */
	public void setOtherAssociationEndMetadata(
			AssociationMetadata otherAssociationEndMetadata) {
		this.otherAssociationEndMetadata = otherAssociationEndMetadata;
	}

	/**
	 * @return Returns the otherEndColumn.
	 */
	public ColumnMetadata getOtherEndColumn() {
		return otherEndColumn;
	}

	/**
	 * @param otherEndColumn The otherEndColumn to set.
	 */
	public void setOtherEndColumn(ColumnMetadata otherEndColumn2) {
		this.otherEndColumn = otherEndColumn;

	}
}
