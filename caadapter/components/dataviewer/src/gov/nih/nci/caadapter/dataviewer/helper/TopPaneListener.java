package gov.nih.nci.caadapter.dataviewer.helper;

import gov.nih.nci.caadapter.dataviewer.MainDataViewerFrame;
import gov.nih.nci.caadapter.dataviewer.util.Querypanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: Apr 27, 2007
 * Time: 5:01:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class TopPaneListener implements ChangeListener
{

    private MainDataViewerFrame mD;

    public TopPaneListener(MainDataViewerFrame _mD)
    {
        this.mD = _mD;
    }

    public void stateChanged(ChangeEvent evt)
    {
        JTabbedPane pane = (JTabbedPane) evt.getSource();
        int sel = pane.getSelectedIndex();
        //System.out.println("jtab title " + pane.getTitleAt(sel));
        if (!mD.get_alreadyFilled().contains(new Integer(sel)))
        {
            //this has to be a loop begin
            ArrayList tableList = null;
            try
            {
                tableList = (ArrayList) mD.getTabsForDomains().get(pane.getTitleAt(sel).substring(0, 2));
            } catch (Exception e)
            {
                JOptionPane.showMessageDialog(mD.get_jf(), e.getMessage().toString() + "\n Please restart the Application", "General Exception", JOptionPane.ERROR_MESSAGE);
            }
            for (int i = 0; i < tableList.size(); i++)
            {
                StringTokenizer temp = new StringTokenizer(tableList.get(i).toString(), ".");
                String schema = temp.nextElement().toString();
                String table = temp.nextElement().toString();
                ((Querypanel) mD.get_aryList().get(mD.get_tPane().getSelectedIndex())).loadTables(schema, table);
            }
            mD.get_alreadyFilled().add(new Integer(mD.get_tPane().getSelectedIndex()));
            mD.get_lPane().setSelectedIndex(0);
            mD.get_jf().repaint();
        }
        mD.get_lPane().setSelectedIndex(0);
    }
}