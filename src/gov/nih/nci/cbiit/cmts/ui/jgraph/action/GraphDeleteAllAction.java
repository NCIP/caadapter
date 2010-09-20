/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */

package gov.nih.nci.cbiit.cmts.ui.jgraph.action;


import org.jgraph.JGraph;

import gov.nih.nci.cbiit.cmts.ui.actions.DefaultAbstractJgraphAction;
import gov.nih.nci.cbiit.cmts.ui.jgraph.MiddlePanelJGraphController;
import gov.nih.nci.cbiit.cmts.ui.mapping.MappingMiddlePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * This class defines the action to delete selected graphic cells.
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-10 15:43:02 $
 */
public class GraphDeleteAllAction extends DefaultAbstractJgraphAction
{
	private static final String COMMAND_NAME = "Delete All";
	private static final Character COMMAND_MNEMONIC = new Character('A');
	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);
	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public GraphDeleteAllAction(MiddlePanelJGraphController controller)
	{
		this(null, controller);
	}

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public GraphDeleteAllAction(MappingMiddlePanel middlePanel, MiddlePanelJGraphController controller)
	{
		this(COMMAND_NAME, middlePanel, controller);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public GraphDeleteAllAction(String name, MappingMiddlePanel middlePanel, MiddlePanelJGraphController controller)
	{
		this(name, null, middlePanel, controller);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public GraphDeleteAllAction(String name, Icon icon, MappingMiddlePanel middlePanel, MiddlePanelJGraphController controller)
	{
		super(name, icon, middlePanel, controller);
		setMnemonic(COMMAND_MNEMONIC);
//		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e)
	{
//		Log.logInfo(this, "GraphDeleteAction's actionPerformed() is called.");
		JGraph graph = getController().getMiddlePanel().getGraph();
//		if (!graph.isSelectionEmpty())
//		{
			int userChoice = JOptionPane.showConfirmDialog(getMiddlePanel(),
				"ALL mappings will be deleted, are you sure?", "Question",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (userChoice == JOptionPane.YES_OPTION)
			{
				getController().handleDeleteAll();
			}
//		}
//		else
//		{
//			JOptionPane.showMessageDialog(getMiddlePanel(), "No graph is currently selected.", "No selection", JOptionPane.WARNING_MESSAGE);
//		}
		setSuccessfullyPerformed(true);
		return isSuccessfullyPerformed();
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return getMiddlePanel();
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 */
