/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common.csv.data.impl;

import gov.nih.nci.caadapter.common.csv.data.CSVSegment;
import gov.nih.nci.caadapter.common.csv.data.CSVSegmentedFile;

import java.util.ArrayList;

/**
 * Implementation of an in-memory segmented csv data file.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.2 $
 * @date        $Date: 2008-06-09 19:53:49 $
 */

public class CSVSegmentedFileImpl implements CSVSegmentedFile{
    private static final String LOGID = "$RCSfile: CSVSegmentedFileImpl.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/data/impl/CSVSegmentedFileImpl.java,v 1.2 2008-06-09 19:53:49 phadkes Exp $";

    ArrayList<CSVSegment> logicalRecords = new ArrayList<CSVSegment>();

    public ArrayList<CSVSegment> getLogicalRecords() {
        return logicalRecords;
    }

    public void setLogicalRecords(ArrayList<CSVSegment> logicalRecords) {
        this.logicalRecords = logicalRecords;
    }

    public void addLogicalRecord(CSVSegment segment){
        logicalRecords.add(segment);
    }
}
