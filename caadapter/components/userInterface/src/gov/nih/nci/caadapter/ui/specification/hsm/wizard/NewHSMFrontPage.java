/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.specification.hsm.wizard;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;

import gov.nih.nci.caadapter.hl7.mif.MIFIndex;
import gov.nih.nci.caadapter.hl7.mif.MIFIndexParser;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Set;

/**
 * Define the first page in the open wizard.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version     Since caAdapter v1.2
 * revision    $Revision: 1.5 $
 * date        $Date: 2008-06-09 19:54:07 $
 */
public class NewHSMFrontPage extends JPanel implements ActionListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: NewHSMFrontPage.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/wizard/NewHSMFrontPage.java,v 1.5 2008-06-09 19:54:07 phadkes Exp $";

	private static final String HL7_MESSAGE_CATEGORY_LABEL = "Select an HL7 Message Category:";
	private static final String HL7_MESSAGE_TYPE_LABEL = "Select an HL7 Message Type:";
	private static final String BROWSE_COMMAND = "Browse...";

	private File userSelectionFile;
	private JComboBox hl7MessageTypeComboBox;
	private JTextField userInputField;
	private MIFIndex mifIndex;
	private JComboBox hl7MessageCategoryComboBox;

    private boolean success = false;
    private String errorMessage = "";
    /**
	 * Creates a new <code>JPanel</code> with a double buffer
	 * and a flow layout.
	 */
	public NewHSMFrontPage(NewHSMWizard wizard)
	{
//		this.wizard = wizard;
		initialize();
	}

	private void initialize()
	{
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel centerPanel = new JPanel(new GridBagLayout());
		Insets insets = new Insets(5, 5, 5, 5);
		centerPanel.add(new JLabel(HL7_MESSAGE_CATEGORY_LABEL), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
		centerPanel.add(new JLabel(HL7_MESSAGE_TYPE_LABEL), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));

		//		hl7MessageTypeComboBox = new JComboBox(CaadapterUtil.getAllSupportedMessageTypes());
		try {
			mifIndex =MIFIndexParser.loadMIFInfos();
			hl7MessageTypeComboBox=new JComboBox();
			hl7MessageTypeComboBox.setEnabled(false);
			hl7MessageCategoryComboBox = new JComboBox(mifIndex.getMessageCategory().toArray());
			hl7MessageCategoryComboBox.addActionListener(this);
			hl7MessageCategoryComboBox.setSelectedIndex(-1);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
            success = false;
            errorMessage = e.getMessage();
            return;
        }

		centerPanel.add(hl7MessageCategoryComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		centerPanel.add(hl7MessageTypeComboBox, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

		this.add(centerPanel);
        success = true;
        errorMessage = "";
    }

    public boolean wasSuccess()
    {
        return success;
    }
    public String getErrorMessage()
    {
        return errorMessage;
    }
    private String getUserInputFromEnabledField()
	{
		return userInputField.getText();
	}

	private void setUserInputToEnabledField(String newValue)
	{
		userInputField.setText(newValue);
	}

	public File getUserSelectionFile()
	{
		String userInputText = getUserInputFromEnabledField();
		if (userInputText != null && userInputText.length() > 0)
		{
			File tempFile = new File(userInputText);
			if (!GeneralUtilities.areEqual(userSelectionFile, tempFile))
			{//user input supersedes browsed value.
				userSelectionFile = tempFile;
			}
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

//	public MessageType getUserSelectedMessageType() throws Exception
//	{
//		MessageType messageType = HL7Util.getMessageType(hl7MessageTypeComboBox.getSelectedItem());
//		return messageType;
//	}
	public String getUserSelectedMIFFileName ()
	{
		String slctMsgType= (String)hl7MessageTypeComboBox.getSelectedItem().toString();

		return mifIndex.findMIFFileName(slctMsgType);
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource()==hl7MessageCategoryComboBox)
		{
			hl7MessageTypeComboBox.removeAllItems();
			String slctMsgCat=(String)hl7MessageCategoryComboBox.getSelectedItem();
			if (slctMsgCat==null)
				return;
			hl7MessageTypeComboBox.setEnabled(true);
			Set msgTypes=mifIndex.fingMessageTypesWithCategory(slctMsgCat);
			if (!msgTypes.isEmpty())
			{
				for(Object msgOneType:msgTypes.toArray())
					hl7MessageTypeComboBox.addItem(msgOneType);
			}
		}
		String command = e.getActionCommand();
		if(BROWSE_COMMAND.equals(command))
		{
			File file = DefaultSettings.getUserInputOfFileFromGUI(this, //FileUtil.getUIWorkingDirectoryPath(),
					Config.TARGET_TREE_FILE_DEFAULT_EXTENTION, Config.OPEN_DIALOG_TITLE_FOR_DEFAULT_TARGET_FILE, false, false);
			if (file != null)
			{
				setUserSelectionFile(file);
			}
		}
	}
}

