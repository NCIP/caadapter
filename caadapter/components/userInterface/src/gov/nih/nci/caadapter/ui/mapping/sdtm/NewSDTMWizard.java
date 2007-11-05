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
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.CaadapterFileFilter;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
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
 * @version Since caAdapter v3.2 revision $Revision: 1.16 $
 */
@SuppressWarnings("serial")
public class NewSDTMWizard extends JDialog implements ActionListener {
    //
    private JTextField dataFileInputField = null;
    private JTextField csvDirInoutField = null;
    public static final String DATA_FILE_BROWSE_MODE = "Data File:";
    public static final String MAP_FILE_BROWSE_MODE = "Choose save directoy:";
    private static final String OK_COMMAND = "Process";
    private static final String CANCEL_COMMAND = "Cancel";
    private File selFile = null;
    private File dataFile, dirLoc = null;
    private String _genSCSFileName = null;
    private AbstractMainFrame callingFrame = null;
    private boolean isDatabase = false;
    private String defineXMLFileLocation = null;
    private JFileChooser csvUserDirectoryLoc = null;

    public NewSDTMWizard(final AbstractMainFrame _callingFrame) {
        super(_callingFrame);
        final JDialog preFrame = new JDialog(_callingFrame, true);
        callingFrame = _callingFrame;
        //final JDialog preFrame = new JDialog();
        preFrame.setLocation(400, 300);
        preFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        preFrame.setTitle("Create RDS Txt file....");
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        //
        JPanel centerPan = new JPanel(new GridLayout(1, 3));
        centerPan.setBorder(new TitledBorder("Choose a Map file"));
        centerPan.add(new JLabel("Select Map file"));
        JButton button = new JButton("Browse");
        final JTextField textField = new JTextField();
        textField.setEnabled(false);
        button.setPreferredSize(new Dimension(100, 25));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                handleButtonAction(_callingFrame);
                if (selFile != null)
                    textField.setText(selFile.getAbsolutePath());
            }
        });
        centerPan.add(textField);
        centerPan.add(button);
        //
        JPanel butPan = new JPanel();
        butPan.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        JButton okBut = new JButton("OK");
        okBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                preFrame.dispose();
                try {
                    if (selFile != null) {
                        preProcessMapFile(selFile.getAbsolutePath());
                        if (isDatabase) {
                            new QBTransformAction(_callingFrame, selFile.getAbsolutePath(), defineXMLFileLocation, null);
                        } else {
                            // transformSCS(_callingFrame, selFile.getAbsolutePath());
                            collectDataAndDirectoryName();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        JButton canBut = new JButton("Cancel");
        canBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                preFrame.dispose();
            }
        }
        );
        butPan.add(okBut);
        butPan.add(canBut);
        //
        mainPanel.add(centerPan, BorderLayout.CENTER);
        mainPanel.add(butPan, BorderLayout.SOUTH);
        preFrame.add(mainPanel);
        preFrame.pack();
        preFrame.setVisible(true);
    }

    private void handleButtonAction(AbstractMainFrame _callingFrame) {
        try {
            JFileChooser fc = new JFileChooser(FileUtil.getWorkingDirPath()+File.separator+"workingspace"+File.separator+"RDS_Example");
            CaadapterFileFilter filter = new CaadapterFileFilter();
            filter.addExtension("map");
            filter.setDescription("map");
            fc.setFileFilter(filter);
            fc.setDialogTitle("Open a RDS map file.....");
            fc.showOpenDialog(_callingFrame);
            selFile = fc.getSelectedFile();
            if (selFile != null) {
                if (!selFile.getName().endsWith("map")) {
                    JOptionPane.showMessageDialog(_callingFrame, "Please a valid map file", "Invalid file", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void collectDataAndDirectoryName() {
        csvUserDirectoryLoc = new JFileChooser();
        csvUserDirectoryLoc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        this.setLayout(new BorderLayout());
        this.setTitle("SCS Transformation");
        JPanel centerPanel = new JPanel(new GridBagLayout());
        Insets insets = new Insets(5, 5, 5, 5);
        JLabel dataFileLabel = new JLabel(DATA_FILE_BROWSE_MODE);
        centerPanel.add(dataFileLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        dataFileInputField = new JTextField();
        dataFileInputField.setPreferredSize(new Dimension(350, 25));
        centerPanel.add(dataFileInputField, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        JButton dataFileBrowseButton = new JButton("Browse");
        dataFileBrowseButton.setActionCommand("data");
        dataFileBrowseButton.addActionListener(this);
        centerPanel.add(dataFileBrowseButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
        JLabel mapFileLabel = new JLabel(MAP_FILE_BROWSE_MODE);
        centerPanel.add(mapFileLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        csvDirInoutField = new JTextField();
        csvDirInoutField.setPreferredSize(new Dimension(350, 25));
        centerPanel.add(csvDirInoutField, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        JButton mapFileBrowseButton = new JButton("Browse");
        mapFileBrowseButton.setActionCommand("dir");
        mapFileBrowseButton.addActionListener(this);
        centerPanel.add(mapFileBrowseButton, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
        JPanel southPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        JButton okButton = new JButton(OK_COMMAND);
        okButton.setMnemonic('O');
        okButton.addActionListener(this);
        JButton cancelButton = new JButton(CANCEL_COMMAND);
        cancelButton.setMnemonic('C');
        cancelButton.addActionListener(this);
        JPanel tempPanel = new JPanel(new GridLayout(1, 2));
        tempPanel.add(okButton);
        tempPanel.add(cancelButton);
        buttonPanel.add(tempPanel);
        southPanel.add(buttonPanel, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);
        this.setLocation(300, 300);
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
        String command = e.getActionCommand();
        if (command.equalsIgnoreCase(OK_COMMAND)) {
            try {
                this.dispose();
                String dataFileInput, directoryInput;
                // new RDSTransformer(callingFrame, new File(hl7MessageFile.getAbsolutePath().toString()), directory.getAbsolutePath().toString(), _saveCSV);
                try {
                    dataFileInput=dataFile.getAbsolutePath();
                } catch (Exception e1) {
                    dataFileInput=dataFileInputField.getText();
                }
                try {
                    directoryInput=dirLoc.getAbsolutePath();
                } catch (Exception e1) {
                    directoryInput=csvDirInoutField.getText();
                }
                new RDSTransformer(callingFrame, selFile, dataFileInput, directoryInput);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } else if (command.equalsIgnoreCase(CANCEL_COMMAND)) {
            System.out.println("cancel");
            this.dispose();
        } else if (command.equalsIgnoreCase("data")) {
            dataFile = DefaultSettings.getUserInputOfFileFromGUI(this, ".csv", "Choose CSV file ...", false, false);
            dataFileInputField.setText(dataFile.getAbsolutePath());
            dataFileInputField.setEnabled(false);
        } else if (command.equalsIgnoreCase("dir")) {
             int returnVal = csvUserDirectoryLoc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                 dirLoc = csvUserDirectoryLoc.getSelectedFile();
                csvDirInoutField.setText(dirLoc.getAbsolutePath().toString());
                csvDirInoutField.setEnabled(false);
            }
        }
    }

    private void preProcessMapFile(String mapFileName) throws Exception {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(new File(mapFileName));
        //System.out.println("Root element of the doc is " + doc.getDocumentElement().getNodeName());
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
            new NewSDTMWizard(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}