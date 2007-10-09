/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/actions/TreeExpandAllAction.java,v 1.2 2007-10-09 18:32:14 wangeug Exp $
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


package gov.nih.nci.caadapter.ui.common.actions;

import gov.nih.nci.caadapter.common.util.GeneralUtilities;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * This class defines the tree "Expand All" action.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2007-10-09 18:32:14 $
 */
public class TreeExpandAllAction extends AbstractContextAction
{
	/**
	 * Logging constant used to identify source of log entry, that could be later used to create
	 * logging mechanism to uniquely identify the logged class.
	 */
	private static final String LOGID = "$RCSfile: TreeExpandAllAction.java,v $";

	/**
	 * String that identifies the class version and solves the serial version UID problem.
	 * This String is for informational purposes only and MUST not be made final.
	 *
	 * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
	 */
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/actions/TreeExpandAllAction.java,v 1.2 2007-10-09 18:32:14 wangeug Exp $";

	private static final String COMMAND_NAME = "Expand All";
	private static final ImageIcon IMAGE_ICON = new ImageIcon(DefaultSettings.getImage("treeExpandAll.gif"));
	private static final Character COMMAND_MNEMONIC = new Character('E');
	private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Event.CTRL_MASK, false);
	private static final String TOOL_TIP_DESCRIPTION = "Expand one level down on selected tree nodes (Ctrl+'+')";

	private transient JTree tree;

	/**
	 * Defines an <code>Action</code> object with a default
	 * description string and default icon.
	 */
	public TreeExpandAllAction(JTree tree)
	{
		this(COMMAND_NAME, tree);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a default icon.
	 */
	public TreeExpandAllAction(String name, JTree tree)
	{
		this(name, IMAGE_ICON, tree);
	}

	/**
	 * Defines an <code>Action</code> object with the specified
	 * description string and a the specified icon.
	 */
	public TreeExpandAllAction(String name, Icon icon, JTree tree)
	{
		super(name, icon);
		this.tree = tree;
		setAdditionalAttributes();
	}

	/**
	 * Will be called by the constructor to set additional attributes.
	 */
	protected void setAdditionalAttributes()
	{//override super class's one to plug in its own attributes.
		setMnemonic(COMMAND_MNEMONIC);
		setAcceleratorKey(ACCELERATOR_KEY_STROKE);
		setActionCommandType(DOCUMENT_ACTION_TYPE);
		setShortDescription(TOOL_TIP_DESCRIPTION);
	}

	/**
	 * Set the new tree value.
	 * @param newTree
	 */
	public void setTree(JTree newTree)
	{
		this.tree = newTree;
	}

	private void expandChildrenPathUnderParentPath(TreePath parentTreePath, TreePath[] existingPathsToExpand)
	{
		if(parentTreePath==null)
		{
			return;
		}
		Object obj = parentTreePath.getLastPathComponent();
		if(obj instanceof DefaultMutableTreeNode)
		{
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) obj;
			int size = parentNode.getChildCount();
			TreePath[] childrenPathArray = new TreePath[size];
			for(int i=0; i<size; i++)
			{
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) parentNode.getChildAt(i);
				childrenPathArray[i] = new TreePath(childNode.getPath());
			}

			size = childrenPathArray.length;
			TreePath[] newPathArrayToExpand = new TreePath[size + existingPathsToExpand.length];
			//copy two arrays together to help filter out those that is about to expand so as to avoid undefinite loop;
			System.arraycopy(childrenPathArray, 0, newPathArrayToExpand, 0, size);
			System.arraycopy(existingPathsToExpand, 0, newPathArrayToExpand, size, existingPathsToExpand.length);

			for (int i = size - 1; i >= 0; i--)
			{//expand last row first
				if (tree.isExpanded(childrenPathArray[i]) && isTreePathInArray(childrenPathArray[i], newPathArrayToExpand))
				{
					expandChildrenPathUnderParentPath(childrenPathArray[i], newPathArrayToExpand);
				}
				else
				{
					tree.expandPath(childrenPathArray[i]);
				}
			}

		}
	}

	private boolean isTreePathInArray(TreePath treePath, TreePath[] treePathArray)
	{
		boolean result = false;
		if(treePathArray!=null)
		{
			for(int i=0; i<treePathArray.length; i++)
			{
				if(GeneralUtilities.areEqual(treePath, treePathArray[i]))
				{
					result = true;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * The abstract function that descendant classes must be overridden to provide customsized handling.
	 *
	 * @param e
	 * @return true if the action is finished successfully; otherwise, return false.
	 */
	protected boolean doAction(ActionEvent e) throws Exception
	{
		if (tree != null&&tree.getModel().getRoot()!=null)
		{
			/**
			 * Impelementation:
			 * Due to GUI performance concern, we abandoned the attempt to expand all possible tree node,
			 * instead, we changed the implementation as following:
			 * Expand all selected tree node;
			 * If nothing is selected, use root as default;
			 */
			TreePath[] selectedPaths = tree.getSelectionPaths();
			if (selectedPaths == null || selectedPaths.length == 0)
			{
				Object root = tree.getModel().getRoot();
				selectedPaths = new TreePath[1];
				selectedPaths[0] = new TreePath(root);//implies the root
			}
			int size = selectedPaths.length;
			for (int i = size - 1; i >= 0; i--)
			{//expand last row first
				if (tree.isExpanded(selectedPaths[i]) && isTreePathInArray(selectedPaths[i], selectedPaths))
				{
					expandChildrenPathUnderParentPath(selectedPaths[i], selectedPaths);
				}
				else
				{
					tree.expandPath(selectedPaths[i]);
				}
			}
		}
		setSuccessfullyPerformed(true);
		return isSuccessfullyPerformed();
	}

	/**
	 * Return the associated UI component.
	 *
	 * @return the associated UI component.
	 */
	protected Component getAssociatedUIComponent()
	{
		return tree;
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:15  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 19:16:53  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 18:56:26  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 23:06:18  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/14 21:37:20  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/20 18:19:27  jiangsc
 * HISTORY      : Updated tooltip text to refect the fix to defect #121.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/10/19 18:51:24  jiangsc
 * HISTORY      : Re-engineered Action calling sequence.
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/10/12 21:41:54  jiangsc
 * HISTORY      : Implemented Expand All function
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/26 15:57:43  jiangsc
 * HISTORY      : TreeExpandAll and TreeCollapseAll Class package move
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/04 22:22:21  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/07/25 22:02:26  jiangsc
 * HISTORY      : 1) Added expand all and collapse all;
 * HISTORY      : 2) Added toolbar on the mapping panel;
 * HISTORY      : 3) Consolidated menus;
 * HISTORY      :
 */
