/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.core;

import gov.nih.nci.cbiit.cdms.formula.gui.properties.PropertiesProvider;
import gov.nih.nci.cbiit.cdms.formula.gui.properties.PropertiesResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.beans.PropertyDescriptor;
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
public abstract class BaseMeta implements PropertiesProvider, Serializable, Cloneable{
    @XmlAttribute
	private String name;

    @XmlAttribute
	private String id;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public abstract String formatJavaStatement();
	
	@Override
	public String toString()
	{
		return name;
		
	}
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
	
	public PropertiesResult getPropertyDescriptors() throws Exception {
		Class<?> beanClass = this.getClass();
		List<PropertyDescriptor> propList = new ArrayList<PropertyDescriptor>();
		
		propList.add(new PropertyDescriptor("Name", beanClass, "getName", null));
		PropertiesResult result = new PropertiesResult();
		result.addPropertyDescriptors(this, propList);
		return result;
	}
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Base Meta Properties";
	}

    
}
