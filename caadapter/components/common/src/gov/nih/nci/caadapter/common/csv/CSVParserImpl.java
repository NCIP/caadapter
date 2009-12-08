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
 * An implementation of CSVParser.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.3 $
 * @date        $Date: 2008-09-24 20:52:37 $
 * @deprecated
 */
public class CSVParserImpl implements CSVParser {
    private static final String LOGID = "$RCSfile: CSVParserImpl.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/CSVParserImpl.java,v 1.3 2008-09-24 20:52:37 phadkes Exp $";

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

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
