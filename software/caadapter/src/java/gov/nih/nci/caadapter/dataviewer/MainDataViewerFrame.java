/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dataviewer;

import gov.nih.nci.caadapter.dataviewer.helper.DialogClosingListener;
import gov.nih.nci.caadapter.dataviewer.helper.DialogComponentListener;
import gov.nih.nci.caadapter.dataviewer.helper.TopPaneListener;
import gov.nih.nci.caadapter.dataviewer.util.CaDataViewHelper;
import gov.nih.nci.caadapter.dataviewer.util.QBAddButtons;
import gov.nih.nci.caadapter.dataviewer.util.Querypanel;
import gov.nih.nci.caadapter.dataviewer.util.SDTMDomainLookUp;
import nickyb.sqleonardo.querybuilder.QueryModel;
import nickyb.sqleonardo.querybuilder.syntax.SQLParser;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.util.*;

/**
 * This is data viewer main window. The RDS module calls this class with arguments to
 * show tables to the user
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.18 $
 *          $Date: 2008-06-09 19:53:50 $
 */
public class MainDataViewerFrame {
    private JFrame dataViewerFrame = null;
    private String schema = null;
    private Hashtable tabsForDomains = null;
    private Hashtable sqls4Domain = null;
    private JTabbedPane tabbedPane = null;
    private JToolBar toolBar = null;
    private Connection connection = null;
    private File saveFile = null;
    private String xmlString = null;
    private ArrayList sqlsForDomain = null;
    private Hashtable openActionQueriesList = null;
    private JButton transformBut = null;
    private QBAddButtons qbAddButtons = null;
    private HashSet columnsForTables = null;
    private boolean isSQLStmtSaved = false;
    private boolean isOpenMapAction = false;
    private HashMap changedPanels = null;
    private HashMap sqlListWODataViewer = null;
    //private Database2SDTMMappingPanel mainPanel = null;
    /*
     These variables are used by other classes
     */
    private HashSet alreadyFilled = new HashSet();
    private ArrayList arrayList = new ArrayList();
    private java.util.HashMap sqlSaveHashMap = new HashMap();

    public boolean isOpenMapAction() {
        return isOpenMapAction;
    }

    public JButton getTransformBut() {
        return transformBut;
    }

    public boolean isSQLStmtSaved() {
        return isSQLStmtSaved;
    }

    public void setSQLStmtSaved(boolean SQLStmtSaved) {
        isSQLStmtSaved = SQLStmtSaved;
    }

    public QBAddButtons getQbAddButtons() {
        return qbAddButtons;
    }

    public HashMap getSqlSaveHashMap() {
        return sqlSaveHashMap;
    }

    public Hashtable getSqls4Domain() {
        return sqls4Domain;
    }

    public HashSet getColumnsForTables() {
        return columnsForTables;
    }

    public JFrame getDialog() {
        return dataViewerFrame;
    }

    public Connection get_con() {
        if (connection != null)
            return connection;
        else
            return null;
    }

    public Hashtable getTabsForDomains() {
        return tabsForDomains;
    }

    public String getSchema() {
        return schema;
    }

    public File getSaveFile() {
        return saveFile;
    }

    public String getXmlString() {
        return xmlString;
    }

    public void setSQLForDomains(ArrayList object) {
        this.sqlsForDomain = object;
    }

    public ArrayList getAllSQLForDomains() {
        return sqlsForDomain;
    }

    public HashSet get_alreadyFilled() {
        return alreadyFilled;
    }

    public JFrame get_jf() {
        return dataViewerFrame;
    }

    public HashMap getChangedPanels() {
        return changedPanels;
    }

    public ArrayList get_aryList() {
        return arrayList;
    }

    public JTabbedPane get_tPane() {
        return tabbedPane;
    }

    public Hashtable getOpenActionQueriesList() {
        return openActionQueriesList;
    }

    public HashMap getSqlListWODataViewer() {
        return sqlListWODataViewer;
    }

