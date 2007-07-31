/**
 * <!-- LICENSE_TEXT_START -->
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.validation;

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
 * @author LAST UPDATE $Author: wuye $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-07-31 14:02:07 $
 */

public class HL7V3MessageValidator {
	private Validator validator = null;
	
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
	public void validate(String xmlString) {

		if (validator == null) {
			System.out.println("No validator specified");
			return;
		}
        // 4. Parse the document you want to check.
        ByteArrayInputStream domSource = new ByteArrayInputStream(xmlString.getBytes());  
        Source source = new StreamSource(domSource);
        
        // 5. Check the document
        try {
            validator.validate(source);
            System.out.println("valid.");
        }
        catch (SAXException ex) {
            System.out.println("not valid because ");
            System.out.println(ex.getMessage());
        }  
        catch (Exception ex) {
        	System.out.println(ex.getMessage());
        }

	}

	/**
	 * @param xmlString is the HL7 v3 xml string to be validated
	 * @param xsdSchema is the XSD associated with the HL7 v3 message
	 */
	public void validate(String xmlString, String xsdSchema) {

		Validator validator = getValidator(xsdSchema);
        
		if (validator == null) {
			System.out.println("Not valid");
			return;
		}
        // 4. Parse the document you want to check.
        ByteArrayInputStream domSource = new ByteArrayInputStream(xmlString.getBytes());  
        Source source = new StreamSource(domSource);
        
        // 5. Check the document
        try {
            validator.validate(source);
            System.out.println("valid.");
        }
        catch (SAXException ex) {
            System.out.println("not valid because ");
            System.out.println(ex.getMessage());
        }  
        catch (Exception ex) {
        	System.out.println(ex.getMessage());
        }

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
	        	System.out.println(ex.getMessage());
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
