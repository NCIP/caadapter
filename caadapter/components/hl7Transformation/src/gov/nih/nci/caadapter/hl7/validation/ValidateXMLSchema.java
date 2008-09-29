/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
 
package gov.nih.nci.caadapter.hl7.validation;


/**
 * This class defines XML schema validator 
 * 
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 * revision    $Revision: 1.4 $
 * date        $Date: 2008-09-29 15:37:32 $
 */

import java.io.StringReader;
import java.io.File;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;


public class ValidateXMLSchema
{
    //Constants when using XML Schema for SAX parsing.
    static final String JAXP_SCHEMA_LANGUAGE =
        "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    static final String W3C_XML_SCHEMA =
        "http://www.w3.org/2001/XMLSchema";
    static final String JAXP_SCHEMA_SOURCE =
        "http://java.sun.com/xml/jaxp/properties/schemaSource";

//    protected static final Logger LOGGER = Logger.getLogger("gov.nih.nci.caadapter.hl7.validation");

    private StringBuffer errors = new StringBuffer();;

    public String getErrors()
    {
        return errors.toString();
    }

    /**
     * If the exception throws, the source is not valid against the schema
     */
    public boolean isValidSAX(String source, String schema) throws Exception
    {
        StringReader sr = null;
        InputSource is = null;

        try
        {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            spf.setValidating(true);
            SAXParser sp = spf.newSAXParser();
            sp.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            sp.setProperty(JAXP_SCHEMA_SOURCE, schema);
            ValidateXMLSchemaHandler dh = new ValidateXMLSchemaHandler(errors);

            //Check the source path to see if the input is a fill path or an XML string
            //If it is a XML file send it directly to the XML parser, else validate an
            //XML snippet against the XMLSchema.

            if (isValidPath(source))
            {
                sp.parse(source, dh);
            }
            else
            {
                //System.err.println("###"+source+"###");

                sr = new StringReader(source);
                is = new InputSource(sr);
                sp.parse(is, dh);
            }
            return (dh.isValid()) ? true : false;  //If the default handler is valid, return true.  Otherwise, return false.

        }

            //Catch the exceptions
            //TODO Add better Error Handling.
        catch (SAXNotRecognizedException snr)
        {
//            snr.printStackTrace();
            throw snr;
        }
        catch (SAXException se)
        {
//            se.printStackTrace();
            throw se;
        }
        catch (Exception e)
        {
//            e.printStackTrace();
            throw e;
        }
//   return false;
    }

    /**
     * Check the passing parameter strpath is xml file name or xml string
     * @param strpath
     * @return
     */
    public static boolean isValidPath(String strpath)
    {
        if (strpath.indexOf(File.separator) > 0)
        {
            return true; //This is a valid path
        }
        else
        {
            return false; //This is not a valid path
        }
    }

    /**
     * If the exception throws out, the source is not well formed
     * @param source
     * @return
     * @throws Exception
     */
    public boolean isWellFormedSAX(String source) throws Exception
    {
        StringReader sr = null;
        InputSource is = null;

        try
        {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            spf.setValidating(false);
            SAXParser sp = spf.newSAXParser();
            sp.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
//     sp.setProperty(JAXP_SCHEMA_SOURCE, schema);
            ValidateXMLSchemaHandler dh = new ValidateXMLSchemaHandler(errors);

            if (isValidPath(source))
            {
                sp.parse(source, dh);
            }
            else
            {
                sr = new StringReader(source);
                is = new InputSource(sr);
                sp.parse(is, dh);
            }
            return (dh.isValid()) ? true : false;
        }
        catch (SAXNotRecognizedException snr)
        {
            throw snr;
        }
        catch (SAXException se)
        {
            throw se;
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public static void main(String args[]) throws Exception
    {
        new ValidateXMLSchema().isValidSAX(args[0], args[1]);
    }
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 */
