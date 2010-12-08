package gov.nih.nci.cbiit.cdms.formula.gui.view;

import gov.nih.nci.cbiit.cdms.formula.core.BaseMeta;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaStatus;
import gov.nih.nci.cbiit.cdms.formula.core.TermType;
import gov.nih.nci.cbiit.cdms.formula.gui.EditTermWizard;
import gov.nih.nci.cbiit.cdms.formula.gui.FrameMain;
import gov.nih.nci.cbiit.cdms.formula.gui.action.EditTermAction;

import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class ViewMouseAdapter extends MouseAdapter {

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		super.mouseClicked(e);
		BaseMeta baseMeta=FrameMain.getSingletonInstance().getMainPanel().getCentralSplit().getControllMeta();
		if (!(baseMeta instanceof FormulaMeta))
			return;

		FormulaMeta formula=(FormulaMeta)baseMeta;
		if (formula.getStatus()!=FormulaStatus.DRAFT)
			return;
		TermUiComponent metaUi=(TermUiComponent)e.getSource();
		if (e.getClickCount()==2)
		{
			System.out.println("ViewMouseAdapter.mouseClicked()..double click:"+metaUi.getViewMeta());
			//do not allow user to edit an expression with double-click
			if(metaUi.getViewMeta().getTerm().getType().equals(TermType.EXPRESSION))
				return;
			
			FrameMain mainFrame=FrameMain.getSingletonInstance();
			EditTermWizard wizard = new EditTermWizard(mainFrame, metaUi.getViewMeta().getTerm(),true);
		    wizard.setLocation(mainFrame.getX()+mainFrame.getWidth()/4,
		    		   mainFrame.getY()+mainFrame.getHeight()/4);
		       wizard.setSize((int)mainFrame.getSize().getWidth()/2,
						(int)mainFrame.getSize().getHeight()/2);
		       wizard.setVisible(true);
		}
		else if (SwingUtilities.isRightMouseButton(e))
		{   
			Container parentC = e.getComponent().getParent();
			while ( !(parentC instanceof JScrollPane))
			{
				parentC=parentC.getParent();
			}
			// Create PopupMenu for the Cell
			JPopupMenu popupMenu =new JPopupMenu();
			EditTermAction editAction=new EditTermAction("Edit Term", EditTermAction.TYPE_EDIT);
			editAction.setTermView(metaUi.getViewMeta());
			JMenuItem editItem=new JMenuItem(editAction);
			popupMenu.add(editItem); 
			
			EditTermAction deleteAction=new EditTermAction("Delete Term", EditTermAction.TYPE_DELETE);
			deleteAction.setTermView(metaUi.getViewMeta());
			JMenuItem deleteItem=new JMenuItem(deleteAction);
			popupMenu.add(deleteItem);
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		
		}
	}
}
