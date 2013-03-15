/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */


package gov.nih.nci.caadapter.ui.common.nodeloader;

import java.util.Iterator;
import gov.nih.nci.caadapter.common.metadata.AssociationMetadata;
import gov.nih.nci.caadapter.common.metadata.AttributeMetadata;
import gov.nih.nci.caadapter.common.metadata.ObjectMetadata;
import gov.nih.nci.caadapter.common.metadata.XsdModelMetadata;
import gov.nih.nci.caadapter.ui.common.tree.DefaultSourceTreeNode;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * This class helps convert a CSV meta object graph (SCM) into a graph of TreeNodes.
 *
 * @author OWNER: wangeug
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v1.2
 *          revision    $Revision: 1.6 $
 *          date        $Date: 2008-09-24 17:52:22 $
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
		String projectNameDecoder=java.net.URLDecoder.decode(xsdModel.getProjectNamespace());
		DefaultMutableTreeNode gmeNode = constructTreeNode(projectNameDecoder);//xsdModel.getGmeSchema().getTargetNamespace());
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
    /**
     * Overloaded version of the function above.
     *
     * @param userObject
     * @param allowsChildren
     * @return a tree node that wraps the user object.
     */
    public DefaultMutableTreeNode constructTreeNode(Object userObject, boolean allowsChildren)
    {
        DefaultMutableTreeNode node = new DefaultSourceTreeNode(userObject, allowsChildren);
        return node;
    }

}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
*/
