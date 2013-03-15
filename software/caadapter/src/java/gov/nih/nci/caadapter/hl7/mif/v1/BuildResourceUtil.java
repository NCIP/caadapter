/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.hl7.mif.v1;
/**
 * The class defines the Utiliy object loading HL7 v3 normative artifacts
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: altturbo $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.8 $
 *          date        $Date: 2009-08-07 21:45:14 $
 */

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.hl7.mif.MIFIndex;
import gov.nih.nci.caadapter.hl7.mif.MIFIndexParser;
import gov.nih.nci.caadapter.hl7.mif.v1.ReassignSortKeyToMIF;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class BuildResourceUtil {

	public static String CMET_INFOR_SOURCE_FILE="cmetinfo.coremif";
	public static String CMET_INFOR_TARGET_FILE="cmetInfos";
	public static String RESOURCE_DIR="tempDir/processable";
	public static String DATATYPE_FILE="datatypes";

	public static String findResourceFile(String fileName)
	{
		File libDir=new File("lib");
		if (!libDir.exists()||!libDir.isDirectory())
			return null;
		String[] childFileName=libDir.list();
		for(String childName:childFileName)
		{
			if (childName.equalsIgnoreCase(fileName))
				return libDir.getAbsolutePath()+File.separator+fileName;
		}

		return null;
	}
	public static void parserCmetInfor(String fileName, String targetFile)
	{
		CMETInfoParser cmetInfoParser = new CMETInfoParser();
		try {
			InputStream is=new FileInputStream(new File(fileName));
			cmetInfoParser.parserCMETInfoWithStream(is);//.parseCMETInfo(fileName);
//			cmetInfoParser.printCMETInfo();
			cmetInfoParser.saveCMETInofs(targetFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void parerMifIndexFromFileDirectory(String fileDirName)
	{
		try {
			 File mifDir = new File(fileDirName);
			 if (!mifDir.exists()||!mifDir.isDirectory())
				 return;
			 String[] children = mifDir.list();

			 MIFIndexParser mifInfoParser = new MIFIndexParser();
			 MIFIndex mifIndexInfos = new MIFIndex();
			 for (int i=0; i<children.length; i++)
			 {
				 String filename = children[i];
	             if (filename.indexOf(".mif")>-1)
	            	 mifIndexInfos.addMessageType(filename.substring(0));
			 }
			 mifInfoParser.saveMIFIndex(RESOURCE_DIR+"/mifIndexInfos",mifIndexInfos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void parserMifFromFileDirectory(String fileDirName)
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try
		{
			DocumentBuilder db = dbf.newDocumentBuilder();
			 File mifDir = new File(fileDirName);
			 if (!mifDir.exists()||!mifDir.isDirectory())
				 return;
			 String[] children = mifDir.list();
			 for (int i=0; i<children.length; i++)
			 {
				String fileName=children[i];
				String msgType=fileName;
				if (fileName.equals(CMET_INFOR_SOURCE_FILE))
				{
					String cmetTarget=RESOURCE_DIR+"/"+CMET_INFOR_TARGET_FILE;
					parserCmetInfor(mifDir.getAbsolutePath()+"/"+fileName, cmetTarget);
				}

				if (!msgType.endsWith(".mif"))
					continue;
				String filePath=mifDir.getAbsolutePath()+"/"+fileName;
				Document mifDoc = db.parse(filePath);
				MIFParser mifParser = new MIFParser();
	        	mifParser.parse(mifDoc);

	    		if (msgType.indexOf("UV")>-1)
	    			msgType=msgType.substring(0, msgType.indexOf("UV"));
	    		else if (msgType.indexOf(".mif")>-1)
	    			msgType=msgType.substring(0, msgType.indexOf(".mif"));

	    		mifParser.saveMIFs(RESOURCE_DIR+"/mif/" + fileName,msgType);
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static void parerMifIndexFromZipFile(String zipFileName) throws Exception
	{
		File resourceFile=new File(RESOURCE_DIR);
		if (!resourceFile.exists())
		{
			resourceFile.mkdir();
			File mifFile=new File(RESOURCE_DIR+File.separator+"mif");
			mifFile.mkdir();
		}
		ZipFile zip = new ZipFile(zipFileName);
		 Enumeration entryEnum=zip.entries();
		 MIFIndexParser mifInfoParser = new MIFIndexParser();
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
		 mifInfoParser.saveMIFIndex(RESOURCE_DIR+File.separator+"mifIndexInfos",mifIndexInfos);
	}
	public static void parserCmetFromZipFile(String zipFileName)
	{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		System.out.println("BuildResourceUtil.parserMifFromZipFile()..creat target dir:"+RESOURCE_DIR);
		File resourceFile=new File(RESOURCE_DIR);
		if (!resourceFile.exists())
		{
			resourceFile.mkdir();
			File mifFile=new File(RESOURCE_DIR+File.separator+"mif");
			mifFile.mkdir();
		}
		try {

			ZipFile zip=new ZipFile(zipFileName);
			Enumeration entryEnum=zip.entries();
			while (entryEnum.hasMoreElements())
			{
				ZipEntry zipEntry=(ZipEntry)entryEnum.nextElement();
				String fileName=zipEntry.getName();

				//only process the "mif" directory
				String validNameStart="mif/";
				if (!fileName.startsWith(validNameStart))
					continue;
				String msgType=fileName;
				if (fileName.equals("mif/"+CMET_INFOR_SOURCE_FILE))
				{
					//parer CMET INFO
					CMETInfoParser cmetInfoParser = new CMETInfoParser();
					InputStream cmetIs;
					cmetIs = zip.getInputStream(zipEntry);
					cmetInfoParser.parserCMETInfoWithStream(cmetIs);//.parseCMETInfo(fileName);
					String targetFile=RESOURCE_DIR+File.separator+CMET_INFOR_TARGET_FILE;
					cmetInfoParser.saveCMETInofs(targetFile);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
    public static void parserMifFromZipFile(String zipFileName, boolean isSortKeyReassigning) throws Exception
	{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		System.out.println("BuildResourceUtil.parserMifFromZipFile()..creat target dir:"+RESOURCE_DIR);
		File resourceFile=new File(RESOURCE_DIR);
		if (!resourceFile.exists())
		{
			resourceFile.mkdir();
			File mifFile=new File(RESOURCE_DIR+File.separator+"mif");
			mifFile.mkdir();
		}
		DocumentBuilder db = dbf.newDocumentBuilder();
		ZipFile zip=new ZipFile(zipFileName);
		Enumeration entryEnum=zip.entries();
		while (entryEnum.hasMoreElements())
		{
			ZipEntry zipEntry=(ZipEntry)entryEnum.nextElement();
			String fileName=zipEntry.getName();

			//only process the "mif" directory
			String validNameStart="mif/";
			if (!fileName.startsWith(validNameStart))
				continue;
			String msgType=fileName;
			if (fileName.equals("mif/"+CMET_INFOR_SOURCE_FILE))
			{
				//parer CMET INFO
				CMETInfoParser cmetInfoParser = new CMETInfoParser();
				InputStream cmetIs=zip.getInputStream(zipEntry);
				cmetInfoParser.parserCMETInfoWithStream(cmetIs);//.parseCMETInfo(fileName);
				String targetFile=RESOURCE_DIR+File.separator+CMET_INFOR_TARGET_FILE;
				cmetInfoParser.saveCMETInofs(targetFile);
			}
			if (!msgType.endsWith(".mif"))
				continue;
			/**
			 * "cmetList.mif is the name of CMET configuration of HL7 release 2.1 dated on August, 2008
			 */
//			if (msgType.equalsIgnoreCase("cmetList.mif"))
//				continue;
			InputStream mifIs = null;
            InputStream mifIs2 = null;
            File newFile = null;
            while(isSortKeyReassigning)
            {
                //if (!pathName.toLowerCase().endsWith(".mif")) break;
                ReassignSortKeyToMIF rs = null;
                try
                {
                    rs = new ReassignSortKeyToMIF(msgType, zip.getInputStream(zipEntry));
                }
                catch(ApplicationException ae)
                {
                    System.out.println("Sort Key Reassigning error ("+msgType+") : " + ae.getMessage());
                    break;
                }
                newFile = new File(rs.getNewFileName());

                mifIs = new FileInputStream(newFile);
                mifIs2 = new FileInputStream(newFile);
                newFile.deleteOnExit();
                break;
            }

            if (mifIs == null)
            {
                mifIs = zip.getInputStream(zipEntry);
                mifIs2 = zip.getInputStream(zipEntry);
            }

            Document mifDoc = null;
            try
            {
                mifDoc = db.parse(mifIs);
            }
            catch(org.xml.sax.SAXParseException se)
            {
                //This block is for error protecting from Invalid byte 3 of 3-byte UTF-8 sequence.
                String tempName = FileUtil.getTemporaryFileName(".mif");

                try
                {
                    FileOutputStream fos = new FileOutputStream(tempName);
                    DataOutputStream dos = new DataOutputStream(fos);

                    while(true)
                    {
                        int i = -1;
                        try
                        {
                            i = mifIs2.read();
                        }
                        catch(Exception ee)
                        {
                            break;
                        }
                        if (i < 0) break;
                        if (i > 127) continue;

                        dos.writeByte((byte)i);
                    }
                    dos.close();
                    fos.close();
                    mifDoc = db.parse(new FileInputStream(tempName));
                }
                catch(Exception fe)
                {
                    System.out.println("######### ERROR : "+se.getMessage()+"\n" + FileUtil.readFileIntoString(newFile.getAbsolutePath()));
                    return;
                }
                File ff = new File(tempName);
                if ((ff.exists())&&(ff.isFile())) ff.delete();
            }
            if (newFile != null) newFile.delete();
            MIFParser mifParser = new MIFParser();
        	boolean mifParsed=mifParser.parse(mifDoc);
      		if (!mifParsed)
      		{
      			System.out.println("BuildResourceUtil.parserMifFromZipFile()...failed parsing:"+msgType);
      			continue;
      		}
    		if (msgType.indexOf("UV")>-1)
    			msgType=msgType.substring(0, msgType.indexOf("UV"));
    		else if (msgType.indexOf(".mif")>-1)
    			msgType=msgType.substring(0, msgType.indexOf(".mif"));
//    		System.out.println("BuildResourceUtil.parserMifFromZipFile()..save:"+fileName+"="+msgType);
        	  mifParser.saveMIFs(RESOURCE_DIR+"/" + fileName,msgType);
        }
	}

    public static void zipDir(ZipOutputStream zipFileOut, String dir, String extension) throws Exception
    {
        zipDir(zipFileOut, dir, extension, false);
    }
    public static void zipDir(ZipOutputStream zipFileOut, String dir, String extension, boolean isSortKeyReassigning) throws Exception
    {
        File dirObj = new File(dir);
        if(!dirObj.isDirectory())
        {
            throw new Exception(dir +"is not a directory");
        }
        addDir(dirObj,extension, zipFileOut,dirObj.getName(), isSortKeyReassigning);
    }

    private static void addDir(File dirObj, String extension, ZipOutputStream out,String parentPath, boolean isSortKeyReassigning) throws IOException
    {
        File[] files = dirObj.listFiles();
        byte[] tmpBuf = new byte[1024];

        for (int i=0; i<files.length; i++)
        {
            if(files[i].isDirectory())
            {
            	String subDirPath=files[i].getName();
            	if (parentPath!=null&&!parentPath.equals(""))
            		subDirPath=parentPath+"/"+subDirPath;
                addDir(files[i], extension, out, subDirPath, isSortKeyReassigning);
                continue;
            }
//            if(!files[i].isFile()) continue;

            String pathName = files[i].getAbsolutePath();
            //verify file extension if required
            if (extension!=null&&!extension.equals("")&&!pathName.endsWith(extension))
            	continue;
            while(isSortKeyReassigning)
            {
                if (!pathName.toLowerCase().endsWith(".mif"))
                	break;
                ReassignSortKeyToMIF rs = null;

                try
                {
                    rs = new ReassignSortKeyToMIF(pathName);
                }
                catch(ApplicationException ae)
                {
                    System.out.println("Sort Key Reassigning error ("+pathName+") : " + ae.getMessage());
                    break;
                }
                File newFile = new File(rs.getNewFileName());
                if (!files[i].delete())
                {
                    System.out.println("Deleteing error ("+pathName+") ");
                    newFile.delete();
                    break;
                }
                newFile.renameTo(new File(pathName));
                break;
            }

            FileInputStream in = new FileInputStream(pathName);

            String entryName=files[i].getName();
            if (parentPath!=null&&!parentPath.equals(""))
            	entryName=parentPath+"/"+entryName;
            out.putNextEntry(new ZipEntry(entryName));//.getAbsolutePath()));

            // Transfer from the file to the ZIP file
            int len;
            while((len = in.read(tmpBuf)) > 0)
            {
                out.write(tmpBuf, 0, len);
            }
            // Complete the entry
            out.closeEntry();
            in.close();
        }
    }

	public static void copyFiles(String strPath, String dstPath, String srcExtension) throws IOException
	{
	// if I remove all condition then it throws exception which I wrote below
	/* if ((src.getName().equals("admin")) ||
	(src.getParentFile().getParentFile().getName().equals("presentation")) ||
	(src.getName().equals("cashteam")) &&
	(src.isDirectory()))
	; // Do not copy anything under this.

	elseif (src.isDirectory())
	*/
	File src = new File(strPath);
	File dest = new File(dstPath);

	if (src.isDirectory())
	{
		//if(dest.exists()!=true)
		dest.mkdirs();
		String list[] = src.list();



		for (int i = 0; i < list.length; i++)
		{
			String dest1 = dest.getAbsolutePath() + "\\" + list[i];
			String src1 = src.getAbsolutePath() + "\\" + list[i];
			copyFiles(src1 , dest1,  srcExtension);
		}
	}
	else if (strPath==null||strPath.endsWith(srcExtension))
	{
		FileInputStream fin = new FileInputStream(src);
		FileOutputStream fout = new FileOutputStream (dest);
		int c;
		while ((c = fin.read()) >= 0)
			fout.write(c);
			fin.close();
			fout.close();
	}
}
	public static void main(String[] args)
	{
		System.out.println(findResourceFile("resourceV2.zip"));

		if (args.length<1)
		{
			System.out.println("BuildResourceUtil.main()..parameter:"+args);
			System.exit(-1);
		}
	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.7  2009/03/25 14:00:09  wangeug
 * HISTORY :load HL7 artifacts with new procedure
 * HISTORY :
 * HISTORY :Revision 1.6  2008/09/29 15:42:45  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */