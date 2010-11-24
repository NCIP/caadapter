package gov.nih.nci.cbiit.cdms.formula.gui.view;

import gov.nih.nci.cbiit.cdms.formula.core.BaseMeta;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaStatus;
import gov.nih.nci.cbiit.cdms.formula.gui.EditTermWizard;
import gov.nih.nci.cbiit.cdms.formula.gui.FrameMain;

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
		if (e.getClickCount()==2)
		{
			if (formula.getStatus()!=FormulaStatus.DRAFT)
				return;
			TermUiComponent metaUi=(TermUiComponent)e.getSource();
			System.out.println("ViewMouseAdapter.mouseClicked()..double click:"+metaUi.getViewMeta());
			FrameMain mainFrame=FrameMain.getSingletonInstance();
			EditTermWizard wizard = new EditTermWizard(mainFrame, metaUi.getViewMeta(), "givenType", true);
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
			JMenuItem editItem=new JMenuItem("Edit");
			editItem.setEnabled(false);
			popupMenu.add(editItem); 
			JMenuItem deleteItem=new JMenuItem("Delete");
			deleteItem.setEnabled(false);
			
			if (formula.getStatus()==FormulaStatus.DRAFT)
			{
					editItem.setEnabled(true);
					deleteItem.setEnabled(true);
			}
			popupMenu.add(deleteItem);
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		
		}
	}
}
