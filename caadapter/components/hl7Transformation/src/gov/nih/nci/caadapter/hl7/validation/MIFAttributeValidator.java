/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/validation/MIFAttributeValidator.java,v 1.1 2007-07-03 18:23:11 wangeug Exp $
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
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;

import java.util.HashSet;
//import java.util.List;

/**
 * Validate if the abstract data type has been specialized. 
 *
 * @author OWNER: Eric Chen  Date: Aug 23, 2005
 * @author LAST UPDATE: $Author: wangeug $
 * @version $Revision: 1.1 $
 * @date $$Date: 2007-07-03 18:23:11 $
 * @since caAdapter v1.2
 */


public class MIFAttributeValidator extends Validator
{
    public MIFAttributeValidator(MIFClass mifClass)
    {
        super(mifClass);
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
            MIFClass cloneMeta = (MIFClass)toBeValidatedObject;
            final HashSet<MIFAttribute> attributes = cloneMeta.getAttributes();
            for (MIFAttribute mifAttr :attributes)
            {
            	Datatype mifDatatype=mifAttr.getDatatype();
				if (mifDatatype!=null&&mifDatatype.isAbstract())
				{
					results.addValidatorResults(validateAbstractTypeAttribute(mifAttr));
				}
				results.addValidatorResults(validateDatatypeType(mifDatatype));
            }
        }
        return results;
    }

    private ValidatorResults validateDatatypeType(Datatype datatype)
    {
    	ValidatorResults results = new ValidatorResults();
    	
    	return results;
    }
    
    private ValidatorResults validateAbstractTypeAttribute(MIFAttribute cloneAttributeMeta)
    {
        ValidatorResults results = new ValidatorResults();
        if (cloneAttributeMeta.getConcreteDatatype()==null)//.isAbstract() && GeneralUtilities.isBlank(cloneAttributeMeta.getSubClass()))
        {
            Message message = MessageResources.getMessage("HSM1",
                new Object[]{cloneAttributeMeta.getXmlPath(),cloneAttributeMeta.getType()});
            ValidatorResult one = new ValidatorResult(ValidatorResult.Level.ERROR, message);
            results.addValidatorResult(one);
        }

//        List<CloneAttributeMeta> childAttributes = cloneAttributeMeta.getChildAttributes();
//        for (int j = 0; j < childAttributes.size(); j++)
//        {
//            CloneAttributeMeta attributeMeta =  childAttributes.get(j);
//            results.addValidatorResults(validateAttribute(attributeMeta));
//        }

        return results;
    }
}

