/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.mapping.jgraph.actions;

import gov.nih.nci.caadapter.common.function.meta.FunctionMeta;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.caadapter.ui.mapping.jgraph.MiddlePanelJGraphController;

import org.jgraph.JGraph;
import org.jgraph.graph.BasicMarqueeHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;

/**
 * This class defines the add function box action.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-11-10 21:30:38 $
 */
public class AddFunctionalBoxAction extends DefaultAbstractJgraphAction
{
//	private static final String ADD_FUNCTION_COMMAND_NAME = "Add Function";
	private static final String COMMAND_NAME = "Add Function";
	private static final Character COMMAND_MNEMONIC = new Character('A');
//	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_F4, Event.ALT_MASK, false);
	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public AddFunctionalBoxAction(MappingMiddlePanel middlePanel, MiddlePanelJGraphController controller)
	{
		this(COMMAND_NAME, null, middlePanel, controller);
	}


	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public AddFunctionalBoxAction(String name, Icon icon, MappingMiddlePanel middlePanel, MiddlePanelJGraphController controller)
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
		Object function = getMiddlePanel().getFunctionBoxSelection();
		if (function == null || !(function instanceof FunctionMeta))
		{
			String msg = null;
			if (function == null)
			{
				msg = "No function is selected!";
			}
			else
			{
				msg = "Invalid function type. It is expected to be of type FunctionMeta.";
			}
			JOptionPane.showMessageDialog(getMiddlePanel(), msg, "Warning", JOptionPane.WARNING_MESSAGE);
			setSuccessfullyPerformed(false);
			return false;
		}
		Point2D startPoint = null;
		Object eSource = e.getSource();
		if (eSource instanceof JGraph)
		{
			BasicMarqueeHandler marqueeHandler = ((JGraph) eSource).getMarqueeHandler();
			startPoint = marqueeHandler.getCurrentPoint();
		}
		getController().addFunction((FunctionMeta) function, startPoint);
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
 * HISTORY      : Revision 1.2  2008/06/09 19:54:06  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:57  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/07/22 20:53:17  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
