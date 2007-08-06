package gov.nih.nci.caadapter.dataviewer.util;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: Jul 9, 2007
 * Time: 2:02:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetConnectionSingleton
{

    private static GetConnectionSingleton singletonObject;

    private static Connection connection;

    private static String serverURL;

    private static String userID;

    private static JPasswordField password;

    public static synchronized Connection getConnection() throws Exception
    {
        if (connection !=null && !connection.isClosed())
            return connection;
        else
            return null;
    }

    public static synchronized String getServerName()
    {
        return serverURL;
    }

    /**
     * A private Constructor prevents any other class from instantiating.
     */
    private GetConnectionSingleton(String drv, String url, String uid, String pwd) throws Exception
    {
        try
        {
            Class.forName(drv);
            serverURL = url;
            userID = uid;
            connection = DriverManager.getConnection(url, uid, pwd);
        } catch (Exception e)
        {
            throw e;
        }
    }

    public static String getPassword()
    {
        return password.getText();
    }

    public static synchronized Connection getConnectionSingletonObject(String drv, String _url, String uid, String pwd) throws Exception
    {
        if (singletonObject == null)
        {
            singletonObject = new GetConnectionSingleton(drv, _url, uid, pwd);
            password = new JPasswordField(pwd);
        }
        return connection;
    }

    public Object clone() throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException();
    }

    public static synchronized void closeConnection()
    {
        System.out.println("close connection called");
        try
        {
            if ((connection != null) && (!connection.isClosed()))
            {
                connection.close();
                connection = null;
                singletonObject = null;
                serverURL = null;
                password = null;
                userID = null;
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static String isConnectionAvailable()
    {
        try
        {
            if ((connection != null) && (!connection.isClosed()))
            {
                return serverURL;
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
