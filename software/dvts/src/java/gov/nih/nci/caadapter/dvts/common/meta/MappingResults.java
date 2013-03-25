/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.common.meta;


import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for mappingResults complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="mappingResults">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Result" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="elsecaseApplied" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mappingResults", propOrder = {
    "result"
})
public class MappingResults {

    @XmlElement(name = "Result", required = true)
    protected List<String> result;
    @XmlAttribute(name = "elsecaseApplied")
    protected Boolean elsecaseApplied;

    /**
     * Gets the value of the result property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the result property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResult().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getResult() {
        if (result == null) {
            result = new ArrayList<String>();
        }
        return this.result;
    }

    /**
     * Gets the value of the elsecaseApplied property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isElsecaseApplied() {
        return elsecaseApplied;
    }

    /**
     * Sets the value of the elsecaseApplied property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setElsecaseApplied(Boolean value) {
        this.elsecaseApplied = value;
    }

}
