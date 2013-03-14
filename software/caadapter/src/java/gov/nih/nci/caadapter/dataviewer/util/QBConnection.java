/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
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
