/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/validation/XMLValidator.java,v 1.1 2007-07-03 18:23:11 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 *
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 *
 *
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 *
 *
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear.
 *
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software.
 *
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick.
 *
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.hl7.validation;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.validation.Validator;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;

import org.hl7.xml.validator.ValidateXMLSchema;
import java.util.logging.Logger;

/**
 * Perform XML validations.
 */
public class XMLValidator extends Validator
{
    protected static final Logger LOGGER = Logger.getLogger("gov.nih.nci.caadapter.hl7.validation");
    private String xsd = null;

    public XMLValidator(String xmlFilename)
    {
        super(xmlFilename);
    }

    public XMLValidator(Object object, String xsd)
    {
        super(object);
        this.xsd = xsd;
        //if (object instanceof String) checkHeadAndTail((String) object);
    }

    public String getXsd()
    {
        return xsd;
    }

    public void setXsd(String xsd)
    {
        this.xsd = xsd;
    }

    public ValidatorResults validate()
    {
        ValidatorResults result = new ValidatorResults();
        if (toBeValidatedObject instanceof String)
        {
            String xmlFileName = (String)toBeValidatedObject;
            if (!ValidateXMLSchema.isValidPath((String)toBeValidatedObject))
            {
                xmlFileName = "";
            }
            else    // inserted by umkis 08/10/2006  In case of temporary file, file name won't be appeared in spite of a valid file name.
            {
                if (FileUtil.isTemporaryFileName((String)toBeValidatedObject)) xmlFileName = "";
            }       // insert end

            try
            {
                ValidateXMLSchema validateXMLSchema = new ValidateXMLSchema();

                boolean wellFormedSAX = validateXMLSchema.isWellFormedSAX((String) toBeValidatedObject);
                if (!wellFormedSAX)
                {
                    Message msg = MessageResources.getMessage("XML1", new Object[]{xmlFileName, validateXMLSchema.getErrors()});
                    ValidatorResult one = new ValidatorResult(ValidatorResult.Level.FATAL, msg);
                    result.addValidatorResult(one);
                }
            }
            catch (Exception e)
            {
                Message msg = MessageResources.getMessage("XML1", new Object[]{xmlFileName, e.getMessage()});
                ValidatorResult one = new ValidatorResult(ValidatorResult.Level.FATAL, msg);
                result.addValidatorResult(one);
                Log.logException(this, e);
            }

            // If xml is not well formed, we will not proceed for the schema validation
            if (result.hasFatal())
                return result;

            if (xsd == null)
            {
                Message msg = MessageResources.getMessage("GEN1", new Object[]{"Parameter XSD is null."});
                ValidatorResult one = new ValidatorResult(ValidatorResult.Level.ERROR, msg);
                result.addValidatorResult(one);
            }
            else
            {
                try
                {
                    ValidateXMLSchema validateXMLSchema = new ValidateXMLSchema();
                    boolean validSAX = validateXMLSchema.isValidSAX((String) toBeValidatedObject, xsd);
                    if (validSAX)
                    {
                        Message msg = MessageResources.getMessage("XML3", new Object[]{xsd});
                        ValidatorResult one = new ValidatorResult(ValidatorResult.Level.INFO, msg);
                        result.addValidatorResult(one);
                    }
                    else
                    {
                        Message msg = MessageResources.getMessage("XML2",
                            new Object[]{xmlFileName, xsd, validateXMLSchema.getErrors()});
                        ValidatorResult one = new ValidatorResult(ValidatorResult.Level.ERROR, msg);
                        result.addValidatorResult(one);
                        System.out.println("validateXMLSchema.getErrors()" + validateXMLSchema.getErrors());
                    }

                }
                catch (Exception e)
                {
                    Message msg = MessageResources.getMessage("XML2",
                        new Object[]{xmlFileName, xsd, e.getMessage()});
                    ValidatorResult one = new ValidatorResult(ValidatorResult.Level.ERROR, msg);
                    result.addValidatorResult(one);
                    Log.logException(this, e);
                }

            }
        }
        return result;
    }

