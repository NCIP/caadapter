/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.hl7message.actions;

import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.hl7message.OpenHL7MessageFrontPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * This class defines the Browse action used across the message wisard.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: linc $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-06-26 19:45:51 $
 */
public class BrowseHLV3MessageAction extends AbstractContextAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: BrowseHLV3MessageAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/hl7message/actions/BrowseHLV3MessageAction.java,v 1.4 2008-06-26 19:45:51 linc Exp $";

	private static final String COMMAND_NAME = "Browse...";
	private static final Character COMMAND_MNEMONIC = new Character('B');
	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);

	private transient OpenHL7MessageFrontPage frontPage;
	private transient String browseMode;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public BrowseHLV3MessageAction(OpenHL7MessageFrontPage frontPage, String browseMode)
	{
		this(COMMAND_NAME, frontPage, browseMode);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public BrowseHLV3MessageAction(String name, OpenHL7MessageFrontPage frontPage, String browseMode)
	{
		this(name, null, frontPage, browseMode);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public BrowseHLV3MessageAction(String name, Icon icon, OpenHL7MessageFrontPage frontPage, String browseMode)
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
				fileExtension, getOpenDialogTitle(fileExtension), browseMode.equals(OpenHL7MessageFrontPage.DEST_FILE_BROWSE_MODE), browseMode.equals(OpenHL7MessageFrontPage.DEST_FILE_BROWSE_MODE));
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
        if (fileExtension.equals(Config.CSV_DATA_FILE_DEFAULT_EXTENSTION)) scrTitle = Config.OPEN_DIALOG_TITLE_FOR_CSV_FILE;
        else if (fileExtension.equals(Config.MAP_FILE_DEFAULT_EXTENTION)) scrTitle = Config.OPEN_DIALOG_TITLE_FOR_MAP_FILE;
        else if (fileExtension.equals(Config.CSV_METADATA_FILE_DEFAULT_EXTENTION)) scrTitle = Config.OPEN_DIALOG_TITLE_FOR_CSV_METADATA_FILE;
        else if (fileExtension.equals(Config.HSM_META_DEFINITION_FILE_DEFAULT_EXTENSION)) scrTitle = Config.OPEN_DIALOG_TITLE_FOR_H3S_HSM_FILE;
        //else if (fileExtension.equals(Config.HL7_V3_MESSAGE_FILE_DEFAULT_EXTENSION)) scrTitle = Config.OPEN_DIALOG_TITLE_FOR_HL7_V3_MESSAGE_FILE;
        //else if (fileExtension.equals(Config.DATABASE_META_FILE_DEFAULT_EXTENSION)) scrTitle = Config.OPEN_DIALOG_TITLE_FOR_MAP_FILE;
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
 * HISTORY      : Revision 1.3  2008/06/09 19:53:52  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/08/13 15:22:26  wangeug
 * HISTORY      : add new constants :open_dialog_tile_hsm_xml
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/03 19:33:17  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/29 23:06:14  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/11/03 19:06:25  umkis
 * HISTORY      : When calling DefaultSettings.getUserInputOfFileFromGUI, the title of the file chooser screen can be dynamically displayed according to file extension.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/17 22:12:39  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/10 20:48:59  jiangsc
 * HISTORY      : Enhanced dialog operation.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/11 22:10:31  jiangsc
 * HISTORY      : Open/Save File Dialog consolidation.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/22 20:53:16  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/07 21:41:23  jiangsc
 * HISTORY      : New Structure
 * HISTORY      :
 */
