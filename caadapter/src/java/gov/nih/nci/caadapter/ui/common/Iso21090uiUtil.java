package gov.nih.nci.caadapter.ui.common;

import javax.swing.tree.DefaultMutableTreeNode;

import gov.nih.nci.caadapter.common.metadata.AttributeMetadata;
import gov.nih.nci.caadapter.common.metadata.ObjectMetadata;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAttribute;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;

public class Iso21090uiUtil {
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
	
	public static String findAttributeRelativePath(DefaultMutableTreeNode localAttributeNode)
	{
		AttributeMetadata attrMeta=(AttributeMetadata)localAttributeNode.getUserObject();
		String rtnPath=attrMeta.getName();
		DefaultMutableTreeNode parentNode =(DefaultMutableTreeNode)localAttributeNode.getParent();
		while (parentNode.getUserObject() instanceof AttributeMetadata)
		{
			attrMeta=(AttributeMetadata)parentNode.getUserObject();
			rtnPath=attrMeta.getName()+"."+rtnPath;
			parentNode=(DefaultMutableTreeNode)parentNode.getParent();
		}
		//remove the last attribute name since it is the direct attribute
		if (rtnPath.length()>attrMeta.getName().length()+1)
			rtnPath=rtnPath.substring(attrMeta.getName().length()+1);//rtnPath.replace(attrMeta.getName()+".","");
		
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
