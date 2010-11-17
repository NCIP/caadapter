package gov.nih.nci.cbiit.cdms.formula.gui;

import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import gov.nih.nci.cbiit.cdms.formula.FormulaFactory;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 17, 2010
 * Time: 10:03:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class CalculateFrontPage extends JPanel
{

    private JTextField[] fields;
    private JLabel[] labels;

    java.util.List<NodeContentElement> variableDefinitions = null;

    int lastNum = -1;

    private String wizardTitle;
    /**
     * Creates a new <code>JPanel</code> with a double buffer
     * and a flow layout.
     */
    public CalculateFrontPage(CalculateWizard wizard, java.util.List<NodeContentElement> variableDefinitions)
    {
        wizardTitle=wizard.getTitle();
        this.variableDefinitions = variableDefinitions;
        initialize();
    }

    private void initialize()
    {
        int num = variableDefinitions.size();

        fields = new JTextField[num+1];
        labels = new JLabel[num+1];

        this.setLayout(new BorderLayout());//new FlowLayout(FlowLayout.LEADING));
        JPanel centerPanel = new JPanel(new GridBagLayout());
        Insets insets = new Insets(5, 5, 5, 5);

        for(int i=0;i<(num+1);i++)
        {
            NodeContentElement ele = null;
            String lebelText = "";
            try
            {
                ele = variableDefinitions.get(i);
                lebelText = ele.getNodeName();
            }
            catch(Exception ee)
            {
                lebelText = "Calculate Result  ";
                lastNum = i;
            }
            labels[i] = new JLabel(lebelText);
            centerPanel.add(labels[i], new GridBagConstraints(0, i, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
            fields[i] = new JTextField();
            fields[i].setPreferredSize(new Dimension(350, 25));
            centerPanel.add(fields[i], new GridBagConstraints(1, i, 2, 1, 1.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        }
        fields[lastNum].setEditable(false);
        this.add(centerPanel, BorderLayout.CENTER);
    }

//
//    public String getFormulaName()
//    {
//        return formulaNameField.getText();
//    }
//
//    public String getAnnotation()
//    {
//        return annotationField.getText();
//    }
    public void setCalculatingResult(String str)
    {
        fields[lastNum].setText(str);
    }
    public boolean validateInputFields()
    {
        for(int i=0;i<variableDefinitions.size();i++)
        {
            String tt = fields[i].getText();
            if (tt == null) return false;
            if (tt.trim().equals("")) return false;
            try
            {
                Double.parseDouble(tt);
            }
            catch(NumberFormatException ne)
            {
                return false;
            }
        }
        return true;
    }

    public HashMap<String, String> getParameters()
    {
        if (!validateInputFields()) return null;

        HashMap<String, String> paramHash=new HashMap<String, String>();

        for(int i=0;i<variableDefinitions.size();i++)
        {
            NodeContentElement ele = variableDefinitions.get(i);
            String tt = fields[i].getText();
            paramHash.put(ele.getNodeName(), tt);
        }
        return paramHash;
    }

}



