/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.mms.validator;
import gov.nih.nci.caadapter.mms.map.AttributeMapping;
import gov.nih.nci.caadapter.mms.map.CumulativeMapping;


/**
 * The purpose of this class is to validate that the object attribute
 * linked to a database column has the same semantic meaning as another
 * attribute of another object in another uml model that has already
 * been mapped to this database column.
 * @version 1.0
 * @created 11-Aug-2006 8:18:18 AM
 */
public class SemanticMappingValidator {

	/**
	 * This instance of CummulativeMapping will be loaded with all the
	 * mappings contained within a third party xmi file. The concept id's
	 * associated to AttributeMappings' AttributeMetadata object will be
	 * used to compare the same in the source object model currently being
	 * mapped.
	 */
	private CumulativeMapping cumulativeMapping;

	private String thirdPartyConcept;
	private AttributeMapping mapping;

	public SemanticMappingValidator(){

	}
	/**
	 *
	 * @param attributeMapping
	 */
	public void checkForSemanticMapping(AttributeMapping attributeMapping){

	}

	/**
	 * This method checks to see if matching concepts for attributes of
	 * different models linked to the same database table column have like
	 * concept codes.
	 *
	 * @param attributeMapping
	 */
	public boolean hasMatchingConceptCode(AttributeMapping attributeMapping){
		return false;
	}

	/**
	 * This method loads a third party xmi file.
	 *
	 * @param pathToFile
	 */
	public void loadThirdPartyXMI(String pathToFile){

	}

}
