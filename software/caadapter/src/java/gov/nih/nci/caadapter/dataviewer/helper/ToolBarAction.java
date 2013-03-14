/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */
package gov.nih.nci.caadapter.dataviewer.helper;

import gov.nih.nci.caadapter.dataviewer.MainDataViewerFrame;
import gov.nih.nci.caadapter.dataviewer.util.*;
import nickyb.sqleonardo.querybuilder.QueryModel;
import nickyb.sqleonardo.querybuilder.syntax.SQLParser;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class file handles the events generated from the data viewer frame
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.10 $
 *          $Date: 2008-06-09 19:53:50 $
 */
public class ToolBarAction implements ActionListener {
    MainDataViewerFrame _mD;

    public ToolBarAction(MainDataViewerFrame _mD) {
        this._mD = _mD;
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        int selectedIndex = _mD.get_tPane().getSelectedIndex();
        // Handle each button.
        if (QBConstants.Save.equals(cmd)) {
            String _sqlSTR = (((Querypanel) _mD.get_aryList().get(_mD.get_tPane().getSelectedIndex())).get_queryBuilder()).getQueryModel().toString().toUpperCase();
            String domainName = _mD.get_tPane().getTitleAt(selectedIndex).substring(0, 2);
            _mD.getSqlSaveHashMap().put(domainName, _sqlSTR);
            _mD.get_alreadyFilled().add(new Integer(_mD.get_tPane().getSelectedIndex()));
            if (_mD.getSqlSaveHashMap().size() == _mD.get_tPane().getTabCount()) {
                _mD.getQbAddButtons().getSaveButton().setEnabled(true);
            }
        } else if (cmd.equals("exitwithoutsave")) {
            Object[] options = {"Yes", "No"};
            int n = JOptionPane.showOptionDialog(_mD.get_jf(), "Any changes made will be discarded, Continue?", "Exit without Saving", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
            if (n == 0) {
                  _mD.get_jf().dispose();
            }

        } else if (cmd.equalsIgnoreCase("Exit")) {
            _mD.getDialog().removeAll();
            BufferedWriter out = null;
            try {
                if (_mD.getSaveFile().exists())
                    _mD.getSaveFile().delete();
                out = new BufferedWriter(new FileWriter(_mD.getSaveFile()));
                //remove the all sql elements
                String tempStr = removeSQLElements(_mD.getXmlString());
                //remove all sql elements
                out.write(tempStr);
                out.append("\n</mapping>");
                //closing because the user clicked save and exit; so, LOADING and MARKING all columns
                out.close();
                /**
                 *
                 * Add code for going into each domain and calling a setselected JTabbedpane and getting all the sqls
                 * right here
                 */
                int numberOfTabs = _mD.get_tPane().getTabCount();
                for (int i = 0; i < numberOfTabs; i++) {
                    if (!_mD.get_alreadyFilled().contains((Integer) i)) {
                        _mD.get_tPane().setSelectedIndex(i);
                        loadTablesQuietly();
                        //generate the query because the user clicked on save all
                        String _sqlSTR = (((Querypanel) _mD.get_aryList().get(_mD.get_tPane().getSelectedIndex())).get_queryBuilder()).getQueryModel().toString().toUpperCase();
                        String domainName = _mD.get_tPane().getTitleAt(i).substring(0, 2);
                        String saveSQLForMapFile = saveSQLForMapFile = markSelectedColumns(domainName, _sqlSTR);
                        try {
                            _mD.getSqlSaveHashMap().put(domainName, saveSQLForMapFile);
                        } catch (Exception e1) {
                            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    } else {
                        _mD.get_tPane().setSelectedIndex(i);
                        String domainName = _mD.get_tPane().getTitleAt(i).substring(0, 2);
                        String _sqlSTR = (((Querypanel) _mD.get_aryList().get(_mD.get_tPane().getSelectedIndex())).get_queryBuilder()).getQueryModel().toString().toUpperCase();
                        _mD.getSqlSaveHashMap().put(domainName, _sqlSTR);
                    }
                }

                /**
                 * End code
                 */
                BufferedWriter out1 = new BufferedWriter(new FileWriter(_mD.getSaveFile()));
                out1.write(tempStr);
                Set set = _mD.getSqlSaveHashMap().keySet();
                out1.write("<!-- PLEASE DO NOT MODIFY THE SQL STATEMENTS BELOW-->");
                for (Iterator iterator = set.iterator(); iterator.hasNext();) {
                    String domainName = (String) iterator.next();
                    String sql4Domain = (String) _mD.getSqlSaveHashMap().get(domainName);
                    out1.write("\n<sql name=\"" + domainName + "\">");
                    out1.write("" + sql4Domain);
                    out1.write("</sql>");
                }
                out1.append("\n</mapping>");
                out1.close();
                JOptionPane.showMessageDialog(_mD.get_jf(), "File \"" + _mD.getSaveFile().getName() + "\" was saved successfully with the mapping and generated SQL information.", "Mapping file is saved", JOptionPane.INFORMATION_MESSAGE);
                _mD.getDialog().dispose();
                //_mD.getTransformBut().setEnabled(true);
            } catch (IOException ee) {
                ee.printStackTrace();
                JOptionPane.showMessageDialog(_mD.get_jf(), "The file \"" + _mD.getSaveFile().getName() + "\" was not saved dure to " + ee.getLocalizedMessage(), "Mapping file is not saved", JOptionPane.ERROR_MESSAGE);
            } finally {
                _mD.setSQLStmtSaved(true);
                try {
                    if (out != null)
                        out.close();
                } catch (IOException ee) {
                    ee.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
                }
            }
        } else if (QBConstants.NEXT.equals(cmd)) {// third button clicked
            final Dialog _queryWaitDialog = new Dialog(_mD.get_jf());
            new Thread(new Runnable() {
                public void run() {
                    try {
                        String _sqlSTR = (((Querypanel) _mD.get_aryList().get(_mD.get_tPane().getSelectedIndex())).get_queryBuilder()).getQueryModel().toString().toUpperCase();
                        DatabaseTest _dbtest = new DatabaseTest(_mD.get_con(), _sqlSTR);
                        TableSorter sorter = new TableSorter(_dbtest.getQTM());
                        JTable table = new JTable(sorter);//NEW
                        sorter.setTableHeader(table.getTableHeader());//ADDED THIS
                        JScrollPane _jp4RunSQL = new JScrollPane(table);
                        final JDialog _tmpDialog = new JDialog(_mD.get_jf(), true);
                        _tmpDialog.setTitle("Query Results");
                        _tmpDialog.setSize(600, 200);
                        _tmpDialog.setLocationRelativeTo(null);
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
                        _okBtn.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                _tmpDialog.dispose();
                            }
                        });
                        _okButPane.add(_okBtn);
                        LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
                        _okButPane.setBorder(lineBorder);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        _queryWaitDialog.dispose();
                        labelButton.add(_okButPane, BorderLayout.SOUTH);
                        _tmpDialog.getContentPane().add(labelButton, BorderLayout.SOUTH);
                        _tmpDialog.setVisible(true);
                        // getContentPane().add(scrollpane, BorderLayout.CENTER);
                    } catch (Exception e1) {
                        final JDialog _tmpDialog = new JDialog(_mD.get_jf(), true);
                        _tmpDialog.setLocationRelativeTo(null);
                        _tmpDialog.setTitle("Bad Query");
                        _tmpDialog.setLayout(new BorderLayout());
                        LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
                        JPanel _borderPane = new JPanel();
                        _borderPane.add(new JLabel("       Query did not execute due to:\n " + e1.getMessage()));
                        _borderPane.setBorder(lineBorder);
                        _tmpDialog.getContentPane().add(_borderPane, BorderLayout.CENTER);
                        JPanel _okButPane = new JPanel();
                        JButton _okBtn = new JButton("   OK   ");
                        _okBtn.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                _tmpDialog.dispose();
                            }
                        });
                        _okButPane.add(_okBtn);
                        // LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
                        _okButPane.setBorder(lineBorder);
                        _queryWaitDialog.dispose();
                        _tmpDialog.getContentPane().add(_okButPane, BorderLayout.SOUTH);
                        int length = e1.getMessage().length() + 50 + 425;
                        System.out.println("length-----------> " + length);
                        _tmpDialog.setSize(length, 100);
                        _tmpDialog.setVisible(true);
                    }
                }
            }).start();
            _queryWaitDialog.setTitle("Query in Progress");
            _queryWaitDialog.setSize(350, 100);
            _queryWaitDialog.setLocationRelativeTo(null);
            _queryWaitDialog.setLayout(new BorderLayout());
            LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
            JPanel _waitLabel = new JPanel();
            _waitLabel.setBorder(lineBorder);
            _waitLabel.add(new JLabel("      Query in Progress, Please wait ..."));
            _queryWaitDialog.add(new JLabel("                       "), BorderLayout.NORTH);
            _queryWaitDialog.add(_waitLabel, BorderLayout.CENTER);
            _queryWaitDialog.setVisible(true);
        } else if ("".equals(cmd)) {
            System.out.println("Edit Sql");
        } else if (QBConstants.ADD.equals(cmd)) {
            System.out.println("Add Tables");
        } else if (QBConstants.PRINT.equals(cmd)) {
            try {
                PrintableComponent _pc = new PrintableComponent(_mD.get_tPane());
                _pc.print();
            } catch (Exception e1) {
                e1.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
            }
        } else if (QBConstants.HELP.equals(cmd)) {
            String imgLocation = "/_Help.gif";
            // URL imageURL = MainDataViewerFrame.class.getResource(imgLocation);
            JOptionPane.showMessageDialog(_mD.get_jf(), "Help Content Under Development!", "Help", JOptionPane.INFORMATION_MESSAGE);
        } else if (cmd.equalsIgnoreCase("checkcolumns")) {
            try {
                System.out.println(" the query is " + ((Querypanel) _mD.get_aryList().get(_mD.get_tPane().getSelectedIndex())).get_queryBuilder().getQueryModel().toString().toUpperCase());
                String query = ((Querypanel) _mD.get_aryList().get(_mD.get_tPane().getSelectedIndex())).get_queryBuilder().getQueryModel().toString().toUpperCase();
                String domainName = _mD.get_tPane().getTitleAt(selectedIndex).substring(0, 2);
                String returnedQuery = new CaDataViewHelper(_mD, domainName).processColumns(query, _mD.getSaveFile());
                System.out.println("returned query is " + returnedQuery);
                final QueryModel qm = SQLParser.toQueryModel(returnedQuery.toString());
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ((Querypanel) _mD.get_aryList().get(_mD.get_tPane().getSelectedIndex())).get_queryBuilder().setQueryModel(qm);
                    }
                });
            } catch (IOException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else if ("validate".equalsIgnoreCase(cmd)) {
            {// third button clicked
                final Dialog _queryWaitDialog = new Dialog(_mD.get_jf());
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            String _sqlSTR = (((Querypanel) _mD.get_aryList().get(_mD.get_tPane().getSelectedIndex())).get_queryBuilder()).getQueryModel().toString().toUpperCase();
                            ResultSet rs = _mD.get_con().createStatement().executeQuery(_sqlSTR);
                            //                        DatabaseTest _dbtest = new DatabaseTest(_mD.get_con(), _sqlSTR);
                            //                        TableSorter sorter = new TableSorter(_dbtest.getQTM());
                            //                        JTable table = new JTable();//NEW
                            //                        sorter.setTableHeader(table.getTableHeader());//ADDED THIS
                            //                        JScrollPane _jp4RunSQL = new JScrollPane(table);
                            final JDialog _tmpDialog = new JDialog(_mD.get_jf(), true);
                            _tmpDialog.setTitle("Validate Query Result");
                            _tmpDialog.setSize(400, 200);
                            _tmpDialog.setLocationRelativeTo(null);
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
                            _okBtn.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    _tmpDialog.dispose();
                                }
                            });
                            _okButPane.add(_okBtn);
                            LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
                            _okButPane.setBorder(lineBorder);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            _queryWaitDialog.dispose();
                            _tmpDialog.getContentPane().add(labelPan, BorderLayout.CENTER);
                            labelButton.add(_okButPane, BorderLayout.SOUTH);
                            _tmpDialog.getContentPane().add(labelButton, BorderLayout.SOUTH);
                            _tmpDialog.setVisible(true);
                            //  getContentPane().add(, BorderLayout.CENTER);
                        } catch (Exception e1) {
                            final JDialog _tmpDialog = new JDialog(_mD.get_jf(), true);
                            _tmpDialog.setLocationRelativeTo(null);
                            _tmpDialog.setTitle("Bad Query");
                            _tmpDialog.setLayout(new BorderLayout());
                            LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
                            JPanel _borderPane = new JPanel();
                            _borderPane.add(new JLabel("       Query did not execute due to:\n " + e1.getMessage()));
                            _borderPane.setBorder(lineBorder);
                            _tmpDialog.getContentPane().add(_borderPane, BorderLayout.CENTER);
                            JPanel _okButPane = new JPanel();
                            JButton _okBtn = new JButton("   OK   ");
                            _okBtn.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    _tmpDialog.dispose();
                                }
                            });
                            _okButPane.add(_okBtn);
                            // LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
                            _okButPane.setBorder(lineBorder);
                            _queryWaitDialog.dispose();
                            _tmpDialog.getContentPane().add(_okButPane, BorderLayout.SOUTH);
                            int length = e1.getMessage().length() + 50 + 425;
                            System.out.println("length-----------> " + length);
                            _tmpDialog.setSize(length, 100);
                            _tmpDialog.setVisible(true);
                        }
                    }
                }).start();
                _queryWaitDialog.setTitle("Query in Progress");
                _queryWaitDialog.setSize(350, 100);
                _queryWaitDialog.setLocationRelativeTo(null);
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

    private String removeSQLElements(String xmlStr) {
        if (xmlStr.indexOf("<sql") > 0) {
            int beginSQL = xmlStr.indexOf("<sql");
            xmlStr = xmlStr.substring(0, beginSQL);
            return xmlStr;
        } else {
            return xmlStr;
        }
    }

    private void loadTablesQuietly() {
        ArrayList tableList = (ArrayList) _mD.getTabsForDomains().get(_mD.get_tPane().getTitleAt(0).substring(0, 2));
        for (int i = 0; i < tableList.size(); i++) {
            StringTokenizer temp = new StringTokenizer(tableList.get(i).toString(), ".");
            String schema = temp.nextElement().toString();
            String table1 = temp.nextElement().toString();
            try {
                ((Querypanel) _mD.get_aryList().get(0)).loadTables(schema, table1);
            } catch (Exception e) {
                //e.printStackTrace();
                //throw e;
            }
        }
    }

    private void loadTablesQuietly(int pos) {
        ArrayList tableList = (ArrayList) _mD.getTabsForDomains().get(_mD.get_tPane().getTitleAt(pos).substring(0, 2));
        for (int i = 0; i < tableList.size(); i++) {
            StringTokenizer temp = new StringTokenizer(tableList.get(i).toString(), ".");
            String schema = temp.nextElement().toString();
            String table1 = temp.nextElement().toString();
            try {
                ((Querypanel) _mD.get_aryList().get(pos)).loadTables(schema, table1);
            } catch (Exception e) {
                //e.printStackTrace();
                //throw e;
            }
        }
    }

    private String markSelectedColumns(String domainName, String queryFromQueryPanel) {
        String returnedQuery = new CaDataViewHelper(_mD, domainName).processColumns(queryFromQueryPanel, _mD.getSaveFile());
        return returnedQuery;
    }
}

/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.9  2007/11/05 15:40:06  jayannah
 * Changed the message/wording
 *
 * Revision 1.8  2007/09/13 14:26:28  jayannah
 * handled the user choice if no is chosen
 *
 * Revision 1.7  2007/09/13 14:11:15  jayannah
 * Added a comment to the map file for the user not to change the sql statements
 *
 * Revision 1.6  2007/09/13 13:53:56  jayannah
 * Changes made to fix, window position, parameters during the launch of data viewer, handling of the toolbar buttons and to GEnerate the SQL when the user does not want to use the data viewer
 *
 * Revision 1.5  2007/09/11 15:33:25  jayannah
 * made changes for the window so that when the user clicks on x the control is passed to save all and exit button and panel reload does not cause map file corruption
 *
 * Revision 1.4  2007/08/16 18:53:55  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */

