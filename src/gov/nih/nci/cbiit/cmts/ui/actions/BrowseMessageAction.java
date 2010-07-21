/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */


package gov.nih.nci.cbiit.cmts.ui.actions;

import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmts.ui.message.OpenMessageFrontPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * This class defines the Browse action used across the message wisard.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-09 19:04:17 $
 */
public class BrowseMessageAction extends AbstractContextAction
{

	private static final String COMMAND_NAME = "Browse...";
	private static final Character COMMAND_MNEMONIC = new Character('B');
	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);

	private transient OpenMessageFrontPage frontPage;
	private transient String browseMode;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public BrowseMessageAction(OpenMessageFrontPage frontPage, String browseMode)
	{
		this(COMMAND_NAME, frontPage, browseMode);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public BrowseMessageAction(String name, OpenMessageFrontPage frontPage, String browseMode)
	{
		this(name, null, frontPage, browseMode);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public BrowseMessageAction(String name, Icon icon, OpenMessageFrontPage frontPage, String browseMode)
	{
		super(name, icon);
		this.frontPage = frontPage;
		this.browseMode = browseMode;
		setMnemonic(COMMAND_MNEMONIC);
		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
	}


	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e)
	{
		String fileExtension = frontPage.getFileExtension(browseMode);

        File file = DefaultSettings.getUserInputOfFileFromGUI(frontPage, //FileUtil.getUIWorkingDirectoryPath(),
				fileExtension, getOpenDialogTitle(fileExtension), browseMode.equals(OpenMessageFrontPage.DEST_FILE_BROWSE_MODE), browseMode.equals(OpenMessageFrontPage.DEST_FILE_BROWSE_MODE));
		if (file != null)
		{
			frontPage.setUserSelectionFile(file, browseMode);
			setSuccessfullyPerformed(true);
		}
		else
		{
			setSuccessfullyPerformed(false);
			//			Log.logInfo(this, "Open command cancelled by user.");
		}
		return isSuccessfullyPerformed();
	}

    public String getOpenDialogTitle(String fileExtension)
    {
        String scrTitle = "";
        if (fileExtension.equals(DefaultSettings.CSV_DATA_FILE_DEFAULT_EXTENSTION)) scrTitle = DefaultSettings.OPEN_DIALOG_TITLE_FOR_CSV_FILE;
        else if (fileExtension.equals(DefaultSettings.MAP_FILE_DEFAULT_EXTENTION)) scrTitle = DefaultSettings.OPEN_DIALOG_TITLE_FOR_MAP_FILE;
        else scrTitle = "Open";

        return scrTitle;
    }

    /**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return frontPage;
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 */
