/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dvts.ws;

/**
 * Created by IntelliJ IDEA.
 * User: kium
 * Date: Jun 18, 2009
 * Time: 1:07:12 PM
 * To change this template use File | Settings | File Templates.
 */
import java.net.*;

class TestIPAddress
{
	TestIPAddress()
	{
        InetAddress[] addr = null;
        try
        {
            addr = InetAddress.getAllByName("10.1.1.67");
        }
        catch(UnknownHostException ue)
        {
            System.out.println("UnknownHostException : " + ue.getMessage());
            return;
        }

        for(InetAddress address:addr)
        {
            byte[] bts = address.getAddress();
            String ip = "";
            for(byte bt:bts)
            {
               int inc = (int) bt;
               ip = ip + "." + inc;
            }
            ip = ip.substring(1);
            System.out.println("***** IP : " + ip + ", cName:" + address.getCanonicalHostName() + ", name:" + address.getHostName() + ", hAddr:" + address.getHostAddress());
            System.out.println("  *** isAnyLocalAddress : " + address.isAnyLocalAddress());
            System.out.println("  *** isLinkLocalAddress : " + address.isLinkLocalAddress());
            System.out.println("  *** isLoopbackAddress : " + address.isLoopbackAddress());
            System.out.println("  *** isMCGlobal : " + address.isMCGlobal());
            System.out.println("  *** isMCNodeLocal : " + address.isMCNodeLocal());
            System.out.println("  *** isMCOrgLocal : " + address.isMCOrgLocal());
            System.out.println("  *** isMCSiteLocal : " + address.isMCSiteLocal());
            System.out.println("  *** isMulticastAddress : " + address.isMulticastAddress());
            System.out.println("  *** isSiteLocalAddress : " + address.isSiteLocalAddress());



        }

    }

    public static void main(String[] arg)
    {
        new TestIPAddress();
    }
}
