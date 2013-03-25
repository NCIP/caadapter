/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**

The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location:



 */
package gov.nih.nci.caadapter.dataviewer.util;

import javax.swing.table.AbstractTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * This class manages the connection and ensures that one connection is created and
 * returns the password for that connection if the connection is alive
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.3 $
 *          $Date: 2008-06-09 19:53:50 $
 */
public class QueryTableModel extends AbstractTableModel {
    Vector cache=null;// will hold String[] objects . . .
    int colCount=0;
    String[] headers=null;
    Connection db=null;
    Statement statement=null;
    String currentURL=null;

    public QueryTableModel() {
        cache = new Vector();
        //new oracle.jdbc.OracleDriver();
        //initDB(null);
    }

    public String getColumnName(int i) {
        return headers[i];
    }

    public int getColumnCount() {
        return colCount;
    }

    public int getRowCount() {
        return cache.size();
    }

    public Object getValueAt(int row, int col) {
        return ((String[]) cache.elementAt(row))[col];
    }

    public void setHostURL(String url) {
        if (url.equals(currentURL)) {
            // same database, we can leave the current connection open
            return;
        }
        // Oops . . . new connection required
        closeDB();
        initDB(url);
        currentURL = url;
    }

    // All the real work happens here; in a real application,
    // we'd probably perform the query in a separate thread.

    public String setQuery(String q) throws Exception {
        initDB(null);
        cache = new Vector();
        try {
            StringTokenizer _str = new StringTokenizer(q);
            while (_str.hasMoreTokens()) {
                if (_str.nextToken().equalsIgnoreCase("Select")) {
                    if (_str.nextToken().equalsIgnoreCase("From")) {
                        return "bad_token";
                    }
                }
            }
            // Execute the query and store the result set and its metadata
            ResultSet rs = statement.executeQuery(q);
            ResultSetMetaData meta = rs.getMetaData();
            colCount = meta.getColumnCount();
            int r = 0;
            //System.out.println("result set "+rs.);
            // Now we must rebuild the headers array with the new column names
            headers = new String[colCount];
            for (int h = 1; h <= colCount; h++) {
                headers[h - 1] = meta.getColumnName(h);
            }
            // and file the cache with the records from our query. This would
            // not be
            // practical if we were expecting a few million records in response
            // to our
            // query, but we aren't, so we can do this.
            while (rs.next()) {
                if (r == 10)
                    break;
                String[] record = new String[colCount];
                for (int i = 0; i < colCount; i++) {
                    record[i] = rs.getString(i + 1);
                }
                cache.addElement(record);
                r++;
            }
            fireTableChanged(null);// notify everyone that we have a new table.
        } catch (Exception e) {
            cache = new Vector();// blank it out and keep going.
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        return "success";
    }

    public void initDB(String url) {
        try {
            //db = DriverManager.getConnection(url);
            //db = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "hr", "hr");
            statement = _con.createStatement();
            //statement = db.createStatement();
        } catch (Exception e) {
            System.out.println("Could not initialize the database.");
            e.printStackTrace();
        }
    }

    Connection _con;

    public void setConnection(Connection con) {
        this._con = con;
    }

    public void closeDB() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (db != null) {
                db.close();
            }
        } catch (Exception e) {
            System.out.println("Could not close the current connection.");
            e.printStackTrace();
        }
    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2007/08/16 18:53:55  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */