/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.common.meta;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for returnMessage complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="returnMessage">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="errorLevel" use="required" type="{}errorLevel" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "returnMessage", propOrder = {
    "value"
})
public class ReturnMessage {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "errorLevel", required = true)
    protected ErrorLevel errorLevel;

    /**
     * Gets the value of the value property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the errorLevel property.
     *
     * @return
     *     possible object is
     *     {@link ErrorLevel }
     *
     */
    public ErrorLevel getErrorLevel() {
        return errorLevel;
    }

    /**
     * Sets the value of the errorLevel property.
     *
     * @param value
     *     allowed object is
     *     {@link ErrorLevel }
     *
     */
    public void setErrorLevel(ErrorLevel value) {
        this.errorLevel = value;
    }

}
