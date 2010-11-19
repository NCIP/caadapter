package gov.nih.nci.cbiit.cdms.formula.gui;

import javax.swing.*;
import java.awt.*;
import java.util.StringTokenizer;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 13, 2010
 * Time: 6:39:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewFormulaFrontPage extends JPanel
{

    private final String FORMULA_NAME_MODE = "Formula Name";
    private final String ANNOTATION_MODE = "Annotation";

    private JTextField formulaNameField;
    private JComboBox expressionTypeField;
    private JTextField varuableListField;
    private JTextField annotationField;

    //private String formulaName;
    //private String annotation;

    private String wizardTitle;
    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     */
    public NewFormulaFrontPage(NewFormulaWizard wizard)
    {
        wizardTitle=wizard.getTitle();
        initialize();
    }

    private void initialize()
    {
        this.setLayout(new BorderLayout());//new FlowLayout(FlowLayout.LEADING));
        JPanel centerPanel = new JPanel(new GridBagLayout());
        Insets insets = new Insets(5, 5, 5, 5);
        JLabel dataFileLabel = new JLabel(FORMULA_NAME_MODE);
        centerPanel.add(dataFileLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        formulaNameField = new JTextField();
        formulaNameField.setPreferredSize(new Dimension(350, 25));
        centerPanel.add(formulaNameField, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        //JButton dataFileBrowseButton = new JButton(FORMULA_NAME_MODE);
        //centerPanel.add(dataFileBrowseButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
        //		GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));

        JLabel expressionTypeLabel = new JLabel("Expression Type");
        centerPanel.add(expressionTypeLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        expressionTypeField = new JComboBox();
        for(String typ:NodeContentElement.OPERATION_NAMES) expressionTypeField.addItem(typ);
        expressionTypeField.setPreferredSize(new Dimension(350, 25));
        centerPanel.add(expressionTypeField, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));

        JLabel mapFileLabel = new JLabel("Variable List");
        centerPanel.add(mapFileLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        varuableListField = new JTextField();
        varuableListField.setPreferredSize(new Dimension(350, 25));
        centerPanel.add(varuableListField, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));

        JLabel variablesLabel = new JLabel(ANNOTATION_MODE);
        centerPanel.add(variablesLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        annotationField = new JTextField();
        annotationField.setPreferredSize(new Dimension(350, 25));
        centerPanel.add(annotationField, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        //JButton mapFileBrowseButton = new JButton(ANNOTATION_MODE);
        //centerPanel.add(mapFileBrowseButton, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
        //		GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));

        //JLabel destFileLabel = new JLabel(DEST_FILE_BROWSE_MODE);
        //centerPanel.add(destFileLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
        //		GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
        //destFileInputField = new JTextField();
        //destFileInputField.setPreferredSize(new Dimension(350, 25));
        //centerPanel.add(destFileInputField, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0,
        //		GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        //JButton destFileBrowseButton = new JButton(new BrowseMessageAction(this, DEST_FILE_BROWSE_MODE));
        //centerPanel.add(destFileBrowseButton, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
        //		GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
        this.add(centerPanel, BorderLayout.CENTER);
    }


    public String getFormulaName()
    {
        return formulaNameField.getText();
    }

    public String getAnnotation()
    {
        return annotationField.getText();
    }
    public java.util.List<NodeContentElement> getVariableList()
    {
        String line = varuableListField.getText();
        if (line == null) return null;
        if (line.trim().equals("")) return null;
        java.util.List<NodeContentElement> list = new ArrayList<NodeContentElement>();
        StringTokenizer st = new StringTokenizer(line, ",");
        while(st.hasMoreTokens())
        {
            NodeContentElement ele = new NodeContentElement(NodeContentElement.TYPES[3], st.nextToken().trim(), "variable");
            list.add(ele);
        }
        return list;
    }
    public String getFirstExpression()
    {
        return (String) expressionTypeField.getSelectedItem();
    }

    public boolean validateInputFields()
    {
        String name = getFormulaName();
        if (name == null) return false;
        if (name.trim().equals("")) return false;
        String line = varuableListField.getText();
        if (line == null) return false;
        if (line.trim().equals("")) return false;
        return true;
    }


}

