/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/wizard/NewCSVPanelFrontPage.java,v 1.2 2007-12-19 22:33:50 schroedn Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.ui.specification.csv.wizard;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.ui.specification.csv.actions.BrowseCsvAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * This class defines the first page for NewCSVPanelWizard.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: schroedn $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2007-12-19 22:33:50 $
 */
public class NewCSVPanelFrontPage extends FrontPage implements ActionListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: NewCSVPanelFrontPage.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/wizard/NewCSVPanelFrontPage.java,v 1.2 2007-12-19 22:33:50 schroedn Exp $";

	public static final int BLANK_COMMAND_SELECTED = 1;
	public static final int GENERATE_FROM_CSV_INSTANCE_COMMAND_SELECTED = 2;
    public static final int GENERATE_FROM_CSV_NONSTRUCTURE_INSTANCE_COMMAND_SELECTED = 4;
    public static final int NEW_FROM_AN_EXISTING_CSV_SCHEMA_COMMAND_SELECTED = 3;


	private static final String BLANK_COMMAND = "Create a CSV Specification from scratch";
	private static final String GENERATE_FROM_CSV_INSTANCE_COMMAND = "Generate from a CSV file";
    private static final String GENERATE_FROM_CSV_NONSTRUCTURE_INSTANCE_COMMAND = "Generate from a Non-Structure CSV file";
    //	private static final String GENERATE_FROM_HMD_COMMAND = "Generate from a HL7 Metadata File";
	private static final String NEW_FROM_AN_EXISTING_CSV_SCHEMA_COMMAND = "Copy from an Existing CSV Specification";

	//	private NewCSVPanelWizard wizard;

	private ButtonGroup buttonGroup;
	private JRadioButton blankButton;
	private JRadioButton generateFromCSVInstanceButton;
    private JRadioButton generateFromNonStructureCSVInstanceButton;
    //	private JRadioButton generateFromHMDButton;
	private JRadioButton newFromCSVSchemaButton;

	private JPanel selectCSVLocationPanel;
	private JTextField csvLocationField;
	private JButton csvLocationBrowseButton;

	//  private JPanel selectHMDLocationPanel;
	//	private JTextField hmdLocationField;
	//	private JButton hmdLocationBrowseButton;

	private JPanel selectCSVSchemaLocationPanel;
	private JTextField csvSchemaLocationField;
	private JButton csvSchemaLocationBrowseButton;

	private BrowseCsvAction browseAction;


