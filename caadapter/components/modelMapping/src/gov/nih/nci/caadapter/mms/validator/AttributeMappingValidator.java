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
import gov.nih.nci.caadapter.mms.map.DependencyMapping;
import gov.nih.nci.caadapter.common.metadata.AttributeMetadata;
import gov.nih.nci.caadapter.common.metadata.ColumnMetadata;
import gov.nih.nci.caadapter.mms.util.DatatypeCompatablityProperties;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
/**
 * The purpose of this class is to validate that an object attribute
 * linked to a database table column are compatible with respect to
 * datatypes.
 * @version 1.0
 * @created 11-Aug-2006 8:18:15 AM
 */
public class AttributeMappingValidator {

	private String validationErrorMessage;
	private AttributeMapping mapping;
    private static Properties datatypeCompatabilityProp;
    private CumulativeMapping cummulativeMapping;
	{
		try{
			datatypeCompatabilityProp = DatatypeCompatablityProperties.getInstance().getProperties();
		} catch(Exception e) {
			//Add logging here
			}
	}

	public AttributeMappingValidator(AttributeMapping mapping){
		this.mapping = mapping;
	}

	public void setAttributeMapping(AttributeMapping mapping) {
		this.mapping = mapping;
	}

	public void checkSemanticMapping(){
		//Add semantic mapping here
	}

	public boolean hasBeenMapped(){
		boolean beenMapped = false;
		try {
			cummulativeMapping = CumulativeMapping.getInstance();
		} catch (Exception e) {

		}
		List attributeMappings = cummulativeMapping.getAttributeMappings();
		Iterator i = attributeMappings.iterator();

		while (i.hasNext()) {
			try {
				AttributeMapping m = (AttributeMapping)i.next();
				if (m.getAttributeMetadata().getName().equals(mapping.getAttributeMetadata().getName()) && m.getAttributeMetadata().getXPath().equals(mapping.getAttributeMetadata().getXPath())){
					beenMapped = true;
				}
			} catch (Exception e) {
		            //LOGGER.fine(e.getMessage());
		            e.printStackTrace();
			}
		}
 		return beenMapped;
	}

    public boolean hasDependencyMapping(){
		boolean dependencyMapped = false;
		try {
			cummulativeMapping = CumulativeMapping.getInstance();
		} catch (Exception e) {

		}
		List dependencyMappings = cummulativeMapping.getDependencyMappings();
		Iterator i = dependencyMappings.iterator();
		while (i.hasNext()) {
			try {
				DependencyMapping m = (DependencyMapping) i.next();
				if(m.areMappedEntities(mapping.getAttributeMetadata().getParentXPath(), mapping.getColumnMetadata().getParentXPath())){
					dependencyMapped = true;
					break;
				}
			} catch (Exception e) {
				//LOGGER.fine(e.getMessage());
				e.printStackTrace();
			}
		}
		if (!dependencyMapped)
			this.validationErrorMessage = "Parent object and table are not dependency mapped.";
 		return dependencyMapped;
	}

	public boolean areCompatibleDatatypes(){
		String datatype = datatypeCompatabilityProp.getProperty(mapping.getColumnMetadata().getDatatype());
		if (datatype.equals(mapping.getAttributeMetadata().getDatatype())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isObjectId(){
		AttributeMetadata attributeMetadata = mapping.getAttributeMetadata();
		if (attributeMetadata.getName().equals("id")) {
			return true;
		} else {
			return false;
		}
	}

    public boolean isCorrectObjectIdToPrimaryKeyMapping() {
		boolean isCorrectMapping = false;
		ColumnMetadata columnMetadata = mapping.getColumnMetadata();
		if (columnMetadata.isPrimaryKey()) {
			isCorrectMapping = true;
		}
		return isCorrectMapping;
	}


	public boolean isValid(){
		// This method returns "True" unless any of the following tests fail:
		// the parent object and parent table have not been mapped as a dependency
		// if the attribute is an "id" attribute but the column is not a primary key column
		// if the attribute datatype is not compatible with the column datatype
		// if the attribute has been previously mapped
		boolean isValidMapping = true;
		if (!hasDependencyMapping()) {
			isValidMapping = false;
		}
		/*
		if (isObjectId()) {
			if (!isCorrectObjectIdToPrimaryKeyMapping()) {
				validationErrorMessage = "Selected column is not a primary key.";
				isValidMapping = false;
			}
		} */
		/*
		if (!areCompatibleDatatypes()) {
			isValidMapping = false;
			this.validationErrorMessage = "Datatypes are not compatible.";
		}
		*/
		if (hasBeenMapped()) {
		   isValidMapping = false;

		   this.validationErrorMessage = "Attribute has already been mapped.";
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
