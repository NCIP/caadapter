/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.core;

import javax.xml.bind.annotation.XmlEnumValue;

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


	@Override
    public String toString()
    {
        if (value().equals(ADDITION.value))
            return " + ";
        else if (value().equals(SUBTRACTION.value))
            return " - ";
        else if (value().equals(MULTIPLICATION.value))
            return " \u00D7 ";
        else if (value().equals(DIVISION.value()))
        	 return " \u00F7 ";
        else if (value().equals(POWER.value))
            return "^";
        else if (value().equals(LOGARITHM.value))
        	return "log";
        return value();
    }
}
