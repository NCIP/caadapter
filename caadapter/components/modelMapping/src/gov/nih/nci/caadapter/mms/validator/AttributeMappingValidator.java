/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.mms.validator;
import gov.nih.nci.caadapter.mms.generator.CumulativeMappingGenerator;
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
 *
 * @author OWNER: Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     caAdatper v4.0
 * @version    $Revision: 1.7 $
 * @date       $Date: 2009-09-30 17:09:58 $
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


	private boolean hasBeenMapped(){
		boolean beenMapped = false;
		try {
			cummulativeMapping = CumulativeMappingGenerator.getInstance().getCumulativeMapping();
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

    private boolean hasDependencyMapping(){
		boolean dependencyMapped = false;
		try {
			cummulativeMapping = CumulativeMappingGenerator.getInstance().getCumulativeMapping();
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

	public boolean isValid(){
		// This method returns "True" unless any of the following tests fail:
		// the parent object and parent table have not been mapped as a dependency
		// if the attribute has been previously mapped
		boolean isValidMapping = true;
		if (!hasDependencyMapping()) {
			isValidMapping = false;
		} 

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
/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.6  2009/06/12 15:52:30  wangeug
 * HISTORY: clean code: caAdapter MMS 4.1.1
 * HISTORY:
 * HISTORY: Revision 1.5  2008/09/26 20:35:27  linc
 * HISTORY: Updated according to code standard.
 * HISTORY:
 */
