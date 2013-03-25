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
 *
 * @author OWNER: Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     caAdatper v4.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2009-07-14 16:36:00 $
 * @created 11-Aug-2006 8:18:19 AM
 */
public class AssociationMapping {

	private AssociationMetadata associationEndMetadata;
	private ColumnMetadata columnMetadata;


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

}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.5  2008/09/26 20:35:27  linc
 * HISTORY: Updated according to code standard.
 * HISTORY:
 */
