package gov.nih.nci.cbiit.cdms.formula.gui;

import gov.nih.nci.cbiit.cdms.formula.FormulaFactory;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaStore;
import gov.nih.nci.cbiit.cdms.formula.gui.tree.CellRenderFormula;
import gov.nih.nci.cbiit.cdms.formula.gui.tree.TreeMouseAdapter;
import gov.nih.nci.cbiit.cdms.formula.gui.tree.TreeNodeFormulaStore;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;


public class PanelMainFrame extends JPanel {

	private FormulaStore localStore=null;
	private SplitPaneFormula rightSplit;
	private SplitCentralPane centralSplit;
	public PanelMainFrame()
	{
		super();
		initUI();
	}
	
	public SplitCentralPane getCentralSplit() {
		return centralSplit;
	}

	private void initUI()
	{
		this.setLayout(new BorderLayout());
		add(createLeftJSplitPane(),  BorderLayout.WEST);
		JSplitPane centerRightSplit =new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		centerRightSplit.add(centralSplit);
		centerRightSplit.add(rightSplit);
		add(centerRightSplit, BorderLayout.CENTER);
 
	}
	
	private JSplitPane createLeftJSplitPane()
	{
		rightSplit=new SplitPaneFormula();
		centralSplit=new SplitCentralPane();
		
		JSplitPane leftSplit=new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		localStore=FormulaFactory.getLocalStore();
		
		TreeNodeFormulaStore localStoreNode= new TreeNodeFormulaStore(localStore);
		JTree localTree = new JTree(localStoreNode);
		localTree.setCellRenderer(new CellRenderFormula());
		localTree.addMouseListener(new TreeMouseAdapter());
		localTree.addTreeSelectionListener(rightSplit);
		localTree.addTreeSelectionListener(centralSplit);
		JScrollPane localScroll =new JScrollPane(localTree);
		localScroll.setPreferredSize(new Dimension(150,350));
		leftSplit.add(localScroll);
		
		TreeNodeFormulaStore commonStoreNode= new TreeNodeFormulaStore(FormulaFactory.getCommonStore());
		JTree commonStoreTree = new JTree(commonStoreNode);
		commonStoreTree.setCellRenderer(new CellRenderFormula());
		commonStoreTree.addMouseListener(new TreeMouseAdapter());
		commonStoreTree.addTreeSelectionListener(rightSplit);
		commonStoreTree.addTreeSelectionListener(centralSplit);
		JScrollPane commonScroll =new JScrollPane(commonStoreTree);
		commonScroll.setPreferredSize(new Dimension(150,150));
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
