/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui;

import gov.nih.nci.cbiit.cdms.formula.FormulaFactory;
import gov.nih.nci.cbiit.cdms.formula.core.BaseMeta;
import gov.nih.nci.cbiit.cdms.formula.core.DataElement;
import gov.nih.nci.cbiit.cdms.formula.core.DataElementUsageType;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import gov.nih.nci.cbiit.cdms.formula.core.TermType;
import gov.nih.nci.cbiit.cdms.formula.core.TermMeta;
import gov.nih.nci.cbiit.cdms.formula.core.OperationType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 13, 2010
 * Time: 6:42:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class EditTermFrontPage extends JPanel implements ActionListener 
{
    
    private JComboBox typeComboBox;
    private JComboBox operationComboBox;
    private JTextField descriptionField;
    private JTextField valueField;
    private JComboBox variableComboBox;
    private JTextField unitField;
    private JLabel valueLabel;
    private JLabel variableLabel;
    
    private TermMeta metaView;

    public EditTermFrontPage(TermMeta meta)
    {
        metaView=meta;
        initialize();
    }

    private void initialize()
    {
        this.getComponents();
        this.setLayout(new BorderLayout());
        JPanel centerPanel = new JPanel(new GridBagLayout());
        Insets insetsLeft = new Insets(5, 5, 5, 5);
        Insets insetsRight = new Insets(5, 5, 5, 25);
        int idx=0;
        centerPanel.add(new JLabel("Name"), new GridBagConstraints(0, idx, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insetsLeft, 0, 0));
        centerPanel.add(new JLabel(metaView.getName()), new GridBagConstraints(1, idx, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsRight, 0, 0));
        idx++;
        centerPanel.add(new JLabel("Type"), new GridBagConstraints(0, idx, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insetsLeft, 0, 0));
        typeComboBox =new JComboBox();
        for (TermType type:TermType.values())
        	typeComboBox.addItem(type);
        
        centerPanel.add(typeComboBox, new GridBagConstraints(1, idx, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsRight, 0, 0));
        idx++;
        centerPanel.add(new JLabel("Operation Type"), new GridBagConstraints(0, idx, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insetsLeft, 0, 0));
        operationComboBox =new JComboBox();
        for (OperationType type:OperationType.values())
        	operationComboBox.addItem(type);
        if (metaView.getOperation()!=null)
        	operationComboBox.setSelectedItem(metaView.getOperation());
        else
        	operationComboBox.setSelectedItem(null);
        centerPanel.add(operationComboBox, new GridBagConstraints(1, idx, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsRight, 0, 0));
      
        idx++;
 
        valueLabel=new JLabel("Value");
        centerPanel.add(valueLabel, new GridBagConstraints(0, idx, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insetsLeft, 0, 0));
        valueField =new JTextField(metaView.getValue());
        centerPanel.add(valueField, new GridBagConstraints(1, idx, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsRight, 0, 0));
      
        idx++;
        variableLabel=new JLabel("Variable");
        centerPanel.add(variableLabel, new GridBagConstraints(0, idx, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insetsLeft, 0, 0));
        variableComboBox=new JComboBox();
		BaseMeta baseMeta=FrameMain.getSingletonInstance().getMainPanel().getCentralSplit().getControllMeta();
		if (!(baseMeta instanceof FormulaMeta))
			return;

		FormulaMeta formula=(FormulaMeta)baseMeta;
		if (formula.getParameter()==null)
			formula.setParameter(new ArrayList<DataElement>());
		for (DataElement parameter:formula.getParameter())
			if (parameter.getUsage().equals(DataElementUsageType.PARAMETER))
				variableComboBox.addItem(parameter);
		
        centerPanel.add(variableComboBox, new GridBagConstraints(1, idx, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsRight, 0, 0));
      
        idx++;
        centerPanel.add(new JLabel("Unit"), new GridBagConstraints(0, idx, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insetsLeft, 0, 0));
        unitField =new JTextField(metaView.getUnit());
        centerPanel.add(unitField, new GridBagConstraints(1, idx, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsRight, 0, 0));
      
        idx++;
        centerPanel.add(new JLabel("Description"), new GridBagConstraints(0, idx, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insetsLeft, 0, 0));
        descriptionField =new JTextField(metaView.getDescription());
        centerPanel.add(descriptionField, new GridBagConstraints(1, idx, 2, 1, 1, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsRight, 0, 0));
        this.add(centerPanel, BorderLayout.CENTER);

        typeComboBox.addActionListener(this);

        variableComboBox.addActionListener(this);
        operationComboBox.setSelectedItem(metaView.getOperation());
        
        //set select item to trigger "selection event"
        typeComboBox.setSelectedItem(metaView.getType());
        unitField.setText(metaView.getUnit());
        descriptionField.setText(metaView.getDescription());
        if (metaView.getType().equals(TermType.CONSTANT))
        	valueField.setText(metaView.getValue());
        else if (metaView.getType().equals(TermType.VARIABLE))
        {
        	for (int i=0;i<variableComboBox.getItemCount();i++)
        	{
        		DataElement var=(DataElement)variableComboBox.getItemAt(i);
        		if (var.getName().equals(metaView.getValue()))
        		{
        			variableComboBox.setSelectedIndex(i);
        			break;
        		}
        	}
        }
    }

    public String validateInputFields()
    {
    	StringBuffer rtnB=new StringBuffer();
        TermType val = (TermType)typeComboBox.getSelectedItem();
        switch (val) 
        {
	        case EXPRESSION:
	     	   if (operationComboBox.getSelectedItem()==null)
	     		   rtnB.append("Select OperationType for expression !!");
	     	   break;
	        case CONSTANT:
	      	   if (valueField.getText()==null||valueField.getText().trim().equals("")) 
	     		   rtnB.append("Set value for "+val.toString() +" !!");
	      	   try {
	      		   Double.valueOf(((JTextField)valueField).getText());
	      	   }
	      	   catch (NumberFormatException e)
	      	   {
	      		 rtnB.append( e.getMessage()+" is invalid value for " +val.toString() + " !!");
	      	   }
	      	   break;
	        case VARIABLE:
	      	   if (variableComboBox.getSelectedItem()==null) 
	     		   rtnB.append("Set value for "+val.toString() +" !!");
//	      	   if (valueField.getText().matches("[0-9][a-zA-Z_0-]*"))
//	      		 rtnB.append( valueField.getText() + " is an invalid variable name for " +val.toString() + " !!");

	      	   break;
	        default:
	     		
	     		break;
        } 
        return rtnB.toString();
    }

    protected void updateTerm()
    {
    	TermType val = (TermType)typeComboBox.getSelectedItem();
		metaView.setType(val);
		metaView.setDescription(descriptionField.getText());

    	switch (val) 
    	{
	    	case UNKNOWN:
	    		metaView.setOperation(null);
	    		metaView.setValue(null);
	    		metaView.setUnit(null);
	    		if (metaView.getTerm()!=null)
	    			metaView.getTerm().clear();
	    		break;
	    	case EXPRESSION:
	    		OperationType slctOperation=(OperationType)operationComboBox.getSelectedItem();
	    		if (!slctOperation.equals(metaView.getOperation()))
	    		{
	    			metaView.setOperation(slctOperation);
		    		TermMeta newTerm=FormulaFactory.createTemplateTerm(slctOperation);
		    		//reset the term name if it is the top expression of a formula
		    		if (metaView.getName().endsWith("Template"))
		    			metaView.setName(newTerm.getName());
		    		if (metaView.getTerm()==null
		    				||metaView.getTerm().size()==0
		    				||newTerm.getTerm()==null)
		    			metaView.setTerm(newTerm.getTerm());
		    		else if (metaView.getTerm().size()==newTerm.getTerm().size())
		    		{
		    			for (int i=0;i<metaView.getTerm().size();i++)
		    				metaView.getTerm().get(i).setName(newTerm.getTerm().get(i).getName());
    				}
		    		else
		    		{
		    			//either the original term or new term has two child terms
		    			metaView.getTerm().get(0).setName(newTerm.getTerm().get(0).getName());
		    			if (metaView.getTerm().size()==1)
		    				metaView.getTerm().add(newTerm.getTerm().get(1));
		    			else
		    				metaView.getTerm().remove(1);
		    		}
	    		}
	    		metaView.setValue(null);
	    		metaView.setUnit(null);
	    		break;
	    	case VARIABLE:
	    		metaView.setOperation(null);
	    		metaView.setUnit(unitField.getText());
	    		metaView.setValue(((DataElement)variableComboBox.getSelectedItem()).getName());
	    		if (metaView.getTerm()!=null)
	    			metaView.getTerm().clear();
	    		break;
	    	case CONSTANT:
	    		metaView.setOperation(null);
	    		metaView.setUnit(unitField.getText());
	    		metaView.setValue(valueField.getText());
	    		if (metaView.getTerm()!=null)
	    			metaView.getTerm().clear();
	    		break;
	    	default:
//	    		metaView.setOperation(null);
//	    		metaView.setValue(valueField.getText());
//	    		metaView.setUnit(unitField.getText());
//	    		if (metaView.getTerm()!=null)
//	    			metaView.getTerm().clear();
	    		break;
    	}
    	
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		JComboBox cb = (JComboBox)e.getSource();
		Object slctObj=cb.getSelectedItem();
		if (slctObj==null)
			return;
		if (slctObj instanceof TermType)
		{
			unitField.setText("");
			descriptionField.setText("");
			valueField.setText("");
	        TermType val = (TermType)slctObj;
	        if (val.equals(TermType.EXPRESSION))
	        {
	     	       	   
	     	   operationComboBox.setVisible(true);
	     	   operationComboBox.setEnabled(true);
	     	   descriptionField.setEditable(true);
	
	     	   valueField.setEnabled(false);
	    	   variableComboBox.setVisible(false);
	    	   unitField.setEnabled(false);
	        }
	        else if (val.equals(TermType.UNKNOWN))
	        {
	      	   valueField.setEnabled(false);
	     	   variableComboBox.setVisible(false);
	     	   operationComboBox.setEnabled(false);
	     	   unitField.setEnabled(false);
	     	   descriptionField.setEditable(false);
	        }
	        else if (val.equals(TermType.CONSTANT))
	        {
	
	      	   	valueLabel.setVisible(true);
	     	   	valueField.setEnabled(true);
	     	   	valueField.setVisible(true);
	     	    variableLabel.setVisible(false);
	     	    variableComboBox.setEnabled(false);
	     	    variableComboBox.setVisible(false);
	     	    
	        	unitField.setEditable(true);
	     	    unitField.setEnabled(true);
	        	descriptionField.setEditable(true);
	        	
	     	    operationComboBox.setEnabled(false);   	        	    
	        }
	        else 
	        {
	      	   	valueLabel.setVisible(false);
	     	   	valueField.setEnabled(false);
	     	   	valueField.setVisible(false);
	     	    variableLabel.setVisible(true);
	     	    variableComboBox.setEnabled(true);
	     	    variableComboBox.setVisible(true);
	     	    variableComboBox.setSelectedItem(null);
	     	   
	      	   	operationComboBox.setEnabled(false);
	        	unitField.setEditable(false);
	        	descriptionField.setEditable(false);
	        	
	        }
		}
		else if (slctObj instanceof DataElement)
		{
			DataElement slctVar=(DataElement)slctObj;
			unitField.setText(slctVar.getUnit());
			descriptionField.setText(slctVar.getDescription());		
		}
	}
}


