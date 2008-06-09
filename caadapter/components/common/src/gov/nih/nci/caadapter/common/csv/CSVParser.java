/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.common.csv;

import java.io.IOException;

/**
 * An interface that provides access to CSV files.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.2 $
 * @date        $Date: 2008-06-09 19:53:49 $
 */

public interface CSVParser {

    /**
     * Open a CSV file and retrieve the data for a the specified row and column.
     *
     * @param file
     * @param row
     * @param column
     * @return value
     */
    public String fetch(String file, int row, int column)throws IOException;

    /**
     * Open a CSV file and retrieve the data for a the specified row and column.
     *
     * @param file
     * @param segmentName
     * @param column
     * @return value
     */
    public String fetch(String file, String segmentName, int column)throws IOException;

    public String[][] fetch(String file)throws IOException;

    public int countRows(String file)throws IOException;

}
