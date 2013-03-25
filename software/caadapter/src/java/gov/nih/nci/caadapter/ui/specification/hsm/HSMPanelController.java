/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.ui.specification.hsm;

import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.common.validation.ValidatorResults;
//import gov.nih.nci.caadapter.hl7.clone.meta.CloneAttributeMeta;
//import gov.nih.nci.caadapter.hl7.clone.meta.HL7V3MetaUtil;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.nodeloader.NewHSMBasicNodeLoader;
import gov.nih.nci.caadapter.ui.common.tree.DefaultHSMTreeMutableTreeNode;
//import gov.nih.nci.caadapter.ui.common.nodeloader.HSMBasicNodeLoader;
//import gov.nih.nci.caadapter.ui.common.nodeloader.NewHSMBasicNodeLoader;

import gov.nih.nci.caadapter.hl7.datatype.Attribute;
import gov.nih.nci.caadapter.hl7.datatype.Datatype;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeBaseObject;
import gov.nih.nci.caadapter.hl7.mif.MIFAttribute;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

import java.awt.*;

/**
 * This class defines the tree event handler that will support HSMPanel.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.7 $
 *          date        $Date: 2008-09-29 20:14:14 $
 */
public class HSMPanelController implements TreeSelectionListener, TreeModelListener
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: HSMPanelController.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/HSMPanelController.java,v 1.7 2008-09-29 20:14:14 wangeug Exp $";

	private transient HSMPanel parentPanel;
	private DefaultMutableTreeNode currentNode;
	private boolean treeModelChanged;

	public HSMPanelController(HSMPanel hsmPanel)
	{
		this.parentPanel = hsmPanel;
	}

	private DefaultMutableTreeNode getCurrentNode()
	{
		return currentNode;
	}

	public synchronized boolean updateCurrentNodeWithUserObject(Object userObject)
	{
		DefaultMutableTreeNode targetNode = this.getCurrentNode();
		if(userObject instanceof DatatypeBaseObject)
		{
			JTree mifTree=parentPanel.getTree();
			TreePath treePath = mifTree.getSelectionPath();
			if (treePath == null)
			{//do nothing
				return false;
			}
		}

		targetNode.setUserObject(userObject);
		if (userObject instanceof MIFAttribute)
		{
			MIFAttribute mifAttr=(MIFAttribute )userObject;
			if (mifAttr.getDatatype()!=null&&mifAttr.getDatatype().isAbstract())
			{
		        NewHSMBasicNodeLoader mifTreeLoader=new NewHSMBasicNodeLoader(true);
		        DefaultHSMTreeMutableTreeNode hsmNode=(DefaultHSMTreeMutableTreeNode)targetNode;
		        DefaultMutableTreeNode  newAddressNode =mifTreeLoader.buildObjectNode(mifAttr,hsmNode.getRootMif());
		    	NewHSMBasicNodeLoader.refreshSubTreeByGivenMifObject(targetNode, newAddressNode, parentPanel.getTree());
			}
		}
		else if (userObject instanceof Attribute)
		{
			Attribute dtAttr=(Attribute )userObject;
			Datatype refDt=dtAttr.getReferenceDatatype();
			if (refDt!=null)//&&refDt.isAbstract())
			{
		        NewHSMBasicNodeLoader mifTreeLoader=new NewHSMBasicNodeLoader(true);
		        DefaultHSMTreeMutableTreeNode hsmNode=(DefaultHSMTreeMutableTreeNode)targetNode;
		        DefaultMutableTreeNode  newAddressNode =mifTreeLoader.buildObjectNode(dtAttr,hsmNode.getRootMif());
		    	NewHSMBasicNodeLoader.refreshSubTreeByGivenMifObject(targetNode, newAddressNode, parentPanel.getTree());
			}
		}
		TreeModel treeModel = parentPanel.getTree().getModel();
		if (treeModel instanceof DefaultTreeModel)
		{//notify change.
			((DefaultTreeModel) treeModel).nodeStructureChanged(targetNode);
		}
		return true;
	}
	/**
	 * Called whenever the value of the selection changes.
	 *
	 * @param e the event that characterizes the change.
	 */
	public void valueChanged(TreeSelectionEvent e)
	{
//		Log.logInfo(this, "Selection is '" + e.getPath().getLastPathComponent() + "'.");
		TreePath treePath = e.getNewLeadSelectionPath();
		if (treePath == null)
		{//do nothing
			return;
		}
		DefaultMutableTreeNode newNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
		if (GeneralUtilities.areEqual(newNode, currentNode))
		{//no need to do anything
			return;
		}
		parentPanel.setPropertiesPaneVisible(true);
		boolean isSuccess = parentPanel.getPropertiesPane().setDisplayData(newNode);
		if(isSuccess)
		{
			currentNode = newNode;
		}
		else
		{//user may veto the selection change, so have to roll back selection
			if (currentNode != null)
			{
//				TreePath oldPath = e.getOldLeadSelectionPath();
				TreePath oldPath = new TreePath(currentNode.getPath());
				Object source = e.getSource();
				if (source instanceof TreeSelectionModel)
				{
					((TreeSelectionModel)source).setSelectionPath(oldPath);
				}
				else if(source instanceof JTree)
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
//		Log.logInfo(this, "HSM Tree Node Changed.");
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
//		Log.logInfo(this, "HSM Tree Node Inserted.");
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
//		Log.logInfo(this, "HSM Tree Node Removed.");
		parentPanel.setPropertiesPaneVisible(false);
		parentPanel.setMessagePaneVisible(false);
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
//		Log.logInfo(this, "HSM Tree Structure Changed.");
		parentPanel.setPropertiesPaneVisible(false);
		parentPanel.setMessagePaneVisible(false);
		this.treeModelChanged = true;
	}

	public boolean isDataChanged()
	{
		//explicitly force the property UI to persist the value from user input to the tree structure.
		//so that we won't missed any unsaved data on property panel.
		boolean userDataChangeSuccess = parentPanel.getPropertiesPane().setDisplayData(currentNode);
		if(!userDataChangeSuccess)
		{//want to explicitly stay, so return as data changed.
			return true;
		}
		return treeModelChanged;
	}

	/**
	 * Explicitly set the value.
	 *
	 * @param value
	 */
	public void setDataChanged(boolean value)
	{
		this.treeModelChanged = value;
	}

	/**
	 * Currently utilize JOptionPane to report any given throwable to UI.
	 *
	 * @param t
	 * @param parentComponent
	 */
	protected void reportThrowableToUI(Throwable t, Component parentComponent)
	{
		DefaultSettings.reportThrowableToLogAndUI(this, t, null, parentComponent, false, false);
	}

	/**
	 * Display validator results to UI.
	 * @param results
	 */
	public void displayValidationMessage(ValidatorResults results)
	{
		parentPanel.setMessagePaneVisible(true);
		parentPanel.getMessagePane().setValidatorResults(results);
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 *
 * **/