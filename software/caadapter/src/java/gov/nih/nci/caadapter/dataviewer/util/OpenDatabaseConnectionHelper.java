/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.dataviewer.util;

import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * This class allows the connection window to show up and manages all the connections
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.16 $
 *          $Date: 2008-06-09 19:53:50 $
 */
public class OpenDatabaseConnectionHelper implements TreeSelectionListener, WindowListener, KeyListener {
    private JTree tree = null;
    private JDialog dialog = null;
    private JTextField _profileField = null;
    private JTextField _hostField = null;
    private JTextField _userIdField = null;
    private JTextField _dataBaseDriver = null;
    private JPasswordField _pwdField = null;
    private JTextField _schemaField = null;
    private Hashtable _profileConnections = null;
    private String _nodeName = null;//this is to disable right click for the root
    private DefaultMutableTreeNode top = null;
    private DefaultTreeModel model = null;
    private JCheckBox _def = null;
    private JButton _ok = null;
    private Hashtable driverInfo = new Hashtable();
    private DefaultMutableTreeNode firstNode = null;
    private boolean isCancelled = false;

    public JTextField getPwdFld() {
        return _pwdField;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public OpenDatabaseConnectionHelper(JFrame owner) throws Exception {
        try {
            if (System.getProperty("debug").equalsIgnoreCase("true")) {
                //System.out.println("+++++++++++++++++++++++++++++++++++++++++ debugging");
                String temp = System.getProperty("java.library.path");
                EmptyStringTokenizer emt = new EmptyStringTokenizer(temp, ";");
                while (emt.hasMoreTokens()) {
                    System.out.println("path is : " + emt.nextToken());
                }
                // System.out.println("+++++++++++++++++++++++++++++++++++++++++");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //todo : this information needs to come from the properties file in the future
        driverInfo.put("Oracle", "jdbc:oracle:thin:@~oracle.jdbc.OracleDriver");
        driverInfo.put("MySql", "jdbc:mysql://~com.mysql.jdbc.Driver");
        _profileConnections = new Hashtable();
        dialog = new JDialog(owner);
        dialog.setTitle("Enter Connection Parameters");
        //dialog.setUndecorated(true);
        Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        dialog.setSize(700, 330);
        dialog.setLocationRelativeTo(null);
        JPanel superpanel = new JPanel();
        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(7, 2));
        //
        p1.add(new JLabel("Profile Name: "));
        p1.add(_profileField = new JTextField());
        //
        p1.add(new JLabel("Host URL: "));
        p1.add(_hostField = new JTextField());
        //
        p1.add(new JLabel("Database driver: "));
        p1.add(_dataBaseDriver = new JTextField());
        //
        p1.add(new JLabel("User ID: "));
        p1.add(_userIdField = new JTextField());
        //
        p1.add(new JLabel("Password"));
        p1.add(_pwdField = new JPasswordField());
        //
        p1.add(new JLabel("Schema Name"));
        p1.add(_schemaField = new JTextField());
        //
        /*
            If already connected panel
         */
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        JPanel statpan = new JPanel();
        statpan.setLayout(new BorderLayout());
        /*
           If already connected panel
        */
        JPanel _butPan = new JPanel();
        //_butPan.setLayout(new BorderLayout());
        _butPan.setBounds(2, 1, 10, 10);
        _butPan.setBorder(raisedetched);
        JButton _prof = new JButton("New profile");
        _prof.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showConnectionWindow(dialog, "New Profile", null, null);
            }
        });
        _ok = new JButton("      Connect      ");
        _ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeDialog();
            }
        });
        JButton _can = new JButton("      Cancel     ");
        _can.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                isCancelled = true;
                dialog.dispose();
            }
        });
        _butPan.add(_prof);
        // _butPan.add(_save);
        _butPan.add(_ok);
        _butPan.add(_can);
        _def = new JCheckBox("Click to save the connection profile");
        _def.setSelected(true);
        TitledBorder title = BorderFactory.createTitledBorder("Enter Connection Information");
        p1.setBorder(title);
        superpanel.add(p1);
        LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
        //JLabel label01 = new JLabel("Enter the Connection Parameters");
        //label01.setBorder(lineBorder);
        //label01.setOpaque(true);
        Color blue = new Color(0, 0, 153);
        //label01.setBackground(blue);
        //label01.setFont(new Font("Arial", Font.BOLD, 13));
        //label01.setForeground(Color.WHITE);
        //label01.setHorizontalAlignment(SwingConstants.CENTER);
        JScrollPane treeView = new JScrollPane(createTrees());
        TitledBorder title1 = BorderFactory.createTitledBorder("Connection Profiles");
        treeView.setBorder(title1);
        JPanel treePane = new JPanel();
        treePane.setLayout(new BorderLayout());
        treePane.add(treeView, BorderLayout.CENTER);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePane, p1);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(225);
        //
        superpanel.setLayout(new BorderLayout());
        //superpanel.add(label01, BorderLayout.NORTH);
        superpanel.add(splitPane, BorderLayout.CENTER);
        bottomPanel.add(statpan, BorderLayout.CENTER);
        bottomPanel.add(_butPan, BorderLayout.SOUTH);
        superpanel.add(bottomPanel, BorderLayout.SOUTH);//harsha
        superpanel.setBorder(lineBorder);
        dialog.add(superpanel);
        dialog.setModal(true);
        _profileField.setEditable(false);
        _hostField.setEditable(false);
        _dataBaseDriver.setEditable(false);
        _userIdField.setEditable(false);
        _schemaField.setEditable(false);
        _pwdField.setEditable(true);
        _pwdField.addKeyListener(this);
        readDriverProperties();
        dialog.addWindowListener(this);
        setSelected(firstNode);
        dialog.setVisible(true);
        //tree.expandPath( );
        //tree.setSelectionRow(((DefaultMutableTreeNode)tree.getModel().getChild(tree, 0)));
        //treePane.transferFocus();
        //_profileField.transferFocus(); _hostField.transferFocus(); _userIdField.transferFocus(); _dataBaseDriver.transferFocus();;
    }

    public void setSelected(DefaultMutableTreeNode node) {
        if (node != null) {
            TreePath path = new TreePath(node.getPath());
            tree.setSelectionPath(path);
        }
    }

    private void closeDialog() {
        if (_pwdField.getText().length() == 0) {
            JOptionPane.showMessageDialog(dialog, "Please enter a password, before connecting to database");
            return;
        }
        if (_def.isSelected()) {
            try {
                File saveConInfo = new File((System.getProperty("user.home") + "\\.dataviewer"));
                saveConInfo.delete();
                BufferedWriter out = new BufferedWriter(new FileWriter(saveConInfo, saveConInfo.exists()));
                //write the contents of the hashtable
                Iterator it = _profileConnections.values().iterator();
                while (it.hasNext()) {
                    EmptyStringTokenizer empt = new EmptyStringTokenizer(it.next().toString(), "~");
                    out.write(empt.getTokenAt(0) + "~" + empt.getTokenAt(1) + "~" + empt.getTokenAt(2) + "~" + empt.getTokenAt(3) + "~" + empt.getTokenAt(4) + "\n");
                }
                out.close();
            } catch (IOException e1) {
                e1.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
            }
        }
        dialog.dispose();
    }

    public Hashtable getDatabaseConnectionInfo() {
        Hashtable<String, String> _conInfo = new Hashtable<String, String>();
        //map the host field to the right URL depending on the database
        //_conInfo.put("URL", _hostField.getText());
        String key = _dataBaseDriver.getText().toString();
        EmptyStringTokenizer empt = new EmptyStringTokenizer(driverInfo.get(_dataBaseDriver.getText()).toString(), "~");
        String urlString = empt.getTokenAt(0) + _hostField.getText();
        String driverString = empt.getTokenAt(1);
        _conInfo.put("URL", urlString);
        _conInfo.put("UserID", _userIdField.getText());
        _conInfo.put("PWD", _pwdField.getText());
        _conInfo.put("SCHEMA", _schemaField.getText());
        _conInfo.put("Driver", driverString);
        return _conInfo;
    }

    private JTree createTrees() {
        top = new DefaultMutableTreeNode("Profiles");
        model = new DefaultTreeModel(top);
        tree = new JTree(model);
        //query if the file exists and add the connections
        //
        File saveConInfo = new File((System.getProperty("user.home") + "\\.dataviewer"));
        try {
            BufferedReader in = new BufferedReader(new FileReader(saveConInfo));
            String str;
            while ((str = in.readLine()) != null) {
                //process(str);
                EmptyStringTokenizer empt = new EmptyStringTokenizer(str, "~");
                _profileConnections.put(empt.getTokenAt(0), str);
            }
            in.close();
        } catch (IOException e) {
        }
        // Iterate over the values in the map
        Iterator it = _profileConnections.keySet().iterator();
        RightClickHelper rightClickHelp = null;
        while (it.hasNext()) {
            // Get key
            EmptyStringTokenizer empt = new EmptyStringTokenizer(it.next().toString(), "~");
            DefaultMutableTreeNode def = null;
            try {
                def = new DefaultMutableTreeNode(empt.getTokenAt(0));
                if (firstNode == null) {
                    firstNode = new DefaultMutableTreeNode();
                    firstNode = def;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            if (def != null)
                top.add(def);
        }
        //
        tree.addMouseListener(new RightClickHelper(tree));
        tree.addTreeSelectionListener(this);
        tree.expandPath((tree.getPathForRow(0)));
        return tree;
    }

    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        //System.out.println("Node " + node);
        if (node == null) {
            //System.out.println("Returned");
            return;
        }
        Object nodeInfo = node.getUserObject();
        if (node.isLeaf()) {
            _nodeName = nodeInfo.toString();
            if (_profileConnections.containsKey(nodeInfo.toString())) {
                EmptyStringTokenizer empt = new EmptyStringTokenizer(_profileConnections.get(nodeInfo.toString()).toString(), "~");
                //System.out.println(empt.toSameString());
                _profileField.setText(empt.getTokenAt(0));
                _profileField.setEditable(false);
                _hostField.setText(empt.getTokenAt(1));
                _hostField.setEditable(false);
                _dataBaseDriver.setText(empt.getTokenAt(2));
                _dataBaseDriver.setEditable(false);
                _userIdField.setText(empt.getTokenAt(3));
                _userIdField.setEditable(false);
                _schemaField.setText(empt.getTokenAt(4));
                _schemaField.setEditable(false);
                _pwdField.setText("");
                _pwdField.setEditable(true);
            }
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    _pwdField.requestFocus();
                }
            });
        }
    }

    public void focusGained(FocusEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
        //System.out.println("kjsdhfkjhsdfdsf");
    }

    public void focusLost(FocusEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowOpened(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                _pwdField.requestFocus();
            }
        });
    }

    public void windowClosing(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowClosed(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowIconified(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowDeiconified(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowActivated(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowDeactivated(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void keyTyped(KeyEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void keyPressed(KeyEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            closeDialog();
    }

    public void keyReleased(KeyEvent e) {
    }

    class RightClickHelper implements MouseListener, ActionListener {
        private JTextField textField;
        private JTree _tree;
        private JPopupMenu popedit;
        JMenuItem copy;
        JMenuItem cut;
        JMenuItem new_;

        RightClickHelper(JTextField fld) {
            this.textField = fld;
            this.popedit = new JPopupMenu();
            init();
        }

        RightClickHelper(JTree fld) {
            this._tree = fld;
            this.popedit = new JPopupMenu();
            init();
        }

        private void init() {
            copy = new JMenuItem("Edit Profile");
            copy.addActionListener(this);
            copy.setActionCommand("edit");
            cut = new JMenuItem("Delete Profile");
            cut.addActionListener(this);
            cut.setActionCommand("delete");
            new_ = new JMenuItem("New Profile");
            new_.addActionListener(this);
            new_.setActionCommand("new");
            popedit.add(new_);
            popedit.add(copy);
            popedit.add(cut);


        }

        //this method exists
        public void mousePressed(MouseEvent e) {
            if (e.getModifiers() == 4) {// right click
                DefaultMutableTreeNode tempString = (DefaultMutableTreeNode) (((JTree) e.getSource()).getAnchorSelectionPath().getLastPathComponent());

                if (!tempString.toString().equals("Profiles")) {
                    new_.setEnabled(false);
                    copy.setEnabled(true);
                    cut.setEnabled(true);
                } else {
                    copy.setEnabled(false);
                    cut.setEnabled(false);
                    new_.setEnabled(true);
                }
                popedit.show(_tree, e.getX(), e.getY());
            }
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        //this method exists
        public void actionPerformed(ActionEvent a) {
            String command = a.getActionCommand();
            if (command.equals("edit")) {
                showConnectionWindow(dialog, "Edit Profile", _profileField.getText() + "~" + _hostField.getText() + "~" + _dataBaseDriver.getText() + "~" + _userIdField.getText() + "~" + _schemaField.getText(), getSelectedNode());
            } else if (command.equals("delete")) {
                int response = JOptionPane.showConfirmDialog(dialog, "Do you want to delete profile \"" + _nodeName + "\" ?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.NO_OPTION) {
                    //System.out.println("No button clicked");
                } else if (response == JOptionPane.YES_OPTION) {
                    model.removeNodeFromParent(getSelectedNode());
                    _profileConnections.remove(_nodeName);
                    //check if at least one profile exists
                    if (_profileConnections.size() > 0) {
                        tree.setSelectionRow(1);
                    } else {
                        _profileField.setText("");
                        _hostField.setText("");
                        _userIdField.setText("");
                        _dataBaseDriver.setText("");
                        _pwdField.setText("");
                        _schemaField.setText("");
                    }
                } else if (response == JOptionPane.CLOSED_OPTION) {
                    System.out.println("JOptionPane closed");
                }
            } else if (command.equals("new")) {
                showConnectionWindow(dialog, "New Profile", null, null);
            }
            //popedit.removeAll();
        }

        private DefaultMutableTreeNode getSelectedNode() {
            return (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        }

        public String toString() {
            //return super.toString();//To change body of overridden methods use File | Settings | File Templates.
            return textField.getText();
        }
    }

    JDialog conDialog;
    JTextField profile = new JTextField();
    JTextField host = new JTextField();
    //JTextField three = new JTextField();
    String selectedDriver = null;
    JComboBox driver = new JComboBox();
    JTextField four = new JTextField();
    JPasswordField five = new JPasswordField();
    JTextField six = new JTextField();
    boolean retVal = false;
    ArrayList driverDropDown = null;
    String editDriver = null;

    private void showConnectionWindow(JDialog owner, final String title, String values, final DefaultMutableTreeNode selectedNode) {
        conDialog = new JDialog(owner);
        conDialog.setTitle(title);
        JPanel conPan = new JPanel();
        conPan.setLayout(new BorderLayout());
        if (title.equalsIgnoreCase("New Profile")) {
            conDialog.setTitle("Add New Profile");
            profile.setText("");
            host.setText("");
            //three.setText(""); set the conbox here
            four.setText("");
            five.setText("");
            six.setText("");
        }
        if (values != null) {
            EmptyStringTokenizer empt = new EmptyStringTokenizer(values, "~");
            profile.setText(empt.getTokenAt(0));
            host.setText(empt.getTokenAt(1));
            //three.setText(empt.getTokenAt(2));
            editDriver = empt.getTokenAt(2);
            driver.setSelectedItem(empt.getTokenAt(2));
            four.setText(empt.getTokenAt(3));
            five.setText(empt.getTokenAt(4));
            six.setText(empt.getTokenAt(4));
        }
        //
        JPanel conInfo = new JPanel();
        conInfo.setLayout(new GridLayout(5, 2));
        conInfo.add(new JLabel("Profile name: "));
        if (title.equalsIgnoreCase("Edit Profile")) {
            profile.setEditable(false);
        } else {
            profile.setEditable(true);
        }
        conInfo.add(profile);
        //
        conInfo.add(new JLabel("Host URL <server:1521:SID>:"));
        conInfo.add(host);
        //
        conInfo.add(new JLabel("Database driver: "));
        if (title.equalsIgnoreCase("New Profile")) {
            conInfo.add(driver);
            //driver.setSelectedIndex(0);
        } else {
            conInfo.add(driver);
        }
        driver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    selectedDriver = ((JComboBox) e.getSource()).getSelectedItem().toString();
                } catch (Exception ee) {
                    selectedDriver = driver.getItemAt(0).toString();
                }
                //System.out.println("selcetd driver is " + selectedDriver);
            }
        });
        //
        conInfo.add(new JLabel("User ID: "));
        conInfo.add(four);
        //
        //conInfo.add(new JLabel("Enter your Password:"));
        //conInfo.add(five);
        //
        conInfo.add(new JLabel("Schema"));
        conInfo.add(six);
        //
        JPanel butInfo = new JPanel();
        butInfo.setLayout(new FlowLayout());
        JButton okBut = new JButton("OK");
        okBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (!areFieldsEmpty(title)) {
                    if (selectedDriver == null) {
                        selectedDriver = editDriver;
                    }
                    String _temp = profile.getText() + "~" + host.getText() + "~" + selectedDriver + "~" + four.getText() + "~" + six.getText();
                    _profileConnections.put(profile.getText(), _temp);
                    if (title.equalsIgnoreCase("New profile")) {
                        model.insertNodeInto(new DefaultMutableTreeNode(profile.getText()), top, 0);
                    }
                    //tree.setSelectionRow(1);
                    _profileField.setText(profile.getText());
                    _profileField.setEditable(false);
                    _hostField.setText(host.getText());
                    _hostField.setEditable(false);
                    _dataBaseDriver.setText(selectedDriver);
                    _dataBaseDriver.setEditable(false);
                    _userIdField.setText(four.getText());
                    _userIdField.setEditable(false);
                    _schemaField.setText(six.getText());
                    _schemaField.setEditable(false);
                    _pwdField.setText("");
                    _pwdField.setEditable(true);
                    tree.setSelectionRow(1);
                    tree.expandPath(tree.getPathForRow(0));
                    tree.setSelectionPath(tree.getPathForRow(1));
                    conDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "All the fields are required");
                }
            }
        });
        JButton canBut = new JButton("Cancel");
        canBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                conDialog.dispose();
            }
        });
        butInfo.add(okBut);
        butInfo.add(canBut);
        LineBorder lineBorder = (LineBorder) BorderFactory.createLineBorder(Color.black);
        butInfo.setBorder(lineBorder);
        //
        conPan.add(conInfo, BorderLayout.CENTER);
        conPan.add(butInfo, BorderLayout.SOUTH);
        //
        conInfo.setBorder(new TitledBorder(new LineBorder(Color.black, 1), "Enter Connection Information"));
        conDialog.add(conPan);
        conDialog.setSize(500, 250);
        conDialog.setLocationRelativeTo(null);
        conDialog.setVisible(true);
    }

    private boolean areFieldsEmpty(String title) {
        if (title.equals("Edit Profile"))
            return false;
        boolean retVal = false;
        if (profile.getText().length() == 0) {
            return true;
        } else if (host.getText().length() == 0) {
            return true;
        } else if (selectedDriver == null) {
            return true;
        } else if (four.getText().length() == 0) {
            return true;
        } else if (six.getText().length() == 0) {
            return true;
        }
        return retVal;
    }

    public static void main(String args[]) throws Exception {
        new OpenDatabaseConnectionHelper(null);
    }

    private void populateDriverDropDown() {
        driverDropDown = new ArrayList();
        try {
            driver.addItem("--Select--");
            //add oracle
            driver.addItem("Oracle");
            driverDropDown.add("Oracle");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readDriverProperties(String old) {
        driverDropDown = new ArrayList();
        try {
            BufferedReader in = new BufferedReader(new FileReader("driver.properties"));
            String str;
            driver.addItem("--Select--");
            while ((str = in.readLine()) != null) {
                driver.addItem(str);
                driverDropDown.add(str);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readDriverProperties() {
        //todo : this has to be read from a property file in future

        /**
         *
         * hard coding it for now;
         */
        populateDriverDropDown();
//        driverDropDown = new ArrayList();
//        Properties properties = new Properties();
//        InputStream fi = null;
//        try
//        {
//            fi = CaadapterUtil.class.getClassLoader().getResource("driver.properties").openStream();
//            properties.load(fi);
//            if (properties != null)
//            {
//                String databases = properties.getProperty("databases");
//                EmptyStringTokenizer empt = new EmptyStringTokenizer(databases, ",");
//                while (empt.hasMoreTokens())
//                {
//                    String temp = empt.nextToken();
//                    driver.addItem(temp);
//                    driverDropDown.add(temp);
//                }
//            }
//        } catch (Exception ex)
//        {
//            Log.logException(CaadapterUtil.class, "message-types.properties is not found", ex);
//        } finally
//        {
//            if (fi != null)
//                try
//                {
//                    fi.close();
//                } catch (IOException ignore)
//                {
//                }
//        }
    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.15  2007/11/05 15:40:23  jayannah
 * Changed the message/wording
 *
 * Revision 1.14  2007/10/17 20:03:37  jayannah
 * -Changed the behavior of the jtree. enables a create new profile action upon right clicking the root
 * -In the event of the file not found a pop box shows up to collect the values
 *
 * Revision 1.13  2007/10/17 15:55:12  jayannah
 * set the fields to blank when a profile is deleted
 *
 * Revision 1.12  2007/10/16 12:34:18  jayannah
 * Highlighted the first node when a profile is added
 *
 * Revision 1.11  2007/10/16 12:28:05  jayannah
 * The right click on the root node will not do anything, when a new profile is entered, the window automatically shows the first node
 *
 * Revision 1.10  2007/10/11 20:01:31  jayannah
 * Changed the input screen label during collection of database information
 *
 * Revision 1.9  2007/10/10 19:50:02  jayannah
 * Commented System outs
 *
 * Revision 1.8  2007/09/13 13:53:56  jayannah
 * Changes made to fix, window position, parameters during the launch of data viewer, handling of the toolbar buttons and to GEnerate the SQL when the user does not want to use the data viewer
 *
 * Revision 1.7  2007/09/13 02:48:19  jayannah
 * Made a fix to position the window at the centre of the screen
 *
 * Revision 1.6  2007/08/28 14:47:18  jayannah
 * added a todo comment
 *
 * Revision 1.5  2007/08/17 15:53:24  jayannah
 * cosmetic changes for dialog closing listener
 * handled the cancel request from the user., previously it was going back and loading the defing xml which is not required
 *
 * Revision 1.4  2007/08/16 18:53:55  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */