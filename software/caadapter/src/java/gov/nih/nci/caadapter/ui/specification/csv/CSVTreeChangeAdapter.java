/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.specification.csv;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
import gov.nih.nci.caadapter.ui.common.tree.DropCompatibleComponent;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.dnd.DropTargetListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This class defines the listener implementation to listen to user selection change on tree widget
 * so as to change property display accordingly.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-06-09 19:54:07 $
 */
public class CSVTreeChangeAdapter extends MouseAdapter implements TreeSelectionListener, TreeModelListener, FocusListener
{
	private DefaultMutableTreeNode currentNode;
	private CSVPanel parentPanel;
	private boolean treeModelChanged;

//	private CSVMetadataTreeNodePropertiesPane propertyPane;

	public CSVTreeChangeAdapter(CSVPanel parentController)
	{
		this.parentPanel = parentController;
		this.treeModelChanged = false;
	}

	public void mouseClicked(MouseEvent event)
	{
		//to handle the aftermath of drag-and-drop to clean up the drag-and-drop flag
		boolean previousValue = parentPanel.isInDragDropMode();
		parentPanel.setInDragDropMode(false);
		if (previousValue)
		{//previously in drag and drop mode, so to ensure the highlight back up, generate the corresponding tree or graph selection event.
			Object source = event.getSource();
			//			String name = source == null ? "null" : source.getClass().getName();
			//			System.out.println("Source is ' " + name + "'," + "LinkSelectionHighlighter's mouseClicked is called");
			//following code tries to trigger the valueChanged() methods above to mimic and restore the "highlight" command
			if (source instanceof JTree)
			{
				JTree mTree = (JTree) source;
				//following code does not trigger valueChanged(TreeSelectionEvent) above.
				//mTree.setSelectionPaths(mTree.getSelectionPaths());
				//mTree.setSelectionRows(mTree.getSelectionRows());
				TreePath[] paths = mTree.getSelectionPaths();
				int size = paths == null ? 0 : paths.length;
				if (size > 0)
				{
					boolean[] areNew = new boolean[size];
					for (int i = 0; i < size; i++)
					{
						areNew[i] = true;
					}
					TreePath leadingPath = mTree.getLeadSelectionPath();
					TreeSelectionEvent treeEvent = new TreeSelectionEvent(source, paths, areNew, null, leadingPath);
					valueChanged(treeEvent);
				}
			}
		}
	}
	/**
	 * Called whenever the value of the selection changes.
	 *
	 * @param e the event that characterizes the change.
	 */
	public void valueChanged(TreeSelectionEvent e)
	{
		//Log.logInfo(this, "CSVTreeChangeAdapter.valueChanged() is called");
		if(parentPanel.isInDragDropMode())
		{//if tree is in dragging, no need to handle value change.
			return;
		}

		TreePath treePath = e.getNewLeadSelectionPath();
		if(treePath==null)
		{//do nothing
			return;
		}
		DefaultMutableTreeNode newNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
		if(GeneralUtilities.areEqual(newNode, currentNode))
		{//no need to do anything
			return;
		}
		CSVMetadataTreeNodePropertiesPane propertiesPane = parentPanel.getPropertiesPane();
		if(!parentPanel.isPropertiesPaneVisible())
		{
			parentPanel.setPropertiesPaneVisible(true);
		}
		boolean isSuccess = propertiesPane.setDisplayData(newNode);
		if(isSuccess)
		{
			currentNode = newNode;
		}
		else
		{//user may veto the selection change, so have to roll back selection
			if(currentNode!=null)
			{
//				TreePath oldPath = e.getOldLeadSelectionPath();
				TreePath oldPath = new TreePath(currentNode.getPath());
				Object source = e.getSource();
				if (source instanceof TreeSelectionModel)
				{
					((TreeSelectionModel) source).setSelectionPath(oldPath);
				}
				else if (source instanceof JTree)
				{
					((JTree) source).setSelectionPath(oldPath);
				}
			}
		}
	}

