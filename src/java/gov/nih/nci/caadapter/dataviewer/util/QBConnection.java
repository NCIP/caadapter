/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location:
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.dataviewer.util;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: Apr 27, 2007
 * Time: 3:42:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class QBConnection
{

    public static Connection getDBConnection(String url, String driver, String uid, String pwd)
    {
        try
        {
            Connection connection;
            Class.forName(driver);
            connection = DriverManager.getConnection(url, uid, pwd);
            return connection;
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
