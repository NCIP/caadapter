/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.ui.mapping.V2V3;

import javax.swing.*;
import java.util.List;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.3 $
 *          date        Mar 31, 2008
 *          Time:       5:41:15 PM $
 */
public class SampleProgramForCSVConvertingFromV2
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = ": SampleProgramForCSVConvertingFromV2.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = ": /share/content/cvsroot/hl7sdk/src/gov/nih/nci/hl7/common/standard/impl/SampleProgramForCSVConvertingFromV2.java,v 1.00 Mar 31, 2008 5:41:15 PM umkis Exp $";


    public SampleProgramForCSVConvertingFromV2()
    {
        // Constructor
        // This program is for generating csv file from HL7 V2 file and scs file must be prepared.
        ConvertFromV2ToCSV con = new ConvertFromV2ToCSV("C:\\projects\\temp\\v2Meta" // v2 meta data directory or zip file, if null and there is the default resource zip file, system uses the default resources.
                                                        , "c:\\v2.msg"               // input v2 message file
                                                        , "ORU^R01"                  // message type
                                                        , "2.4"                      // target version
                                                        , "c:\\fff.csv"              // output csv file
                                                        , "c:\\fff.scs"              // scs file for the output csv file validation
                                                        , false                      // this value must be false
                                                        );
        // If input v2 file is well made, with only the constructor, output csv file will be generated.

        // APIs

        boolean result = con.isSuccessful();    // if true, output csv file is normally generated.
        String logFile = con.getLogFileName();  // Generated log file name. General format is {output csv file name} + '.log'.
        int errorLevel = con.getErrorLevel();   // Values are followings
                                                // JOptionPane.ERROR_MESSAGE :  output csv file was not generated.
                                                // JOptionPane.WARNING_MESSAGE :  normally generated, but some messages.
                                                // JOptionPane.INFORMATION_MESSAGE :  All the messages are successfully converted.
        String message = con.getMessage();      // This message explains why the errorLevel.
        List<String> errorMessages = con.getErrorMessages(); // this list includes the log file contents.
        int messageCount = con.getMessageCount();   // How many v2 messages are in the input file.
        int failCount = con.getFailCount();         // How many fail messages are among the v2 input messages.
        List<Integer> fails = con.getFailMessageSequenceList(); // The list of sequence number which faild in the input file.

        System.out.println("### result : " + result);
        System.out.println("### logFile : " + logFile);
        System.out.println("### errorLevel : " + errorLevel);
        if (errorLevel == JOptionPane.ERROR_MESSAGE) System.out.println(" ## This errorLevel : ERROR!! ");
        if (errorLevel == JOptionPane.WARNING_MESSAGE) System.out.println(" ## This errorLevel : WARNING!! ");
        if (errorLevel == JOptionPane.INFORMATION_MESSAGE) System.out.println(" ## This errorLevel : INFORMATION!! ");
        System.out.println("### message : " + message);
        for (String st:errorMessages) System.out.println("### errorMessages : " + st);
        System.out.println("### messageCount : " + messageCount);
        System.out.println("### failCount : " + failCount);
        for (int in:fails) System.out.println("### fails : " + in);


    }

    public static void main(String[] args)
    {
        new SampleProgramForCSVConvertingFromV2();
    }

}
/**
 * HISTORY      : : SampleProgramForCSVConvertingFromV2.java,v $
 */
