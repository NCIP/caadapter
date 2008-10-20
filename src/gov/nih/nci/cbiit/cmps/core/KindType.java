//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.10.09 at 10:34:00 AM EDT 
//


package gov.nih.nci.cbiit.cmps.core;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for kindType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="kindType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="xml"/>
 *     &lt;enumeration value="csv"/>
 *     &lt;enumeration value="core"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "kindType")
@XmlEnum
public enum KindType {

    @XmlEnumValue("xml")
    XML("xml"),
    @XmlEnumValue("csv")
    CSV("csv"),
    @XmlEnumValue("core")
    CORE("core");
    private final String value;

    KindType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static KindType fromValue(String v) {
        for (KindType c: KindType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
