/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/wizard/CSVValidationChoicePage.java,v 1.1 2007-04-03 16:18:15 wangeug Exp $
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
 * This class defines the first page for CSVValidationWizard.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:18:15 $
 */
public class CSVValidationChoicePage extends FrontPage implements ActionListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: CSVValidationChoicePage.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/csv/wizard/CSVValidationChoicePage.java,v 1.1 2007-04-03 16:18:15 wangeug Exp $";

	public static final int NO_COMMAND_SELECTED = -1;
	public static final int VALIDATE_SPECIFICATION_COMMAND_SELECTED = 1;
	public static final int VALIDATE_DATA_AGAINST_SPECIFICATION_COMMAND_SELECTED = 2;

	private static final String VALIDATE_SPECIFICATION_COMMAND = "Validate " + Config.COMMON_METADATA_DISPLAY_NAME;
	private static final String VALIDATE_DATA_AGAINST_SPECIFICATION_COMMAND = "Validate CSV Data Against " + Config.COMMON_METADATA_DISPLAY_NAME;

	private ButtonGroup buttonGroup;
	private JRadioButton validateSpecificationButton;
	private JRadioButton validateDataAgainstSpecificationButton;

	private JPanel selectCSVLocationPanel;
	private JTextField csvLocationField;
	private JButton csvLocationBrowseButton;

	private BrowseCsvAction browseAction;

	/**
	 * Descendant class needs to implement this UI initialization.
	 */
	protected void initialize()
	{
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel centerPanel = new JPanel(new GridBagLayout());
		Insets insets = new Insets(5, 5, 5, 5);
		int posY = 0;
		validateSpecificationButton = new JRadioButton(VALIDATE_SPECIFICATION_COMMAND);
		validateSpecificationButton.setMnemonic('S');
		validateSpecificationButton.addActionListener(this);
		centerPanel.add(validateSpecificationButton, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		posY++;

		validateDataAgainstSpecificationButton = new JRadioButton(VALIDATE_DATA_AGAINST_SPECIFICATION_COMMAND);
		validateDataAgainstSpecificationButton.setMnemonic('D');
		validateDataAgainstSpecificationButton.addActionListener(this);
		centerPanel.add(validateDataAgainstSpecificationButton, new GridBagConstraints(0, posY, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		posY++;

		buttonGroup = new ButtonGroup();
		buttonGroup.add(validateSpecificationButton);
		buttonGroup.add(validateDataAgainstSpecificationButton);

		selectCSVLocationPanel = new JPanel(new BorderLayout());
		JPanel tempPanel = new JPanel(new BorderLayout(5, 5));
		csvLocationField = new JTextField();
		csvLocationField.setEditable(false);
		csvLocationField.setForeground(Config.DEFAULT_READ_ONLY_BACK_GROUND_COLOR);
		browseAction = new BrowseCsvAction(this);
		csvLocationBrowseButton = new JButton(browseAction);
		centerPanel.add(csvLocationField, new GridBagConstraints(0, posY, 2, 1, 1, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		centerPanel.add(csvLocationBrowseButton, new GridBagConstraints(3, posY, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		//		tempPanel.add(csvLocationField, BorderLayout.CENTER);
//		tempPanel.add(csvLocationBrowseButton, BorderLayout.EAST);
//		selectCSVLocationPanel.add(tempPanel, BorderLayout.NORTH);
//		centerPanel.add(selectCSVLocationPanel, new GridBagConstraints(0, posY, 2, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		this.add(centerPanel);
		enableAllFileBrowseFields(false);
	}

	private void enableAllFileBrowseFields(boolean value)
	{
		csvLocationField.setEnabled(value);
		if(!value)
		{
			csvLocationField.setToolTipText(null);
		}
		csvLocationBrowseButton.setEnabled(value);
	}

	private void setUserInputToEnabledField(String text)
	{
		if (csvLocationField.isEnabled())
		{
//			csvLocationField.setEditable(true);
			csvLocationField.setText(text);
			csvLocationField.setToolTipText(text);
		}
		else
		{
			csvLocationField.setToolTipText(null);
		}
	}

	private String getUserInputFromEnabledField()
	{
		if (csvLocationField.isEnabled())
		{
			return csvLocationField.getText();
		}
		else
		{
			//none is enabled, so assume blank is chosen.
			return "";
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

	/**
	 * It is highly recommended that descendant class overrides this method to provide dynamic
	 * information of file extension (in sync with the getActiveBrowseDialogTitle() return value),
	 * to be utilized by BrowseCsvAction, if needed.
	 */
	public String getActiveFileExtension()
	{
		String result = null;
		if (validateSpecificationButton.isSelected())
		{
			System.err.println("This function should not be called.");
		}

		if (validateDataAgainstSpecificationButton.isSelected())
		{
			result = Config.CSV_DATA_FILE_DEFAULT_EXTENSTION;
		}
		else
		{
			System.err.println("I don't understand which option user is selected.");
		}
		return result;
	}

	/**
	 * It is highly recommended that descendant class overrides this method to provide dynamic
	 * information of dialog title (in sync with the getActiveFileExtension() return value),
	 * to be utilized by BrowseCsvAction, if needed.
	 */
	public String getActiveBrowseDialogTitle()
	{
		String result = null;
		if (validateSpecificationButton.isSelected())
		{
			System.err.println("This function should not be called.");
		}

		if (validateDataAgainstSpecificationButton.isSelected())
		{
			result = Config.OPEN_DIALOG_TITLE_FOR_CSV_FILE;
		}
		else
		{
			System.err.println("I don't understand which option user is selected.");
		}
		return result;
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		//every time clear it unless being set explicitly
		buttonSelectedMode = NO_COMMAND_SELECTED;
		String command = e.getActionCommand();
		//		Log.logInfo(this, "command='"+ command + "'.");
		enableAllFileBrowseFields(false);
		if (VALIDATE_SPECIFICATION_COMMAND.equals(command))
		{//do nothing
			setUserSelectionFile(null);
			buttonSelectedMode = VALIDATE_SPECIFICATION_COMMAND_SELECTED;
		}
		else if (VALIDATE_DATA_AGAINST_SPECIFICATION_COMMAND.equals(command))
		{
			csvLocationField.setEnabled(true);
			String text = csvLocationField.getText();
			csvLocationField.setToolTipText((GeneralUtilities.isBlank(text))? null : text);
			csvLocationBrowseButton.setEnabled(true);
			buttonSelectedMode = VALIDATE_DATA_AGAINST_SPECIFICATION_COMMAND_SELECTED;
		}
		else
		{
			System.err.println("Why do I come here? Command: '" + command + "'.");
		}
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.10  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 23:06:14  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/10/25 17:13:27  jiangsc
 * HISTORY      : Added Validation implemenation.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/10/21 18:26:37  jiangsc
 * HISTORY      : First round validation implementation in CSV module.
 * HISTORY      :
 */
