package gov.nih.nci.caadapter.dataviewer.helper;

import gov.nih.nci.caadapter.dataviewer.MainDataViewerFrame;
import gov.nih.nci.caadapter.dataviewer.util.Querypanel;
import nickyb.sqleonardo.querybuilder.QueryModel;
import nickyb.sqleonardo.querybuilder.syntax.SQLParser;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.ResultSet;

import gov.nih.nci.caadapter.dataviewer.util.PrintableComponent;
import gov.nih.nci.caadapter.dataviewer.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: Apr 27, 2007
 * Time: 4:42:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToolBarAction implements ActionListener
{

    MainDataViewerFrame _mD;

    public ToolBarAction(MainDataViewerFrame _mD)
    {
        this._mD = _mD;
    }

    public void actionPerformed(ActionEvent e)
    {
        String cmd = e.getActionCommand();
        String description = null;
        // Handle each button.
        if (QBConstants.Load.equals(cmd))
        {//first button clicked
            SQLFileFilter filter = new SQLFileFilter();
            _mD.getSaveSQLFile().setName("Load SQL File....");
            filter.addExtension("sql");
            filter.setDescription("sql");
            _mD.getSaveSQLFile().setFileFilter(filter);
            int returnVal = _mD.getSaveSQLFile().showOpenDialog(_mD.get_jf());
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                String _sqlFileLocation = _mD.getSaveSQLFile().getSelectedFile().getAbsolutePath();
                System.out.println("_sqlFileLocation " + _sqlFileLocation);
                BufferedReader in = null;
                try
                {
                    in = new BufferedReader(new FileReader(_sqlFileLocation));
                    StringBuffer _sqlString = new StringBuffer();
                    String str;
                    while ((str = in.readLine()) != null)
                    {
                        _sqlString.append(" " + str);
                    }
                    System.out.println("!--------------------- " + _sqlString.toString());
                    boolean _tmpBool = ((Querypanel) _mD.get_aryList().get(_mD.get_tPane().getSelectedIndex())).removeAllTables();
                    if (_tmpBool)
                    {
                        QueryModel qm = SQLParser.toQueryModel(_sqlString.toString());
                        ((Querypanel) _mD.get_aryList().get(_mD.get_tPane().getSelectedIndex())).get_queryBuilder().setModel(qm);
                        _mD.get_alreadyFilled().add(new Integer(_mD.get_tPane().getSelectedIndex()));
                    }
                    //_mD.get_tPane().addTab("OpenDomain", _tmpqp);
                } catch (Exception eq)
                {
                } finally
                {
                    try
                    {
                        in.close();
                    } catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }
        } else if (QBConstants.Save.equals(cmd))
        {
            SQLFileFilter filter = new SQLFileFilter();
            _mD.getSaveSQLFile().setName("Save SQL File....");
            filter.addExtension("sql");
            filter.setDescription("sql");
            _mD.getSaveSQLFile().setFileFilter(filter);
            int returnVal = _mD.getSaveSQLFile().showSaveDialog(_mD.get_jf());
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                String _sqlFileLocation = _mD.getSaveSQLFile().getSelectedFile().getAbsolutePath();
                // This is where a real application would open the file.
                System.out.println("_sqlFileLocation " + _sqlFileLocation);
                try
                {
                    BufferedWriter out = new BufferedWriter(new FileWriter(_sqlFileLocation));
                    String _sqlSTR = (((Querypanel) _mD.get_aryList().get(_mD.get_tPane().getSelectedIndex())).get_queryBuilder()).getModel().toString(true).toUpperCase();
                    out.write(_sqlSTR);
                    out.close();
                } catch (IOException ew)
                {
                    JOptionPane.showMessageDialog(_mD.get_jf(), ew.getMessage().toString(), "SQL Save Error", JOptionPane.ERROR_MESSAGE);
                }
                JOptionPane.showMessageDialog(_mD.get_jf(), "      \"" + _sqlFileLocation + "\" sucessfully saved         ", "SQL Save Sucess", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (QBConstants.NEXT.equals(cmd))
        {// third button clicked
            final Dialog _queryWaitDialog = new Dialog(_mD.get_jf());
            new Thread(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        String _sqlSTR = (((Querypanel) _mD.get_aryList().get(_mD.get_tPane().getSelectedIndex())).get_queryBuilder()).getModel().toString(true).toUpperCase();
                        DatabaseTest _dbtest = new DatabaseTest(_mD.get_con(), _sqlSTR);
                        TableSorter sorter = new TableSorter(_dbtest.getQTM());
                        JTable table = new JTable(sorter);//NEW
                        sorter.setTableHeader(table.getTableHeader());//ADDED THIS
                        JScrollPane _jp4RunSQL = new JScrollPane(table);
                        final JDialog _tmpDialog = new JDialog(_mD.get_jf(), true);
                        _tmpDialog.setTitle("Query Results");
                        _tmpDialog.setSize(600, 200);
                        _tmpDialog.setLocation(300, 300);
                        _tmpDialog.setLayout(new BorderLayout());
                        TitledBorder titleTop = BorderFactory.createTitledBorder("Query Results");
                        _jp4RunSQL.setBorder(titleTop);
                        _tmpDialog.getContentPane().add(_jp4RunSQL, BorderLayout.CENTER);
                        JPanel labelButton = new JPanel();
                        labelButton.setLayout(new BorderLayout());
                        JLabel label = new JLabel("The query results are adjusted to 10 rows");
                        label.setFont(new Font("Serif", Font.BOLD, 10));
                        label.setForeground(Color.BLUE);
                        labelButton.add(label, BorderLayout.CENTER);
                        JPanel _okButPane = new JPanel();
                        JButton _okBtn = new JButton("   OK   ");
                        _okBtn.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                _tmpDialog.dispose();
                            }
                        });
                        _okButPane.add(_okBtn);
                        LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
                        _okButPane.setBorder(lineBorder);
                        try
                        {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1)
                        {
                            e1.printStackTrace();
                        }
                        _queryWaitDialog.dispose();

                        labelButton.add(_okButPane, BorderLayout.SOUTH);
                        _tmpDialog.getContentPane().add(labelButton, BorderLayout.SOUTH);
                        _tmpDialog.setVisible(true);
                        // getContentPane().add(scrollpane, BorderLayout.CENTER);
                    } catch (Exception e1)
                    {
                        final JDialog _tmpDialog = new JDialog(_mD.get_jf(), true);

                        _tmpDialog.setLocation(400, 300);
                        _tmpDialog.setTitle("Bad Query");
                        _tmpDialog.setLayout(new BorderLayout());
                        LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
                        JPanel _borderPane = new JPanel();
                        _borderPane.add(new JLabel("       Query did not execute due to:\n " + e1.getMessage()));
                        _borderPane.setBorder(lineBorder);
                        _tmpDialog.getContentPane().add(_borderPane, BorderLayout.CENTER);
                        JPanel _okButPane = new JPanel();
                        JButton _okBtn = new JButton("   OK   ");
                        _okBtn.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                _tmpDialog.dispose();
                            }
                        });
                        _okButPane.add(_okBtn);
                        // LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
                        _okButPane.setBorder(lineBorder);
                        _queryWaitDialog.dispose();
                        _tmpDialog.getContentPane().add(_okButPane, BorderLayout.SOUTH);
                        int length = e1.getMessage().length() + 50+425;
                        System.out.println("length-----------> "+length);
                         _tmpDialog.setSize(length, 100);
                        _tmpDialog.setVisible(true);
                    }
                }
            }).start();
            _queryWaitDialog.setTitle("Query in Progress");
            _queryWaitDialog.setSize(350, 100);
            _queryWaitDialog.setLocation(450, 300);
            _queryWaitDialog.setLayout(new BorderLayout());
            LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
            JPanel _waitLabel = new JPanel();
            _waitLabel.setBorder(lineBorder);
            _waitLabel.add(new JLabel("      Query in Progress, Please wait ..."));
            _queryWaitDialog.add(new JLabel("                       "), BorderLayout.NORTH);
            _queryWaitDialog.add(_waitLabel, BorderLayout.CENTER);
            _queryWaitDialog.setVisible(true);
        } else if ("".equals(cmd))
        {
            System.out.println("Edit Sql");
            _mD.get_lPane().setSelectedIndex(3);
            if (_mD.get_jp3().getComponentCount() > 0)
                _mD.get_jp3().removeAll();
            //_jp3 = new JPanel();
            _mD.get_jp3().setLayout(new BorderLayout());
            _mD.get_jp3().add((((Querypanel) _mD.get_aryList().get(_mD.get_tPane().getSelectedIndex())).get_queryBuilder()).getBrowser(), BorderLayout.CENTER);
            _mD.get_jp3().revalidate();
        } else if (QBConstants.ADD.equals(cmd))
        {
            System.out.println("Add Tables");
            _mD.get_lPane().setSelectedIndex(4);
            if (_mD.get_jp4().getComponentCount() > 0)
                _mD.get_jp4().removeAll();
            _mD.get_jp4().setLayout(new BorderLayout());
            _mD.get_jp4().add((((Querypanel) _mD.get_aryList().get(_mD.get_tPane().getSelectedIndex())).get_queryBuilder()).getObjects(), BorderLayout.CENTER);
            _mD.get_jp4().revalidate();
        } else if (QBConstants.PRINT.equals(cmd))
        {
            try
            {
                PrintableComponent _pc = new PrintableComponent(_mD.get_tPane());
                _pc.print();
            } catch (Exception e1)
            {
                e1.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
            }
        } else if (QBConstants.HELP.equals(cmd))
        {
            String imgLocation = "/_Help.gif";
           // URL imageURL = MainDataViewerFrame.class.getResource(imgLocation);
            JOptionPane.showMessageDialog(_mD.get_jf(), "Help Content Under Development!", "Help", JOptionPane.INFORMATION_MESSAGE);
        } else if (QBConstants.Reset.equals(cmd))
        {
            System.out.println("reset");
            ((Querypanel) _mD.get_aryList().get(_mD.get_tPane().getSelectedIndex())).removeAllTables();
            //remove all tables
            ((Querypanel) _mD.get_aryList().get(_mD.get_tPane().getSelectedIndex())).fillPanel();//add all tables
            _mD.get_alreadyFilled().add(new Integer(_mD.get_tPane().getSelectedIndex()));
        } else if("validate".equalsIgnoreCase(cmd)){
            {// third button clicked
            final Dialog _queryWaitDialog = new Dialog(_mD.get_jf());
            new Thread(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        String _sqlSTR = (((Querypanel) _mD.get_aryList().get(_mD.get_tPane().getSelectedIndex())).get_queryBuilder()).getModel().toString(true).toUpperCase();
                        ResultSet rs = _mD.get_con().createStatement().executeQuery(_sqlSTR);
//                        DatabaseTest _dbtest = new DatabaseTest(_mD.get_con(), _sqlSTR);
//                        TableSorter sorter = new TableSorter(_dbtest.getQTM());
//                        JTable table = new JTable();//NEW
//                        sorter.setTableHeader(table.getTableHeader());//ADDED THIS
//                        JScrollPane _jp4RunSQL = new JScrollPane(table);
                        final JDialog _tmpDialog = new JDialog(_mD.get_jf(), true);
                        _tmpDialog.setTitle("Validate Query Result");
                        _tmpDialog.setSize(400, 200);
                        _tmpDialog.setLocation(300, 300);
                        _tmpDialog.setLayout(new BorderLayout());
                        TitledBorder titleTop = BorderFactory.createTitledBorder("Query Result");
                        JPanel labelPan = new JPanel();
                        labelPan.setLayout(new BorderLayout());
                        labelPan.add(new JLabel("The query executed successfully"), BorderLayout.CENTER);
                        labelPan.setBorder(titleTop);
//                        _jp4RunSQL.setBorder(titleTop);
//                        _tmpDialog.getContentPane().add(_jp4RunSQL, BorderLayout.CENTER);
                        JPanel labelButton = new JPanel();
                        labelButton.setLayout(new BorderLayout());
//                        JLabel label = new JLabel("The query results are adjusted to 10 rows");
//                        label.setFont(new Font("Serif", Font.BOLD, 10));
//                        label.setForeground(Color.BLUE);
//                        labelButton.add(label, BorderLayout.CENTER);
                        JPanel _okButPane = new JPanel();
                        JButton _okBtn = new JButton("   OK   ");
                        _okBtn.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                _tmpDialog.dispose();
                            }
                        });
                        _okButPane.add(_okBtn);
                        LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
                        _okButPane.setBorder(lineBorder);
                        try
                        {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1)
                        {
                            e1.printStackTrace();
                        }
                        _queryWaitDialog.dispose();

                         _tmpDialog.getContentPane().add(labelPan, BorderLayout.CENTER);
                        labelButton.add(_okButPane, BorderLayout.SOUTH);
                        _tmpDialog.getContentPane().add(labelButton, BorderLayout.SOUTH);
                        _tmpDialog.setVisible(true);
                       //  getContentPane().add(, BorderLayout.CENTER);
                    } catch (Exception e1)
                    {
                        final JDialog _tmpDialog = new JDialog(_mD.get_jf(), true);

                        _tmpDialog.setLocation(400, 300);
                        _tmpDialog.setTitle("Bad Query");
                        _tmpDialog.setLayout(new BorderLayout());
                        LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
                        JPanel _borderPane = new JPanel();
                        _borderPane.add(new JLabel("       Query did not execute due to:\n " + e1.getMessage()));
                        _borderPane.setBorder(lineBorder);
                        _tmpDialog.getContentPane().add(_borderPane, BorderLayout.CENTER);
                        JPanel _okButPane = new JPanel();
                        JButton _okBtn = new JButton("   OK   ");
                        _okBtn.addActionListener(new ActionListener()
                        {
                            public void actionPerformed(ActionEvent e)
                            {
                                _tmpDialog.dispose();
                            }
                        });
                        _okButPane.add(_okBtn);
                        // LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
                        _okButPane.setBorder(lineBorder);
                        _queryWaitDialog.dispose();
                        _tmpDialog.getContentPane().add(_okButPane, BorderLayout.SOUTH);
                        int length = e1.getMessage().length() + 50+425;
                        System.out.println("length-----------> "+length);
                         _tmpDialog.setSize(length, 100);
                        _tmpDialog.setVisible(true);
                    }
                }
            }).start();
            _queryWaitDialog.setTitle("Query in Progress");
            _queryWaitDialog.setSize(350, 100);
            _queryWaitDialog.setLocation(450, 300);
            _queryWaitDialog.setLayout(new BorderLayout());
            LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
            JPanel _waitLabel = new JPanel();
            _waitLabel.setBorder(lineBorder);
            _waitLabel.add(new JLabel("      Query in Progress, Please wait ..."));
            _queryWaitDialog.add(new JLabel("                       "), BorderLayout.NORTH);
            _queryWaitDialog.add(_waitLabel, BorderLayout.CENTER);
            _queryWaitDialog.setVisible(true);
        }
            
        }
    }
}
