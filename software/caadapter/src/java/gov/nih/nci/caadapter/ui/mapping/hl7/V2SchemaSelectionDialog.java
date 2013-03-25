/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.mapping.hl7;

import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Jan 21, 2009
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2009-01-23 18:22:00 $
 * @since caAdapter v4.2
 */
public class V2SchemaSelectionDialog extends JDialog implements ActionListener
{
    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: V2SchemaSelectionDialog.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/hl7/V2SchemaSelectionDialog.java,v 1.1 2009-01-23 18:22:00 wangeug Exp $";

    private static final String OK_COMMAND = "OK";
    private static final String CANCEL_COMMAND = "Cancel";
    private boolean okButtonClicked = false;
    private V2SchemaSelectionPanel frontPage;
     private String errorMessage = "";

    public V2SchemaSelectionDialog(Frame owner, String title, boolean modal) throws HeadlessException
    {
        super(owner, title, modal);
        initialize();
        setSize(400, 180);
        setResizable(false);
        setLocation(400, 300);
        setVisible(true);
    }

    private void initialize()
    {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        frontPage = new V2SchemaSelectionPanel(this);
        errorMessage = "";
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
        try
        {
        	String messageSchemaName = frontPage.getUserSelectedMessageSchema();
            String messageVersionName = frontPage.getUserSelectedMessageVersion();
        	if(messageSchemaName==null||messageVersionName==null)
            {
                JOptionPane.showMessageDialog(this,
                        "Cannot find user selected message schema !",
                        "HL7 V2 Schema Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        }
        catch (Exception e)
        {
            DefaultSettings.reportThrowableToLogAndUI(this, e, null, this, false, false);
            return false;
        }
    }

    /**
	 * @return the frontPage
	 */
	public V2SchemaSelectionPanel getFrontPage() {
		return frontPage;
	}

	/**
	 * @param frontPage the frontPage to set
	 */
	public void setFrontPage(V2SchemaSelectionPanel frontPage) {
		this.frontPage = frontPage;
	}

	public boolean isOkButtonClicked()
    {
        return okButtonClicked;
    }

}
/**
* HISTORY: $Log: not supported by cvs2svn $
**/