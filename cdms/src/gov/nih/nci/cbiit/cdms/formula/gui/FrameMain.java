package gov.nih.nci.cbiit.cdms.formula.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import gov.nih.nci.cbiit.cdms.formula.gui.action.WindowAdapterMain;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class FrameMain extends JFrame {

	public FrameMain()
	{
		super();
		this.setTitle("caAdapter Formula Service");
		setSize(800, 640);
	    addWindowListener(new WindowAdapterMain());
		//centralize the window
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = this.getSize();
		setLocation(screenSize.width / 2 - (frameSize.width / 2), screenSize.height / 2 - (frameSize.height / 2));
		//set menu
		this.setJMenuBar(new MenuBarMain(this));
		
		this.getContentPane().add(new PanelMainFrame());
	}
	
    public static void main(String[] args)
    {
    	try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FrameMain frame=new FrameMain();
    	FormulaGuiUtil.setMainFrame(frame);
    	frame.setVisible(true);
    }
}
