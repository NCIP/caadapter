/*
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
* <!-- LICENSE_TEXT_END -->
 */
 
package gov.nih.nci.caadapter.hl7.v2v3.test;

import gov.nih.nci.caadapter.hl7.transformation.TransformationService;
import gov.nih.nci.caadapter.hl7.transformation.data.XMLElement;

import java.util.List;
import java.io.File;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: altturbo $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.4 $
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

//&umkis        ts.setSchemaValidation(true);
        ts.setOutputFile(new File("output.zip"));
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

        String v1 = "";
        String v2 = "";
        String v3 = "";

        try
        {
            v1 = args[0];
            v2 = args[1];
        }
        catch(Exception ee)
        {
            System.out.println("Usage : <command> [CSV File] [MAP File]");
            v1 = csv;
            v2 = map;
        }
        new TestTransformationServiceCSV_V3(v2, v1);
    }
}

/**
 * HISTORY      : : TestTransformationServiceCSV_V3.java,v $
 */
