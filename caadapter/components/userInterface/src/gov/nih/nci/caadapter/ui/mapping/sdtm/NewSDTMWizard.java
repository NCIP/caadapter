package gov.nih.nci.caadapter.ui.mapping.sdtm;

/**
 * <!-- LICENSE_TEXT_START --> $Header: /share/content/cvsroot/hl7sdk/src/gov/nih/nci/caAdapter/ui/db2SDTM/NewSDTMWizard.java,v 1.1 2006/10/30 16:01:50 jayannah
 * Exp $ ****************************************************************** COPYRIGHT NOTICE ******************************************************************
 * The caAdapter Software License, Version 3.2 Copyright Notice. Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer
 * Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in
 * binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution. 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment: "This
 * product includes software developed by the SAIC and the National Cancer Institute." If no such end-user documentation is to be included, this acknowledgment
 * shall appear in the software itself, wherever such third-party acknowledgments normally appear. 3. The names "The National Cancer Institute", "NCI" and
 * "SAIC" must not be used to endorse or promote products derived from this software. 4. This license does not authorize the incorporation of this software into
 * any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 5. THIS
 * SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. <!--
 * LICENSE_TEXT_END -->
 */

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.CaadapterFileFilter;
import gov.nih.nci.caadapter.ui.mapping.sdtm.actions.QBTransformAction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: jayannah $
 * @version Since caAdapter v3.2 revision $Revision: 1.6 $
 */
@SuppressWarnings("serial")
public class NewSDTMWizard extends JDialog implements ActionListener {

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

    JPanel defineXMLPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    JPanel fileZone1 = new JPanel(new GridLayout(7, 1, 2, 2));

    JPanel fileZone2 = new JPanel(new GridLayout(2, 3));

    JPanel csvPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    // New fields begin here
    JButton directoryLocation, hl7MessageFilelocation, hl7csvlocation, defineXMLLocation;

    JTextField dirLocTextField, hl7MesLocTextField, hl7csvTextField, defineXMLTextField;

    JFileChooser directoryLoc, HL7V24Message, saveCSVLocation, choosedefineXMLLocation;

    JButton process = new JButton("  Transform  ");

    JButton cancel = new JButton("   Cancel    ");

    // JButton next = new JButton("Next");
    // JPanel navZone1 = new JPanel(new GridLayout(3, 1, 2, 2));
    JPanel _bPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

    JPanel emptyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    File directory, hl7MessageFile, csvSaveFile, defineXMLFile;

    String _saveCSV = "";

    String _defineXML = "";

    //HashMap prefs;

    /*
      * The SCS file that is generated
      */
    String _genSCSFileName = "";

    AbstractMainFrame callingFrame;

    boolean isDatabase;

    String defineXMLFileLocation;


