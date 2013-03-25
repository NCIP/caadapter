/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmts.ui.actions;

import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmts.ui.util.GeneralUtilities;
import gov.nih.nci.cbiit.cmts.util.FileUtil;

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
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2008-12-29 22:18:18 $
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
	protected ArrayList<?> getMissedResources()
	{
		return null;
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
