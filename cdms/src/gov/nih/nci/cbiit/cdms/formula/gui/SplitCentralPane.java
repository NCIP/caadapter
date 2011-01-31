package gov.nih.nci.cbiit.cdms.formula.gui;

import gov.nih.nci.cbiit.cdms.formula.FormulaFactory;
import gov.nih.nci.cbiit.cdms.formula.core.BaseMeta;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import gov.nih.nci.cbiit.cdms.formula.gui.view.FormulaPanel;
import gov.nih.nci.cbiit.cdms.formula.gui.view.FormulaPanelWithJGraph;

import java.awt.*;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

public class SplitCentralPane extends JSplitPane implements TreeSelectionListener {

	private JScrollPane topScroll;
	private JLabel formulaLabel;
	private JTextArea formulaXml;
	private BaseMeta controllMeta;

    Dimension preferredDim = new Dimension(460, 350);

    public SplitCentralPane()
	{
		super(JSplitPane.VERTICAL_SPLIT);
		//FormulaPanel topPanel=new FormulaPanel(null);

        FormulaPanelWithJGraph topPanel=new FormulaPanelWithJGraph(null, this, preferredDim);

        topPanel.setBorder(BorderFactory.createTitledBorder(""));
		topScroll=new JScrollPane(topPanel);
		topScroll.setPreferredSize(preferredDim);

        add(topScroll);

        JPanel lowPanel=new JPanel();
		lowPanel.setLayout(new BorderLayout());
		lowPanel.setBorder(BorderFactory.createEtchedBorder());
		formulaLabel=new JLabel("");
		lowPanel.add(formulaLabel,BorderLayout.NORTH);
		JScrollPane xmlScroll=new JScrollPane();
		formulaXml=new JTextArea();
		xmlScroll.setViewportView(formulaXml);
		lowPanel.add(xmlScroll, BorderLayout.CENTER);
		add(lowPanel);
	}
	
	
	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		// TODO Auto-generated method stub

		JTree slctTree=(JTree)arg0.getSource();
		DefaultMutableTreeNode slectTreeNode=(DefaultMutableTreeNode)slctTree.getLastSelectedPathComponent();
		if (slectTreeNode==null)
			return;
		Object slctObj=slectTreeNode.getUserObject();
		if (slctObj instanceof BaseMeta)
		{
			controllMeta=(BaseMeta)slctObj;
			updataDisplayPane();
		}
	}
	
	public BaseMeta getControllMeta() {
		return controllMeta;
	}
    public JScrollPane getScrollPane() {
		return topScroll;
	}


    private void updataDisplayPane()
	{
		formulaLabel.setText(controllMeta.formatJavaStatement());
		formulaXml.setText("");

        Dimension dim = preferredDim;
        Component comp = topScroll.getViewport().getView();
        if ((comp != null)&&(comp instanceof FormulaPanelWithJGraph)) dim = ((FormulaPanelWithJGraph) comp).getDimension();
        else System.out.println("DDDD 90990 topScroll.getViewport().getView() is null");
        if (controllMeta instanceof FormulaMeta)
		{
			FormulaMeta formula=(FormulaMeta)controllMeta;
			//FormulaPanel newFormulaPanel=new FormulaPanel(formula);
            this.validate();
            FormulaPanelWithJGraph newFormulaPanel=new FormulaPanelWithJGraph(formula, this, dim);
            System.out.println("DDDD 80 (JSplitPane)  getX=" + this.getX()+ ", getWidh="+this.getWidth()/2 + ", getY=" + this.getY() + ", getHeight=" +this.getHeight()/2);
            topScroll.getViewport().setView(newFormulaPanel);
			formulaXml.setText(FormulaFactory.convertFormulaToXml(formula));						
		}
		else
		{
 			//FormulaPanel newFormulaPanel=new FormulaPanel(null);
            FormulaPanelWithJGraph newFormulaPanel=new FormulaPanelWithJGraph(null, this, dim);
            topScroll.getViewport().setView(newFormulaPanel);

        }
		topScroll.validate();
        //this.addPropertyChangeListener();
    }
}
