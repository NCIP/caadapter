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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;

public class ExecuteFormulaDialog extends JDialog implements ActionListener {
	private FrameMain mainFrame;
	private FormulaMeta formula;
	private JTextField resultField;
	private HashMap <String, JTextField> paramFieldHash;
	public ExecuteFormulaDialog(JFrame owner, String title, boolean modal) 
	{
		super(owner, title,modal);
		mainFrame=(FrameMain)owner;
		initUI();
		setLocation(owner.getX()+owner.getWidth()/4,
				owner.getY()+owner.getHeight()/4);
		setSize((int)owner.getSize().getWidth()/2,
				(int)owner.getSize().getHeight()/2);
		
		
	}
	
	private void initUI()
	{
		PanelMainFrame mainPanel =null;
		for (Component com:mainFrame.getContentPane().getComponents())
		{
			if (com instanceof PanelMainFrame)
			{
				mainPanel=(PanelMainFrame)com;
			}
		}
		
		if (mainPanel==null)
			return;
		SplitCentralPane centerPane=mainPanel.getCentralSplit();
		BaseMeta baseMeta=centerPane.getControllMeta();
		if (baseMeta!=null)
			setTitle(this.getTitle() +":"+baseMeta.getName());

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));//new BorderLayout());
        JButton okButton = new JButton("Run");
        okButton.addActionListener(this);
        JButton cancelButton = new JButton("CANCEL");
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

	private void initParameterPanel(JPanel panel)
	{
        if (formula==null)
        	return;

        paramFieldHash=new HashMap<String, JTextField>();
        panel.setLayout(new GridLayout(3,2));
        int startP=50;
		for (String  pName:formula.getExpression().listParameters())
		{
			JLabel pLabel=new JLabel(pName);
			pLabel.setLocation(50, startP);
			JTextField pField=new JTextField("");
			pField.setPreferredSize(new Dimension(125, 25));
			pField.setLocation(150, startP);
			pField.setAutoscrolls(false);
			panel.add(pLabel);
			panel.add(pField);
			paramFieldHash.put(pName, pField);
			startP=startP+50;
		}
		
		JLabel pLabel=new JLabel("Result:  ");
		pLabel.setLocation(50, startP);
		resultField=new JTextField("      " );
		resultField.setPreferredSize(new Dimension(125, 25));
		resultField.setLocation(150, startP);
		
		panel.add(pLabel);
		panel.add(resultField);
 
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getActionCommand().equals("CANCEL"))
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
