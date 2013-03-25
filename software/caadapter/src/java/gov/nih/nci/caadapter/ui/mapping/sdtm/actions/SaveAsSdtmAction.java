/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.mapping.sdtm.actions;

import edu.stanford.ejalbert.BrowserLauncher;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.EmptyStringTokenizer;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.dataviewer.MainDataViewerFrame;
import gov.nih.nci.caadapter.dataviewer.util.CaDataViewHelper;
import gov.nih.nci.caadapter.dataviewer.util.QBParseMappingFile;
import gov.nih.nci.caadapter.dataviewer.util.Querypanel;
import gov.nih.nci.caadapter.sdtm.SDTMMappingGenerator;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.DefaultSaveAsAction;
import gov.nih.nci.caadapter.ui.main.MainFrame;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.sdtm.Database2SDTMMappingPanel;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * This class saves the map file and passes the control over to the
 * Data Viewer
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.20 $
 *          $Date: 2008-06-09 19:54:06 $
 */
public class SaveAsSdtmAction extends DefaultSaveAsAction {
    private MainDataViewerFrame _mD = null;
    /**
     * Logging constant used to identify source of log entry, that could be later used to create logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: SaveAsSdtmAction.java,v $";
    /**
     * String that identifies the class version and solves the serial version UID problem. This String is for informational purposes only and MUST not be made
     * final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/sdtm/actions/SaveAsSdtmAction.java,v 1.20 2008-06-09 19:54:06 phadkes Exp $";
    protected AbstractMappingPanel mappingPanel;
    public SDTMMappingGenerator sdtmMappingGenerator;
    private boolean alreadySaved = false;

    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    public SaveAsSdtmAction(AbstractMappingPanel mappingPanel, SDTMMappingGenerator _sdtmMappingGenerator) {
        this(COMMAND_NAME, mappingPanel);
        this.sdtmMappingGenerator = _sdtmMappingGenerator;
    }

    public SaveAsSdtmAction(String command, AbstractMappingPanel mappingPanel, SDTMMappingGenerator _sdtmMappingGenerator) {
        this(command, mappingPanel);
        this.sdtmMappingGenerator = _sdtmMappingGenerator;
    }

    /**
     * Defines an <code>Action</code> object with the specified description string and a default icon.
     */
    public SaveAsSdtmAction(String name, AbstractMappingPanel mappingPanel) {
        this(name, null, mappingPanel);
    }

    /**
     * Defines an <code>Action</code> object with the specified description string and a the specified icon.
     */
    public SaveAsSdtmAction(String name, Icon icon, AbstractMappingPanel mappingPanel) {
        super(name, icon, null);
        this.mappingPanel = mappingPanel;
        // setAdditionalAttributes();
    }

    /**
     * Invoked when an action occurs.
     */
    protected boolean doAction(ActionEvent e) throws Exception {
        if (this.mappingPanel != null) {
            if (!mappingPanel.isSourceTreePopulated() || !mappingPanel.isTargetTreePopulated()) {
                String msg = "Enter both source and target information before saving the map specification.";
                JOptionPane.showMessageDialog(mappingPanel, msg, "Error", JOptionPane.ERROR_MESSAGE);
                setSuccessfullyPerformed(false);
                return false;
            }
        }
        File file = DefaultSettings.getUserInputOfFileFromGUI(this.mappingPanel, Config.MAP_FILE_DEFAULT_EXTENTION, "Save As...", true, true);
        if (file != null) {
            System.out.println("SaveSdtmAction ------ " + mappingPanel.getMappingDataManager().retrieveMappingData(false));
            // setSuccessfullyPerformed(processSaveFile(file));
            processSaveFile(file);
        }
        return isSuccessfullyPerformed();
    }

    java.util.HashMap addedList, deletedList, updatedList;
    ArrayList removeBeforeAfterList;

