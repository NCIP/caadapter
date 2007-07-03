/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.mif;


import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
/**
 * The class defines attributes of a HL7 Mif class.
 * 
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0 revision $Revision: 1.5 $ date $Date: 2007-07-03 18:22:16 $
 */

public class MIFAttribute extends DatatypeBaseObject implements Serializable, Comparable <MIFAttribute>, Cloneable{
	static final long serialVersionUID = 5L;
	private String sortKey;
	private String defaultValue;
	private String defaultFrom;
	private String fixedValue;
	private int minimumSupportedLength;
	private String updateModeDefault; //not populate yet
	private String updateModesAllowd;//not populate yet
	private boolean mandatory;
	private String conformance;
	private int minimumMultiplicity;
	private int maximumMultiplicity;
	private int multiplicityIndex=0;
	private String name;
	
	private String domainName;
	private String mnemonic;
	private String codingStrength;
	private String type;
	private boolean strutural;
	private boolean optionChosen = false;
	private String parentXmlPath;
	private Datatype datatype;
	private Datatype concreteDatatype;

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
	 * @return the defaultFrom
	 */
	public String getDefaultFrom() {
		return defaultFrom;
	}
	/**
	 * @param defaultFrom the defaultFrom to set
	 */
	public void setDefaultFrom(String defaultFrom) {
		this.defaultFrom = defaultFrom;
	}
	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}
	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	/**
	 * @return the fixedValue
	 */
	public String getFixedValue() {
		return fixedValue;
	}
	/**
	 * @param fixedValue the fixedValue to set
	 */
	public void setFixedValue(String fixedValue) {
		this.fixedValue = fixedValue;
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
	
	public int getMultiplicityIndex() {
		return multiplicityIndex;
	}
	public void setMultiplicityIndex(int index) {
		multiplicityIndex = index;
	}
	/**
	 * @param maximumMultiplicity the maximumMultiplicity to set
	 */
	public void setMaximumMultiplicity(int maximumMultiplicity) {
		this.maximumMultiplicity = maximumMultiplicity;
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
	 * @return the minimumSupportedLength
	 */
	public int getMinimumSupportedLength() {
		return minimumSupportedLength;
	}
	/**
	 * @param minimumSupportedLength the minimumSupportedLength to set
	 */
	public void setMinimumSupportedLength(int minimumSupportedLength) {
		this.minimumSupportedLength = minimumSupportedLength;
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
		if (getMaximumMultiplicity()==1)
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
	/**
	 * @return the mnemonic
	 */
	public String getMnemonic() {
		return mnemonic;
	}
	/**
	 * @param mnemonic the mnemonic to set
	 */
	public void setMnemonic(String mnemonic) {
		this.mnemonic = mnemonic;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the strutural
	 */
	public boolean isStrutural() {
		return strutural;
	}
	/**
	 * @param strutural the strutural to set
	 */
	public void setStrutural(boolean strutural) {
		this.strutural = strutural;
	}
	public int compareTo(MIFAttribute attr) {
		// TODO Auto-generated method stub	
		String myCompKey=this.getSortKey()+this.getMultiplicityIndex();
		String attrCompKey=attr.getSortKey()+attr.getMultiplicityIndex();
		
		return (myCompKey.compareToIgnoreCase(attrCompKey));	
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
			 MIFAttribute clonnedObj = (MIFAttribute)super.clone();
			 if (getDatatype()!=null)
				 clonnedObj.setDatatype((Datatype)getDatatype().clone());
			 if (getConcreteDatatype()!=null)
				 clonnedObj.setConcreteDatatype((Datatype)getConcreteDatatype().clone());
			 
			 return clonnedObj;
         }
         catch (CloneNotSupportedException e) {
             throw new InternalError(e.toString());
         }

	}
	public String toString()
	{
		if (this.getMaximumMultiplicity()==1)
			return getName();
		else
			return getName()+ "  [" + (this.getMultiplicityIndex() +1) +"]";
	}
	public String getParentXmlPath() {
		return parentXmlPath;
	}
	public void setParentXmlPath(String parentXmlPath) {
		this.parentXmlPath = parentXmlPath;
	}
	/**
	 * The persistent Datatype associated with this MIFAttribute
	 * @return
	 */
	public Datatype getDatatype() {
		return datatype;
	}
	public void setDatatype(Datatype datatype) {
		this.datatype = datatype;
	}
	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public void setEnabled(boolean enable) {
		// TODO Auto-generated method stub
		
	}
	public Datatype getConcreteDatatype() {
		return concreteDatatype;
	}
	public void setConcreteDatatype(Datatype concreteDatatype) {
		this.concreteDatatype = concreteDatatype;
	}

}
