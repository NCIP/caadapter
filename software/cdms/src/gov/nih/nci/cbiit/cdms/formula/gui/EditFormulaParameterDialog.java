/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui;

import gov.nih.nci.cbiit.cdms.formula.core.BaseMeta;
import gov.nih.nci.cbiit.cdms.formula.core.DataElement;
import gov.nih.nci.cbiit.cdms.formula.core.DataElementUsageType;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import gov.nih.nci.cbiit.cdms.formula.gui.action.ExecuteFormulaAction;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class EditFormulaParameterDialog extends JDialog implements ActionListener {
	private DefaultMutableTreeNode formulaNode;
	private FormulaMeta formula;
	private DataElement parameter;
	private int actionType;
	
	private JTextField nameField;
	private JComboBox dataTypeField;
	private JTextField unitField;
	private JTextField descField;
	private JComboBox usageField;
	private JTextField cdeCodeField;
	private JTextField cdeUrlField;
	
	public EditFormulaParameterDialog(JFrame owner, String title, boolean modal)
	{
		super(owner, title,modal);
		initUI();
		initParameter();
		setLocation(owner.getX()+owner.getWidth()/4,
				owner.getY()+owner.getHeight()/4);
		setSize((int)owner.getSize().getWidth()/2,
				(int)owner.getSize().getHeight()/2);
		//set default action type
		actionType=ExecuteFormulaAction.FORMULA_ACTION_ADD_PARAMETER;
	}

	public void setFormulaNode(DefaultMutableTreeNode node) {
		formulaNode = node;
		formula=(FormulaMeta)formulaNode.getUserObject();
	}
 
	public void setParameter(DataElement parameter) {
		this.parameter = parameter;
		initParameter();
	}

	public int getActionType() {
		return actionType;
	}

	public void setActionType(int actionType) {
		this.actionType = actionType;
	}

	private void initParameter()
	{
		if (parameter==null)
			return;
		nameField.setText(parameter.getName());
		dataTypeField.setSelectedItem(parameter.getDataType());
		descField.setText(parameter.getDescription());
		unitField.setText(parameter.getUnit());
		usageField.setSelectedItem(parameter.getUsage());
		cdeCodeField.setText(parameter.getCdeId());
		cdeUrlField.setText(parameter.getCdeReference());
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
        JButton okButton = new JButton("Submit");
        okButton.addActionListener(this);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        if (baseMeta instanceof FormulaMeta)
			formula=(FormulaMeta)baseMeta;

        JPanel centerPanel = new JPanel();
        initParameterPanel(centerPanel);
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        pack();
	}

	private void initParameterPanel(JPanel centerPanel)
	{
         centerPanel.setLayout(new GridBagLayout());
        Insets insets = new Insets(5, 20, 5, 50);
        int i=0;

        centerPanel.add(new JLabel("Name:"), new GridBagConstraints(0, i, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        nameField=new JTextField();
        centerPanel.add(nameField, new GridBagConstraints(1, i, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        i++;
        
        centerPanel.add(new JLabel("Data Type:"), new GridBagConstraints(0, i, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        dataTypeField=new JComboBox();
        dataTypeField.addItem("number");
        dataTypeField.addItem("string");
        centerPanel.add(dataTypeField, new GridBagConstraints(1, i, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        i++;
        centerPanel.add(new JLabel("Unit:"), new GridBagConstraints(0, i, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        unitField=new JTextField();
        centerPanel.add(unitField, new GridBagConstraints(1, i, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        i++;
        centerPanel.add(new JLabel("Description:"), new GridBagConstraints(0, i, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        descField=new JTextField();
        centerPanel.add(descField, new GridBagConstraints(1, i, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        i++;
        centerPanel.add(new JLabel("Usage:"), new GridBagConstraints(0, i, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        usageField=new JComboBox();
        for (DataElementUsageType usageType:DataElementUsageType.values())
        	usageField.addItem(usageType);
                
        centerPanel.add(usageField, new GridBagConstraints(1, i, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        i++;

        centerPanel.add(new JLabel("CDE Code:"), new GridBagConstraints(0, i, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        cdeCodeField=new JTextField();
        centerPanel.add(cdeCodeField, new GridBagConstraints(1, i, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        i++;
        centerPanel.add(new JLabel("CDE Location:"), new GridBagConstraints(0, i, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        cdeUrlField=new JTextField();
        centerPanel.add(cdeUrlField, new GridBagConstraints(1, i, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        i++;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getActionCommand().equals("Cancel"))
			this.dispose();
		else if (arg0.getActionCommand().equals("Submit"))
		{
        	String errMsg=validateInputFields();
            if(errMsg.equals(""))
            {
            	if (parameter==null)
            		parameter=new DataElement();
            	
            	parameter.setName(nameField.getText());
            	parameter.setDataType((String)dataTypeField.getSelectedItem());
            	parameter.setDescription(descField.getText());
            	parameter.setUnit(unitField.getText());
            	parameter.setUsage((DataElementUsageType)usageField.getSelectedItem());
            	parameter.setCdeId(cdeCodeField.getText());
            	parameter.setCdeReference(cdeUrlField.getText());
            	if (formula.getParameter()==null)
            		formula.setParameter(new ArrayList<DataElement>());
            	if (!formula.getParameter().contains(parameter))
            	{
            		formula.getParameter().add(parameter);
    				DefaultMutableTreeNode newParameterNode=new DefaultMutableTreeNode(parameter);
    				formulaNode.add(newParameterNode);
    				DefaultTreeModel  treeModel=(DefaultTreeModel)FrameMain.getSingletonInstance().getMainPanel().getLocalTree().getModel();
    				treeModel.reload(formulaNode);
            	}	
    			formula.setDateModified(new Date());
            	FrameMain mainFrame=FrameMain.getSingletonInstance();
            	mainFrame.getMainPanel().selectedTermUpdated();
            	this.setVisible(false);
            	dispose();
            }
            else
            {
                JOptionPane.showMessageDialog(this, errMsg, "Invalid Inputs", JOptionPane.ERROR_MESSAGE);
                return;
            }	
        }
		
	}
	
	private String validateInputFields()
    {
    	StringBuffer rtnB=new StringBuffer();
    	if (nameField.getText().isEmpty())
    		rtnB.append("Please input name of the paameter !");
    	else if (getActionType()==ExecuteFormulaAction.FORMULA_ACTION_ADD_PARAMETER&&formula.getParameter()!=null) 
    	{
    		for (DataElement p:formula.getParameter())
    			if (p.getName().equalsIgnoreCase(nameField.getText().trim()))
    				rtnB.append("Invalid parameter name: existing");
    	}	
    	if (dataTypeField.getSelectedItem()==null)
    		rtnB.append("Please input data type of paramter  !");
    	
    	return rtnB.toString();
    }
 
}
