/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.hl7.transformation.data;

/**
 * The class contains attributes of a HL7 v3 XMLElement.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-06-09 19:53:50 $
 */

public class Attribute {
	private String name;
	private String value;
	private String datatype;
	private String domainName;
	private String codingStrength;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the datatype
	 */
	public String getDatatype() {
		return datatype;
	}
	/**
	 * @param datatype the datatype to set
	 */
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	/**
	 * @return the codingStrength
	 */
	public String getCodingStrength() {
		return codingStrength;
	}
	/**
	 * @param codingStrength the codingStrength to set
	 */
	public void setCodingStrength(String codingStrength) {
		this.codingStrength = codingStrength;
	}
	/**
	 * @return the domainName
	 */
	public String getDomainName() {
		return domainName;
	}
	/**
	 * @param domainName the domainName to set
	 */
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
}
