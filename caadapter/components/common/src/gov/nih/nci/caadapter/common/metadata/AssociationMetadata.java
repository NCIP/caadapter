/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.common.metadata;

import java.util.HashMap;
import gov.nih.nci.caadapter.common.SDKMetaData;
import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociation;

/**
 * This class holds the associationMetaData during mapping
 * @version 1.0
 * @created 11-Aug-2006 8:18:14 AM
 * @author LAST UPDATE $Author: phadkes $
 * @since      caAdapter  v4.2    
 * @version    $Revision: 1.6 $
 * @date       $Date: 2008-09-25 19:30:38 $
 */
public class AssociationMetadata extends MetaObjectImpl implements SDKMetaData{
	
	/**
	 * This value indicates the multipicity of the role
	 */
	private int multiplicity;
	
	/**
	 * This value inidcates the multiplicity of the other end role
	 */
	private int reciprocalMultiplity;
	
	/**
	 * This boolean value indicates whether or not the object can be
	 * accessed from the other end of the relationship this object is an end
	 * of.
	 */
	private boolean navigability;
	
	private boolean isBidirectional;
	
	private UMLAssociation uMLAssociation;
	/**
	 * This value represents the name of the association.
	 */
	private String roleName;
	public String toString() {
		return getRoleName() + " (" + (getReciprocalMultiplity()== -1?"Many":getReciprocalMultiplity()) + " to " + (getMultiplicity()==-1?"Many":getMultiplicity()) +")";
//		return getRoleName() + " (" + (getMultiplicity()== -1?"Many":getMultiplicity()) + " to " + (getReciprocalMultiplity()==-1?"Many":getReciprocalMultiplity()) +")";
	}
	public String getTitle() {
		return getRoleName();
	}
	/**
	 * This value represents the rolename of the other end of the association
	 */
	private String recipricolRoleName;

	/**
	 *This value represents the location of the association within the XMI file
	 */
	private String xPath;

	/**
	 * This object is the type of object this role name represents.
	 * For example, for the role name Gene.taxon
	 * the parent object would be Taxon
	 */
	private String returnTypeXpath;

	public int getMultiplicity(){
		return multiplicity;
	}

	public boolean getNavigability(){
		return navigability;
	}

	public String getRoleName(){
		return roleName;
	}

	public String getReciprocolRoleName(){
		return recipricolRoleName;
	}
	public String getXPath(){
		return xPath;
	}

	public String getReturnTypeXPath() {
		ModelMetadata myModel = ModelMetadata.getInstance();
		HashMap objectMap = myModel.getObjectMetadata();
		return (String)objectMap.get(returnTypeXpath);
	}


	/**
	 *
	 * @param multiplicity
	 */
	public void setMultiplicity(int multiplicity){
		this.multiplicity = multiplicity;
	}

	/**
	 *
	 * @param navigability
	 */
	public void setNavigability(boolean navigability){
		this.navigability = navigability;
	}

	/**
	 *
	 * @param roleName
	 */
	public void setRoleName(String roleName){
		this.roleName = roleName;
	}

    public String getName(){
		return this.roleName;
	}
    /**
	 *
	 * @param reciprocolRoleName
	 */
	public void setReciprocolRoleName(String reciprocolRoleName){
		this.recipricolRoleName = reciprocolRoleName;
	}

	/**
	 *
	 * @param xPath
	 */
	public void setXPath(String xPath){
		this.xPath = xPath;
	}

	/**
	 *
	 * @param returnTypeXpath
	 */
	public void setReturnTypeXPath(String returnTypeXpath){
		this.returnTypeXpath = returnTypeXpath;
	}

	/**
	 * @return Returns the reciprocalMultiplity.
	 */
	public int getReciprocalMultiplity() {
		return reciprocalMultiplity;
	}

	/**
	 * @param reciprocalMultiplity The reciprocalMultiplity to set.
	 */
	public void setReciprocalMultiplity(int reciprocalMultiplity) {
		this.reciprocalMultiplity = reciprocalMultiplity;
	}
	 public String getParentXPath() {
			String parentXPath;
			int attributeStartPoint = this.xPath.lastIndexOf(".");
			parentXPath = this.xPath.substring(0,attributeStartPoint);
			return parentXPath;
		}

	public boolean isBidirectional() {
		return isBidirectional;
	}

	public void setBidirectional(boolean isBidirectional) {
		this.isBidirectional = isBidirectional;
	}
	public UMLAssociation getUMLAssociation() {
		return uMLAssociation;
	}
	public void setUMLAssociation(UMLAssociation association) {
		uMLAssociation = association;
	}
	boolean manyToOne;
	public boolean isManyToOne() {
		return manyToOne;
	}
	public void setManyToOne(boolean manyToOne) {
		this.manyToOne = manyToOne;
	}

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
