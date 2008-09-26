/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.mms.validator;
import java.util.Iterator;
import java.util.List;

import gov.nih.nci.caadapter.mms.map.CumulativeMapping;
import gov.nih.nci.caadapter.mms.map.DependencyMapping;

/**
 * The purpose of this class is to validate that an object has been
 * correcltly mapped to a database table.
 *
 * @author OWNER: Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     caAdatper v4.0
 * @version    $Revision: 1.4 $
 * @date       $Date: 2008-09-26 20:35:27 $
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
/**
 * HISTORY: $Log: not supported by cvs2svn $
 */
