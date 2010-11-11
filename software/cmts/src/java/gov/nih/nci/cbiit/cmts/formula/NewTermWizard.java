package gov.nih.nci.cbiit.cmts.formula;

import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 11, 2010
 * Time: 12:07:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class NewTermWizard extends JDialog implements ActionListener
{
	private static final String CREATE_TERM_COMMAND = "Add Term";

	private static final String CANCEL_COMMAND = "Cancel";
	private boolean createButtonClicked = false;
	private NewTermFrontPage frontPage;

	NewTermWizard parentTerm = null;
    NewFormulaWizard parentFormula = null;
    FormulaMain formulaMain;

    public NewTermWizard(FormulaMain main, NewTermWizard parent, boolean modal)
			throws HeadlessException
	{
		super(parent, "NewTerm:" + parent.getTermValue(), modal);
        formulaMain = main;
        parentTerm = parent;
        initialize();
	}
    public NewTermWizard(FormulaMain main, NewFormulaWizard parent, boolean modal)
			throws HeadlessException
	{
		super(parent, "NewTerm:Formula", modal);
        formulaMain = main;
        parentFormula = parent;
        initialize();
	}
    private void initialize()
	{
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

        if (parentTerm != null) frontPage = new NewTermFrontPage(this, parentTerm.getFrontPage());
        else if (parentFormula != null) frontPage = new NewTermFrontPage(this, parentFormula.getFrontPage());
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
                if (getTermType().equalsIgnoreCase("Expression"))
                {
                    NewTermWizard wizard = new NewTermWizard(formulaMain, this, true);
                    wizard.setSize(350, 300);
                    wizard.setVisible(true);

                    wizard.setLocation((new Double(this.getLocation().getX())).intValue() + 20, (new Double(this.getLocation().getX())).intValue() + 20);
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