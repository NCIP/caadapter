/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
 
package gov.nih.nci.caadapter.hl7.mif;

import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import java.io.*;
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
			String validNameStart="mif/";
			if (!fileName.startsWith(validNameStart))
				continue;
			if (!fileName.endsWith(".mif"))
				continue;
			String msgType=fileName.substring(4);
     	  	mifIndexInfos.addMessageType(msgType);
		}
		 return mifIndexInfos;
	}
	public  static MIFIndex loadMIFInfos() throws Exception {
		return loadMIFInfosZip();
//		InputStream is = Thread.currentThread().getClass().getResourceAsStream("/mifIndexInfos");
//	    if (is == null)
//	    	throw new Exception("Loading MIF index information failure : Check the location of mif index file");
//		ObjectInputStream ois = new ObjectInputStream(is);
//		MIFIndex mifIndex = (MIFIndex)ois.readObject();
//		ois.close();
//		is.close();
//		return mifIndex;
	}

    public static void main(String[] args) throws Exception {
		MIFIndexParser mifInfoParser = new MIFIndexParser();
//		MIFIndex mifIndexInfos= mifInfoParser.parseMIFIndexInfo();
//		MIFIndex mifIndexInfos=mifInfoParser.readMIFIndexInfo();
//		mifInfoParser.saveMIFIndex("c:/temp/mifIndexInfos",mifIndexInfos);

//		MIFIndex mifIndexInfos=MIFIndexParser.loadMIFInfos();
		MIFIndex mifIndexInfos=MIFIndexParser.loadMIFInfosZip();
		mifInfoParser.printMIFIndex(mifIndexInfos);
	}
}

