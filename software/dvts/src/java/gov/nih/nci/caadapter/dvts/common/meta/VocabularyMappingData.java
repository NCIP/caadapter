/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.common.meta;

import javax.xml.bind.annotation.*;


/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Oct 26, 2011
 * Time: 10:53:19 AM
 * To change this template use File | Settings | File Templates.
 */
 /**
 * <p>Java class for vocabularyMappingData complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="vocabularyMappingData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ReturnMessage" type="{}returnMessage"/>
 *         &lt;element name="MappingSource" type="{}mappingSource" minOccurs="0"/>
 *         &lt;element name="MappingResults" type="{}mappingResults" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "vocabularyMappingData", propOrder = {
    "returnMessage",
    "mappingSource",
    "mappingResults"
})
@XmlRootElement(name="VocabularyMappingData")
public class VocabularyMappingData {

    @XmlElement(name = "ReturnMessage", required = true)
    protected ReturnMessage returnMessage;
    @XmlElement(name = "MappingSource")
    protected MappingSource mappingSource;
    @XmlElement(name = "MappingResults")
    protected MappingResults mappingResults;

    /**
     * Gets the value of the returnMessage property.
     *
     * @return
     *     possible object is
     *     {@link ReturnMessage }
     *
     */
    public ReturnMessage getReturnMessage() {
        return returnMessage;
    }

    /**
     * Sets the value of the returnMessage property.
     *
     * @param value
     *     allowed object is
     *     {@link ReturnMessage }
     *
     */
    public void setReturnMessage(ReturnMessage value) {
        this.returnMessage = value;
    }

    /**
     * Gets the value of the mappingSource property.
     *
     * @return
     *     possible object is
     *     {@link MappingSource }
     *
     */
    public MappingSource getMappingSource() {
        return mappingSource;
    }

    /**
     * Sets the value of the mappingSource property.
     *
     * @param value
     *     allowed object is
     *     {@link MappingSource }
     *
     */
    public void setMappingSource(MappingSource value) {
        this.mappingSource = value;
    }

    /**
     * Gets the value of the mappingResults property.
     *
     * @return
     *     possible object is
     *     {@link MappingResults }
     *
     */
    public MappingResults getMappingResults() {
        return mappingResults;
    }

    /**
     * Sets the value of the mappingResults property.
     *
     * @param value
     *     allowed object is
     *     {@link MappingResults }
     *
     */
    public void setMappingResults(MappingResults value) {
        this.mappingResults = value;
    }

}
