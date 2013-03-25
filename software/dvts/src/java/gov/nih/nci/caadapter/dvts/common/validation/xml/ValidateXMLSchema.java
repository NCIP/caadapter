/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.dvts.common.validation.xml;


/**
 * This class defines XML schema validator
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v4.0
 * revision    $Revision: 1.6 $
 * date        $Date: 2009-02-10 05:13:55 $
 */

import java.io.*;
import java.util.List;
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

//    protected static final Logger LOGGER = Logger.getLogger("gov.nih.nci.caadapter.dvts.common.validation.xml");

    private StringBuffer errors = new StringBuffer();;

    public String getErrors()
    {
        return errors.toString();
    }

    /**
     * If the exception throws, the source is not valid against the schema
     */
    public boolean isValidSAX(Object sourceObj, String schema) throws Exception
    {
        if (sourceObj == null) throw new Exception("NULL source XML instance");
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
            ValidateXMLSchemaHandler xmlSchemaHandler = new ValidateXMLSchemaHandler(errors);

            //Check the source path to see if the input is a fill path or an XML string
            //If it is a XML file send it directly to the XML parser, else validate an
            //XML snippet against the XMLSchema.
            if (sourceObj instanceof String)
            {
                String source = (String) sourceObj;
                if (isValidPath(source))
                {
                    sp.parse(source, xmlSchemaHandler);
                }
                else
                {
                    //System.err.println("###"+source+"###");

                    sr = new StringReader(source);
                    is = new InputSource(sr);

                    sp.parse(is, xmlSchemaHandler);
                }
            }
            else if (sourceObj instanceof File)
            {
                File source = (File) sourceObj;

                FileReader srF = new FileReader(source);
                is = new InputSource(srF);

                sp.parse(is, xmlSchemaHandler);
                srF.close();

            }
            else throw new Exception("Invalid source XML instance ('String' or 'File' only) : " + sourceObj.getClass().getCanonicalName());
            return (xmlSchemaHandler.isValid()) ? true : false;  //If the default handler is valid, return true.  Otherwise, return false.

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
        if (strpath.trim().length() > 4096) return false;

        File file = new File(strpath.trim());

        if (!file.exists()) return false;
        if (!file.isFile()) return false;

        return true;
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
 * HISTORY :Revision 1.5  2009/02/06 18:26:05  umkis
 * HISTORY :upgrade v3 message validating by referring xsd file
 * HISTORY :
 * HISTORY :Revision 1.4  2008/09/29 15:37:32  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */
