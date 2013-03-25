/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.v2meta;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Hashtable;

import javax.swing.tree.DefaultMutableTreeNode;

import gov.nih.nci.caadapter.castor.csv.meta.impl.types.CardinalityType;
import gov.nih.nci.caadapter.common.csv.meta.CSVMeta;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVMetaImpl;
import gov.nih.nci.caadapter.common.csv.meta.impl.CSVSegmentMetaImpl;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.cbiit.cmps.common.XSDParser;
import gov.nih.nci.cbiit.cmps.core.ElementMeta;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Oct 6, 2008
 * @author   LAST UPDATE: $Author: wangeug
 * @version  REVISION: $Revision: 1.9 $
 * @date 	 DATE: $Date: 2009-02-25 15:58:36 $
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
		String rtnSt=eMeta.getName().replace(".", "_");
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
		try {
			System.out.println("V2MetaXSDUtil.loadMessageMeta()..xsdPath:"+xsdPath);
			URL xsdURL=FileUtil.retrieveResourceURL(xsdPath);
			System.out.println("V2MetaXSDUtil.loadMessageMeta()..URL:"+xsdURL);
			if (xsdURL==null)
				return null;
			String xsdRscr = xsdURL.toURI().toString();
			System.out.println("V2MetaXSDUtil.loadMessageMeta()..message schema URI:"+xsdRscr);
			p.loadSchema(xsdRscr);

//			V2MetaXSDUtil.class.getClassLoader().getResource("mifIndex.obj").openStream();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("V2MetaXSDUtil.loadMessageMeta()..rootElmnt:"+rootElmnt);
		ElementMeta e = p.getElementMeta("urn:hl7-org:v2xml", rootElmnt);
		return e;
	}


	public static ElementMeta loadMessageMeta(String v2Version,String v2MessageSchema)
	{
		String v2XsdHome="hl7v2xsd";
		String xsdFile=v2XsdHome+"/"+v2Version;
		if (v2MessageSchema!=null)
			xsdFile=xsdFile+"/"+v2MessageSchema;//+".xsd";
		ElementMeta e = loadMessageMeta(xsdFile);
		return e;
	}

	public static DefaultMutableTreeNode findV2MetaTreeNode(String nodeXmlPath)
	{
		return elementNodehash.get(nodeXmlPath);
	}

	public static CSVMeta createDefaultCsvMeta(String messageRoot)
	{
		CSVMetaImpl csvFileMetaImpl = new CSVMetaImpl();
//		csvFileMetaImpl.setXmlPath(messageRoot);
		CSVSegmentMetaImpl rootSeg = new CSVSegmentMetaImpl(messageRoot, null);
		rootSeg.setXmlPath(messageRoot);
		rootSeg.setCardinalityType(CardinalityType.VALUE_1);
		csvFileMetaImpl.setRootSegment(rootSeg);
		return csvFileMetaImpl;
	}

}


/**
* HISTORY: $Log: not supported by cvs2svn $
* HISTORY: Revision 1.8  2009/02/02 14:53:59  wangeug
* HISTORY: Load V2 meta with version number and message schema name; do not use the absolute path of schema file
* HISTORY:
* HISTORY: Revision 1.7  2009/01/26 19:02:03  wangeug
* HISTORY: load V2 message from zip file
* HISTORY:
* HISTORY: Revision 1.6  2009/01/23 18:17:35  wangeug
* HISTORY: load V2 meta with version number and the name of message schema (include .xsd)
* HISTORY:
* HISTORY: Revision 1.5  2008/11/03 21:38:13  wangeug
* HISTORY: set xmlPath name of V2Meta element: replacing "." with "_"
* HISTORY:
* HISTORY: Revision 1.4  2008/10/29 14:16:36  wangeug
* HISTORY: load V2 meta from zip file with relative path
* HISTORY:
* HISTORY: Revision 1.3  2008/10/24 19:37:17  wangeug
* HISTORY: transfer a v2 message into v3 message using SUN v2 schema
* HISTORY:
* HISTORY: Revision 1.2  2008/10/16 14:32:28  wangeug
* HISTORY: add new method to return a V2Meta tree node with xmlPath
* HISTORY:
* HISTORY: Revision 1.1  2008/10/09 17:55:33  wangeug
* HISTORY: rename file
* HISTORY:
* HISTORY: Revision 1.2  2008/10/06 20:03:03  wangeug
* HISTORY: set code template
* HISTORY:
**/