/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.ui.mapping.mms.actions;

import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.mapping.mms.Object2DBMappingPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * This class defines the new Map panel action.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.2
 *          revision    $Revision: 1.5 $
 *          date        $Date: 2008-06-09 19:54:06 $
 */
public class NewObject2DBMapAction extends AbstractContextAction
		{
	private static final String COMMAND_NAME = ActionConstants.NEW_O2DB_MAP_FILE;
	private static final Character COMMAND_MNEMONIC = new Character('O');
	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK, false);

	private AbstractMainFrame mainFrame;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public NewObject2DBMapAction(AbstractMainFrame mainFrame)
	{
		this(COMMAND_NAME, mainFrame);
		//mainContextManager = cm;
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public NewObject2DBMapAction(String name, AbstractMainFrame mainFrame)
	{
		this(name, null, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public NewObject2DBMapAction(String name, Icon icon, AbstractMainFrame mainFrame)
	{
		super(name, icon);
		this.mainFrame = mainFrame;
		setMnemonic(COMMAND_MNEMONIC);
		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DESKTOP_ACTION_TYPE);
		//do not know how to set the icon location name, or just do not matter.
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e) throws Exception
	{
//		verify resource
		if (!isResourceReady(mainFrame))
		{
			setSuccessfullyPerformed(false);
			return isSuccessfullyPerformed();
		}
		Object2DBMappingPanel mp = new Object2DBMappingPanel("Test");
		mp.setChanged(false);
		mainFrame.addNewTab(mp);
		setSuccessfullyPerformed(true);
		return isSuccessfullyPerformed();
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

	@Override
	protected ArrayList getMissedResources() {
		// TODO Auto-generated method stub
		return CaadapterUtil.getModuleResourceMissed(Config.CAADAPTER_COMMON_RESOURCE_REQUIRED);
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.4  2007/10/04 18:09:23  wangeug
 * HISTORY      : verify resource based on module
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/09/20 16:40:14  schroedn
 * HISTORY      : License text
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/06/18 15:22:38  schroedn
 * HISTORY      : added setChanged(false) flag to fix save on close bug
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:57  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.3  2006/11/15 20:12:36  wuye
 * HISTORY      : reorganize menu items
 * HISTORY      :
 * HISTORY      : Revision 1.2  2006/11/08 15:44:53  wuye
 * HISTORY      : Main Menu Re-Org
 * HISTORY      :
 * HISTORY      : Revision 1.1  2006/09/26 15:48:54  wuye
 * HISTORY      : New default actions for object 2 db mapping
 * HISTORY      :
 */
