package gov.nih.nci.caadapter.dataviewer.helper;

import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
import gov.nih.nci.caadapter.dataviewer.MainDataViewerFrame;
import gov.nih.nci.caadapter.dataviewer.util.Querypanel;
import gov.nih.nci.caadapter.dataviewer.util.SQLFileFilter;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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

    private ArrayList returnSQLForDomain;

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
            returnSQLForDomain = new ArrayList();
            int _numberOfTabs = mainDataViewerFrame.get_tPane().getTabCount();
            for (int i = 0; i < _numberOfTabs; i++)
            {
                mainDataViewerFrame.get_tPane().setSelectedIndex(i);
                String domainName = mainDataViewerFrame.get_tPane().getTitleAt(i);
                //saveSQLPanel(domainName);
                String _sqlSTR = (((Querypanel) mainDataViewerFrame.get_aryList().get(mainDataViewerFrame.get_tPane().getSelectedIndex())).get_queryBuilder()).getModel().toString(false).toUpperCase();
                returnSQLForDomain.add(domainName + "~" + _sqlSTR);
            }
            //int _numberOfTabs = mainDataViewerFrame.get_tPane().getTabCount();
            for (int i = 0; i < _numberOfTabs; i++)
            {
                mainDataViewerFrame.get_tPane().setSelectedIndex(i);
                ((Querypanel) mainDataViewerFrame.get_aryList().get(mainDataViewerFrame.get_tPane().getSelectedIndex())).get_queryBuilder();
            }
            mainDataViewerFrame.setSQLForDomains(returnSQLForDomain);
            mainDataViewerFrame.getDialog().removeAll();
            BufferedWriter out = null;
            try
            {
                if (mainDataViewerFrame.getSaveFile().exists())
                    mainDataViewerFrame.getSaveFile().delete();
                out = new BufferedWriter(new FileWriter(mainDataViewerFrame.getSaveFile()));
                //remove the all sql elements
                String tempStr = removeSQLElements(mainDataViewerFrame.getXmlString());
                //remove all sql elements
                out.write(tempStr);
                EmptyStringTokenizer str = null;
                for (int i = 0; i < returnSQLForDomain.size(); i++)
                {
                    str = new EmptyStringTokenizer(returnSQLForDomain.get(i).toString(), "~");
                    out.write("\n<sql name=\"" + str.getTokenAt(0).substring(0, 2) + "\">");
                    out.write("" + str.getTokenAt(1));
                    out.write("</sql>");
                }
                out.append("\n</mapping>");
                JOptionPane.showMessageDialog(mainDataViewerFrame.get_jf(), "The file \"" + mainDataViewerFrame.getSaveFile().getName() + "\" is saved successfully with the mapping and generated SQL information", "Mapping file is saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e)
            {
                e.printStackTrace();
                JOptionPane.showMessageDialog(mainDataViewerFrame.get_jf(), "The file \"" + mainDataViewerFrame.getSaveFile().getName() + "\" was not saved dure to " + e.getLocalizedMessage(), "Mapping file is not saved", JOptionPane.ERROR_MESSAGE);
            } finally
            {
                try
                {
                    if (out != null)
                        out.close();
                } catch (IOException e)
                {
                    e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
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

    private String removeSQLElements(String xmlStr)
    {
        if (xmlStr.indexOf("<sql") > 0)
        {
            int beginSQL = xmlStr.indexOf("<sql");
            xmlStr = xmlStr.substring(0, beginSQL);
            return xmlStr;
        } else
        {
            return xmlStr;
        }
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