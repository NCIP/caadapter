package gov.nih.nci.cbiit.cdms.formula.gui;

import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import gov.nih.nci.cbiit.cdms.formula.core.BaseMeta;
import gov.nih.nci.cbiit.cdms.formula.FormulaFactory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 13, 2010
 * Time: 9:50:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class FormulaMainPanel extends JPanel implements TreeSelectionListener//, ActionListener,
{
    String formulaName = null;
    String formulaAnnotation = null;
    java.util.List<NodeContentElement> variableDefinitions = new ArrayList<NodeContentElement>();


//    private MenuItem newMenu;
//    private MenuItem exitMenu;
//    private MenuItem closeMenu;
//    private MenuItem calculateMenu;
//    private MenuItem openMenu;
//    private MenuItem saveMenu;
//    private MenuItem saveAsMenu;
    private Frame parentFrame;

    private FormulaButtonPane headButtonPane;
    private JPanel centerPanel;
    private JTextField variableField;
    private JTextArea javaExpressionTextArea;
    private JTextArea xmlTextArea;

    private JButton addVariableButton;
    private BaseMeta controllMeta = null;

    private File currentFile = null;

    FormulaMainPanel(Frame fr)
    {
        parentFrame = fr;
//        FormulaMenuBar frameMenu=new FormulaMenuBar(this);
//
//        parentFrame.setMenuBar(frameMenu);
//        newMenu = frameMenu.getNewMenuItem();
//        newMenu.addActionListener(this);
//        exitMenu = frameMenu.getExitMenuItem();
//        exitMenu.addActionListener(this);
//        closeMenu = frameMenu.getCloseMenuItem();
//        closeMenu.addActionListener(this);
//        calculateMenu = frameMenu.getCalculateMenuItem();
//        calculateMenu.addActionListener(this);
//        openMenu = frameMenu.getOpenMenuItem();
//        openMenu.addActionListener(this);
//        saveMenu = frameMenu.getSaveMenuItem();
//        saveMenu.addActionListener(this);
//        saveAsMenu = frameMenu.getSaveAsMenuItem();
//        saveAsMenu.addActionListener(this);

        initialize();
        centerPanel.setPreferredSize(new Dimension(450, 350));
    }

    private void initialize()
    {
        JPanel northPanel1 = new JPanel(new BorderLayout());
        variableField = new JTextField();
        //addVariableButton = new JButton("Add Variable");
        //northPanel1.add(addVariableButton, BorderLayout.EAST);
        northPanel1.add(variableField, BorderLayout.CENTER);
        northPanel1.add(new JLabel("   Variable(s) "), BorderLayout.WEST);
        //addVariableButton.addActionListener(this);
        variableField.setEditable(false);

        centerPanel = new JPanel(new GridLayout());
        //centerPanel.add(createHeadButtonPane());
        centerPanel.add(new Button("No Formula Invoked..."));

        JPanel upperPanel = new JPanel(new BorderLayout());
        upperPanel.add(northPanel1, BorderLayout.NORTH);
        upperPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel northPanel2 = new JPanel(new BorderLayout());
        javaExpressionTextArea = new JTextArea();
        JScrollPane js1 = new JScrollPane(javaExpressionTextArea);

        javaExpressionTextArea.setEditable(false);
        js1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        //js1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        northPanel2.add(js1, BorderLayout.CENTER);
        northPanel2.add(new JLabel("   Java Expression "), BorderLayout.WEST);


        xmlTextArea = new JTextArea();
        JScrollPane js2 = new JScrollPane(xmlTextArea);
        //js.setHorizontalScrollBar(new JScrollBar());
        xmlTextArea.setEditable(false);
        js2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //js.setVerticalScrollBar(new JScrollBar());
        js2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel lowerPanel = new JPanel(new BorderLayout());
        lowerPanel.add(northPanel2, BorderLayout.NORTH);
        lowerPanel.add(js2, BorderLayout.CENTER);
        //TestFileButtonPanel testButtonPanel = new TestFileButtonPanel(this);
        //lowerPanel.add(testButtonPanel, BorderLayout.SOUTH);
        //this.setLayout(new GridLayout(2, 1));
        //this.add(upperPanel);
        //this.add(lowerPanel);

        JSplitPane centerSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPanel,lowerPanel);
        //centerSplit.add(upperPanel);
        //centerSplit.add(lowerPanel);
        centerSplit.setDividerLocation(0.5);
        this.setLayout(new GridLayout());
        this.add(centerSplit);
    }
//    private JPanel createHeadButtonPane()
//    {
//        return createHeadButtonPane(null);
//    }
    private JPanel createHeadButtonPane(NodeContentElement firstExpression)
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

        if (firstExpression != null)
        {
            headButtonPane.concretePanel(firstExpression);
        }

        return thisPane;
    }
    //public void actionPerformed(ActionEvent e)
    //{
