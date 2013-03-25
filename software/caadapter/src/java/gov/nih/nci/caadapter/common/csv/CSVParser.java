/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.common.csv;

import java.io.IOException;

/**
 * An interface that provides access to CSV files.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.3 $
 * @date        $Date: 2008-09-24 20:52:37 $
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

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
