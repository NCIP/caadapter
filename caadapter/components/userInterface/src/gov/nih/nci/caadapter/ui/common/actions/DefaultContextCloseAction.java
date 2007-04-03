/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/actions/DefaultContextCloseAction.java,v 1.1 2007-04-03 16:17:15 wangeug Exp $
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

import gov.nih.nci.caadapter.ui.common.context.ContextManagerClient;

import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.Icon;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;


/**
 * This class provides generic handling of close action against a context client,
 * which in most cases is a JPanel or DefaultContextManagerClientPanel descendant
 * to provide UI representation of certain functionality.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:15 $
 */
public abstract class DefaultContextCloseAction extends DefaultCloseAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: DefaultContextCloseAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/actions/DefaultContextCloseAction.java,v 1.1 2007-04-03 16:17:15 wangeug Exp $";

	private transient ContextManagerClient contextClient;
	protected transient  JFrame mainFrame;

	public DefaultContextCloseAction(String name, Icon icon, ContextManagerClient contextClient)
	{
		super(name, icon, null);
		this.contextClient = contextClient;
		if(contextClient instanceof JComponent)
		{
			JRootPane rootPane = ((JComponent)contextClient).getRootPane();
			if (rootPane != null)
			{
				this.mainFrame = (JFrame) rootPane.getParent();
				super.setFrame(mainFrame);
			}
		}
	}

	/**
	 * This function will be called by actionPerfermed() to do some pre-work
	 * before the real action is performed.
	 * @param e
	 * @return 	true if the action shall proceed;
	 * 			false if the action cannot continue, shall return immediately.
	 */
	protected boolean preActionPerformed(ActionEvent e)
	{
		try
		{
			if (contextClient.isChanged())
			{
				JComponent comp = null;
				if (contextClient instanceof JComponent)
				{
					comp = (JComponent) contextClient;
				}
				int choice = JOptionPane.showConfirmDialog(comp, "Data has been changed but is not saved. Would you like to save your changes?", "Data Changed", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (choice == JOptionPane.CANCEL_OPTION)
				{//do nothing just abort the close action.
					return false;
				}
				if (choice == JOptionPane.YES_OPTION)
				{
					Action saveAction = contextClient.getDefaultSaveAction();
					saveAction.actionPerformed(e);
				}
				return true;
			}
			else
			{
				return true;
			}
		}
		catch (Exception e1)
		{
			reportThrowableToUI(e1, this.mainFrame);
			return false;
		}
	}

	/**
	 * Descendant class could override this method to provide actions to be executed after the
	 * given action is performed, such as update menu status, etc.
	 * @param e
	 * @return the indication whether it is successfully performed.
	 */
	protected boolean postActionPerformed(ActionEvent e)
	{
		return true;
	}

	/**
	 * Handle action.
	 * @param e
	 */
	protected boolean doAction(ActionEvent e)
	{
		try
		{
            if(!preActionPerformed(e))
			{//return immediately if no further action is needed.
				setSuccessfullyPerformed(false);
				return false;
			}
			if (this.mainFrame == null)
			{
				JRootPane rootPane = ((JComponent) contextClient).getRootPane();
				if(rootPane != null)
				{
					this.mainFrame = (JFrame) rootPane.getParent();
				}
			}
			super.doAction(e);
			//call post action
			if(isSuccessfullyPerformed())
			{//only go further if and only if
				// the previous steps are executed successfully.
				if(!postActionPerformed(e))
				{
					setSuccessfullyPerformed(false);
				}
				else
				{
					setSuccessfullyPerformed(true);
				}
			}
		}
		catch (Exception e1)
		{
			reportThrowableToUI(e1, this.mainFrame);
			setSuccessfullyPerformed(false);
		}
		return isSuccessfullyPerformed();
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.14  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/18 14:52:28  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/09/30 21:18:03  jiangsc
 * HISTORY      : Minor update - corrected wording
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/30 19:27:37  jiangsc
 * HISTORY      : minor arrangement
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/05 20:35:45  jiangsc
 * HISTORY      : 0)Implemented field sequencing on CSVPanel but needs further rework;
 * HISTORY      : 1)Removed (Yes/No) for questions;
 * HISTORY      : 2)Removed double-checking after Save-As;
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/02 22:23:27  jiangsc
 * HISTORY      : Newly enhanced context-sensitive menus and toolbar.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/27 22:41:18  jiangsc
 * HISTORY      : Consolidated context sensitive menu implementation.
 * HISTORY      :
 */
