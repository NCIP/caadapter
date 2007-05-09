package gov.nih.nci.caadapter.sdtm.util;

import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: Mar 22, 2007
 * Time: 10:20:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class DBConnector
{

    static HashSet<String> _ignoreSchemaList = new HashSet<String>();

    static Connection connection;

    public DBConnector() throws Exception
    {
        BufferedReader inputTMP = new BufferedReader(new FileReader("db.properties"));
        String lineTMP;
        while ((lineTMP = inputTMP.readLine()) != null)
        {
            System.out.println("line " + lineTMP);
        }
    }

    public static ArrayList getSchemaCollection(Hashtable conParam) throws Exception
    {
        ArrayList<String> _retAry = new ArrayList<String>();
        try
        {
            //URL, Driver, UserID, pwd
            DatabaseMetaData dbmd = getDBConnection(conParam.get("URL").toString(), conParam.get("Driver").toString(), conParam.get("UserID").toString(), conParam.get("PWD").toString()).getMetaData();
            ResultSet rsSchemas = dbmd.getSchemas();
            Object schema;
            while (rsSchemas.next())
            {//big loop begin
                String _tmp = rsSchemas.getString(1).trim();
                //if (_ignoreSchemaList.contains(conParam.get("SCHEMA").toString()))
                if (!(conParam.get("SCHEMA").toString().equalsIgnoreCase(_tmp)))
                    continue;
                _retAry.add("key" + _tmp);
                schema = _tmp;
                String catalog = schema == null ? null : dbmd.getConnection().getCatalog();
                ResultSet rsTables = dbmd.getTables(catalog, (schema == null ? null : schema.toString()), "%", null);
                if (rsTables != null)
                {
                    while (rsTables.next())
                    {
                        Entity entity = new Entity((schema == null ? null : schema.toString()), rsTables.getString(3).trim());
                        //System.out.println("\t\t\ttest002 " + entity.toString());
                        _retAry.add("tab" + entity.toString());
                        try
                        {
                            if (schema != null)
                            {
                                ResultSet rsColumns = dbmd.getColumns(null, schema.toString(), entity.toString(), "%");
                                while (rsColumns.next())
                                {
                                    String columnName = rsColumns.getString(4).trim();
                                    //String typeName = rsColumns.getString(6);
                                    //int size = rsColumns.getInt(7);
                                    //System.out.println("col" + columnName);
                                    _retAry.add("col" + columnName);
                                }
                                rsColumns.close();
                            }
                        } catch (SQLException e)
                        {
                            throw e;
                        }
                    }
                    rsTables.close();
                }
            }// big loop end
            rsSchemas.close();
        } catch (SQLException e)
        {
            throw e;
        }
        return _retAry;
    }

    public static Connection getDBConnection(String url, String drv, String uid, String pwd) throws Exception
    {
        try
        {
            Class.forName(drv);
            connection = DriverManager.getConnection(url, uid, pwd);
            return connection;
        } catch (Exception e)
        {
            throw e;
        }
    }

    public static Connection getConnection() throws SQLException
    {
        if (!connection.isClosed())
            return connection;
        else
            return null;
    }

    public static void main(String[] args) throws Exception
    {
        try
        {
            BufferedReader inputTMP = new BufferedReader(new FileReader("db.properties"));
            String lineTMP;
            while ((lineTMP = inputTMP.readLine()) != null)
            {
                EmptyStringTokenizer _emp = new EmptyStringTokenizer(lineTMP, ",");
                while (_emp.hasMoreTokens())
                {
                    _ignoreSchemaList.add(_emp.nextToken());
                }
            }
            System.out.println(_ignoreSchemaList.toString());
            //System.exit(0);
            DatabaseMetaData dbmd = null;//getDBConnection().getMetaData();
            ResultSet rsSchemas = dbmd.getSchemas();
            Object schema;
            while (rsSchemas.next())
            {//big loop begin
                String _tmp = rsSchemas.getString(1).trim();
                if (_ignoreSchemaList.contains(_tmp))
                    continue;
                schema = _tmp;
                String catalog = schema == null ? null : dbmd.getConnection().getCatalog();
                ResultSet rsTables = dbmd.getTables(catalog, (schema == null ? null : schema.toString()), "%", null);
                if (rsTables != null)
                {
                    while (rsTables.next())
                    {
                        Entity entity = new Entity((schema == null ? null : schema.toString()), rsTables.getString(3).trim());
                        System.out.println("\t\t\ttest002 " + entity.toString());
                    }
                    rsTables.close();
                }
            }// big loop end
            rsSchemas.close();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
