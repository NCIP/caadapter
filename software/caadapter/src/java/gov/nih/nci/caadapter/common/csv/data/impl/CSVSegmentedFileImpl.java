/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
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
 * @version    $Revision: 1.3 $
 * @date        $Date: 2008-09-24 20:00:11 $
 */

public class CSVSegmentedFileImpl implements CSVSegmentedFile{
    private static final String LOGID = "$RCSfile: CSVSegmentedFileImpl.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/data/impl/CSVSegmentedFileImpl.java,v 1.3 2008-09-24 20:00:11 phadkes Exp $";

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
/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
