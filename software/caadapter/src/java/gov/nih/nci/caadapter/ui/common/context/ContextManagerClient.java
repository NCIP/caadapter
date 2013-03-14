/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.common.context;

import javax.swing.Action;
import java.util.List;
import java.util.Map;

/**
 * This interface defines a list contracts that a context manager expects
 * any pluggable context to be implemented. A typical example of a ContextManagerClient
 * is a panel that will be hosted by the MainFrame.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:53:51 $
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
	 * Explicitly reload information from the internal given file.
	 * @throws Exception
	 */
	void reload() throws Exception;
	//public void report();

    /**
	 * Return a list of Action objects that is included in this Context manager.
	 * @return a list of Action objects that is included in this Context manager.
	 */
    java.util.List<Action> getToolbarActionList();
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:14  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/11/16 20:59:49  umkis
 * HISTORY      : defect# 195, getToolbarActionList() is added for tool bar menu.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/18 15:30:15  jiangsc
 * HISTORY      : First implementation on Switch control.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/05 20:35:45  jiangsc
 * HISTORY      : 0)Implemented field sequencing on CSVPanel but needs further rework;
 * HISTORY      : 1)Removed (Yes/No) for questions;
 * HISTORY      : 2)Removed double-checking after Save-As;
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/02 22:23:26  jiangsc
 * HISTORY      : Newly enhanced context-sensitive menus and toolbar.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/07/27 22:41:18  jiangsc
 * HISTORY      : Consolidated context sensitive menu implementation.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/22 20:53:04  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
