/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/validation/MIFAttributeValidator.java,v 1.2 2007-07-11 17:55:31 wangeug Exp $
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

import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
//import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.validation.Validator;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;

import java.util.Enumeration;
import java.util.HashSet;
//import java.util.List;
import java.util.Hashtable;

/**
 * Validate if the abstract data type has been specialized. 
 *
 * @author OWNER: Eric Chen  Date: Aug 23, 2005
 * @author LAST UPDATE: $Author: wangeug $
 * @version $Revision: 1.2 $
 * @date $$Date: 2007-07-11 17:55:31 $
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

