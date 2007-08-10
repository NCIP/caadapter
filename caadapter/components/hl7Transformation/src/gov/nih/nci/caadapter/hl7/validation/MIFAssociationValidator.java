/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/hl7Transformation/src/gov/nih/nci/caadapter/hl7/validation/MIFAssociationValidator.java,v 1.3 2007-08-10 16:47:29 wangeug Exp $
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

import java.util.TreeSet;
import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.MessageResources;
import gov.nih.nci.caadapter.common.validation.Validator;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;

/**
 * Validate if the choice is made on the choice box options, only validate at clone level, not the child clone level.
 *
 * @author OWNER: Eric Chen  Date: Aug 23, 2005
 * @author LAST UPDATE: $Author: wangeug $
 * @version $Revision: 1.3 $
 * @date $$Date: 2007-08-10 16:47:29 $
 * @since caAdapter v1.2
 */


public class MIFAssociationValidator extends Validator
{
    public MIFAssociationValidator(MIFAssociation choiceAssc)
    {
        super(choiceAssc);
    }

    /**
     * Only validate Clone level, not Child Clone
     *
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
        	MIFAssociation mifAssc = (MIFAssociation) toBeValidatedObject;
        	MIFClass asscMifClass=mifAssc.getMifClass();
        	if (asscMifClass.getChoices()!=null&&asscMifClass.getChoices().size()>0)
        		results.addValidatorResults(validateChoiceAssociation(mifAssc));
        	else
        		results.addValidatorResults(new MIFClassValidator(asscMifClass).validate());
        	
        	if (mifAssc.getMultiplicityIndex()>0)
            {
            	Message message = MessageResources.getMessage("HSM4INFO",
                        new Object[]{mifAssc.getXmlPath(),
            			mifAssc.getMultiplicityIndex()});
            	results.addValidatorResult(new ValidatorResult(ValidatorResult.Level.INFO, message));
            }
        }

        return results;
    }

	private ValidatorResults validateChoiceAssociation(MIFAssociation choiceAssc)
	{
		ValidatorResults results = new ValidatorResults();

		int chosenCnt=0;
		TreeSet<MIFClass> choiceClasses=choiceAssc.getMifClass().getSortedChoices();
    	for (MIFClass chosenClass:choiceClasses)
    	{
    		if (chosenClass.isChoiceSelected())
    		{

    			Message message = MessageResources.getMessage("HSM3",
                        new Object[]{chosenClass.getXmlPath()});
                ValidatorResult one = new ValidatorResult(ValidatorResult.Level.INFO, message);
                results.addValidatorResult(one);

    			results.addValidatorResults(new MIFClassValidator(chosenClass).validate());
    			chosenCnt++;
    		}
    	}
		if (chosenCnt==0)
        {
            Message message = MessageResources.getMessage("HSM2",
                new Object[]{choiceAssc.getXmlPath()});
            ValidatorResult one = new ValidatorResult(ValidatorResult.Level.ERROR, message);
            results.addValidatorResult(one);
        }   	
    	return results;
	}
}

