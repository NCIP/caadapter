/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.resource;

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
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public class BrowseHL7ResourceAction extends AbstractContextAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: BrowseHL7ResourceAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/resource/BrowseHL7ResourceAction.java,v 1.2 2008-06-09 19:53:51 phadkes Exp $";

	private static final String COMMAND_NAME = "Browse...";
	private static final Character COMMAND_MNEMONIC = new Character('B');
	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);

	private transient OpenHL7ResourceFrontPage frontPage;
	private transient String browseMode;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public BrowseHL7ResourceAction(OpenHL7ResourceFrontPage frontPage, String browseMode)
	{
		this(COMMAND_NAME, frontPage, browseMode);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public BrowseHL7ResourceAction(String name, OpenHL7ResourceFrontPage frontPage, String browseMode)
	{
		this(name, null, frontPage, browseMode);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public BrowseHL7ResourceAction(String name, Icon icon, OpenHL7ResourceFrontPage frontPage, String browseMode)
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
		String fileExtension = null;// frontPage.getFileExtension(browseMode);
		//get user's input of resource directory
        File file = DefaultSettings.getUserInputOfFileFromGUI(frontPage, //FileUtil.getUIWorkingDirectoryPath(),
        		fileExtension, "Select Resource", false, false);
		if (file != null)
		{
			frontPage.setUserSelectionFile(file, browseMode);
			setSuccessfullyPerformed(true);
		}
		else
		{
			setSuccessfullyPerformed(false);
		}
		return isSuccessfullyPerformed();
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

