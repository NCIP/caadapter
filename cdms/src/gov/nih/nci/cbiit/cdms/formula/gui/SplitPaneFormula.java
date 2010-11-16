package gov.nih.nci.cbiit.cdms.formula.gui;

import gov.nih.nci.cbiit.cdms.formula.core.BaseMeta;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import gov.nih.nci.cbiit.cdms.formula.gui.properties.PanelDefaultProperties;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

public class SplitPaneFormula extends JSplitPane implements TreeSelectionListener {

	private JPanel topPanel;
	private PanelDefaultProperties lowPanel;
	
	private BaseMeta controllMeta;
	
	public SplitPaneFormula()
	{
		super(JSplitPane.VERTICAL_SPLIT);
		topPanel=new JPanel();
		topPanel.setBorder(BorderFactory.createTitledBorder("Formula Parameters"));

		add(topPanel);
		lowPanel=new PanelDefaultProperties(null);
		
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
	private void updataDisplayPane()
	{
		topPanel.removeAll();
		topPanel.setLayout(new GridLayout( 5,2));
		if (controllMeta instanceof FormulaMeta)
		{
			FormulaMeta formula=(FormulaMeta)controllMeta;
			topPanel.add(new JLabel("Name:  " +formula.getName()));
			topPanel.add(new JLabel("Annotation: "+formula.getAnnotation()));
			Iterator<String> it=formula.getExpression().paramters().keySet().iterator();
			while(it.hasNext())
			{
				String pKey=it.next();
				String lbTxt=pKey+":  "+formula.getExpression().paramters().get(pKey);
				topPanel.add(new JLabel(lbTxt));
			}
		}
		else
			topPanel.add(new JLabel ("Name:  " +controllMeta.getName()));
		lowPanel.updateProptiesDisplay(controllMeta);
		this.updateUI();
	}
}
