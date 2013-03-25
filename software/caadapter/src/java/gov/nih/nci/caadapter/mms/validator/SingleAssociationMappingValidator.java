/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.mms.validator;

import gov.nih.nci.caadapter.mms.map.AssociationMapping;
import gov.nih.nci.caadapter.common.metadata.AssociationMetadata;

/**
 * The purpose of this class is to validate the single association role
 * name mapping to a foreign key column of a dependant table.
 *
 * @author OWNER: Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     caAdatper v4.0
 * @version    $Revision: 1.8 $
 * @date       $Date: 2009-07-14 16:36:13 $
 * @created 11-Aug-2006 8:18:19 AM
 */
public class SingleAssociationMappingValidator {

	private String validationErrorMessage;
//	private CumulativeMapping cumulativeMapping;
	private AssociationMapping associationMapping;

	public SingleAssociationMappingValidator(AssociationMapping mapping){
         this.associationMapping = mapping;
	}
//   /**
//    * This method verifies that the starting end of an association has been
//    * mapped to a database table
//    * Verification Step #1
//    */
//
//    public boolean isAssociationEndObjectDependencyMapped() {
//		boolean associationEndObjectDependencyMapped = false;
//		try {
//			cumulativeMapping = CumulativeMappingGenerator.getInstance().getCumulativeMapping();
//		} catch (Exception e) {
//
//		}
//		List dependencyMappings = cumulativeMapping.getDependencyMappings();
//
//		Iterator i = dependencyMappings.iterator();
//		while (i.hasNext()) {
//			try {
//				DependencyMapping m = (DependencyMapping) i.next();
//				if(m.areMappedEntities(associationMapping.getAssociationEndMetadata().getParentXPath(), associationMapping.getColumnMetadata().getParentXPath())){
//					associationEndObjectDependencyMapped = true;
//				}
//			} catch (Exception e) {
//				//LOGGER.fine(e.getMessage());
//				 e.printStackTrace();
//			}
//		}
//		return associationEndObjectDependencyMapped;
//	}

//    /**
//     * This method verifies that the column being mapped is in fact a foreign key column
//     * within the table that is belongs to.
//     * Verification Step #2
//     */
//
//	public boolean isMappedColumnForeignKey(){
//		boolean isForeignKey = associationMapping.getColumnMetadata().isForeignKey();
//		return isForeignKey;
//	}

//	 /**
//	  * This method verifies that the column being mapped has not
//	  * been mapped in any other SingleAssociationMapping
//	  * Verification Step #3
//	  */
//	public boolean isColumnPreviouslyMapped(){
//		boolean previouslyMapped = false;
//		try {
//		cumulativeMapping = CumulativeMappingGenerator.getInstance().getCumulativeMapping();
//		} catch (Exception e){
//
//		}
//		List singleAssociationMappings = cumulativeMapping.getAssociationMappings();
//		ColumnMetadata colMetadata = associationMapping.getColumnMetadata();
//		Iterator i = singleAssociationMappings.iterator();
//		while (i.hasNext()) {
//			try {
//				AssociationMapping m = (AssociationMapping) i.next();
//				if(m.containsColumnMetadata(colMetadata)) {
//					previouslyMapped = true;
//				}
//			} catch (Exception e) {
//				//LOGGER.fine(e.getMessage());
//				e.printStackTrace();
//			}
//		}
//	 	return previouslyMapped;
//	}

