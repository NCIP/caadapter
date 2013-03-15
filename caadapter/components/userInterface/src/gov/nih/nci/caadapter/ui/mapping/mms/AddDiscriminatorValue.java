/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.ui.mapping.mms;
import gov.nih.nci.caadapter.common.metadata.ModelMetadata;
import gov.nih.nci.caadapter.common.metadata.ObjectMetadata;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;

/**
 * @author wuye
 *
 */
@SuppressWarnings("serial")
public class AddDiscriminatorValue extends JDialog implements ActionListener {



	/**
	 * @author OWNER: Ye Wu
	 * @author LAST UPDATE $Author: phadkes $
	 * @version Since caAdapter v3.2 revision $Revision: 1.6 $
	 */
	    String curDir;
	    String sourceName1 = "";
	    String reportName1 = "";
	    JPanel north = new JPanel(new FlowLayout());
	    JPanel center = new JPanel(new BorderLayout());
	    JPanel main = new JPanel(new BorderLayout());
	    JTextArea log = new JTextArea();
	    File sourceFile;
	    File reportFile = new File(reportName1);
	    JLabel sourceLabel1 = new JLabel("Source: ");
	    JTextField sourceField1 = new JTextField(12);
	    JButton sourceBrow1 = new JButton("Browse...");
	    JPanel dirBrowsePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    JLabel reportLabel1 = new JLabel("Report: ");
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

//	    public AddDiscriminatorValue(final AbstractMainFrame _callingFrame, final ObjectMetadata objectMetadata) {
	    public AddDiscriminatorValue(final JFrame _callingFrame, final ObjectMetadata objectMetadata) {
	        final JDialog preFrame = new JDialog(_callingFrame, true);
	        //final JDialog preFrame = new JDialog();
	        preFrame.setLocation(400, 300);
	        preFrame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	        preFrame.setTitle("Specifing Discriminator Value....");
            preFrame.setAlwaysOnTop(true);
            preFrame.setResizable(false);
            JPanel mainPanel = new JPanel();
	        mainPanel.setLayout(new BorderLayout());
	        //
	        JPanel centerPan = new JPanel(new GridLayout(1, 3));
	        centerPan.setBorder(new TitledBorder("Please Define Discriminator Value...."));
	        centerPan.add(new JLabel("Discriminator Value:"));
	        final JTextField textField = new JTextField();
	    	ModelMetadata modelMetadata = ModelMetadata.getInstance();
	    	Hashtable<String, String> discriminatorValues = modelMetadata.getDiscriminatorValues();
	    	int startpos = modelMetadata.getMmsPrefixObjectModel().length();

	    	if (discriminatorValues.get(objectMetadata.getXPath().substring(startpos+1))!= null)
	    	{
	    		textField.setText(discriminatorValues.get(objectMetadata.getXPath().substring(startpos+1)));
	    	}
	        textField.setEnabled(true);
	        centerPan.add(textField);
	        //
	        JPanel butPan = new JPanel();
	        butPan.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
	        JButton okBut = new JButton("OK");
	        okBut.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent event) {
	                preFrame.dispose();
	                try {
				    	ModelMetadata modelMetadata = ModelMetadata.getInstance();
				    	Hashtable<String, String> discriminatorValues = modelMetadata.getDiscriminatorValues();
				    	System.out.println("AAAAAAAAAAAAAAAA:"+objectMetadata.getXPath()+"   " + textField.getText());
				    	int startpos = modelMetadata.getMmsPrefixObjectModel().length();
				    	discriminatorValues.put(objectMetadata.getXPath().substring(startpos+1),textField.getText());
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
	    }
	}
