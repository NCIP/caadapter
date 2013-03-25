/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.dataviewer.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

/**
 * This class helps in executing the query and returning the resultset
 * which is limited to just 10 rows only
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.3 $
 *          $Date: 2008-06-09 19:53:50 $
 */
public class DatabaseTest extends JFrame {
    JTextField hostField=null;
    JTextField queryField=null;
    QueryTableModel qtm=null;
    JScrollPane scrollpane=null;
    JTable table=null;

    public JTable getTable() {
        return table;
    }

    public JScrollPane getScrollpane() {
        return scrollpane;
    }

    public QueryTableModel getQTM() {
        return qtm;
    }

    public DatabaseTest(Connection conn, String query) throws Exception {
        qtm = new QueryTableModel();
        qtm.setConnection(conn);
        if (qtm.setQuery(query).equalsIgnoreCase("bad_token")) {
            throw new Exception("badtoken");
        }
    }

    public DatabaseTest() throws Exception {
        super("Database Test Frame");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(350, 200);
        qtm = new QueryTableModel();
        JTable table = new JTable(qtm);
        JScrollPane scrollpane = new JScrollPane(table);
        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(3, 2));
        p1.add(new JLabel("Enter the Host URL: "));
        p1.add(hostField = new JTextField());
        p1.add(new JLabel("Enter your query: "));
        p1.add(queryField = new JTextField());
        p1.add(new JLabel("Click here to send: "));
        JButton jb = new JButton("Search");
        jb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                qtm.setHostURL(hostField.getText().trim());
                try {
                    qtm.setQuery(queryField.getText().trim());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        p1.add(jb);
        getContentPane().add(p1, BorderLayout.NORTH);
        getContentPane().add(scrollpane, BorderLayout.CENTER);
    }

    public static void main(String args[]) throws Exception {
        DatabaseTest tt = new DatabaseTest();
        tt.setVisible(true);
    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2007/08/16 18:53:55  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */



