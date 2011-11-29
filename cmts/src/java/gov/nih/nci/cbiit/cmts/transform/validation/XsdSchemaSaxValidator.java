package gov.nih.nci.cbiit.cmts.transform.validation;

/**
 * This class validate an XML against using XQuery
 * 
 * @author Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @since CMTS v1.0
 * @version $Revision: 1.0 $
 * @date $Date: 2010-10-14 19:01:17 $
 * 
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringReader;

import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Schema;
import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.validation.Validator;
public class XsdSchemaSaxValidator {
	
  public static void validateXmlSource(Schema schema,SAXSource saxSource, ErrorHandler errorHandler)
  {
	    try {
	        // creating a Validator instance
	        Validator validator = schema.newValidator();
	        System.out.println();
	        System.out.println("Validator Class: "
	          + validator.getClass().getName());
	        validator.setErrorHandler(errorHandler);     

	        // validating the SAX source against the schema
	        validator.validate(saxSource);
	        System.out.println();
	        System.out.println("Validation passed.");

	      } catch (Exception e) {
	        // catching all validation exceptions
	        System.out.println();
	        System.out.println(e.toString());
	      }  
  }
  
  public static void validateXmlFile(Schema schema, String xmlName, ErrorHandler errorHandler) {
	  // preparing the XML file as a SAX source
      try {
		SAXSource source = new SAXSource(
		    new InputSource(new java.io.FileInputStream(xmlName)));
		validateXmlSource(schema, source, errorHandler);
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
  
  public static void validateXmlData(Schema schema, String xmlData, ErrorHandler errorHandler) {
//	  System.out.println("XsdSchemaSaxValidator.validateXml()..xml:"+xmlData);
      // preparing the XML file as a SAX source
   	  StringReader stReader=new StringReader(xmlData);
   	  InputSource inSrc =new InputSource(stReader);
   	  SAXSource source = new SAXSource(inSrc);
   	  validateXmlSource(schema, source, errorHandler);
 
   }
 
  public static Schema loadSchema(String name, ErrorHandler errorHandler) {
	  Schema schema = null;
      String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
      SchemaFactory factory = SchemaFactory.newInstance(language);
      factory.setErrorHandler(errorHandler);

      File file = null;
      String name2 = name;
      while(true)
      {
          file = new File(name2);
          if ((file.exists())&&(file.isFile())) break;
          if (name2.toLowerCase().startsWith("file:/"))
          {
              name2 = name2.substring(6);
              while(name2.startsWith("/")) name2 = name2.substring(1);
              continue;
          }
          return null;
      }

      try {
		schema = factory.newSchema(file);
	} catch (SAXException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    return schema;
  }
}

