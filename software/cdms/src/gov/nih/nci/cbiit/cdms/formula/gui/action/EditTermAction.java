/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui.action;

 
import gov.nih.nci.cbiit.cdms.formula.core.TermMeta;
import gov.nih.nci.cbiit.cdms.formula.core.TermType;
import gov.nih.nci.cbiit.cdms.formula.gui.EditTermWizard;
import gov.nih.nci.cbiit.cdms.formula.gui.FrameMain;
 
import gov.nih.nci.cbiit.cdms.formula.gui.view.TermView;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
 

public class EditTermAction extends AbstractAction {

	TermView viewMeta;
	private int actionType;
	public static final int TYPE_EDIT=0;
	public static final int TYPE_DELETE=1;
	public EditTermAction (String name, int type)
	{
		super(name);
		actionType=type;
	}
	
	public void setTermView(TermView view) {
		viewMeta = view;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("EditTermAction.actionPerformed()..:"+arg0.getSource());
		FrameMain mainFrame=FrameMain.getSingletonInstance();
		TermView parentView=viewMeta.getParentView();
		switch (actionType)
		{
			case TYPE_EDIT:
                TermMeta t2Meta = viewMeta.getTerm();
                if (t2Meta.getType() == TermType.EXPRESSION) parentView = viewMeta;
                else parentView = viewMeta.getParentView();

                EditTermWizard wizard ;
				if (parentView==null)
					wizard= new EditTermWizard(mainFrame, viewMeta.getTerm(),true);
				else
					wizard= new EditTermWizard(mainFrame, parentView.getTerm(),true);
			    wizard.setLocation(mainFrame.getX()+mainFrame.getWidth()/4,
			    		   mainFrame.getY()+mainFrame.getHeight()/4);
			    wizard.setSize((int)mainFrame.getSize().getWidth()/2,
						(int)mainFrame.getSize().getHeight()/2);
			    wizard.setVisible(true);
			    break;
			case TYPE_DELETE:
                TermMeta tMeta = viewMeta.getTerm();
                if (tMeta.getType() == TermType.EXPRESSION) parentView = viewMeta;
                else parentView = viewMeta.getParentView();

                if (parentView.getParentView()==null)
				{
					String msg="You can not delete the root expression !!\n"+parentView.toString() ;
		            JOptionPane.showMessageDialog(mainFrame, msg, "Warning", JOptionPane.ERROR_MESSAGE);
					return;
				}
				String errMsg="Are you sure to delete ?\n"+parentView.toString() ;
	            int v=JOptionPane.showConfirmDialog(mainFrame, errMsg, "Confirm Deleting", JOptionPane.YES_NO_OPTION);

	            if (v==JOptionPane.YES_OPTION)
	            {
	            	TermMeta term=parentView.getTerm();
	            	term.setTerm(null);
	            	term.setUnit(null);
	            	term.setOperation(null);
	            	term.setDescription(null);
	            	term.setType(TermType.UNKNOWN);
	            }
	            mainFrame.getMainPanel().selectedTermUpdated();
				break;
				
			default:
				break;
		}
	}

}
