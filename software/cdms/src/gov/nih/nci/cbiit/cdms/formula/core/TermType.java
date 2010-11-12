package gov.nih.nci.cbiit.cdms.formula.core;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "baseType")
@XmlEnum

public enum TermType {
    @XmlEnumValue("unknown")
    UNKNOWN("unknown"),
    @XmlEnumValue("constant")
    CONSTANT("constant"),
    @XmlEnumValue("variable")
    VARIABLE("variable"),
    @XmlEnumValue("expression")
    EXPRESSION("expression");
    private final String value;
    
    TermType(String v)
    {
    	value=v;
    }
    
    public String value() {
        return value;
    }
}
