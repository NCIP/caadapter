package gov.nih.nci.cbiit.cdms.formula.gui.action;

import gov.nih.nci.cbiit.cdms.formula.gui.ExecuteFormulaDialog;
import gov.nih.nci.cbiit.cdms.formula.gui.FrameMain;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class ExecuteFormulaAction extends AbstractAction {

	public ExecuteFormulaAction (String name)
	{
		super(name);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
       ExecuteFormulaDialog wizard = new ExecuteFormulaDialog(FrameMain.getSingletonInstance(), "Execute Formula", true);
       wizard.setVisible(true);
      
	}

}
