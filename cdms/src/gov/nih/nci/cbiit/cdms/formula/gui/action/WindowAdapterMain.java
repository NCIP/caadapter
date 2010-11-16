package gov.nih.nci.cbiit.cdms.formula.gui.action;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowAdapterMain extends WindowAdapter {

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		super.windowClosed(arg0);
		System.exit(-1);
	}

	  public void windowClosing(WindowEvent e)
	  {
         super.windowClosing(e);
         System.exit(-1);
      }
}
