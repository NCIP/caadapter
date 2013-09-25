/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */

package gov.nih.nci.caadapter.hl7.mif.v1;

import gov.nih.nci.caadapter.common.ApplicationException;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.hl7.datatype.DatatypeParser;
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
	            	 mifIndexInfos.addMessageType(filename);
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
		 ZipFile zip = new ZipFile(zipFileName);
		 Enumeration entryEnum=zip.entries();
		 MIFIndexParser mifInfoParser = new MIFIndexParser();
		 MIFIndex mifIndexInfos = new MIFIndex();
		 while (entryEnum.hasMoreElements())
		 {
			ZipEntry zipEntry=(ZipEntry)entryEnum.nextElement();
			String fileName=zipEntry.getName();
			String msgType=fileName;
			if (!msgType.endsWith(".mif"))
				continue;
      	  	mifIndexInfos.addMessageType(fileName);
		 }
		 mifInfoParser.saveMIFIndex(RESOURCE_DIR+File.separator+"mifIndexInfos",mifIndexInfos);
	}

	public static void parserMifFromZipFile(String zipFileName) throws Exception
	{
        parserMifFromZipFile(zipFileName, false);
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
			String msgType=fileName;
			if (fileName.equals(CMET_INFOR_SOURCE_FILE))
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

			InputStream mifIs = null;
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
                newFile.deleteOnExit();
                break;
            }

            if (mifIs == null) mifIs = zip.getInputStream(zipEntry);

            Document mifDoc = null;
            try
            {
                mifDoc = db.parse(mifIs);//"T:/YeWu/Edition2006/mif/" + filename);
            }
            catch(org.xml.sax.SAXParseException se)
            {
                System.out.println("######### ERROR : "+se.getMessage()+"\n" + FileUtil.readFileIntoString(newFile.getAbsolutePath()));
                return;
            }
            if (newFile != null) newFile.delete();
            MIFParser mifParser = new MIFParser();
        	mifParser.parse(mifDoc);

    		if (msgType.indexOf("UV")>-1)
    			msgType=msgType.substring(0, msgType.indexOf("UV"));
    		else if (msgType.indexOf(".mif")>-1)
    			msgType=msgType.substring(0, msgType.indexOf(".mif"));
        	  mifParser.saveMIFs(RESOURCE_DIR+"/mif/" + fileName,msgType);
        }
	}

	public static void loadDatatypes(String coreSchemaHome) throws Exception
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
  		DocumentBuilder db;
//  		System.out.println("BuildResourceUtil.loadDatatypes()..coreSchemaHome:"+coreSchemaHome);
			db = dbf.newDocumentBuilder();
			DatatypeParser datatypeParser = new DatatypeParser();
			String pathVoc=coreSchemaHome+"/voc.xsd";
			String pathBase=coreSchemaHome+"/datatypes-base.xsd";
			String pathDatatype=coreSchemaHome+"/datatypes.xsd";
            //String pathNarrativeBlock=coreSchemaHome+"/NarrativeBlock.xsd";

//			InputStream isVoc =datatypeParser.getClass().getResourceAsStream(pathVoc);
//			InputStream isDatatypeBase =datatypeParser.getClass().getResourceAsStream(pathBase);
//			InputStream isDatatype =datatypeParser.getClass().getResourceAsStream(pathDatatype);
//
//			Document vocDoc = db.parse(isVoc);
//			Document baseDoc = db.parse(isDatatypeBase);
//			Document allDoc = db.parse(isDatatype);

			Document vocDoc = db.parse(new File(pathVoc));
			Document baseDoc = db.parse(new File(pathBase));
			Document allDoc = db.parse(new File(pathDatatype));
            //Document narrativeDoc = db.parse(new File(pathNarrativeBlock));
            datatypeParser.handleGTS();
			datatypeParser.parse(vocDoc);
			datatypeParser.parse(baseDoc);
			datatypeParser.parse(allDoc);
            //datatypeParser.parse(narrativeDoc);
            datatypeParser.populateDatatypes();
			datatypeParser.saveDatatypes(RESOURCE_DIR+"/"+DATATYPE_FILE);
//			populateSubclasses();
	}


    public static void zipDir(String zipFileName, String dir) throws Exception
    {
        zipDir(zipFileName, dir, false);
    }
    public static void zipDir(String zipFileName, String dir, boolean isSortKeyReassigning) throws Exception
    {
        File dirObj = new File(dir);
        if(!dirObj.isDirectory())
        {
            throw new Exception(zipFileName +"is not a directory");
        }

        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
//        System.out.println("Creating : " + zipFileName);

        addDir(dirObj, out,"", isSortKeyReassigning);
        // Complete the ZIP file
        out.close();
//        System.out.println("BuildResourceUtil.zipDir() deleting:"+dirObj);
        dirObj.delete();
    }

    private static void addDir(File dirObj, ZipOutputStream out,String parentPath, boolean isSortKeyReassigning) throws IOException
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
                addDir(files[i], out, subDirPath, isSortKeyReassigning);
                continue;
            }
            if(!files[i].isFile()) continue;

            String pathName = files[i].getAbsolutePath();

            while(isSortKeyReassigning)
            {
                if (!pathName.toLowerCase().endsWith(".mif")) break;
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
            files[i].delete();
        }
        dirObj.delete();
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
//		System.out.println("BuildResourceUtil.copyFiles()..copy:"+strPath +"..target:"+dstPath);
		FileInputStream fin = new FileInputStream(src);
		FileOutputStream fout = new FileOutputStream (dest);
		int c;
		while ((c = fin.read()) >= 0)
			fout.write(c);
			fin.close();
			fout.close();
	}
//	else
//	{
//		System.out.println("BuildResourceUtil.copyFiles()..igonor:"+strPath);
//
//	}
}
	public static void main(String[] args)
	{
		System.out.println(findResourceFile("resourceV2.zip"));

		if (args.length<1)
		{
			System.out.println("BuildResourceUtil.main()..parameter:"+args);
			System.exit(-1);
		}
		for(String arg:args)
			System.out.println("BuildResourceUtil.main()..parameter:"+arg);
		String tempFileHome=System.getProperty("user.dir")+File.separator+"tempMifSerialized";
		if(args.length>1)
			tempFileHome=args[1];

		BuildResourceUtil.RESOURCE_DIR=tempFileHome;

		String mifZipPath=args[0]+"/processable/mif/mif.zip";
		String datatypeHomePath=args[0]+"/processable/coreschemas";
		try {
			BuildResourceUtil.parserMifFromZipFile(mifZipPath);

			BuildResourceUtil.parerMifIndexFromZipFile(mifZipPath);

//		BuildResourceUtil.RESOURCE_DIR="C:/temp/buildMIF/dirResource";
//		BuildResourceUtil.parerMifIndexFromFileDirectory("T:/YeWu/Edition2006/mif");
//		BuildResourceUtil.parserMifFromFileDirectory("T:/YeWu/Edition2006/mif");
			BuildResourceUtil.loadDatatypes(datatypeHomePath);

			BuildResourceUtil.zipDir("resource.zip",BuildResourceUtil.RESOURCE_DIR);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}