    public NewSDTMWizard(AbstractMainFrame _callingFrame) {
        try {
            JFileChooser fc = new JFileChooser(Config.CAADAPTER_HOME_DIR_TAG);
            CaadapterFileFilter filter = new CaadapterFileFilter();
            filter.addExtension("map");
            filter.setDescription("map");
            fc.setFileFilter(filter);
            fc.setDialogTitle("Open a RDS map file.....");
            fc.showOpenDialog(_callingFrame);
            File selFile = fc.getSelectedFile();
            if (!selFile.getName().endsWith("map")) {
                JOptionPane.showMessageDialog(_callingFrame, "Please a valid map file", "Invalid file", JOptionPane.ERROR_MESSAGE);
            }
            preProcessMapFile(selFile.getAbsolutePath());
            if (isDatabase) {
                new QBTransformAction(_callingFrame, selFile.getAbsolutePath(), defineXMLFileLocation, null);
            } else {
                transformSCS(_callingFrame, selFile.getAbsolutePath());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void transformSCS(AbstractMainFrame _callingFrame, String mapFile) {
        // super("Create SDTM Structure");

        callingFrame = _callingFrame;
        Color logColor = new Color(220, 220, 220);
        //setBounds(355, 355, 670, 180);
        setLocation(255, 355);
        setResizable(false);
        sourceField1.setEnabled(false);
        reportField1.setEnabled(false);
        // sourceField1.setText(sourceName1);
        reportField1.setText(reportName1);
        dirBrowsePanel.setBackground(logColor);
        hl7MessagePanel.setBackground(logColor);
        csvPanel.setBackground(logColor);
        /**
         * new code begin
         */
        @SuppressWarnings("unused") String _defaultLoc = System.getProperty("user.dir") + "\\workingspace\\examples";
        directoryLoc = new JFileChooser(_defaultLoc);
        // directoryLoc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        HL7V24Message = new JFileChooser(_defaultLoc);
        directoryLocation = new JButton("Browse..");
        directoryLocation.addActionListener(this);
        dirLocTextField = new JTextField("");
        dirLocTextField.setColumns(30);
        JLabel dirLabel = new JLabel("Data File (csv)");
        JLabel hl7fileLabel = new JLabel("Choose the Map File");
        //JLabel defineXMLfileLabel = new JLabel("Choose the SCS File");
        JLabel hl7csvfileLabel = new JLabel("Path to save the SDTM TXT file");
        fileZone2.add(dirLabel);
        fileZone2.add(dirLocTextField);
        directoryLocation.setSize(2, 10);
        fileZone2.add(directoryLocation);
        hl7MessageFilelocation = new JButton("Browse..");
        hl7MessageFilelocation.addActionListener(this);
        hl7MesLocTextField = new JTextField("");
        defineXMLLocation = new JButton("Browse..");
        defineXMLLocation.addActionListener(this);
        defineXMLTextField = new JTextField("");
        defineXMLTextField.setColumns(30);
        defineXMLPanel.setBackground(logColor);
        //fileZone2.add(defineXMLfileLabel);
        //fileZone2.add(defineXMLTextField);
        //fileZone2.add(defineXMLLocation);
        hl7csvlocation = new JButton("Browse..");
        hl7csvlocation.addActionListener(this);
        hl7csvTextField = new JTextField("");
        hl7MesLocTextField.setColumns(30);
//        fileZone2.add(hl7fileLabel);
//        fileZone2.add(hl7MesLocTextField);
//        fileZone2.add(hl7MessageFilelocation);
        saveCSVLocation = new JFileChooser(_defaultLoc);
        choosedefineXMLLocation = new JFileChooser(_defaultLoc);
        hl7csvTextField.setColumns(30);
        fileZone2.add(hl7csvfileLabel);
        fileZone2.add(hl7csvTextField);
        fileZone2.add(hl7csvlocation);
        emptyPanel.setBackground(logColor);
        //_bPanel.setBackground(logColor);
        EtchedBorder lineBorder = (EtchedBorder) BorderFactory.createEtchedBorder();
        //createLineBorder(Color.black);
        _bPanel.setBorder(lineBorder);
        emptyPanel.add(new JLabel("                                                     "));
        _bPanel.add(process);
        process.addActionListener(this);
        _bPanel.add(new JLabel("    "));
        setModal(true);
        setTitle("SDTM Text File");
        _bPanel.add(cancel);
        cancel.addActionListener(this);
        /**
         * new code end        
         */
        fileZone1.add(dirBrowsePanel);
        fileZone1.add(hl7MessagePanel);
        fileZone1.add(defineXMLPanel);
        fileZone1.add(csvPanel);
        fileZone1.add(emptyPanel);
        fileZone1.add(emptyPanel);
        fileZone1.add(_bPanel);
        fileZone1.setBackground(logColor);
        //fileZone2.add(new JLabel(""));fileZone2.add(process);fileZone2.add(cancel);
        fileZone2.setBorder(new TitledBorder("File Settings"));
        // fileZone1.setBorder(new TitledBorder("Command"));
        curDir = System.getProperty("user.dir") + File.separator;
        javax.swing.JScrollPane s = new javax.swing.JScrollPane();// javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED,
        // javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        s.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        s.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        log.add(s);
        log.setEnabled(false);
        log.setBorder(new TitledBorder("Log Information"));
        log.setBackground(logColor);
        main.add("North", fileZone2);
        main.add("South", _bPanel);
        this.getContentPane().add("Center", main);
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
            CaadapterFileFilter filter = new CaadapterFileFilter();
            filter.addExtension("csv");
            // filter.setDescription("csv");
            directoryLoc.setFileFilter(filter);
            int returnVal = directoryLoc.showOpenDialog(NewSDTMWizard.this);
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
            CaadapterFileFilter filter = new CaadapterFileFilter();
            filter.addExtension("map");
            // filter.setDescription("map");
            HL7V24Message.setFileFilter(filter);
            int returnVal = HL7V24Message.showOpenDialog(NewSDTMWizard.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                hl7MessageFile = HL7V24Message.getSelectedFile();
                hl7MesLocTextField.setText(hl7MessageFile.getAbsolutePath().toString());
                hl7MesLocTextField.setEnabled(false);
            } else {
                log.append("command cancelled by user." + newline);
            }
        } else if (e.getSource() == hl7csvlocation) {
            String CSVSaveFileName = "";
            CaadapterFileFilter filter = new CaadapterFileFilter();
            //filter.addExtension("txt");
            saveCSVLocation.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            // filter.setDescription("txt");
            //saveCSVLocation.setFileFilter(filter);
            int returnVal = saveCSVLocation.showOpenDialog(NewSDTMWizard.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                csvSaveFile = saveCSVLocation.getSelectedFile();
                _saveCSV = csvSaveFile.getAbsolutePath();
                hl7csvTextField.setText(_saveCSV);
                hl7csvTextField.setEnabled(false);
            } else {
                log.append("command cancelled by user." + newline);
            }
        }
        if (e.getSource() == defineXMLLocation) {
            CaadapterFileFilter filter = new CaadapterFileFilter();
            filter.addExtension("scs");
            choosedefineXMLLocation.setFileFilter(filter);
            int returnVal = choosedefineXMLLocation.showOpenDialog(NewSDTMWizard.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                defineXMLFile = choosedefineXMLLocation.getSelectedFile();
                _defineXML = defineXMLFile.getAbsolutePath();
                defineXMLTextField.setText(_defineXML);
                defineXMLTextField.setEnabled(false);
            } else {
                log.append("command cancelled by user." + newline);
            }
        } else if (e.getSource() == process) {
            try {
                String CSVSaveFileName = "";
                if (csvSaveFile != null) {
                    CSVSaveFileName = hl7csvTextField.getText();
                }
                try {
                    this.dispose();
                    new RDSTransformer(callingFrame, new File(hl7MessageFile.getAbsolutePath().toString()), directory.getAbsolutePath().toString(), _saveCSV);
                } catch (RuntimeException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                log.append("Created the \"" + _genSCSFileName + "\" successfully" + newline);
                log.append("Created the \"" + CSVSaveFileName + "\" successfully" + newline);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                log.append(e1.getMessage());
            }
        } else if (e.getSource() == cancel) {
            this.dispose();
        } else if (e.getSource() == directoryLocation) {
        }
    }

    private void preProcessMapFile(String mapFileName) throws Exception {

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(new File(mapFileName));
        System.out.println("Root element of the doc is " + doc.getDocumentElement().getNodeName());
        NodeList compLinkNodeList = doc.getElementsByTagName("components");

        for (int s = 0; s < compLinkNodeList.getLength(); s++) {
            Node node = compLinkNodeList.item(s);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element firstCompElement = (Element) node;
                NodeList targetNode = firstCompElement.getElementsByTagName("component");
                Element targetName1 = (Element) targetNode.item(0);
                targetName1.getAttribute("location").toString();
                if (targetName1.getAttribute("kind").toString().equalsIgnoreCase("SCS")) {

                } else if (targetName1.getAttribute("kind").toString().equalsIgnoreCase("Database")) {
                    isDatabase = true;
                }
                Element targetName2 = (Element) targetNode.item(1);
                if (targetName2.getAttribute("kind").toString().equalsIgnoreCase("XML")) {
                    defineXMLFileLocation = targetName2.getAttribute("location").toString();
                }
            }
        }
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //  new NewSDTMWizard(null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
