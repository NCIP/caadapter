/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





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
