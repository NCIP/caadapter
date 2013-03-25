/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.mms.map;
import gov.nih.nci.caadapter.common.metadata.AttributeMetadata;

/**
 * This class represents the semantic relationship between attributes of
 * objects contained within different object models.
 * @version 1.0
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
