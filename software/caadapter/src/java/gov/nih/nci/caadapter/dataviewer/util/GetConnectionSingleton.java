/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.dataviewer.util;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class manages the connection and ensures that one connection is created and
 * returns the password for that connection if the connection is alive
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.6 $
 *          $Date: 2008-06-09 19:53:50 $
 */
public class GetConnectionSingleton {
    private static GetConnectionSingleton singletonObject=null;
    private static Connection connection=null;
    private static String serverURL=null;
    private static String userID=null;
    private static JPasswordField password=null;

    public static synchronized Connection getConnection() throws Exception {
        if (connection != null && !connection.isClosed())
            return connection;
        else
            return null;
    }

    public static synchronized String getServerName() {
        return serverURL;
    }

    /**
     * A private Constructor prevents any other class from instantiating.
     */
    private GetConnectionSingleton(String drv, String url, String uid, String pwd) throws Exception {
        try {
            Class.forName(drv);
            serverURL = url;
            userID = uid;
            connection = DriverManager.getConnection(url, uid, pwd);
        } catch (Exception e) {
            throw e;
        }
    }

    public static String getPassword() {
        return password.getText();
    }

    public static synchronized Connection getConnectionSingletonObject(String drv, String _url, String uid, String pwd) throws Exception {
        if (singletonObject == null) {
            singletonObject = new GetConnectionSingleton(drv, _url, uid, pwd);
            password = new JPasswordField(pwd);
        }
        return connection;
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public static synchronized void closeConnection() {
        //System.out.println("close connection called");
        try {
            if ((connection != null) && (!connection.isClosed())) {
                connection.close();
                connection = null;
                singletonObject = null;
                serverURL = null;
                password = null;
                userID = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String isConnectionAvailable() {
        try {
            if ((connection != null) && (!connection.isClosed())) {
                return serverURL;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.5  2007/10/10 19:48:49  jayannah
 * commented a System.out
 *
 * Revision 1.4  2007/08/16 18:53:55  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */
