/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.hl7.demo;
import gov.nih.nci.caadapter.common.util.WebstartUtil;
import gov.nih.nci.caadapter.ui.main.MainFrame;

import javax.swing.*;

/**
 * A tiny driver which will launch the HL7SDK Swing Mapping Tool.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: wangeug $
 * @version $Revision: 1.11 $
 * @since caAdapter v1.2
 */
public class LaunchUI {
    private static final String LOGID = "$RCSfile: LaunchUI.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/demo/gov/nih/nci/caadapter/hl7/demo/LaunchUI.java,v 1.11 2009-03-25 19:56:08 wangeug Exp $";

	public static void main(String[] args)
    {
        try	
            {
        	System.out.println("LaunchUI.main()..args:"+args);
        		if (args!=null && args.length>0 && args[0].startsWith("http:")) {
        			System.out.println("LaunchUI.main()..webstart codebase:"+args[0]);
        			gov.nih.nci.caadapter.ui.help.actions.HelpTopicAction.setCodeBase(args[0]);
        			WebstartUtil.downloadFile("V2_to_V3.vom","workingspace/V2_to_V3.vom");
        			WebstartUtil.setWebstartDeployed(true);
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
