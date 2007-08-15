package gov.nih.nci.caadapter.dataviewer.helper;

import gov.nih.nci.caadapter.dataviewer.MainDataViewerFrame;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: Apr 27, 2007
 * Time: 4:13:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class DialogClosingListener implements WindowListener {
    private MainDataViewerFrame mainDataViewerFrame = null;

    public DialogClosingListener(MainDataViewerFrame mainDataViewerFrame) {
        this.mainDataViewerFrame = mainDataViewerFrame;
    }

    public void windowClosing(java.awt.event.WindowEvent evt) {
        if (!(mainDataViewerFrame.getSqlSaveHashMap().size() == mainDataViewerFrame.get_tPane().getTabCount())) {
            JOptionPane.showMessageDialog(mainDataViewerFrame.get_jf(), "Please save all the SQL statements in the panels before exiting", "Save exception", JOptionPane.INFORMATION_MESSAGE);
        } else if (!mainDataViewerFrame.isSQLStmtSaved()) {
            Object[] options = {"Yes", "No"};
            int n = JOptionPane.showOptionDialog(mainDataViewerFrame.get_jf(), " SQL are not saved, Do you want to save?", "Save all queries confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
            if (n == 0) {
                mainDataViewerFrame.getDialog().removeAll();
                BufferedWriter out = null;
                try {
                    if (mainDataViewerFrame.getSaveFile().exists())
                        mainDataViewerFrame.getSaveFile().delete();
                    out = new BufferedWriter(new FileWriter(mainDataViewerFrame.getSaveFile()));
                    //remove the all sql elements
                    String tempStr = removeSQLElements(mainDataViewerFrame.getXmlString());
                    //remove all sql elements
                    out.write(tempStr);
                    Set set = mainDataViewerFrame.getSqlSaveHashMap().keySet();
                    for (Iterator iterator = set.iterator(); iterator.hasNext();) {
                        String domainName = (String) iterator.next();
                        String sql4Domain = (String) mainDataViewerFrame.getSqlSaveHashMap().get(domainName);
                        out.write("\n<sql name=\"" + domainName + "\">");
                        out.write("" + sql4Domain);
                        out.write("</sql>");
                    }
                    out.append("\n</mapping>");
                    JOptionPane.showMessageDialog(mainDataViewerFrame.get_jf(), "The file \"" + mainDataViewerFrame.getSaveFile().getName() + "\" is saved successfully with the mapping and generated SQL information", "Mapping file is saved", JOptionPane.INFORMATION_MESSAGE);
                    mainDataViewerFrame.getDialog().dispose();
                    mainDataViewerFrame.getTransformBut().setEnabled(true);
                } catch (Exception e) {
                } finally {
                    try {
                        if (out != null)
                            out.close();
                    } catch (IOException ee) {
                        ee.printStackTrace();
                    }
                }
            }
            mainDataViewerFrame.getDialog().dispose();
        } else {
            mainDataViewerFrame.getDialog().dispose();
        }
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
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
}