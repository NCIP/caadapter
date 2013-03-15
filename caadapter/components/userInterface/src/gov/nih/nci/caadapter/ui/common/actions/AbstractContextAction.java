/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.actions;

import gov.nih.nci.caadapter.common.Message;
import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.validation.ValidatorResult;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.message.ValidationMessageDialog;
import gov.nih.nci.caadapter.ui.main.HL7AuthorizationDialog;
import gov.nih.nci.caadapter.ui.main.VerifyResourceDialog;

import javax.swing.JFrame;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.JComponent;

import java.awt.event.ActionEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Dialog;
import java.util.ArrayList;

/**
 * This class provides generic data structure for actions in a context-based application.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-06-09 19:53:51 $
 */
public abstract class AbstractContextAction extends AbstractAction
{
	/**
	 * Commands that can be handled by individual Document panel object types
	 */
	public static final int DOCUMENT_ACTION_TYPE = 1;

	/**
	 * Commands that can be handled by the desktop itself (such as EXIT)
	 */
	public static final int DESKTOP_ACTION_TYPE = 2;

	/**
	 * The filename of the icon for this menu (if any)
	 */
	private String iconName;

	/**
	 * The type of action (if any) to perform for this menu
	 */
	private int actionCommandType;

	/**
	 * The flag indicate whether this action is successfully performed.
	 */
	private boolean successfullyPerformed = false;

	/**
	 * The flag indicate whether this action is only for authorized user
	 */
	private boolean authorizationRequired=false;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	protected AbstractContextAction()
	{
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	protected AbstractContextAction(String name)
	{
		super(name);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	protected AbstractContextAction(String name, Icon icon)
	{
		super(name, icon);
	}

	public Character getMnemonic()
	{
		Integer valueInt = (Integer) getValue(Action.MNEMONIC_KEY);
		Character character = new Character((char)valueInt.intValue());
		return (Character) character;
	}

	public void setMnemonic(Character mnemonic)
	{
		putValue(Action.MNEMONIC_KEY, new Integer(mnemonic.charValue()));
	}

	public String getIconName()
	{
		return iconName;
	}

	public void setIconName(String iconName)
	{
		this.iconName = iconName;
	}

	public int getActionCommandType()
	{
		return actionCommandType;
	}

	public void setActionCommandType(int actionCommandType)
	{
		this.actionCommandType = actionCommandType;
	}

	public String getShortDescription()
	{
		return (String)getValue(Action.SHORT_DESCRIPTION);
	}

	public void setShortDescription(String description)
	{
		putValue(Action.SHORT_DESCRIPTION, description);
	}

	public KeyStroke getAcceleratorKey()
	{
		return (KeyStroke)getValue(Action.ACCELERATOR_KEY);
	}

	public void setAcceleratorKey(KeyStroke acceleratorKey)
	{
		putValue(Action.ACCELERATOR_KEY, acceleratorKey);
	}

	public String getName()
	{
		return (String) getValue(Action.NAME);
	}

	public void setName(String name)
	{
		putValue(Action.NAME, name);
	}

	public Icon getIcon()
	{
		return (Icon) getValue(Action.SMALL_ICON);
	}

	public void setIcon(Icon icon)
	{
		putValue(Action.SMALL_ICON, icon);
	}

	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof AbstractContextAction)) return false;

		final AbstractContextAction abstractContextAction = (AbstractContextAction) o;

		String thisName = getName();
		String thatName = abstractContextAction.getName();

		Icon thisIcon = getIcon();
		Icon thatIcon = abstractContextAction.getIcon();
		if(!GeneralUtilities.areEqual(thisName, thatName)) return false;
		if(!GeneralUtilities.areEqual(thisIcon, thatIcon)) return false;
		if (actionCommandType != abstractContextAction.actionCommandType) return false;
		if (getAcceleratorKey() != null ? !getAcceleratorKey().equals(abstractContextAction.getAcceleratorKey()) : abstractContextAction.getAcceleratorKey()!= null) return false;
		if (getShortDescription() != null ? !getShortDescription().equals(abstractContextAction.getShortDescription()) : abstractContextAction.getShortDescription() != null) return false;
		if (iconName != null ? !iconName.equals(abstractContextAction.iconName) : abstractContextAction.iconName != null) return false;
		if (getMnemonic() != null ? !getMnemonic().equals(abstractContextAction.getMnemonic()) : abstractContextAction.getMnemonic() != null) return false;

