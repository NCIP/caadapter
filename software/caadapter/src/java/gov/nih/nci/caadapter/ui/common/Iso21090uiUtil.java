/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.ui.common;

import javax.swing.tree.DefaultMutableTreeNode;

import gov.nih.nci.caadapter.common.metadata.AttributeMetadata;
import gov.nih.nci.caadapter.common.metadata.ObjectMetadata;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAttribute;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLGeneralization;
import gov.nih.nci.ncicb.xmiinout.domain.UMLModel;

public class Iso21090uiUtil {
	public static boolean isCollectionDatatype(AttributeMetadata attributeMeta)
	{
		if (attributeMeta.getDatatype()==null)
			return false;
		if (attributeMeta.getDatatype().startsWith("DSET<"))
			return true;
		return false;
	}
	public static boolean isDatatypeWithCollectionAttribute(AttributeMetadata attributeMeta)
	{
		if (attributeMeta.getDatatype()==null)
			return false;
		if (attributeMeta.getDatatype().equals("AD"))
			return true;
		if (attributeMeta.getDatatype().equals("EN"))
			return true;
		return false;
	}
	public static AttributeMetadata findAnnotationAttribute(DefaultMutableTreeNode localAttributeNode)
	{
		AttributeMetadata rtnMeta=(AttributeMetadata)localAttributeNode.getUserObject();
		DefaultMutableTreeNode parentNode =(DefaultMutableTreeNode)localAttributeNode.getParent();
		while (parentNode.getUserObject() instanceof AttributeMetadata)
		{
			rtnMeta=(AttributeMetadata)parentNode.getUserObject();
			parentNode=(DefaultMutableTreeNode)parentNode.getParent();
		}
		return rtnMeta;
	}
	
	public static UMLAttribute findInheritedAttributeDefinition(UMLModel model, AttributeMetadata attributeMeta, UMLClass holdingObject)
	{
        UMLClass superClass=holdingObject;
        while (superClass !=null){
        	UMLClass preSuperClass=superClass;
        	superClass=null;
			for (UMLGeneralization clazzG : preSuperClass.getGeneralizations()) {
	        	UMLClass gClass =(UMLClass) clazzG.getSupertype();
	            if (gClass != preSuperClass) {
	            	for(UMLAttribute att : gClass.getAttributes()) 
	            	{
	            		if (att.getName().equals(attributeMeta.getName()))
	            			return att;
	            	}
	            	superClass=gClass;
	            } 
	        }
        }
		return null;
	}

	
	public static String findAttributeRelativePath(DefaultMutableTreeNode localAttributeNode)
	{
		AttributeMetadata attrMeta=(AttributeMetadata)localAttributeNode.getUserObject();
		String rtnPath=attrMeta.getName();
		AttributeMetadata parentAttrMeta=null;
		DefaultMutableTreeNode parentNode =(DefaultMutableTreeNode)localAttributeNode.getParent();
		while (parentNode.getUserObject() instanceof AttributeMetadata)
		{
			parentAttrMeta=(AttributeMetadata)parentNode.getUserObject();
			rtnPath=parentAttrMeta.getName()+"."+rtnPath;
			parentNode=(DefaultMutableTreeNode)parentNode.getParent();
		}
		if (parentAttrMeta==null)
			//the mapped attribute is the direct attribute of object
			//this attribute is the location of the annotation tag 
			rtnPath= "";
		else
		//remove the last attribute name since it is the direct attribute
//		if (rtnPath.length()>attrMeta.getName().length()+1)
			rtnPath=rtnPath.substring(parentAttrMeta.getName().length()+1);	
		return rtnPath;
	}
	
	public static UMLAttribute findUMLAttributeFromMeta(DefaultMutableTreeNode attributeNode)
	{
		UMLAttribute rtnAttr=null;
		DefaultMutableTreeNode parentNode =(DefaultMutableTreeNode)attributeNode.getParent();
		String attrName=((AttributeMetadata)attributeNode.getUserObject()).getName();
		while (parentNode.getUserObject() instanceof AttributeMetadata)
		{
			parentNode=(DefaultMutableTreeNode)parentNode.getParent();
		}
		ObjectMetadata parentMeta=(ObjectMetadata)parentNode.getUserObject();
		UMLClass parentUmlClass=parentMeta.getUmlClass();
		if(parentUmlClass!=null)
			rtnAttr=parentUmlClass.getAttribute(attrName);
		//The UML attribute may still be null if it is an inherited attribute
		
		return rtnAttr;
	}

}
