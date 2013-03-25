/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.hl7.mif.v1;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFReferenceResolver;

/**
 * The class provides Utilities to access the MIF info.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.8 $
 *          date        $Date: 2008-08-14 18:36:41 $
 */
public class MIFParserUtil {

	private static MIFClass loadUnprocessedMIF(String mifFileName)
	{
		try
        {
        	InputStream mifIs =Thread.currentThread().getClass().getResourceAsStream("/mif/" + mifFileName);
        	//this.getClass().getResourceAsStream("/mif/" + mifFileName);
        	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        	DocumentBuilder db = dbf.newDocumentBuilder();
        	Document mifDoc = db.parse(mifIs);
        	MIFParser mifParser=new MIFParser();
        	boolean mifParsed=mifParser.parse(mifDoc);
      		if (!mifParsed)
      		{
      			System.out.println("BuildResourceUtil.parserMifFromZipFile()...failed parsing:"+mifFileName);

      		}
      		return mifParser.getMIFClass();
        }
        catch(org.xml.sax.SAXParseException se)
        {
            System.out.println("######### ERROR : "+se.getMessage());
            return null;
        } catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	public static MIFClass getMIFClass(String mifFileName)
	{
		MIFParser mifParser = new MIFParser();
		MIFClass mifClass = null;

        System.out.println("MIFParserUtil.getMIFClass() ... mif file name : " + mifFileName);
        if (mifFileName.trim().endsWith("QQQ")) mifFileName = mifFileName.substring(0, mifFileName.length()-4);
        try {
			mifClass = mifParser.loadMIF(mifFileName);
			//mifClass=loadUnprocessedMIF(mifFileName);
		//resolve the internal reference
			MIFReferenceResolver refResolver=new MIFReferenceResolver();
			refResolver.getReferenceResolved(mifClass);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return mifClass;
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//  		DocumentBuilder db;
//  		MIFParser mifParser = new MIFParser();
//		try {
//			db = dbf.newDocumentBuilder();
//			System.out.println("MIFParserUtil.getMIFClass()...build document:"+mifFileName);
//			InputStream is =mifParser.getClass().getResourceAsStream("/"+mifFileName);
//			Document mifDoc = db.parse(is);
//		    mifParser.parse(mifDoc);
//		} catch (ParserConfigurationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
// 		String msgType=mifFileName;
//		if (msgType.indexOf("UV")>-1)
//			msgType=msgType.substring(0, msgType.indexOf("UV"));
//		else if (msgType.indexOf(".mif")>-1)
//			msgType=msgType.substring(0, msgType.indexOf(".mif"));
//
//		MIFClass rtnClass=mifParser.getMIFClass();
//		rtnClass.setMessageType(msgType);
//		MIFReferenceResolver refResolver=new MIFReferenceResolver();
//		refResolver.getReferenceResolved(rtnClass);
//		return rtnClass;
	}

	/**
	 *
	 * @return
	 */
	public static Hashtable<String, String> getDocumentElementAttributes( Node docNode )
	{
		Hashtable<String, String> rtnHash=new Hashtable<String, String>();
		if (docNode==null)
			return rtnHash;

		NamedNodeMap attrMap=docNode.getAttributes();
		if (attrMap != null)
		{
			for (int i=0;i<attrMap.getLength();i++)
			{
				Node attrNode=attrMap.item(i);
				rtnHash.put(attrNode.getNodeName(), attrNode.getNodeValue());
			}
		}
		return rtnHash;
	}
}
