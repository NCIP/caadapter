package gov.nih.nci.caadapter.dataviewer;

import gov.nih.nci.caadapter.dataviewer.helper.DialogClosingListener;
import gov.nih.nci.caadapter.dataviewer.helper.DialogComponentListener;
import gov.nih.nci.caadapter.dataviewer.helper.TopPaneListener;
import gov.nih.nci.caadapter.dataviewer.util.CaDataViewHelper;
import gov.nih.nci.caadapter.dataviewer.util.QBAddButtons;
import gov.nih.nci.caadapter.dataviewer.util.Querypanel;
import gov.nih.nci.caadapter.dataviewer.util.SDTMDomainLookUp;
import gov.nih.nci.caadapter.ui.mapping.sdtm.Database2SDTMMappingPanel;
import nickyb.sqleonardo.querybuilder.QueryModel;
import nickyb.sqleonardo.querybuilder.syntax.SQLParser;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;

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

    public ArrayList get_aryList() {
        return arrayList;
    }

    public JTabbedPane get_tPane() {
        return tabbedPane;
    }

    public Hashtable getOpenActionQueriesList() {
        return openActionQueriesList;
    }

    /*
        1. Called by the opendataviewer helper; during times times when the dataviewer needs to be opened after the mapping is complete;
        2. Called by saveassdtmaction;
     */
    public MainDataViewerFrame(boolean openDBMap, Dialog _ref, Hashtable table, HashSet tableColums, Hashtable connectionParams, File saveFile, String out, Hashtable sqlTables, JButton transFormBut) {
        this.columnsForTables = tableColums;
        this.saveFile = saveFile;
        this.xmlString = out;
        this.sqls4Domain = sqlTables;
        this.transformBut = transFormBut;
        schema = connectionParams.get("SCHEMA").toString();
        dataViewerFrame = new JFrame();
        dataViewerFrame.setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
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
            Querypanel _qp = new Querypanel(connection, this, schema, tmp);
            arrayList.add(_qp);
            tabbedPane.addTab(tmp + "-" + tmp1, _qp);
            dataViewerFrame.add(tabbedPane, BorderLayout.CENTER);
        }
        tabbedPane.addChangeListener(new TopPaneListener(this));
        dataViewerFrame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        dataViewerFrame.setSize(1100, 950);
        dataViewerFrame.setLocation(85, 30);
        JPanel jp_status = new JPanel();
        jp_status.setLayout(new BorderLayout());
        jp_status.add(new JLabel(connectionParams.get("UserID").toString() + "@" + connectionParams.get("URL").toString()), BorderLayout.CENTER);
        jp_status.setBorder(new BevelBorder(BevelBorder.LOWERED));
        TextArea _showDefaultValues = new TextArea();
        _showDefaultValues.setEditable(false);
        dataViewerFrame.add(jp_status, BorderLayout.SOUTH);
        dataViewerFrame.setTitle("Data Viewer for caAdapter");
        dataViewerFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        dataViewerFrame.addWindowListener(new DialogClosingListener(this));
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            ((Querypanel) tabbedPane.getComponentAt(0)).get_queryBuilder().setQueryModel(qm);
        } else {
            ArrayList tableList = (ArrayList) this.getTabsForDomains().get(tabbedPane.getTitleAt(0).substring(0, 2));
            for (int i = 0; i < tableList.size(); i++) {
                StringTokenizer temp = new StringTokenizer(tableList.get(i).toString(), ".");
                String schema = temp.nextElement().toString();
                String table1 = temp.nextElement().toString();
                ((Querypanel) this.get_aryList().get(0)).loadTables(schema, table1);
            }
            try {
                String query = ((Querypanel) tabbedPane.getComponentAt(0)).get_queryBuilder().getQueryModel().toString().toUpperCase();
                String returnedQuery = new CaDataViewHelper().processColumns(tabbedPane.getTitleAt(0).substring(0, 2), query, this.getSaveFile());
                final QueryModel qm2 = SQLParser.toQueryModel(returnedQuery);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ((Querypanel) tabbedPane.getComponentAt(0)).get_queryBuilder().setQueryModel(qm2);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.get_alreadyFilled().add(new Integer(0));
        if (openDBMap) {
            qbAddButtons.getSaveButton().setEnabled(true);
            setSQLStmtSaved(true);
            Enumeration en = sqlTables.keys();
            while (en.hasMoreElements()) {
                String domainName = (String) en.nextElement();
                sqlSaveHashMap.put(domainName, sqlTables.get(domainName));
            }
        }
    }
}