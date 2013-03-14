/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.common.metadata;

import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.caadapter.common.SDKMetaData;
/**
 * This class holds attributemetadata during the mapping.
 * @version 1.0
 * @created 11-Aug-2006 8:18:15 AM
 * @author LAST UPDATE $Author: wangeug $
 * @since      caAdapter  v4.2
 * @version    $Revision: 1.7 $
 * @date       $Date: 2009-07-30 17:31:55 $
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
	private boolean derived;
	private boolean childTag=true; //define if a child tag is required to represent this the attribute
									//as GME present a object metadata as an XML

	public String getDatatype(){
		return datatype;
	}

	public String getName(){
		return name;
	}

	public String toString() {
		String rtnSt="";
		if (isDerived()) {
			rtnSt= getName() + " (A - Inherited)";
		}
		else  if (isChildTag()){
        	rtnSt=getName() + " (A)";
		}
		else
			rtnSt= "@"+getName() + " (A)";
        if (this.getDatatype()==null)
        	return rtnSt;
        else
        	return rtnSt+":"+this.getDatatype();
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

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.6  2008/09/25 19:30:38  phadkes
 * HISTORY      : Changes for code standards
 * HISTORY      :
*/
