/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui;

import gov.nih.nci.cbiit.cdms.formula.FormulaFactory;
import gov.nih.nci.cbiit.cdms.formula.core.BaseMeta;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import gov.nih.nci.cbiit.cdms.formula.gui.view.FormulaPanel;
import gov.nih.nci.cbiit.cdms.formula.gui.view.FormulaPanelWithJGraph;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

public class SplitCentralPane extends JSplitPane implements TreeSelectionListener {

	private JScrollPane topScroll;
	private JLabel formulaLabel;
	private JTextArea formulaXml;
    private JTextArea formulaMathML;
    private JTabbedPane tabbedPane;
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

        tabbedPane = new JTabbedPane();

        JPanel lowPanel1=new JPanel();
		lowPanel1.setLayout(new BorderLayout());
		lowPanel1.setBorder(BorderFactory.createEtchedBorder());
		formulaLabel=new JLabel("");
		lowPanel1.add(formulaLabel,BorderLayout.NORTH);
		JScrollPane xmlScroll1=new JScrollPane();
		formulaXml=new JTextArea();
		xmlScroll1.setViewportView(formulaXml);
		lowPanel1.add(xmlScroll1, BorderLayout.CENTER);

        tabbedPane.addTab("XML", null, lowPanel1);


        JPanel lowPanel2=new JPanel();
		lowPanel2.setLayout(new BorderLayout());
		lowPanel2.setBorder(BorderFactory.createEtchedBorder());
		//formulaLabel=new JLabel("");
		lowPanel2.add(new JLabel(""),BorderLayout.NORTH);
		JScrollPane xmlScroll2=new JScrollPane();
		formulaMathML=new JTextArea();
		xmlScroll2.setViewportView(formulaMathML);
		lowPanel2.add(xmlScroll2, BorderLayout.CENTER);
		tabbedPane.addTab("MathML", null, lowPanel2);

        add(tabbedPane);
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
        //else System.out.println("DDDD 90990 topScroll.getViewport().getView() is null");
        if (controllMeta instanceof FormulaMeta)
		{
			FormulaMeta formula=(FormulaMeta)controllMeta;
			//FormulaPanel newFormulaPanel=new FormulaPanel(formula);
            this.validate();
            FormulaPanelWithJGraph newFormulaPanel=new FormulaPanelWithJGraph(formula, this, dim);
            //System.out.println("DDDD 80 (JSplitPane)  getX=" + this.getX()+ ", getWidh="+this.getWidth()/2 + ", getY=" + this.getY() + ", getHeight=" +this.getHeight()/2);
            topScroll.getViewport().setView(newFormulaPanel);
			formulaXml.setText(FormulaFactory.convertFormulaToXml(formula));
            formulaMathML.setText(formula.getMathML());
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