    /*
       1. Called by the opendataviewer helper; during times times when the dataviewer needs to be opened after the mapping is complete;
       2. Called by saveassdtmaction;
    */
    public MainDataViewerFrame(boolean isOpenDBmap, Dialog _ref, Hashtable table, HashSet tableColums, Hashtable connectionParams, File saveFile, String out, Hashtable sqlTables, JButton transFormBut, boolean quiet) throws Exception {
        try {
            this.sqlListWODataViewer = new HashMap();
            this.isOpenMapAction = isOpenDBmap;
            this.columnsForTables = tableColums;
            this.saveFile = saveFile;
            this.xmlString = out;
            this.sqls4Domain = sqlTables;
            this.transformBut = transFormBut;
            schema = connectionParams.get("SCHEMA").toString();
            dataViewerFrame = new JFrame();
            dataViewerFrame.setLayout(new BorderLayout());
            tabbedPane = new JTabbedPane();
            changedPanels = new HashMap();
            TitledBorder titleTop = BorderFactory.createTitledBorder("SQL Data Panel(s)");
            tabbedPane.setBorder(titleTop);
            toolBar = new JToolBar("");
            qbAddButtons = new QBAddButtons(this);
            qbAddButtons.addButtons(toolBar);
            dataViewerFrame.add(toolBar, BorderLayout.PAGE_START);
            // Add the panes after here
            dataViewerFrame.addComponentListener(new DialogComponentListener(this));
            connection = (Connection) connectionParams.get("connection");
            this.tabsForDomains = table;
            Enumeration enum1 = table.keys();
            // Register a change listener
            while (enum1.hasMoreElements()) {
                String tmp = enum1.nextElement().toString();
                String tmp1 = new SDTMDomainLookUp().getDescription(tmp);
                Querypanel _qp = null;
                try {
                    _qp = new Querypanel(connection, schema);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
                arrayList.add(_qp);
                tabbedPane.addTab(tmp + "-" + tmp1, _qp);
                dataViewerFrame.add(tabbedPane, BorderLayout.CENTER);
            }
            tabbedPane.addChangeListener(new TopPaneListener(this));
            dataViewerFrame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
            dataViewerFrame.setSize(1100, 950);
            //dataViewerFrame.setLocationRelativeTo(null);
            JPanel jp_status = new JPanel();
            jp_status.setLayout(new BorderLayout());
            jp_status.add(new JLabel(connectionParams.get("UserID").toString() + "@" + connectionParams.get("URL").toString()), BorderLayout.CENTER);
            jp_status.setBorder(new BevelBorder(BevelBorder.LOWERED));
            TextArea _showDefaultValues = new TextArea();
            _showDefaultValues.setEditable(false);
            dataViewerFrame.add(jp_status, BorderLayout.SOUTH);
            dataViewerFrame.setTitle("Data Viewer for caAdapter");
            dataViewerFrame.addWindowListener(new DialogClosingListener(this));
            dataViewerFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            if (quiet)
                dataViewerFrame.setVisible(false);
            else
                dataViewerFrame.setVisible(true);
            try {
                _ref.dispose();
            } catch (Exception e) {
            }
            if (sqlTables != null && sqlTables.size() > 0) {
                String query = (String) sqlTables.get(tabbedPane.getTitleAt(0).substring(0, 2));
                QueryModel qm = null;
                try {
                    qm = SQLParser.toQueryModel(query);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
                ((Querypanel) tabbedPane.getComponentAt(0)).get_queryBuilder().setQueryModel(qm);
            } else {
                ArrayList tableList = (ArrayList) this.getTabsForDomains().get(tabbedPane.getTitleAt(0).substring(0, 2));
                for (int i = 0; i < tableList.size(); i++) {
                    StringTokenizer temp = new StringTokenizer(tableList.get(i).toString(), ".");
                    String schema = temp.nextElement().toString();
                    String table1 = temp.nextElement().toString();
                    try {
                        ((Querypanel) this.get_aryList().get(0)).loadTables(schema, table1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw e;
                    }
                }
                try {
                    String query = ((Querypanel) tabbedPane.getComponentAt(0)).get_queryBuilder().getQueryModel().toString().toUpperCase();
                    String returnedQuery = new CaDataViewHelper(this, tabbedPane.getTitleAt(0).substring(0, 2)).processColumns(query, this.getSaveFile());
                    final QueryModel qm2 = SQLParser.toQueryModel(returnedQuery);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            ((Querypanel) tabbedPane.getComponentAt(0)).get_queryBuilder().setQueryModel(qm2);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
            this.get_alreadyFilled().add(new Integer(0));
            if (isOpenMapAction) {
                qbAddButtons.getSaveButton().setEnabled(true);
                setSQLStmtSaved(true);
                Enumeration en = null;
                try {
                    en = sqlTables.keys();
                    while (en.hasMoreElements()) {
                        String domainName = (String) en.nextElement();
                        sqlSaveHashMap.put(domainName, sqlTables.get(domainName));
                    }
                } catch (Exception e) {
                }
            }
        }
        catch (Exception eee) {
            eee.printStackTrace();
        }
    }
}

/**
 Change History
 $Log: not supported by cvs2svn $
 Revision 1.17  2007/09/27 20:45:43  jayannah
 commented the data viewer location so that the menu does not flip over the top the monitor

 Revision 1.16  2007/09/13 14:07:59  jayannah
 swallowed an exception

 Revision 1.15  2007/09/13 13:53:56  jayannah
 Changes made to fix, window position, parameters during the launch of data viewer, handling of the toolbar buttons and to GEnerate the SQL when the user does not want to use the data viewer

 Revision 1.14  2007/09/11 16:48:52  jayannah
 to over come build issues

 Revision 1.13  2007/09/11 15:33:25  jayannah
 made changes for the window so that when the user clicks on x the control is passed to save all and exit button and panel reload does not cause map file corruption

 Revision 1.12  2007/08/28 14:44:58  jayannah
 removed the system.outs

 Revision 1.11  2007/08/16 18:53:55  jayannah
 Reformatted and added the Comments and the log tags for all the files

 Revision 1.10  2007/08/16 18:13:26  jayannah
 Change history log test
 */