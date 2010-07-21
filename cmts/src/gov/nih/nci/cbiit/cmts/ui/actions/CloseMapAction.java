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
import gov.nih.nci.cbiit.cmts.ui.mapping.MappingMainPanel;

import javax.swing.Icon;
import java.awt.event.ActionEvent;

/**
 * This class defines the close action of Mapping panel.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-09 19:04:17 $
 */
public class CloseMapAction extends DefaultContextCloseAction //implements ContextManagerClient
{
	public CloseMapAction(MappingMainPanel mappingPanel)
	{
		this(COMMAND_NAME, mappingPanel);
	}

	public CloseMapAction(String name, MappingMainPanel mappingPanel)
	{
		this(name, null, mappingPanel);
	}

	public CloseMapAction(String name, Icon icon, MappingMainPanel mappingPanel)
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
			cm.enableAction(ActionConstants.NEW_MAP_FILE, true);
		}
		return true;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 */
