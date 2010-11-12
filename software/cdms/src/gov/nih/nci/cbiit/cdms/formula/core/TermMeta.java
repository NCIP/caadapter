package gov.nih.nci.cbiit.cdms.formula.core;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "term", propOrder = {
	"term",
})

public class TermMeta extends BaseMeta{
	private List<TermMeta> term;
    @XmlAttribute
	private OperationType operation;
    @XmlAttribute
	private TermType type;
    @XmlAttribute
	private String value;
    
	public List<TermMeta> getTerm() {
		return term;
	}
	public void setTerm(List<TermMeta> term) {
		this.term = term;
	}
	public OperationType getOperation() {
		return operation;
	}
	public void setOperation(OperationType operation) {
		this.operation = operation;
	}
	public TermType getType() {
		return type;
	}
	public void setType(TermType type) {
		this.type = type;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String toString()
	{
		if (term==null||term.isEmpty())
			return value;
		
		StringBuffer rtnBf=new StringBuffer();
		rtnBf.append(getTerm().get(0).toString());
		if (getOperation()!=null)
			rtnBf.append(this.getOperation().toString());
		if (getTerm().size()>1)
			rtnBf.append(getTerm().get(1).toString());
		return rtnBf.toString();
	}
}
