/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.actions;

import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.context.ContextManagerClient;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Currently play as place holder to define the general look and feel of a "Save As..." action.
 * Descendant class will provide concrete implementation of the action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public class DefaultSaveAsAction extends AbstractContextAction
{
	protected static final String COMMAND_NAME = "      " + ActionConstants.SAVE_AS;
	protected static final Character COMMAND_MNEMONIC = new Character('a');
	public static final ImageIcon IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("blank.gif"));

	private static final String LOGID = "$RCSfile: DefaultSaveAsAction.java,v $";
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/actions/DefaultSaveAsAction.java,v 1.2 2008-06-09 19:53:51 phadkes Exp $";

	protected transient AbstractMainFrame mainFrame = null;

	protected transient File defaultFile = null;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public DefaultSaveAsAction(AbstractMainFrame mainFrame)
	{
		this(COMMAND_NAME, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public DefaultSaveAsAction(String name, AbstractMainFrame mainFrame)
	{
		this(name, null, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public DefaultSaveAsAction(String name, Icon icon, AbstractMainFrame mainFrame)
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
//		figureOutMainFrameLocation(client);
//		if(this.mainFrame!=null)
//		{
//			this.mainFrame.getMainContextManager().notifySaveActionPerformedOnContextClient(client);
//		}
	}

	protected void figureOutMainFrameLocation(ContextManagerClient contextClient)
	{
		if (this.mainFrame == null && (contextClient instanceof JComponent))
		{
			JRootPane rootPane = ((JComponent) contextClient).getRootPane();
			if (rootPane != null)
			{
				this.mainFrame = (AbstractMainFrame) rootPane.getParent();
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
 * HISTORY      : Revision 1.1  2007/04/03 16:17:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/12/06 20:27:03  wuye
 * HISTORY      : Change the command name to align the menu items
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/08/02 18:44:20  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/14 21:37:16  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/26 16:22:10  jiangsc
 * HISTORY      : Face lift to provide better error report.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/30 21:14:19  jiangsc
 * HISTORY      : minor update
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/30 20:48:12  jiangsc
 * HISTORY      : minor update
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/22 20:52:57  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/15 18:58:39  jiangsc
 * HISTORY      : 1) Reconstucted Menu bars;
 * HISTORY      : 2) Integrated FunctionPane to display property;
 * HISTORY      : 3) Enabled drag and drop functions to mapping panel.
 * HISTORY      :
 */
