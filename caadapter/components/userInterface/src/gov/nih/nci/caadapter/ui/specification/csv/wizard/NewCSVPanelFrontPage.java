/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





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
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-06-09 19:54:07 $
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
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/wizard/NewCSVPanelFrontPage.java,v 1.4 2008-06-09 19:54:07 phadkes Exp $";

	public static final int BLANK_COMMAND_SELECTED = 1;
	public static final int GENERATE_FROM_CSV_INSTANCE_COMMAND_SELECTED = 2;
    public static final int GENERATE_FROM_CSV_NONSTRUCTURE_INSTANCE_COMMAND_SELECTED = 4;
    public static final int NEW_FROM_AN_EXISTING_CSV_SCHEMA_COMMAND_SELECTED = 3;

	private static final String BLANK_COMMAND = "Create a CSV Specification from scratch";
	private static final String GENERATE_FROM_CSV_INSTANCE_COMMAND = "Generate from a Structure CSV file";
    private static final String GENERATE_FROM_CSV_NONSTRUCTURE_INSTANCE_COMMAND = "Generate from a Non-Structure CSV file";
   	private static final String NEW_FROM_AN_EXISTING_CSV_SCHEMA_COMMAND = "Copy from an Existing CSV Specification";


	private ButtonGroup buttonGroup;
	private JRadioButton blankButton;
	private JRadioButton generateFromCSVInstanceButton;
    private JRadioButton generateFromNonStructureCSVInstanceButton;
   	private JRadioButton newFromCSVSchemaButton;

	private JPanel selectCSVLocationPanel;
	private JTextField csvLocationField;
	private JButton csvLocationBrowseButton;

	private JPanel selectCSVSchemaLocationPanel;
	private JTextField csvSchemaLocationField;
	private JButton csvSchemaLocationBrowseButton;

	private BrowseCsvAction browseAction;


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

		JPanel centerPanel = new JPanel(new GridLayout(6, 1));
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

		newFromCSVSchemaButton = new JRadioButton(NEW_FROM_AN_EXISTING_CSV_SCHEMA_COMMAND);
		newFromCSVSchemaButton.setMnemonic('N');
		newFromCSVSchemaButton.addActionListener(this);
		centerPanel.add(newFromCSVSchemaButton);

		selectCSVSchemaLocationPanel = new JPanel(new BorderLayout());
		tempPanel = new JPanel(new BorderLayout(5, 5));
		csvSchemaLocationField = new JTextField();
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

		csvSchemaLocationField.setEnabled(value);
		csvSchemaLocationBrowseButton.setEnabled(value);
	}

	private void setUserInputToEnabledField(String text)
	{
		if (csvLocationField.isEnabled())
		{
			csvLocationField.setText(text);
		}
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
 * HISTORY      : Revision 1.3  2007/12/20 16:16:23  wangeug
 * HISTORY      : change layout
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/12/19 22:33:50  schroedn
 * HISTORY      : Added Non-Structure new to CSV
 * HISTORY      :
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
