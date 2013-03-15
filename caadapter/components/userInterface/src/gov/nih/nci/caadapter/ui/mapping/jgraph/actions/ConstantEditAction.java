/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.mapping.jgraph.actions;

import gov.nih.nci.caadapter.common.function.FunctionConstant;
import gov.nih.nci.caadapter.common.function.FunctionException;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.functions.FunctionBoxCell;
import gov.nih.nci.caadapter.ui.common.functions.FunctionBoxMutableViewInterface;
import gov.nih.nci.caadapter.ui.common.functions.FunctionConstantDefinitionDialog;
import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.caadapter.ui.mapping.jgraph.MiddlePanelJGraphController;

import org.jgraph.JGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * This class defines the edit constant action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2008-06-09 19:54:06 $
 */
public class ConstantEditAction extends DefaultAbstractJgraphAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: ConstantEditAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/jgraph/actions/ConstantEditAction.java,v 1.2 2008-06-09 19:54:06 phadkes Exp $";

	private static final String COMMAND_NAME = "Edit Constant...";
	private static final Character COMMAND_MNEMONIC = new Character('E');
//	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false);

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public ConstantEditAction(MappingMiddlePanel middlePanel, MiddlePanelJGraphController controller)
	{
		super(COMMAND_NAME, middlePanel, controller);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public ConstantEditAction(String name, MappingMiddlePanel middlePanel, MiddlePanelJGraphController controller)
	{
		this(name, null, middlePanel, controller);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public ConstantEditAction(String name, Icon icon, MappingMiddlePanel middlePanel, MiddlePanelJGraphController controller)
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
		JGraph graph = getController().getGraph();
		FunctionConstant constant = null;
		if (!graph.isSelectionEmpty())
		{//Find the constant metadata information to edit
			Object selectedObj = graph.getSelectionCell();
			if (selectedObj instanceof FunctionBoxCell)
			{
				Object userObject = ((FunctionBoxCell) selectedObj).getUserObject();
				if (userObject instanceof FunctionBoxMutableViewInterface)
				{
					constant = ((FunctionBoxMutableViewInterface) userObject).getFunctionConstant();
				}
			}
		}

		if (constant == null)
		{//no constant is actually selected.
			setSuccessfullyPerformed(false);
			return false;
		}
		FunctionConstantDefinitionDialog dialog = new FunctionConstantDefinitionDialog(new JFrame(), constant.getConstantFunctionName());
		dialog.setTitle("Edit Constant...");
		dialog.setConstantTypeClassString(constant.getType(), false);
		dialog.setConstantValue(constant.getValue());
		DefaultSettings.centerWindow(dialog);
		dialog.setVisible(true);
		if (dialog.isOkButtonClicked())
		{
			String typeValue = "";
            if (dialog.getConstantTypeClass() != null) typeValue = DefaultSettings.getClassNameWithoutPackage(dialog.getConstantTypeClass());
			else typeValue = dialog.getConstantFunctionName();
            try
            {
                constant.setType(typeValue);
			    constant.setValue(dialog.getConstantValue());
            }
            catch(FunctionException fe)
            {
                setSuccessfullyPerformed(false);
                return false;
            }
        }
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
 * HISTORY      : Revision 1.1  2007/04/03 16:17:57  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/12/28 20:50:36  umkis
 * HISTORY      : saveValue() and readValue() in FunctionConstant
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/10/07 21:48:10  jiangsc
 * HISTORY      : Enabled Constant Edit capability
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/09/29 22:12:20  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 */
