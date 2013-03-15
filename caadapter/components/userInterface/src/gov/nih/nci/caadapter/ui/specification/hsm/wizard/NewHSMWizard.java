/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.specification.hsm.wizard;

import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Handles the selection of HL7 V3 type and file source.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version     Since caAdapter v1.2
 * revision    $Revision: 1.6 $
 * date        $Date: 2008-06-09 19:54:07 $
 */
public class NewHSMWizard extends JDialog implements ActionListener
{
    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: NewHSMWizard.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/wizard/NewHSMWizard.java,v 1.6 2008-06-09 19:54:07 phadkes Exp $";

    private static final String OK_COMMAND = "OK";
    private static final String CANCEL_COMMAND = "Cancel";
    private boolean okButtonClicked = false;
    private NewHSMFrontPage frontPage;
    private String userSelectedMessageType;

    //private boolean success = false;
    private String errorMessage = "";

    public NewHSMWizard(Frame owner, String title, boolean modal) throws HeadlessException
    {
        super(owner, title, modal);
        initialize();
    }

    public NewHSMWizard(Dialog owner, String title, boolean modal) throws HeadlessException
    {
        super(owner, title, modal);
        initialize();
    }

    private void initialize()
    {
    	setSize(400, 280);
        userSelectedMessageType = null;
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        frontPage = new NewHSMFrontPage(this);

        errorMessage = "";
        if (!frontPage.wasSuccess())
        {
            errorMessage = ".. " + frontPage.getErrorMessage();
            return;
        }
        JScrollPane scrollPane = new JScrollPane(frontPage);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        JPanel btnPane=setButtonPane();
        contentPane.add(btnPane, BorderLayout.SOUTH);
    }
    public String getErrorMessage()
    {
        return errorMessage;
    }
    private JPanel setButtonPane()
    {
    	JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));//.TRAILING));//new BorderLayout());
        JButton okButton = new JButton(OK_COMMAND);
        okButton.setMnemonic('O');
        okButton.addActionListener(this);
        JButton cancelButton = new JButton(CANCEL_COMMAND);
        cancelButton.setMnemonic('C');
        cancelButton.addActionListener(this);

        JPanel tempPanel = new JPanel(new GridLayout(1, 2));
        tempPanel.add(okButton);
        tempPanel.add(cancelButton);
        buttonPanel.add(tempPanel);

    	return buttonPanel;
    }
    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();
        if (OK_COMMAND.equals(command))
        {
            if(!validateUserMessageType())
            {
                okButtonClicked = false;
                return;
            }
            okButtonClicked = true;
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

    private boolean validateUserMessageType()
    {
       String messageMIFFileName = null;
        try
        {
        	messageMIFFileName = frontPage.getUserSelectedMIFFileName();
            if(messageMIFFileName==null)
            {
                JOptionPane.showMessageDialog(this,
                        "Cannot find user selected message type!",
                        "New HL7 v3 Specification Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
            this.userSelectedMessageType = messageMIFFileName;
            return true;
        }
        catch (Exception e)
        {
            DefaultSettings.reportThrowableToLogAndUI(this, e, null, this, false, false);
            return false;
        }
    }

    public boolean isOkButtonClicked()
    {
        return okButtonClicked;
    }

    /**
     * Return the message type user selected. Return null if ok button is not being clicked.
     * @return the message type.
     */
    public String getUserSelectedMessageType()
    {
        if(okButtonClicked)
        {
            //since the code handling OK button and validation method already obtain the value, just return it directly.
            return this.userSelectedMessageType;
        }
        else
        {//return null if ok button is not clicked
            return null;
        }
    }

    protected void setOkButtonClicked(boolean okButtonClicked)
    {
        this.okButtonClicked = okButtonClicked;
    }
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.5  2007/10/03 18:47:03  umkis
 * HISTORY      : Protect from crashing and display a fit message when resouce.zip is absent.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2007/08/30 16:09:21  wangeug
 * HISTORY      : set dialog size
 * HISTORY      :
 * HISTORY      : Revision 1.3  2007/08/30 14:21:19  wangeug
 * HISTORY      : remove "How to add new message button"
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/07/03 20:19:34  wangeug
 * HISTORY      : initila loading hl7 code without "clone"
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:18:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.18  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.17  2006/08/02 00:04:43  umkis
 * HISTORY      : Change node number for Extending caAdapter to include new HL7 message types from 6.1.3 to 6.1.2.3.
 * HISTORY      :
 * HISTORY      : Revision 1.16  2006/01/20 23:22:45  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.15  2006/01/20 21:49:21  chene
 * HISTORY      : Change the button name
 * HISTORY      :
 * HISTORY      : Revision 1.14  2006/01/20 21:47:57  chene
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/20 21:36:51  umkis
 * HISTORY      : add Extending caAdapter to include new HL7 message types (6.1.3)
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/20 17:05:03  jiangsc
 * HISTORY      : Update the wording
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/20 17:02:06  jiangsc
 * HISTORY      : Added support on the "add message type" button.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 23:06:13  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/14 21:37:17  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/10/26 18:12:29  jiangsc
 * HISTORY      : replaced printStackTrace() to Log.logException
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/17 22:32:00  umkis
 * HISTORY      : no message
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/07/29 21:59:59  jiangsc
 * HISTORY      : Enhanced HSMPanel
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/27 13:57:43  jiangsc
 * HISTORY      : Added the first round of HSMPanel.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/07 19:16:23  jiangsc
 * HISTORY      : New Structure
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/06 21:46:13  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/05 16:24:17  jiangsc
 * HISTORY      : Added new dialog to handle HSM tree opening in mapping panel.
 * HISTORY      :
 */
