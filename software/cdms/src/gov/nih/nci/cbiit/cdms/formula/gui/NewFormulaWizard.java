/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 13, 2010
 * Time: 6:40:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewFormulaWizard extends JDialog implements ActionListener
{
    private static final String CREATE_TERM_COMMAND = "Submit";
    private static final String CANCEL_COMMAND = "Cancel";
    private NewFormulaFrontPage frontPage;


    public NewFormulaFrontPage getFrontPage() {
		return frontPage;
	}


	public NewFormulaWizard(Frame owner, String title, boolean modal) throws HeadlessException
    {
        super(owner, title, modal);
        initialize();
    }


    private void initialize()
    {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        frontPage = new NewFormulaFrontPage(this);
        contentPane.add(frontPage, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));//new BorderLayout());
        JButton okButton = new JButton(CREATE_TERM_COMMAND);
        okButton.addActionListener(this);
        JButton cancelButton = new JButton(CANCEL_COMMAND);
        cancelButton.addActionListener(this);
        JPanel tempPanel = new JPanel(new GridLayout(1, 2));
        tempPanel.add(okButton);
        tempPanel.add(cancelButton);
        buttonPanel.add(tempPanel);
        southPanel.add(buttonPanel, BorderLayout.NORTH);
        contentPane.add(southPanel, BorderLayout.SOUTH);
    }


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();
        if (CREATE_TERM_COMMAND.equals(command))
        {
        	String errMsg=frontPage.validateInputFields();
            if(errMsg.equals(""))
            {
            	frontPage.createNewFormula();
               	FrameMain mainFrame=FrameMain.getSingletonInstance();
            	mainFrame.getMainPanel().selectedTermUpdated();
            }
            else
            {
                JOptionPane.showMessageDialog(this, errMsg, "Null Mandatory data", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        setVisible(false);
        dispose();
    }

}

