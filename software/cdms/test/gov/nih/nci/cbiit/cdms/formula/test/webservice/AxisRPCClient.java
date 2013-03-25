/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.test.webservice;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Dec 6, 2010
 * Time: 11:24:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class AxisRPCClient
{
    public static void main(String[] args)
    {
        try
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

            CalculateOnWSDLService service = new CalculateOnWSDLServiceLocator();
            CalculateOnWSDL calculateService = service.getCalculateOnWSDL();
            String res = calculateService.execute(formula, new String[]{"BDY_WT_KG","PERS_HT_CM"}, new String[] {"12", "12"});

            System.out.println("Result : " + res);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
