package gov.nih.nci.cbiit.cdms.formula.gui;

import gov.nih.nci.cbiit.cdms.formula.FormulaFactory;
import gov.nih.nci.cbiit.cdms.formula.core.BaseMeta;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;

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

	private JPanel topPanel;
	private JLabel topLabel;
	private JLabel formulaLabel;
	private JTextArea formulaXml;
	private BaseMeta controllMeta;
	
	public SplitCentralPane()
	{
		super(JSplitPane.VERTICAL_SPLIT);
		topPanel=new JPanel();
		topPanel.setBorder(BorderFactory.createTitledBorder(""));
		topLabel=new JLabel();
		topPanel.add(topLabel, BorderLayout.CENTER);
		topPanel.setPreferredSize(new Dimension(200, 200));
		
		add(topPanel);
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
		this.setMinimumSize(new Dimension(200, 500));
	}
	
	
	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		// TODO Auto-generated method stub

		JTree slctTree=(JTree)arg0.getSource();
		slctTree.getLastSelectedPathComponent();
		DefaultMutableTreeNode slectTreeNode=(DefaultMutableTreeNode)slctTree.getLastSelectedPathComponent();
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
			topLabel.setText(formula.toString());
			formulaLabel.setText(formula.formatJavaStatement());
			formulaXml.setText(FormulaFactory.convertFormulaToXml(formula));
		}
		else
		{
			topLabel.setText("<html>"+controllMeta.toString()+"</html>");
			formulaLabel.setText(controllMeta.toString());
			formulaXml.setText("");
		}
		this.updateUI();
	}
}
