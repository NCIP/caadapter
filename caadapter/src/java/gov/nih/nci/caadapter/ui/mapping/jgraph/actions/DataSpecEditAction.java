package gov.nih.nci.caadapter.ui.mapping.jgraph.actions;

import gov.nih.nci.caadapter.ui.mapping.MappingMiddlePanel;
import gov.nih.nci.caadapter.ui.mapping.jgraph.MiddlePanelJGraphController;
import gov.nih.nci.caadapter.ui.common.functions.FunctionBoxCell;
import gov.nih.nci.caadapter.ui.common.functions.FunctionBoxMutableViewInterface;

import gov.nih.nci.caadapter.ui.common.functions.FunctionDataSpecDefinitionDialog;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;

import gov.nih.nci.caadapter.common.function.FunctionException;
import gov.nih.nci.caadapter.common.function.FunctionDataSpecExe;

import java.awt.event.ActionEvent;
import java.awt.*;

import org.jgraph.JGraph;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Apr 25, 2010
 * Time: 6:46:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataSpecEditAction extends DefaultAbstractJgraphAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: DataSpecEditAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/jgraph/actions/DataSpecEditAction.java,v 1.3 2010-03-25 19:56:23 wangeug Exp $";

	private static final String COMMAND_NAME = "Edit DataSpec...";

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public DataSpecEditAction(MappingMiddlePanel middlePanel, MiddlePanelJGraphController controller)
	{
		super(COMMAND_NAME, middlePanel, controller);
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
		FunctionDataSpecExe fDataSpec = null;
		if (!graph.isSelectionEmpty())
		{//Find the constant metadata information to edit
			Object selectedObj = graph.getSelectionCell();
			if (selectedObj instanceof FunctionBoxCell)
			{
				Object userObject = ((FunctionBoxCell) selectedObj).getUserObject();
				if (userObject instanceof FunctionBoxMutableViewInterface)
				{
					fDataSpec = ((FunctionBoxMutableViewInterface) userObject).getFunctionDataSpecExe();
				}
			}
		}

		if (fDataSpec == null)
		{//no constant is actually selected.
			setSuccessfullyPerformed(false);
			return false;
		}
		FunctionDataSpecDefinitionDialog dialog = new FunctionDataSpecDefinitionDialog(new JFrame(), fDataSpec.getFunctionName(), fDataSpec.getDataSpec());
		dialog.setTitle("Edit DataSpec of "+fDataSpec.getFunctionName()+"...");
		dialog.setType(fDataSpec.getType());
		dialog.setValue(fDataSpec.getValue());
		DefaultSettings.centerWindow(dialog);
		dialog.setVisible(true);
		if (dialog.isOkButtonClicked())
		{
			String typeValue = "";
            if (dialog.getType() != null) typeValue = dialog.getType();

            try
            {
                fDataSpec.setType(typeValue);
			    fDataSpec.setValue(dialog.getValue());
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
