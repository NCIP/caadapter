/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/*




* <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.hl7.transformation.data;

import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.hl7.transformation.TransformationService;

import java.io.File;
import java.util.Calendar;
import java.util.List;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.8 $
 *          date        Jun 2, 2008
 *          Time:       10:53:35 PM $
 */
public class PerformanceTestHL7Generating
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = ": PerformanceTestHL7Generating.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = ": /share/content/cvsroot/hl7sdk/src/gov/nih/nci/hl7/common/standard/impl/PerformanceTestHL7Generating.java,v 1.00 Jun 2, 2008 10:53:35 PM umkis Exp $";

    public PerformanceTestHL7Generating()
    {
        String path = "";
        String path1 = "dist\\";
        //
        String path0 = "C:\\projects\\caadapter\\"+path1+"workingspace\\PerformanceTest\\";

        String comm = "java -cp ./lib/caAdapter.jar;./lib/caadapterCommon.jar;./lib/castor-0.9.9.jar;./lib/client.jar;./lib/knuHL7V2tree.jar;./lib/commons-collections-3.2.jar;./lib/commons-logging-1.0.4.jar;./lib/dom4j-1.4.jar;./lib/jaxen-jdom.jar;./lib/jdom.jar;./lib/log4j-1.2.8.jar;./lib/poi-2.5.1-final-20040804.jar;./lib/resource.zip;./lib/saxon8.jar;./lib/sdk-codegen.jar;./lib/spring.jar;./lib/xercesImpl.jar;./lib/xmi.in.out.jar;./lib/xml-apis.jar gov.nih.nci.caadapter.hl7.transformation.data.PerformanceTestHL7Generating ";
        String third = " N";

        path = path0 + "150000\\";
        System.out.println(comm + path + "150000_basic.csv " +path + "150000_basic.map" + third);
        System.out.println(comm + path + "150000_ten.csv " +path + "150000_basic.map" + third);
        System.out.println(comm + path + "150000_hundred.csv " +path + "150000_basic.map" + third);
        System.out.println(comm + path + "150000_thousand.csv " +path + "150000_basic.map" + third);

        path = path0 + "404001\\";
        System.out.println(comm + path + "404001_basic.csv " +path + "404001_basic.map" + third);
        System.out.println(comm + path + "404001_basic_ten.csv " +path + "404001_basic.map" + third);
        System.out.println(comm + path + "404001_basic_hundred.csv " +path + "404001_basic.map" + third);
        System.out.println(comm + path + "404001_basic_thousand.csv " +path + "404001_basic.map" + third);

        path = path0 + "040011\\";
        System.out.println(comm + path + "040011_basic2.csv " +path + "040011_basic2.map" + third);
        System.out.println(comm + path + "040011_basic2_ten.csv " +path + "040011_basic2.map" + third);
        System.out.println(comm + path + "040011_basic2_hundred.csv " +path + "040011_basic2.map" + third);
        System.out.println(comm + path + "040011_basic2_thousand.csv " +path + "040011_basic2.map" + third);

        path = path0 + "404001\\";
        System.out.println(comm + path + "404001_basic_oneBulk_1M.csv " +path + "404001_basic.map" + third);
        System.out.println(comm + path + "404001_basic_oneBulk_2M.csv " +path + "404001_basic.map" + third);
        System.out.println(comm + path + "404001_basic_twoBulk_2M.csv " +path + "404001_basic.map" + third);
        System.out.println(comm + path + "404001_basic_oneBulk_3M.csv " +path + "404001_basic.map" + third);
        System.out.println(comm + path + "404001_basic_oneBulk_4M.csv " +path + "404001_basic.map" + third);
        System.out.println(comm + path + "404001_basic_oneBulk_5M.csv " +path + "404001_basic.map" + third);
        System.out.println(comm + path + "404001_basic_oneBulk_6M.csv " +path + "404001_basic.map" + third);
        System.out.println(comm + path + "404001_basic_oneBulk_7M.csv " +path + "404001_basic.map" + third);
        System.out.println(comm + path + "404001_basic_oneBulk_8M.csv " +path + "404001_basic.map" + third);



        System.out.println(comm + path + "404001_basic_multi_seg_3000.csv " +path + "404001_basic.map" + third);
        System.out.println(comm + path + "404001_basic_multi_seg_4000.csv " +path + "404001_basic.map" + third);
        System.out.println(comm + path + "404001_basic_multi_seg_5000.csv " +path + "404001_basic.map" + third);
        System.out.println(comm + path + "404001_basic_multi_seg_6000.csv " +path + "404001_basic.map" + third);
        System.out.println(comm + path + "404001_basic_multi_seg_7000.csv " +path + "404001_basic.map" + third);
        System.out.println(comm + path + "404001_basic_multi_seg_8000.csv " +path + "404001_basic.map" + third);



        path = path0 + "150000\\";
//        processUnit(path + "150000_basic.csv", path + "150000_basic.map");
//        processUnit(path + "150000_ten.csv", path + "150000_basic.map");
//        processUnit(path + "150000_hundred.csv", path + "150000_basic.map");
//        processUnit(path + "150000_thousand.csv", path + "150000_basic.map");
//
        path = path0 + "404001\\";
//        processUnit(path + "404001_basic.csv", path + "404001_basic.map");
//        processUnit(path + "404001_basic_ten.csv", path + "404001_basic.map");
//        processUnit(path + "404001_basic_hundred.csv", path + "404001_basic.map");
//        processUnit(path + "404001_basic_thousand.csv", path + "404001_basic.map");

        path = path0 + "040011\\";
//        processUnit(path + "040011_basic2.csv", path + "040011_basic2.map");
//        processUnit(path + "040011_basic2_ten.csv", path + "040011_basic2.map");
//        processUnit(path + "040011_basic2_hundred.csv", path + "040011_basic2.map");
//        processUnit(path + "040011_basic2_thousand.csv", path + "040011_basic2.map");

        path = path0 + "404001\\";
//        processUnit(path + "404001_basic_oneBulk_1M.csv", path + "404001_basic.map");
//        processUnit(path + "404001_basic_oneBulk_2M.csv", path + "404001_basic.map");
//        processUnit(path + "404001_basic_twoBulk_2M.csv", path + "404001_basic.map");
//        processUnit(path + "404001_basic_oneBulk_3M.csv", path + "404001_basic.map");
//        processUnit(path + "404001_basic_oneBulk_4M.csv", path + "404001_basic.map");
//        processUnit(path + "404001_basic_oneBulk_5M.csv", path + "404001_basic.map");
//        processUnit(path + "404001_basic_oneBulk_6M.csv", path + "404001_basic.map");
//        processUnit(path + "404001_basic_oneBulk_7M.csv", path + "404001_basic.map");
//        processUnit(path + "404001_basic_oneBulk_8M.csv", path + "404001_basic.map");



//        processUnit(path + "404001_basic_multi_seg_3000.csv", path + "404001_basic.map");
//        processUnit(path + "404001_basic_multi_seg_4000.csv", path + "404001_basic.map");
//        processUnit(path + "404001_basic_multi_seg_5000.csv", path + "404001_basic.map");
        processUnit(path + "404001_basic_multi_seg_6000.csv", path + "404001_basic.map");
//        processUnit(path + "404001_basic_multi_seg_7000.csv", path + "404001_basic.map");
        //processUnit(path + "404001_basic_thousand.csv", path + "404001_basic.map");
    }
    public PerformanceTestHL7Generating(boolean ss, String csv, String map) //throws IOException
    {
        processUnit2(ss, csv, map);
    }
    public PerformanceTestHL7Generating(String csv, String map) //throws IOException
    {
        processUnit(csv, map);
    }
    public PerformanceTestHL7Generating(String cc, String csv, String map) //throws IOException
    {
        processUnit2(csv, map);
    }

    public void processUnit(String csv, String map) //throws IOException
    {
        System.out.println("BAT===========================================================================");
        System.out.println(" Source csv : " + csv);
        System.out.println(" Source map : " + map);
        java.util.Date dat = new java.util.Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dat);
        long dd1 = cal.getTimeInMillis();
        TransformationService serv = new TransformationService(map, csv);
        serv.setOutputFile(new File(csv.replace(".", "_") + ".zip"));

        int res1 = 0;
        try
        {
            res1 = serv.batchProcess();
            //res = serv.process();
        }
        catch(Exception ee)
        {
            System.out.println("*******************************\n  Exception : " + ee.getMessage());
            //ee.printStackTrace();
            displayStackTrace(ee.getStackTrace());
            return;
            //throw new IOException("Exception : " + ee.getMessage());
        }
        catch(java.lang.OutOfMemoryError er)
        {
            System.out.println("*******************************\n OutOfMemoryError : " + er.getMessage());
            //er.printStackTrace();
            displayStackTrace(er.getStackTrace());
            return;
            //throw new IOException("OutOfMemoryError : " + er.getMessage());
        }
        if (res1 == 0)
        {
            System.out.println("*******************************\n  NO RESULT ");
            return;
        }
        java.util.Date dat1 = new java.util.Date();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(dat1);
        long dd2 = cal1.getTimeInMillis();
        int tm = (int)(dd2 - dd1);
        double dl = (double)tm / 1000.0;

        System.out.println("   time : " + dl);
        System.out.println("   Generated message(s) : " + res1);
