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


/**
 * <p>Java class for mappingSource complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="mappingSource">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="ip" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="domainName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="sourceValue" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="context" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="inverse" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mappingSource")
public class MappingSource {

    @XmlAttribute(name = "ip")
    protected String ip;
    @XmlAttribute(name = "domainName")
    protected String domainName;
    @XmlAttribute(name = "sourceValue")
    protected String sourceValue;
    @XmlAttribute(name = "context")
    protected String context;
    @XmlAttribute(name = "inverse")
    protected Boolean inverse;

    /**
     * Gets the value of the ip property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIp() {
        return ip;
    }

    /**
     * Sets the value of the ip property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIp(String value) {
        this.ip = value;
    }

    /**
     * Gets the value of the domainName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDomainName() {
        return domainName;
    }

    /**
     * Sets the value of the domainName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDomainName(String value) {
        this.domainName = value;
    }

    /**
     * Gets the value of the sourceValue property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSourceValue() {
        return sourceValue;
    }

    /**
     * Sets the value of the sourceValue property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSourceValue(String value) {
        this.sourceValue = value;
    }

    /**
     * Gets the value of the context property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getContext() {
        return context;
    }

    /**
     * Sets the value of the context property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setContext(String value) {
        this.context = value;
    }

    /**
     * Gets the value of the inverse property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isInverse() {
        return inverse;
    }

    /**
     * Sets the value of the inverse property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setInverse(Boolean value) {
        this.inverse = value;
    }

}
