/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/nodeloader/HSMBasicNodeLoader.java,v 1.2 2007-07-05 14:12:22 wangeug Exp $
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

//import gov.nih.nci.caadapter.common.Log;
//import gov.nih.nci.caadapter.common.SystemException;
//import gov.nih.nci.caadapter.hl7.clone.meta.CloneAttributeMeta;
//import gov.nih.nci.caadapter.hl7.clone.meta.CloneDatatypeFieldMeta;
//import gov.nih.nci.caadapter.hl7.clone.meta.CloneMeta;
//import gov.nih.nci.caadapter.hl7.clone.meta.HL7V3Meta;
//import gov.nih.nci.caadapter.hl7.clone.meta.impl.CloneAttributeMetaImpl;
//import gov.nih.nci.caadapter.hl7.clone.meta.impl.CloneDatatypeFieldMetaImpl;
//import gov.nih.nci.caadapter.hl7.clone.meta.impl.CloneMetaImpl;
//import gov.nih.nci.caadapter.hl7.clone.meta.impl.HL7V3MetaImpl;

//import javax.swing.tree.DefaultMutableTreeNode;
//import javax.swing.tree.TreeNode;
//import java.util.Iterator;
//import java.util.List;

/**
 * This class defines a basic node loader implementation focusing on how to traverse HSM
 * meta data tree to convert them to a Java UI tree structure, whose nodes are either
 * DefaultMutableTreeNode or its various descendants that may individual requirement.
 *
 * Therefore, it is highly recommended to individual panel developers to sub-class this loader
 * class, with main purpose of providing customized DefaultMutableTreeNode descendant implementation,
 * while leaving the algorithm of traversing HSM meta data tree defined here intact.
 *
 * @author OWNER: Scott Jiang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.2 $
 *          date        $Date: 2007-07-05 14:12:22 $
 */
public class HSMBasicNodeLoader extends DefaultNodeLoader
{
	/**
	 * Based on the given object type, this function will convert the meta-data tree to a TreeNode-based tree structure, whose root is the returned TreeNode.
	 * @param o the meta-data object
	 * @return the root node representing the TreeNode structure mapping the given meta-data tree.
	 * @throws NodeLoader.MetaDataloadException
	 */
//	public TreeNode loadData(Object o) throws NodeLoader.MetaDataloadException
//	{
//		DefaultMutableTreeNode root = null;
//		if(o instanceof HL7V3Meta)
//		{
//			HL7V3Meta hl7Meta = (HL7V3Meta) o;
//			root = processHL7V3Meta(hl7Meta);
//			if (root != null)
//			{//the root is of HL7V3Meta, so no use to return, should remove it as first child's parent.
//				DefaultMutableTreeNode returnNode = (DefaultMutableTreeNode) root.getChildAt(0);
//				returnNode.setParent(null);
//				return returnNode;
//			}
//			else
//			{//the root is null, so return the problem.
//				return root;
//			}
//		}
//		else if(o instanceof CloneMeta)
//		{
//			return processClone((CloneMeta) o);
//		}
//		else if(o instanceof CloneAttributeMeta)
//		{
//			return processCloneAttribute((CloneAttributeMeta) o);
//		}
//		else
//		{
//			throw new SystemException("HL7MetaNodeLoader.loadData() input " + "not recognized. " + o);
//		}
//	}

//	private DefaultMutableTreeNode processHL7V3Meta(HL7V3Meta hl7Meta)
//	{
//		DefaultMutableTreeNode node = constructTreeNode(hl7Meta, true);
//		DefaultMutableTreeNode child = processClone(hl7Meta.getRootCloneMeta());
//		node.add(child);
//		return node;
//	}

	/**
	 * This function will recursively iterate the CloneMeta to construct the underline tree structure.
	 * Known data struture:
	 * CloneMeta contains a list of CloneAttributeMeta (i.e. Attributes) and list of CloneMeta (i.e. childClones).
	 * @param cloneMeta
	 * @return the newly created TreeNode whose userObject is the given CloneMeta and whose substree contains the list of children under the given meta object.
	 */
//	private DefaultMutableTreeNode processClone(CloneMeta cloneMeta)
//	{
//		List attributeList = cloneMeta.getAttributes();
//		List childCloneList = cloneMeta.getChildClones();
//		boolean allowsChildren = attributeList.size()>0 || childCloneList.size()>0;
//		DefaultMutableTreeNode cloneTreeNode = constructTreeNode(cloneMeta, allowsChildren);
//
//		for(Iterator it = attributeList.iterator(); it.hasNext();)
//		{
//			CloneAttributeMeta attributeMeta = (CloneAttributeMeta) it.next();
//			cloneTreeNode.add(processCloneAttribute(attributeMeta));
//		}
//		for (Iterator it = childCloneList.iterator(); it.hasNext();)
//		{
//			CloneMeta childCloneMeta = (CloneMeta) it.next();
//			cloneTreeNode.add(processClone(childCloneMeta));
//		}
//		return cloneTreeNode;
//	}

