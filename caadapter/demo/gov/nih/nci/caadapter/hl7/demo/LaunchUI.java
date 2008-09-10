/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.hl7.demo;
import gov.nih.nci.caadapter.ui.main.MainFrame;

import javax.swing.*;

/**
 * A tiny driver which will launch the HL7SDK Swing Mapping Tool.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: linc $
 * @version $Revision: 1.9 $
 * @since caAdapter v1.2
 */
public class LaunchUI {
    private static final String LOGID = "$RCSfile: LaunchUI.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/demo/gov/nih/nci/caadapter/hl7/demo/LaunchUI.java,v 1.9 2008-09-10 18:08:14 linc Exp $";
    private static String codebase = null;

    /**
	 * @return the codebase
	 */
	public static String getCodebase() {
		return codebase;
	}

	/**
	 * @param codebase the codebase to set
	 */
	public static void setCodebase(String codebase) {
		LaunchUI.codebase = codebase;
	}

	public static void main(String[] args)
    {
        try	
            {
        		if(args!=null && args.length>0 && args[0].startsWith("http:"))
        			codebase = args[0];
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
            new MainFrame().launch();
    }
}
