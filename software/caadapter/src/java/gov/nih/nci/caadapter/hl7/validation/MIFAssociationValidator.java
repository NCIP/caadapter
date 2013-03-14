/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
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
 * @version $Revision: 1.5 $
 * @date $$Date: 2008-09-29 15:37:31 $
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

/**
 * HISTORY :$Log: not supported by cvs2svn $
 */