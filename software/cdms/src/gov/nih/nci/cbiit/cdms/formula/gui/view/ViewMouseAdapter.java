package gov.nih.nci.cbiit.cdms.formula.gui.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ViewMouseAdapter extends MouseAdapter {

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		super.mouseClicked(e);
		
		if (e.getClickCount()==2)
		{
			System.out.println("ViewMouseAdapter.mouseClicked()..double click:"+e.getSource());
		}
	}

}
