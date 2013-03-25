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
import gov.nih.nci.caadapter.common.validation.Validator;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;

import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFAssociation;

import java.util.HashSet;


/**
 * Validation for H3S Component. By given any CloneMeta as a root CloneMeta,
 * validate the current CloneMeta as well as its child clones. Support abstract data type validation,
 * choice validation
 *
 * @author OWNER: Eric Chen  Date: Aug 23, 2005
 * @author LAST UPDATE: $Author: phadkes $
 * @version $Revision: 1.3 $
 * @date $$Date: 2008-06-09 19:53:50 $
 * @since caAdapter v1.2
 */


public class MIFClassValidator extends Validator
{
	private boolean initialMessageRequired=false;

    public MIFClassValidator(MIFClass mifClass)
    {
        this(mifClass,false);
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
        final HashSet<MIFAttribute> attributes = mifClass.getAttributes();
        for (MIFAttribute mifAttr :attributes)
        	validatorResults.addValidatorResults(new MIFAttributeValidator(mifAttr).validate());

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
