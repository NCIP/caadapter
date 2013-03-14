/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.core;


import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gov.nih.nci.cbiit.cdms.formula.gui.properties.PropertiesResult;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "formula", propOrder = {
	"annotation",
	"expression",
	"parameter"
})
public class FormulaMeta extends BaseMeta {
	private TermMeta expression;
	private String annotation;
	private List<DataElement> parameter;
    @XmlAttribute
	private FormulaType type;
    @XmlAttribute
	private FormulaStatus status;
    @XmlAttribute
    private Date dateModified;
    
	public List<DataElement> getParameter() {
		return parameter;
	}

	public void setParameter(List<DataElement> parameter) {
		this.parameter = parameter;
	}

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public TermMeta getExpression() {
		return expression;
	}

	public void setExpression(TermMeta formula)
	{
		expression = formula;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public FormulaStatus getStatus() {
		return status;
	}

	public void setStatus(FormulaStatus status) {
		this.status = status;
	}

	public FormulaType getType() {
		return type;
	}

	public void setType(FormulaType type) {
		this.type = type;
	}

	@Override
	public String formatJavaStatement() {
		// TODO Auto-generated method stub
		return getExpression().formatJavaStatement();
	}

	public String toString()
	{
		StringBuffer rtnBf=new StringBuffer();
		rtnBf.append(this.getName() + " = ");
		rtnBf.append(this.getExpression().toString());
		return rtnBf.toString();
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Formula Properties";
	}

	@Override
	public PropertiesResult getPropertyDescriptors() throws Exception {
		Class<?> beanClass = this.getClass();

		List<PropertyDescriptor> propList = new ArrayList<PropertyDescriptor>();
		propList.add( new PropertyDescriptor("Group", beanClass, "getType", null));
		propList.add( new PropertyDescriptor("Status", beanClass, "getStatus", null));
		propList.add( new PropertyDescriptor("Unit", beanClass, "findExpressionUnit", null));
		propList.add( new PropertyDescriptor("Description", beanClass, "findExpressionDescription", null));
		propList.add( new PropertyDescriptor("Annotation", beanClass, "getAnnotation", null));
		propList.add( new PropertyDescriptor("Last Modified", beanClass, "getDateModified", null));
		PropertiesResult result =super.getPropertyDescriptors();

		result.addPropertyDescriptors(this, propList);
		return result;
	}
	
	public String findExpressionUnit()
	{
		return getExpression().getUnit();
	}
	public String findExpressionDescription()
	{
		return getExpression().getDescription();
	}

    public String getMathML()
    {
        String lbText="";

        lbText=this.getName();
        if (this.getExpression().getUnit()!=null
                &&this.getExpression().getUnit().trim().length()>0)
            lbText=lbText+"("+this.getExpression().getUnit()+")";
        String head = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<math xmlns=\"http://www.w3.org/1998/Math/MathML\" display=\"block\">\n" +
                "   <mstyle displaystyle=\"true\">\n" +
                "      <mi>"+lbText+"</mi>\n" +
                "      <mo>=</mo>\n";


        return head + expression.getMathML(2) + "   </mstyle>\n</math>";
    }
}
