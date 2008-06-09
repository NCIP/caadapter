/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
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
