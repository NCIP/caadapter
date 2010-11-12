package gov.nih.nci.cbiit.cdms.formula.core;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "formula", propOrder = {
	"annotation",
	"expression",
})
public class FormulaMeta extends BaseMeta {
	private TermMeta expression;
	private String annotation;
    @XmlAttribute
	private FormulaType type;
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

	public FormulaType getType() {
		return type;
	}

	public void setType(FormulaType type) {
		this.type = type;
	}
	 
	public String toString()
	{
		StringBuffer rtnBf=new StringBuffer();
		rtnBf.append(this.getName() + " = ");
		rtnBf.append(this.getExpression().toString());
		return rtnBf.toString();
	}
}
