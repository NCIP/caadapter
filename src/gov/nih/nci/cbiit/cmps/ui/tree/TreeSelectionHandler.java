package gov.nih.nci.cbiit.cmps.ui.tree;

import gov.nih.nci.cbiit.cmps.ui.mapping.ElementMetaLoader;
import gov.nih.nci.cbiit.cmps.ui.properties.PropertiesSwitchController;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class TreeSelectionHandler implements TreeSelectionListener {

	private PropertiesSwitchController propertyController;
	public TreeSelectionHandler(PropertiesSwitchController controller)
	{
		propertyController=controller;
	}
	// TODO Auto-generated method stub
	/**
	 * Called whenever the value of the selection changes.
	 *
	 * @param e the event that characterizes the change.
	 */
	public void valueChanged(TreeSelectionEvent e)
	{
		TreePath newPath = e.getNewLeadSelectionPath();
		if(newPath==null)
		{
			propertyController.setSelectedItem(null);
		}
		else
		{
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) newPath.getLastPathComponent();
			Object newSelection = treeNode.getUserObject();
			if(newSelection instanceof ElementMetaLoader.MyTreeObject)
				newSelection = ((ElementMetaLoader.MyTreeObject)newSelection).getObj();
			
			propertyController.setSelectedItem(newSelection);
		}
		propertyController.getPropertiesPage().updateProptiesDisplay(null);
	}

}
