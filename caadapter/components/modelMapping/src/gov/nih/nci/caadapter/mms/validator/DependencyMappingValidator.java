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

import gov.nih.nci.caadapter.mms.map.CumulativeMapping;
import gov.nih.nci.caadapter.mms.map.DependencyMapping;

/**
 * The purpose of this class is to validate that an object has been
 * correcltly mapped to a database table.
 * @version 1.0
 * @created 11-Aug-2006 8:18:16 AM
 */

public class DependencyMappingValidator {

	public DependencyMappingValidator(){
	}

	private String validationErrorMessage;
	private DependencyMapping mapping;

    private CumulativeMapping cummulativeMapping;


	public DependencyMappingValidator(DependencyMapping mapping){
		this.mapping = mapping;
	}

	public void setDependencyMapping(DependencyMapping mapping) {
		this.mapping = mapping;
	}

	/**
	 * This method checks to see that:
	 * 1. These two objects are already mapped to each other
	 * 2. These two objects have been mapped to some other objects.
	 *
	 */
	public boolean areAlreadyMapped(){
		boolean hasBeenMapped = false;
		try {
			cummulativeMapping = CumulativeMapping.getInstance();
		}catch (Exception e){
			//Add logging here
		}
		List dependencyMappings = cummulativeMapping.getDependencyMappings();
			Iterator i = dependencyMappings.iterator();
			while (i.hasNext()) {
				try {
					DependencyMapping m = (DependencyMapping) i.next();
					if (m.areMappedEntities(mapping.getSourceDependency().getXPath(), mapping.getTargetDependency().getXPath())) {
						hasBeenMapped = true;
						this.validationErrorMessage = "Element previously mapped.";
					}
				} catch (Exception e) {
					//LOGGER.fine(e.getMessage());
					e.printStackTrace();
				}
			}
 		return hasBeenMapped;
	}

/*
    public boolean containsCorrelationTable(){
			boolean containsCorrelationTable = false;
			if (mapping.getTargetDependency() != null){

			}
			if (mapping.getTargetDependency().getType().equalsIgnoreCase("Correlation") ){
				containsCorrelationTable = true;
				this.validationErrorMessage = "Target table is a correlation table.";
			}
			return containsCorrelationTable;
	}
	*/
	/**
	 * This method checks to see that these two elements can legally be
	 * mapped first by checking that neither of the two elements have
	 * already been mapped and then checking if the target database table is
	 * not a correlation table.
	 *
	 */

	public boolean isValid(){
			// This method returns "True" unless any of the following tests fail:
		    // The object and/or table have already been mapped
		    // The table being mapped is a correlation table and shouldn't be mapped as a dependency table
			boolean isValidMapping = true;
//			if (areAlreadyMapped() || containsCorrelationTable()) {
			if (areAlreadyMapped()) {
				isValidMapping = false;
			}
			return isValidMapping;
	}

	public String getValidationErrorMessage() {
		return validationErrorMessage;
	}

	public void setValidationErrorMessage(String validationErrorMessage) {
		this.validationErrorMessage = validationErrorMessage;
	}

}
