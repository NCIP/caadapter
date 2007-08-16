package gov.nih.nci.caadapter.dataviewer.helper;

import gov.nih.nci.caadapter.dataviewer.MainDataViewerFrame;
import gov.nih.nci.caadapter.dataviewer.util.CaDataViewHelper;
import gov.nih.nci.caadapter.dataviewer.util.Querypanel;
import nickyb.sqleonardo.querybuilder.QueryModel;
import nickyb.sqleonardo.querybuilder.syntax.SQLParser;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * This class file handles the events generated from the data viewer top pane
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: jayannah $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.4 $
 *          $Date: 2007-08-16 18:53:55 $
 */
public class TopPaneListener implements ChangeListener {
    private MainDataViewerFrame mD;

    public TopPaneListener(MainDataViewerFrame _mD) {
        this.mD = _mD;
    }

    public void stateChanged(ChangeEvent evt) {
        JTabbedPane pane = (JTabbedPane) evt.getSource();
        final int sel = pane.getSelectedIndex();
        if (!mD.get_alreadyFilled().contains(new Integer(sel))) {
            if ((mD.getSqls4Domain() != null) && (mD.getSqls4Domain().size() > 0)) {
                try {
                    String query = (String) mD.getSqls4Domain().get(pane.getTitleAt(sel).substring(0, 2));
                    final int _int = sel;
                    final QueryModel qm = SQLParser.toQueryModel(query);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            ((Querypanel) mD.get_tPane().getComponentAt(_int)).get_queryBuilder().setQueryModel(qm);
                        }
                    });
                    mD.get_jf().repaint();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                final ArrayList tableList = (ArrayList) mD.getTabsForDomains().get(pane.getTitleAt(sel).substring(0, 2));
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        for (int i = 0; i < tableList.size(); i++) {
                            StringTokenizer temp = new StringTokenizer(tableList.get(i).toString(), ".");
                            String schema = temp.nextElement().toString();
                            String table = temp.nextElement().toString();
                            ((Querypanel) mD.get_aryList().get(mD.get_tPane().getSelectedIndex())).loadTables(schema, table);
                        }
                        // So, Mr.Thread finished loading; now go and check-mark the columns
                        try {
                            String query = ((Querypanel) mD.get_tPane().getComponentAt(sel)).get_queryBuilder().getQueryModel().toString().toUpperCase();
                            String returnedQuery = new CaDataViewHelper().processColumns(mD.get_tPane().getTitleAt(sel).substring(0, 2), query, mD.getSaveFile());
                            final QueryModel qm2 = SQLParser.toQueryModel(returnedQuery);
                            ((Querypanel) mD.get_tPane().getComponentAt(sel)).get_queryBuilder().setQueryModel(qm2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        mD.get_alreadyFilled().add(new Integer(mD.get_tPane().getSelectedIndex()));     
    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 */