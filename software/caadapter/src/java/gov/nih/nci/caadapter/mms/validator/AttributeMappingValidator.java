/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.mms.validator;
import gov.nih.nci.caadapter.common.SDKMetaData;
import gov.nih.nci.caadapter.mms.generator.CumulativeMappingGenerator;
import gov.nih.nci.caadapter.mms.map.AttributeMapping;
import gov.nih.nci.caadapter.mms.map.CumulativeMapping;
import gov.nih.nci.caadapter.mms.map.DependencyMapping;

import java.util.Iterator;
import java.util.List;

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
//    private static Properties datatypeCompatabilityProp;
    private CumulativeMapping cummulativeMapping;
//	{
//		try{
//			datatypeCompatabilityProp = DatatypeCompatablityProperties.getInstance().getProperties();
//		} catch(Exception e) {
//			//Add logging here
//			}
//	}

	public AttributeMappingValidator(AttributeMapping mapping){
		this.mapping = mapping;
	}

	public void setAttributeMapping(AttributeMapping mapping) {
		this.mapping = mapping;
	}

//	private boolean hasParentsBeenMapped()
//	{
//		boolean beenMapped = false;
//		try {
//			cummulativeMapping = CumulativeMappingGenerator.getInstance().getCumulativeMapping();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		cummulativeMapping.findMappedSource(tgrtPath)
//		return beenMapped;
//	}
	private boolean hasBeenMapped(){
		boolean beenMapped = false;
		validationErrorMessage = "Attribute has already been mapped.";
		try {
			cummulativeMapping = CumulativeMappingGenerator.getInstance().getCumulativeMapping();
		} catch (Exception e) {
			e.printStackTrace();
		}
		SDKMetaData trgtMeta=(SDKMetaData)cummulativeMapping.findMappedTarget(mapping.getAttributeMetadata().getXPath());
		SDKMetaData srcMeta=(SDKMetaData)cummulativeMapping.findMappedSource(mapping.getColumnMetadata().getXPath());
		if (trgtMeta==null|srcMeta==null)
			return false;
		if (!trgtMeta.getXPath().equals(mapping.getColumnMetadata().getXPath()))
			return false;
		if (!srcMeta.getXPath().equals(mapping.getAttributeMetadata().getXPath()))
			return false;
		beenMapped = true;
		validationErrorMessage =null;
		return beenMapped;
//		List attributeMappings = cummulativeMapping.getAttributeMappings();
//		Iterator i = attributeMappings.iterator();
//
//		while (i.hasNext()) {
//			try {
//				AttributeMapping m = (AttributeMapping)i.next();
//				if (m.getAttributeMetadata().getName().equals(mapping.getAttributeMetadata().getName()) && m.getAttributeMetadata().getXPath().equals(mapping.getAttributeMetadata().getXPath())){
//					beenMapped = true;
//					validationErrorMessage = "Attribute has already been mapped.";
//					break;
//				}
//			} catch (Exception e) {
//		            //LOGGER.fine(e.getMessage());
//		            e.printStackTrace();
//			}
//		}
// 		return beenMapped;
	}

    private boolean hasDependencyMapping(){
		boolean dependencyMapped = true;
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
		if (dependencyMapped)
			return dependencyMapped;
		//check if the parent attribute is mapped in case it is an ISO 21090 datatype attribute
		if (!dependencyMapped)
			this.validationErrorMessage = "Parent object and table are not dependency mapped.";
 		return dependencyMapped;
	}

	public boolean isValid(){
		// This method returns "True" unless any of the following tests fail:
		// the parent object and parent table have not been mapped as a dependency
		// if the attribute has been previously mapped
		boolean isValidMapping = true;
		if (!hasDependencyMapping())
			isValidMapping = false;
		else	if (hasBeenMapped())
		   isValidMapping = false;
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
