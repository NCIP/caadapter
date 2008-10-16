/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.v2meta;

import java.io.File;
import java.util.Hashtable;

import javax.swing.tree.DefaultMutableTreeNode;

import gov.nih.nci.cbiit.cmps.common.XSDParser;
import gov.nih.nci.cbiit.cmps.core.ElementMeta;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Oct 6, 2008
 * @author   LAST UPDATE: $Author: wangeug 
 * @version  REVISION: $Revision: 1.2 $
 * @date 	 DATE: $Date: 2008-10-16 14:32:28 $
 * @since caAdapter v4.2
 */

public class V2MetaXSDUtil {
	private static Hashtable <String, DefaultMutableTreeNode>elementNodehash;
	
	/**
	 * Reset the ElmentMeta hash to load a new ElmentMeta
	 */
	public static void resetElementHash( DefaultMutableTreeNode rootNode)
	{
		elementNodehash=new Hashtable<String, DefaultMutableTreeNode>();
		addElementTreeNode(rootNode, null);
	}
	/**
	 * Nevigate the ElementMeta tree to build the xmlPath for a tree node
	 * @param metaNode
	 * @return xmlPath of an ElementMeta
	 */
	public static String findNodeXmlPath(DefaultMutableTreeNode metaNode)
	{
		ElementMeta elmnt=(ElementMeta)metaNode.getUserObject();
		String xmlPath=formatElmentMetaName(elmnt);
		DefaultMutableTreeNode parentNode=(DefaultMutableTreeNode)metaNode.getParent();
		while(parentNode!=null)
		{
			String pXmlPath=formatElmentMetaName((ElementMeta)parentNode.getUserObject());
			xmlPath=pXmlPath+"."+xmlPath;
			parentNode=(DefaultMutableTreeNode)parentNode.getParent();
		}
		
		return xmlPath;
	}
	
	/*
	 * Add a new child ElemntMeta to the data structure for late searching
	 * @param childNode  A new child ElementMeta tree node
	 */
	private static void addElementTreeNode(DefaultMutableTreeNode elmntNode, String parentPath)
	{
		ElementMeta elmnt=(ElementMeta)elmntNode.getUserObject();
		String xmlPath=formatElmentMetaName(elmnt);
		if (parentPath!=null&&!parentPath.equals(""))
			xmlPath=parentPath+"."+xmlPath;
 		elementNodehash.put(xmlPath, elmntNode);

//		System.out.println("V2MetaXSDUtil.addElementTreeNode()..add node:"+elmntNode.hashCode()+"___"+xmlPath);
 		int chldCnt=elmntNode.getChildCount();
		for (int i=0;i<chldCnt; i++)
		{
			DefaultMutableTreeNode childNode=(DefaultMutableTreeNode)elmntNode.getChildAt(i);
			addElementTreeNode(childNode, xmlPath);
		}
	}
	/**
	 * Remove the leading XML namespace context from an ElmentMeta name
	 * @param eMeta An ElementMeta object
	 * @return
	 */
	private static String formatElmentMetaName(ElementMeta eMeta)
	{
		String rtnSt=eMeta.getName();
		if (rtnSt.indexOf(":")>-1)
    		rtnSt=rtnSt.substring(rtnSt.lastIndexOf(":")+1); // remove the leading XML namespace value

		return rtnSt;
	}
	public static ElementMeta loadMessageMeta(String xsdPath)
	{
		XSDParser p = new XSDParser();
		String rootElmnt=xsdPath.substring(xsdPath.lastIndexOf("/")+1);
		if (xsdPath.indexOf(File.separatorChar)>-1)
			rootElmnt=xsdPath.substring(xsdPath.lastIndexOf(File.separatorChar)+1);
		rootElmnt=rootElmnt.substring(0,rootElmnt.indexOf(".xsd") );
		System.out.println("XSDUtil.loadMessageMeta()..rootElmnt:"+rootElmnt);
		
		p.loadSchema(xsdPath);
		ElementMeta e = p.getElementMeta("urn:hl7-org:v2xml", rootElmnt);
		return e;
	}
	
	
	public static ElementMeta loadMessageMeta(String v2Verion,String v2Msg)
	{
		String v2XsdHome="C:/eclipseJ2ee/workspace/hl7v2xsd";
		String xsdFile=v2XsdHome+"/"+v2Verion+"/"+v2Msg+".xsd";
		ElementMeta e = loadMessageMeta(xsdFile);
		return e;
	}
	
	public static DefaultMutableTreeNode findV2MetaTreeNode(String nodeXmlPath)
	{
		return elementNodehash.get(nodeXmlPath);
	}
}


/**
* HISTORY: $Log: not supported by cvs2svn $
* HISTORY: Revision 1.1  2008/10/09 17:55:33  wangeug
* HISTORY: rename file
* HISTORY:
* HISTORY: Revision 1.2  2008/10/06 20:03:03  wangeug
* HISTORY: set code template
* HISTORY:
**/