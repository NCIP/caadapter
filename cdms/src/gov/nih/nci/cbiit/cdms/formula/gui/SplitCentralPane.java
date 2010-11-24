package gov.nih.nci.cbiit.cdms.formula.gui;

import gov.nih.nci.cbiit.cdms.formula.FormulaFactory;
import gov.nih.nci.cbiit.cdms.formula.core.BaseMeta;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import gov.nih.nci.cbiit.cdms.formula.gui.view.FormulaPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;

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
	
	public SplitCentralPane()
	{
		super(JSplitPane.VERTICAL_SPLIT);
		FormulaPanel topPanel=new FormulaPanel(null);
		topPanel.setBorder(BorderFactory.createTitledBorder(""));
		topPanel.setPreferredSize(new Dimension(450, 350));
		topScroll=new JScrollPane(topPanel);
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
		this.setMinimumSize(new Dimension(450, 150));
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


	private void updataDisplayPane()
	{
		if (controllMeta instanceof FormulaMeta)
		{
			FormulaMeta formula=(FormulaMeta)controllMeta;
			FormulaPanel newFormulaPanel=new FormulaPanel(formula);
			topScroll.getViewport().setView(newFormulaPanel);
			topScroll.validate();
			formulaLabel.setText(formula.formatJavaStatement());
			formulaXml.setText(FormulaFactory.convertFormulaToXml(formula));
		}
		else
		{
			formulaLabel.setText(controllMeta.formatJavaStatement());
			formulaXml.setText("");
		}
		this.updateUI();
	}
}