    protected boolean processSaveFile(final File file) throws Exception {
        preActionPerformed(mappingPanel);
        BufferedOutputStream bw = null;
        boolean oldChangeValue = mappingPanel.isChanged();
        try {
            final StringBuffer out = new StringBuffer();
            out.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
            out.append("<mapping>\n");
            out.append("  <components>\n");
            if (((Database2SDTMMappingPanel) mappingPanel).isConnectDB()) {
                Hashtable params = ((Database2SDTMMappingPanel) mappingPanel).getConnectionParameters();
                String paramString = params.get("URL").toString() + "~" + params.get("Driver").toString() + "~" + params.get("UserID").toString() + "~" + params.get("SCHEMA").toString();
                out.append("  \t<component kind=\"Database\" param=\"" + paramString + "\"/>\n");
            } else {
            	String srcFileName=FileUtil.getAssociatedFileRelativePath(file.getAbsolutePath(), sdtmMappingGenerator.getScsSDTMFile());
                out.append("  \t<component kind=\"SCS\" location=\"" +  srcFileName + "\"/>\n");
            }
            String trgtFileName=FileUtil.getAssociatedFileRelativePath(file.getAbsolutePath(), sdtmMappingGenerator.getScsDefineXMLFIle());
            out.append("  \t<component kind=\"XML\" location=\"" + trgtFileName + "\"/>\n");
            out.append("  </components>\n");
            for (int i = 0; i < sdtmMappingGenerator.results.size(); i++) {
                out.append("  <link>\n");
                StringTokenizer strTk = new StringTokenizer(sdtmMappingGenerator.results.get(i), "~");
                out.append("  \t<source>");
                out.append(strTk.nextToken());
                out.append("</source>\n");
                out.append("  \t<target>");
                out.append(strTk.nextToken());
                out.append("</target>\n");
                out.append("  </link>\n");
            }
            BufferedWriter out1 = new BufferedWriter(new FileWriter(file));
            out1.write(out.toString());
            out1.write("</mapping>");
            out1.close();
            // clear the change flag.
            mappingPanel.setChanged(false);
            // try to notify affected panels
            postActionPerformed(mappingPanel);
            final TitledBorder _title = BorderFactory.createTitledBorder("Generating SQL");
            if (!alreadySaved) {
                Object[] options = {"Yes", "No"};
                int n;
                if (((Database2SDTMMappingPanel) mappingPanel).isConnectDB()) {
                    //so the map file is saved successfully; now go enable the transform and dataviewer button just because a save file exists(*.map)
                    ((Database2SDTMMappingPanel) mappingPanel).get_commonBut().setEnabled(true);
                    //
                    n = JOptionPane.showOptionDialog(mappingPanel.getParent(), "File "+file.getName() + " was saved successfully \n Do you want to open the Data Viewer using the \" " + file.getName() + " \" file?", "Open Data Viewer", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    if (n == 0) {
                        //parse the mapping file
                        OpenQueryBuilder((Hashtable) getMappingsFromMapFile(file).get(0), (HashSet) getMappingsFromMapFile(file).get(1), file, out.toString());
                    } else {
                        // the USER CHOSE NOT TO OPEN THE DATA VIEWER DARN!!!!!
                        final Dialog d = new Dialog(mainFrame, "SQL Query", true);
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                try {
                                    _mD = new MainDataViewerFrame(((Database2SDTMMappingPanel) mappingPanel).isOpenDBmap(), null, (Hashtable) getMappingsFromMapFile(file).get(0), (HashSet) getMappingsFromMapFile(file).get(1), ((Database2SDTMMappingPanel) mappingPanel).getConnectionParameters(), file, out.toString(), null, ((Database2SDTMMappingPanel) mappingPanel).getTransFormBut(), true);
                                    saveAndCloseDataViewer();
                                    _mD.get_jf().dispose();
                                    Thread.sleep(1000);
                                } catch (Exception e) {
                                    d.dispose();
                                    JOptionPane.showMessageDialog(mainFrame, e.getMessage().toString(), "Unknown Problem...", JOptionPane.ERROR_MESSAGE);
                                }
                                JPanel resPan = new JPanel();
                                resPan.setLayout(new BorderLayout());
                                resPan.setBorder(new TitledBorder(new LineBorder(Color.black, 1), "Generating SQL"));
                                JButton okBut = new JButton("OK");
                                okBut.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        d.dispose();
                                    }
                                });
                                JPanel labelPane = new JPanel();
                                JLabel label = new JLabel("The file \"" + file.getName() + "\" has been successfully saved");
                                label.setFont(new Font("SansSerif", Font.BOLD, 14));
                                labelPane.add(label);
                                resPan.add(labelPane, BorderLayout.CENTER);
                                JPanel butPan = new JPanel();
                                butPan.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
                                butPan.add(okBut);
                                resPan.add(butPan, BorderLayout.SOUTH);
                                d.removeAll();
                                d.add(resPan);
                                d.validate();
                            }
                        });
                        JPanel pane = new JPanel();
                        pane.setBorder(new TitledBorder(new LineBorder(Color.black, 1), "Generating SQL"));
                        //pane.setLayout(new GridLayout(0, 1));
                        pane.setLayout(new BorderLayout());
                        JLabel _jl = new JLabel("SQL(s) are now being Generated, please wait.....");
                        _jl.setFont(new Font("SansSerif", Font.BOLD, 14));
                        //
                        JLabel imageIcon = new JLabel(getWaitButton("animated"));

