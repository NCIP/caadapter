/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.mapping.hl7.actions;

import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.actions.DefaultContextCloseAction;
import gov.nih.nci.caadapter.ui.common.context.ContextManager;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;

import javax.swing.Icon;
import java.awt.event.ActionEvent;

/**
 * This class defines the close action of Mapping panel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-06-09 19:54:06 $
 */
public class CloseMapAction extends DefaultContextCloseAction //implements ContextManagerClient
{
	public CloseMapAction(AbstractMappingPanel mappingPanel)
	{
		this(COMMAND_NAME, mappingPanel);
	}

	public CloseMapAction(String name, AbstractMappingPanel mappingPanel)
	{
		this(name, null, mappingPanel);
	}

	public CloseMapAction(String name, Icon icon, AbstractMappingPanel mappingPanel)
	{
		super(name, icon, mappingPanel);
	}

	/**
	 * Descendant class could override this method to provide actions to be executed after the
	 * given action is performed, such as update menu status, etc.
	 *
	 * @param e
	 * @return if the action has been executed successfully.
	 */
	protected boolean postActionPerformed(ActionEvent e)
	{
		if(mainFrame!=null)
		{
			ContextManager cm = ContextManager.getContextManager();
			cm.enableAction(ActionConstants.OPEN_MAP_FILE, true);
			cm.enableAction(ActionConstants.NEW_O2DB_MAP_FILE, true);
			cm.enableAction(ActionConstants.NEW_MAP_FILE, true);
			cm.enableAction(ActionConstants.NEW_CSV2XMI_MAP_FILE, true);
		}
		return true;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2007/11/30 14:40:53  wangeug
 * HISTORY      : create CSV_TO_XMI mapping module
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/03 19:37:42  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/10/23 16:24:35  wuye
 * HISTORY      : Added code to lock & unlock new object-2-db mapping menu.
 * HISTORY      :
 * HISTORY      : Revision 1.16  2006/08/02 18:44:23  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/07/27 22:41:12  jiangsc
 * HISTORY      : Consolidated context sensitive menu implementation.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/07/22 20:53:08  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
