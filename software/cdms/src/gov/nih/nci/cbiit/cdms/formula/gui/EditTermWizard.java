/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui;

import gov.nih.nci.cbiit.cdms.formula.core.TermMeta;

import javax.swing.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 13, 2010
 * Time: 6:41:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class EditTermWizard extends JDialog implements ActionListener
{
    private static final String TITLE = "Edit ";
    private static final String CREATE_TERM_COMMAND = "Submit";
    private static final String CANCEL_COMMAND = "Cancel";
    private EditTermFrontPage frontPage;
    private TermMeta metaView;

    public EditTermWizard(Frame frame, TermMeta meta, boolean modal)
            throws HeadlessException
    {
        super(frame, TITLE, modal);
        metaView=meta;
        if (meta!=null)
        	this.setTitle(this.getTitle()+meta.toString());
        initialize();
    }

    private void initialize()
    {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        frontPage = new EditTermFrontPage(metaView);
        contentPane.add(frontPage, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
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
            	frontPage.updateTerm();
            	FrameMain mainFrame=FrameMain.getSingletonInstance();
            	mainFrame.getMainPanel().selectedTermUpdated();
            }
            else
            {
                JOptionPane.showMessageDialog(this, errMsg, "Invalid Inputs", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        setVisible(false);
        dispose();
    }

}
