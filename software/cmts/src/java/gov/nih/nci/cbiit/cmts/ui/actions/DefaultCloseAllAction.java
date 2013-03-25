/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.ui.actions;

import gov.nih.nci.cbiit.cmts.ui.common.ActionConstants;
import gov.nih.nci.cbiit.cmts.ui.common.ContextManager;
import gov.nih.nci.cbiit.cmts.ui.common.ContextManagerClient;
import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmts.ui.main.MainFrame;
import gov.nih.nci.cbiit.cmts.ui.main.MainFrameContainer;
import gov.nih.nci.cbiit.cmts.ui.mapping.MappingMainPanel;
import gov.nih.nci.cbiit.cmts.ui.message.MessagePanel;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * This class defines the closeAll action.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2009-11-23 18:32:47 $
 */
public class DefaultCloseAllAction extends AbstractContextAction
{
	protected static final String COMMAND_NAME = ActionConstants.CLOSE_ALL;
	protected static final Character COMMAND_MNEMONIC = new Character('A');
    public static final ImageIcon IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("closeAllPane.png"));
//	protected static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.CTRL_MASK, false);

	private static final String LOGID = "$RCSfile: CloseAllAction.java,v $";
	public static String RCSID = "$Header: /share/content/gforge/caadapter/cmts/src/gov/nih/nci/cbiit/cmts/ui/actions/CloseAllAction.java,v 1.2 2009-11-23 18:32:47 wangeug Exp $";

	protected MainFrameContainer mainFrame = null;

	public DefaultCloseAllAction(MainFrameContainer mainFrame)
	{
		this(COMMAND_NAME, mainFrame);
	}

	public DefaultCloseAllAction(String name, MainFrameContainer mainFrame)
	{
		this(name, IMAGE_ICON , mainFrame);
	}

	public DefaultCloseAllAction(String name, Icon icon, MainFrameContainer mainFrame)
	{
		super(name, icon);
		this.mainFrame = mainFrame;
		setAdditionalAttributes();
	}

	protected void setAdditionalAttributes()
	{//override super class's one to plug in its own attributes.
		setMnemonic(COMMAND_MNEMONIC);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
	}


	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e)
	{
//		Log.logInfo(this, "CloseAllAction is called.");
		java.util.List<Component> componentList = mainFrame.getAllTabs();
		int count = componentList.size();//tabbedPane.getComponentCount();
		ArrayList<AbstractContextAction> actionList = new ArrayList<AbstractContextAction>();
        //boolean closedAllSuccessfully = true;

        String ll = "";
        int cnt = 0;
        for (int i = 0; i < count; i++)
		{//retrieve the list of close actions,
			//shall call individual after the loop, since the close action will remove the referred tab
			//which will cause the component count decreased.
			Component comp = componentList.get(i);

            if (comp instanceof ContextManagerClient)
			{
                String title = mainFrame.getTabbedPane().getTitleAt(i);
                if (comp instanceof MappingMainPanel)
                {
                    MappingMainPanel panel = (MappingMainPanel) comp;
                    if (panel.isChanged())
                    {
                        ll = ll + "\n" + title;
                        cnt++;
                    }
                }
                else if (comp instanceof MessagePanel)
                {
                    MessagePanel panel = (MessagePanel) comp;
                    if (!panel.hasBeenSaved())
                    {
                        ll = ll + "\n" + title;
                        cnt++;
                    }
                }

                actionList.add((AbstractContextAction) ((ContextManagerClient) comp).getDefaultCloseAction());
			}
		}
        String message = null;
        String titleT = "Unsaved documents found";
        if (cnt == 1)
        {
            String m2 = "Are you sure to close all tabs?";
            titleT = "Unsaved document found";
            if (count == 1) m2 = "Are you sure to close this tab?";

            message = "One document '" + ll.trim() + "' is not saved yet. " + m2;
        }
        else if (cnt > 1)
        {
            message = "Following "+cnt+" documents are not saved yet. Are you sure to close all tabs?\n" + ll;
        }

        if (message != null)
        {
            int n = JOptionPane.showConfirmDialog(mainFrame.getAssociatedUIComponent(), message, titleT, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (n != JOptionPane.YES_OPTION) return isSuccessfullyPerformed();
        }

        try
		{
			//shall notify context manager not to update context anymore.
			ContextManager.getContextManager().setInClosingAllOrShutdownMode(true, false);
			int size = actionList.size();
			for (int i = 0; i < size; i++)
			{
				AbstractContextAction action = actionList.get(i);
                if (action instanceof DefaultCloseAction)
                {
                    DefaultCloseAction closeA = (DefaultCloseAction) action;
                    closeA.setForceClose(true);
                }
                action.actionPerformed(e);
				if (!action.isSuccessfullyPerformed())
				{//stop at the first failed execution of close action.
					this.setSuccessfullyPerformed(false);
					break;
				}
				else
				{
					this.setSuccessfullyPerformed(true);
				}
			}
		}
		catch (Throwable t)
		{
			setSuccessfullyPerformed(false);
			//Log.logException(this, t);
//			System.err.println("Exception: " + t);
			t.printStackTrace();
		}
		finally
		{//roll back the mode.
			if (isSuccessfullyPerformed())
            {
                if (mainFrame.getAllTabs().size() == 0)
                    ContextManager.getContextManager().setInClosingAllOrShutdownMode(false, isSuccessfullyPerformed());
            }
            int size = actionList.size();
            for (int i = 0; i < size; i++)
            {
                AbstractContextAction action = actionList.get(i);
                if (action instanceof DefaultCloseAction)
                {
                    DefaultCloseAction closeA = (DefaultCloseAction) action;
                    closeA.setForceClose(false);
                }
            }
        }
		return isSuccessfullyPerformed();
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return mainFrame.getAssociatedUIComponent();
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
 */
