/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dataviewer.helper;

import gov.nih.nci.caadapter.dataviewer.MainDataViewerFrame;
import gov.nih.nci.caadapter.dataviewer.util.Querypanel;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * This class is a listener to save the map file during a window close event
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.10 $
 *          $Date: 2008-06-09 19:53:50 $
 */
public class DialogClosingListener implements WindowListener {
    private MainDataViewerFrame mainDataViewerFrame = null;

    public DialogClosingListener(MainDataViewerFrame mainDataViewerFrame) {
        this.mainDataViewerFrame = mainDataViewerFrame;
    }

    public void windowClosing(java.awt.event.WindowEvent evt) {

        JOptionPane.showMessageDialog(mainDataViewerFrame.get_jf(), "Please use \"Save & Exit or Exit w/o Save\" button to exit Data Viewer", "Close Data Viewer...", JOptionPane.INFORMATION_MESSAGE);
//        //check if the sql is modified
//        boolean needsSave = false;
//        HashMap sqlHashMap = mainDataViewerFrame.getSqlSaveHashMap();
//        //go through each panel and get the queries
//        for (int i = 0; i < mainDataViewerFrame.get_tPane().getTabCount(); i++) {
//            if (!mainDataViewerFrame.get_alreadyFilled().contains(i)) {
//                mainDataViewerFrame.get_tPane().setSelectedIndex(i);
//                loadTablesQuietly();
//            }
//            String _sqlSTR = (((Querypanel) mainDataViewerFrame.get_aryList().get(i)).get_queryBuilder()).getQueryModel().toString().toUpperCase();
//            String domainName = mainDataViewerFrame.get_tPane().getTitleAt(i).substring(0, 2);
//            String _openSQLFromHashMap = (String) sqlHashMap.get(domainName);
//            System.out.println("comparing");
//            String _sqlSTRREP = _sqlSTR.replaceAll(" ", "");
//            _openSQLFromHashMap = _openSQLFromHashMap.replaceAll(" ", "");
//            System.out.println(_sqlSTRREP);
//            System.out.println(_openSQLFromHashMap);
//            if (!_sqlSTRREP.equalsIgnoreCase(_openSQLFromHashMap) && (!_sqlSTRREP.equalsIgnoreCase("SELECT"))) {
//                Object[] options = {"Yes", "No"};
//                //JOptionPane.showMessageDialog(mainDataViewerFrame.get_jf(), "The SQL for " + domainName + " is modified", "SQL's Modified...", JOptionPane.INFORMATION_MESSAGE);
//                int n = JOptionPane.showOptionDialog(mainDataViewerFrame.get_jf(), "\"" + domainName + "\" has been modified, Do you want to save it now?", "Save the map file confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
//                if (n == 0) {
//                    sqlHashMap.remove(domainName);
//                    sqlHashMap.put(domainName, _sqlSTR);
//                    needsSave = true;
//                    continue;
//                } else {
//                    continue;
//                }
//            }
//        }
//        //so, if the the users response are caught even if one of them is true then go save the file;otherwise JUSTEXIT!!!
//        if (needsSave) {
//            try {
//                if (mainDataViewerFrame.getSaveFile().exists())
//                    mainDataViewerFrame.getSaveFile().delete();
//                BufferedWriter out = new BufferedWriter(new FileWriter(mainDataViewerFrame.getSaveFile()));
//                //remove the all sql elements
//                String tempStr = removeSQLElements(mainDataViewerFrame.getXmlString());
//                out.write(tempStr);
//                Set set = mainDataViewerFrame.getSqlSaveHashMap().keySet();
//                for (Iterator iterator = set.iterator(); iterator.hasNext();) {
//                    String domainName = (String) iterator.next();
//                    String sql4Domain = (String) mainDataViewerFrame.getSqlSaveHashMap().get(domainName);
//                    out.write("\n<sql name=\"" + domainName + "\">");
//                    out.write("" + sql4Domain);
//                    out.write("</sql>");
//                }
//                out.append("\n</mapping>");
//                out.close();
//                JOptionPane.showMessageDialog(mainDataViewerFrame.get_jf(), "The file \"" + mainDataViewerFrame.getSaveFile().getName() + "\" is saved successfully with the mapping and generated SQL information", "Mapping file is saved", JOptionPane.INFORMATION_MESSAGE);
//            } catch (IOException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//        }
//        mainDataViewerFrame.getDialog().dispose();
        //
//        if (!(mainDataViewerFrame.getSqlSaveHashMap().size() == mainDataViewerFrame.get_tPane().getTabCount())) {
//            JOptionPane.showMessageDialog(mainDataViewerFrame.get_jf(), "Please save all the SQL statements in the panels before exiting", "Save exception", JOptionPane.INFORMATION_MESSAGE);
//        } else if (!mainDataViewerFrame.isSQLStmtSaved()) {
//            Object[] options = {"Yes", "No"};
//            int n = JOptionPane.showOptionDialog(mainDataViewerFrame.get_jf(), "\"" + mainDataViewerFrame.getSaveFile() + "\" map file is not saved, Do you want to save it now?", "Save the map file confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
//            if (n == 0) {
//                mainDataViewerFrame.getDialog().removeAll();
//                BufferedWriter out = null;
//                try {
//                    if (mainDataViewerFrame.getSaveFile().exists())
//                        mainDataViewerFrame.getSaveFile().delete();
//                    out = new BufferedWriter(new FileWriter(mainDataViewerFrame.getSaveFile()));
//                    //remove the all sql elements
//                    String tempStr = removeSQLElements(mainDataViewerFrame.getXmlString());
//                    //remove all sql elements
//                    out.write(tempStr);
//                    Set set = mainDataViewerFrame.getSqlSaveHashMap().keySet();
//                    for (Iterator iterator = set.iterator(); iterator.hasNext();) {
//                        String domainName = (String) iterator.next();
//                        String sql4Domain = (String) mainDataViewerFrame.getSqlSaveHashMap().get(domainName);
//                        out.write("\n<sql name=\"" + domainName + "\">");
//                        out.write("" + sql4Domain);
//                        out.write("</sql>");
//                    }
//                    out.append("\n</mapping>");
//                    JOptionPane.showMessageDialog(mainDataViewerFrame.get_jf(), "The file \"" + mainDataViewerFrame.getSaveFile().getName() + "\" is saved successfully with the mapping and generated SQL information", "Mapping file is saved", JOptionPane.INFORMATION_MESSAGE);
//                    mainDataViewerFrame.getDialog().dispose();
//                    mainDataViewerFrame.getTransformBut().setEnabled(true);
//                } catch (Exception e) {
//                } finally {
//                    try {
//                        if (out != null)
//                            out.close();
//                    } catch (IOException ee) {
//                        ee.printStackTrace();
//                    }
//                }
//            }
//            mainDataViewerFrame.getDialog().dispose();
//        } else {
//            mainDataViewerFrame.getDialog().dispose();
//        }
    }