    public boolean isTargetColumnPrimaryKey() {
    	boolean isPrimaryKey = false;
    	return isPrimaryKey;
    }


//	/**
//	 * This method verifies that the object associated to the single association role name
//	 * has an id attribute that has been mapped to a primary key column of its dependent
//	 * table.
//	 * Verification Step #5
//	 */
//
//	public boolean isTargetObjectMappedToPrimaryKey(){
//
//		boolean isMappedToPrimaryKey = false;
//		DependencyMapping targetDependencyMapping = new DependencyMapping();
//		TableMetadata targetTableMetadata = new TableMetadata();
//		ColumnMetadata columnMetadata = new ColumnMetadata();
//
//		//This target equates to Taxon in the example
//		String parentXPath = getParentXPath(associationMapping.getColumnMetadata().getXPath());
//		//Get the table dependency mapping associated with Taxon
//		try {
//			cumulativeMapping = CumulativeMappingGenerator.getInstance().getCumulativeMapping();
//		} catch (Exception e){
//
//		}
//		List dependencyMappings = cumulativeMapping.getDependencyMappings();
//		Iterator i = dependencyMappings.iterator();
//		while (i.hasNext()) {
//			try {
//				DependencyMapping m = (DependencyMapping) i.next();
//				if(m.isAMappedEntities(parentXPath)) {
//					targetDependencyMapping = m;
//				}
//			} catch (Exception e) {
//				//LOGGER.fine(e.getMessage());
//				 e.printStackTrace();
//			}
//		}
//
//		// Get the table
//		targetTableMetadata = targetDependencyMapping.getTargetDependency();
//		//Get the column that is the primary key
//		columnMetadata = targetTableMetadata.getPrimaryKeyColumn();
//		//check the attribute mappings to see if that column is mapped to Taxon.id
//	    List attributeMappings = cumulativeMapping.getAttributeMappings();
//			Iterator j = attributeMappings.iterator();
//			while (j.hasNext()) {
//				try {
//					AttributeMapping m = (AttributeMapping) j.next();
//					if(m.getColumnMetadata().getXPath().equals(columnMetadata.getXPath()) && m.getAttributeMetadata().getName().equals("id") && m.getAttributeMetadata().getXPath().equals(parentXPath)) {
//						isMappedToPrimaryKey = true;
//					}
//				} catch (Exception e) {
//					//LOGGER.fine(e.getMessage());
//					 e.printStackTrace();
//				}
//		}
//		return isMappedToPrimaryKey;
//	}

//    /**
//	 * This method verifies that the foreign key column being mapped is of the same datatype
//	 * as the primary key column of the table dependency mapped to the return type of the
//	 * role being mapped.
//	 * i.e " GENE_TV.TAXON_ID has the same datatype as TAXON.ID
//	 * Verification Step #6
//	 */
//
//    public boolean isForeignKeyCorrectDatatype() {
//		boolean correctDatatype = false;
//		DependencyMapping targetDependencyMapping = new DependencyMapping();
//		TableMetadata targetTableMetadata = new TableMetadata();
//		ColumnMetadata targetColumnMetadata = new ColumnMetadata();
//		ColumnMetadata columnMetadata = associationMapping.getColumnMetadata();
//		//This target equates to Taxon in the example
//		String parentXPath = getParentXPath(associationMapping.getAssociationEndMetadata().getXPath());
//		//Get the table dependency mapping associated with Taxon
//		try {
//			cumulativeMapping = CumulativeMappingGenerator.getInstance().getCumulativeMapping();
//		} catch (Exception e) {
//			//Add logging here
//		}
//		List dependencyMappings = cumulativeMapping.getDependencyMappings();
//		Iterator i = dependencyMappings.iterator();
//		while (i.hasNext()) {
//			try {
//				DependencyMapping m = (DependencyMapping) i.next();
//				if(m.isAMappedEntities(parentXPath)) {
//					targetDependencyMapping = m;
//				}
//			} catch (Exception e) {
//				//LOGGER.fine(e.getMessage());
//					e.printStackTrace();
//			}
//		}
//
//		// Get the table
//		targetTableMetadata = targetDependencyMapping.getTargetDependency();
//		//Get the column that is the primary key
//		targetColumnMetadata = targetTableMetadata.getPrimaryKeyColumn();
//		//Compare the datatypes
//		if (targetColumnMetadata.getDatatype().equals(columnMetadata.getDatatype())) {
//			correctDatatype = true;
//		}
//		return correctDatatype;
//	}
//
//	 /**
//	  * This method verifies that the foreign key column being mapped is of the same datatype
//	  * as the primary key column of the table dependency mapped to the return type of the
//	  * role being mapped.
//	  * i.e " GENE_TV.TAXON_ID has the same datatype as TAXON.ID
//	  * Verification Step #6
//	  */
//
//	public boolean hasCorrectMultiplicity() {
//		boolean hasCorrectMultiplicity = false;
//		AssociationMetadata thisEnd = associationMapping.getAssociationEndMetadata();
//		if (thisEnd.getMultiplicity() < 2) {
//			hasCorrectMultiplicity = true;
//		}
//		return hasCorrectMultiplicity;
//	}

    public boolean hasCorrectManyToOneMultiplicity() {
			boolean hasCorrectMultiplicity = false;
			AssociationMetadata thisEnd = associationMapping.getAssociationEndMetadata();
			if (thisEnd.getMultiplicity() > 1) {
				hasCorrectMultiplicity = true;
			}
			return hasCorrectMultiplicity;
	}

