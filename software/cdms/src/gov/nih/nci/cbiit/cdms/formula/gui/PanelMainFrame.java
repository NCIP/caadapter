package gov.nih.nci.cbiit.cdms.formula.gui;

import gov.nih.nci.cbiit.cdms.formula.FormulaFactory;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaStore;
import gov.nih.nci.cbiit.cdms.formula.gui.tree.CellRenderFormula;
import gov.nih.nci.cbiit.cdms.formula.gui.tree.TreeNodeFormulaStore;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.CellRendererPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.bind.JAXBException;


public class PanelMainFrame extends JPanel {

	private FormulaStore localStore=null;
	private SplitPaneFormula rightSplit;
	public PanelMainFrame()
	{
		super();
		initUI();
	}
	
	private void initUI()
	{
		this.setLayout(new BorderLayout());
		add(createLeftJSplitPane(),  BorderLayout.WEST);
		JSplitPane centerRightSplit =new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		centerRightSplit.add(createCenterJSplitPane());
		centerRightSplit.add(rightSplit);
		add(centerRightSplit, BorderLayout.CENTER);
 
	}
	
	private JSplitPane createLeftJSplitPane()
	{
		rightSplit=new SplitPaneFormula();
		JSplitPane leftSplit=new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		localStore=FormulaFactory.getLocalStore();
		TreeNodeFormulaStore localStoreNode= new TreeNodeFormulaStore(localStore);
		JTree localTree = new JTree(localStoreNode);
		leftSplit.add(new JScrollPane(localTree));
		
		TreeNodeFormulaStore commonStoreNode= new TreeNodeFormulaStore(FormulaFactory.getCommonStore());
 
		
		JTree commonStoreTree = new JTree(commonStoreNode);
		commonStoreTree.setCellRenderer(new CellRenderFormula());
		
		commonStoreTree.addTreeSelectionListener(rightSplit);
		JScrollPane commonScroll =new JScrollPane(commonStoreTree);
		commonScroll.setPreferredSize(new Dimension(150,250));
		leftSplit.add( commonScroll);
		

		
		return leftSplit;
	}
	
	private JSplitPane createCenterJSplitPane()
	{
		JSplitPane centerSplit=new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		centerSplit.add(new JButton("Uper"));
		centerSplit.add(new JButton("Down"));
		return centerSplit;
	}
	 
}
