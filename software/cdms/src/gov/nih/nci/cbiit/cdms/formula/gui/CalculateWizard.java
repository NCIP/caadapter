package gov.nih.nci.cbiit.cdms.formula.gui;

import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 17, 2010
 * Time: 9:57:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class CalculateWizard extends JDialog implements ActionListener
{
    private static final String CALCULATE_COMMAND = "Calculate";

    private static final String CLOSE_COMMAND = "Close";
    private boolean createButtonClicked = false;
    private CalculateFrontPage frontPage;
    FormulaMainPanel mainPanel;
    //FormulaTreeEditor formulaEditor;

    public CalculateWizard(Frame owner, String title, FormulaMainPanel mainP, boolean modal) throws HeadlessException
    {
        super(owner, title, modal);
        mainPanel = mainP;
        initialize();
    }

    public CalculateWizard(Dialog owner, String title, FormulaMainPanel mainP, boolean modal)
            throws HeadlessException
    {
        super(owner, title, modal);
        mainPanel = mainP;
        initialize();
    }

    private void initialize()
    {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        frontPage = new CalculateFrontPage(this, mainPanel.getVariableDefinitions());
        contentPane.add(frontPage, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));//new BorderLayout());
        JButton okButton = new JButton(CALCULATE_COMMAND);
        okButton.setMnemonic('O');
        okButton.addActionListener(this);
        JButton cancelButton = new JButton(CLOSE_COMMAND);
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

    
    public CalculateFrontPage getFrontPage()
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
        if (CALCULATE_COMMAND.equals(command))
        {
            createButtonClicked = false;

            HashMap<String, String> params = frontPage.getParameters();
            if(params == null)
            {
                JOptionPane.showMessageDialog(this, "Invalid or empty parameter data", "Invalid Parameter", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!mainPanel.getHeadButtonPane().isReadyToCalculate())
            {
                JOptionPane.showMessageDialog(this, "Calculation is not ready. Check the undecided buttons.", "Not Ready Calculation", JOptionPane.ERROR_MESSAGE);
                return;
            }
            FormulaMeta myFormula = mainPanel.getFormulaMeta();
            if (myFormula == null)
            {
                JOptionPane.showMessageDialog(this, "Formula is invalid or incomplete", "Invlaid Formula", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String res = myFormula.getExpression().excute(params);
            frontPage.setCalculatingResult(res);
            createButtonClicked = true;
            return;
        }
        else if (CLOSE_COMMAND.equals(command))
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


