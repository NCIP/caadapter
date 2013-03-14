/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.common.csv;

import gov.nih.nci.caadapter.common.BaseResult;
import gov.nih.nci.caadapter.common.csv.data.CSVSegmentedFile;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;

/**
 * Contains CSV data information and validation results.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @version $Revision: 1.3 $
 * @date $Date: 2008-09-24 20:52:37 $
 * @since caAdapter v1.2
 */

public class CSVDataResult extends BaseResult{
    private static final String LOGID = "$RCSfile: CSVDataResult.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/CSVDataResult.java,v 1.3 2008-09-24 20:52:37 phadkes Exp $";

    CSVSegmentedFile csvSegmentedFile = null;

    public CSVDataResult() {
    }

    public CSVDataResult(CSVSegmentedFile csvSegmentedFile, ValidatorResults validatorResults) {
        this.csvSegmentedFile = csvSegmentedFile;
        this.validatorResults = validatorResults;
    }

    public CSVSegmentedFile getCsvSegmentedFile() {
        return csvSegmentedFile;
    }

    public void setCsvSegmentedFile(CSVSegmentedFile csvSegmentedFile) {
        this.csvSegmentedFile = csvSegmentedFile;
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
