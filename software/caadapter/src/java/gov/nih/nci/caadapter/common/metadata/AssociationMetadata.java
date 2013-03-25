/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.common.metadata;

import gov.nih.nci.caadapter.common.SDKMetaData;
import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociation;

/**
 * This class holds the associationMetaData during mapping
 * @version 1.0
 * @created 11-Aug-2006 8:18:14 AM
 * @author LAST UPDATE $Author: wangeug $
 * @since      caAdapter  v4.2
 * @version    $Revision: 1.9 $
 * @date       $Date: 2009-07-30 17:31:38 $
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

	public String getXPath(){
		return xPath;
	}

// new method added to return already set returnTypeXpath and not from model.
    public String getreturnTypeXPath() {
        return returnTypeXpath;
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
 * HISTORY      : Revision 1.8  2009/06/12 15:21:36  wangeug
 * HISTORY      : clean code: caAdapter MMS 4.1.1
 * HISTORY      :
 * HISTORY      : Revision 1.7  2008/10/20 16:35:16  phadkes
 * HISTORY      : Added new method to return returnTypeXPath. GME changes for correctly referencing RoleName/Class fom xsd.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2008/09/25 19:30:38  phadkes
 * HISTORY      : Changes for code standards
 * HISTORY      :
*/
