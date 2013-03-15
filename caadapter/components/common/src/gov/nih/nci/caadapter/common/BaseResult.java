/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common;

import gov.nih.nci.caadapter.common.validation.ValidatorResults;

/**
 * A base class that contains validation resuts (errors, warning, etc.) after
 * a particular action takes place.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @version $Revision: 1.2 $
 * @since caAdapter v1.2
 */

public abstract class BaseResult {
    private static final String LOGID = "$RCSfile: BaseResult.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/BaseResult.java,v 1.2 2008-06-09 19:53:49 phadkes Exp $";

    protected ValidatorResults validatorResults = null;

    public ValidatorResults getValidatorResults() {
        return validatorResults;
    }

    public void setValidatorResults(ValidatorResults validatorResults) {
        this.validatorResults = validatorResults;
    }
}
