/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/V2V3/actions/MapV2V3.java,v 1.8 2008-06-09 19:54:05 phadkes Exp $



 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.ui.mapping.V2V3.actions;

import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.CaadapterFileFilter;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.mapping.V2V3.V2ConverterToSCSPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * This class provides a SWING panel to help the user to browse the files and choose the necessary files and directories
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.2 revision $Revision: 1.8 $ date $Date: 2008-06-09 19:54:05 $
 */
@SuppressWarnings("serial")
public class MapV2V3 extends JDialog implements ActionListener {
    String curDir;
    String sourceName1 = "";
    String reportName1 = "";
    // main is split into north/center regions
    JPanel north = new JPanel(new FlowLayout());
    JPanel center = new JPanel(new BorderLayout());
    // BorderLayout panel to hold all JPanels using the Center region
    JPanel main = new JPanel(new BorderLayout());
    JTextArea log = new JTextArea();
    File sourceFile;
    File reportFile = new File(reportName1);
    // declare file area components
    JLabel sourceLabel1 = new JLabel("Source: ");
    JTextField sourceField1 = new JTextField(12);
    JButton sourceBrow1 = new JButton("Browse...");
    JPanel dirBrowsePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel reportLabel1 = new JLabel("Report: ");
    // JLabel reportLabel1 = new JLabel("Report: ");
    JTextField reportField1 = new JTextField(12);
    JButton reportBrow1 = new JButton("Browse...");
    JPanel hl7MessagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    //JPanel fileZone1 = new JPanel(new GridLayout(6, 3, 2, 2));
    JPanel fileZone1 = new JPanel(new GridLayout(6, 3));
    JPanel csvPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JPanel scsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    // New fields begin here
    JButton directoryLocation, hl7MessageFilelocation, hl7csvlocation, scsLocation;
    JTextField dirLocTextField, hl7MesLocTextField, hl7csvTextField, scsSaveTextField;
    JFileChooser directoryLoc, HL7V24Message, saveCSVLocation, scsSaveFileLocation;
    JButton process = new JButton("Process");
    JButton next = new JButton("Next");
    JButton cancel = new JButton("Cancel");
    JButton advanced = new JButton("Advanced >>");
    // JPanel navZone1 = new JPanel(new GridLayout(3, 1, 2, 2));
    JPanel _bPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JPanel emptyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    File directory, hl7MessageFile, csvSaveFile, scsSaveFile;
    String _saveCSV = "", _saveSCS = "";
    /*
    * The SCS file that is generated
    */ String _genSCSFileName = "";
    AbstractMainFrame callingFrame;

