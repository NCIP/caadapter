package gov.nih.nci.cbiit.cdms.formula.gui;

import gov.nih.nci.cbiit.cdms.formula.core.BaseMeta;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;

public class ExecuteFormulaDialog extends JDialog implements ActionListener {
//	private FrameMain mainFrame;
	private FormulaMeta formula;
	private JTextField resultField;
	private HashMap <String, JTextField> paramFieldHash;
	public ExecuteFormulaDialog(JFrame owner, String title, boolean modal)
	{
		super(owner, title,modal);
//		mainFrame=(FrameMain)owner;
		initUI();
		setLocation(owner.getX()+owner.getWidth()/4,
				owner.getY()+owner.getHeight()/4);
		setSize((int)owner.getSize().getWidth()/2,
				(int)owner.getSize().getHeight()/2);


	}

	private void initUI()
	{
		PanelMainFrame mainPanel =FrameMain.getSingletonInstance().getMainPanel();

		if (mainPanel==null)
			return;
		SplitCentralPane centerPane=mainPanel.getCentralSplit();
        BaseMeta baseMeta=centerPane.getControllMeta();
		if (baseMeta!=null)
			setTitle(this.getTitle() +":"+baseMeta.getName());

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        JButton okButton = new JButton("Run");
        okButton.addActionListener(this);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        if (baseMeta instanceof FormulaMeta)
			formula=(FormulaMeta)baseMeta;

        JPanel centerPanel = new JPanel();
        if (formula==null)
        	centerPanel.add(new JLabel ("Please select your formula !"));
        initParameterPanel(centerPanel);
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        pack();
	}

	private void initParameterPanel(JPanel centerPanel)
	{
        if (formula==null)
        	return;

        paramFieldHash=new HashMap<String, JTextField>();

        centerPanel.setLayout(new GridBagLayout());
        Insets insets = new Insets(5, 25, 25, 50);
        int i=0;
		JLabel tLabel=new JLabel("Parameter");
		JLabel inputLabel=new JLabel("User Input");


        centerPanel.add(tLabel, new GridBagConstraints(0, i, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        centerPanel.add(inputLabel, new GridBagConstraints(1, i, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        i++;
  		for (String  pName:formula.getExpression().listParameters())
		{
			JLabel pLabel=new JLabel(pName +":");
			JTextField pField=new JTextField("");
			paramFieldHash.put(pName, pField);

            centerPanel.add(pLabel, new GridBagConstraints(0, i, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
            centerPanel.add(pField, new GridBagConstraints(1, i, 2, 1, 1.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
            i++;
		}

		JLabel pLabel=new JLabel(formula.getName()+formula.getExpression().getDescription() +":");
		resultField=new JTextField("      " );
		resultField.setEditable(false);
        centerPanel.add(pLabel, new GridBagConstraints(0, i, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        centerPanel.add(resultField, new GridBagConstraints(1, i, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getActionCommand().equals("Cancel"))
			this.dispose();
		else if (arg0.getActionCommand().equals("Run"))
		{
			System.out.println("ExecuteFormulaDialog.actionPerformed()... execute:"+formula);
			HashMap<String, String> paramHash=new HashMap<String, String>();
			Iterator<String> pKey=paramFieldHash.keySet().iterator();
			while (pKey.hasNext())
			{
				String pName=pKey.next();
				JTextField pField= (JTextField)paramFieldHash.get(pName);
				paramHash.put(pName, pField.getText());
			}
			System.out.println("ExecuteFormulaDialog.actionPerformed()..parameter:"+paramHash);
			resultField.setText(formula.getExpression().excute(paramHash)+"");
		}
		
	}
}
