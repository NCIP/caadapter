/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.common.meta;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the generated package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _VocabularyMappingData_QNAME = new QName("", "VocabularyMappingData");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: generated
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link VocabularyMappingData }
     *
     */
    public VocabularyMappingData createVocabularyMappingData() {
        return new VocabularyMappingData();
    }

    /**
     * Create an instance of {@link MappingResults }
     *
     */
    public MappingResults createMappingResults() {
        return new MappingResults();
    }

    /**
     * Create an instance of {@link MappingSource }
     *
     */
    public MappingSource createMappingSource() {
        return new MappingSource();
    }

    /**
     * Create an instance of {@link ReturnMessage }
     *
     */
    public ReturnMessage createReturnMessage() {
        return new ReturnMessage();
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link VocabularyMappingData }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "", name = "VocabularyMappingData")
    public JAXBElement<VocabularyMappingData> createVocabularyMappingData(VocabularyMappingData value) {
        return new JAXBElement<VocabularyMappingData>(_VocabularyMappingData_QNAME, VocabularyMappingData.class, null, value);
    }

}
