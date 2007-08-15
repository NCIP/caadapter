package gov.nih.nci.caadapter.dataviewer.helper;

import gov.nih.nci.caadapter.dataviewer.MainDataViewerFrame;
import gov.nih.nci.caadapter.dataviewer.util.Querypanel;
import nickyb.sqleonardo.querybuilder.QueryModel;
import nickyb.sqleonardo.querybuilder.syntax.SQLParser;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: May 23, 2007
 * Time: 10:55:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class TopPaneListenerOpenAction implements ChangeListener
{

    private MainDataViewerFrame mD;

    public TopPaneListenerOpenAction(MainDataViewerFrame _mD)
    {
        this.mD = _mD;
    }

    public void stateChanged(ChangeEvent evt)
    {
        JTabbedPane pane = (JTabbedPane) evt.getSource();
        int sel = pane.getSelectedIndex();
        //System.out.println("jtab title " + pane.getTitleAt(sel));
        mD.get_jf().repaint();
        if (!mD.get_alreadyFilled().contains(new Integer(sel)))
        {
            /*
                Get the domain name (meaning the first two characters of the domain name from the hashtable)
             */
            try
            {
                String queryFromList = mD.getOpenActionQueriesList().get(pane.getTitleAt(sel).substring(0, 2)).toString();
                QueryModel qm = SQLParser.toQueryModel(queryFromList);
                int selected = mD.get_tPane().getSelectedIndex();
                Querypanel panel = (Querypanel) mD.get_aryList().get(selected);
                panel.get_queryBuilder().setQueryModel(qm);
                //((Querypanel) mD.get_aryList().get(mD.get_tPane().getSelectedIndex())).get_queryBuilder().setModel(qm);
                //((Querypanel) mD.get_aryList().get(mD.get_tPane().getSelectedIndex())).get_queryBuilder().getDiagram().getTemporany().callResize();
                //System.out.println("The number of components are "+((Querypanel) mD.get_aryList().get(mD.get_tPane().getSelectedIndex())).get_queryBuilder().getDiagram().getComponents().length);
            } catch (Exception e)
            {
                e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
            }
            mD.get_alreadyFilled().add(new Integer(mD.get_tPane().getSelectedIndex()));
            //mD.get_lPane().setSelectedIndex(0);
            mD.get_jf().repaint();
        }
       // mD.get_lPane().setSelectedIndex(0);
    }
}
