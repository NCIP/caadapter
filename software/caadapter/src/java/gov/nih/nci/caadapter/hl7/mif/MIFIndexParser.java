/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */


package gov.nih.nci.caadapter.hl7.mif;

/**
 * The class defines an object parsing the index of HL7 message MIF.
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.20 $
 *          date        $Date: 2009-05-05 19:01:26 $
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
	public  static void printMIFIndex(MIFIndex mifIndexInfos)
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

	public static MIFIndex loadMIFIndexFromZipFile(String zipFilePath) throws IOException
	{
//		String zipFilePath = System.getProperty("caadapter.hl7.mif.path");

		System.out.println("MIFIndexParser.loadMIFIndexFromZipFile()..zipFilePath"+zipFilePath);
		File zipFile=new File(zipFilePath);
		if (!zipFile.exists())
		{
			String mifOjbPath=zipFilePath.substring(0, zipFilePath.lastIndexOf("/"))+"/mifIndex.obj";
			return loadMifIndexObject(mifOjbPath);
		}


		ZipFile zip = new ZipFile(zipFile);
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
	public  static MIFIndex loadMIFIndex() throws Exception {
		if (NormativeVersionUtil.getCurrentMIFIndex()!=null)
			return NormativeVersionUtil.getCurrentMIFIndex();
		String zipFilePath=CaadapterUtil.getHL7_MIF_FILE_PATH();
		return loadMIFIndexFromZipFile(zipFilePath);
	}

	public static MIFIndex loadMifIndexObject(String objPath) {
		try {
			System.out.println("MIFIndexParser.loadMifIndexObject()...:"+objPath);
			URL isURL=FileUtil.retrieveResourceURL(objPath);

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

	public static void saveV2MessageIndexObject(MIFIndex mifIndex) throws Exception {
		OutputStream os = new FileOutputStream("mifIndex.obj");
		ObjectOutputStream oos = new ObjectOutputStream(os);
//		MIFIndex mifIndex= loadMIFIndex() ;
		oos.writeObject(mifIndex);
		oos.close();
		os.close();
	}

    public static void main(String[] args) throws Exception {
//		MIFIndex mifIndexInfos=MIFIndexParser.loadMIFInfos();
    	String mifFilePath="hl7_home/Normative_2005/mif_2005.zip";

		MIFIndex mifIndexInfos=MIFIndexParser.loadMIFIndexFromZipFile(mifFilePath);
		MIFIndexParser.printMIFIndex(mifIndexInfos);
    	MIFIndexParser.saveV2MessageIndexObject(mifIndexInfos);
	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.18  2009/03/25 14:00:32  wangeug
 * HISTORY :clean code
 * HISTORY :
 * HISTORY :Revision 1.17  2009/03/18 15:50:36  wangeug
 * HISTORY :enable wesstart to support multiple normatives
 * HISTORY :
 * HISTORY :Revision 1.16  2009/03/12 15:00:46  wangeug
 * HISTORY :support multiple HL& normatives
 * HISTORY :
 * HISTORY :Revision 1.15  2009/02/25 15:59:04  wangeug
 * HISTORY :enable webstart
 * HISTORY :
 * HISTORY :Revision 1.14  2008/09/29 15:44:41  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */
