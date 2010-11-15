package gov.nih.nci.cbiit.cdms.formula.core;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "baseMeta")
@XmlSeeAlso({
	FormulaMeta.class,
	TermMeta.class
}
)
public abstract class BaseMeta implements Serializable, Cloneable{
    @XmlAttribute
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public abstract String formatJavaStatement();
	@Override
	public Object clone()
	{
		try {
			BaseMeta clonnedObj=(BaseMeta)super.clone();
			return clonnedObj;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
}
