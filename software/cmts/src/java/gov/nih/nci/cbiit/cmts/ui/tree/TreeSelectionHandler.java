/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.ui.tree;

import gov.nih.nci.cbiit.cmts.ui.jgraph.MiddlePanelJGraphController;
import gov.nih.nci.cbiit.cmts.ui.mapping.ElementMetaLoader;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class TreeSelectionHandler implements TreeSelectionListener {

	private MiddlePanelJGraphController graphController;
	public TreeSelectionHandler(MiddlePanelJGraphController controller)
	{
		graphController=controller;
	}
	// TODO Auto-generated method stub
	/**
	 * Called whenever the value of the selection changes.
	 *
	 * @param e the event that characterizes the change.
	 */
	public void valueChanged(TreeSelectionEvent e)
	{
		if (graphController.isGraphSelected())
			return;
		
		TreePath newPath = e.getNewLeadSelectionPath();
		if(newPath==null)
		{
			graphController.getPropertiesSwitchController().setSelectedItem(null);
		}
		else
		{
			//clear graph selection
			graphController.getMiddlePanel().getGraph().clearSelection();//.getMiddlePanel().getGraph().clearSelection();
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) newPath.getLastPathComponent();
			//clear selection of "the other tree"
			if (treeNode instanceof DefaultSourceTreeNode)
			{
				if (graphController.getMappingPanel().getTargetTree()!=null)
					graphController.getMappingPanel().getTargetTree().clearSelection();
			}
			else if (treeNode instanceof DefaultTargetTreeNode)
			{
				if ( graphController.getMappingPanel().getSourceTree()!=null)
					graphController.getMappingPanel().getSourceTree().clearSelection();
			}
			Object newSelection = treeNode.getUserObject();
			if(newSelection instanceof ElementMetaLoader.MyTreeObject)
				newSelection = ((ElementMetaLoader.MyTreeObject)newSelection).getUserObject();		
			graphController.getPropertiesSwitchController().setSelectedItem(newSelection);
		}
		graphController.getPropertiesSwitchController().getPropertiesPage().updateProptiesDisplay(null);
	}

}
