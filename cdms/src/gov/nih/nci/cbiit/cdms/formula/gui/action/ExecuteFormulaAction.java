package gov.nih.nci.cbiit.cdms.formula.gui.action;

import gov.nih.nci.cbiit.cdms.formula.gui.ExecuteFormulaDialog;
import gov.nih.nci.cbiit.cdms.formula.gui.FormulaGuiUtil;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

public class ExecuteFormulaAction extends AbstractAction {

	public ExecuteFormulaAction (String name)
	{
		super(name);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
       JMenuItem menuItem=(JMenuItem)arg0.getSource();
       ExecuteFormulaDialog wizard = new ExecuteFormulaDialog(FormulaGuiUtil.getMainFrame(), "Execute Formula", true);
       wizard.setVisible(true);
      
	}

}
