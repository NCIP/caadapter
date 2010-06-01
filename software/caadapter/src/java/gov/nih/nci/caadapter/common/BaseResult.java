/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.common;

import gov.nih.nci.caadapter.common.validation.ValidatorResults;

/**
 * A base class that contains validation resuts (errors, warning, etc.) after
 * a particular action takes place.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @version $Revision: 1.3 $
 * @since caAdapter v1.2
 */

public abstract class BaseResult {
    private static final String LOGID = "$RCSfile: BaseResult.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/BaseResult.java,v 1.3 2008-09-24 19:51:48 phadkes Exp $";

    protected ValidatorResults validatorResults = null;

    public ValidatorResults getValidatorResults() {
        return validatorResults;
    }

    public void setValidatorResults(ValidatorResults validatorResults) {
        this.validatorResults = validatorResults;
    }
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
