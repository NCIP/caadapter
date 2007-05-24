package gov.nih.nci.caadapter.hl7.mif;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Set;
import java.util.Iterator;
import java.util.zip.ZipEntry;

public class MIFIndexParser {
	/**
	 * Read MIF names from MIF file directory
	 * @throws Exception
	 */
	public MIFIndex readMIFIndexInfo()throws Exception 
	{
		MIFIndex mifIndexInfos = new MIFIndex();
		File fileDir=new File("C:/myProject/HL7 Datatype Parser/extractedMIF");
		if (fileDir.isDirectory())
		{
			File[] filesInDir=fileDir.listFiles();
			for (File oneFile:filesInDir)
				mifIndexInfos.addMessageType(oneFile.getName());
		}
		return mifIndexInfos;
	}
	/**
	 * Read MIF name from zipped MIF file directory
	 * @throws Exception
	 */
	public MIFIndex parseMIFIndexInfo() throws Exception {
		java.net.URL url= Thread.currentThread().getClass().getResource("/mif/COCT_MT150003UV03.mif");
		String urlSt=url.getFile();
		urlSt=urlSt.substring(6,urlSt.indexOf(".zip")+4);
		MIFIndex mifIndexInfos = new MIFIndex();
		try {
			java.util.zip.ZipFile zipFile=new java.util.zip.ZipFile(urlSt);
			for (Enumeration em1 = zipFile.entries(); em1.hasMoreElements();)
			{
				ZipEntry zipFileEntry=(ZipEntry)em1.nextElement();
				String zipFileName=zipFileEntry.getName();
				if(zipFileName.indexOf(".mif")>-1)
				{
					mifIndexInfos.addMessageType(zipFileName.substring(zipFileName.indexOf("/")+1));
				}					
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	public  static MIFIndex loadMIFInofs() throws Exception {
		InputStream is = Thread.currentThread().getClass().getResourceAsStream("/mifIndexInfos");
		ObjectInputStream ois = new ObjectInputStream(is);
		MIFIndex mifIndex = (MIFIndex)ois.readObject();
		ois.close();
		is.close();
		return mifIndex;
	}

	public static void main(String[] args) throws Exception {
		MIFIndexParser mifInfoParser = new MIFIndexParser();
//		MIFIndex mifIndexInfos= mifInfoParser.parseMIFIndexInfo();
//		MIFIndex mifIndexInfos=mifInfoParser.readMIFIndexInfo();
//		mifInfoParser.saveMIFIndex("c:/temp/mifIndexInfos",mifIndexInfos);

		MIFIndex mifIndexInfos=MIFIndexParser.loadMIFInofs();
		
		mifInfoParser.printMIFIndex(mifIndexInfos);
	}
}

