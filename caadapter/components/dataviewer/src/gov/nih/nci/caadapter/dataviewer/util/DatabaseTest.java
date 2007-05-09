package gov.nih.nci.caadapter.dataviewer.util;

import gov.nih.nci.caadapter.dataviewer.util.QueryTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class DatabaseTest extends JFrame
{

    JTextField hostField;

    JTextField queryField;

    QueryTableModel qtm;

    public JTable getTable()
    {
        return table;
    }

    JTable table;

    public JScrollPane getScrollpane()
    {
        return scrollpane;
    }

    JScrollPane scrollpane;                           

    public QueryTableModel getQTM(){
        return qtm;
    }
    public DatabaseTest(Connection conn, String query) throws Exception
    {
        qtm = new QueryTableModel();
        qtm.setConnection(conn);
        if (qtm.setQuery(query).equalsIgnoreCase("bad_token"))
        {
            throw new Exception("badtoken");
        }
        //JTable table = new JTable(qtm);
        //scrollpane = new JScrollPane(table);
    }

    public DatabaseTest() throws Exception
    {
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
        jb.addActionListener(new ActionListener()
        {

            public void actionPerformed(ActionEvent e)
            {
                qtm.setHostURL(hostField.getText().trim());
                try
                {
                    qtm.setQuery(queryField.getText().trim());
                } catch (Exception e1)
                {
                    e1.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
        p1.add(jb);
        getContentPane().add(p1, BorderLayout.NORTH);
        getContentPane().add(scrollpane, BorderLayout.CENTER);
    }

    public static void main(String args[])  throws Exception
    {
        DatabaseTest tt = new DatabaseTest();
        tt.setVisible(true);
    }
}



