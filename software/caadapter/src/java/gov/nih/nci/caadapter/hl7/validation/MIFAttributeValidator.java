/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.validation;

import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.validation.Validator;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Validate if the abstract data type has been specialized.
 *
 * @author OWNER: Eric Chen  Date: Aug 23, 2005
 * @author LAST UPDATE: $Author: wangeug $
 * @version $Revision: 1.5 $
 * @date $$Date: 2008-09-29 15:37:32 $
 * @since caAdapter v1.2
 */


public class MIFAttributeValidator extends Validator
{
    public MIFAttributeValidator(MIFAttribute mifAttribute)
    {
        super(mifAttribute);
    }

    /**
     * Only validate Clone Attribute level, not Child Clone
     * @return ValidatorResults
     */
    public ValidatorResults validate()
    {
        ValidatorResults results = new ValidatorResults();
        if (toBeValidatedObject == null)
        {
            Message msg = MessageResources.getMessage("GEN1", new Object[0]);
            results.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
        }
        else
        {
        	MIFAttribute attrToValidate=(MIFAttribute)toBeValidatedObject;
        	Datatype mifDatatype=attrToValidate.getDatatype();
			if (mifDatatype!=null&&mifDatatype.isAbstract())
			{
				results.addValidatorResults(validateAbstractTypeAttribute(attrToValidate));
				if (attrToValidate.getConcreteDatatype()!=null)
					mifDatatype=attrToValidate.getConcreteDatatype();
			}
			results.addValidatorResults(validateDatatypeType(mifDatatype));
        }
        return results;
    }

    private ValidatorResults validateDatatypeType(Datatype datatype)
    {
    	ValidatorResults results = new ValidatorResults();
    	if (datatype==null)
    		return results;

    	final Hashtable <String, Attribute>attrHash=datatype.getAttributes();
    	Enumeration<Attribute> attrElements=attrHash.elements();
    	while (attrElements.hasMoreElements())
    	{
    		Attribute oneAttr=(Attribute)attrElements.nextElement();
    		if (oneAttr.getDefaultValue()!=null)
    		{
            	Message message = MessageResources.getMessage("HSM6INFO",
                        new Object[]{oneAttr.getXmlPath(),
            			oneAttr.getDefaultValue()});
            	results.addValidatorResult(new ValidatorResult(ValidatorResult.Level.INFO, message));
       		}
    		//validate Abstract datatype
    		if (oneAttr.getReferenceDatatype()!=null)
    		{
    			if(oneAttr.getReferenceDatatype().isAbstract())
	    		{
	            	Message message = MessageResources.getMessage("HSM1",
	                        new Object[]{oneAttr.getXmlPath(),
	            			oneAttr.getType()});
	            	results.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, message));
	       		}
    			else if (!oneAttr.getType().equals(oneAttr.getReferenceDatatype().getName()))
    			{
	            	Message message = MessageResources.getMessage("HSM1INFO",
	                        new Object[]{oneAttr.getXmlPath(),
	            			oneAttr.getType(), oneAttr.getReferenceDatatype().getName()});
	            	results.addValidatorResult(new ValidatorResult(ValidatorResult.Level.INFO, message));

    			}
    			results.addValidatorResults(validateDatatypeType(oneAttr.getReferenceDatatype()));
    		}
    	}
    	return results;
    }

    private ValidatorResults validateAbstractTypeAttribute(MIFAttribute cloneAttributeMeta)
    {
        ValidatorResults results = new ValidatorResults();
        if (cloneAttributeMeta.getConcreteDatatype()==null)//.isAbstract() && GeneralUtilities.isBlank(cloneAttributeMeta.getSubClass()))
        {
            Message message = MessageResources.getMessage("HSM1",
                new Object[]{cloneAttributeMeta.getXmlPath(),cloneAttributeMeta.getType()});
            results.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, message));
        }
        else
        {
        	Message message = MessageResources.getMessage("HSM1INFO",
                    new Object[]{cloneAttributeMeta.getXmlPath(),
        			cloneAttributeMeta.getDatatype(),
        			cloneAttributeMeta.getConcreteDatatype()});
        		results.addValidatorResult( new ValidatorResult(ValidatorResult.Level.INFO, message));
        }

        if (cloneAttributeMeta.getMultiplicityIndex()>0)
        {
        	Message message = MessageResources.getMessage("HSM4INFO",
                    new Object[]{cloneAttributeMeta.getXmlPath(),
        			cloneAttributeMeta.getMultiplicityIndex()});
        	results.addValidatorResult(new ValidatorResult(ValidatorResult.Level.INFO, message));
        }
        return results;
    }
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 */
