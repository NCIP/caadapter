/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/*





 */

package gov.nih.nci.caadapter.hl7.v2v3.test;

import gov.nih.nci.caadapter.hl7.transformation.TransformationService;
import gov.nih.nci.caadapter.hl7.transformation.TransformationServiceUtil;
import gov.nih.nci.caadapter.hl7.transformation.data.XMLElement;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;

import javax.swing.*;
import java.util.List;
import java.io.File;
import java.io.IOException;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: altturbo $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.6 $
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
        mainTest(csv, map, null);
    }

    public TestTransformationServiceCSV_V3(String csv, String map, String out)
    {
        mainTest(csv, map, out);
    }

    private void mainTest(String csv, String map, String out)
    {
        TransformationService ts = new TransformationService(csv, map);

//&umkis        ts.setSchemaValidation(true);

        if ((out == null)||(out.trim().equals(""))) out = "testOutput.zip";
        else out = out.trim();
        ts.setOutputFile(new File(out));
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
        int currentCount = 0;
        while(true)
        {
            currentCount++;

            try{
                String xmlMsg1 = "";
                try { xmlMsg1 = TransformationServiceUtil.readFromZip(new File(out) ,String.valueOf(currentCount-1)+"_Reorganized.xml"); }
                catch (IOException ie)
                { xmlMsg1 = null;  }

                String xmlMsg2 = "";
                try { xmlMsg2 = TransformationServiceUtil.readFromZip(new File(out) ,String.valueOf(currentCount-1)+".xml"); }
                catch (IOException ie)
                {  xmlMsg2 = null; }

                if ((xmlMsg1 == null)&&((xmlMsg2 == null))) break;

                ValidatorResults validatorsToShow=new ValidatorResults();
                //add structure validation ... level_0
                try { validatorsToShow.addValidatorResults((ValidatorResults)TransformationServiceUtil.readObjFromZip(new File(out),String.valueOf(currentCount-1)+".ser"));   }
                catch (IOException ie)
                { validatorsToShow = null; }

                if (xmlMsg1 != null)
                {
                    System.out.println("###############  Reorganized Message : \n" + xmlMsg1);
                }
                if (xmlMsg2 != null)
                {
                    System.out.println("###############  Original Message : \n" + xmlMsg2);
                }
                if (validatorsToShow != null)
                {
                    System.out.println("###############  Validator Result : \n" + validatorsToShow.toString());
                }

            }catch(Exception e){
                e.printStackTrace();
            }
        }

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
            System.out.println("Usage : <command> [CSV File] [MAP File] [optional:output]");
            v1 = csv;
            v2 = map;
        }

        try
        {
            v3 = args[3];
        }
        catch(Exception ee)
        {
            v3 = null;
        }
        new TestTransformationServiceCSV_V3(v2, v1, v3);
    }
}

/**
 * HISTORY      : : TestTransformationServiceCSV_V3.java,v $
 */
