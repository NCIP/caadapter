/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.mapping.V2V3.actions;

import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.CaadapterFileFilter;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.mapping.V2V3.V2ConverterToSCSPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * This is data viewer main window. The RDS module calls this class with arguments to
 * show tables to the user
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.5 $
 *          $Date: 2008-06-09 19:54:05 $
 */
public class NewV2V3GUI extends JDialog implements ActionListener {
    //
    public static final String HL7_RESOURCE_DIR = "Resource Dir";
    public static final String HL7_MESSAGE_FILE = "v2 Message File";
    public static final String SCS_SAVE_FILE = "SCS File";
    public static final String CSV_SAVE_FILE = "CSV File";
    private static final String OK_COMMAND = "Process";
    private static final String CANCEL_COMMAND = "Cancel";
    private static final String ADVANCED = "Advanced";
    //
    private JFileChooser HL7directoryLoc = null;
    private JFileChooser HL7V24Message = null;
    private JFileChooser saveCSVLocation = null;
    private JFileChooser scsSaveFileLocation = null;
    //
    private JTextField HL7ResourceDirInputField = new JTextField();
    private JTextField HL7MessageFileInputField = new JTextField();
    private JTextField CSVSaveFileInputField = new JTextField();
    private JTextField SCSSaveFileInputField = new JTextField();
    //
    private File HL7directory = null;
    private File hl7MessageFile = null;
    private File csvSaveFile = null;
    private File scsSaveFile = null;
    private String _saveCSV = null;
    private String _saveSCS = null;
    private AbstractMainFrame callingFrame = null;
    String directoryPath = null;

    //
    public NewV2V3GUI(AbstractMainFrame _callingFrame) {
        super(_callingFrame, true);
        directoryPath = FileUtil.getWorkingDirPath()+File.separator+"workingspace"+File.separator+"HL7_V2_to_V3_Example";
        System.out.println(FileUtil.getWorkingDirPath());
        callingFrame = _callingFrame;
        this.setLayout(new BorderLayout());
        this.setTitle("Generate SCS & CSV Files");
        initialize();
        HL7directoryLoc = new JFileChooser(FileUtil.getV2DataDirPath());
        HL7directoryLoc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        HL7V24Message = new JFileChooser(directoryPath);
        saveCSVLocation = new JFileChooser(directoryPath);
        scsSaveFileLocation = new JFileChooser(directoryPath);
        //
        pack();
        setVisible(true);
    }

