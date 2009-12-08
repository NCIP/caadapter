/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */


package gov.nih.nci.cbiit.cmps.ui.actions;


import gov.nih.nci.cbiit.cmps.ui.common.ContextManagerClient;

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
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-09 19:04:17 $
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
	public static String RCSID = "$Header: /share/content/gforge/caadapter/cmps/src/gov/nih/nci/cbiit/cmps/ui/actions/DefaultContextCloseAction.java,v 1.1 2008-12-09 19:04:17 linc Exp $";

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
 */
