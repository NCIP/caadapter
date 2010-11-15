package gov.nih.nci.cbiit.cdms.formula.gui;

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
public class NewTermWizard extends JDialog implements ActionListener
{
    private static final String TITLE = "New Term Definition";
    private static final String CREATE_TERM_COMMAND = "Add Term";
    private static final String CANCEL_COMMAND = "Cancel";
    private boolean createButtonClicked = false;
    private NewTermFrontPage frontPage;


    FormulaMainPanel mainPanel = null;
    String type = null;

    public NewTermWizard(Frame frame, FormulaMainPanel mainP, boolean modal)
            throws HeadlessException
    {
        super(frame, TITLE, modal);
        this.type = type;

        mainPanel = mainP;
        initialize();
    }
    public NewTermWizard(Dialog dialog, FormulaMainPanel mainP, boolean modal)
            throws HeadlessException
    {
        super(dialog, TITLE, modal);
        this.type = type;

        mainPanel = mainP;
        initialize();
    }
    public NewTermWizard(Frame frame, FormulaMainPanel mainP, String type, boolean modal)
            throws HeadlessException
    {
        super(frame, TITLE, modal);
        this.type = type;

        mainPanel = mainP;
        initialize();
    }
    public NewTermWizard(Dialog dialog, FormulaMainPanel mainP, String type, boolean modal)
            throws HeadlessException
    {
        super(dialog, TITLE, modal);
        this.type = type;

        mainPanel = mainP;
        initialize();
    }


    private void initialize()
    {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        frontPage = new NewTermFrontPage(mainPanel, this, type);


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
        this.setSize(340,350);
        this.setVisible(true);
    }

    public NodeContentElement getNodeContentElement()
    {
        return frontPage.getNodeContentElement();
    }
    public String getTermType()
    {
        return frontPage.getTermType();
    }

    public String getTermName()
    {
        return frontPage.getTermName();
    }
    public String getTermValue()
    {
        return frontPage.getTermValue();
    }
    public NewTermFrontPage getFrontPage()
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
                JOptionPane.showMessageDialog(this, "Term is null or blank", "Null Term values", JOptionPane.ERROR_MESSAGE);
                createButtonClicked = false;
                return;
            }
            else
            {
                String type = frontPage.getTermType();
                if (type.equals(NodeContentElement.TYPES[4]))
                {
                    String val = frontPage.getTermValue();
                    if (val == null) val = "";
                    else val = val.trim();
                    if (val.equals(""))
                    {
                        JOptionPane.showMessageDialog(this, "Null or Empty Number value", "Null Number value", JOptionPane.ERROR_MESSAGE);
                        createButtonClicked = false;
                        return;
                    }
                    try
                    {
                        Double.parseDouble(val);
                    }
                    catch(NumberFormatException ne)
                    {
                        JOptionPane.showMessageDialog(this, "This is Not a Number : " + val, "NumberFormatException", JOptionPane.ERROR_MESSAGE);
                        createButtonClicked = false;
                        return;
                    }
                }
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
