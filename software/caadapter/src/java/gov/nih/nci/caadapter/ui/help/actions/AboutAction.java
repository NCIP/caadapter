/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.help.actions;


import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.ui.common.ActionConstants;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.help.AboutWindow;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * The class defines the about action for the whole HL7SDK application.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: linc $
 * @since caAdapter v1.2
 * @version    $Revision: 1.9 $
 * @date       $Date: 2008-09-26 20:35:27 $
 */
public class AboutAction extends AbstractContextAction
{
	public static final String COMMAND_NAME = ActionConstants.ABOUT;
	public static final Character COMMAND_MNEMONIC = new Character('A');
//	public static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, false);
	private static final ImageIcon IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("About16.gif"));
	private static final String TOOL_TIP_DESCRIPTION = ActionConstants.ABOUT;

	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: AboutAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/help/actions/AboutAction.java,v 1.9 2008-09-26 20:35:27 linc Exp $";

	private JFrame mainFrame = null;

	public AboutAction(JFrame mainFrame)
	{
		this(COMMAND_NAME, mainFrame);
	}

	public AboutAction(String name, JFrame mainFrame)
	{
		this(name, IMAGE_ICON, mainFrame);
	}

	public AboutAction(String name, Icon icon, JFrame mainFrame)
	{
		super(name, icon);
		this.mainFrame = mainFrame;
		setMnemonic(COMMAND_MNEMONIC);
//		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
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
	protected boolean doAction(ActionEvent e)
	{
		 if (false && CaadapterUtil.getAllActivatedComponents().contains(Config.CAADAPTER_CSV_XMI_MENU_ACTIVATED))
        {
			 String aboutTextPath="/license/caAdapter_about.txt";

        	String warningMsg=readMessageFromFile(aboutTextPath).toString();
        	String frmName="About caAdapter TDMS";
			JOptionPane.showMessageDialog(mainFrame, warningMsg, frmName, JOptionPane.DEFAULT_OPTION);
        }
		 else
		 {
			AboutWindow cc = new AboutWindow(mainFrame);
			DefaultSettings.centerWindow(cc);
			cc.setVisible(true);
	        cc.setAlwaysOnTop(true);
		 }
        setSuccessfullyPerformed(true);
		return isSuccessfullyPerformed();
	}

	private StringBuffer readMessageFromFile(String filePath)
	{
		StringBuffer licenseBf=new StringBuffer();
		try {
			InputStream input=this.getClass().getResourceAsStream(filePath);
			InputStreamReader reader=new InputStreamReader(input);
			BufferedReader bfReader=new BufferedReader(reader);
			String lineSt;
			lineSt = bfReader.readLine();
			while (lineSt!=null)
			{
				licenseBf.append(lineSt+"\n");
				lineSt = bfReader.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		return licenseBf;
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return mainFrame;
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.8  2008/06/18 16:08:28  linc
 * HISTORY      : Fixed about windows issues.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2008/06/17 17:33:47  linc
 * HISTORY      : updated with new about screen.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2008/06/09 19:53:52  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2008/01/10 16:44:51  wangeug
 * HISTORY      : update window title
 * HISTORY      :
 * HISTORY      : Revision 1.4  2008/01/03 17:11:01  wangeug
 * HISTORY      : *** empty log message ***
 * HISTORY      :
 * HISTORY      : Revision 1.3  2008/01/03 15:52:04  wangeug
 * HISTORY      : customize the about window based on user's configuration
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/10/10 19:58:17  umkis
 * HISTORY      : Fix bug item #7
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.18  2006/08/02 18:44:20  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.16  2006/01/03 18:26:16  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/12/29 23:06:12  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/12/14 21:37:16  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/11/17 21:02:54  umkis
 * HISTORY      : change Icon image
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/11/14 19:55:51  jiangsc
 * HISTORY      : Implementing UI enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/09/22 02:07:10  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/09/14 19:57:48  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/09/07 22:27:36  umkis
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
