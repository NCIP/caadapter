/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmts.ui.message;


import gov.nih.nci.cbiit.cmts.ui.actions.BrowseMessageAction;
import gov.nih.nci.cbiit.cmts.ui.common.ActionConstants;
import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmts.ui.util.GeneralUtilities;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * The front page of open  Message panel.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-09 19:04:17 $
 */
@SuppressWarnings("serial")
public class OpenMessageFrontPage extends JPanel
{

	private final String DATA_FILE_BROWSE_MODE = "Source Data File";
	private final String MAP_FILE_BROWSE_MODE = "Transformation Mapping File";
	public static final String DEST_FILE_BROWSE_MODE = "Target Data File";
	private JTextField dataFileInputField;
	private JTextField mapFileInputField;
	private JTextField destFileInputField;
	private File mapFile;
	private File dataFile;
	private File destFile;
	private String openWizardTitle;
	/**
	 * Creates a new <code>JPanel</code> with a double buffer
	 * and a flow layout.
	 */
	public OpenMessageFrontPage(OpenMessageWizard wizard)
	{
		openWizardTitle=wizard.getTitle();
		initialize();
	}

	private void initialize()
	{
		this.setLayout(new BorderLayout());//new FlowLayout(FlowLayout.LEADING));
		JPanel centerPanel = new JPanel(new GridBagLayout());
		Insets insets = new Insets(5, 5, 5, 5);
		JLabel dataFileLabel = new JLabel(DATA_FILE_BROWSE_MODE);
		centerPanel.add(dataFileLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		dataFileInputField = new JTextField();
		dataFileInputField.setPreferredSize(new Dimension(350, 25));
		centerPanel.add(dataFileInputField, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		JButton dataFileBrowseButton = new JButton(new BrowseMessageAction(this, DATA_FILE_BROWSE_MODE));
		centerPanel.add(dataFileBrowseButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));

		JLabel mapFileLabel = new JLabel(MAP_FILE_BROWSE_MODE);
		centerPanel.add(mapFileLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		mapFileInputField = new JTextField();
		mapFileInputField.setPreferredSize(new Dimension(350, 25));
		centerPanel.add(mapFileInputField, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		JButton mapFileBrowseButton = new JButton(new BrowseMessageAction(this, MAP_FILE_BROWSE_MODE));
		centerPanel.add(mapFileBrowseButton, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));

		JLabel destFileLabel = new JLabel(DEST_FILE_BROWSE_MODE);
		centerPanel.add(destFileLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		destFileInputField = new JTextField();
		destFileInputField.setPreferredSize(new Dimension(350, 25));
		centerPanel.add(destFileInputField, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		JButton destFileBrowseButton = new JButton(new BrowseMessageAction(this, DEST_FILE_BROWSE_MODE));
		centerPanel.add(destFileBrowseButton, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
		this.add(centerPanel, BorderLayout.CENTER);
	}

	public String getFileExtension(String browseMode)
	{
		String result = null;
		if(DATA_FILE_BROWSE_MODE.equals(browseMode))
		{
			if (openWizardTitle.equals(ActionConstants.NEW_CSV_Transformation))
				result=DefaultSettings.CSV_DATA_FILE_DEFAULT_EXTENSTION;
			else 	if (openWizardTitle.equals(ActionConstants.NEW_HL7_V2_Transformation))
				result=DefaultSettings.HL7_V2_DATA_FILE_DEFAULT_EXTENSTION;
			else if (openWizardTitle.equals(ActionConstants.NEW_XML_Transformation))
				result = DefaultSettings.XML_DATA_FILE_DEFAULT_EXTENSTION;
			else
				result = DefaultSettings.XML_DATA_FILE_DEFAULT_EXTENSTION;
		}
		else if(MAP_FILE_BROWSE_MODE.equals(browseMode))
		{
			result = DefaultSettings.MAP_FILE_DEFAULT_EXTENTION;
		}
		else if(DEST_FILE_BROWSE_MODE.equals(browseMode))
		{
			result = ".xml";
		}
		else
		{
			result = "";
		}
		return result;
	}

	public void setUserSelectionFile(File file, String browseMode)
	{
		if (DATA_FILE_BROWSE_MODE.equals(browseMode))
		{
			dataFile=file;
			setFileTextFieldFile(dataFileInputField, file);
		}
		else if (MAP_FILE_BROWSE_MODE.equals(browseMode))
		{
			mapFile=file;
			setFileTextFieldFile(mapFileInputField, file);
		}
		else if (DEST_FILE_BROWSE_MODE.equals(browseMode))
		{
			destFile=file;
			setFileTextFieldFile(destFileInputField, file);
		}
		else
		{
			System.err.println(getClass().getName() + ".getFileExtension() does not understand this mode '" + browseMode + "'.");
		}
	}


	/**
	 * Return true if user inputs are valid.
	 * @return
	 */
	public boolean validateInputFields()
	{
		boolean result = validateInputByMode(DATA_FILE_BROWSE_MODE) && validateInputByMode(MAP_FILE_BROWSE_MODE) && validateInputByMode(DEST_FILE_BROWSE_MODE);
		return result;
	}

	private boolean validateInputByMode(String mode)
	{
		File tempFile = null;
		String fileTypeMsg = null;

		//if csv->, validate dest file
		if(DEST_FILE_BROWSE_MODE.equals(mode))
		{
			tempFile = getDestFile();
			if(tempFile == null){
				JOptionPane.showMessageDialog(this, "Please choose result file", "File Not Found Error",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			return true;
		}

		if(DATA_FILE_BROWSE_MODE.equals(mode))
		{
			tempFile = getDataFile();
			fileTypeMsg = "The data file ";
		}
		else if(MAP_FILE_BROWSE_MODE.equals(mode))
		{
			tempFile = getMapFile();
			fileTypeMsg = "The map specification ";
		}

		if (tempFile == null || !tempFile.isFile())
		{
			String errorDetail = (tempFile==null) ? "should not be null."
					: tempFile.getAbsolutePath() + " does not represent a valid file.";
			JOptionPane.showMessageDialog(this, fileTypeMsg + errorDetail, "File Not Found Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		else
		{
			return true;
		}


	}

	/**
	 * Return null if user does not specify any file.
	 *
	 * @return
	 */
	public File getMapFile()
	{
		String userInputText = mapFileInputField.getText();
		if (userInputText != null && userInputText.length() > 0)
		{
			File tempFile = new File(userInputText);
			if (!GeneralUtilities.areEqual(mapFile, tempFile))
			{//user input supersedes browsed value.
				mapFile = tempFile;
			}
		}
		else
		{
			mapFile = null;
		}

		return mapFile;
	}

	/**
	 * Return null if user does not specify any file.
	 * @return
	 */
	public File getDataFile()
	{
		String userInputText = dataFileInputField.getText();
		if (userInputText != null && userInputText.length() > 0)
		{
			File tempFile = new File(userInputText);
			if (!GeneralUtilities.areEqual(dataFile, tempFile))
			{//user input supersedes browsed value.
				dataFile = tempFile;
			}
		}
		else
		{
			dataFile = null;
		}

		return dataFile;
	}

	/**
	 * Return null if user does not specify any file.
	 * @return
	 */
	public File getDestFile()
	{
		String userInputText = destFileInputField.getText();
		if (userInputText != null && userInputText.length() > 0)
		{
			File tempFile = new File(userInputText);
			if (!GeneralUtilities.areEqual(destFile, tempFile))
			{//user input supersedes browsed value.
				destFile = tempFile;
			}
		}
		else
		{
			destFile = null;
		}

		return destFile;
	}

	private void setFileTextFieldFile(JTextField field, File dataFile)
	{
		if (dataFile != null)
		{
			field.setText(dataFile.getAbsolutePath());
		}
		else
		{
			field.setText("");
		}
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 */
