/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common.csv;


import java.io.IOException;

/**
 * An implementation of CSVParser.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.2 $
 * @date        $Date: 2008-06-09 19:53:49 $
 * @deprecated
 */
public class CSVParserImpl implements CSVParser {
    private static final String LOGID = "$RCSfile: CSVParserImpl.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/CSVParserImpl.java,v 1.2 2008-06-09 19:53:49 phadkes Exp $";

    public String fetch(String file, int row, int column) throws IOException {
        String[][] csvData = null;
        String data = "";

        try {
            csvData = CsvCache.getCsv(file);
            data = csvData[row - 1][column - 1];
        } catch (java.lang.ArrayIndexOutOfBoundsException ae) {
            throw new IOException("Row # " + row + " and Column # " +
                column + " could not be found in " + file);
        }

        return data;
    }

    public String fetch(String file, String segmentName, int column) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String[][] fetch(String file) throws IOException {
        //String[][] csvData = null;
        String[][] csvData = CsvCache.getCsv(file);
        return csvData;
    }

    public int countRows(String file) throws IOException {
        String[][] data = fetch(file);
        return data.length;
    }

}
