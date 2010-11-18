package gov.nih.nci.cbiit.cdms.formula.core;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "operationType")
@XmlEnum

public enum OperationType {
    @XmlEnumValue("addition")
    ADDITION("addition"),
    @XmlEnumValue("subtraction")
    SUBTRACTION("subtraction"),
    @XmlEnumValue("multiplication")
    MULTIPLICATION("multiplication"),
    @XmlEnumValue("division")
    DIVISION("division"),
    @XmlEnumValue("power")
    POWER("power"),
    @XmlEnumValue("radical")
    RADICAL("radical"),
    @XmlEnumValue("squareRoot")
    SQUAREROOT("squareRoot"),
    @XmlEnumValue("exponential")
    EXPONENTIAL("exponential"),
    @XmlEnumValue("logarithm")
    LOGARITHM("logarithm"),

    @XmlEnumValue("trigonometric")
    TRIGONOMETRIC("trigonometric");

    private final String value;

    OperationType(String v)
    {
        value=v;
    }

    public String value() {
        return value;
    }

    public String operatorSymbol()
    {
        if (value().equals(ADDITION.value))
            return "+";
        else if (value().equals(SUBTRACTION.value))
            return "-";
        else if (value().equals(MULTIPLICATION.value))
            return "*";
        else if (value().equals(DIVISION.value()))
            return "/";
        else if (value().equals(POWER.value))
            return "pow";
        else if (value().equals(SQUAREROOT.value))
            return "sqrt";
        else if (value().equals(EXPONENTIAL.value))
            return "exp";
        else if (value().equals(LOGARITHM.value))
            return "logarithm";
        return value;
    }
    public static OperationType getOperationTypeWithSymbol(String str)
    {
        if (str.equals("+")) return ADDITION;
        else if (str.equals("-")) return SUBTRACTION;
        else if (str.equals("*")) return MULTIPLICATION;
        else if (str.equals("/")) return DIVISION;
        else if (str.equals("pow")) return POWER;
        else if (str.equals("sqrt")) return SQUAREROOT;
        else if (str.equals("exp")) return EXPONENTIAL;
        else if (str.equals("logarithm")) return LOGARITHM;

        return null;
    }

    public String toString()
    {
        if (value().equals(ADDITION.value))
            return " + ";
        else if (value().equals(SUBTRACTION.value))
            return " - ";
        else if (value().equals(MULTIPLICATION.value))
            return " * ";
        else if (value().equals(DIVISION.value()))
            return " / ";
        else if (value().equals(POWER.value))
            return "^";
        return value();
    }
}
