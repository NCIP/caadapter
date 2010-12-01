package gov.nih.nci.cbiit.cdms.formula.gui;

import gov.nih.nci.cbiit.cdms.formula.core.BaseMeta;
import gov.nih.nci.cbiit.cdms.formula.core.DataElement;
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
		topPanel.setBorder(BorderFactory.createTitledBorder("Parameters"));
		topPanel.setPreferredSize(new Dimension(120, 150));
		add(topPanel);
		lowPanel=new PanelDefaultProperties(null);
		
		add(lowPanel);
		this.setMinimumSize(new Dimension(120, 350));
	}
	@Override
	public void valueChanged(TreeSelectionEvent arg0) {
		// TODO Auto-generated method stub

		JTree slctTree=(JTree)arg0.getSource();
		slctTree.getLastSelectedPathComponent();
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
	private void updataDisplayPane()
	{
		topPanel.removeAll();
		topPanel.setLayout(new GridLayout( 5,2));
		if (controllMeta instanceof FormulaMeta)
		{
			FormulaMeta formula=(FormulaMeta)controllMeta;
			topPanel.add(new JLabel("Name:  " +formula.getName()));
			topPanel.add(new JLabel("Annotation: "+formula.getAnnotation()));
			for (DataElement p:formula.getParameter()) 
			{
				String lbTxt="Parameter:  "+p;
				topPanel.add(new JLabel(lbTxt));
			}
		}
		else
			topPanel.add(new JLabel ("Name:  " +controllMeta.getName()));
		lowPanel.updateProptiesDisplay(controllMeta);
		this.updateUI();
	}

    public PanelDefaultProperties getPropertiePanel()
    {
        return lowPanel;
    }
}
