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
    private static final String CREATE_TERM_COMMAND = "Create Formula";

    private static final String CANCEL_COMMAND = "Cancel";
    private boolean createButtonClicked = false;
    private NewFormulaFrontPage frontPage;
    //FormulaMain formulaMain;
    //FormulaTreeEditor formulaEditor;

    public NewFormulaWizard(Frame owner, String title, boolean modal) throws HeadlessException
    {
        super(owner, title, modal);
        //formulaMain = main;
        initialize();
    }

    public NewFormulaWizard(Dialog owner, String title, boolean modal)
            throws HeadlessException
    {
        super(owner, title, modal);
        //formulaMain = main;
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

    public String getFormulaName()
    {
        return frontPage.getFormulaName();
    }

    public String getAnnotation()
    {
        return frontPage.getAnnotation();
    }
    public NewFormulaFrontPage getFrontPage()
    {
        return frontPage;
    }

    public boolean isCreateTermButtonClicked()
    {
        return createButtonClicked;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();
        if (CREATE_TERM_COMMAND.equals(command))
        {
            if(!frontPage.validateInputFields())
            {
                JOptionPane.showMessageDialog(this, "Formula name or variable list is null.", "Null Mandatory data", JOptionPane.ERROR_MESSAGE);

                createButtonClicked = false;
                return;
            }
            else
            {
               createButtonClicked = true;
            }
        }
        else if (CANCEL_COMMAND.equals(command))
        {
            createButtonClicked = false;
        }
        else
        {
            System.err.println("Strange command '" + command + "'?");
        }
        setVisible(false);
        dispose();
    }

}

