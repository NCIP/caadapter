/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.hl7.validation;

import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;

import java.io.ByteArrayInputStream;
import java.io.File;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * The class will process the .map file an genearte HL7 v3 messages.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.6 $
 *          date        $Date: 2008-09-29 15:37:31 $
 */

public class HL7V3MessageValidator {
	private Validator validator = null;
    ValidatorResults theValidatorResults = new ValidatorResults();

	/**
	 * @param xsdSchema is XSD associated with the HL7 v3 message
	 */
	public HL7V3MessageValidator() {}
	public HL7V3MessageValidator(String xsdSchema) {
		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

		File schemaLocation = new File(xsdSchema);
		Schema schema=null;
		try {
			schema = factory.newSchema(schemaLocation);
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		validator = schema.newValidator();
	}

	/**
	 * @param xmlString is the HL7 v3 xml string to be validated
	 */
	public ValidatorResults validate(String xmlString) {

		if (validator == null) {
            Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"Error loading XSD for this Message!"});
            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
			return theValidatorResults;
		}
        // 4. Parse the document you want to check.
        ByteArrayInputStream domSource = new ByteArrayInputStream(xmlString.getBytes());
        Source source = new StreamSource(domSource);

        // 5. Check the document
        try {
            validator.validate(source);
            Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"The HL7 v3 message is valid against xsd file"});
            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
			return theValidatorResults;
        }
        catch (SAXException ex) {
            Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"Not Valid:" + ex.getMessage()});
            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
			return theValidatorResults;
        }
        catch (Exception ex) {
            Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"Unexpected Error:" + ex.getMessage()});
            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
			return theValidatorResults;
        }

	}

	/**
	 * @param xmlString is the HL7 v3 xml string to be validated
	 * @param xsdSchema is the XSD associated with the HL7 v3 message
	 */
	public ValidatorResults validate(String xmlString, String xsdSchema) {

		Validator validator = getValidator(xsdSchema);

		if (validator == null) {
            Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"Error loading XSD for this Message!..schema:"+xsdSchema});
            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
			return theValidatorResults;
		}
        // 4. Parse the document you want to check.
        ByteArrayInputStream domSource = new ByteArrayInputStream(xmlString.getBytes());
        Source source = new StreamSource(domSource);

        // 5. Check the document
        try {
            validator.validate(source);
            Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"The HL7 v3 message is valid against xsd file"});
            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
        }
        catch (SAXException ex) {
            Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"Not Valid:" + ex.getMessage()});
            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
			return theValidatorResults;
        }
        catch (Exception ex) {
            Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"Unexpected Error:" + ex.getMessage()});
            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
			return theValidatorResults;
        }
		return theValidatorResults;

	}
	private Validator getValidator(String xsdSchema) {
		if (validator == null) {
	        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
	        File schemaLocation = new File(xsdSchema);
	        Schema schema=null;
	        try {
	        	schema = factory.newSchema(schemaLocation);
	        }
	        catch (Exception ex) {
	            Message msg = MessageResources.getMessage("EMP_IN", new Object[]{"Loading Schema Error:" + ex.getMessage()});
	            theValidatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
	        	return null;
	        }
	        validator = schema.newValidator();
			return validator;
		}
		else {
			return validator;
		}
	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 */