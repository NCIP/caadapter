/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.mms.map;
import gov.nih.nci.caadapter.common.metadata.AttributeMetadata;

/**
 * This class represents the semantic relationship between attributes of
 * objects contained within different object models.
 *
 * @author OWNER: Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     caAdatper v4.0
 * @version    $Revision: 1.5 $
 * @date       $Date: 2008-09-26 20:35:27 $
 * @created 11-Aug-2006 8:18:18 AM
 */
public class SemanticMapping {

	private String sourceConcept;
	private String targetConcept;
	private AttributeMetadata targetAttribute;
	private AttributeMetadata sourceAttribute;

	public SemanticMapping(){
	}

	/**
	 *
	 * @param targetConcept
	 * @param sourceConcept
	 * @param sourceAttribute
	 * @param targetAttribute
	 */
	public void SemanticMapping(String targetConcept, String sourceConcept, AttributeMetadata sourceAttribute, AttributeMetadata targetAttribute){
		this.sourceConcept=sourceConcept;
		this.targetConcept=targetConcept;
		this.targetAttribute = targetAttribute;
		this.sourceAttribute = sourceAttribute;
	}

	public void setSourceConcept(String sourceConcept) {
		this.sourceConcept = sourceConcept;
	}

	public void setTargetConcept(String targetConcept) {
		this.targetConcept = targetConcept;
	}

	public String getSourceConcept() {
		return sourceConcept;
	}

	public String getTargetConcept() {
		return targetConcept;
	}

	public void setTargetAttribute(AttributeMetadata targetAttribute) {
		this.targetAttribute = targetAttribute;
	}

	public void setSourceAttribute(AttributeMetadata sourceAttribute) {
		this.sourceAttribute = sourceAttribute;
	}

	public AttributeMetadata getTargetAttribute() {
		return targetAttribute;
	}

	public AttributeMetadata getSourceAttribute() {
		return sourceAttribute;
	}

}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 */