		return true;
	}

	public int hashCode()
	{
		int result;
		String name = getName();
		Icon thisIcon = getIcon();
		result = name!=null ? name.hashCode() : 0;
		result = 29 * result + (thisIcon!=null ? thisIcon.hashCode() : 0);
		result = 29 * result + (getMnemonic() != null ? getMnemonic().hashCode() : 0);
		result = 29 * result + (iconName != null ? iconName.hashCode() : 0);
		result = 29 * result + actionCommandType;
		result = 29 * result + (getShortDescription() != null ? getShortDescription().hashCode() : 0);
		result = 29 * result + (getAcceleratorKey() != null ? getAcceleratorKey().hashCode() : 0);
		return result;
	}

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

	/**
	 * Currently utilize JOptionPane to report any given throwable to UI.
	 * @param t
	 * @param parentComponent
	 */
	protected void reportThrowableToUI(Throwable t, Component parentComponent)
	{
		DefaultSettings.reportThrowableToLogAndUI(this, t, "", parentComponent, false, false);
	}

	/**
	 * Return a convenient UI Working Directory, which may or may not be the same as the value from FileUtil.getWorkingDirPath().
	 * @return a convenient UI Working Directory, which may or may not be the same as the value from FileUtil.getWorkingDirPath().
	 */
	protected String getUIWorkingDirectoryPath()
	{
		/**
		 * Though current implementation may just turn around call FileUtil.getUIWorkingDirectoryPath(),
		 * this provides a layer of flexiblity of change of implemnetation at later time.
		 */
		return FileUtil.getUIWorkingDirectoryPath();
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
			DefaultSettings.reportThrowableToLogAndUI(this, t, "", getAssociatedUIComponent(), false, false);
		}
		finally
		{
		}
	}
	/**
	 * Find the missed system resource in performing the defined action, the missed resource may
	 * include library jar file, zip file, image, property file, etc
	 * @return List of missed resourc or null as default if this method is not overridden by subclass
	 */
	protected ArrayList getMissedResources()
	{
		return null;
	}

	/**
	 * Verify if system resources are ready in performing the defined actioin
	 * @return true if all resource are ready
	 */
	protected boolean isResourceReady(JFrame owner)
	{
		ArrayList rscMissed=getMissedResources();
		if (rscMissed==null|rscMissed.isEmpty())
			return true;
		//some resource is missing
		String warningMsg=VerifyResourceDialog.setWarningContext(rscMissed, VerifyResourceDialog.DEFAULT_CONTEXT_FILE_PATH);
		JOptionPane.showMessageDialog(owner, warningMsg, "Warning: Resources Missing - "+this.getName(), JOptionPane.DEFAULT_OPTION);
//
//		new VerifyResourceDialog(owner, "Warning: Resources Missing ", rscMissed);
		return false;
	}

	/**
	 * Authorized request in performing the defined action
	 */
	protected boolean isRequestAuthorized(JFrame owner)
	{
		if (isAuthorizationRequired())
		{
			if (!CaadapterUtil.isAuthorizedUser())
				new HL7AuthorizationDialog (owner,"Notice: Use HL7 Artifacts");
			System.out.println("AbstractContextAction.isRequestAuthorized()..waiting:"+CaadapterUtil.isAuthorizedUser());
			return CaadapterUtil.isAuthorizedUser();
		}
		return true;
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
	 * exam the given validator results and report to UI if necessary.
	 * @param validatorResults
	 * @return false, if anything serious is reported to UI for user's attention, which means the process has to abort;
	 * true, if everything is OK and process could continue;
	 */
	public boolean handleValidatorResults(ValidatorResults validatorResults)
	{
		boolean everythingGood = true;
		if ((validatorResults != null && validatorResults.hasFatal()))
		{
			int size = validatorResults.getAllMessages().size();
			if (size == 1)
			{
				Message msg = validatorResults.getMessages(ValidatorResult.Level.FATAL).get(0);
				DefaultSettings.reportThrowableToLogAndUI(this, null, msg.toString(), getAssociatedUIComponent(), true, false);
			}
			else
			{
				Container parentContainer = figureOutContainer();
				ValidationMessageDialog dlg = null;
				if(parentContainer instanceof Frame)
				{
					dlg = new ValidationMessageDialog((Frame) parentContainer, true);
				}
				else if(parentContainer instanceof Dialog)
				{
					dlg = new ValidationMessageDialog((Dialog) parentContainer, true);
				}
				if(dlg!=null)
				{
					dlg.setDisplayPopupConfirmationMessage(false);
					dlg.setValidatorResults(validatorResults);
					DefaultSettings.centerWindow(dlg);
					dlg.setVisible(true);
				}
			}
			everythingGood = false;
		}
		return everythingGood;
	}

	private Container figureOutContainer()
	{
		Component comp = getAssociatedUIComponent();
		Container container = null;
		if(comp instanceof Container)
		{
			container = (Container) comp;
		}
		else if(comp instanceof JComponent)
		{
			container = DefaultSettings.findRootContainer((JComponent) comp);
		}
		if(container==null)
		{
			container = new JFrame();
		}
		return container;
	}

	public boolean isAuthorizationRequired() {
		return authorizationRequired;
	}

	public void setAuthorizationRequired(boolean authorizationRequired) {
		this.authorizationRequired = authorizationRequired;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2007/10/04 18:08:38  wangeug
 * HISTORY      : verify resource based on module
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/09/19 16:41:21  wangeug
 * HISTORY      : authorized user request
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.21  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.20  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.19  2006/01/03 18:26:16  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/12/14 21:37:16  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/12/08 23:21:41  jiangsc
 * HISTORY      : Upgrade the handleValidatorResults() function.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/12/01 20:03:39  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/11/14 19:55:51  jiangsc
 * HISTORY      : Implementing UI enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/10/26 18:12:29  jiangsc
 * HISTORY      : replaced printStackTrace() to Log.logException
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/10/26 16:22:10  jiangsc
 * HISTORY      : Face lift to provide better error report.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/10/13 17:37:41  jiangsc
 * HISTORY      : Enhanced UI reporting on exceptions.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/10/13 15:44:39  jiangsc
 * HISTORY      : Re-engineered exception reporting to DefaultSettings class.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/11 22:10:27  jiangsc
 * HISTORY      : Open/Save File Dialog consolidation.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/02 22:23:26  jiangsc
 * HISTORY      : Newly enhanced context-sensitive menus and toolbar.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/07/22 20:53:02  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