	/**
	 * Known data struture:
	 * Attribute contains a list of CloneAttributeMeta (i.e. child attributes) and a list of CloneDatatypeFieldMeta (i.e. dataTypeFields).
	 * @param attributeMeta
	 */
//	private DefaultMutableTreeNode processCloneAttribute(CloneAttributeMeta attributeMeta)
//	{
//		List dataTypeFieldList = attributeMeta.getDatatypeFields();
//		List childAttributeList = attributeMeta.getChildAttributes();
//		//if dataTypeFieldList is enabled, add it to the following logic operation.
//		boolean allowsChildren = childAttributeList.size() > 0 || dataTypeFieldList.size()>0;
//		DefaultMutableTreeNode attributeTreeNode = constructTreeNode(attributeMeta, allowsChildren);
//
//		//skip parsing dataTypeFields to display if it only contains one inlineText field, just present attribute and its children.
//		int dataTypeFieldsSize = dataTypeFieldList.size();
//		for (int i=0; i<dataTypeFieldsSize; i++)
//		{
//			CloneDatatypeFieldMeta dataTypeFieldMeta = (CloneDatatypeFieldMeta) dataTypeFieldList.get(i);
//			//will not ignore single inlineText field to make mapping consistent.
////			if(dataTypeFieldsSize==1 && DatatypePresenterBase.TEXT.equals(dataTypeFieldMeta.getName()))
////			{//if contains only one inlineText field, ingore.
////				continue;
////			}
//			DefaultMutableTreeNode dataTypeFieldTreeNode = constructTreeNode(dataTypeFieldMeta, false);
//			attributeTreeNode.add(dataTypeFieldTreeNode);
////			processDataTypeFields(attributeTreeNode, dataTypeFieldMeta);
//		}
//
//		for (Iterator it = childAttributeList.iterator(); it.hasNext();)
//		{
//			CloneAttributeMeta childAttributeMeta = (CloneAttributeMeta) it.next();
//			attributeTreeNode.add(processCloneAttribute(childAttributeMeta));
//		}
//		return attributeTreeNode;
//	}

//	private void processDataTypeFields(DefaultMutableTreeNode attributeTreeNode, CloneDatatypeFieldMeta dataTypeFieldMeta)
//	{
//		DefaultMutableTreeNode dataTypeFieldTreeNode = constructTreeNode(dataTypeFieldMeta, false);
//		attributeTreeNode.add(dataTypeFieldTreeNode);
//	}
	/*** End of loadData() related functions ***/

