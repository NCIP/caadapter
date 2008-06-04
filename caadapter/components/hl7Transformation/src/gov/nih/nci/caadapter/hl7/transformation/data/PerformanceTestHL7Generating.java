/*
 *  : /share/content/cvsroot/hl7sdk/src/gov/nih/nci/hl7/common/standard/impl/PerformanceTestHL7Generating.java,v 1.00 Jun 2, 2008 10:53:35 PM umkis Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE  
 * ******************************************************************
 *
 *	The caAdapter Software License, Version 1.0
 *
 *	Copyright 2001 SAIC. This software was developed in conjunction with the National Cancer
 *	Institute, and so to the extent government employees are co-authors, any rights in such works
 *	shall be subject to Title 17 of the United States Code, section 105.
 *
 *	Redistribution and use in source and binary forms, with or without modification, are permitted
 *	provided that the following conditions are met:
 *
 *	1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *	and the disclaimer of Article 3, below.  Redistributions in binary form must reproduce the above
 *	copyright notice, this list of conditions and the following disclaimer in the documentation and/or
 *	other materials provided with the distribution.
 *
 *	2.  The end-user documentation included with the redistribution, if any, must include the
 *	following acknowledgment:
 *
 *	"This product includes software developed by the SAIC and the National Cancer
 *	Institute."
 *
 *	If no such end-user documentation is to be included, this acknowledgment shall appear in the
 *	software itself, wherever such third-party acknowledgments normally appear.
 *
 *	3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or
 *	promote products derived from this software.
 *
 *	4. This license does not authorize the incorporation of this software into any proprietary
 *	programs.  This license does not authorize the recipient to use any trademarks owned by either
 *	NCI or SAIC-Frederick.
 *
 *	5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *	WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *	MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE
 *	DISCLAIMED.  IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR
 *	THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *	EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *	PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *	PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 *	OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *	NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * ******************************************************************
 */

package gov.nih.nci.caadapter.hl7.transformation.data;

import gov.nih.nci.caadapter.hl7.transformation.TransformationService;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.1 $
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


        path = path0 + "150000\\";
        System.out.println(comm + path + "150000_basic.csv " +path + "150000_basic.map");
        System.out.println(comm + path + "150000_ten.csv " +path + "150000_basic.map");
        System.out.println(comm + path + "150000_hundred.csv " +path + "150000_basic.map");
        System.out.println(comm + path + "150000_thousand.csv " +path + "150000_basic.map");

        path = path0 + "404001\\";
        System.out.println(comm + path + "404001_basic.csv " +path + "404001_basic.map");
        System.out.println(comm + path + "404001_basic_ten.csv " +path + "404001_basic.map");
        System.out.println(comm + path + "404001_basic_hundred.csv " +path + "404001_basic.map");
        System.out.println(comm + path + "404001_basic_thousand.csv " +path + "404001_basic.map");

        path = path0 + "040011\\";
        System.out.println(comm + path + "040011_basic2.csv " +path + "040011_basic2.map");
        System.out.println(comm + path + "040011_basic2_ten.csv " +path + "040011_basic2.map");
        System.out.println(comm + path + "040011_basic2_hundred.csv " +path + "040011_basic2.map");
        System.out.println(comm + path + "040011_basic2_thousand.csv " +path + "040011_basic2.map");

        path = path0 + "404001\\";
        System.out.println(comm + path + "404001_basic_oneBulk_1M.csv " +path + "404001_basic.map");
        System.out.println(comm + path + "404001_basic_oneBulk_2M.csv " +path + "404001_basic.map");
        System.out.println(comm + path + "404001_basic_twoBulk_2M.csv " +path + "404001_basic.map");
        System.out.println(comm + path + "404001_basic_oneBulk_3M.csv " +path + "404001_basic.map");
        System.out.println(comm + path + "404001_basic_oneBulk_4M.csv " +path + "404001_basic.map");
        System.out.println(comm + path + "404001_basic_oneBulk_5M.csv " +path + "404001_basic.map");
        System.out.println(comm + path + "404001_basic_oneBulk_6M.csv " +path + "404001_basic.map");
        System.out.println(comm + path + "404001_basic_oneBulk_7M.csv " +path + "404001_basic.map");
        System.out.println(comm + path + "404001_basic_oneBulk_8M.csv " +path + "404001_basic.map");



        System.out.println(comm + path + "404001_basic_multi_seg_3000.csv " +path + "404001_basic.map");
        System.out.println(comm + path + "404001_basic_multi_seg_4000.csv " +path + "404001_basic.map");
        System.out.println(comm + path + "404001_basic_multi_seg_5000.csv " +path + "404001_basic.map");
        System.out.println(comm + path + "404001_basic_multi_seg_6000.csv " +path + "404001_basic.map");
        System.out.println(comm + path + "404001_basic_multi_seg_7000.csv " +path + "404001_basic.map");
        System.out.println(comm + path + "404001_basic_thousand.csv " +path + "404001_basic.map");
        


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
        processUnit(ss, csv, map);
    }
    public PerformanceTestHL7Generating(String csv, String map) //throws IOException
    {
        processUnit(csv, map);
    }
    public void processUnit(String csv, String map) //throws IOException
    {
        processUnit(false, csv, map);
    }
    public void processUnit(boolean ss, String csv, String map) //throws IOException
    {
        System.out.println("===========================================================================");
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
            int res = serv.batchProcess();
            res = serv.process();
        }
        catch(Exception ee)
        {
            System.out.println("*******************************\n  Exception : " + ee.getMessage());
            ee.printStackTrace();
            return;
            //throw new IOException("Exception : " + ee.getMessage());
        }
        catch(java.lang.OutOfMemoryError er)
        {
            System.out.println("*******************************\n OutOfMemoryError : " + er.getMessage());
            er.printStackTrace();
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
            else new PerformanceTestHL7Generating(args[0], args[1]);
        }
        else System.out.println("Invalid argument number : " + args.length);
    }
}

/**
 * HISTORY      : : PerformanceTestHL7Generating.java,v $
 */
