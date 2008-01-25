/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/common/nodeloader/XsdTreeNodeLoader.java,v 1.1 2008-01-25 16:48:02 wangeug Exp $
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

import java.util.Iterator;
import gov.nih.nci.caadapter.common.metadata.AssociationMetadata;
import gov.nih.nci.caadapter.common.metadata.AttributeMetadata;
import gov.nih.nci.caadapter.common.metadata.ObjectMetadata;
import gov.nih.nci.caadapter.common.metadata.XsdModelMetadata;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * This class helps convert a CSV meta object graph (SCM) into a graph of TreeNodes.
 *
 * @author OWNER: wangeug
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2008-01-25 16:48:02 $
 */
public class XsdTreeNodeLoader extends DefaultNodeLoader
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
			if (o instanceof XsdModelMetadata)
			{
				return processXsdMeta((XsdModelMetadata)o);
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
	
	private DefaultMutableTreeNode processXsdMeta(XsdModelMetadata xsdModel)
	{
		DefaultMutableTreeNode gmeNode = constructTreeNode(xsdModel.getGmeSchema().getTargetNamespace());
	    Iterator objKeys=xsdModel.getObjectMap().keySet().iterator();

	    System.out.println("XsdTreeNodeLoader.processXsdMeta() .. \nclassMapping:");
	    while(objKeys.hasNext())
	    {
	    	String objKey=(String)objKeys.next();
	    	ObjectMetadata objMeta=(ObjectMetadata)xsdModel.getObjectMap().get(objKey);
	    	DefaultMutableTreeNode clsNode=buildObjectMetadataNode(objMeta);
	    	String clsPackagePath=objMeta.getXPath();
	    	addObjectMetadataNodeWithPackage( gmeNode,clsNode, clsPackagePath );
	    	 
	    }
		return gmeNode;
	}
	
	/**
	 * recursive add an objectMetadata tree node into the root root
	 * @param parentNode
	 * @param childNode
	 * @param packagePath
	 */
	private void addObjectMetadataNodeWithPackage(DefaultMutableTreeNode parentNode, DefaultMutableTreeNode childNode, String packagePath )
	{
		if (packagePath.indexOf(".")<0)
		{
			parentNode.add(childNode);
			return;
		}
		String rootPackageName=packagePath.substring(0,packagePath.indexOf("."));
		String leftPackagePath=packagePath.substring(packagePath.indexOf(".")+1);
		DefaultMutableTreeNode packageNode=foundPackageOnPath(parentNode,rootPackageName);
		if (packageNode==null)
		{
			packageNode=constructTreeNode(rootPackageName);
			parentNode.add(packageNode);
		}
		addObjectMetadataNodeWithPackage(packageNode, childNode,leftPackagePath );
	}
	/**
	 * Check if the package node exists
	 * @param parentNode
	 * @param pkName
	 * @return
	 */
	private DefaultMutableTreeNode foundPackageOnPath(DefaultMutableTreeNode parentNode, String pkName)
	{
		if (!(parentNode.getUserObject() instanceof String))
			return null;
		for (int i=0;i<parentNode.getChildCount();i++)
		{
			DefaultMutableTreeNode childPackageNode=(DefaultMutableTreeNode)parentNode.getChildAt(i);
			if (childPackageNode.getUserObject() instanceof String)
			{
				String childPkName=(String)childPackageNode.getUserObject() ;
				if (childPkName.equals(pkName))
					return childPackageNode;
			}
		}
		return null;
	}
	private DefaultMutableTreeNode buildObjectMetadataNode(ObjectMetadata objMeta)
	{
		DefaultMutableTreeNode clsNode=constructTreeNode(objMeta);
    	if (!objMeta.getAttributes().isEmpty())
    	{
    		for(AttributeMetadata attr:objMeta.getAttributes())
    		{
    			DefaultMutableTreeNode attrNode=constructTreeNode(attr, false);
    			clsNode.add(attrNode);
    		}
    	}
    	if (!objMeta.getAssociations().isEmpty())
    	{
    		for(AssociationMetadata assc:objMeta.getAssociations())
    		{
    			DefaultMutableTreeNode asscNode=constructTreeNode(assc, false);
    			clsNode.add(asscNode);
    		}
    	}
    	return clsNode;
	}
}
