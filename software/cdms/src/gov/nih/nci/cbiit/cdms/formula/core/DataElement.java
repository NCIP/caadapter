package gov.nih.nci.cbiit.cdms.formula.core;

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

	public String toString()
	{
		if (getUnit()==null)
			return getName();
		return getName()+"("+getUnit()+")";
	}
	
}
