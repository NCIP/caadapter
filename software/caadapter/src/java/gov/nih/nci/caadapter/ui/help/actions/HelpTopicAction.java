/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.help.actions;


import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * The class defines the about action for the whole HL7SDK application.
 *
 * @author OWNER: Ki Sung Um
 * @author LAST UPDATE $Author: wangeug $
 * @since caAdapter v1.2
 * @version    $Revision: 1.9 $
 * @date       $Date: 2008-11-25 15:45:49 $
*/
public class HelpTopicAction extends AbstractContextAction
{
	public static final String COMMAND_NAME = ActionConstants.HELP;
	public static final Character COMMAND_MNEMONIC = new Character('H');
	public static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, false);
	private static final ImageIcon IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("Help16.gif"));
	private static final String TOOL_TIP_DESCRIPTION = ActionConstants.HELP;
	private static String codeBase = null;
    //private static HelpContentViewer helpContentViewer = null;

    private JFrame mainFrame = null;

	/**
	 * @return the codeBase
	 */
	public static final String getCodeBase() {
		return codeBase;
	}

	/**
	 * @param codeBase the codeBase to set
	 */
	public static final void setCodeBase(String codeBase) {
		if(!codeBase.endsWith("/")) codeBase += "/";
		HelpTopicAction.codeBase = codeBase;
	}

	public HelpTopicAction(JFrame mainFrame)
	{
		this(COMMAND_NAME, mainFrame);

    }

	public HelpTopicAction(String name, JFrame mainFrame)
	{
		this(name, IMAGE_ICON, mainFrame);
	}

	public HelpTopicAction(String name, Icon icon, JFrame ownerFrame)
	{
		super(name, icon);
		this.mainFrame = ownerFrame;
		setMnemonic(COMMAND_MNEMONIC);
		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DESKTOP_ACTION_TYPE);
		setShortDescription(TOOL_TIP_DESCRIPTION);

        //do not know how to set the icon location name, or just do not matter.
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e) throws Exception
	{
        try
        {
            ((AbstractMainFrame)mainFrame).showHelpContentViewer();

//        	gov.nih.nci.caadapter.common.BrowserLaunch.openURL("file:///"+System.getProperty("user.dir") + "/docs/help/caAdapter-Help.html");
        	edu.stanford.ejalbert.BrowserLauncher brLauncher = new edu.stanford.ejalbert.BrowserLauncher(null);
//        	System.out.println(System.getProperty("user.dir"));
        	String location = codeBase;
        	if(location==null || location.trim().length()==0)
        		location = "file:///"+System.getProperty("user.dir") + "/docs/help/index.html";
        	else
        		location = location+"help/index.html";
//        		location = location+"caadapter-mms/help/index.html";
        	brLauncher.openURLinBrowser(location);

        }
        catch(NullPointerException ne)
        {
        	ne.printStackTrace();
        }

		setSuccessfullyPerformed(true);
		return isSuccessfullyPerformed();
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return this.mainFrame;
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.8  2008/10/09 20:08:58  linc
 * HISTORY      : updated.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2008/09/26 20:35:27  linc
 * HISTORY      : Updated according to code standard.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2008/09/23 15:43:28  linc
 * HISTORY      : modified method to set codebase.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2008/09/10 19:19:34  linc
 * HISTORY      : updated.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2008/09/10 18:08:14  linc
 * HISTORY      : MMS 4.1 with help enabled.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2008/06/09 19:53:52  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/09/28 06:41:27  wuye
 * HISTORY      : enable help
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.18  2007/01/04 00:31:11  umkis
 * HISTORY      : change back the execution module to HelpContentViewer
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/12/12 22:43:18  wuye
 * HISTORY      : change the index file
 * HISTORY      :
 * HISTORY      : Revision 1.16  2006/12/12 22:41:18  wuye
 * HISTORY      : Updated help
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/08/02 18:44:20  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/01/17 19:37:21  umkis
 * HISTORY      : HelpContentViewer is Locate in mainFrame
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/12 19:50:14  umkis
 * HISTORY      : by asking from UAT team, the help frame can be NOT-disappeared in spite of its lost-focusing.
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/14 21:37:16  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/11/17 21:02:21  umkis
 * HISTORY      : change Icon image
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/10/25 21:11:01  jiangsc
 * HISTORY      : Help Kisung correct the wrong code due to merging in doAction() function
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/20 15:37:18  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/20 13:09:11  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/09/09 13:18:36  umkis
 * HISTORY      : Help Content Viewer
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/09/07 22:27:37  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/04 19:04:35  jiangsc
 * HISTORY      : Minor update
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/02 22:23:09  jiangsc
 * HISTORY      : Newly enhanced context-sensitive menus and toolbar.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/27 22:41:17  jiangsc
 * HISTORY      : Consolidated context sensitive menu implementation.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/22 20:52:56  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