    public MapV2V3(AbstractMainFrame _callingFrame) {
        new NewV2V3GUI(_callingFrame);
    }
    public MapV2V3(AbstractMainFrame _callingFrame, String deprected) {
        callingFrame = _callingFrame;
        this.setModal(true);
        this.setTitle("Map HL7 v2 to HL7 v3 messages");
        setLocation(200, 300);
        setResizable(false);
        sourceField1.setEnabled(false);
        reportField1.setEnabled(false);
        reportField1.setText(reportName1);
        /**
         * new code begin
         */
        @SuppressWarnings("unused") String _defaultLoc = System.getProperty("user.dir") + "\\workingspace\\examples\\V2V3 Mapping Examples";
        directoryLoc = new JFileChooser(FileUtil.getV2DataDirPath());
        directoryLoc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        HL7V24Message = new JFileChooser(_defaultLoc);
        directoryLocation = new JButton("Browse..");
        directoryLocation.addActionListener(this);
        dirLocTextField = new JTextField(FileUtil.getV2DataDirPath());
        dirLocTextField.setColumns(20);
        JLabel dirLabel = new JLabel("Select HL7 v2.x Resources Directory");
        JLabel hl7fileLabel = new JLabel("Select HL7 v2.x Message file");
        JLabel hl7csvfileLabel = new JLabel("Save CSV file");
        JLabel hl7scsfileLabel = new JLabel("Save SCS file");
        dirBrowsePanel.add(dirLabel);
        dirBrowsePanel.add(dirLocTextField);
        dirBrowsePanel.add(directoryLocation);
        hl7MessageFilelocation = new JButton("Browse..");
        hl7MessageFilelocation.addActionListener(this);
        hl7MesLocTextField = new JTextField("");
        hl7csvlocation = new JButton("Browse..");
        hl7csvlocation.addActionListener(this);
        hl7csvTextField = new JTextField("");
        hl7MesLocTextField.setColumns(20);
        hl7MessagePanel.add(hl7fileLabel);
        hl7MessagePanel.add(hl7MesLocTextField);
        hl7MessagePanel.add(hl7MessageFilelocation);
        scsSaveTextField = new JTextField("");
        scsLocation = new JButton("Browse..");
        scsLocation.addActionListener(this);
        hl7csvTextField = new JTextField("");
        saveCSVLocation = new JFileChooser(_defaultLoc);
        scsSaveFileLocation = new JFileChooser(_defaultLoc);
        hl7csvTextField.setColumns(20);
        csvPanel.add(hl7csvfileLabel);
        csvPanel.add(hl7csvTextField);
        csvPanel.add(hl7csvlocation);
        scsSaveTextField.setColumns(30);
        scsPanel.add(hl7scsfileLabel);
        scsPanel.add(scsSaveTextField);
        scsPanel.add(scsLocation);
        //emptyPanel.setBackground(logColor);
        // _bPanel.setBackground(logColor);
        emptyPanel.add(new JLabel(""));
        _bPanel.add(advanced);
        advanced.addActionListener(this);
        _bPanel.add(new JLabel("    "));
        _bPanel.add(process);
        process.addActionListener(this);
        _bPanel.add(new JLabel("    "));
        // _bPanel.add( next);
        next.addActionListener(this);
        _bPanel.add(new JLabel("    "));
        _bPanel.add(cancel);
        cancel.addActionListener(this);
        fileZone1.add(dirLabel);
        fileZone1.add(dirLocTextField);
        fileZone1.add(directoryLocation);
        //
        //fileZone1.add(hl7MessagePanel);
        fileZone1.add(hl7fileLabel);
        fileZone1.add(hl7MesLocTextField);
        fileZone1.add(hl7MessageFilelocation);
        //
        //fileZone1.add(csvPanel);
        fileZone1.add(hl7csvfileLabel);
        fileZone1.add(hl7csvTextField);
        fileZone1.add(hl7csvlocation);
        //
        //fileZone1.add(scsPanel);
        fileZone1.add(hl7scsfileLabel);
        fileZone1.add(scsSaveTextField);
        fileZone1.add(scsLocation);
        //
        //fileZone1.add(emptyPanel);
        fileZone1.add(new JLabel());
        fileZone1.add(new JLabel());
        fileZone1.add(new JLabel());
        //
        //fileZone1.add(_bPanel);
        //advanced.setPreferredSize(new Dimension(5,2));
        fileZone1.add(advanced);
        fileZone1.add(process);
        fileZone1.add(cancel);
        fileZone1.setBorder(new TitledBorder("File Settings"));
        curDir = System.getProperty("user.dir") + File.separator;
        log.setEnabled(false);
        log.setBorder(new TitledBorder("Log Information"));
        main.add("North", fileZone1);
        this.getContentPane().add("Center", main);
        advanced.setPreferredSize(new Dimension(135, 25));
        pack();
        setVisible(true);
    }

