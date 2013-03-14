/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.common.metadata;

import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.caadapter.common.SDKMetaData;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @created 11-Aug-2006 8:18:17 AM
 * @author LAST UPDATE $Author: phadkes $
 * @since      caAdapter  v4.2
 * @version    $Revision: 1.4 $
 * @date       $Date: 2008-09-25 19:30:38 $
 */
public class ObjectMetadata extends MetaObjectImpl implements SDKMetaData{

	/**
	 * This should be a list of this object's attributes represented by
	 * AttributeMetadata objects.
	 */
	private List<AttributeMetadata> attributes = new ArrayList<AttributeMetadata>();

	/**
	 * This should be a list of this object's associations represented by
	 * AssociationMetadata objects.
	 */
	private List<AssociationMetadata> associations = new ArrayList<AssociationMetadata>();

	/**
	 * This value should be the fully qualifed domain name of the object.
	 */
	private String name;
	/**
	 * This attribute indicates the fully qualified name of the parent
	 * object if one exists.
	 */
	private ObjectMetadata parentObject;

	/**
	 * This attribute indicates the location of the object within the source model xmi file
	 */

	private String xPath;

	public ObjectMetadata(){
	}

	/**
	 * @param parentObject
	 */
	public ObjectMetadata(ObjectMetadata parentObject) {
		super();
		// TODO Auto-generated constructor stub
		this.parentObject = parentObject;
	}

	/**
	 * @param name
	 */
	public ObjectMetadata(String name) {
		super();
		// TODO Auto-generated constructor stub
		this.name = name;
	}

	/**
	 * @param attributes
	 * @param name
	 * @param path
	 */
	public ObjectMetadata(List<AttributeMetadata> attributes, String name, String path) {
		super();
		// TODO Auto-generated constructor stub
		this.attributes = attributes;
		this.name = name;
		xPath = path;
	}

	/**
	 * @param associations
	 * @param name
	 * @param parentObject
	 * @param path
	 */
	public ObjectMetadata(List<AssociationMetadata> associations, String name, ObjectMetadata parentObject, String path) {
		super();
		// TODO Auto-generated constructor stub
		this.associations = associations;
		this.name = name;
		this.parentObject = parentObject;
		xPath = path;
	}

	/**
	 *
	 * @param parentObject
	 * @param xPath
	 * @param attributes
	 * @param name
	 * @param associations
	 */
	public ObjectMetadata(ObjectMetadata parentObject, String xPath, List<AttributeMetadata> attributes, List<AssociationMetadata> associations, String name){
		this.attributes = attributes;
		this.associations = associations;
		this.parentObject = parentObject;
		this.name = name;
		this.xPath = xPath;
	}

	/**
	 * @param name
	 * @param path
	 */
	public ObjectMetadata(String name, String path) {
		super();
		// TODO Auto-generated constructor stub
		this.name = name;
		xPath = path;
	}

	/**
	 *
	 * @param attribute
	 */
	public void addAttribute(AttributeMetadata attribute){
		attributes.add(attribute);
	}

	/**
	 *
	 * @param association
	 */
	public void addAssociation(AssociationMetadata association){
		associations.add(association);
	}

	/**
	 *
	 * @param association
	 */
	public void removeAssociation(AssociationMetadata association){
			associations.remove(association);
	}
	/**
	 *
	 * @param attribute
	 */
	public void removeAttribute(AttributeMetadata attribute){
		attributes.remove(attribute);
	}

	/**
	 *
	 * @param attributeMetadata
	 */
	public boolean containsAttribute(AttributeMetadata attributeMetadata){
		boolean containsAttribute = false;
		if (attributes.contains(attributeMetadata)){
		   containsAttribute = true;
	   	}
	   	return containsAttribute;
	}
	/**
	 *
	 * @param associationMetadata
	 */
	public boolean containsAssociation(AssociationMetadata associationMetadata){
		boolean containsAssociation = false;
		if (associations.contains(associationMetadata)) {
			containsAssociation = true;
		}
		return containsAssociation;
	}

	public List<AttributeMetadata> getAttributes(){
		return attributes;
	}

	public List<AssociationMetadata> getAssociations(){
			return associations;
	}

	public String getName(){
		return name;
	}

	public ObjectMetadata getParentObject(){
		return parentObject;
	}

	public String getXPath(){
		return xPath;
	}

	/**
	 *
	 * @param attributes
	 */
	public void setAttributes(List<AttributeMetadata> attributes){
		this.attributes = attributes;
	}

	/**
	 *
	 * @param associations
	 */
	public void setAssociations(List<AssociationMetadata> associations){
		this.associations = associations;
	}
	/**
	 *
	 * @param name
	 */
	public void setName(String name){
		this.name = name;
	}

	public String toString() {
		return getName();
	}

	public String getTitle() {
		return getName();
	}

	/**
	 *
	 * @param parentObject
	 */
	public void setParentObject(ObjectMetadata parentObject){
		this.parentObject = parentObject;
	}

	/**
	 *
	 * @param xPath
	 */
	public void setXPath(String xPath){
		this.xPath = xPath;
	}

	String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

//	public String getRootId() {
//		ModelMetadata myModel = ModelMetadata.getInstance();
//		HashMap myMap = myModel.getInheritanceMetadata();
//		HashMap objectMap = myModel.getObjectMetadata();
//		if (myMap.get(this.getId()) == null) return "";
//		else {
//			String root = (String)myMap.get(this.getId());
//			while (myMap.get(root)!= null) {
//				root = (String)myMap.get(root);
//			}
//		return (String)objectMap.get(root);
//		}
//	}
//
	private UMLClass umlClass;

	/**
	 * @return the umlClass
	 */
	public UMLClass getUmlClass() {
		return umlClass;
	}

	/**
	 * @param umlClass the umlClass to set
	 */
	public void setUmlClass(UMLClass umlClass) {
		this.umlClass = umlClass;
	}

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
