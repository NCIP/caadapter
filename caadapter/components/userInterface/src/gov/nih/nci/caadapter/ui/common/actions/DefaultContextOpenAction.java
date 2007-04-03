/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/actions/DefaultContextOpenAction.java,v 1.1 2007-04-03 16:17:15 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.caadapter.ui.common.actions;

import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.context.ContextManagerClient;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;

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
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:15 $
 */
public abstract class DefaultContextOpenAction extends AbstractContextAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: DefaultContextOpenAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/actions/DefaultContextOpenAction.java,v 1.1 2007-04-03 16:17:15 wangeug Exp $";

	protected static final String COMMAND_NAME = ActionConstants.OPEN;
	protected static final Character COMMAND_MNEMONIC = new Character('O');
	protected static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK, false);
	protected static final ImageIcon IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("fileOpen.gif"));
	protected static final String TOOL_TIP_DESCRIPTION = ActionConstants.OPEN;

	protected transient AbstractMainFrame mainFrame;
	protected transient ActionEvent actionEvent;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public DefaultContextOpenAction(AbstractMainFrame mainFrame)
	{
		this(COMMAND_NAME, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public DefaultContextOpenAction(String name, AbstractMainFrame mainFrame)
	{
		this(name, IMAGE_ICON, mainFrame);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public DefaultContextOpenAction(String name, Icon icon, AbstractMainFrame mainFrame)
	{
		super(name, icon);
		this.mainFrame = mainFrame;
		setAdditionalAttributes();
	}

	protected void setAdditionalAttributes()
	{//override super class's one to plug in its own attributes.
		setMnemonic(COMMAND_MNEMONIC);
		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
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
			reportThrowableToUI(t, this.mainFrame);
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
			reportThrowableToUI(t, this.mainFrame);
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
			reportThrowableToUI(e1, this.mainFrame);
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
		return this.mainFrame;
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.12  2006/10/19 21:15:34  wuye
 * HISTORY      : Added a new code to initiate a object2db mapping window.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/21 15:12:19  jiangsc
 * HISTORY      : Reporting error enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/04 18:54:06  jiangsc
 * HISTORY      : Consolidated tabPane management into MainFrame
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/02 22:23:27  jiangsc
 * HISTORY      : Newly enhanced context-sensitive menus and toolbar.
 * HISTORY      :
 */
