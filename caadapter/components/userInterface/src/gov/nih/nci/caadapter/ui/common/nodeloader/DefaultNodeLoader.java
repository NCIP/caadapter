/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/nodeloader/DefaultNodeLoader.java,v 1.1 2007-04-03 16:17:13 wangeug Exp $
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


package gov.nih.nci.caadapter.ui.common.nodeloader;

import javax.swing.*;
import javax.swing.tree.*;

/**
 * This is a default implementation of a node loader.
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-04-03 16:17:13 $
 */
public class DefaultNodeLoader implements NodeLoader
{
//	/**
//	 * To descendant of this class:
//	 * This is an overridable function to allow descendant class to provide different tree node implementations for the "root node".
//	 * @param userObject
//	 * @return a tree node that wraps the user object.
//	 */
//	protected DefaultMutableTreeNode constructRootTreeNode(Object userObject, boolean allowsChildren)
//	{
//		/**
//		 * The default implementation is to return the same value as the constructTreeNode();
//		 */
//		return constructTreeNode(userObject, allowsChildren);
//	}

	/**
	 * To descendant of this class:
	 * This is an overridable function to allow descendant class to provide
	 * different tree node implementations.
	 * @param userObject
	 * @return a tree node that wraps the user object.
	 */
	protected DefaultMutableTreeNode constructTreeNode(Object userObject)
	{
		return constructTreeNode(userObject, true);
	}

	/**
	 * Overloaded version of the function above.
	 *
	 * @param userObject
	 * @param allowsChildren
	 * @return a tree node that wraps the user object.
	 */
	public DefaultMutableTreeNode constructTreeNode(Object userObject, boolean allowsChildren)
	{
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(userObject, allowsChildren);
		return node;
	}

	/**
	 * Default implementation of the function defined in interface and do nothing.
	 * @param o
	 * @return
	 * @throws NodeLoader.MetaDataloadException
	 */
	public TreeNode loadData(Object o) throws NodeLoader.MetaDataloadException
	{
		TreeNode root = constructTreeNode("Empty Node Loader");
		return root;
	}

	/**
	 * Default implementation of the function defined in interface and do nothing.
	 * @param node
	 * @param resetUUID
	 * @return
	 * @throws NodeLoader.MetaDataloadException
	 */
	public Object unLoadData(DefaultMutableTreeNode node, boolean resetUUID) throws NodeLoader.MetaDataloadException
	{
		return new Object();
	}

	/**
	 * Refresh subtree whose root is the given treeNode reflecting the given object.
	 * After the refreshing, the given tree will be notified for the update information.
	 * @param targetNode
	 * @param object
	 * @param tree if null, no corresponding tree update information will be broadcast.
	 */
	public void refreshSubTreeByGivenMetaObject(DefaultMutableTreeNode targetNode, Object object, JTree tree) throws MetaDataloadException
	{
		DefaultMutableTreeNode newNode = (DefaultMutableTreeNode) this.loadData(object);
		if(newNode!=null)
		{//clear out all children and add in new ones, if any
			targetNode.removeAllChildren();
			int childCount = newNode.getChildCount();
			targetNode.setAllowsChildren(true);
			for(int i=0; i<childCount; i++)
			{
				//the targetNode.add() shall remove the given node from its previous parent
				//therefore, only need to find add the first one, which always be different from each iteration.
				targetNode.add((MutableTreeNode) newNode.getChildAt(0));
			}
		}
		targetNode.setUserObject(object);
		if(tree!=null)
		{
			TreeModel treeModel = tree.getModel();
			if (treeModel instanceof DefaultTreeModel)
			{//notify change.
				((DefaultTreeModel) treeModel).nodeStructureChanged(targetNode);
			}
		}
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.14  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.11  2005/12/29 23:06:15  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.10  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/14 21:37:18  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/11/11 19:23:59  jiangsc
 * HISTORY      : Support Pseudo Root in Mapping Panel.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/08/19 21:09:58  jiangsc
 * HISTORY      : Save Point.
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/08/12 18:38:14  jiangsc
 * HISTORY      : Enable HL7 V3 Message to be saved in multiple XML file.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/08/11 22:10:35  jiangsc
 * HISTORY      : Open/Save File Dialog consolidation.
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/04 22:22:27  jiangsc
 * HISTORY      : Updated license and class header information.
 * HISTORY      :
 */
