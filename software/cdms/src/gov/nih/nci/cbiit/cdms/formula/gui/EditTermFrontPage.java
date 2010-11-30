package gov.nih.nci.cbiit.cdms.formula.gui;

import gov.nih.nci.cbiit.cdms.formula.FormulaFactory;
import gov.nih.nci.cbiit.cdms.formula.core.TermType;
import gov.nih.nci.cbiit.cdms.formula.core.TermMeta;
import gov.nih.nci.cbiit.cdms.formula.core.OperationType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 13, 2010
 * Time: 6:42:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class EditTermFrontPage extends JPanel implements ActionListener 
{
    public static final String[] TYPES = new String[] {NodeContentElement.TYPES[0], NodeContentElement.TYPES[2], NodeContentElement.TYPES[3], NodeContentElement.TYPES[4], "Vriable Definition"};
    
    private JComboBox typeComboBox;
    private JComboBox operationComboBox;
    private JTextField descriptionField;
    private JTextField valueField;
    private JTextField unitField;
    private JLabel valueLabel;

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

        centerPanel.add(new JLabel("Term Name"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insetsLeft, 0, 0));
        centerPanel.add(new JLabel(metaView.getName()), new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsRight, 0, 0));
        
        centerPanel.add(new JLabel("Term Type"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insetsLeft, 0, 0));
        typeComboBox =new JComboBox();
        for (TermType type:TermType.values())
        	typeComboBox.addItem(type);
        
        centerPanel.add(typeComboBox, new GridBagConstraints(1, 1, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsRight, 0, 0));
      
        centerPanel.add(new JLabel("Operation Type"), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insetsLeft, 0, 0));
        operationComboBox =new JComboBox();
        for (OperationType type:OperationType.values())
        	operationComboBox.addItem(type);
        if (metaView.getOperation()!=null)
        	operationComboBox.setSelectedItem(metaView.getOperation());
        else
        	operationComboBox.setSelectedItem(null);
        centerPanel.add(operationComboBox, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsRight, 0, 0));
      
        valueLabel=new JLabel("Value");
        centerPanel.add(valueLabel, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insetsLeft, 0, 0));
        valueField =new JTextField(metaView.getValue());
        centerPanel.add(valueField, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsRight, 0, 0));
      
        centerPanel.add(new JLabel("Unit"), new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insetsLeft, 0, 0));
        unitField =new JTextField(metaView.getUnit());
        centerPanel.add(unitField, new GridBagConstraints(1, 4, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsRight, 0, 0));
      
        
        centerPanel.add(new JLabel("Description"), new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insetsLeft, 0, 0));
        descriptionField =new JTextField(metaView.getDescription());
        centerPanel.add(descriptionField, new GridBagConstraints(1, 5, 2, 1, 1, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsRight, 0, 0));
        this.add(centerPanel, BorderLayout.CENTER);

        typeComboBox.addActionListener(this);

        //set select item to trigger "selection event"
        if (metaView.getType()!=null)
        	typeComboBox.setSelectedItem(metaView.getType());
        else
        	typeComboBox.setSelectedItem(null);
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
	      		   Double.valueOf(valueField.getText());
	      	   }
	      	   catch (NumberFormatException e)
	      	   {
	      		 rtnB.append( e.getMessage()+" is invalid value for " +val.toString() + " !!");
	      	   }
	      	   break;
	        case VARIABLE:
	      	   if (valueField.getText()==null||valueField.getText().trim().equals("")) 
	     		   rtnB.append("Set value for "+val.toString() +" !!");
	      	   if (valueField.getText().matches("[0-9][a-zA-Z_0-]*"))
	      		 rtnB.append( valueField.getText() + " is an invalid variable name for " +val.toString() + " !!");

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
		metaView.setUnit(unitField.getText());
    	switch (val) 
    	{
	    	case UNKNOWN:
	    		metaView.setOperation(null);
	    		metaView.setValue(null);
	    		metaView.setUnit(null);
	    		break;
	    	case EXPRESSION:
	    		metaView.setOperation((OperationType)operationComboBox.getSelectedItem());
	    		metaView.setValue(null);
	    		TermMeta term=FormulaFactory.createTemplateTerm(metaView.getOperation());
	    		for (TermMeta childTerm:term.getTerm())
	    			metaView.getTerm().add(childTerm);
	    		break;
	    	default:
	    		metaView.setOperation(null);
	    		metaView.setValue(valueField.getText());
	    		break;
    	}
    	
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		JComboBox cb = (JComboBox)e.getSource();

        TermType val = (TermType)cb.getSelectedItem();
  	   	operationComboBox.setVisible(false);
        if (val.equals(TermType.EXPRESSION))
        {
     	   valueField.setEditable(false);
     	   operationComboBox.setVisible(true);
        }
        else if (val.equals(TermType.UNKNOWN))
        {
     	   valueField.setEditable(false);
        }
        else
        {
     	   valueField.setEditable(true);
        }
        
        if (val.equals(TermType.VARIABLE))
        	valueLabel.setText("Variable Name");
        else
        	valueLabel.setText("Value");
	}
}


