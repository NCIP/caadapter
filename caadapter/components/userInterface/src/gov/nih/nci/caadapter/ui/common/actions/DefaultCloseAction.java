/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.actions;

import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * This class defines the default close action.
 * Descendant classes may implement additional functions.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public class DefaultCloseAction extends AbstractContextAction
{
	protected static final String COMMAND_NAME = ActionConstants.CLOSE;
	protected static final Character COMMAND_MNEMONIC = new Character('C');
	protected static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.CTRL_MASK, false);

    public static final ImageIcon IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("closePane.png"));
    public static final String TOOL_TIP_DESCRIPTION = "Close this tab";



    protected AbstractMainFrame ownerFrame = null;

	public DefaultCloseAction(AbstractMainFrame owner)
	{
		this(COMMAND_NAME, owner);
	}

	public DefaultCloseAction(String name, AbstractMainFrame owner)
	{
		this(name, null, owner);
	}

	public DefaultCloseAction(String name, Icon icon, AbstractMainFrame owner)
	{
		super(name, icon);
		ownerFrame = owner;
		setAdditionalAttributes();
	}

	protected void setAdditionalAttributes()
	{//override super class's one to plug in its own attributes.
		setMnemonic(COMMAND_MNEMONIC);
		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
        setIcon(IMAGE_ICON);
		setShortDescription(TOOL_TIP_DESCRIPTION);
    }

	protected void setFrame(JFrame newFrame)
	{
		if (newFrame instanceof AbstractMainFrame)
		{
			ownerFrame = (AbstractMainFrame) newFrame;
		}
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e)
	{
		try
		{
			if (ownerFrame != null)
			{
				ownerFrame.closeTab();
				ownerFrame.resetCenterPanel();  // inserted by umkis on 01/18/2006, defaect# 252
            }
			else
			{
				System.err.println("Main Frame is null. Ignore!");
			}
			setSuccessfullyPerformed(true);
		}
		catch (Exception e1)
		{
			reportThrowableToUI(e1, ownerFrame);
			setSuccessfullyPerformed(false);
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
		return ownerFrame;
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/08/02 18:44:20  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.16  2006/01/18 19:48:50  umkis
 * HISTORY      : defaect# 252, after closing a tab, main frame UI is refresh.
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/14 21:37:16  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/11/17 21:03:12  umkis
 * HISTORY      : change Icon image
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/11/16 20:52:45  umkis
 * HISTORY      : defect# 195, Set new image icon and tool-tip
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/02 22:23:10  jiangsc
 * HISTORY      : Newly enhanced context-sensitive menus and toolbar.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/07/25 21:56:46  jiangsc
 * HISTORY      : 1) Added expand all and collapse all;
 * HISTORY      : 2) Added toolbar on the mapping panel;
 * HISTORY      : 3) Consolidated menus;
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/07/22 20:52:57  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
