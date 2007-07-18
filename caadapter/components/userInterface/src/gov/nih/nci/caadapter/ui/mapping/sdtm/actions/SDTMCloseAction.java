package gov.nih.nci.caadapter.ui.mapping.sdtm.actions;

import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;
import gov.nih.nci.caadapter.ui.common.context.ContextManager;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.actions.DefaultContextCloseAction;
import gov.nih.nci.caadapter.dataviewer.util.GetConnectionSingleton;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: hjayanna
 * Date: Jul 13, 2007
 * Time: 1:55:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class SDTMCloseAction extends DefaultContextCloseAction//implements ContextManagerClient
{
	public SDTMCloseAction(AbstractMappingPanel mappingPanel)
	{
		this(COMMAND_NAME, mappingPanel);
	}

	public SDTMCloseAction(String name, AbstractMappingPanel mappingPanel)
	{
		this(name, null, mappingPanel);
	}

	public SDTMCloseAction(String name, Icon icon, AbstractMappingPanel mappingPanel)
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
            try
            {
                GetConnectionSingleton.closeConnection();
            } catch (Exception e1)
            {
                e1.printStackTrace();//To change body of catch statement use File | Settings | File Templates.
            }
            ContextManager cm = ContextManager.getContextManager();
			cm.enableAction(ActionConstants.OPEN_MAP_FILE, true);
			cm.enableAction(ActionConstants.NEW_O2DB_MAP_FILE, true);
			cm.enableAction(ActionConstants.NEW_MAP_FILE, true);
		}
		return true;
	}
}
