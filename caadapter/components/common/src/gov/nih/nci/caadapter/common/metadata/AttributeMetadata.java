/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
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
	
	public AttributeMetadata(){
	}

	/**
	 * @param name
	 */
	public AttributeMetadata(String name) {
		super();
		// TODO Auto-generated constructor stub
		this.name = name;
	}

	/**
	 * @param name
	 * @param path
	 */
	public AttributeMetadata(String name, String path) {
		super();
		// TODO Auto-generated constructor stub
		this.name = name;
		xPath = path;
	}

	/**
	 * @param datatype
	 * @param name
	 * @param path
	 */
	public AttributeMetadata(String datatype, String name, String path) {
		super();
		// TODO Auto-generated constructor stub
		this.datatype = datatype;
		this.name = name;
		xPath = path;
	}

	/**
	 *
	 * @param xPath
	 * @param datatype
	 * @param name
	 * @param semanticConcept
	 */
	public AttributeMetadata(String xPath, String datatype, String name, String semanticConcept){
		this.datatype = datatype;
		this.name = name;
		this.xPath = xPath;
		this.semanticConcept = semanticConcept;
	}
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
		}else {
			return getName() + " (A)";
		}
		
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

}