package gov.nih.nci.cbiit.cdms.formula.gui.tree;

import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class CellRenderFormula  extends DefaultTreeCellRenderer{

//	private static ImageIcon elementNodeIcon = new ImageIcon(DefaultSettings.getImage("elementNode.gif"));
//	private static ImageIcon attributeNodeIcon = new ImageIcon(DefaultSettings.getImage("attributeNode.gif"));
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		Component returnValue = null;
		try {
			if (!selected)
				setBackgroundNonSelectionColor(UIManager.getColor("Tree.textBackground"));
			returnValue = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

			Object userObj=node.getUserObject();
			if (userObj instanceof FormulaMeta)
			{
				FormulaMeta formula=(FormulaMeta)userObj;
				this.setText(formula.getName());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return returnValue;
	}
}
