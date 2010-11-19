package gov.nih.nci.cbiit.cdms.formula.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import gov.nih.nci.cbiit.cdms.formula.gui.action.WindowAdapterMain;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class FrameMain extends JFrame {

	private static FrameMain singletonFrameMain;
	private PanelMainFrame mainPanel;

	public FrameMain()
	{
		super();
		this.setTitle("caAdapter Data Derivation Service");
		setSize(800, 640);
	    addWindowListener(new WindowAdapterMain());
		//centralize the window
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = this.getSize();
		setLocation(screenSize.width / 2 - (frameSize.width / 2), screenSize.height / 2 - (frameSize.height / 2));
		//set menu
		this.setJMenuBar(new MenuBarMain(this));
		mainPanel=new PanelMainFrame(this);
		this.getContentPane().add(mainPanel);
	}

	public PanelMainFrame getMainPanel() {
		return mainPanel;
	}

	public static FrameMain getSingletonInstance()
	{
		if (singletonFrameMain==null)
			singletonFrameMain=new FrameMain();
		return singletonFrameMain;
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
		FrameMain frame=FrameMain.getSingletonInstance();
    	frame.setVisible(true);
    }
}
