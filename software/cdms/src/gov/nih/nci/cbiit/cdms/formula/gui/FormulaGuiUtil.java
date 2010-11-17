package gov.nih.nci.cbiit.cdms.formula.gui;

import javax.swing.JFrame;

public class FormulaGuiUtil {
	private static JFrame mainFrame;
	
	public static JFrame getMainFrame()
	{
 
		return mainFrame;
		
	}
	
	public static void setMainFrame(JFrame frame)
	{
		mainFrame=frame;
	}
 
}
