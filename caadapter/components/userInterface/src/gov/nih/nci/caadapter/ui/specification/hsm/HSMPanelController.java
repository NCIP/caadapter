/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/HSMPanelController.java,v 1.3 2007-08-29 18:49:09 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
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
 *          revision    $Revision: 1.3 $
 *          date        $Date: 2007-08-29 18:49:09 $
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
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/HSMPanelController.java,v 1.3 2007-08-29 18:49:09 wangeug Exp $";

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
			if (mifAttr.getDatatype().isAbstract())
			{
		        NewHSMBasicNodeLoader mifTreeLoader=new NewHSMBasicNodeLoader(true);
		        DefaultHSMTreeMutableTreeNode hsmNode=(DefaultHSMTreeMutableTreeNode)targetNode;
		        DefaultMutableTreeNode  newAddressNode =mifTreeLoader.buildObjectNode(mifAttr,hsmNode.getRootMif());
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
//		t.printStackTrace();
//		StringWriter sw = new StringWriter();
//		PrintWriter pw = new PrintWriter(sw);
//		t.printStackTrace(pw);
//		pw.flush();
//		JOptionPane.showMessageDialog(parentComponent,
//				sw.toString(),
//				"Exception Occurred",
//				JOptionPane.ERROR_MESSAGE);
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

