/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.hl7message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * This class is the main entry class of message wizard to collect user's inputs.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: linc $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2008-06-26 19:45:51 $
 */
public class OpenHL7MessageWizard extends JDialog implements ActionListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: OpenHL7MessageWizard.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/hl7message/OpenHL7MessageWizard.java,v 1.3 2008-06-26 19:45:51 linc Exp $";

	private static final String OK_COMMAND = "OK";

	private static final String CANCEL_COMMAND = "Cancel";
	private boolean okButtonClicked = false;
	private OpenHL7MessageFrontPage frontPage;

	public static final String TITLE = "Select Data Input and Map Specification...";

	public OpenHL7MessageWizard(Frame owner, String title, boolean modal) throws HeadlessException
	{
		super(owner, title, modal);
		initialize();
	}

	public OpenHL7MessageWizard(Dialog owner, String title, boolean modal)
			throws HeadlessException
	{
		super(owner, title, modal);
		initialize();
	}

    private void initialize()
	{
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		frontPage = new OpenHL7MessageFrontPage(this);
		contentPane.add(frontPage, BorderLayout.CENTER);

		JPanel southPanel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));//new BorderLayout());
		JButton okButton = new JButton(OK_COMMAND);
		okButton.setMnemonic('O');
		okButton.addActionListener(this);
		JButton cancelButton = new JButton(CANCEL_COMMAND);
		cancelButton.setMnemonic('C');
		cancelButton.addActionListener(this);
		JPanel tempPanel = new JPanel(new GridLayout(1, 2));
		tempPanel.add(okButton);
		tempPanel.add(cancelButton);
		buttonPanel.add(tempPanel);//, BorderLayout.EAST);
		southPanel.add(buttonPanel, BorderLayout.NORTH);
		contentPane.add(southPanel, BorderLayout.SOUTH);
		pack();
	}

	public File getMapFile()
	{
		return frontPage.getMapFile();
	}

	public File getDataFile()
	{
		return frontPage.getDataFile();
	}

	public File getDestFile()
	{
		return frontPage.getDestFile();
	}

	public boolean isOkButtonClicked()
	{
		return okButtonClicked;
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();
		if (OK_COMMAND.equals(command))
		{
			if(!frontPage.validateInputFields())
			{
				okButtonClicked = false;
				return;
			}
			else
			{
				okButtonClicked = true;
			}
		}
		else if (CANCEL_COMMAND.equals(command))
		{
			okButtonClicked = false;
		}
		else
		{
			System.err.println("Why am I here with command '" + command + "'?");
		}
		setVisible(false);
		dispose();
	}

//	public static void main(String[] args)
//	{
//		OpenHL7MessageWizard openWizard = new OpenHL7MessageWizard((Frame) null, "test openWizard", true);
//		DefaultSettings.centerWindow(openWizard);
//		openWizard.setVisible(true);
//		if(openWizard.isOkButtonClicked())
//		{
//			Log.logInfo(openWizard, "Data file selected: '" + openWizard.getDataFile().getAbsolutePath());
//			Log.logInfo(openWizard, "Map specification selected: '" + openWizard.getMapFile().getAbsolutePath());
//		}
//	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.2  2008/06/09 19:53:52  phadkes
 * HISTORY      : New license text replaced for all .java files.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/03 19:33:17  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 23:06:14  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/10/17 22:06:39  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/26 14:30:49  jiangsc
 * HISTORY      : Name changed
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/07/26 14:27:38  jiangsc
 * HISTORY      : Name changed
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/07/26 14:19:01  jiangsc
 * HISTORY      : To be deleted
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/22 20:53:17  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
