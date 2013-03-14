/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cdms.formula.gui.tree;

import gov.nih.nci.cbiit.cdms.formula.core.DataElement;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaStatus;
import gov.nih.nci.cbiit.cdms.formula.core.FormulaStore;
import gov.nih.nci.cbiit.cdms.formula.gui.action.DeleteFormulaAction;
import gov.nih.nci.cbiit.cdms.formula.gui.action.EditFormulaAction;
import gov.nih.nci.cbiit.cdms.formula.gui.action.ExecuteFormulaAction;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
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
			ExecuteFormulaAction excFormulaAction=new ExecuteFormulaAction("Execute Formula", ExecuteFormulaAction.FORMULA_ACTION_EXECUTION);
			excFormulaAction.setEnabled(false);
			JMenuItem excformulaItem=new JMenuItem(excFormulaAction);
			popupMenu.add(excformulaItem);		
			
			
			EditFormulaAction editFormulaAction=new EditFormulaAction("Edit Formula");
			editFormulaAction.setEnabled(false);
			JMenuItem editFormulaItem=new JMenuItem (editFormulaAction);
			popupMenu.add(editFormulaItem);
			
			DeleteFormulaAction deleteFormulaAction=new DeleteFormulaAction("Delete Formula");
			deleteFormulaAction.setEnabled(false);
			JMenuItem deleteFormulaItem =new JMenuItem (deleteFormulaAction);
			popupMenu.add(deleteFormulaItem);
			
			popupMenu.addSeparator();
			//menu item for data element
			ExecuteFormulaAction paramAddAction=new ExecuteFormulaAction("Add Data Element", ExecuteFormulaAction.FORMULA_ACTION_ADD_PARAMETER);
			paramAddAction.setEnabled(false);
			JMenuItem paramAddItem=new JMenuItem(paramAddAction);
			popupMenu.add(paramAddItem);
			
			ExecuteFormulaAction paramEditAction=new ExecuteFormulaAction("Edit Data Element", ExecuteFormulaAction.FORMULA_ACTION_EDIT_PARAMETER);
			JMenuItem paramEditItem=new JMenuItem(paramEditAction); 
			paramEditItem.setEnabled(false);
			popupMenu.add(paramEditItem);
			
			ExecuteFormulaAction paramDeleteAction=new ExecuteFormulaAction("Delete Data Element", ExecuteFormulaAction.FORMULA_ACTION_DELTE_PARAMETER);
			JMenuItem paramDeleteItem=new JMenuItem(paramDeleteAction);
			paramDeleteItem.setEnabled(false);
			popupMenu.add(paramDeleteItem);
						
			//popupMenu.show(e.getComponent(), e.getX(), e.getY());
			
			//enable action items
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) slctedPath.getLastPathComponent();
			if (treeNode.getUserObject() instanceof FormulaMeta)
			{
				FormulaMeta formula=(FormulaMeta)treeNode.getUserObject();
				excFormulaAction.setFormulaNode(treeNode);
				paramAddAction.setFormulaNode(treeNode);
				if (formula.getStatus()==FormulaStatus.COMPLETE)
				{
					editFormulaItem.setEnabled(true);
					excFormulaAction.setEnabled(true);
					paramAddItem.setEnabled(true);
                }
				else if (formula.getStatus()==FormulaStatus.DRAFT)
				{
					paramAddItem.setEnabled(true);
					editFormulaItem.setEnabled(true);
					deleteFormulaItem.setEnabled(true);
                }
				else if (formula.getStatus()==FormulaStatus.FINAL)
                {
                    excFormulaAction.setEnabled(true);
                }

				
				editFormulaAction.setFormulaNode(treeNode);
				deleteFormulaAction.setFormulaNode(treeNode);
			}
			else if (treeNode.getUserObject() instanceof DataElement)
			{			
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

            boolean IsThereEnabledMenuItem = false;
            for (MenuElement ele:popupMenu.getSubElements())
            {
                Component comp = ele.getComponent();
                if (!(comp instanceof JMenuItem)) continue;
                JMenuItem i = (JMenuItem) comp;
                if (i.isEnabled())
                {
                    IsThereEnabledMenuItem = true;
                    break;
                }
            }
            if (IsThereEnabledMenuItem) popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
	}
}
