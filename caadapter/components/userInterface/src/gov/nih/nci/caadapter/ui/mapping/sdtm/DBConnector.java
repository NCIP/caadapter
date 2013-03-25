/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.mapping.sdtm;

import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
import gov.nih.nci.caadapter.dataviewer.util.GetConnectionSingleton;
import gov.nih.nci.caadapter.sdtm.meta.QueryBuilderMeta;
import gov.nih.nci.caadapter.sdtm.meta.QBTableMetaData;
import gov.nih.nci.caadapter.sdtm.util.Entity;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

/**
 * This is the class which read the db.properties file
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.3 $
 *          $Date: 2008-06-09 19:54:06 $
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
        ArrayList _retAry = new ArrayList();
        try
        {
            DatabaseMetaData dbmd = getDBConnection(conParam.get("URL").toString(), conParam.get("Driver").toString(), conParam.get("UserID").toString(), conParam.get("PWD").toString()).getMetaData();
            ResultSet rsSchemas = dbmd.getSchemas();
            Object schema;
            while (rsSchemas.next())
            {//big loop begin
                String _tmp = rsSchemas.getString(1).trim();
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
                        _retAry.add(new QBTableMetaData(rsTables.getString("TABLE_TYPE"),entity.toString()));

                        try
                        {
                            if (schema != null)
                            {
                                ResultSet rsColumns = dbmd.getColumns(null, schema.toString(), entity.toString(), "%");
                                while (rsColumns.next())
                                {
                                    String columnName = rsColumns.getString("COLUMN_NAME");
                                    String columnType = rsColumns.getString("TYPE_NAME");
                                    int size = rsColumns.getInt("COLUMN_SIZE");
                                    int nullable = rsColumns.getInt("NULLABLE");
                                    boolean state;
                                    if (nullable == DatabaseMetaData.columnNullable)
                                    {
                                        state = true;
                                    } else
                                    {
                                        state = false;
                                    }
                                    int position = rsColumns.getInt("ORDINAL_POSITION");
                                    _retAry.add(new QueryBuilderMeta(columnName, columnType, new Integer(size).toString(), state, new Integer(position).toString()));
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
        return GetConnectionSingleton.getConnectionSingletonObject(drv, url, uid, pwd);
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
