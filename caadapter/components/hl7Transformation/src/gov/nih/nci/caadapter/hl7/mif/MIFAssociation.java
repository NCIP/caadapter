/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.mif;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;
import java.io.Serializable;
import java.util.List;

/**
 * The class defines an MIF association of a HL7 Mif class.
 * 
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wuye $
 * @version Since caAdapter v4.0 revision $Revision: 1.8 $ date $Date: 2007-07-17 20:06:51 $
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
	private boolean choiceSelected =false;//make it serializable
	private String parentXmlPath;
	private boolean optionForced=false;
	private List<String> csvSegments;
	private String csvSegment;
	private boolean mapped;

	
	/**
	 * @return the mapped
	 */
	public boolean isMapped() {
		return mapped;
	}
	/**
	 * @param mapped the mapped to set
	 */
	public void setMapped(boolean mapped) {
		this.mapped = mapped;
	}
	/**
	 * @return the csvSegment
	 */
	public String getCsvSegment() {
		return csvSegment;
	}
	/**
	 * @param csvSegment the csvSegment to set
	 */
	public void setCsvSegment(String csvSegment) {
		this.csvSegment = csvSegment;
	}
	/**
	 * @return the csvSegments
	 */
	public List<String> getCsvSegments() {
		return csvSegments;
	}
	/**
	 * @param csvSegments the csvSegments to set
	 */
	public void setCsvSegments(List<String> csvSegments) {
		this.csvSegments = csvSegments;
	}
	public String getParentXmlPath() {
		return parentXmlPath;
	}
	public void setParentXmlPath(String parentXmlPath) {
		this.parentXmlPath = parentXmlPath;
	}

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
	
	public boolean isChoiceSelected() {
		return choiceSelected;
	}

	public void setChoiceSelected(boolean choiceSelected) {
		this.choiceSelected = choiceSelected;
	}
	public void setChoiceClass(String mifClassName)
	{
//		System.out.println("MIFAssociation.setChoiceClass()..xmlPath:"+getXmlPath());
		if (this.isChoiceSelected())
		{
			//unset the previous choice
			for (MIFClass choiceClass:getMifClass().getChoices())
			{
				if (choiceClass.isChoiceSelected())
				{
					choiceClass.setChoiceSelected(false);
					//remove attribute, association from the chosen class 
					//if they were added from parent class
					for (MIFAttribute parentAttr:getMifClass().getAttributes())
						choiceClass.removeAttributeWithName(parentAttr.getName());
					for (MIFAssociation parentAssc:getMifClass().getAssociations())
						choiceClass.removeAassociationWithNodeXmlName(parentAssc.getNodeXmlName());
					break;
				}
			}
		}
		this.setChoiceSelected(true);
		for (MIFClass choiceClass:getMifClass().getChoices())
		{
			if (choiceClass.getName().equals(mifClassName))
			{
				choiceClass.setChoiceSelected(true);
				//add attribute, associatoin from parent class to the chosen class
				for (MIFAttribute parentAttr:getMifClass().getAttributes())
					choiceClass.addAttribute((MIFAttribute)parentAttr.clone());
				for (MIFAssociation parentAssc:getMifClass().getAssociations())
					choiceClass.addAssociation((MIFAssociation)parentAssc.clone());	
				break;
			}
		}
	}
	public Object clone()
	{
		 try {
			 MIFAssociation clonnedObj = (MIFAssociation)super.clone();
			 MIFClass clonnedMIFClass=(MIFClass)getMifClass().clone();
			 clonnedObj.setMifClass(clonnedMIFClass);
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

	public boolean isEnabled() {
		return true;
	}
	@Override
	public void setEnabled(boolean enable) {
		// TODO Auto-generated method stub
		
	}
	public boolean isOptionForced() {
		return optionForced;
	}
	public void setOptionForced(boolean optionForced) {
		this.optionForced = optionForced;
	}


}	