                        //
                        JPanel labelPane = new JPanel();
                        labelPane.add(_jl, BorderLayout.CENTER);
                        //
                        pane.add(imageIcon, BorderLayout.CENTER);
                        pane.add(labelPane, BorderLayout.NORTH);
                        //
                        d.add(pane, BorderLayout.CENTER);
                        d.setUndecorated(true);
                        d.setLocation(350, 420);
                        d.setAlwaysOnTop(true);
                        d.setSize(650, 160);
                        d.setVisible(true);
                    }
                }
            }
            return true;
        } catch (Throwable e) {
            // restore the change value since something occurred and believe
            // the save process is aborted.
            mappingPanel.setChanged(oldChangeValue);
            // rethrow the exeception
            throw new Exception(e);
            // return false;
        } finally {
            try {
                // close buffered writer will automatically close enclosed file
                // writer.
                if (bw != null) {
                    bw.close();
                }
                mappingPanel.setSaveFile(file);
            } catch (Throwable e) {// intentionally ignored.
            }
        }
    }

    public ArrayList getMappingsFromMapFile(File mapFile) {
        ArrayList retAry = new ArrayList();
        QBParseMappingFile _qbparse = new QBParseMappingFile(mapFile);
        _qbparse.parseFile();
        retAry.add(_qbparse.getHashTable());
        retAry.add(_qbparse.getHashTblColumns());
        return retAry;
    }

    public static ImageIcon getWaitButton(String imageName) {
        String imgLocation = "/images/_" + imageName + ".gif";
        URL imageURL = null;
        try {
            imageURL = MainFrame.class.getResource(imgLocation);
            return new ImageIcon(imageURL);
        } catch (Exception e) {
            System.out.println("Unable to find image _" + imageName);
            e.printStackTrace();
        }
        return null;
    }

    public void OpenQueryBuilder(final Hashtable list, final HashSet cols, final File file, final String out) {
        try {
            nickyb.sqleonardo.querybuilder.QueryBuilder.getDefaultLocale();
        } catch (Error e) {
            Object[] options = {"Open Instructions page..", "Cancel..."};
            int n = JOptionPane.showOptionDialog(mainFrame, "This module is missing the JAR file and unable to continue further processing \n" +
                    "To download the jar file, please refer to the instructions", "Missing SQLeonardo.jar file...",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
            if (n == 0) {
                try {
                    BrowserLauncher.openURL("https://cabig.nci.nih.gov/tools/caAdapter");
                    return;
                } catch (Exception e1) {
                    System.out.println("Unable to launch browser due to " + e1.getMessage());
                }
            } else {
                return;
            }
        }
        final Dialog d = new Dialog(mainFrame, "SQL Query", true);
        final ArrayList tempArray;
        (new Thread() {
            public void run() {
                try {
                    new MainDataViewerFrame(((Database2SDTMMappingPanel) mappingPanel).isOpenDBmap(), d, list, cols, ((Database2SDTMMappingPanel) mappingPanel).getConnectionParameters(), file, out, null, ((Database2SDTMMappingPanel) mappingPanel).getTransFormBut(), false);
                } catch (Exception e) {
                    d.dispose();
                    JOptionPane.showMessageDialog(mainFrame, e.getMessage().toString(), "Could not open the Querybuilder", JOptionPane.ERROR_MESSAGE);
                }
                d.dispose();
            }
        }).start();
        //d = new Dialog(mainFrame, "SQL Query", true);
        JPanel pane = new JPanel();
        TitledBorder _title = BorderFactory.createTitledBorder("Visual SQL Builder");
        pane.setBorder(_title);
        pane.setLayout(new GridLayout(0, 1));
        JLabel _jl = new JLabel("Data Viewer loading , please wait.....");
        pane.add(_jl);
        d.add(pane, BorderLayout.CENTER);
        d.setLocation(400, 400);
        d.setSize(500, 130);
        d.setVisible(true);
    }

    private void saveAndCloseDataViewer() {
        _mD.getDialog().removeAll();
        BufferedWriter out = null;
        try {
            if (_mD.getSaveFile().exists())
                _mD.getSaveFile().delete();
            out = new BufferedWriter(new FileWriter(_mD.getSaveFile()));
            //remove the all sql elements
            String tempStr = removeSQLElements(_mD.getXmlString());
            //remove all sql elements
            out.write(tempStr);
            out.append("\n</mapping>");
            //closing because the user clicked save and exit; so, LOADING and MARKING all columns
            out.close();
            try {
                int numberOfTabs = _mD.get_tPane().getTabCount();
                for (int i = 0; i < numberOfTabs; i++) {
                    _mD.get_tPane().setSelectedIndex(i);
                    Thread.sleep(2000);  // 2 secs
                    loadTablesQuietly();
                    Thread.sleep(3000);  // 3 secs
                    String _sqlSTR = (((Querypanel) _mD.get_aryList().get(i)).get_queryBuilder()).getQueryModel().toString().toUpperCase();
                    String domainName = _mD.get_tPane().getTitleAt(i).substring(0, 2);
                    String saveSQLForMapFile = markSelectedColumns(domainName, _sqlSTR);
                    _mD.getSqlSaveHashMap().put(domainName, saveSQLForMapFile);
                    System.gc();
                }
            } catch (Exception e) {
            }
            BufferedWriter out1 = new BufferedWriter(new FileWriter(_mD.getSaveFile()));
            out1.write(tempStr);
            Set set = _mD.getSqlSaveHashMap().keySet();
            out1.write("<!-- PLEASE DO NOT MODIFY THE SQL STATEMENTS BELOW-->");
            for (Iterator iterator = set.iterator(); iterator.hasNext();) {
                String domainName = (String) iterator.next();
                //String sql4Domain = (String) _mD.getSqlSaveHashMap().get(domainName);
                String sql4Domain = (String) _mD.getSqlListWODataViewer().get(domainName);
                out1.write("\n<sql name=\"" + domainName + "\">");
                out1.write("" + sql4Domain);
                out1.write("</sql>");
            }
            out1.append("\n</mapping>");
            out1.close();
        } catch (
                IOException ee) {
            ee.printStackTrace();
            JOptionPane.showMessageDialog(_mD.get_jf(), "The file \"" + _mD.getSaveFile().getName() + "\" was not saved due to " + ee.getLocalizedMessage(), "Mapping file is not saved", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String removeSQLElements(String xmlStr) {
        if (xmlStr.indexOf("<sql") > 0) {
            int beginSQL = xmlStr.indexOf("<sql");
            xmlStr = xmlStr.substring(0, beginSQL);
            return xmlStr;
        } else {
            return xmlStr;
        }
    }

    private void loadTablesQuietly() {
        HashSet tableLoad = new HashSet();
        ArrayList tableList = (ArrayList) _mD.getTabsForDomains().get(_mD.get_tPane().getTitleAt(0).substring(0, 2));
        for (int i = 0; i < tableList.size(); i++) {
            StringTokenizer temp = new StringTokenizer(tableList.get(i).toString(), ".");
            String schema = temp.nextElement().toString();
            String table1 = temp.nextElement().toString();
            try {
                if (tableLoad.add(table1))
                    ((Querypanel) _mD.get_aryList().get(0)).loadTables(schema, table1);
            } catch (Exception e) {
                e.printStackTrace();
                //throw e;
            }
        }
    }

    private void loadTablesQuietly(int position) {
        HashSet tableLoad = new HashSet();
        ArrayList tableList = (ArrayList) _mD.getTabsForDomains().get(_mD.get_tPane().getTitleAt(position).substring(0, 2));
        for (int i = 0; i < tableList.size(); i++) {
            StringTokenizer temp = new StringTokenizer(tableList.get(i).toString(), ".");
            String schema = temp.nextElement().toString();
            String table1 = temp.nextElement().toString();
            try {
                if (tableLoad.add(table1))
                    ((Querypanel) _mD.get_aryList().get(position)).loadTables(schema, table1);
            } catch (Exception e) {
                e.printStackTrace();
                //throw e;
            }
        }
    }

    private String markSelectedColumns(String domainName, String queryFromQueryPanel) {
        String returnedQuery = new CaDataViewHelper(_mD, domainName).processColumns(queryFromQueryPanel, _mD.getSaveFile());
        return returnedQuery;
    }

    public static void centerWindow(Window frame) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        frame.setLocation(screenSize.width / 2 - (frameSize.width / 2), screenSize.height / 2 - (frameSize.height / 2));
    }

    private String trimExtraTables(String SQLStmt) {
        HashSet _set = new HashSet();
        String subStr = SQLStmt.substring(SQLStmt.indexOf("FROM"));
        EmptyStringTokenizer empt = new EmptyStringTokenizer(subStr, ",");
        while (empt.hasMoreTokens()) {
            _set.add(empt.nextToken());
        }
        int last = _set.toString().indexOf("]");
        return _set.toString().substring(1, last);
    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.19  2007/11/16 17:19:00  wangeug
 * update SDTM module
 *
 * Revision 1.18  2007/11/05 15:42:19  jayannah
 * Changed the message/wording
 *
 * Revision 1.17  2007/10/16 14:10:27  jayannah
 * Changed the absolute path to getName during times when the pop up is displayed to the world;
 * made changes so that the Tables cannot be mapped
 *
 * Revision 1.16  2007/09/19 16:52:12  jayannah
 * added the animated icon to the process so that the user has the sense of a background process
 *
 * Revision 1.15  2007/09/14 15:38:54  jayannah
 * Changed the name from Query builder to data viewer
 *
 * Revision 1.14  2007/09/13 15:53:54  jayannah
 * decreased the size of the font and increased the window length
 *
 * Revision 1.13  2007/09/13 14:11:16  jayannah
 * Added a comment to the map file for the user not to change the sql statements
 *
 * Revision 1.12  2007/09/13 13:51:41  jayannah
 * Changes made to ensure that flow is correct, the save , reopen etc
 *
 * Revision 1.11  2007/09/11 16:49:16  jayannah
 * to over come build issues
 *
 * Revision 1.10  2007/09/11 15:31:01  jayannah
 * added code to hold the previous values and the condition when the user does not choose to go thro the data viewer
 *
 * Revision 1.9  2007/08/22 15:01:24  jayannah
 * new changes to display that the sql jar is not available
 *
 * Revision 1.8  2007/08/16 19:39:46  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */
