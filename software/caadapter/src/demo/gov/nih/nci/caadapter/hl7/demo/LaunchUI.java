/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.demo;
import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.WebstartUtil;
import gov.nih.nci.caadapter.ui.main.MainFrame;

import javax.swing.*;

/**
 * A tiny driver which will launch the HL7SDK Swing Mapping Tool.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: wangeug $
 * @version $Revision: 1.12 $
 * @since caAdapter v1.2
 */
public class LaunchUI {
    private static final String LOGID = "$RCSfile: LaunchUI.java,v $";
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/demo/gov/nih/nci/caadapter/hl7/demo/LaunchUI.java,v 1.12 2009-09-17 15:01:39 wangeug Exp $";

	public static void main(String[] args)
    {
        try
            {
        	System.out.println("LaunchUI.main()..args.length:"+args.length);
        		if (args!=null && args.length>0 && args[0].startsWith("http:")) {
        			System.out.println("LaunchUI.main()..webstart codebase:"+args[0]);
        			gov.nih.nci.caadapter.ui.help.actions.HelpTopicAction.setCodeBase(args[0]);
        			if (CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_COMPONENT_HL7_TRANSFORMATION_ACTIVATED))
        				WebstartUtil.downloadFile("V2_to_V3.vom","workingspace/V2_to_V3.vom");
        			WebstartUtil.downloadFile("workingspace/sampleModels/SDKEATemplate.xmi","workingspace/sampleModels/SDKEATemplate.xmi");
        			WebstartUtil.downloadFile("workingspace/sampleModels/SDKEATest.xmi","workingspace/sampleModels/SDKEATest.xmi");
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
