/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/HSMPanelController.java,v 1.1 2007-04-03 16:18:15 wangeug Exp $
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
import gov.nih.nci.caadapter.hl7.clone.meta.CloneAttributeMeta;
import gov.nih.nci.caadapter.hl7.clone.meta.HL7V3MetaUtil;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.nodeloader.HSMBasicNodeLoader;

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
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:18:15 $
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
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/specification/hsm/HSMPanelController.java,v 1.1 2007-04-03 16:18:15 wangeug Exp $";

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
		if(userObject instanceof CloneAttributeMeta)
		{
			CloneAttributeMeta meta = (CloneAttributeMeta) userObject;
			if(meta.isAbstract())
			{
				try
				{
					HL7V3MetaUtil.loadAbstractDatatype(meta);
					HSMBasicNodeLoader nodeLoader = parentPanel.getDefaultHSMNodeLoader();
					//will notify tree in this class itself after the if block.
					nodeLoader.refreshSubTreeByGivenMetaObject(targetNode, meta, null);
//					DefaultMutableTreeNode newNode = (DefaultMutableTreeNode) nodeLoader.loadData(meta);
//					if(newNode!=null)
//					{//clear out all children and add in new ones, if any
//						targetNode.removeAllChildren();
//						int childCount = newNode.getChildCount();
//						targetNode.setAllowsChildren(true);
//						for(int i=0; i<childCount; i++)
//						{
//							//the targetNode.add() shall remove the given node from its previous parent
//							//therefore, only need to find add the first one, which always be different from each iteration.
//							targetNode.add((MutableTreeNode) newNode.getChildAt(0));
//						}
//					}
				}
				catch (Exception e)
				{
					reportThrowableToUI(e, parentPanel);
					return false;
				}
			}
		}
		targetNode.setUserObject(userObject);
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

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.23  2006/08/02 18:44:22  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.22  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.21  2006/01/03 18:56:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.20  2005/12/29 23:06:16  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.19  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.18  2005/11/29 16:23:55  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.17  2005/10/26 18:12:29  jiangsc
 * HISTORY      : replaced printStackTrace() to Log.logException
 * HISTORY      :
 * HISTORY      : Revision 1.16  2005/10/25 22:00:43  jiangsc
 * HISTORY      : Re-arranged system output strings within UI packages.
 * HISTORY      :
 * HISTORY      : Revision 1.15  2005/08/28 18:12:28  jiangsc
 * HISTORY      : Implemented Validation on HSM panel.
 * HISTORY      :
 * HISTORY      : Revision 1.14  2005/08/24 22:28:37  jiangsc
 * HISTORY      : Enhanced JGraph implementation;
 * HISTORY      : Save point of CSV and HSM navigation update;
 * HISTORY      :
 * HISTORY      : Revision 1.13  2005/08/19 21:09:57  jiangsc
 * HISTORY      : Save Point.
 * HISTORY      :
 * HISTORY      : Revision 1.12  2005/08/19 20:43:48  jiangsc
 * HISTORY      : Change to use HSMBasicNodeLoader
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/08/19 18:03:58  jiangsc
 * HISTORY      : Further enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/08/17 19:04:20  chene
 * HISTORY      : Refactor adding abstractDatatype method
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/08/12 18:38:12  jiangsc
 * HISTORY      : Enable HL7 V3 Message to be saved in multiple XML file.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/08/08 17:43:48  jiangsc
 * HISTORY      : Enhanced the support of Abstract Datatype.
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/08/08 17:12:53  jiangsc
 * HISTORY      : Support Abstract Datatype.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/05 20:35:51  jiangsc
 * HISTORY      : 0)Implemented field sequencing on CSVPanel but needs further rework;
 * HISTORY      : 1)Removed (Yes/No) for questions;
 * HISTORY      : 2)Removed double-checking after Save-As;
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/03 19:11:02  jiangsc
 * HISTORY      : Some cosmetic update and make HSMPanel able to save the same content to different file.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/03 16:56:17  jiangsc
 * HISTORY      : Further consolidation of context sensitive management.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/02 22:28:55  jiangsc
 * HISTORY      : Newly enhanced context-sensitive menus and toolbar.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/07/29 22:00:00  jiangsc
 * HISTORY      : Enhanced HSMPanel
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/27 13:57:46  jiangsc
 * HISTORY      : Added the first round of HSMPanel.
 * HISTORY      :
 */
