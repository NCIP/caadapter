/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/validation/MIFClassValidator.java,v 1.1 2007-07-03 18:23:11 wangeug Exp $
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
import gov.nih.nci.caadapter.common.validation.Validator;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;

import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;

import java.util.HashSet;
//import java.util.List;

/**
 * Validation for H3S Component. By given any CloneMeta as a root CloneMeta,
 * validate the current CloneMeta as well as its child clones. Support abstract data type validation,
 * choice validation
 *
 * @author OWNER: Eric Chen  Date: Aug 23, 2005
 * @author LAST UPDATE: $Author: wangeug $
 * @version $Revision: 1.1 $
 * @date $$Date: 2007-07-03 18:23:11 $
 * @since caAdapter v1.2
 */


public class MIFClassValidator extends Validator
{
	private boolean initialMessageRequired=false;
	
    public MIFClassValidator(MIFClass cloneMeta)
    {
        this(cloneMeta,false);
    }
    
    public MIFClassValidator(MIFClass cloneMeta, boolean initMsg)
    {
        super(cloneMeta);
        initialMessageRequired=initMsg;
    }

    public ValidatorResults validate()
    {
        ValidatorResults validatorResults = new ValidatorResults();
        if (toBeValidatedObject == null)
        {
            Message msg = MessageResources.getMessage("GEN1", new Object[0]);
            validatorResults.addValidatorResult(new ValidatorResult(ValidatorResult.Level.ERROR, msg));
            Log.logError(this, msg.toString());
        }
        else
        {
        	MIFClass mifClass= (MIFClass) toBeValidatedObject;
            validatorResults.addValidatorResults(processMIFClass(mifClass));

            // information only by the end of validation
            if (initialMessageRequired)
            {
	            for (int i = 4; i < 7; i++)
	            {
	                Message message = MessageResources.getMessage("HSM" + i,
	                    new Object[]{});
	                ValidatorResult one = new ValidatorResult(ValidatorResult.Level.INFO, message);
	                validatorResults.addValidatorResult(one);
	            }
            }
        }

        return validatorResults;
    }

    private ValidatorResults processMIFClass(MIFClass mifClass)
    {
        ValidatorResults validatorResults = new ValidatorResults();
        //validate all its attributes
        validatorResults.addValidatorResults(new MIFAttributeValidator(mifClass).validate());
        
        //validate all choice association
        HashSet<MIFAssociation> assHash=mifClass.getAssociations();
        for (MIFAssociation assc:assHash)
        {
        	if (assc.getMinimumMultiplicity()!=0||assc.isOptionChosen())
        		validatorResults.addValidatorResults(new MIFAssociationValidator(assc).validate());
        }

        return validatorResults;
    }
}
