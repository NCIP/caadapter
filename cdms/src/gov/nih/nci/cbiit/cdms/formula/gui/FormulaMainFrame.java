package gov.nih.nci.cbiit.cdms.formula.gui;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 13, 2010
 * Time: 9:46:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class FormulaMainFrame extends JFrame //implements ActionListener
{
    FormulaMainFrame()
    {
        FormulaMainPanel m = new FormulaMainPanel(this);
        this.setTitle("Formula Generator");
        this.getContentPane().setLayout(new GridLayout());
        this.getContentPane().add(m, "Center");
        this.setSize(600, 600);
        addWindowListener(new WinCloseExit(this));
        this.setVisible(true);

    }

      class WinCloseExit extends WindowAdapter
	  {
		  FormulaMainFrame tt;

		  WinCloseExit(FormulaMainFrame st)
			  {
				  tt = st;
			  }
		  public void windowClosing(WindowEvent e)
			  {
                  tt.dispose();
              }
          public void windowLostFocus(WindowEvent e)
              {

                  //tt.dispose();
              }
          public void windowStateChange(WindowEvent e)
              {

                  //tt.dispose();
              }
          public void windowDeactivated(WindowEvent e)
            {

            }
          public void windowActivated(WindowEvent e)
            {

            }
          public void windowClosed(WindowEvent e)
            {
               //Log.logInfo(this, "Closed : " + pageSetTag);
            }
          public void windowOpened(WindowEvent e)
            {
               //Log.logInfo(this, "Opened : " + pageSetTag);
            }
        }

    public static void main(String[] args)
    {
        try
		{
			try
			{
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			}
			catch (ClassNotFoundException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (InstantiationException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (IllegalAccessException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (UnsupportedLookAndFeelException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			new FormulaMainFrame();
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			//	        Log.logException(new Object(), t);
		}

    }
}
