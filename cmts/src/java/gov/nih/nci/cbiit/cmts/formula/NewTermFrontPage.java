package gov.nih.nci.cbiit.cmts.formula;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 11, 2010
 * Time: 12:07:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class NewTermFrontPage extends JPanel
{
    private final String[] TYPES = new String[] {"Expression", "Variable", "Number"};
    private final String[] OPERATIONS = new String[]
           {"Addition",
            "Subtraction",
            "Multiplication",
            "Division",
            "Square Root",
            "Radical",
            "Power",
            "Exponential",
            "Sine",
            "Cosine",
            "Tangent"};
    private final String TYPE_MODE = "Type";
    private final String NAME_MODE = "Name";

    private JLabel destFileLabel1;
    private JLabel destFileLabel2;
    private JLabel destFileLabel3;

    private JComboBox typeComboBox;
    private JTextField nameField;
    private JComboBox operationComboBox;
    private JTextField descriptionField;
    private JTextField valueField;

    private NewTermWizard parentTermWizard;
    private NewTermFrontPage termPage;
    private NewFormulaFrontPage formulaPage;
    private String currentType = "";
    //private String annotation;

    //private String wizardTitle;
    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     */
    public NewTermFrontPage(NewTermWizard wizard, NewTermFrontPage page)
    {
        parentTermWizard=wizard;
        termPage = page;
        formulaPage = null;
        initialize1();
    }
    public NewTermFrontPage(NewTermWizard wizard, NewFormulaFrontPage page)
    {
        parentTermWizard=wizard;
        termPage = null;
        formulaPage = page;
        initialize2();
    }
    private void initialize1()
    {
        initialize("");
    }
    private void initialize2()
    {
        initialize("");
    }
    private void initialize(String name)
    {
        this.getComponents();
        this.setLayout(new BorderLayout());//new FlowLayout(FlowLayout.LEADING));
        JPanel centerPanel = new JPanel(new GridBagLayout());
        Insets insets = new Insets(5, 5, 5, 5);
        JLabel dataFileLabel = new JLabel(TYPE_MODE);
        centerPanel.add(dataFileLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        typeComboBox = new JComboBox();
        for(String typ:TYPES) typeComboBox.addItem(typ);
        if (!currentType.equals("")) typeComboBox.setSelectedItem(currentType);

        typeComboBox.setPreferredSize(new Dimension(350, 25));
        centerPanel.add(typeComboBox, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        //JButton dataFileBrowseButton = new JButton(FORMULA_NAME_MODE);
        //centerPanel.add(dataFileBrowseButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
        //		GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));

        JLabel mapFileLabel = new JLabel(NAME_MODE);
        centerPanel.add(mapFileLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(350, 25));
        centerPanel.add(nameField, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        //JButton mapFileBrowseButton = new JButton(ANNOTATION_MODE);
        //centerPanel.add(mapFileBrowseButton, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
        //		GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
        if ((name != null)&&(!name.trim().equals(""))) nameField.setText(name);
        if (currentType.equals("")) currentType = TYPES[0];


            destFileLabel1 = new JLabel("Operation");
            centerPanel.add(destFileLabel1, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
            operationComboBox = new JComboBox();
            //descriptionField = null;
            //valueField = null;
            for(String typ:OPERATIONS) operationComboBox.addItem(typ);
            operationComboBox.setPreferredSize(new Dimension(350, 25));
            centerPanel.add(operationComboBox, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));


            destFileLabel2 = new JLabel("Description");
            centerPanel.add(destFileLabel2, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
            //operationComboBox = null;
            descriptionField = new JTextField();
            //valueField = null;
            descriptionField.setPreferredSize(new Dimension(350, 25));
            centerPanel.add(descriptionField, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));


            destFileLabel3 = new JLabel("Value");
            centerPanel.add(destFileLabel3, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
            //operationComboBox = null;
            //descriptionField = null;
            valueField = new JTextField();
            valueField.setPreferredSize(new Dimension(350, 25));
            centerPanel.add(valueField, new GridBagConstraints(1, 4, 2, 1, 1.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        //System.out.println("   CCC current=" + currentType);
                    destFileLabel1.setEnabled(true);
                    destFileLabel2.setEnabled(false);
                    destFileLabel3.setEnabled(false);
                    operationComboBox.setEnabled(true);
                    descriptionField.setEnabled(false);
                    valueField.setEnabled(false);




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
                   System.out.println("CCC val1=" + val + ", val2="+ (String) typeComboBox.getSelectedItem() +", current="+ currentType);
                       currentType = val;
                       //initialize(nameField.getText());

                   if (currentType.equals(TYPES[0]))
                    {
                        destFileLabel1.setEnabled(true);
                        destFileLabel2.setEnabled(false);
                        destFileLabel3.setEnabled(false);
                        operationComboBox.setEnabled(true);
                        descriptionField.setEnabled(false);
                        valueField.setEnabled(false);

                    }
                    else if (currentType.equals(TYPES[1]))
                    {
                        destFileLabel1.setEnabled(false);
                        destFileLabel2.setEnabled(true);
                        destFileLabel3.setEnabled(false);
                        operationComboBox.setEnabled(false);
                        descriptionField.setEnabled(true);
                        valueField.setEnabled(false);
                    }
                    else if (currentType.equals(TYPES[2]))
                    {
                        destFileLabel1.setEnabled(false);
                        destFileLabel2.setEnabled(false);
                        destFileLabel3.setEnabled(true);
                        operationComboBox.setEnabled(false);
                        descriptionField.setEnabled(false);
                        valueField.setEnabled(true);
                    }

               }
           }
        );
    }


    public String getTermType()
    {
        return (String) typeComboBox.getSelectedItem();
    }
    public String getTermName()
    {
        return nameField.getText();
    }
    public String getTermValue()
    {
        String res = null;
        if (operationComboBox.isEnabled()) res = (String) operationComboBox.getSelectedItem();
        else if (descriptionField.isEnabled()) res = descriptionField.getText();
        else if (valueField.isEnabled()) res = valueField.getText();
        return res;
    }

    public boolean validateInputFields()
    {
        String name = getTermValue();
        if (name == null) return false;
        if (name.trim().equals("")) return false;
        return true;
    }


}

