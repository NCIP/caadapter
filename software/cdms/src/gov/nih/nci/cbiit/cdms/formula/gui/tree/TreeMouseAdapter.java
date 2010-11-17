package gov.nih.nci.cbiit.cdms.formula.gui.tree;

import gov.nih.nci.cbiit.cdms.formula.core.FormulaMeta;
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
				popupMenu.add(new JMenuItem(new ExecuteFormulaAction("Execute")));
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
}
