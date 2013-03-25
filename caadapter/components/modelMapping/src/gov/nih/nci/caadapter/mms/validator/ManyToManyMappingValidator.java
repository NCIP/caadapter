/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.mms.validator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import gov.nih.nci.caadapter.mms.map.AttributeMapping;
import gov.nih.nci.caadapter.mms.map.CumulativeMapping;
import gov.nih.nci.caadapter.mms.map.DependencyMapping;
import gov.nih.nci.caadapter.mms.map.ManyToManyMapping;
import gov.nih.nci.caadapter.common.metadata.AssociationMetadata;
import gov.nih.nci.caadapter.mms.util.DatatypeCompatablityProperties;


/**
 * The purpose of this class is to validate the mapping of the two ends
 * of a many to many association to the two columns of the associated
 * correlation table.
 * @version 1.0
 * @created 11-Aug-2006 8:18:17 AM
 */
public class ManyToManyMappingValidator {

	private String validationErrorMessage;
	private CumulativeMapping cumulativeMapping;
	private ManyToManyMapping associationMapping;
	private static Properties datatypeCompatabilityProp;

	/**
	 * The purpose of this class is to validate whether a mapped source role name
	 * and target column are valid by applying various business rule checks.
	 */
	public ManyToManyMappingValidator(ManyToManyMapping mapping){
		this.associationMapping = mapping;
		try{
			datatypeCompatabilityProp = DatatypeCompatablityProperties.getInstance().getProperties();
		} catch(Exception e) {
			//System.out.println("Could not find datatype.properties ");
		}
	}
	/**
	 * This method determines if the multiplicity of the association role being mapped
	 * is greater than one which it would need to be to qualify as an end of a many to many relationship.
	 */
	public boolean associationEndMultiplicityIsCorrect() {
		boolean asssociationEndMutltiplicityIsCorrect = false;
		if (associationMapping.getAssociationEndMetadata().getMultiplicity() == -1) {
			asssociationEndMutltiplicityIsCorrect = true;
		}
		return asssociationEndMutltiplicityIsCorrect;
	}

	/**
	 * This method determines if the multiplicity of the association role being mapped
	 * is greater than one which it would need to be to qualify as an end of a many to many relationship.
	 */

	public boolean otherAssociationEndMultiplicityIsCorrect(){
		boolean otherAssociationEndMultiplicityIsCorrect = false;
		if (associationMapping.getOtherAssociationEndMetadata().getMultiplicity() == -1) {
			otherAssociationEndMultiplicityIsCorrect = true;
		}
		return otherAssociationEndMultiplicityIsCorrect;
	}

	/**
	 * This method determines if the parent object of association role being mapped
	 * has itself been mapped to a target table.
	 */
	public boolean associatonEndIsDependencyMapped() {
		boolean associationEndObjectDependencyMapped = false;
		AssociationMetadata thisEnd = associationMapping.getAssociationEndMetadata();
		String parentXPath = getParentXPath(associationMapping.getAssociationEndMetadata().getXPath());
		try {
			cumulativeMapping = CumulativeMapping.getInstance();
		} catch (Exception e) {

		}
		List dependencyMappings = cumulativeMapping.getDependencyMappings();

		Iterator i = dependencyMappings.iterator();
		while (i.hasNext()) {
			try {
				DependencyMapping m = (DependencyMapping) i.next();
				if(m.isAMappedEntities(parentXPath)) {
					associationEndObjectDependencyMapped = true;
				}
			} catch (Exception e) {
				//LOGGER.fine(e.getMessage());
				 e.printStackTrace();
			}
		}
		return associationEndObjectDependencyMapped;
	}


	   /**
     * This method verifies that the terminating end of an association has been
     * mapped to a database table
     * Verification Step #4
     */

	public boolean isOtherAssociationEndObjectDependencyMapped() {
		boolean otherAssociationEndObjectDependencyMapped = false;
		AssociationMetadata otherEnd = associationMapping.getOtherAssociationEndMetadata();
		String parentXPath = getParentXPath(associationMapping.getOtherAssociationEndMetadata().getXPath());
		try {
			cumulativeMapping = CumulativeMapping.getInstance();
		} catch (Exception e) {

		}
		List dependencyMappings = cumulativeMapping.getDependencyMappings();
		Iterator i = dependencyMappings.iterator();
		while (i.hasNext()) {
			try {
				DependencyMapping m = (DependencyMapping) i.next();
				if(m.isAMappedEntities(parentXPath)) {
					otherAssociationEndObjectDependencyMapped = true;
				}
			} catch (Exception e) {
				//LOGGER.fine(e.getMessage());
					e.printStackTrace();
			}
		}
		return otherAssociationEndObjectDependencyMapped;
	}

	/**
	 * This method determines if the parent table of the column being mapped is a correlation table.
	 */

	public boolean selectedTableIsCorrelationTable(){
		boolean isCorrelationTable = false;
		if (associationMapping.getThisEndColumn().getTableMetadata().getType().equals("correlation")); {
			isCorrelationTable = true;
		}
		return isCorrelationTable;
	}

	/**
	 * This method checks to make sure the selected column has not been mapped before.
	 */
	public boolean selectedColumnAlreadyMapped(){
		boolean isAlreadyMapped = false;
		for(AttributeMapping att : cumulativeMapping.getAttributeMappings()) {
			if (att.getColumnMetadata().getName().equals(associationMapping.getThisEndColumn().getName())) {
				isAlreadyMapped = true;
			}
		}
		return isAlreadyMapped;
	}
	/**
	 * This method returns the reason for the invalidation
	 * @return validationErrorMessage
	 */
	public String getValidationErrorMessage() {
		return validationErrorMessage;
	}
	/**
	 * This method sets the validation error message
	 * @param validationErrorMessage
	 */
	public void setValidationErrorMessage(String validationErrorMessage) {
		this.validationErrorMessage = validationErrorMessage;
	}
	/**
	 * This method calls all the specific business rule verification checks for a many
	 * to many mapping relationship.
	 */

	public boolean isValid(){
		boolean isValidMapping = true;

		if (!associationEndMultiplicityIsCorrect()) {

			this.validationErrorMessage = "The multiplicity of the selected role is not correct for a many to many relationship mapping.";
			isValidMapping = false;
		}
		if (isValidMapping && !otherAssociationEndMultiplicityIsCorrect()) {
			this.validationErrorMessage = "The multiplicity of the opposite end of the selected role is not correct for a many to many relationship mapping.";
		    isValidMapping = false;
		}
		if (isValidMapping && !associatonEndIsDependencyMapped()) {
			this.validationErrorMessage = "The parent object represented by the selected role name is not dependency mapped.";
		    isValidMapping = false;
		}
		/*
		if (isValidMapping && !isOtherAssociationEndObjectDependencyMapped()) {
			this.validationErrorMessage = "The parent object represented by the opposite end of the selected role name is not dependency mapped.";
		    isValidMapping = false;
		}
		if (isValidMapping && selectedTableIsCorrelationTable()){
			this.validationErrorMessage = "The parent table of the column selected has not been annotated as a correlation table.";
		}

		if (isValidMapping && selectedColumnAlreadyMapped()) {
			this.validationErrorMessage = "The target column selected has already been mapped.";
		}
		*/
		return isValidMapping;
	}
	   public String getParentXPath(String childString){
	    	String parentPath = null;
	      	int end = childString.lastIndexOf(".");
	    	parentPath = childString.substring(0,end);
	    	return parentPath;

	    }
}

