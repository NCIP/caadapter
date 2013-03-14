/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.core;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the gov.nih.nci.cbiit.cmts.core package. 
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

    private final static QName _FORMULA_QNAME = new QName("http://cdms.cbiit.nci.nih.gov/core", "formula");
    private final static QName _EXPRESSION_QNAME = new QName("http://cdms.cbiit.nci.nih.gov/core", "expression");
    
    
    /**
     * Create an instance of {@formula FormulaStore }
     * 
     */
    public FormulaStore createFormulaStore() {
        return new FormulaStore();
    }
    
 
    /**
     * Create an instance of {@formula FormulaMeta }
     * 
     */
    public FormulaMeta createFormulaMeta() {
        return new FormulaMeta();
    }
    
    /**
     * Create an instance of {@term TermMeta }
     * 
     */
    public TermMeta createTermMeta() {
        return new TermMeta();
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Mapping }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://cdms.cbiit.nci.nih.gov/core", name = "formula")
    public JAXBElement<FormulaMeta> createFormula(FormulaMeta value) {
        return new JAXBElement<FormulaMeta>(_FORMULA_QNAME, FormulaMeta.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FunctionMeta }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://cmts.cbiit.nci.nih.gov/core", name = "term")
    public JAXBElement<TermMeta> createTermMeta(TermMeta value) {
        return new JAXBElement<TermMeta>(_EXPRESSION_QNAME, TermMeta.class, null, value);
    }
}
