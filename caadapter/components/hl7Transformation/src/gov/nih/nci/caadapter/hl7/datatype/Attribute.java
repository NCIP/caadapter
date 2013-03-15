/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.hl7.datatype;

import gov.nih.nci.caadapter.common.util.PropertiesResult;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFCardinality;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * The class defines attributes of a HL7 Datatype.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0 revision $Revision: 1.14 $ date $Date: 2008-06-10 19:54:26 $
 */

public class Attribute extends DatatypeBaseObject implements Serializable, Comparable <Attribute>, Cloneable  {
	static final long serialVersionUID = 1L;

	private String name;

	private String type;

	private int min;

	private int max;
	private int multiplicityIndex=0;
	private String defaultValue;

	private boolean optional;

	private boolean prohibited;

	private boolean attribute;
	private boolean optionChosen = false;
	private String parentXmlPath;
	private boolean enabled = true;
	private boolean simple=true;
	private Datatype referenceDatatype;
	private int sortKey=0;

	public int compareTo(Attribute attr)
	{
		int rtnValue=0;
		//compare attribute type
		if (this.isAttribute()&&!attr.isAttribute())
			rtnValue=-1; //this is attribute and attr is element
		else if (!this.isAttribute()&&attr.isAttribute())
			rtnValue=1; //this is element and attr is attribute
		else
		{
			//compare sortKey
			if (getSortKey()>attr.getSortKey())
				rtnValue=1;
			else if (getSortKey()<attr.getSortKey())
				rtnValue=-1;
			else
			{
				//compare index
				if (getMultiplicityIndex()>attr.getMultiplicityIndex())
					rtnValue=1;
				else if (getMultiplicityIndex()<attr.getMultiplicityIndex())
					rtnValue=-1;
				else
					rtnValue=this.getName().compareTo(attr.getName());
			}
		}
		return rtnValue;
	}
	/**
	  * Build nodeXmlName with node name and multiplicityIndex
	 * @return nodeXmlName as part of the element XML patth
	 */
	public String getNodeXmlName() {
		if (getMax()==1)
			return getName();

		//backward compitable
		if (getMultiplicityIndex()==0)
			return getName();

		String stB="";
		if (getMultiplicityIndex()<10)
			stB="0";
		stB=stB+getMultiplicityIndex();
		return getName()+stB;
	}

	public String getParentXmlPath() {
		return parentXmlPath;
	}

	public void setParentXmlPath(String parentXmlPath) {
		this.parentXmlPath = parentXmlPath;
	}

	/**
	 * Return ture if the attribute is visible/accessable/allowed,
	 * return false, otherwise.
	 *
	 * @return boolean
	 */
	public boolean isValid() {
		if (isProhibited())
			return false;
		if (min == 0 && max == 0)
			return false;
		return true;
	}

	/**
	 * @return the attribute
	 */
	public boolean isAttribute() {
		return attribute;
	}

	/**
	 * @param attribute the attribute to set
	 */
	public void setAttribute(boolean attribute) {
		this.attribute = attribute;
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
	 * @return the max
	 */
	public int getMax() {
		return max;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(int max) {
		this.max = max;
	}

	/**
	 * @return the min
	 */
	public int getMin() {
		return min;
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(int min) {
		this.min = min;
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
	 * @return the optional
	 */
	public boolean isOptional() {
		return optional;
	}

	/**
	 * @param optional the optional to set
	 */
	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	/**
	 * @return the prohibited
	 */
	public boolean isProhibited() {
		return prohibited;
	}

	/**
	 * @param prohibited the prohibited to set
	 */
	public void setProhibited(boolean prohibited) {
		this.prohibited = prohibited;
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

	public Object clone()
	{
		 try {
             Attribute clonnedObj = (Attribute)super.clone();
             if (getReferenceDatatype()!=null)
            	 clonnedObj.setReferenceDatatype((Datatype)getReferenceDatatype().clone());
             return clonnedObj;
         }
         catch (CloneNotSupportedException e) {
             throw new InternalError(e.toString());
         }

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
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enable) {
		this.enabled = enable;
	}

	public boolean isSimple() {
		return simple;
	}

	public void setSimple(boolean valueSimple) {
		simple = valueSimple;
	}

	public Datatype getReferenceDatatype() {
		return referenceDatatype;
	}

	public void setReferenceDatatype(Datatype referenceDatatype) {
		this.referenceDatatype = referenceDatatype;
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
//		propList.add(new PropertyDescriptor("HL7 Default Value", beanClass, "getDefaultValue", null));
		propList.add(new PropertyDescriptor("User Default Value", beanClass, "getDefaultValue", null));
		propList.add(new PropertyDescriptor("Cardinality", beanClass, "findCardinality", null));
		PropertiesResult result = new PropertiesResult();
		result.addPropertyDescriptors(this, propList);
		return result;
	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return "MIF Data Field Properties";
	}
	public String findCardinality() {


		return (new MIFCardinality(getMin(),getMax())).toString();

	}
	public String findTypeProperty() {
		// TODO Auto-generated method stub
		return "Data Type Field";
	}

	public int getMultiplicityIndex() {
		return multiplicityIndex;
	}

	public void setMultiplicityIndex(int multiplicityIndex) {
		this.multiplicityIndex = multiplicityIndex;
	}

	public int getSortKey() {
		return sortKey;
	}

	public void setSortKey(int sortKey) {
		this.sortKey = sortKey;
	}
}
