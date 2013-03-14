/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.mapping.sdtm.actions;

import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.mapping.sdtm.Database2SDTMMappingPanel;

import java.awt.Component;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.prefs.Preferences;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.KeyStroke;

/**
 * This class creates a Menu item Create V2 V3 action and assigns the action to
 * it.
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.2 revision $Revision: 1.9 $ date $Date:
 *          2006/10/03 13:50:47 $
 */
public class Database2SDTMAction extends AbstractContextAction {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private static final String COMMAND_NAME = "CSV/Database To RDS Map Specification";

	private static final Character COMMAND_MNEMONIC = new Character('P');

	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke
			.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK, false);

	private AbstractMainFrame mainFrame;



    /**
	 * Defines an <code>Action</code> object with a default description string
	 * and default icon.
	 */
	public Database2SDTMAction(AbstractMainFrame mainFrame) {
		this(COMMAND_NAME, mainFrame);
		// mainContextManager = cm;
	}

	/**
	 * Defines an <code>Action</code> object with the specified description
	 * string and a default icon.
	 */
	public Database2SDTMAction(String name, AbstractMainFrame mainFrame) {
		this(name, null, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified description
	 * string and a the specified icon.
	 */
	public Database2SDTMAction(String name, Icon icon, AbstractMainFrame mainFrame) {
		super(name, icon);
		this.mainFrame = mainFrame;

        setMnemonic(COMMAND_MNEMONIC);
		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DESKTOP_ACTION_TYPE);
		// do not know how to set the icon location name, or just do not
		// matter.
	}

	/**
	 * The abstract function that descendant classes must be overridden to
	 * provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return
	 *         false.
	 */
	protected boolean doAction(ActionEvent e) throws Exception {
//		verify resource
		if (!isResourceReady(mainFrame))
		{
			setSuccessfullyPerformed(false);
			return isSuccessfullyPerformed();
		}
		Database2SDTMMappingPanel mp = new Database2SDTMMappingPanel(mainFrame,"Test");
		mainFrame.addNewTab(mp);
		setSuccessfullyPerformed(true);
		return isSuccessfullyPerformed();
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent() {
		return mainFrame;
	}
    @Override
	protected ArrayList getMissedResources() {
		// TODO Auto-generated method stub
    	return CaadapterUtil.getModuleResourceMissed(Config.CAADAPTER_QUERYBUILDER_RESOURCE_REQUIRED);
	}
}
/**
 * HISTORY : $Log: not supported by cvs2svn $
 * HISTORY : Revision 1.8  2007/10/04 18:09:34  wangeug
 * HISTORY : verify resource based on module
 * HISTORY :
 * HISTORY : Revision 1.7  2007/08/08 20:54:35  jayannah
 * HISTORY : Changed the verbage from SDTM to RDS
 * HISTORY :
 * HISTORY : Revision 1.6  2007/07/26 19:57:50  jayannah
 * HISTORY : Changes for preferences menu
 * HISTORY :
 * HISTORY : Revision 1.5  2007/07/19 18:51:37  jayannah
 * HISTORY : Changes for 4.0 release
 * HISTORY :
 * HISTORY : Revision 1.3  2007/05/10 15:40:39  jayannah
 * HISTORY : *** empty log message ***
 * HISTORY :
 * HISTORY : Revision 1.1  2007/04/03 16:17:57  wangeug
 * HISTORY : initial loading
 * HISTORY :
 * HISTORY : Revision 1.5  2006/11/28 15:13:43  jayannah
 * HISTORY : Changed the order and names of the menuitems
 * HISTORY :
 * HISTORY : Revision 1.4  2006/11/15 20:12:36  wuye
 * HISTORY : reorganize menu items
 * HISTORY :
 * HISTORY : Revision 1.3  2006/11/08 15:44:53  wuye
 * HISTORY : Main Menu Re-Org
 * HISTORY :
 * HISTORY : Revision 1.2  2006/10/30 20:27:15  jayannah
 * HISTORY : caAdapter_3_2_QA_10_30_2006_V2
 * HISTORY :
 * HISTORY : Revision 1.1  2006/10/30 18:40:52  jayannah
 * HISTORY : action class for the SDTM module
 * HISTORY : HISTORY : Revision 1.2 2006/10/03
 * 13:50:47 jayannah HISTORY : This class creates a Menu item Create V2 V3
 * action and assigns the action to it HISTORY : HISTORY : Revision 1.1
 * 2006/10/03 13:46:57 jayannah HISTORY : This class creates a Menu item Create
 * V2 V3 action and assigns the action to it. HISTORY :
 */
