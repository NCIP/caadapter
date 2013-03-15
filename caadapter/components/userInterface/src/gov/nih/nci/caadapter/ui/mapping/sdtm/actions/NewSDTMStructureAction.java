/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.mapping.sdtm.actions;

/**





 */

import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.mapping.sdtm.NewSDTMWizard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.prefs.Preferences;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v3.2 revision $Revision: 1.8 $
 */
public class NewSDTMStructureAction extends AbstractContextAction
{

	/**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final String COMMAND_NAME = "RDS Text File";

    private static final Character COMMAND_MNEMONIC = new Character('S');

    private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_T, Event.CTRL_MASK, false);

    private AbstractMainFrame mainFrame;

   // HashMap prefs;

    /**
     * Defines an <code>Action</code> object with a default description
     * string and default icon.
     */
    public NewSDTMStructureAction(AbstractMainFrame mainFrame)
    {
        this(COMMAND_NAME, mainFrame);
        // mainContextManager = cm;
    }

    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a default icon.
     */
    public NewSDTMStructureAction(String name, AbstractMainFrame mainFrame)
    {
        this(name, null, mainFrame);
    }

    /**
     * Defines an <code>Action</code> object with the specified
     * description string and a the specified icon.
     */
    public NewSDTMStructureAction(String name, Icon icon, AbstractMainFrame mainFrame)
    {
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
     * @return true if the action is finished successfully; otherwise,
     *         return false.
     */
    protected boolean doAction(ActionEvent e) throws Exception
    {
//    	verify resource
		if (!isResourceReady(mainFrame))
		{
			setSuccessfullyPerformed(false);
			return isSuccessfullyPerformed();
		}
        new NewSDTMWizard(mainFrame);
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
    	return CaadapterUtil.getModuleResourceMissed(Config.CAADAPTER_QUERYBUILDER_RESOURCE_REQUIRED);
	}
}


