package gov.nih.nci.cbiit.cdms.formula.gui;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 13, 2010
 * Time: 9:50:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class FormulaMainPanel extends JPanel implements ActionListener
{
    String formulaName = "BSA_Valdo";
    String formulaAnnotation = null;
    java.util.List<NodeContentElement> variableDefinitions = new ArrayList<NodeContentElement>();
    java.util.List<Double> variableValues = new ArrayList<Double>();

    private MenuItem newMenu;
    private MenuItem exitMenu;
    private MenuItem closeMenu;
    private MenuItem calculateMenu;
    private Frame parentFrame;

    private FormulaButtonPane headButtonPane;
    private JPanel centerPanel;
    private JTextField variableField;
    private JTextField javaExpressionField;
    private JTextArea xmlTextArea;

    private JButton addVariableButton;



    FormulaMainPanel(Frame fr)
    {
        parentFrame = fr;
        FormulaMenuBar frameMenu=new FormulaMenuBar(this);

        parentFrame.setMenuBar(frameMenu);
        newMenu = frameMenu.getNewMenuItem();
        newMenu.addActionListener(this);
        exitMenu = frameMenu.getExitMenuItem();
        exitMenu.addActionListener(this);
        closeMenu = frameMenu.getCloseMenuItem();
        closeMenu.addActionListener(this);
        calculateMenu = frameMenu.getCalculateMenuItem();
        calculateMenu.addActionListener(this);

        initialize();
    }

    private void initialize()
    {
        JPanel northPanel1 = new JPanel(new BorderLayout());
        variableField = new JTextField();
        addVariableButton = new JButton("Add Variable");
        northPanel1.add(addVariableButton, BorderLayout.EAST);
        northPanel1.add(variableField, BorderLayout.CENTER);
        northPanel1.add(new JLabel("Variable(s) "), BorderLayout.WEST);
        addVariableButton.addActionListener(this);
        variableField.setEditable(false);

        centerPanel = new JPanel(new GridLayout());
        //centerPanel.add(createHeadButtonPane());
        centerPanel.add(new Button("Not Ready..."));

        JPanel upperPanel = new JPanel(new BorderLayout());
        upperPanel.add(northPanel1, BorderLayout.NORTH);
        upperPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel northPanel2 = new JPanel(new BorderLayout());
        javaExpressionField = new JTextField();
        northPanel2.add(javaExpressionField, BorderLayout.CENTER);
        northPanel2.add(new JLabel("Java Expression "), BorderLayout.WEST);
        javaExpressionField.setEditable(false);

        xmlTextArea = new JTextArea();
        JScrollPane js = new JScrollPane(xmlTextArea);
        js.setHorizontalScrollBar(new JScrollBar());
        xmlTextArea.setEditable(false);
        js.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        js.setVerticalScrollBar(new JScrollBar());
        js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel lowerPanel = new JPanel(new BorderLayout());
        lowerPanel.add(northPanel2, BorderLayout.NORTH);
        lowerPanel.add(js, BorderLayout.CENTER);

        this.setLayout(new GridLayout(2, 1));
        this.add(upperPanel);
        this.add(lowerPanel);
    }

    private JPanel createHeadButtonPane()
    {
        JPanel thisPane = new JPanel();
        thisPane.setLayout(new BorderLayout());
        JButton nameButton = new JButton(formulaName);
        JButton equalButton = new JButton("=");
        headButtonPane = new FormulaButtonPane(this, null, parentFrame);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(headButtonPane, BorderLayout.CENTER);
        panel.add(equalButton, BorderLayout.WEST);
        thisPane.add(nameButton, BorderLayout.WEST);
        thisPane.add(panel, BorderLayout.CENTER);
        return thisPane;
    }
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == addVariableButton)
        {
            NewTermWizard wizard = new NewTermWizard(parentFrame, this, NewTermFrontPage.TYPES[4], true);
            if (!wizard.isCreateTermButtonClicked()) return;

            NodeContentElement ele = new NodeContentElement(NodeContentElement.TYPES[3], wizard.getTermName(), wizard.getFrontPage().getTermDescription());
            variableDefinitions.add(ele);
            String tt = "";
            for (NodeContentElement elem:variableDefinitions)
            {
                tt = tt + ", " + elem.getNodeName();
            }
            variableField.setText(tt.substring(2));
        }
        if (e.getSource() == newMenu)
        {
            if (headButtonPane != null)
            {
                int ans = JOptionPane.showConfirmDialog(this, "A Formula is already exist. Are you sure to discard this?", "Discard Current Formula?", JOptionPane.YES_NO_OPTION);
                if (ans != JOptionPane.YES_OPTION) return;
            }
            if (variableDefinitions.size() == 0)
            {
                JOptionPane.showMessageDialog(this, "Define variable(s) first.", "No Variable", JOptionPane.ERROR_MESSAGE);
                return;
            }
            NewFormulaWizard wizard = new NewFormulaWizard(parentFrame, "Create a NEW Formula", true);
            wizard.setSize(350, 150);
            wizard.setVisible(true);
            wizard.setLocation((new Double(this.getLocation().getX())).intValue() + 20, (new Double(this.getLocation().getX())).intValue() + 20);
            if (!wizard.isCreateTermButtonClicked()) return;
            formulaAnnotation = wizard.getAnnotation();
            formulaName = wizard.getFormulaName();
            //System.out.println("GGGG : " + formulaAnnotation + ", " + formulaName);
            centerPanel.removeAll();
            centerPanel.setLayout(new GridLayout());
            centerPanel.add(createHeadButtonPane());
            centerPanel.updateUI();
        }
    }
    public java.util.List<String> getVariableNames()
    {
        java.util.List<String> list = new ArrayList<String>();
        //list.add("Hight");
        //list.add("Weight");
        for (NodeContentElement elem:variableDefinitions)
        {
            list.add(elem.getNodeName());
        }
        return list;
    }

    public FormulaButtonPane getHeadButtonPane()
    {
        return headButtonPane;
    }
    public void refreshContents()
    {
        javaExpressionField.setText(headButtonPane.generateJavaExpression());
        String rr = headButtonPane.generateXML(0);
        //System.out.println("%%%%%%%%% \n" + rr);
        xmlTextArea.setText(rr);

    }
}
