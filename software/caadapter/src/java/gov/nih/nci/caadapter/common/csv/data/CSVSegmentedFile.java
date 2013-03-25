/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.common.csv.data;

import java.util.List;

/**
 * Interface for an in-memory segmented csv data file.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.3 $
 * @date        $Date: 2008-09-24 20:36:54 $
 */

public interface CSVSegmentedFile {
    public List<CSVSegment> getLogicalRecords();
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