	/**
	 * <p>Invoked after a node (or a set of siblings) has changed in some
	 * way. The node(s) have not changed locations in the tree or
	 * altered their children arrays, but other attributes have
	 * changed and may affect presentation. Example: the name of a
	 * file has changed, but it is in the same location in the file
	 * system.</p>
	 * <p>To indicate the root has changed, childIndices and children
	 * will be null. </p>
	 * <p/>
	 * <p>Use <code>e.getPath()</code>
	 * to get the parent of the changed node(s).
	 * <code>e.getChildIndices()</code>
	 * returns the index(es) of the changed node(s).</p>
	 */
	public void treeNodesChanged(TreeModelEvent e)
	{
		//Log.logInfo(this, "CSVTreeChangeAdapter.treeNodesChanged() is called");
		//current node changed so as to refresh the node.
		parentPanel.getPropertiesPane().reloadData();
		//explicitly set the flag since it is indeed updated.
		this.treeModelChanged = true;
	}

	/**
	 * <p>Invoked after nodes have been inserted into the tree.</p>
	 * <p/>
	 * <p>Use <code>e.getPath()</code>
	 * to get the parent of the new node(s).
	 * <code>e.getChildIndices()</code>
	 * returns the index(es) of the new node(s)
	 * in ascending order.</p>
	 */
	public void treeNodesInserted(TreeModelEvent e)
	{
		//Log.logInfo(this, "CSVTreeChangeAdapter.treeNodesInserted() is called");
		parentPanel.setPropertiesPaneVisible(false);
		parentPanel.setMessagePaneVisible(false);
		this.treeModelChanged = true;
	}

	/**
	 * <p>Invoked after nodes have been removed from the tree.  Note that
	 * if a subtree is removed from the tree, this method may only be
	 * invoked once for the root of the removed subtree, not once for
	 * each individual set of siblings removed.</p>
	 * <p/>
	 * <p>Use <code>e.getPath()</code>
	 * to get the former parent of the deleted node(s).
	 * <code>e.getChildIndices()</code>
	 * returns, in ascending order, the index(es)
	 * the node(s) had before being deleted.</p>
	 */
	public void treeNodesRemoved(TreeModelEvent e)
	{
//		Log.logInfo(this, "CSVTreeChangeAdapter.treeNodesRemoved() is called");
		parentPanel.setPropertiesPaneVisible(false);
		parentPanel.setMessagePaneVisible(false);
//        DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getTreePath().getLastPathComponent();
//        parentPanel.getPropertiesPane().setDisplayData(node);
        parentPanel.getPropertiesPane().reloadData();
        this.treeModelChanged = true;
	}

	/**
	 * <p>Invoked after the tree has drastically changed structure from a
	 * given node down.  If the path returned by e.getPath() is of length
	 * one and the first element does not identify the current root node
	 * the first element should become the new root of the tree.<p>
	 * <p/>
	 * <p>Use <code>e.getPath()</code>
	 * to get the path to the node.
	 * <code>e.getChildIndices()</code>
	 * returns null.</p>
	 */
	public void treeStructureChanged(TreeModelEvent e)
	{
		//Log.logInfo(this, "CSVTreeChangeAdapter.treeStructureChanged() is called");
		parentPanel.setPropertiesPaneVisible(false);
		parentPanel.setMessagePaneVisible(false);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getTreePath().getLastPathComponent();
        parentPanel.getPropertiesPane().setDisplayData(node);
        this.treeModelChanged = true;
	}

	/**
	 * Invoked when a component gains the keyboard focus.
	 */
	public void focusGained(FocusEvent e)
	{
		//To change body of implemented methods use File | Settings | File Templates.
	}

	/**
	 * Invoked when a component loses the keyboard focus.
	 */
	public void focusLost(FocusEvent e)
	{
		DropCompatibleComponent dropTransferHandler = parentPanel.getDropTransferHandler();
		if(dropTransferHandler!=null)
		{
			if(dropTransferHandler.isInDragDropMode())
			{
				DropTargetListener dropListener = dropTransferHandler.getDropTargetAdapter();
				dropListener.dragExit(null);
			}
		}

		Component sourceComp = e.getComponent();
		Component targetComp = e.getOppositeComponent();
		String sourceStr = sourceComp==null? "null" : sourceComp.getClass().getName();
		String targetStr = targetComp==null? "null" : targetComp.getClass().getName();
		Log.logInfo(this, "Source Comp: '" + sourceStr);
		Log.logInfo(this, "Target Comp: '" + targetStr);
	}

	public boolean isDataChanged()
	{
		//explicitly force the property UI to persist the value from user input to the tree structure.
		//so that we won't missed any unsaved data on property panel.
		if (currentNode == null) {
			return true;
			//this means currentNode has just been deleted, and will be considered as changed.
		}
		boolean userDataChangeSuccess = parentPanel.getPropertiesPane().setDisplayData(currentNode);
		if(!userDataChangeSuccess)
		{//want to explicitly stay, so return as data changed.
			return true;
		}
		return treeModelChanged;
	}

	/**
	 * Explicitly set the value.
	 * @param value
	 */
	public void setDataChanged(boolean value)
	{
		this.treeModelChanged = value;
	}

	/**
	 * Display validator results to UI.
	 *
	 * @param results
	 */
	public void displayValidationMessage(ValidatorResults results, boolean displayConfirmationMessage)
	{
		parentPanel.setMessagePaneVisible(true);
		parentPanel.getMessagePane().setDisplayPopupConfirmationMessage(displayConfirmationMessage);
		parentPanel.getMessagePane().setValidatorResults(results);
	}


}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2007/10/13 03:07:00  jayannah
 * HISTORY      : Changes to enable delete action from the properties pane and refresh the tree as well as the property pane, And show a confirmation window for the delete
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/10/11 20:18:51  jayannah
 * HISTORY      : added a properties update listener
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:18:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.29  2006/10/19 21:01:18  wuye
 * HISTORY      : Fixed the lose focus bug when delete an segment or attribute from scs tree
 * HISTORY      :
 * HISTORY      : Revision 1.28  2006/08/02 18:44:21  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.27  2006/01/26 22:40:30  jiangsc
 * HISTORY      : Fix drap and drop issue on CSV panel
 * HISTORY      :
 * HISTORY      : Revision 1.26  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.25  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.24  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.23  2005/12/29 15:39:05  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.22  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.21  2005/11/29 16:23:56  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.20  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.19  2005/10/21 22:37:49  jiangsc
 * HISTORY      : Save point
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/10/21 18:26:37  jiangsc
 * HISTORY      : First round validation implementation in CSV module.
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/10/18 17:01:03  jiangsc
 * HISTORY      : Changed one function signature in DragDrop component;
 * HISTORY      : Enhanced drag-drop status monitoring in HL7MappingPanel;
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/10/04 20:49:14  jiangsc
 * HISTORY      : UI Enhancement to fix data inconsistency between tree and properties panel.
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/08/23 19:57:01  jiangsc
 * HISTORY      : Name change
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/08/23 19:54:47  jiangsc
 * HISTORY      : Name change
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/08/23 19:26:15  jiangsc
 * HISTORY      : File name update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/08/22 16:02:40  jiangsc
 * HISTORY      : Work on Add Field/Segment
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/08/19 18:54:37  jiangsc
 * HISTORY      : Added reshuffle functionality.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/08/17 20:44:03  jiangsc
 * HISTORY      : Removed some comments
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/08/05 20:35:48  jiangsc
 * HISTORY      : 0)Implemented field sequencing on CSVPanel but needs further rework;
 * HISTORY      : 1)Removed (Yes/No) for questions;
 * HISTORY      : 2)Removed double-checking after Save-As;
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/08/03 22:07:55  jiangsc
 * HISTORY      : Save Point.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/03 19:11:00  jiangsc
 * HISTORY      : Some cosmetic update and make HSMPanel able to save the same content to different file.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/02 22:28:53  jiangsc
 * HISTORY      : Newly enhanced context-sensitive menus and toolbar.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/07/25 21:56:49  jiangsc
 * HISTORY      : 1) Added expand all and collapse all;
 * HISTORY      : 2) Added toolbar on the mapping panel;
 * HISTORY      : 3) Consolidated menus;
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/07/22 20:53:12  jiangsc
 * HISTORY      : Structure change and added License and history anchors.
 * HISTORY      :
 */
