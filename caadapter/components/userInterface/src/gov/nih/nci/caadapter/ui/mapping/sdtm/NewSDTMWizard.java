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
import gov.nih.nci.caadapter.ui.common.CaadapterFileFilter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;

/**
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v3.2 revision $Revision: 1.1 $
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

	/*
	 * The SCS file that is generated
	 */
	String _genSCSFileName = "";

	AbstractMainFrame callingFrame;

	public NewSDTMWizard(AbstractMainFrame _callingFrame) {
		// super("Create SDTM Structure");
		callingFrame = _callingFrame;
		Color logColor = new Color(220, 220, 220);
		setBounds(355, 355, 670, 275);
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
		@SuppressWarnings("unused")
		String _defaultLoc = System.getProperty("user.dir") + "\\workingspace\\examples";
		directoryLoc = new JFileChooser(_defaultLoc);
		// directoryLoc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		HL7V24Message = new JFileChooser(_defaultLoc);
		directoryLocation = new JButton("Browse..");
		directoryLocation.addActionListener(this);
		dirLocTextField = new JTextField("");
		dirLocTextField.setColumns(30);
		JLabel dirLabel = new JLabel("Data File (csv)                                           ");
		JLabel hl7fileLabel = new JLabel("Choose the Map File                               ");
		JLabel defineXMLfileLabel = new JLabel("Choose the SCS File                              ");
		JLabel hl7csvfileLabel = new JLabel("Path to save the SDTM TXT file             ");
		dirBrowsePanel.add(dirLabel);
		dirBrowsePanel.add(dirLocTextField);
		dirBrowsePanel.add(directoryLocation);
		hl7MessageFilelocation = new JButton("Browse..");
		hl7MessageFilelocation.addActionListener(this);
		hl7MesLocTextField = new JTextField("");
		defineXMLLocation = new JButton("Browse..");
		defineXMLLocation.addActionListener(this);
		defineXMLTextField = new JTextField("");
		defineXMLTextField.setColumns(30);
		defineXMLPanel.setBackground(logColor);
		defineXMLPanel.add(defineXMLfileLabel);
		defineXMLPanel.add(defineXMLTextField);
		defineXMLPanel.add(defineXMLLocation);
		hl7csvlocation = new JButton("Browse..");
		hl7csvlocation.addActionListener(this);
		hl7csvTextField = new JTextField("");
		hl7MesLocTextField.setColumns(30);
		hl7MessagePanel.add(hl7fileLabel);
		hl7MessagePanel.add(hl7MesLocTextField);
		hl7MessagePanel.add(hl7MessageFilelocation);
		saveCSVLocation = new JFileChooser(_defaultLoc);
		choosedefineXMLLocation = new JFileChooser(_defaultLoc);
		hl7csvTextField.setColumns(30);
		csvPanel.add(hl7csvfileLabel);
		csvPanel.add(hl7csvTextField);
		csvPanel.add(hl7csvlocation);
		emptyPanel.setBackground(logColor);
		_bPanel.setBackground(logColor);
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
		fileZone1.setBorder(new TitledBorder("File Settings"));
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
		// center.add("Center", scrollPane);
		main.add("North", fileZone1);
		// main.add("Center", log);
		this.getContentPane().add("Center", main);
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
			filter.addExtension("txt");
			// filter.setDescription("txt");
			saveCSVLocation.setFileFilter(filter);
			int returnVal = saveCSVLocation.showSaveDialog(NewSDTMWizard.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				csvSaveFile = saveCSVLocation.getSelectedFile();
				// This is where a real application would open the file.
				if (csvSaveFile != null) {
					if (csvSaveFile.getAbsolutePath().endsWith("txt")) {
						CSVSaveFileName = csvSaveFile.getAbsolutePath();
					} else {
						CSVSaveFileName = csvSaveFile.getAbsolutePath() + ".txt";
					}
				} else {
					CSVSaveFileName = hl7csvTextField.getText();
				}
				_saveCSV = csvSaveFile.getAbsolutePath();
				hl7csvTextField.setText(CSVSaveFileName);
				hl7csvTextField.setEnabled(false);
			} else {
				log.append("command cancelled by user." + newline);
			}
		}
		if (e.getSource() == defineXMLLocation) {
			CaadapterFileFilter filter = new CaadapterFileFilter();
			filter.addExtension("scs");
			// filter.setDescription("txt");
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
					if (csvSaveFile.getAbsolutePath().endsWith("txt")) {
						CSVSaveFileName = csvSaveFile.getAbsolutePath();
					} else {
						CSVSaveFileName = csvSaveFile.getAbsolutePath() + ".txt";
					}
				} else {
					CSVSaveFileName = hl7csvTextField.getText();
				}
				try {
					// new SDTMMapFileTransformer(hl7MessageFile.getAbsolutePath().toString(),
					// directory.getAbsolutePath().toString(), callingFrame, CSVSaveFileName);
					this.dispose();
					new SDTMNewTransformer(hl7MessageFile.getAbsolutePath().toString(), directory.getAbsolutePath().toString(), _defineXML, callingFrame, CSVSaveFileName);
				} catch (RuntimeException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				log.append("Created the \"" + _genSCSFileName + "\" successfully" + newline);
				log.append("Created the \"" + CSVSaveFileName + "\" successfully" + newline);
				// scsFilewithPath = "D:\\dev\\caAdapter\\" + _tmp;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				log.append(e1.getMessage());
			}
		} else if (e.getSource() == cancel) {
			this.dispose();
		} else if (e.getSource() == directoryLocation) {
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
