package gov.nih.nci.cbiit.cdms.formula.core;

import gov.nih.nci.cbiit.cdms.formula.gui.properties.PropertiesResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.beans.PropertyDescriptor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "term", propOrder = {
	"term"
})

public class TermMeta extends BaseMeta{
	@XmlAttribute
	private String description;
    @XmlAttribute
	private OperationType operation;
    private List<TermMeta> term;
    @XmlAttribute
	private TermType type;
    @XmlAttribute
	private String unit;
    @XmlAttribute
	private String value;
    
    
	@Override
	public Object clone()
	{
		try {
			TermMeta clonnedObj=(TermMeta)super.clone();
			//deep cloning
			clonnedObj.setTerm(new ArrayList<TermMeta>());
			
			if (this.getTerm()!=null)
			{
				for (TermMeta childTerm:this.getTerm())
				{
					TermMeta clonnedChild=(TermMeta)childTerm.clone();
					clonnedObj.getTerm().add(clonnedChild);
				}
			}

			return clonnedObj;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	public String excute(HashMap param)
	{
		if (getType()==null||
				getType().equals(TermType.CONSTANT))
			return getValue();
		else if (getType().equals(TermType.VARIABLE))
		{
			System.out.println("TermMeta.excute()..parameter:"+param);
			System.out.println("TermMeta.excute()...:"+getValue() +"..."+ param.get(getValue()));
			return (String) param.get(getValue());
		
		}
		else if (getType().equals(TermType.EXPRESSION))
		{
			Double resultSt;
			String termOne=getTerm().get(0).excute(param);
			String termTwo="";
			if (getTerm().size()>1)
				termTwo=getTerm().get(1).excute(param);
			switch ( getOperation())
			{
			case ADDITION:
				resultSt=Double.valueOf(termOne)+Double.valueOf(termTwo);
				break;
			case SUBTRACTION:
				resultSt=Double.valueOf(termOne)-Double.valueOf(termTwo);
				break;
			case MULTIPLICATION:
				resultSt=Double.valueOf(termOne)*Double.valueOf(termTwo);
				break;
			case DIVISION:
				resultSt=Double.valueOf(termOne)/ Double.valueOf(termTwo);
				break;
			case SQUAREROOT:
				resultSt=Math.sqrt(Double.valueOf(termOne));
				break;
				
			case EXPONENTIAL:
				resultSt=Math.exp(Double.valueOf(termOne));
				break;
			case POWER:
				resultSt=Math.pow(Double.valueOf(termOne),
						Double.valueOf(termTwo));
				break;
			case LOGARITHM:
				resultSt=Math.log(Double.valueOf(termTwo))/Math.log(Double.valueOf(termOne));
				break;
			default:
				return termOne+getOperation()+termTwo;
			}
			return resultSt+"";
		}
		return null;
	}
	public String formatJavaStatement(){
		StringBuffer rtnBf=new StringBuffer();
		if (getType()==null||
				getType().value().equals(TermType.CONSTANT.value()))
			rtnBf.append(getValue());
		else if (getType().value().equals(TermType.VARIABLE.value()))
				rtnBf.append("Double.valueof("+getValue()+")");
		else if (getType().value().equals(TermType.EXPRESSION.value()))
		{
			rtnBf.append("(");
			String javaMathSt="";
			switch ( getOperation())
			{
			case SQUAREROOT:
				javaMathSt="Math.sqrt("+getValue()+")";
				break;
				
			case EXPONENTIAL:
				javaMathSt="Math.exp("+getValue()+")";
				break;
			case POWER:
				javaMathSt="Math.pow("+getTerm().get(0).formatJavaStatement()
					+","+ getTerm().get(1).formatJavaStatement()+")";
				break;
			case LOGARITHM:
				javaMathSt="Math.log("+getValue()+")";
			default:
				javaMathSt=getTerm().get(0).formatJavaStatement()
				+ getOperation().toString()
				+ getTerm().get(1).formatJavaStatement();
				break;
			}
			rtnBf.append(javaMathSt);
			rtnBf.append(")");
		}
		else
		{
			if (getTerm()==null||getTerm().isEmpty())
				return rtnBf.toString();
			rtnBf.append(getTerm().get(0).formatJavaStatement());
			if (getOperation()!=null)
				rtnBf.append(this.getOperation().toString());
			if (getTerm().size()>1)
				rtnBf.append(getTerm().get(1).formatJavaStatement());
		}
		return rtnBf.toString();
	}
	public String getDescription() {
		return description;
	}
	public OperationType getOperation() {
		return operation;
	}
	@Override
	public PropertiesResult getPropertyDescriptors() throws Exception {
		Class<?> beanClass = this.getClass();

		List<PropertyDescriptor> propList = new ArrayList<PropertyDescriptor>();
        propList.add( new PropertyDescriptor("Type", beanClass, "getType", null));
        if (getType().equals(TermType.EXPRESSION)) propList.add( new PropertyDescriptor("Operator", beanClass, "getOperation", null));
        propList.add( new PropertyDescriptor("Value", beanClass, "getValue", null));
        propList.add( new PropertyDescriptor("Unit", beanClass, "getUnit", null));
        propList.add( new PropertyDescriptor("Description", beanClass, "getDescription", null));
		PropertiesResult result =super.getPropertyDescriptors();

		result.addPropertyDescriptors(this, propList);
		return result;
	}
	
	public List<TermMeta> getTerm() {
		return term;
	}
	
	@Override
	public String getTitle() {
	    if (getType().equals(TermType.EXPRESSION)) return "Operator Properties";
	    if (getType().equals(TermType.CONSTANT)) return "Constant Properties";
	    if (getType().equals(TermType.VARIABLE)) return "Variable Properties";
	return "Term Properties";
}
	
	public TermType getType() {
		return type;
	}
	public String getUnit() {
		return unit;
	}
	public String getValue() {
		return value;
	}


	public void setDescription(String description) {
		this.description = description;
	}
	public void setOperation(OperationType operation) {
		this.operation = operation;
	}
	public void setTerm(List<TermMeta> term) {
		this.term = term;
	}
	
	public void setType(TermType type) {
		this.type = type;
	}
	
	public void setUnit(String unit) {
		this.unit = unit;
	}

    	public void setValue(String value) {
			this.value = value;
		}

	public String toString()
	{
		StringBuffer rtnBf=new StringBuffer();
		if (getTerm()==null||getTerm().isEmpty())
			rtnBf.append(getValue());
		else
		{
			rtnBf.append("(");
			rtnBf.append(getTerm().get(0).toString());
			if (getOperation()!=null)
				rtnBf.append(this.getOperation().toString());
			if (getTerm().size()>1)
				rtnBf.append(getTerm().get(1).toString());
			rtnBf.append(")");
		}
		if (getDescription()!=null)
			 rtnBf.append(getDescription());
		
		return rtnBf.toString();
	}
}
