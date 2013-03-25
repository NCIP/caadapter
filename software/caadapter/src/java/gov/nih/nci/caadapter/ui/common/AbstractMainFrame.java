/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.ui.common;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

/* AbstractMainFrame extends JFrame
 * @author OWNER: $Author: phadkes $
 * @author LAST UPDATE $Author: phadkes $
 * @since      caAdapter  v4.2
 * @version    $Revision: 1.4 $
 * @date       $Date: 2008-09-24 17:59:38 $
 */
public abstract class AbstractMainFrame extends JFrame {

	public abstract void addNewTab(JPanel panel);
	public abstract JTabbedPane getTabbedPane();
	public abstract void closeTab();

	/**
	 * Will return the component of the given classValue. If nothing is found, return null.
	 *
	 * @param classValue
	 * @param bringToFront if exists, will bring it to front to be the selected.
	 * @return the component of the given classValue. If nothing is found, return null.
	 */
	public abstract JComponent hasComponentOfGivenClass(Class classValue,
			boolean bringToFront);

	/**
	 * Return all tabs in the frame, if nothing exists, will return an empty list.
	 * @return all tabs in list.
	 */
	public abstract java.util.List<Component> getAllTabs();

	/**
	 * Set the title value of currently selected inner panel.
	 * @param newTitle
	 * @return true if the title is successfully updated.
	 */
	public abstract boolean setCurrentPanelTitle(String newTitle);

	public abstract void showHelpContentViewer();

	public abstract void showHelpContentWithNodeID(String id);

	public abstract void showHelpContentWithNodeID(String id, Dialog dispose);

	public abstract void resetCenterPanel();
	public abstract void updateToolBar(JToolBar newToolBar) ;
	public abstract void updateToolBar(JToolBar newToolBar, JButton rightSideButton) ;
	/**
	 * Overridden so we can exit when window is closed
	 */
	public  void processWindowEvent(WindowEvent e)
	{
		super.processWindowEvent(e);
	}

	public abstract void exit();

}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