//	/**
//	 * Creates a new <code>JPanel</code> with a double buffer
//	 * and a flow layout.
//	 */
//	public NewCSVPanelFrontPage()
//	{
//	}

	protected void initialize()
	{
		this.setLayout(new BorderLayout());

		JScrollPane centerPane = new JScrollPane();

		JPanel northPanel = new JPanel(new BorderLayout());
		JTextArea noteArea = new JTextArea("Select one of the following as the source to create a CSV specification");
		noteArea.setEditable(false);
		noteArea.setBackground(northPanel.getBackground());
		northPanel.add(noteArea, BorderLayout.CENTER);
		this.add(northPanel, BorderLayout.NORTH);

		JPanel centerPanel = new JPanel(new GridLayout(5, 1));
		this.setBorder(BorderFactory.createTitledBorder(this.getBorder(), "" /*ActionConstants.NEW_CSV_SPEC*/));

		blankButton = new JRadioButton(BLANK_COMMAND);
		blankButton.setMnemonic('B');
		blankButton.addActionListener(this);
		centerPanel.add(blankButton);

		generateFromCSVInstanceButton = new JRadioButton(GENERATE_FROM_CSV_INSTANCE_COMMAND);
		generateFromCSVInstanceButton.setMnemonic('G');
		generateFromCSVInstanceButton.addActionListener(this);
		centerPanel.add(generateFromCSVInstanceButton);

        generateFromNonStructureCSVInstanceButton = new JRadioButton(GENERATE_FROM_CSV_NONSTRUCTURE_INSTANCE_COMMAND);
        generateFromNonStructureCSVInstanceButton.setMnemonic('N');
        generateFromNonStructureCSVInstanceButton.addActionListener(this);
        centerPanel.add(generateFromNonStructureCSVInstanceButton);

        selectCSVLocationPanel = new JPanel(new BorderLayout());
		JPanel tempPanel = new JPanel(new BorderLayout(5, 5));
		csvLocationField = new JTextField();
		browseAction = new BrowseCsvAction(this);
		csvLocationBrowseButton = new JButton(browseAction);
		tempPanel.add(csvLocationField, BorderLayout.CENTER);
		tempPanel.add(csvLocationBrowseButton, BorderLayout.EAST);
		selectCSVLocationPanel.add(tempPanel, BorderLayout.NORTH);
		centerPanel.add(selectCSVLocationPanel);

		//		generateFromHMDButton = new JRadioButton(GENERATE_FROM_HMD_COMMAND);
		//		generateFromHMDButton.setMnemonic('H');
		//		generateFromHMDButton.addActionListener(this);
		//		centerPanel.add(generateFromHMDButton);
		//
		//		selectHMDLocationPanel = new JPanel(new BorderLayout());
		//		tempPanel = new JPanel(new BorderLayout(5, 5));
		//		hmdLocationField = new JTextField();
		//		hmdLocationBrowseButton = new JButton(browseAction);
		//		tempPanel.add(hmdLocationField, BorderLayout.CENTER);
		//		tempPanel.add(hmdLocationBrowseButton, BorderLayout.EAST);
		//		selectHMDLocationPanel.add(tempPanel, BorderLayout.NORTH);
		//		centerPanel.add(selectHMDLocationPanel);

		newFromCSVSchemaButton = new JRadioButton(NEW_FROM_AN_EXISTING_CSV_SCHEMA_COMMAND);
		newFromCSVSchemaButton.setMnemonic('N');
		newFromCSVSchemaButton.addActionListener(this);
		centerPanel.add(newFromCSVSchemaButton);

		selectCSVSchemaLocationPanel = new JPanel(new BorderLayout());
		tempPanel = new JPanel(new BorderLayout(5, 5));
		csvSchemaLocationField = new JTextField();
		//		browseAction = new BrowseCsvAction(this);
		csvSchemaLocationBrowseButton = new JButton(browseAction);
		tempPanel.add(csvSchemaLocationField, BorderLayout.CENTER);
		tempPanel.add(csvSchemaLocationBrowseButton, BorderLayout.EAST);
		selectCSVSchemaLocationPanel.add(tempPanel, BorderLayout.NORTH);
		centerPanel.add(selectCSVSchemaLocationPanel);

		centerPane.getViewport().setView(centerPanel);
		this.add(centerPane, BorderLayout.CENTER);

		buttonGroup = new ButtonGroup();
		buttonGroup.add(blankButton);
		buttonGroup.add(generateFromCSVInstanceButton);
        buttonGroup.add(generateFromNonStructureCSVInstanceButton);
        //		buttonGroup.add(generateFromHMDButton);
		buttonGroup.add(newFromCSVSchemaButton);

		//default selection
		blankButton.setSelected(true);
		buttonSelectedMode = BLANK_COMMAND_SELECTED;

		enableAllFileBrowseFields(false);
	}

	private void enableAllFileBrowseFields(boolean value)
	{
		csvLocationField.setEnabled(value);
		csvLocationBrowseButton.setEnabled(value);

		//		hmdLocationField.setEnabled(value);
		//		hmdLocationBrowseButton.setEnabled(value);

		csvSchemaLocationField.setEnabled(value);
		csvSchemaLocationBrowseButton.setEnabled(value);
	}

	private void setUserInputToEnabledField(String text)
	{
		if (csvLocationField.isEnabled())
		{
			csvLocationField.setText(text);
		}
		//		else if (hmdLocationField.isEnabled())
		//		{
		//			hmdLocationField.setText(text);
		//		}
		else if (csvSchemaLocationField.isEnabled())
		{
			csvSchemaLocationField.setText(text);
		}
		else
		{
			//none is enabled, so assume blank is chosen.
		}
	}

	private String getUserInputFromEnabledField()
	{
		if (csvLocationField.isEnabled())
		{
			return csvLocationField.getText();
		}
		//		else if(hmdLocationField.isEnabled())
		//		{
		//			return hmdLocationField.getText();
		//		}
		else if (csvSchemaLocationField.isEnabled())
		{
			return csvSchemaLocationField.getText();
		}
		else
		{
			//none is enabled, so assume blank is chosen.
			return null;
		}
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		//every time clear it unless being set by explicit BLANK_COMMAND
		buttonSelectedMode = NO_COMMAND_SELECTED;
		String command = e.getActionCommand();
		//		Log.logInfo(this, "command='"+ command + "'.");
		enableAllFileBrowseFields(false);
		if (BLANK_COMMAND.equals(command))
		{//do nothing
			setUserSelectionFile(null);
			buttonSelectedMode = BLANK_COMMAND_SELECTED;
		}
		else if (GENERATE_FROM_CSV_INSTANCE_COMMAND.equals(command))
		{
			csvLocationField.setEnabled(true);
			csvLocationBrowseButton.setEnabled(true);
			buttonSelectedMode = GENERATE_FROM_CSV_INSTANCE_COMMAND_SELECTED;
		}
        else if (GENERATE_FROM_CSV_NONSTRUCTURE_INSTANCE_COMMAND.equals(command))
        {
            csvLocationField.setEnabled(true);
            csvLocationBrowseButton.setEnabled(true);
            buttonSelectedMode = GENERATE_FROM_CSV_NONSTRUCTURE_INSTANCE_COMMAND_SELECTED;
        }
        //		else if(GENERATE_FROM_HMD_COMMAND.equals(command))
		//		{
		//			hmdLocationField.setEnabled(true);
		//			hmdLocationBrowseButton.setEnabled(true);
		//		}
		else if (NEW_FROM_AN_EXISTING_CSV_SCHEMA_COMMAND.equals(command))
		{
			csvSchemaLocationField.setEnabled(true);
			csvSchemaLocationBrowseButton.setEnabled(true);
			buttonSelectedMode = NEW_FROM_AN_EXISTING_CSV_SCHEMA_COMMAND_SELECTED;
		}
		else
		{
			System.err.println("Why do I come here? Command: '" + command + "'.");
		}
	}

	/**
	 * return null if blank is selected.
	 *
	 * @return the user selected file.
	 */
	public File getUserSelectionFile()
	{
		String userInputText = getUserInputFromEnabledField();
		if (userInputText != null && userInputText.trim().length() > 0)
		{
			File tempFile = new File(userInputText);
			if (!GeneralUtilities.areEqual(userSelectionFile, tempFile))
			{//user input supersedes browsed value.
				userSelectionFile = tempFile;
			}
		}
		else
		{
			userSelectionFile = null;
		}
		return userSelectionFile;
	}

	public void setUserSelectionFile(File userSelectionFile)
	{
		this.userSelectionFile = userSelectionFile;
		if (userSelectionFile != null)
		{
			setUserInputToEnabledField(userSelectionFile.getAbsolutePath());
		}
	}

	public String getActiveFileExtension()
	{
		String result = null;
		if (blankButton.isSelected())
		{
			System.err.println("This function should not be called.");
		}

		if (generateFromCSVInstanceButton.isSelected() || generateFromNonStructureCSVInstanceButton.isSelected() )
		{
			result = Config.CSV_DATA_FILE_DEFAULT_EXTENSTION;
		}
		//		else if(generateFromHMDButton.isSelected())
		//		{
		//			result = Config.HL7_META_DEFINITION_FILE_DEFAULT_EXTENSION;
		//		}
		else if (newFromCSVSchemaButton.isSelected())
		{
			result = Config.CSV_METADATA_FILE_DEFAULT_EXTENTION;
		}
		else
		{
			System.err.println("I don't understand which option user is selected.");
		}
		return result;
	}

	public String getActiveBrowseDialogTitle()
	{
		String result = null;
		if (blankButton.isSelected())
		{
			System.err.println("This function should not be called.");
		}

		if (generateFromCSVInstanceButton.isSelected() || generateFromNonStructureCSVInstanceButton.isSelected())
		{
			result = Config.OPEN_DIALOG_TITLE_FOR_CSV_FILE;
		}
		//		else if (generateFromHMDButton.isSelected())
		//		{
		//			result = Config.OPEN_DIALOG_TITLE_FOR_DEFAULT_HL7_META_DEFINITION_FILE;
		//		}
		else if (newFromCSVSchemaButton.isSelected())
		{
			result = Config.OPEN_DIALOG_TITLE_FOR_CSV_METADATA_FILE;
		}
		else
		{
			System.err.println("I don't understand which option user is selected.");
		}
		return result;
	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:18:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/25 16:38:27  chene
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/25 16:37:37  chene
 * HISTORY      : Better wording
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 23:06:14  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/11/04 15:33:00  umkis
 * HISTORY      : defect# 155
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/11/03 21:01:37  umkis
 * HISTORY      : defect# 155, line 138 ; Change "Select one of the followings as the..." to "Select on of the following as the..." (no "s" on "following").
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/10/21 18:26:37  jiangsc
 * HISTORY      : First round validation implementation in CSV module.
 * HISTORY      :
 */
