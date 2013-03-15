/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.mapping.sdtm.actions;

import gov.nih.nci.caadapter.dataviewer.util.GetConnectionSingleton;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.actions.DefaultContextCloseAction;
import gov.nih.nci.caadapter.ui.common.context.ContextManager;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * The class closes the connection during the time when the panel is closed
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.3 $
 *          $Date: 2008-06-09 19:54:06 $
 */
public class SDTMCloseAction extends DefaultContextCloseAction//implements ContextManagerClient
{
    public SDTMCloseAction(AbstractMappingPanel mappingPanel) {
        this(COMMAND_NAME, mappingPanel);
    }

    public SDTMCloseAction(String name, AbstractMappingPanel mappingPanel) {
        this(name, null, mappingPanel);
    }

    public SDTMCloseAction(String name, Icon icon, AbstractMappingPanel mappingPanel) {
        super(name, icon, mappingPanel);
    }

    /**
     * Descendant class could override this method to provide actions to be executed after the
     * given action is performed, such as update menu status, etc.
     *
     * @param e
     * @return if the action has been executed successfully.
     */
    protected boolean postActionPerformed(ActionEvent e) {
        if (mainFrame != null) {
            try {
                GetConnectionSingleton.closeConnection();
            } catch (Exception e1) {
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
/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2007/08/16 19:39:45  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */
