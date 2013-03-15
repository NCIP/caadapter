/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.cbiit.cmts.ui.tree;

import gov.nih.nci.cbiit.cmts.core.ElementMeta;
import gov.nih.nci.cbiit.cmts.core.Mapping;
import gov.nih.nci.cbiit.cmts.ui.actions.ElementAnnotationAction;
import gov.nih.nci.cbiit.cmts.ui.mapping.ElementMetaLoader;
import gov.nih.nci.cbiit.cmts.ui.mapping.MappingMainPanel;

import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
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
			
			Container parentC = e.getComponent().getParent();
			
			while ( !(parentC instanceof MappingMainPanel))
			{
				parentC=parentC.getParent();
			}
			
			// Create PopupMenu for the Cell
			JPopupMenu menu = createTreePopupMenu(treeNode.getUserObject(), slctTree, ((MappingMainPanel)parentC).getGraphController().retrieveMappingData(false));
						
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	/**
	 * Set popup menu for the nodes of source tree 
	 * @return sourceNodePopup
	 */
	private JPopupMenu createTreePopupMenu(Object obj, JTree tree, Mapping mappingData)
	{
		ElementMetaLoader.MyTreeObject treeNodeObj=(ElementMetaLoader.MyTreeObject)obj;
		if (treeNodeObj.getUserObject() instanceof ElementMeta)
		{
			ElementMeta elementMeta=(ElementMeta)treeNodeObj.getUserObject();
			JPopupMenu popupMenu = new JPopupMenu();
			ElementAnnotationAction cloneAddAction=new ElementAnnotationAction("Add Clone", ElementAnnotationAction.CLONE_ADD_ACTION);
			ElementAnnotationAction cloneRemoveAction=new ElementAnnotationAction("Remove Clone", ElementAnnotationAction.CLONE_REMOVE_ACTION);
			ElementAnnotationAction choiceSelectAction=new ElementAnnotationAction("Select Choice", ElementAnnotationAction.CHOICE_SELECT_ACTION);
			ElementAnnotationAction choiceDeselectAction=new ElementAnnotationAction("De-select Choice", ElementAnnotationAction.CHOICE_DESELECT_ACTION);

	        popupMenu.add(new JMenuItem(cloneAddAction));
	        popupMenu.add(new JMenuItem(cloneRemoveAction));
	        
	        popupMenu.addSeparator();
	        popupMenu.add(new JMenuItem(choiceSelectAction));
	        popupMenu.add(new JMenuItem(choiceDeselectAction));
	        
	        if (elementMeta.getMultiplicityIndex()==null
	        		||elementMeta.getMultiplicityIndex().intValue()==0)
	        {
		        if (elementMeta.getMaxOccurs()!=null
		        		&&elementMeta.getMaxOccurs().intValue()==-1)
		        	enableMenuAction(cloneAddAction, tree,mappingData);
	        }
	        else
	        	enableMenuAction(cloneRemoveAction, tree,mappingData);
	        
	        if (elementMeta.isIsChoice())
	        {
		        if (elementMeta.isIsChosen())
		        	enableMenuAction(choiceDeselectAction, tree,mappingData);
		        else
		        	enableMenuAction(choiceSelectAction, tree,mappingData);
	        }
	        return popupMenu;
		}
		return null;
	}
	private void enableMenuAction(ElementAnnotationAction action, JTree tree, Mapping mappingData )
	{
		action.setEnabled(true);
		action.setTreeAnnotate(tree);
		action.setMappingData(mappingData);
	}
}