    public void windowActivated
            (WindowEvent
                    e) {
    }

    public void windowClosed
            (WindowEvent
                    e) {
    }

    public void windowDeactivated
            (WindowEvent
                    e) {
    }

    public void windowDeiconified
            (WindowEvent
                    e) {
    }

    public void windowIconified
            (WindowEvent
                    e) {
    }

    public void windowOpened
            (WindowEvent
                    e) {
    }

    private String removeSQLElements
            (String
                    xmlStr) {
        if (xmlStr.indexOf("<sql") > 0) {
            int beginSQL = xmlStr.indexOf("<sql");
            xmlStr = xmlStr.substring(0, beginSQL);
            return xmlStr;
        } else {
            return xmlStr;
        }
    }

    private void loadTablesQuietly() {
        ArrayList tableList = (ArrayList) mainDataViewerFrame.getTabsForDomains().get(mainDataViewerFrame.get_tPane().getTitleAt(0).substring(0, 2));
        for (int i = 0; i < tableList.size(); i++) {
            StringTokenizer temp = new StringTokenizer(tableList.get(i).toString(), ".");
            String schema = temp.nextElement().toString();
            String table1 = temp.nextElement().toString();
            try {
                ((Querypanel) mainDataViewerFrame.get_aryList().get(0)).loadTables(schema, table1);
            } catch (Exception e) {
                //e.printStackTrace();
                //throw e;
            }
        }
    }
}

/*
    Change History
    $Log: not supported by cvs2svn $
    Revision 1.9  2007/10/16 14:10:18  jayannah
    Changed the absolute path to getName during times when the pop up is displayed to the world;
    made changes so that the Tables cannot be mapped

    Revision 1.8  2007/09/13 13:53:56  jayannah
    Changes made to fix, window position, parameters during the launch of data viewer, handling of the toolbar buttons and to GEnerate the SQL when the user does not want to use the data viewer

    Revision 1.7  2007/09/11 15:33:25  jayannah
    made changes for the window so that when the user clicks on x the control is passed to save all and exit button and panel reload does not cause map file corruption

    Revision 1.6  2007/08/17 15:53:23  jayannah
    cosmetic changes for dialog closing listener
    handled the cancel request from the user., previously it was going back and loading the defing xml which is not required

    Revision 1.5  2007/08/16 18:53:55  jayannah
    Reformatted and added the Comments and the log tags for all the files

 */