/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui.action;

import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaStore;
import gov.nih.nci.cbiit.cdms.formula.gui.FrameMain;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class DeleteFormulaAction extends AbstractAction {

	DefaultMutableTreeNode formulaNode;
	public DeleteFormulaAction (String name)
	{
		super(name);
	}
	
	
	public void setFormulaNode(DefaultMutableTreeNode formulaNode) {
		this.formulaNode = formulaNode;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		DefaultMutableTreeNode parentNode=(DefaultMutableTreeNode)formulaNode.getParent();
		parentNode.remove(formulaNode);
		FormulaStore fs=(FormulaStore)parentNode.getUserObject();
		
		FormulaMeta f=(FormulaMeta)formulaNode.getUserObject();
		fs.getFormula().remove(f);
		DefaultTreeModel  treeModel=(DefaultTreeModel)FrameMain.getSingletonInstance().getMainPanel().getLocalTree().getModel();
		treeModel.reload(parentNode);
		FrameMain.getSingletonInstance().getMainPanel().getLocalTree().setSelectionRow(0);
	}
}
