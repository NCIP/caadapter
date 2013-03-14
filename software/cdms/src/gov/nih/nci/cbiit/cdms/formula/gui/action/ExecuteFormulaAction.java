/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui.action;

import gov.nih.nci.cbiit.cdms.formula.core.DataElement;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaStore;
import gov.nih.nci.cbiit.cdms.formula.gui.EditFormulaParameterDialog;
import gov.nih.nci.cbiit.cdms.formula.gui.ExecuteFormulaDialog;
import gov.nih.nci.cbiit.cdms.formula.gui.FrameMain;
import gov.nih.nci.cbiit.cdms.formula.gui.tree.TreeNodeFormulaStore;

import java.awt.event.ActionEvent;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class ExecuteFormulaAction extends AbstractAction {

	public final static int FORMULA_ACTION_ADD_PARAMETER=1;
	public final static int FORMULA_ACTION_EDIT_PARAMETER=2;
	public final static int FORMULA_ACTION_DELTE_PARAMETER=3;
	
	public final static int FORMULA_ACTION_EXECUTION=0;
	private int type=0;
	DefaultMutableTreeNode formulaNode;
	private DataElement parameter;
	public ExecuteFormulaAction (String name, int dialogType)
	{
		super(name);
		type=dialogType;
	}
	
 

	public void setFormulaNode(DefaultMutableTreeNode node) {
		formulaNode=node;
	}

	public void setParameter(DataElement parameter) {
		this.parameter = parameter;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if (type==FORMULA_ACTION_EXECUTION)
		{
			ExecuteFormulaDialog wizard = new ExecuteFormulaDialog(FrameMain.getSingletonInstance(), "Execute Formula", true);
			wizard.setVisible(true);
		}
		else if (type==FORMULA_ACTION_ADD_PARAMETER
				||type==FORMULA_ACTION_EDIT_PARAMETER)
		{
			EditFormulaParameterDialog wizard = new EditFormulaParameterDialog(FrameMain.getSingletonInstance(), "Edit Data Element", true);
			wizard.setFormulaNode(formulaNode);
 
			wizard.setParameter(parameter);
			wizard.setActionType(ExecuteFormulaAction.FORMULA_ACTION_EDIT_PARAMETER);
			wizard.setVisible(true);		
 		}
		else if (type==FORMULA_ACTION_DELTE_PARAMETER)
		{
			FormulaMeta formula=(FormulaMeta)formulaNode.getUserObject();
			formula.getParameter().remove(parameter);
			for (int i=formulaNode.getChildCount();i>0;i--)
			{
				DefaultMutableTreeNode childNode=(DefaultMutableTreeNode)formulaNode.getChildAt(i-1);
				DataElement param=(DataElement)childNode.getUserObject();
				if (param.equals(parameter))
					formulaNode.remove(childNode);
			}
			formula.setDateModified(new Date());
			DefaultTreeModel  treeModel=(DefaultTreeModel)FrameMain.getSingletonInstance().getMainPanel().getLocalTree().getModel();
			treeModel.reload(formulaNode);
		}
	}

}
