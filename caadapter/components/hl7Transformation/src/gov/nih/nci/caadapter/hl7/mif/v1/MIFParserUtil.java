/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.hl7.mif.v1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFReferenceResolver;
import gov.nih.nci.caadapter.hl7.mif.NormativeVersionUtil;

/**
 * The class provides Utilities to access the MIF info.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.15 $
 *          date        $Date: 2009-03-13 14:55:45 $
 */
public class MIFParserUtil {

	private static MIFClass loadUnprocessedMIF(String mifFileName)
	{
		try
        {
			//normative 2008 structure /mif/COCT_MTxxxxxxxUVxx.mif
			String mifPath="mif/" + mifFileName;
			URL mifURL=FileUtil.retrieveResourceURL(mifPath);
        	//normative 2006 structure /COCT_MTxxxxxxxUVxx.mif
			if (mifURL==null)
				mifURL=FileUtil.retrieveResourceURL(mifFileName);
			InputStream mifIs =null;
			if (mifURL!=null)
				mifIs=mifURL.openStream();
			else
			{
				String mifZipFilePath= NormativeVersionUtil.getCurrentMIFIndex().getMifPath();
				System.out.println("MIFParserUtil.loadUnprocessedMIF()..mifZip path:"+mifZipFilePath);
				ZipFile mifZipFile=new ZipFile(mifZipFilePath);
				ZipEntry mifEntry=mifZipFile.getEntry(mifFileName);
				if (mifEntry==null)
					mifEntry=mifZipFile.getEntry(mifPath);
				System.out.println("MIFParserUtil.loadUnprocessedMIF()..mifEntry:"+mifEntry.getName());
				mifIs=mifZipFile.getInputStream(mifEntry);
			}
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
      		String msgType=mifFileName;
      		if (msgType.indexOf("UV")>-1)
    			msgType=msgType.substring(0, msgType.indexOf("UV"));
    		else if (msgType.indexOf(".mif")>-1)
    			msgType=msgType.substring(0, msgType.indexOf(".mif"));
      		mifParser.getMIFClass().setMessageType(msgType);
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
	
	public static MIFClass loadMIFClassWithVersion(String mifFileName, String version)
	{
		String mifParent="hl7_home";
		File mifParentDir=new File(mifParent);
		
		if (mifParentDir.exists()&&mifParentDir.isDirectory())
		{
			File[] childFiles=mifParentDir.listFiles();
			for (File childFile:childFiles)
			{
				System.out.println("MIFParserUtil.loadMIFClassWithVersion()...fileName:"+childFile.getName());
				if (childFile.getName().equalsIgnoreCase(version))
				{
					File[] hl7Files=childFile.listFiles();
					for (File hl7File:hl7Files)
					{
						System.out.println("MIFParserUtil.loadMIFClassWithVersion()...hl7 fileName:"+hl7File);
					}
				}
			}
		}
		
		return null;
	}
	public static MIFClass getMIFClass(String mifFileName)
	{
		MIFClass mifClass = null;

        System.out.println("MIFParserUtil.getMIFClass() ... mif file name : " + mifFileName);
        if (mifFileName.trim().endsWith("QQQ")) mifFileName = mifFileName.substring(0, mifFileName.length()-4);
        try {
//			mifClass = mifParser.loadMIF(mifFileName);
			mifClass=loadUnprocessedMIF(mifFileName);
		//resolve the internal reference
			MIFReferenceResolver refResolver=new MIFReferenceResolver();
			refResolver.getReferenceResolved(mifClass);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return mifClass;
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
	
	public static void main(String[] args) throws Exception {
		MIFParserUtil.loadMIFClassWithVersion("mif","Normative_2006");
		
	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.14  2009/03/12 15:01:18  wangeug
 * HISTORY :support multiple HL& normatives
 * HISTORY :
 * HISTORY :Revision 1.13  2009/02/25 15:57:28  wangeug
 * HISTORY :enable webstart
 * HISTORY :
 * HISTORY :Revision 1.12  2008/09/29 15:42:45  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */