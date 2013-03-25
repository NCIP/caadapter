/*L
 * Copyright SAIC, SAIC-Frederick.
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
import gov.nih.nci.caadapter.hl7.validation.complement.ReorganizingForValidating;
import gov.nih.nci.caadapter.hl7.validation.complement.XSDValidationTree;
import gov.nih.nci.caadapter.hl7.v2v3.tools.XmlReorganizingTree;

import java.io.File;

/**
 * This class defines XML document validator
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: altturbo $
 * @version Since caAdapter v4.0
 * revision    $Revision: 1.7 $
 * date        $Date: 2009-10-08 01:23:07 $
 */
public class XMLValidator extends Validator
{
    private String xsd = null;
    private boolean reorganizing = false;

    private String tempReorganizedV3File = null;

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

    public XMLValidator(Object object, String xsd, boolean reorganizing)
    {
        super(object);
        this.xsd = xsd;
        this.reorganizing = reorganizing;
    }

    public String getXsd()
    {
        return xsd;
    }

    public void setXsd(String xsd)
    {
        this.xsd = xsd;
    }

    public boolean getReorganizing()
    {
        return reorganizing;
    }

    public void setReorganizing(boolean reorganizing)
    {
        this.reorganizing = reorganizing;
    }

    public String getTempReorganizedV3File()
    {
        return tempReorganizedV3File;
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
                    boolean validSAX = false;

                    if (getReorganizing())
                    {
                        String xmlFile = "";
                        if (validateXMLSchema.isValidPath((String) toBeValidatedObject)) xmlFile = (String) toBeValidatedObject;
                        else xmlFile = FileUtil.saveStringIntoTemporaryFile((String) toBeValidatedObject);

                        ReorganizingForValidating rfv = new ReorganizingForValidating(xmlFile, xsd, true);
                        String tempFile = FileUtil.getTemporaryFileName();

                        XmlReorganizingTree xmlTree = rfv.getXMLTree();

                        xmlTree.generatingXMLFile(tempFile);
                        validSAX = validateXMLSchema.isValidSAX(new File(tempFile), rfv.getActiveXSDFileName());

                        tempReorganizedV3File = tempFile;

                        (new File(tempFile)).deleteOnExit();

                    }
                    else
                    {
                        validSAX = validateXMLSchema.isValidSAX((String) toBeValidatedObject, xsd);
                    }

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

    public static void main(String[] args)
    {
        //String xml = "C:\\project\\caAdapter_NCI_CVS\\caadapter\\workingspace\\ddd\\0.xml";
        //String xsd = "C:\\project\\schemas\\multicacheschemas\\PORR_MT049006UV01.xsd";
        //String xml = "C:\\project\\caadapter2\\caadapter\\workingspace\\ddd\\res.xml";

        //String xml = "jar:file:///E:/testInstance/aa.zip!/i.xml";

        //String xsd = "file:/C:/project/caadapter/hl7_home/Normative_2009_ICSR/schemas.zip/multicacheschemas/PORR_IN049006UV.xsd";
        //String xsd = "file:/C:/project/caadapter/hl7_home/Normative_2009_ICSR/schemas/multicacheschemas/PORR_IN049006UV.xsd";
        //String xsd = "C:\\project\\caadapter\\hl7_home\\Normative_2009_ICSR\\schemas\\multicacheschemas\\PORR_IN049006UV.xsd";
        //String xsd = "C:\\project\\caadapter\\schemas\\multicacheschemas\\PORR_MT049006UV01.xsd";

        String xml = "E:\\testInstance\\i.xml";
        //String xsd = "jar:file:///C:/project/caadapter/hl7_home/Normative_2009_ICSR/schemas.zip!/multicacheschemas/PORR_IN049006UV.xsd";
        String xsd = "C:\\project\\caadapter\\hl7_home\\Normative_2009_ICSR\\schemas\\multicacheschemas\\PORR_IN049006UV.xsd";

        //String xml = "E:\\testInstance\\0.xml";
        //String xsd = "jar:file:///C:/project/caadapter/hl7_home/Normative_2009_ICSR/schemas.zip!/multicacheschemas/PORR_MT049006UV.xsd";


        XMLValidator v = new XMLValidator(xml, xsd, true);
        //XMLValidator v = new XMLValidator(xml, xsd);
        ValidatorResults res = v.validate();
        if (res.isValid()) System.out.println("*** RESULTS:\n" +res.toString());
        else System.out.println("*** ERROR(s):\n" +res.toString());
    }
  }
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.6  2009/02/10 05:13:55  umkis
 * HISTORY :Include schema validation against xsd files when V3 message generating.
 * HISTORY :
 * HISTORY :Revision 1.5  2009/02/06 18:26:05  umkis
 * HISTORY :upgrade v3 message validating by referring xsd file
 * HISTORY :
 * HISTORY :Revision 1.4  2008/09/29 15:37:31  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */