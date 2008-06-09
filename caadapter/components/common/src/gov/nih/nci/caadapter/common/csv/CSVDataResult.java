/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
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
 * @version $Revision: 1.2 $
 * @date $Date: 2008-06-09 19:53:49 $
 * @since caAdapter v1.2
 */

public class CSVDataResult extends BaseResult{
    private static final String LOGID = "$RCSfile: CSVDataResult.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/CSVDataResult.java,v 1.2 2008-06-09 19:53:49 phadkes Exp $";

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
