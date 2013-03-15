/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.hl7.mif;
import gov.nih.nci.caadapter.common.util.PropertiesResult;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;

/**
 * The class defines an MIF association of a HL7 Mif class.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision $Revision: 1.19 $ date $Date: 2008-06-09 19:53:50 $
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
	private Hashtable <String,String> participantTraversalNames;

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
	 * Found the choiceSelected MIFClass if this Association contains a choice
	 * @return chosenMIFClass
	 */
	public MIFClass findChoiceSelectedMifClass() {
		if (!isChoiceSelected())
			return null;
		for(MIFClass choiceClass:getMifClass().getSortedChoices())
		{
			if (choiceClass.isChoiceSelected())
				return choiceClass;
		}
		return null;
	}
	/**
	 * @param mifClass the mifClass to set
	 */
	public void setMifClass(MIFClass mifClass) {
        //mifClass.setParent(this);
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
		String viewName=getName();
		if (isChoiceSelected()&&getMifClass().getChoices().size()>0)
		{
			MIFClass chosenMif=findChoiceSelectedMifClass();
			if (chosenMif!=null)
				viewName=chosenMif.getNodeXmlName();
		}

		if (getMaximumMultiplicity()==1)
		{
			if(!MIFUtil.containChoiceAssociation(this))
				return viewName;

		}

		String stB="";
		if (getMultiplicityIndex()<10)
			stB="0";
		stB=stB+getMultiplicityIndex();
		return viewName+stB;
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
//		String myCompKey=this.getSortKey()+this.getMultiplicityIndex();
//		String asscCompKey=mifAssc.getSortKey()+mifAssc.getMultiplicityIndex();
//
//		return (myCompKey.compareToIgnoreCase(asscCompKey));
		int mySortKey=Integer.valueOf( getSortKey());
		int myIndex= getMultiplicityIndex();
		int asscSortKey=Integer.valueOf(mifAssc.getSortKey());
		int asscIndex=mifAssc.getMultiplicityIndex();
		int rtnValue=0;
		if (mySortKey==asscSortKey)
		{//compare index if sortKey is equal
			if (myIndex>asscIndex)
				rtnValue=1;
			else if (myIndex<asscIndex)
				rtnValue=-1;
		}
		else if (mySortKey>asscSortKey)
			rtnValue= 1;
		else
			rtnValue= -1;
		return rtnValue;
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

	public Object clone()
	{
		 try {
			 MIFAssociation clonnedObj = (MIFAssociation)super.clone();
			 MIFClass clonnedMIFClass=(MIFClass)getMifClass().clone();
			 clonnedObj.setMifClass(clonnedMIFClass);
			 if(this.getParticipantTraversalNames()!=null)
			 {
				 Hashtable <String, String> clonedHT=new Hashtable<String,String>();
				for(String partKey:this.getParticipantTraversalNames().keySet())
				{
					String traversalName=this.getParticipantTraversalNames().get(partKey);
					clonedHT.put(partKey, traversalName);
				}
				clonnedObj.setParticipantTraversalNames(clonedHT);
			 }

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

	public PropertiesResult getPropertyDescriptors() throws Exception {
		// TODO Auto-generated method stub
		Class beanClass = this.getClass();

		PropertyDescriptor _name = new PropertyDescriptor("Name", beanClass, "getName", null);
		PropertyDescriptor _parentPath = new PropertyDescriptor("Parent", beanClass, "getParentXmlPath", null);
		PropertyDescriptor _class = new PropertyDescriptor("Type", beanClass, "findTypeProperty", null);
		List<PropertyDescriptor> propList = new ArrayList<PropertyDescriptor>();
		propList.add(_name);
		propList.add(_parentPath);
		propList.add(_class);
		propList.add(new PropertyDescriptor("isReference", beanClass, "findIsRerence", null));
		propList.add(new PropertyDescriptor("Cardinality", beanClass, "findCardinality", null));
		PropertiesResult result = new PropertiesResult();
		result.addPropertyDescriptors(this, propList);
		return result;
	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return "MIF Association Properties";
	}

	public String findCardinality() {

		int multMin=Integer.valueOf(this.getMinimumMultiplicity());
		int multMax=Integer.valueOf(this.getMaximumMultiplicity());
		return (new MIFCardinality(multMin,multMax)).toString();

	}

	public String findIsRerence() {

		if (this.getMifClass()!=null)
			return String.valueOf(getMifClass().isReference());
		return "false";
	}
	public String findTypeProperty() {
		// TODO Auto-generated method stub
		return "Association Clone";
	}
	public Hashtable<String, String> getParticipantTraversalNames() {
		return participantTraversalNames;
	}
	public void setParticipantTraversalNames(
			Hashtable<String, String> participantTraversalNames) {
		this.participantTraversalNames = participantTraversalNames;
	}
}
