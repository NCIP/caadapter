/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmps.ui.common;

import gov.nih.nci.cbiit.cmps.ui.mapping.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * This class defines a default implementation of Save action.
 * The primary focus of this class is to provide consistent look and feel definition
 * to SaveObjectToDbMapAction across the system.
 * Please see SaveObjectToDbMapAction defined in ui.map.actions for some reference.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-03 20:46:14 $
 */
public class DefaultSaveAction extends DefaultSaveAsAction
{
	public static final String COMMAND_NAME = ActionConstants.SAVE;
	public static final Character COMMAND_MNEMONIC = new Character('S');
	public static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK, false);
	public static final ImageIcon IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("fileSave.gif"));
	public static final String TOOL_TIP_DESCRIPTION = "Save";

	private static final String LOGID = "$RCSfile: DefaultSaveAction.java,v $";
	public static String RCSID = "$Header: /share/content/gforge/caadapter/cmps/src/gov/nih/nci/cbiit/cmps/ui/common/DefaultSaveAction.java,v 1.1 2008-12-03 20:46:14 linc Exp $";

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public DefaultSaveAction(MainFrame mainFrame)
	{
		this(COMMAND_NAME, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public DefaultSaveAction(String name, MainFrame mainFrame)
	{
		this(name, IMAGE_ICON, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public DefaultSaveAction(String name, Icon icon, MainFrame mainFrame)
	{
		super(name, icon, mainFrame);
	}

	protected void setAdditionalAttributes()
	{//override super class's one to plug in its own attributes.
		setMnemonic(COMMAND_MNEMONIC);
		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
		setShortDescription(TOOL_TIP_DESCRIPTION);
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      :
 */
