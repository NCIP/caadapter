/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import gov.nih.nci.cbiit.cdms.formula.common.util.WebstartUtil;
import gov.nih.nci.cbiit.cdms.formula.gui.action.WindowAdapterMain;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class FrameMain extends JFrame {

	private static FrameMain singletonFrameMain;
	private PanelMainFrame mainPanel;

	private FrameMain()
	{
		super();
		this.setTitle("caAdapter Data Transformation Service");
		setSize(800, 640);
	    addWindowListener(new WindowAdapterMain());
		//centralize the window
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = this.getSize();
		setLocation(screenSize.width / 2 - (frameSize.width / 2), screenSize.height / 2 - (frameSize.height / 2));
		//set menu
		this.setJMenuBar(new MenuBarMain(this));
		mainPanel=new PanelMainFrame();//this);

        this.getContentPane().add(mainPanel);

        addWindowListener(new WinCloseExit(this));
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

    class WinCloseExit extends WindowAdapter
	{
		FrameMain tt;


        WinCloseExit(FrameMain st)
  	    {
		    tt = st;
		}
		public void windowClosing(WindowEvent e)
	    {
            tt.dispose();
        }
    }

    public static void main(String[] args)
    {
    	try {
    		if (args!=null && args.length>0 && args[0].startsWith("http:")) {
    			System.out.println("FrameMain.main()...webstart codebase:"+args[0]);
    			for (String arg:args)
    				System.out.println("FrameMain.main()..arg:"+arg);
    			WebstartUtil.downloadFile("workingspace/bodySurfaceArea.xml","samples/bodySurfaceArea.xml");
    		}

    		System.out.println("FrameMain.main()..args length:"+args.length);
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
