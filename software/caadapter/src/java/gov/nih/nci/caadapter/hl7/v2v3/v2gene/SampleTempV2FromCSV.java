/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.v2v3.v2gene;

import gov.nih.nci.caadapter.common.util.FileUtil;

import java.util.List;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.5 $
 *          date        Jan 15, 2008
 *          Time:       1:03:44 PM $
 */
public class SampleTempV2FromCSV
{
    public static void main(String[] args)
    {
        TempV2FromCSV gen = null;
        try
        {
            // argument 1 : input csv file name
            // argument 2 : output directory (full pathname)
            gen = new TempV2FromCSV("C:\\baylorhealth\\Sample input file Test 1.csv", "C:\\baylorhealth\\test");
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            System.exit(0);
        }

        // At this point the v2 message file may be generated at the output directory.
        // The all messages are saved to the output file of which file name can be gotten by getOutputFileName().
        // "Message Control-ID" consists of "VISION" + Generating Time(yyyymmddhhMMss) + "_" + 6 digit record_number in the input csv file.


        String outFile = gen.getOutputFileName();       // get the output v2 message file name
        String logFile = gen.getLogFileName();          // get the log file name for each csv record.
        String errorFile = gen.getErrorListFileName();  // get the error message list file name for each failure csv record.
        int successCount = gen.getSuccessCount();       // get the number of the records generated successfully
        int errorCount = gen.getErrorCount();           // get the number of the error records
        int recordCount = gen.getRecordCount();         // get the number of the total records in the csv file


        System.out.println("\n\n--------------------------\nMessagr File ("+outFile+")\n\n" + FileUtil.readFileIntoString(outFile));
        System.out.println("\n\n--------------------------\nLog File ("+logFile+")\n\n" + FileUtil.readFileIntoString(logFile));
        System.out.println("\n\n--------------------------\nError File ("+errorFile+")\n\n" + FileUtil.readFileIntoString(errorFile));


    }

}

/**
 * HISTORY      : : SampleTempV2FromCSV.java,v $
 */
