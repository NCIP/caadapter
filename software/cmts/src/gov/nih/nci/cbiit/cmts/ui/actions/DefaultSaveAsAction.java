/*L
 * Copyright SAIC.
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

package gov.nih.nci.cbiit.cmts.ui.actions;


import gov.nih.nci.cbiit.cmts.ui.common.ActionConstants;
import gov.nih.nci.cbiit.cmts.ui.common.ContextManager;
import gov.nih.nci.cbiit.cmts.ui.common.ContextManagerClient;
import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmts.ui.main.MainFrame;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Currently play as place holder to define the general look and feel of a "Save As..." action.
 * Descendant class will provide concrete implementation of the action.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2009-11-23 18:32:47 $
 */
public class DefaultSaveAsAction extends AbstractContextAction
{
	protected static final String COMMAND_NAME = "      " + ActionConstants.SAVE_AS;
	protected static final Character COMMAND_MNEMONIC = new Character('a');
	public static final ImageIcon IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("blank.gif"));


	protected transient MainFrame mainFrame = null;

	protected transient File defaultFile = null;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public DefaultSaveAsAction(MainFrame mainFrame)
	{
		this(COMMAND_NAME, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public DefaultSaveAsAction(String name, MainFrame mainFrame)
	{
		this(name, null, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public DefaultSaveAsAction(String name, Icon icon, MainFrame mainFrame)
	{
		super(name, icon);
		this.mainFrame = mainFrame;
		setAdditionalAttributes();
	}

	/**
	 * provide descendant class to override.
	 */
	protected void setAdditionalAttributes()
	{
		setMnemonic(COMMAND_MNEMONIC);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
	}

	public File getDefaultFile()
	{
		return defaultFile;
	}

	public void setDefaultFile(String fullFileName)
	{
		setDefaultFile(new File(fullFileName));
	}

	public void setDefaultFile(File file)
	{
		this.defaultFile = file;
	}


	/**
	 * This function is to help perform any action before action is performed.
	 * @param client
	 */
	protected void preActionPerformed(ContextManagerClient client)
	{
		figureOutMainFrameLocation(client);
	}

	/**
	 * This function is to notify context manager upon save.
	 * @param client
	 */
	protected void postActionPerformed(ContextManagerClient client)
	{
		figureOutMainFrameLocation(client);
		if(this.mainFrame!=null)
		{
			ContextManager.getContextManager().notifySaveActionPerformedOnContextClient(client);
		}
	}

	protected void figureOutMainFrameLocation(ContextManagerClient contextClient)
	{
		if (this.mainFrame == null && (contextClient instanceof JComponent))
		{
			JRootPane rootPane = ((JComponent) contextClient).getRootPane();
			if (rootPane != null)
			{
				this.mainFrame = (MainFrame) rootPane.getParent();
			}
		}
	}

	protected void removeFileUsageListener(File file, ContextManagerClient client)
	{
//		figureOutMainFrameLocation(client);
//		if (this.mainFrame != null)
//		{
//			this.mainFrame.getMainContextManager().getContextFileManager().removeFileUsageListener(file, client);
//		}
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e) throws Exception
	{
		return true;
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return mainFrame;
	}

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2008/12/09 19:04:17  linc
 * HISTORY      : First GUI release
 * HISTORY      :
 * HISTORY      : Revision 1.1  2008/12/03 20:46:14  linc
 * HISTORY      : UI update.
 * HISTORY      :
 */
