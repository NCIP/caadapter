package gov.nih.nci.cbiit.cdms.formula.gui;

import gov.nih.nci.cbiit.cdms.formula.core.TermMeta;
import gov.nih.nci.cbiit.cdms.formula.core.TermType;
import gov.nih.nci.cbiit.cdms.formula.gui.listener.FormulaButtonMouseListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.Border;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: umkis
 * Date: Nov 13, 2010
 * Time: 5:58:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class FormulaButtonPane extends JPanel implements ActionListener
{
    FormulaButtonPane priorTermButtonPane = null;
    java.util.List<FormulaButtonPane> operatorButtonPanes = null;
    java.util.List<FormulaButtonPane> termButtonPanes = null;

    JButton mainButton;

    FormulaButtonPane parent = null;
    FormulaMainPanel rootPanel = null;

    public static int MODE_UNUSED = -1;
    public static int MODE_UNDECIDED = 0;
    public static int MODE_OPERATOR = 1;
    public static int MODE_TERM = 2;

    private int mode = MODE_UNDECIDED;
    private String UNDECIDED_MARK = "--";
    NodeContentElement contentElement = null;
    Frame frame = null;
    Dialog dialog = null;
    //boolean useOperator = true;
    FormulaButtonPane(FormulaMainPanel root, FormulaButtonPane parent)
    {
        this.parent = parent;
        rootPanel = root;
        //useOperator = true;
        initialize();
    }
    FormulaButtonPane(FormulaMainPanel root, FormulaButtonPane parent, Frame frame)
    {
        this.frame = frame;
        this.parent = parent;
        rootPanel = root;
        //useOperator = true;
        initialize();
    }
    FormulaButtonPane(FormulaMainPanel root, FormulaButtonPane parent, NodeContentElement ele)
    {
        this.parent = parent;
        rootPanel = root;
        contentElement = ele;
        //useOperator = true;
        initialize();
    }
//    FormulaButtonPane(FormulaMainPanel root, FormulaButtonPane parent, boolean useOper)
//    {
//        this.parent = parent;
//        rootPanel = root;
//        useOperator = useOper;
//        initialize();
//    }
//    FormulaButtonPane(FormulaMainPanel root, FormulaButtonPane parent, NodeContentElement ele, boolean useOper)
//    {
//        this.parent = parent;
//        rootPanel = root;
//        contentElement = ele;
//        useOperator = useOper;
//        initialize();
//    }
    FormulaButtonPane(FormulaMainPanel root, FormulaButtonPane parent, String case1)
    {
        this.parent = parent;
        rootPanel = root;
        //useOperator = false;
        //contentElement = ele;
        initialize(case1);
    }
    private void initialize(String case1)
    {
        mainButton = new JButton(case1);
        this.setLayout(new GridLayout());
        this.add(mainButton, "Center");
        mode = MODE_UNUSED;
        return;
    }
    private void initialize()
    {
        if ((frame == null)&&(dialog == null))
        {
            if (rootPanel == null)
            {
                JOptionPane.showMessageDialog(this,"Root Panel is null", "Null Root Panel", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Container con = rootPanel;
            while(true)
            {
                if (con == null)
                {
                    JOptionPane.showMessageDialog(this,"Not Fount owner Frame or Dialog", "Not Found Owner", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                //System.out.println("TTTT : " + con.getClass().getCanonicalName());
                if (con instanceof Frame)
                {
                    frame = (Frame) con;
                    break;
                }
                if (con instanceof Dialog)
                {
                    dialog = (Dialog) con;
                    break;
                }
                con = con.getParent();
            }
        }

        this.removeAll();
        String oper = null;
        if (contentElement != null)
        {
            oper = contentElement.getOperator();
        }
        if (oper == null)
        {
            mainButton = new JButton(UNDECIDED_MARK);
            //mainButton.setBackground(new Color(200, 255, 255));

        }
        else
        {
            String operText = oper;
            if (oper.length() > 1) operText = oper + "(";
            mainButton = new JButton(operText);
            //mainButton.setBackground(new Color(240, 225, 200));
            mode = MODE_OPERATOR;
        }

        this.setLayout(new GridLayout());
        this.add(mainButton, "Center");
        mainButton.addActionListener(this);
        mainButton.addMouseListener(new FormulaButtonMouseListener(this));


    }
    public void actionPerformed(ActionEvent e)
    {
        //System.out.println("TTTTT : FF");
        if (e.getSource() == mainButton)
        {
            //System.out.println("TTTTT : FF " + mode);
            if (mode == MODE_OPERATOR)
            {
                //int ans = JOptionPane.showConfirmDialog(this, "Are you sure to insert another operator?", "Insert Operator", JOptionPane.YES_NO_OPTION);
                int ans = JOptionPane.showConfirmDialog(this, "Are you sure to delete this operator?", "Delete Operator", JOptionPane.YES_NO_OPTION);
                                if (ans != JOptionPane.YES_OPTION) return;
                String delMenu = "   D: Delete this Expression\n";
//                if (parent.getOperatorButtonPanes().size()==1)
//                {
//                    delMenu = "   D0: Delete the Prior Term\n" +
//                              "   D1: Delete the Post Term\n" +
//                              "   DA: Delete Both Terms";
//                }
                String mes = "Select a menu among followings:\n" +
                             delMenu +
                             "   1: Add '+' After this Expression\n" +
                             "   2: Add '+' Before this Expression\n" +
                             "   3: Add '-' After this Expression\n" +
                             "   4: Add '-' Before this Expression";
//                             "   5: Add '*' After this Expression\n" +
//                             "   6: Add '*' Before this Expression\n" +
//                             "   7: Add '/' After this Expression\n" +
//                             "   8: Add '/' Before this Expression";
                String opt = "D";
                //opt = JOptionPane.showInputDialog(this, mes, "Select Menu", JOptionPane.QUESTION_MESSAGE);

                if (opt == null) opt = "";
                else opt = opt.trim();

                if (opt.equals("")) return;
                if (mes.toUpperCase().indexOf(opt.toUpperCase() + ":") < 0)
                {
                    JOptionPane.showMessageDialog(this, "Invalid menu : " + opt, "Invalid Menu", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (opt.equalsIgnoreCase("D"))
                {
                    int ans2 = JOptionPane.showConfirmDialog(this, "Really, are you sure to delete this expression?", "Delete Confirm", JOptionPane.YES_NO_OPTION);
                    if (ans2 != JOptionPane.YES_OPTION) return;
                    deleteOneExpression();
                    return;
                }
                String thisOper = this.getOperator();

                if (!((thisOper.equals("+"))||(thisOper.equals("-"))))
                {
                    JOptionPane.showMessageDialog(this, "Add Operartor is only for '+' or '-'. But this is '" + thisOper + "'", "Invalid operator", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (opt.equals("1")) addOneExpression("+", true);
                else if (opt.equals("2")) addOneExpression("+", false);
                else if (opt.equals("3")) addOneExpression("-", true);
                else if (opt.equals("4")) addOneExpression("-", false);
                //else if (opt.equals("5")) addOneExpression("*", true);
                //else if (opt.equals("6")) addOneExpression("*", false);
                //else if (opt.equals("7")) addOneExpression("/", true);
                //else if (opt.equals("8")) addOneExpression("/", false);
                else JOptionPane.showMessageDialog(this, "Invalid Menu Selection : " + opt, "Invalid Menu Selection", JOptionPane.ERROR_MESSAGE);

                return;
            }
            else if (mode == MODE_UNDECIDED)
            {
                //System.out.println("TTTTT : FF1 " + mode);
                NewTermWizard wizard = null;
                String givenType = null;
                if (parent == null) givenType = NodeContentElement.TYPES[2];
                if (frame != null) wizard = new NewTermWizard(frame, rootPanel, givenType, true);
                else if (dialog != null) wizard = new NewTermWizard(dialog, rootPanel, givenType, true);
                if (!wizard.isCreateTermButtonClicked()) return;
                NodeContentElement ele = wizard.getNodeContentElement();
                if (ele == null)
                {
                    JOptionPane.showMessageDialog(this,"There is something wrong", "Something Wrong", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!concretePanel(ele)) return;



                mode = MODE_TERM;
            }
            else if (mode == MODE_TERM)
            {
                int ans = JOptionPane.showConfirmDialog(this, "This button is already defined. Are you sure to Modify?", "Modify Term", JOptionPane.YES_NO_OPTION);
                if (ans != JOptionPane.YES_OPTION) return;
                NewTermWizard wizard = null;
                if (frame != null) wizard = new NewTermWizard(frame, rootPanel, true);
                else if (dialog != null) wizard = new NewTermWizard(dialog, rootPanel, true);
                if (!wizard.isCreateTermButtonClicked()) return;
                NodeContentElement ele = wizard.getNodeContentElement();
                if (ele == null)
                {
                    JOptionPane.showMessageDialog(this,"There is something wrong 1", "Something Wrong", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!concretePanel(ele)) return;

            }
        }
    }
    public boolean concretePanel(NodeContentElement element)
    {

        if (element.getNodeType().equals(NodeContentElement.TYPES[0]))
        {
            JOptionPane.showMessageDialog(this,"Invalid inserting a Formula", "Formula Inserting", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else if (element.getNodeType().equals(NodeContentElement.TYPES[1]))
        {
            JOptionPane.showMessageDialog(this,"Invalid inserting a Term", "Term Inserting", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else if (element.getNodeType().equals(NodeContentElement.TYPES[2]))
        {
            //System.out.println("TTTTT : FF1 " + mode + element.getNodeType());
            if (operatorButtonPanes != null) return true;
            priorTermButtonPane = new FormulaButtonPane(rootPanel, this);

            if (operatorButtonPanes == null) operatorButtonPanes = new ArrayList<FormulaButtonPane>();
            else
            {
                System.out.println("***** Somthing wrong : " + operatorButtonPanes.size() + ", " + element.getNodeName());
            }
            FormulaButtonPane operatorButtonPane = new FormulaButtonPane(rootPanel, this, element);
            operatorButtonPanes.add(operatorButtonPane);

            if (termButtonPanes == null) 
                termButtonPanes = new ArrayList<FormulaButtonPane>();
            FormulaButtonPane termButtonPane = new FormulaButtonPane(rootPanel, this);
            termButtonPanes.add(termButtonPane);
            //this.removeAll();
            //String oper = element.getOperator();
            mode = MODE_TERM;
            refreshPanel();
            //initialConcretPanel(oper);
            contentElement = element;
            mainButton = null;
            //this.updateUI();
            //System.out.println("TTTTT : FF2 : " + element.getOperator());

        }
        else if (element.getNodeType().equals(NodeContentElement.TYPES[3]))
        {
            mainButton.setText("Var:"+element.getNodeValue());
            contentElement = element;
            //mainButton.setBackground(new Color(245, 200, 240));
            mainButton.updateUI();
            rootPanel.refreshContents();
        }
        else if (element.getNodeType().equals(NodeContentElement.TYPES[4]))
        {
            mainButton.setText(element.getNodeValue());
            contentElement = element;
            //mainButton.setBackground(new Color(243, 225, 200));
            mainButton.updateUI();
            rootPanel.refreshContents();
        }
        else
        {
            JOptionPane.showMessageDialog(this,"Invalid Type", "Invalid Type", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    private JPanel getOneExpressionPanel(FormulaButtonPane operPane, FormulaButtonPane termPane)
    {
        String oper = operPane.getOperator();
        if (oper == null) return null;
        JPanel panel = new JPanel(new BorderLayout());

        if (oper.equals("/"))
        {
            panel.add(operPane, BorderLayout.NORTH);
            panel.add(termPane, BorderLayout.CENTER);
            panel.add(new JLabel(" "), BorderLayout.WEST);
            panel.add(new JLabel(" "), BorderLayout.EAST);
        }
        else if ((oper.equals("+"))||(oper.equals("-"))||(oper.equals("*"))||(oper.equals("%")))
        {
            panel.add(operPane, BorderLayout.WEST);
            panel.add(termPane, BorderLayout.CENTER);
            panel.add(new JLabel(" "), BorderLayout.SOUTH);
            panel.add(new JLabel(" "), BorderLayout.NORTH);
        }

        return panel;
    }
    /*
    private void initialConcretPanel(String oper)
    {
        this.removeAll();

        if (oper.length() == 1)
        {
            if (oper.equals("/")) this.setLayout(new GridLayout(2,1));
            else this.setLayout(new GridLayout(1,2));
            JPanel priorPanel = setupOnePanel(oper, priorTermButtonPane);

            this.add(priorPanel);
            JPanel panel = getOneExpressionPanel(operatorButtonPanes.get(0), termButtonPanes.get(0));
            this.add(panel);
        }
    }
    */
    //private JPanel setupOnePanel(FormulaButtonPane onePane)
    //{
    //    return setupOnePanel("", onePane);
    //}
    private JPanel setupOnePanel(String oper, FormulaButtonPane onePane)
    {
        JPanel priorPanel = new JPanel(new BorderLayout());
        priorPanel.add(onePane, BorderLayout.CENTER);
        if (oper.equals("/"))
        {
            priorPanel.add(new JLabel(" "), BorderLayout.WEST);
            priorPanel.add(new JLabel(" "), BorderLayout.EAST);
        }
        else
        {
            priorPanel.add(new JLabel(" "), BorderLayout.SOUTH);
            priorPanel.add(new JLabel(" "), BorderLayout.NORTH);
        }
        return priorPanel;
    }
    //private JPanel setupTwoPanel(FormulaButtonPane opPane, FormulaButtonPane trPane)
    //{
    //    return setupTwoPanel("", opPane, trPane);
    //}
    private JPanel setupTwoPanel(String oper, FormulaButtonPane opPane, FormulaButtonPane trPane)
    {
        JPanel pPanel = new JPanel(new BorderLayout());

        if (oper.length() == 1)
        {
            pPanel.add(setupOnePanel(oper, trPane), BorderLayout.CENTER);
            if (oper.equals("/")) pPanel.add(opPane, BorderLayout.NORTH);
            else pPanel.add(opPane, BorderLayout.WEST);
        }
        else
        {
            if ((oper.equalsIgnoreCase("pow"))||(oper.equalsIgnoreCase("logarithm")))
            {

            }
            else
            {
                pPanel.add(setupOnePanel(oper, trPane), BorderLayout.CENTER);
                pPanel.add(opPane, BorderLayout.WEST);
                pPanel.add(new JButton(")"), BorderLayout.EAST);
            }
        }
        return pPanel;
    }
    private void refreshPanel()
    {
        //System.out.println(==);
        if (priorTermButtonPane == null) return;

        this.removeAll();
        if ((operatorButtonPanes == null)||(operatorButtonPanes.size() == 0))
        {
//            this.setLayout(new GridLayout());
//            this.add(priorTermButtonPane, "Center");

            this.setLayout(new BorderLayout());
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(priorTermButtonPane, BorderLayout.WEST);
            this.add(panel, BorderLayout.CENTER);
        }
        else
        {
            String oper = operatorButtonPanes.get(0).getOperator();
            if (oper.length() == 1)
            {
                if (oper.equals("/"))
                {
                    this.setLayout(new GridLayout((operatorButtonPanes.size() + 1), 1));
                    this.add(setupOnePanel(oper, priorTermButtonPane));
                    for (int i=0;i<operatorButtonPanes.size();i++)
                    {
                        this.add(setupTwoPanel(oper, operatorButtonPanes.get(i), termButtonPanes.get(i)));
                    }
                }
                else
                //else this.setLayout(new GridLayout(1,(operatorButtonPanes.size() + 1)));
                {
                    this.setLayout(new BorderLayout());
                    this.add(setupOnePanel(oper, priorTermButtonPane), BorderLayout.WEST);

                    JPanel[] panels = new JPanel[operatorButtonPanes.size()];
                    for (int i=0;i<operatorButtonPanes.size();i++)
                    {
                        panels[i] = new JPanel(new BorderLayout());
                        panels[i].add(setupTwoPanel(oper, operatorButtonPanes.get(i), termButtonPanes.get(i)), BorderLayout.WEST);
                        if (i == 0) continue;
                        panels[i-1].add(panels[i], BorderLayout.CENTER);
                    }
                    this.add(panels[0], BorderLayout.CENTER);
                }

            }
            else if ((oper.equalsIgnoreCase("pow"))||(oper.equalsIgnoreCase("logarithm")))
            {
//                this.setLayout(new GridLayout(1, 2));
//                JPanel panelL = new JPanel(new BorderLayout());
//                JPanel panelR = new JPanel(new BorderLayout());
//                panelL.add(operatorButtonPanes.get(0), BorderLayout.WEST);
//                panelL.add(setupOnePanel(oper, priorTermButtonPane), BorderLayout.CENTER);
//                panelR.add(new JButton(")"), BorderLayout.EAST);
//                panelR.add(setupOnePanel(oper, termButtonPanes.get(0)), BorderLayout.CENTER);
//
//                this.add(panelL);
//                this.add(panelR);

                this.setLayout(new BorderLayout());

                JPanel panelL = new JPanel(new BorderLayout());
                JPanel panelR = new JPanel(new BorderLayout());
                panelL.add(operatorButtonPanes.get(0), BorderLayout.WEST);
                panelL.add(setupOnePanel(oper, priorTermButtonPane), BorderLayout.CENTER);
                panelR.add(new JButton(")"), BorderLayout.EAST);
                panelR.add(setupOnePanel(oper, termButtonPanes.get(0)), BorderLayout.CENTER);

                this.add(panelL, BorderLayout.WEST);
                JPanel panelL2 = new JPanel(new BorderLayout());
                panelL2.add(panelR, BorderLayout.WEST);
                this.add(panelL2, BorderLayout.CENTER);
            }
            else
            {
//                this.setLayout(new BorderLayout());
//                this.add(operatorButtonPanes.get(0), BorderLayout.WEST);
//                this.add(setupOnePanel(oper, priorTermButtonPane), BorderLayout.CENTER);
//                termButtonPanes.get(0).setUnused(")");
//                this.add(termButtonPanes.get(0), BorderLayout.EAST);
//
                this.setLayout(new BorderLayout());
                JPanel panel1 = new JPanel(new BorderLayout());
                panel1.add(operatorButtonPanes.get(0), BorderLayout.WEST);
                JPanel panel2 = new JPanel(new BorderLayout());
                panel2.add(setupOnePanel(oper, priorTermButtonPane), BorderLayout.WEST);
                panel1.add(panel2, BorderLayout.CENTER);
                termButtonPanes.get(0).setUnused(")");
                JPanel panel3 = new JPanel(new BorderLayout());
                panel3.add(termButtonPanes.get(0), BorderLayout.WEST);
                panel2.add(panel3, BorderLayout.CENTER);
                this.add(panel1, BorderLayout.CENTER);
            }
        }
        TitledBorder border = BorderFactory.createTitledBorder("");
        Border border2 = BorderFactory.createMatteBorder(1,1,1,1,Color.LIGHT_GRAY);
        border.setBorder(border2);
        //Border border = BorderFactory.createMatteBorder(1,1,1,1,Color.LIGHT_GRAY);

        this.setBorder(border);

        this.updateUI();
        rootPanel.refreshContents();
    }
    public void setMode(int i)
    {
        if ((i == MODE_UNDECIDED)||(i == MODE_OPERATOR)||(i == MODE_TERM)) mode = i;
    }
    public int getMode()
    {
        return mode;
    }
    public void setNodeContentElement(NodeContentElement ele)
    {
        contentElement = ele;
    }
    public NodeContentElement getNodeContentElement()
    {
        return contentElement;
    }
    public FormulaButtonPane getParentPane()
    {
        return parent;
    }
    public FormulaMainPanel getRootPanel()
    {
        return rootPanel;
    }
    public String getOperator()
    {
        if (contentElement == null) return null;
        return contentElement.getOperator();
    }
    public boolean isOperator()
    {
        if (getOperator() == null) return false;
        return true;
    }
    public FormulaButtonPane getPriorTermButtonPane()
    {
        return priorTermButtonPane;
    }
    public java.util.List<FormulaButtonPane> getOperatorButtonPanes()
    {
        return operatorButtonPanes;
    }
    public java.util.List<FormulaButtonPane> getTermButtonPanes()
    {
        return termButtonPanes;
    }
    public void setUnused(String text)
    {
        mode = MODE_UNUSED;
        mainButton.setText(text);
    }
    private void deleteOneExpression()
    {
        if (mode != MODE_OPERATOR)
        {
            JOptionPane.showMessageDialog(this, "This command is valid only for operator", "Not operator", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (parent == null)
        {
            JOptionPane.showMessageDialog(this, "Parent is null", "Null Parent", JOptionPane.ERROR_MESSAGE);
            return;
        }
        parent.deleteOneOperaor(this);
    }
    protected void deleteOneOperaor(FormulaButtonPane operP)
    {
        if (operatorButtonPanes.size() == 0)
        {
            JOptionPane.showMessageDialog(this, "No child operation", "No Operator", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idx = -1;
        for(int i=0;i<operatorButtonPanes.size();i++) if (operatorButtonPanes.get(i) == operP) idx = i;
        if (idx < 0)
        {
            JOptionPane.showMessageDialog(this, "Not Found Target Operator", "Not Found Operator", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (operatorButtonPanes.size() == 1)
        {
            contentElement = null;
            mode = MODE_UNDECIDED;
            initialize();
            this.updateUI();
            return;
        }
        operatorButtonPanes.remove(idx);
        termButtonPanes.remove(idx);
        refreshPanel();
    }
    private void addOneExpression(String oper, boolean after)
    {
        if (mode != MODE_OPERATOR)
        {
            JOptionPane.showMessageDialog(this, "This command is valid only for operator", "Not operator", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String thisOper = this.getOperator();
        boolean cTag = true;
        if (!((thisOper.equals("+"))||(thisOper.equals("-")))) cTag = false;
        if (!((oper.equals("+"))||(oper.equals("-")))) cTag = false;
        if (!cTag)
        {
            JOptionPane.showMessageDialog(this, "Add Operartor is only for '+' or '-' : '" + oper + "' at '" + thisOper + "'", "Invalid operator", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (parent == null)
        {
            JOptionPane.showMessageDialog(this, "Parent is null", "Null Parent", JOptionPane.ERROR_MESSAGE);
            return;
        }
        parent.addOneOperaor(this, oper,after);
    }
    protected void addOneOperaor(FormulaButtonPane operP, String oper, boolean after)
    {
        int idx = -1;
        for(int i=0;i<operatorButtonPanes.size();i++) if (operatorButtonPanes.get(i) == operP) idx = i;
        if (idx < 0)
        {
            JOptionPane.showMessageDialog(this, "Not Found Target Operator", "Not Found Operator", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String opt = JOptionPane.showInputDialog(this, "Input Name of this Expression : " + oper, "Input Name", JOptionPane.QUESTION_MESSAGE);
        if (opt == null) opt = "";
        else opt= opt.trim();
        if (opt.equals("")) return;

        NodeContentElement element = new NodeContentElement(NodeContentElement.TYPES[2], opt, oper);

        java.util.List operatorButtonPanesT = new ArrayList<FormulaButtonPane>();
        FormulaButtonPane operatorButtonPane = new FormulaButtonPane(rootPanel, this, element);


        java.util.List termButtonPanesT = new ArrayList<FormulaButtonPane>();
        FormulaButtonPane termButtonPane = new FormulaButtonPane(rootPanel, this);

        for(int i=0;i<operatorButtonPanes.size();i++)
        {
            FormulaButtonPane operatorButtonPaneS = operatorButtonPanes.get(i);
            FormulaButtonPane termButtonPaneS = termButtonPanes.get(i);

            if (i == idx)
            {
                if (after)
                {
                    operatorButtonPanesT.add(operatorButtonPaneS);
                    termButtonPanesT.add(termButtonPaneS);

                    operatorButtonPanesT.add(operatorButtonPane);
                    termButtonPanesT.add(termButtonPane);
                }
                else
                {
                    operatorButtonPanesT.add(operatorButtonPane);
                    termButtonPanesT.add(termButtonPane);

                    operatorButtonPanesT.add(operatorButtonPaneS);
                    termButtonPanesT.add(termButtonPaneS);
                }
            }
            else
            {
                operatorButtonPanesT.add(operatorButtonPaneS);
                termButtonPanesT.add(termButtonPaneS);
            }
        }

        operatorButtonPanes = operatorButtonPanesT;
        termButtonPanes = termButtonPanesT;
        refreshPanel();
    }
//    public String generateJavaExpression()
//    {
//        String xml = rootPanel.getFormulaXMLText();
//    }
    public String generateJavaExpression()
    {
        String cont = "";
        //cont = contentElement.getNodeName();
        if (priorTermButtonPane == null)
        {
            if (contentElement != null)
            {
                if (contentElement.getNodeType().equals(NodeContentElement.TYPES[3])) return contentElement.getNodeName();
                else return contentElement.getNodeValue();
            }
            else if (mainButton != null) return mainButton.getText();
            else System.out.println("GGG no...");
        }
        if ((operatorButtonPanes != null)&&(operatorButtonPanes.size() > 0))
        {
            int n = 0;
            cont = cont + "(" + priorTermButtonPane.generateJavaExpression() + ")";
            for (FormulaButtonPane p:operatorButtonPanes)
            {
                cont = cont + " " + p.getOperator() + " (" + termButtonPanes.get(n).generateJavaExpression() + ")";

                n++;
            }
        }
        return cont;
    }
    public String generateXML(int depth)
    {
        String oneStepSapce = "   ";
        String contn = "";
        String termTag = "term";
        String tail = "";

        if (depth == 0)
        {
            if ((mainButton != null)&&(mainButton.getText().trim().equals("--"))) return "";
            contn = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<formula type=\"math\" name=\""+rootPanel.getFormulaName()+"\">\n" +
                    oneStepSapce + "<annotation>"+rootPanel.getFormulaaAnnotation()+"</annotation>\n";
            termTag = "expression";
            tail = "</formula>\n";
        }
        String space = "";
        for(int i=0;i<(depth+1);i++) space = space + oneStepSapce;

        depth++;

        String type = "";
        String header = "";
        String footer = "";

        if ((contentElement == null)||((mainButton != null)&&(mainButton.getText().trim().equals(UNDECIDED_MARK))))
        {

            type = "unknown";
            header = space + "<" + termTag + " type=\"" + type + "\"/>\n";
            //footer = "";
        }
        else if (contentElement.getNodeType().equals(NodeContentElement.TYPES[2]))
        {
            type = "expression";
            String descrip = "";
            String dd = contentElement.getNodeDescription();
            if ((dd != null)&&(!dd.trim().equals(""))) descrip = "\" description=\"" + contentElement.getNodeDescription();
            header = space + "<" + termTag + " type=\"" + type + "\" name=\"" + contentElement.getNodeName() + descrip + "\" operation=\"" + contentElement.getOperatorName().toLowerCase() + "\">\n";
            footer = space + "</" + termTag + ">\n";

        }
        else if (contentElement.getNodeType().equals(NodeContentElement.TYPES[3]))
        {
            type = "variable";
            String descrip = "";
            String dd = contentElement.getNodeDescription();
            if ((dd != null)&&(!dd.trim().equals(""))) descrip = "\" description=\"" + contentElement.getNodeDescription();
            header = space + "<" + termTag + " type=\"" + type + "\" name=\"" + contentElement.getNodeName() + "\" value=\"" +contentElement.getNodeValue()+ descrip + "\"/>\n";
        }
        else if (contentElement.getNodeType().equals(NodeContentElement.TYPES[4]))
        {
            type = "constant";
            header = space + "<" + termTag + " type=\"" + type + "\" name=\"" + contentElement.getNodeName() + "\" value=\"" +contentElement.getNodeValue()+ "\"/>\n";
        }
        else
        {
            type = "INVALID";
            header = space + "<" + termTag + " type=\"" + type + "\"/>\n";
        }

        if (footer.equals("")) return header;

        String middle = "";
        if (priorTermButtonPane == null)
        {
            middle = space + "<" + termTag + " type=\"INVALID_NULL_PRIORPANE\"/>\n";
        }
        else
        {
            middle = priorTermButtonPane.generateXML(depth);
        }

        if ((termButtonPanes != null)&&(termButtonPanes.size() > 0))
        {
            if (termButtonPanes.get(0).getMode() != MODE_UNUSED)
            {
                middle = middle + termButtonPanes.get(0).generateXML(depth);
            }
        }

        contn = contn + header + middle + footer + tail;

        return contn;
    }

    public boolean isReadyToCalculate()
    {
        if (mainButton != null)
        {
            if (mainButton.getText().equals(UNDECIDED_MARK)) return false;
            else return true;
        }
        if (priorTermButtonPane == null) return false;

        if (!priorTermButtonPane.isReadyToCalculate()) return false;

        if ((operatorButtonPanes != null)&&(operatorButtonPanes.size() > 0))
        {
            int n = 0;
            for (FormulaButtonPane p:operatorButtonPanes)
            {
                if (!p.isReadyToCalculate()) return false;
                if (!termButtonPanes.get(n).isReadyToCalculate()) return false;
            }
        }
        return true;
    }
    public Double calculate(java.util.List<Double> values)
    {
        if (!isReadyToCalculate()) return null;
        java.util.List<String> variables = rootPanel.getVariableNames();
        if ((values == null)||(values.size() == 0)) return null;
        if ((variables == null)||(variables.size() == 0)) return null;
        if (values.size() != variables.size()) return null;

        if (mainButton != null)
        {
            if (contentElement == null) return null;
            if (contentElement.getNodeType().equals(NodeContentElement.TYPES[4]))
            {
                double val = 0.0;
                try
                {
                    val = Double.parseDouble(contentElement.getNodeValue());
                }
                catch(NumberFormatException ne)
                {
                    return null;
                }
                return new Double(val);
            }
            else if (contentElement.getNodeType().equals(NodeContentElement.TYPES[3]))
            {
                String vName = contentElement.getNodeName();
                for (int i=0;i<variables.size();i++)
                {
                   if (vName.equals(variables.get(i))) return values.get(i);
                }
                return null;
            }
        }
        double ds = 0.0;
        if (priorTermButtonPane == null) return null;

        Double dbl = priorTermButtonPane.calculate(values);
        if (dbl == null) return null;
        ds = dbl.doubleValue();

        if ((operatorButtonPanes != null)&&(operatorButtonPanes.size() > 0))
        {
            int n = 0;
            for (FormulaButtonPane p:operatorButtonPanes)
            {
                String oper = p.getOperator();
                if (oper == null) return null;

                if (oper.equals("sqrt"))
                {
                    ds = Math.sqrt(ds);
                    continue;
                }
                //else if (oper.equals("rad"))
                //{
                //    ds = Math.rad(ds);
                //    continue;
                //}
                else if (oper.equals("log10"))
                {
                    ds = Math.log10(ds);
                    continue;
                }
                else if (oper.equals("log"))
                {
                    ds = Math.log(ds);
                    continue;
                }
                else if (oper.equals("exp"))
                {
                    ds = Math.exp(ds);
                    continue;
                }
                else if (oper.equals("sin"))
                {
                    ds = Math.sin(ds);
                    continue;
                }
                else if (oper.equals("cos"))
                {
                    ds = Math.cos(ds);
                    continue;
                }
                else if (oper.equals("tan"))
                {
                    ds = Math.tan(ds);
                    continue;
                }

                Double dbl2 = termButtonPanes.get(n).calculate(values);
                if (dbl2 == null) return null;

                if (oper.equals("+")) ds = ds + dbl2;
                else if (oper.equals("-")) ds = ds - dbl2;
                else if (oper.equals("*")) ds = ds * dbl2;
                else if (oper.equals("/")) ds = ds / dbl2;
                else if (oper.equals("%")) ds = ds % dbl2;
                else if (oper.equals("pow")) ds = Math.pow(ds, dbl2);

            }
        }

        return ds;
    }

    public boolean invokeTermMeta(TermMeta meta)
    {
        if (meta.getType().equals(TermType.UNKNOWN)) return true;
        if (!concretePanel(NodeContentElement.convertMetaToElement(meta))) return false;

        java.util.List<TermMeta> childM = meta.getTerm();
        if (((childM == null)||(childM.size() == 0))&&(priorTermButtonPane == null)) return true;

        if (priorTermButtonPane != null)
        {
            if ((childM == null)||(childM.size() == 0))
            {
                System.out.println("###No Child TermMeta.");
                return false;
            }
        }
        else
        {
            if ((childM != null)&&(childM.size() > 1))
            {
                System.out.println("###Not null but no button");
                return false;
            }
        }
        if ((termButtonPanes == null)||(termButtonPanes.size() == 0)||(termButtonPanes.get(0).getMode() == MODE_UNUSED))
        {
            if ((childM != null)&&(childM.size() > 1))
            {
                System.out.println("###Excess Child Term");
                return false;
            }
        }
        else
        {
            if ((childM == null)||(childM.size() <= 1))
            {
                System.out.println("###Less Child Term");
                return false;
            }

        }
        for(int i=0;i<childM.size();i++)
        {
            TermMeta oneMeta = childM.get(i);
            if (oneMeta.getType().equals(TermType.VARIABLE))
            {
                rootPanel.addVariableDefinitions(oneMeta.getValue(), oneMeta.getDescription());
            }
            if (i == 0)
            {
                if (!priorTermButtonPane.invokeTermMeta(oneMeta)) return false;

            }
            else
            {
                if (termButtonPanes.get(i-1).getMode() != MODE_UNUSED)
                {
                    if (!termButtonPanes.get(i-1).invokeTermMeta(oneMeta)) return false;
                }
            }
        }
        return true;
    }
}