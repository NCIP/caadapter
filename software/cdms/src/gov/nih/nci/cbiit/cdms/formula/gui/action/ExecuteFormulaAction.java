package gov.nih.nci.cbiit.cdms.formula.gui.action;

import gov.nih.nci.cbiit.cdms.formula.gui.EditFormulaParameterDialog;
import gov.nih.nci.cbiit.cdms.formula.gui.ExecuteFormulaDialog;
import gov.nih.nci.cbiit.cdms.formula.gui.FrameMain;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class ExecuteFormulaAction extends AbstractAction {

	public final static int FORMULA_ACTION_ADD_PARAMETER=1;
	public final static int FORMULA_ACTION_EXECUTION=0;
	private int type=0;
	public ExecuteFormulaAction (String name, int dialogType)
	{
		super(name);
		type=dialogType;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if (type==FORMULA_ACTION_EXECUTION)
		{
			ExecuteFormulaDialog wizard = new ExecuteFormulaDialog(FrameMain.getSingletonInstance(), "Execute Formula", true);
			wizard.setVisible(true);
		}
		else if (type==FORMULA_ACTION_ADD_PARAMETER)
		{
			EditFormulaParameterDialog wizard = new EditFormulaParameterDialog(FrameMain.getSingletonInstance(), "Add Formula Parameter", true);
			wizard.setVisible(true);
		}
      
	}

}
