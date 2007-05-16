/**
 * <!-- LICENSE_TEXT_START -->
  * <!-- LICENSE_TEXT_END -->
 */

package gov.nih.nci.caadapter.hl7.mif.v1;

import gov.nih.nci.caadapter.hl7.mif.MIFClass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import dom.ParserWrapper;

/**
 * The class load a MIF document into the MIF class object.
 *
 * @author OWNER: Ye Wu
 * @author LAST UPDATE $Author: wuye $
 * @version Since caAdapter v4.0
 *          revision    $Revision: 1.1 $
 *          date        $Date: 2007-05-16 20:20:59 $
 */

public class MIFParser {
	private String prefix;
    private MIFClass mifClass=null;
    
	public boolean parse(Node node) {
        Document document = (Document)node;
        String schemaString = document.getDocumentElement().getNodeName();
        if (!schemaString.endsWith("serializedStaticModel")) return false;
        if (schemaString.equals("serializedStaticModel")) prefix = "";
        else if (schemaString.endsWith(":serializedStaticModel")) {
        	prefix = schemaString.substring(0,schemaString.lastIndexOf(":serializedStaticModel")+1);
        }
        Node child = document.getDocumentElement().getFirstChild();
        while (child != null) {
        	if (child.getNodeName().equals(prefix+"ownedEntryPoint")) {
        		Node ownedEntryPointChild = child.getFirstChild();
        		while (ownedEntryPointChild != null) {
        			if (ownedEntryPointChild.getNodeName().equals(prefix+"specializedClass")) {
        				SpecializedClassParser specializedClass = new SpecializedClassParser();
        				mifClass = specializedClass.parseSpecializedClass(ownedEntryPointChild, prefix);
        			}
        			ownedEntryPointChild = ownedEntryPointChild.getNextSibling();
        		}
        	}
            child = child.getNextSibling();
        }
        return true;
	}
	
	public MIFClass getMIFClass() {
		return this.mifClass;
	}
	
	public void saveMIFs(String fileName) throws Exception {
		OutputStream os = new FileOutputStream(fileName);
		ObjectOutputStream oos = new ObjectOutputStream(os); 
		oos.writeObject(mifClass);
		oos.close();
		os.close();
	}
	
	public MIFClass loadMIF(String mifFileName) {
		try {
			InputStream is = this.getClass().getResourceAsStream("/mif/" + mifFileName);
			ObjectInputStream ois = new ObjectInputStream(is);
			mifClass = (MIFClass)ois.readObject();
			ois.close();
			is.close();
			return mifClass;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String argv[]) throws Exception{
    
  		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
  		DocumentBuilder db = dbf.newDocumentBuilder();

//		Document mifDoc = db.parse("T:/YeWu/Edition2006/mif/COCT_MT150003UV03.mif");
//		Document mifDoc = db.parse("T:/YeWu/Edition2006/processable/mif/COCT_MT150003UV03.mif");
//		Document mifDoc = db.parse("T:/YeWu/Edition2006/mif/PORR_MT040011UV01.mif");

  		
  		File dir = new File("T:/YeWu/Edition2006/mif/");
      
  		String[] children = dir.list();
  		if (children == null) {
          // Either dir does not exist or is not a directory
  		} else {
  			for (int i=0; i<children.length; i++) {
              String filename = children[i];
            if (filename.contains("POLB_MT004000.mif")) {//templateParameter
//                if (filename.contains("_MT")) {//templateParameter
//            if (filename.contains("COCT_MT7100")) {//templateParameter
//                  if (filename.contains("MCAI_MT700201UV01.mif")) {//templateParameter and has recursion
//                if (filename.contains("_MT210000")) {// The file contains RTO_STY_STY and choice
//                if (filename.contains("_MT150003")) {// The file contains association
//                if (filename.contains("_MT040203")) {// The file contains ref
            	  System.out.println("Parsing:" + "T:/YeWu/Edition2006/mif/" + filename + "... ...");
            	  Document mifDoc = db.parse("T:/YeWu/Edition2006/mif/" + filename);
            	  MIFParser mifParser = new MIFParser();
            	  mifParser.parse(mifDoc);
            	  mifParser.saveMIFs("c:/temp/mif/" + filename);
            	  mifParser.getMIFClass().printMIFClass(0, new HashSet());
              }
  			}
  		}

    }
}
