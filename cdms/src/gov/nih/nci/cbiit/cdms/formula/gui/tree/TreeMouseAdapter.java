package gov.nih.nci.cbiit.cdms.formula.gui.tree;

import gov.nih.nci.cbiit.cdms.formula.core.DataElement;
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

			Container parentC = e.getComponent().getParent();
			while ( !(parentC instanceof JScrollPane))
			{
				parentC=parentC.getParent();
			}
			// Create PopupMenu for the Cell
			JPopupMenu popupMenu =new JPopupMenu();
			JMenuItem excItem=new JMenuItem(new ExecuteFormulaAction("Execute Formula", ExecuteFormulaAction.FORMULA_ACTION_EXECUTION));
			popupMenu.add(excItem);
			
			JMenuItem paramAddItem=new JMenuItem(new ExecuteFormulaAction("Add Formula Parameter", ExecuteFormulaAction.FORMULA_ACTION_ADD_PARAMETER));
			popupMenu.add(paramAddItem);
			
			JMenuItem paramEditItem=new JMenuItem(new ExecuteFormulaAction("Edit Formula Parameter", ExecuteFormulaAction.FORMULA_ACTION_ADD_PARAMETER));
			paramEditItem.setEnabled(false);
			popupMenu.add(paramEditItem);
			
			JMenuItem paramDeleteItem=new JMenuItem(new ExecuteFormulaAction("Delete Formula Parameter", ExecuteFormulaAction.FORMULA_ACTION_ADD_PARAMETER));
			paramDeleteItem.setEnabled(false);
			popupMenu.add(paramDeleteItem);
			
			JMenuItem deleteItem =new JMenuItem (new DeleteFormulaAction("Delete Formula", null));
			JMenuItem editItem=new JMenuItem (new EditFormulaAction("Edit Formula", null));

			popupMenu.addSeparator();
			popupMenu.add(editItem);
			popupMenu.add(deleteItem);
				
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
			
			//enable action items
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) slctedPath.getLastPathComponent();
			if (treeNode.getUserObject() instanceof FormulaMeta)
			{
				FormulaMeta formula=(FormulaMeta)treeNode.getUserObject();
				if (formula.getStatus()==FormulaStatus.FINAL)
				{
					editItem.setEnabled(false);
					deleteItem.setEnabled(false);
					paramAddItem.setEnabled(false);
				}
				deleteItem =new JMenuItem (new DeleteFormulaAction("Delete Formula", treeNode));
				editItem=new JMenuItem (new EditFormulaAction("Edit Formula", treeNode));

				if (formula.getStatus()==FormulaStatus.DRAFT)
					excItem.setEnabled(false);
			}
			else if (treeNode.getUserObject() instanceof DataElement)
			{
				DefaultMutableTreeNode parentTreeNode=(DefaultMutableTreeNode)treeNode.getParent();
				FormulaMeta parentFormula=(FormulaMeta)parentTreeNode.getUserObject();
				if (parentFormula.getStatus().equals(FormulaStatus.DRAFT))
				{
					paramDeleteItem.setEnabled(true);
					paramEditItem.setEnabled(true);
				}
			}
		}
	}
}