    protected ImageIcon createImageIcon(String icon) {
        java.net.URL imgURL = getClass().getClassLoader().getResource(icon);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: ");
            return null;
        }
    }

    public void actionPerformed(ActionEvent e) {
        String newline = "\n";
        if (e.getSource() == directoryLocation) {
            try {
            } catch (RuntimeException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            int returnVal = directoryLoc.showOpenDialog(MapV2V3.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                directory = directoryLoc.getSelectedFile();
                // This is where a real application would open the file.
                dirLocTextField.setText(directory.getAbsolutePath().toString());
                dirLocTextField.setEnabled(false);
            } else {
                log.append("command cancelled by user." + newline);
            }
            log.setCaretPosition(log.getDocument().getLength());
        } else if (e.getSource() == hl7MessageFilelocation) {
            int returnVal = HL7V24Message.showOpenDialog(MapV2V3.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                hl7MessageFile = HL7V24Message.getSelectedFile();
                hl7MesLocTextField.setText(hl7MessageFile.getAbsolutePath().toString());
                hl7MesLocTextField.setEnabled(false);
            } else {
                log.append("command cancelled by user." + newline);
            }
        } else if (e.getSource() == hl7csvlocation) {
            CaadapterFileFilter filter = new CaadapterFileFilter();
            filter.addExtension("csv");
            filter.setDescription("csv");
            saveCSVLocation.setFileFilter(filter);
            int returnVal = saveCSVLocation.showSaveDialog(MapV2V3.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                csvSaveFile = saveCSVLocation.getSelectedFile();
                // This is where a real application would open the file.
                if (csvSaveFile.getAbsolutePath().endsWith("csv")) {
                    _saveCSV = csvSaveFile.getAbsolutePath();
                } else {
                    _saveCSV = csvSaveFile.getAbsolutePath() + ".csv";
                }
                hl7csvTextField.setText(_saveCSV);
                hl7csvTextField.setEnabled(false);
            } else {
                log.append("command cancelled by user." + newline);
            }
        } else if (e.getSource() == process) {
            try {
                @SuppressWarnings("unused") String CSVSaveFileName = "";
                String SCSSaveFileName = "";
                if (csvSaveFile != null) {
                    String tmpStr = csvSaveFile.getAbsolutePath().toString();
                    // String tmpStr1 = scsSaveFile.getAbsolutePath().toString();
                    try {
                        if (tmpStr.endsWith("csv")) {
                            CSVSaveFileName = csvSaveFile.getAbsolutePath();
                        } else {
                            CSVSaveFileName = csvSaveFile.getAbsolutePath() + ".csv";
                        }
                    } catch (Exception ee) {
                        CSVSaveFileName = csvSaveFile.getAbsolutePath() + ".csv";
                    }
                } else {
                    CSVSaveFileName = hl7csvTextField.getText();
                    if (!CSVSaveFileName.endsWith("csv")) {
                        CSVSaveFileName = CSVSaveFileName + ".csv";
                    }
                }
                if (scsSaveFile != null) {
                    String tmpStr = scsSaveFile.getAbsolutePath().toString();
                    // String tmpStr1 = scsSaveFile.getAbsolutePath().toString();
                    try {
                        if (tmpStr.endsWith("scs")) {
                            SCSSaveFileName = scsSaveFile.getAbsolutePath();
                        } else {
                            SCSSaveFileName = scsSaveFile.getAbsolutePath() + ".scs";
                        }
                    } catch (Exception ee) {
                        SCSSaveFileName = scsSaveFile.getAbsolutePath() + ".scs";
                    }
                } else {
                    SCSSaveFileName = scsSaveTextField.getText();
                    if (!SCSSaveFileName.endsWith("scs")) {
                        SCSSaveFileName = SCSSaveFileName + ".scs";
                    }
                }
                try {
                    if (new gov.nih.nci.caadapter.ui.mapping.V2V3.MappingMain().execute(directory.getAbsolutePath(), hl7MessageFile.getAbsolutePath(), CSVSaveFileName, SCSSaveFileName)) {
                        // log.append( "Created the \"" + CSVSaveFileName + "\" successfully" + newline);
                        // log.append( "Created the \"" + SCSSaveFileName + "\" successfully" + newline);
                        this.dispose();
                        JOptionPane.showMessageDialog(callingFrame, "Created the file \"" + CSVSaveFileName + "\" successfully" + newline + "Created the file \"" + SCSSaveFileName + "\" successfully");
                    } else {
                        this.dispose();
                        JOptionPane.showMessageDialog(this, "An error has occured, Please contact Administrator");
                    }
                } catch (Exception e1) {
                    this.dispose();
                    JOptionPane.showMessageDialog(this, "An error has occured, Please contact Administrator " + newline + " Error message is " + e1.getMessage());
                }
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                log.append(e1.getMessage());
            }
        } else if (e.getSource() == next) {
            this.dispose();
            String _sourceFileDir = System.getProperty("user.dir");
            gov.nih.nci.caadapter.ui.mapping.hl7.HL7MappingPanel mp;
            try {
                mp = new gov.nih.nci.caadapter.ui.mapping.hl7.HL7MappingPanel(_sourceFileDir + "\\" + _genSCSFileName, null);
                callingFrame.addNewTab(mp);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                log.append(e1.getMessage());
            }
        } else if (e.getSource() == cancel) {
            this.dispose();
        } else if (e.getSource() == scsLocation) {
            CaadapterFileFilter filter = new CaadapterFileFilter();
            filter.addExtension("scs");
            filter.setDescription("scs");
            scsSaveFileLocation.setFileFilter(filter);
            int returnVal = scsSaveFileLocation.showSaveDialog(MapV2V3.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                scsSaveFile = scsSaveFileLocation.getSelectedFile();
                // This is where a real application would open the file.
                if (scsSaveFile.getAbsolutePath().endsWith("scs")) {
                    _saveSCS = scsSaveFile.getAbsolutePath();
                } else {
                    _saveSCS = scsSaveFile.getAbsolutePath() + ".scs";
                }
                scsSaveTextField.setText(_saveSCS);
                scsSaveTextField.setEnabled(false);
            }
        } else if (e.getSource() == advanced) {
            try {
                this.dispose();
                V2ConverterToSCSPanel v2ConverterPanel = new V2ConverterToSCSPanel(FileUtil.getV2DataDirPath());
                JDialog dialog = v2ConverterPanel.setupDialogBasedOnMainFrame(callingFrame);
                v2ConverterPanel.setNextButtonVisible();
                v2ConverterPanel.setCloseButtonVisible();
                dialog.setSize(v2ConverterPanel.getMinimumSize());
                dialog.setVisible(true);
                DefaultSettings.centerWindow(dialog);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            new MapV2V3(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
/**
 * HISTORY : $Log: not supported by cvs2svn $
 * HISTORY : Revision 1.7  2007/10/10 18:47:51  jayannah
 * HISTORY : updated the reference to NewV2V3GUI changes
 * HISTORY :
 * HISTORY : Revision 1.6  2007/10/10 18:45:30  jayannah
 * HISTORY : Addressed the GUI item changes only
 * HISTORY :
 * HISTORY : Revision 1.5  2007/10/03 18:47:03  umkis
 * HISTORY : Protect from crashing and display a fit message when resouce.zip is absent.
 * HISTORY :
 * HISTORY : Revision 1.4  2007/08/17 01:13:28  umkis
 * HISTORY : generated SCS file using xml path
 * HISTORY :
 * HISTORY : Revision 1.3  2007/08/01 16:02:25  jayannah
 * HISTORY : further modifications for the GUI and resize
 * HISTORY :
 * HISTORY : Revision 1.2  2007/08/01 15:25:16  jayannah
 * HISTORY : changed the look and feel of the v2-v3 GUI
 * HISTORY :
 * HISTORY : Revision 1.1  2007/07/03 19:32:58  wangeug
 * HISTORY : initila loading
 * HISTORY :
 * HISTORY : Revision 1.8  2006/12/08 16:17:18  jayannah
 * HISTORY : changed the menu title for v2v3 mapping pop up
 * HISTORY :
 * HISTORY : Revision 1.7  2006/11/30 16:37:16  jayannah
 * HISTORY : increased the width of the v2 v3 map window to accomadate various desk top sizes
 * HISTORY :
 * HISTORY : Revision 1.6  2006/11/27 20:44:33  jayannah
 * HISTORY : added an advanced button to provide the access to the other version of v2v3 mappings
 * HISTORY : HISTORY : Revision 1.5 2006/11/07 16:02:26 jayannah HISTORY : made the frame modal HISTORY : HISTORY : Revision 1.4
 * 2006/10/12 16:20:00 jayannah HISTORY : made changes to read the examples directory to check the functioning of the V2V3 module HISTORY : HISTORY : Revision
 * 1.3 2006/10/03 17:40:43 jayannah HISTORY : calling the new constructor in HL7MappingPanel (string, string) HISTORY : in order not to step on the Database
 * mapping code HISTORY : HISTORY : Revision 1.2 2006/10/03 15:23:32 jayannah HISTORY : Changed the package names HISTORY : HISTORY : Revision 1.1 2006/10/03
 * 13:53:44 jayannah HISTORY : Adding the file to CVS HISTORY :
 */
