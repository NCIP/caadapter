/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.common.actions;

/**
 * @author OWNER: $Author: phadkes $
 * @author LAST UPDATE $Author: phadkes $
 * @since      caAdapter  v4.2
 * @version    $Revision: 1.3 $
 * @date       $Date: 2008-09-24 17:49:22 $
*/

import gov.nih.nci.caadapter.ui.common.DefaultSettings;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

public abstract class AbstractContextInsensitiveAction extends AbstractAction {


	/**
	 * The flag indicate whether this action is successfully performed.
	 */
	private boolean successfullyPerformed = false;


	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	protected AbstractContextInsensitiveAction(String name, Icon icon)
	{
		super(name, icon);
	}

	/**
	 * Invoked when an action occurs.
	 * To make this function final is to force descendant classes to implement or override doAction() method instead of this method.
	 */
	public final void actionPerformed(ActionEvent e)
	{
		try
		{
			setSuccessfullyPerformed(doAction(e));
		}
		catch (Throwable t)
		{
//			t.printStackTrace();
			DefaultSettings.reportThrowableToLogAndUI(this, t, "", getAssociatedUIComponent(), false, false);
		}
		finally
		{
		}
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected abstract boolean doAction(ActionEvent e) throws Exception;

	/**
	 * Return the associated UI component.
	 * @return the associated UI component.
	 */
	protected abstract Component getAssociatedUIComponent();


	/**
	 * Set the flag value to indicate whether this action is successfully performed.
	 * It is better called "after" the actionPerformed() is called.
	 */
	public void setSuccessfullyPerformed(boolean successfullyPerformed)
	{
		this.successfullyPerformed = successfullyPerformed;
	}

	/**
	 * Return the flag indicate whether this action is successfully performed.
	 * It is better called "after" the actionPerformed() is called.
	 * @return the flag indicate whether this action is successfully performed.
	 */
	public boolean isSuccessfullyPerformed()
	{
		return successfullyPerformed;
	}

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/