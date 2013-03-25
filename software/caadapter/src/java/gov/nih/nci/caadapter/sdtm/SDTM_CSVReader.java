/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.sdtm;

import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.2 revision
 *          $Revision: 1.3 $
 *          $Date: 2008-06-06 18:55:19 $
 */
public class SDTM_CSVReader {
    HashMap<String, String> _CSVData = new HashMap<String, String>();
    MultiMap _mhm = new MultiHashMap();

    public SDTM_CSVReader() {
    }

    // MultiMap mhm = new MultiHashMap();
    // mhm.put(key, "A");
    // mhm.put(key, "B");
    // mhm.put(key, "C");
    // Collection coll = (Collection) mhm.get(key);
    public MultiMap readCSVFile(String filename) throws Exception {
        BufferedReader inputTMP = new BufferedReader(new FileReader(filename));
        String lineTMP = null;
        EmptyStringTokenizer strTk = null;
        while ((lineTMP = inputTMP.readLine()) != null) {
            String columnName = "";
            ArrayList<String> dataForEachColumn = new ArrayList<String>();
            strTk = new EmptyStringTokenizer(lineTMP.toString(), ",");
            columnName = strTk.nextToken();
            StringBuffer _strBuf = new StringBuffer();
            while (strTk.hasMoreTokens()) {
                _strBuf.append(strTk.nextToken() + ",");
            }
            _mhm.put(columnName, _strBuf);
        }
        return _mhm;
    }

    public static void main(String[] args) throws Exception {
        SDTM_CSVReader s = new SDTM_CSVReader();
        s.readCSVFile("c:\\b.csv");
        System.out.println(" " + s._mhm.toString());
    }

    public HashMap<String, String> get_CSVData() {
        return _CSVData;
    }

    public MultiMap getCSVData() {
        return _mhm;
    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2007/08/16 19:04:58  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */
