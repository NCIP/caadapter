/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/nodeloader/SCMBasicNodeLoader.java,v 1.4 2007-07-06 20:43:03 umkis Exp $
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

import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVFieldMetaImpl;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVMetaImpl;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVSegmentMetaImpl;


import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Collections;
import java.util.List;

/**
 * This class defines a basic node loader implementation focusing on how to traverse CSV SCM
 * meta data tree to convert them to a Java UI tree structure, whose nodes are either
 * DefaultMutableTreeNode or its various descendants that may individual requirement.
 *
 * Therefore, it is highly recommended to individual panel developers to sub-class this loader
 * class, with main purpose of providing customized DefaultMutableTreeNode descendant implementation,
 * while leaving the algorithm of traversing CSV SCM meta data tree defined here intact.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2007-07-06 20:43:03 $
 */
public class SCMBasicNodeLoader extends DefaultNodeLoader
{

	/**
	 * Based on the given object type, this function will convert the meta-data tree to a TreeNode-based tree structure, whose root is the returned TreeNode.
	 * @param o the meta-data object
	 * @return the root node representing the TreeNode structure mapping the given meta-data tree.
	 * @throws NodeLoader.MetaDataloadException
	 */
	public TreeNode loadData(Object o) throws NodeLoader.MetaDataloadException
	{
		try
		{
			if (o instanceof CSVMeta)
			{
				return processSegment(((CSVMeta) o).getRootSegment());
			}
			else if(o instanceof CSVSegmentMeta)
			{
				return processSegment((CSVSegmentMeta)o);
			}
			else
			{
				throw new RuntimeException("SCMBasicNodeLoader.loadData() input " +
						"not recognized. " + o);
			}
		}
		catch (Exception e)
		{
			throw new NodeLoader.MetaDataloadException(e.getMessage(), e);
		}
	}

	/**
	 * Called by loadData().
	 *
	 * @param s
	 * @return a tree node to wrap the given meta data and its sub-tree.
	 */
	private DefaultMutableTreeNode processSegment(CSVSegmentMeta s)
	{
		DefaultMutableTreeNode node = constructTreeNode(s);

		List<CSVSegmentMeta> segmentChildren = s.getChildSegments();
		List<CSVFieldMeta> fields = s.getFields();
		//sort the fields based on the criteria defined in the comparator class before inserted into the tree.
		Collections.sort(fields, new CSVFieldMetaColumnNumberComparator());

		for (int i = 0; i < fields.size(); i++)
		{
			CSVFieldMeta csvFieldMeta = fields.get(i);
			node.add(constructTreeNode(csvFieldMeta, false));
		}

		for (int i = 0; i < segmentChildren.size(); i++)
		{
			CSVSegmentMeta csvSegmentMeta = segmentChildren.get(i);
			DefaultMutableTreeNode subNode = processSegment(csvSegmentMeta);
			node.add(subNode);
		}
		return node;
	}

	/**
	 * Given the node as the root of UI tree structure, this function will traverse the UI tree structure
	 * and construct a user object tree structure and return the root of the meta-data user object tree.
	 * @param treeNode  the root of the sub-tree to be processed.
	 * @param resetUUID if true, will tell loader to reset UUID field; otherwise, it will keep existing UUID;
	 *                  The reason to have the option is that the original data may come from another CSV metadata file and
	 *                  UUIDs of those data should be re-assigned before being persisted.
	 * @return the root of the meta-data user object tree.
	 * @throws NodeLoader.MetaDataloadException
	 */
	public CSVMeta unLoadData(DefaultMutableTreeNode treeNode, boolean resetUUID) throws NodeLoader.MetaDataloadException
	{
		CSVMetaImpl meta = new CSVMetaImpl();
		meta.setRootSegment(unLoadSegment(treeNode, null, resetUUID));
		if(resetUUID)
		{//force to generate a new UUID.
			meta.setUUID(null);
		}
		return meta;
	}

	/**
	 * Called by unLoadData().
	 *
	 * @param treeNode
	 * @param parent
	 * @return
	 * @throws NodeLoader.MetaDataloadException
	 *
	 */
	private CSVSegmentMeta unLoadSegment(DefaultMutableTreeNode treeNode, CSVSegmentMeta parent, boolean resetUUID) throws NodeLoader.MetaDataloadException
	{
		CSVSegmentMetaImpl newSegmentMeta;

		Object userObject = treeNode.getUserObject();
		if (!(userObject instanceof CSVSegmentMeta))
		{
			throw new NodeLoader.MetaDataloadException
					("UserObject not understood " + userObject, null);
		}

		CSVSegmentMeta oldSegment = (CSVSegmentMeta) userObject;
		newSegmentMeta = new CSVSegmentMetaImpl(oldSegment.getName(), parent);
        newSegmentMeta.setCardinalityType(oldSegment.getCardinalityType());
        if (resetUUID)
		{
			newSegmentMeta.setUUID(null);
		}
		else
		{
			newSegmentMeta.setUUID(oldSegment.getUUID());
		}

		int childCount = treeNode.getChildCount();
		for (int i = 0; i < childCount; i++)
		{
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) treeNode.getChildAt(i);
			Object childUserObject = child.getUserObject();
			if (childUserObject instanceof CSVSegmentMeta)
			{
				newSegmentMeta.addSegment(unLoadSegment(child, newSegmentMeta, resetUUID));
			}
			else if (childUserObject instanceof CSVFieldMeta)
			{
				CSVFieldMeta oldFieldMeta = (CSVFieldMeta) childUserObject;
				CSVFieldMetaImpl newFieldMeta = new CSVFieldMetaImpl(oldFieldMeta.getColumn(), oldFieldMeta.getName(), newSegmentMeta);
				if (resetUUID)
				{
					newFieldMeta.setUUID(null);
				}
				else
				{
					newFieldMeta.setUUID(oldFieldMeta.getUUID());
				}
				newSegmentMeta.addField(newFieldMeta);
			}
			else
			{
				throw new NodeLoader.MetaDataloadException
						("UserObject not understood " + childUserObject, null);
			}
		}
		return newSegmentMeta;
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2007/07/03 19:27:05  wangeug
 * HISTORY      : clean code
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/04/03 16:17:13  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.8  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/29 23:06:17  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/12/14 21:37:19  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/08/19 18:04:47  jiangsc
 * HISTORY      : Further enhancement
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/12 18:38:14  jiangsc
 * HISTORY      : Enable HL7 V3 Message to be saved in multiple XML file.
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/11 22:10:34  jiangsc
 * HISTORY      : Open/Save File Dialog consolidation.
 * HISTORY      :
 */
