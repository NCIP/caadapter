package gov.nih.nci.cbiit.cdms.formula.gui.view;

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
		
		if (e.getClickCount()==2)
		{
			System.out.println("ViewMouseAdapter.mouseClicked()..double click:"+e.getSource());
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
			popupMenu.add(deleteItem);
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		
		}
	}

}