	public boolean hasCorrectDirctionality() {
		boolean hasCorrectDirectionality = false;
		return hasCorrectDirectionality;
	}

    public boolean isManyToOne() {
		boolean isManyToOne = false;
		int thisEnd = associationMapping.getAssociationEndMetadata().getMultiplicity();
		if (thisEnd >1|| thisEnd == -1) {
		    isManyToOne = true;
		}
		return isManyToOne;
	}

	public boolean isOneToMany() {
		boolean isOneToMany = false;
		int thisEnd = associationMapping.getAssociationEndMetadata().getMultiplicity();
		if (thisEnd >1 || thisEnd == -1) {
			isOneToMany = true;
		}
		return isOneToMany;
	}

    public boolean isOneToOne() {
		boolean isOneToOne = false;
		int thisEnd = associationMapping.getAssociationEndMetadata().getMultiplicity();
		if (thisEnd ==1) {
			isOneToOne = true;
		}
		return isOneToOne;
	}

	/* This method calls all the other validation methods within the SingleAssociationValidator
	 * class. If any of the methods return false the validation fails.
	 */

	public boolean isValid(){
		boolean isValidMapping = true;
//		if (!isAssociationEndObjectDependencyMapped()) {
//			this.validationErrorMessage = "The object associated with this association is not dependency mapped.";
//		    isValidMapping = false;
//		    return false;
//		}
		/* If you want to verify the other end of a relationship has been dependency mapped you would neet to make this section work.
		if (isValidMapping && !isOtherAssociationEndObjectDependencyMapped()){
			System.out.println("SA debug #3\n");
			this.validationErrorMessage = "The object associated with the other end of this association is not dependency mapped.";
		    isValidMapping = false;
		}
		*/
/*		if (isValidMapping && isManyToOne()) {
			if (isTargetColumnPrimaryKey()) {
				this.validationErrorMessage = "Target column must be a primary key.";
				isValidMapping = false;
			}
			if (isTargetObjectMappedToPrimaryKey()) {
				this.validationErrorMessage = "The the id attribute of the object associated with the other end of this assocaition has not been mapped to a primary key column of a dependent table.";
				isValidMapping = false;
			}
		} else if (isOneToMany()) {
			if (!isAssociationEndObjectDependencyMapped()) {
					this.validationErrorMessage = "The object associated with this association is not dependency mapped.";
			    isValidMapping = false;
			}if (!isMappedColumnForeignKey()) {
				this.validationErrorMessage = "Target column must be a primary key.";
				isValidMapping = false;
			} if (isColumnPreviouslyMapped()) {
				this.validationErrorMessage = "The target column has already been previously mapped.";
			} if (!isTargetObjectMappedToPrimaryKey()) {
				this.validationErrorMessage = "The the id attribute of the object associated with the other end of this assocaition has not been mapped to a primary key column of a dependent table.";
				isValidMapping = false;
			} if (!isForeignKeyCorrectDatatype()){
				this.validationErrorMessage = "The id attribute of the source object is not compatible with the target column datatype.";
				isValidMapping = false;
			}
		} else if (isOneToOne()) {
			  if (!isMappedColumnForeignKey()) {
				  this.validationErrorMessage = "Target column must be a primary key.";
				  isValidMapping = false;
			  }if (isValidMapping && !isForeignKeyCorrectDatatype()){
				 	this.validationErrorMessage = "The id attribute of the source object is not compatible with the target column datatype.";
					isValidMapping = false;
			  }
		}*/
		return isValidMapping;
	}
    public String getParentXPath(String childString){
    	String parentPath = null;

    	int end = childString.lastIndexOf(".");
    	parentPath = childString.substring(0,end);
    	return parentPath;

    }
	public String getValidationErrorMessage() {
		return validationErrorMessage;
	}

	public void setValidationErrorMessage(String validationErrorMessage) {
		this.validationErrorMessage = validationErrorMessage;
	}

}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.7  2009/06/12 15:52:30  wangeug
 * HISTORY: clean code: caAdapter MMS 4.1.1
 * HISTORY:
 * HISTORY: Revision 1.6  2008/09/26 20:35:27  linc
 * HISTORY: Updated according to code standard.
 * HISTORY:
 */
