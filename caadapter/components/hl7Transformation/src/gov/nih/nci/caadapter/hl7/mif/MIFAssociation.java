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
 * @version Since caAdapter v4.0 revision $Revision: 1.3 $ date $Date: 2007-06-07 15:02:47 $
 */

public class MIFAssociation extends DatatypeBaseObject implements Serializable,Comparable <MIFAssociation>, Cloneable {
	static final long serialVersionUID = 4L;
	private String sortKey;
	private String updateModeDefault;//not populate yet
	private String updateModesAllowd;//not populate yet
	private boolean mandatory;
	private String conformance;
	private int minimumMultiplicity;
	private int maximumMultiplicity;
	private int multiplicityIndex=0;
	
	private String name;
	private MIFClass mifClass;
	private boolean optionChosen = false;

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
	public int getMultiplicityIndex() {
		return multiplicityIndex;
	}
	public void setMultiplicityIndex(int multiplicityIndex) {
		this.multiplicityIndex = multiplicityIndex;
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
	 * Build nodeXmlName with node name and multiplicityIndex 
	 * @return
	 */
	public String getNodeXmlName()
	{
		if (getMultiplicityIndex()==0)
			return getName();
		
		String stB="";
		if (getMultiplicityIndex()<10)
			stB="0";
		stB=stB+getMultiplicityIndex();
		return getName()+stB;
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
		String myCompKey=this.getSortKey()+this.getMultiplicityIndex();
		String asscCompKey=mifAssc.getSortKey()+mifAssc.getMultiplicityIndex();
		
		return (myCompKey.compareToIgnoreCase(asscCompKey));	
	
	}
	@Override
	public boolean isOptionChosen() {
		// TODO Auto-generated method stub
		return  optionChosen ;
	}
	@Override
	public void setOptionChosen(boolean option) {
		// TODO Auto-generated method stub
		optionChosen=option;
	}
	
	public Object clone()
	{
		 try {
			 MIFAssociation clonnedObj = (MIFAssociation)super.clone();
			 clonnedObj.setMifClass((MIFClass)getMifClass().clone());
             return clonnedObj;
         }
         catch (CloneNotSupportedException e) {
             throw new InternalError(e.toString());
         }

	}
	public String toString()
	{
		if (getMultiplicityIndex()==0)
			return super.toString();
		
		return super.toString()+" ["+getMultiplicityIndex() +"]";
	}
}	
