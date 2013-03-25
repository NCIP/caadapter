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


package gov.nih.nci.cbiit.cmts.ui.common;

import javax.swing.Action;
import java.util.List;
import java.util.Map;

/**
 * This interface defines a list contracts that a context manager expects
 * any pluggable context to be implemented. A typical example of a ContextManagerClient
 * is a panel that will be hosted by the MainFrame.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-03 20:46:14 $
 */
public interface ContextManagerClient
{
	/**
	 * Indicate whether or not it is changed.
	 * @return whether or not it is changed.
	 */
	boolean isChanged();

	/**
	 * Explicitly set the value.
	 * @param newValue
	 */
	void setChanged(boolean newValue);

	/**
	 * Return a list menu items under the given menu to be updated.
	 * @param menu_name
	 * @return the action map
	 */
	Map getMenuItems(String menu_name);

	/**
	 * return the save action inherited with this client.
	 * @return the save action inherited with this client.
	 */
	Action getDefaultSaveAction();

	/**
	 * return the close action inherited with this client.
	 * @return the close action inherited with this client.
	 */
	Action getDefaultCloseAction();

	/**
	 * return the open action inherited with this client.
	 * @return the open action inherited with this client.
	 */
	Action getDefaultOpenAction();

	/**
	 * Return a list of file objects that this context is associated with;
	 * if nothing is associated, will return an empty list instead of null.
	 * @return a list of file objects that this context is associated with.
	 */
	List<java.io.File> getAssociatedFileList();


    /**
	 * Return a list of Action objects that is included in this Context manager.
	 * @return a list of Action objects that is included in this Context manager.
	 */
    java.util.List<Action> getToolbarActionList();
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 */
