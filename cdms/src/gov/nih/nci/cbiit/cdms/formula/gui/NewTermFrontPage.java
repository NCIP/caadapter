package gov.nih.nci.cbiit.cdms.formula.gui;

import gov.nih.nci.cbiit.cdms.formula.core.TermType;
import gov.nih.nci.cbiit.cdms.formula.core.TermMeta;
import gov.nih.nci.cbiit.cdms.formula.core.OperationType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 13, 2010
 * Time: 6:42:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewTermFrontPage extends JPanel
{
    public static final String[] TYPES = new String[] {NodeContentElement.TYPES[0], NodeContentElement.TYPES[2], NodeContentElement.TYPES[3], NodeContentElement.TYPES[4], "Vriable Definition"};
    //public static final String[] TYPES = new String[] {"Formula", NodeContentElement.TYPES[2], NodeContentElement.TYPES[3], NodeContentElement.TYPES[4], "Vriable Definition"};
    //public static String[] TYPES = new String[] {"Formula", "Term" ,"Expression", "Variable", "Number"};
    private final String TYPE_MODE = "Type";
    private final String NAME_MODE = "Name";

    private JLabel destLabelName;
    private JLabel destLabelVariable;
    private JLabel destLabelOperation;
    private JLabel destLabelDescription;
    private JLabel destLabelValue;

    private JComboBox typeComboBox;
    private JTextField nameField;
    private JComboBox variableComboBox;
    private JComboBox operationComboBox;
    private JTextField descriptionField;
    private JTextField valueField;

    private NewTermWizard parentTermWizard;

    private String currentType = "";
    private String givenType = null;
    FormulaMainPanel mainPanel = null;
    //private String annotation;

    //private String wizardTitle;
    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     */
    public NewTermFrontPage(FormulaMainPanel mainPanel, NewTermWizard wizard)
    {
        parentTermWizard=wizard;
        this.mainPanel = mainPanel;
        givenType = null;
        initialize();
    }
    public NewTermFrontPage(FormulaMainPanel mainPanel, NewTermWizard wizard, String type)
    {
        parentTermWizard=wizard;
        this.mainPanel = mainPanel;
        givenType = type;
        initialize();
    }
//    public NewTermFrontPage(NewTermWizard wizard, NewTermFrontPage page)
//    {
//        parentTermWizard=wizard;
//        termPage = page;
//        formulaPage = null;
//        initialize1();
//    }
//    public NewTermFrontPage(NewTermWizard wizard, NewFormulaFrontPage page)
//    {
//        parentTermWizard=wizard;
//        termPage = null;
//        formulaPage = page;
//        initialize2();
//    }

    private void initialize()
    {
        this.getComponents();
        this.setLayout(new BorderLayout());//new FlowLayout(FlowLayout.LEADING));
        JPanel centerPanel = new JPanel(new GridBagLayout());
        Insets insets = new Insets(5, 5, 5, 5);
        JLabel dataFileLabel = new JLabel(TYPE_MODE);
        centerPanel.add(dataFileLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        if (typeComboBox == null)
        {
            typeComboBox = new JComboBox();
            if (givenType == null)
            {
                for(int i=1;i<(TYPES.length-1);i++) typeComboBox.addItem(TYPES[i]);
                typeComboBox.setEditable(true);
                if (currentType.equals("")) currentType = TYPES[1];
            }
            else
            {
                typeComboBox.addItem(givenType);
                typeComboBox.setEditable(false);
                currentType = givenType;
            }
        }
        for(String typ:TYPES) typeComboBox.addItem(typ);
        if (!currentType.equals("")) typeComboBox.setSelectedItem(currentType);

        typeComboBox.setPreferredSize(new Dimension(350, 25));
        centerPanel.add(typeComboBox, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        //JButton dataFileBrowseButton = new JButton(FORMULA_NAME_MODE);
        //centerPanel.add(dataFileBrowseButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
        //		GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));





            destLabelName = new JLabel(NAME_MODE);
            centerPanel.add(destLabelName, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
            nameField = new JTextField();
            nameField.setPreferredSize(new Dimension(350, 25));
            centerPanel.add(nameField, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));

            destLabelVariable = new JLabel("Var Name");
            centerPanel.add(destLabelVariable, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
            variableComboBox = new JComboBox();
            for(String var:mainPanel.getVariableNames()) variableComboBox.addItem(var);
            variableComboBox.setPreferredSize(new Dimension(350, 25));
            centerPanel.add(variableComboBox, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));

            destLabelOperation = new JLabel("Operation");
            centerPanel.add(destLabelOperation, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
            operationComboBox = new JComboBox();
            //descriptionField = null;
            //valueField = null;
            for(String typ:NodeContentElement.OPERATION_NAMES) operationComboBox.addItem(typ);
            operationComboBox.setPreferredSize(new Dimension(350, 25));
            centerPanel.add(operationComboBox, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));

            String lbName = "Description";
            if ((givenType != null)&&(givenType.equals(TYPES[0]))) lbName = "Annotation";
            destLabelDescription = new JLabel(lbName);
            centerPanel.add(destLabelDescription, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
            //operationComboBox = null;
            descriptionField = new JTextField();
            //valueField = null;
            descriptionField.setPreferredSize(new Dimension(350, 25));
            centerPanel.add(descriptionField, new GridBagConstraints(1, 4, 2, 1, 1.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));


            destLabelValue = new JLabel("Value");
            centerPanel.add(destLabelValue, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
            //operationComboBox = null;
            //descriptionField = null;
            valueField = new JTextField();
            valueField.setPreferredSize(new Dimension(350, 25));
            centerPanel.add(valueField, new GridBagConstraints(1, 5, 2, 1, 1.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        //System.out.println("   CCC current=" + currentType);
                    setContentActivations();




        this.add(centerPanel, BorderLayout.CENTER);
        this.
        //NewTermFrontPage thisThing = this;
        //String val = (String) typeComboBox.getSelectedItem();
        typeComboBox.addItemListener
        (
           new ItemListener()
           {
               public void itemStateChanged(ItemEvent e)
               {
                   //String val = (String) typeComboBox.getSelectedItem();
                   String val = (String)e.getItem();

                   if (val == null) return;
                   val = val.trim();
                   if (val.equals(currentType)) return;
                   //System.out.println("CCC val1=" + val + ", val2="+ (String) typeComboBox.getSelectedItem() +", current="+ currentType);
                       currentType = val;
                       //initialize(nameField.getText());

                   setContentActivations();

               }
           }
        );
    }

    private void setContentActivations()
    {
        if (currentType.equals(TYPES[0]))  //Formula
        {
            nameField.setEnabled(true);
            operationComboBox.setEnabled(false);
            descriptionField.setEnabled(true);
            valueField.setEnabled(false);
        }
        if (currentType.equals(TYPES[1]))   //Expression
        {
            nameField.setEnabled(true);
            variableComboBox.setEnabled(false);
            operationComboBox.setEnabled(true);
            descriptionField.setEnabled(true);
            valueField.setEnabled(false);

        }
        else if (currentType.equals(TYPES[2]))  //Variable
        {
            nameField.setEnabled(true);
            variableComboBox.setEnabled(true);
            operationComboBox.setEnabled(false);
            descriptionField.setEnabled(true);
            valueField.setEnabled(false);
        }
        else if (currentType.equals(TYPES[3]))  // Number
        {
            nameField.setEnabled(true);
            variableComboBox.setEnabled(false);
            operationComboBox.setEnabled(false);
            descriptionField.setEnabled(true);
            valueField.setEnabled(true);
        }
        else if (currentType.equals(TYPES[4]))  // Variable definition
        {
            nameField.setEnabled(true);
            variableComboBox.setEnabled(false);
            operationComboBox.setEnabled(false);
            descriptionField.setEnabled(true);
            valueField.setEnabled(true);
        }

        destLabelName.setEnabled(nameField.isEnabled());
        destLabelVariable.setEnabled(variableComboBox.isEnabled());
        destLabelOperation.setEnabled(operationComboBox.isEnabled());
        destLabelDescription.setEnabled(descriptionField.isEnabled());
        destLabelValue.setEnabled(valueField.isEnabled());
    }

    public TermMeta getTermMeta()
    {
        TermMeta term = null;
        if (currentType.equals(TYPES[0]))  //Formula
        {
            return null;
        }
        else if (currentType.equals(TYPES[1]))   //Expression
        {
            term = new TermMeta();
            term.setType(TermType.EXPRESSION);
            term.setName(nameField.getText());
            term.setOperation(OperationType.getOperationTypeWithSymbol((String)operationComboBox.getSelectedItem()));
        }
        else if (currentType.equals(TYPES[2]))  //Variable
        {
            term = new TermMeta();
            term.setType(TermType.VARIABLE);
            term.setName(nameField.getText());
            term.setValue((String)variableComboBox.getSelectedItem());
            term.setDescription(descriptionField.getText());

        }
        else if (currentType.equals(TYPES[3]))  // Number
        {
            term = new TermMeta();
            term.setType(TermType.CONSTANT);
            term.setName(nameField.getText());
            term.setValue(valueField.getText());

        }
        else if (currentType.equals(TYPES[4]))  // Variable definition
        {
            return null;
            //element = new NodeContentElement(currentType, nameField.getText(), valueField.getText());
            //element.setNodeDescription(descriptionField.getText());
        }
        else return null;
        //if (!element.isValid()) return null;
        return term;
    }
    public NodeContentElement getNodeContentElement()
    {
        NodeContentElement element = null;
        if (currentType.equals(TYPES[0]))  //Formula
        {
            element = new NodeContentElement(currentType, nameField.getText(), descriptionField.getText());
        }
        else if (currentType.equals(TYPES[1]))   //Expression
        {
            element = new NodeContentElement(currentType, nameField.getText(), (String)operationComboBox.getSelectedItem());
        }
        else if (currentType.equals(TYPES[2]))  //Variable
        {
            element = new NodeContentElement(currentType, nameField.getText(), descriptionField.getText());
            element.setNodeValue((String)variableComboBox.getSelectedItem());
        }
        else if (currentType.equals(TYPES[3]))  // Number
        {
            element = new NodeContentElement(currentType, nameField.getText(), valueField.getText());
            element.setNodeDescription(descriptionField.getText());
        }
        else if (currentType.equals(TYPES[4]))  // Variable definition
        {
            return null;
            //element = new NodeContentElement(currentType, nameField.getText(), valueField.getText());
            //element.setNodeDescription(descriptionField.getText());
        }
        else return null;
        if (!element.isValid()) return null;
        return element;
    }
    public String getTermType()
    {
        return (String) typeComboBox.getSelectedItem();
    }
    public String getTermName()
    {
        String res = null;
        if (variableComboBox.isEnabled()) res = (String) variableComboBox.getSelectedItem();
        else if (nameField.isEnabled()) res = nameField.getText();
        return res;

    }
    public String getTermDescription()
    {
        return descriptionField.getText();
    }
    public String getTermValue()
    {
        String res = null;
        if (operationComboBox.isEnabled()) res = (String) operationComboBox.getSelectedItem();
        else if (valueField.isEnabled()) res = valueField.getText();
        return res;
    }

    public boolean validateInputFields()
    {
        String name = getTermName();
        if (name == null) return false;
        if (name.trim().equals("")) return false;
        return true;
    }


}


