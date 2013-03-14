/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

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
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.8 $
 *          $Date: 2008-06-09 19:53:50 $
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
                            try {
                                ((Querypanel) mD.get_tPane().getComponentAt(_int)).get_queryBuilder().setQueryModel(qm);
                            } catch (Exception e) {
                                //System.out.println("No worries!! " + e.getMessage());
                            }
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
                            String returnedQuery = new CaDataViewHelper(mD, mD.get_tPane().getTitleAt(sel).substring(0, 2)).processColumns( query, mD.getSaveFile());
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
 * Revision 1.7  2007/10/10 19:48:18  jayannah
 * commented a System.out
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