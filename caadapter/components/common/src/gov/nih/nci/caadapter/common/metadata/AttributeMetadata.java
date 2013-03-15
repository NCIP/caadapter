/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.common.metadata;

import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.caadapter.common.SDKMetaData;
/**
 * @version 1.0
 * @created 11-Aug-2006 8:18:15 AM
 */
public class AttributeMetadata extends MetaObjectImpl implements SDKMetaData{

	private String datatype;
	/**
	 * This attribute represents the name of an object attribute
	 * concatenated with the fully qualified domain name of the parent
	 * object.
	 */
	private String name;
	private String xPath;
	private String semanticConcept;
	private boolean derived;
	private boolean childTag=true; //define if a child tag is required to represent this the attribute
									//as GME present a object metadata as an XML

	public String getSemanticConcept(){
		return semanticConcept;
	}
	public String getDatatype(){
		return datatype;
	}

	public String getName(){
		return name;
	}

	public String toString() {
		if (isDerived()) {
			return getName() + " (A - Derived)";
		}

        if (isChildTag()){
			return getName() + " (A)";
		}
		else
			return "@"+getName() + " (A)";
		//TODO: check for xmi
	}

	public String getTitle() {
		return getName();
	}

	public String getXPath(){
		return xPath;
	}

	public String getParentXPath() {
		String parentXPath;
		int attributeStartPoint = this.xPath.lastIndexOf(".");
		parentXPath = this.xPath.substring(0,attributeStartPoint);
		return parentXPath;
	}
	/**
	 *
	 * @param datatype
	 */
	public void setDatatype(String datatype){
 		this.datatype = datatype;
	}

	/**
	 *
	 * @param semanticConcept
	 */
	public void setSemanticConcept(String semanticConcept){
		this.semanticConcept = semanticConcept;
	}

	/**
	 *
	 * @param name
	 */
	public void setName(String name){
		this.name = name;
	}

	/**
	 *
	 * @param xPath
	 */
	public void setXPath(String xPath){
		this.xPath = xPath;
	}

	public boolean isDerived() {
		return derived;
	}

	public void setDerived(boolean derived) {
		this.derived = derived;
	}
	public boolean isChildTag() {
		return childTag;
	}
	public void setChildTag(boolean childTag) {
		this.childTag = childTag;
	}

}
