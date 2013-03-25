/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui;

import gov.nih.nci.cbiit.cdms.formula.FormulaFactory;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaStatus;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaStore;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaType;
import gov.nih.nci.cbiit.cdms.formula.core.OperationType;
import gov.nih.nci.cbiit.cdms.formula.core.TermMeta;
import gov.nih.nci.cbiit.cdms.formula.validation.FormulaValidator;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 13, 2010
 * Time: 6:39:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewFormulaFrontPage extends JPanel
{
    private JTextField fStoreNameField;
    private JTextField formulaUnitField;
    private JTextField formulaNameField;
    private JComboBox expressionTypeList;
    private JComboBox formulaStatusList;
    private JTextField annotationField;
    private FormulaMeta formula;

    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     */
    public NewFormulaFrontPage(NewFormulaWizard wizard)
    {
        initialize();
    }

    private void initialize()
    {
        this.setLayout(new BorderLayout());
        JPanel centerPanel = new JPanel(new GridBagLayout());
        Insets insetsLabel= new Insets(5, 5, 5, 5);
        Insets insetsField = new Insets(5, 5, 5, 25);
        
        JLabel formulaStoreLabel=new JLabel ("Formula Store Name");
        fStoreNameField=new JTextField();
        FormulaStore fs=FormulaFactory.getLocalStore();
        if (fs!=null)
        {
        	fStoreNameField.setText(fs.getName());
        	fStoreNameField.setEditable(false);
        }
        
        centerPanel.add(formulaStoreLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insetsLabel, 0, 0));
        centerPanel.add(fStoreNameField, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsField, 0, 0));

        JLabel seperatorLabel=new JLabel ( "     ");
        JLabel seperatorLabelright=new JLabel ( "     ");
        centerPanel.add(seperatorLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insetsLabel, 0, 0));
        centerPanel.add(seperatorLabelright, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsField, 0, 0));

        
        JLabel fNameLabel = new JLabel("Formula Name");
        centerPanel.add(fNameLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insetsLabel, 0, 0));
        formulaNameField = new JTextField();
        centerPanel.add(formulaNameField, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsField, 0, 0));

        JLabel fUnitLabel = new JLabel("Result Uint");
        centerPanel.add(fUnitLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insetsLabel, 0, 0));
        formulaUnitField = new JTextField();
        centerPanel.add(formulaUnitField, new GridBagConstraints(1, 4, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsField, 0, 0));

        
        JLabel expressionTypeLabel = new JLabel("Expression Type",JLabel.RIGHT);
        centerPanel.add(expressionTypeLabel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insetsLabel, 0, 0));
        expressionTypeList = new JComboBox();
        for (OperationType type:OperationType.values())
        	expressionTypeList.addItem(type);
        
        centerPanel.add(expressionTypeList, new GridBagConstraints(1, 5, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsField, 0, 0));

        JLabel formulaStatusLabel = new JLabel("Formula Status");
        centerPanel.add(formulaStatusLabel, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insetsLabel, 0, 0));
        formulaStatusList = new JComboBox();
        	for(FormulaStatus status:FormulaStatus.values())
        		formulaStatusList.addItem(status);
        formulaStatusList.setEditable(false);
        centerPanel.add(formulaStatusList, new GridBagConstraints(1, 6, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsField, 0, 0));

        JLabel variablesLabel = new JLabel("Annotation");
        centerPanel.add(variablesLabel, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insetsLabel, 0, 0));
        annotationField = new JTextField();
        centerPanel.add(annotationField, new GridBagConstraints(1, 7, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insetsField, 0, 0));

        this.add(centerPanel, BorderLayout.CENTER);
    }

	public void eidtFormula(FormulaMeta meta)
	{
		formula=meta;
		annotationField.setText(formula.getAnnotation());
		formulaNameField.setText(formula.getName());
		formulaUnitField.setText(formula.getExpression().getUnit());
		formulaStatusList.setSelectedItem(formula.getStatus());
		expressionTypeList.setSelectedItem(formula.getExpression().getOperation());
	}

    public String validateInputFields()
    {
    	StringBuffer rtnB=new StringBuffer();
    	
    	String fsName = fStoreNameField.getText();
    	if (fsName == null||fsName.trim().equals("")) 
    		rtnB.append("Set name for the new formula store!!\n");
        String name = formulaNameField.getText();
        if (name == null||name.trim().equals("")) 
        	rtnB.append("Set name for the new formula !!\n");
        FormulaStatus newStatus=(FormulaStatus)formulaStatusList.getSelectedItem();
        if (newStatus.equals(FormulaStatus.COMPLETE)
        		||newStatus.equals(FormulaStatus.FINAL))
        {
        	List<String> formulaValidation=FormulaValidator.validateFormula(formula);
        	for (String msg: formulaValidation)
        		rtnB.append(msg+"\n");
        }
        return rtnB.toString();
    }

    public void createNewFormula()
    {
    	if (formula!=null)
    	{
    		formula.setName(formulaNameField.getText());
    		formula.setAnnotation(annotationField.getText());
    		formula.setStatus((FormulaStatus)formulaStatusList.getSelectedItem());
    		if (formula.getExpression().getOperation()!=(OperationType)expressionTypeList.getSelectedItem())
    		{
    			TermMeta newFormulaExpression=FormulaFactory.createTemplateTerm((OperationType)expressionTypeList.getSelectedItem());
    			formula.setExpression(newFormulaExpression);
    		}
    		formula.getExpression().setUnit(formulaUnitField.getText());
    		formula.setDateModified(new Date());
    		return;
    	}
    	FormulaStore fs=FormulaFactory.getLocalStore();
        if (fs==null)
        {	
        	fs=new FormulaStore();
        	fs.setName(fStoreNameField.getText());
        	ArrayList<FormulaMeta> fsFormulas=new ArrayList<FormulaMeta>();
        	fs.setFormula(fsFormulas);
        }
        OperationType type=(OperationType)expressionTypeList.getSelectedItem();
		FormulaMeta newFormula=new FormulaMeta();
		newFormula.setName(formulaNameField.getText());
		newFormula.setType(FormulaType.MATH);
		newFormula.setAnnotation(annotationField.getText());
		newFormula.setStatus((FormulaStatus)formulaStatusList.getSelectedItem());
		newFormula.setDateModified(new Date());
		//the following create formula expression
		TermMeta formulaExpression=FormulaFactory.createTemplateTerm(type);
		formulaExpression.setName(formulaNameField.getText());
		formulaExpression.setUnit(formulaUnitField.getText());
		newFormula.setExpression(formulaExpression);
		
		fs.getFormula().add(newFormula);
		FormulaFactory.updateLocalStore(fs);

		PanelMainFrame mainPanel= FrameMain.getSingletonInstance().getMainPanel();
  	   	mainPanel.localFormulaStoreUpdated(fs, newFormula);
     }

}

