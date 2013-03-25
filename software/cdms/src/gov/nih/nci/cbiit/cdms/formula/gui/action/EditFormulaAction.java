/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui.action;

import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import gov.nih.nci.cbiit.cdms.formula.gui.FrameMain;
import gov.nih.nci.cbiit.cdms.formula.gui.NewFormulaWizard;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.tree.DefaultMutableTreeNode;

public class EditFormulaAction extends AbstractAction {

	DefaultMutableTreeNode formulaNode;
	public EditFormulaAction (String name)
	{
		super(name);
	}
	
	public void setFormulaNode(DefaultMutableTreeNode formulaNode) {
		this.formulaNode = formulaNode;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("EditFormulaAction.actionPerformed()..:"+arg0.getSource());
	       FrameMain mainFrame=FrameMain.getSingletonInstance();
	       NewFormulaWizard wizard = new NewFormulaWizard(mainFrame, "Edit Formula", true);
	       wizard.setLocation(mainFrame.getX()+mainFrame.getWidth()/4,
	    		   mainFrame.getY()+mainFrame.getHeight()/4);
	       wizard.setSize((int)mainFrame.getSize().getWidth()/2,
					(int)mainFrame.getSize().getHeight()/2);
	       FormulaMeta f=(FormulaMeta)formulaNode.getUserObject();
	       wizard.getFrontPage().eidtFormula(f);
	       wizard.setVisible(true);
	}

}
