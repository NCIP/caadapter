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
import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmts.ui.main.MainFrame;

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
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2009-11-23 18:32:47 $
 */
public class DefaultSaveAction extends DefaultSaveAsAction
{
	public static final String COMMAND_NAME = ActionConstants.SAVE;
	public static final Character COMMAND_MNEMONIC = new Character('S');
	public static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK, false);
	public static final ImageIcon IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("fileSave.gif"));
	public static final String TOOL_TIP_DESCRIPTION = "Save";

	private static final String LOGID = "$RCSfile: DefaultSaveAction.java,v $";
	public static String RCSID = "$Header: /share/content/gforge/caadapter/cmts/src/gov/nih/nci/cbiit/cmts/ui/actions/DefaultSaveAction.java,v 1.2 2009-11-23 18:32:47 wangeug Exp $";

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
 * HISTORY      : Revision 1.1  2008/12/09 19:04:17  linc
 * HISTORY      : First GUI release
 * HISTORY      :
 * HISTORY      : Revision 1.1  2008/12/03 20:46:14  linc
 * HISTORY      : UI update.
 * HISTORY      :
 * HISTORY      :
 */
