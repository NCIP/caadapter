/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/*




* <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.hl7.v2v3.test;

import gov.nih.nci.caadapter.hl7.transformation.TransformationService;
import gov.nih.nci.caadapter.hl7.transformation.data.XMLElement;

import java.util.List;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.3 $
 *          date        Mar 6, 2008
 *          Time:       12:39:01 PM $
 */
public class TestTransformationServiceCSV_V3
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = ": TestTransformationServiceCSV_V3.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = ": /share/content/cvsroot/hl7sdk/src/gov/nih/nci/hl7/common/standard/impl/TestTransformationServiceCSV_V3.java,v 1.00 Mar 6, 2008 12:39:01 PM umkis Exp $";

    public TestTransformationServiceCSV_V3(String csv, String map)
    {
        TransformationService ts = new TransformationService(csv, map);
        List<XMLElement> msgs = null;
        try
        {
            msgs = ts.process();
        }
        catch(Exception ee)
        {
            System.out.println("Exception : " + ee.getMessage());
            ee.printStackTrace();
            return;
        }
        int n = 0;
        for(XMLElement msg:msgs)
        {
            n++;
            System.out.println("#### MESSAGE " + n + " #####");
            System.out.println(msg.toXML().toString());
        }
        System.out.println("\n#### Validation Results #####");
        System.out.println(ts.getValidatorResults().toString());

    }

    public static void main(String[] args)
    {
        String csv = "C:\\projects\\caadapter\\workingspace\\CDA\\POCD_MT000030.csv";
        String map = "C:\\projects\\caadapter\\workingspace\\CDA\\POCD_MT000030.map";
        new TestTransformationServiceCSV_V3(map, csv);
    }
}

/**
 * HISTORY      : : TestTransformationServiceCSV_V3.java,v $
 */
