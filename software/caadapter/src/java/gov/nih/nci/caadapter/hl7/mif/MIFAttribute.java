/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.mif;


import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.util.PropertiesResult;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import gov.nih.nci.caadapter.hl7.datatype.Datatype;

/**
 * The class defines attributes of a HL7 Mif class.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: altturbo $
 * @version Since caAdapter v4.0 revision $Revision: 1.25 $ date $Date: 2009-11-11 20:27:09 $
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

//&umkis    private String annotation;
//&umkis	private String comment;

    private String domainName;
	private String mnemonic;
	private String codingStrength;
	private String type;
	private boolean strutural;
	private boolean optionChosen = false;
	private String parentXmlPath;
	private boolean enabled = false;
	private Datatype datatype;
	private Datatype concreteDatatype;
	private boolean mapped;
	private List<String> csvSegments;
	private String csvSegment;
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
		int mySortKey=Integer.valueOf( getSortKey());
		int myIndex= getMultiplicityIndex();
		int attrSortKey=Integer.valueOf(attr.getSortKey());
		int attrIndex=attr.getMultiplicityIndex();
		int rtnValue=0;
		if (mySortKey==attrSortKey)
		{//compare index if sortKey is equal
			if (myIndex>attrIndex)
				rtnValue=1;
			else if (myIndex<attrIndex)
				rtnValue=-1;
		}
		else if (mySortKey>attrSortKey)
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
		return enabled;
	}
	@Override
	public void setEnabled(boolean value) {
		// TODO Auto-generated method stub
		enabled=value;
		if (getDatatype()!=null)
			getDatatype().setEnabled(value);
	}
	public Datatype getConcreteDatatype() {
		return concreteDatatype;
	}
	public void setConcreteDatatype(Datatype concreteDatatype) {
		this.concreteDatatype = concreteDatatype;
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

	public PropertiesResult getPropertyDescriptors() throws Exception {
		// TODO Auto-generated method stub
		Class beanClass = this.getClass();
		List<PropertyDescriptor> propList = new ArrayList<PropertyDescriptor>();
		propList.add(new PropertyDescriptor("Parent", beanClass, "getParentXmlPath", null));
		propList.add(new PropertyDescriptor("Name", beanClass, "getNodeXmlName", null));
		propList.add(new PropertyDescriptor("Type", beanClass, "findTypeProperty", null));

//&umkis        PropertyDescriptor _annotation = new PropertyDescriptor("Annotation", beanClass, "getAnnotation", null);
//&umkis		PropertyDescriptor _comment = new PropertyDescriptor("Comment", beanClass, "getComment", null);
//&umkis		propList.add(_annotation);
//&umkis		propList.add(_comment);

        propList.add(new PropertyDescriptor("Cardinality", beanClass, "findCardinality", null));
		propList.add(new PropertyDescriptor("isMultiple", beanClass, "findIsMultiple", null));
		propList.add(new PropertyDescriptor("Mandatory", beanClass, "isMandatory", null));
		propList.add(new PropertyDescriptor("Conformance", beanClass, "getConformance", null));

		propList.add(new PropertyDescriptor("isAbstract", beanClass, "findIsAbstract", null));
		propList.add(new PropertyDescriptor("Data Type", beanClass, "getType", null));
//		propList.add(new PropertyDescriptor("HL7 Default Value", beanClass, "getDefaultValue", null));
		//propList.add(new PropertyDescriptor("HL7 Default Value", beanClass, "findDefaultValueProperty", null));
        propList.add(new PropertyDescriptor("HL7 Default Value", beanClass, "getFixedValue", null));
        propList.add(new PropertyDescriptor("HL7 Domain", beanClass, "findDomainNameOidProperty", null));
		propList.add(new PropertyDescriptor("Coding Strength", beanClass, "getCodingStrength", null));
		PropertiesResult result = new PropertiesResult();
		result.addPropertyDescriptors(this, propList);

		return result;
	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return "MIF Attribute Properties";
	}
	public String findCardinality() {

		int multMin=Integer.valueOf(this.getMinimumMultiplicity());
		int multMax=Integer.valueOf(this.getMaximumMultiplicity());
		return (new MIFCardinality(multMin,multMax)).toString();

	}
	public String findIsMultiple() {
		if (this.getMaximumMultiplicity()==1)
			return "false";

		return "true";
	}

	public String findIsAbstract() {
		if (getDatatype()!=null
				&&getDatatype().isAbstract())
			return "true";

		return "false";
	}
	public String findTypeProperty() {
		// TODO Auto-generated method stub
		return "Attribute";
	}

	public String findDomainNameOidProperty() {
		// TODO Auto-generated method stub
		String dmName=getDomainName();
		if(dmName==null||dmName.equals(""))
			return dmName;
		String odiSetting=CaadapterUtil.readPrefParams(Config.CAADAPTER_COMPONENT_HL7_SPECFICATION_ODI_ENABLED);
		if (odiSetting==null||!odiSetting.equalsIgnoreCase("true"))
        {
            odiSetting=FileUtil.searchProperty(Config.CAADAPTER_COMPONENT_HL7_SPECFICATION_ODI_ENABLED);
            if (odiSetting==null||!odiSetting.equalsIgnoreCase("true"))
            {
                return dmName;
            }
        }

		long sTime=System.currentTimeMillis();
		String oid="";
		try {
			oid= FileUtil.findODIWithDomainName(dmName);
			if (oid!=null&&!oid.equals(""))
            {
                dmName=dmName+" ("+oid +")";
            }
        } catch (IOException e) {
			// TODO Auto-generated catch block
            //System.out.println(" ### Failure => FileUtil.findODIWithDomainName(domainName='"+dmName+"') : " + e.getMessage());
            //e.printStackTrace();
		}
		return dmName;
	}
	/**
	 * Use fixedValue as default value if available
	 * @return
	 */
	public String findHL7DefaultValueProperty()
	{
		if (getFixedValue()!=null
				&&!getFixedValue().equals(""))
			return getFixedValue();
		return getMnemonic();
	}

//&umkis    public String getAnnotation()
//&umkis    {
//&umkis        if ((annotation != null)&&(!annotation.trim().equals(""))) return annotation;
//&umkis        if (this.getDatatype()!= null)
//&umkis        {
//&umkis            String annot = this.getDatatype().getAnnotation();
//&umkis            if ((annot != null)&&(!annot.trim().equals(""))) return annot;
//&umkis        }
//&umkis        return null;
//&umkis	}

//&umkis	public void setAnnotation(String annotation) {
//&umkis		this.annotation = annotation;
//&umkis	}

//&umkis	public String getComment() {
//&umkis		return comment;
//&umkis	}
//&umkis	public void setComment(String comment) {
//&umkis		this.comment = comment;
//&umkis    }
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.24  2009/10/29 21:35:09  altturbo
 * HISTORY :upgrade FileUtil.findODIWithDomainName()
 * HISTORY :
 * HISTORY :Revision 1.23  2009/10/29 21:33:55  altturbo
 * HISTORY :upgrade FileUtil.findODIWithDomainName()
 * HISTORY :
 * HISTORY :Revision 1.22  2009/04/21 17:06:58  altturbo
 * HISTORY :minor change -- add comment
 * HISTORY :
 * HISTORY :Revision 1.21  2009/04/15 21:20:09  altturbo
 * HISTORY :Upgrade findDomainNameOdiProperty()
 * HISTORY :
 * HISTORY :Revision 1.20  2009/04/02 20:34:40  altturbo
 * HISTORY :add comment and annotation items but deactivated
 * HISTORY :
 * HISTORY :Revision 1.19  2008/09/29 15:44:40  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */