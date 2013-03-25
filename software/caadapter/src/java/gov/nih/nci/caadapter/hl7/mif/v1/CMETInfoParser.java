/*L
 * Copyright SAIC, SAIC-Frederick.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

package gov.nih.nci.caadapter.hl7.mif.v1;

/**
 * The class defines an object parsing CMET information.
 *
 * @author OWNER: Eugene Wang
 * @author LAST UPDATE $Author: altturbo $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.11 $
 *          date        $Date: 2009-11-11 20:27:19 $
 */

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.hl7.datatype.XSDParserUtil;
import gov.nih.nci.caadapter.hl7.mif.CMETRef;
import gov.nih.nci.caadapter.hl7.mif.NormativeVersionUtil;

/**
 * The class load a MIF document into the MIF class object.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: altturbo $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.11 $
 *          date        $Date: 2009-11-11 20:27:19 $
 */

public class CMETInfoParser {
	HashSet<CMETRef> cmetInfos = new HashSet();

	public HashSet<CMETRef> getCMETRefs() {
		return cmetInfos;
	}
	public void parserCMETInfoWithStream (InputStream is)throws Exception
	{
  		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
  		DocumentBuilder db = dbf.newDocumentBuilder();

		Document doc = db.parse(is);
		Node node = doc.getDocumentElement(); //This is the root element of the document

		Node child = node.getFirstChild();
		while (child != null) {
			if (child.getNodeName().contains("ownedCommonModelElement"))
				parseOwnedCommonModelElement(child);
			child = child.getNextSibling();
		}
	}

	private void parseOwnedCommonModelElement(Node ownedCommonModelElementNode) {
		CMETRef cmetRef = new CMETRef();
		cmetRef.setName(XSDParserUtil.getAttribute(ownedCommonModelElementNode, "name"));
		cmetRef.setClassName("");
		Node node = XSDParserUtil.getFirstChildElement(ownedCommonModelElementNode);

		while (node != null) {
			//if (node.getNodeName().endsWith("annotations")); //Ignore for now
            if ((node.getNodeName().endsWith("annotations"))||(node.getNodeName().endsWith("annotation"))) //Ignore for now
            {
                String str = MIFParserUtil.searchAnnotation(node);
                if ((str != null)||(!str.trim().equals(""))) System.out.println("##FFF# CmetInfoParser Annotation : " + str);
            }
            if (node.getNodeName().endsWith("supplierStructuralDomain")); //Ignore for now
			if (node.getNodeName().endsWith("specializationChildStaticModel")) //Ignore for now
			{
				String filename = XSDParserUtil.getAttribute(node, "subSection");
				filename = filename + XSDParserUtil.getAttribute(node, "domain");
				filename = filename + "_";
				if (XSDParserUtil.getAttribute(node, "artifact").contains("MT")) {
					filename = filename+"MT";
				}
				filename = filename + XSDParserUtil.getAttribute(node, "id");
				if (XSDParserUtil.getAttribute(node, "realm") != null)
					filename = filename + XSDParserUtil.getAttribute(node, "realm");
				if (XSDParserUtil.getAttribute(node, "version")!= null)
					filename = filename + XSDParserUtil.getAttribute(node, "version");
				cmetRef.setFilename(filename);
			}
			if (node.getNodeName().endsWith("specializationChildEntryClass")) //Ignore for now
			{
				cmetRef.setClassName(XSDParserUtil.getAttribute(node, "name"));
			}
			if (node.getNodeName().endsWith("mif:specializationClass")); //Use for choices, and will be ignored for now
			cmetInfos.add(cmetRef);
			node = XSDParserUtil.getNextElement(node);
		}
	}

	public void saveCMETInofs(String fileName) throws Exception {
		OutputStream os = new FileOutputStream(fileName);
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(cmetInfos);
		oos.close();
		os.close();
	}

