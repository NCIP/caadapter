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
			ExecuteFormulaAction excAction=new ExecuteFormulaAction("Execute Formula", ExecuteFormulaAction.FORMULA_ACTION_EXECUTION);
			JMenuItem excItem=new JMenuItem(excAction);
			popupMenu.add(excItem);
			
			ExecuteFormulaAction paramAddAction=new ExecuteFormulaAction("Add Formula Parameter", ExecuteFormulaAction.FORMULA_ACTION_ADD_PARAMETER);
			JMenuItem paramAddItem=new JMenuItem(paramAddAction);
			popupMenu.add(paramAddItem);
			
			ExecuteFormulaAction paramEditAction=new ExecuteFormulaAction("Edit Formula Parameter", ExecuteFormulaAction.FORMULA_ACTION_EDIT_PARAMETER);
			JMenuItem paramEditItem=new JMenuItem(paramEditAction); 
			paramEditItem.setEnabled(false);
			popupMenu.add(paramEditItem);
			
			ExecuteFormulaAction paramDeleteAction=new ExecuteFormulaAction("Delete Formula Parameter", ExecuteFormulaAction.FORMULA_ACTION_DELTE_PARAMETER);
			JMenuItem paramDeleteItem=new JMenuItem(paramDeleteAction);
			paramDeleteItem.setEnabled(false);
			popupMenu.add(paramDeleteItem);
			
			DeleteFormulaAction deleteFormulaAction=new DeleteFormulaAction("Delete Formula");
			EditFormulaAction editFormulaAction=new EditFormulaAction("Edit Formula");
			JMenuItem deleteItem =new JMenuItem (deleteFormulaAction);
			JMenuItem editItem=new JMenuItem (editFormulaAction);

			popupMenu.addSeparator();
			popupMenu.add(editItem);
			popupMenu.add(deleteItem);
				
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
			
			//enable action items
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) slctedPath.getLastPathComponent();
			if (treeNode.getUserObject() instanceof FormulaMeta)
			{
				FormulaMeta formula=(FormulaMeta)treeNode.getUserObject();
				excAction.setFormulaNode(treeNode);
				paramAddAction.setFormulaNode(treeNode);
				if (formula.getStatus()==FormulaStatus.FINAL)
				{
					editItem.setEnabled(false);
					deleteItem.setEnabled(false);
					paramAddItem.setEnabled(false);
				}
				editFormulaAction.setFormulaNode(treeNode);
				deleteFormulaAction.setFormulaNode(treeNode);
				

				if (formula.getStatus()==FormulaStatus.DRAFT)
					excItem.setEnabled(false);
			}
			else if (treeNode.getUserObject() instanceof DataElement)
			{
				editItem.setEnabled(false);
				deleteItem.setEnabled(false);
				excItem.setEnabled(false);
				paramAddItem.setEnabled(false);
				
				DefaultMutableTreeNode parentTreeNode=(DefaultMutableTreeNode)treeNode.getParent();
				FormulaMeta parentFormula=(FormulaMeta)parentTreeNode.getUserObject();
				if (parentFormula.getStatus()==FormulaStatus.DRAFT)
				{
					paramDeleteItem.setEnabled(true);
					paramDeleteAction.setParameter((DataElement)treeNode.getUserObject());
					paramDeleteAction.setFormulaNode(parentTreeNode);
					
					paramEditItem.setEnabled(true);
					paramEditAction.setParameter((DataElement)treeNode.getUserObject());
					paramEditAction.setFormulaNode(parentTreeNode);
				}
			}
		}
	}
}
