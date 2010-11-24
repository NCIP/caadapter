package gov.nih.nci.cbiit.cdms.formula.gui.tree;

import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaStatus;
import gov.nih.nci.cbiit.cdms.formula.gui.action.DeleteFormulaAction;
import gov.nih.nci.cbiit.cdms.formula.gui.action.EditFormulaAction;
import gov.nih.nci.cbiit.cdms.formula.gui.action.ExecuteFormulaAction;

import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class TreeMouseAdapter extends MouseAdapter {

	/**
	 * Invoked when the mouse has been clicked on a component.
	 */
	public void mousePressed(MouseEvent e)
	{
		if (SwingUtilities.isRightMouseButton(e))
		{         	
			JTree slctTree=(JTree)e.getSource();
			TreePath slctedPath=slctTree.getSelectionPath();
			if (slctedPath==null)
				return;
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) slctedPath.getLastPathComponent();
			if (treeNode.getUserObject() instanceof FormulaMeta)
			{
				Container parentC = e.getComponent().getParent();
				while ( !(parentC instanceof JScrollPane))
				{
					parentC=parentC.getParent();
				}
				// Create PopupMenu for the Cell
				JPopupMenu popupMenu =new JPopupMenu();
				JMenuItem excItem=new JMenuItem(new ExecuteFormulaAction("Execute Formula"));
				popupMenu.add(excItem);
				
				JMenuItem deleteItem =new JMenuItem (new DeleteFormulaAction("Delete Formula", treeNode));
				JMenuItem editItem=new JMenuItem (new EditFormulaAction("Edit Formula", treeNode));
				FormulaMeta formula=(FormulaMeta)treeNode.getUserObject();
				if (formula.getStatus()==FormulaStatus.FINAL)
				{
					editItem.setEnabled(false);
					deleteItem.setEnabled(false);
				}
				
				if (formula.getStatus()==FormulaStatus.DRAFT)
					excItem.setEnabled(false);
				popupMenu.addSeparator();
				popupMenu.add(editItem);
				popupMenu.add(deleteItem);
				
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
}
