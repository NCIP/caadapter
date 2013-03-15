/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.demo;
import gov.nih.nci.caadapter.ui.main.MainFrame;

import javax.swing.*;

/**
 * A tiny driver which will launch the HL7SDK Swing Mapping Tool.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: linc $
 * @version $Revision: 1.10 $
 * @since caAdapter v1.2
 */
public class LaunchUI {
    private static final String LOGID = "$RCSfile: LaunchUI.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/demo/gov/nih/nci/caadapter/hl7/demo/LaunchUI.java,v 1.10 2008-09-23 15:43:28 linc Exp $";

	public static void main(String[] args)
    {
        try
            {
        		if (args!=null && args.length>0 && args[0].startsWith("http:")) {
        			gov.nih.nci.caadapter.ui.help.actions.HelpTopicAction.setCodeBase(args[0]);
        		}
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