//        int n = 0;
//        for(XMLElement ele:res)
//        {
//            n++;
//            ValidatorResults val = ele.getValidatorResults();
//            StringBuffer msg = ele.toXML();
//            if (ss) System.out.println("  ---- Validate Result ("+n+") ---------------------\n" + val.toString());
//
//            if (ss) System.out.println("  ---- HL7 message ("+n+") ---------------------\n" + msg.toString());
//        }
    }
    public void processUnit2(String csv, String map) //throws IOException
    {
        processUnit2(false, csv, map);
    }
    public void processUnit2(boolean ss, String csv, String map) //throws IOException
    {
        System.out.println("OLD===========================================================================");
        System.out.println(" Source csv : " + csv);
        System.out.println(" Source map : " + map);
        java.util.Date dat = new java.util.Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dat);
        long dd1 = cal.getTimeInMillis();
        TransformationService serv = new TransformationService(map, csv);

        List<XMLElement> res = null;
        try
        {
            //int res1 = serv.batchProcess();
            res = serv.process();
        }
        catch(Exception ee)
        {
            System.out.println("*******************************\n  Exception : " + ee.getMessage());
            //ee.printStackTrace();
            displayStackTrace(ee.getStackTrace());
            return;
            //throw new IOException("Exception : " + ee.getMessage());
        }
        catch(java.lang.OutOfMemoryError er)
        {
            System.out.println("*******************************\n OutOfMemoryError (1) : " + er.getMessage());
            displayStackTrace(er.getStackTrace());
            //er.printStackTrace();
            return;
            //throw new IOException("OutOfMemoryError : " + er.getMessage());
        }
        java.util.Date dat1 = new java.util.Date();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(dat1);
        long dd2 = cal1.getTimeInMillis();
        int tm = (int)(dd2 - dd1);
        double dl = (double)tm / 1000.0;

        System.out.println("   time : " + dl);
        System.out.println("   Generated message(s) : " + res.size());
        try
        {
            int n = 0;
            for(XMLElement ele:res)
            {
                n++;
                ValidatorResults val = ele.getValidatorResults();
                StringBuffer msg = ele.toXML();
                if (ss) System.out.println("  ---- Validate Result ("+n+") ---------------------\n" + val.toString());

                if (ss) System.out.println("  ---- HL7 message ("+n+") ---------------------\n" + msg.toString());
            }
        }
        catch(java.lang.OutOfMemoryError er)
        {
            System.out.println("*******************************\n OutOfMemoryError (2) : " + er.getMessage());
            //er.printStackTrace();
            displayStackTrace(er.getStackTrace());
            return;
            //throw new IOException("OutOfMemoryError : " + er.getMessage());
        }
    }
    private void displayStackTrace(StackTraceElement[] eles)
    {
        if (eles == null)
        {
            System.out.println("     **Null StackTrace");
            return;
        }
        for (StackTraceElement ele:eles)
        {
            System.out.println("     " + ele.toString());
        }
    }
    public static void main(String[] args)
    {
        if (args.length == 0) new PerformanceTestHL7Generating();
        else if (args.length == 2) new PerformanceTestHL7Generating(args[0], args[1]);
        else if (args.length == 3)
        {
            if ((args[2].equalsIgnoreCase("Y"))||(args[2].equalsIgnoreCase("Yes")))
            {
                new PerformanceTestHL7Generating(true, args[0], args[1]);
            }
            else if ((args[2].equalsIgnoreCase("N"))||(args[2].equalsIgnoreCase("No")))
            {
                new PerformanceTestHL7Generating(false, args[0], args[1]);
            }
            else new PerformanceTestHL7Generating(args[2], args[0], args[1]);
        }
        else System.out.println("Invalid argument number : " + args.length);
    }
}

/**
 * HISTORY      : : PerformanceTestHL7Generating.java,v $
 */