	/*** Begin unLoadData() related functions ***/
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
//	public HL7V3Meta unLoadData(DefaultMutableTreeNode treeNode, boolean resetUUID) throws NodeLoader.MetaDataloadException
//	{
//		HL7V3MetaImpl meta = new HL7V3MetaImpl();
//		meta.setRootCloneMeta(unLoadClone(treeNode, resetUUID));
//		//force to generate a new UUID.
//		if (resetUUID)
//		{
//			meta.setUUID(null);
//		}
//		return meta;
//	}

//	private CloneMeta unLoadClone(DefaultMutableTreeNode treeNode, boolean resetUUID) throws NodeLoader.MetaDataloadException
//	{
//		Object userObject = treeNode.getUserObject();
//		if(!(userObject instanceof CloneMetaImpl))
//		{
//			throw new NodeLoader.MetaDataloadException("UserObject not understood " + userObject, null);
//		}
//
//		CloneMetaImpl newCloneMeta = null;
//		CloneMetaImpl oldCloneMeta = (CloneMetaImpl) userObject;
//		try
//		{
//			newCloneMeta = (CloneMetaImpl) oldCloneMeta.clone(false, !resetUUID);
//			//break the link to the same parent object due to clone() call,
//			//since code following will reset it to a new one, which do auto remove before the add.
//			newCloneMeta.setParentMeta(null);
//		}
//		catch(Throwable e)
//		{
//			Log.logException(this, e);
//		}
////		newCloneMeta = new CloneMetaImpl();
////		newCloneMeta.setName(oldCloneMeta.getName());
////		newCloneMeta.setCmetID(oldCloneMeta.getCmetID());
////		newCloneMeta.setReferenceCloneUUID(oldCloneMeta.getReferenceCloneUUID());
//		if(resetUUID)
//		{
//			newCloneMeta.setUUID(null);
//		}
////		else
////		{
////			newCloneMeta.setUUID(oldCloneMeta.getUUID());
////		}
//		int childCount = treeNode.getChildCount();
//		for(int i=0; i<childCount; i++)
//		{
//			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) treeNode.getChildAt(i);
//			Object childObject = childNode.getUserObject();
//			if(childObject instanceof CloneMeta)
//			{
//				newCloneMeta.addClone(unLoadClone(childNode, resetUUID));
//			}
//			else if(childObject instanceof CloneAttributeMeta)
//			{
//				newCloneMeta.addAttribute(unLoadCloneAttribute(childNode, resetUUID));
//			}
//		}
//		return newCloneMeta;
//	}

//	private CloneAttributeMeta unLoadCloneAttribute(DefaultMutableTreeNode treeNode, boolean resetUUID) throws NodeLoader.MetaDataloadException
//	{
//		Object userObject = treeNode.getUserObject();
//		if (!(userObject instanceof CloneAttributeMetaImpl))
//		{
//			throw new NodeLoader.MetaDataloadException("UserObject not understood " + userObject, null);
//		}
//		CloneAttributeMeta newCloneAttributeMeta = null;
//		CloneAttributeMetaImpl oldCloneAttributeMeta = (CloneAttributeMetaImpl) userObject;
//		try
//		{
//			newCloneAttributeMeta = (CloneAttributeMeta) oldCloneAttributeMeta.clone(false, !resetUUID);
//			//break the link to the same parent object due to clone() call,
//			//since code following will reset it to a new one, which do auto remove before the add.
//			newCloneAttributeMeta.setParentMeta(null);
//		}
//		catch (CloneNotSupportedException e)
//		{
//			Log.logException(this, e);
//		}
////		if(resetUUID)
////		{
////			newCloneAttributeMeta.setUUID(null);
////		}
//		int childCount = treeNode.getChildCount();
//		for (int i = 0; i < childCount; i++)
//		{
//			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) treeNode.getChildAt(i);
//			Object childObject = childNode.getUserObject();
//			if (childObject instanceof CloneAttributeMeta)
//			{
//				newCloneAttributeMeta.addAttribute(unLoadCloneAttribute(childNode, resetUUID));
//			}
//			else if (childObject instanceof CloneDatatypeFieldMeta)
//			{
//				CloneDatatypeFieldMetaImpl oldCloneDatatypeFieldMeta = (CloneDatatypeFieldMetaImpl) childObject;
//				try
//				{
//					CloneDatatypeFieldMetaImpl newCloneDatatypeFieldMeta = (CloneDatatypeFieldMetaImpl) oldCloneDatatypeFieldMeta.clone(!resetUUID);
//					//break the link to the same parent object due to clone() call,
//					//since code following will reset it to a new one, which do auto remove before the add.
//					newCloneDatatypeFieldMeta.setParentMeta(null);
//					if(resetUUID)
//					{
//						newCloneDatatypeFieldMeta.setUUID(null);
//					}
//					newCloneAttributeMeta.addDatatyepField(newCloneDatatypeFieldMeta);
//				}
//				catch (CloneNotSupportedException e)
//				{
//					e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//				}
//			}
//		}
//		return newCloneAttributeMeta;
//	}
	/*** End of unLoadData() related functions ***/
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/04/03 16:17:13  wangeug
 * HISTORY      : initial loading
 * HISTORY      :
 * HISTORY      : Revision 1.13  2006/08/02 18:44:24  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.12  2006/06/13 18:12:12  jiangsc
 * HISTORY      : Upgraded to catch Throwable instead of Exception.
 * HISTORY      :
 * HISTORY      : Revision 1.11  2006/01/03 19:16:52  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.10  2006/01/03 18:56:25  jiangsc
 * HISTORY      : License Update
 * HISTORY      :
 * HISTORY      : Revision 1.9  2005/12/29 23:06:17  jiangsc
 * HISTORY      : Changed to latest project name.
 * HISTORY      :
 * HISTORY      : Revision 1.8  2005/12/29 15:39:06  chene
 * HISTORY      : Optimize imports
 * HISTORY      :
 * HISTORY      : Revision 1.7  2005/12/14 21:37:19  jiangsc
 * HISTORY      : Updated license information
 * HISTORY      :
 * HISTORY      : Revision 1.6  2005/11/29 16:23:54  jiangsc
 * HISTORY      : Updated License
 * HISTORY      :
 * HISTORY      : Revision 1.5  2005/11/11 19:23:59  jiangsc
 * HISTORY      : Support Pseudo Root in Mapping Panel.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2005/10/26 18:12:29  jiangsc
 * HISTORY      : replaced printStackTrace() to Log.logException
 * HISTORY      :
 * HISTORY      : Revision 1.3  2005/09/26 22:16:40  chene
 * HISTORY      : Add CMET 999900 support
 * HISTORY      :
 * HISTORY      : Revision 1.2  2005/08/31 16:52:31  chene
 * HISTORY      : Saved point
 * HISTORY      :
 * HISTORY      : Revision 1.1  2005/08/12 18:38:13  jiangsc
 * HISTORY      : Enable HL7 V3 Message to be saved in multiple XML file.
 * HISTORY      :
 */
