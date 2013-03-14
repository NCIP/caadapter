/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.mif.v1;

import java.io.*;
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
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.hl7.mif.MIFClass;
import gov.nih.nci.caadapter.hl7.mif.MIFReferenceResolver;
import gov.nih.nci.caadapter.hl7.mif.NormativeVersionUtil;

/**
 * The class provides Utilities to access the MIF info.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: altturbo $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.22 $
 *          date        $Date: 2009-11-09 21:46:05 $
 */
public class MIFParserUtil {

	private static MIFClass loadUnprocessedMIF(String mifFileName)
	{
		try
        {
			String mifPath=null;
        	URL mifURL=null;

			while(mifURL==null)
			{
				//webStart deployment
				String specHome=NormativeVersionUtil.getCurrentMIFIndex().findSpecificationHome();

                File temp = new File(specHome);
                if (!temp.exists()) break;

                String specHome1 = null;

                if (specHome.startsWith(FileUtil.getWorkingDirPath()))
                {
                    specHome1 = specHome.substring(FileUtil.getWorkingDirPath().length()).trim();
                    if (specHome1.startsWith(File.separator)) specHome1 = specHome1.substring(File.separator.length());
                }


                //normative 2008 structure
                for (int i=0;i<2;i++)
                {
                    if (mifURL!=null) break;
                    String specHome2 = "";
                    if (i == 0)
                    {
                        if (temp.isDirectory())
                        {
                            if (specHome1 != null) specHome2 = specHome1;
                            else specHome2 = temp.getName();
                        }
                        else
                        {
                            File parent = temp.getParentFile();
                            if (specHome1 != null) specHome2 = specHome1;
                            else specHome2 = parent.getName() + File.separator +temp.getName();
                        }
                    }
                    else specHome2 = specHome;
                    mifPath=specHome2+"/mif/"+mifFileName;

                    mifURL=FileUtil.retrieveResourceURL(specHome2, "mif", mifFileName);
                    //normative 2006 structure
                    if (mifURL==null)
                    {
                        mifPath=specHome2+"/"+mifFileName;
                        mifURL=FileUtil.retrieveResourceURL(specHome2, null, mifFileName);
                    }
                }
                break;
            }
            //normative 2006 structure /COCT_MTxxxxxxxUVxx.mif
            if (mifURL==null)
				mifURL=FileUtil.retrieveResourceURL(mifFileName);
            //normative 2008 structure /mif/COCT_MTxxxxxxxUVxx.mif
            if (mifURL==null)
            {
                mifPath="mif/" + mifFileName;
			    mifURL=FileUtil.retrieveResourceURL(mifPath);
            }

            InputStream mifIs =null;
            InputStream mifIs2 =null;
            if (mifURL!=null)
            {
                mifIs=mifURL.openStream();
                mifIs2=mifURL.openStream();
            }
            else
			{
				String mifZipFilePath= NormativeVersionUtil.getCurrentMIFIndex().getMifPath();
				System.out.println("MIFParserUtil.loadUnprocessedMIF()..mifZip path:"+mifZipFilePath);
				File zipFile=new File(mifZipFilePath);
				if (!zipFile.exists())
					return null;
				ZipFile mifZipFile=new ZipFile(mifZipFilePath);
				ZipEntry mifEntry=mifZipFile.getEntry(mifFileName);
				if (mifEntry==null)
					mifEntry=mifZipFile.getEntry(mifPath);
				System.out.println("MIFParserUtil.loadUnprocessedMIF()..mifEntry:"+mifEntry.getName());
				mifIs=mifZipFile.getInputStream(mifEntry);
                mifIs2=mifZipFile.getInputStream(mifEntry);
            }
			//this.getClass().getResourceAsStream("/mif/" + mifFileName);
        	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        	DocumentBuilder db = dbf.newDocumentBuilder();
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
                    System.out.println(" ### This [Fatal Error] is Completely recovered -- " + se.getMessage());
                }
                catch(Exception fe)
                {
                    throw se;
                }
                File ff = new File(tempName);
                if ((ff.exists())&&(ff.isFile())) ff.delete();
            }

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

        System.out.println("## MIFParserUtil.getMIFClass() ... mif file name : " + mifFileName);
        //if (mifFileName.trim().endsWith("QQQ")) mifFileName = mifFileName.substring(0, mifFileName.length()-4);
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

    public static String searchAnnotation(Node node)
    {
        //System.out.println("FFFF search annotation : " );

        if (node == null) return null;
        Node n = node;
        String str = n.getTextContent();
        if ((str != null)||(!str.trim().equals("")))
        {
            str = str.trim();
            char[] cc = str.toCharArray();
            String stt = "";
            String before = " ";
            for (char ch:cc)
            {
                int inn = (int)((byte) ch);
                if (inn <= 32)
                {
                    if (before.equals(" ")) {}
                    else stt = stt + " ";
                    before = " ";
                }
                else
                {
                    stt = stt + ch;
                    before = "" + ch;
                }
            }

            if (!stt.trim().equals("")) return stt;
            else
            {
                str = null;
                //System.out.println("FFFF annotation : " + stt);
            }
        }
        else str = null;

        if (!n.hasChildNodes()) return null;

        NodeList nlist = n.getChildNodes();

        for (int i=0;i<nlist.getLength();i++)
        {
            Node nn = nlist.item(i);
            str = searchAnnotation(nn);
            if ((str != null)&&(!str.trim().equals(""))) return str;
        }

        return null;
    }


    public static void main(String[] args) throws Exception {
		MIFParserUtil.loadMIFClassWithVersion("mif","Normative_2006");

	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.21  2009/11/09 21:45:38  altturbo
 * HISTORY :add searchAnnotation(Node node)
 * HISTORY :
 * HISTORY :Revision 1.20  2009/08/18 15:18:41  altturbo
 * HISTORY :minor change
 * HISTORY :
 * HISTORY :Revision 1.19  2009/08/17 20:26:50  altturbo
 * HISTORY :Change the searching priority for resource file
 * HISTORY :
 * HISTORY :Revision 1.18  2009/08/14 21:44:49  altturbo
 * HISTORY :When search mifURL, First priority is spechome+mif+mifFilename
 * HISTORY :
 * HISTORY :Revision 1.17  2009/08/07 21:45:02  altturbo
 * HISTORY :protecting from Invalid byte 3 of 3-byte UTF-8 sequence.
 * HISTORY :
 * HISTORY :Revision 1.16  2009/03/18 15:50:53  wangeug
 * HISTORY :enable wesstart to support multiple normatives
 * HISTORY :
 * HISTORY :Revision 1.15  2009/03/13 14:55:45  wangeug
 * HISTORY :support multiple HL& normatives
 * HISTORY :
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