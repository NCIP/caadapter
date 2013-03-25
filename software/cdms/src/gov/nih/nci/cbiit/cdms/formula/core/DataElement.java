/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.core;

import gov.nih.nci.cbiit.cdms.formula.gui.properties.PropertiesResult;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;

public class DataElement extends BaseMeta {

    @XmlAttribute
    private String cdeId;
    @XmlAttribute
    private String cdeReference;
    @XmlAttribute
    private String dataType;
    @XmlAttribute
    private String description;
    @XmlAttribute
    private String unit;
    @XmlAttribute
    private DataElementUsageType usage;
	
	@Override
	public String formatJavaStatement() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCdeId() {
		return cdeId;
	}

	public String getCdeReference() {
		return cdeReference;
	}

	public String getDataType() {
		return dataType;
	}

	public String getDescription() {
		return description;
	}

	public String getUnit() {
		return unit;
	}

	public DataElementUsageType getUsage() {
		return usage;
	}

	public void setCdeId(String cdeId) {
		this.cdeId = cdeId;
	}

	public void setCdeReference(String cdeReference) {
		this.cdeReference = cdeReference;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public void setUsage(DataElementUsageType usage) {
		this.usage = usage;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Data Element Properties";
	}

	@Override
	public PropertiesResult getPropertyDescriptors() throws Exception {
		Class<?> beanClass = this.getClass();

		List<PropertyDescriptor> propList = new ArrayList<PropertyDescriptor>();
		propList.add( new PropertyDescriptor("Description", beanClass, "getDescription", null));
		propList.add( new PropertyDescriptor("Data Type", beanClass, "getDataType", null));
		propList.add( new PropertyDescriptor("Unit", beanClass, "getUnit", null));
		propList.add( new PropertyDescriptor("Usage", beanClass, "getUsage", null));	
		propList.add( new PropertyDescriptor("CDE Public ID", beanClass, "getCdeId", null));
		propList.add( new PropertyDescriptor("CDE URI", beanClass, "getCdeReference", null));
		PropertiesResult result =super.getPropertyDescriptors();

		result.addPropertyDescriptors(this, propList);
		return result;
	}
	
	@Override
	public String toString()
	{
 		if (getUnit()==null||getUnit().isEmpty())
			return getName();
		
		return getName()+"("+getUnit()+")";
	}
	
}
