/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.ui.common.resource;

import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
 
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * This class defines the Browse action used across the message wisard.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.6 $
 *          date        $Date: 2009-03-25 13:57:19 $
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
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/resource/BrowseHL7ResourceAction.java,v 1.6 2009-03-25 13:57:19 wangeug Exp $";

	private static final String COMMAND_NAME = "Browse...";
	private static final Character COMMAND_MNEMONIC = new Character('B');
	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);

	private transient JTextField inputTargetField;
	private transient OpenHL7ResourceFrontPage frontPage;
	private transient String browseMode;
	private transient String fileExt;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public BrowseHL7ResourceAction(OpenHL7ResourceFrontPage front, JTextField targetFiled, String bMode, String fExt)
	{
		this(COMMAND_NAME, front,targetFiled, bMode,fExt);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public BrowseHL7ResourceAction(String name,OpenHL7ResourceFrontPage front, JTextField targetFiled, String bMode,String fExt)
	{
		this(name, null, front,targetFiled, bMode,fExt);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public BrowseHL7ResourceAction(String name, Icon icon, OpenHL7ResourceFrontPage front,JTextField targetFiled, String bMode,String fExt)
	{
		super(name, icon);
		inputTargetField = targetFiled;
		frontPage=front;
		browseMode = bMode;
		fileExt=fExt;
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
		String fileExtension = fileExt;// frontPage.getFileExtension(browseMode);
 
		//get user's input of resource directory
        File file = null;
        if (inputTargetField.getText()==null
        		||inputTargetField.getText().equals(""))
        	file=DefaultSettings.getUserInputOfFileFromGUI(frontPage, //FileUtil.getUIWorkingDirectoryPath(),
        		fileExtension, "Select Resource", false, false);
        else
        	file=DefaultSettings.getUserInputOfFileFromGUI(frontPage,this.inputTargetField.getText(),
            		fileExtension, "Select Resource", false, false);
		if (file != null)
		{
//			frontPage.setUserSelectionFile(file, browseMode);
			inputTargetField.setText(file.getAbsolutePath());
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



/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.5  2008/09/24 17:55:15  phadkes
 * HISTORY      : Changes for code standards
 * HISTORY      :
*/
