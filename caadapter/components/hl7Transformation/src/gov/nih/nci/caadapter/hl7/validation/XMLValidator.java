/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/validation/XMLValidator.java,v 1.2 2007-08-27 15:36:24 wangeug Exp $
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

/**
 * Perform XML validations.
 */
public class XMLValidator extends Validator
{
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
  }
