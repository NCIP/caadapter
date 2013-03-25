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

import gov.nih.nci.caadapter.mms.generator.CumulativeMappingGenerator;
import gov.nih.nci.caadapter.mms.map.CumulativeMapping;
import gov.nih.nci.caadapter.mms.map.DependencyMapping;

/**
 * The purpose of this class is to validate that an object has been
 * correcltly mapped to a database table.
 *
 * @author OWNER: Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     caAdatper v4.0
 * @version    $Revision: 1.6 $
 * @date       $Date: 2009-09-30 17:09:34 $
 * @created 11-Aug-2006 8:18:16 AM
 */

public class DependencyMappingValidator {

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
	private boolean areAlreadyMapped(){
		boolean hasBeenMapped = false;
		try {
			cummulativeMapping = CumulativeMappingGenerator.getInstance().getCumulativeMapping();
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
			boolean isValidMapping = true;
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
 * HISTORY: Revision 1.5  2009/06/12 15:52:30  wangeug
 * HISTORY: clean code: caAdapter MMS 4.1.1
 * HISTORY:
 * HISTORY: Revision 1.4  2008/09/26 20:35:27  linc
 * HISTORY: Updated according to code standard.
 * HISTORY:
 */
