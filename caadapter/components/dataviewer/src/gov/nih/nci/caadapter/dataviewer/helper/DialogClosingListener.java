package gov.nih.nci.caadapter.dataviewer.helper;

import gov.nih.nci.caadapter.dataviewer.util.SQLFileFilter;
import gov.nih.nci.caadapter.dataviewer.util.Querypanel;
import gov.nih.nci.caadapter.dataviewer.MainDataViewerFrame;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: Apr 27, 2007
 * Time: 4:13:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class DialogClosingListener implements WindowListener
{

    private MainDataViewerFrame mainDataViewerFrame = null;

    public DialogClosingListener(MainDataViewerFrame mainDataViewerFrame)
    {
        this.mainDataViewerFrame = mainDataViewerFrame;
    }

    public void windowClosing(java.awt.event.WindowEvent evt)
    {
        Object[] options = {"Yes", "No"};
        int n = JOptionPane.showOptionDialog(mainDataViewerFrame.get_jf(), " Do you want to save the SQL statements?", "Save all queries confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        if (n == 0)
        {
            int _numberOfTabs = mainDataViewerFrame.get_tPane().getTabCount();
            for (int i = 0; i < _numberOfTabs; i++)
            {
                mainDataViewerFrame.get_tPane().setSelectedIndex(i);
                String domainName = mainDataViewerFrame.get_tPane().getTitleAt(i);
                saveSQLPanel(domainName);
            }
        }
        int _numberOfTabs = mainDataViewerFrame.get_tPane().getTabCount();
        for (int i = 0; i < _numberOfTabs; i++)
        {
            mainDataViewerFrame.get_tPane().setSelectedIndex(i);
            ((Querypanel) mainDataViewerFrame.get_aryList().get(mainDataViewerFrame.get_tPane().getSelectedIndex())).get_queryBuilder();
        }
        //        try
        //        {
        //                if(mainDataViewerFrame.get_con()!=null)
        //                    mainDataViewerFrame.get_con().close();
        //        } catch (SQLException e)
        //        {
        //            e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
        //        }
        mainDataViewerFrame.getDialog().removeAll();
        mainDataViewerFrame.getDialog().dispose();

    }

    public void windowActivated(WindowEvent e)
    {
    }

    public void windowClosed(WindowEvent e)
    {
    }

    public void windowDeactivated(WindowEvent e)
    {
    }

    public void windowDeiconified(WindowEvent e)
    {
    }

    public void windowIconified(WindowEvent e)
    {
    }

    public void windowOpened(WindowEvent e)
    {
    }

    private boolean saveSQLPanel(String domain)
    {
        //parse the mapping file
        SQLFileFilter filter = new SQLFileFilter();
        filter.setDescription("Save " + domain);
        mainDataViewerFrame.getSaveSQLFile().setName("Save ");
        filter.addExtension("sql");
        filter.setDescription("sql");
        mainDataViewerFrame.getSaveSQLFile().setFileFilter(filter);
        int returnVal = mainDataViewerFrame.getSaveSQLFile().showSaveDialog(mainDataViewerFrame.get_jf());
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            String _sqlFileLocation = mainDataViewerFrame.getSaveSQLFile().getSelectedFile().getAbsolutePath();
            // This is where a real application would open the file.
            System.out.println("_sqlFileLocation " + _sqlFileLocation);
            try
            {
                BufferedWriter out = new BufferedWriter(new FileWriter(_sqlFileLocation));
                String _sqlSTR = (((Querypanel) mainDataViewerFrame.get_aryList().get(mainDataViewerFrame.get_tPane().getSelectedIndex())).get_queryBuilder()).getModel().toString(true).toUpperCase();
                out.write(_sqlSTR);
                out.close();
            } catch (IOException ew)
            {
                JOptionPane.showMessageDialog(mainDataViewerFrame.get_jf(), ew.getMessage().toString(), "SQL Save Error", JOptionPane.ERROR_MESSAGE);
            }
            JOptionPane.showMessageDialog(mainDataViewerFrame.get_jf(), "      \"" + _sqlFileLocation + "\" sucessfully saved         ", "SQL Save Sucess", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
        return false;
    }
}