//        if (e.getSource() == addVariableButton)
//        {
//            NewTermWizard wizard = new NewTermWizard(parentFrame, this, NewTermFrontPage.TYPES[4], true);
//            if (!wizard.isCreateTermButtonClicked()) return;
//
//            NodeContentElement ele = new NodeContentElement(NodeContentElement.TYPES[3], wizard.getTermName(), wizard.getFrontPage().getTermDescription());
//            variableDefinitions.add(ele);
//            String tt = "";
//            for (NodeContentElement elem:variableDefinitions)
//            {
//                tt = tt + ", " + elem.getNodeName();
//            }
//            variableField.setText(tt.substring(2));
//        }
//        if (e.getSource() == newMenu)
//        {
//            createNewFormulaPanel();
//        }
//        if (e.getSource() == openMenu)
//        {
//            OpenFormulaAction openAction = new OpenFormulaAction(this);
//            openAction.actionPerformed(e);
//        }
//        if (e.getSource() == saveMenu)
//        {
//            SaveAsFormulaAction saveAction = new SaveAsFormulaAction("Save", this, currentFile);
//            saveAction.actionPerformed(e);
//        }
//        if (e.getSource() == saveAsMenu)
//        {
//            SaveAsFormulaAction saveAction = new SaveAsFormulaAction("Save", this);
//            saveAction.actionPerformed(e);
//        }
//        if (e.getSource() == exitMenu)
//        {
//            parentFrame.dispose();
//        }
//        if (e.getSource() == calculateMenu)
//        {
//            if (headButtonPane == null)
//            {
//                JOptionPane.showMessageDialog(this, "No Formula is Ready.", "No Formula Exist", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//            if (variableDefinitions.size() == 0)
//            {
//                JOptionPane.showMessageDialog(this, "No Variable is Ready.", "No Variable Exist", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//            CalculateWizard wizard = new CalculateWizard(parentFrame, "Input Test Parameter",this, true);
//            wizard.setSize(300, (45 * (variableDefinitions.size() + 1)) + 60);
//            wizard.setVisible(true);
//            //if (!wizard.isCreateTermButtonClicked()) return;
//        }
    //}
    public java.util.List<String> getVariableNames()
    {
        java.util.List<String> list = new ArrayList<String>();

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

        String rr = headButtonPane.generateXML(0);
        if ((rr == null)||(rr.trim().equals(""))) return;
        xmlTextArea.setText(rr);
        FormulaMeta myFormula = null;

        try
        {
            //myFormula=FormulaFactory.loadFormula(new File(FileUtil.saveStringIntoTemporaryFile(rr)));
            myFormula=FormulaFactory.loadFormula(rr);
        }
        catch(JAXBException je)
        {
            System.out.println("====== JAXBException: " + je.getMessage() + "\n" + rr);
            javaExpressionTextArea.setText(headButtonPane.generateJavaExpression());
            je.printStackTrace();
            return;
        }
//        catch(IOException je)
//        {
//            System.out.println("IOException: " + je.getMessage());
//            javaExpressionField.setText(headButtonPane.generateJavaExpression());
//            je.printStackTrace();
//            return;
//        }
        String expr = "";
        try
        {
            expr = myFormula.formatJavaStatement();
            javaExpressionTextArea.setText(expr);
        }
        catch(Exception ee)
        {
            javaExpressionTextArea.setText("ERROR: "+headButtonPane.generateJavaExpression());
        }

    }



    public void addVariableDefinitions(String s,String d)
    {
        boolean alreadyExist = false;
        for (NodeContentElement elem:variableDefinitions)
        {
            String name = elem.getNodeName();
            if (name.equals(s)) alreadyExist = true;
        }

        if (alreadyExist) return;
        NodeContentElement ele = new NodeContentElement(NodeContentElement.TYPES[3], s, d);
        variableDefinitions.add(ele);

    }
    public String getFormulaName()
    {
        return formulaName;
    }
    public String getFormulaaAnnotation()
    {
        return formulaAnnotation;
    }
    public String getFormulaXMLText()
    {
        return xmlTextArea.getText();
    }
    public java.util.List<NodeContentElement> getVariableDefinitions()
    {
        return variableDefinitions;
    }
    public FormulaMeta getFormulaMeta()
    {
        String rr = xmlTextArea.getText();
        if ((rr == null)||(rr.trim().equals(""))) return null;

        FormulaMeta myFormula = null;

        try
        {
            //myFormula=FormulaFactory.loadFormula(new File(FileUtil.saveStringIntoTemporaryFile(rr)));
            myFormula=FormulaFactory.loadFormula(rr);
        }
        catch(JAXBException je)
        {
            System.out.println("====== JAXBException: " + je.getMessage() + "\n" + rr);
            javaExpressionTextArea.setText(headButtonPane.generateJavaExpression());
            je.printStackTrace();
            return null;
        }

        return myFormula;
    }

    public String calculate(HashMap<String, String> params)
    {
        if (!getHeadButtonPane().isReadyToCalculate())
        {
            JOptionPane.showMessageDialog(this, "Calculation is not ready. Check the undecided buttons.", "Not Ready Calculation", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        FormulaMeta myFormula = getFormulaMeta();
        if (myFormula == null)
        {
            JOptionPane.showMessageDialog(this, "Formula is invalid or incomplete", "Invlaid Formula", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return myFormula.getExpression().excute(params);
    }

    public void valueChanged(TreeSelectionEvent arg0)
    {
		// TODO Auto-generated method stub

		JTree slctTree=(JTree)arg0.getSource();
		slctTree.getLastSelectedPathComponent();
		DefaultMutableTreeNode slectTreeNode=(DefaultMutableTreeNode)slctTree.getLastSelectedPathComponent();
		Object slctObj=slectTreeNode.getUserObject();
		if (slctObj instanceof BaseMeta)
		{
            controllMeta=(BaseMeta)slctObj;
            if (controllMeta instanceof FormulaMeta)
            {
                FormulaMeta formula=(FormulaMeta)controllMeta;
                //topLabel.setText(formula.toString());
                //formulaLabel.setText(formula.formatJavaStatement());
//                this.openWithString(FormulaFactory.convertFormulaToXml(formula));
            }
            //controllMeta=(BaseMeta)slctObj;
			//updataDisplayPane();
		}
	}

    public BaseMeta getControllMeta()
    {
        return controllMeta;
    }
}
