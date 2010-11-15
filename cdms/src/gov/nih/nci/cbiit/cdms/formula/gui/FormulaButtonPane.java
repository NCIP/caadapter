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
            mainButton = new JButton("--");
            mainButton.setBackground(new Color(200, 255, 255));

        }
        else
        {
            String operText = oper;
            if (oper.length() > 1) operText = oper + "(";
            mainButton = new JButton(operText);
            mainButton.setBackground(new Color(240, 225, 200));
            mode = MODE_OPERATOR;
        }

        this.setLayout(new GridLayout());
        this.add(mainButton, "Center");
        mainButton.addActionListener(this);


    }
    public void actionPerformed(ActionEvent e)
    {
        //System.out.println("TTTTT : FF");
        if (e.getSource() == mainButton)
        {
            //System.out.println("TTTTT : FF " + mode);
            if (mode == MODE_OPERATOR)
            {
                int ans = JOptionPane.showConfirmDialog(this, "Are you sure to insert another operator?", "Insert Operator", JOptionPane.YES_NO_OPTION);
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
                String opt = JOptionPane.showInputDialog(this, mes, "Select Menu", JOptionPane.QUESTION_MESSAGE);

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
                    int ans2 = JOptionPane.showConfirmDialog(this, "Are you sure to delete this expression?", "Delete Expression", JOptionPane.YES_NO_OPTION);
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
                if (frame != null) wizard = new NewTermWizard(frame, rootPanel, true);
                else if (dialog != null) wizard = new NewTermWizard(dialog, rootPanel, true);
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
    private boolean concretePanel(NodeContentElement element)
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
            priorTermButtonPane = new FormulaButtonPane(rootPanel, this);

            operatorButtonPanes = new ArrayList<FormulaButtonPane>();
            FormulaButtonPane operatorButtonPane = new FormulaButtonPane(rootPanel, this, element);
            operatorButtonPanes.add(operatorButtonPane);

            termButtonPanes = new ArrayList<FormulaButtonPane>();
            FormulaButtonPane termButtonPane = new FormulaButtonPane(rootPanel, this);
            termButtonPanes.add(termButtonPane);
            //this.removeAll();
            //String oper = element.getOperator();
            refreshPanel();
            //initialConcretPanel(oper);
            contentElement = null;
            mainButton = null;
            //this.updateUI();
            //System.out.println("TTTTT : FF2 " + oper);
        }
        else if (element.getNodeType().equals(NodeContentElement.TYPES[3]))
        {
            mainButton.setText("Var:"+element.getNodeName());
            contentElement = element;
            mainButton.setBackground(new Color(245, 200, 240));
            mainButton.updateUI();
            rootPanel.refreshContents();
        }
        else if (element.getNodeType().equals(NodeContentElement.TYPES[4]))
        {
            mainButton.setText(element.getNodeValue());
            contentElement = element;
            mainButton.setBackground(new Color(243, 225, 200));
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
            if (oper.equalsIgnoreCase("pow"))
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
        if (priorTermButtonPane == null) return;

        this.removeAll();
        if (operatorButtonPanes.size() == 0)
        {
            this.setLayout(new GridLayout());
            this.add(priorTermButtonPane, "Center");
        }
        else
        {
            String oper = operatorButtonPanes.get(0).getOperator();
            if (oper.length() == 1)
            {
                if (oper.equals("/")) this.setLayout(new GridLayout((operatorButtonPanes.size() + 1), 1));
                else this.setLayout(new GridLayout(1,(operatorButtonPanes.size() + 1)));
                this.add(setupOnePanel(oper, priorTermButtonPane));
                for (int i=0;i<operatorButtonPanes.size();i++)
                {
                    this.add(setupTwoPanel(oper, operatorButtonPanes.get(i), termButtonPanes.get(i)));
                }
            }
            else if (oper.equalsIgnoreCase("pow"))
            {
                this.setLayout(new GridLayout(1, 2));
                JPanel panelL = new JPanel(new BorderLayout());
                JPanel panelR = new JPanel(new BorderLayout());
                panelL.add(operatorButtonPanes.get(0), BorderLayout.WEST);
                panelL.add(setupOnePanel(oper, priorTermButtonPane), BorderLayout.CENTER);

                panelR.add(new JButton(")"), BorderLayout.EAST);
                panelR.add(setupOnePanel(oper, termButtonPanes.get(0)), BorderLayout.CENTER);
                this.add(panelL);
                this.add(panelR);
            }
            else
            {
                this.setLayout(new BorderLayout());
                this.add(operatorButtonPanes.get(0), BorderLayout.WEST);
                this.add(setupOnePanel(oper, priorTermButtonPane), BorderLayout.CENTER);
                termButtonPanes.get(0).setUnused(")");
                this.add(termButtonPanes.get(0), BorderLayout.EAST);
            }
        }
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
        String space = "";
        for(int i=0;i<depth;i++) space = space + "   ";
        depth++;
        String cont = "";
        String rr = "";

        if (contentElement != null)
        {
            if (contentElement.getNodeType().equals(NodeContentElement.TYPES[3])) rr = contentElement.getNodeName();
            else rr = contentElement.getNodeValue();
        }
        else if (mainButton != null) rr = mainButton.getText();

        cont = space + "<term:" + rr + ">\n";
        if (priorTermButtonPane != null) cont = cont + priorTermButtonPane.generateXML(depth);
        else return space + "<term:" + rr + "/>\n";
        if ((operatorButtonPanes != null)&&(operatorButtonPanes.size() > 0))
        {
            int n = 0;
            for (FormulaButtonPane p:operatorButtonPanes)
            {
                cont = cont + space + "   <term:" + p.getOperator() + "/>\n";
                cont = cont + termButtonPanes.get(n).generateXML(depth);
                n++;
            }
        }

        cont = cont + space + "</term:" + rr + ">\n";
        return cont;
    }
}