/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.mif;



import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;

import java.io.Serializable;
/**
 * The class defines an MIF association of a HL7 Mif class.
 * 
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0 revision $Revision: 1.2 $ date $Date: 2007-05-25 15:05:19 $
 */

public class MIFAssociation extends DatatypeBaseObject implements Serializable,Comparable <MIFAssociation> {
	static final long serialVersionUID = 4L;
	private String sortKey;
	private String updateModeDefault;//not populate yet
	private String updateModesAllowd;//not populate yet
	private boolean mandatory;
	private String conformance;
	private int minimumMultiplicity;
	private int maximumMultiplicity;
	private String name;
	private MIFClass mifClass;

	/**
	 * @return the conformance
	 */
	public String getConformance() {
		return conformance;
	}
	/**
	 * @param conformance the conformance to set
	 */
	public void setConformance(String conformance) {
		this.conformance = conformance;
	}
	/**
	 * @return the mandatory
	 */
	public boolean isMandatory() {
		return mandatory;
	}
	/**
	 * @param mandatory the mandatory to set
	 */
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}
	/**
	 * @return the maximumMultiplicity
	 */
	public int getMaximumMultiplicity() {
		return maximumMultiplicity;
	}
	/**
	 * @param maximumMultiplicity the maximumMultiplicity to set
	 */
	public void setMaximumMultiplicity(int maximumMultiplicity) {
		this.maximumMultiplicity = maximumMultiplicity;
	}
	/**
	 * @return the mifClass
	 */
	public MIFClass getMifClass() {
		return mifClass;
	}
	/**
	 * @param mifClass the mifClass to set
	 */
	public void setMifClass(MIFClass mifClass) {
		this.mifClass = mifClass;
	}
	/**
	 * @return the minimumMultiplicity
	 */
	public int getMinimumMultiplicity() {
		return minimumMultiplicity;
	}
	/**
	 * @param minimumMultiplicity the minimumMultiplicity to set
	 */
	public void setMinimumMultiplicity(int minimumMultiplicity) {
		this.minimumMultiplicity = minimumMultiplicity;
	}
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
	 * @return the sortKey
	 */
	public String getSortKey() {
		return sortKey;
	}
	/**
	 * @param sortKey the sortKey to set
	 */
	public void setSortKey(String sortKey) {
		System.out.println("MIFAssociation.setSortKey():"+sortKey);
		this.sortKey = sortKey;
	}
	/**
	 * @return the updateModeDefault
	 */
	public String getUpdateModeDefault() {
		return updateModeDefault;
	}
	/**
	 * @param updateModeDefault the updateModeDefault to set
	 */
	public void setUpdateModeDefault(String updateModeDefault) {
		this.updateModeDefault = updateModeDefault;
	}
	/**
	 * @return the updateModesAllowd
	 */
	public String getUpdateModesAllowd() {
		return updateModesAllowd;
	}
	/**
	 * @param updateModesAllowd the updateModesAllowd to set
	 */
	public void setUpdateModesAllowd(String updateModesAllowd) {
		this.updateModesAllowd = updateModesAllowd;
	}
	public int compareTo(MIFAssociation mifAssc) {
		// TODO Auto-generated method stub
		return (this.getSortKey().compareToIgnoreCase(mifAssc.getSortKey()));//.getSortKey()));
	}
}	
