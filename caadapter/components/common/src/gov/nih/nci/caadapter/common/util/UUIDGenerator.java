/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.common.util;

/***
 * Utility class to generate UUID
 */

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class UUIDGenerator {
    public static String getUniqueString() {
        return UUID.randomUUID().toString();
    }

    public static String getUniqueIDByHostName() {
        String id = null;
        try {
            id = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ue) {
            id = ".....";
        }

        id = id + System.currentTimeMillis();

        return id;
    }

    public static void main(String[] args)
    {
        for (int i=0; i< 10; i++)
        {
            System.out.println(getUniqueString());
        }
    }
}
