package gov.nih.nci.cbiit.cdms.formula.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import gov.nih.nci.cbiit.cdms.formula.gui.action.WindowAdapterMain;

import javax.swing.JFrame;

public class FrameMain extends JFrame {

	public FrameMain()
	{
		super();
		this.setTitle("caAdapter Formula Service");
		setSize(600, 600);
	    addWindowListener(new WindowAdapterMain());
		//centralize the window
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = this.getSize();
		setLocation(screenSize.width / 2 - (frameSize.width / 2), screenSize.height / 2 - (frameSize.height / 2));
		//set menu
		this.setJMenuBar(new MenuBarMain());
		
		this.getContentPane().add(new PanelMainFrame());
	}
	
    public static void main(String[] args)
    {
    	new FrameMain().setVisible(true);
    }
}
