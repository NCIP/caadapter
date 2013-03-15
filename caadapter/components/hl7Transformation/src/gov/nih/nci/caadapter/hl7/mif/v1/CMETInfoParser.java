/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**





 */
package gov.nih.nci.caadapter.hl7.mif.v1;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import gov.nih.nci.caadapter.hl7.datatype.XSDParserUtil;
import gov.nih.nci.caadapter.hl7.mif.CMETRef;

/**
 * The class load a MIF document into the MIF class object.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: phadkes $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-06-09 19:53:50 $
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
	public void parseCMETInfo(String fileName) throws Exception {
//  		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//  		DocumentBuilder db = dbf.newDocumentBuilder();
		InputStream is =getClass().getResourceAsStream("/"+fileName);
		parserCMETInfoWithStream(is);
//		Document doc = db.parse(is);
//		Node node = doc.getDocumentElement(); //This is the root element of the document
//
//		Node child = node.getFirstChild();
//		while (child != null) {
//			if (child.getNodeName().contains("ownedCommonModelElement"))
//				parseOwnedCommonModelElement(child);
//			child = child.getNextSibling();
//		}
	}
	public void parseOwnedCommonModelElement(Node ownedCommonModelElementNode) {
		CMETRef cmetRef = new CMETRef();
		cmetRef.setName(XSDParserUtil.getAttribute(ownedCommonModelElementNode, "name"));
		cmetRef.setClassName("");
		Node node = XSDParserUtil.getFirstChildElement(ownedCommonModelElementNode);

		while (node != null) {
			if (node.getNodeName().endsWith("annotations")); //Ignore for now
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
	public void printCMETInfo() {
		for(CMETRef cmetRef:cmetInfos) {
			System.out.println("Ref name is " + cmetRef.getName());
			System.out.println("  Filename is: " + cmetRef.getFilename());
			System.out.println("  class name is: " + cmetRef.getClassName());
		}
	}

	public void saveCMETInofs(String fileName) throws Exception {
		OutputStream os = new FileOutputStream(fileName);
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(cmetInfos);
		oos.close();
		os.close();
	}

	public void loadCMETInofs() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/cmetInfos");
		ObjectInputStream ois = new ObjectInputStream(is);
		cmetInfos = (HashSet)ois.readObject();
		ois.close();
		is.close();
//		//always load from mif.zip/
//		String cmetFileName="cmetinfo.coremif";
//		parseCMETInfo(cmetFileName);
	}

	public static void main(String[] args) throws Exception {
		CMETInfoParser cmetInfoParser = new CMETInfoParser();
		cmetInfoParser.parseCMETInfo("T:/YeWu/Edition2006/mif/cmetinfo.coremif");
		cmetInfoParser.printCMETInfo();
		cmetInfoParser.saveCMETInofs("C:/temp/serializedMIF/resource/cmetInfos");
//		CMETInfoParser cmetInfoParser = new CMETInfoParser();
//		cmetInfoParser.loadCMETInofs();
//		cmetInfoParser.printCMETInfo();
	}
}
