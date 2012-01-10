/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */


package gov.nih.nci.cbiit.cmts.ui.actions;


import gov.nih.nci.cbiit.cmts.ui.common.ActionConstants;
import gov.nih.nci.cbiit.cmts.ui.common.ContextManagerClient;
import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmts.ui.main.MainFrame;
import gov.nih.nci.cbiit.cmts.ui.main.MainFrameContainer;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;

import java.awt.event.ActionEvent;
import java.awt.Component;
import java.awt.Event;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.io.File;


/**
 * This class defines the default implementation of context sensitive open action.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2009-11-23 18:32:47 $
 */
public abstract class DefaultContextOpenAction extends AbstractContextAction
{

	protected static final String COMMAND_NAME = ActionConstants.OPEN;
	protected static final Character COMMAND_MNEMONIC = new Character('O');
	//hotkey//protected static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK, false);
	//protected static final ImageIcon IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("fileOpen.gif"));
    protected static final ImageIcon IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("ico_open.bmp"));

    protected static final String TOOL_TIP_DESCRIPTION = ActionConstants.OPEN;

	protected transient MainFrameContainer mainFrame;
	protected transient ActionEvent actionEvent;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public DefaultContextOpenAction(MainFrameContainer mainFrame)
	{
		this(COMMAND_NAME, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public DefaultContextOpenAction(String name, MainFrameContainer mainFrame)
	{
		this(name, IMAGE_ICON, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public DefaultContextOpenAction(String name, Icon icon, MainFrameContainer mainFrame)
	{
		super(name, icon);
		this.mainFrame = mainFrame;
		setAdditionalAttributes();
	}

	protected void setAdditionalAttributes()
	{//override super class's one to plug in its own attributes.
		setMnemonic(COMMAND_MNEMONIC);
		//hotkey//setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DESKTOP_ACTION_TYPE);
		setShortDescription(TOOL_TIP_DESCRIPTION);
	}

	/**
	 * Return the real implementation of ContextClient class.
	 * @return the real implementation of ContextClient class.
	 */
	protected abstract Class getContextClientClass();

	/**
	 * Return the real user input of the file to be opened.
	 * @return the real user input of the file to be opened.
	 */
	protected abstract File getFileFromUserInput();

	/**
	 * Launch the context manager client to UI.
	 * @param panel
	 * @param file
	 */
	protected abstract void launchPanel(final ContextManagerClient panel, final File file);

	/**
	 * This function will be called by actionPerfermed() to do some pre-work
	 * before the real action is performed.
	 *
	 * @param e
	 * @return true if the action shall proceed;
	 *         false if the action cannot continue, shall return immediately.
	 */
	protected boolean preActionPerformed(ActionEvent e)
	{
		try
		{
			JComponent currentActivePanel = mainFrame.hasComponentOfGivenClass(getContextClientClass(), true);
			if (currentActivePanel!=null)
			{
				AbstractContextAction closeAction = (AbstractContextAction)((ContextManagerClient)currentActivePanel).getDefaultCloseAction();
				closeAction.actionPerformed(e);
				return closeAction.isSuccessfullyPerformed();
			}
			else
			{//no need to close
				return true;
			}
		}
		catch (Throwable t)
		{
			reportThrowableToUI(t, this.mainFrame.getAssociatedUIComponent());
			return false;
		}
	}


	/**
	 * Descendant class could override this method to provide actions to be executed after the
	 * given action is performed, such as update menu status, etc.
	 *
	 * @param e
	 * @return true if the action shall proceed;
	 *         false if the action cannot continue, shall return immediately.
	 */
	protected boolean postActionPerformed(ActionEvent e)
	{
		ContextManagerClient panel = null;
		boolean everythingGood = true;
		try
		{
			Class contextClientClass = getContextClientClass();
			if(contextClientClass!=null)
			{
			    panel = (ContextManagerClient) contextClientClass.newInstance();
				launchPanel(panel, getFileFromUserInput());
			}
		}
		catch (Throwable t)
		{
			reportThrowableToUI(t, this.mainFrame.getAssociatedUIComponent());
			if (panel != null)
			{//close panel if accidentally there.
				panel.getDefaultCloseAction().actionPerformed(e);
			}
			everythingGood = false;
		}
		return everythingGood;
	}

	/**
	 * Invoked when an action occurs.
	 */
	protected boolean doAction(ActionEvent e)
	{
		try
		{
			this.actionEvent = e;
			if (!preActionPerformed(e))
			{//return immediately if no further action is needed.
				setSuccessfullyPerformed(false);
				return false;
			}
			if (!postActionPerformed(e))
			{
				setSuccessfullyPerformed(false);
			}
			else
			{
				setSuccessfullyPerformed(true);
			}
		}
		catch (Exception e1)
		{
			reportThrowableToUI(e1, this.mainFrame.getAssociatedUIComponent());
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
		return this.mainFrame.getAssociatedUIComponent();
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2008/12/09 19:04:17  linc
 * HISTORY      : First GUI release
 * HISTORY      :
 */
