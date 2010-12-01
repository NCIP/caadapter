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
    private String usage;
	
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

	public String getUsage() {
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

	public void setUsage(String usage) {
		this.usage = usage;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Parameter Properties";
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
		if (getUnit()==null)
			return getName();
		return getName()+"("+getUnit()+")";
	}
	
}