    private void initialize() {
        JPanel centerPanel = new JPanel(new GridBagLayout());
        Insets insets = new Insets(5, 5, 5, 5);
        //1st row
        JLabel HL7resourceDir = new JLabel(HL7_RESOURCE_DIR);
        centerPanel.add(HL7resourceDir, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        HL7ResourceDirInputField.setPreferredSize(new Dimension(350, 25));
        centerPanel.add(HL7ResourceDirInputField, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        JButton HL7DirBrowseButton = new JButton("Browse");
        HL7DirBrowseButton.setActionCommand("1");
        HL7DirBrowseButton.addActionListener(this);
        centerPanel.add(HL7DirBrowseButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
        //2nd row
        JLabel HL7MessageFileLabel = new JLabel(HL7_MESSAGE_FILE);
        centerPanel.add(HL7MessageFileLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        HL7MessageFileInputField.setPreferredSize(new Dimension(350, 25));
        centerPanel.add(HL7MessageFileInputField, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        JButton HL7MessageFileBrowseButton = new JButton("Browse");
        HL7MessageFileBrowseButton.setActionCommand("2");
        HL7MessageFileBrowseButton.addActionListener(this);
        centerPanel.add(HL7MessageFileBrowseButton, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
        //3rd row
        JLabel CSVSaveFileLabel = new JLabel(CSV_SAVE_FILE);
        centerPanel.add(CSVSaveFileLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        CSVSaveFileInputField.setPreferredSize(new Dimension(350, 25));
        centerPanel.add(CSVSaveFileInputField, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        JButton CSVSaveFileBrowseButton = new JButton("Browse");
        CSVSaveFileBrowseButton.setActionCommand("3");
        CSVSaveFileBrowseButton.addActionListener(this);
        centerPanel.add(CSVSaveFileBrowseButton, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
        //4th row
        JLabel SCSSaveFileLabel = new JLabel(SCS_SAVE_FILE);
        centerPanel.add(SCSSaveFileLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        SCSSaveFileInputField.setPreferredSize(new Dimension(350, 25));
        centerPanel.add(SCSSaveFileInputField, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        JButton SCSSaveFileBrowseButton = new JButton("Browse");
        SCSSaveFileBrowseButton.setActionCommand("4");
        SCSSaveFileBrowseButton.addActionListener(this);
        centerPanel.add(SCSSaveFileBrowseButton, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
        //
        JPanel southPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        //
        JButton advancedButton = new JButton(ADVANCED);
        advancedButton.setActionCommand("5");
        advancedButton.addActionListener(this);
        //
        JButton okButton = new JButton(OK_COMMAND);
        okButton.setActionCommand("6");
        okButton.addActionListener(this);
        //
        JButton cancelButton = new JButton(CANCEL_COMMAND);
        cancelButton.setActionCommand("7");
        cancelButton.addActionListener(this);
        //
        JPanel tempPanel = new JPanel(new GridLayout(1, 2));
        tempPanel.add(advancedButton);
        tempPanel.add(okButton);
        tempPanel.add(cancelButton);
        buttonPanel.add(tempPanel);
        southPanel.add(buttonPanel, BorderLayout.NORTH);
        //
        this.add(centerPanel, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);
        this.setLocation(300, 300);
        HL7ResourceDirInputField.setText(FileUtil.getV2DataDirPath());
    }

    public static void main(String[] args) {
        new NewV2V3GUI(null);
    }

    public void actionPerformed(ActionEvent e) {
        int command = new Integer(e.getActionCommand()).intValue();
        int returnVal = -1;
        CaadapterFileFilter filter;
        switch (command) {
            case 1:
                returnVal = HL7directoryLoc.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    HL7directory = HL7directoryLoc.getSelectedFile();
                    HL7ResourceDirInputField.setText(HL7directory.getAbsolutePath().toString());
                    HL7ResourceDirInputField.setEnabled(false);
                }
                break;
            case 2:
                returnVal = HL7V24Message.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    hl7MessageFile = HL7V24Message.getSelectedFile();
                    HL7MessageFileInputField.setText(hl7MessageFile.getAbsolutePath().toString());
                    HL7MessageFileInputField.setEnabled(false);
                }
                break;
            case 3:
                filter = new CaadapterFileFilter();
                filter.addExtension("csv");
                filter.setDescription("csv");
                saveCSVLocation.setFileFilter(filter);
                returnVal = saveCSVLocation.showSaveDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    csvSaveFile = saveCSVLocation.getSelectedFile();
                    if (csvSaveFile.getAbsolutePath().endsWith("csv")) {
                        _saveCSV = csvSaveFile.getAbsolutePath();
                    } else {
                        _saveCSV = csvSaveFile.getAbsolutePath() + ".csv";
                    }
                    CSVSaveFileInputField.setText(_saveCSV);
                    CSVSaveFileInputField.setEnabled(false);
                }
                break;
            case 4:
                filter = new CaadapterFileFilter();
                filter.addExtension("scs");
                filter.setDescription("scs");
                scsSaveFileLocation.setFileFilter(filter);
                returnVal = scsSaveFileLocation.showSaveDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    scsSaveFile = scsSaveFileLocation.getSelectedFile();
                    if (scsSaveFile.getAbsolutePath().endsWith("scs")) {
                        _saveSCS = scsSaveFile.getAbsolutePath();
                    } else {
                        _saveSCS = scsSaveFile.getAbsolutePath() + ".scs";
                    }
                    SCSSaveFileInputField.setText(_saveSCS);
                    SCSSaveFileInputField.setEnabled(false);
                }
                break;
            case 5:
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
                break;
            case 6:
                doProcess();
                break;
            case 7:
                this.dispose();
                break;
            default:
                break;
        }
    }

    private void doProcess() {
        String newline = "\n";
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
            CSVSaveFileName = CSVSaveFileInputField.getText();
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
            SCSSaveFileName = SCSSaveFileInputField.getText();
            if (!SCSSaveFileName.endsWith("scs")) {
                SCSSaveFileName = SCSSaveFileName + ".scs";
            }
        }
        System.out.println(HL7ResourceDirInputField.getText());
        System.out.println(HL7MessageFileInputField.getText());
        System.out.println();
        try {
            if (new gov.nih.nci.caadapter.ui.mapping.V2V3.MappingMain().execute(HL7ResourceDirInputField.getText(), hl7MessageFile.getAbsolutePath(), CSVSaveFileName, SCSSaveFileName)) {
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
    }
}
