/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
 
package gov.nih.nci.caadapter.hl7.mif;

/**
 * The class defines an object parsing the index of HL7 message MIF.
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.15 $
 *          date        $Date: 2009-02-25 15:59:04 $
 */


import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.FileUtil;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MIFIndexParser {
	/**
	 * Read MIF names from MIF file directory
	 * @throws Exception
	 */
	public MIFIndex readMIFIndexInfo(String mifDirPath)throws Exception
	{
		MIFIndex mifIndexInfos = new MIFIndex();
		File fileDir=new File(mifDirPath);
		if (fileDir.isDirectory())
		{
			File[] filesInDir=fileDir.listFiles();
			for (File oneFile:filesInDir)
				mifIndexInfos.addMessageType(oneFile.getName());
		}
		return mifIndexInfos;
	}

	/**
	 * Print the MIF index with message category and MIF files for each message type
	 *
	 */
	public void printMIFIndex(MIFIndex mifIndexInfos)
	{

		for(Object msgCat:mifIndexInfos.getMessageCategory()) {
			System.out.println("message category: "+msgCat);
			Set msgTypeSet=mifIndexInfos.fingMessageTypesWithCategory((String)msgCat);
			Iterator msgIt=msgTypeSet.iterator();
			while (msgIt.hasNext())
			{
				String msgType=(String) msgIt.next();
				System.out.println("MIFIndexParser.printMIFIndex()..MsgCat:"+msgCat+"...MsgType:"+msgType +"..MIF file:"+mifIndexInfos.findMIFFileName(msgType));
			}
		}
	}

	/**
	 * Save a MIFIndex object
	 * @param fileName
	 * @throws Exception
	 */
	public void saveMIFIndex(String fileName, MIFIndex mifIndexInfos ) throws Exception {
		OutputStream os = new FileOutputStream(fileName);
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(mifIndexInfos);
		oos.close();
		os.close();
	}

	private static MIFIndex loadMIFInfosZip() throws IOException
	{
//		String zipFilePath = System.getProperty("caadapter.hl7.mif.path");
		String zipFilePath=CaadapterUtil.getHL7_MIF_FILE_PATH();
		System.out.println("MIFIndexParser.loadMIFInfosZip()..zipFilePath"+zipFilePath);
		ZipFile zip = new ZipFile(zipFilePath);
		Enumeration entryEnum=zip.entries();

		MIFIndex mifIndexInfos = new MIFIndex();
		while (entryEnum.hasMoreElements())
		{
			ZipEntry zipEntry=(ZipEntry)entryEnum.nextElement();			
			String fileName=zipEntry.getName();
			//only process the "mif" directory
			//normative 2008 structure /mif/COCT_MTxxxxxxxUVxx.mif
			//normative 2006 structure /COCT_MTxxxxxxxUVxx.mif
			String validNameStart="";
			if (fileName.indexOf("/")>-1)
				validNameStart="mif/";
			if (!validNameStart.equals("")&&!fileName.startsWith(validNameStart))
				continue;
			if (!fileName.endsWith(".mif"))
				continue;
			//normative 2008 structure /mif/COCT_MTxxxxxxxUVxx.mif
			//normative 2006 structure /COCT_MTxxxxxxxUVxx.mif
			String msgType=fileName.substring(validNameStart.length());
     	  	mifIndexInfos.addMessageType(msgType);
		}
		 return mifIndexInfos;
	}
	public  static MIFIndex loadMIFInfos() throws Exception {
		return loadMIFInfosZip();
	}
	
	public static MIFIndex loadMifIndexObject() {
		try {
			System.out.println("MIFIndexParser.loadMifIndexObject()...mifIndex.obj");
			URL isURL=FileUtil.retrieveResourceURL("mifIndex.obj");

			if (isURL==null)
				return null;
			InputStream is =isURL.openStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			MIFIndex mifIndex= (MIFIndex)ois.readObject();
			ois.close();
			is.close();
			return mifIndex;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void saveV2MessageIndexObject() throws Exception {
		OutputStream os = new FileOutputStream("mifIndex.obj");
		ObjectOutputStream oos = new ObjectOutputStream(os); 
		MIFIndex mifIndex= loadMIFInfosZip() ;
		oos.writeObject(mifIndex);
		oos.close();
		os.close();
	}

    public static void main(String[] args) throws Exception {
//		MIFIndexParser mifInfoParser = new MIFIndexParser();
//		MIFIndex mifIndexInfos= mifInfoParser.parseMIFIndexInfo();
//		MIFIndex mifIndexInfos=mifInfoParser.readMIFIndexInfo();
//		mifInfoParser.saveMIFIndex("c:/temp/mifIndexInfos",mifIndexInfos);

//		MIFIndex mifIndexInfos=MIFIndexParser.loadMIFInfos();
//		MIFIndex mifIndexInfos=MIFIndexParser.loadMIFInfosZip();
//		mifInfoParser.printMIFIndex(mifIndexInfos);
    	MIFIndexParser.saveV2MessageIndexObject();
	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.14  2008/09/29 15:44:41  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */
