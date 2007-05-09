package gov.nih.nci.caadapter.dataviewer;

import gov.nih.nci.caadapter.dataviewer.helper.DialogClosingListener;
import gov.nih.nci.caadapter.dataviewer.helper.DialogComponentListener;
import gov.nih.nci.caadapter.dataviewer.helper.LowerPaneListener;
import gov.nih.nci.caadapter.dataviewer.helper.TopPaneListener;
import gov.nih.nci.caadapter.dataviewer.util.SDTMDomainLookUp;
import gov.nih.nci.caadapter.dataviewer.util.Querypanel;
import gov.nih.nci.caadapter.dataviewer.util.QBAddButtons;
import gov.nih.nci.caadapter.sdtm.util.OpenDatabaseConnectionHelper;
import nickyb.sqleonardo.environment.Preferences;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;

public class MainDataViewerFrame
{

    private boolean fromMenu = false;

    public String schema;

    private Hashtable tabsForDomains;

    static String url = "jdbc:oracle:thin:@localhost:1521:XE";

    static String url2 = "jdbc:oracle:thin:@cbiodb540.nci.nih.gov:1521:CDSDEV";

    public JSplitPane splitPane = null;

    JFileChooser saveSQLFile = new JFileChooser();

    static double result = 0;

    protected JTextArea textArea = null;

    protected String newline = "\n";

    public JTabbedPane _tPane = null;

    public JTabbedPane _lPane = null;

    private TextArea _showSQL = null;

    private TextArea _ta = null;

    private JToolBar toolBar = null;

    private JPanel _jp0 = null;

    private JPanel _jp1 = null;

    private JPanel _jp2 = null;

    private JPanel _jp3 = null;

    private JPanel _jp4 = null;

    private Connection _con = null;

    private Dialog d = null;

    static String sqlStr = "SELECT FROM \"HR\".\"COUNTRIES\" COUNTRIES INNER JOIN \"HR\".\"LOCATIONS\" LOCATIONS ON COUNTRIES.\"COUNTRY_ID\" = LOCATIONS.\"COUNTRY_ID\" AND COUNTRIES.\"REGION_ID\" = LOCATIONS.\"CITY\" AND COUNTRIES.\"COUNTRY_NAME\" = LOCATIONS.\"STREET_ADDRESS\" INNER JOIN \"HR\".\"DEPARTMENTS\" DEPARTMENTS ON LOCATIONS.\"LOCATION_ID\" = DEPARTMENTS.\"LOCATION_ID\" AND COUNTRIES.\"COUNTRY_NAME\" = DEPARTMENTS.\"DEPARTMENT_NAME\" AND COUNTRIES.\"REGION_ID\" = DEPARTMENTS.\"MANAGER_ID\"";

    public HashSet _alreadyFilled = new HashSet();

    public ArrayList _aryList = new ArrayList();

    public JFrame _jf = null;

    public boolean isFromMenu()
    {
        return fromMenu;
    }

    public HashSet getColumnsForTables()
    {
        return columnsForTables;
    }

    private HashSet columnsForTables;

    public JFrame getDialog()
    {
        return _jf;
    }

    public Connection get_con()
    {
        if (_con != null)
            return _con;
        else
            return null;
    }

    public Hashtable getTabsForDomains()
    {
        return tabsForDomains;
    }

    public TextArea get_showSQL()
    {
        _showSQL = new TextArea();
        return _showSQL;
    }

    public JTabbedPane get_lPane()
    {
        return _lPane;
    }

    public String getSchema()
    {
        return schema;
    }

