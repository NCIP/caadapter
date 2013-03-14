/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.test.webservice;

import gov.nih.nci.cbiit.cdms.formula.common.util.FileUtil;
import gov.nih.nci.cbiit.cdms.formula.common.util.DefaultSettings;

import javax.swing.text.html.HTML;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.net.SocketTimeoutException;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Dec 6, 2010
 * Time: 11:41:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class WebServiceTestDoGet
{
    public static void main(String[] args)
    {
        String formula = "<formula type=\"math\" name=\"BSA1\" status=\"complete\">\n" +
                    "        <annotation>Dubois  Dubois formula : http://en.wikipedia.org/wiki/Body_surface_area#Calculation</annotation>\n" +
                    "        <expression unit= \"m^2\" description=\"(m^2)\" type=\"expression\" operation=\"multiplication\" name=\"BSA1\">\n" +
                    "            <term value=\"0.00718\" type=\"constant\" name=\"factor\"/>\n" +
                    "            <term type=\"expression\" operation=\"multiplication\" name=\"factor\">\n" +
                    "                <term type=\"expression\" operation=\"power\" name=\"factor\">\n" +
                    "                    <term unit= \"kg\" description=\"(kg)\" value=\"BDY_WT_KG\" type=\"variable\" name=\"base\"/>\n" +
                    "                    <term value=\"0.425\" type=\"constant\" name=\"index\"/>\n" +
                    "                </term>\n" +
                    "                <term type=\"expression\" operation=\"power\" name=\"factor\">\n" +
                    "                    <term unit= \"cm\" description=\"(cm)\" value=\"PERS_HT_CM\" type=\"variable\" name=\"base\"/>\n" +
                    "                    <term value=\"0.725\" type=\"constant\" name=\"index\"/>\n" +
                    "                </term>\n" +
                    "            </term>\n" +
                    "        </expression>\n" +
                    "        <parameter name=\"BDY_WT_KG\" unit=\"kg\" dataType=\"number\" usage=\"parameter\" description=\"Body Weight in Kilograms\" cdeId=\"2003303\" cdeReference=\"https://cdebrowser.nci.nih.gov/CDEBrowser/\"/>\n" +
                    "\t\t<parameter name=\"PERS_HT_CM\" unit=\"cm\" dataType=\"number\" usage=\"parameter\" description=\"Person Height Measurement\" cdeId=\"2003304\" cdeReference=\"https://cdebrowser.nci.nih.gov/CDEBrowser/\"/>\n" +
                    "    </formula>";
        String baseURL = "http://165.112.133.125:8080/caAdapterWS_cdms/FormulaCalculateService?";

        String formula2 = DefaultSettings.changeCharacter(formula);
        
        URL ur = null;
        try
        {
            ur = new URL(baseURL + "formula=" + formula2 + "&BDY_WT_KG=12&PERS_HT_CM=12");
        }
        catch(MalformedURLException ue)
        {
            System.out.println("Invalid URL : " + ue.getMessage());
            return;
        }
        URLConnection uc = null;
        try
        {
            uc = ur.openConnection();
        }
        catch(IOException ue)
        {
            System.out.println("IOException connection fail : " + ue.getMessage());
            return;
        }
        InputStream is = null;
        try
        {
            uc.connect();
            is = uc.getInputStream();
        }
        catch(SocketTimeoutException se)
        {
            System.out.println("SocketTimeoutException : " + se.getMessage());
            return;
        }
        catch(IOException se)
        {
            System.out.println("IOException Socket fail : " + se.getMessage());
            return;
        }



        DataInputStream dis = new DataInputStream(is);

        byte bt = 0;

        boolean started = false;

        String res = "";
        while(true)
        {
            try { bt = dis.readByte(); }
            catch(IOException ie) { break; }
            catch(NullPointerException ie) { break; }

            char cr = (char) bt;
            res = res + cr;

        }


        try
        {
            dis.close();
            is.close();
        }
        catch(IOException se)
        {
            System.out.println("IOException Closing fail : " + se.getMessage());
            return;
        }
        System.out.println("$$$ Result:" + res);
    }
}