    public void loadCMETInofs() throws Exception
    {
		InputStream cmetIs=null;

        String mifZipFilePath= NormativeVersionUtil.getCurrentMIFIndex().getMifPath();
        ZipFile mifZipFile=new ZipFile(mifZipFilePath);
        ZipEntry cmetEntry=mifZipFile.getEntry("mif/cmetinfo.coremif");
        if (cmetEntry==null)
            cmetEntry=mifZipFile.getEntry("cmetinfo.coremif");
        if (cmetEntry!=null) cmetIs=mifZipFile.getInputStream(cmetEntry);

        if (cmetIs==null)
        {
            //normative 2008 structure /mif/cmetinfo.coremif
		    //normative 2006 structure /cmetinfo.coremif
            URL cmetURL=null;
            //webStart deployment
            String specHome=NormativeVersionUtil.getCurrentMIFIndex().findSpecificationHome();
            //normative 2008 structure

            cmetURL=FileUtil.retrieveResourceURL(specHome+"/mif/cmetinfo.coremif");
            //normative 2006 structure
            if (cmetURL==null)
                cmetURL=FileUtil.retrieveResourceURL(specHome+"/cmetinfo.coremif");
            //working for Normative 2006
            if (cmetURL==null)
                cmetURL=FileUtil.retrieveResourceURL("cmetinfo.coremif");
            if (cmetURL==null)
            {
                cmetURL=FileUtil.retrieveResourceURL("mif/cmetinfo.coremif");
            }
            System.out.println("##FFF# cmetURL=" + cmetURL.toString());

            if (cmetURL!=null)
                cmetIs=cmetURL.openStream();
            else
            {
                throw new Exception("Not Found CMET Information.");
            }
        }
        parserCMETInfoWithStream(cmetIs);
	}


/*
	public void loadCMETInofs() throws Exception {
		//normative 2008 structure /mif/cmetinfo.coremif
		//normative 2006 structure /cmetinfo.coremif
		URL cmetURL=FileUtil.retrieveResourceURL("mif/cmetinfo.coremif");
		//working for Normative 2006
		if (cmetURL==null)
			cmetURL=FileUtil.retrieveResourceURL("cmetinfo.coremif");
		if (cmetURL==null)
		{
			//webStart deployment
			String specHome=NormativeVersionUtil.getCurrentMIFIndex().findSpecificationHome();
			//normative 2008 structure
			cmetURL=FileUtil.retrieveResourceURL(specHome+"/mif/cmetinfo.coremif");
			//normative 2006 structure
			if (cmetURL==null)
				cmetURL=FileUtil.retrieveResourceURL(specHome+"/cmetinfo.coremif");
		}
		InputStream cmetIs=null;
		if (cmetURL!=null)
			cmetIs=cmetURL.openStream();
		else
		{
			String mifZipFilePath= NormativeVersionUtil.getCurrentMIFIndex().getMifPath();
			ZipFile mifZipFile=new ZipFile(mifZipFilePath);
			ZipEntry cmetEntry=mifZipFile.getEntry("cmetinfo.coremif");
			if (cmetEntry==null)
				cmetEntry=mifZipFile.getEntry("mif/cmetinfo.coremif");
			cmetIs=mifZipFile.getInputStream(cmetEntry);
		}
		parserCMETInfoWithStream(cmetIs);
	}
 */

	public static void main(String[] args) throws Exception {
//		CMETInfoParser cmetInfoParser = new CMETInfoParser();
//		cmetInfoParser.parseCMETInfo("T:/YeWu/Edition2006/mif/cmetinfo.coremif");
//		cmetInfoParser.printCMETInfo();
//		cmetInfoParser.saveCMETInofs("C:/temp/serializedMIF/resource/cmetInfos");
//		CMETInfoParser cmetInfoParser = new CMETInfoParser();
//		cmetInfoParser.loadCMETInofs();
//		cmetInfoParser.printCMETInfo();
	}
}
/**
 * HISTORY :$Log: not supported by cvs2svn $
 * HISTORY :Revision 1.10  2009/03/18 15:50:53  wangeug
 * HISTORY :enable wesstart to support multiple normatives
 * HISTORY :
 * HISTORY :Revision 1.9  2009/03/13 14:54:01  wangeug
 * HISTORY :support multiple HL& normatives
 * HISTORY :
 * HISTORY :Revision 1.8  2009/02/25 15:57:20  wangeug
 * HISTORY :enable webstart
 * HISTORY :
 * HISTORY :Revision 1.7  2008/09/29 15:42:44  wangeug
 * HISTORY :enforce code standard: license file, file description, changing history
 * HISTORY :
 */