    public MainDataViewerFrame(JFrame owner, boolean modal, Dialog _ref, Hashtable table, HashSet tableColums, Hashtable connectionParams)
    {
        this.columnsForTables = tableColums;
        schema = connectionParams.get("SCHEMA").toString();
        JMenuBar _jm = new JMenuBar();
        JMenu _jMenu1 = new JMenu("File");
        JMenu _jMenu2 = new JMenu("About");
        _jMenu1.add(new JMenuItem("Open"));
        _jMenu2.add(new JMenuItem("License"));
        _jm.add(_jMenu1);
        _jm.add(_jMenu2);
        _jf = new JFrame();
        _jf.setLayout(new BorderLayout());
        _tPane = new JTabbedPane();
        _lPane = new JTabbedPane(JTabbedPane.BOTTOM);
        _jp0 = new JPanel();
        _jp1 = new JPanel();
        _jp2 = new JPanel();
        _jp3 = new JPanel();
        _jp4 = new JPanel();
        TitledBorder titleTop = BorderFactory.createTitledBorder("SQL Data Panel(s)");
        TitledBorder titleBottom = BorderFactory.createTitledBorder("SQL Helper");
        _tPane.setBorder(titleTop);
        _lPane.setBorder(titleBottom);
        toolBar = new JToolBar("");
        new QBAddButtons(this).addButtons(toolBar);
        _jf.setJMenuBar(_jm);
        _jf.add(toolBar, BorderLayout.PAGE_START);
        // add the panes after here
        _jf.addComponentListener(new DialogComponentListener(this));
        //_con = QBConnection.getDBConnection(url, "oracle.jdbc.driver.OracleDriver", "hr", "hr");
        _con = (Connection) connectionParams.get("connection");
        this.tabsForDomains = table;
        Enumeration enum1 = table.keys();
        while (enum1.hasMoreElements())
        {
            String tmp = enum1.nextElement().toString();
            String tmp1 = new SDTMDomainLookUp().getDescription(tmp);
            Querypanel _qp = new Querypanel(_con, this, schema);
            _aryList.add(_qp);
            _tPane.addTab(tmp + "-" + tmp1, _qp);
            _jf.add(_tPane, BorderLayout.CENTER);
        }
        // Register a change listener
        _tPane.addChangeListener(new TopPaneListener(this));
        _lPane.addChangeListener(new LowerPaneListener(this));
        _lPane.addTab("Connection Info", _jp0);
        _lPane.addTab("SQL Statement", _jp1);
        _lPane.addTab("Edit Sql", _jp3);
        _lPane.addTab("Add Tables", _jp4);
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, _tPane, _lPane);
        splitPane.setOneTouchExpandable(true);
        _jf.getContentPane().add(splitPane, BorderLayout.CENTER);
        _jf.setSize(1100, 950);
        _jf.setLocation(85, 30);
        JPanel jp_status = new JPanel();
        jp_status.setLayout(new BorderLayout());
        jp_status.add(new JLabel("Connected to Database : " + connectionParams.get("URL").toString()), BorderLayout.CENTER);
        jp_status.setBorder(new BevelBorder(BevelBorder.LOWERED));
        TextArea _showDefaultValues = new TextArea();
        _showDefaultValues.setEditable(false);
        _jp0.setLayout(new BorderLayout());
        JTextArea _jt = new JTextArea(10, 850);
        JScrollPane _jp = new JScrollPane(_jt);
        _jt.setEditable(false);
        _jt.setText("Database :" + connectionParams.get("URL").toString() + "\nUser : " + connectionParams.get("UserID").toString() + "\nSchema : " + connectionParams.get("SCHEMA").toString());
        _jp0.add(_jp, BorderLayout.CENTER);
        _jp0.revalidate();
        _jf.add(jp_status, BorderLayout.SOUTH);
        _jf.setTitle("Data Viewer for caAdapter");
        //_jf.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        _jf.addWindowListener(new DialogClosingListener(this));
        _jf.setVisible(true);
        try
        {
            _ref.dispose();
        } catch (Exception e)
        {
            //e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
        }
        // fill the first panel begin
        ArrayList tableList = (ArrayList) this.getTabsForDomains().get(_tPane.getTitleAt(0).substring(0, 2));
        for (int i = 0; i < tableList.size(); i++)
        {
            StringTokenizer temp = new StringTokenizer(tableList.get(i).toString(), ".");
            String schema = temp.nextElement().toString();
            String table1 = temp.nextElement().toString();
            ((Querypanel) this.get_aryList().get(0)).loadTables(schema, table1);
        }
        this.get_alreadyFilled().add(new Integer(0));
        splitPane.setDividerLocation(.65);
        // fill the first panel end
    }

    public JSplitPane getSplitPane()
    {
        return splitPane;
    }

    public JPanel get_jp1()
    {
        return _jp1;
    }

    public JPanel get_jp2()
    {
        return _jp2;
    }

    public JPanel get_jp3()
    {
        return _jp3;
    }

    public JPanel get_jp4()
    {
        return _jp4;
    }

    public HashSet get_alreadyFilled()
    {
        return _alreadyFilled;
    }

    public Dialog getD()
    {
        return d;
    }

    public JFrame get_jf()
    {
        return _jf;
    }

    public JFileChooser getSaveSQLFile()
    {
        return saveSQLFile;
    }

    public ArrayList get_aryList()
    {
        return _aryList;
    }

    public JTabbedPane get_tPane()
    {
        return _tPane;
    }

    public void refreshSQLWindow()
    {
        if (_jp1.getComponentCount() > 0)
        {
            _jp1.removeAll();
        }
        try
        {
            _jp1.setLayout(new BorderLayout());
            JTextArea _jt = new JTextArea(10, 850);
            JScrollPane _jp = new JScrollPane(_jt);
            String _sqlSTR = (((Querypanel) _aryList.get(_tPane.getSelectedIndex())).get_queryBuilder()).getModel().toString(true).toUpperCase();
            _jt.setEditable(false);
            _jt.setText(_sqlSTR);
            _jp1.add(_jp, BorderLayout.CENTER);
            _jp1.revalidate();
        } catch (Exception e1)
        {
        }
    }

    public static void main(String args[])
    {
        System.setProperty("sun.swing.enableImprovedDragGesture", "");
        Preferences.loadDefaults();
        try
        {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e)
        {
        }
        new MainDataViewerFrame(null, false, null);
        //splitPane.setDividerLocation(.8);
        //        try
        //        {
        //            QueryModel qm = SQLParser.toQueryModel(sqlStr);
        //            ((Querypanel) _aryList.get(_tPane.getSelectedIndex())).get_queryBuilder().setModel(qm);
        //        } catch (IOException e)
        //        {
        //            e.printStackTrace();
        //        }
    }

    public MainDataViewerFrame(JFrame owner, boolean modal, Dialog _ref)
    {
        JMenuBar _jm = new JMenuBar();
        JMenu _jMenu1 = new JMenu("File");
        JMenu _jMenu2 = new JMenu("About");
        _jMenu1.add(new JMenuItem("Open"));
        _jMenu2.add(new JMenuItem("License"));
        _jm.add(_jMenu1);
        _jm.add(_jMenu2);
        _jp0 = new JPanel();
        _jp1 = new JPanel();
        _jp2 = new JPanel();
        _jp3 = new JPanel();
        _jp4 = new JPanel();
        //_jf = new JFrame(owner, modal);
        _jf = new JFrame();
        _jf.setLayout(new BorderLayout());
        _tPane = new JTabbedPane();
        _lPane = new JTabbedPane(JTabbedPane.BOTTOM);
        TitledBorder titleTop = BorderFactory.createTitledBorder("Domains");
        TitledBorder titleBottom = BorderFactory.createTitledBorder("SQL Helper");
        _tPane.setBorder(titleTop);
        _lPane.setBorder(titleBottom);
        toolBar = new JToolBar("");
        new QBAddButtons(this).addButtons(toolBar);
        _jf.setJMenuBar(_jm);
        _jf.add(toolBar, BorderLayout.PAGE_START);
        // add the panes after here
        _jf.addComponentListener(new DialogComponentListener(this));
        //_con = QBConnection.getDBConnection(url, "oracle.jdbc.driver.OracleDriver", "hr", "hr");
        Querypanel _qp1 = new Querypanel(_con, this);
        _aryList.add(_qp1);
        _tPane.addTab("Query-Builder-Panel", _qp1);
        _jf.add(_tPane, BorderLayout.CENTER);
        _alreadyFilled.add(new Integer(0));
        // Register a change listener
        _tPane.addChangeListener(new TopPaneListener(this));
        _lPane.addChangeListener(new LowerPaneListener(this));
        _lPane.addTab("Connection Info", _jp0);
        _lPane.addTab("SQL Statement", _jp1);
        //_lPane.addTab("Run SQL", _jp2);
        _lPane.addTab("Edit Sql", _jp3);
        _lPane.addTab("Add Tables", _jp4);
        //(_jp1);
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, _tPane, _lPane);
        splitPane.setOneTouchExpandable(true);
        _jf.getContentPane().add(splitPane, BorderLayout.CENTER);
        _jf.setSize(1100, 950);
        _jf.setLocation(85, 30);
        JPanel jp_status = new JPanel();
        jp_status.setLayout(new BorderLayout());
        jp_status.add(new JLabel("Connected to Database : " + url), BorderLayout.CENTER);
        jp_status.setBorder(new BevelBorder(BevelBorder.LOWERED));
        TextArea _showDefaultValues = new TextArea();
        _showDefaultValues.setEditable(false);
        _jp0.setLayout(new BorderLayout());
        JTextArea _jt = new JTextArea(10, 850);
        JScrollPane _jp = new JScrollPane(_jt);
        _jt.setEditable(false);
        _jp0.add(_jp, BorderLayout.CENTER);
        _jp0.revalidate();
        _jf.add(jp_status, BorderLayout.SOUTH);
        _jf.setTitle("Data Viewer for caAdapter");
        //_jf.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        _jf.addWindowListener(new DialogClosingListener(this));
        //_jf.setAlwaysOnTop(true);
        _jf.setVisible(true);
        try
        {
            _ref.dispose();
        } catch (Exception e)
        {
            //e.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
        }
        Hashtable conParam = null;
        try
        {
            if (_con == null)
            {
                OpenDatabaseConnectionHelper _openDatabaseConnectionHelper = new OpenDatabaseConnectionHelper(_jf);
                conParam = _openDatabaseConnectionHelper.getDatabaseConnectionInfo();
                if (conParam != null)
                {
                    Class.forName(conParam.get("Driver").toString());
                    _con = DriverManager.getConnection(conParam.get("URL").toString(), conParam.get("UserID").toString(), conParam.get("PWD").toString());
                    _qp1.get_queryBuilder().setConnection(_con);
                }
            }
        } catch (Exception e)
        {
            JOptionPane.showMessageDialog(_jf, e.getMessage().toString(), "Could not establish SQL connection ", JOptionPane.ERROR_MESSAGE);
        }
        if (_con != null)
           _jt.setText("Database :" + conParam.get("URL").toString() + "\nUser : " + conParam.get("UserID").toString() + "\nSchema : " + conParam.get("SCHEMA").toString());
    }
}