    /**
     * Is this XML document well formed?
     *
     * @return true if valid, false if not
     * @deprecated replaced by validate method
     */
    private boolean isWellFormed() throws Exception
    {
        return new ValidateXMLSchema().isWellFormedSAX((String) toBeValidatedObject);
    }

    /**
     * Is this XML document valid according to this schema?
     *
     * @param xml The XML Instance
     * @param xsd The Schema Definition
     * @return true if valid, false if not
     * @deprecated replaced by validate method
     */
    private boolean validAgainstSchema(String xml, String xsd)
    {   //todo: should be depreciated, using validate
        try
        {
            return new ValidateXMLSchema().isValidSAX(xml, xsd);
        }
        catch (Exception e)
        {
            Log.logException(this, e);
        }
        return false;
    }

    private void checkHeadAndTail(String xml)
    {
        String src = xml.trim();
        if (!src.startsWith("<?")) return;
        if (!src.endsWith(">")) return;
        src = src.substring(2);
        src = src.substring(src.indexOf("<"));
        int idx = 0;
        int idx1 = src.indexOf(" ");
        int idx2 = src.indexOf(">");
        if (idx1 < 0) return;
        if (idx2 < 0) return;
        if (idx1 < idx2) idx = idx1;
        else idx = idx2;
        String head = src.substring(1, idx);
        String tailString = "</" + head + ">";
        String tail = "";
        String achar = "";
        int i = src.length();
        while(true)
        {
            achar = src.substring(i-1, i);
            tail = achar + tail;
            if (achar.equals("<")) break;
            i--;
        }
        if (tail.equals(tailString)) return;
        src = xml.trim() + "\n" + tailString;
        super.setToBeValidatedObject(src);
        return;
    }
}


/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.20  2006/08/10 20:34:41  umkis
 * HISTORY      : In case of temporary file, file name won't be appeared in spite of a valid file name using FileUtil.isTemporaryFileName(String)
 * HISTORY      :
 * HISTORY      : Revision 1.19  2006/08/10 20:16:27  umkis
 * HISTORY      : small modify - remarks change
 * HISTORY      :
 * HISTORY      : Revision 1.18  2006/08/10 20:13:34  umkis
 * HISTORY      : small modify - remarks change
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/08/10 20:09:35  umkis
 * HISTORY      : In case of temporary file, file name won't be appeared in spite of valid file name.
 * HISTORY      :
 * HISTORY      : Revision 1.16  2006/08/02 18:44:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/05/16 16:16:44  umkis
 * HISTORY      : The inserted method 'checkHeadAndTail' is a temporary expedient to aviod from the fatal error caused by not matched head and tail.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/01/05 17:14:24  giordanm
 * HISTORY      : remove some hard coded values + move the metamigrator and metamerge over to the test directory.
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 18:56:26  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/29 15:39:07  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/21 19:35:34  giordanm
 * HISTORY      : more function rework / overhaul
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/15 19:50:43  chene
 * HISTORY      : Collect all schema validation error when parsing the xml
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/11/22 18:13:19  chene
 * HISTORY      : Validate XML Schema now throws SAXException contains column and row number related to this error.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/11/22 17:53:34  chene
 * HISTORY      : Validate XML Schema now throws SAXException contains column and row number related to this error.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/02 22:36:06  chene
 * HISTORY      : change "\\" to "/"
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/26 21:30:10  chene
 * HISTORY      : Clean up e.printStackTrace()
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/25 21:49:19  chene
 * HISTORY      : Kludge Fix to add a log at ValidatorResult constructor
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/10/25 20:20:25  chene
 * HISTORY      : Support Schema Location
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/10/21 20:19:59  chene
 * HISTORY      : Created XMLValidator
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/10/21 15:23:11  chene
 * HISTORY      : Clean up ValidateXML.
 * HISTORY      :
 * HISTORY      :
 */
